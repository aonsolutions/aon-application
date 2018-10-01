package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;


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

	// TODO: @Test
	public void TestDraftWeekHours() throws Exception {
		
		if (!isDisplayed("draft_parcial,_vacio"))
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

		if (!isDisplayed("draft_completo,_convenio"))
			open("draft");

		wait4Id("draft_completo,_convenio");

		// [1] SALARIO BASE
		// [2] COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3] PAGA EXTRAORDINARIA DE JULIO
		// [4] PAGA EXTRAORDINARIA DE DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		
		expand("expand-button-agreement");
		expand("expand-button-system");
		
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
	

	// TODO:@Test
	public void TestDraftSetHours() throws Exception {
		if (!isDisplayed("draft_completo,_convenio"))
			open("draft");

		wait4Id("draft_completo,_convenio");

		// [1] SALARIO BASE
		// [2] COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3] PAGA EXTRAORDINARIA DE JULIO
		// [4] PAGA EXTRAORDINARIA DE DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		
		expand("expand-button-agreement");
		expand("expand-button-system");
		
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
		wait4Value("totalPaymentsLabel", totalPayment * 18.00 / 40.00);
		setValue("editor-horas_viernes", "40/5");
		wait4Value("totalPaymentsLabel", totalPayment * 26.00 / 40.00);
		
	}

	@Test
	public void TestDraftRedefinePayments() throws Exception {
		if (!isDisplayed("draft_completo,_convenio"))
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
		if (!isDisplayed("draft_completo,_convenio"))
			open("draft");

		wait4Id("draft_completo,_convenio");
		// [1]SALARIO BASE
		// [2]COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )
		// [3]PAGA EXTRAORDINARIA JULIO
		// [4]PAGA EXTRAORDINARIA DICIEMBRE
		draft("DRAFT COMPLETO, CONVENIO");

		calculate(Calendar.DECEMBER);
		double cgcBase = getValue("cgcBaseLabel");
		double totalPayment = getValue("totalPaymentsLabel");
		double prorationBase = getText("prorationBaseLabel");
		wait4Id("description-box-3");
		
		// Redefine description only.   
		setValue("description-box-3", "[3]PAGA EXTRAORDINARIA VERANO");
		wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		wait4Class("payment-row-4", "aon-dataTable-row-highlight");
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentsLabel", totalPayment);
		assertText("prorationBaseLabel", prorationBase);

		click("undoButton");
		wait4Value("description-box-3", "[3]PAGA EXTRAORDINARIA JULIO");
		assertValue("description-box-4", "[4]PAGA EXTRAORDINARIA DICIEMBRE");
		assertNotElement("agreement-button-3");
		assertNotElement("agreement-button-4");
		assertDisabled("undoButton", true);
		assertDisabled("undoAllButton", true);
		
		click("redoButton");
		wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		wait4Class("payment-row-4", "aon-dataTable-row-highlight");
		assertValue("description-box-3", "[3]PAGA EXTRAORDINARIA VERANO");
		assertValue("description-box-4", "[4]PAGA EXTRAORDINARIA DICIEMBRE");
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentsLabel", totalPayment);
		assertText("prorationBaseLabel", prorationBase);

		click("agreement-button-3");
		wait4Value("description-box-3", "[3]PAGA EXTRAORDINARIA JULIO");
		assertValue("description-box-4", "[4]PAGA EXTRAORDINARIA DICIEMBRE");
		assertNotElement("agreement-button-3");
		assertNotElement("agreement-button-4");

		setValue("description-box-4", "[4]PAGA EXTRAORDINARIA NAVIDAD");
		wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		wait4Class("payment-row-4", "aon-dataTable-row-highlight");
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentsLabel", totalPayment);
		assertText("prorationBaseLabel", prorationBase);

		click("acceptButton");
		wait4Disabled("acceptButton", true);
		
		click("agreement-button-4");
		wait4Value("description-box-4", "[4]PAGA EXTRAORDINARIA DICIEMBRE");
		assertValue("description-box-3", "[3]PAGA EXTRAORDINARIA JULIO");
		assertNotElement("agreement-button-3");
		assertNotElement("agreement-button-4");
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		assertValue("description-box-4", "[4]PAGA EXTRAORDINARIA DICIEMBRE");
		assertValue("description-box-3", "[3]PAGA EXTRAORDINARIA JULIO");
		assertNotElement("agreement-button-3");
		assertNotElement("agreement-button-4");
		
		
	}
	
	@Test
	public void TestDraftFromScratch() throws Exception {

		if (!isDisplayed("draft_completo,_convenio"))
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
	public void TestDraftUserExpression() throws Exception {

		if (!isDisplayed("user_expression,_/*user*/.../**/"))
			open("draft");

		wait4Id("user_expression,_/*user*/.../**/");

		draft("USER EXPRESSION, /*user*/.../**/");
		calculate(Calendar.getInstance().get(Calendar.MONTH));

		getElementById("db-amount-label-1").focus();
		wait4Value("db-amount-label-1", "PLUS_SALARIAL");
		click("fxButton");
		wait4Id("fxExpressionCodeArea");
		Assert.assertEquals(((HtmlTextArea) getElementById("fxExpressionCodeArea")).getText(),"DIAS_TRABAJADOS / DIAS_MES * /*user*/PLUS_SALARIAL/**/" );
		click("fxCancelButton");
		
		getElementById("db-amount-label-2").focus();
		wait4Value("db-amount-label-2", "SALARIO_MENSUAL");
		click("fxButton");
		wait4Id("fxExpressionCodeArea");
		Assert.assertEquals(((HtmlTextArea) getElementById("fxExpressionCodeArea")).getText(),"/*user*/SALARIO_MENSUAL/**/ * DIAS_TRABAJADOS / DIAS_MES " );
		click("fxCancelButton");
		 
		
		getElementById("db-amount-label-3").focus();
		wait4Value("db-amount-label-3", "PLUS_DISPONIBILIDAD");
		click("fxButton");
		wait4Id("fxExpressionCodeArea");
		Assert.assertEquals(((HtmlTextArea) getElementById("fxExpressionCodeArea")).getText(),"DIAS_TRABAJADOS * /*user*/ PLUS_DISPONIBILIDAD/**/ / DIAS_MES" );
		click("fxCancelButton");
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
	public void TestBasesSMIYIPREM() throws Exception {

		open("smi_&_iprem");

		wait4Id("salario,_minimo");

		draft("SALARIO, MÍNIMO");
		calculate(Calendar.JANUARY,2018);
		assertText("totalPaymentLabel", 735.9);

		draft("INDICADOR, PÚBLICO DE RENTA DE EFECTOS MÚLTIPLES");
		calculate(Calendar.JANUARY,2018);
		assertText("totalPaymentLabel", 537.84);
	}
	
	@Test
	public void TestBasesMaximasYMinimas() throws Exception {

		open("bases_maximas_y_minimas");

		wait4Id("base,_maxima_(_grupo_01_)");

		draft("BASE, MÁXIMA ( GRUPO 01 )");
		calculate(Calendar.DECEMBER,2016);
		assertValue("cgcBaseLabel", 3642.00);
		assertValue("cgpBaseLabel", 3642.00);
		calculate(Calendar.JANUARY,2017);
		assertValue("cgcBaseLabel", 3751.20);
		assertValue("cgpBaseLabel", 3751.20);
		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 3751.20);
		assertValue("cgpBaseLabel", 3751.20);
		calculate(Calendar.JULY,2018);
		assertValue("cgcBaseLabel", 3751.20);
		assertValue("cgpBaseLabel", 3751.20);
		calculate(Calendar.AUGUST,2018);
		assertValue("cgcBaseLabel", 3803.70);
		assertValue("cgpBaseLabel", 3803.70);

		draft("BASE, MÍNIMA ( GRUPO 01 )");
		calculate(Calendar.DECEMBER,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 764.40);
		calculate(Calendar.JANUARY,2017);
		assertValue("cgcBaseLabel", 1152.90);
		assertValue("cgpBaseLabel",  825.60);
		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 1199.10);
		assertValue("cgpBaseLabel",  858.60);
		
		draft("BASE, MÍNIMA ( GRUPO 02 )");
		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 994.20);
		assertValue("cgpBaseLabel",  858.60);

		draft("BASE, MÍNIMA ( GRUPO 03 )");
		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 864.90);
		assertValue("cgpBaseLabel",  858.60);

		draft("BASE, MÍNIMA ( GRUPO 04 )");
		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 858.60);
		assertValue("cgpBaseLabel",  858.60);

		// M : 2
		// T : 4
		// W : 2
		// J : 4
		// V : 2
		// S : 0
		// D : 0
		Map<Integer, Double> weekHours = new HashMap<Integer, Double>();
		weekHours.put(Calendar.MONDAY, 2.00);
		weekHours.put(Calendar.TUESDAY, 4.00);
		weekHours.put(Calendar.WEDNESDAY, 2.00);
		weekHours.put(Calendar.THURSDAY, 4.00);
		weekHours.put(Calendar.FRIDAY, 2.00);
		weekHours.put(Calendar.SATURDAY, 0.00);
		weekHours.put(Calendar.SUNDAY, 0.00);
		
		double hours = 0.00;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2017);
		for ( ; calendar.get(Calendar.MONTH) == Calendar.JANUARY; calendar.add(calendar.DATE, 1))
			hours += weekHours.get(calendar.get(Calendar.DAY_OF_WEEK));
			
		draft("BASE, MÍNIMA ( GRUPO 07 )");
		calculate(Calendar.JANUARY,2017);
