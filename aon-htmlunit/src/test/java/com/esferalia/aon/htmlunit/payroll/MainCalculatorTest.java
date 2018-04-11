package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.GWT_DEBUG_ID_PREFIX;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MainCalculatorTest {
	

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.general.payroll.url";
	public static final String AON_PAYROLL_MENU_FORM = "aonContent:payrollMenu";

	private static WebClient webClient;
	private static HtmlPage htmlPage;
	
	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.setLevel(Level.WARNING);
		webClient = new WebClient(BrowserVersion.FIREFOX_45);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAlertHandler(new AlertHandler() {
			@Override
			public void handleAlert(Page page, String message) {
				LOGGER.warning("ALERT '" + message + "'" );
				
			}
		});
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		htmlPage = login(webClient, url, user, password);

		// Payroll Menu
		HtmlAnchor menuPayrollAnchor = htmlPage
				.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asText());
		htmlPage = menuPayrollAnchor.click();

		// MainCalculator
		HtmlAnchor gwtCalculatorAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_calculator");
		LOGGER.warning("Cick on: " + gwtCalculatorAnchor.asText());
		htmlPage = gwtCalculatorAnchor.click();
		
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"calcButton") != null);
		
	}

	@AfterClass
	public static void tearDown() {
		webClient.close();
	}

	@Test
	public void TestCalcular() throws Exception {
		
		DomElement enterprisesDataGrid = 
				htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"enterprisesDataGrid");
		
		HtmlCheckBoxInput allCheckBoxInput =  
				enterprisesDataGrid.getFirstByXPath("//input[@type='checkbox']");
		allCheckBoxInput.click();
		
		
		HtmlButton buttonInput = (HtmlButton)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "calcButton");
		buttonInput.click();
		
		

	}
	

}
