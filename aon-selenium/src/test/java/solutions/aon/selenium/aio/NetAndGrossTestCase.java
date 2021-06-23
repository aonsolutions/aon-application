package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;
import static solutions.aon.selenium.aio.id.AonHeaderId.LABORAL_BUTTON;
import static solutions.aon.selenium.aio.id.LaboralId.CONSTANTE_BRUTO;
import static solutions.aon.selenium.aio.id.LaboralId.CONSTANTE_BRUTO_DRAFT;
import static solutions.aon.selenium.aio.id.LaboralId.FOR_DUMMIES;
import static solutions.aon.selenium.aio.id.LaboralId.INTEGRAL_DE_NOMINAS;
import static solutions.aon.selenium.aio.id.LaboralId.TOTAL_LIQUID;
import static solutions.aon.selenium.aio.id.LaboralId.TOTAL_PAYMENTS;
import static solutions.aon.selenium.aio.id.LaboralId.TOTAL_PAYMENTS_INPUT;
import static solutions.aon.selenium.tools.DataTreatment.safeDouble;
import static solutions.aon.selenium.tools.Logger.log;
import static solutions.aon.selenium.tools.Logger.Status.CLICK;
import static solutions.aon.selenium.tools.Logger.Status.GET;
import static solutions.aon.selenium.tools.Logger.Status.INPUT;
import static solutions.aon.selenium.tools.Logger.Status.START;
import static solutions.aon.selenium.tools.Logger.Status.SUCCESS;
import static solutions.aon.selenium.tools.SeleniumTools.getAmount;
import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;

public class NetAndGrossTestCase extends AioBaseTestCase {
	
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
	public void grossTest() {
		
		// Open 4dummies 
		log(CLICK, "Selecting 4 dummies enterprise.");
        retryingFindClick(driver, By.cssSelector("#" + FOR_DUMMIES + " img"));
 
        // Open 'constante, bruto'
        log(CLICK, "Selecting Constante, bruto employee.");
        retryingFindClick(driver, By.cssSelector("*[id='" + CONSTANTE_BRUTO + "'] img"));

        // Open draft
        log(CLICK, "Entering draft editor.");
        retryingFindClick(driver, By.id(CONSTANTE_BRUTO_DRAFT));
      
        try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
        
        String chimboSelector = "#rootPanel table td:nth-child(3) td:nth-child(2) div span";
      
        Double payments = getAmount(driver, By.id(TOTAL_PAYMENTS));
        Double deductions = getAmount(driver, By.cssSelector(chimboSelector));
        Double total = getAmount(driver, By.id(TOTAL_LIQUID));
                
        Logger.jump();
        Logger.start("Getting data");
        log(GET, "Payments", payments + "");
        log(GET, "Deductions", deductions + "");
        log(GET, "Liquid", total + "");
 
        
        Double diff =  safeDouble(payments,0.00) - safeDouble(deductions,0.00);
        assertEquals(diff, total);
        log(SUCCESS, "DONE.");
        
        
        Logger.jump();
        log(START, "Changing value");     
        
        WebElement totalPaymentInput = wait.until(ExpectedConditions.elementToBeClickable(By.id(TOTAL_PAYMENTS_INPUT)));
        
        log(INPUT, "Changing payment total values");   
        totalPaymentInput.sendKeys("2500");
        
        log(INPUT, "TAB");   
        totalPaymentInput.sendKeys(Keys.TAB);
        
        try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}        
        
        payments = getAmount(driver, By.id(TOTAL_PAYMENTS));
        deductions = getAmount(driver, By.cssSelector(chimboSelector));
        total = getAmount(driver, By.id(TOTAL_LIQUID));
        
        Logger.jump();
        Logger.start("Getting data");
        log(GET, "Payments", payments + "");
        log(GET, "Deductions", deductions + "");
        log(GET, "Liquid", total + "");
                
