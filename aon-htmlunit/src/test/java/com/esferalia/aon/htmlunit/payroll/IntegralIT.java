package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.AON_MAIN_MENU_FORM;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.login;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.wait4;

import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.htmlunit.HtmlUnitIT;
import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class IntegralIT {

	public static final String INTEGRATION_PAYROLL_URL = "integration.payroll.url";
	public static final String AON_PAYROLL_MENU_FORM = "aonContent:payrollMenu";

	private static WebClient webClient;
	private static HtmlPage htmlPage;

	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.setLevel(Level.WARNING);
		webClient = new WebClient(BrowserVersion.FIREFOX_24);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAlertHandler(new AlertHandler() {

			@Override
			public void handleAlert(Page page, String message) {
				LOGGER.warning("ALERT '" + message + "'");
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

		// Integral
		HtmlAnchor gwtEmployeeAnchor = htmlPage
				.getAnchorByName(AON_PAYROLL_MENU_FORM + ":gwt_employee");
		LOGGER.warning("Cick on: " + gwtEmployeeAnchor.asText());
		htmlPage = gwtEmployeeAnchor.click();

		wait4(htmlPage,
				htmlPage -> htmlPage
						.getFirstByXPath("//div[@id='aonContent:enterpriseForm']") != null);
	}

	@AfterClass
	public static void tearDown() {
		webClient.closeAllWindows();
	}

	@Test
	public void TestIrpfPreview() throws Exception {
		DomElement aonContent = htmlPage.getElementById("aonContent");

		HtmlDivision draftItem = aonContent
				.getFirstByXPath(".//div[normalize-space(text())='Borrador']");
		htmlPage = draftItem.click();

		HtmlButton irpfPreviewButton = wait4(
				htmlPage,
				() -> aonContent
						.getFirstByXPath(".//button[normalize-space(text())='IRPF']"));
		htmlPage = irpfPreviewButton.click();

		assert (wait4(
				htmlPage,
				() -> aonContent.getFirstByXPath(".//div[@class='page']") != null));
		LOGGER.warning("Success: Impresion del calculo del IRPF da error https://github.com/aonsolutions/aon-application/issues/83");

	}

}
