package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.GWT_DEBUG_ID_PREFIX;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.text.ParseException;
import java.util.Calendar;
import java.util.logging.Level;

import org.apache.xpath.operations.Div;
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
import com.gargoylesoftware.htmlunit.html.DomNode;
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
	
	@BeforeClass
	public static void setUp() throws Exception {
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
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
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
		
		HtmlDivision agreementTreeItem = 
				(HtmlDivision)getElementById("estatuto_de_los_trabajadores");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();
		
		HtmlButton draftButton = 
				(HtmlButton) getElementById("draftButton");
		Assert.assertNotEquals(draftButton.isDisplayed(), true );
		
		
		agreementTreeItem.rightClick();
		DomElement deleteItem = getElementById("deleteItem");
		Assert.assertNotEquals(deleteItem.isDisplayed(), true );
		
		wait4(htmlPage,
				htmlPage -> "ESTATUTO DE LOS TRABAJADORES".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));

		// AgreementDraft ToolBar
		for ( String id : new String []{
				"fxButton", 
				"undoButton",
				"redoButton",
				"undoAllButton",
				"acceptButton",
				"deleteButton",
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

	@Test
	public void TestStarsWarsAgreement() throws Exception {
		
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
		
		for ( HtmlElement datePickerDay : moreDatePicker.getElementsByAttribute(HtmlDivision.TAG_NAME, "class", "datePickerDay ") ) {
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
		
		HtmlDivision agreementTreeItem = 
				(HtmlDivision)getElementById("mensajes_de_ayuda,_ejemplos");
		LOGGER.warning("Cick on: " + agreementTreeItem.asText());
		agreementTreeItem.click();
		
		wait4(htmlPage,
				htmlPage -> "MENSAJES DE AYUDA, EJEMPLOS".equals(((HtmlInput)htmlPage.getElementById(GWT_DEBUG_ID_PREFIX +"descriptionTextBox")).getValueAttribute()));
		
		wait4NoId("toggleButton_01_01_1970");
		
	}

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
