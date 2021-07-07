package solutions.aon.selenium.tools;

import static solutions.aon.selenium.tools.Logger.log;
import static solutions.aon.selenium.tools.Logger.Status.CLICK;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.tools.SeleniumTools.SALARY_TYPE;

/**
 * Toolkit for easy java test
 * with selenium API
 * 
 * @author akrck02
 */
public class SeleniumTools {

	/**
	 * Web platforms
	 */
	public static enum PLATFORM {
		FIREFOX("webdriver.gecko.driver"),
		EDGE("webdriver.edge.driver"),
		CHROME("webdriver.chrome.driver"),
		SAFARY("");
		
		private String driverName;
		
		private PLATFORM(String driverName) {
			this.driverName = driverName;
		}
		
		public String getDriverName() {
			return this.driverName;
		}
	}
	
	/**
	 * Log level
	 */
	public static enum LOG_LEVEL {
		INFO,
		WARNING,
		ERROR,
		FATAL,
		NONE
	}
	
	public static enum SALARY_TYPE {
		SALARY,
		EXTRA,
		DELAY,
		SETTLE;
	}

	/**
	 * Headless browser 
	 * @param driver - The driver PATH
	 * @param platform - The platform to get
	 * @return browser
	 */
	public static WebDriver headLessBrowser(String driver, Device device, PLATFORM platform, LOG_LEVEL level) {

		switch (platform) {
		case FIREFOX:
			return headlessFirefox(driver,device,level);
		case EDGE:
			return headlessEdge(driver,device,level);
		case SAFARY:
			return headlessSafari(driver,device,level);
		default:
			return headlessChrome(driver,device,level);
		}

	}

	/**
	 * Chrome headless driver.
	 * @param driver - The driver path
	 * @return Chrome 
	 */
	private static ChromeDriver headlessChrome(String driver, Device device, LOG_LEVEL level) {
		
		System.setProperty(PLATFORM.CHROME.getDriverName(), driver);
		ChromeOptions options = new ChromeOptions();		
		
		/**
		 * Set browser options
		 */
		options.setHeadless(true);
		options.addArguments("--disable-gpu"); 
		options.addArguments("--start-maximized"); 
		options.addArguments("--disable-infobars"); 
		options.addArguments("--disable-extensions"); 
		options.addArguments("--disable-dev-shm-usage"); 
		options.addArguments("--no-sandbox"); 
		options.addArguments("--user-agent=" + device.getUserAgent());		
		options.addArguments("--width=" + device.getWidth());			
		options.addArguments("--height=" + device.getHeight());			
		options.addArguments("--pixelRatio=" + device.getPixelRatio());	
		
		/**
		 * Disable Image loading
		 */
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.managed_default_content_settings.images", 2);
		options.setExperimentalOption("prefs", prefs);
		
		
		/**
		 * Setting log level
		 */
		if(level == LOG_LEVEL.NONE) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
		}else {
			options.addArguments("--log-level=" + level.ordinal());		
		}
		
