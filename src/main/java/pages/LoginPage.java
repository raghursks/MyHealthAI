package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitUntilState;

public class LoginPage extends BasePage {
    private final String emailField = "#email";
    private final String passwordField = "#password-input";
    private final String loginButton = "button[type='submit']";
    private final String url = "https://doctorstaging.myhealthai.io/";

    public LoginPage(Page page) {
        super(page);
    }
    public void navigate() {
        Page.NavigateOptions navOptions = new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.LOAD)
                .setTimeout(240000);

        System.out.println("Navigating to: " + url);
        long start = System.currentTimeMillis();

        try {
            page.navigate(url, navOptions);
            long end = System.currentTimeMillis();
            System.out.println("Navigation took: " + (end - start) + "ms");

            // Wait for login button to ensure page is usable
            page.waitForSelector(loginButton, new Page.WaitForSelectorOptions().setTimeout(30000));
        } catch (TimeoutError e) {
            System.out.println("Navigation or login button failed to load. Retrying...");

            // Retry once in case of a transient failure
            try {
                page.reload(new Page.ReloadOptions()
                        .setWaitUntil(WaitUntilState.LOAD)
                        .setTimeout(30000));

                page.waitForSelector(loginButton, new Page.WaitForSelectorOptions().setTimeout(30000));
            } catch (TimeoutError retryError) {
                System.out.println("Retry also failed. Giving up.");
                throw retryError;
            }
        }
    }
    public void login(String username, String password) {
        fill(emailField, username);
        fill(passwordField, password);
        click(loginButton);
    }
}