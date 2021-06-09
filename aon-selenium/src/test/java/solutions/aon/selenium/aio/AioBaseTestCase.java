package solutions.aon.selenium.aio;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import solutions.aon.selenium.AbstractTestCase;

public class AioBaseTestCase extends AbstractTestCase{


	protected static void login(WebDriver driver) {
		driver.get(getUrl());
		
		WebElement jUserName = driver.findElement(By.name("j_username"));
		jUserName.sendKeys(getUser());
		WebElement jPassword = driver.findElement(By.name("j_password"));
		jPassword.sendKeys(getPassword());
		
		WebElement loginBtn = driver.findElement(By.name("login_btn"));
		loginBtn.click();
	}

	protected static void logout(WebDriver driver) {
		WebElement headerOptionsFormLogout = driver.findElement(By.cssSelector("a[id='headerOptionsForm:logout']"));
		headerOptionsFormLogout.click();
	}
	
	protected static String getUrl() {
		return System.getProperty("url", "https://general-payroll-test.aonsolutions.org/");
//		return System.getProperty("url", "https://payroll-test.aonsolutions.net/");
	}

	protected static String getUser() {
		return System.getProperty("user", "admin");
	}

	protected static String getPassword() {
		return System.getProperty("password", "org");
	}
	
}