//		assertValue("cgcBaseLabel", 825.60);
//		assertValue("cgpBaseLabel", 825.60);

		draft("BASE, MÍNIMA IT ( GRUPO 01 )");
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 764.40);

		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 13 * 1067.40 / 30 + 764.40 * 17 / 30);

		calculate(Calendar.JANUARY,2017);
		assertValue("cgcBaseLabel", 1152.90);
		assertValue("cgpBaseLabel",  825.60);

		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 1199.10);
		assertValue("cgpBaseLabel",  858.60);
	}

	@Test
	public void TestIT() throws Exception {

		open("i.t");

		wait4Id("base_minima_diaria,_i.t");

		draft("LACTANCIA, PERIODO");
		calculate(Calendar.APRIL,2018);
		double cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.MAY,2018);
		assertValue("cgcBaseLabel", cgcBase );
		calculate(Calendar.JUNE,2018);
		assertValue("cgcBaseLabel", cgcBase );
		assertValue("totalPaymentsLabel", 0.00 );

		draft("RIESGO, DURANTE EL EMBARAZO");
		calculate(Calendar.APRIL,2018);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.MAY,2018);
		assertValue("cgcBaseLabel", cgcBase );
		calculate(Calendar.JUNE,2018);
		assertValue("cgcBaseLabel", cgcBase );
		assertValue("totalPaymentsLabel", 0.00 );

		draft("BASE MÍNIMA DIARIA, I.T");
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", 25.48 * 31); // GRUPO 09
		assertValue("cgpBaseLabel", 25.48 * 31); // GRUPO 09

		draft("BASE MÍNIMA MENSUAL, I.T");
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", 764.40); // GRUPO 05
		assertValue("cgpBaseLabel", 764.40); // GRUPO 05

		draft("ENFERMEDAD, COMÚN");
		calculate(Calendar.MARCH,2016);

		draft("ENFERMEDAD, PROFESIONAL");

		draft("GARANTIZADO, 100%");
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("totalPaymentsLabel", 1067.40 * 13 / 30 + (1067.40 + 1067.40 / 6) * 17 / 30 // 17
																								// DIAS
																								// COTIZADOS
		);
		calculate(Calendar.AUGUST,2016);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("totalPaymentsLabel", 1067.40 + 1067.40 / 6);

		draft("GARANTIZADO, ENFERMEDAD COMÚN");
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);

		draft("GARANTIZADO, ENFERMEDAD PROFESIONAL");
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentsLabel", 1067.40);

		draft("GARANTIZADO, EXTRAS CON GARANTIZADO"); // 3 PAGAS
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("cgpBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("totalPaymentsLabel", 1200.00);
		calculate(Calendar.AUGUST,2016);
		assertValue("cgcBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("cgpBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("totalPaymentsLabel", 1200.00);
		// TODO: EXTRA

		draft("GARANTIZADO ENFERMEDAD COMÚN, Y PROFESIONAL");
		calculate(Calendar.JUNE, 2016);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentsLabel", 764.40);

		draft("GARANTIZADOS, ENFERMEDAD COMÚN");
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentsLabel", 764.40);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentsLabel", 764.40);


		draft("GARANTIZADOS, ENFERMEDAD PROFESIONAL");
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1000.00);
		assertValue("cgpBaseLabel", 1000.00);
		assertValue("totalPaymentsLabel", 1000.00 * 20 / 30 + 900.00 * 10 / 30);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1000.00);
		assertValue("cgpBaseLabel", 1000.00);
		assertValue("totalPaymentsLabel", 1000.00);

		draft("EXTRAS, IT");
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		calculate(Calendar.AUGUST,2016);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		calculate(Calendar.SEPTEMBER,2016);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6 );
		calculate(Calendar.OCTOBER,2016);
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
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40  + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40  + 1067.40 / 6 );
		calculate(Calendar.AUGUST,2016);
		assertValue("cgcBaseLabel", 1067.40  + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40  + 1067.40 / 6 );
		calculate(Calendar.SEPTEMBER,2016);
		assertValue("cgcBaseLabel", 1067.40  + 1067.40 / 6 );
		assertValue("cgpBaseLabel", 1067.40  + 1067.40 / 6 );
		extra(issueDate, endDate);
		assertValue("totalPaymentsLabel", 1067.40/6 * 5  + (1067.40*20/30)/6);
		
		draft("MATERNIDAD, COMPLETA");
		calculate(Calendar.FEBRUARY,2016);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.MARCH,2016);
		assertValue("cgcBaseLabel", cgcBase );
		// Here start I.T
		calculate(Calendar.APRIL,2016);
		assertValue("cgcBaseLabel", cgcBase );
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", cgcBase );
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", cgcBase );


		draft("MATERNIDAD, PARCIAL");
		calculate(Calendar.NOVEMBER,2016);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.DECEMBER,2016);
		assertValue("cgcBaseLabel", cgcBase );

		draft("PATERNIDAD, PARCIAL");
		calculate(Calendar.NOVEMBER,2016);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.DECEMBER,2016);
		assertValue("cgcBaseLabel", cgcBase );

		draft("PATERNIDAD, PARCIAL");
		calculate(Calendar.NOVEMBER,2016);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.DECEMBER,2016);
		assertValue("cgcBaseLabel", cgcBase );

	}

	@Test
	public void TestBrutoYNeto() throws Exception {

		open("bruto_y_neto");

		wait4Id("bruto,_enfermedad_comun_(bases)");

		draft("BRUTO, ENFERMEDAD COMÚN (BASES)");
		calculate(Calendar.JUNE, 2016);
		assertValue("totalPaymentsLabel", 1067.40 / 30 * 5 * 0.60 + 1000.00 * 22 / 30);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40 / 30 * 8 + 1000.00 * 22 / 30);

		draft("BRUTO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.MAY, 2016);
		assertValue("totalPaymentsLabel", 1500.00);
		calculate(Calendar.JUNE, 2016);
		assertValue("totalPaymentsLabel", 1500.00);

		draft("NETO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.MAY, 2016);
		assertValue("totalLiquidLabel", 2125.00);
		calculate(Calendar.JUNE, 2016);
		assertValue("totalLiquidLabel", 2125.00);

	}

	@Test
	public void TestIRPFAraba() throws Exception {

		open("i.r.p.f_-_alava/araba");

		wait4Id("i.r.p.f_araba_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F ARABA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY, 2017); // January, 2016 it's not visible
		assertValue("irpfPercentTexTBox", "2,00 %");

	}

	@Test
	public void TestIRPFBizkaia() throws Exception {

		open("i.r.p.f_-_bizkaia");

		wait4Id("i.r.p.f_bizkaia_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F BIZKAIA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY, 2017); // January, 2016 it's not visible
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
		assertText("prorationBaseLabel", ( 1027.65 / 6 ) / 12.00 * 2.00 );
		calculate(Calendar.MAY);
		assertText("prorationBaseLabel", ( 1027.65 / 6 ) / 12.00 * 2.00 );
		
//		click("viewButton");
//		wait4Id("formerMenuItem");
//		click("formerMenuItem");
		
		wait4Id("extra,_fin_de_contrato");
		draft("EXTRA, FIN DE CONTRATO");

		calculate(Calendar.FEBRUARY, 2019);
		double prorationBase = getText("prorationBaseLabel");
		calculate(Calendar.MARCH, 2019);
		prorationBase += getText("prorationBaseLabel");
		calculate(Calendar.APRIL, 2019);
		prorationBase += getText("prorationBaseLabel");
		calculate(Calendar.MAY, 2019);
		prorationBase += getText("prorationBaseLabel");
		
		calendar = Calendar.getInstance();
		calendar.set(2019, Calendar.JULY, 15);
		issueDate = calendar.getTime();
		calendar.set(2019, Calendar.MAY, 8);
		endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "");
		assertValue("cgpBaseLabel", "");
		assertValue("totalPaymentsLabel", prorationBase);
		
		
		wait4Id("extra_cra_001,_no_incuida_en_otros_apartados");
		draft("EXTRA CRA 001, NO INCUIDA EN OTROS APARTADOS");
		calculate(Calendar.SEPTEMBER, 2018);
		
		double paga3 = getValue("db-amount-label-3");
		double paga2 = getValue("db-amount-label-2");
		double paga1 = getValue("db-amount-label-1");

		double salarioBase = getValue("db-amount-label-4");
		Assert.assertEquals(paga1, salarioBase/12.00, 0.005);
		Assert.assertEquals(paga2, salarioBase/12.00, 0.005);
		Assert.assertEquals(paga3, salarioBase/12.00, 0.005);

	}

	public void TestNomina() throws Exception {

		open("nominas");

		wait4Id("nomina,_diferencias");

		draft("NOMINA, DIFERENCIAS");
		calculate(Calendar.JUNE, 2016);
		HtmlCheckBoxInput dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());
		
		
		calculate(Calendar.JULY, 2016);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());

		calculate(Calendar.AUGUST, 2016);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());

		calculate(Calendar.SEPTEMBER, 2016);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertTrue(dbSalaryCheck.isChecked());
		Assert.assertTrue(getElementById("dbTotalLiquidLabel").isDisplayed());
		
		calculate(Calendar.OCTOBER, 2016);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertTrue(dbSalaryCheck.isDisplayed());
		Assert.assertFalse(dbSalaryCheck.isChecked());
		Assert.assertFalse(getElementById("dbTotalLiquidLabel").isDisplayed());

		calculate(Calendar.DECEMBER, 2016);
		dbSalaryCheck = getElementById("dbSalaryCheck-input");
		Assert.assertFalse(dbSalaryCheck.isDisplayed());
		Assert.assertFalse(getElementById("dbTotalLiquidLabel").isDisplayed());

