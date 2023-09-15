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
		calculate(Calendar.JUNE, 2023);
		assertValue("cgcBaseLabel", 54.78 * 10);
		assertValue("cgpBaseLabel", 54.78 * 10);

		draft("BASE MÍNIMA, MENSUAL ( GRUPO 01 )");
		calculate(Calendar.JUNE, 2023);
		assertValue("cgcBaseLabel", 1759.50);
		//assertValue("cgpBaseLabel", 1260.00);
	}

	@Test
	public void TestIT() throws Exception {

		if (!isDisplayed("enfermedad,_comun"))
			open("i.t");

		wait4Id("enfermedad,_comun");

		draft("ENFERMEDAD, COMÚN");
		calculate(Calendar.AUGUST, 2019);
		assertValue("cgcBaseLabel", 1350.00);
		assertValue("cgpBaseLabel", 1350.00);
		
		assertValue("totalPaymentLabel", 
		1350.00 / 31.00 * 4  
		+ 1350.00 / 31.00 * 3 * 0.00
		+ 1350.00 / 31.00 * 17 * 0.60
		+ 1350.00 / 31.00 * 7  * 0.75
		);

		draft("ENFERMEDAD, PROFESIONAL");
		calculate(Calendar.AUGUST, 2019);
		assertValue("cgcBaseLabel", 99.69 * 31 * 0.50);
		assertValue("cgpBaseLabel", 99.69 * 31 * 0.50);
		
		assertValue("totalPaymentLabel", 
		99.69 * 28.00  * 0.50
		+ 99.69 * 3.00 * 0.75 * 0.50
		);

	}

	// -------------------------------------------------------------------------

}
