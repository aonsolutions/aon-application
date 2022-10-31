package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.htmlunit.HtmlUnitIT;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HomeIntegralTest extends BaseIntegralTestCase {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.home.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		setup(url, user, password);
		
		wait4Id("empleados_de_hogar");
	}

	// ------------------------------------------------------------------------


	@Test
	@Ignore("Deprecated")
	public void TestQuote() throws Exception {

		if (!isDisplayed("tramo,_1"))
			open("principal");

		wait4Id("tramo,_1");

		draft("TRAMO, 1");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 167.74);
		assertValue("cgcBaseLabel", 167.74);
		assertText("common_contingency", 167.74*4.55/100.00);

		draft("TRAMO, 2");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 277.51);
		assertValue("cgcBaseLabel", 277.51);
		assertText("common_contingency", 277.51*4.55/100.00);

		draft("TRAMO, 3");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 387.29);
		assertValue("cgcBaseLabel", 387.29);
	
		draft("TRAMO, 3 (DOS PERCEPCIONES)");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 387.29);
		assertValue("cgcBaseLabel", 387.29);
		assertText("common_contingency", 387.29*4.55/100.00);

		draft("TRAMO, 4");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 497.08);
		assertValue("cgcBaseLabel", 497.08);

		draft("TRAMO, 5");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 606.86);
		assertValue("cgcBaseLabel", 606.86);

		draft("TRAMO, 6");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 716.65);
		assertValue("cgcBaseLabel", 716.65);

		draft("TRAMO, 7");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 858.60);
		assertValue("cgcBaseLabel", 858.60);

		draft("TRAMO, 8");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 896.94);
		assertValue("cgcBaseLabel", 896.94);
	}

	@Test
	public void TestQuoteII() throws Exception {

		if (!isDisplayed("tramo,_1"))
			open("principal");

		wait4Id("tramo,_1");

		draft("TRAMO, 1");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 206.00);
		assertValue("cgcBaseLabel", 206.00);
		assertText("common_contingency", 206.00*4.70/100.00);

		draft("TRAMO, 2");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 340.00);
		assertValue("cgcBaseLabel", 340.00);
		assertText("common_contingency", 340.00*4.70/100.00);

		draft("TRAMO, 3");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 474.00);
		assertValue("cgcBaseLabel", 474.00);

		draft("TRAMO, 3 (DOS PERCEPCIONES)");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 474.00);
		assertValue("cgcBaseLabel", 474.00);
		assertText("common_contingency", 474.00*4.70/100.00);

		draft("TRAMO, 4");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 608.00);
		assertValue("cgcBaseLabel", 608.00);

		draft("TRAMO, 5");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 743.00);
		assertValue("cgcBaseLabel", 743.00);

		draft("TRAMO, 6");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 877.00);
		assertValue("cgcBaseLabel", 877.00);

		draft("TRAMO, 7");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 1050.00);
		assertValue("cgcBaseLabel", 1050.00);

		draft("TRAMO, 8");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 1097.00);
		assertValue("cgcBaseLabel", 1097.00);

		draft("TRAMO, 9");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 1232.00);
		assertValue("cgcBaseLabel", 1232.00);

		draft("TRAMO, _10");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 1555.00);
		assertValue("cgcBaseLabel", 1555.00);

	}

	@Test
	public void TestQuoteIII() throws Exception {

		if (!isDisplayed("tramo,_1"))
			open("principal");

		wait4Id("tramo,_1");

		draft("TRAMO, 1");
		click("costsCheck-input");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 206.00);
		assertValue("cgcBaseLabel", 206.00);
		assertText("common_contingency", 206.00*4.70/100.00);
		assertText("cgc_bonus", (206.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 222.00);
		assertValue("cgcBaseLabel", 222.00);
		assertText("cgc_bonus", (222.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 231.00);
		assertValue("cgcBaseLabel", 231.00);
		assertText("common_contingency", 231.00*4.70/100.00);
		assertText("cgc_bonus", (231.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 231.00);
		assertValue("cgcBaseLabel", 231.00);
		double cgcBase = getValue("cgcBaseLabel");
		double cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);
		

		draft("TRAMO, 2");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 340.00);
		assertValue("cgcBaseLabel", 340.00);
		assertText("common_contingency", 340.00*4.70/100.00);
		assertText("cgc_bonus", (340.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 365.00);
		assertValue("cgcBaseLabel", 365.00);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 379.00);
		assertValue("cgcBaseLabel", 379.00);
		assertText("common_contingency", 379.00*4.70/100.00);
		assertText("cgc_bonus", (379.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 379.00);
		assertValue("cgcBaseLabel", 379.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 3");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 474.00);
		assertValue("cgcBaseLabel", 474.00);
		assertText("cgc_bonus", (474.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 509.00);
		assertValue("cgcBaseLabel", 509.00);
		assertText("cgc_bonus", (509.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 528.00);
		assertValue("cgcBaseLabel", 528.00);
		assertText("common_contingency", 528.00*4.70/100.00);
		assertText("cgc_bonus", (528.00*23.60/100.00)*0.20);
		draft("TRAMO, 3 (DOS PERCEPCIONES)");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 474.00);
		assertValue("cgcBaseLabel", 474.00);
		assertText("common_contingency", 474.00*4.70/100.00);
		assertText("cgc_bonus", (474.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 509.00);
		assertValue("cgcBaseLabel", 509.00);
		assertText("cgc_bonus", (509.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 528.00);
		assertValue("cgcBaseLabel", 528.00);
		assertText("common_contingency", 528.00*4.70/100.00);
		assertText("cgc_bonus", (528.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 528.00);
		assertValue("cgcBaseLabel", 528.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 4");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 608.00);
		assertValue("cgcBaseLabel", 608.00);
		assertText("cgc_bonus", (608.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 653.00);
		assertValue("cgcBaseLabel", 653.00);
		assertText("cgc_bonus", (653.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 677.00);
		assertValue("cgcBaseLabel", 677.00);
		assertText("common_contingency", 677.00*4.70/100.00);
		assertText("cgc_bonus", (677.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 677.00);
		assertValue("cgcBaseLabel", 677.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 5");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 743.00);
		assertValue("cgcBaseLabel", 743.00);
		assertText("cgc_bonus", (743.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 798.00);
		assertValue("cgcBaseLabel", 798.00);
		assertText("cgc_bonus", (798.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 827.00);
		assertValue("cgcBaseLabel", 827.00);
		assertText("common_contingency", 827.00*4.70/100.00);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 827.00);
		assertValue("cgcBaseLabel", 827.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 6");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 877.00);
		assertValue("cgcBaseLabel", 877.00);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 941.00);
		assertValue("cgcBaseLabel", 941.00);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 976.00);
		assertValue("cgcBaseLabel", 976.00);
		assertText("common_contingency", 976.00*4.70/100.00);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 976.00);
		assertValue("cgcBaseLabel", 976.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 7");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 1050.00);
		assertValue("cgcBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 1125.90);
		assertValue("cgcBaseLabel", 1125.90);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 1166.70);
		assertValue("cgcBaseLabel", 1166.70);
		assertText("common_contingency", 1166.70*4.70/100.00);
		assertText("cgc_bonus", (1166.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 1166.70);
		assertValue("cgcBaseLabel", 1166.70);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 8");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 1097.00);
		assertValue("cgcBaseLabel", 1097.00);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 1177.00);
		assertValue("cgcBaseLabel", 1177.00);
		assertText("cgc_bonus", (1177.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 1220.00);
		assertValue("cgcBaseLabel", 1220.00);
		assertText("common_contingency", 1220.00*4.70/100.00);
		assertText("cgc_bonus", (1220.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 1220.00);
		assertValue("cgcBaseLabel", 1220.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

		draft("TRAMO, 9");
		calculate(Calendar.AUGUST, 2021);
		assertValue("cgpBaseLabel", 1232.00);
		assertValue("cgcBaseLabel", 1232.00);
		assertText("cgc_bonus", (1232.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 1322.00);
		assertValue("cgcBaseLabel", 1322.00);
		assertText("cgc_bonus", (1322.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 1370.00);
		assertValue("cgcBaseLabel", 1370.00);
		assertText("common_contingency", 1370.00*4.70/100.00);
		assertText("cgc_bonus", (1370.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 1370.00);
		assertValue("cgcBaseLabel", 1370.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);
		
		draft("TRAMO, _10");
		calculate(Calendar.JANUARY, 2019);
		assertValue("cgpBaseLabel", 1555.00);
		assertValue("cgcBaseLabel", 1555.00);
		assertText("cgc_bonus", (1555.00*23.60/100.00)*0.20);
		calculate(Calendar.SEPTEMBER, 2021);
		assertValue("cgpBaseLabel", 1555.00);
		assertValue("cgcBaseLabel", 1555.00);
		assertText("cgc_bonus", (1555.00*23.60/100.00)*0.20);
		calculate(Calendar.JANUARY, 2022);
		assertValue("cgpBaseLabel", 1555.00);
		assertValue("cgcBaseLabel", 1555.00);
		assertText("common_contingency", 1555.00*4.70/100.00);
		assertText("cgc_bonus", (1555.00*23.60/100.00)*0.20);
		calculate(Calendar.OCTOBER, 2022);
		assertValue("cgpBaseLabel", 1555.00);
		assertValue("cgcBaseLabel", 1555.00);
		cgcBase = getValue("cgcBaseLabel");
		cgpBase = getValue("cgcBaseLabel");
		assertText("common_contingency", cgcBase*4.70/100.00);
		assertText("unemployment", cgpBase*1.05/100.00);
		assertText("unemployment_cost", cgpBase*5.00/100.00);
		assertText("fogasa_cost", cgpBase*0.2/100.00);
		assertText("cgc_bonus", (cgcBase*23.60/100.00)*0.20);
		assertText("desmpl_bonus", (cgpBase*5.2/100.00)*0.80);

	}


	// -------------------------------------------------------------------------

}