		/**
		 * Building chrome driver
		 */
		ChromeDriver chrome = new ChromeDriver(options);
		return chrome;
	}

	/**
	 * Firefox headless driver.
	 * @param driver - The driver path
	 * @return Firefox 
	 */
	private static FirefoxDriver headlessFirefox(String driver, Device device, LOG_LEVEL level) {
		
		System.setProperty(PLATFORM.FIREFOX.getDriverName(), driver);
		FirefoxOptions options = new FirefoxOptions();
		
		/**
		 * Setting userAgent , blocking image loading and disabling CSS.
		 */
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("general.useragent.override", device.getUserAgent()); 
		profile.setPreference("permissions.default.image", 2);
		profile.setPreference("permissions.default.stylesheet", 2);
		profile.setPreference("permissions.default.microphone", 1);
		profile.setPreference("permissions.default.camera", 1);
		profile.setPreference("permissions.default.geolocation", 1);
		
		/**
		 * Setting firefox options
		 */
		options.setProfile(profile);
		options.setHeadless(true);	
		options.addArguments("--width=" + device.getWidth());			
		options.addArguments("--height=" + device.getHeight());			
		options.addArguments("--pixelRatio=" + device.getPixelRatio());		
		
		/**
		 * Setting log level
		 */
		if(level == LOG_LEVEL.NONE) {
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
		}else {
			options.addArguments("--log-level=" + level.ordinal());		
		}
		
		/**
		 * Building firefox
		 */
		FirefoxDriver firefox = new FirefoxDriver(options);
		return firefox;
	}
	

	/**
	 * Edge headless driver.
	 * @param driver - The driver path
	 * @return Edge 
	 */
	private static EdgeDriver headlessEdge(String driver, Device device, LOG_LEVEL level) {
		throw new RuntimeException("Not Implemented yet");
	}

	/**
	 * Safari headless driver.
	 * @param driver - The driver path
	 * @return Safari 
	 */
	private static SafariDriver headlessSafari(String driver, Device device, LOG_LEVEL level) {
		throw new RuntimeException("Not Implemented yet");
	}	
	
	
	/**
	 * Fake location 
	 */
	public static void setFakeLocation(WebDriver browser) {
		String script = 
				"window.navigator.geolocation.getCurrentPosition = function(success){"
				+ "var position = {"
						+ "'coords\' : {'latitude': '42.84998', 'longitude': ' 	-2.67268'}"
				+ "};"
				+ "success(position);"
				+ "}";
	
		JavascriptExecutor js = (JavascriptExecutor) browser;
		js.executeScript(script);
	}
	
	//The way to get elements clicked without random 'StaleElementReferenceException' exceptions
	public static boolean retryingFindClick(WebDriver driver, By by) {
	    boolean result = false;
	    int attempts = 0;
	    while(attempts < 4) {
	        try {
	            driver.findElement(by).click();
	            result = true;
	            break;
	        } catch(StaleElementReferenceException e) {
	        }
	        attempts++;
	    }
	    return result;
	}
		
	public static void waitNClick(WebDriver app, By by) {
		waitNClick(app,by, 10);
	}
	
	public static void waitNClick(WebDriver app, By by, int customTimeout) {
		WebDriverWait wait = new WebDriverWait(app, customTimeout);
		wait.until(ExpectedConditions.elementToBeClickable(by));
		retryingFindClick(app, by);
	}
	
	public static String getDownloadPath() {
		String downloadFolder = System.getProperty("user.home") + File.separator + "Descargas";
		
		Path downloadPath = Paths.get(downloadFolder);
		
		if (!Files.exists(downloadPath))
			downloadFolder = System.getProperty("user.home") + File.separator + "Downloads";
		
		return downloadFolder;
	}
	
	public static boolean clickUntilNotExists(WebDriver app, WebElement elem) {
	    boolean result = false;
	    int attempts = 0;
	    while(attempts < 8) {
	    	try {
	            elem.click();
	    	} catch (Exception e) {
	    		result = true;
	    		break;
	    	}
	        attempts++;
	    }
	    return result;
	}
	
	
	public static boolean clickUntilNotExists(WebDriver app, By by) {
	    boolean result = false;
	    int attempts = 0;
	    WebElement elem = new WebDriverWait(app, 10).until(ExpectedConditions.elementToBeClickable(by));
	    while(attempts < 8) {
	    	try {
	            elem.click();
	    	} catch (Exception e) {
	    		result = true;
	    		break;
	    	}
	        attempts++;
	    }
	    return result;
	}
	
	public static boolean checkAmount(Double actualAmount, Double expectedAmount) {
		expectedAmount = Math.round(expectedAmount*100.0) / 100.0;
		return actualAmount.equals(expectedAmount);
	}
	
	
	public static Double getAmount(WebDriver driver, By selector) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement baseSalaryAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		
		String baseSalaryStr = baseSalaryAmount.getAttribute("value");
		Double baseSalary = null;
		try {
			baseSalaryStr = baseSalaryStr.replaceAll("\\.", "").replaceAll(",", ".").replaceAll("%", "").trim();
			baseSalary = Double.parseDouble(baseSalaryStr);
			
			return baseSalary;
			
		} catch (NullPointerException | NumberFormatException e) {
			try {
				baseSalaryStr = baseSalaryAmount.getAttribute("innerText");
				baseSalaryStr = baseSalaryStr.replaceAll("\\.", "").replaceAll(",", ".").replaceAll("%", "").trim();
				baseSalary = Double.parseDouble(baseSalaryStr);
				return baseSalary;
			} catch (NullPointerException | NumberFormatException e1) {
				return null;
			}
		}
		
	}
	
	public static boolean waitUntilElementContains(WebDriver driver, By selector, String text) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		
		String content = element.getAttribute("innerText");
		String value = element.getAttribute("value");
		
		content = content != null ? content.trim() : "";
		value = value != null ? value.trim() : "";
		
		for (int i=0; i<10; i++) {
			Thread.sleep(1000);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
			content = element.getAttribute("innerText");
			value = element.getAttribute("value");			
			if (content.equalsIgnoreCase(text) || content.equalsIgnoreCase(value))
				return true;
		}
		
		return false;
		
	}

	public static Double unmessDouble(Double number) {
		if (number != null)
			return Math.round(number * 100.0) / 100.0;
		return null;
	}
	
	public static int differenceInMonths(Date d1, Date d2) {
	    Calendar c1 = Calendar.getInstance();
	    c1.setTime(d1);
	    c1 = resetCalendar(c1);
	    
	    Calendar c2 = Calendar.getInstance();
	    c2.setTime(d2);
	    c2 = resetCalendar(c2);
	    int diff = 0;
	    if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
	    	return 0;
	    } else if (c2.after(c1)) {
	        while (c2.after(c1)) {
	            c1.add(Calendar.MONTH, 1);
//	            if (c2.after(c1)) {
	                diff++;
//	            }
	        }
	    } else if (c2.before(c1)) {
	        while (c2.before(c1)) {
	            c1.add(Calendar.MONTH, -1);
//	            if (c2.before(c1)) {
	                diff++;
//	            }
	        }
	    }
	    return diff;
	}
	
	public static Calendar resetCalendar (Calendar c) {
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c;
	}
	
	public static void main(String[] args) {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Calendar.AUGUST);
		
		System.out.println(differenceInMonths(new Date(), c.getTime()));
	}
	
	
	@Deprecated
	public static void selectMonthScrolling (WebDriver driver, Date date) throws InterruptedException, ParseException {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' YYYY", new Locale("es", "ES"));
		String dateStr = df.format(date);
		WebElement monthPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#gwt-debug-monthListBox-popup > div > div")));
		
		String xpath = "//div[@id='gwt-debug-monthListBox-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+dateStr+"')]";

		
		List<WebElement> elems = monthPopup.findElements(By.xpath(xpath));
//		List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
		
		String currentDateStr = driver.findElement(By.id("gwt-debug-monthListBox-item0")).getAttribute("innerText");
		Date currentDate = df.parse(currentDateStr);
		
		Keys key = (currentDate.getTime() > date.getTime()) ? Keys.PAGE_UP : Keys.PAGE_DOWN;
		
		
		
		while (elems.size() < 1) {
			for (int i=0; i<20; i++) {
				monthPopup.sendKeys(key);
				Thread.sleep(100);
			}
			elems = monthPopup.findElements(By.xpath(xpath));
		}
		
		
		retryingFindClick(driver, By.xpath(xpath));
		Thread.sleep(5000);
	}
	
	public static void selectFromMonthScrollingV2 (WebDriver driver, Date date) throws Exception {
		selectMonthScrollingCommon (driver, date, "gwt-debug-fromMonthListBox");
	}
	public static void selectMonthScrollingV2 (WebDriver driver, Date date) throws Exception {
		selectMonthScrollingCommon (driver, date, "gwt-debug-monthListBox");
	}
	
	public static String getAttribute(WebDriver driver, By selector, String attributeName) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
		String attribute = null;
		int attempts = 0;
		do {
			try {
				attribute = elem.getAttribute(attributeName);
				return attribute;
			} catch (StaleElementReferenceException e) {
				elem = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
			}
			attempts++;
		} while (attempts < 10);
		throw new Exception("Unable to get the attribute");
	}
	
	private static void selectMonthScrollingCommon (WebDriver driver, Date date, String boxId) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' yyyy", new Locale("es", "ES"));
		Date today = new Date();
		String dateStr = df.format(today);
		
		retryingFindClick(driver, By.id(boxId));
		
		WebElement monthPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#" + boxId + "-popup > div > div")));
		
		String xpath = "//div[@id='" + boxId + "-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+dateStr+"')]/..";
		
		
		WebElement currentElem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		String idx = getAttribute(driver, By.xpath(xpath), "__idx");
