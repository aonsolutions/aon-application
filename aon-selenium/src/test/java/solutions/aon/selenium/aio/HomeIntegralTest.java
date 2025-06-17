package solutions.aon.selenium.aio;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.Calendar;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.SeleniumTools;

public class HomeIntegralTest extends AioBaseTestCase {

	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		Logger.start(testName.getMethodName().toUpperCase());
	}
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        driver = newChromeDriver();
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login(driver, HOME);
        
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
	public void TestQuote() throws Exception {
		By cgpBaseLabel = By.id(GWT_ID_PROFIX + "cgpBaseLabel");
		By cgcBaseLabel = By.id(GWT_ID_PROFIX + "cgcBaseLabel");
		By commonContingencyElem = By.id(GWT_ID_PROFIX + "common_contingency");

//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.draft(driver, "TRAMO, 1");
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.MAY);

		switchSalaryMonth(driver, calendar);
		
		
		Double cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		Double cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		Double commonContingency = SeleniumTools.getAmount(driver, commonContingencyElem);
		
		assertEquals((Double)167.74, cgpBase);
		assertEquals((Double)167.74, cgcBase);
		assertEquals(SeleniumTools.unmessDouble(167.74*4.55/100.00), commonContingency);
		
		SeleniumTools.draft(driver, "TRAMO, 2");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		commonContingency = SeleniumTools.getAmount(driver, commonContingencyElem);
		assertEquals((Double)277.51, cgpBase);
		assertEquals((Double)277.51, cgcBase);
		assertEquals(SeleniumTools.unmessDouble(277.51*4.55/100.00), commonContingency);
		
		SeleniumTools.draft(driver, "TRAMO, 3");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)387.29, cgpBase);
		assertEquals((Double)387.29, cgcBase);

		SeleniumTools.draft(driver, "TRAMO, 3 (DOS PERCEPCIONES)");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		commonContingency = SeleniumTools.getAmount(driver, commonContingencyElem);
		assertEquals((Double)387.29, cgpBase);
		assertEquals((Double)387.29, cgcBase);
		assertEquals(SeleniumTools.unmessDouble(387.29*4.55/100.00), commonContingency);

		SeleniumTools.draft(driver, "TRAMO, 4");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)497.08, cgpBase);
		assertEquals((Double)497.08, cgcBase);

		SeleniumTools.draft(driver, "TRAMO, 5");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)606.86, cgpBase);
		assertEquals((Double)606.86, cgcBase);

		SeleniumTools.draft(driver, "TRAMO, 6");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)716.65, cgpBase);
		assertEquals((Double)716.65, cgcBase);

		SeleniumTools.draft(driver, "TRAMO, 7");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)858.60, cgpBase);
		assertEquals((Double)858.60, cgcBase);
		
		SeleniumTools.draft(driver, "TRAMO, 8");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)896.94, cgpBase);
		assertEquals((Double)896.94, cgcBase);
	}
	
	@Test
	public void TestQuoteII() throws Exception {
		By cgpBaseLabel = By.id(GWT_ID_PROFIX + "cgpBaseLabel");
		By cgcBaseLabel = By.id(GWT_ID_PROFIX + "cgcBaseLabel");
		By commonContingencyElem = By.id(GWT_ID_PROFIX + "common_contingency");

//		ENTER 'INTEGRAL DE NÓMINAS'
		SeleniumTools.integralFromIndex(driver);
		
		SeleniumTools.draft(driver, "TRAMO, 1");
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		SeleniumTools.cleanCalendar(calendar);
		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);

		switchSalaryMonth(driver, calendar);
		
		
		Double cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		Double cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		Double commonContingency = SeleniumTools.getAmount(driver, commonContingencyElem);
		
		assertEquals((Double)206.00, cgpBase);
		assertEquals((Double)206.00, cgcBase);
		assertEquals(SeleniumTools.unmessDouble(206.00*4.70/100.00), commonContingency);

		SeleniumTools.draft(driver, "TRAMO, 2");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		commonContingency = SeleniumTools.getAmount(driver, commonContingencyElem);
		assertEquals((Double)340.00, cgpBase);
		assertEquals((Double)340.00, cgcBase);
		assertEquals(SeleniumTools.unmessDouble(340.00*4.70/100.00), commonContingency);
		
		SeleniumTools.draft(driver, "TRAMO, 3");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)474.00, cgpBase);
		assertEquals((Double)474.00, cgcBase);
		
		SeleniumTools.draft(driver, "TRAMO, 3 (DOS PERCEPCIONES)");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		commonContingency = SeleniumTools.getAmount(driver, commonContingencyElem);
		assertEquals((Double)474.00, cgpBase);
		assertEquals((Double)474.00, cgcBase);
		assertEquals(SeleniumTools.unmessDouble(474.00*4.70/100.00), commonContingency);

		SeleniumTools.draft(driver, "TRAMO, 4");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)608.00, cgpBase);
		assertEquals((Double)608.00, cgcBase);
		
		SeleniumTools.draft(driver, "TRAMO, 5");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)743.00, cgpBase);
		assertEquals((Double)743.00, cgcBase);	

		SeleniumTools.draft(driver, "TRAMO, 6");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)877.00, cgpBase);
		assertEquals((Double)877.00, cgcBase);	
		
		SeleniumTools.draft(driver, "TRAMO, 7");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1050.00, cgpBase);
		assertEquals((Double)1050.00, cgcBase);	
		
		SeleniumTools.draft(driver, "TRAMO, 8");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1097.00, cgpBase);
		assertEquals((Double)1097.00, cgcBase);	
		
		SeleniumTools.draft(driver, "TRAMO, 9");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1232.00, cgpBase);
		assertEquals((Double)1232.00, cgcBase);	
		
		SeleniumTools.draft(driver, "TRAMO, _10");
		switchSalaryMonth(driver, calendar);
		cgpBase = SeleniumTools.getAmount(driver, cgpBaseLabel);
		cgcBase = SeleniumTools.getAmount(driver, cgcBaseLabel);
		assertEquals((Double)1555.00, cgpBase);
		assertEquals((Double)1555.00, cgcBase);	
	}
	
}
