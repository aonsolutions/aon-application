package com.esferalia.aon.appium.timeControl;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.appium.AbstractTestCase;

public class TimeControlTestCase extends AbstractTestCase {

	@BeforeClass
	public static void setUpTimeControl() throws Exception {
		setUpTestDefaultData();
		setUpTestCase(apk, username, password, driver);
	}

//	@Before
//	public void backToHome() {
//		WebDriverWait wait = new WebDriverWait(app, 10);
//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
//		
//		WebElement homeIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonMobileMenuHomeButtonIcon")));
//		homeIcon.click();
//	}

	@After
	public void closeAtEnd() {
		app.closeApp();
	}

	@Before
	public void openOnStart() {
		app.launchApp();
	}

	@Test
	public void signInTest() {

	}

	@Test
	public void filterTest() {
		try {
			WebDriverWait wait = new WebDriverWait(app, 10);
//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			
			WebElement timeElem = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.cssSelector("*[id='aonMobileMenuControl HorarioButtonIcon']")));
			timeElem.click();

//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		
			WebElement filterElem = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.id("aonSigninToolbarHeaderToolSectionfilterButtonIcon")));
			filterElem.click();

			WebElement closeElement = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.id("aonFilterDialogMenuFilterDialogClick")));
			closeElement.click();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
