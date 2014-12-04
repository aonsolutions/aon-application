package com.esferalia.aon.htmlunit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HtmlUnitIT {

	private static final Logger LOGGER = Logger
			.getLogger("com.gargoylesoftware.htmlunit");

	private static String webUrl;

	private static WebClient webClient;
	private static HtmlPage homePage;

	private static Map<String, HtmlPage> mainMenuPages = 
			new HashMap<String, HtmlPage>();

	@BeforeClass
	public static void setUp() {
		webUrl = System.getProperty("integration.base.url");
		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		LOGGER.setLevel(Level.WARNING);
	}

	@AfterClass
	public static void tearDown() {
		webClient.closeAllWindows();
	}

	/**
	 * Test login
	 */
	@Test
	public void test01Login() throws Exception {
		LOGGER.warning("Connecting to: " + webUrl);

		// Get the login/index page
		final HtmlPage loginPage = webClient.getPage(webUrl);

		// Get the login form and within that form, find the submit button,
		// the user name input text and password input.
		final HtmlForm loginForm = loginPage
				.getFirstByXPath("//form[@action='j_security_check']");

		final HtmlSubmitInput loginButton = loginForm
				.getInputByName("login_btn");
		final HtmlTextInput userText = loginForm.getInputByName("j_username");
		final HtmlPasswordInput passwdPassword = loginForm
				.getInputByName("j_password");

		// Sets the value of user name text field
		userText.setValueAttribute("admin");
		// Sets the value of password text field
		passwdPassword.setValueAttribute("test");

		// Now submit the form by clicking the button and get back the second
		// page
		homePage = loginButton.click();
	}

	/**
	 * Test Main Menu
	 */
	@Test
	public void test02MainMenu() throws Exception {

		final HtmlForm mainMenuForm = homePage
				.getFormByName("aonContent:mainMenuForm");

		List<HtmlAnchor> menuItems = (List<HtmlAnchor>) mainMenuForm
				.getByXPath(".//a");
		for (HtmlAnchor menuItem : menuItems) {
			if  ( !menuItem.isDisplayed() )
				continue;
			String id = menuItem.getId().replaceFirst("aonContent:mainMenuForm:", "");
			LOGGER.warning("Click on: " + id );
			mainMenuPages.put(id, menuItem.click());
		}
	}
}
