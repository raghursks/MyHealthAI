package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

public class DashboardPage extends BasePage {

    private final String selectAllAcrossPagesLink = "text=Select all 254 patients";
    // Reads the total number value inside the Tile
    //private final String nonConsentedTileCount = "div.card-body:has(p:has-text(\"Non-Consented But Eligible\")) span.counter-value";
    private final String nonConsentedTileCount = "div.card.card-animate:has(p:has-text('Non-Consented But Eligible')) span.counter-value";

    // Clicks the tile to navigate to the patient list
    //private final String nonConsentedTileClick = "div.card-body:has(p:has-text(\"Non-Consented But Eligible\"))";
    private final String nonConsentedTileClick = "div.card.card-animate:has(p:has-text('Non-Consented But Eligible'))";

    // Read tile count
    private final String totalPatientsTileCount = "div.card-body:has(p:has-text(\"Total Patients\")) span.counter-value";
    // Click tile
    private final String totalPatientsTileClick = "div.card-body:has(p:has-text(\"Total Patients\"))";
    // Row selector (table rows)
    private final String patientRows = "#customerList table tbody tr";
    // Pagination 'Next' button (only if not disabled)
    private final String nextButtonSelector = "a.page-link[aria-label='Next']:not([disabled])";
    private final String selectAllCheckbox = "#checkAll";
    private final String lastRowCheckbox = "#customerList tbody tr:last-child input[type='checkbox']";
    private final String communicationButton = "button:has-text('Communication')";
    private final String logo = "img[alt='My Health AI']";
    private final String profileImg = "span.header-profile-user.rounded-circle";

    public DashboardPage(Page page) {
        super(page);
    }

    // Getter methods
    public String getTotalPatientsTileCountSelector() {
        return totalPatientsTileCount;
    }

    public String getTotalPatientsTileClickSelector() {
        return totalPatientsTileClick;
    }

    public String getPatientRowsSelector() {
        return patientRows;
    }

    public String getNextButtonSelector() {
        return nextButtonSelector;
    }

    public String getNonConsentedTileCountSelector() {
        return nonConsentedTileCount;
    }

    public String getNonConsentedTileClickSelector() {
        return nonConsentedTileClick;
    }

    public int getCheckedCount() {
        return page.locator("input[type='checkbox']:checked").count();
    }


    public int clickTileAndCount(String tileClickSelector, String rowSelector, String nextButtonSelector) {
        page.locator(tileClickSelector).click();
        page.waitForSelector(rowSelector);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        int totalRowCount = 0;
        int pageIndex = 1;

        while (true) {
            Locator rows = page.locator(rowSelector);
            // Wait for at least 2 rows to appear
            for (int i = 0; i < 10; i++) {
                if (rows.count() > 1) break;
                page.waitForTimeout(1000);
            }
            int rowsOnPage = rows.count();
            System.out.println("Page " + pageIndex + " rows: " + rowsOnPage);
            totalRowCount += rowsOnPage;

            Locator nextBtn = page.locator(nextButtonSelector);
            if (nextBtn.count() == 0 || !nextBtn.first().isEnabled()) {
                System.out.println("No next button or it is disabled. Stopping pagination.");
                break;
            }
            System.out.println("Clicking next button on page " + pageIndex);
            nextBtn.first().click();
            page.waitForTimeout(1000);  // wait for new data to load
            page.waitForSelector(rowSelector);
            pageIndex++;
        }

        System.out.println("Total rows across all pages: " + totalRowCount);
        return totalRowCount;
    }

    public int getTileCount(String tileSelector) {
        Locator tile = page.locator(tileSelector);
        System.out.println("Matching tile elements count: " + tile.count());
        // Wait for the tile to become visible
        tile.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForTimeout(2000);
        String lastValue = "";
        int stableCount = 0;
        int stableAttempts = 0;
        for (int i = 1; i <= 20; i++) {  // up to ~20 seconds
            try {
                String currentText = tile.innerText().trim();
                System.out.println("Attempt " + i + ": " + currentText);
                // Check if current value is same as last attempt
                if (currentText.equals(lastValue) && currentText.matches("\\d+")) {
                    stableAttempts++;
                    if (stableAttempts >= 5) {  // value is stable for 5 checks
                        stableCount = Integer.parseInt(currentText);
                        break;
                    }
                } else {
                    stableAttempts = 0; // reset stability check
                }

                lastValue = currentText;
            } catch (Exception e) {
                System.out.println("Error reading count: " + e.getMessage());
            }

            page.waitForTimeout(1000);
        }

        if (stableCount > 0) {
            return stableCount;
        }

        throw new RuntimeException("Tile count never stabilized: " + tileSelector);
    }

    public int selectAllExceptOne() {
        // 1. Click "select all" checkbox
        click(selectAllCheckbox);

        // 2. Ensure checkboxes are selected (at least 1 should be checked)
        Locator selectedCheckboxes = page.locator("input[type='checkbox']:checked");
        selectedCheckboxes.first().waitFor(new Locator.WaitForOptions().setTimeout(3000));

        // 3. Get all visible row checkboxes (excluding header)
        Locator allCheckboxes = page.locator("table tbody input[type='checkbox']");
        int totalCheckboxes = allCheckboxes.count();

        if (totalCheckboxes == 0) {
            throw new RuntimeException("No row checkboxes found to select.");
        }

        // 4. Target the last row checkbox
        Locator lastCheckbox = allCheckboxes.nth(totalCheckboxes - 1);
        lastCheckbox.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(3000));

        // 5. Deselect it if it was selected
        if (lastCheckbox.isChecked()) {
            lastCheckbox.click();
            System.out.println("Deselected last row checkbox.");
        } else {
            System.out.println("Last row checkbox was already not selected.");
        }

        // 6. Count selected checkboxes (after deselection)
        int checkedCount = selectedCheckboxes.count();
        System.out.println("Selected checkboxes after deselection: " + checkedCount);

        return checkedCount;
    }

    public void clickCommunication() {
        // After selectAllExceptOne()
        Locator communicationbtn = page.locator(communicationButton);
        for (int i = 0; i < 10; i++) {
            if (communicationbtn.isEnabled()) {
                break;
            }
            page.waitForTimeout(500); // wait 0.5 sec
        }

        if (!communicationbtn.isEnabled()) {
            throw new RuntimeException("Communication button not enabled after selection.");
        }
        communicationbtn.click();

    }

    public void clickProfileImage() {
        click(profileImg);
    }
    public void clickLogo() {
        click(logo);
    }
}
