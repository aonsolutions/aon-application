package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;
import solutions.aon.selenium.tools.SeleniumTools.SalaryType;


/*
 -Dintegration.test.user=admin
 -Dintegration.test.password=org
 -Dintegration.test.payroll.url=http://payroll-test.aonsolutions.org:8080/aon-aio/
 -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/aon-aio/
 -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/aon-aio/
 -Dintegration.test.home.payroll.url=http://home-payroll-test.aonsolutions.org:8080/aon-aio/
 * */
public class GeneralIntegralTest extends AioBaseTestCase {
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		Logger.start(testName.getMethodName().toUpperCase());
	}
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        driver = newChromeDriver();
        
        wait = new WebDriverWait(driver, 10);
        login(driver, GENERAL);
        
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//logout(driver);
		driver.quit();
	}

	@Before
	public void setUp() throws Exception {
		driver.navigate().to("http://payroll-test.aonsolutions.org:8080/aon-aio/");
	}
	
	@Test
	public void testAntiguedad() throws InterruptedException, ParseException {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
//		OPEN ANTIGÜEDAD WORKPLACE
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "antiguedad");
		
		SeleniumTools.draft(driver, "1989 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		assertTrue(SeleniumTools.waitAndCheckAmount(driver,
				By.id(GWT_ID_PROFIX + "totalPaymentsLabel"),
				"value"
				, SeleniumTools.unmessDouble(
						15454.46 / 14 					// SALARIO_BASE
						+ 15454.46 / 14 * 5 / 100 		// ANTIGUEDAD 1989-1992 ( 1 TRIENIO 5%)
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1992-1995 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 6 * 4 / 100 	// ANTIGUEDAD 1995-2016 ( 6 CUATRIENIOS 4% )
						)
				)
		);
		
		SeleniumTools.draft(driver, "1991 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		assertTrue(SeleniumTools.waitAndCheckAmount(driver,
				By.id(GWT_ID_PROFIX + "totalPaymentsLabel"),
				"value",
				SeleniumTools.unmessDouble(
						15454.46 / 14 					// SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1991-1994 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 6 * 4 / 100 	// ANTIGUEDAD 1994-2016 ( 6 CUATRIENIOS 4% )
						)
				)
		);
		SeleniumTools.draft(driver, "1993 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		assertTrue(SeleniumTools.waitAndCheckAmount(driver,
				By.id(GWT_ID_PROFIX + "totalPaymentsLabel"),
				"value",
				SeleniumTools.unmessDouble(
						15454.46 / 14 					// SALARIO_BASE
						+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1993-1996 ( 1 TRIENIO 4%)
						+ 15454.46 / 14 * 6 * 4 / 100 	// ANTIGUEDAD 1996-2016 ( 6 CUATRIENIOS 4% )
						)
				)
		);
		
		SeleniumTools.draft(driver, "2012 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		assertTrue(SeleniumTools.waitAndCheckAmount(driver,
				By.id(GWT_ID_PROFIX + "totalPaymentsLabel"),
				"value",
				SeleniumTools.unmessDouble(
						15454.46 / 14 					// SALARIO_BASE
						+ 15454.46 / 14 * 2 * 4 / 100	// ANTIGUEDAD 2012-2016 ( 2 CUATRIENIOS 4% )
						)
				)
		);

		SeleniumTools.draft(driver, "CONCEPTO ANTIGUEDAD, DESCRIPCION");
		By byid = By.id(GWT_ID_PROFIX + "employeeNameLabel");
		assertTrue(wait.until(ExpectedConditions.attributeContains(byid, "innerText", "CONCEPTO ANTIGUEDAD, DESCRIPCION")));
	}
	
	@Ignore
	@Test
	public void testAtrasos() throws Exception {
		
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
//		OPEN ATRASOS WORKPLACE
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "atrasos");
		
		wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "atrasos_tiempo_completo_ordinario,_indefinido")));
		
		SeleniumTools.draft(driver, "ATRASOS TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		for ( int month = 0; month < 12; month++ ) {
			calendar.set(Calendar.MONTH, month);
			switchSalaryMonth(driver, calendar);
			retryingFindClick(driver, By.id(GWT_ID_PROFIX + "salaryButton"));
			wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "dbSalaryCheck")));
		}
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		SeleniumTools.delay(driver, calendar.getTime(), new Date());
		
		Double totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)0.00, totalPayments);
		Double netLiquid = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals((Double)0.00, netLiquid);
		
		
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "fxButton"))))
			Assert.fail("fxButton not hidden");
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "undoAllButton"))))
			Assert.fail("'undo all' button not hidden");
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "undoButton"))))
			Assert.fail("'undo' button not hidden");
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "redoButton"))))
			Assert.fail("'redo' button not hidden");
		
		SeleniumTools.selectPayrollType(driver, SalaryType.SALARY);
		SeleniumTools.selectMonthScrolling(driver, calendar.getTime());
		
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX +  "description-box-new-payment", "[3]ATRASOS");
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "description-box-new-payment")));
		input.sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "amount-box-new-payment", "100");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "amount-box-new-payment")));
		input.sendKeys(Keys.TAB);
		boolean checkInput = SeleniumTools.changingElementAssert(driver, By.id(GWT_ID_PROFIX + "description-box-3"), "value", "[3]ATRASOS");
		assertTrue("[3]ATRASOS not found", checkInput);
		
		SeleniumTools.acceptDraft(driver);
		
		By deleteButton = By.id(GWT_ID_PROFIX + "delete-button-3");
		SeleniumTools.click(driver, wait, deleteButton);
		SeleniumTools.acceptDraft(driver);
		
		SeleniumTools.selectPayrollType(driver, SalaryType.SALARY);
		switchSalaryMonth(driver, calendar);
		
		SeleniumTools.safeInput(driver, "#"+ GWT_ID_PROFIX + "description-box-new-payment", "[3]ATRASOS");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "description-box-new-payment")));
		input.sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "amount-box-new-payment", "100");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "amount-box-new-payment")));
		input.sendKeys(Keys.TAB);
		checkInput = SeleniumTools.changingElementAssert(driver, By.id(GWT_ID_PROFIX + "description-box-3"), "value", "[3]ATRASOS");
		assertTrue("[3]ATRASOS not found", checkInput);
		
		SeleniumTools.delay(driver, calendar.getTime(), new Date());
		Double ccBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals((Double)0.00d, ccBase);
		Double payments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)0.00d, payments);
		Double liquid = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals((Double)0.00d, liquid);
	}
	
	@Test
	public void TestBasesMaximasYMinimas() throws Exception {
		
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
//		OPEN 'BASES MÁXIMAS Y MÍNIMAS' WORKPLACE
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "bases_maximas_y_minimas");
		
		SeleniumTools.draft(driver, "BASE, MÁXIMA ( GRUPO 01 )");
		checkCgcAndCgpByDate(2016, Calendar.DECEMBER, 3642.00, 3642.00);
		checkCgcAndCgpByDate(2017, Calendar.JANUARY, 3751.20, 3751.20);
		checkCgcAndCgpByDate(2018, Calendar.JANUARY, 3751.20, 3751.20);
		checkCgcAndCgpByDate(2018, Calendar.JULY, 3751.20, 3751.20);
		checkCgcAndCgpByDate(2018, Calendar.AUGUST, 3803.70, 3803.70);
		checkCgcAndCgpByDate(2019, Calendar.AUGUST, 4070.10, 4070.10);
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA ( GRUPO 01 )");
		checkCgcAndCgpByDate(2016, Calendar.DECEMBER, 1067.40, 764.40);
		checkCgcAndCgpByDate(2017, Calendar.JANUARY, 1152.90, 825.60);
		checkCgcAndCgpByDate(2018, Calendar.JANUARY, 1199.10, 858.60);
		checkCgcAndCgpByDate(2019, Calendar.JANUARY, 1466.40, 1050.00);
		
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA ( GRUPO 02 )");
		checkCgcAndCgpByDate(2018, Calendar.JANUARY, 994.20, 858.60);
		checkCgcAndCgpByDate(2019, Calendar.JANUARY, 1215.90, 1050.00);

		SeleniumTools.draft(driver, "BASE, MÍNIMA ( GRUPO 03 )");
		checkCgcAndCgpByDate(2018, Calendar.JANUARY, 864.90, 858.60);
		checkCgcAndCgpByDate(2019, Calendar.JANUARY, 1057.80, 1050.00);

		SeleniumTools.draft(driver, "BASE, MÍNIMA ( GRUPO 04 )");
		checkCgcAndCgpByDate(2018, Calendar.JANUARY, 858.60, 858.60);
		checkCgcAndCgpByDate(2019, Calendar.JANUARY, 1050.00, 1050.00);
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA ( GRUPO 09 )");
		checkCgcAndCgpByDate(2019, Calendar.AUGUST, 1050.00, 1050.00);
		checkCgcAndCgpByDate(2019, Calendar.SEPTEMBER, 1050.00, 1050.00);
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA ( GRUPO 10 )");
		checkCgcAndCgpByDate(2019, Calendar.AUGUST, 35.00 * 31, 1050.00);
		checkCgcAndCgpByDate(2019, Calendar.SEPTEMBER, 1050.00, 1050.00);
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA IT ( GRUPO 01 )");
		checkCgcByDate(2016, Calendar.JUNE, 1067.40);
		checkCgcByDate(2016, Calendar.JULY, 1067.40);
		checkCgcByDate(2017, Calendar.JANUARY, 1152.90);
		checkCgcByDate(2018, Calendar.JANUARY, 1199.10);
		checkCgcByDate(2019, Calendar.JANUARY, 1466.40);
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA PARCIAL ( HORAS )");
		checkCgcByDate(2019, Calendar.AUGUST, 6.33 * 44.00);
		checkCgcAndCgpByDate(2019, Calendar.SEPTEMBER, 6.33 * 42.00, 6.33 * 42.00);
		
		SeleniumTools.draft(driver, "BASE, MÍNIMA PARCIAL ( MENSUAL )");
		checkCgcByDate(2019, Calendar.AUGUST, 1050.00 * 0.25);
		checkCgcByDate(2019, Calendar.SEPTEMBER, 1050.00 * 0.25);
		
	}
	
	@Test
	public void TestBasesSMIYIPREM() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);		
