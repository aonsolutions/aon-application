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

public abstract class AbstractTestCase {
	static String url;
	static String driver;
	static String username;
	static String password;
	static Console console;
	
	static Device device;
	static PLATFORM platform;
	static WebDriver browser;
	@Rule public TestName testName = new TestName();
	
	@BeforeClass
	public static void setUpTestCase() {
		
		console 	= new Console();
		url 		= getURL();
		driver 		= getDriver();
		username 	= getUser();
		password 	= getPassword();
		device 		= getDevice();
		platform 	= getPlatform();
				
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
	public void setUp() throws Exception {
		console.start(testName.getMethodName().toUpperCase()+"");
	}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//browser.close();
	}
	
	static PLATFORM getPlatform(){
		return PLATFORM.FIREFOX;
	}
	
	static Device getDevice(){
		return Device.HD_LAPTOP;
	}		
	
	static String getURL() {
		final String URL = "http://timecontrol-solutions-test.aonsolutions.org:8080/";
		return URL;
	}
	
	static String getDriver() {
		final String FIREFOX_DRIVER = "/home/aeguzkiza/drivers/selenium/gecko/geckodriver";
		final String CHROME_DRIVER = "/home/aeguzkiza/drivers/selenium/chrome/chromedriver";
		
		return FIREFOX_DRIVER;
	}
	
	static String getUser() {
		return "usuario@aonsolutions.org";
	}
	
	static String getPassword(){
		return "org";
	}
	
}
