/**
 * 
 */
package com.esferalia.aon.tests;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * @author rtrepiana
 *
 */
public class AonSelenium extends DefaultSelenium {
	
	
	/**
	 * @param processor
	 */
	public AonSelenium(CommandProcessor processor) {
		super(processor);
	}

	/**
	 * @param serverHost
	 * @param serverPort
	 * @param browserStartCommand
	 * @param browserURL
	 */
	public AonSelenium(String serverHost, int serverPort,
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}


	@Override
	public void click(String locator) {
		waitForELementPresent(locator, 60);
		super.click(locator);
	}

	
	@Override
	public void type(String locator, String value) {
		waitForELementPresent(locator, 60);
		super.type(locator, value);
	}

	
	@Override
	public void select(String selectLocator, String optionLocator) {
		waitForELementPresent(selectLocator, 60);
		super.select(selectLocator, optionLocator);
	}
	
	public void waitForELementPresent(String locator, int timeout ) {
		for (int seconds = 0;seconds < timeout; seconds++) {
			try { 
				if (isElementPresent(locator)) {
					return; 
				}
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		throw new SeleniumException("ERROR: Element "+locator+" not found");
	}
	
	
	

}
