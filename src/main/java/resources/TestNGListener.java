package resources;

import com.aventstack.extentreports.Status;

import testRailManager.TestRailManager;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;

import java.io.File;
import static constants.ThreadConstants.testCaseId;

public class TestNGListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        // Initialize ExtentReports only once at suite level
        if (ExtentReportManager.getExtent() == null) {
            ExtentReportManager.initializeExtentReports();
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        // Flush the reports at suite level
        ExtentReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        if (isTestMethod(result)) {
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();

            var test = ExtentReportManager.getExtent().createTest(testName, "Test: " + testName);
            test.assignCategory(className);
            ExtentReportManager.setCurrentTest(test);

            System.out.println("Starting test: " + testName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        var test = ExtentReportManager.getCurrentTest();
        if (isTestMethod(result) && test != null) {
            test.log(Status.PASS, "Test passed successfully");
            System.out.println("Test passed: " + result.getMethod().getMethodName());
            
            try {
                String testName = result.getMethod().getMethodName();
                BaseClass.takeScreenshot(testName + "_Passed");
                String screenshotPath = System.getProperty("user.dir") + "/ScreenShots/" + testName + "_Passed.png";
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    test.addScreenCaptureFromPath(screenshotPath);
                }
            } catch (Exception e) {
                System.out.println("Failed to attach screenshot: " + e.getMessage());
            }
            
            TestRailManager.addResultsForTestCase(testCaseId.get(), TestRailManager.TEST_CASE_PASS_STATUS,
				      ", testcase passed through script" + "  Method Name :  " + result.getName() + "  is passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        var test = ExtentReportManager.getCurrentTest();
        if (isTestMethod(result) && test != null) {
            test.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());

            // Optionally take screenshot, attach, etc.
            try {
                String testName = result.getMethod().getMethodName();
                BaseClass.takeScreenshot(testName + "_failed");
                String screenshotPath = System.getProperty("user.dir") + "/ScreenShots/" + testName + "_failed.png";
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    test.addScreenCaptureFromPath(screenshotPath);
                }
            } catch (Exception e) {
                System.out.println("Failed to attach screenshot: " + e.getMessage());
            }
            TestRailManager.addResultsForTestCase(testCaseId.get(), TestRailManager.TEST_CASE_FAIL_STATUS,
	       			", testcase failed through script" + "   Method Name :  " + result.getName() + "is Failed");
            System.out.println("Test failed: " + result.getMethod().getMethodName());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        var test = ExtentReportManager.getCurrentTest();
        if (isTestMethod(result) && test != null) {
            String skipMessage = result.getThrowable() != null
                    ? "Test skipped due to: " + result.getThrowable().getMessage()
                    : "Test skipped.";
            test.log(Status.SKIP, skipMessage);
            System.out.println("Test skipped: " + result.getMethod().getMethodName());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // Clean up thread local
        ExtentReportManager.setCurrentTest(null);
    }

    private boolean isTestMethod(ITestResult result) {
        return result.getMethod().isTest();
    }
    
}
