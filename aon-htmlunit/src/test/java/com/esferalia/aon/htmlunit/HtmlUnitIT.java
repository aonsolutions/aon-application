package com.esferalia.aon.htmlunit;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTextInput;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HtmlUnitIT {

	public static final Logger LOGGER = Logger
			.getLogger("org.htmlunit");
	
	public static final String GWT_DEBUG_ID_PREFIX= "gwt-debug-";
	public static final String AON_MAIN_MENU_FORM = "aonContent:mainMenuForm";
	public static final String INTEGRATION_BASE_USER = "integration.test.user";
	public static final String INTEGRATION_BASE_PASSWORD = "integration.test.password";

	// ------------------------------------------------------------------------
	
	public static void wait4(HtmlPage page, Predicate<HtmlPage> predicate) throws InterruptedException{
        //try 20 times to wait .5 second each for filling the page.
        for (int i = 0; i < 20; i++) {
            if (predicate.test(page)) {
                return;
            }
            synchronized (page) {
                page.wait(500);
            }
        }
        Assert.fail();
        
	}

	public static <T> T wait4(HtmlPage page, Supplier<T> supplier) throws InterruptedException{
        //try 20 times to wait .5 second each for filling the page.
        for (int i = 0; i < 20; i++) {
        	T t = supplier.get();
            if ( t != null ) {
                return t;
            }
            synchronized (page) {
                page.wait(500);
            }
        }
        return null;
	}

	public static HtmlPage login(WebClient webClient, String url, String user, String password)
			throws Exception {
		LOGGER.warning("Connecting to: " + url);

		// Get the login/index page
		final HtmlPage loginPage = webClient.getPage(url);

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
		userText.setValue(user);
		// Sets the value of password text field
		passwdPassword.setValue(password);

		// Now submit the form by clicking the button and get back the second
		// page
		return loginButton.click();
	}



}
