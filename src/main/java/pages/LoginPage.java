package pages;

import com.microsoft.playwright.Page;
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
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(60000));
    }

    public void login(String username, String password) {
        fill(emailField, username);
        fill(passwordField, password);
        click(loginButton);
    }
}