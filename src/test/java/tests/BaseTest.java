package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import org.testng.annotations.*;
import pages.*;
import org.testng.annotations.Listeners;
import listeners.TestListener;

import java.io.File;
import java.nio.file.Paths;

@Listeners(TestListener.class)
public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected ExtentReports extent;
    protected ExtentTest test;

    public ExtentTest getTest() {
        return test;
    }

    public String captureScreenshot(String testName) {
        File dir = new File("reports/screenshots");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String screenshotPath = "reports/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(screenshotPath))
                .setFullPage(true));
        return screenshotPath;
    }

    @BeforeSuite
    public void setupReport() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("reports/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @BeforeClass
    public void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(300));
    }

    @BeforeMethod
    public void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod
    public void closeContext() {
        context.close();
    }

    @AfterSuite
    public void flushReports() {
        if (extent != null) {
            extent.flush();  // Save the report
        }
        browser.close();
        playwright.close();
    }

    protected LoginPage loginPage() { return new LoginPage(page); }
    protected DashboardPage dashboardPage() { return new DashboardPage(page); }
    protected CommunicationModal communicationModal() { return new CommunicationModal(page); }
    protected GroupPage groupPage() { return new GroupPage(page); }
}