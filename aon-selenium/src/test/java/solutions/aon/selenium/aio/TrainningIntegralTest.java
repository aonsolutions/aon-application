package solutions.aon.selenium.aio;

import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.Calendar;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;


/*
 -Dintegration.test.user=admin
 -Dintegration.test.password=org
 -Dintegration.test.payroll.url=http://payroll-test.aonsolutions.org:8080/aon-aio/
 -Dintegration.test.general.payroll.url=http://general-payroll-test.aonsolutions.org:8080/aon-aio/
 -Dintegration.test.trainning.payroll.url=http://trainning-payroll-test.aonsolutions.org:8080/aon-aio/
 -Dintegration.test.home.payroll.url=http://home-payroll-test.aonsolutions.org:8080/aon-aio/
 * */
public class TrainningIntegralTest extends AioBaseTestCase {
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		Logger.start(testName.getMethodName().toUpperCase());
	}
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        driver = newChromeDriver();
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login(driver, TRAINNING);
        
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
	public void TestQuote() throws Exception {
		SeleniumTools.integralFromIndex(driver);
		
		if(!driver.findElement(By.id(GWT_ID_PROFIX + "finiquito_formacion,_aprendizaje-content")).isDisplayed()) {
			SeleniumTools.openWorkplace(driver, GWT_ID_PROFIX + "cotizacion_formacion_y_el_aprendizaje");
		}
		
		SeleniumTools.draft(driver, "FINIQUITO FORMACIÓN, APRENDIZAJE");
		
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,18")));
		
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,67")));

		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,67")));
		
		SeleniumTools.draft(driver, "FORMACIÓN Y EL, APRENDIZAJE");
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,18")));
		
		calendar.set(Calendar.MONTH, Calendar.MAY);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,18")));
		
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,67")));
		
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,94")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "unemployment"), "13,31")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "job_training"), "0,17")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgcBaseLabel"), "value", "858,60")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgpBaseLabel"), "value", "858,60")));
		
		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "8,49")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "unemployment"), "16,28")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "job_training"), "0,17")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgcBaseLabel"), "value", "1.050,00")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgpBaseLabel"), "value", "1.050,00")));
		
		SeleniumTools.checkboxCheck(driver, By.id(GWT_ID_PROFIX + "costsCheck-input"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(GWT_ID_PROFIX + "common_contingency_cost")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency_cost"), "42,56")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "unemployment_cost"), "57,75")));
		
		SeleniumTools.draft(driver, "BECARIO, EL");
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,18")));
		
		calendar.set(Calendar.YEAR, 2017);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,67")));
		
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,94")));
		
		calendar.set(Calendar.YEAR, 2019);
		switchSalaryMonthMatchingMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "8,49")));

		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency_cost"), "42,56")));
		
		SeleniumTools.draft(driver, "FORMACION Y APRENDIZAJE, IT");
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		switchSalaryMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "6,94")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "unemployment"), "13,31")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgcBaseLabel"), "value", "858,60")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgpBaseLabel"), "value", "858,60")));
		
		calendar.set(Calendar.YEAR, 2019);
		switchSalaryMonth(driver, calendar);
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "common_contingency"), "8,49")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "unemployment"), "16,28")));
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(GWT_ID_PROFIX + "job_training"), "0,17")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgcBaseLabel"), "value", "1.050,00")));
		assertTrue(wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "cgpBaseLabel"), "value", "1.050,00")));
	}

}
