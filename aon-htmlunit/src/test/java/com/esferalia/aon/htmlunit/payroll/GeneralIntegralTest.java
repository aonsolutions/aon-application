package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

public class GeneralIntegralTest extends BaseIntegralTestCase {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.general.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		System.out.println("PROPERTIES:" + url+","+ user+","+ password);

		setup(url, user, password);
		
		wait4Id("regimen_general");
	}

	// ------------------------------------------------------------------------

	@Test
	public void TestAntiguedad() throws Exception {

		// + ANTIGÜEDAD
		open("antiguedad");

		wait4Id("1989_tiempo_completo_ordinario,_indefinido");

		draft("1989 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel",
				15454.46 / 14 // SALARIO_BASE
						+ 15454.46 / 14 * 5 / 100 // ANTIGUEDAD 1989-1992 ( 1
													// TRIENIO 5%)
						+ 15454.46 / 14 * 4 / 100 // ANTIGUEDAD 1992-1995 ( 1
													// TRIENIO 4%)
						+ 15454.46 / 14 * 5 * 4 / 100 // ANTIGUEDAD 1995-2016 (
														// 5 CUATRIENIOS 4% )
		);

		draft("1991 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel",
				15454.46 / 14 // SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 // ANTIGUEDAD 1991-1994 ( 1
													// TRIENIO 4%)
						+ 15454.46 / 14 * 5 * 4 / 100 // ANTIGUEDAD 1994-2016 (
														// 5 CUATRIENIOS 4% )
		);

		draft("1993 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel",
				15454.46 / 14 // SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 // ANTIGUEDAD 1993-1996 ( 1
													// TRIENIO 4%)
						+ 15454.46 / 14 * 5 * 4 / 100 // ANTIGUEDAD 1996-2016 (
														// 5 CUATRIENIOS 4% )
		);

		draft("2012 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel", 15454.46 / 14 // SALARIO_BASE
				+ 15454.46 / 14 * 4 / 100 // ANTIGUEDAD 2012-2016 ( 1 CUATRIENIO
											// 4% )
		);

		// + CONCEPTO ANTIGUEDAD, DESCRIPCION ?
		open("concepto_antiguedad,_descripcion");
		select("concepto_antiguedad,_descripcion-draft");
		wait4Text("employeeNameLabel", "CONCEPTO ANTIGUEDAD, DESCRIPCION");
	}

	@Test
	public void TestBasesMaximasYMinimas() throws Exception {

		open("bases_maximas_y_minimas");

		wait4Id("base,_maxima_(_grupo_01_)");

		draft("BASE, MÁXIMA ( GRUPO 01 )");
		assertValue("cgcBaseLabel", 3642.00);
		assertValue("cgpBaseLabel", 3642.00);

		draft("BASE, MÍNIMA ( GRUPO 01 )");
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 764.40);

		draft("BASE, MÍNIMA ( GRUPO 07 )");

		draft("BASE, MÍNIMA IT ( GRUPO 01 )");
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 764.40);

		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 13 * 1067.40 / 30 + 764.40 * 17 / 30);

	}

	@Test
	public void TestIT() throws Exception {

		open("i.t");

		wait4Id("base_minima_diaria,_i.t");

		draft("BASE MÍNIMA DIARIA, I.T");
		calculate(Calendar.MAY);
		assertValue("cgcBaseLabel", 25.48 * 31); // GRUPO 09
		assertValue("cgpBaseLabel", 25.48 * 31); // GRUPO 09

		draft("BASE MÍNIMA MENSUAL, I.T");
		calculate(Calendar.MAY);
		assertValue("cgcBaseLabel", 764.40); // GRUPO 05
		assertValue("cgpBaseLabel", 764.40); // GRUPO 05

		draft("ENFERMEDAD, COMÚN");
		calculate(Calendar.MARCH);

		draft("ENFERMEDAD, PROFESIONAL");

		draft("EXTRAS, IT");

		draft("GARANTIZADO, 100%");
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("totalPaymentsLabel", 1067.40 * 13 / 30 + (1067.40 + 1067.40 / 6) * 17 / 30 // 17
																								// DIAS
																								// COTIZADOS
		);
		calculate(Calendar.AUGUST);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("totalPaymentsLabel", 1067.40 + 1067.40 / 6);

		draft("GARANTIZADO, ENFERMEDAD COMÚN");
		calculate(Calendar.MAY);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);

		draft("GARANTIZADO, ENFERMEDAD PROFESIONAL");
		calculate(Calendar.MAY);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);

		draft("GARANTIZADO, EXTRAS CON GARANTIZADO"); // 3 PAGAS
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("cgpBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("totalPaymentsLabel", 1200.00);
		calculate(Calendar.AUGUST);
		assertValue("cgcBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("cgpBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("totalPaymentsLabel", 1200.00);
		// TODO: EXTRA

		draft("GARANTIZADO ENFERMEDAD COMÚN, Y PROFESIONAL");
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentsLabel", 764.40);

		draft("GARANTIZADOS, ENFERMEDAD COMÚN");
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentsLabel", 764.40);
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentsLabel", 764.40);

		draft("GARANTIZADOS, ENFERMEDAD PROFESIONAL");
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", 1000.00);
		assertValue("cgpBaseLabel", 1000.00);
		assertValue("totalPaymentsLabel", 1000.00 * 20 / 30 + 900.00 * 10 / 30);
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1000.00);
		assertValue("cgpBaseLabel", 1000.00);
		assertValue("totalPaymentsLabel", 1000.00);

//		draft("GARANTIZADOS, SIN I.Ts");
//
//		draft("MATERNIDAD, ");
//
//		draft("PATERNIDAD, ");
	}

	@Test
	public void TestBrutoYNeto() throws Exception {

		open("bruto_y_neto");

		wait4Id("bruto,_enfermedad_comun_(bases)");

		draft("BRUTO, ENFERMEDAD COMÚN (BASES)");
		calculate(Calendar.JUNE);
		assertValue("totalPaymentsLabel", 1067.40 / 30 * 5 * 0.60 + 1000.00 * 22 / 30);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40 / 30 * 8 + 1000.00 * 22 / 30);

		draft("BRUTO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.MAY);
		assertValue("totalPaymentsLabel", 1500.00);
		calculate(Calendar.JUNE);
		assertValue("totalPaymentsLabel", 1500.00);

		draft("NETO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.MAY);
		assertValue("totalLiquidLabel", 2125.00);
		calculate(Calendar.JUNE);
		assertValue("totalLiquidLabel", 2125.00);

	}

	@Test
	public void TestIRPFAraba() throws Exception {

		open("i.r.p.f_-_alava/araba");

		wait4Id("i.r.p.f_araba_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F ARABA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY);
		assertValue("irpfPercentTexTBox", "2,00 %");

	}

	@Test
	public void TestIRPFBizkaia() throws Exception {

		open("i.r.p.f_-_bizkaia");

		wait4Id("i.r.p.f_bizkaia_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F BIZKAIA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY);
		assertValue("irpfPercentTexTBox", "39,00 %");

	}

	@Test
	public void TestIRPFGipuzkoa() throws Exception {

		open("i.r.p.f_-_gipuzkoa");

		wait4Id("i.r.p.f_gipuzkoa_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F GIPUZKOA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY);
		assertValue("irpfPercentTexTBox", "0,00 %");

	}
	// -------------------------------------------------------------------------

}
