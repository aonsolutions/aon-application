package com.esferalia.aon.tests;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class ClickDesktopTestCase extends SeleneseTestCase {	

	@Before
	public void setUp() throws Exception {
		selenium = new AonSelenium("localhost", 4444, "*chrome", "http://test.esferalia.org/");
		selenium.start();
	}

	@Test
	public void testClickDesktopTestCase() throws Exception {
		selenium.open("/aon-desktop/");
		selenium.type("j_username_view", "test");
		selenium.type("j_password", "demo");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[@id='aonContent:j_id31:j_id39']/img");
		selenium.click("//a[@id='aonContent:j_id254:j_id267']/img");
		selenium.click("//a[@id='aonContent:j_id391:j_id414']/img");
		selenium.click("//a[@id='aonContent:j_id514:j_id542']/img");
		selenium.click("//a[@id='aonContent:j_id645:j_id678']/img");
		selenium.click("//a[@id='aonContent:j_id816:j_id854']/img");
		selenium.click("//a[@id='aonContent:j_id1000:j_id1043']/img");
		selenium.click("//a[@id='aonContent:j_id1123:j_id1171']/img");
		selenium.click("//a[@id='j_id24:j_id25']/span");
		selenium.waitForPageToLoad("30000");
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
