package resources;

import org.chaintest.TestResult;
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
        // Initialize ChainTestReport only once at suite level
        if (ChainTestReportManager.getChainTestReport() == null) {
            ChainTestReportManager.initializeChainTestReport();
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        // Flush the reports at suite level
        ChainTestReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        if (isTestMethod(result)) {
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();
            System.out.println("Starting test: " + testName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (isTestMethod(result)) {
            String testName = result.getMethod().getMethodName();
            System.out.println("Test passed: " + testName);
            
            try {
                BaseClass.takeScreenshot(testName + "_Passed");
                String screenshotPath = System.getProperty("user.dir") + "/ScreenShots/" + testName + "_Passed.png";
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    // Log test pass to ChainTest
                }
            } catch (Exception e) {
                System.out.println("Failed to attach screenshot: " + e.getMessage());
            }
            
            TestRailManager.addResultsForTestCase(testCaseId.get(), TestRailManager.TEST_CASE_PASS_STATUS,
                ", testcase passed through script" + " Method Name : " + result.getName() + " is passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (isTestMethod(result)) {
            String testName = result.getMethod().getMethodName();
            System.out.println("Test failed: " + testName);
            System.out.println("Failure reason: " + result.getThrowable().getMessage());
            
            try {
                BaseClass.takeScreenshot(testName + "_failed");
                String screenshotPath = System.getProperty("user.dir") + "/ScreenShots/" + testName + "_failed.png";
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    // Log test failure to ChainTest
                }
            } catch (Exception e) {
                System.out.println("Failed to attach screenshot: " + e.getMessage());
            }
            
            TestRailManager.addResultsForTestCase(testCaseId.get(), TestRailManager.TEST_CASE_FAIL_STATUS,
                ", testcase failed through script" + " Method Name : " + result.getName() + " is Failed");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (isTestMethod(result)) {
            String testName = result.getMethod().getMethodName();
            String skipMessage = result.getThrowable() != null
                ? "Test skipped due to: " + result.getThrowable().getMessage()
                : "Test skipped.";
            System.out.println("Test skipped: " + testName + " - " + skipMessage);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // Clean up thread local
        ChainTestReportManager.setCurrentTest(null);
    }

    private boolean isTestMethod(ITestResult result) {
        return result.getMethod().isTest();
    }
}