//		draft("NOMINA, EXTRAS");
//
//		draft("NOMINA, SALARIO BASE");
//
//		draft("NOMINA, OFICINAS Y DESPACHOS");
	}
	
	//@Test
	public void TestIncidenciasCompleto() throws Exception {

		if (!isDisplayed("complemento_i,_incidencia"))
			open("incidencias");
		
		wait4Id("complemento_i,_incidencia");
		events("COMPLEMENTO_I, INCIDENCIA");

		//Verificar valores iniciales
		for(int i = 7; i<13; i++){
			if(i == 8)
				assertText("complemento_i_"+i,"4");
			else if (i == 7)
				assertText("complemento_i_"+i,"3");
			else
				assertText("complemento_i_"+i,"2");
		}
		
		//Click al complemento de agosto
		HtmlDivision complementoAgosto = getElementById("complemento_i_8");
		complementoAgosto.click();
		
		//Click a new value
		HtmlButton newValue = getElementById("new_value_complemento_i");
		newValue.click();
		
		//Poner nuevo valor DoubleBox
		wait4Id("value_box");
		setValue("value_box", "7");
		HtmlButton accept = getElementById("input_accept");
		accept.click();
		
		//Comparar nuevo valor
		wait4Id("complemento_i");
		assertText("complemento_i_8","7");
		
		//Comparar nuevo valor al hacer undo
		HtmlButton undo = getElementById("undo_complemento_i");
		undo.click();
		wait4Id("complemento_i");
		assertText("complemento_i_8","4");
		
		//Comparar nuevo valor al hacer redo
		HtmlButton redo = getElementById("redo_complemento_i");
		redo.click();
		wait4Id("complemento_i");
		assertText("complemento_i_8","7");
		
	}
	
	//@Test
	public void TestIncidenciasParcial() throws Exception {

		if (!isDisplayed("complemento,_tiempo_parcial"))
			open("incidencias");

		wait4Id("complemento,_tiempo_parcial");
		events("COMPLEMENTO, TIEMPO_PARCIAL");

		//Verificamos unos valores iniciales
		assertText("kms_9","30");
		assertText("kms_6","-");
		
		changeDisplayedHolidays(false);
		HtmlTableRow holidaysRow = getElementById("row_1");
		Assert.assertFalse(holidaysRow.isDisplayed());
		changeDisplayedHolidays(true);
		Assert.assertTrue(holidaysRow.isDisplayed());
		
	}
	
	//@Test
	public void TestCalendar() throws Exception {
		if (!isDisplayed("testing,_calendario"))
			open("calendario");
		
		wait4Id("testing,_calendario");
		calendar("TESTING, CALENDARIO");
		
		HtmlDivision cuatroSept = getElementById("13_8");
		cuatroSept.click();
		HtmlTableDataCell archivo_mi = getElementById("archivo_mi");
		archivo_mi.click();
		HtmlTableDataCell holiday_mi = getElementById("holiday_mi");
		holiday_mi.click();
		String style = cuatroSept.getAttribute("class");
		Assert.assertTrue(style.contains("holidayStyle"));
		assertValue("4_8_6_hour", 6);
		
		HtmlButton undo = getElementById("undo_btn");
		undo.click();
		cuatroSept = getElementById("13_8");
		style = cuatroSept.getAttribute("class");
		Assert.assertFalse(style.contains("holidayStyle"));
		
		HtmlButton redo = getElementById("redo_btn");
		redo.click();
		cuatroSept = getElementById("13_8");
		style = cuatroSept.getAttribute("class");
		Assert.assertTrue(style.contains("holidayStyle"));
		
	}

	@Test
	public void TestAtrasos() throws Exception {
		
		if (!isDisplayed("atrasos_tiempo_completo_ordinario,_indefinido"))
			open("atrasos");

		wait4Id("atrasos_tiempo_completo_ordinario,_indefinido");

		draft("ATRASOS TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		for ( int month = 0; month < 12; month++ ) {
			calculate(month, year); 
			click("salaryButton");
			wait4Id("dbSalaryCheck");
		}
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date endDate = calendar.getTime();
		
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.MONTH,Calendar.JANUARY);
		Date startDate = calendar.getTime();
		
		delay(startDate, endDate);
		assertValue("totalPaymentsLabel", 0.00, 0.00);
		assertValue("totalLiquidLabel", 00.00, 0.00);
		
		//visual asserts
		HtmlButton fxButton = getElementById("fxButton");
		
		Pattern hidden = Pattern.compile("display\\s*:\\s*none");
		
		Assert.assertEquals(true, hidden.matcher(fxButton.getAttribute("style")).find());
		//Assert.assertFalse(fxButton.isDisplayed());
		HtmlButton undoAllButton = getElementById("undoAllButton");
		Assert.assertEquals(true, hidden.matcher(undoAllButton.getAttribute("style")).find());
		//Assert.assertFalse(undoAllButton.isDisplayed());
		HtmlButton undoButton = getElementById("undoButton");
		Assert.assertEquals(true, hidden.matcher(undoButton.getAttribute("style")).find());
		//Assert.assertFalse(undoButton.isDisplayed());
		HtmlButton redoButton = getElementById("redoButton");
		Assert.assertEquals(true, hidden.matcher(redoButton.getAttribute("style")).find());
		//Assert.assertFalse(redoButton.isDisplayed());


		calculate(Calendar.JANUARY);
		setValue("description-box-new-payment", "[3]ATRASOS");
		setValue("amount-box-new-payment", "100");
		wait4Id("description-box-3");
		assertValue("description-box-3", "[3]ATRASOS");

		click("acceptButton"); // click without waiting for calculate ?
		wait4Disabled("acceptButton", true);
		
		
		delay(startDate, endDate);
		calendar.setTime(endDate);
		int endMonth = calendar.get(Calendar.MONTH);
		assertValue("totalPaymentsLabel", 100.00 * (endMonth + 1));

		calculate(Calendar.JANUARY);
		click("delete-button-3");
		click("acceptButton"); // click without waiting for calculate ?
		wait4Disabled("acceptButton", true);
		
		calculate(Calendar.JANUARY);
		setValue("description-box-new-payment", "[3]ATRASOS");
		setValue("amount-box-new-payment", "100");
		wait4Id("description-box-3");
		assertValue("description-box-3", "[3]ATRASOS");
		
		delay(startDate, endDate);
		//Assert.assertFalse(getElementById("description-box-2").isDisplayed()); ???
		assertValue("cgcBaseLabel", 0.00);
		assertValue("totalPaymentsLabel", 0.00);
		assertValue("totalLiquidLabel", 0.00);
		
	}

	@Test
	public void TestSonny() throws Exception {

		if (!isDisplayed("constantes,_i"))
			open("sonny");

		wait4Id("constantes,_i");

		draft("CONSTANTES, I");
		
		calculate(Calendar.MARCH, 2018);
		Double cgcBase = getValue("cgcBaseLabel");
		Double totalPayment = getValue("totalPaymentsLabel");
		Assert.assertEquals(cgcBase, totalPayment);

		calculate(Calendar.APRIL, 2018);
		Double sonnyCgcBase = getValue("cgcBaseLabel");
		Double sonnytotalPayment = getValue("totalPaymentsLabel");
		Assert.assertEquals(cgcBase, sonnyCgcBase);
		Assert.assertEquals(totalPayment, sonnytotalPayment);
		HtmlTable eventsTable = getElementById("eventsTable");
		Assert.assertEquals(0, eventsTable.getRowCount());
		
		
		draft("CONSTANTES, II (PAGAS)");
		
		calculate(Calendar.MARCH, 2018);
		cgcBase = getValue("cgcBaseLabel");
		totalPayment = getValue("totalPaymentsLabel");

		calculate(Calendar.APRIL, 2018);
		sonnyCgcBase = getValue("cgcBaseLabel");
		sonnytotalPayment = getValue("totalPaymentsLabel");
		Assert.assertEquals(cgcBase, sonnyCgcBase);
		//Assert.assertEquals(totalPayment, sonnytotalPayment);
		eventsTable = getElementById("eventsTable");
		Assert.assertEquals(0, eventsTable.getRowCount());
		
		draft("CONSTANTES, III (BONO)");
		
		calculate(Calendar.MARCH, 2018);
		cgcBase = getValue("cgcBaseLabel");
		totalPayment = getValue("totalPaymentsLabel");

		calculate(Calendar.APRIL, 2018);
		sonnyCgcBase = getValue("cgcBaseLabel");
		sonnytotalPayment = getValue("totalPaymentsLabel");
		Assert.assertEquals(cgcBase, sonnyCgcBase);
		//Assert.assertEquals(totalPayment, sonnytotalPayment);
		eventsTable = getElementById("eventsTable");
		Assert.assertEquals(0, eventsTable.getRowCount());
	}

	
	@Test
	public void TestPercepcionesDelSistema() throws Exception {

		if (!isDisplayed("prest,_enfermedad_comun"))
			open("percepciones_del_sistema");
		
		wait4Id("prest,_enfermedad_comun");

		// PREST, ENFERMEDAD COMUN
		draft("PREST, ENFERMEDAD COMUN");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // PREST. POR ENFERMEDAD COMÚN
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DE LA EMPRESA
		assertDisabled("delete-button-2", true);
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS		
		assertDisabled("delete-button-3", true);
		assertInputDisabled( "db-amount-label-4" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-4", false);
		
		
		setValue("description-box-1", "PRESTACIÓN POR ENFERMEDAD COMÚN");
		
		wait4Class("payment-row-1", "aon-dataTable-row-highlight");
		wait4Class("payment-row-2", "aon-dataTable-row-highlight");
		wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PREST. POR ENFERMEDAD COMÚN
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DE LA EMPRESA
		assertDisabled("delete-button-2", true);
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS		
		assertDisabled("delete-button-3", true);
		assertInputDisabled( "db-amount-label-4" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-4", false);
		
		click("edit-button-1");
		wait4Id("paymetDialogHTMLPanel");
		assertDisplay("fxPaymentButton", false);
		assertDisplay("resetPaymentButton", false);
		click("paymetDialogCancelButton");

		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		wait4NoClass("payment-row-2", "aon-dataTable-row-highlight");
		wait4NoClass("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PREST. POR ENFERMEDAD COMÚN
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DE LA EMPRESA
		assertDisabled("delete-button-2", true);
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS		
		assertDisabled("delete-button-3", true);
		assertInputDisabled( "db-amount-label-4" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-4", false);
		
		
		// PREST, ENFERMEDAD PROFESIONAL
		draft("PREST, ENFERMEDAD PROFESIONAL");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // PREST. POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-2", false);
		
		setValue("description-box-1", "PRESTACIÓN POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL");
		
		wait4Class("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-2", false);
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR ENFERMEDAD COMÚN
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-2", false);

		// PREST, MATERNIDAD
		draft("PREST, MATERNIDAD");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // PREST. POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		
		setValue("description-box-1", "PRESTACIÓN POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO");
		
		wait4Class("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		
		// PREST, PATERNIDAD
		draft("PREST, PATERNIDAD");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // PREST. POR PATERNIDAD
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR PATERNIDAD
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-2", false);
		
		setValue("description-box-1", "PRESTACIÓN POR PATERNIDAD");
		
		wait4Class("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR PATERNIDAD
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-2", false);
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // PRESTACIÓN POR PATERNIDAD
		assertDisabled("delete-button-1", true);
		assertInputDisabled( "db-amount-label-2" , false); // SALARIO BASE MENSUAL
		assertDisabled("delete-button-2", false);

		//VACACIONES, NO DISFRUTADAS
		draft("VACACIONES, NO DISFRUTADAS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2018, Calendar.JUNE, 15, 0, 0, 0);
		settle(calendar.getTime());

		assertInputDisabled( "db-amount-label-1" , true); // VACACIONES RETRIBUIDAS NO DISFRUTADAS
		assertNotElement("description-box-2");
		
		setValue("description-box-1", "VACACIONES");
		wait4Class("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // VACACIONES
		assertNotElement("description-box-2");

		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-1" , true); // VACACIONES
		assertNotElement("description-box-2");
		
		
	}	
	
	@Test
	public void TestCalculador() throws Exception {

		if (!isDisplayed("conceptos,_sin_nombre"))
			open("calculador");


		wait4Id("conceptos,_sin_nombre");

		draft("CONCEPTOS, SIN NOMBRE");
		calculate(Calendar.SEPTEMBER, 2018);
		Double totalPayment = getValue("totalPaymentsLabel");
		double salarioMensual = 666 * 2 ;
		double plus = salarioMensual * 0.25;
		double paga = ( salarioMensual + plus ) / 12;
		double antiguedad = ( salarioMensual + plus ) * 0.05;
		Assert.assertEquals((Double) ( salarioMensual + plus + 2 * paga + antiguedad )  , totalPayment, 0.001);
		
		
		draft("CONCEPTOS, APELLIDO");
		calculate(Calendar.SEPTEMBER, 2018);
		totalPayment = getValue("totalPaymentsLabel");
		salarioMensual = 999 ;
		plus = salarioMensual * 0.10;
		paga = ( salarioMensual + plus ) / 12;
		Assert.assertEquals((Double) ( salarioMensual + plus + 2 * paga )  , totalPayment, 0.001);
		
	}

	@Test
	public void TestInterinidad() throws Exception {

		if (!isDisplayed("interinidad,_tiempo_completo"))
			open("interinidad");


		wait4Id("interinidad,_tiempo_completo");

		draft("INTERINIDAD, TIEMPO COMPLETO");
		
		assertValue("textBox_PORCENTAJE_DESMPL", "1,55 %");
		click("costsCheck-input");
		assertValue("textBox_PORCENTAJE_DESMPL_E", "5,50 %");
		click("costsCheck-input");
		
		draft("INTERINIDAD, TIEMPO PARCIAL");
		
		assertValue("textBox_PORCENTAJE_DESMPL", "1,55 %");
		click("costsCheck-input");
		assertValue("textBox_PORCENTAJE_DESMPL_E", "5,50 %");
		click("costsCheck-input");
	}

	@Test
	public void TestPracticas() throws Exception {

		if (!isDisplayed("practicas,_tiempo_completo"))
			open("practicas");


		wait4Id("practicas,_tiempo_completo");

		draft("PRACTICAS, TIEMPO COMPLETO");
		
		assertValue("textBox_PORCENTAJE_DESMPL", "1,55 %");
		click("costsCheck-input");
		assertValue("textBox_PORCENTAJE_DESMPL_E", "5,50 %");
		click("costsCheck-input");
		
		draft("PRACTICAS, TIEMPO PARCIAL");
		
		assertValue("textBox_PORCENTAJE_DESMPL", "1,55 %");
		click("costsCheck-input");
		assertValue("textBox_PORCENTAJE_DESMPL_E", "5,50 %");
		click("costsCheck-input");
	}
	
	// -------------------------------------------------------------------------
	
	private void changeDisplayedHolidays(boolean flag) throws IndexOutOfBoundsException, IOException, InterruptedException{
		HtmlTableDataCell viewMore = getElementById("view_menu_item");
		viewMore.click();
		
		HtmlTableDataCell showVariables = getElementById("show_variables_menu_item");
		showVariables.click();
		
		wait4Id("checkbox_0");
		HtmlInput holidaysCheckBox = getElementById("checkbox_0-input");
		holidaysCheckBox.click();
		HtmlButton accept = getElementById("input_accept");
		accept.click();
		
	}
	
	private void expand(String id) throws IndexOutOfBoundsException, IOException, InterruptedException {
		HtmlButton button = getElementById(id);
		if ( button.getAttribute("class").contains("aon-icon-expandAll"))
			button.click();
	}

	private void collapse(String id) throws IndexOutOfBoundsException, IOException, InterruptedException {
		HtmlButton button = getElementById(id);
		if ( button.getAttribute("class").contains("aon-icon-collapseAll"))
			button.click();
	}
	
	private void assertDisplay(String id, boolean display ) {
		DomElement el = getElementById(id);
		Pattern hidden = Pattern.compile("display\\s*:\\s*none");
		Assert.assertEquals(!display, hidden.matcher(el.getAttribute("style")).find());
		
	}
}
