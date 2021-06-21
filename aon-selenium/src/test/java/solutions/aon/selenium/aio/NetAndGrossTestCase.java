package solutions.aon.selenium.aio;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

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

}
