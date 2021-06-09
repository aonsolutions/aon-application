package solutions.aon.selenium;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.solutions.TimeControlTestCase;

public class AbstractTestCase {

	protected static String getUrl() {
		return System.getProperty("url", "http://127.0.0.1:8080/");
	}

	protected static String getUser() {
		return System.getProperty("user", "usuario@aonsolutions.org");
	}

	protected static String getPassword() {
		return System.getProperty("password", "org");
	}

	protected static WebDriver newChromeDriver() {
		URL chromedriverURL =  TimeControlTestCase.class.getResource("/solutions/aon/selenium/webdriver/linux64/chromedriver");
		System.setProperty("webdriver.chrome.driver", chromedriverURL.getPath());
		
		
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox"); // Bypass OS security model MUST BE THE VERY FIRST OPTION
		options.addArguments("start-maximized"); // open Browser in maximized mode
		options.addArguments("disable-infobars"); // disabling infobars
		options.addArguments("--disable-extensions"); // disabling extensions
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--remote-debugging-port=9222");
		
//		Map<String, Object> prefs = new HashMap<String, Object>();
//		prefs.put("download.default_directory", "/home/igonzalez/a/");
//		options.setExperimentalOption("prefs", prefs);
		
		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    
		return driver;
	}

	protected static WebDriver newFirefoxDriver() {
		URL chromedriverURL =  TimeControlTestCase.class.getResource("/solutions/aon/selenium/webdriver/linux64/geckodriver");
		System.setProperty("webdriver.gecko.driver", chromedriverURL.getPath());
		
		WebDriver driver = new FirefoxDriver();
	    
		
		return driver;
	}

	protected static void login(WebDriver driver) {
		driver.get(getUrl());
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement aonLoginUserInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonLoginUserInput")));
		aonLoginUserInput.sendKeys(getUser());
		WebElement aonLoginPasswordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonLoginPasswordInput")));
		aonLoginPasswordInput.sendKeys(getPassword());
		
		WebElement aonLoginSignin = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonLoginSignin")));
		aonLoginSignin.click();
		
	}

	protected static void logout(WebDriver driver) {
		
		WebDriverWait wait = new WebDriverWait(driver, 20);
		WebElement aonHeaderUserButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonHeaderUserButton")));		
		aonHeaderUserButton.click();
		
		WebElement logoutOption = wait.until(ExpectedConditions.elementToBeClickable(By.id("dialogLogout")));
		logoutOption.click();
		
	}

}