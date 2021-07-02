package solutions.aon.selenium.tools;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Toolkit for easy java test
 * with selenium API
 * 
 * @author akrck02
 */
public class SeleniumTools {

	/**
	 * Web platforms
	 */
	public static enum PLATFORM {
		FIREFOX("webdriver.gecko.driver"),
		EDGE("webdriver.edge.driver"),
		CHROME("webdriver.chrome.driver"),
		SAFARY("");
		
		private String driverName;
		
		private PLATFORM(String driverName) {
			this.driverName = driverName;
		}
		
		public String getDriverName() {
			return this.driverName;
		}
	}
	
	/**
	 * Log level
	 */
	public static enum LOG_LEVEL {
		INFO,
		WARNING,
		ERROR,
		FATAL,
		NONE
	}

	/**
	 * Headless browser 
	 * @param driver - The driver PATH
	 * @param platform - The platform to get
	 * @return browser
	 */
	public static WebDriver headLessBrowser(String driver, Device device, PLATFORM platform, LOG_LEVEL level) {

		switch (platform) {
		case FIREFOX:
			return headlessFirefox(driver,device,level);
		case EDGE:
			return headlessEdge(driver,device,level);
		case SAFARY:
			return headlessSafari(driver,device,level);
		default:
			return headlessChrome(driver,device,level);
		}

	}

	/**
	 * Chrome headless driver.
	 * @param driver - The driver path
	 * @return Chrome 
	 */
	private static ChromeDriver headlessChrome(String driver, Device device, LOG_LEVEL level) {
		
		System.setProperty(PLATFORM.CHROME.getDriverName(), driver);
		ChromeOptions options = new ChromeOptions();		
		
		/**
		 * Set browser options
		 */
		options.setHeadless(true);
		options.addArguments("--disable-gpu"); 
		options.addArguments("--start-maximized"); 
		options.addArguments("--disable-infobars"); 
		options.addArguments("--disable-extensions"); 
		options.addArguments("--disable-dev-shm-usage"); 
		options.addArguments("--no-sandbox"); 
		options.addArguments("--user-agent=" + device.getUserAgent());		
		options.addArguments("--width=" + device.getWidth());			
		options.addArguments("--height=" + device.getHeight());			
		options.addArguments("--pixelRatio=" + device.getPixelRatio());	
		
		/**
		 * Disable Image loading
		 */
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.managed_default_content_settings.images", 2);
		options.setExperimentalOption("prefs", prefs);
		
		
		/**
		 * Setting log level
		 */
		if(level == LOG_LEVEL.NONE) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
		}else {
			options.addArguments("--log-level=" + level.ordinal());		
		}
		
