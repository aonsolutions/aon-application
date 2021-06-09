package solutions.aon.selenium.aio;

import static org.junit.Assert.fail;
import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.SeleniumTools;

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
	public void CostsExcelTest() throws InterruptedException {
//		Thread.sleep(700);
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		By costItem = By.cssSelector(".gwt-StackLayoutPanelContent .gwt-Tree > div:nth-child(2) > div:nth-of-type(1) > div:nth-of-type(1) > .gwt-TreeItem");
		
		wait.until(ExpectedConditions.elementToBeClickable(costItem));
		retryingFindClick(driver, costItem);

		
//		String classPath = AbstractTestCase.class.getResource("./").getPath();
//		driver.get("chrome://settings/downloads");
//		
//		WebDriverWait wait = new WebDriverWait(driver, 10);
//		
//		wait.until(ExpectedConditions.elementToBeClickable(By.id("changeDownloadsPath")));
	
		
		String[] fileNames = {"Costes.xlsx","Costes.xlsx","Costes.csv"};
		for (int i=1; i<=3; i++) {
			
			retryingFindClick(driver, By.cssSelector("button[title='Excel']"));

			File f = new File(SeleniumTools.getDownloadPath() + File.separator + fileNames[i-1]);
			
			if (f.exists()) {
				f.delete();				
			}
			
			retryingFindClick(driver, By.cssSelector(
					".gwt-MenuBarPopup > .popupContent > .gwt-MenuBar > table > tbody > tr:nth-of-type("+i+") > .gwt-MenuItem"));

			Thread.sleep(5000);
			
			f = new File(f.getPath());
			
			if (!f.exists())
				fail("The file was not downloaded");
			else
				f.delete();
		}
		
	}
//	@Ignore
	@Test
	public void CostsTest() throws InterruptedException {
		
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
		
		By costItem = By.cssSelector(".gwt-StackLayoutPanelContent .gwt-Tree > div:nth-child(2) > div:nth-of-type(1) > div:nth-of-type(1) > .gwt-TreeItem");
		
		wait.until(ExpectedConditions.elementToBeClickable(costItem));
		retryingFindClick(driver, costItem);
		
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[title=\"PDF\"]")));
		retryingFindClick(driver, By.cssSelector("button[title=\"PDF\"]"));
		
		List<String> windowList = driver.getWindowHandles().stream().collect(Collectors.toList());
		driver.switchTo().window(windowList.get(1));
		
//		for (String handle : driver.getWindowHandles()) {
//		    driver.switchTo().window(handle);
//		    System.out.println(String.format("handle: %s, url: %s", handle, driver.getCurrentUrl()));
//		} 
		if (!driver.getCurrentUrl().contains("pdf"))
			fail("Did not download the PDF");
		driver.close();
		driver.switchTo().window(windowList.get(0));
		Thread.sleep(1000);
	}
	
	
	@Ignore
	@Test
	public void searchTest () throws InterruptedException {
		WebElement search = null;
		for (int i=0; i<3; i++)
			search = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[id='headerOptionsForm:smartFilter']")));
		search.click();
		Thread.sleep(500);
		search.sendKeys("CONSTANTE, BRUTO");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id='headerOptionsForm:smartFilterTablePanel']")));
		
		List<WebElement> results = driver.findElements(By.cssSelector("tbody > tr > td > .aon-outputText"));
		
		driver.findElement(By.xpath("//html")).click();
		
		if (results.stream().filter(elem -> elem.getText().contains("CONSTANTE, BRUTO")).findAny().isEmpty()) {
			fail("SEARCHED ELEMENT NOT FOUND");
		}
	}
	
	
	
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logout(driver);
		driver.quit();
	}

	@Before
	public void setUp() throws Exception {
		retryingFindClick(driver, By.cssSelector("*[id='headerOptionsForm:index']"));
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("*"))));
	}

	@After
	public void tearDown() throws Exception {
	}

	
}
