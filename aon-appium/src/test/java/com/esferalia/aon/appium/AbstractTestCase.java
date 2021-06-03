package com.esferalia.aon.appium;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.appium.id.AonIdHeader;
import com.esferalia.aon.appium.id.AonIdLogin;
import com.esferalia.aon.appium.tools.AppiumTools;
import com.esferalia.aon.appium.tools.Console;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.service.local.AppiumDriverLocalService;

public abstract class AbstractTestCase {

	protected static String apk;
	protected static String driver;
	protected static String username;
	protected static String password;
	protected static Console console;
	protected static AppiumDriver<MobileElement> app;
    protected static AppiumDriverLocalService appiumService;

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

		driver = AbstractTestCase.class.getResource("chromedriver").getPath();
		apk = AbstractTestCase.class.getResource("aon-test.apk").getPath();
		username = "usuario@aonsolutions.org";
		password = "org";

		console = new Console();
	}

	/**
	 * Set up the test case
	 * 
	 * @param newUrl      - The new url to use
	 * @param newUsername - The username for login.
	 * @param newPassword - The password for login.
	 * @param newPlaftorm - The platform to use.
	 * @param newDevice   - The device to use.
	 * @param newDriver   - The driver for platform.
	 */
	public static void setUpTestCase(String newApk, String newUsername, String newPassword, String newDriver) {
		
		appiumService = AppiumDriverLocalService.buildDefaultService();
		appiumService.start();
		
		apk = newApk;
		driver = newDriver;
		username = newUsername;
		password = newPassword;
		
		
		String appiumServiceUrl = appiumService.getUrl().toString();
		try {
			app = AppiumTools.getDriverAndroid(apk, driver, appiumServiceUrl);
		} catch (MalformedURLException e1) {
			fail("Cannot start app.");
		}
		app.setLogLevel(Level.SEVERE);
		app.manage().timeouts().implicitlyWait(150, TimeUnit.SECONDS);
		
        
		
		
		/**
		 * Getting data for starting stats
		 */
		console.start("STATS");
		console.info("Apk", apk);
		console.info("User", username);
		console.info("Password", password);
		console.info("Appium Service", appiumServiceUrl);

		try {
			console.start("TRYING LOGIN");
			app.findElementsByClassName("android.webkit.WebView");

			WebDriverWait wait = new WebDriverWait(app, 50);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("android.webkit.WebView")));
			List<MobileElement> mList = app.findElements(By.className("android.webkit.WebView"));
			Set<String> contextNames = app.getContextHandles();

			for (String context : contextNames) {
				System.out.println("Context: " + context);
			}
			String currentContext = (String) contextNames.toArray()[1];
			System.out.println("CurrentContext: " + currentContext);
			app.context(currentContext);
			
			app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

			WebElement usernameInput = app.findElement(By.id(AonIdLogin.USER_INPUT));
			WebElement passwordInput = app.findElement(By.id(AonIdLogin.PASSWORD_INPUT));
			System.out.println(usernameInput);

//			String username = "usuario@aonsolutions.org";
//			String password = "org";

			wait.until(ExpectedConditions.visibilityOf(usernameInput));
			usernameInput.click();
			app.hideKeyboard();
			usernameInput.sendKeys(username);
			
			WebDriverWait wait2 = new WebDriverWait(app, 2);
			
			do {
			AppiumTools.fillInput(app, "#"+AonIdLogin.PASSWORD_INPUT, "");
			passwordInput.click();
			app.hideKeyboard();
			passwordInput.sendKeys(password);
			} while (!passwordInput.getAttribute("value").equals(password));
			
			
			
			Thread.sleep(1000);
		
			WebElement loginButtonEl = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdLogin.LOGIN_SUBMIT)));
			
			
			loginButtonEl.click();
			
			
			WebElement userIcon = wait2.until(ExpectedConditions.presenceOfElementLocated(By.id(AonIdHeader.USER_BUTTON)));
			if (!userIcon.isDisplayed())
				fail("User icon didn't load properly");

		} catch (NoSuchElementException e) {
			String message = "AON SOLUTIONS : The element does not exist";
			e.printStackTrace();
			fail(message);
		} catch (WebDriverException e) {			
			String message = "AON SOLUTIONS : The webapp is not responding";
			e.printStackTrace();
			fail(message);
		} catch (Exception e) {
			String message = "AON SOLUTIONS : Unexpected exception";
			e.printStackTrace();
			fail(message);
		}
	}

	@Before
	/**
	 * Logs test name before every test is executed.
	 * 
	 * @throws Exception
	 */
	public void setUp() throws Exception {
		console.start(testName.getMethodName().toUpperCase() + "");
	}

	@AfterClass
	/**
	 * Closes the browser after all test case is executed.
	 * 
	 * @throws Exception
	 */
	public static void tearDownAfterClass() throws Exception {
		app.resetApp();
		app.quit();
		appiumService.stop();
	}

}
