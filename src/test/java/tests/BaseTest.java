package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import listeners.TestListener;
import org.testng.annotations.*;
import pages.*;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Listeners(TestListener.class)
public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    private static ExtentReports extent;
    private static final String REPORT_DIR = "reports";
    private static final String STORAGE_STATE_PATH = "auth/storageState.json"; // For persistent login (optional)

    protected ExtentTest test;
    protected String tracePath;
    // === Extent ===
    public ExtentTest getTest() {
        return test;
    }

    public void setTest(ExtentTest test) {
        this.test = test;
    }

    public ExtentReports getExtent() {
        return extent;
    }

    public String getTracePath() {
        return tracePath;
    }

    public String captureScreenshot(String testName) {
        File dir = new File(REPORT_DIR + "/screenshots");
        if (!dir.exists()) dir.mkdirs();

        String screenshotPath = REPORT_DIR + "/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(screenshotPath))
                .setFullPage(true));
        return screenshotPath;
    }
    // === Reporting Setup ===
    @BeforeSuite
    public void setupReport() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String runDir = REPORT_DIR + "/run_" + timestamp;

        new File(runDir + "/screenshots").mkdirs();
        new File(runDir + "/traces").mkdirs();
        new File("auth").mkdirs(); // For storage state (if needed)

        ExtentSparkReporter reporter = new ExtentSparkReporter(REPORT_DIR + "/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    // === Browser and Context Setup ===
    @BeforeClass
    public void launchBrowserAndContext() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(300)
        );

        createContextWithLogin();
    }

    public void createContextWithLogin() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();

        File storageFile = new File(STORAGE_STATE_PATH);
        if (storageFile.exists()) {
            options.setStorageStatePath(Paths.get(STORAGE_STATE_PATH));
        }

        context = browser.newContext(options);
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();
    }

    @AfterClass
    public void closeContext() {
        String traceFileName = "trace_" + System.currentTimeMillis() + ".zip";
        tracePath = REPORT_DIR + "/traces/" + traceFileName;

        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
        context.close();
    }

    @AfterSuite
    public void flushReport() {
        if (extent != null) {
            extent.flush();
        }

        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    // === Save login to storage file ===
    public void saveLoginState() {
        File file = new File(STORAGE_STATE_PATH);
        file.getParentFile().mkdirs();
        context.storageState(new BrowserContext.StorageStateOptions()
                .setPath(Paths.get(STORAGE_STATE_PATH)));
    }

    // === Page Objects ===
    protected LoginPage loginPage() {
        return new LoginPage(page);
    }

    protected DashboardPage dashboardPage() {
        return new DashboardPage(page);
    }

    protected CommunicationModal communicationModal() {
        return new CommunicationModal(page);
    }

    protected GroupPage groupPage() {
        return new GroupPage(page);
    }
}