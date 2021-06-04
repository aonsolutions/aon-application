package solutions.aon.selenium.solutions;

import static solutions.aon.selenium.tools.SeleniumTools.setFakeLocation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.AbstractTestCase;
import solutions.aon.selenium.solutions.id.AonIdHome;
import solutions.aon.selenium.tools.SeleniumTools;

public class TimeControlTestCase extends AbstractTestCase {
	
	private static WebDriver driver;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
        driver = newChromeDriver();
        
        login(driver);
        
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
		WebDriverWait wait = new WebDriverWait(driver, 20);
		setFakeLocation(driver);
		
		WebElement timeElem = wait.until(ExpectedConditions
				.elementToBeClickable(By.id(AonIdHome.ENTRANCE_BUTTON)));
		
		timeElem.click();
		
		WebElement pauseElem = wait.until(ExpectedConditions.elementToBeClickable(By.id(AonIdHome.EXIT_BUTTON)));
		pauseElem.click();
	}

}
