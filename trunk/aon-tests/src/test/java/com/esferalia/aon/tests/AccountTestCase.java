package com.esferalia.aon.tests;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class AccountTestCase extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new AonSelenium("localhost", 4444, "*chrome", "http://test.esferalia.org/");
		selenium.setSpeed("1000");
		selenium.start();
		selenium.open("/aon-account/");
		selenium.type("j_username_view", "account");
		selenium.type("j_password", "demo");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
	}

	@Test
	public void testCustomers() throws Exception {
		selenium.click("aonContent:j_id26:masters");
		selenium.click("aonContent:mastersMenu:customer");
		selenium.click("aonContent:customerForm:customerToolbar-reset");
		selenium.type("aonContent:customerForm:Registry_document", "G01457993");
		selenium.type("aonContent:customerForm:Registry_name", "innopyme");
		selenium.type("aonContent:customerForm:Registry_alias", "innopyme");
		selenium.click("aonContent:customerForm:Registry_type:0");
		selenium.click("aonContent:customerForm:Customer_status:0");
		selenium.click("aonContent:customerForm:Customer_surcharge");
		selenium.click("aonContent:customerForm:Customer_transaction:0");
		selenium.type("aonContent:customerForm:Company_address", "Duque de Wellington 52");
		selenium.type("aonContent:customerForm:Company_address_city", "Vitoria-Gasteiz");
		selenium.type("aonContent:customerForm:Company_address_zip", "01010");
		selenium.type("aonContent:customerForm:Company_telephone", "945121010");
		selenium.type("aonContent:customerForm:Company_fax", "945121011");
		selenium.type("aonContent:customerForm:Company_email", "info@innopyme.org");
		selenium.click("aonContent:customerForm:customerToolbar-save");
		//selenium.type("aonContent:customerForm:Observation", "Asociación para la innovación y mejora tecnológica");
		//selenium.click("aonContent:customerForm:customerToolbar-save");
		selenium.click("aonContent:customerForm:customerToolbar-remove");
		selenium.click("cb_customerToolbar-remove-yes");
	}

	@After
	public void tearDown() throws Exception {
		selenium.click("//a[@id='j_id21:j_id22']/span");
		selenium.waitForPageToLoad("30000");
		selenium.stop();
	}
}
