package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void click(String selector) {
        page.click(selector);
    }

    public String textContent(String selector) {
        return page.textContent(selector);
    }

    public int countRows(String tableSelector) {
        return page.querySelectorAll(tableSelector + " tbody tr").size();
    }

    public void waitForSelector(String selector) {
        page.waitForSelector(selector);
    }
    public void fill(String selector, String text) {
        Locator element = page.locator(selector);
        element.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));

        // Check if it's a contenteditable element
        boolean isContentEditable = element.evaluate("el => el.hasAttribute('contenteditable')").toString().equals("true");

        if (isContentEditable) {
            // Clear and type into contenteditable
            element.click(); // Focus the element
            page.keyboard().press("Control+A"); // Select all existing content
            page.keyboard().press("Backspace"); // Clear existing content
            page.keyboard().type(text);         // Type new content
        } else {
            // Regular input or textarea
            element.fill(text);
        }
    }

}