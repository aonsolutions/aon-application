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

		draft("TRAMO, 2");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 277.51);
		assertValue("cgcBaseLabel", 277.51);

		draft("TRAMO, 3");
		calculate(Calendar.MAY, 2018);
		assertValue("cgpBaseLabel", 387.29);
		assertValue("cgcBaseLabel", 387.29);
	
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

	// -------------------------------------------------------------------------

}
