package solutions.aon.selenium;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.solutions.TimeControlTestCase;

public class AbstractTestCase {

	protected static String getUrl() {
		return System.getProperty("url", "http://127.0.0.1:8080");
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
		//options.addArguments("--disable-gpu"); // applicable to windows os only
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--remote-debugging-port=9222");  
		WebDriver driver = new ChromeDriver(options);
	    
		WebDriverWait wait = new WebDriverWait(driver, 10);
		return driver;
	}

	protected static WebDriver newFirefoxDriver() {
		URL chromedriverURL =  TimeControlTestCase.class.getResource("/solutions/aon/selenium/webdriver/linux64/geckodriver");
		System.setProperty("webdriver.gecko.driver", chromedriverURL.getPath());
		
		WebDriver driver = new FirefoxDriver();
	    
		WebDriverWait wait = new WebDriverWait(driver, 10);
		return driver;
	}

	protected static void login(WebDriver driver) {
		driver.get(getUrl());
		
		WebElement aonLoginUserInput = driver.findElement(By.id("aonLoginUserInput"));
		aonLoginUserInput.sendKeys(getUser());
		WebElement aonLoginPasswordInput = driver.findElement(By.id("aonLoginPasswordInput"));
		aonLoginPasswordInput.sendKeys(getPassword());
		
		WebElement aonLoginSignin = driver.findElement(By.id("aonLoginSignin"));
		aonLoginSignin.click();
		
	}

	protected static void logout(WebDriver driver) {
		WebElement aonHeaderUserButton = driver.findElement(By.id("aonHeaderUserButton"));
		aonHeaderUserButton.click();
	}

}