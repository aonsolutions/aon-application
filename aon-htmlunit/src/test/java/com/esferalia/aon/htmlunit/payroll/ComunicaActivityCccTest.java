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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

public class ComunicaActivityCccTest {

	public static final String INTEGRATION_TGSS_URL = "integration.test.tgss.payroll.url";
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
		
		url = System.getProperty(INTEGRATION_TGSS_URL);
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
		
		wait4Id("tgss-content");
	}

	@AfterClass
	public static void tearDown() {
		webClient.close();
	}
	
	@Test
	public void TestActivityUp2Date() throws Exception {
		loadActiviry("servicios_cloud/saas-content");
		
		wait4InputText("activityNameTB", "SERVICIOS CLOUD/SAAS");
		
		HtmlTable cccTable = (HtmlTable)getElementById("cccTable");
		Assert.assertEquals(cccTable.getRowCount(), 2);
		
		wait4Id("pdfNotLoaded");
		htmlPage = getElementById("checkUpdateCert").click();
		wait4Id("printPreviewViewer");
		wait4Id("closePDFViewerBtn");
		wait4Id("pdfLoaded");
		
		htmlPage = getElementById("closePDFViewerBtn").click();
		wait4Id("pdfNotLoaded");
	}

	@Test
	public void TestCccEmployeesWorking() throws Exception {
		loadActiviry("servicios_cloud/saas-content");
		
		wait4InputText("activityNameTB", "SERVICIOS CLOUD/SAAS");
		
		HtmlTable cccTable = (HtmlTable)getElementById("cccTable");
		Assert.assertEquals(cccTable.getRowCount(), 2);
		
		getElementById("tgssMenu_1").click();
		wait4Id("employeesWorking");
		
		wait4Id("pdfNotLoaded");
		htmlPage = getElementById("employeesWorking").click();
		wait4Id("printPreviewViewer");
		wait4Id("closePDFViewerBtn");
		wait4Id("pdfLoaded");
		
		htmlPage = getElementById("closePDFViewerBtn").click();
		wait4Id("pdfNotLoaded");
	}
	
	@Test
	public void TestCccEmployeePrevMov() throws Exception {
		loadActiviry("servicios_cloud/saas-content");
		
		wait4InputText("activityNameTB", "SERVICIOS CLOUD/SAAS");
		
		HtmlTable cccTable = (HtmlTable)getElementById("cccTable");
		Assert.assertEquals(cccTable.getRowCount(), 2);
		
		getElementById("tgssMenu_1").click();
		wait4Id("employeePrevMov");
		
		wait4Id("pdfNotLoaded");
		htmlPage = getElementById("employeePrevMov").click();
		wait4Id("printPreviewViewer");
		wait4Id("closePDFViewerBtn");
		wait4Id("pdfLoaded");
		
		htmlPage = getElementById("closePDFViewerBtn").click();
		wait4Id("pdfNotLoaded");
	}
	
	@Test
	public void TestCccIdc() throws Exception {
		loadActiviry("servicios_cloud/saas-content");
		
		wait4InputText("activityNameTB", "SERVICIOS CLOUD/SAAS");
		
		HtmlTable cccTable = (HtmlTable)getElementById("cccTable");
		Assert.assertEquals(cccTable.getRowCount(), 2);
		
		getElementById("tgssMenu_1").click();
		wait4Id("idc");
		
		wait4Id("pdfNotLoaded");
		htmlPage = getElementById("idc").click();
		wait4Id("printPreviewViewer");
		wait4Id("closePDFViewerBtn");
		wait4Id("pdfLoaded");
		
		htmlPage = getElementById("closePDFViewerBtn").click();
		wait4Id("pdfNotLoaded");
	}
	
	@Test
	public void TestCccLaboralLife() throws Exception {
		loadActiviry("servicios_cloud/saas-content");
		
		wait4InputText("activityNameTB", "SERVICIOS CLOUD/SAAS");
		
		HtmlTable cccTable = (HtmlTable)getElementById("cccTable");
		Assert.assertEquals(cccTable.getRowCount(), 2);
		
		getElementById("tgssMenu_1").click();
		wait4Id("laboralLife");
		
		wait4Id("pdfNotLoaded");
		htmlPage = getElementById("laboralLife").click();
		wait4Id("printPreviewViewer");
		wait4Id("closePDFViewerBtn");
		wait4Id("pdfLoaded");
		
		htmlPage = getElementById("closePDFViewerBtn").click();
		wait4Id("pdfNotLoaded");
	}

	private void loadActiviry(String activityId) throws InterruptedException, IOException {
		wait4Id(activityId);
		htmlPage = getElementById(activityId).click();
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
		input.setValueAttribute(text);
		input.blur();
	}
	
	protected static void wait4InputText(String id, String value) throws InterruptedException {
		wait4(htmlPage, 
				htmlPage -> ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id)).getValueAttribute().equals(value));
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
