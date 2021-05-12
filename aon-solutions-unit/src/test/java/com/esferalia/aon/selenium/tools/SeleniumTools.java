package com.esferalia.aon.selenium.tools;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.safari.SafariDriver;

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
}
