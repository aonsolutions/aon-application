package com.esferalia.aon.selenium.timeControl;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.selenium.AbstractTestCase;
import com.esferalia.aon.selenium.tools.SeleniumTools;

public abstract class TimeControlAbstractTest extends AbstractTestCase {

	public void signTestImpl() {

		try {
			/**
			 * Setting page
			 */
			browser.get(url);
			
			/*
			 * Waiting enter button
			 */
			WebDriverWait wait = new WebDriverWait(browser, 10);
			WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonSignEntrada")));
			console.info("Element found","Enter button.");
			
			/**
			 * Faking location
			 */
			console.info("Setting location"," done.");
			SeleniumTools.setFakeLocation(browser);
			
			/**
			 * Enter to work
			 */
			enter.click();
			console.info("Click","Entering.");		
			
			/**
			 * Waiting for exit button
			 */
			WebElement exit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonSignSalida")));
			console.info("Element found","Exit button.");
			
			
			/**
			 * Exiting work
			 */
			exit.click();
			console.info("Click","Exiting.");
			console.success("sign in / out", "DONE.");
		} catch(WebDriverException e) {
			e.printStackTrace();
			String message = "AON SOLUTIONS: Element does not exist anymore";
			fail(message);
		}
	}
	
	
	public void multiClickSignTestImpl() {

		try {
			/**
			 * Setting page
			 */
			browser.get(url);
			
			/*
			 * Waiting enter button
			 */
			WebDriverWait wait = new WebDriverWait(browser, 10);
			WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonSignEntrada")));
			console.info("Element found","Enter button.");
			
			/**
			 * Faking location
			 */
			console.info("Setting location"," done.");
			SeleniumTools.setFakeLocation(browser);
			
			/**
			 * Enter to work
			 */
			
			for (int i = 0; i < 2; i++) {
				enter.click();
			}
			
			console.info("Click","Entering.");		
			
			/**
			 * Waiting for exit button
			 */
			WebElement exit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#aonSignSalida")));
			console.info("Element found","Exit button.");
			
			
			/**
			 * Exiting work
			 */
			exit.click();
			console.info("Click","Exiting.");
			console.success("sign in / out", "DONE.");
		} catch(WebDriverException e) {
			e.printStackTrace();
			String message = "AON SOLUTIONS: Element does not exist anymore";
			fail(message);
		}
	}

}
