package com.esferalia.aon.appium.testCases;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esferalia.aon.appium.AbstractTestCase;
import com.esferalia.aon.appium.id.AonIdHome;
import com.esferalia.aon.appium.id.AonIdNavigationBar;
import com.esferalia.aon.appium.id.AonIdTimeControl;

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
	public void backToHome() {
		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement homeBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdNavigationBar.HOME_BUTTON)));
		homeBtn.click();
	}

	@Test
	public void filterTest() {
		try {
			WebDriverWait wait = new WebDriverWait(app, 10);
			openFilter(app);
			
			currentDayFilter(app);
			previousDayFilter(app);
			currentWeekFilter(app);
			previousWeekFilter(app);
			currentMonthFilter(app);
			previousMonthFilter(app);
			currentYearFilter(app);
			previousYearFilter(app);
			
			By by = By.id(AonIdTimeControl.FILTER_CLOSE);
			ExpectedCondition<WebElement> cnd = ExpectedConditions.visibilityOfElementLocated(by);
	
			WebElement closeElement = wait.until(cnd);
			closeElement.click();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Interrupted");
		}
	}

	private static void openFilter (AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		app.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			
		
//		WebElement timeElem = wait.until(ExpectedConditions
//				.presenceOfElementLocated(By.cssSelector("i[id='"+AonIdNavigationBar.TIME_CONTROL_BUTTON+"']")));
		WebElement filterElem = null;
		boolean failed;
		int failcount = 0;
		do {
			failed = false;
			WebElement timeElem = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.cssSelector("aon-icon-button[icon='alarm_on']")));
			timeElem.click();
			try {
				Thread.sleep(100);
				timeElem.click();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
			filterElem = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER)));
			
			} catch (Exception e) {
				failed = true;
				failcount++;
			}
			
		} while (failed && failcount < 10);
		
		do {
			failed = false;
			filterElem.click();
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_GROUP_BY)));
			} catch (Exception e) {
				failed = true;
			}
		} while (failed == true);
	}
	
	private static void currentDayFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		
		period.click();
		
		WebElement currentWeek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(1)")));
		
		currentWeek.click();
		
		
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");

		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		checkDay(cal.getTime(), dFromStr, dToStr);
	}
	
	private static void previousDayFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		
		period.click();
		
		WebElement currentWeek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(2)")));
		
		currentWeek.click();
		
		
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");
		
		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DATE, -1);
		
		checkDay(cal.getTime(), dFromStr, dToStr);
	}
	
	private static void checkDay (Date day, String dFromStr, String dToStr) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = df.parse(dFromStr);
			Date endDate = df.parse(dToStr);
			
			if (!startDate.equals(day) || !endDate.equals(day))
				fail("Day does not match");
			
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unparseable date/s");
		}
	}
	
	
	
	private static void currentWeekFilter(AppiumDriver<MobileElement> app) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(app, 10);
		
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		
		period.click();
		
		WebElement currentWeek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(3)")));
		
		currentWeek.click();
		
		
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");

		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		checkWeeklyDate(cal.getTime(),dFromStr, dToStr);
	}
	
	private static void previousWeekFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		period.click();
		WebElement previousWeek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(4)")));
		previousWeek.click();
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");

		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DATE, -7);
		
		checkWeeklyDate(cal.getTime(), dFromStr, dToStr);
		
	}
	
	private static void currentMonthFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		period.click();
		WebElement previousWeek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(5)")));
		previousWeek.click();
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");
		
		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		checkMonthlyDate(cal.getTime(), dFromStr, dToStr);
		
	}
	
	private static void previousMonthFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		period.click();
		WebElement previousWeek = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(6)")));
		previousWeek.click();
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");
		
		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		checkMonthlyDate(cal.getTime(), dFromStr, dToStr);
	}
	
	
	private void currentYearFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		period.click();
		WebElement currentYear = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(7)")));
		currentYear.click();
		
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");
		
		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.set(Calendar.DAY_OF_YEAR, 1);
		
		checkYearlyDate(cal.getTime(), dFromStr, dToStr);
	}
	
	private static void previousYearFilter(AppiumDriver<MobileElement> app) {
		WebDriverWait wait = new WebDriverWait(app, 10);
		WebElement period = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_PERIOD)));
		period.click();
		WebElement currentYear = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#"+AonIdTimeControl.PERIOD_DROPDOWN_DIV+" > ul > li:nth-child(8)")));
		currentYear.click();
		
		WebElement dateFromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_START_DATE)));
		String dFromStr = dateFromInput.getAttribute("value");
		
		WebElement dateToInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(AonIdTimeControl.FILTER_END_DATE)));
		String dToStr = dateToInput.getAttribute("value");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.add(Calendar.YEAR, -1);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		
		checkYearlyDate(cal.getTime(), dFromStr, dToStr);
	}

	private static void checkWeeklyDate(Date realFirstWeekDate, String dFromStr, String dToStr) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = df.parse(dFromStr);
			Date endDate = df.parse(dToStr);
			
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
				fail("Start date not monday");
			
			long sDateMilis = cal.getTimeInMillis();
			
			cal.setTime(endDate);
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				fail("Start date not sunday");
			
			long eDateMilis = cal.getTimeInMillis();
			
			long timeDiff = eDateMilis-sDateMilis;
			
			cal.setTimeInMillis(timeDiff);
			
			if (cal.get(Calendar.DATE) != 7)
				fail("No 7 days diff");
			
			cal.setTime(realFirstWeekDate);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			
			if (!startDate.equals(realFirstWeekDate))
				fail("Weekly Start Date does not match");
			if (!endDate.equals(cal.getTime()))
				fail("Weekly End Date does not match");
			
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unparseable date/s");
		}
	}
	
	private static void checkMonthlyDate(Date realFirstMonthDate, String dFromStr, String dToStr) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = df.parse(dFromStr);
			Date endDate = df.parse(dToStr);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(realFirstMonthDate);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			if (!startDate.equals(realFirstMonthDate))
				fail("Monthly Start Date does not match");
			if (!endDate.equals(cal.getTime()))
				fail("Monthly End Date does not match");
			
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unparseable date/s");
		}
	}
	
	private static void checkYearlyDate(Date realFirstYearDate, String dFromStr, String dToStr) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = df.parse(dFromStr);
			Date endDate = df.parse(dToStr);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(realFirstYearDate);
			cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
			
			if (!startDate.equals(realFirstYearDate))
				fail("Yearly Start Date does not match");
			if (!endDate.equals(cal.getTime()))
				fail("Yearly End Date does not match");
			
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Unparseable date/s");
		}
	}
	
	//LOCATION NOT WORKING
	@Ignore
	@Test
	public void signInTest() {
		WebDriverWait wait = new WebDriverWait(app, 20);
		
		//AppiumTools.setFakeLocation(app);
		WebElement timeElem = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.id(AonIdHome.ENTRANCE_BUTTON)));
		
		timeElem.click();
		LogEntries logEntries = app.manage().logs().get("driver");

		logEntries.forEach(log ->  System.out.println(log));

		
		WebElement pauseElem = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("#"+ AonIdHome.SIGNIN_BUTTONS_CONTAINER +" > .aonButton:nth-child(2)")));
		
		timeElem.click();
		
	}
	
	
	
	
}
