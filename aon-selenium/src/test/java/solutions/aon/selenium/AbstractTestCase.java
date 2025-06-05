package solutions.aon.selenium;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.solutions.TimeControlTestCase;

public class AbstractTestCase {

	protected static String getUrl() {
		return System.getProperty("url", "http://127.0.0.1:8080/app");
	}

	protected static String getUser() {
		return System.getProperty("user", "usuario@aonsolutions.org");
	}

	protected static String getPassword() {
		return System.getProperty("password", "123456");
	}

	protected static WebDriver newChromeDriver() {
//		URL chromedriverURL =  TimeControlTestCase.class.getResource("/solutions/aon/selenium/webdriver/linux64/chromedriver");
//		System.out.println(chromedriverURL.getPath());
//		System.setProperty("webdriver.chrome.driver", chromedriverURL.getPath());
		
		
		
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless=new"); // 
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
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	    
		return driver;
	}

	protected static WebDriver newFirefoxDriver() {
//		URL chromedriverURL =  TimeControlTestCase.class.getResource("/solutions/aon/selenium/webdriver/linux64/geckodriver");
//		System.setProperty("webdriver.gecko.driver", chromedriverURL.getPath());
		
		WebDriver driver = new FirefoxDriver();
	    
		
		return driver;
	}

	protected static void login(WebDriver driver) {
		login(driver, getUrl(), getUser(), getPassword() );
		
	}

	protected static void logout(WebDriver driver) {
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		WebElement aonHeaderUserButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonHeaderUserButton")));		
		aonHeaderUserButton.click();
		
		WebElement logoutOption = wait.until(ExpectedConditions.elementToBeClickable(By.id("dialogLogout")));
		logoutOption.click();
		
	}

	protected static void login(WebDriver driver, String url, String user, String password ) {
		driver.get(url);
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		WebElement aonLoginUserInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonLoginUserInput")));
		aonLoginUserInput.sendKeys(user);
		WebElement aonLoginPasswordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonLoginPasswordInput")));
		aonLoginPasswordInput.sendKeys(password);
		
		WebElement aonLoginSignin = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonLoginSignin")));
		aonLoginSignin.click();
		
	}
}