package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

@Listeners(resources.TestNGListener.class)
public class BaseClass {
	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static final ThreadLocal<JavascriptExecutor> js = new ThreadLocal<>();
	private static final ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
	private static final ThreadLocal<Process> hubProcess = new ThreadLocal<>();
	private static final ThreadLocal<Process> nodeProcess = new ThreadLocal<>();
	private static final ThreadLocal<String> browserType = new ThreadLocal<>();

	private static final String SELENIUM_JAR_PATH = System.getProperty("user.dir")
			+ "/seleniumJar/selenium-server-4.28.0.jar";
	private static boolean isGridStarted = false;
	private static final Object gridLock = new Object();

	private static final int TIMEOUT_SECONDS = 40;
	public static Properties prop;
	public static String url;

	@BeforeSuite(alwaysRun = true)
	public void setupGrid() {
		readProperties();
		ExtentReportManager.createDirectories();
		if (Boolean.parseBoolean(getProperty("runOnGrid", "true"))) {
			synchronized (gridLock) {
				if (!isGridStarted) {
					try {
						startHub();
						TimeUnit.SECONDS.sleep(5);
						startNode();
						TimeUnit.SECONDS.sleep(5);
						isGridStarted = true;
						System.out.println("Selenium Grid hub and node started successfully");
					} catch (Exception e) {
						throw new RuntimeException("Failed to start Selenium Grid: " + e.getMessage(), e);
					}
				}
			}
		}
	}

	private void startHub() throws IOException {
		ProcessBuilder hubBuilder = new ProcessBuilder("java", "-jar", SELENIUM_JAR_PATH, "hub", "--port", "4444");
		hubBuilder.redirectErrorStream(true);
		hubProcess.set(hubBuilder.start());

		startOutputLogging(hubProcess.get(), "Hub");
	}

	private void startNode() throws IOException {
		ProcessBuilder nodeBuilder = new ProcessBuilder("java", "-jar", SELENIUM_JAR_PATH, "node", "--hub",
				"http://12.0.32.116", "--port", "5555", "--max-sessions", "15" // Allow multiple concurrent sessions
		);
		nodeBuilder.redirectErrorStream(true);
		nodeProcess.set(nodeBuilder.start());

		startOutputLogging(nodeProcess.get(), "Node");
	}

	private void startOutputLogging(Process process, String prefix) {
		new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(prefix + ": " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@AfterSuite(alwaysRun = true)
	public void shutdownGridAfterSuite() {
		if (Boolean.parseBoolean(getProperty("runOnGrid", "true"))) {
			synchronized (gridLock) {
				if (isGridStarted) {
					shutdownGrid();
					isGridStarted = false;
				}
			}
		}
	}

	private void shutdownGrid() {
		try {
			if (nodeProcess.get() != null) {
				nodeProcess.get().destroy();
				nodeProcess.get().waitFor(10, TimeUnit.SECONDS);
				if (nodeProcess.get().isAlive()) {
					nodeProcess.get().destroyForcibly();
				}
				nodeProcess.remove();
			}

			if (hubProcess.get() != null) {
				hubProcess.get().destroy();
				hubProcess.get().waitFor(10, TimeUnit.SECONDS);
				if (hubProcess.get().isAlive()) {
					hubProcess.get().destroyForcibly();
				}
				hubProcess.remove();
			}

			System.out.println("Selenium Grid hub and node shut down successfully");
		} catch (Exception e) {
			System.err.println("Error shutting down Selenium Grid: " + e.getMessage());
		}
	}

	public void readProperties() {
		prop = new Properties();
		String configFilePath = System.getProperty("user.dir") + "/Configurations/config.properties";
		loadProperties(configFilePath);
		System.out.println("Config properties from both files loaded successfully.");
		url = prop.getProperty("Url");
		System.out.println("url : " + url);
	}

	private void loadProperties(String filePath) {
		try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
			prop.load(fileInputStream);
			System.out.println("Loaded properties from: " + filePath);
		} catch (IOException e) {
			System.err.println("Failed to load properties file: " + filePath + " - " + e.getMessage());
		}
	}

	@BeforeClass(alwaysRun = true)
	public void createFolder() {
		CommonMethods.initializeFolder();
	}

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		try {
			String browserName = getProperty("browser");
			String headless = getProperty("headlessbrowser");
			// Store browser type in ThreadLocal for later use
			browserType.set(browserName.toLowerCase());
			boolean runOnGrid = Boolean.parseBoolean(getProperty("runOnGrid"));
			String gridUrl = getProperty("gridUrl", "http://localhost:4444");
			if (runOnGrid) {
				setupGridDriver(gridUrl, browserType.get(), Boolean.parseBoolean(headless));
			} else {
				setupLocalDriver(browserType.get(), Boolean.parseBoolean(headless));
			}
			initializeBrowserSettings();
		} catch (Exception e) {
			System.err.println("Failed to initialize browser: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void setupLocalDriver(String browserType, boolean headless) {
		switch (browserType) {
		case "chrome":
			driver.set(new ChromeDriver(getChromeBrowserOptions(headless)));
			break;
		case "firefox":
			driver.set(new FirefoxDriver(getFirefoxBrowserOptions(headless)));
			break;
		case "edge":
			driver.set(new EdgeDriver(getEdgeBrowserOptions(headless)));
			break;
		case "safari":
			driver.set(new SafariDriver());
			break;
		default:
			throw new IllegalArgumentException("Unsupported browser type: " + browserType);
		}
		System.out.println("Running tests locally using " + browserType);
	}

	private void setupGridDriver(String gridUrl, String browserType, boolean headless) throws MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();

		switch (browserType) {
		case "chrome":
			capabilities.setCapability(ChromeOptions.CAPABILITY, getChromeBrowserOptions(headless));
			break;
		case "firefox":
			capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, getFirefoxBrowserOptions(headless));
			break;
		case "edge":
			capabilities.setCapability("ms:edgeOptions", getEdgeBrowserOptions(headless));
			break;
		case "safari":
			capabilities.setBrowserName("safari");
			break;
		default:
			throw new IllegalArgumentException("Unsupported browser type: " + browserType);
		}

		driver.set(new RemoteWebDriver(new URL(gridUrl), capabilities));
		System.out.println("Running tests on Selenium Grid using " + browserType);
	}

