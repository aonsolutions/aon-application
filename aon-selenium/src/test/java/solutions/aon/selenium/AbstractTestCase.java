package solutions.aon.selenium;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AbstractTestCase {

	protected static String getUrl() {
		return System.getProperty("url", "http://127.0.0.1:8080");
	}

	protected static String getUser() {
		return System.getProperty("user", "usuario@aonsolutions.org");
	}

	protected static String getPassword() {
		return System.getProperty("password", "123456");
	}
	
	protected static boolean onGitHubActions(){
		// Check if the environment variable GITHUB_ACTIONS is set to true
		String githubActions = System.getenv("GITHUB_ACTIONS");
		return githubActions != null && githubActions.equalsIgnoreCase("true");
	}

	public static  WebDriver newWebDriver() throws MalformedURLException, URISyntaxException {
		WebDriver webDriver;
		if (onGitHubActions()) {
			// Use Remote in GitHub Actions
			webDriver = newRemoteDriver();
		} else {
			// Use Chrome or Firefox locally
			webDriver = newChromeDriver();
			// webDriver = newFirefoxDriver();
		}
		webDriver.manage().window().maximize();
		return webDriver;
	}

	public static WebDriver newChromeDriver() {
		
		
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless=new"); // 
		options.addArguments("--no-sandbox"); // Bypass OS security model MUST BE THE VERY FIRST OPTION
		options.addArguments("start-maximized"); // open Browser in maximized mode
		options.addArguments("disable-infobars"); // disabling infobars
		options.addArguments("--disable-extensions"); // disabling extensions
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--remote-debugging-port=9222");
		
		
		
		WebDriver driver = new ChromeDriver(options);
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	    
		return driver;
	}

	protected static WebDriver newFirefoxDriver() {
		
		WebDriver driver = new FirefoxDriver();
	    
		
		return driver;
	}

	protected static WebDriver newRemoteDriver() throws URISyntaxException, MalformedURLException {
		
		
		ChromeOptions chromeOptions = new ChromeOptions();
		
		RemoteWebDriver driver = new RemoteWebDriver(new URI("http://localhost:4444").toURL(), chromeOptions);
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	    
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