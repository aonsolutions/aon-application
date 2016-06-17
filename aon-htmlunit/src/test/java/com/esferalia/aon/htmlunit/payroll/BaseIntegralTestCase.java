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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.InteractivePage;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public abstract class BaseIntegralTestCase {

	
	public static final String AON_PAYROLL_MENU_FORM = "aonContent:payrollMenu";
	
	private static WebClient webClient;
	private static HtmlPage htmlPage;

	@AfterClass
	public static void tearDown() {
		webClient.close();
	}
	
	// ------------------------------------------------------------------------
		
	protected static  void setup(String url, String user, String password) throws Exception {
		LOGGER.setLevel(Level.WARNING);
		webClient = new WebClient(BrowserVersion.FIREFOX_45);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAlertHandler(new AlertHandler() {
			@Override
			public void handleAlert(Page page, String message) {
				LOGGER.warning("ALERT '" + message + "'" );
				
			}
		});
	
		htmlPage = login(webClient, url, user, password);
	
		// Payroll Menu
		HtmlAnchor menuPayrollAnchor = htmlPage
				.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asText());
		htmlPage = menuPayrollAnchor.click();
	
		// Integral
		HtmlAnchor gwtEmployeeAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_employee");
		LOGGER.warning("Cick on: " + gwtEmployeeAnchor.asText());
		htmlPage = gwtEmployeeAnchor.click();
		
	}
	
	// ------------------------------------------------------------------------

	protected static void draft(String employeeName) throws IndexOutOfBoundsException, IOException, InterruptedException {
		String employeeId = normalize(employeeName);
		open(employeeId);
		select(employeeId + "-draft");
		wait4Text("employeeNameLabel", employeeName);
	}

	protected static boolean hasElementById(String id) {
		LOGGER.warning(GWT_DEBUG_ID_PREFIX +id + ": " +htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id));
		return  htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null;
	}

	protected static <T extends DomElement> T getElementById(String id) {
		return (T) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id);
	}

	protected static void calculate(int month) throws IOException, InterruptedException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calculate(calendar.getTime());
	}

	protected static void calculat3(Date date) throws IOException, InterruptedException {
		getElementById("monthListBox").click();
		((HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$tB de %1$tY", date)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	
		wait4Text("periodLabel", String.format( new Locale("es","ES"),"1/%2$d/%1$d - %3$d/%2$d/%1$d", year, month, end));
	}


	protected static void calculate(Date date) throws IOException, InterruptedException {
		getElementById("monthListBox").click();
		((HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$tB de %1$tY", date)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/%2$d/%1$d - [0-9]+/%2$d/%1$d", year, month, end));
	}

	protected static void settle(Date date) throws IOException, InterruptedException {
		
		HtmlSelect typeSelect = getElementById("typeListBox");
		typeSelect.click();
		HtmlOption settleOption = typeSelect.getOptionByValue("SETTLE");
		settleOption.click();
		
		getElementById("dateListBox").click();
		((HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$te de %1$tB de %1$tY", date)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/%2$d/%1$d - [0-9]+/%2$d/%1$d", year, month, end));
	}

	protected static void extra(Date issueDate, Date endDate) throws IOException, InterruptedException {
		
		HtmlSelect typeSelect = getElementById("typeListBox");
		typeSelect.click();
		HtmlOption settleOption = typeSelect.getOptionByValue("EXTRA");
		settleOption.click();
		
		getElementById("dateListBox").click();
		((HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$te de %1$tB de %1$tY", issueDate)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(endDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.get(Calendar.DAY_OF_MONTH);
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/[0-9]+/[0-9]+ - %3$d/%2$d/%1$d", year, month, end));
	}

	protected static void wait4Id(String id) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
	}

	protected static void wait4Text(String id, String text) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("Found el: '" + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent()+"'");
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getTextContent().trim().equals(text.trim()));
	}

	protected static void wait4Regex(String id, String regex) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("Found el: '" + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent()+"'");
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getTextContent().matches(regex));
	}

	protected static void assertText(String id, String text) throws ParseException {
		HtmlElement el = getElementById(id);
		Assert.assertEquals(text, el.getTextContent());
	}

	protected static void assertValue(String id, String value) throws ParseException {
		HtmlInput input = getElementById(id);
		Assert.assertEquals(value, input.getValueAttribute());
	}

	protected static void assertValue(String id, double value) throws ParseException {
		HtmlInput input = getElementById(id);
		Assert.assertEquals(value, NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(input.getValueAttribute()).doubleValue(), 0.04);
	}

	protected static void open(String id) throws IndexOutOfBoundsException, IOException {
		((HtmlImage)((HtmlTable)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id).getFirstChild()).getRow(0).getCell(0).getFirstChild()).click();
		LOGGER.warning("Cick on: " + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id + "-content").getTextContent());
	}

	protected static void close(String id) throws IndexOutOfBoundsException, IOException {
		((HtmlImage)((HtmlTable)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id).getFirstChild()).getRow(0).getCell(0).getFirstChild()).click();
		LOGGER.warning("Cick on: " + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id + "-content").getTextContent());
	}

	protected static void select(String id) throws IndexOutOfBoundsException, IOException, InterruptedException {
		htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).click();
		LOGGER.warning("Cick on: " + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id + "-content").getTextContent());
	}

	protected static String normalize(String str) {
		return str
		.toLowerCase()
		.replace('\u00E1', 'a')
		.replace('\u00E9', 'e')
		.replace('\u00ED', 'i')
		.replace('\u00F3', 'o')
		.replace('\u00FA', 'u')
		.replace('\u00F1', 'n')
		.replace('\u00FC', 'u')
		.replaceAll("\\s+", "_")
		;
		
	}

	public BaseIntegralTestCase() {
		super();
	}

}