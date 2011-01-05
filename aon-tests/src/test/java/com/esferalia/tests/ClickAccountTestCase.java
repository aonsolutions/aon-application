package com.esferalia.aon.tests;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class ClickAccountTestCase extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://test.esferalia.org/");
		selenium.start();
	}

	@Test
	public void testClickAccountTestCase() throws Exception {
		selenium.open("/aon-account/");
		selenium.type("j_username_view", "account");
		selenium.type("j_password", "demo");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
		selenium.click("aonContent:j_id26:masters");
		selenium.click("aonContent:j_id86:entries");
		selenium.click("aonContent:j_id123:reports");
		selenium.click("aonContent:j_id170:balance");
		selenium.click("aonContent:j_id199:fixed_assets");
		selenium.click("aonContent:j_id225:finance");
		selenium.click("aonContent:j_id249:configuration");
		//selenium.click("//img[@title='Favorites']");
		//selenium.click("//table[@id='aonContent:j_id273:mainMenu']/tbody/tr/td[17]/a");
		//selenium.waitForPageToLoad("30000");
		selenium.click("//a[@id='j_id21:j_id22']/span");
		selenium.waitForPageToLoad("30000");
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