	private ChromeOptions getChromeBrowserOptions(boolean headless) {
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.default_directory", CommonMethods.folderPath);
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		options.setCapability("goog:loggingPrefs", logPrefs);
		options.setExperimentalOption("prefs", prefs);
		if (headless) {
			options.addArguments("--headless", "--disable-gpu", "--window-size=1920x1080");
		}
		return options;
	}

	private FirefoxOptions getFirefoxBrowserOptions(boolean headless) {
		FirefoxOptions options = new FirefoxOptions();
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.dir", CommonMethods.folderPath);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/pdf,application/x-pdf,application/octet-stream");
		options.setProfile(profile);
		if (headless) {
			options.addArguments("-headless");
		}
		return options;
	}

	private EdgeOptions getEdgeBrowserOptions(boolean headless) {
		EdgeOptions options = new EdgeOptions();
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.default_directory", CommonMethods.folderPath);
		options.setExperimentalOption("prefs", prefs);
		if (headless) {
			options.addArguments("--headless", "--disable-gpu", "--window-size=1920x1080");
		}
		return options;
	}

	private void initializeBrowserSettings() {
		WebDriver webDriver = driver.get();
		if (webDriver != null) {
			webDriver.get(url != null ? url : "https://www.saucedemo.com/");
			webDriver.manage().window().maximize();
			webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT_SECONDS));
			wait.set(new WebDriverWait(webDriver, Duration.ofSeconds(TIMEOUT_SECONDS)));
			js.set((JavascriptExecutor) webDriver);
		} else {
			throw new RuntimeException("WebDriver instance is null");
		}
	}

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}

	@AfterTest(alwaysRun = true)
	public void deleteCreatedFolder() {
		CommonMethods.deleteFolder();
	}

	@AfterMethod(alwaysRun = true)
	public void teardown() {
		try {
			WebDriver webDriver = driver.get();
			if (webDriver != null) {
				System.out.println("Folder deleted Successfully");
				webDriver.quit();
				driver.remove();
				wait.remove();
				browserType.remove();
			}
		} catch (Exception e) {
			System.err.println("Error in teardown: " + e.getMessage());
		}
	}

	public static void takeScreenshot(String testName) {
		try {
			WebDriver webDriver = driver.get();
			if (webDriver != null) {
				File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				Path destinationPath = Path.of("ScreenShots", testName + ".png");
				Files.createDirectories(destinationPath.getParent());
				Files.copy(screenshot.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Screenshot saved at: " + destinationPath.toString());
			}
		} catch (IOException e) {
			System.out.println("Failed to take screenshot: " + e.getMessage());
		}
	}

	// Static getter methods for thread-local variables
	public static WebDriver getDriver() {
		return driver.get();
	}

	public static WebDriverWait getWait() {
		return wait.get();
	}

	public static JavascriptExecutor getJs() {
		return js.get();
	}

	public static String getBrowserType() {
		return browserType.get();
	}
}