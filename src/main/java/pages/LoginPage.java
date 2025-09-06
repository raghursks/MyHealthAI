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
    	Page.NavigateOptions navOptions = new Page.NavigateOptions().setWaitUntil(WaitUntilState.LOAD).setTimeout(60000);
        page.navigate(url, navOptions);
    }

    public void login(String username, String password) {
        fill(emailField, username);
        fill(passwordField, password);
        click(loginButton);
    }
}