//		OPEN 'BASES MÁXIMAS Y MÍNIMAS' WORKPLACE
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "smi_&_iprem");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		SeleniumTools.draft(driver, "SALARIO, MÍNIMO");
		switchSalaryMonth(driver, calendar);
		assertTrue(SeleniumTools.changingElementAssert(driver, By.id(GWT_ID_PROFIX + "totalPaymentLabel"), "innerText", "900,00"));
		
		SeleniumTools.draft(driver, "INDICADOR, PÚBLICO DE RENTA DE EFECTOS MÚLTIPLES");
		switchSalaryMonth(driver, calendar);
		assertTrue(SeleniumTools.changingElementAssert(driver, By.id(GWT_ID_PROFIX + "totalPaymentLabel"), "innerText", "537,84"));
		
	}
	
	@Test
	public void TestBrutoYNeto() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);		
//		OPEN 'BASES MÁXIMAS Y MÍNIMAS' WORKPLACE
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "bruto_y_neto");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2016);
		
		SeleniumTools.draft(driver, "BRUTO, ENFERMEDAD COMÚN (BASES)");
		switchSalaryMonth(driver, calendar);
		Double totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals(SeleniumTools.unmessDouble(1067.40 / 30 * 5 * 0.60 + 1000.00 * 22 / 30), totalPayments);
		Double cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals((Double)1067.40, cgcBase);
		
		calendar.set(Calendar.MONTH, Calendar.MAY);
		
		SeleniumTools.draft(driver, "BRUTO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		switchSalaryMonth(driver, calendar);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)1500.0, totalPayments);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)1500.0, totalPayments);
		
		calendar.set(Calendar.MONTH, Calendar.MAY);
		
		SeleniumTools.draft(driver, "NETO TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		switchSalaryMonth(driver, calendar);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals((Double)2125.0, totalPayments);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals((Double)2125.0, totalPayments);
		
	}
	
	@Test
	public void TestCalculador() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.search(driver, "CONCEPTOS, SIN NOMBRE");
		SeleniumTools.draft(driver, "CONCEPTOS, SIN NOMBRE");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2018);
		
		switchSalaryMonth(driver, calendar);
		
		Double totalPayment = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		
		double salarioMensual = 666 * 2 ;
		double plus = salarioMensual * 0.25;
		double paga = ( salarioMensual + plus ) / 12;
		double antiguedad = ( salarioMensual + plus ) * 0.05;
		Assert.assertEquals(SeleniumTools.unmessDouble( salarioMensual + plus + 2 * paga + antiguedad )  , totalPayment, 0.001);
		
		SeleniumTools.search(driver, "CONCEPTOS, APELLIDO");
		SeleniumTools.draft(driver, "CONCEPTOS, APELLIDO");
		switchSalaryMonth(driver, calendar);
		totalPayment = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		salarioMensual = 999 ;
		plus = salarioMensual * 0.10;
		paga = ( salarioMensual + plus ) / 12;
		Assert.assertEquals(SeleniumTools.unmessDouble( salarioMensual + plus + 2 * paga )  , totalPayment, 0.001);
	}
	
	@Test
	public void TestDraftFromScratch() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.search(driver, "DRAFT COMPLETO, VACIO");
		SeleniumTools.draft(driver, "DRAFT COMPLETO, VACIO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		
		switchSalaryMonth(driver, calendar);
				
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "description-box-new-payment", "[1] S4L4R10 B4S3");
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "description-box-new-payment")));
		input.sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "amount-box-new-payment", "1666.00 * DIAS_TRABAJADOS / DIAS_MES");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "amount-box-new-payment")));
		input.sendKeys(Keys.TAB);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "description-box-1")));
		Double cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals((Double) 1666d, cgcBase);
		
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "description-box-new-payment", "[2] PLU3");
		input = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "description-box-new-payment")));
		input.sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "amount-box-new-payment", "100.00 * DIAS_TRABAJADOS / DIAS_MES");
		input = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "amount-box-new-payment")));
		input.sendKeys(Keys.TAB);
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "description-box-2")));
		cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals(SeleniumTools.unmessDouble(1666.00 + 100.00), cgcBase);
		Double totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals(SeleniumTools.unmessDouble(1666.00 + 100.00), totalPayments);
	}
	
	@Test
	public void TestDraftUserExpression() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.search(driver, "USER EXPRESSION, /*user*/.../**/");
		SeleniumTools.draft(driver, "USER EXPRESSION, /*user*/.../**/");
		
		SeleniumTools.selectMonthScrolling(driver, new Date());
		SeleniumTools.checkSalaryPeriod(driver, new Date());
		
		By amountLbl1 = By.id(GWT_ID_PROFIX + "db-amount-label-1");
		wait.until(ExpectedConditions.elementToBeClickable(amountLbl1));
		
