package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.GWT_DEBUG_ID_PREFIX;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.htmlunit.HtmlUnitIT;

import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.javascript.JavaScriptErrorListener;
import org.htmlunit.util.WebConnectionWrapper;

public class GeneralActivityTest {
	

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
		
		webClient.setAlertHandler((page, message) -> LOGGER.warning("ALERT '" + message + "'" ));
		
		webClient.setJavaScriptErrorListener( new  JavaScriptErrorListener() {
			
			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
				LOGGER.severe("Warn " + message + "" );
			}

			@Override
			public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
				LOGGER.severe("Timeout " + executionTime + "ms" );
			}
			
			@Override
			public void scriptException(HtmlPage page, ScriptException scriptException) {
				LOGGER.severe("Script Exception [" + scriptException.getFailingLine() + ","+ scriptException.getFailingLineNumber() +"] '" + scriptException.getMessage() + "'" );
				
			}
			
			@Override
			public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
				LOGGER.severe("Malformed Script URL '" + url + "' " + malformedURLException.getMessage() + "'" );
				
			}
			
			@Override
			public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
				LOGGER.severe("Script Error '" + scriptUrl + "' " + exception.getMessage() + "'" );
			}

		});
				
		webClient.setWebConnection(new WebConnectionWrapper(webClient) {
			@Override
			public WebResponse getResponse(WebRequest request) throws IOException {
				String file = request.getUrl().getFile();
				if (file.toLowerCase().contains("viewer.html")) {
		            /* Give the program a response, protect against pdfjs. */
		            throw new IllegalArgumentException(file);
		        } else {
		            return super.getResponse(request); // Pass the responsibility up.
		        }
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
	
		// Integral
		HtmlAnchor gwtEmployeeAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_employee");
		LOGGER.warning("Cick on: " + gwtEmployeeAnchor.asNormalizedText());
		htmlPage = gwtEmployeeAnchor.click();
		
		wait4Id("regimen_general");
	}

	@AfterClass
	public static void tearDown() {
		webClient.close();
	}

	@Test
	public void TestActivity() throws Exception {
		loadActivity("oficinas_y_despachos-content");
		
		wait4InputText("activityNameInput", "OFICINAS Y DESPACHOS");
		wait4InputText("activityCnaeInput", "6201 - Actividades de programaci\u00f3n inform\u00e1tica");
		
		// Change name
		String activityNameTextBoxValue = ((HtmlInput)getElementById("activityNameInput")).getValue();
		
		wait4NoClass("activityName", "aon_custom_error");
		setValue("activityNameInput", "");
		wait4Class("activityName", "aon_custom_error");
		setValue("activityNameInput", activityNameTextBoxValue);
		wait4NoClass("activityName", "aon_custom_error");
	}
	
	@Test
	public void TestCCC() throws Exception {
		loadActivity("oficinas_y_despachos");
		
		wait4InputText("activityNameInput", "OFICINAS Y DESPACHOS");
		
		HtmlDivision cccTable = (HtmlDivision)getElementById("cccTable");
		Assert.assertEquals(cccTable.querySelectorAll(".aon_custom_row").size(), 4);
		
		// Create Artist CCC
		getElementById("createCCCBtn").click();
		wait4Id("cccDialog_Content");
		
		wait4NoClass("cccDialog_Account", "aon_custom_warning");
		wait4NoClass("cccDialog_Geozone", "aon_custom_warning");
		
		((HtmlSelect)getElementById("cccDialog_TypeSelect")).setSelectedIndex(9);
		wait4InputText("cccDialog_RegimeInput", "0112");
		setValue("cccDialog_AccountInput", "12345678901");
		wait4InputText("cccDialog_GeozoneInput", "CASTELLON");
		
		wait4Class("cccDialog_Account", "aon_custom_warning");
		wait4Class("cccDialog_Geozone", "aon_custom_warning");
		
		getElementById("cccDialog_Accept").click();
		
		wait4Id("cccTable");
		HtmlDivision newCccTable = (HtmlDivision)getElementById("cccTable");
		wait4(htmlPage, htmlPage -> newCccTable.querySelectorAll(".aon_custom_row").size() > 4);
		
		// Remove Artist CCC
		HtmlDivision createdRow = (HtmlDivision)newCccTable.querySelectorAll(".aon_custom_row").get(0);
		HtmlButton deleteArtistButton = (HtmlButton)createdRow.querySelector("#gwt-debug-delete");
		deleteArtistButton.click();
		wait4Id("acceptDialogButton");
		((HtmlButton)getElementById("acceptDialogButton")).click();
		
		wait4Id("cccTable");
		HtmlDivision deletionCccTable = (HtmlDivision)getElementById("cccTable");
		wait4(htmlPage, htmlPage -> deletionCccTable.querySelectorAll(".aon_custom_row").size() == 4);
	}

	private void loadActivity(String activityId) throws InterruptedException, IOException {
		wait4Id(activityId);
		htmlPage = getElementById(activityId).click();
		wait4Id("activityName");
	}

	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected <T extends DomElement> T getElementById(String id ) {
		return (T)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id );
	}
	
	protected  static void wait4Id(String id) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
	}

	protected void setValue(String id, String text) throws ParseException {
		HtmlInput input = getElementById(id);
		input.focus();
		input.setValue(text);
		input.blur();
	}
	
	protected static void wait4InputText(String id, String value) throws InterruptedException {
		wait4(htmlPage, 
				htmlPage -> ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id)).getValue().equals(value));
	}
	
	protected static void wait4DivText(String id, String value) throws InterruptedException {
		wait4(htmlPage, 
				htmlPage -> ((HtmlDivision)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id)).getTextContent().equals(value));
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
