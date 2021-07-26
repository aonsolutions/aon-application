package solutions.aon.selenium.aio;

import static org.junit.Assert.fail;
import static solutions.aon.selenium.tools.Logger.log;
import static solutions.aon.selenium.tools.Logger.Status.CLICK;
import static solutions.aon.selenium.tools.Logger.Status.INPUT;

import java.util.Calendar;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.AbstractTestCase;
import solutions.aon.selenium.tools.Logger;
import solutions.aon.selenium.tools.Logger.Status;
import solutions.aon.selenium.tools.SeleniumTools;

public class AioBaseTestCase extends AbstractTestCase{
	
	protected static WebDriver driver;
	protected static WebDriverWait wait;
	
	protected static final String GWT_ID_PROFIX = "gwt-debug-";

	protected static final String GENERAL = "RÉGIMEN GENERAL";
	protected static final String HOME = "EMPLEADOS DE HOGAR";
	protected static final String TRAINNING = "FORMACIÓN Y APRENDIZAJE";

	protected static void login(WebDriver driver, String section) {
		
		Logger.start("AON AIO - LOGIN");
		log(Status.CONNECT, "URL", getUrl());
		driver.get(getUrl());
		
		WebElement jUserName = driver.findElement(By.name("j_username"));
		jUserName.sendKeys(getUser());
		log(INPUT, "Seting user", getUser());
		
		WebElement jPassword = driver.findElement(By.name("j_password"));
		jPassword.sendKeys(getPassword());
		log(INPUT, "Seting user", getPassword().replaceAll(".", "*"));
		
		WebElement loginBtn = driver.findElement(By.name("login_btn"));
		loginBtn.click();
		log(CLICK, "Login button.");
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		if (section != null) {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='aon-outputText' and contains(text(), '" + section + "')]")));
			
			SeleniumTools.clickUntilNotExists(driver, By.xpath("//span[@class='aon-outputText' and contains(text(), '" + section + "')]"));

			
			if (!checkIfEntered(driver, section))
				fail("Didn't enter "+ section);

		} else {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='aon-outputText' and contains(text(), 'RÉGIMEN GENERAL')]")));
		}
	}
	
	private static boolean checkIfEntered(WebDriver driver, String text) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		return wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("span.aon-header-domain-title"), text));
	}

	protected static void logout(WebDriver driver) {
		WebElement headerOptionsFormLogout = driver.findElement(By.cssSelector("a[id='headerOptionsForm:logout']"));
		headerOptionsFormLogout.click();
	}
	
	protected static String getUrl() {
//		return System.getProperty("url", "https://general-payroll-test.aonsolutions.org/");
		return System.getProperty("url", "http://payroll-test.aonsolutions.org:8080/aon-aio/");
	}

	protected static String getUser() {
		return System.getProperty("user", "admin");
	}

	protected static String getPassword() {
		return System.getProperty("password", "org");
	}
	
	protected static void switchSalaryMonth(WebDriver driver, Calendar calendar) throws Exception {
		SeleniumTools.selectMonthScrolling(driver, calendar.getTime());
		SeleniumTools.checkSalaryPeriod(driver, calendar.getTime());
	}
	
	protected static void switchSalaryMonthMatchingMonth(WebDriver driver, Calendar calendar) throws Exception {
		SeleniumTools.selectMonthScrolling(driver, calendar.getTime());
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		
		String regex = String.format("\\s*\\d+\\/%1$d\\/%2$d\\s*-\\s*\\d+\\/%1$d\\/%2$d\\s*", month, year);
		
		new WebDriverWait(driver, 10).until(ExpectedConditions.textMatches(By.id(GWT_ID_PROFIX + "periodLabel"), Pattern.compile(regex)));
	}
	
}