//		SeleniumTools.retryingFindClick(driver, By.id(GWT_ID_PROFIX + "db-amount-label-1"));
		SeleniumTools.focusUntilValueContains(driver, amountLbl1, "PLUS_SALARIAL");
//		assertTrue(SeleniumTools.changingElementAssert(driver, By.id(GWT_ID_PROFIX + "db-amount-label-1"), "value", "PLUS_SALARIAL"));
		retryingFindClick(driver, By.id(GWT_ID_PROFIX + "fxButton"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "fxExpressionCodeArea")));
		assertEquals("DIAS_TRABAJADOS / DIAS_MES * /*user*/PLUS_SALARIAL/**/", SeleniumTools.getAttribute(driver, By.id(GWT_ID_PROFIX + "fxExpressionCodeArea"), "value"));
		By cancelBtn = By.id(GWT_ID_PROFIX + "fxCancelButton");
		SeleniumTools.safelyCloseModal(driver, cancelBtn);
		By amountLabel3 = By.id(GWT_ID_PROFIX + "db-amount-label-3");
		wait.until(ExpectedConditions.elementToBeClickable(amountLabel3));
//		SeleniumTools.retryingFindClick(driver, By.id(GWT_ID_PROFIX + "db-amount-label-3"));
//		SeleniumTools.focus(driver, "#" + GWT_ID_PROFIX + "db-amount-label-3");
//		wait.until(ExpectedConditions.attributeContains(amountLabel3, "value", "PLUS_DISPONIBILIDAD"));
		
		SeleniumTools.focusUntilValueContains(driver, amountLabel3, "PLUS_DISPONIBILIDAD");
		
