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
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.javascript.host.event.MouseEvent;
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
		Assert.assertEquals("acceptButton", ((HtmlButton) getElementById("acceptButton")).isDisabled(), true);
		Assert.assertEquals("infoButton", ((HtmlButton) getElementById("infoButton")).isDisabled(), false);

		
		// Cehck tabs visibility
		Assert.assertEquals(false, hidden.matcher(getElementById("agreementPreviewButton").getAttribute("style")).find());
		Assert.assertEquals(true, hidden.matcher(getElementById("agreementLevelTabButton").getAttribute("style")).find());
		Assert.assertEquals(true, hidden.matcher(getElementById("agreementSalaryTableTabButton").getAttribute("style")).find());
		Assert.assertEquals(true, hidden.matcher(getElementById("agreementPaymentTabButton").getAttribute("style")).find());

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

		// Click en el tab de Devengos
		HtmlDivision salaryTableTab = (HtmlDivision)getElementById("agreementPaymentTabButton");
		LOGGER.warning("Cick on: " + salaryTableTab.asNormalizedText());
		htmlPage = salaryTableTab.click();
		
		// Esperamos a la tabla de Devengos
		wait4Id("agreementPaymentDG");
		
		Assert.assertEquals("savePaymentButton", ((HtmlButton) getElementById("acceptButton")).isDisabled(), true);
		
		// Obtenemos tabla de Devengos
		HtmlTableBody paymentsTable = null;
		Optional<DomNode> nodeOpt = htmlPage.querySelectorAll("#" + GWT_DEBUG_ID_PREFIX + "agreementPaymentDG div").stream().filter(node -> node.getVisibleText().equals("COMPLEMENTO PERSONAL DE ANTIGÜEDAD")).findAny();
		if(nodeOpt.isPresent()) {
			DomNode node = nodeOpt.get();
			paymentsTable = (HtmlTableBody)node.getParentNode().getParentNode().getParentNode();
		}
			
		if(null != paymentsTable) {
			Assert.assertEquals(paymentsTable.getRows().size(), 4);
			
			HtmlButton newPaymentButton = (HtmlButton) getElementById("newPaymentButton");
			newPaymentButton.click();
			
			wait4Id("descriptionSuggest");
			setValue("descriptionSuggest", "0001 PLUS SALARIAL MENSUAL ( PLUS_SALARIAL )");
			
			HtmlButton acceptNewPaymentButton = (HtmlButton) getElementById("acceptNewPaymentButton");
			htmlPage = acceptNewPaymentButton.click();

			Assert.assertEquals(paymentsTable.getRows().size(), 5);
			
			HtmlTableRow newRow = (HtmlTableRow)paymentsTable.getLastChild();
			
			Assert.assertEquals(newRow.getAttribute("class").contains("modify"), true);
			
			HtmlButton deleteNewPaymentButton = (HtmlButton) getElementById("deletePaymentTabButton-" + (paymentsTable.getRows().size() - 1));
			htmlPage = deleteNewPaymentButton.click();
			
			wait4Id("acceptDialogButton");
			HtmlButton acceptDeleteNewPaymentButton = (HtmlButton) getElementById("acceptDialogButton");
			htmlPage = acceptDeleteNewPaymentButton.click();
			
			wait4Id("savePaymentButton");
			Assert.assertEquals(((HtmlButton) getElementById("savePaymentButton")).isDisabled(), false);
			htmlPage = ((HtmlButton) getElementById("savePaymentButton")).click();
			
			Assert.assertEquals(paymentsTable.getRows().size(), 4);
			
			Assert.assertEquals(((HtmlButton) getElementById("savePaymentButton")).isDisabled(), true);
		} else
			throw new IllegalArgumentException("No se ha podido cargar la tabla de devengos");

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

	@Ignore("Fix soon")
	@Test
	public void TestSalaryTable() throws Exception {

		wait4Id("star_wars_agreement");

		HtmlDivision agreementTreeItem = (HtmlDivision) getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();

		wait4(htmlPage, htmlPage -> "STAR WARS AGREEMENT".equals(
				((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));

		// New Tab for 01/01/2018
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		newPrevSalaryDataTab(calendar.getTime());

		wait4Id("textBox_SALARIO_MENSUAL_I");

		setValue("textBox_SALARIO_MENSUAL_I", "666 + 0.66");
		wait4Value("textBox_SALARIO_MENSUAL_I", "666.66");

		getElementById("acceptButton").click();

		wait4NoClass("textBox_SALARIO_MENSUAL_I", "aon-icon-changed");

		HtmlTable salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size());

		getElementById("deleteButton_01_01_2018").click();
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		getElementById("acceptButton").click();
		System.out.println(getElementById("acceptButton").asXml());
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		htmlPage = webClient.getPage(url);
		// Payroll Menu
		HtmlAnchor menuPayrollAnchor = htmlPage.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asNormalizedText());
		htmlPage = menuPayrollAnchor.click();
		// MainAgreement
		HtmlAnchor gwtAgreementAnchor = htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asNormalizedText());
		htmlPage = gwtAgreementAnchor.click();
		wait4Id("star_wars_agreement");
		agreementTreeItem = (HtmlDivision) getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();
		wait4(htmlPage, htmlPage -> "STAR WARS AGREEMENT".equals(
				((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		// 01-01-2018
		newPrevSalaryDataTab(calendar.getTime());
		wait4Id("textBox_SALARIO_MENSUAL_I");
		HtmlInput htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		String value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		setValue("textBox_SALARIO_MENSUAL_I", "888 + 0.88");
		wait4Value("textBox_SALARIO_MENSUAL_I", "888.88");
//		getElementById("acceptButton").click();
//		wait4NoClass("textBox_SALARIO_MENSUAL_I", "aon-icon-changed");
//		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
//		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		// 01-01-2019
		calendar.add(Calendar.YEAR, 1);
		newNextSalaryDataTab(calendar.getTime());
		wait4Id("textBox_SALARIO_MENSUAL_I");
		htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		setValue("textBox_SALARIO_MENSUAL_I", "999 + 0.99");
		wait4Value("textBox_SALARIO_MENSUAL_I", "999.99");
		getElementById("acceptButton").click();
		wait4NoClass("textBox_SALARIO_MENSUAL_I", "aon-icon-changed");
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(6, salaryToggleButtonsPanel.getRow(0).getCells().size());

		getElementById("deleteButton_01_01_2018").click();
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size());

		getElementById("acceptButton").click();
		System.out.println(getElementById("acceptButton").asXml());
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size());

		htmlPage = webClient.getPage(url);
		// Payroll Menu
		menuPayrollAnchor = htmlPage.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asNormalizedText());
		htmlPage = menuPayrollAnchor.click();
		// MainAgreement
		gwtAgreementAnchor = htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asNormalizedText());
		htmlPage = gwtAgreementAnchor.click();
		wait4Id("star_wars_agreement");
		agreementTreeItem = (HtmlDivision) getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();
		wait4(htmlPage, htmlPage -> "STAR WARS AGREEMENT".equals(
				((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size());

		// 01-01-2018
		calendar.add(Calendar.YEAR, -1);
		newPrevSalaryDataTab(calendar.getTime());
		wait4Id("textBox_SALARIO_MENSUAL_I");
		htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		getElementById("deleteButton_01_01_2018").click();
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size());
		getElementById("deleteButton_01_01_2019").click();
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		getElementById("acceptButton").click();
		System.out.println(getElementById("acceptButton").asXml());
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		htmlPage = webClient.getPage(url);
		// Payroll Menu
		menuPayrollAnchor = htmlPage.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asNormalizedText());
		htmlPage = menuPayrollAnchor.click();
		// MainAgreement
		gwtAgreementAnchor = htmlPage.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asNormalizedText());
		htmlPage = gwtAgreementAnchor.click();
		wait4Id("star_wars_agreement");
		agreementTreeItem = (HtmlDivision) getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();
		wait4(htmlPage, htmlPage -> "STAR WARS AGREEMENT".equals(
				((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		getElementById("deleteButton_01_01_2017").click();
		Assert.assertEquals(0, salaryToggleButtonsPanel.getRow(0).getCells().size());
	}

	@Ignore("Sergio :-)")
	@Test
	public void TestExtras() throws Exception {

		wait4Id("pagas_extras_anulaes,_semestrales_y_trimestreales");

		HtmlDivision agreementTreeItem = (HtmlDivision) getElementById(
				"pagas_extras_anulaes,_semestrales_y_trimestreales");
		LOGGER.warning("Cick on: " + agreementTreeItem.asNormalizedText());
		agreementTreeItem.click();

		wait4(htmlPage, htmlPage -> "PAGAS EXTRAS ANULAES, SEMESTRALES Y TRIMESTREALES".equals(
				((HtmlInput) htmlPage.getElementById(GWT_DEBUG_ID_PREFIX + "descriptionTextBox")).getValueAttribute()));

		int year = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 1; i < 8; i++) {
			Assert.assertTrue(((HtmlInput) getElementById("endDateBox" + i)).getValueAttribute()
					.endsWith(Integer.toString(year)));
			Assert.assertTrue(((HtmlInput) getElementById("startDateBox" + i)).getValueAttribute()
					.endsWith(Integer.toString(year)));
		}
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

	protected void newPrevSalaryDataTab(Date date) throws IOException, InterruptedException {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		int months = -1;
		for (; calendar.getTime().after(date); months++)
			calendar.add(Calendar.MONTH, -1);

		getElementById("moreButton").click();
		wait4Id("moreDatePicker");
		HtmlElement moreDatePicker = getElementById("moreDatePicker");

		HtmlTable datePickerMonthSelector = moreDatePicker.getOneHtmlElementByAttribute(HtmlTable.TAG_NAME, "class",
				"datePickerMonthSelector");
		DomElement previousButton = datePickerMonthSelector.getRow(0).getCell(0).getFirstElementChild();
		DomElement datePickerMonth = datePickerMonthSelector.getRow(0).getCell(1);

		for (int i = 0; i < months; i++) {
			String actualMonth = datePickerMonth.getTextContent();
			previousButton.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
			previousButton.click();
			wait4(htmlPage, htmlPage -> !datePickerMonth.getTextContent().equals(actualMonth));
		}
		LOGGER.warning("Prev Month : " + datePickerMonth.getTextContent());

		String day = String.valueOf(date.getDate());
		for (HtmlElement datePickerDay : moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class",
				"datePickerDay ")) {
			if (day.equals(datePickerDay.getTextContent())) {
				datePickerDay.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
				datePickerDay.click();
				break;
			}
		}
		wait4NoId("moreDatePicker");

	}

	protected void newNextSalaryDataTab(Date date) throws IOException, InterruptedException {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

		int months = 0;
		for (; calendar.getTime().before(date); months++)
			calendar.add(Calendar.MONTH, +1);

		getElementById("moreButton").click();
		wait4Id("moreDatePicker");
		HtmlElement moreDatePicker = getElementById("moreDatePicker");

		HtmlTable datePickerMonthSelector = moreDatePicker.getOneHtmlElementByAttribute(HtmlTable.TAG_NAME, "class",
				"datePickerMonthSelector");
		DomElement nextButton = datePickerMonthSelector.getRow(0).getCell(2).getFirstElementChild();
		DomElement datePickerMonth = datePickerMonthSelector.getRow(0).getCell(1);

		for (int i = 0; i < months; i++) {
			String actualMonth = datePickerMonth.getTextContent();
			nextButton.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
			nextButton.click();
			wait4(htmlPage, htmlPage -> !datePickerMonth.getTextContent().equals(actualMonth));
		}
		LOGGER.warning("Next Month : " + datePickerMonth.getTextContent() + " for " + date);

		String day = String.valueOf(date.getDate());
		for (HtmlElement datePickerDay : moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class",
				"datePickerDay ")) {
			if (day.equals(datePickerDay.getTextContent())) {
				datePickerDay.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
				datePickerDay.click();
				break;
			}
		}
		wait4NoId("moreDatePicker");

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
