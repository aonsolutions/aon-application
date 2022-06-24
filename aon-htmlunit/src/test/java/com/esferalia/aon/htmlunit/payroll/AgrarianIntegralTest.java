package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

public class AgrarianIntegralTest extends BaseIntegralTestCase {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.agrarian.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		setup(url, user, password);
		
		wait4Id("regimen_especial_agrario");
	}

	// ------------------------------------------------------------------------

	@Test
	public void TestQuote() throws Exception {

		if (!isDisplayed("base_mïnima,_jornadas_(_grupo_10_)"))
			open("principal");

		wait4Id("base_mïnima,_jornadas_(_grupo_10_)");

		draft("BASE MÏNIMA, JORNADAS ( GRUPO 10 )");
		calculate(Calendar.JUNE, 2022);
		assertValue("cgcBaseLabel", 50.73 * 10);
		assertValue("cgpBaseLabel", 50.73 * 10);

	}


	// -------------------------------------------------------------------------

}
