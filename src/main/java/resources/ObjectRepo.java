package resources;

import com.aventstack.extentreports.ExtentTest;

public class ObjectRepo {
    
    public static ExtentTest getTest() {
        return ExtentReportManager.getCurrentTest();
    }
}