package com.esferalia.aon.appium.tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class AppiumTools {

	public static AppiumDriver<MobileElement> getDriverAndroid(String apk,String driver, String appiumServiceUrl) throws MalformedURLException{
		
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "android11");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
		capabilities.setCapability(MobileCapabilityType.APP, apk);
		capabilities.setCapability("chromedriverExecutable", driver);
		capabilities.setCapability("gpsEnabled", true);
		capabilities.setCapability("avd", "android11");
		capabilities.setCapability("autoGrantPermissions", true);
		capabilities.setCapability("enablePerformanceLogging", true);
//		capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
		
		AppiumDriver<MobileElement> app = new AppiumDriver<MobileElement>(new URL(appiumServiceUrl),capabilities);
		app.setLocation(new Location(42.8678439, -2.6972783, 0));
		return app;
	}
	
	public static void fillInput(WebDriver driver, String selector, String text) {
		((JavascriptExecutor)driver).executeScript("document.querySelector('"+selector+"').value='" + text + "'");	
	}
	
	public static void humanType (WebElement element, String text) throws InterruptedException {
		for (int i=0;i<text.length(); i++) {
			element.sendKeys(""+text.charAt(i));
//			Thread.sleep(25);
			while (element.getAttribute("value") == null) {
				element.sendKeys(""+text.charAt(i));
			}
			while (element.getAttribute("value").length() == i) {
				element.sendKeys(""+text.charAt(i));
			}
				
		}
	}
	
	/**
	 * Fake location 
	 */
	public static void setFakeLocation(WebDriver browser) {
		String script = 
				"window.navigator.geolocation.getCurrentPosition = function(success){"
				+ "var position = {"
						+ "'coords\' : {'latitude': '42.867918', 'longitude': ' 	-2.697234'}"
				+ "};"
				+ "success(position);"
				+ "}";
	
		JavascriptExecutor js = (JavascriptExecutor) browser;
		js.executeScript(script);
	}
	
	//The way to get elements clicked without random 'StaleElementReferenceException' exceptions
	public static boolean retryingFindClick(WebDriver app, By by) {
	    boolean result = false;
	    int attempts = 0;
	    while(attempts < 4) {
	        try {
	            app.findElement(by).click();
	            result = true;
	            break;
	        } catch(StaleElementReferenceException e) {
	        }
	        attempts++;
	    }
	    return result;
	}
	
	public static boolean clickUntilNotExists(WebDriver app, WebElement elem) {
	    boolean result = false;
	    int attempts = 0;
	    while(attempts < 8) {
	    	try {
	            elem.click();
	    	} catch (Exception e) {
	    		System.out.println(e.getClass());
	    		result = true;
	    		break;
	    	}
	        attempts++;
	    }
	    return result;
	}
	
	
	public static WebElement getFunction (String optionName, List<WebElement> elements) {
		try {
			Thread.sleep(2000);
			return elements.stream().filter(elem -> {
				WebElement nameElem = elem.findElement(By.cssSelector(".aonAppTitle"));
				return nameElem.getAttribute("innerHTML").equalsIgnoreCase(optionName);
			}).findFirst().orElse(null);			
		} catch (Exception e) {
			return null;
		}
	}
	
}
