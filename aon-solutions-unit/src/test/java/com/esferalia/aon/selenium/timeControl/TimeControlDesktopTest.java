package com.esferalia.aon.selenium.timeControl;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.selenium.AbstractTestCase;
import com.esferalia.aon.selenium.tools.Device;
import com.esferalia.aon.selenium.tools.SeleniumTools;

public class TimeControlDesktopTest extends TimeControlAbstractTest{

	@BeforeClass
	public static void setUpTestcase() {		
		setUpTestDefaultData();
		setUpTestCase(url, username, password, platform, device, driver);		
	}
	
	@Test
	public void signTest() {
		this.signTestImpl();
	}
	
	@Test
	public void multiClickSignTest() {
		this.multiClickSignTestImpl();
	}
	
	
	public void showRequestView() {
		try {
			WebDriverWait wait = new WebDriverWait(browser, 10);
			WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonDesktopMainSidenavSolicitudes")));
			console.info("Element found","Solicitudes navbar option.");
			
		}catch(WebDriverException e) {
			String message = "Element does not exist.";
			fail(message);
		}
	}
	
	
	
	
}
