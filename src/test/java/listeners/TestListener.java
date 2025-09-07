package listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import tests.BaseTest;

import java.io.File;
import java.io.IOException;

public class TestListener implements ITestListener {

    private void attachScreenshot(BaseTest testInstance, ExtentTest test, String testName, Status status) {
        String screenshotPath = testInstance.captureScreenshot(testName);

        try {
            test.addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            test.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
        }

        test.log(status, "Screenshot captured: " + testName);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        BaseTest testInstance = (BaseTest) result.getInstance();

        ExtentTest test = testInstance.getExtent().createTest(testName);
        testInstance.setTest(test);  // assign ExtentTest instance

        System.out.println("Test Started: " + testName);
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        BaseTest testInstance = (BaseTest) result.getInstance();
        ExtentTest test = testInstance.getTest();

        test.log(Status.PASS, "Test Passed: " + result.getName());
        attachScreenshot(testInstance, test, result.getName(), Status.PASS);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        BaseTest testInstance = (BaseTest) result.getInstance();
        ExtentTest test = testInstance.getTest();
        test.log(Status.FAIL, "Test Failed: " + result.getName());
        test.fail(result.getThrowable());

        // Attach screenshot
        attachScreenshot(testInstance, test, result.getName(), Status.FAIL);

        // Attach trace file (if it exists)
        String tracePath = testInstance.getTracePath();
        if (tracePath != null && new File(tracePath).exists()) {
            test.info("📎 Trace file: [View in Playwright](file:///" + new File(tracePath).getAbsolutePath() + ")");
        }
        // Optional console log
        System.err.println("Trace available at: " + tracePath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        BaseTest testInstance = (BaseTest) result.getInstance();
        ExtentTest test = testInstance.getTest();

        test.log(Status.SKIP, "Test Skipped: " + result.getName());
        if (result.getThrowable() != null) {
            test.skip(result.getThrowable());
        }

        attachScreenshot(testInstance, test, result.getName(), Status.SKIP);
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Test Suite Started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Test Suite Finished: " + context.getName());
    }
}
