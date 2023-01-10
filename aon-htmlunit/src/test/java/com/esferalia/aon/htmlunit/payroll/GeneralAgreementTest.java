package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.GWT_DEBUG_ID_PREFIX;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

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
		webClient = new WebClient(BrowserVersion.CHROME);
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
		HtmlAnchor menuPayrollAnchor = htmlPage.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asNormalizedText());
		htmlPage = menuPayrollAnchor.click();
		
		// MainAgreement
		HtmlAnchor gwtAgreementAnchor = htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
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

		HtmlDivision agreementTreeItem = (HtmlDivision)getElementById("pagas_anuales_verano_&_navidad");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();
		
		wait4(htmlPage, htmlPage -> "PAGAS ANUALES VERANO & NAVIDAD".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));
		
		HtmlDivision salaryTableTab = (HtmlDivision)getElementById("salary_table_tab");
		LOGGER.warning("Cick on: " + salaryTableTab.asNormalizedText());
		htmlPage = salaryTableTab.click();
		
		wait4Id("category_filter");
		
		setValue("textBox_SALARIO_MENSUAL_I", "666.66");
		
		wait4Class("textBox_SALARIO_MENSUAL_I", "modify");
		
		HtmlInput input = getElementById("textBox_SALARIO_MENSUAL_I");
		
		Assert.assertEquals(input.getValue(), "666.66");
		
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
	
	// ------------------------------------------------------------------------
	
	// BUILD A FILE FROM ARRAY OF BYTES
	public static void buildFile(byte[] arr_bytes, String docName) {
		File f = new File(docName);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(arr_bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado");
		} catch (IOException e) {
			System.err.println("Error al escribir");
		}
		
		// For use this method need this where we want it
		// buildFile(htmlPage.asXml().getBytes(), "/Users/svaldepenas/Desktop/convenios.html");

	}
	
}
