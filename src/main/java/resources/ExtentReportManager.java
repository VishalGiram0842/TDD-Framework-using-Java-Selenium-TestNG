package resources;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    public static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static final String REPORT_PATH = System.getProperty("user.dir") + "/ExecutionReports";
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/ScreenShots";

    public static void initializeExtentReports() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String reportName = "ExtentReport_" + timeStamp + ".html";
        String reportPath = System.getProperty("user.dir") + "/ExecutionReports/" + reportName;

        createDirectories();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Automation Test Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");

        System.out.println("ExtentReports initialized. Report will be saved at: " + reportPath);
    }

    public static ExtentReports getExtent() {
        return extent;
    }

    public static ExtentTest getCurrentTest() {
        return extentTest.get();
    }
    
    public static void setCurrentTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void createDirectories() {
        try {
            File reportDir = new File(REPORT_PATH);
            File screenshotDir = new File(SCREENSHOT_PATH);
            if (reportDir.exists()) {
                FileUtils.cleanDirectory(reportDir);
            }
            if (screenshotDir.exists()) {
                FileUtils.cleanDirectory(screenshotDir);
            }
            FileUtils.forceMkdir(reportDir);
            FileUtils.forceMkdir(screenshotDir);

            logger.info("Cleared and recreated report and screenshot directories.");
        } catch (IOException e) {
            logger.error("Failed to create/clean directories", e);
        }
    }
}
