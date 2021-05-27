package com.esferalia.aon.appium.tools;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class AppiumTools {

	public static AppiumDriver<MobileElement> getDriverAndroid(String apk,String driver){
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "android11");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
		capabilities.setCapability(MobileCapabilityType.APP, apk);
		capabilities.setCapability("chromedriverExecutable", driver);
		capabilities.setCapability("avd", "android11");
		
		
		return new AppiumDriver<MobileElement>(capabilities);
	}
	
	
}
