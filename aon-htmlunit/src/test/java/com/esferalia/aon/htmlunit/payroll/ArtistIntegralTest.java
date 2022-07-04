package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ArtistIntegralTest extends BaseIntegralTestCase {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.artist.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		setup(url, user, password);
		
		wait4Id("artistas");
	}

	// ------------------------------------------------------------------------

	@Test
	public void TestQuote() throws Exception {

		if (!isDisplayed("base_minima,_parcial"))
			open("principal");

		wait4Id("base_minima,_parcial");

		draft("BASE MÍNIMA, PARCIAL");
		calculate(Calendar.JULY, 2022);
		assertValue("cgcBaseLabel", 1166.70 * 0.45);
		assertValue("cgpBaseLabel", 1166.70 * 0.45);

	}


	// -------------------------------------------------------------------------

}
