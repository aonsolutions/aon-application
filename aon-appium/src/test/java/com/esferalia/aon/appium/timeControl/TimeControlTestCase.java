package com.esferalia.aon.appium.timeControl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.appium.AbstractTestCase;

public class TimeControlTestCase extends AbstractTestCase {

	@BeforeClass
	public static void setUpTimeControl() throws Exception {
		setUpTestDefaultData();
		setUpTestCase(apk, username, password, driver);
	}
	
	@Before
	public void openApp() {
		//app.activateApp("aon APP");
		
	}
	
	@Test
	public void signInTest() {
		console.info("HOLIIIII");	
	}

	
	@Test
	public void signInMultipleClickTest() {
		console.info("QUE TAAAAL");	
	}

	
}
