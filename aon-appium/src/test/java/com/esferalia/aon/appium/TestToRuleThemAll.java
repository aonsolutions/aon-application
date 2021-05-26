package com.esferalia.aon.appium;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class TestToRuleThemAll {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		AppiumDriver<MobileElement> app = null;
		try {
			
			String chromedriverPath = "/home/akrck02/eclipse-workspace/aon-application/aon-appium/src/test/resources/com/esferalia/aon/appium/chromedriver";
			String apk = "/home/akrck02/eclipse-workspace/aon-application/aon-appium/src/test/resources/com/esferalia/aon/appium/aon.apk";
			
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "android11");
			capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
			capabilities.setCapability(MobileCapabilityType.APP, apk);
			capabilities.setCapability("chromedriverExecutable", chromedriverPath);
			capabilities.setCapability("avd", "android11");
			
			
			app = new AppiumDriver<MobileElement>(capabilities);
			 app.findElementsByClassName("android.webkit.WebView");
			
			WebDriverWait wait = new WebDriverWait(app,20);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("android.webkit.WebView")));
	        List<MobileElement> mList = app.findElements(By.className("android.webkit.WebView"));	        
	        Set<String> contextNames = app.getContextHandles();
	        
	    	
			for (String context : contextNames) {
				System.out.println("Context: " + context);
			}
			
			app.context("WEBVIEW_aon.solutions");
			
			WebElement loginButtonEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonLoginSignin")));
			System.err.println(loginButtonEl);
			
			WebElement usernameInput = app.findElement(By.id("aonLoginUserInput"));
			WebElement passwordInput = app.findElement(By.id("aonLoginPasswordInput"));
			System.out.println(usernameInput);
			
			String username =  "usuario@aonsolutions.org";
			String password =  "org";

			
			usernameInput.sendKeys(username);
			passwordInput.sendKeys(password);
	
			loginButtonEl.click();
			
			WebElement userIcon = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("aonHeaderUserButtonIconButton")));
			assertTrue("AON SOLUTIONS: Incorrect login",userIcon.isDisplayed());
			
		} catch (Exception e) {
			if(app != null) app.quit();
			e.printStackTrace();
			fail("Login failed");
		}

	}

}