//		boolean stale = false;
//		int attempts = 0;
//		do {
//			try {
//				idx = currentElem.getAttribute("__idx");
//				stale = false;
//			} catch (StaleElementReferenceException e) {
//				currentElem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
//				stale = true;
//			}
//			attempts++;
//		} while (stale == true && attempts < 10);
		int idxNum = Integer.parseInt(idx);
		Keys key = (today.getTime() > date.getTime()) ? Keys.PAGE_UP : Keys.PAGE_DOWN;
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Math.abs(date.getTime()-today.getTime()));
		int diff = SeleniumTools.differenceInMonths(today,date);
		
		idxNum += (date.getTime() < today.getTime()) ? -diff : diff;
		
		String firstIdx = (today.getTime() > date.getTime()) ?
				getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:nth-child(1)"), "__idx")
					:
				getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:last-child"), "__idx");
		int firstIdxNum = Integer.parseInt(firstIdx);
		
		if (today.getTime() > date.getTime()) {
			while (firstIdxNum > idxNum) {
				monthPopup.sendKeys(key);
//				Thread.sleep(250);
				try {
					firstIdx = getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:nth-child(1)"), "__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
				} catch (StaleElementReferenceException e) {}
			}
		} else {
			while (firstIdxNum < idxNum) {
				monthPopup.sendKeys(key);
//				Thread.sleep(250);
				try {
					firstIdx = getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:last-child"), "__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
				} catch (StaleElementReferenceException e) {}
			}
		}
		
		monthPopup.sendKeys(key);
		Thread.sleep(250);
		monthPopup.sendKeys(key);
		
		xpath = "//div[@id='" + boxId + "-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+df.format(date)+"')]";
		
		retryingFindClick(driver, By.xpath(xpath));
	}
	
	public static void checkSalaryPeriod (WebDriver driver, Date date) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		String periodId = "gwt-debug-periodLabel";
		String xpath = "//div[@id='" + periodId + "' and contains(text(), '" + getPeriodStr(date) + "')]";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	}
	
	public static void selectPayrollType (WebDriver driver, SALARY_TYPE type) {
		String boxId = "gwt-debug-typeListBox";
		retryingFindClick(driver, By.id(boxId));
		retryingFindClick(driver, By.cssSelector("option[value='" + type.name() + "']"));
	}
	
	private static String getPeriodStr(Date date) {
		DateFormat df = new SimpleDateFormat("d/M/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SeleniumTools.resetCalendar(calendar);
		String d1 = df.format(calendar.getTime());
		
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		String d2 = df.format(calendar.getTime());
		
		return d1 + " - " + d2;
	}
	
	public static void draft(WebDriver driver, String employee) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		By xpath = By.xpath("//tr [.//div[contains(text(), '"+employee+"')]] //td[1]");
		wait.until(ExpectedConditions.elementToBeClickable(xpath));
		retryingFindClick(driver, xpath);
		String xpathStr = "//div[./table//div[contains(text(), '"+employee+"')]] //div[contains(@id, '-draft-content')]";
		xpath = By.xpath(xpathStr);
		wait.until(ExpectedConditions.elementToBeClickable(xpath));
		retryingFindClick(driver, xpath);
		Thread.sleep(200);
		retryingFindClick(driver, xpath);
	}

	public static void integralFromIndex(WebDriver driver) throws InterruptedException {
		log(CLICK, "Entering \"Laboral\"");
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		Thread.sleep(1000);
		log(CLICK, "Entering \"Integral de Nóminas\"");
		retryingFindClick(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
	}
	
	public static void openWorkplace(WebDriver driver, String workplaceId) throws InterruptedException {
		By selector = By.cssSelector("#" + workplaceId + " > table > tbody > tr > td:nth-of-type(1)");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(selector));
		Thread.sleep(500);
		log(CLICK, "Deploying \"WORKPLACE\"");
		retryingFindClick(driver, selector);
	}
	
	public static void delay (WebDriver driver, Date startDate, Date endDate) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		SeleniumTools.selectPayrollType(driver, SALARY_TYPE.DELAY);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-fromMonthListBox")));
		Thread.sleep(500);
		
		Calendar calendar = Calendar.getInstance();
		
		SeleniumTools.selectFromMonthScrollingV2(driver, startDate);
		
		calendar.setTime(startDate);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH) + 1;
		
		SeleniumTools.selectMonthScrollingV2(driver, endDate);
		Thread.sleep(500);
		calendar.setTime(endDate);
		int endYear = calendar.get(Calendar.YEAR);
		int endMonth = calendar.get(Calendar.MONTH) + 1;
		
		String xpathMatch1 = "/"+startMonth+"/"+startYear;
		String xpathMatch2 = "/"+endMonth+"/"+endYear;
		
		String xpath = "//div[@id='gwt-debug-periodLabel' and contains(text(), '"+xpathMatch1+"') and contains(text(), '"+xpathMatch2+"')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		
		
		String regex = String.format( new Locale("es","ES"),"\\s*[0-9]+\\/%2$d\\/%1$d\\s*\\-\\s*[0-9]+\\/%4$d\\/%3$d\\s*", startYear, startMonth, endYear, endMonth);
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-periodLabel")));
		
		String period = element.getAttribute("innerText");
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(period);
		if (!matcher.matches())
			Assert.fail("Pediod ("+period+") does not math with selection");
		
	}
	
	public static void safeInput (WebDriver driver, String cssSelector, String text) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
		String value = "";
		int attempts = 0;
		do {
			try {
				retryingFindClick(driver, By.cssSelector(cssSelector));
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("document.querySelector('"+cssSelector+"').value = ''");
				input.sendKeys(text);
				value = input.getAttribute("value");
			} catch (StaleElementReferenceException e) {
				input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
			} finally {
				attempts++;
			}
		}while (attempts < 10 && !value.equals(text));
	}
	
	public static boolean changingElementAssert (WebDriver driver, By selector, String attribute, String text) throws Exception {
		int attempts = 0;
		do {
			String attr = SeleniumTools.getAttribute(driver, By.id("gwt-debug-description-box-3"), attribute);
			if (attr.equals(text))
				return true;
			attempts++;
		} while (attempts < 10);
		return false;
	}
}
