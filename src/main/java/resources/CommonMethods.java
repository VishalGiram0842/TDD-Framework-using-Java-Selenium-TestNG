package resources;


import java.io.File;
import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

public class CommonMethods {

	private static final int TIMEOUT_SECONDS = 60;
	private static File parentDirectory;
	public static  String folderPath;

	private WebDriver driver;

	public CommonMethods() {
		this.driver = BaseClass.getDriver();
		PageFactory.initElements(driver, this);
	}

	public static void initializeFolder() {
		String parentDirectoryPath = System.getProperty("user.dir") + File.separator + "directory";
		parentDirectory = new File(parentDirectoryPath);
		if (!parentDirectory.exists()) {
			return;
		}
		String newFolderName = UUID.randomUUID().toString();
		File folder = new File(parentDirectory, newFolderName);
		if (folder.mkdir()) {
			folderPath = folder.getAbsolutePath();
			System.out.println("folderPath : " + folderPath);
		}
	}

	public static boolean deleteFolder(File folder) {
		if (!folder.exists()) {
			return true;
		}
		File[] contents = folder.listFiles();
		if (contents != null) {
			for (File file : contents) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		return folder.delete();
	}

	public static void deleteFolder() {
		if (!parentDirectory.exists()) {
			return;
		}
		File[] folders = parentDirectory.listFiles(File::isDirectory);
		if (folders != null) {
			for (File folder : folders) {
				deleteFolder(folder);
			}
		}
	}

	public void waitForElementToBeClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
		wait.until(ExpectedConditions.elementToBeClickable(element));
		int retries = 0;
		boolean clicked = false;
		while (retries < 3 && !clicked) {
			try {
				switch (retries) {
				case 0:
					element.click();
					clicked = true;
					break;

				case 1:
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
					clicked = true;
					break;

				case 2:
					new Actions(driver).moveToElement(element).click().perform();
					clicked = true;
					break;
				}
			} catch (ElementClickInterceptedException e) {
				retries++;
				if (retries == 3) {
					throw new RuntimeException("Element not clickable after 3 attempts.");
				}
				System.out.println("Retrying click due to overlay or blocking element...");
				WebElement spinner = driver.findElement(By.id("appSpinner"));

				wait.until(ExpectedConditions.invisibilityOf(spinner));
			}
		}
	}

	public void waitForElementToBeVisible(WebElement element) {
		int retries = 0;
		boolean isVisible = false;

		while (retries < 3 && !isVisible) {
			try {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
				wait.until(ExpectedConditions.visibilityOf(element));
				isVisible = true; // If no exception is thrown, element is visible
			} catch (TimeoutException e) {
				retries++;
				if (retries == 3) {
					throw new RuntimeException("Element not visible after 3 attempts.");
				}
				System.out.println("Retrying visibility check due to timeout...");

				// Wait for any loading indicators to disappear
				try {
					WebElement spinner = driver.findElement(By.id("appSpinner"));
					WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
					wait.until(ExpectedConditions.invisibilityOf(spinner));
				} catch (NoSuchElementException spinnerException) {
					// If spinner is not found, continue with next retry
					System.out.println("No loading indicator found, continuing with retry...");
				}
			} catch (StaleElementReferenceException e) {
				retries++;
				if (retries == 3) {
					throw new RuntimeException("Element became stale after 3 attempts.");
				}
				System.out.println("Retrying visibility check due to stale element...");
			}
		}
	}

	public void waitForElementToBeVisible(By element) {
		int retries = 0;
		boolean isVisible = false;

		while (retries < 3 && !isVisible) {
			try {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(element));
				isVisible = true; // If no exception is thrown, element is visible
			} catch (TimeoutException e) {
				retries++;
				if (retries == 3) {
					throw new RuntimeException("Element not visible after 3 attempts.");
				}
				System.out.println("Retrying visibility check due to timeout...");

				// Wait for any loading indicators to disappear
				try {
					WebElement spinner = driver.findElement(By.id("appSpinner"));
					WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
					wait.until(ExpectedConditions.invisibilityOf(spinner));
				} catch (NoSuchElementException spinnerException) {
					// If spinner is not found, continue with next retry
					System.out.println("No loading indicator found, continuing with retry...");
				}
			} catch (StaleElementReferenceException e) {
				retries++;
				if (retries == 3) {
					throw new RuntimeException("Element became stale after 3 attempts.");
				}
				System.out.println("Retrying visibility check due to stale element...");
			}
		}
	}

	public static String getLatestFilefromDir() {
		File dir = new File(folderPath);
		File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			return null;
		}

		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		String fileName = lastModifiedFile.getName();
		String completeFilePath = folderPath + File.separator + fileName;
		System.out.println("file Naming Convention:- " + fileName);
		ObjectRepo.getTest().log(Status.PASS, "file Naming Convention:- " + fileName);
		ObjectRepo.getTest().log(Status.PASS, "Report downloaded successfully.");
		return completeFilePath;
	}
}