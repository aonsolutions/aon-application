package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;
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
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.SeleniumTools;

public class NetAndGrossTestCase extends AioBaseTestCase {
	
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
	public void grossTest() {
		
	}
	
	@Test
	public void netTest() throws InterruptedException {
		
//		ENTER 'INTEGRAL DE NÓMINAS'
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		String dummiesId = "gwt-debug-4dummies";
		By dummies = By.cssSelector("#" + dummiesId + " > table > tbody > tr > td:nth-of-type(1)");
		wait.until(ExpectedConditions.elementToBeClickable(dummies));
		retryingFindClick(driver, dummies);
		
		String netConstantId = "gwt-debug-constante,_neto";
		
		retryingFindClick(driver, By.cssSelector("div [id='" + netConstantId + "'] > table tr td:nth-of-type(1)"));
		
		String draftId = "gwt-debug-constante,_neto-draft-content";
		
		retryingFindClick(driver, By.id(draftId));
		
		retryingFindClick(driver, By.id("gwt-debug-monthListBox"));
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		
		
		selectMonth(driver, c.getTime());

		
		checkFields(driver);
		
		//CHANGE NET
		
		WebElement netInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-debug-totalLiquidLabel")));
		
		netInput.click();
		
		for (int i=0; i<10; i++) {
			Thread.sleep(250);
			netInput.sendKeys(Keys.BACK_SPACE);
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
		
		Double actualTotalDeductions = getAmount(driver, By.cssSelector("#rootPanel  table  td:nth-child(3)  td:nth-child(2) div.aon-text-center > span:nth-child(1)"));
		
		assertEquals(actualTotalDeductions, totalDeductions);
	}
}
