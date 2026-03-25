package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Locale;

import org.htmlunit.html.HtmlSelect;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
		calculate(Calendar.SEPTEMBER, 2025);
		assertText("common_contingency", "11,16");


		draft("FORMACIÓN Y EL, APRENDIZAJE");
//		calculate(Calendar.FEBRUARY, 2016);
//		assertText("common_contingency", "6,18");
//		calculate(Calendar.MAY, 2016);
//		assertText("common_contingency", "6,18");
//		calculate(Calendar.DECEMBER, 2017);
//		assertText("common_contingency", "6,67");
//		calculate(Calendar.FEBRUARY, 2018);
//		assertText("common_contingency", "6,94");
//		assertText("unemployment", "13,31");
//		assertText("job_training", "0,17");
//		assertValue("cgcBaseLabel", "858,60");
//		assertValue("cgpBaseLabel", "858,60");
//		calculate(Calendar.FEBRUARY, 2019);
//		assertText("common_contingency", "8,49");
//		assertText("unemployment", "16,28");
//		assertNotElement("job_training");
//		assertValue("cgcBaseLabel", "1.050,00");
//		assertValue("cgpBaseLabel", "1.050,00");
//		calculate(Calendar.SEPTEMBER, 2021);
//		assertText("common_contingency", "9,10");
//		assertText("unemployment", "17,45");
//		assertNotElement("job_training");
//		assertValue("cgcBaseLabel", "1.125,90");
//		assertValue("cgpBaseLabel", "1.125,90");
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
		check("costsCheck-input");
		wait4Id("common_contingency_cost");
		assertText("common_contingency_cost", "51,06");
		assertText("unemployment_cost", "69,30");
		assertText("job_training_cost", "1,90");
		assertText("fogasa_cost", "3,88");
		assertText("it_cost", "3,93");
		assertText("ims_cost", "3,10");
		uncheck("costsCheck-input");
		calculate(Calendar.JANUARY, 2024);
		assertText("common_contingency", "10,69");
		assertText("unemployment", "20,51"); // 1323.00 * 1.55 / 100.00
		assertText("job_training", "0,26");
		//assertValue("cgcBaseLabel", "1.323,00");
		//assertValue("cgpBaseLabel", "1.323,00");
		check("costsCheck-input");
		wait4Id("common_contingency_cost");
		assertText("common_contingency_cost", "53,61");
		assertText("unemployment_cost", "72,76"); // 1323.00 * 5.50 / 100.00
		assertText("job_training_cost", "2,00");
		assertText("fogasa_cost", "4,07");
		assertText("it_cost", "3,82");
		assertText("ims_cost", "3,56");
		uncheck("costsCheck-input");
		calculate(Calendar.JANUARY, 2025);
		assertText("common_contingency", "11,16");
		assertText("unemployment", "21,41"); // 1381.20 * 1.55 / 100.00
		assertText("job_training", "0,27");
		//assertValue("cgcBaseLabel", "1.381,20");
		//assertValue("cgpBaseLabel", "1.381,20");
		check("costsCheck-input");
		wait4Id("common_contingency_cost");
		assertText("common_contingency_cost", "55,97");
		assertText("unemployment_cost", "75,96"); // 1381.20 * 5.50 / 100.00
		assertText("job_training_cost", "2,09");
		assertText("fogasa_cost", "4,25");
		assertText("it_cost", "3,99");
		assertText("ims_cost", "3,72");
		uncheck("costsCheck-input");
		

		//costsCheck-input

		draft("BECARIO, EL");
