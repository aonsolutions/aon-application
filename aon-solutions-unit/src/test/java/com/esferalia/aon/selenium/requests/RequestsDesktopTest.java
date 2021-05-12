package com.esferalia.aon.selenium.requests;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RequestsDesktopTest extends RequestsAbstractTest{

	@BeforeClass
	public static void setUpTestcase() {		
		setUpTestDefaultData();
		setUpTestCase(url, username, password, platform, device, driver);		
	}

	@Test
	@Ignore
	/**
	 * @TO_DO Creating a new Request
	 * 
	 *  1. Click in new Request button.
	 *  2. Change title.
	 *  3. Write first message.
	 *  4. Click send button.
	 *  5. Check chat (if message is there).
	 *  
	 */
	public void NewRequestTest() {
	
	}
	
	@Override
	@Before
	public void showRequestView() {
		try {
			WebDriverWait wait = new WebDriverWait(browser, 10);
			WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonDesktopMainSidenavSolicitudes")));
			console.info("Element found","Solicitudes navbar option.");
			
			enter.click();
			console.info("Click", "showing request view.");
			
		}catch(WebDriverException e) {
			String message = "Element does not exist.";
			fail(message);
		}
	}
}
