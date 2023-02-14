package com.esferalia.aon.htmlunit.payroll;

import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_PASSWORD;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.INTEGRATION_BASE_USER;
import static com.esferalia.aon.htmlunit.HtmlUnitIT.LOGGER;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.htmlunit.HtmlUnitIT;
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

	private static final double DELTA = 0.01;
	public static final String INTEGRATION_PAYROLL_URL = "integration.test.general.payroll.url";

	@BeforeClass
	public static void setUp() throws Exception {
		String url = System.getProperty(INTEGRATION_PAYROLL_URL);
		String user = System.getProperty(INTEGRATION_BASE_USER);
		String password = System.getProperty(INTEGRATION_BASE_PASSWORD);
		
		setup(url, user, password);
		
		wait4Id("regimen_general");
		
		click("viewButton");
		wait4Id("formerMenuItem");
		click("formerMenuItem");
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
		
		//expand("expand-button-agreement");
		//expand("expand-button-system");
		
		assertNotElement("editor-antiguedad");
		assertNotElement("editor-salario_base");
		assertNotElement("editor-prest_it");
		
		//assertElement("editor-dias_nomina");
		
		// PLUS_EXTRA_SALARIAL
		// Expression : PLUS_EXTRA_SALARIAL
		// Description : PLUS REGIMEN GENERAL ( VARIABLE == DEVENGO ) 
		assertElement("editor-plus_extra_salarial");
		double totalPayment = getValue("totalPaymentLabel");
		
		setValue("editor-plus_extra_salarial", "66666.00 / 100.00");
		wait4Value("totalPaymentLabel", totalPayment + 66666.00 / 100.00 );
		
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
		double totalPayment = getValue("totalPaymentLabel");

		selectOption("editor-tiempo_completo", "false");
		wait4Id("editor-horas_lunes");
		assertElement("editor-horas_martes");
		assertElement("editor-horas_miercoles");
		assertElement("editor-horas_jueves");
		assertElement("editor-horas_viernes");
		assertElement("editor-horas_sabado");
		assertElement("editor-horas_domingo");
		
		setValue("editor-horas_lunes", "4.00");
		wait4Value("totalPaymentLabel", totalPayment * 4.00 / 40.00);
		setValue("editor-horas_martes", "4");
		wait4Value("totalPaymentLabel", totalPayment * 8.00 / 40.00);
		setValue("editor-horas_miercoles", "2");
		wait4Value("totalPaymentLabel", totalPayment * 10.00 / 40.00);
		setValue("editor-horas_jueves", "40/5");
		wait4Value("totalPaymentLabel", totalPayment * 18.00 / 40.00);
		setValue("editor-horas_viernes", "40/5");
		wait4Value("totalPaymentLabel", totalPayment * 26.00 / 40.00);
		
	}
	@Ignore("Obsolet")
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
		double totalPayment = getValue("totalPaymentLabel");
		double totalLiquid = getValue("totalLiquidLabel");
		wait4Id("description-box-1");
		
		// Redefine description only.   
		setValue("description-box-1", "[1] S4L4R10 B4S3");
		wait4Id("agreement-button-1");
		
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentLabel", totalPayment);
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
	@Ignore("Obsolet")
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
		double totalPayment = getValue("totalPaymentLabel");
		double prorationBase = getText("prorationBaseLabel");
		wait4Id("description-box-3");
		
		// Redefine description only.   
		setValue("description-box-3", "[3]PAGA EXTRAORDINARIA VERANO");
		wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		wait4Class("payment-row-4", "aon-dataTable-row-highlight");
		assertValue("cgcBaseLabel", cgcBase);
		assertValue("totalPaymentLabel", totalPayment);
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
		assertValue("totalPaymentLabel", totalPayment);
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
		assertValue("totalPaymentLabel", totalPayment);
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
		calculate(Calendar.APRIL);
		setValue("description-box-new-payment", "[1] S4L4R10 B4S3");
		setValue("amount-box-new-payment", "1666.00 * DIAS_TRABAJADOS / DIAS_MES");
		wait4Id("description-box-1");
		assertValue("cgcBaseLabel", 1666.00);
		assertValue("totalPaymentLabel", 1666.00);
		
		wait4Id("description-box-new-payment");
		
		DomElement el = getElementById("description-box-new-payment");
		LOGGER.warning( "description-box-new-payment : " + el  +", " + el.asNormalizedText()); 
		
		HtmlInput input = getElementById("description-box-new-payment");
		input.focus();
		input.setValueAttribute("[2] PLU3");
		input.blur();
		//setValue("description-box-new-payment", "[2] PLU3");
		input = getElementById("amount-box-new-payment");
		input.focus();
		input.setValueAttribute("100.00 * DIAS_TRABAJADOS / DIAS_MES");
		input.blur();
		//setValue("amount-box-new-payment", "100.00 * DIAS_TRABAJADOS / DIAS_MES");
		wait4Id("description-box-2");
		assertValue("cgcBaseLabel", 1666.00 + 100.00);
		assertValue("totalPaymentLabel", 1666.00 + 100.00);
		
		
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

