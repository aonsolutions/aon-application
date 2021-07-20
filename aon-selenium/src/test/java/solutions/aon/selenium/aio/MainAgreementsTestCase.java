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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
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
		By showAll = By.cssSelector("button[title='Mostrar todos los convenios']");
		wait.until(ExpectedConditions.elementToBeClickable(showAll));
		SeleniumTools.retryingFindClick(driver, showAll);
		
		SeleniumTools.searchAndEnterAgreement(driver, "ESTATUTO DE LOS TRABAJADORES");
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(GWT_ID_PROFIX + "draftButton")));
		} catch (Exception e) {
			fail("Draft button still visible");
		}
		By collapseAllButton = By.id(GWT_ID_PROFIX + "collapseAllButton");
		wait.until(ExpectedConditions.elementToBeClickable(collapseAllButton));
		SeleniumTools.retryingFindClick(driver, collapseAllButton);
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
	//TODO
	@Ignore
	@Test
	public void TestMensajesdeAyuda() throws Exception {
		SeleniumTools.mainAgreementFromIndex(driver);
		
		
	}
	
	
}