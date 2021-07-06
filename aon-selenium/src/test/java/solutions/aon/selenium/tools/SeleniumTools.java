package solutions.aon.selenium.tools;

import static solutions.aon.selenium.tools.SeleniumTools.retryingFindClick;

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
	    Calendar c2 = Calendar.getInstance();
	    c2.setTime(d2);
	    int diff = 0;
	    if (c2.after(c1)) {
	        while (c2.after(c1)) {
	            c1.add(Calendar.MONTH, 1);
	            if (c2.after(c1)) {
	                diff++;
	            }
	        }
	    } else if (c2.before(c1)) {
	        while (c2.before(c1)) {
	            c1.add(Calendar.MONTH, -1);
	            if (c2.before(c1)) {
	                diff++;
	            }
	        }
	    }
	    return diff;
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
	
	public static void selectMonthScrollingV2 (WebDriver driver, Date date) throws InterruptedException, ParseException {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM 'de' YYYY", new Locale("es", "ES"));
		Date today = new Date();
		String dateStr = df.format(today);
		WebElement monthPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#gwt-debug-monthListBox-popup > div > div")));
		
		String xpath = "//div[@id='gwt-debug-monthListBox-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+dateStr+"')]/..";
		
		
		WebElement currentElem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		String idx = currentElem.getAttribute("__idx");
		int idxNum = Integer.parseInt(idx);
		Keys key = (today.getTime() > date.getTime()) ? Keys.PAGE_UP : Keys.PAGE_DOWN;
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Math.abs(date.getTime()-today.getTime()));
		int diff = SeleniumTools.differenceInMonths(today,date);
		
		idxNum += (date.getTime() < today.getTime()) ? -diff : diff;
		
		String firstIdx = (today.getTime() > date.getTime()) ?
				driver.findElement(By.cssSelector("#gwt-debug-monthListBox-celllist > div:nth-child(1) > div:nth-child(1)")).getAttribute("__idx")
				:
					driver.findElement(By.cssSelector("#gwt-debug-monthListBox-celllist > div:nth-child(1) > div:last-child")).getAttribute("__idx");
		int firstIdxNum = Integer.parseInt(firstIdx);
		
		if (today.getTime() > date.getTime()) {
			while (firstIdxNum > idxNum) {
				monthPopup.sendKeys(key);
//				Thread.sleep(250);
				try {
					firstIdx = driver.findElement(By.cssSelector("#gwt-debug-monthListBox-celllist > div:nth-child(1) > div:nth-child(1)")).getAttribute("__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
				} catch (StaleElementReferenceException e) {}
			}
		} else {
			while (firstIdxNum < idxNum) {
				monthPopup.sendKeys(key);
//				Thread.sleep(250);
				try {
					firstIdx = driver.findElement(By.cssSelector("#gwt-debug-monthListBox-celllist > div:nth-child(1) > div:last-child")).getAttribute("__idx");
					firstIdxNum = Integer.parseInt(firstIdx);
				} catch (StaleElementReferenceException e) {}
			}
		}
		
		monthPopup.sendKeys(key);
		Thread.sleep(250);
		monthPopup.sendKeys(key);
		
		xpath = "//div[@id='gwt-debug-monthListBox-celllist'] //span[@class='aon-nowrap ' and contains(text(), '"+df.format(date)+"')]";
		
		retryingFindClick(driver, By.xpath(xpath));
//		driver.findElement(By.cssSelector(""))
		
		
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

	
}
