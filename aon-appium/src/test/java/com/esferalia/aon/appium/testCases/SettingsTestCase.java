package com.esferalia.aon.appium.testCases;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.appium.AbstractTestCase;
import com.esferalia.aon.appium.id.AonIdNavigationBar;
import com.esferalia.aon.appium.id.AonIdSettings;
import com.esferalia.aon.appium.tools.AppiumTools;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class SettingsTestCase extends AbstractTestCase {

	@BeforeClass
	public static void setUpTimeControl() throws Exception {
		setUpTestDefaultData();
		setUpTestCase(apk, username, password, driver);
	}


	@After
	public void backToHome() {
		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement homeBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdNavigationBar.HOME_BUTTON)));
		homeBtn.click();
	}
	
	
	@Test
	public void changeNameTest() {
		openNavigationElement(AonIdNavigationBar.USER_BUTTON, AonIdSettings.SETTINGS_BUTTON, app);
		openNavigationElement(AonIdSettings.SETTINGS_BUTTON, AonIdSettings.NAME_INPUT, app);
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id(AonIdSettings.NAME_INPUT)));
		AppiumTools.retryingFindClick(app, By.id(AonIdSettings.NAME_INPUT));
		AppiumTools.fillInput(app, "#"+AonIdSettings.NAME_INPUT, "");
		app.hideKeyboard();
		nameInput.sendKeys("Monkey");

		WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdSettings.SURNAMES_INPUT)));
		passwordInput.click();
		AppiumTools.fillInput(app, "#"+AonIdSettings.SURNAMES_INPUT, "");
		app.hideKeyboard();
		passwordInput.sendKeys("D. Luffy");
		
		AppiumTools.retryingFindClick(app, By.id(AonIdSettings.SAVE_SETTINGS));
		
	}
	
	public static void openNavigationElement (String elementId, String elementIdToWaitFor, AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		boolean failed;
		int failcount = 0;
		do {
			failed = false;
			wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
			try {
				Thread.sleep(100);
				AppiumTools.retryingFindClick(app, By.id(elementId));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.id(elementIdToWaitFor)));
			
			} catch (Exception e) {
				failed = true;
				failcount++;
			}
			
		} while (failed && failcount < 10);
	}
	
	
}
