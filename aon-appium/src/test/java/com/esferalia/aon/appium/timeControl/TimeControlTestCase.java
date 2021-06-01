package com.esferalia.aon.appium.timeControl;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.appium.AbstractTestCase;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class TimeControlTestCase extends AbstractTestCase {

	@BeforeClass
	public static void setUpTimeControl() throws Exception {
		setUpTestDefaultData();
		setUpTestCase(apk, username, password, driver);
	}

//	@Before
//	public void backToHome() {
//		WebDriverWait wait = new WebDriverWait(app, 10);
//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
//		
//		WebElement homeIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonMobileMenuHomeButtonIcon")));
//		homeIcon.click();
//	}


	@After
	public void openOnStart() {
		WebDriverWait wait = new WebDriverWait(app, 5);
		WebElement homeBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonMobileMenuHomeButtonIcon")));
		homeBtn.click();
	}

	@Test
	public void filterTest() {
		WebDriverWait wait = new WebDriverWait(app, 10);
		defaultFilter(app);
		
		

		WebElement closeElement = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.id("aonFilterDialogMenuFilterDialogClick")));
		closeElement.click();
	}

	private static void defaultFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			
		WebElement timeElem = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("*[id='aonMobileMenuControl HorarioButtonIcon']")));
		timeElem.click();

//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		
		WebElement filterElem = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.id("aonSigninToolbarHeaderToolSectionfilterButtonIcon")));
		filterElem.click();
		
		
		WebElement groupBy = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("groupInput")));
		if (!groupBy.getAttribute("value").equalsIgnoreCase("dia")&&!groupBy.getAttribute("value").equalsIgnoreCase("día"))
			fail("Group by input not set to 'DÍA' by default");
		
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("periodInput")));
		if (!period.getAttribute("value").equalsIgnoreCase("Semana actual"))
			fail("Period input not set to 'Semana actual' by default");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
		String dFromStr = dateFromInput.getAttribute("value");

		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("endDate")));
		String dToStr = dateToInput.getAttribute("value");
		
		try {
			Date startDate = df.parse(dFromStr);
			Date endDate = df.parse(dToStr);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			if (cal.get(Calendar.DAY_OF_WEEK) != 0)
				fail("Start date not monday");
			
			long sDateMilis = cal.getTimeInMillis();
			
			cal.setTime(endDate);
			if (cal.get(Calendar.DAY_OF_WEEK) != 6)
				fail("Start date not sunday");
			
			long eDateMilis = cal.getTimeInMillis();
			
			long timeDiff = eDateMilis-sDateMilis;
			
			cal.setTimeInMillis(timeDiff);
			
			if (cal.get(Calendar.DATE) != 7)
				fail("No 7 days diff");
			
			
			
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unparseable date/s");
		}
	}
	
	private static void previousWeekFilter(AppiumDriver<MobileElement> app) {
		
	}

	@Test
	public void afterTest() {
		WebDriverWait wait = new WebDriverWait(app, 10);
//		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			
		WebElement timeElem = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.id("aonSignEntrada")));
		
	}
	
	
	
	
}
