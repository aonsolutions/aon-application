package com.esferalia.aon.selenium.requests;

import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.selenium.AbstractTestCase;

public abstract class RequestsAbstractTest extends AbstractTestCase{

	/**
	 * Show view
	 * show common method
	 */
	public abstract void showRequestView();
	
	
}
