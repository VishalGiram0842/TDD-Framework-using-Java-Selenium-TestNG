package resources;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import com.aventstack.extentreports.Status;

public class CommonLib {

	private WebDriver driver;

	public CommonLib() {
		this.driver = BaseClass.getDriver();
		PageFactory.initElements(driver, this);
	}

	public  void click(WebElement element, String clickType, String elementName) {
		try {
			switch (clickType) {
			case "seleniumClick":
				element.click();
				break;
			case "javascriptClick":
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", element);
				break;
			case "moveToElementClick":
				Actions actions = new Actions(driver);
				actions.doubleClick(element).perform();
				break;
			default: // default clause should be the last one
				break;
			}
			ObjectRepo.getTest().log(Status.PASS, elementName + " has been clicked");
			ObjectRepo.getTest().log(Status.INFO, elementName + " has been clicked");
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void scroll(WebElement element, String scrollType, String elementName) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			switch (scrollType) {
			case "scrollToElement":
				js.executeScript("arguments[0].scrollIntoView();", element);
				break;
			case "scrollToBottomOFPage":
				js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
				break;
			case "scrollToTopOFPage":
				js.executeScript("window.scrollTo(0, -document.body.scrollHeight);");
				break;
			default: // default clause should be the last one
				break;
			}
			ObjectRepo.getTest().log(Status.INFO, elementName + " has been scrolled");
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public void typeText(WebElement element, String value, String elementName) {
		try {
			element.clear();
			element.sendKeys(value);
			ObjectRepo.getTest().log(Status.PASS, elementName + " has been entered");
			ObjectRepo.getTest().log(Status.INFO, elementName + " has been entered");
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	public String getTextElement(WebElement element) {
		try {
			return (element.getText());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void isDisplayed(WebElement element, String elementName) {
		boolean actualResult = element.isDisplayed();
		if (actualResult) {
			ObjectRepo.getTest().log(Status.PASS, elementName + " has not been displayed");
			ObjectRepo.getTest().log(Status.INFO, elementName + " has not been displayed!");
			Assert.assertFalse(false, elementName + " has not been displayed!!");
		} else {
			ObjectRepo.getTest().log(Status.PASS, elementName + " has been displayed!!!");
			ObjectRepo.getTest().log(Status.INFO, elementName + " has been displayed!!!");
		}

	}

	public void isEnabled(WebElement element, String elementName) {
		boolean actualResult = element.isEnabled();
		if (actualResult) {
			ObjectRepo.getTest().log(Status.PASS, elementName + " has not been enabled!");
			Assert.assertFalse(false, elementName + " has not been enabled!!");
		} else {
			ObjectRepo.getTest().log(Status.PASS, elementName + " has been enabled!!!");
			ObjectRepo.getTest().log(Status.INFO, elementName + " has been enabled!!!");
		}
	}

	public void verifyUrl(String actualUrl, String expectedUrl, String message) {
		Assert.assertEquals(actualUrl, expectedUrl, message);
		ObjectRepo.getTest().log(Status.PASS, message);
		ObjectRepo.getTest().log(Status.INFO, message);
	}

	public void verifyTextTypeInt(int actualValue, int expectedValue, String message) {
		Assert.assertEquals(actualValue, expectedValue, message);
		ObjectRepo.getTest().log(Status.PASS, message);
		ObjectRepo.getTest().log(Status.INFO, message);
	}

	public void selectValueFromSelectDropDown(WebElement element, String elementName, String value) {
		try {
			Select ele = new Select(element);
			ele.selectByValue(value);
			ObjectRepo.getTest().log(Status.PASS, elementName + " has been select");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void verifyTextTypeString(String actualString, String expectedString, String message) {
		Assert.assertEquals(actualString, expectedString, message);
		ObjectRepo.getTest().log(Status.PASS, message);
		ObjectRepo.getTest().log(Status.INFO, message);
	}

	public void selectIndexFromDropDown(WebElement element, String elementName1, int index) {
		try {
			Select ele = new Select(element);
			ele.selectByIndex(index);
			ObjectRepo.getTest().log(Status.PASS, elementName1 + " has been selected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectVisibleTextFromDropDown(WebElement element, String elementName2, String visibleText) {
		try {
			Select ele = new Select(element);
			ele.selectByVisibleText(visibleText);
			ObjectRepo.getTest().log(Status.PASS, elementName2 + " has been selected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSelectedTextFromDropDown(WebElement element, String elementName) {
		String dropValue = null;
		try {
			Select ele = new Select(element);
			dropValue = ele.getFirstSelectedOption().getText();
			ObjectRepo.getTest().log(Status.PASS, elementName + " is selected");
			ObjectRepo.getTest().log(Status.INFO, elementName + " is not selected");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dropValue;
	}

	public void isDisabled(WebElement element, String elementName) {
		boolean actualResult = element.isEnabled();
		if (actualResult) {
			ObjectRepo.getTest().log(Status.PASS, elementName + " is disabled!!!");
			ObjectRepo.getTest().log(Status.INFO, elementName + " is disabled!!!");

		} else {
			ObjectRepo.getTest().log(Status.PASS, elementName + " is not disabled!!!");
			Assert.fail(elementName + " is not disabled!!!");
		}
	}

	public void switchToIframe(String elementName, int n) {
		int iframeindex = driver.findElements(By.tagName("iframe")).size();
		if (iframeindex >= 0) {
			driver.switchTo().frame(n);
			ObjectRepo.getTest().log(Status.PASS, elementName + "iframe is present");
			ObjectRepo.getTest().log(Status.INFO, elementName + "Switch to iframe");

		} else {
			ObjectRepo.getTest().log(Status.PASS, elementName + "iframe is not present");
			ObjectRepo.getTest().log(Status.INFO, elementName + "Unable to switch to iframe");
		}
	}

	public void eventDropDownHandling(String eventtName, List<WebElement> element) {
		String eveName = getEventName(eventtName, element);
		if (eventtName.equalsIgnoreCase(eveName)) {
			ObjectRepo.getTest().log(Status.PASS, eventtName + "Event is present in the event list");
			ObjectRepo.getTest().log(Status.INFO, eventtName + "Event is present in the event list");
		} else {
			Assert.fail("Event not found");
			ObjectRepo.getTest().log(Status.PASS, eventtName + "Event is not present in the event list");
			ObjectRepo.getTest().log(Status.INFO, eventtName + "Event is not present in the event list");
		}

	}

	public String getEventName(String eventtName, List<WebElement> element) {
		String event = null;
		List<WebElement> events = element;
		// Iterating through the list selecting the desire option
		for (int j = 0; j < events.size(); j++) {

			if (events.get(j).getText().equals(eventtName)) {
				event = eventtName;
				events.get(j).click();
				break;
			}

		}
		return event;
	}
}
