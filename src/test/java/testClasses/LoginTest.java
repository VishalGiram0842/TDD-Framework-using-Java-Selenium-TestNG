package testClasses;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LoginPage;
import resources.BaseClass;
import static constants.ThreadConstants.testCaseId;

public class LoginTest extends BaseClass {

	  
	@Test(retryAnalyzer = analyzer.RetryAnalyzer.class)   //set the retry logic by navigating RetryAnalyzer class
    public void testValidLogin() {
		testCaseId.set("");              // set the testrail testcase suite id to update result 
		LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertFalse(getDriver().getCurrentUrl().contains("login"));
    }
	
	@Test(retryAnalyzer = analyzer.RetryAnalyzer.class)   //set the retry logic by navigating RetryAnalyzer class
    public void testValidLoginFailed() {
		testCaseId.set("");              // set the testrail testcase suite id to update result 
		LoginPage loginPage = new LoginPage(getDriver());
	    loginPage.login("standard_user", "secret_saucep");
        Assert.assertTrue(false);
    }
}
