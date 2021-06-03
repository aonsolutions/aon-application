package solutions.aon.selenium.solutions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import solutions.aon.selenium.AbstractTestCase;

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
		//fail("Not yet implemented");
	}

}
