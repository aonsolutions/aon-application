package solutions.aon.selenium.solutions;

import java.net.URL;

import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import solutions.aon.selenium.AbstractTestCase;
import solutions.aon.selenium.solutions.TimeControlTestCase;
@Ignore
public class SolutionsBaseTestCase extends AbstractTestCase{


	protected static void login(WebDriver driver) {
		driver.get(getUrl());
		
		WebElement aonLoginUserInput = driver.findElement(By.id("aonLoginUserInput"));
		aonLoginUserInput.sendKeys(getUser());
		WebElement aonLoginPasswordInput = driver.findElement(By.id("aonLoginPasswordInput"));
		aonLoginPasswordInput.sendKeys(getPassword());
		
		WebElement aonLoginSignin = driver.findElement(By.id("aonLoginSignin"));
		aonLoginSignin.click();
		
	}

	protected static void logout(WebDriver driver) {
		WebElement aonHeaderUserButton = driver.findElement(By.id("aonHeaderUserButton"));
		aonHeaderUserButton.click();
		//*[@id="aonHeaderDialogUserOptionDialogMenuContent"]/ul/li[3]/span
	}

}