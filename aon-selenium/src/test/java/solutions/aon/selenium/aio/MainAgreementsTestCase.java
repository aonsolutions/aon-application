package solutions.aon.selenium.aio;

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
        By menuPayroll = By.name("aonContent:mainMenuForm:menu_payroll");
//        WebElement el = wait.until((d) -> ExpectedConditions.elementToBeClickable(menuPayroll));
        
		// Click on Menu 'Convenios'
		driver.findElement(By.name("aonContent:payrollMenu:gwt_agreement2")).click();
		
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
