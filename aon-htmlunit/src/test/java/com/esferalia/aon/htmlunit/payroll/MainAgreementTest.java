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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

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
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

public class MainAgreementTest {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.payroll.url";
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

		webClient.setAlertHandler((page, message) -> LOGGER.warning("ALERT '" + message + "'"));

		webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {

			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
				LOGGER.severe("Warn " + message + "");
			}

			@Override
			public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
				LOGGER.severe("Timeout " + executionTime + "ms");
			}

			@Override
			public void scriptException(HtmlPage page, ScriptException scriptException) {
				LOGGER.severe("Script Exception [" + scriptException.getFailingLine() + ","
						+ scriptException.getFailingLineNumber() + "] '" + scriptException.getMessage() + "'");

			}

			@Override
			public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
				LOGGER.severe("Malformed Script URL '" + url + "' " + malformedURLException.getMessage() + "'");

			}

			@Override
			public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
				LOGGER.severe("Script Error '" + scriptUrl + "' " + exception.getMessage() + "'");
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
		HtmlAnchor gwtAgreementAnchor = htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement2");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asNormalizedText());
		htmlPage = gwtAgreementAnchor.click();

		wait4Id("agreements");
	}

	@AfterClass
	public static void tearDown() {
		webClient.close();
	}

	@Test
	public void TestSalaryTable() throws Exception {
		
		loadStartWarsAgreementSalaryTableTab();
		
		// New Tab for 01/01/2018
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		newSalaryDataTab(calendar.getTime());
		
		setValue("textBox_SALARIO_MENSUAL_I", "666 + 0.66");
		wait4Class("textBox_SALARIO_MENSUAL_I", "modify");
		wait4Value("textBox_SALARIO_MENSUAL_I", "666,66");
		
		getElementById("acceptButton").click();

		wait4NoClass("textBox_SALARIO_MENSUAL_I", "modify");
		
		HtmlDivision datesTabs = getElementById("datesTabs");
		Assert.assertEquals(2, datesTabs.getChildElementCount());
		
		getElementById("deleteSalaryTabButton").click();
		wait4Id("acceptDialogButton");
		HtmlButton acceptDialogButton = getElementById("acceptDialogButton");
		htmlPage = acceptDialogButton.click();
		
		wait4(htmlPage, htmlPage -> ((HtmlDivision) getElementById("datesTabs")).getChildElementCount()==1);
		
		datesTabs = getElementById("datesTabs");
		Assert.assertEquals(1, datesTabs.getChildElementCount());

		// 01-01-2018
		newSalaryDataTab(calendar.getTime());
		
		wait4Id("textBox_SALARIO_MENSUAL_I");
		
		HtmlInput htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		String value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		setValue("textBox_SALARIO_MENSUAL_I", "888 + 0.88");
		wait4Value("textBox_SALARIO_MENSUAL_I", "888,88");

		// 01-01-2019
		calendar.add(Calendar.YEAR, 1);
		newSalaryDataTab(calendar.getTime());
		
		wait4Id("textBox_SALARIO_MENSUAL_I");
		
		htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		setValue("textBox_SALARIO_MENSUAL_I", "999 + 0.99");
		wait4Value("textBox_SALARIO_MENSUAL_I", "999,99");
		
		getElementById("acceptButton").click();
		
		wait4NoClass("textBox_SALARIO_MENSUAL_I", "modify");
		
		datesTabs = getElementById("datesTabs");
		Assert.assertEquals(3, datesTabs.getChildElementCount());
		
		// Borramos los tramos
		getElementById("deleteSalaryTabButton").click();
		wait4Id("acceptDialogButton");
		htmlPage = getElementById("acceptDialogButton").click();
		
		wait4(htmlPage, htmlPage -> ((HtmlDivision) getElementById("datesTabs")).getChildElementCount()==2);
		
		datesTabs = getElementById("datesTabs");
		Assert.assertEquals(2, datesTabs.getChildElementCount());
		
		getElementById("deleteSalaryTabButton").click();
		wait4Id("acceptDialogButton");
		htmlPage = getElementById("acceptDialogButton").click();
		
		wait4(htmlPage, htmlPage -> ((HtmlDivision) getElementById("datesTabs")).getChildElementCount()==1);
		
		datesTabs = getElementById("datesTabs");
		Assert.assertEquals(1, datesTabs.getChildElementCount());
	}
	
	@Test
	public void TestEstatutoDeLosTrabajadores() throws Exception {

		wait4Id("mostrarConvenios");

		// Mostramos los convenios ocultos
		HtmlButton agreementsButton = (HtmlButton) getElementById("mostrarConvenios");
		if (!agreementsButton.getAttribute("class").contains("aon_icon_visibility_off"))
			agreementsButton.click();

		wait4Id("estatuto_de_los_trabajadores");

		// Click en el convenio EstatutoDeLosTrabajadores
		HtmlDivision agreementTreeItem = (HtmlDivision) getElementById("estatuto_de_los_trabajadores");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();

		// Comprobamos que el boton de eliminar tanto del toolbar de agreements como el del contextMenu esta oculta para EstatutoDeLosTrabajadores
		Pattern hidden = Pattern.compile("display\\s*:\\s*none");

		HtmlButton draftButton = (HtmlButton) getElementById("draftButton");
		
		Assert.assertEquals(true, hidden.matcher(draftButton.getAttribute("style")).find());

		getElementById("collapseAllButton").click();
		DomElement deleteItem = getElementById("deleteItem");
		Assert.assertEquals(true, hidden.matcher(deleteItem.getAttribute("style")).find());
		
		// Comprobamos que los campos descripcion y ssNumber han cargado y estan en readOnly
		wait4(htmlPage, htmlPage -> "ESTATUTO DE LOS TRABAJADORES".equals(((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));
		
		Assert.assertEquals(((HtmlInput) getElementById("descriptionTextBox")).isReadOnly(), true);
		Assert.assertEquals(((HtmlInput) getElementById("ssNumberTextBox")).isReadOnly(), true);

		// Comprobamos que el toolBar de AgreementPreview tiene los botones como se esperan (acceptButton, infoButton)
		Assert.assertEquals("infoButton", ((HtmlButton) getElementById("infoButton")).isDisabled(), false);

	}

	@Test
	public void TestStarsWarsAgreement() throws Exception {

		wait4Id("star_wars_agreement");

		// Click en el convenio StarsWarsAgreement
		HtmlDivision agreementTreeItem = (HtmlDivision) getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();

		// Comprobamos el campo descripcion
		wait4(htmlPage, htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));
		
		// Esperamos a la tabla de Devengos
		wait4Id("agreementPaymentDG");
		
		Assert.assertEquals("acceptButton", ((HtmlButton) getElementById("acceptButton")).isDisabled(), true);
		
		// Obtenemos tabla de Devengos
		HtmlTable paymentsTable = getElementById("agreementPaymentDG");
		
		if(null != paymentsTable) {
			Assert.assertEquals(paymentsTable.getRows().size(), 4);
			
			HtmlButton newPaymentButton = (HtmlButton) getElementById("newPaymentButton");
			newPaymentButton.click();
			
			wait4Id("descriptionSuggest");
			setValue("descriptionSuggest", "0001 PLUS SALARIAL MENSUAL ( PLUS_SALARIAL )");
			
			HtmlButton acceptNewPaymentButton = (HtmlButton) getElementById("acceptNewPaymentButton");
			htmlPage = acceptNewPaymentButton.click();

			Assert.assertEquals(paymentsTable.getRows().size(), 5);
			
//			HtmlTableRow newRow = (HtmlTableRow)paymentsTable.getLastChild();
//			
//			Assert.assertEquals(newRow.getAttribute("class").contains("modify"), true);
			
			HtmlButton deleteNewPaymentButton = (HtmlButton) getElementById("deletePaymentTabButton-" + (paymentsTable.getRows().size() - 1));
			htmlPage = deleteNewPaymentButton.click();
			
			wait4Id("acceptDialogButton");
			HtmlButton acceptDeleteNewPaymentButton = (HtmlButton) getElementById("acceptDialogButton");
			htmlPage = acceptDeleteNewPaymentButton.click();
			
			wait4Id("acceptButton");
			Assert.assertEquals(((HtmlButton) getElementById("acceptButton")).isDisabled(), false);
			htmlPage = ((HtmlButton) getElementById("acceptButton")).click();
			
			Assert.assertEquals(paymentsTable.getRows().size(), 4);
			
			Assert.assertEquals(((HtmlButton) getElementById("acceptButton")).isDisabled(), true);
		} else
			throw new IllegalArgumentException("No se ha podido cargar la tabla de devengos");
		
		
		// Obtenemos tabla de Devengos (extras)
		wait4Id("agreementExtraPaymentDG");
		
		HtmlTable paymentsExtraTable = getElementById("agreementExtraPaymentDG");
			
		if(null != paymentsExtraTable) {
			Assert.assertEquals(paymentsExtraTable.getRows().size(), 2);
		} else
			throw new IllegalArgumentException("No se ha podido cargar la tabla de devengos (extras)");

	}

	@Test
	public void TestPrintPreview() throws Exception {
		wait4Id("convenio_colectivo_de_oficinas_y_despachos_para_madrid");

		HtmlDivision agreementTreeItem = (HtmlDivision) getElementById("convenio_colectivo_de_oficinas_y_despachos_para_madrid");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();

		wait4(htmlPage, htmlPage -> "CONVENIO COLECTIVO DE OFICINAS Y DESPACHOS PARA MADRID".equals(((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));

		wait4Id("printPreviewButton");
		wait4Id("pdfNotLoaded");

		htmlPage = getElementById("printPreviewButton").click();
		wait4Id("printPreviewViewer");
		
		wait4Id("closeSimulatorBtn");
		wait4Id("pdfLoaded");
		
		htmlPage = getElementById("closeSimulatorBtn").click();
		wait4Id("pdfNotLoaded");
	}

	// ------------------------------------------------------------------------

	protected <T extends DomElement> T getElementById(String id) {
		return (T) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id);
	}

	protected static void wait4Id(String id) throws InterruptedException {
		wait4(htmlPage, htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id) != null);
	}

	protected static void wait4NoId(String id) throws InterruptedException {
		wait4(htmlPage, htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id) == null);
	}

	protected void setValue(String id, String text) throws ParseException {
		HtmlInput input = getElementById(id);
		input.focus();
		input.setValueAttribute(text);
		input.blur();
	}

	protected void wait4Value(String id, String value) throws InterruptedException {
		wait4(htmlPage, htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id) != null);
		LOGGER.warning("wait4Value : [ " + id + "] '"
				+ ((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id)).getValueAttribute().trim() + "' = '"
				+ value.trim() + "'");
		wait4(htmlPage, htmlPage -> ((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id)).getValueAttribute()
				.trim().equals(value.trim()));
	}

	protected void wait4Class(String id, String clazz) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id).getAttribute("class").contains(clazz));
	}

	protected void wait4NoClass(String id, String clazz) throws InterruptedException {
		wait4(htmlPage,
				htmlPage -> !htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + id).getAttribute("class").contains(clazz));
	}
	
	// ------------------------------------------------------------------------
	
	private void loadStartWarsAgreementSalaryTableTab() throws Exception {
		// Load agian
		htmlPage = webClient.getPage(url);
		
		// Payroll Menu
		HtmlAnchor menuPayrollAnchor = htmlPage.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asNormalizedText());
		htmlPage = menuPayrollAnchor.click();
		wait4(htmlPage, htmlPage -> htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement2") != null);
		
		// MainAgreement
		HtmlAnchor gwtAgreementAnchor = htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement2");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asNormalizedText());
		htmlPage = gwtAgreementAnchor.click();
		
		wait4Id("agreements");
		
		wait4Id("star_wars_agreement");

		// Click en el convenio StarsWarsAgreement
		HtmlDivision agreementTreeItem = (HtmlDivision) getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();

		// Comprobamos el campo descripcion
		wait4(htmlPage, htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));
		
		wait4Id("category_filter");
	}

	protected void newSalaryDataTab(Date date) throws Exception {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		int months = -1;
		for (; calendar.getTime().after(date); months++)
			calendar.add(Calendar.MONTH, -1);

		getElementById("newSalaryTabButton").click();
		wait4Id("moreDatePicker");
		HtmlElement moreDatePicker = getElementById("moreDatePicker");

		HtmlTable datePickerMonthSelector = moreDatePicker.getOneHtmlElementByAttribute(HtmlTable.TAG_NAME, "class", "datePickerMonthSelector");
		HtmlSelect datePickerMonth = (HtmlSelect) datePickerMonthSelector.getRow(0).getCell(1).getFirstElementChild();
		String month = getMonth(date.getMonth());
		datePickerMonth.setSelectedAttribute(month, true);
		
		HtmlSelect datePickerYear = (HtmlSelect) datePickerMonthSelector.getRow(0).getCell(2).getFirstElementChild();
		String year = (date.getYear() + 1900) + "";
		datePickerYear.setSelectedAttribute(year, true);
		
		 List<HtmlElement> datePickerDays = moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class", "datePickerDay ");
		 String day = String.valueOf(date.getDate());
		 Optional<HtmlElement> datePickerDayEl = datePickerDays.stream().filter(datePickerDay -> datePickerDay.getTextContent().equals(day)).findFirst();
		 datePickerDayEl.ifPresent(datePickerDay -> {
			try {
				htmlPage = datePickerDay.click();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		 
		 wait4NoId("moreDatePicker");
	}

	private String getMonth(int month) {
		switch (month) {
		case 0:
			return "Ene.";
		case 1:
			return "Feb.";
		case 2:
			return "Mar.";
		case 3:
			return "Abr.";
		case 4:
			return "May.";
		case 5:
			return "Jun.";
		case 6:
			return "Ju.";
		case 7:
			return "Ago";
		case 8:
			return "Sep.";
		case 9:
			return "Oct.";
		case 10:
			return "Nov.";
		case 11:
			return "Dic.";
		default:
			return null;
		}
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
//		buildFile(htmlPage.asXml().getBytes(),  "/Users/svaldepenas/Desktop/estatutoTrabajadores.html");

	}

}
