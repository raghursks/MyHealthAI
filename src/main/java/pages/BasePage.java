package pages;

import com.microsoft.playwright.Page;

public class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void click(String selector) {
        page.click(selector);
    }

    public void fill(String selector, String text) {
        page.fill(selector, text);
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
}