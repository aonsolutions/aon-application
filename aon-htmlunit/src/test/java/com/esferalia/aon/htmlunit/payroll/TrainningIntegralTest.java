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
		calculate(Calendar.MARCH, 2016);
		assertText("common_contingency", "6,18");
		calculate(Calendar.APRIL, 2017);
		assertText("common_contingency", "6,67");
		calculate(Calendar.DECEMBER, 2017);
		assertText("common_contingency", "6,67");


		draft("FORMACIÓN Y EL, APRENDIZAJE");
		calculate(Calendar.FEBRUARY, 2016);
		assertText("common_contingency", "6,18");
		calculate(Calendar.MAY, 2016);
		assertText("common_contingency", "6,18");
		calculate(Calendar.DECEMBER, 2017);
		assertText("common_contingency", "6,67");
		calculate(Calendar.FEBRUARY, 2018);
		assertText("common_contingency", "6,94");
		assertText("unemployment", "13,31");
		assertText("job_training", "0,17");
		assertValue("cgcBaseLabel", "858,60");
		assertValue("cgpBaseLabel", "858,60");
		calculate(Calendar.FEBRUARY, 2019);
		assertText("common_contingency", "8,49");
		assertText("unemployment", "16,28");
		assertText("job_training", "0,17");
		assertValue("cgcBaseLabel", "1.050,00");
		assertValue("cgpBaseLabel", "1.050,00");
		
		getElementById("costsCheck-input").click();
		wait4Id("common_contingency_cost");
		assertText("common_contingency_cost", "51,05");
		assertText("unemployment_cost", "57,75");
//		assertText("job_training_cost", "1,38");
		
		//costsCheck-input

		draft("BECARIO, EL");
		calculate(Calendar.DECEMBER, 2016);
		assertText("common_contingency", "6,18");
		calculate(Calendar.DECEMBER, 2017);
		assertText("common_contingency", "6,67");
		calculate(Calendar.APRIL, 2018);
		assertText("common_contingency", "6,94");
		calculate(Calendar.APRIL, 2019);
		assertText("common_contingency", "8,49");

		//assertValue("cgcBaseLabel", "858,60");
		//assertValue("cgpBaseLabel", "858,60");
		wait4Id("common_contingency_cost");
		assertText("common_contingency_cost", "51,05");

		draft("FORMACION Y APRENDIZAJE, IT");
		calculate(Calendar.OCTOBER, 2018);
		assertText("common_contingency", "6,94");
		assertText("unemployment", "13,31");
//		assertText("job_training", "0,17");
		assertValue("cgcBaseLabel", "858,60");
		assertValue("cgpBaseLabel", "858,60");
		calculate(Calendar.OCTOBER, 2019);
		assertText("common_contingency", "8,49");
		assertText("unemployment", "16,28");
		assertText("job_training", "0,17");
		assertValue("cgcBaseLabel", "1.050,00");
		assertValue("cgpBaseLabel", "1.050,00");
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
