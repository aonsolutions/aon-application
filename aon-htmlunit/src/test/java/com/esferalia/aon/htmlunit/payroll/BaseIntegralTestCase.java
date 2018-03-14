package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.GWT_DEBUG_ID_PREFIX;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
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
import com.gargoylesoftware.htmlunit.javascript.host.event.KeyboardEvent;

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
		webClient.setJavaScriptErrorListener( new  JavaScriptErrorListener() {
			
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
		
		DomElement draft = getElementById(employeeId + "-draft");
		if ( draft == null || !draft.isDisplayed() )
			open(employeeId);
		
		select(employeeId + "-draft");
		wait4Text("employeeNameLabel", employeeName);
	}
	
	protected static void events(String employeeName) throws IndexOutOfBoundsException, IOException, InterruptedException {
		String employeeId = normalize(employeeName);
		
		DomElement draft = getElementById(employeeId + "-events");
		if ( draft == null || !draft.isDisplayed() )
			open(employeeId);
		
		select(employeeId + "-events");
		wait4Id("dias_vacaciones");
	}
	
	protected static void calendar(String employeeName) throws IndexOutOfBoundsException, IOException, InterruptedException {
		String employeeId = normalize(employeeName);
		
		DomElement draft = getElementById(employeeId + "-employeecalendar");
		if ( draft == null || !draft.isDisplayed() )
			open(employeeId);
		
		select(employeeId + "-employeecalendar");
		wait4Id("31_11");
	}

	protected static boolean hasElementById(String id) {
		LOGGER.warning(GWT_DEBUG_ID_PREFIX +id + ": " +htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id));
		return  htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null;
	}

	protected static <T extends DomElement> T getElementById(String id) {
		return (T) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id);
	}

	protected static void calculate(int month) throws IOException, InterruptedException, ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		
		calculate(calendar.getTime());
	}

	protected static void calculate(int month, int year) throws IOException, InterruptedException, ParseException {
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		
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


	protected static void calculate(Date date) throws IOException, InterruptedException, ParseException {
		HtmlSelect typeSelect = getElementById("typeListBox");
		typeSelect.click();
		HtmlOption salaryOption = typeSelect.getOptionByValue("SALARY");
		salaryOption.click();

		getElementById("monthListBox").click();
		
		scroll2MonthListBox(date);
		
		((HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$tB de %1$tY", date)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/%2$d/%1$d - [0-9]+/%2$d/%1$d", year, month, end));
	}

	protected static void selectSaveTo(String value) throws IOException, InterruptedException {
		getElementById("datesListBox").click();
		((HtmlSelect)getElementById("datesListBox")).getOptionByValue(value).click();
	}

	protected static void settle(Date date) throws IOException, InterruptedException, ParseException {
		
		HtmlSelect typeSelect = getElementById("typeListBox");
		typeSelect.click();
		HtmlOption settleOption = typeSelect.getOptionByValue("SETTLE");
		settleOption.click();
		
		getElementById("dateListBox").click();
		
		scroll2DateListBox(date);
		

		((HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$te de %1$tB de %1$tY", date)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/%2$d/%1$d - [0-9]+/%2$d/%1$d", year, month, end));
	}

	protected static void extra(Date issueDate, Date endDate) throws IOException, InterruptedException, ParseException {
		
		HtmlSelect typeSelect = getElementById("typeListBox");
		typeSelect.click();
		HtmlOption settleOption = typeSelect.getOptionByValue("EXTRA");
		settleOption.click();
		
		getElementById("dateListBox").click();
		
		//scroll2DateListBox(issueDate);
		
		((HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$te de %1$tB de %1$tY", issueDate)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(endDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.get(Calendar.DAY_OF_MONTH);
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/[0-9]+/[0-9]+ - %3$d/%2$d/%1$d", year, month, end));
	}
	
	protected static void delay(Date startDate, Date endDate) throws IOException, InterruptedException, ParseException {
		
		HtmlSelect typeSelect = getElementById("typeListBox");
		typeSelect.click();
		HtmlOption settleOption = typeSelect.getOptionByValue("DELAY");
		settleOption.click();
		
		wait4Id("fromMonthListBox");
		getElementById("fromMonthListBox").click();
		scroll2FromMonthListBox(startDate);
		((HtmlSpan)((HtmlDivision)getElementById("fromMonthListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$tB de %1$tY", startDate)+"']")).click();

		wait4Id("monthListBox");
		getElementById("monthListBox").click();
		scroll2MonthListBox(endDate);

		((HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("//span[text()='"+String.format( new Locale("es","ES"),"%1$tB de %1$tY", endDate)+"']")).click();
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(startDate);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH)+1;
		calendar.setTime(endDate);
		int endYear = calendar.get(Calendar.YEAR);
		int endMonth = calendar.get(Calendar.MONTH)+1;
	
		wait4Regex("periodLabel", String.format( new Locale("es","ES"),"[0-9]+/%2$d/%1$d - [0-9]+/%4$d/%3$d", startYear, startMonth, endYear, endMonth));
	}
	

	protected static void scroll2DateListBox(Date date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMMM 'de' yyyy", new Locale("es","ES"));

		HtmlSpan firstSpan = (HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("div/div/span");
		Date firstDate = dateFormat.parse(firstSpan.getTextContent());
		LOGGER.warning("First visible date is : " + dateFormat.format(firstDate) );
		while ( firstDate.after(date) )  {
			LOGGER.warning("Opps we need to scroll up to : " + dateFormat.format(date) );
			htmlPage.setFocusedElement(firstSpan);
			firstSpan.type(KeyboardEvent.DOM_VK_PAGE_UP);
			firstSpan = (HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("div/div/span");
			firstDate = dateFormat.parse(firstSpan.getTextContent());
			LOGGER.warning("First visible date is : " + dateFormat.format(firstDate) );
		}

		HtmlSpan lastSpan = (HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("div/div[last()]/span");
		Date lastDate = dateFormat.parse(lastSpan.getTextContent());
		LOGGER.warning("Last visible date is : " + dateFormat.format(lastDate) );
		while ( lastDate.before(date) )  {
			LOGGER.warning("Opps we need to scroll down to : " + dateFormat.format(date) );
			htmlPage.setFocusedElement(firstSpan);
			lastSpan.type(KeyboardEvent.DOM_VK_PAGE_DOWN);
			lastSpan = (HtmlSpan)((HtmlDivision)getElementById("dateListBox-celllist")).getFirstByXPath("div/div[last()]/span");
			lastDate = dateFormat.parse(firstSpan.getTextContent());
			LOGGER.warning("Last visible date is : " + dateFormat.format(lastDate) );
		}
		
	}

	protected static void scroll2MonthListBox(Date date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM 'de' yyyy", new Locale("es","ES"));
		
		HtmlSpan firstSpan = (HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("div/div/span");
		Date firstDate = dateFormat.parse(firstSpan.getTextContent());
		LOGGER.warning("First visible month is : " + dateFormat.format(firstDate) );
		while ( firstDate.after(date) )  {
			LOGGER.warning("Opps we need to scroll up to : " + dateFormat.format(date) );
			htmlPage.setFocusedElement(firstSpan);
			firstSpan.type(KeyboardEvent.DOM_VK_PAGE_UP);
			firstSpan = (HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("div/div/span");
			firstDate = dateFormat.parse(firstSpan.getTextContent());
			LOGGER.warning("First visible date is : " + dateFormat.format(firstDate) );
		}

		HtmlSpan lastSpan = (HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("div/div[last()]/span");
		Date lastDate = dateFormat.parse(lastSpan.getTextContent());
		LOGGER.warning("Last visible month is : " + dateFormat.format(lastDate) );
		while ( lastDate.before(date) )  {
			LOGGER.warning("Opps we need to scroll down to : " + dateFormat.format(date) );
			htmlPage.setFocusedElement(firstSpan);
			lastSpan.type(KeyboardEvent.DOM_VK_PAGE_DOWN);
			lastSpan = (HtmlSpan)((HtmlDivision)getElementById("monthListBox-celllist")).getFirstByXPath("div/div[last()]/span");
			lastDate = dateFormat.parse(firstSpan.getTextContent());
			LOGGER.warning("Last visible date is : " + dateFormat.format(lastDate) );
		}
		
	}
	
	protected static void scroll2FromMonthListBox(Date date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM 'de' yyyy", new Locale("es","ES"));
		
		HtmlSpan firstSpan = (HtmlSpan)((HtmlDivision)getElementById("fromMonthListBox-celllist")).getFirstByXPath("div/div/span");
		Date firstDate = dateFormat.parse(firstSpan.getTextContent());
		LOGGER.warning("First visible month is : " + dateFormat.format(firstDate) );
		while ( firstDate.after(date) )  {
			LOGGER.warning("Opps we need to scroll up to : " + dateFormat.format(date) );
			htmlPage.setFocusedElement(firstSpan);
			firstSpan.type(KeyboardEvent.DOM_VK_PAGE_UP);
			firstSpan = (HtmlSpan)((HtmlDivision)getElementById("fromMonthListBox-celllist")).getFirstByXPath("div/div/span");
			firstDate = dateFormat.parse(firstSpan.getTextContent());
			LOGGER.warning("First visible date is : " + dateFormat.format(firstDate) );
		}

		HtmlSpan lastSpan = (HtmlSpan)((HtmlDivision)getElementById("fromMonthListBox-celllist")).getFirstByXPath("div/div[last()]/span");
		Date lastDate = dateFormat.parse(lastSpan.getTextContent());
		LOGGER.warning("Last visible month is : " + dateFormat.format(lastDate) );
		while ( lastDate.before(date) )  {
			LOGGER.warning("Opps we need to scroll down to : " + dateFormat.format(date) );
			htmlPage.setFocusedElement(firstSpan);
			lastSpan.type(KeyboardEvent.DOM_VK_PAGE_DOWN);
			lastSpan = (HtmlSpan)((HtmlDivision)getElementById("fromMonthListBox-celllist")).getFirstByXPath("div/div[last()]/span");
			lastDate = dateFormat.parse(firstSpan.getTextContent());
			LOGGER.warning("Last visible date is : " + dateFormat.format(lastDate) );
		}
		
	}

	protected boolean isDisplayed(String id) {
		DomElement el = getElementById(id);
		return  el != null && el.isDisplayed();
	}

	protected static void wait4Id(String id) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
	}


	protected static void wait4Text(String id, String text) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("Found el: [" + id + "]'" + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent()+"'");
		wait4(htmlPage,
				htmlPage -> {
					LOGGER.warning(htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent().trim()+"'== '" + text.trim() + "'");
					return htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getTextContent().trim().equals(text.trim());
					}
			);
	}

	protected static void wait4Value(String id, String value) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("wait4Value : [ "+ id +"] '" + ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id )).getValueAttribute().trim() +"' = '" +value.trim()+"'");
		wait4(htmlPage,
				htmlPage -> ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id)).getValueAttribute().trim().equals(value.trim()));
	}

	protected static void wait4Value(String id, Double value) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("wait4Value : [ "+ id +"] '" + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent()+"'");
		
		wait4(htmlPage,
				htmlPage -> {
						String attrStr = ((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id)).getValueAttribute();
						Double attrNum;
						try {
							attrNum = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(attrStr).doubleValue();
							return Math.abs(value - attrNum ) < 0.4;
						} catch (ParseException e) {
							return  false;
						}
					});
	}

	protected static void wait4Disabled(String id, boolean disabled) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("Found el: '" + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent()+"'");
		wait4(htmlPage,
				htmlPage -> ((HtmlButton)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id)).isDisabled() == disabled );
	}

	protected static void wait4Regex(String id, String regex) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id) != null);
		LOGGER.warning("Found el: '" + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).getTextContent()+"'");
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getTextContent().matches(regex));
	}

	protected static void wait4Class(String id, String clazz) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id).getAttribute("class").contains(clazz));
	}

	protected static void assertNotElement(String id) throws ParseException {
		Assert.assertNull(getElementById(id));
	}

	protected static void assertElement(String id) throws ParseException {
		Assert.assertNotNull(getElementById(id));
	}

	protected static void assertText(String id, String text) throws ParseException {
		HtmlElement el = getElementById(id);
		Assert.assertEquals(text, el.getTextContent());
	}

	protected static void assertText(String id, double value) throws ParseException {
		HtmlElement el = getElementById(id);
		Assert.assertEquals(value, NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(el.getTextContent()).doubleValue(), 0.04 );
	}

	protected static void assertValue(String id, String value) throws ParseException {
		HtmlInput input = getElementById(id);
		Assert.assertEquals(value, input.getValueAttribute());
	}

	protected static void assertValue(String id, double value) throws ParseException {
		HtmlInput input = getElementById(id);
		Assert.assertEquals(value, NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(input.getValueAttribute()).doubleValue(), 0.04);
	}

	protected static void assertDisabled(String id, boolean disabled) throws InterruptedException {
		HtmlButton button = (HtmlButton)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +id);
		Assert.assertEquals(button.isDisabled(), disabled);
	}


	protected static double getValue(String id) throws ParseException {
		HtmlInput input = getElementById(id);
		return NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(input.getValueAttribute()).doubleValue();
	}

	protected static double getText(String id) throws ParseException {
		HtmlElement el = getElementById(id);
		return NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(el.getTextContent()).doubleValue();
	}

	protected static void setValue(String id, String text) throws ParseException {
		HtmlInput input = getElementById(id);
		input.focus();
		input.setValueAttribute(text);
		input.blur();
	}


	protected static void selectOption(String id, String value) throws IOException {
		HtmlSelect htmlSelect = getElementById(id);
		htmlSelect.focus();
		htmlSelect.click();
		htmlSelect.getOptionByValue(value).click();
		htmlSelect.blur();
	}


	protected static DomElement open(String id) throws IndexOutOfBoundsException, IOException {
		DomElement idElement =  htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id);
		((HtmlImage)((HtmlTable)idElement.getFirstChild()).getRow(0).getCell(0).getFirstChild()).click();
		LOGGER.warning("Open [" + id + "]: " + htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id + "-content").getTextContent());
		return idElement;
	}

	protected static void close(String id) throws IndexOutOfBoundsException, IOException {
		((HtmlImage)((HtmlTable)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id).getFirstChild()).getRow(0).getCell(0).getFirstChild()).click();
		LOGGER.warning("Close [" + id + "]: "+ htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id + "-content").getTextContent());
	}

	protected static void click(String id) throws IndexOutOfBoundsException, IOException, InterruptedException {
		htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).click();
		LOGGER.warning("Cick on: " + id);
	}
	
	protected static void select(String id) throws IndexOutOfBoundsException, IOException, InterruptedException {
		htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id ).click();
		LOGGER.warning("Select ["  + id + "]: "+ htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id + "-content").getTextContent());
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