package tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

import java.nio.file.Paths;

public class LoginTest extends BaseTest {

    @Test
    public void testLogin() {
        var test = extent.createTest("Login Test");
        loginPage().navigate();
        loginPage().login("carlos@test.com", "Admin@456");

        try {
            // Wait for navigation to complete and title to be set
            page.waitForLoadState();
            String title = page.title();
            Assert.assertTrue(title.contains("KaiCare Ai - Doctor"), "Page title should contain 'KaiCare Ai - Doctor'");
            test.pass("Login successful. Page title: " + title);
        } catch (Exception e) {
            test.fail("Login failed or dashboard title not found: " + e.getMessage());
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("reports/login-failure.png")));
            Assert.fail("Login failed or dashboard not visible.");
        }
    }
    }