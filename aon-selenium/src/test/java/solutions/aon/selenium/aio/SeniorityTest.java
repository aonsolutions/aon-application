package solutions.aon.selenium.aio;

import static org.junit.Assert.assertTrue;
import static solutions.aon.selenium.tools.SeleniumTools.checkAmount;
import static solutions.aon.selenium.tools.SeleniumTools.getAmount;
import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.SeleniumTools;

public class SeniorityTest extends AioBaseTestCase {
	private static WebDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        driver = newChromeDriver();
        
        wait = new WebDriverWait(driver, 10);
        login(driver);
        
        // Click on Top Menu 'Laboral'
//        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonContent:mainMenuForm:menu_payroll")));
//        el.click();
        
		// Click on Menu 'Convenios'
		//driver.findElement(By.name("aonContent:payrollMenu:gwt_agreement2")).click();
		
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logout(driver);
		driver.quit();
	}

	@Before
	public void setUp() throws Exception {
		driver.navigate().to(getUrl());
//		retryingFindClick(driver, By.cssSelector("*[id='headerOptionsForm:index']"));
//		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("*"))));
	}
	@Test
	public void test1989() throws InterruptedException {
//		ENTER 'INTEGRAL DE NÓMINAS'
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String seniorityId = "gwt-debug-antiguedad";
		By seniority = By.cssSelector("#" + seniorityId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(seniority));
		retryingFindClick(driver, seniority);
		
		String id1989 = "gwt-debug-1989_tiempo_completo_ordinario,_indefinido";
		
		retryingFindClick(driver, By.cssSelector("div [id='" + id1989 + "'] > table tr td:nth-of-type(1)"));
		
		String draftId = "gwt-debug-1989_tiempo_completo_ordinario,_indefinido-draft-content";
		
		retryingFindClick(driver, By.id(draftId));
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-totalPaymentsLabel")));
		
		Double yearlyPay = 15454.46;
		
		Double salary = getAmount(driver, By.id("gwt-debug-db-amount-label-1"));
		
		assertTrue("Wrong base salary", SeleniumTools.checkAmount(salary, yearlyPay/14));
		
		Double[] complements = new Double[3];
		
		complements[0] = getAmount(driver, By.id("gwt-debug-db-amount-label-2"));
		complements[1] = getAmount(driver, By.id("gwt-debug-db-amount-label-3"));
		complements[2] = getAmount(driver, By.id("gwt-debug-db-amount-label-4"));
		
		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( 1993-1996 )", checkAmount(complements[0], yearlyPay/14*4/100));
		
		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( < 1993 )", checkAmount(complements[1], yearlyPay/14*5/100));

		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )", checkAmount(complements[2], yearlyPay/14*6*4/100));
		
		
		Double complementsSum = Arrays.stream(complements).reduce((acc, value) -> acc+value).orElse(null);
		
		
		
		Double  expectedPayments = salary + complementsSum;
		Double payments = getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		
		assertTrue("Wrong IMPORTE TOTAL DEVENGOS", checkAmount(payments, expectedPayments));
		
		String ssLabelId = (driver.findElements(By.id("gwt-debug-quote-label-5")).size() > 0) ?
				"gwt-debug-quote-label-5" : "gwt-debug-quote-label-6";
		
		Double ssAmount = getAmount(driver, By.id(ssLabelId));
		Double expectedSS = expectedPayments/6;
		
//		assertTrue("", checkAmount(ssAmount, expectedSS));
		
	}
	
	@Test
	public void test1991() throws InterruptedException {
//		ENTER 'INTEGRAL DE NÓMINAS'
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String seniorityId = "gwt-debug-antiguedad";
		By seniority = By.cssSelector("#" + seniorityId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(seniority));
		retryingFindClick(driver, seniority);
		
		String id1991 = "gwt-debug-1991_tiempo_completo_ordinario,_indefinido";
		
		retryingFindClick(driver, By.cssSelector("div [id='" + id1991 + "'] > table tr td:nth-of-type(1)"));
		
		String draftId = "gwt-debug-1991_tiempo_completo_ordinario,_indefinido-draft-content";
		
		retryingFindClick(driver, By.id(draftId));
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-totalPaymentsLabel")));
		
		Double yearlyPay = 15454.46;
		
		Double salary = getAmount(driver, By.id("gwt-debug-db-amount-label-1"));
		
		assertTrue("Wrong base salary", checkAmount(salary, yearlyPay/14));
		
		Double[] complements = new Double[2];
		
		complements[0] = getAmount(driver, By.id("gwt-debug-db-amount-label-2"));
		complements[1] = getAmount(driver, By.id("gwt-debug-db-amount-label-4"));
		
		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( 1993-1996 )", checkAmount(complements[0], yearlyPay/14*4/100));

		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )", checkAmount(complements[1], yearlyPay/14*6*4/100));
		
		
		Double complementsSum = Arrays.stream(complements).reduce((acc, value) -> acc+value).orElse(null);
		
		
		
		Double  expectedPayments = salary + complementsSum;
		Double payments = getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		
		assertTrue("Wrong IMPORTE TOTAL DEVENGOS", checkAmount(payments, expectedPayments));
		
		String ssLabelId = (driver.findElements(By.id("gwt-debug-quote-label-5")).size() > 0) ?
				"gwt-debug-quote-label-5" : "gwt-debug-quote-label-6";
		
		Double ssAmount = getAmount(driver, By.id(ssLabelId));
		Double expectedSS = expectedPayments/6;
		
