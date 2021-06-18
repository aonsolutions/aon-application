package solutions.aon.selenium.aio;

import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.AbstractTestCase;
import solutions.aon.selenium.tools.SeleniumTools;

public class AioBaseTestCase extends AbstractTestCase{


	protected static void login(WebDriver driver) {
		driver.get(getUrl());
		
		WebElement jUserName = driver.findElement(By.name("j_username"));
		jUserName.sendKeys(getUser());
		WebElement jPassword = driver.findElement(By.name("j_password"));
		jPassword.sendKeys(getPassword());
		
		WebElement loginBtn = driver.findElement(By.name("login_btn"));
		loginBtn.click();
		
		WebElement generalRegime = searchEnterprise(driver, "régimen general");
		
		SeleniumTools.clickUntilNotExists(driver, generalRegime);
		
		if (!checkIfEntered(driver, "RÉGIMEN GENERAL"))
			fail("Didn't enter 'RÉGIMEN GENERAL'");
		
	}
	
	private static boolean checkIfEntered(WebDriver driver, String text) {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.aon-header-domain-title")));
		return elem.getAttribute("innerText").trim().equalsIgnoreCase(text);
	}
	
	private static WebElement searchEnterprise(WebDriver driver, String string) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".aon-dataTable .rich-table-row")));
		
		System.out.println(elements.size());
		
		return elements.stream().filter(elem -> {
			WebElement nameElem = elem.findElement(By.cssSelector("td span.aon-outputText"));
			return nameElem.getAttribute("innerText").equalsIgnoreCase(string);
		}).findFirst().orElse(null);
		
		
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
	
}