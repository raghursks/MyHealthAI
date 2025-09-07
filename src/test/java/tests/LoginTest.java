package tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;

public class LoginTest extends BaseTest {
    @Test(groups = "login")
    public void testLogin() {

        try {
            loginPage().navigate();
            loginPage().login("carlos@test.com", "Admin@456");

            page.waitForFunction("() => document.title.includes('KaiCare Ai - Doctor')", null,
                    new Page.WaitForFunctionOptions().setTimeout(10000));

            String title = page.title();
            System.out.println("Page title: " + title);

            Assert.assertTrue(title.contains("KaiCare Ai - Doctor"), "Expected title not found.");
            test.pass("Login successful. Page title: " + title);

        } catch (Exception e) {
            test.fail("Login failed or dashboard not visible: " + e.getMessage());

            String screenshotPath = captureScreenshot("login-failure");
            test.addScreenCaptureFromPath(screenshotPath);

            String tracePath = getTracePath();
            if (tracePath != null && new File(tracePath).exists()) {
                test.info("Trace file: [View in Playwright](file:///" + new File(tracePath).getAbsolutePath() + ")");
            }

            Assert.fail("Login failed or dashboard not visible.", e);
        }

            // Screenshot
            String screenshotPath = captureScreenshot("login-failure");
            if (test != null) {
                test.addScreenCaptureFromPath(screenshotPath);
            }

            // Trace
            String tracePath = getTracePath();
            if (tracePath != null && new File(tracePath).exists()) {
                String traceLink = "file:///" + new File(tracePath).getAbsolutePath();
                if (test != null) {
                    test.info("Trace file: [View in Playwright](" + traceLink + ")");
                }
                System.out.println("Trace saved at: " + traceLink);
            }
        }
    }