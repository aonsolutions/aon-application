package com.esferalia.aon.selenium;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.esferalia.aon.selenium.tools.Console;
import com.esferalia.aon.selenium.tools.Device;
import com.esferalia.aon.selenium.tools.SeleniumTools;
import com.esferalia.aon.selenium.tools.SeleniumTools.LOG_LEVEL;
import com.esferalia.aon.selenium.tools.SeleniumTools.PLATFORM;

/**
 * Abstract test case for Selenium tests.
 * @author aeguzkiza
 */
public abstract class AbstractTestCase {
	

	protected static String url;
	protected static String driver;
	protected static String username;
	protected static String password;
	protected static Console console;
	
	protected static Device device;
	protected static PLATFORM platform;
	protected static WebDriver browser;
	
	@Rule 
	/**
	 * Test name (Variable needed for test name log)
	 */
	public TestName testName = new TestName();
	
	
	@BeforeClass
	/**
	 * Set up the test case (Default implementation)
	 */
	public static void setUpTestDefaultData() {
		driver = "/home/aeguzkiza/drivers/selenium/gecko/geckodriver";
		url = "http://timecontrol-solutions-test.aonsolutions.org:8080/";
		username =  "usuario@aonsolutions.org";
		password =  "org";
		console 	= new Console();
		platform = platform.FIREFOX;
		device = Device.HD_LAPTOP;
	}

	
	/**
	 * Set up the test case
	 * @param newUrl - The new url to use
	 * @param newUsername - The username for login.
	 * @param newPassword - The password for login.
	 * @param newPlaftorm - The platform to use.
	 * @param newDevice - The device to use.
	 * @param newDriver - The driver for platform.
	 */
	public static void setUpTestCase(String newUrl, String newUsername, String newPassword, PLATFORM newPlaftorm, Device newDevice, String newDriver) {
		url 		= newUrl;
		driver 		= newDriver;
		username 	= newUsername;
		password 	= newPassword;
		device 		= newDevice;
		platform 	= newPlaftorm;
		browser = SeleniumTools.headLessBrowser(driver , device , platform , LOG_LEVEL.NONE);
		
		/**
		 * Getting data for starting stats
		 */
		JavascriptExecutor js = (JavascriptExecutor) browser;
		String agent = "" + js.executeScript("return navigator.userAgent","");
		agent = agent.equals("") ? "none" : agent;
	
		console.start("STATS");
		console.info("Browser", platform);
		console.info("Device", device.getName());
		console.info("User agent", agent);
		console.info("URL", url);
		console.info("User", username);
		
		try {
			
			console.start("TRYING LOGIN");
			browser.get(url);
			WebElement usernameInput = browser.findElement(By.id("aonLoginUserInput"));
			console.info("Element found" , "username input.");
			
			WebElement passwordInput = browser.findElement(By.id("aonLoginPasswordInput"));
			console.info("Element found" , "password input.");
			
			WebElement loginButtonEl  = browser.findElement(By.id("aonLoginSignin"));
			console.info("Element found" , "login button.");

			usernameInput.sendKeys(username);
			passwordInput.sendKeys(password);
			loginButtonEl.click();
			
			WebElement userIcon = browser.findElement(By.id("aonHeaderUserButtonIconButton"));
			assertTrue("AON SOLUTIONS: Incorrect login",userIcon.isDisplayed());
			console.success("login" , "DONE.");
		} 
		catch (NoSuchElementException e) {
			String message = "AON SOLUTIONS : The element does not exist";
			fail(message);
		}
		catch (WebDriverException e) {
			String message = "AON SOLUTIONS : The webapp is not responding";
			fail(message);
		}
		catch (Exception e) {
			String message = "AON SOLUTIONS : Unexpected exception";
			fail(message);
		}		
	}

	@Before
	/**
	 * Logs test name before every test is executed.
	 * @throws Exception
	 */
	public void setUp() throws Exception {
		console.start(testName.getMethodName().toUpperCase()+"");
	}

	
	@AfterClass
	/**
	 * Closes the browser after all test case is executed.
	 * @throws Exception
	 */
	public static void tearDownAfterClass() throws Exception {
		if(browser != null) browser.close();
	}
	
}
