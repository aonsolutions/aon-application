package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.htmlunit.HtmlUnitIT;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TrainningIntegralTest extends BaseIntegralTestCase {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.trainning.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		setup(url, user, password);
		
		wait4Id("formacion_y_aprendizaje");
	}

	// ------------------------------------------------------------------------


	@Test
	public void TestQuote() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("finiquito_formacion,_aprendizaje");

		draft("FINIQUITO FORMACIÓN, APRENDIZAJE");
		calculate(Calendar.MARCH);
		assertText("common_contingency", "6,18");
		calculate(Calendar.APRIL);
		assertText("common_contingency", "6,18");
		calculate(Calendar.MAY);
		assertText("common_contingency", "6,18");
		calculate(Calendar.DECEMBER);
		assertText("common_contingency", "6,18");


		draft("FORMACIÓN Y EL, APRENDIZAJE");
		calculate(Calendar.FEBRUARY);
		assertText("common_contingency", "6,18");
		calculate(Calendar.MAY);
		assertText("common_contingency", "6,18");
		calculate(Calendar.DECEMBER);
		assertText("common_contingency", "6,18");

		draft("BECARIO, EL");
		calculate(Calendar.MARCH);
		assertText("common_contingency", "6,18");
		calculate(Calendar.DECEMBER);
		assertText("common_contingency", "6,18");
	
	}

	@Test
	public void TestSettle() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("finiquito_formacion,_aprendizaje");

		draft("FINIQUITO FORMACIÓN, APRENDIZAJE");
		

	
	}
	// -------------------------------------------------------------------------

}