		/**
		 * Building chrome driver
		 */
		ChromeDriver chrome = new ChromeDriver(options);
		return chrome;
	}

	/**
	 * Firefox headless driver.
	 * @param driver - The driver path
	 * @return Firefox 
	 */
	private static FirefoxDriver headlessFirefox(String driver, Device device, LOG_LEVEL level) {
		
		System.setProperty(PLATFORM.FIREFOX.getDriverName(), driver);
		FirefoxOptions options = new FirefoxOptions();
		
		/**
		 * Setting userAgent , blocking image loading and disabling CSS.
		 */
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("general.useragent.override", device.getUserAgent()); 
		profile.setPreference("permissions.default.image", 2);
		profile.setPreference("permissions.default.stylesheet", 2);
		profile.setPreference("permissions.default.microphone", 1);
		profile.setPreference("permissions.default.camera", 1);
		profile.setPreference("permissions.default.geolocation", 1);
		
		/**
		 * Setting firefox options
		 */
		options.setProfile(profile);
		options.setHeadless(true);	
		options.addArguments("--width=" + device.getWidth());			
		options.addArguments("--height=" + device.getHeight());			
		options.addArguments("--pixelRatio=" + device.getPixelRatio());		
		
		/**
		 * Setting log level
		 */
		if(level == LOG_LEVEL.NONE) {
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
		}else {
			options.addArguments("--log-level=" + level.ordinal());		
		}
		
		/**
		 * Building firefox
		 */
		FirefoxDriver firefox = new FirefoxDriver(options);
		return firefox;
	}
	

	/**
	 * Edge headless driver.
	 * @param driver - The driver path
	 * @return Edge 
	 */
	private static EdgeDriver headlessEdge(String driver, Device device, LOG_LEVEL level) {
		throw new RuntimeException("Not Implemented yet");
	}

	/**
	 * Safari headless driver.
	 * @param driver - The driver path
	 * @return Safari 
	 */
	private static SafariDriver headlessSafari(String driver, Device device, LOG_LEVEL level) {
		throw new RuntimeException("Not Implemented yet");
	}	
	
	
	/**
	 * Fake location 
	 */
	public static void setFakeLocation(WebDriver browser) {
		String script = 
				"window.navigator.geolocation.getCurrentPosition = function(success){"
				+ "var position = {"
						+ "'coords\' : {'latitude': '42.84998', 'longitude': ' 	-2.67268'}"
				+ "};"
				+ "success(position);"
				+ "}";
	
		JavascriptExecutor js = (JavascriptExecutor) browser;
		js.executeScript(script);
	}
	
	//The way to get elements clicked without random 'StaleElementReferenceException' exceptions
	public static boolean retryingFindClick(WebDriver driver, By by) {
	    boolean result = false;
	    int attempts = 0;
	    while(attempts < 4) {
	        try {
	            driver.findElement(by).click();
	            result = true;
	            break;
	        } catch(StaleElementReferenceException e) {
	        }
	        attempts++;
	    }
	    return result;
	}
		
	public static void waitNClick(WebDriver app, By by) {
		waitNClick(app,by, 10);
	}
	
	public static void waitNClick(WebDriver app, By by, int customTimeout) {
		WebDriverWait wait = new WebDriverWait(app, customTimeout);
		wait.until(ExpectedConditions.elementToBeClickable(by));
		retryingFindClick(app, by);
	}
	
	public static String getDownloadPath() {
		String downloadFolder = System.getProperty("user.home") + File.separator + "Descargas";
		
		Path downloadPath = Paths.get(downloadFolder);
		
		if (!Files.exists(downloadPath))
			downloadFolder = System.getProperty("user.home") + File.separator + "Downloads";
		
		return downloadFolder;
	}
	
	public static boolean clickUntilNotExists(WebDriver app, WebElement elem) {
	    boolean result = false;
	    int attempts = 0;
	    while(attempts < 8) {
	    	try {
	            elem.click();
	    	} catch (Exception e) {
	    		result = true;
	    		break;
	    	}
	        attempts++;
	    }
	    return result;
	}
	
	
	public static boolean clickUntilNotExists(WebDriver app, By by) {
	    boolean result = false;
	    int attempts = 0;
	    WebElement elem = new WebDriverWait(app, 10).until(ExpectedConditions.elementToBeClickable(by));
	    while(attempts < 8) {
	    	try {
	            elem.click();
	    	} catch (Exception e) {
	    		result = true;
	    		break;
	    	}
	        attempts++;
	    }
	    return result;
	}
	
	public static boolean checkAmount(Double actualAmount, Double expectedAmount) {
		expectedAmount = Math.round(expectedAmount*100.0) / 100.0;
		return actualAmount.equals(expectedAmount);
	}
	
	
	public static Double getAmount(WebDriver driver, By selector) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement baseSalaryAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		
		String baseSalaryStr = baseSalaryAmount.getAttribute("value");
		Double baseSalary = null;
		try {
			baseSalaryStr = baseSalaryStr.replaceAll("\\.", "").replaceAll(",", ".").replaceAll("%", "").trim();
			baseSalary = Double.parseDouble(baseSalaryStr);
			
			return baseSalary;
			
		} catch (NullPointerException | NumberFormatException e) {
			try {
				baseSalaryStr = baseSalaryAmount.getAttribute("innerText");
				baseSalaryStr = baseSalaryStr.replaceAll("\\.", "").replaceAll(",", ".").replaceAll("%", "").trim();
				baseSalary = Double.parseDouble(baseSalaryStr);
				return baseSalary;
			} catch (NullPointerException | NumberFormatException e1) {
				return null;
			}
		}
		
	}
	
	public static boolean waitUntilElementContains(WebDriver driver, By selector, String text) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		
		String content = element.getAttribute("innerText");
		
		content = content != null ? content.trim() : "";
		
		for (int i=0; i<10; i++) {
			Thread.sleep(1000);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
			content = element.getAttribute("innerText");
			if (content.equalsIgnoreCase(text))
				return true;
		}
		
		return false;
		
	}

	public static Double unmessDouble(Double number) {
		if (number != null)
			return Math.round(number * 100.0) / 100.0;
		return null;
	}

	
}
