package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.GWT_DEBUG_ID_PREFIX;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.javascript.host.event.MouseEvent;

public class GeneralAgreementTest {
	

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.general.payroll.url";
	public static final String AON_PAYROLL_MENU_FORM = "aonContent:payrollMenu";

	private static WebClient webClient;
	private static HtmlPage htmlPage;
	
	private static String url;
	private static String user;
	private static String password;
	
	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.setLevel(Level.WARNING);
		webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);

		webClient.setAlertHandler(new AlertHandler() {
			@Override
			public void handleAlert(Page page, String message) {
				LOGGER.warning("ALERT '" + message + "'" );
				
			}
		});
		url = System.getProperty(INTEGRATION_PAYROLL_URL);
		user = System.getProperty(INTEGRATION_BASE_USER);
		password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		htmlPage = login(webClient, url, user, password);

		// Payroll Menu
		HtmlAnchor menuPayrollAnchor = htmlPage
				.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asNormalizedText());
		htmlPage = menuPayrollAnchor.click();

		// MainAgreement
		HtmlAnchor gwtAgreementAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asNormalizedText());
		htmlPage = gwtAgreementAnchor.click();
		
		wait4Id("agreements");
		
	}


	@AfterClass
	public static void tearDown() {
		webClient.close();
	}

	@Test
	public void TestPagasAnulaesVAranoNavidad() throws Exception {

		wait4Id("pagas_anuales_verano_&_navidad");

		HtmlDivision agreementTreeItem = 
				(HtmlDivision)getElementById("pagas_anuales_verano_&_navidad");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();
		
		wait4(htmlPage,
				htmlPage -> "PAGAS ANUALES VERANO & NAVIDAD".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));
		
		setValue("textBox_SALARIO_MENSUAL_I", "666.66");
		
		wait4Class("textBox_SALARIO_MENSUAL_I", "aon-icon-changed");
		
	}

	// ------------------------------------------------------------------------
	
	protected <T extends DomElement> T getElementById(String id ) {
		return (T)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id );
	}
	

	protected  static void wait4Id(String id) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
	}

	protected  static void wait4NoId(String id) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) == null);
	}

	protected void setValue(String id, String text) throws ParseException {
		HtmlInput input = getElementById(id);
		input.focus();
		input.setValueAttribute(text);
		input.blur();
	}
	
	protected void wait4Value(String id, String value) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("wait4Value : [ "+ id +"] '" + ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id )).getValueAttribute().trim() +"' = '" +value.trim()+"'");
		wait4(htmlPage,
				htmlPage -> ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id)).getValueAttribute().trim().equals(value.trim()));
	}
	
	protected void wait4Class(String id, String clazz) throws InterruptedException{
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getAttribute("class").contains(clazz));
	}

	protected void wait4NoClass(String id, String clazz ) throws InterruptedException{
		wait4(htmlPage,
				htmlPage -> !htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getAttribute("class").contains(clazz));
	}
	
}