//  Now SALARIO_BASE is read-only.		
//		getElementById("db-amount-label-2").focus();
//		wait4Value("db-amount-label-2", "SALARIO_MENSUAL");
//		click("fxButton");
//		wait4Id("fxExpressionCodeArea");
//		Assert.assertEquals(((HtmlTextArea) getElementById("fxExpressionCodeArea")).getText(),"/*user*/SALARIO_MENSUAL/**/ * DIAS_TRABAJADOS / DIAS_MES " );
//		click("fxCancelButton");
		 
		
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
		assertValue("totalPaymentLabel", 0.00);
		assertValue("totalLiquidLabel", 0.00);


		draft("COTIZACIÓN, MÁX");
		settle(calendar.getTime());
		// 2016  
		double maxCgcBase = 3642.00 * 12/30;
		assertValue("cgcBaseLabel", maxCgcBase);
		assertValue("cgpBaseLabel", maxCgcBase);
		assertValue("totalPaymentLabel", 666000.00);
		assertValue("totalLiquidLabel", 666000.00 - (maxCgcBase * (4.70 + 1.55 + 0.10) / 100.00));

	
		draft("FINIQUITO, REDEFINIDO");
		settle(calendar.getTime());
		assertValue("cgcBaseLabel", 200.00);
		assertValue("cgpBaseLabel", 200.00);
		assertValue("totalPaymentLabel", 300.00);
		assertValue("totalLiquidLabel", 300.00 - (200.00 * (4.70 + 1.55 + 0.10 + 18.49) / 100.00) - ( 100.00 * 18.49 / 100.00 ));
		

	}

	@Test
	public void TestAntiguedad() throws Exception {

		// + ANTIGÜEDAD
		open("antiguedad");

		wait4Id("1989_tiempo_completo_ordinario,_indefinido");

		draft("1989 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentLabel",
				15454.46 / 14 							// SALARIO_BASE
						+ 15454.46 / 14 * 5 / 100 		// ANTIGUEDAD 1989-1992 ( 1 TRIENIO 5%)
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1992-1995 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 6 * 4 / 100 	// ANTIGUEDAD 1995-2016 ( 6 CUATRIENIOS 4% ) 
		);

		draft("1991 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentLabel",
				15454.46 / 14 							// SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1991-1994 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 6 * 4 / 100 	// ANTIGUEDAD 1994-2016 ( 6 CUATRIENIOS 4% )
		);

		draft("1993 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentLabel",
				15454.46 / 14 							// SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1993-1996 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 6 * 4 / 100 	// ANTIGUEDAD 1996-2016 ( 6 CUATRIENIOS 4% )
		);

		draft("2012 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertValue("totalPaymentLabel", 
				15454.46 / 14 // SALARIO_BASE
						+ 15454.46 / 14 * 2 * 4 / 100 				// ANTIGUEDAD 2012-2016 ( 2 CUATRIENIOS 4% )
		);

		// + CONCEPTO ANTIGUEDAD, DESCRIPCION ?
		draft("CONCEPTO ANTIGUEDAD, DESCRIPCION");
		//open("concepto_antiguedad,_descripcion");
		//select("concepto_antiguedad,_descripcion-draft");
		wait4Text("employeeNameLabel", "CONCEPTO ANTIGUEDAD, DESCRIPCION");
	}

	@Test
	public void TestBasesSMIYIPREM() throws Exception {

		open("smi_&_iprem");

		wait4Id("salario,_minimo");

		draft("SALARIO, MÍNIMO");
		calculate(Calendar.JANUARY,2019);
		assertValue("totalPaymentLabel", 900.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("totalPaymentLabel", 965.00);
		calculate(Calendar.JANUARY,2022);
		assertValue("totalPaymentLabel", 1000.00);

		draft("INDICADOR, PÚBLICO DE RENTA DE EFECTOS MÚLTIPLES");
		calculate(Calendar.JANUARY,2019);
		assertValue("totalPaymentLabel", 537.84);
		calculate(Calendar.JANUARY,2022);
		assertValue("totalPaymentLabel", 579.02);
	}
	
	@Test
	public void TestBasesMaximasYMinimas() throws Exception {

		open("bases_maximas_y_minimas");

		wait4Id("base,_maxima_(_grupo_01_)");

		draft("BASE, MÁXIMA ( GRUPO 01 )");
//		calculate(Calendar.DECEMBER,2016);
//		assertValue("cgcBaseLabel", 3642.00);
//		assertValue("cgpBaseLabel", 3642.00);
//		calculate(Calendar.JANUARY,2017);
//		assertValue("cgcBaseLabel", 3751.20);
//		assertValue("cgpBaseLabel", 3751.20);
//		calculate(Calendar.JANUARY,2018);
//		assertValue("cgcBaseLabel", 3751.20);
//		assertValue("cgpBaseLabel", 3751.20);
//		calculate(Calendar.JULY,2018);
//		assertValue("cgcBaseLabel", 3751.20);
//		assertValue("cgpBaseLabel", 3751.20);
//		calculate(Calendar.AUGUST,2018);
//		assertValue("cgcBaseLabel", 3803.70);
//		assertValue("cgpBaseLabel", 3803.70);
		calculate(Calendar.JANUARY,2019);
		assertValue("cgcBaseLabel", 4070.10);
		assertValue("cgpBaseLabel", 4070.10);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 4139.40);
		assertValue("cgpBaseLabel", 4139.40);
		calculate(Calendar.JANUARY,2023);
		assertValue("cgcBaseLabel", 4495.50);
		assertValue("cgpBaseLabel", 4495.50);

		draft("BASE, MÁXIMA ( GRUPO 10 )");
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 4139.40);
		assertValue("cgpBaseLabel", 4139.40);
		calculate(Calendar.FEBRUARY,2022);
		assertValue("cgcBaseLabel", 4139.40 / 30.00 * 28);
		assertValue("cgpBaseLabel", 4139.40 / 30.00 * 28);
		calculate(Calendar.JANUARY,2023);
		assertValue("cgcBaseLabel", 4495.50);
		assertValue("cgpBaseLabel", 4495.50);
		calculate(Calendar.FEBRUARY,2023);
		assertValue("cgcBaseLabel", 4495.50 / 30.00 * 28);
		assertValue("cgpBaseLabel", 4495.50 / 30.00 * 28);

		draft("BASE, MÍNIMA ( GRUPO 01 )");
//		calculate(Calendar.DECEMBER,2016);
//		assertValue("cgcBaseLabel", 1067.40);
//		assertValue("cgpBaseLabel", 764.40);
//		calculate(Calendar.JANUARY,2017);
//		assertValue("cgcBaseLabel", 1152.90);
//		assertValue("cgpBaseLabel", 825.60);
//		calculate(Calendar.JANUARY,2018);
//		assertValue("cgcBaseLabel", 1199.10);
//		assertValue("cgpBaseLabel", 858.60);
		calculate(Calendar.JANUARY,2019);
		assertValue("cgcBaseLabel", 1466.40);
		assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1572.30);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1629.30);
		assertValue("cgpBaseLabel", 1166.70);
		
		draft("BASE, MÍNIMA ( GRUPO 02 )");
//		calculate(Calendar.JANUARY,2018);
//		assertValue("cgcBaseLabel", 994.20);
//		assertValue("cgpBaseLabel",  858.60);
		calculate(Calendar.JANUARY,2019);
		assertValue("cgcBaseLabel", 1215.90);
		assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1303.80);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1351.20);
		assertValue("cgpBaseLabel", 1166.70);

		draft("BASE, MÍNIMA ( GRUPO 03 )");
//		calculate(Calendar.JANUARY,2018);
//		assertValue("cgcBaseLabel", 864.90);
//		assertValue("cgpBaseLabel",  858.60);
		calculate(Calendar.JANUARY,2019);
		assertValue("cgcBaseLabel", 1057.80);
		assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1134.30);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1175.40);
		assertValue("cgpBaseLabel", 1166.70);

		draft("BASE, MÍNIMA ( GRUPO 04 )");
