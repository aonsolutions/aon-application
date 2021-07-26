package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;

public class MainAgreementsTestCase extends AioBaseTestCase {
	
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
        login(driver, null);
        
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
	public void TestEstatutoDeLosTrabajadores() throws Exception {
		SeleniumTools.mainAgreementFromIndex(driver);
		clickShowAllAgreements(driver);
		
		SeleniumTools.searchAndEnterAgreement(driver, "ESTATUTO DE LOS TRABAJADORES");
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "draftButton")));
		} catch (Exception e) {
			fail("Draft button still visible");
		}
		By collapseAllButton = By.id(GWT_ID_PROFIX + "collapseAllButton");
		SeleniumTools.click(driver, wait, collapseAllButton);
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "deleteItem")));
		} catch (Exception e) {
			fail("Delete item still visible");
		}
		
		String descriptionValue = SeleniumTools.getAttributeNotEmpty(driver, By.id(GWT_ID_PROFIX + "descriptionTextBox"), "value");
		assertEquals("ESTATUTO DE LOS TRABAJADORES", descriptionValue);
		
		for ( String id : new String []{
				"fxButton",
				"undoButton",
				"redoButton",
				"undoAllButton",
				"acceptButton",
//				"deleteButton",
				}){
			assertFalse(id + " not disabled", wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GWT_ID_PROFIX + id))).isEnabled());
		}
		
		try {
		wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "descriptionTextBox"), "readOnly", "true"));
		} catch (Exception e) {
			fail("descriptionTextBox is not read only");
		}
		
		for ( String id : new String [] {
				"paymentsTable",
				"extrasTable",
				//"salaryTable"
				} ){
			
			List<WebElement> inputs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#" + GWT_ID_PROFIX + id + " input")));
			
			for (WebElement input : inputs) {
				Assert.assertTrue(
						(!input.isDisplayed())
						|| !input.isEnabled()
						|| input.getAttribute("readOnly").equals("true")
				);
			}
			
			List<WebElement> selects = driver.findElements(By.cssSelector("#" + GWT_ID_PROFIX + id + " select"));
			
			for (WebElement select : selects) {
				assertTrue(!select.isDisplayed()
						|| !select.isEnabled());
			}
		}
	}
	
	@Test
	public void TestMensajesdeAyuda() throws Exception {
		SeleniumTools.mainAgreementFromIndex(driver);
		clickShowAllAgreements(driver);
		By by = By.id(GWT_ID_PROFIX + "mensajes_de_ayuda,_ejemplos");
		SeleniumTools.click(driver, wait, by);
		try {
			wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "descriptionTextBox"), "value", "MENSAJES DE AYUDA, EJEMPLOS"));
		} catch (Exception e) {
			fail("descriptionTextBox's value is not 'MENSAJES DE AYUDA, EJEMPLOS'");
		}
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "toggleButton_01_01_1970")));
	}
	
	@Test
	public void TestPrintPreview() throws Exception {
		SeleniumTools.mainAgreementFromIndex(driver);
		clickShowAllAgreements(driver);
		
		By convenioMadrid = By.id(GWT_ID_PROFIX + "convenio_colectivo_de_oficinas_y_despachos_para_madrid");
		
		SeleniumTools.click(driver, wait, convenioMadrid);
		wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "descriptionTextBox"), "value", "CONVENIO COLECTIVO DE OFICINAS Y DESPACHOS PARA MADRID"));
		By printPreview = By.id(GWT_ID_PROFIX + "printPreviewButton");
		SeleniumTools.click(driver, wait, printPreview);
		assertTrue(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#" + GWT_ID_PROFIX + "printPreviewViewer table"))).size() > 0);
	}
	
	@Test
	public void TestExtras() throws Exception {
		SeleniumTools.mainAgreementFromIndex(driver);
		clickShowAllAgreements(driver);
		By extrasAnuales = By.id(GWT_ID_PROFIX + "pagas_extras_anulaes,_semestrales_y_trimestreales");
		SeleniumTools.click(driver, wait, extrasAnuales);
		wait.until(ExpectedConditions.attributeToBe(By.id(GWT_ID_PROFIX + "descriptionTextBox"), "value", "PAGAS EXTRAS ANULAES, SEMESTRALES Y TRIMESTREALES"));
		for ( int i = 1; i < 8; i++ ) {
			wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "endDateBox" + i), "value", "2021"));
			wait.until(ExpectedConditions.attributeContains(By.id(GWT_ID_PROFIX + "startDateBox" + i), "value", "2021"));
			
		}
	}
	
	private static void clickShowAllAgreements (WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		By showAll = By.cssSelector("button[title='Mostrar todos los convenios']");
		SeleniumTools.click(driver, wait, showAll);
	}
}