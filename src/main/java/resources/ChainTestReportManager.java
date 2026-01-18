package resources;

import org.chaintest.ChainTestReport;
import org.chaintest.ChainTestReportException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChainTestReportManager {

    private static ChainTestReport chainTestReport;
    private static ThreadLocal<ChainTestReport> chainTestReportThread = new ThreadLocal<>();
    public static final Logger logger = LogManager.getLogger(ChainTestReportManager.class);
    private static final String REPORT_PATH = System.getProperty("user.dir") + "/ExecutionReports";
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/ScreenShots";

    public static void initializeChainTestReport() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String reportName = "ChainTestReport_" + timeStamp + ".html";
        String reportPath = System.getProperty("user.dir") + "/ExecutionReports/" + reportName;
        
        createDirectories();
        
        try {
            chainTestReport = new ChainTestReport(reportPath);
            chainTestReport.setDocumentTitle("Automation Test Report");
            chainTestReport.setReportName("Test Execution Report");
            chainTestReport.setSystemInfo("OS", System.getProperty("os.name"));
            chainTestReport.setSystemInfo("Java Version", System.getProperty("java.version"));
            chainTestReport.setSystemInfo("User", System.getProperty("user.name"));
            chainTestReport.setSystemInfo("Environment", "QA");
            System.out.println("ChainTestReport initialized. Report will be saved at: " + reportPath);
        } catch (ChainTestReportException e) {
            logger.error("Failed to initialize ChainTestReport", e);
            throw new RuntimeException("Failed to initialize ChainTestReport", e);
        }
    }

    public static ChainTestReport getChainTestReport() {
        return chainTestReport;
    }

    public static ChainTestReport getCurrentTest() {
        return chainTestReportThread.get();
    }

    public static void setCurrentTest(ChainTestReport test) {
        chainTestReportThread.set(test);
    }

    public static void flushReports() {
        if (chainTestReport != null) {
            try {
                chainTestReport.generateReport();
            } catch (ChainTestReportException e) {
                logger.error("Failed to generate ChainTestReport", e);
            }
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
