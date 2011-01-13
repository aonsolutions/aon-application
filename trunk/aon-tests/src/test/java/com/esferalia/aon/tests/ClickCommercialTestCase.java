package com.esferalia.aon.tests;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class ClickCommercialTestCase extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new AonSelenium("localhost", 4444, "*chrome", "http://test.esferalia.org/");
		selenium.start();
	}

	@Test
	public void testClickCommercialTestCase() throws Exception {
		selenium.open("/aon-commercial/");
		selenium.type("j_username_view", "commercial");
		selenium.type("j_password", "demo");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
		selenium.click("aonContent:j_id26:agent");
		selenium.click("aonContent:j_id75:commercial");
		selenium.click("aonContent:j_id109:marketing");
		selenium.click("aonContent:j_id152:statistics");
		selenium.click("aonContent:j_id180:config");
		selenium.click("//a[@id='j_id21:j_id22']/span");
		selenium.waitForPageToLoad("30000");
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
