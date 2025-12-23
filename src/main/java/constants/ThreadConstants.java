package constants;

import org.openqa.selenium.WebDriver;

public class ThreadConstants {

	public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	public static ThreadLocal<String> testCaseId = new ThreadLocal<>();
}
 