package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.htmlunit.HtmlUnitIT;

import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;

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
		calculate(Calendar.SEPTEMBER, 2021);
		assertText("common_contingency", "9,10");


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
		assertNotElement("job_training");
		assertValue("cgcBaseLabel", "1.050,00");
		assertValue("cgpBaseLabel", "1.050,00");
		calculate(Calendar.SEPTEMBER, 2021);
		assertText("common_contingency", "9,10");
		assertText("unemployment", "17,45");
		assertNotElement("job_training");
		assertValue("cgcBaseLabel", "1.125,90");
		assertValue("cgpBaseLabel", "1.125,90");
		calculate(Calendar.JANUARY, 2022);
		assertText("common_contingency", "9,43");
		assertText("unemployment", "18,08");
		assertNotElement("job_training");
		assertValue("cgcBaseLabel", "1.166,70");
		assertValue("cgpBaseLabel", "1.166,70");

		calculate(Calendar.JANUARY, 2023);
		assertText("common_contingency", "10,18");
		assertText("unemployment", "19,53");
		assertText("job_training", "0,25");
		assertValue("cgcBaseLabel", "1.260,00");
		assertValue("cgpBaseLabel", "1.260,00");
		getElementById("costsCheck-input").click();
		wait4Id("common_contingency_cost");
		assertText("common_contingency_cost", "51,06");
		assertText("unemployment_cost", "69,30");
		assertText("job_training_cost", "1,90");
		assertText("fogasa_cost", "3,88");
		assertText("it_cost", "3,93");
		assertText("ims_cost", "3,10");
		

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
		calculate(Calendar.SEPTEMBER, 2021);
		assertText("common_contingency", "9,10");
		calculate(Calendar.JANUARY, 2022);
		assertText("common_contingency", "9,43");

		calculate(Calendar.JANUARY, 2023);
		assertValue("cgcBaseLabel", "1.260,00");
		assertValue("cgpBaseLabel", "1.260,00");
		assertText("common_contingency", "10,18");
		assertText("job_training", "0,25");
		//assertText("totalDeductionLabel", "10,69"); // + MEI
		if ( !isDisplayed("it_cost") )
			click("costsCheck-input");
		wait4Id("it_cost");
		assertText("common_contingency_cost", "51,06");
		assertText("job_training_cost", "1,90");
		assertText("fogasa_cost", "3,88");
		assertText("it_cost", "3,93");
		assertText("ims_cost", "3,10");

		draft("FORMACION Y APRENDIZAJE, IT");
		calculate(Calendar.OCTOBER, 2018);
		assertText("common_contingency", "6,94");
		assertText("unemployment", "13,32");
//		assertText("job_training", "0,17");
		assertValue("cgcBaseLabel", "858,60");
		assertValue("cgpBaseLabel", "858,60");
		calculate(Calendar.OCTOBER, 2019);
		assertText("common_contingency", "8,49");
		assertText("unemployment", "16,28");
		assertNotElement("job_training");
		assertValue("cgcBaseLabel", "1.050,00");
		assertValue("cgpBaseLabel", "1.050,00");
//		calculate(Calendar.SEPTEMBER, 2021);
//		assertText("common_contingency", "9,10");
//		assertText("unemployment", "17.45");
//		assertNotElement("job_training");
//		assertValue("cgcBaseLabel", "1.125,90");
//		assertValue("cgpBaseLabel", "1.125,90");
	}


	@Test
	public void TestEnAlternancia() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("formacion_aprendizaje,_alternancia_(superior)");

		draft("FORMACIÓN APRENDIZAJE, ALTERNANCIA (SUPERIOR)");
		calculate(Calendar.MAY, 2023);
		double cgcBase = getValue("cgcBaseLabel");
		assertText("common_contingency", 10.18);
		assertText("unemployment", cgcBase * 1.55 / 100.00);
		assertText("job_training", 0.25);
		assertText("mei", 1.26);
		
		
		calculate(Calendar.JUNE, 2023);
		double totalPayment = getValue("totalPaymentLabel");
		assertValue("cgcBaseLabel", totalPayment);
		assertValue("cgpBaseLabel", totalPayment);
		assertText("common_contingency", 10.18);
		assertText("unemployment", 19.53);
		assertText("job_training", 0.25);
		assertText("mei", 1.26);
		if ( !isDisplayed("it_cost") )
			click("costsCheck-input");
		wait4Id("it_cost");
		assertText("common_contingency_cost", "51,06");
		assertText("job_training_cost", "1,90");
		assertText("fogasa_cost", "3,88");
		assertText("it_cost", "3,93");
		assertText("ims_cost", "3,10");
		assertText("unemployment_cost", 69.30);
		click("costsCheck-input");
		
		draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 1)");
		calculate(Calendar.JUNE, 2023);
		((HtmlSelect) getElementById("editor-cotiza_exceso")).getOptionByText("SI").click();
		assertElement("structural_overtime");
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		
		
		draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 2)");
		calculate(Calendar.JUNE, 2023);
		((HtmlSelect) getElementById("editor-cotiza_exceso")).getOptionByText("SI").click();
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		
		draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 3)");
		calculate(Calendar.JUNE, 2023);
		((HtmlSelect) getElementById("editor-cotiza_exceso")).getOptionByText("SI").click();
		assertElement("structural_overtime");
		Assert.assertEquals(1, getElementsById("common_contingency").size());
		Assert.assertEquals(1, getElementsById("unemployment").size());
		Assert.assertEquals(1, getElementsById("job_training").size());

	}


	@Test
	public void TestSettle() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("finiquito_formacion,_aprendizaje");

		draft("FINIQUITO FORMACIÓN, APRENDIZAJE");
		

	
	}

	@Test
	public void TestMEI() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("mecanismo,_equidad");

		draft("MECANISMO, EQUIDAD");
		calculate(Calendar.MARCH, 2023);
		assertText("mei", "1,26");
		click("costsCheck-input");
		assertText("mei_cost", "6,30");
		click("costsCheck-input");
		
		
		
		
	}
	// -------------------------------------------------------------------------

}
