package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;
import static solutions.aon.selenium.aio.id.AonHeaderId.LABORAL_BUTTON;
import static solutions.aon.selenium.aio.id.LaboralId.INTEGRAL_DE_NOMINAS;
import static solutions.aon.selenium.tools.Logger.log;
import static solutions.aon.selenium.tools.Logger.Status.CLICK;
import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

import java.text.ParseException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;

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
		log(CLICK, "Entering \"Laboral\"");
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		Thread.sleep(1000);
		log(CLICK, "Entering \"Integral de Nóminas\"");
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String antiguedadId = "gwt-debug-antiguedad";
		By antiguedad = By.cssSelector("#" + antiguedadId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(antiguedad));
		Thread.sleep(500);
		log(CLICK, "Deploying \"ANTIGÜEDAD\"");
		retryingFindClick(driver, antiguedad);
		
		SeleniumTools.draft(driver, "1989 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		Double amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		Double expected = 15454.46 / 14 							// SALARIO_BASE
				+ 15454.46 / 14 * 5 / 100 		// ANTIGUEDAD 1989-1992 ( 1 TRIENIO 5%)
				+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1992-1995 ( 1 TRIENIO 4%)
				+ 15454.46 / 14 * 6 * 4 / 100; 	// ANTIGUEDAD 1995-2016 ( 6 CUATRIENIOS 4% )
		expected = SeleniumTools.unmessDouble(expected);
		assertEquals(expected, amount);
		
		SeleniumTools.draft(driver, "1991 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		expected = 15454.46 / 14 							// SALARIO_BASE
			+ 15454.46 / 14 * 4 / 100 		// ANTIGUEDAD 1991-1994 ( 1 TRIENIO 4%)
			+ 15454.46 / 14 * 6 * 4 / 100; 	// ANTIGUEDAD 1994-2016 ( 6 CUATRIENIOS 4% )
		expected = SeleniumTools.unmessDouble(expected);
		assertEquals(expected, amount);
		
		SeleniumTools.draft(driver, "1993 TIEMPO COMPLETO ORDINARIO, INDEFINIDO");
		Thread.sleep(500);
		amount = SeleniumTools.getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		expected = 15454.46 / 14 							// SALARIO_BASE
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

	}
	
	
}
