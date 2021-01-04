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
import org.junit.Ignore;

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
		webClient = new WebClient(BrowserVersion.FIREFOX_45);

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
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asText());
		htmlPage = menuPayrollAnchor.click();

		// MainAgreement
		HtmlAnchor gwtAgreementAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asText());
		htmlPage = gwtAgreementAnchor.click();

		wait4Id("agreements");

	}


	@AfterClass
	public static void tearDown() {
		webClient.close();
	}

	@Test
	public void TestEstatutoDeLosTrabajadores() throws Exception {

		wait4Id("estatuto_de_los_trabajadores");

		HtmlDivision agreementTreeItem =
				(HtmlDivision)getElementById("estatuto_de_los_trabajadores");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();

		Pattern hidden = Pattern.compile("display\\s*:\\s*none");

		HtmlButton draftButton =
				(HtmlButton) getElementById("draftButton");
//		Assert.assertNotEquals(draftButton.isDisplayed(), true );
		Assert.assertEquals(true, hidden.matcher(draftButton.getAttribute("style")).find());


		agreementTreeItem.rightClick();
		DomElement deleteItem = getElementById("deleteItem");
//		Assert.assertNotEquals(deleteItem.isDisplayed(), true );
		Assert.assertEquals(true, hidden.matcher(deleteItem.getAttribute("style")).find());

		wait4(htmlPage,
				htmlPage -> "ESTATUTO DE LOS TRABAJADORES".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));

		// AgreementDraft ToolBar
		for ( String id : new String []{
				"fxButton",
				"undoButton",
				"redoButton",
				"undoAllButton",
				"acceptButton",
//				"deleteButton",
				}){
			Assert.assertEquals(id, ((HtmlButton)getElementById(id)).isDisabled(), true );
		}

		Assert.assertEquals(((HtmlInput)getElementById("descriptionTextBox")).isReadOnly(), true );


		for ( String id : new String [] {
				"paymentsTable",
				"extrasTable",
				//"salaryTable"
				} ){
			DomElement paymentsTable = getElementById(id);

			DomNodeList<HtmlElement> inputs = paymentsTable.getElementsByTagName(HtmlInput.TAG_NAME);
			for (int i = 0; i < inputs.getLength(); i++) {
				HtmlInput input = (HtmlInput) inputs.get(i);


				LOGGER.warning("Input [ " + input.asXml() +"] :"
				+ "display : " + input.isDisplayed()
				+ ", disabled : " + input.isDisabled()
				+ ", read-only : " + input.isReadOnly()
				);

				Assert.assertEquals(
						(!input.isDisplayed())
						|| input.isDisabled()
						|| input.isReadOnly(),

						true );
			}

			DomNodeList<HtmlElement> selects = paymentsTable.getElementsByTagName(HtmlSelect.TAG_NAME);
			for (int i = 0; i < selects.getLength(); i++) {
				HtmlSelect select = (HtmlSelect) selects.get(i);
				LOGGER.warning("Select [ " + ( select.getSelectedIndex() >= 0 ? select.getOptions().get(select.getSelectedIndex()).getText() : "") +"] :"
				+ "display : " + select.isDisplayed()
				+ ", disabled : " + select.isDisabled()
				);
				Assert.assertEquals(
						(!select.isDisplayed())
						|| select.isDisabled(),
						true );
			}
		}

	}
	@Ignore("Sergio :-)")
	@Test
	public void TestStarsWarsAgreement() throws Exception {

		wait4Id("star_wars_agreement");

		HtmlDivision agreementTreeItem =
				(HtmlDivision)getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();

		wait4(htmlPage,
				htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));


		 setValue("description-box-new-payment", "[1]PLUS PELIGROSIDAD" );
		 wait4Value("description-box1", "[1]PLUS PELIGROSIDAD");

		 setValue("expression-box1", "666.00 * DIAS_TRABAJADOS / DIAS_MES" );
		 wait4Value("expression-box1", "666.00 * DIAS_TRABAJADOS / DIAS_MES");

		 wait4Class("payment-row-1", "aon-dataTable-row-highlight");

		 getElementById("acceptButton").click();

		 wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		 wait4Value("description-box1", "[1]PLUS PELIGROSIDAD");
		 wait4Value("expression-box1", "666.00 * DIAS_TRABAJADOS / DIAS_MES");


		getElementById("moreButton").click();
		wait4Id("moreDatePicker");
		HtmlElement moreDatePicker = getElementById("moreDatePicker");

		HtmlTable datePickerMonthSelector = moreDatePicker.getOneHtmlElementByAttribute(HtmlTable.TAG_NAME, "class", "datePickerMonthSelector") ;
		DomElement previousButton = datePickerMonthSelector.getRow(0).getCell(0).getFirstElementChild();
		DomElement datePickerMonth = datePickerMonthSelector.getRow(0).getCell(1);
		for ( int i = 0; i < 12 * 5; i++ ) {
			String actualMonth = datePickerMonth.getTextContent();
			previousButton.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
			previousButton.click();
			wait4(htmlPage, htmlPage ->  !datePickerMonth.getTextContent().equals(actualMonth) );
		}
		LOGGER.warning("Month : " + datePickerMonth.getTextContent());

		//for ( HtmlElement datePickerDay : moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class", "datePickerDay ") ) {
		for ( HtmlElement divElement : moreDatePicker.getElementsByTagName(HtmlDivision.TAG_NAME) ) {
			if ( !divElement.getAttribute("class").contains("datePickerDay"))
				continue;

			HtmlElement datePickerDay = divElement;
			LOGGER.warning("datePickerDay : " + datePickerDay.getTextContent());
			if ( "1".equals(datePickerDay.getTextContent())) {
				datePickerDay.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
				datePickerDay.click();
				break;
			}
		}
		wait4NoId("moreDatePicker");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -12*5);

		LOGGER.warning("Wait4: " + String.format("toggleButton_01_%1$tm_%1$tY", calendar.getTime() ));
		wait4Id(String.format("toggleButton_01_%1$tm_%1$tY", calendar.getTime() ) );

		getElementById(String.format("deleteButton_01_%1$tm_%1$tY", calendar.getTime() ) ).click();

		wait4Value("description-box1", "[1]PLUS PELIGROSIDAD");
		wait4Value("expression-box1", "666.00 * DIAS_TRABAJADOS / DIAS_MES");

		HtmlTable paymentsTable = getElementById("paymentsTable");
		int paymentsRows = paymentsTable.getRowCount();
		getElementById("delete-button-1").click();

		wait4(htmlPage, htmlPage -> paymentsTable.getRowCount() == paymentsRows -1 );

		getElementById("acceptButton").click();

	}

	@Test
	public void TestMensajesdeAyuda() throws Exception {

		wait4Id("mensajes_de_ayuda,_ejemplos");

		HtmlDivision agreementTreeItem =
				(HtmlDivision)getElementById("mensajes_de_ayuda,_ejemplos");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();

		wait4(htmlPage,
				htmlPage -> "MENSAJES DE AYUDA, EJEMPLOS".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));

		wait4NoId("toggleButton_01_01_1970");

	}

	@Test
	public void TestPrintPreview() throws Exception {

		wait4Id("convenio_colectivo_de_oficinas_y_despachos_para_madrid");

		HtmlDivision agreementTreeItem =
				(HtmlDivision)getElementById("convenio_colectivo_de_oficinas_y_despachos_para_madrid");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();

		wait4(htmlPage,
				htmlPage -> "CONVENIO COLECTIVO DE OFICINAS Y DESPACHOS PARA MADRID".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));

		wait4Id("printPreviewButton");

		getElementById("printPreviewButton").click();


		wait4Id("printPreviewHTML");

		HtmlElement printPreviewHTML = getElementById("printPreviewHTML");

		wait4(htmlPage, htmlPage -> printPreviewHTML.getElementsByTagName(HtmlTable.TAG_NAME).size() > 0);

	}

	@Ignore("Fix soon")
	@Test
	public void TestSalaryTable() throws Exception {

		wait4Id("star_wars_agreement");

		HtmlDivision agreementTreeItem =
				(HtmlDivision)getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();

		wait4(htmlPage,
				htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));

		// New Tab for 01/01/2018
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		newPrevSalaryDataTab(calendar.getTime());

		wait4Id("textBox_SALARIO_MENSUAL_I");

		setValue("textBox_SALARIO_MENSUAL_I", "666 + 0.66" );
		wait4Value("textBox_SALARIO_MENSUAL_I", "666.66");


		getElementById("acceptButton").click();

		wait4NoClass("textBox_SALARIO_MENSUAL_I", "aon-icon-changed");

		HtmlTable salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		getElementById("deleteButton_01_01_2018").click();
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		getElementById("acceptButton").click();
		System.out.println(getElementById("acceptButton").asXml());
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		htmlPage = webClient.getPage(url);
		// Payroll Menu
		HtmlAnchor menuPayrollAnchor = htmlPage
				.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asText());
		htmlPage = menuPayrollAnchor.click();
		// MainAgreement
		HtmlAnchor gwtAgreementAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asText());
		htmlPage = gwtAgreementAnchor.click();
		wait4Id("star_wars_agreement");
		agreementTreeItem =
				(HtmlDivision)getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();
		wait4(htmlPage,
				htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		// 01-01-2018
		newPrevSalaryDataTab(calendar.getTime());
		wait4Id("textBox_SALARIO_MENSUAL_I");
		HtmlInput htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		String value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		setValue("textBox_SALARIO_MENSUAL_I", "888 + 0.88" );
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

		setValue("textBox_SALARIO_MENSUAL_I", "999 + 0.99" );
		wait4Value("textBox_SALARIO_MENSUAL_I", "999.99");
		getElementById("acceptButton").click();
		wait4NoClass("textBox_SALARIO_MENSUAL_I", "aon-icon-changed");
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(6, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		getElementById("deleteButton_01_01_2018").click();
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		getElementById("acceptButton").click();
		System.out.println(getElementById("acceptButton").asXml());
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size());

		htmlPage = webClient.getPage(url);
		// Payroll Menu
		menuPayrollAnchor = htmlPage
				.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asText());
		htmlPage = menuPayrollAnchor.click();
		// MainAgreement
		gwtAgreementAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asText());
		htmlPage = gwtAgreementAnchor.click();
		wait4Id("star_wars_agreement");
		agreementTreeItem =
				(HtmlDivision)getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();
		wait4(htmlPage,
				htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		// 01-01-2018
		calendar.add(Calendar.YEAR, -1);
		newPrevSalaryDataTab(calendar.getTime());
		wait4Id("textBox_SALARIO_MENSUAL_I");
		htmlInput = getElementById("textBox_SALARIO_MENSUAL_I");
		value = htmlInput.getValueAttribute();
		Assert.assertNotNull(value);
		Assert.assertNotEquals(value.trim(), "");

		getElementById("deleteButton_01_01_2018").click();
		Assert.assertEquals(4, salaryToggleButtonsPanel.getRow(0).getCells().size() );
		getElementById("deleteButton_01_01_2019").click();
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		getElementById("acceptButton").click();
		System.out.println(getElementById("acceptButton").asXml());
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size());

		htmlPage = webClient.getPage(url);
		// Payroll Menu
		menuPayrollAnchor = htmlPage
				.getAnchorByName(AON_MAIN_MENU_FORM + ":menu_payroll");
		LOGGER.warning("Cick on: " + menuPayrollAnchor.asText());
		htmlPage = menuPayrollAnchor.click();
		// MainAgreement
		gwtAgreementAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_agreement");
		LOGGER.warning("Cick on: " + gwtAgreementAnchor.asText());
		htmlPage = gwtAgreementAnchor.click();
		wait4Id("star_wars_agreement");
		agreementTreeItem =
				(HtmlDivision)getElementById("star_wars_agreement");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();
		wait4(htmlPage,
				htmlPage -> "STAR WARS AGREEMENT".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));
		salaryToggleButtonsPanel = getElementById("salaryToggleButtonsPanel");
		Assert.assertEquals(2, salaryToggleButtonsPanel.getRow(0).getCells().size() );

		getElementById("deleteButton_01_01_2017").click();
		Assert.assertEquals(0, salaryToggleButtonsPanel.getRow(0).getCells().size() );
	}

	@Test
	public void TestExtras() throws Exception {

		wait4Id("pagas_extras_anulaes,_semestrales_y_trimestreales");

		HtmlDivision agreementTreeItem =
				(HtmlDivision)getElementById("pagas_extras_anulaes,_semestrales_y_trimestreales");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();

		wait4(htmlPage,
				htmlPage -> "PAGAS EXTRAS ANULAES, SEMESTRALES Y TRIMESTREALES".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));

		for ( int i = 1; i < 8; i++ ) {
			Assert.assertTrue(((HtmlInput) getElementById("endDateBox" + i)).getValueAttribute().endsWith("2021"));
			Assert.assertTrue(((HtmlInput) getElementById("startDateBox" + i)).getValueAttribute().endsWith("2021"));
		}
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

	protected void newPrevSalaryDataTab (Date date) throws IOException, InterruptedException {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

		int months = -1;
		for ( ; calendar.getTime().after(date); months++)
			calendar.add(Calendar.MONTH, -1);

		getElementById("moreButton").click();
		wait4Id("moreDatePicker");
		HtmlElement moreDatePicker = getElementById("moreDatePicker");

		HtmlTable datePickerMonthSelector = moreDatePicker.getOneHtmlElementByAttribute(HtmlTable.TAG_NAME, "class", "datePickerMonthSelector") ;
		DomElement previousButton = datePickerMonthSelector.getRow(0).getCell(0).getFirstElementChild();
		DomElement datePickerMonth = datePickerMonthSelector.getRow(0).getCell(1);

		for ( int i = 0; i < months; i++ ) {
			String actualMonth = datePickerMonth.getTextContent();
			previousButton.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
			previousButton.click();
			wait4(htmlPage, htmlPage ->  !datePickerMonth.getTextContent().equals(actualMonth) );
		}
		LOGGER.warning("Prev Month : " + datePickerMonth.getTextContent());

		String day = String.valueOf(date.getDate());
		for ( HtmlElement datePickerDay : moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class", "datePickerDay ") ) {
			if ( day.equals(datePickerDay.getTextContent())) {
				datePickerDay.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
				datePickerDay.click();
				break;
			}
		}
		wait4NoId("moreDatePicker");

	}

	protected void newNextSalaryDataTab (Date date) throws IOException, InterruptedException {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

		int months = 0;
		for ( ; calendar.getTime().before(date); months++)
			calendar.add(Calendar.MONTH, +1);

		getElementById("moreButton").click();
		wait4Id("moreDatePicker");
		HtmlElement moreDatePicker = getElementById("moreDatePicker");

		HtmlTable datePickerMonthSelector = moreDatePicker.getOneHtmlElementByAttribute(HtmlTable.TAG_NAME, "class", "datePickerMonthSelector") ;
		DomElement nextButton = datePickerMonthSelector.getRow(0).getCell(2).getFirstElementChild();
		DomElement datePickerMonth = datePickerMonthSelector.getRow(0).getCell(1);

		for ( int i = 0; i < months; i++ ) {
			String actualMonth = datePickerMonth.getTextContent();
			nextButton.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
			nextButton.click();
			wait4(htmlPage, htmlPage ->  !datePickerMonth.getTextContent().equals(actualMonth) );
		}
		LOGGER.warning("Next Month : " + datePickerMonth.getTextContent() + " for " + date );

		String day = String.valueOf(date.getDate());
		for ( HtmlElement datePickerDay : moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class", "datePickerDay ") ) {
			if ( day.equals(datePickerDay.getTextContent())) {
				datePickerDay.fireEvent(MouseEvent.TYPE_MOUSE_OVER);
				datePickerDay.click();
				break;
			}
		}
		wait4NoId("moreDatePicker");

	}
}
