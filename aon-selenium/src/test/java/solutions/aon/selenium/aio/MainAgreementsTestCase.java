package solutions.aon.selenium.aio;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainAgreementsTestCase extends AioBaseTestCase {

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
	
	@Test
	public void CostsTest() throws InterruptedException {
		
		WebElement el = wait
				.until(ExpectedConditions.elementToBeClickable(By.id("aonContent:mainMenuForm:menu_payroll")));
		el.click();

		WebElement integralElem = wait
				.until(ExpectedConditions.elementToBeClickable(By.id("aonContent:payrollMenu:gwt_employee")));
		integralElem.click();

		WebElement costsElem = wait.until(ExpectedConditions.elementToBeClickable(By.id("gwt-uid-20")));
		costsElem.click();

		// aon_button aon_icon_excel aon_toolbar_button
		WebElement excelMenuElem = wait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title='Excel']")));
		excelMenuElem.click();

		
//		String classPath = AbstractTestCase.class.getResource("./").getPath();
//		driver.get("chrome://settings/downloads");
//		
//		WebDriverWait wait = new WebDriverWait(driver, 10);
//		
//		wait.until(ExpectedConditions.elementToBeClickable(By.id("changeDownloadsPath")));
		
		
		File f = new File(System.getProperty("user.home") + File.separator + "Descargas" + File.separator + "Costes.xlsx");
		
		if (!f.exists())
			f = new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "Costes.xlsx");
		
		if (f.exists()) {
			
			f.delete();
			
		}
		
		
		
		WebElement excelDownloadElem = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(
				".gwt-MenuBarPopup > .popupContent > .gwt-MenuBar > table > tbody > tr:nth-of-type(1) > .gwt-MenuItem")));
		excelDownloadElem.click();

		
		Thread.sleep(5000);
		
		f = new File(f.getPath());
		
		if (!f.exists())
			fail("The file was not downloaded");
	}
	
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logout(driver);
		driver.quit();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void test() {
		
		
	}
	
}