//		calculate(Calendar.JANUARY,2018);
//		assertValue("cgcBaseLabel", 858.60);
//		assertValue("cgpBaseLabel", 858.60);
		calculate(Calendar.JANUARY,2019);
		assertValue("cgcBaseLabel", 1050.00);
		assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1125.90);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1166.70);
		assertValue("cgpBaseLabel", 1166.70);

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

		draft("BASE, MÍNIMA ( GRUPO 09 )");
		calculate(Calendar.AUGUST,2019);
		assertValue("cgcBaseLabel", 1050.00);
		assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1125.90);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1166.70);
		assertValue("cgpBaseLabel", 1166.70);

		draft("BASE, MÍNIMA ( GRUPO 10 )");
		calculate(Calendar.AUGUST,2019);
		assertValue("cgcBaseLabel", 35.00 * 31);
		assertValue("cgpBaseLabel", 1050.00 /*35.00 * 31*/);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 37.53 * 30);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.OCTOBER,2021);
		assertValue("cgcBaseLabel", 37.53 * 31);
		assertValue("cgpBaseLabel", 1125.90);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 38.89 * 31);
		assertValue("cgpBaseLabel", 1166.70);

		draft("BASE, MÍNIMA IT ( GRUPO 01 )");
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1067.40);
		//assertValue("cgpBaseLabel", 764.40);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		//assertValue("cgpBaseLabel", 764.40);
		calculate(Calendar.JANUARY,2017);
		assertValue("cgcBaseLabel", 1152.90);
		//assertValue("cgpBaseLabel", 825.60);
		calculate(Calendar.JANUARY,2018);
		assertValue("cgcBaseLabel", 1199.10);
		//assertValue("cgpBaseLabel", 858.60);
		calculate(Calendar.JANUARY,2019);
		assertValue("cgcBaseLabel", 1466.40);
		//assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1572.30);
		//assertValue("cgpBaseLabel", 1050.00);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1629.30);
		//assertValue("cgpBaseLabel", 1050.00);

		draft("BASE, MÍNIMA PARCIAL ( HORAS )");
		calculate(Calendar.AUGUST,2019);
		assertValue("cgcBaseLabel", 6.33 * 44.00);
		//assertValue("cgpBaseLabel", 6.33 * 44.00);
		calculate(Calendar.SEPTEMBER,2019);
		assertValue("cgcBaseLabel", 6.33 * 42.00);
		//assertValue("cgpBaseLabel", 6.33 * 42.00);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 37.53 * 0.25 * 30.00);
		//assertValue("cgcBaseLabel", 6.78 * 44.00);
		//assertValue("cgpBaseLabel", 6.33 * 42.00);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 38.89 * 0.25 * 31.00);
		assertValue("cgpBaseLabel", 38.89 * 0.25 * 31.00);

		draft("BASE, MÍNIMA PARCIAL ( MENSUAL )");
		calculate(Calendar.AUGUST,2019);
		assertValue("cgcBaseLabel", 1050.00 * 0.25);
		//assertValue("cgpBaseLabel", 1050.00 * 0.25);
		calculate(Calendar.SEPTEMBER,2019);
		assertValue("cgcBaseLabel", 1050.00 * 0.25);
		//assertValue("cgpBaseLabel", 1050.00 * 0.25);
		calculate(Calendar.SEPTEMBER,2021);
		assertValue("cgcBaseLabel", 1125.90 * 0.25);
		//assertValue("cgpBaseLabel", 1050.00 * 0.25);
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1166.70 * 0.25);
		assertValue("cgpBaseLabel", 1166.70 * 0.25);

		draft("HORAS TRABAJADAS, MENSUAL");
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 1166.70 * 0.50);
		calculate(Calendar.FEBRUARY,2022);
		assertValue("cgcBaseLabel", 1166.70 * 0.50);
		calculate(Calendar.MARCH,2022);
		assertValue("cgcBaseLabel", 1166.70 * 0.50);
		assertValue("cgpBaseLabel", 1166.70 * 0.50);

		draft("BASE MÍNIMA, DIARIA ( TRAMOS )");
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 38.89 * 31 * 0.85);
		assertValue("cgpBaseLabel", 38.89 * 31 * 0.85);

		draft("HORAS NÓMINA, ( GRUPO 10 )");
		calculate(Calendar.JANUARY,2022);
		assertValue("cgcBaseLabel", 38.89 * 31 * 0.25);
		assertValue("cgpBaseLabel", 38.89 * 31 * 0.25);
		double salaryHours = getValue("db-amount-label-1");
		assertTrue((salaryHours * 7.03 ) <  ( 38.89 * 31 * 0.25 )  ) ;
		//click("expand-button-system");
		assertNotElement("editor-base_cgc_min_hora");

		draft("HORAS NÓMINA, (PATERNIDAD PARCIAL)");
		calculate(Calendar.JUNE,2022);
		double cgcBase = getValue("cgcBaseLabel");
		salaryHours = getValue("db-amount-label-6");
		assertTrue((salaryHours * 7.03 ) <  ( cgcBase * 0.5 ) ) ;

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
		assertValue("totalPaymentLabel", 0.00 );

		draft("RIESGO, DURANTE EL EMBARAZO");
		calculate(Calendar.APRIL,2018);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.MAY,2018);
		assertValue("cgcBaseLabel", cgcBase , DELTA);
		calculate(Calendar.JUNE,2018);
		assertValue("cgcBaseLabel", cgcBase , DELTA);
		assertValue("totalPaymentLabel", 0.00 );

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
		assertValue("totalPaymentLabel", 1067.40 * 13 / 30 + (1067.40 + 1067.40 / 6) * 17 / 30 // 17
																								// DIAS
																								// COTIZADOS
		);
		calculate(Calendar.AUGUST,2016);
		assertValue("cgcBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("cgpBaseLabel", 1067.40 + 1067.40 / 6);
		assertValue("totalPaymentLabel", 1067.40 + 1067.40 / 6);

		draft("GARANTIZADO, ENFERMEDAD COMÚN");
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentLabel", 1067.40);
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentLabel", 1067.40);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentLabel", 1067.40);

		draft("GARANTIZADO, ENFERMEDAD PROFESIONAL");
		calculate(Calendar.MAY,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentLabel", 1067.40);
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1067.40);
		assertValue("cgpBaseLabel", 1067.40);
		assertValue("totalPaymentLabel", 1067.40);

		draft("GARANTIZADO, EXTRAS CON GARANTIZADO"); // 3 PAGAS
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("cgpBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("totalPaymentLabel", 1200.00);
		calculate(Calendar.AUGUST,2016);
		assertValue("cgcBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("cgpBaseLabel", 1200.00 + 1200.00 / 4);
		assertValue("totalPaymentLabel", 1200.00);
		// TODO: EXTRA

		draft("GARANTIZADO ENFERMEDAD COMÚN, Y PROFESIONAL");
		calculate(Calendar.JUNE, 2016);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentLabel", 764.40);

		draft("GARANTIZADOS, ENFERMEDAD COMÚN");
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentLabel", 764.40);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 764.40);
		assertValue("cgpBaseLabel", 764.40);
		assertValue("totalPaymentLabel", 764.40);


		draft("GARANTIZADOS, ENFERMEDAD PROFESIONAL");
		calculate(Calendar.JUNE,2016);
		assertValue("cgcBaseLabel", 1000.00);
		assertValue("cgpBaseLabel", 1000.00);
		assertValue("totalPaymentLabel", 1000.00 * 20 / 30 + 900.00 * 10 / 30);
		calculate(Calendar.JULY,2016);
		assertValue("cgcBaseLabel", 1000.00);
		assertValue("cgpBaseLabel", 1000.00);
		assertValue("totalPaymentLabel", 1000.00);

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
		assertValue("totalPaymentLabel", 1067.40 * 4 / 30 /6  +  1067.40 * 14 / 30 /6 );
		
		
		
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
		assertValue("totalPaymentLabel", 1067.40/6 * 5  + (1067.40*20/30)/6);
		
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

		draft("PAGO DIRECTO, REDEFINIDO");
		calculate(Calendar.APRIL,2018);
		cgcBase = getValue("cgcBaseLabel");
		calculate(Calendar.MAY,2018);
		assertValue("cgcBaseLabel", 0.00 );
	}

	@Test
	public void TestBrutoYNeto() throws Exception {

		open("bruto_y_neto");

		wait4Id("bruto,_enfermedad_comun_(bases)");

		draft("BRUTO, ENFERMEDAD COMÚN (BASES)");
		calculate(Calendar.JUNE, 2016);
		assertValue("totalPaymentLabel", 1067.40 / 30 * 5 * 0.60 + 1000.00 * 22 / 30);
		assertValue("cgcBaseLabel", 1067.40);
		//assertValue("cgpBaseLabel", 1067.40);

		draft("BRUTO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.MAY, 2016);
		assertValue("totalPaymentLabel", 1500.00);
		calculate(Calendar.JUNE, 2016);
		assertValue("totalPaymentLabel", 1500.00);

		draft("BRUTO CONSTANTE, 1 DÍA");
		calculate(Calendar.OCTOBER, 2022);
		setValue("totalPaymentLabel", "100.00");
		wait4Id("description-box-1");
		assertValue("totalPaymentLabel", 100.00);
		click("acceptButton");  
		wait4Disabled("acceptButton", true);
		assertValue("totalPaymentLabel", 100.00);

		draft("BRUTO CONSTANTE, COMPLETO");
		calculate(Calendar.OCTOBER, 2022);
		setValue("totalPaymentLabel", "1900.00");
		wait4Id("description-box-1");
		assertValue("totalPaymentLabel", 1900.00);
		click("acceptButton");  
		wait4Disabled("acceptButton", true);
		assertValue("totalPaymentLabel", 1900.00);
		calculate(Calendar.NOVEMBER, 2022);
		assertValue("totalPaymentLabel", 3000.00);

		draft("BRUTO CONSTANTE, PARCIAL");
		calculate(Calendar.OCTOBER, 2022);
		setValue("totalPaymentLabel", "950.00");
		wait4Id("description-box-1");
		assertValue("totalPaymentLabel", 950.00);
		calculate(Calendar.NOVEMBER, 2022);
		assertValue("totalPaymentLabel", 1500.00);
		setValue("editor-coeficiente_parcialidad", "1.0");
		calculate(Calendar.OCTOBER, 2022);
		assertValue("totalPaymentLabel", 1900.00);
		calculate(Calendar.NOVEMBER, 2022);
		assertValue("totalPaymentLabel", 3000.00);
		

		draft("NETO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.MAY, 2016);
		assertValue("totalLiquidLabel", 2125.00);
		calculate(Calendar.JUNE, 2016);
		assertValue("totalLiquidLabel", 2125.00);
		
		draft("NETO CONSTANTE, 1 DÍA");
		calculate(Calendar.OCTOBER, 2022);
		setValue("totalLiquidLabel", "100.00");
		wait4Id("description-box-1");
		assertValue("totalLiquidLabel", 100.00);
		click("acceptButton");  
		wait4Disabled("acceptButton", true);
		assertValue("totalLiquidLabel", 100.00);
		

		draft("NETO CONSTANTE, COMPLETO");
		calculate(Calendar.OCTOBER, 2022);
		setValue("totalLiquidLabel", "1900.00");
		wait4Id("description-box-1");
		assertValue("totalLiquidLabel", 1900.00);
		click("acceptButton");  
		wait4Disabled("acceptButton", true);
		assertValue("totalLiquidLabel", 1900.00);
		calculate(Calendar.NOVEMBER, 2022);
		assertValue("totalLiquidLabel", 3000.00);

		draft("NETO CONSTANTE, PARCIAL");
		calculate(Calendar.OCTOBER, 2022);
		//setValue("irpfPercentTexTBox", "10.00");
		setValue("totalLiquidLabel", "950.00");
		wait4Id("description-box-1");
		assertValue("totalLiquidLabel", 950.00);
		click("acceptButton");  
		wait4Disabled("acceptButton", true);
		assertValue("totalLiquidLabel", 950.00);
		calculate(Calendar.NOVEMBER, 2022);
		assertValue("totalLiquidLabel", 1500.00);
		setValue("editor-coeficiente_parcialidad", "1.0");
		calculate(Calendar.OCTOBER, 2022);
		assertValue("totalLiquidLabel", 1900.00);
		calculate(Calendar.NOVEMBER, 2022);
		assertValue("totalLiquidLabel", 3000.00);
	}

	@Test
	public void TestIRPFAraba() throws Exception {

		open("i.r.p.f_-_alava/araba");

		wait4Id("i.r.p.f_araba_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F ARABA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY, 2017); // January, 2016 it's not visible
		assertValue("irpfPercentTexTBox", "2,00 %");
		
		calculate(Calendar.JANUARY, 2022);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		setValue("db-amount-label-1", "BRUTO(14790.01/12)");
		wait4Value("db-amount-label-1", 14790.01/12);
		assertValue("irpfPercentTexTBox", "6,00 %");		

		setValue("db-amount-label-1", "BRUTO(15530.01/12)");
		wait4Value("db-amount-label-1", 15530.01/12);
		assertValue("irpfPercentTexTBox", "7,00 %");		

		setValue("db-amount-label-1", "BRUTO(16460.01/12)");
		wait4Value("db-amount-label-1", 16460.01/12);
		assertValue("irpfPercentTexTBox", "8,00 %");		

		setValue("db-amount-label-1", "BRUTO(17700.01/12)");
		wait4Value("db-amount-label-1", 17700.01/12);
		assertValue("irpfPercentTexTBox", "9,00 %");
		
		//....
		
		setValue("db-amount-label-1", "BRUTO(43840.01/12)");
		wait4Value("db-amount-label-1", 43840.01/12);
		assertValue("irpfPercentTexTBox", "20,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(213160.01/12)");
		wait4Value("db-amount-label-1", 213160.01/12);
		assertValue("irpfPercentTexTBox", "40,00 %");		

		calculate(Calendar.SEPTEMBER, 2022);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		setValue("db-amount-label-1", "BRUTO(15070.00/12)");
		wait4Value("db-amount-label-1", 15070.00/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		
		setValue("db-amount-label-1", "BRUTO(15070.01/12)");
		wait4Value("db-amount-label-1", 15070.01/12);
		assertValue("irpfPercentTexTBox", "6,00 %");		

		setValue("db-amount-label-1", "BRUTO(15830.00/12)");
		wait4Value("db-amount-label-1", 15830.00/12);
		assertValue("irpfPercentTexTBox", "6,00 %");		
		setValue("db-amount-label-1", "BRUTO(15830.01/12)");
		wait4Value("db-amount-label-1", 15830.01/12);
		assertValue("irpfPercentTexTBox", "7,00 %");		

		setValue("db-amount-label-1", "BRUTO(16880.00/12)");
		wait4Value("db-amount-label-1", 16880.01/12);
		assertValue("irpfPercentTexTBox", "7,00 %");		
		setValue("db-amount-label-1", "BRUTO(16880.01/12)");
		wait4Value("db-amount-label-1", 16880.01/12);
		assertValue("irpfPercentTexTBox", "8,00 %");		

		setValue("db-amount-label-1", "BRUTO(18150.00/12)");
		wait4Value("db-amount-label-1", 18150.00/12);
		assertValue("irpfPercentTexTBox", "8,00 %");
		setValue("db-amount-label-1", "BRUTO(18150.01/12)");
		wait4Value("db-amount-label-1", 18150.01/12);
		assertValue("irpfPercentTexTBox", "9,00 %");
		
		calculate(Calendar.JANUARY, 2023);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		setValue("db-amount-label-1", "BRUTO(15220/12)");
		wait4Value("db-amount-label-1", 15220.00/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		
		setValue("db-amount-label-1", "BRUTO(15220.01/12)");
		wait4Value("db-amount-label-1", 15220.01/12);
		assertValue("irpfPercentTexTBox", "6,00 %");		

		setValue("db-amount-label-1", "BRUTO(15980.00/12)");
		wait4Value("db-amount-label-1", 15980.00/12);
		assertValue("irpfPercentTexTBox", "6,00 %");		
		setValue("db-amount-label-1", "BRUTO(15980.01/12)");
		wait4Value("db-amount-label-1", 15980.01/12);
		assertValue("irpfPercentTexTBox", "7,00 %");		

		setValue("db-amount-label-1", "BRUTO(17090.00/12)");
		wait4Value("db-amount-label-1", 17090.01/12);
		assertValue("irpfPercentTexTBox", "7,00 %");		
		setValue("db-amount-label-1", "BRUTO(17090.01/12)");
		wait4Value("db-amount-label-1", 17090.01/12);
		assertValue("irpfPercentTexTBox", "8,00 %");		

		setValue("db-amount-label-1", "BRUTO(18380.00/12)");
		wait4Value("db-amount-label-1", 18380.00/12);
		assertValue("irpfPercentTexTBox", "8,00 %");
		setValue("db-amount-label-1", "BRUTO(18380.01/12)");
		wait4Value("db-amount-label-1", 18380.01/12);
		assertValue("irpfPercentTexTBox", "9,00 %");

		//....
		
		setValue("db-amount-label-1", "BRUTO(46060/12)");
		wait4Value("db-amount-label-1", 46060.00/12);
		assertValue("irpfPercentTexTBox", "19,00 %");		
		setValue("db-amount-label-1", "BRUTO(46060.01/12)");
		wait4Value("db-amount-label-1", 46060.01/12);
		assertValue("irpfPercentTexTBox", "20,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(225470/12)");
		wait4Value("db-amount-label-1", 225470.00/12);
		assertValue("irpfPercentTexTBox", "39,00 %");		
		setValue("db-amount-label-1", "BRUTO(225470.01/12)");
		wait4Value("db-amount-label-1", 225470.01/12);
		assertValue("irpfPercentTexTBox", "40,00 %");		
	}

	@Test
	public void TestIRPFBizkaia() throws Exception {

		open("i.r.p.f_-_bizkaia");

		wait4Id("i.r.p.f_bizkaia_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F BIZKAIA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY, 2017); // January, 2016 it's not visible
		assertValue("irpfPercentTexTBox", "39,00 %");

		calculate(Calendar.JANUARY, 2022);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(63080.01/12)");
		wait4Value("db-amount-label-1", 63080.01/12);
		assertValue("irpfPercentTexTBox", "25,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(213160.01/12)");
		wait4Value("db-amount-label-1", 213160.01/12);
		assertValue("irpfPercentTexTBox", "40,00 %");		

		calculate(Calendar.SEPTEMBER, 2022);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(65050.00/12)");
		wait4Value("db-amount-label-1", 65050.00/12);
		assertValue("irpfPercentTexTBox", "24,00 %");		
		setValue("db-amount-label-1", "BRUTO(65050.01/12)");
		wait4Value("db-amount-label-1", 65050.01/12);
		assertValue("irpfPercentTexTBox", "25,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(220450.00/12)");
		wait4Value("db-amount-label-1", 220450.00/12);
		assertValue("irpfPercentTexTBox", "39,00 %");		
		setValue("db-amount-label-1", "BRUTO(220450.01/12)");
		wait4Value("db-amount-label-1", 220450.01/12);
		assertValue("irpfPercentTexTBox", "40,00 %");		

		calculate(Calendar.JANUARY, 2023);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(66690.00/12)");
		wait4Value("db-amount-label-1", 66690.00/12);
		assertValue("irpfPercentTexTBox", "24,00 %");		
		setValue("db-amount-label-1", "BRUTO(66690.01/12)");
		wait4Value("db-amount-label-1", 66690.01/12);
		assertValue("irpfPercentTexTBox", "25,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(225470.00/12)");
		wait4Value("db-amount-label-1", 225470.00/12);
		assertValue("irpfPercentTexTBox", "39,00 %");		
		setValue("db-amount-label-1", "BRUTO(225470.01/12)");
		wait4Value("db-amount-label-1", 225470.01/12);
		assertValue("irpfPercentTexTBox", "40,00 %");		
	}

	@Test
	public void TestIRPFGipuzkoa() throws Exception {

		open("i.r.p.f_-_gipuzkoa");

		wait4Id("i.r.p.f_gipuzkoa_tiempo_completo_ordinario,_indefinido");

		draft("I.R.P.F GIPUZKOA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		calculate(Calendar.JANUARY);
		assertValue("irpfPercentTexTBox", "0,00 %");

		calculate(Calendar.JANUARY, 2022);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(72700.01/12)");
		wait4Value("db-amount-label-1", 72700.01/12);
		assertValue("irpfPercentTexTBox", "27,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(213160.01/12)");
		wait4Value("db-amount-label-1", 213160.01/12);
		assertValue("irpfPercentTexTBox", "40,00 %");		

	
		calculate(Calendar.SEPTEMBER, 2022);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(74950.00/12)");
		wait4Value("db-amount-label-1", 74950.00/12);
		assertValue("irpfPercentTexTBox", "26,00 %");		
		setValue("db-amount-label-1", "BRUTO(74950.01/12)");
		wait4Value("db-amount-label-1", 74950.01/12);
		assertValue("irpfPercentTexTBox", "27,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(198600.00/12)");
		wait4Value("db-amount-label-1", 198600.00/12);
		assertValue("irpfPercentTexTBox", "38,00 %");		
		setValue("db-amount-label-1", "BRUTO(198600.01/12)");
		wait4Value("db-amount-label-1", 198600.01/12);
		assertValue("irpfPercentTexTBox", "39,00 %");		

	
		calculate(Calendar.JANUARY, 2023);
		setValue("db-amount-label-1", "BRUTO(14000.00/12)");
		wait4Value("db-amount-label-1", 14000.00/12);
		assertValue("irpfPercentTexTBox", "0,00 %");		

		setValue("db-amount-label-1", "BRUTO(14000.01/12)");
		wait4Value("db-amount-label-1", 14000.01/12);
		assertValue("irpfPercentTexTBox", "5,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(76990.00/12)");
		wait4Value("db-amount-label-1", 76990.00/12);
		assertValue("irpfPercentTexTBox", "26,00 %");		
		setValue("db-amount-label-1", "BRUTO(76990.01/12)");
		wait4Value("db-amount-label-1", 76990.01/12);
		assertValue("irpfPercentTexTBox", "27,00 %");		

		//...
		
		setValue("db-amount-label-1", "BRUTO(203240.00/12)");
		wait4Value("db-amount-label-1", 203240.00/12);
		assertValue("irpfPercentTexTBox", "38,00 %");		
		setValue("db-amount-label-1", "BRUTO(203240.01/12)");
		wait4Value("db-amount-label-1", 203240.01/12);
		assertValue("irpfPercentTexTBox", "39,00 %");		
	}


	@Test
	public void TestExtras() throws Exception {

		open("extras");

		wait4Id("extra,_devengo_fuera");

		draft("EXTRA, CALCULADAS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2019, Calendar.JULY, 15);
		Date issueDate = calendar.getTime();
		calendar.set(2019, Calendar.JUNE, 30);
		Date endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 25.42 * 6 );

		calendar.set(2019, Calendar.DECEMBER, 15);
		issueDate = calendar.getTime();
		calendar.set(2019, Calendar.DECEMBER, 31);
		endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 25.42 * 6 );

		calendar = Calendar.getInstance();
		calendar.set(2020, Calendar.JULY, 15);
		issueDate = calendar.getTime();
		calendar.set(2020, Calendar.JUNE, 30);
		endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 29.38 * 6 );
		HtmlTable eventsTable = getElementById("eventsTable");
		Assert.assertEquals(1, eventsTable.getRowCount());

		draft("EXTRA, DEVENGO FUERA");
		
		calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.DECEMBER, 15);
		issueDate = calendar.getTime();
		calendar.set(2016, Calendar.DECEMBER, 31);
		endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 1067.40);
		
		calendar.set(2016, Calendar.JULY, 15);
		issueDate = calendar.getTime();
		calendar.set(2016, Calendar.JUNE, 30);
		endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 1067.40/6 + (1067.40*29/30)/6);

		draft("EXTRA, REDEFINIDAS");
		calculate(Calendar.JANUARY);
		assertText("prorationBaseLabel", ( 1027.65 * 1.04 / 6 ) / 12.00 * 2.00 );
		calculate(Calendar.MAY);
		assertText("prorationBaseLabel", ( 1027.65 * 1.04 / 6 ) / 12.00 * 2.00 );
		
		draft("EXTRAS PRORRATEAR, CONSTANTES");
		double salarioBase = getValue("db-amount-label-1");
		double quotePaga1 = getValue("quote-label-2");
		double quotePaga2 = getValue("quote-label-3");
		Assert.assertEquals(quotePaga1, salarioBase/12.00, 0.005);
		Assert.assertEquals(quotePaga2, salarioBase/12.00, 0.005);

		selectOption("issueDate-listbox-2", "-1");
		wait4Class("payment-row-1", "aon-dataTable-row-highlight");
		wait4Class("payment-row-2", "aon-dataTable-row-highlight");
		assertValue("db-amount-label-1", quotePaga1);
		assertValue("db-amount-label-2", quotePaga2);
		
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
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", prorationBase);
		
		
		wait4Id("extra_cra_001,_no_incuida_en_otros_apartados");
		draft("EXTRA CRA 001, NO INCUIDA EN OTROS APARTADOS");
		calculate(Calendar.SEPTEMBER, 2018);
		
		double paga3 = getValue("db-amount-label-3");
		double paga2 = getValue("db-amount-label-2");
		double paga1 = getValue("db-amount-label-1");

		salarioBase = getValue("db-amount-label-4");
		Assert.assertEquals(paga1, salarioBase/12.00, 0.005);
		Assert.assertEquals(paga2, salarioBase/12.00, 0.005);
		Assert.assertEquals(paga3, salarioBase/12.00, 0.005);

	}


	@Test
	public void TestPAGA_EXTRA_() throws Exception {
		if ( !isDisplayed("extras_redefinidas,_manualmente"))
		open("extras");

		wait4Id("extras_redefinidas,_manualmente");

		draft("EXTRAS REDEFINIDAS, MANUALMENTE");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2021, Calendar.JULY, 15);
		Date issueDate = calendar.getTime();
		calendar.set(2021, Calendar.JUNE, 30);
		Date endDate = calendar.getTime();
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 27.44 * 6 );
		
		click("button-paga_extra_15_7");
		wait4Id("employeeEventsDraftSaveButton");
		
		setValue("paga_extra_15_7_1", "50.00");
		setValue("paga_extra_15_7_2", "50.00");
		setValue("paga_extra_15_7_3", "50.00");
		setValue("paga_extra_15_7_4", "50.00");
		setValue("paga_extra_15_7_5", "50.00");
		setValue("paga_extra_15_7_6", "50.00");
		
		click("employeeEventsDraftSaveButton");
		wait4Disabled("employeeEventsDraftSaveButton", true);
		assertValue("paga_extra_15_7_1", "50");
		assertValue("paga_extra_15_7_2", "50");
		assertValue("paga_extra_15_7_3", "50");
		assertValue("paga_extra_15_7_4", "50");
		assertValue("paga_extra_15_7_5", "50");
		assertValue("paga_extra_15_7_6", "50");
		
		draft("EXTRAS REDEFINIDAS, MANUALMENTE");
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 50.00 * 6 );


		draft("EXTRAS REDEFINIDAS, MANUALMENTE ANUALES");
		
		calendar.set(2021, Calendar.JUNE, 15);
		issueDate = calendar.getTime();
		calendar.set(2021, Calendar.JUNE, 30);
		endDate = calendar.getTime();

		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 79.17 * 12 );
		
		click("button-paga_extra_15_6");
		wait4Id("employeeEventsDraftSaveButton");

		selectOption("employeeEventsDraftYearListBox", "2021");
		setValue("paga_extra_15_6_1", "75.00");
		setValue("paga_extra_15_6_2", "75.00");
		setValue("paga_extra_15_6_3", "75.00");
		setValue("paga_extra_15_6_4", "75.00");
		setValue("paga_extra_15_6_5", "75.00");
		setValue("paga_extra_15_6_6", "75.00");
		click("employeeEventsDraftSaveButton");
		wait4Disabled("employeeEventsDraftSaveButton", true);
		assertValue("paga_extra_15_6_1", "75");
		assertValue("paga_extra_15_6_2", "75");
		assertValue("paga_extra_15_6_3", "75");
		assertValue("paga_extra_15_6_4", "75");
		assertValue("paga_extra_15_6_5", "75");
		assertValue("paga_extra_15_6_6", "75");
		
		selectOption("employeeEventsDraftYearListBox", "2020");
		setValue("paga_extra_15_6_7", "85.00");
		setValue("paga_extra_15_6_8", "85.00");
		setValue("paga_extra_15_6_9", "85.00");
		setValue("paga_extra_15_6_10", "85.00");
		setValue("paga_extra_15_6_11", "85.00");
		setValue("paga_extra_15_6_12", "85.00");
		click("employeeEventsDraftSaveButton");
		wait4Disabled("employeeEventsDraftSaveButton", true);
		assertValue("paga_extra_15_6_7", "85");
		assertValue("paga_extra_15_6_8", "85");
		assertValue("paga_extra_15_6_9", "85");
		assertValue("paga_extra_15_6_10", "85");
		assertValue("paga_extra_15_6_11", "85");
		assertValue("paga_extra_15_6_12", "85");
		
		
		draft("EXTRAS REDEFINIDAS, MANUALMENTE ANUALES");
		extra(issueDate, endDate);
		assertValue("cgcBaseLabel", "0,00");
		assertValue("cgpBaseLabel", "0,00");
		assertValue("totalPaymentLabel", 75.00 * 6 + 85.00 * 6 );
		
		click("button-paga_extra_15_6");
		wait4Id("employeeEventsDraftSaveButton");
		selectOption("employeeEventsDraftYearListBox", "2021");
		setValue("paga_extra_15_6_1", "75.00");
		setValue("paga_extra_15_6_2", "75.00");
		setValue("paga_extra_15_6_3", "75.00");
		setValue("paga_extra_15_6_4", "75.00");
		setValue("paga_extra_15_6_5", "75.00");
		setValue("paga_extra_15_6_6", "75.00");
		selectOption("employeeEventsDraftYearListBox", "2020");
		assertValue("paga_extra_15_6_7", "85");
		assertValue("paga_extra_15_6_8", "85");
		assertValue("paga_extra_15_6_9", "85");
		assertValue("paga_extra_15_6_10", "85");
		assertValue("paga_extra_15_6_11", "85");
		assertValue("paga_extra_15_6_12", "85");

		
		
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
		assertValue("totalPaymentLabel", 0.00, 0.00);
		assertValue("totalLiquidLabel", 00.00, 0.00);
		
		//visual asserts
		HtmlButton fxButton = getElementById("fxButton");
		
		Pattern hidden = Pattern.compile("display\\s*:\\s*none");
		
		assertDisabled("fxButton", true);
		//Assert.assertEquals(true, hidden.matcher(fxButton.getAttribute("style")).find());
		//Assert.assertFalse(fxButton.isDisplayed());
		assertDisabled("undoAllButton", true);
		//HtmlButton undoAllButton = getElementById("undoAllButton");
		//Assert.assertEquals(true, hidden.matcher(undoAllButton.getAttribute("style")).find());
		//Assert.assertFalse(undoAllButton.isDisplayed());
		assertDisabled("undoButton", true);
		//HtmlButton undoButton = getElementById("undoButton");
		//Assert.assertEquals(true, hidden.matcher(undoButton.getAttribute("style")).find());
		//Assert.assertFalse(undoButton.isDisplayed());
		assertDisabled("redoButton", true);
		//HtmlButton redoButton = getElementById("redoButton");
		//Assert.assertEquals(true, hidden.matcher(redoButton.getAttribute("style")).find());
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
		assertValue("totalPaymentLabel", 100.00 * (endMonth + 1));

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
		assertValue("totalPaymentLabel", 0.00);
		assertValue("totalLiquidLabel", 0.00);
		
	}

	@Test
	public void TestATRASO() throws Exception {
		
		if (!isDisplayed("atrasos_redefinidos,_manualmente"))
			open("atrasos");

		wait4Id("atrasos_redefinidos,_manualmente");

		draft("ATRASOS REDEFINIDOS, MANUALMENTE");
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		
		calendar.set(Calendar.YEAR,2020);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.MONTH,Calendar.JANUARY);

		Date startDate = calendar.getTime();

		calendar.set(Calendar.DAY_OF_MONTH,31);
		calendar.set(Calendar.MONTH,Calendar.DECEMBER);
		Date endDate = calendar.getTime();
		
		
		delay(startDate, endDate);
		assertValue("totalPaymentLabel", 0.00, 0.00);
		assertValue("totalLiquidLabel", 00.00, 0.00);
		
		click("button-atraso");
		wait4Id("employeeEventsDraftSaveButton");
		
		setValue("atraso_1", "11.11");
		setValue("atraso_2", "22.22");
		setValue("atraso_3", "33.33");
		
		click("employeeEventsDraftSaveButton");
		wait4Disabled("employeeEventsDraftSaveButton", true);
		assertValue("atraso_1", "11.11");
		assertValue("atraso_2", "22.22");
		assertValue("atraso_3", "33.33");
		
		draft("ATRASOS REDEFINIDOS, MANUALMENTE");
		delay(startDate, endDate);
		
		double totalPayment = 11.11+22.22+33.33;
		assertValue("totalPaymentLabel", totalPayment, 0.00);
		
		click("button-atraso");
		wait4Id("employeeEventsDraftSaveButton");
		assertValue("atraso_1", "11.11");
		assertValue("atraso_2", "22.22");
		assertValue("atraso_3", "33.33");
		
		setValue("atraso_6", "66.66");

		click("employeeEventsDraftSaveButton");
		wait4Disabled("employeeEventsDraftSaveButton", true);
		assertValue("atraso_6", "66.66");

		draft("ATRASOS REDEFINIDOS, MANUALMENTE");
		delay(startDate, endDate);
		
		totalPayment += 66.66;
		assertValue("totalPaymentLabel", totalPayment, 0.00);
	}

	@Test
	public void TestSonny() throws Exception {

		if (!isDisplayed("constantes,_i"))
			open("sonny");

		wait4Id("constantes,_i");

		draft("CONSTANTES, I");
		
		calculate(Calendar.MARCH, 2018);
		Double cgcBase = getValue("cgcBaseLabel");
		Double totalPayment = getValue("totalPaymentLabel");
		Assert.assertEquals(cgcBase, totalPayment);

		calculate(Calendar.APRIL, 2018);
		Double sonnyCgcBase = getValue("cgcBaseLabel");
		Double sonnytotalPayment = getValue("totalPaymentLabel");
		Assert.assertEquals(cgcBase, sonnyCgcBase);
		Assert.assertEquals(totalPayment, sonnytotalPayment);
		HtmlTable eventsTable = getElementById("eventsTable");
		Assert.assertEquals(0, eventsTable.getRowCount());
		
		
		draft("CONSTANTES, II (PAGAS)");
		
		calculate(Calendar.MARCH, 2018);
		cgcBase = getValue("cgcBaseLabel");
		totalPayment = getValue("totalPaymentLabel");

		calculate(Calendar.APRIL, 2018);
		sonnyCgcBase = getValue("cgcBaseLabel");
		sonnytotalPayment = getValue("totalPaymentLabel");
		Assert.assertEquals(cgcBase, sonnyCgcBase);
		//Assert.assertEquals(totalPayment, sonnytotalPayment);
		eventsTable = getElementById("eventsTable");
		Assert.assertEquals(0, eventsTable.getRowCount());
		
		draft("CONSTANTES, III (BONO)");
		
		calculate(Calendar.MARCH, 2018);
		cgcBase = getValue("cgcBaseLabel");
		totalPayment = getValue("totalPaymentLabel");

		calculate(Calendar.APRIL, 2018);
		sonnyCgcBase = getValue("cgcBaseLabel");
		sonnytotalPayment = getValue("totalPaymentLabel");
		Assert.assertEquals(cgcBase, sonnyCgcBase, DELTA);
		//Assert.assertEquals(totalPayment, sonnytotalPayment);
		eventsTable = getElementById("eventsTable");
		Assert.assertEquals(0, eventsTable.getRowCount());
	}

	
	@Test
	public void TestSearch() throws Exception {
		
		if (isDisplayed("constantes,_i"))
			close("sonny");
		
		type("searchTextBox", "CONSTANTES");
		
		wait4Id("sonny");
		wait(10000);
		wait4Id("constantes,_i");
		assertDisplay("constantes,_i", true);
		
	}

	@Test
	public void TestPercepcionesDelSistema() throws Exception {

		if (!isDisplayed("prest,_enfermedad_comun"))
			open("percepciones_del_sistema");
		
		wait4Id("prest,_enfermedad_comun");

		// PREST, ENFERMEDAD COMUN
		draft("PREST, ENFERMEDAD COMUN");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ENFERMEDAD COMÚN
		assertInputDisabled( "description-box-3" , true); 
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-4" , true); // PREST. POR ENFERMEDAD COMÚN 
		assertInputDisabled( "description-box-4" , true); 
		assertHidden("delete-button-4", true);
		assertInputDisabled( "db-amount-label-5" , true); // PREST. POR ENFERMEDAD COMÚN 		
		assertInputDisabled( "description-box-5" , true); 
		assertHidden("delete-button-5", true);
		
		
		setValue("description-box-3", "[1001]P. POR ENFERMEDAD COMÚN");
		
		// HtmlUnit 2.64.0 disabled.
		//wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		//wait4Class("payment-row-4", "aon-dataTable-row-highlight");
		//wait4Class("payment-row-5", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ENFERMEDAD COMÚN
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-4" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DE LA EMPRESA
		assertHidden("delete-button-4", true);
		assertInputDisabled( "db-amount-label-5" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS		
		assertHidden("delete-button-5", true);

		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		
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
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ENFERMEDAD COMÚN
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-4" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DE LA EMPRESA
		assertHidden("delete-button-4", true);
		assertInputDisabled( "db-amount-label-5" , true); // PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS		
		assertHidden("delete-button-5", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		
		
		// PREST, ENFERMEDAD PROFESIONAL
		draft("PREST, ENFERMEDAD PROFESIONAL");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL
		assertHidden("delete-button-3", true);
		
		setValue("description-box-3", "P. POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL");
		
		// HtmlUnit 2.64.0 disabled.
		//wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // P. POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-1", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // PRESTACIÓN POR ENFERMEDAD COMÚN
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);

		// PREST, MATERNIDAD
		draft("PREST, MATERNIDAD");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO
		assertHidden("delete-button-1", false);
		
		setValue("description-box-3", "P.POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO");
		
		// HtmlUnit 2.64.0 disabled.
		//wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // P. POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // P. POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		
		// PREST, PATERNIDAD
		draft("PREST, PATERNIDAD");
		
		calculate(Calendar.JUNE, 2018);
		
		assertInputDisabled( "db-amount-label-3" , true); // PREST. POR PATERNIDAD
		assertInputDisabled( "db-amount-label-3" , true); // PRESTACIÓN POR PATERNIDAD
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		
		setValue("description-box-3", "P. POR PATERNIDAD");
		
		// HtmlUnit 2.64.0 disabled.
		//wait4Class("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // P. POR PATERNIDAD
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);
		
		click("acceptButton");
		wait4Disabled("acceptButton", true);
		wait4NoClass("payment-row-3", "aon-dataTable-row-highlight");
		assertInputDisabled( "db-amount-label-3" , true); // P. POR PATERNIDAD
		assertHidden("delete-button-3", true);
		assertInputDisabled( "db-amount-label-2" , false); // PLUS SALARIAL MENSUAL
		assertHidden("delete-button-2", false);
		assertInputDisabled( "db-amount-label-1" , true); // SALARIO BASE MENSUAL
		assertHidden("delete-button-1", false);

		//VACACIONES, NO DISFRUTADAS
		draft("VACACIONES, NO DISFRUTADAS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2018, Calendar.JUNE, 15, 0, 0, 0);
		settle(calendar.getTime());

		wait4Disabled("acceptButton", true);
		assertInputDisabled( "description-box-1" , true); // VACACIONES RETRIBUIDAS NO DISFRUTADAS
		assertInputDisabled( "db-amount-label-1" , true); // VACACIONES RETRIBUIDAS NO DISFRUTADAS
		assertNotElement("description-box-2");

		
		
	}	
	
	@Test
	public void TestCalculador() throws Exception {

		if (!isDisplayed("conceptos,_sin_nombre"))
			open("calculador");


		wait4Id("conceptos,_sin_nombre");

		draft("CONCEPTOS, SIN NOMBRE");
		calculate(Calendar.SEPTEMBER, 2018);
		Double totalPayment = getValue("totalPaymentLabel");
		double salarioMensual = 666 * 2 ;
		double plus = salarioMensual * 0.25;
		double paga = ( salarioMensual + plus ) / 12;
		double antiguedad = ( salarioMensual + plus ) * 0.05;
		Assert.assertEquals((Double) ( salarioMensual + plus + 2 * paga + antiguedad )  , totalPayment, 0.001);
		
		
		draft("CONCEPTOS, APELLIDO");
		calculate(Calendar.SEPTEMBER, 2018);
		totalPayment = getValue("totalPaymentLabel");
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
	
	@Test
	public void TestHorasTrabajadas() throws Exception {

		if (!isDisplayed("tiempo_parcial,_horas"))
			open("horas_trabajadas");


		wait4Id("tiempo_parcial,_horas");

		draft("TIEMPO COMPLETO, ORDINARIO");
		assertDisplay("employeeWorkedDaysLabel", true);
		assertDisplay("employeeWorkedHoursLabel", false);
		assertDisplay("employeePartialFactorLabel", false);
		
		draft("TIEMPO PARCIAL, HORAS");
		assertDisplay("employeeWorkedDaysLabel", false);
		assertDisplay("employeeWorkedHoursLabel", true);
		assertDisplay("employeePartialFactorLabel", false);

		draft("TIEMPO PARCIAL, ORDINARIO");
		assertDisplay("employeeWorkedDaysLabel", false);
		assertDisplay("employeeWorkedHoursLabel", false);
		assertDisplay("employeePartialFactorLabel", false);
		assertDisplay("editor-coeficiente_parcialidad", true);
		
		
		
	}

	@Test
	public void TestHorasComplementarias() throws Exception {

		if (!isDisplayed("base_minima_horas,_pactadas"))
			open("horas_complementarias");


		wait4Id("base_minima_horas,_pactadas");

		draft("BASE MÍNIMA HORAS, PACTADAS");
		//assertDisplay("employeeWorkedDaysLabel", true);
		assertValue("quote-label-2", 7.03 * 10.00);
		assertValue("quote-label-3", 1166.70 * 0.5 - 50.00);
		assertValue("cgcBaseLabel", 1166.70 * 0.5 + 7.03 * 10.00);
		
		
		
		
	}

	@Test
	public void TestIrpfEstatal() throws Exception {
		

		if (!isDisplayed("irpf,_atrasos"))
			open("i.r.p.f_-_estatal");

		wait4Id("irpf,_atrasos");

		draft("IRPF, ATRASOS");
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		
		calendar.set(Calendar.YEAR,2022);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.MONTH,Calendar.JANUARY);

		Date startDate = calendar.getTime();

		calendar.set(Calendar.DAY_OF_MONTH,28);
		calendar.set(Calendar.MONTH,Calendar.FEBRUARY);
		Date endDate = calendar.getTime();
				
		delay(startDate, endDate);
		assertValue("totalPaymentLabel", 0.00, 0.00);
		assertValue("totalLiquidLabel", 00.00, 0.00);
		
		setValue("db-amount-label-5", "11.11");
		wait4Value("totalPaymentLabel", 11.11);
		setValue("db-amount-label-4", "22.22");
		wait4Value("totalPaymentLabel", 33.33);
		
		setValue("irpfPercentTexTBox", "15.00");
		wait4Text("irpf", "5,00" );
		
		click("delayButton");
		
		wait4Id("dbSalaryCheck");
		assertDisplay("dbSalaryCheck", true);
		assertText("irpf", "5,00" );
		assertValue("irpfPercentTexTBox", "15,00 %" );
		
		
		
	}


	@Test
	public void TestEmbargos() throws Exception {

		if (!isDisplayed("max,_embargable"))
			open("embargos");


		wait4Id("max,_embargable");

		draft("MAX, EMBARGABLE"); 
		calculate(Calendar.SEPTEMBER,2022);
		double totalLiquid = getValue("totalLiquidLabel");
		double totalPayment = getValue("totalPaymentLabel");
		double totalDeduction = getText("totalDeductionLabel");
		
		double totalEmbargable = totalPayment - totalDeduction;
		double totalEmbargado = totalEmbargable - totalLiquid;
		
		double smi = 1000.00 * 14 / 12;
		Assert.assertEquals((totalEmbargable - smi) * 0.30 , totalEmbargado, 0.005);
		
		
		selectOption("issueDate-listbox-2", "6");  //Ene
		selectOption("issueDate-listbox-3", "11"); //Dic
		calculate(Calendar.NOVEMBER,2022);
		
		smi = 1000.00 ;
		totalLiquid = getValue("totalLiquidLabel");
		totalPayment = getValue("totalPaymentLabel");
		totalDeduction = getText("totalDeductionLabel");
		totalEmbargable = totalPayment - totalDeduction;
		totalEmbargado = totalEmbargable - totalLiquid;
		Assert.assertEquals((totalEmbargable - smi) * 0.30 , totalEmbargado, 0.005);
		
		draft("DOS, EMBARGOS");
		calculate(Calendar.FEBRUARY,2023);
		totalLiquid = getValue("totalLiquidLabel");
		totalPayment = getValue("totalPaymentLabel");
		totalDeduction = getText("totalDeductionLabel");
		totalEmbargable = totalPayment - totalDeduction;
		totalEmbargado = totalEmbargable - totalLiquid;
		Assert.assertEquals((totalEmbargable - smi) * 0.30 , totalEmbargado, 0.005);
		calculate(Calendar.MARCH,2023);
		totalLiquid = getValue("totalLiquidLabel");
		totalPayment = getValue("totalPaymentLabel");
		totalDeduction = getText("totalDeductionLabel");
		totalEmbargable = totalPayment - totalDeduction;
		totalEmbargado = totalEmbargable - totalLiquid;
		Assert.assertEquals((totalEmbargable - smi) * 0.30 , totalEmbargado, 0.05);
		
		
	}

	@Test
	public void TestQuote() throws Exception {
		
		if (!isDisplayed("temporal_tiempo_parcial,_ordinario"))
			open("cotizacion");


		wait4Id("temporal_tiempo_parcial,_ordinario");

		draft("TEMPORAL TIEMPO PARCIAL, ORDINARIO");
		calculate(Calendar.JANUARY,2023);
		double cgpBase = getValue("cgpBaseLabel");
		assertText("unemployment", cgpBase*1.60/100.00);
		click("costsCheck-input");
		assertText("unemployment_cost", cgpBase*6.70/100.00);
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

	private void assertHidden(String id, boolean hidden ) {
		DomElement el = getElementById(id);
		Pattern display = Pattern.compile("display\\s*:\\s*none");
		Assert.assertEquals(hidden, display.matcher(el.getAttribute("style")).find());
		
	}

}
