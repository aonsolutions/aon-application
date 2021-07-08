package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static solutions.aon.selenium.aio.id.AonHeaderId.LABORAL_BUTTON;
import static solutions.aon.selenium.aio.id.LaboralId.INTEGRAL_DE_NOMINAS;
import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;
import solutions.aon.selenium.tools.SeleniumTools.SALARY_TYPE;

public class GeneralIntegralTest extends AioBaseTestCase {
	
	private static WebDriver driver;
	private static WebDriverWait wait;
	
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
        login(driver);
        
        // Click on Top Menu 'Laboral'        
        retryingFindClick(driver, By.id(LABORAL_BUTTON));
        
        // Click on 'integral de nominas'
        retryingFindClick(driver, By.id(INTEGRAL_DE_NOMINAS));
        
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//logout(driver);
		driver.quit();
	}

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testAntiguedad() throws InterruptedException, ParseException {
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
//		OPEN ANTIGÜEDAD WORKPLACE
		SeleniumTools.openWorkplace(driver, "gwt-debug-antiguedad");
		
		SeleniumTools.draft(driver, "1989 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		Double amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		Double expected = 15454.46 / 14 		// SALARIO_BASE
				+ 15454.46 / 14 * 5 / 100 		// ANTIGUEDAD 1989-1992 ( 1 TRIENIO 5%)
				+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1992-1995 ( 1 TRIENIO 4%)
				+ 15454.46 / 14 * 6 * 4 / 100; 	// ANTIGUEDAD 1995-2016 ( 6 CUATRIENIOS 4% )
		expected = SeleniumTools.unmessDouble(expected);
		assertEquals(expected, amount);
		
		SeleniumTools.draft(driver, "1991 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		expected = 15454.46 / 14 			// SALARIO_BASE
			+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1991-1994 ( 1 TRIENIO 4%)
			+ 15454.46 / 14 * 6 * 4 / 100; 	// ANTIGUEDAD 1994-2016 ( 6 CUATRIENIOS 4% )
		expected = SeleniumTools.unmessDouble(expected);
		assertEquals(expected, amount);
		
		SeleniumTools.draft(driver, "1993 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		expected = 15454.46 / 14 				// SALARIO_BASE
				+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1993-1996 ( 1 TRIENIO 4%)
				+ 15454.46 / 14 * 6 * 4 / 100; 	// ANTIGUEDAD 1996-2016 ( 6 CUATRIENIOS 4% )
		expected = SeleniumTools.unmessDouble(expected);
		assertEquals(expected, amount);
		
		SeleniumTools.draft(driver, "2012 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		expected = 15454.46 / 14 // SALARIO_BASE
				+ 15454.46 / 14 * 2 * 4 / 100; 				// ANTIGUEDAD 2012-2016 ( 2 CUATRIENIOS 4% )
		expected = SeleniumTools.unmessDouble(expected);
		assertEquals(expected, amount);

		SeleniumTools.draft(driver, "CONCEPTO ANTIGUEDAD, DESCRIPCION");
		Thread.sleep(500);
		By byid = By.id("gwt-debug-employeeNameLabel");
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(byid));
		String elementText = element.getAttribute("innerText");
		assertEquals("CONCEPTO ANTIGUEDAD, DESCRIPCION", elementText);
	}
	
	@Test
	public void TestAtrasos() throws Exception {
		
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
//		OPEN ATRASOS WORKPLACE
		SeleniumTools.openWorkplace(driver, "gwt-debug-atrasos");
		
		wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-atrasos_tiempo_completo_ordinario,_indefinido")));
		
		SeleniumTools.draft(driver, "ATRASOS TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		
		Calendar calendar = Calendar.getInstance();
		SeleniumTools.resetCalendar(calendar);
		for ( int month = 0; month < 12; month++ ) {
			calendar.set(Calendar.MONTH, month);
			SeleniumTools.selectMonthScrollingV2(driver, calendar.getTime());
			SeleniumTools.checkSalaryPeriod(driver, calendar.getTime());
			retryingFindClick(driver, By.id("gwt-debug-salaryButton"));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("gwt-debug-dbSalaryCheck")));
			Thread.sleep(500);
		}
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		SeleniumTools.delay(driver, calendar.getTime(), new Date());
		
		Double totalPayments = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		assertEquals((Double)0.00, totalPayments);
		Double netLiquid = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalLiquidLabel"));
		assertEquals((Double)0.00, netLiquid);
		
		
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gwt-debug-fxButton"))))
			Assert.fail("fxButton not hidden");
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gwt-debug-undoAllButton"))))
			Assert.fail("'undo all' button not hidden");
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gwt-debug-undoButton"))))
			Assert.fail("'undo' button not hidden");
		if (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gwt-debug-redoButton"))))
			Assert.fail("'redo' button not hidden");
		
		SeleniumTools.selectPayrollType(driver, SALARY_TYPE.SALARY);
		SeleniumTools.selectMonthScrollingV2(driver, calendar.getTime());
		
		SeleniumTools.safeInput(driver, "#gwt-debug-description-box-new-payment", "[3]ATRASOS");
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-description-box-new-payment")));
		input.sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#gwt-debug-amount-box-new-payment", "100");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-amount-box-new-payment")));
		input.sendKeys(Keys.TAB);
		boolean checkInput = SeleniumTools.changingElementAssert(driver, By.id("gwt-debug-description-box-3"), "value", "[3]ATRASOS");
		assertTrue("[3]ATRASOS not found", checkInput);
		
		SeleniumTools.acceptDraft(driver);
		
		By deleteButton = By.id("gwt-debug-delete-button-3");
		wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
		retryingFindClick(driver, deleteButton);
		SeleniumTools.acceptDraft(driver);
		
		SeleniumTools.selectPayrollType(driver, SALARY_TYPE.SALARY);
		SeleniumTools.selectMonthScrollingV2(driver, calendar.getTime());
		
		SeleniumTools.safeInput(driver, "#gwt-debug-description-box-new-payment", "[3]ATRASOS");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-description-box-new-payment")));
		input.sendKeys(Keys.TAB);
		SeleniumTools.safeInput(driver, "#gwt-debug-amount-box-new-payment", "100");
		input = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-amount-box-new-payment")));
		input.sendKeys(Keys.TAB);
		checkInput = SeleniumTools.changingElementAssert(driver, By.id("gwt-debug-description-box-3"), "value", "[3]ATRASOS");
		assertTrue("[3]ATRASOS not found", checkInput);
		
		SeleniumTools.delay(driver, calendar.getTime(), new Date());
		Double ccBase = SeleniumTools.getAmount(driver, By.id("gwt-debug-cgcBaseLabel"));
		assertEquals((Double)0.00d, ccBase);
		Double payments = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		assertEquals((Double)0.00d, payments);
		Double liquid = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalLiquidLabel"));
		assertEquals((Double)0.00d, liquid);
	}
	
	@Test
	public void TestBasesMaximasYMinimas() throws Exception {
		
//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
//		OPEN 'BASES MÁXIMAS Y MÍNIMAS' WORKPLACE
		SeleniumTools.openWorkplace(driver, "gwt-debug-bases_maximas_y_minimas");
		
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
	
	private void checkCgcAndCgpByDate(int year, int month, Double expectedCgcBase, Double expectedCgpBase) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		SeleniumTools.resetCalendar(calendar);
		
		SeleniumTools.selectMonthScrollingV2(driver, calendar.getTime());
		Thread.sleep(500);
		SeleniumTools.checkSalaryPeriod(driver, calendar.getTime());
		Double cgcBase = SeleniumTools.getAmount(driver, By.id("gwt-debug-cgcBaseLabel"));
		assertEquals(expectedCgcBase, cgcBase);
		Double cgpBase = SeleniumTools.getAmount(driver, By.id("gwt-debug-cgpBaseLabel"));
		assertEquals(expectedCgpBase, cgpBase);
	}
	
	private void checkCgcByDate(int year, int month, Double expectedCgcBase) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		SeleniumTools.resetCalendar(calendar);
		
		SeleniumTools.selectMonthScrollingV2(driver, calendar.getTime());
		Thread.sleep(500);
		SeleniumTools.checkSalaryPeriod(driver, calendar.getTime());
		Double cgcBase = SeleniumTools.getAmount(driver, By.id("gwt-debug-cgcBaseLabel"));
		assertEquals(expectedCgcBase, cgcBase);
	}
	
	private void checkCgpByDate(int year, int month, Double expectedCgpBase) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		SeleniumTools.resetCalendar(calendar);
		
		SeleniumTools.selectMonthScrollingV2(driver, calendar.getTime());
		Thread.sleep(500);
		SeleniumTools.checkSalaryPeriod(driver, calendar.getTime());
		Double cgpBase = SeleniumTools.getAmount(driver, By.id("gwt-debug-cgpBaseLabel"));
		assertEquals(expectedCgpBase, cgpBase);
	}
	
}
