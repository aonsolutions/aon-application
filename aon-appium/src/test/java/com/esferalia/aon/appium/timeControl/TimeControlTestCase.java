package com.esferalia.aon.appium.timeControl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.esferalia.aon.appium.AbstractTestCase;

public class TimeControlTestCase extends AbstractTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUpTestDefaultData();
		super.setUpTestCase(apk, username, password, driver);
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
