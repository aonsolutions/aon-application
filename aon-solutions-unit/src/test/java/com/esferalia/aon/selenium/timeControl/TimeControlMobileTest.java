package com.esferalia.aon.selenium.timeControl;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.selenium.tools.Device;

public class TimeControlMobileTest extends TimeControlAbstractTest{

	@BeforeClass
	public static void setUpTestcase() {		
		setUpTestCase(url, username, password, platform, Device.IPHONE_X, driver);		
	}

	@Test
	public void signTest() {
		this.signTestImpl();
	}
	
	
}