//		assertTrue("", checkAmount(ssAmount, expectedSS));
		
	}
	
	@Test
	public void test1993() throws InterruptedException {
//		ENTER 'INTEGRAL DE NÓMINAS'
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String seniorityId = "gwt-debug-antiguedad";
		By seniority = By.cssSelector("#" + seniorityId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(seniority));
		retryingFindClick(driver, seniority);
		
		String id1993 = "gwt-debug-1993_tiempo_completo_ordinario,_indefinido";
		
		retryingFindClick(driver, By.cssSelector("div [id='" + id1993 + "'] > table tr td:nth-of-type(1)"));
		
		String draftId = "gwt-debug-1993_tiempo_completo_ordinario,_indefinido-draft-content";
		
		retryingFindClick(driver, By.id(draftId));
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-totalPaymentsLabel")));
		
		Double yearlyPay = 15454.46;
		
		Double salary = getAmount(driver, By.id("gwt-debug-db-amount-label-1"));
		
		assertTrue("Wrong base salary", checkAmount(salary, yearlyPay/14));
		
		Double[] complements = new Double[2];
		
		complements[0] = getAmount(driver, By.id("gwt-debug-db-amount-label-2"));
		complements[1] = getAmount(driver, By.id("gwt-debug-db-amount-label-4"));
		
		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( 1993-1996 )", checkAmount(complements[0], yearlyPay/14*4/100));

		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )", checkAmount(complements[1], yearlyPay/14*6*4/100));
		
		
		Double complementsSum = Arrays.stream(complements).reduce((acc, value) -> acc+value).orElse(null);
		
		
		
		Double  expectedPayments = salary + complementsSum;
		Double payments = getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		
		assertTrue("Wrong IMPORTE TOTAL DEVENGOS", checkAmount(payments, expectedPayments));
		
		String ssLabelId = (driver.findElements(By.id("gwt-debug-quote-label-5")).size() > 0) ?
				"gwt-debug-quote-label-5" : "gwt-debug-quote-label-6";
		
		Double ssAmount = getAmount(driver, By.id(ssLabelId));
		Double expectedSS = expectedPayments/6;
		
//		assertTrue("", checkAmount(ssAmount, expectedSS));
		
	}
	
	@Test
	public void test2012() throws InterruptedException {
//		ENTER 'INTEGRAL DE NÓMINAS'
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String seniorityId = "gwt-debug-antiguedad";
		By seniority = By.cssSelector("#" + seniorityId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(seniority));
		retryingFindClick(driver, seniority);
		
		String id1989 = "gwt-debug-2012_tiempo_completo_ordinario,_indefinido";
		
		retryingFindClick(driver, By.cssSelector("div [id='" + id1989 + "'] > table tr td:nth-of-type(1)"));
		
		String draftId = "gwt-debug-2012_tiempo_completo_ordinario,_indefinido-draft-content";
		
		retryingFindClick(driver, By.id(draftId));
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-totalPaymentsLabel")));
		
		Double yearlyPay = 15454.46;
		
		Double salary = getAmount(driver, By.id("gwt-debug-db-amount-label-1"));
		
		assertTrue("Wrong base salary", checkAmount(salary, yearlyPay/14));
		
		
		Double complement = getAmount(driver, By.id("gwt-debug-db-amount-label-4"));

		assertTrue("Wrong [2]COMPLEMENTO DE ANTIGÜEDAD ( > 1996 )", checkAmount(complement, yearlyPay/14*2*4/100));
		
		
		
		
		
		Double  expectedPayments = salary + complement;
		Double payments = getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		
		assertTrue("Wrong IMPORTE TOTAL DEVENGOS", checkAmount(payments, expectedPayments));
		
		String ssLabelId = (driver.findElements(By.id("gwt-debug-quote-label-5")).size() > 0) ?
				"gwt-debug-quote-label-5" : "gwt-debug-quote-label-6";
		
		Double ssAmount = getAmount(driver, By.id(ssLabelId));
		Double expectedSS = expectedPayments/6;
		
//		assertTrue("", checkAmount(ssAmount, expectedSS));
		
	}

}
