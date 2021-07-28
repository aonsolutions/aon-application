package solutions.aon.selenium.aio;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;

public class GeneralAgreementTest extends AioBaseTestCase {
	
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
	public void TestPagasAnualesVeranoNavidad() throws Exception {
		SeleniumTools.generalAgreementFromIndex(driver);
		clickShowAllAgreements(driver);
		By pagasAnuales = By.id(GWT_ID_PROFIX + "pagas_anuales_verano_&_navidad");
		SeleniumTools.click(driver, wait, pagasAnuales);
		wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "descriptionTextBox"), "value", "PAGAS ANUALES VERANO & NAVIDAD"));
		SeleniumTools.safeInput(driver, "#" + GWT_ID_PROFIX + "textBox_SALARIO_MENSUAL_I", "666.66");
		By textBox = By.id(GWT_ID_PROFIX + "textBox_SALARIO_MENSUAL_I");
		driver.findElement(textBox).sendKeys(Keys.TAB);
		wait.until(ExpectedConditions.attributeContains(textBox, "class", "aon-icon-changed"));
	}
	
	private static void clickShowAllAgreements (WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		By showAll = By.cssSelector("button[title='Mostrar todos los convenios']");
		SeleniumTools.click(driver, wait, showAll);
	}
}