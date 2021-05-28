package com.esferalia.aon.appium.tools;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;

public class AppiumTools {

	public static AppiumDriver<MobileElement> getDriverAndroid(String apk,String driver, String appiumServiceUrl) throws MalformedURLException{
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "android11");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
		capabilities.setCapability(MobileCapabilityType.APP, apk);
		capabilities.setCapability("chromedriverExecutable", driver);
		capabilities.setCapability("avd", "android11");
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
		
		return new AppiumDriver<MobileElement>(new URL(appiumServiceUrl),capabilities);
	}
	
	public static void fillInput(WebDriver driver, String selector, String text) {
		((JavascriptExecutor)driver).executeScript("document.querySelector('"+selector+"').value='" + text + "'");	
	}
	
}
