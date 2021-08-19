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
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
	
	public static enum SalaryType {
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
	
	/**
	 * Method to get elements clicked without random 'StaleElementReferenceException' exceptions
	 * @param driver The WebDriver
	 * @param by The selector
	 * @return returns if element's been eventually clicked or failed
	 */
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
	/**
	 * Right clicks the element of the selector
	 * @param driver The WebDriver
	 * @param by The selector
	 */
	public static void rightClick(WebDriver driver, By by) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		Actions actions = new Actions(driver)
				.contextClick(wait.until(ExpectedConditions.elementToBeClickable(by)));
		Action action = actions.build();
		action.perform();
	}
	
	/**
	 * Double clicks the element of the selector
	 * @param driver The WebDriver
	 * @param by The selector
	 */
	public static void doubleClick(WebDriver driver, By by) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		Actions actions = new Actions(driver)
				.doubleClick(wait.until(ExpectedConditions.elementToBeClickable(by)));
		Action action = actions.build();
		action.perform();
	}
	
	/**
	 * Clicks an element with a 10s explicit wait
	 * @param driver The WebDriver
	 * @param by The selector
	 */
	public static void click(WebDriver driver, By by) {
		waitNClick(driver,by, 10);
	}
	/**
	 * Clicks an element with the desired WebDriverWait object
	 * @param driver The WebDriver
	 * @param wait The explicit wait
	 * @param by The selector
	 */
	public static void click(WebDriver driver, WebDriverWait wait, By by) {
		wait.until(ExpectedConditions.elementToBeClickable(by));
		retryingFindClick(driver, by);
	}
	
	/**
	 * Clicks an element with a custom timeout
	 * @param driver The WebDriverWait
	 * @param by The selector
	 * @param customTimeout The custom timeout to wait for the wished element
	 */
	public static void waitNClick(WebDriver driver, By by, int customTimeout) {
		WebDriverWait wait = new WebDriverWait(driver, customTimeout);
		wait.until(ExpectedConditions.elementToBeClickable(by));
		retryingFindClick(driver, by);
	}
	
	/**
	 * Returns system's default download path (only if the system language is set in english or spanish)
	 * @return System's default download path
	 */
	public static String getDownloadPath() {
		String downloadFolder = System.getProperty("user.home") + File.separator + "Descargas";
		
		Path downloadPath = Paths.get(downloadFolder);
		
		if (!Files.exists(downloadPath))
			downloadFolder = System.getProperty("user.home") + File.separator + "Downloads";
		
		return downloadFolder;
	}
	
	/**
	 * Clicks an element until it throws an exception
	 * @param elem The WebElement to click
	 * @return Returns whether the element's been eventually clicked
	 */
	public static boolean clickUntilNotExists(WebElement elem) {
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
	
	/**
	 * Clicks an element until it throws an exception
	 * @param driver The WebDriver
	 * @param by The selector
	 * @return Returns whether the element's been eventually clicked
	 */
	public static boolean clickUntilNotExists(WebDriver driver, By by) {
	    boolean result = false;
	    int attempts = 0;
	    WebElement elem = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(by));
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
	
	/**
	 * Compares two Doubles (e.g. the one extracted from an element and the expected one), rounding the expected one if necessary
	 * @param actualAmount The real amount (e.g. the one extracted from an element)
	 * @param expectedAmount The expected amount
	 * @return Returns if two numbers are the same
	 */
	public static boolean checkAmount(Double actualAmount, Double expectedAmount) {
		if (actualAmount == expectedAmount)
			return true;
		else if (actualAmount == null || expectedAmount == null)
			return false;
		else {
			expectedAmount = Math.round(expectedAmount*100.0) / 100.0;
			return actualAmount.equals(expectedAmount);			
		}
	}
	
	/**
	 * Extracts the amount from the chosen field. The field may contain the number in different formats: <br/>
	 * <ul>
	 * <li>0.000,00</li>
	 * <li>00,00 %</li>
	 * </ul>
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @return The Double after parsing the text or null if incompatible or no value found
	 */
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
	
	/**
	 * Extracts the amount from the chosen field, only if the amount is placed in the 'value' attribute and it's not empty.
	 * The field may contain the number in different formats:
	 * <ul>
	 * <li>0.000,00</li>
	 * <li>00,00 %</li>
	 * </ul>
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @return The Double after parsing the text or null if incompatible or no value found
	 */
	public static Double getAmountNotEmptyValue(WebDriver driver, By selector) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement baseSalaryAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		wait.until(ExpectedConditions.attributeToBeNotEmpty(baseSalaryAmount, "value"));
		
		String baseSalaryStr = baseSalaryAmount.getAttribute("value");
		
		Double baseSalary = null;
		try {
			baseSalaryStr = baseSalaryStr.replaceAll("\\.", "").replaceAll(",", ".").replaceAll("%", "").trim();
			baseSalary = Double.parseDouble(baseSalaryStr);
			
			return baseSalary;
			
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
		
	}
	
	/**
	 * Extracts the amount from the chosen field, only if the amount is placed in the 'innerText' attribute and it's not empty.
	 * The field may contain the number in different formats:
	 * <ul>
	 * <li>0.000,00</li>
	 * <li>00,00 %</li>
	 * </ul>
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @return The Double after parsing the text or null if incompatible or no value found
	 */
	public static Double getAmountNotEmptyText(WebDriver driver, By selector) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		WebElement baseSalaryAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		wait.until(ExpectedConditions.attributeToBeNotEmpty(baseSalaryAmount, "innerText"));
		
		String baseSalaryStr = baseSalaryAmount.getText();
		
		Double baseSalary = null;
		try {
			baseSalaryStr = baseSalaryStr.replaceAll("\\.", "").replaceAll(",", ".").replaceAll("%", "").trim();
			baseSalary = Double.parseDouble(baseSalaryStr);
			
			return baseSalary;
			
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
		
	}

	/**
	 * Waits (max 10s) until the chosen element's attribute (passed as parameter) is equal to the expected value
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @param attrName The name of the chosen attribute
	 * @param expected The expected value for the attribute
	 * @return Whether the attribute matches the extected value
	 */
	public static boolean waitAndCheckAmount(WebDriver driver, By selector, String attrName, Double expected) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try {
			wait.until(d -> {
				String str = d.findElement(selector).getAttribute(attrName);
				if (expected == null && str == null)
					return true;
				else {
					str = str.replaceAll("\\.", "").replace(",", ".");
					Double actual = Double.parseDouble(str);
					return actual.equals(expected);
				}
			});			
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}
	/**
	 * Rounds a double to 2 decimals
	 * @param number The Double to round
	 * @return The Double rounded
	 */
	public static Double unmessDouble(Double number) {
		if (number != null)
			return Math.round(number * 100.0) / 100.0;
		return null;
	}
	
	/**
	 * Returns the difference in months between two dates (absolute integer value). Only considers month and year of the given dates.
	 * @param d1 The first date
	 * @param d2 The second date
	 * @return The difference in months between the two given dates
	 */
	public static int differenceInMonths(Date d1, Date d2) {
	    Calendar c1 = Calendar.getInstance();
	    c1.setTime(d1);
	    cleanCalendar(c1);
	    
	    Calendar c2 = Calendar.getInstance();
	    c2.setTime(d2);
	    cleanCalendar(c2);
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
	
	/**
	 * Resets the given calendar's fields except for month and year
	 * @param c The calendar object
	 */
	public static void cleanCalendar (Calendar c) {
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
	}
	
	/**
	 * Selects the start date from the dropdown menu when there are two dates to choose
	 * @param driver The WebDriver
	 * @param date The wished start date
	 * @throws Exception
	 */
	public static void selectFromMonthScrolling (WebDriver driver, Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' yyyy", new Locale("es", "ES"));
		String displayedDateId = "gwt-debug-fromMonthListBox-item0";
		int att = 0;
		do {
			selectMonthScrollingCommon (driver, date, "gwt-debug-fromMonthListBox", null);
		} while (!changingElementAssert(driver, By.id(displayedDateId), "innerText", df.format(date)) && att++ < 10);
		
	}
	
	/**
	 * Selects the date from the dropdown menu
	 * @param driver The WebDriver
	 * @param date The wished date
	 * @throws Exception
	 */
	public static void selectMonthScrolling (WebDriver driver, Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' yyyy", new Locale("es", "ES"));
		String displayedDateId = "gwt-debug-monthListBox-item0";
		int att = 0;
		do {
			
			selectMonthScrollingCommon (driver, date, "gwt-debug-monthListBox", null);
		} while (!changingElementAssert(driver, By.id(displayedDateId), "innerText", df.format(date)) && att++ < 10);
	}
	
	/**
	 * Selects the start date from the dropdown menu when delay is selected
	 * @param driver The WebDriver
	 * @param date The wished start date
	 * @throws Exception
	 */
	public static void selectFromMonthScrollingDelay (WebDriver driver, Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' yyyy", new Locale("es", "ES"));
		String displayedDateId = "gwt-debug-fromMonthListBox-item0";
		int att = 0;
		do {
			selectMonthScrollingCommon(driver, date, "gwt-debug-fromMonthListBox", SalaryType.DELAY);
		} while (!changingElementAssert(driver, By.id(displayedDateId), "innerText", df.format(date)) && att++ < 10);
		
	}
	
	/**
	 * Selects the end date from the dropdown menu when delay is selected
	 * @param driver The WebDriver
	 * @param date The wished end date
	 * @throws Exception
	 */
	public static void selectMonthScrollingDelay (WebDriver driver, Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' yyyy", new Locale("es", "ES"));
		String displayedDateId = "gwt-debug-monthListBox-item0";
		int att = 0;
		do {
			
			selectMonthScrollingCommon (driver, date, "gwt-debug-monthListBox", SalaryType.DELAY);
		} while (!changingElementAssert(driver, By.id(displayedDateId), "innerText", df.format(date)) && att++ < 10);
	}
	
	/**
	 * Selects the date from the dropdown menu when settle is selected
	 * @param driver The WebDriver
	 * @param date The wished date
	 * @throws Exception
	 */
	public static void selectMonthScrollingSettle (WebDriver driver, Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("d 'de' MMMMMMMMMM 'de' yyyy", new Locale("es", "ES"));
		String displayedDateId = "gwt-debug-dateListBox-item0";
		int att = 0;
		do {
			
			selectMonthScrollingSettlement (driver, date);
		} while (!changingElementAssert(driver, By.id(displayedDateId), "innerText", df.format(date)) && att++ < 10);
	}
	
	/**
	 * Method to get the selected attribute from an element
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @param attributeName The wished attribute name
	 * @return The attribute value
	 * @throws Exception
	 */
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
	
	/**
	 * Method to get the selected attribute from an element if this is not empty
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @param attributeName The wished attribute name
	 * @return The attribute value
	 * @throws Exception
	 */
	public static String getAttributeNotEmpty(WebDriver driver, By selector, String attributeName) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
		String attribute = null;
		int attempts = 0;
		do {
			try {
				wait.until(ExpectedConditions.attributeToBeNotEmpty(elem, attributeName));
				attribute = elem.getAttribute(attributeName);
				return attribute;
			} catch (StaleElementReferenceException e) {
				elem = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
			}
			attempts++;
		} while (attempts < 10);
		throw new Exception("Unable to get the attribute");
	}
	
	private static void selectMonthScrollingCommon (WebDriver driver, Date date, String boxId, SalaryType type) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 7);
		String formatStr = "MMMMMMMMMM 'de' yyyy";
		if (type == SalaryType.SETTLE)
			formatStr = "d 'de' MMMMMMMMMM 'de' yyyy";
		
		DateFormat df = new SimpleDateFormat(formatStr, new Locale("es", "ES"));
		
		String firstWea = "#" + boxId + "-celllist > div:first-child > div:first-child > span";
		String dateStr = "";
		
		click(driver, wait, By.id(boxId));
		dateStr = getAttributeNotEmpty(driver, By.cssSelector(firstWea), "innerText");
		Date thatDay = df.parse(dateStr);
		
		if (type==SalaryType.DELAY)
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-fromMonthListBox-item0")));
		
		WebElement monthPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#" + boxId + "-popup > div > div")));
		
		String xpath = "//div[@id='" + boxId + "-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+dateStr+"')]/..";

		String idx = getAttributeNotEmpty(driver, By.xpath(xpath), "__idx");

		int idxNum = Integer.parseInt(idx);
		Keys key = (thatDay.getTime() > date.getTime()) ? Keys.PAGE_UP : Keys.PAGE_DOWN;
		Keys oppositeKey = (thatDay.getTime() > date.getTime()) ? Keys.PAGE_DOWN : Keys.PAGE_UP;
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Math.abs(date.getTime()-thatDay.getTime()));
		int diff = SeleniumTools.differenceInMonths(thatDay,date);
		
		idxNum += (date.getTime() < thatDay.getTime()) ? -diff : diff;
		
		String firstIdx = (thatDay.getTime() > date.getTime()) ?
				getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:nth-child(1)"), "__idx")
					:
				getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:last-child"), "__idx");
		int firstIdxNum = Integer.parseInt(firstIdx);
		int[] first = new int[2];
		first[0] = firstIdxNum;
		first[1] = 0;
		if (thatDay.getTime() > date.getTime()) {
			while (firstIdxNum > idxNum) {
				monthPopup.sendKeys(key);
				try {
					firstIdx = getAttributeNotEmpty(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:nth-child(1)"), "__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
					if(first[0] == firstIdxNum) {
						if(++first[1] > 10) {
							monthPopup.sendKeys(oppositeKey);
							Thread.sleep(700);
							monthPopup.sendKeys(key);
							Thread.sleep(700);
							monthPopup.sendKeys(key);	
						}
					}
					else {
						first[0] = firstIdxNum;
						first[1] = 0;
					}	
					
				} catch (StaleElementReferenceException e) {}
			}
		} else {
			while (firstIdxNum < idxNum) {
				monthPopup.sendKeys(key);
				try {
					firstIdx = getAttribute(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:last-child"), "__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
				} catch (StaleElementReferenceException e) {}
			}
		}
		
		xpath = "//div[@id='" + boxId + "-celllist'] //span[@class='aon-nowrap ' and text()='"+df.format(date)+"']";
		By xpathSel = By.xpath(xpath);
		click(driver, wait, xpathSel);
	}

	private static void selectMonthScrollingSettlement (WebDriver driver, Date date) throws Exception {
		String boxId = "gwt-debug-dateListBox";
		WebDriverWait wait = new WebDriverWait(driver, 4);
		String formatStr = "d 'de' MMMMMMMMMM 'de' yyyy";
		
		DateFormat df = new SimpleDateFormat(formatStr, new Locale("es", "ES"));
		
		click(driver, wait, By.id(boxId));
		
		By popupSelector = By.cssSelector("#" + boxId + "-popup > div > div");
		
		WebElement monthPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(popupSelector));
		
		SeleniumTools.focus(driver, "#gwt-debug-dateListBox-popup > div > div");
		
		String dateStr = SeleniumTools.getAttribute(driver, By.cssSelector("#gwt-debug-dateListBox-popup > div > div > div > div > div:nth-of-type(1) > div:nth-of-type(1)"), "innerText");
		
		
		String xpath = "//div[@id='" + boxId + "-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+dateStr+"')]/..";
		
		
		String idx = getAttribute(driver, By.xpath(xpath), "__idx");
		int idxNum = Integer.parseInt(idx);
		
		Date thatDay = df.parse(dateStr);
		
		long diffMilis = Math.abs(thatDay.getTime() - date.getTime());
		long diff = diffMilis / (1000*60*60*24);
		
		Keys key = (thatDay.getTime() > date.getTime()) ? Keys.PAGE_UP : Keys.PAGE_DOWN;
		Keys oppositeKey = (thatDay.getTime() > date.getTime()) ? Keys.PAGE_DOWN : Keys.PAGE_UP;
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Math.abs(date.getTime()-thatDay.getTime()));
		
		idxNum += (date.getTime() < thatDay.getTime()) ? -diff : diff;
		
		String firstIdx = (thatDay.getTime() > date.getTime()) ?
				getAttribute(driver, By.cssSelector("#gwt-debug-dateListBox-popup > div > div > div > div > div:nth-of-type(1) > div:nth-of-type(1)"), "__idx")
					:
				getAttribute(driver, By.cssSelector("#gwt-debug-dateListBox-popup > div > div > div > div > div:nth-of-type(1) > div:last-child"), "__idx");
		int firstIdxNum = Integer.parseInt(firstIdx);
		int[] first = new int[2];
		first[0] = firstIdxNum;
		first[1] = 0;
		if (thatDay.getTime() > date.getTime()) {
			while (firstIdxNum > idxNum) {
				monthPopup.sendKeys(key);
				try {
					firstIdx = getAttributeNotEmpty(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:nth-child(1)"), "__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
					if(first[0] == firstIdxNum) {
						if(++first[1] > 10) {
							monthPopup.sendKeys(oppositeKey);
							Thread.sleep(700);
							monthPopup.sendKeys(key);
							Thread.sleep(700);
							monthPopup.sendKeys(key);	
						}
					}
					else {
						first[0] = firstIdxNum;
						first[1] = 0;
					}	
					
				} catch (StaleElementReferenceException e) {}
			}
		} else {
			while (firstIdxNum < idxNum) {
				monthPopup.sendKeys(key);
				try {
					
					firstIdx = getAttributeNotEmpty(driver, By.cssSelector("#" + boxId + "-celllist > div:nth-child(1) > div:last-child"), "__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
				} catch (StaleElementReferenceException e) {}
			}
		}
		
		xpath = "//div[@id='" + boxId + "-celllist'] //span[@class='aon-nowrap ' and text()='"+df.format(date)+"']";
//		By cssSel = By.cssSelector("*[__idx='" + idxNum + "']");
		By xpathSel = By.xpath(xpath);
		click(driver, wait, xpathSel);
	}
	
	/**
	 * Checks if the date text has changed according to the chosen date in salaries
	 * @param driver The WebDriver
	 * @param date The expected date
	 */
	public static void checkSalaryPeriod (WebDriver driver, Date date) {
		String xpath = "";
		WebDriverWait wait = new WebDriverWait(driver, 10);
		String periodId = "gwt-debug-periodLabel";
		wait.until(ExpectedConditions.attributeContains(By.id(periodId), "innerText", getPeriodStr(date)));
		xpath = "//div[@id='" + periodId + "' and contains(text(), '" + getPeriodStr(date) + "')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
	}
	
	/**
	 * Checks if the date text has changed according to the chosen date in settlements
	 * @param driver The WebDriver
	 * @param date The expected date
	 */
	public static void checkSettlePeriod (WebDriver driver, Date date) {
		String xpath = "";
		WebDriverWait wait = new WebDriverWait(driver, 10);
		String periodId = "gwt-debug-periodLabel";
		String formatStr = "d/M/yyyy";
		DateFormat df = new SimpleDateFormat(formatStr);
		
		xpath = "//div[@id='" + periodId + "' and contains(text(), '" + df.format(date) + "')]";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	}
	
	/**
	 * Chooses the payroll type
	 * @param driver The WebDriver
	 * @param type The payroll type from a enum
	 */
	public static void selectPayrollType (WebDriver driver, SalaryType type) {
		String boxId = "gwt-debug-typeListBox";
		retryingFindClick(driver, By.id(boxId));
		retryingFindClick(driver, By.cssSelector("option[value='" + type.name() + "']"));
	}
	
	private static String getPeriodStr(Date date) {
		DateFormat df = new SimpleDateFormat("d/M/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SeleniumTools.cleanCalendar(calendar);
		String d1 = df.format(calendar.getTime());
		
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		String d2 = df.format(calendar.getTime());
		
		return d1 + " - " + d2;
	}
	
	/**
	 * Selects a draft when it's displayed
	 * @param driver The WebDriver
	 * @param employee The name of the employee
	 * @throws InterruptedException
	 */
	public static void draft(WebDriver driver, String employee) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		By xpath = By.xpath("//tr [.//div[contains(text(), '"+employee+"')]] //td[1]");
		click(driver, wait, xpath);
		String xpathStr = "//div[./table//div[contains(text(), '"+employee+"')]] //div[contains(@id, '-draft-content')]";
		xpath = By.xpath(xpathStr);
		click(driver, wait, xpath);
		try {
			wait.until(ExpectedConditions.attributeContains(By.id("gwt-debug-employeeNameLabel"), "innerText", employee));
		} catch (Exception e) {
			retryingFindClick(driver, xpath);
		}
	}

	/**
	 * Enters 'integral de nóminas' from index page
	 * @param driver The WebDriver
	 * @throws InterruptedException
	 */
	public static void integralFromIndex(WebDriver driver) throws InterruptedException {
		log(CLICK, "Entering \"Laboral\"");
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		log(CLICK, "Entering \"Integral de Nóminas\"");
		click(driver, By.cssSelector("*[id='aonContent:payrollMenu:gwt_employee']"));
	}
	
	/**
	 * Enters 'convenios' from index page when there isn't chosen any enterprise
	 * @param driver
	 * @throws InterruptedException
	 */
	public static void mainAgreementFromIndex(WebDriver driver) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		log(CLICK, "Entering \"Laboral\"");
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		log(CLICK, "Entering \"Convenios\"");
		click(driver, wait, By.cssSelector("*[id='aonContent:payrollMenu:gwt_agreement2']"));
	}
	
	/**
	 * Enters 'convenios' from index page
	 * @param driver
	 * @throws InterruptedException
	 */
	public static void generalAgreementFromIndex(WebDriver driver) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		log(CLICK, "Entering \"Laboral\"");
		retryingFindClick(driver, By.cssSelector("a[id='aonContent:mainMenuForm:menu_payroll']"));
		log(CLICK, "Entering \"Convenios\"");
		click(driver, wait, By.cssSelector("*[id='aonContent:payrollMenu:gwt_agreement']"));
	}
	
	/**
	 * Opens a workplace on integral
	 * @param driver The WebDriver
	 * @param workplaceId The 'id' attribute of the workplace
	 * @throws InterruptedException
	 */
	public static void openWorkplace(WebDriver driver, String workplaceId) throws InterruptedException {
		By selector = By.cssSelector("*[id='" + workplaceId + "'] > table > tbody > tr > td:nth-of-type(1)");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		log(CLICK, "Deploying \"WORKPLACE\"");
		click(driver, wait, selector);
	}
	
	/**
	 * Opens a draft
	 * @param driver The WebDriver
	 * @param startDate The start date
	 * @param endDate The end date
	 * @throws Exception
	 */
	public static void delay (WebDriver driver, Date startDate, Date endDate) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		SeleniumTools.selectPayrollType(driver, SalaryType.DELAY);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gwt-debug-fromMonthListBox")));
		
		Calendar calendar = Calendar.getInstance();
		
		SeleniumTools.selectFromMonthScrollingDelay(driver, startDate);
		
		calendar.setTime(startDate);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH) + 1;
		SeleniumTools.selectMonthScrollingDelay(driver, endDate);
		calendar.setTime(endDate);
		int endYear = calendar.get(Calendar.YEAR);
		int endMonth = calendar.get(Calendar.MONTH) + 1;
		
		String xpathMatch1 = "/"+startMonth+"/"+startYear;
		String xpathMatch2 = "/"+endMonth+"/"+endYear;
		
		String xpath = "//div[@id='gwt-debug-periodLabel' and contains(text(), '"+xpathMatch1+"') and contains(text(), '"+xpathMatch2+"')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		
		
		String regex = String.format( new Locale("es","ES"),"\\s*[0-9]+\\/%2$d\\/%1$d\\s*\\-\\s*[0-9]+\\/%4$d\\/%3$d\\s*", startYear, startMonth, endYear, endMonth);
		
		
		Pattern pattern = Pattern.compile(regex);
		
		try {
			if (!wait.until(ExpectedConditions.textMatches(By.id("gwt-debug-periodLabel"), pattern))) {
				Assert.fail("Period (" + getAttribute(driver, By.id("gwt-debug-periodLabel"), "innerText") + ") does not math with selection");
			}
			
		} catch (Exception e) {
			Assert.fail("Period does not math with selection");
		}
	}
	
	/**
	 * Safely inputs text on textboxes, removes the existing content first
	 * @param driver The WebDriver
	 * @param cssSelector The query selector
	 * @param text The text to input
	 */
	public static void safeInput (WebDriver driver, String cssSelector, String text) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
		String value = "";
		int attempts = 0;
		do {
			try {
				focus(driver, cssSelector);
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
	
	/**
	 * Checks if an element which has been refreshed has changed the selected attribute to a determinate value
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @param attribute The attribute
	 * @param text The expected text contained on the attribute
	 * @return Returns whether the value matches
	 * @throws Exception
	 */
	public static boolean changingElementAssert (WebDriver driver, By selector, String attribute, String text) throws Exception {	
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try {
		return wait.until(ExpectedConditions.attributeContains(selector, attribute, text));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Saves a draft
	 * @param driver The WebDriver
	 * @throws Exception
	 */
	public static void acceptDraft (WebDriver driver) throws Exception {
		boolean checkAcceptButton = false;
		int att = 0;
		while (!checkAcceptButton && att < 10) {
			retryingFindClick(driver, By.id("gwt-debug-acceptButton"));
			checkAcceptButton = changingElementAssert(driver, By.id("gwt-debug-acceptButton"), "disabled", "true");
			att++;
		}
		if (!checkAcceptButton)
			throw new Exception("Could not click on save button");
	}
	
	/**
	 * Searches an employee
	 * @param driver The WebDriver
	 * @param searchText The employee name to be found
	 */
	public static void search(WebDriver driver, String searchText) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		By searchBoxSelector = By.id("gwt-debug-searchTextBox");
		click(driver, wait, searchBoxSelector);
		safeInput(driver, "#gwt-debug-searchTextBox", searchText);
	}
	
	/**
	 * Focuses an element via JavaScript
	 * @param driver The WebDriver
	 * @param cssSelector The query selector
	 */
	public static void focus(WebDriver driver, String cssSelector) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.querySelector('" + cssSelector + "').focus()");
	}
	
	/**
	 * Clicks an element until this one's value contains a text
	 * @param driver The WebDriver
	 * @param selector The selector
	 * @param containingText The text which must be contained into the element
	 */
	public static void focusUntilValueContains (WebDriver driver, By selector, String containingText) {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		boolean focused;
		int max = 4;
		do {
			SeleniumTools.retryingFindClick(driver, selector);
			try {				
				wait.until(ExpectedConditions.attributeContains(selector, "value", containingText));
				focused = true;
			} catch (Exception e) {
				focused = false;
			}
		} while (max-- > 0 && !focused);
	}
	
	/**
	 * Waits until period dropdown has been properly loaded
	 * @param driver The WebDriver
	 */
	public static void wait4periodStabilization(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(d -> {
			DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' yyyy");
			String currentDateStr = df.format(new Date());
			String dateStr = d.findElement(By.id("gwt-debug-monthListBox-item0")).getText();
			Date selected;
			try {
				selected = df.parse(dateStr);
				Calendar c = Calendar.getInstance();
				c.setTime(selected);
				String period = d.findElement(By.id("gwt-debug-periodLabel")).getText();
				return period.contains((c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)) && dateStr.contains(currentDateStr);
			} catch (ParseException e) {
				return false;
			}
		});
	}
	
	/**
	 * Clicks the exit button on modal window until this one disappears
	 * @param driver The WebDriver
	 * @param exitBtnToClick The exit button to click
	 */
	public static void safelyCloseModal (WebDriver driver, By exitBtnToClick) {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		int max = 4;
		boolean modalClosed;
		do {			
			SeleniumTools.retryingFindClick(driver, exitBtnToClick);
			try {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(exitBtnToClick));
				modalClosed = true;	
			} catch (Exception e) {
				modalClosed = false;
			}
		} while (!modalClosed && max-- > 0);
	}
	
	/**
	 * Waits until settle's displayed date is loaded after choosing months
	 * @param driver The WebDriver
	 */
	public static void wait4SettleToLoadDate(WebDriver driver) {
		new WebDriverWait(driver, 10).until(d -> {
			Pattern pattern = Pattern.compile("\\s*\\d+\\s*de\\s*\\w+\\s*de\\s*\\d+\\s*", Pattern.CASE_INSENSITIVE);
			String date = d.findElement(By.id("gwt-debug-dateListBox-item0")).getText();
			return pattern.matcher(date).matches();
		});
	}
	
	private static void checkboxCheckUncheck(WebDriver driver, By checkboxId, boolean checked) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		wait.until(ExpectedConditions.elementToBeClickable(checkboxId));
		
		int max = 4;
		boolean status;
		
		ExpectedCondition<Boolean> condition;
		if (checked)
			condition = ExpectedConditions.elementToBeSelected(checkboxId);
		else
			condition = ExpectedConditions.not(ExpectedConditions.elementToBeSelected(checkboxId));
		
		do {
			retryingFindClick(driver, checkboxId);
			try {
				wait.until(condition);
				status = true;				
			} catch (Exception e) {
				status = false;
			}				
		} while (!status && max-- >0);
	}
	
	/**
	 * Checks a checkbox
	 * @param driver The WebDriver
	 * @param checkboxId The selector
	 */
	public static void checkboxCheck(WebDriver driver, By checkboxId) {
		checkboxCheckUncheck(driver, checkboxId, true);
	}
	
	/**
	 * Unchecks a checkbox
	 * @param driver The WebDriver
	 * @param checkboxId The selector
	 */
	public static void checkboxUncheck(WebDriver driver, By checkboxId) {
		checkboxCheckUncheck(driver, checkboxId, false);
	}
	
	/**
	 * Search an agreement
	 * @param driver The WebDriver
	 * @param agreement The agreement name
	 */
	public static void agreementSearch (WebDriver driver, String agreement) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		String cssSel = "#gwt-debug-agreementsTreeToolbar > input";
		By input = By.cssSelector(cssSel );
		click(driver, wait, input);
		safeInput(driver, cssSel, agreement);
	}
	
	/**
	 * Searches an agreement and chooses it
	 * @param driver The WebDriver
	 * @param agreement The agreement name
	 */
	public static void searchAndEnterAgreement (WebDriver driver, String agreement) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		SeleniumTools.agreementSearch(driver, agreement);
		
		String xpath = "//div[contains(text(), '" + agreement + "')]";
		
		click(driver, wait, By.xpath(xpath));
	}
	
	public static Date firstMondayOfMonth(Date month) {
		Calendar calendar = Calendar.getInstance(new Locale("es",  "ES"));
		calendar.setTime(month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return calendar.getTime();
	}
}
