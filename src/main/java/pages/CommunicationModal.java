package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Paths;

public class CommunicationModal extends BasePage {

    private final String smsTab = "a[role='tab']:has-text('SMS')";
    private final String emailTab = "a[role='tab']:has-text('Email')";
    private final String newGroupOption = "#newgroup";
    private final String groupNameField = ".mt-3 > input[placeholder='Enter the group name']";
    private final String groupListSelection = "button.list-group-item.list-group-item-action.list-item-custom:has-text('Appointment confirmation')";
    private final String saveGroup = "button.btn.btn-primary:has-text('Save Group')";
    private final String popupModal = "//div[contains(@class,'modal-content')][.//h5[contains(text(),'Confirm Save Group')]]";
    private final String popupCount = "div.modal-body span.badge";
    private final String popupYes = "button:has-text('Yes, Save')";
    private final String toField = "#toField";
    private final String subjectField = "input[formcontrolname='subject']";
    private final String bodyField = "div[aria-label='Editor editing area: main']";
    private final String sendButton = "button:has-text('Send')";
    private final String okButton = "button.swal2-confirm:has-text('OK')";
    private final String emailAlertokButton = "button.swal2-confirm";


    public CommunicationModal(Page page) {
        super(page);
    }

    public void clickSmsTab() {
        click(smsTab);
    }

    public void clickEmailTab() {
        click(emailTab);
    }

    public void newGroup(String name) {
        click(newGroupOption);
        fill(groupNameField, name);
        click(groupListSelection);
        click(saveGroup);

    }

    public int confirmAndGetCount() {
        // 1. Wait for the correct modal
        Locator modal = page.locator(popupModal).first();
        modal.waitFor(new Locator.WaitForOptions()
                .setTimeout(3000)
                .setState(WaitForSelectorState.VISIBLE));

        // 2. Validate the modal title
        Locator titleLocator = modal.locator("h5.modal-title");
        titleLocator.waitFor(new Locator.WaitForOptions()
                .setTimeout(3000)
                .setState(WaitForSelectorState.VISIBLE));
        String title = titleLocator.innerText().trim();

        if (!"Confirm Save Group".equals(title)) {
            throw new RuntimeException("Unexpected modal title: " + title);
        }

        // 3. Get the patient count from the badge
        Locator countLocator = modal.locator(popupCount);
        countLocator.waitFor(new Locator.WaitForOptions()
                .setTimeout(5000)
                .setState(WaitForSelectorState.VISIBLE));

        String countText = countLocator.textContent().trim();
        int count;
        try {
            count = Integer.parseInt(countText);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse patient count from popup: '" + countText + "'");
        }

        // 4. Take screenshot before clicking
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("reports/screenshots/modal_before_click_" + System.currentTimeMillis() + ".png"))
                .setFullPage(false));

        // 5. Click "Yes, Save"
        Locator yesButton = modal.locator(popupYes);
        yesButton.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        if (!yesButton.isEnabled()) {
            throw new RuntimeException("Yes button is not enabled.");
        }

        boolean clicked = false;
        try {
            yesButton.click();
            clicked = true;
        } catch (Exception e) {
            System.out.println("Failed to click 'Yes' button: " + e.getMessage());
        }

        if (!clicked) {
            throw new RuntimeException("Yes button click did not happen.");
        } else {
            System.out.println("'Yes' button was clicked successfully.");
        }
        page.waitForTimeout(5000);

        // 6. Wait for modal to disappear
        try {
            modal.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.DETACHED)
                    .setTimeout(10000));
        } catch (Exception e) {
            System.out.println("Modal did not detach, checking if hidden...");
            try {
                modal.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(10000));
            } catch (Exception ex) {
                throw new RuntimeException("Modal did not close or hide after clicking Yes button.");
            }
        }

        // 7. Click OK button if present
        Locator ok = page.locator(okButton);
        ok.waitFor(new Locator.WaitForOptions()
                .setTimeout(10000)
                .setState(WaitForSelectorState.VISIBLE));

        if (ok.isVisible() && ok.isEnabled()) {
            ok.click();
            System.out.println("Clicked OK button successfully.");
        } else {
            System.out.println("OK button not shown. Proceeding without it.");
        }
        return count;
    }

    public int getToCount() {
        String toText = textContent(toField);
        return toText.split(",").length;
    }

    public void sendEmail(String subject, String body) {
        fill(subjectField, subject);
        fill(bodyField, body);
        Locator emailsendButton = page.locator(sendButton);
        emailsendButton.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
        if (!emailsendButton.isVisible() || !emailsendButton.isEnabled()) {
            throw new RuntimeException("Send button is not clickable.");
        }
        emailsendButton.click();
        Locator emailok = page.locator(emailAlertokButton);
        emailok.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        emailok.click(new Locator.ClickOptions().setForce(true));
    }
}