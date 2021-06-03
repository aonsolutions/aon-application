package solutions.aon.selenium.aio;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.AbstractTestCase;
import solutions.aon.selenium.solutions.TimeControlTestCase;

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
		WebElement headerOptionsFormLogout = driver.findElement(By.id("headerOptionsForm:logout"));
		headerOptionsFormLogout.click();
	}
	
	
}