//		assertTrue(SeleniumTools.changingElementAssert(driver, amountLabel3, "value", "PLUS_DISPONIBILIDAD"));
		retryingFindClick(driver, By.id(GWT_ID_PROFIX + "fxButton"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "fxExpressionCodeArea")));
		assertEquals("DIAS_TRABAJADOS * /*user*/ PLUS_DISPONIBILIDAD/**/ / DIAS_MES", SeleniumTools.getAttribute(driver, By.id(GWT_ID_PROFIX + "fxExpressionCodeArea"), "value"));
		SeleniumTools.retryingFindClick(driver, By.id(GWT_ID_PROFIX + "fxCancelButton"));
		
	}
	
//	@Test
//	public void repeatTestFiniquito() throws Exception {
//		
//		for (int i = 0; i < 30; i++) {
//			setUp();
//			TestFiniquito();
//		}
//	}
	
	@Test
	public void TestFiniquito() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, "gwt-debug-finiquitos");
		SeleniumTools.draft(driver, "COTIZACIÓN, CERO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(2016, Calendar.JUNE, 25);
		
		SeleniumTools.selectPayrollType(driver, SalaryType.SETTLE);
		SeleniumTools.selectMonthScrollingSettle(driver, calendar.getTime());
		SeleniumTools.checkSettlePeriod(driver, calendar.getTime());
		
		Double cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals((Double)0.00, cgcBase);
		Double cgpBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgpBaseLabel"));
		assertEquals((Double)0.00, cgpBase);
		Double totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)0.00, totalPayments);
		Double totalLiquid = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals((Double)0.00, totalLiquid);
		
		SeleniumTools.draft(driver, "COTIZACIÓN, MÁX");
		
		SeleniumTools.selectPayrollType(driver, SalaryType.SETTLE);
		SeleniumTools.selectMonthScrollingSettle(driver, calendar.getTime());
		SeleniumTools.checkSettlePeriod(driver, calendar.getTime());
		cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals((Double)666000.00, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgpBaseLabel"));
		assertEquals((Double)666000.00, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)666000.00, totalPayments);
		totalLiquid = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals(SeleniumTools.unmessDouble(666000.00 - (666000.00 * (4.70 + 1.55 + 0.10) / 100.00)), totalLiquid);
		
		SeleniumTools.draft(driver, "FINIQUITO, REDEFINIDO");
		
		SeleniumTools.selectPayrollType(driver, SalaryType.SETTLE);
		SeleniumTools.wait4SettleToLoadDate(driver);
		SeleniumTools.selectMonthScrollingSettle(driver, calendar.getTime());
		String regex = String.format("\\s*\\d+\\/%1$d\\/%2$d\\s*-\\s*\\d+\\/%1$d\\/%2$d\\s*", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
		wait.until(ExpectedConditions.textMatches(By.id(GWT_ID_PROFIX + "periodLabel"), Pattern.compile(regex)));
		cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals((Double)200.00, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgpBaseLabel"));
		assertEquals((Double)200.00, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals((Double)300.00, totalPayments);
		totalLiquid = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalLiquidLabel"));
		assertEquals(SeleniumTools.unmessDouble(300.00 - (200.00 * (4.70 + 1.55 + 0.10) / 100.00)), totalLiquid);
		
	}
	
	@Test
	public void TestInterinidad() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.search(driver, "INTERINIDAD, TIEMPO COMPLETO");
		SeleniumTools.draft(driver, "INTERINIDAD, TIEMPO COMPLETO");
		
		Double unemployPercent = SeleniumTools.getAmountNotEmptyValue(driver, By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL"));
		assertEquals((Double)1.55, unemployPercent);
		
		By costsCheck = By.id(GWT_ID_PROFIX + "costsCheck-input");
		
		SeleniumTools.click(driver, wait, costsCheck);
		
		Double unemployPercentE = SeleniumTools.getAmountNotEmptyValue(driver, By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL_E"));
		assertEquals((Double)5.5, unemployPercentE);
		
		SeleniumTools.click(driver, wait, costsCheck);
		
		SeleniumTools.search(driver, "INTERINIDAD, TIEMPO PARCIAL");
		SeleniumTools.draft(driver, "INTERINIDAD, TIEMPO PARCIAL");
		
		unemployPercent = SeleniumTools.getAmountNotEmptyValue(driver, By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL"));
		assertEquals((Double)1.55, unemployPercent);
		
		SeleniumTools.click(driver, wait, costsCheck);
		
		unemployPercentE = SeleniumTools.getAmountNotEmptyValue(driver, By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL_E"));
		assertEquals((Double)5.5, unemployPercentE);

		SeleniumTools.click(driver, wait, costsCheck);
	}
	
	@Test
	public void TestIRPFAlava() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "i.r.p.f_-_alava/araba");
		SeleniumTools.draft(driver, "I.R.P.F ARABA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		SeleniumTools.selectMonthScrolling(driver, calendar.getTime());
		By irpfPercentBox = By.id(GWT_ID_PROFIX + "irpfPercentTexTBox");
		try {			
			wait.until(ExpectedConditions.attributeToBe(irpfPercentBox, "value", "2,00 %"));
		} catch (Exception e) {
			fail("Irpf percent is not 2,00 %");
		}
		
	}
	
	@Test
	public void TestIRPFVizcaya() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "i.r.p.f_-_bizkaia");
		SeleniumTools.draft(driver, "I.R.P.F BIZKAIA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		SeleniumTools.selectMonthScrolling(driver, calendar.getTime());
		By irpfPercentBox = By.id(GWT_ID_PROFIX + "irpfPercentTexTBox");
		try {			
			wait.until(ExpectedConditions.attributeToBe(irpfPercentBox, "value", "39,00 %"));
		} catch (Exception e) {
			fail("Irpf percent is not 39,00 %");
		}
		
	}
	
	@Test
	public void TestIRPFGuipuzcoa() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "i.r.p.f_-_gipuzkoa");
		SeleniumTools.draft(driver, "I.R.P.F GIPUZKOA TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		SeleniumTools.selectMonthScrolling(driver, calendar.getTime());
		By irpfPercentBox = By.id(GWT_ID_PROFIX + "irpfPercentTexTBox");
		try {			
			wait.until(ExpectedConditions.attributeToBe(irpfPercentBox, "value", "0,00 %"));
		} catch (Exception e) {
			fail("Irpf percent is not 0,00 %");
		}
	}
	
	@Test
	public void TestIT() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "i.t");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		
		SeleniumTools.draft(driver, "LACTANCIA, PERIODO");
		switchSalaryMonth(driver, calendar);
		
		By cgcBaseLabel = By.id(GWT_ID_PROFIX + "cgcBaseLabel");
		Double cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		By totalPaymentsLabel = By.id(GWT_ID_PROFIX + "totalPaymentsLabel");
		Double totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)0.0, totalPayments);
		
		SeleniumTools.draft(driver, "RIESGO, DURANTE EL EMBARAZO");
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		
		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		SeleniumTools.draft(driver, "BASE MÍNIMA DIARIA, I.T");
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(25.48 * 31), cgcBase);	//GRUPO 09
		By cgpBaseLabel = By.id(GWT_ID_PROFIX + "cgpBaseLabel");
		Double cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(25.48 * 31), cgpBase);	//GRUPO 09
		
		SeleniumTools.draft(driver, "BASE MÍNIMA MENSUAL, I.T");
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double) 764.4, cgcBase);	//GRUPO 05
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double) 764.4, cgpBase);	//GRUPO 05
		
		SeleniumTools.draft(driver, "ENFERMEDAD, COMÚN");
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		switchSalaryMonth(driver, calendar);
		
		SeleniumTools.draft(driver, "ENFERMEDAD, PROFESIONAL");
		
		SeleniumTools.draft(driver, "GARANTIZADO, 100%");
		calendar.set(Calendar.MONTH, Calendar.JULY);
		
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals(SeleniumTools.unmessDouble(1067.40 * 13 / 30 + (1067.40 + 1067.40 / 6) * 17 / 30), totalPayments);// 17
		
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "totalPaymentsLabel"));
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), totalPayments);
		
		SeleniumTools.draft(driver, "GARANTIZADO, ENFERMEDAD COMÚN");
		calendar.set(Calendar.MAY, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1067.40, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1067.40, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1067.40, totalPayments);
		
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1067.40, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1067.40, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1067.40, totalPayments);
		
		calendar.set(Calendar.MONTH, Calendar.JULY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1067.40, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1067.40, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1067.40, totalPayments);
		
		SeleniumTools.draft(driver, "GARANTIZADO, ENFERMEDAD PROFESIONAL");
		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1067.40, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1067.40, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1067.40, totalPayments);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1067.40, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1067.40, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1067.40, totalPayments);
		
		SeleniumTools.draft(driver, "GARANTIZADO, EXTRAS CON GARANTIZADO");
		calendar.set(Calendar.MONTH, Calendar.JULY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1200.00 + 1200.00 / 4), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1200.00 + 1200.00 / 4), cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1200.0, totalPayments);
		
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1200.00 + 1200.00 / 4), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1200.00 + 1200.00 / 4), cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1200.0, totalPayments);
		
		SeleniumTools.draft(driver, "GARANTIZADO ENFERMEDAD COMÚN, Y PROFESIONAL");
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)764.4, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)764.4, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)764.4, totalPayments);
		
		SeleniumTools.draft(driver, "GARANTIZADOS, ENFERMEDAD COMÚN");
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)764.4, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)764.4, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)764.4, totalPayments);

		calendar.set(Calendar.MONTH, Calendar.JULY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)764.4, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)764.4, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)764.4, totalPayments);
		
		SeleniumTools.draft(driver, "GARANTIZADOS, ENFERMEDAD PROFESIONAL");
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1000.0, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1000.0, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals(SeleniumTools.unmessDouble(1000.00 * 20 / 30 + 900.00 * 10 / 30), totalPayments);
		
		calendar.set(Calendar.MONTH, Calendar.JULY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1000.0, cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals((Double)1000.0, cgpBase);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals((Double)1000.0, totalPayments);
		
		SeleniumTools.draft(driver, "EXTRAS, IT");
		switchSalaryMonth(driver, calendar);
		
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgpBase);

		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgpBase);
		
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgpBase);
		
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40 + 1067.40 / 6), cgpBase);
		
		calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.DECEMBER, 31);
		Date endDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, 15);
		Date issueDate = calendar.getTime();
		
		extra(issueDate, endDate);
		do {
			totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		} while (totalPayments == 0d);
		assertEquals(SeleniumTools.unmessDouble(1067.40 * 4 / 30 /6  +  1067.40 * 14 / 30 /6), totalPayments);
		
		SeleniumTools.draft(driver, "EXTRAS, IT (REDEFINIDO)");
		SeleniumTools.selectPayrollType(driver, SalaryType.SALARY);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40  + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40  + 1067.40 / 6), cgpBase);
		
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40  + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40  + 1067.40 / 6), cgpBase);
		
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40  + 1067.40 / 6), cgcBase);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40  + 1067.40 / 6), cgpBase);
		
		extra(issueDate, endDate);
		totalPayments = SeleniumTools.getAmount(driver, totalPaymentsLabel);
		assertEquals(SeleniumTools.unmessDouble(1067.40/6 * 5  + (1067.40*20/30)/6), totalPayments);
		
		SeleniumTools.draft(driver, "MATERNIDAD, COMPLETA");
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		switchSalaryMonth(driver, calendar);
		
		
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		// Here start I.T
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		SeleniumTools.draft(driver, "MATERNIDAD, PARCIAL");
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		SeleniumTools.draft(driver, "PATERNIDAD, PARCIAL");
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		switchSalaryMonth(driver, calendar);
		assertEquals(cgcBase, SeleniumTools.getAmount(driver, cgcBaseLabel));
		
		calendar.set(Calendar.YEAR, 2018);
		
		SeleniumTools.draft(driver, "PAGO DIRECTO, REDEFINIDO");
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		switchSalaryMonth(driver, calendar);
		assertEquals((Double)0.00, SeleniumTools.getAmount(driver, cgcBaseLabel));
	}
	
	@Ignore
	@Test
	public void TestPercepcionesDelSistema() throws Exception {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "percepciones_del_sistema");
		SeleniumTools.draft(driver, "PREST, ENFERMEDAD COMUN");
		
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		switchSalaryMonth(driver, calendar);
		
		assertTrue(	// SALARIO BASE MENSUAL
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "db-amount-label-1"))).isEnabled()
		);
		assertTrue(
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "delete-button-1"))).isEnabled()
		);
		
		
		assertTrue(	// PLUS SALARIAL MENSUAL
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "db-amount-label-2"))).isEnabled()
		);
		assertTrue(
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "delete-button-2"))).isEnabled()
		);
		
		
		assertTrue(	// PREST. POR ENFERMEDAD COMÚN
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "db-amount-label-3"))).isEnabled()
		);
		assertTrue(
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "description-box-3"))).isEnabled()
		);
		assertTrue(
			!wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "delete-button-3"))).isEnabled()
		);
		
		
		assertTrue(	// PREST. POR ENFERMEDAD COMÚN
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "db-amount-label-4"))).isEnabled()
		);
		assertTrue(
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "description-box-4"))).isEnabled()
		);
		assertTrue(
			!wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "delete-button-4"))).isEnabled()
		);
		
		
		assertTrue(	// PREST. POR ENFERMEDAD COMÚN
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "db-amount-label-5"))).isEnabled()
		);
		assertTrue(
			!wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "description-box-5"))).isEnabled()
		);
		assertTrue(
			!wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + "delete-button-5"))).isEnabled()
		);
		
		

	}
	
	@Test
	public void TestPracticas() throws Exception {
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "practicas");
		
		SeleniumTools.draft(driver, "PRACTICAS, TIEMPO COMPLETO");

		By percentInput = By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL");
		By porcDesmplE = By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL_E");
		By porcDempl = By.id(GWT_ID_PROFIX + "textBox_PORCENTAJE_DESMPL");	
		By check = By.id(GWT_ID_PROFIX + "costsCheck-input");
		
		wait.until(ExpectedConditions.attributeToBeNotEmpty(driver.findElement(percentInput), "value"));
		assertEquals((Double)1.55, SeleniumTools.getAmount(driver, percentInput));
		
		SeleniumTools.checkboxCheck(driver, check);
		
		assertEquals((Double)5.5, SeleniumTools.getAmountNotEmptyValue(driver, porcDesmplE));
		SeleniumTools.checkboxUncheck(driver, check);
		
		SeleniumTools.draft(driver, "PRACTICAS, TIEMPO PARCIAL");
		assertEquals((Double)1.55, SeleniumTools.getAmountNotEmptyValue(driver, porcDempl));	
		SeleniumTools.checkboxCheck(driver, check);		
		assertEquals((Double)5.5, SeleniumTools.getAmountNotEmptyValue(driver, porcDesmplE));		
		SeleniumTools.checkboxUncheck(driver, check);
	}
	
	@Test
	public void TestSonny() throws Exception {
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "sonny");
		
		SeleniumTools.draft(driver, "CONSTANTES, I");
		
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		
		switchSalaryMonth(driver, calendar);
		
		By cgcBaseLabel = By.id(GWT_ID_PROFIX + "cgcBaseLabel");
		By totalPaymentLabel = By.id(GWT_ID_PROFIX + "totalPaymentsLabel");
		
		Double cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		Double totalPayment = SeleniumTools.getAmount(driver, totalPaymentLabel);
		assertEquals(cgcBase, totalPayment);
		
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonth(driver, calendar);
		Double sonnyCgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		Double sonnytotalPayment = SeleniumTools.getAmount(driver, totalPaymentLabel);
		assertEquals(cgcBase, sonnyCgcBase);
		assertEquals(totalPayment, sonnytotalPayment);
		
		assertEquals(0, driver.findElements(By.cssSelector("#" + GWT_ID_PROFIX + "eventsTable tr")).size());
		
		SeleniumTools.draft(driver, "CONSTANTES, II (PAGAS)");
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		totalPayment = SeleniumTools.getAmount(driver, totalPaymentLabel);
		
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonth(driver, calendar);
		sonnyCgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		sonnytotalPayment = SeleniumTools.getAmount(driver, totalPaymentLabel);
		assertEquals(cgcBase, sonnyCgcBase);

		assertEquals(0, driver.findElements(By.cssSelector("#" + GWT_ID_PROFIX + "eventsTable tr")).size());
		
		SeleniumTools.draft(driver, "CONSTANTES, III (BONO)");
		calendar.set(Calendar.MONTH, Calendar.MARCH);	
		switchSalaryMonth(driver, calendar);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		totalPayment = SeleniumTools.getAmount(driver, totalPaymentLabel);
		
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonth(driver, calendar);
		sonnyCgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		sonnytotalPayment = SeleniumTools.getAmount(driver, totalPaymentLabel);
		assertEquals(cgcBase, sonnyCgcBase);

		assertEquals(0, driver.findElements(By.cssSelector("#" + GWT_ID_PROFIX + "eventsTable tr")).size());
	}
	
	private static void createDraftTestContract() throws InterruptedException {
		SeleniumTools.integralFromIndex(driver);
		
		String workplaceId = GWT_ID_PROFIX + "i.t";
		
		SeleniumTools.openWorkplace(driver, workplaceId);
		
		retryingFindClick(driver, By.id(workplaceId));
		SeleniumTools.rightClick(driver, By.id(workplaceId));
		
		String nuevoSelector = ".gwt-MenuBarPopup tr:nth-of-type(1) td.gwt-MenuItem";
		SeleniumTools.click(driver, By.cssSelector(nuevoSelector));
		String contratoSelector = ".gwt-MenuBarPopup tr:nth-of-type(1) td.gwt-MenuItem > span.aon-icon-employee";
		SeleniumTools.click(driver, By.cssSelector(contratoSelector));
		
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "document", "12345678Z");
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "security_social_num", "00000000000");
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "name", "BORRADOR");
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "first_surname", "PRUEBA");
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "activityCCC"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "activityCCC :nth-child(3)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "activityCCC"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "contractType"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "contractType :nth-child(3)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "contractType"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "modality"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "modality :nth-child(2)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "modality"));
		
		Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.YEAR, -1);
