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
    private final String confirmationPopup = ".modal-body"; // or more specific if needed
    private final String popupCount = ".modal-body .badge.bg-primary";
    private final String popupYes = "button.btn.btn-primary.px-4";
    private final String toField = "#toField";
    private final String subjectField = "input[formcontrolname='subject']";
    private final String bodyField = "div[aria-label='Editor editing area: main']";
    private final String sendButton = "button:has-text('Send')";
    private final String okButton = "button.swal2-confirm:has-text('OK')";
    private final String emailAlertokButton = "button.swal2-confirm";
    private final String popupModal = "div.modal-content:has(h5:has-text('Confirm Save Group'))";

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

/*    public int confirmAndGetCount() {
        waitForSelector(confirmationPopup);
        String countText = textContent(popupCount).trim();
        int count;

        try {
            count = Integer.parseInt(countText);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse patient count from confirmation popup: '" + countText + "'");
        }
        page.locator(popupYes).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.locator(popupYes).click();
        page.locator(okButton).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.locator(okButton).click();
        return count;
    }*/
    public int confirmAndGetCount() {
        // Wait for confirmation popup to appear
        waitForSelector(confirmationPopup);

        // Wait for the popupCount element to be visible and have non-empty text
        Locator countLocator = page.locator(popupCount);
        countLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
        String countText = countLocator.textContent().trim();

        int count;
        try {
            count = Integer.parseInt(countText);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse patient count from confirmation popup: '" + countText + "'");
        }
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("modal_before_click.png")));
        /*Locator yesButton = page.locator(popupYes);
        yesButton.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));

        if (!yesButton.isVisible() || !yesButton.isEnabled()) {
            throw new RuntimeException("Yes button is not visible or is disabled.");
        }
        yesButton.click(new Locator.ClickOptions().setForce(true));
        page.waitForSelector(popupModal, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(5000));*/
        Locator modal = page.locator(confirmationPopup);
        modal.waitFor(new Locator.WaitForOptions()
                .setTimeout(5000)
                .setState(WaitForSelectorState.VISIBLE));
        String title = modal.locator("h5.modal-title").innerText();
        if (!"Confirm Save Group".equals(title)) {
            throw new RuntimeException("Unexpected modal title: " + title);
        }
        Locator yesButton = modal.locator(popupYes);
        yesButton.click();

        modal.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(5000));
        Locator ok = page.locator(okButton);
        ok.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        ok.click(new Locator.ClickOptions().setForce(true));
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