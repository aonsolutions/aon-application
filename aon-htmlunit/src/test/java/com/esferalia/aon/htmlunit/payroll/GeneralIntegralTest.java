package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;


public class GeneralIntegralTest extends BaseIntegralTestCase {

	public static final String INTEGRATION_PAYROLL_URL = "integration.test.general.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		setup(url, user, password);
		
		wait4Id("regimen_general");
	}

	// ------------------------------------------------------------------------

	@Test
	public void TestDraftWeekHours() throws Exception {
		open("draft");

		wait4Id("draft_parcial,_vacio");

		draft("DRAFT PARCIAL, VACIO");
		calculate(Calendar.DECEMBER);
		setValue("description-box-new-payment", "[1] S4L4R10 B4S3");
		setValue("amount-box-new-payment", "1666.00 * DIAS_TRABAJADOS / DIAS_MES");
		wait4Id("description-box-1");
		assertElement("editor-horas_sabado");
		assertElement("editor-horas_domingo");
		assertElement("editor-horas_lunes");
		assertElement("editor-horas_martes");
		assertElement("editor-horas_miercoles");
		assertElement("editor-horas_jueves");
		assertElement("editor-horas_viernes");
	}

	@Test
	public void TestDratPaymentVariables() throws Exception {
		open("draft");

		wait4Id("draft_completo,_convenio");

		// [1] SALARIO BASE
		// [2] COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3] PAGA EXTRAORDINARIA DE JULIO
		// [4] PAGA EXTRAORDINARIA DE DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		
		click("expand-button-agreement");
		click("expand-button-system");
		
		assertNotElement("editor-antiguedad");
		assertNotElement("editor-salario_base");
		assertNotElement("editor-prest_it");
		
		// PLUS_EXTRA_SALARIAL
		// Expression : PLUS_EXTRA_SALARIAL
		// Description : PLUS REGIMEN GENERAL ( VARIABLE == DEVENGO ) 
		assertElement("editor-plus_extra_salarial");
		double totalPayment = getValue("totalPaymentsLabel");
		
		setValue("editor-plus_extra_salarial", "66666.00 / 100.00");
		wait4Value("totalPaymentsLabel", totalPayment + 66666.00 / 100.00 );
		
	}
	

	@Test
	public void TestDratRedefineSystemVariables() throws Exception {
		open("draft");

		wait4Id("draft_completo,_convenio");

		// [1] SALARIO BASE
		// [2] COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3] PAGA EXTRAORDINARIA DE JULIO
		// [4] PAGA EXTRAORDINARIA DE DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		
		click("expand-button-agreement");
		click("expand-button-system");
		
		// Redefine 'FULL_TIME'
		double totalPayment = getValue("totalPaymentsLabel");

		selectOption("editor-tiempo_completo", "false");
		wait4Id("editor-horas_lunes");
		assertElement("editor-horas_martes");
		assertElement("editor-horas_miercoles");
		assertElement("editor-horas_jueves");
		assertElement("editor-horas_viernes");
		assertElement("editor-horas_sabado");
		assertElement("editor-horas_domingo");
		
		setValue("editor-horas_lunes", "4.00");
		wait4Value("totalPaymentsLabel", totalPayment * 4.00 / 40.00);
		setValue("editor-horas_martes", "4");
		wait4Value("totalPaymentsLabel", totalPayment * 8.00 / 40.00);
		setValue("editor-horas_miercoles", "2");
		wait4Value("totalPaymentsLabel", totalPayment * 10.00 / 40.00);
		setValue("editor-horas_jueves", "40/5");
		wait4Value("totalPaymentsLabel", totalPayment * 14.00 / 40.00);
		setValue("editor-horas_viernes", "40/5");
		wait4Value("totalPaymentsLabel", totalPayment * 18.00 / 40.00);
		
	}

	@Test
	public void TestDraftRedefinePayments() throws Exception {
		open("draft");

		wait4Id("draft_completo,_convenio");

		// [1] SALARIO BASE
		// [2] COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3] PAGA EXTRAORDINARIA DE JULIO
		// [4] PAGA EXTRAORDINARIA DE DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		double cgcBase = getValue("cgcBaseLabel");
		double totalPayment = getValue("totalPaymentsLabel");
		double totalLiquid = getValue("totalLiquidLabel");
		wait4Id("description-box-1");
		
		// Redefine description only.   
		setValue("description-box-1", "[1] S4L4R10 B4S3");
		wait4Id("agreement-button-1");
		
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentsLabel", totalPayment);
		assertValue("totalLiquidLabel", totalLiquid);

		click("agreement-button-1");
		wait4Value("description-box-1", "[1]SALARIO BASE");
		assertNotElement("agreement-button-1");
		
		click("undoButton");
		wait4Id("agreement-button-1");
		
		selectSaveTo("FROM_THIS_MONTH");
		click("acceptButton"); // click without waiting for calculate ?
		wait4Disabled("acceptButton", true);
		
		click("agreement-button-1");
		wait4Value("description-box-1", "[1]SALARIO BASE");
		assertNotElement("agreement-button-1");

		click("acceptButton"); 
		wait4Disabled("acceptButton", true);
		assertValue("description-box-1", "[1]SALARIO BASE");
		assertNotElement("agreement-button-1");
		
		
		
	}

	@Test
	public void TestDraftExtrasRedefine() throws Exception {
		open("draft");

		wait4Id("draft_completo,_convenio");
		// [1] SALARIO BASE
		// [2] COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3] PAGA EXTRAORDINARIA DE JULIO
		// [4] PAGA EXTRAORDINARIA DE DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		double cgcBase = getValue("cgcBaseLabel");
		double totalPayment = getValue("totalPaymentsLabel");
		double prorationBase = getText("prorationBaseLabel");
		wait4Id("description-box-3");
		
		// Redefine description only.   
		setValue("description-box-3", "[3] PAGA EXTRAORDINARIA DE VERANO");
		wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		wait4Class("payment-row-4", "aon-dataTable-row-highlight");
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentsLabel", totalPayment);
		assertText("prorationBaseLabel", prorationBase);

		//selectSaveTo("FROM_THIS_MONTH");
		//click("acceptButton");
		
		//click("agreement-button-3");
		//click("agreement-button-4");
		
		
		
	}
	
	@Test
	public void TestDraftFromScratch() throws Exception {

		open("draft");

		wait4Id("draft_completo,_vacio");

		draft("DRAFT COMPLETO, VACIO");
		calculate(Calendar.DECEMBER);
		setValue("description-box-new-payment", "[1] S4L4R10 B4S3");
		setValue("amount-box-new-payment", "1666.00 * DIAS_TRABAJADOS / DIAS_MES");
		wait4Id("description-box-1");
		assertValue("cgcBaseLabel", 1666.00);
		assertValue("totalPaymentsLabel", 1666.00);
		
		setValue("description-box-new-payment", "[2] PLU3");
		setValue("amount-box-new-payment", "100.00 * DIAS_TRABAJADOS / DIAS_MES");
		wait4Id("description-box-2");
		assertValue("cgcBaseLabel", 1666.00 + 100.00);
		assertValue("totalPaymentsLabel", 1666.00 + 100.00);
		
		
	}
	
	@Test
	public void TestFiniquito() throws Exception {
		
		open("finiquitos");

		wait4Id("cotizacion,_cero");

		draft("COTIZACIÓN, CERO");

		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.JUNE, 25);
		settle(calendar.getTime());
		
		assertValue("cgcBaseLabel", 0.00);
		assertValue("cgpBaseLabel", 0.00);
		assertValue("totalPaymentsLabel", 0.00);
		assertValue("totalLiquidLabel", 0.00);


		draft("COTIZACIÓN, MÁX");
		settle(calendar.getTime());
		
		assertValue("cgcBaseLabel", 666000.00);
		assertValue("cgpBaseLabel", 666000.00);
		assertValue("totalPaymentsLabel", 666000.00);
		assertValue("totalLiquidLabel", 666000.00 - (666000.00 * (4.70 + 1.55 + 0.10) / 100.00));

	
		draft("FINIQUITO, REDEFINIDO");
		settle(calendar.getTime());
		assertValue("cgcBaseLabel", 300.00);
		assertValue("cgpBaseLabel", 300.00);
		assertValue("totalPaymentsLabel", 300.00);
		assertValue("totalLiquidLabel", 300.00 - (300.00 * (4.70 + 1.55 + 0.10) / 100.00));
		

	}

	@Test
	public void TestAntiguedad() throws Exception {

		// + ANTIGÜEDAD
		open("antiguedad");

		wait4Id("1989_tiempo_completo_ordinario,_indefinido");

		draft("1989 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel",
				15454.46 / 14 							// SALARIO_BASE
						+ 15454.46 / 14 * 5 / 100 		// ANTIGUEDAD 1989-1992 ( 1 TRIENIO 5%)
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1992-1995 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 5 * 4 / 100 	// ANTIGUEDAD 1995-2016 (	5 CUATRIENIOS 4% )
		);

		draft("1991 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel",
				15454.46 / 14 							// SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1991-1994 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 5 * 4 / 100 	// ANTIGUEDAD 1994-2016 ( 5 CUATRIENIOS 4% )
		);

		draft("1993 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel",
				15454.46 / 14 							// SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1993-1996 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 5 * 4 / 100 	// ANTIGUEDAD 1996-2016 ( 5 CUATRIENIOS 4% )
		);

		draft("2012 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentsLabel", 15454.46 / 14 // SALARIO_BASE
				+ 15454.46 / 14 * 4 / 100 				// ANTIGUEDAD 2012-2016 ( 1 CUATRIENIO 4% )
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

		draft("EXTRAS, IT");
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		calculate(Calendar.AUGUST);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		calculate(Calendar.SEPTEMBER);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		calculate(Calendar.OCTOBER);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.DECEMBER, 31);
		Date endDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, 15);
		Date issueDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("totalPaymentsLabel", 1067.40 * 4 / 30 /6  +  1067.40 * 14 / 30 /6 );
		
		
		
		draft("EXTRAS, IT (REDEFINIDO)");
		calculate(Calendar.JULY);
		assertValue("cgcBaseLabel", 1067.40  + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40  + 1067.40 / 6 );
		calculate(Calendar.AUGUST);
		assertValue("cgcBaseLabel", 1067.40  + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40  + 1067.40 / 6 );
		calculate(Calendar.SEPTEMBER);
		assertValue("cgcBaseLabel", 1067.40  + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40  + 1067.40 / 6 );
		extra(issueDate, endDate);
		assertValue("totalPaymentsLabel", 1067.40/6 * 5  + (1067.40*20/30)/6);
		
		draft("MATERNIDAD, COMPLETA");
		calculate(Calendar.FEBRUARY);
		double cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.MARCH);
		assertValue("cgcBaseLabel", cgcBase );
		// Here start I.T
		calculate(Calendar.APRIL);
		assertValue("cgcBaseLabel", cgcBase );
		calculate(Calendar.MAY);
		assertValue("cgcBaseLabel", cgcBase );
		calculate(Calendar.JUNE);
		assertValue("cgcBaseLabel", cgcBase );


		draft("MATERNIDAD, PARCIAL");
		calculate(Calendar.NOVEMBER);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.DECEMBER);
		assertValue("cgcBaseLabel", cgcBase );

		draft("PATERNIDAD, PARCIAL");
		calculate(Calendar.NOVEMBER);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.DECEMBER);
		assertValue("cgcBaseLabel", cgcBase );

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


	@Test
	public void TestExtras() throws Exception {

		open("extras");

		wait4Id("extra,_devengo_fuera");

		draft("EXTRA, DEVENGO FUERA");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.DECEMBER, 15);
		Date issueDate = calendar.getTime();
		calendar.set(2016, Calendar.DECEMBER, 31);
		Date endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "");
		assertValue("cgpBaseLabel", "");
		assertValue("totalPaymentsLabel", 1067.40);
		
		calendar.set(2016, Calendar.JULY, 15);
		issueDate = calendar.getTime();
		calendar.set(2016, Calendar.JUNE, 30);
		endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "");
		assertValue("cgpBaseLabel", "");
		assertValue("totalPaymentsLabel", 1067.40/6 + (1067.40*29/30)/6);

		draft("EXTRA, REDEFINIDAS");
		calculate(Calendar.JANUARY);
		assertText("prorationBaseLabel", 1027.65 / 12.00 * 2.00 );
		calculate(Calendar.MAY);
		assertText("prorationBaseLabel", 1027.65 / 12.00 * 2.00 );
		
	}

	@Test
	public void TestNomina() throws Exception {

		open("nominas");

		wait4Id("nomina,_diferencias");

		draft("NOMINA, DIFERENCIAS");
		calculate(Calendar.JUNE);
		HtmlCheckBoxInput dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());
		
		
		calculate(Calendar.JULY);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());

		calculate(Calendar.AUGUST);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());

		calculate(Calendar.SEPTEMBER);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());
		
		calculate(Calendar.OCTOBER);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertFalse(dbSalaryCheck.isChecked());
		Assert.assertFalse(getElementById("dbTotalLiquidLabel").isDisplayed());

		calculate(Calendar.DECEMBER);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertFalse(dbSalaryCheck.isDisplayed());
		Assert.assertFalse(getElementById("dbTotalLiquidLabel").isDisplayed());

//		draft("NOMINA, EXTRAS");
//
//		draft("NOMINA, SALARIO BASE");
//
//		draft("NOMINA, OFICINAS Y DESPACHOS");
	}
	// -------------------------------------------------------------------------

}
