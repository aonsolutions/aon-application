package com.esferalia.aon.selenium.requests;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.selenium.tools.Device;

public class RequestsMobileTest extends RequestsAbstractTest{

	@BeforeClass
	public static void setUpTestcase() {		
		setUpTestDefaultData();
		setUpTestCase(url, username, password, platform, Device.IPHONE_X, driver);		
	}
	
	@Test
	public void requestsShowView() {
		this.showRequestView();
	}

	@Override
	public void showRequestView() {
		try {
//			WebDriverWait wait = new WebDriverWait(browser, 10);
//			WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonDesktopMainSidenavSolicitudes")));
//			console.info("Element found","Solicitudes navbar option.");
			
		}catch(WebDriverException e) {
			String message = "Element does not exist.";
			fail(message);
		}
	}
	
}