//		cal.add(Calendar.MONTH, -2);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
		String from = df.format(cal.getTime());
		
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "start_date", from);
		
		String out = ".aon_custom_dialog_title";
		
		SeleniumTools.click(driver, By.cssSelector(out));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "agreement"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "agreement :nth-child(2)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "agreement"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "level"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "level :nth-child(2)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "level"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "quote_group"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "quote_group :nth-child(2)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "quote_group"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "occupation"));
		SeleniumTools.click(driver, By.cssSelector("#" + GWT_ID_PROFIX + "occupation :nth-child(2)"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "occupation"));
		
		SeleniumTools.click(driver, By.cssSelector("button.aon_ok_button_small"));
	}
	
	private void addIT() {
		Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
		cal.set(Calendar.DAY_OF_MONTH, 10);
		String diseaseDate = df.format(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, 13);
		String healDate = df.format(cal.getTime());
		SeleniumTools.click(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		SeleniumTools.click(driver, By.cssSelector("a[id='aonContent:payrollMenu:gwt_main_it']"));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.cssSelector("rect"))));
		SeleniumTools.click(driver, By.cssSelector("div.aon_toolbar_button_container button.aon_icon_add"));
		
		String cssTrabajador = "td > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > input.aon-inputText";
		String cssBaja = "table > tbody > tr:nth-of-type(1) table  td > div:nth-of-type(1) > div:nth-of-type(2) > input.aon-inputText";
		String cssAlta = "table > tbody > tr:nth-of-type(1) table  td > div:nth-of-type(1) > div:nth-of-type(3) > input.aon-inputText";
		String cssCausaBaja = "tbody > tr:nth-child(2) > td > div > div:nth-child(2) > select";
		String cssCausaAlta = "tbody > tr:nth-child(2) > td > div > div:nth-child(3) > select";
		String cssAceptar = "button.aon_ok_button_small";
		String cssOut = ".aon_custom_dialog_title";
		SeleniumTools.safeInput(driver, cssTrabajador, "PRUEBA");
		SeleniumTools.click(driver, By.cssSelector(".gwt-SuggestBoxPopup .item"));
		SeleniumTools.click(driver, By.cssSelector(cssOut));
		SeleniumTools.click(driver, By.cssSelector(cssBaja));
		SeleniumTools.safeInput(driver, cssBaja, diseaseDate);
		SeleniumTools.click(driver, By.cssSelector(cssOut));
		SeleniumTools.click(driver, By.cssSelector(cssAlta));
		SeleniumTools.safeInput(driver, cssAlta, healDate);
		SeleniumTools.click(driver, By.cssSelector(cssOut));
		SeleniumTools.click(driver, By.cssSelector(cssCausaBaja));
		SeleniumTools.click(driver, By.cssSelector(cssCausaBaja + " :nth-child(2)"));
		SeleniumTools.click(driver, By.cssSelector(cssCausaBaja));
		SeleniumTools.click(driver, By.cssSelector(cssCausaAlta));
		SeleniumTools.click(driver, By.cssSelector(cssCausaAlta + " :nth-child(2)"));
		SeleniumTools.click(driver, By.cssSelector(cssCausaAlta));
		SeleniumTools.click(driver, By.cssSelector(cssAceptar));
	}
	
	private void removeIT() {
		SeleniumTools.click(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		SeleniumTools.click(driver, By.cssSelector("a[id='aonContent:payrollMenu:gwt_main_it']"));
		
		SeleniumTools.safeInput(driver, ".gwt-SuggestBox", "PRUEBA , BORRADOR");
		SeleniumTools.click(driver, By.cssSelector(".gwt-SuggestBoxPopup .item"));
		SeleniumTools.click(driver, By.cssSelector("rect[fill='#ffa500']"));
		SeleniumTools.click(driver, By.cssSelector("button.aon_icon_delete"));
		SeleniumTools.click(driver, By.cssSelector("button.aon_ok_button"));
	}
	
	@Test
	public void testDraftConceptChange() throws Exception {
		createDraftTestContract();
		
		SeleniumTools.integralFromIndex(driver);
		String workplaceId = GWT_ID_PROFIX + "i.t";
		SeleniumTools.openWorkplace(driver, workplaceId);
		SeleniumTools.draft(driver, "PRUEBA, BORRADOR");
		
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "description-box-new-payment", "CUALQUIER COSA");
		wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "description-box-new-payment"))).sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "amount-box-new-payment", "100");
		wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "amount-box-new-payment"))).sendKeys(Keys.TAB);
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "edit-button-1"));
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "conceptSuggestBox", "PLUS_CONVENIO");
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "paymetDialogAcceptButton"));
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "edit-button-1"));
		boolean fail = false;
		try {
			wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "conceptSuggestBox"), "value", "PLUS_CONVENIO"));
		} catch (Exception e) {
			fail = true;
		} finally {
			SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "paymetDialogCancelButton"));
			removeContract();
			assertFalse(fail);
		}
		
	}
	
	@Test
	public void testDraftITConceptChange() throws Exception {
		createDraftTestContract();
		addIT();
		
		
		SeleniumTools.integralFromIndex(driver);
		SeleniumTools.search(driver, "PRUEBA, BORRADOR");
		SeleniumTools.draft(driver, "PRUEBA, BORRADOR");
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "description-box-new-payment", "CUALQUIER COSA");
		wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "description-box-new-payment"))).sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "amount-box-new-payment", "DIAS_TRABAJADOS");
		wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "amount-box-new-payment"))).sendKeys(Keys.TAB);
		SeleniumTools.click(driver, By.id(GWT_ID_PROFIX + "acceptButton"));
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "db-amount-label-3", "DIAS_TRABAJADOS/DIAS_MES");
		wait.until(ExpectedConditions.elementToBeClickable(By.id(GWT_ID_PROFIX + "db-amount-label-3"))).sendKeys(Keys.TAB);
		
		boolean fail = false;
		try {
			wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "db-amount-label-3"), "value", "CUALQUIER COSA"));
		} catch (Exception e) {
			fail = true;
		} finally {
			removeIT();
			removeContract();
			assertFalse(fail);
		}
		
	}
	
	private void removeContract() throws InterruptedException {
		SeleniumTools.integralFromIndex(driver);
		SeleniumTools.search(driver, "PRUEBA, BORRADOR");
		String createdId = GWT_ID_PROFIX + "prueba,_borrador-content";
		SeleniumTools.click(driver, By.id(createdId));
		SeleniumTools.rightClick(driver, By.id(createdId));
		SeleniumTools.click(driver, By.cssSelector("span.aon-icon-delete"));
	}


	private void extra(Date issueDate, Date endDate) {
		SeleniumTools.selectPayrollType(driver, SalaryType.EXTRA);
		
		By extraDateItem = By.id(GWT_ID_PROFIX + "dateListBox");
		
		SeleniumTools.click(driver, wait, extraDateItem);
		
		String dateXpath = "//span[text()='" + String.format( new Locale("es","ES"),"%1$te de %1$tB de %1$tY", issueDate) + "']";
		
		SeleniumTools.click(driver, wait, By.xpath(dateXpath));
		
		Calendar calendar = Calendar.getInstance(new Locale("es","ES"));
		calendar.setTime(endDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int end = calendar.get(Calendar.DAY_OF_MONTH);
		String periodStr = String.format( new Locale("es","ES"),"[0-9]+/[0-9]+/[0-9]+\\s*-\\s*%3$d/%2$d/%1$d", year, month, end);
		By periodSelector = By.id(GWT_ID_PROFIX + "periodLabel");
		try {
			wait.until(ExpectedConditions.textMatches(periodSelector, Pattern.compile(periodStr, Pattern.CASE_INSENSITIVE)));
		} catch (Exception e) {
			fail("Didn't change the extra date");
		}
	}
	
	private void checkCgcAndCgpByDate(int year, int month, Double expectedCgcBase, Double expectedCgpBase) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		SeleniumTools.cleanCalendar(calendar);
		
		switchSalaryMonth(driver, calendar);
		Double cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals(expectedCgcBase, cgcBase);
		Double cgpBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgpBaseLabel"));
		assertEquals(expectedCgpBase, cgpBase);
	}
	
	private void checkCgcByDate(int year, int month, Double expectedCgcBase) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		SeleniumTools.cleanCalendar(calendar);
		
		switchSalaryMonth(driver, calendar);
		Double cgcBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgcBaseLabel"));
		assertEquals(expectedCgcBase, cgcBase);
	}
	
	@SuppressWarnings("unused")
	private void checkCgpByDate(int year, int month, Double expectedCgpBase) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		SeleniumTools.cleanCalendar(calendar);
		
		switchSalaryMonth(driver, calendar);
		Double cgpBase = SeleniumTools.getAmount(driver, By.id(GWT_ID_PROFIX + "cgpBaseLabel"));
		assertEquals(expectedCgpBase, cgpBase);
	}
	
}