        diff =  safeDouble(payments,0.00) - safeDouble(deductions,0.00);
        assertEquals(diff, total);
        log(SUCCESS, "DONE.");
	}
	
	@Test
	public void netTest() throws InterruptedException {
		
//		ENTER 'INTEGRAL DE NÓMINAS'
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		Thread.sleep(1000);
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String dummiesId = "gwt-debug-4dummies";
		By dummies = By.cssSelector("#" + dummiesId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(dummies));
		Thread.sleep(500);
		retryingFindClick(driver, dummies);
		
		String netConstantId = "gwt-debug-constante,_neto";
		Thread.sleep(500);
		retryingFindClick(driver, By.cssSelector("div [id='" + netConstantId + "'] > table tr td:nth-of-type(1)"));
		
		String draftId = "gwt-debug-constante,_neto-draft-content";
		Thread.sleep(500);
		retryingFindClick(driver, By.id(draftId));
		Thread.sleep(500);
		retryingFindClick(driver, By.id("gwt-debug-monthListBox"));
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		Thread.sleep(1000);
		selectMonth(driver, c.getTime());
		
		checkFields(driver);
		
		//CHANGE NET
		
		WebElement netInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-totalLiquidLabel")));
		
		netInput.click();
		Thread.sleep(500);
		for (int i=0; i<10; i++) {
			Thread.sleep(250);
			netInput.sendKeys(Keys.BACK_SPACE);
			netInput.sendKeys(Keys.ARROW_RIGHT);
		}
		
		netInput.sendKeys("2500");
		
		
		netInput.sendKeys(Keys.TAB);
		
		
		Thread.sleep(2000);
		checkFields(driver);
		
	}
	
	
	
	private static void selectMonth (WebDriver driver, Date date) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' YYYY", new Locale("es", "ES"));
		String dateString = df.format(date);
		
		retryingFindClick(driver, By.id("gwt-debug-monthListBox"));
		
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#gwt-debug-monthListBox-celllist > div > div[style='outline:none;']")));
		
		Thread.sleep(1500);
		String script = "let elems = document.querySelectorAll(\"#gwt-debug-monthListBox-celllist > div > div[style='outline:none;']\"); for (let i=0; i<elems.length;i++)"
				+ "{let el = elems[i]; console.log(el); let inner = el.innerText != null ? el.innerText.trim() : ''; if (inner.toUpperCase() == '"+dateString+"'.toUpperCase()) {el.click(); break;}}";

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(script);
	}
	
	//FALLA EN EL REDONDEO DE LAS DEDUCCIONES
	private static void checkFields(WebDriver driver) {
		String netLiquidId = "gwt-debug-totalLiquidLabel";
		By netLiquid = By.id(netLiquidId);
		
		Double gross = getAmount(driver, By.id("gwt-debug-totalPaymentsLabel"));
		
		Double net = getAmount(driver, netLiquid);
		
		Double salary = getAmount(driver, By.id("gwt-debug-db-amount-label-1"));
		
		assertEquals(salary, gross);
		
		Double[] deductions = new Double[4];
		
		deductions[0] = getAmount(driver, By.id("gwt-debug-common_contingency"));
		deductions[1] = getAmount(driver, By.id("gwt-debug-unemployment"));
		deductions[2] = getAmount(driver, By.id("gwt-debug-job_training"));
		deductions[3] = getAmount(driver, By.id("gwt-debug-irpf"));
		
		Double totalDeductions = Arrays.stream(deductions).reduce((acc, value) -> acc + value).orElse(0d);
		
		Double expected = SeleniumTools.unmessDouble(salary - totalDeductions);
		
		assertEquals(net, expected);
		
		
		Double totalPayment = getAmount(driver, By.id("gwt-debug-totalPaymentLabel"));
		
		assertEquals(totalPayment, salary);
		
		Double ccPercent = getAmount(driver, By.cssSelector("#gwt-debug-paymentsTable > tbody > tr:nth-of-type(5) >td:nth-of-type(2) > span"));
		
		assertEquals(SeleniumTools.unmessDouble(gross * ccPercent / 100), deductions[0]);
		
		Double unemployPercent = getAmount(driver, By.id("gwt-debug-textBox_PORCENTAJE_DESMPL"));
		
		assertEquals(SeleniumTools.unmessDouble(gross * unemployPercent / 100), deductions[1]);
		
		
		
		Double actualTotalDeductions = getAmount(driver, By.cssSelector("#rootPanel  table  td:nth-child(3)  td:nth-child(2) div.aon-text-center > span:nth-child(1)"));
		
		assertEquals(actualTotalDeductions, totalDeductions);
	}
}