//		calculate(Calendar.DECEMBER, 2016);
//		assertText("common_contingency", "6,18");
//		calculate(Calendar.DECEMBER, 2017);
//		assertText("common_contingency", "6,67");
//		calculate(Calendar.APRIL, 2018);
//		assertText("common_contingency", "6,94");
//		calculate(Calendar.APRIL, 2019);
//		assertText("common_contingency", "8,49");
//		calculate(Calendar.SEPTEMBER, 2021);
//		assertText("common_contingency", "9,10");
		calculate(Calendar.JANUARY, 2022);
		assertText("common_contingency", "9,43");
		calculate(Calendar.JANUARY, 2023);
		assertValue("cgcBaseLabel", "1.260,00");
		assertValue("cgpBaseLabel", "1.260,00");
		assertText("common_contingency", "10,18");
		assertText("job_training", "0,25");
		//assertText("totalDeductionLabel", "10,69"); // + MEI
		check("costsCheck-input");
		wait4Id("it_cost");
		assertText("common_contingency_cost", "51,06");
		assertText("job_training_cost", "1,90");
		assertText("fogasa_cost", "3,88");
		assertText("it_cost", "3,93");
		assertText("ims_cost", "3,10");
		uncheck("costsCheck-input");
		calculate(Calendar.JANUARY, 2024);
		assertValue("cgcBaseLabel", "1.323,00");
		assertValue("cgpBaseLabel", "1.323,00");
		assertText("common_contingency", "10,69");
		assertText("job_training", "0,26");
		assertNotElement("mei");
		check("costsCheck-input");
		wait4Id("it_cost");
		assertText("common_contingency_cost", "53,61");
		assertText("job_training_cost", "2,00");
		assertText("fogasa_cost", "4,07");
		assertText("it_cost", "3,82");
		assertText("ims_cost", "3,56");
		assertNotElement("mei_cost");
		uncheck("costsCheck-input");
		calculate(Calendar.JANUARY, 2025);
		assertValue("cgcBaseLabel", "1.381,20");
		assertValue("cgpBaseLabel", "1.381,20");
		assertText("common_contingency", "11,16");
		assertText("job_training", "0,27");
		assertNotElement("mei");
		check("costsCheck-input");
		wait4Id("it_cost");
		assertText("common_contingency_cost", "55,97");
		assertText("job_training_cost", "2,09");
		assertText("fogasa_cost", "4,25");
		assertText("it_cost", "3,99");
		assertText("ims_cost", "3,72");
		assertNotElement("mei_cost");
		uncheck("costsCheck-input");

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
		assertText("unemployment", 19.53 /*cgcBase * 1.55 / 100.00*/);
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
			check("costsCheck-input");
		wait4Id("it_cost");
		assertText("common_contingency_cost", "51,06");
		assertText("job_training_cost", "1,90");
		assertText("fogasa_cost", "3,88");
		assertText("it_cost", "3,93");
		assertText("ims_cost", "3,10");
		assertText("unemployment_cost", 69.30);
		uncheck("costsCheck-input");
		
		draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 1)");
		calculate(Calendar.MAY, 2024);
		((HtmlSelect) getElementById("editor-cotiza_exceso")).getOptionByText("SI").click();
		assertElement("non_structural_overtime");
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		calculate(Calendar.JUNE, 2024);
		assertNotElement("editor-cotiza_exceso");
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		calculate(Calendar.JANUARY, 2025);
		assertNotElement("editor-cotiza_exceso");
		Assert.assertEquals(1, getElementsById("common_contingency").size());
		Assert.assertEquals(1, getElementsById("unemployment").size());
		Assert.assertEquals(1, getElementsById("job_training").size());
		calculate(Calendar.FEBRUARY, 2025);
		assertNotElement("editor-cotiza_exceso");
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		
		
		draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 2)");
		calculate(Calendar.MAY, 2024);
		((HtmlSelect) getElementById("editor-cotiza_exceso")).getOptionByText("SI").click();
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		calculate(Calendar.JUNE, 2024);
		assertNotElement("editor-cotiza_exceso");
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		calculate(Calendar.JANUARY, 2025);
		assertNotElement("editor-cotiza_exceso");
		Assert.assertEquals(3, getElementsById("common_contingency").size());
		Assert.assertEquals(3, getElementsById("unemployment").size());
		Assert.assertEquals(3, getElementsById("job_training").size());
		
		draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 3)");
		calculate(Calendar.MAY, 2024);
		((HtmlSelect) getElementById("editor-cotiza_exceso")).getOptionByText("SI").click();
		assertElement("non_structural_overtime");
		Assert.assertEquals(1, getElementsById("common_contingency").size());
		Assert.assertEquals(1, getElementsById("unemployment").size());
		Assert.assertEquals(1, getElementsById("job_training").size());
		calculate(Calendar.JUNE, 2024);
		assertNotElement("editor-cotiza_exceso");
		assertElement("non_structural_overtime");
		Assert.assertEquals(1, getElementsById("common_contingency").size());
		Assert.assertEquals(1, getElementsById("unemployment").size());
		Assert.assertEquals(1, getElementsById("job_training").size());
		calculate(Calendar.JANUARY, 2025);
		assertNotElement("editor-cotiza_exceso");
		assertElement("non_structural_overtime");
		Assert.assertEquals(1, getElementsById("common_contingency").size());
		Assert.assertEquals(1, getElementsById("unemployment").size());
		Assert.assertEquals(1, getElementsById("job_training").size());

		draft("FORMACION APRENDIZAJE, ALTERNANCIA (PARCIAL)");
		calculate(Calendar.AUGUST, 2023);
		assertValue("cgcBaseLabel", 1260.00);
		assertValue("cgpBaseLabel", 1260.00);
		assertText("common_contingency", 10.18);
		assertText("unemployment", 19.53);
		assertText("job_training", 0.25);
		assertText("mei", 1.26);
		double totalDeduction = getText("totalDeductionLabel");
		double totalEnterprise = getText("totalEnterpriseLabel");
		calculate(Calendar.SEPTEMBER, 2023);
		assertValue("cgcBaseLabel", 1260.00);
		assertValue("cgpBaseLabel", 1260.00);
		assertText("common_contingency", 10.18);
		assertText("unemployment", 19.53);
		assertText("job_training", 0.25);
		assertText("mei", 1.26);
		assertText("totalDeductionLabel", totalDeduction - 28.00 );
		assertText("totalEnterpriseLabel", totalEnterprise - 91.00 );
		calculate(Calendar.JANUARY, 2024);
		assertValue("cgcBaseLabel", 1323.00);
		assertValue("cgpBaseLabel", 1323.00);
		assertText("common_contingency", 10.69);
		assertText("unemployment", 20.51);
		assertText("job_training", 0.26);
		assertText("mei", 1.59);
		//assertText("totalDeductionLabel", totalDeduction - 28.00 );
		//assertText("totalEnterpriseLabel", totalEnterprise - 91.00 );
		calculate(Calendar.JANUARY, 2025);
		assertValue("cgcBaseLabel", 1381.20);
		assertValue("cgpBaseLabel", 1381.20);
		assertText("common_contingency", 11.16);
		assertText("unemployment", 21.41);
		assertText("job_training", 0.27);
		assertText("mei", 1.80);
		assertText("meiPercentLabel", "0,13 %");

		calculate(Calendar.JANUARY, 2026);
		check("costsCheck-input");
		assertValue("cgcBaseLabel", 1381.20);
		assertValue("cgpBaseLabel", 1381.20);
		assertText("common_contingency", 11.16);
		assertText("unemployment", 21.41);
		assertText("job_training", 0.27);
		assertText("mei", BigDecimal.valueOf(1381.20 * 0.15 / 100.00).setScale(2, RoundingMode.HALF_UP).doubleValue());
		assertText("meiPercentLabel", "0,15 %");
		//assertText("mei_cost", BigDecimal.valueOf(1381.20 * 0.75 / 100.00).setScale(2, RoundingMode.HALF_UP).doubleValue());
		assertText("mei_ePercentLabel", "0,75 %");
		uncheck("costsCheck-input");
		
		draft("FORMACIÓN ALTERNANCIA, PLAN DE PENSIONES");
		calculate(Calendar.SEPTEMBER, 2025);
		check("costsCheck-input");
		assertNotElement("red_ppe_ePercentLabel");
		assertElement("editor-reduccion_aportacion_empresa_ppe");
		uncheck("costsCheck-input");

	}


	@Test
	public void TestSettle() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("finiquito_formacion,_aprendizaje");

		draft("FINIQUITO FORMACIÓN, APRENDIZAJE");
		try {
		    settle(Calendar.getInstance().getTime());
		} catch (AssertionError err  ) {
		    int year = Calendar.getInstance().get(Calendar.YEAR);
		    int month = Calendar.getInstance().get(Calendar.MONTH) +1;
		    wait4Regex("periodLabel", String.format( new Locale("es","ES"),"3/3/2016 - [0-9]+/%2$d/%1$d", year, month));
		}
		setValue("editor-dias_vacaciones_no_disfrutados", "1");
		//double totalPayment = getValue("totalPaymentLabel");
		assertText("unemployment", 21.41);
		
		
		draft("FINIQUITO FORMACION, ALTERNANCIA");
		Calendar  calendar = Calendar.getInstance();
		calendar.set(2023, Calendar.JULY, 15, 0, 0, 0);
		settle(calendar.getTime());
		double irpf = getText("irpf");
		assertText("totalDeductionLabel", irpf);
		assertText("totalEnterpriseLabel", 0.00);
	
	}

	@Test
	public void TestMEI() throws Exception {

		close("cotizacion_formacion_y_el_aprendizaje");
		open("cotizacion_formacion_y_el_aprendizaje");

		wait4Id("mecanismo,_equidad");

		draft("MECANISMO, EQUIDAD");
		calculate(Calendar.MARCH, 2023);
		assertText("mei", "1,26");
		check("costsCheck-input");
		assertText("mei_cost", "6,30");
		uncheck("costsCheck-input");
		
		calculate(Calendar.JANUARY, 2024);
		assertText("mei", "1,59");
		check("costsCheck-input");
		assertText("mei_cost", "7,67");
		uncheck("costsCheck-input");
		
		calculate(Calendar.JANUARY, 2025);
		assertText("mei", "1,80");
		assertText("meiPercentLabel", "0,13 %");
		check("costsCheck-input");
		assertText("mei_cost", "9,25");
		assertText("mei_ePercentLabel", "0,67 %");
		uncheck("costsCheck-input");
		
		
	}
	// -------------------------------------------------------------------------

}
