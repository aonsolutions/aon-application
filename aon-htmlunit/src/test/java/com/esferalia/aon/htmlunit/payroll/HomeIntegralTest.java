package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;

import java.util.Calendar;

import org.junit.BeforeClass;
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
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 206.00);
		assertValue("cgcBaseLabel", 206.00);
		assertText("common_contingency", 206.00*4.55/100.00);

		draft("TRAMO, 2");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 340.00);
		assertValue("cgcBaseLabel", 340.00);
		assertText("common_contingency", 340.00*4.55/100.00);

		draft("TRAMO, 3");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 474.00);
		assertValue("cgcBaseLabel", 474.00);
	
		draft("TRAMO, 3 (DOS PERCEPCIONES)");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 474.00);
		assertValue("cgcBaseLabel", 474.00);
		assertText("common_contingency", 474.00*4.55/100.00);

		draft("TRAMO, 4");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 608.00);
		assertValue("cgcBaseLabel", 608.00);

		draft("TRAMO, 5");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 743.00);
		assertValue("cgcBaseLabel", 743.00);

		draft("TRAMO, 6");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 877.00);
		assertValue("cgcBaseLabel", 877.00);

		draft("TRAMO, 7");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 1050.00);
		assertValue("cgcBaseLabel", 1050.00);

		draft("TRAMO, 8");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 1097.00);
		assertValue("cgcBaseLabel", 1097.00);

		draft("TRAMO, 9");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 1232.00);
		assertValue("cgcBaseLabel", 1232.00);

		draft("TRAMO, _10");
		calculate(Calendar.MAY, 2019);
		assertValue("cgpBaseLabel", 1555.00);
		assertValue("cgcBaseLabel", 1555.00);
	}
	// -------------------------------------------------------------------------

}
