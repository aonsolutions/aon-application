package solutions.aon.selenium.app;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.regex.Pattern;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(OrderAnnotation.class)
public class LoginTestCase extends AppBaseTestCase {

	
//	String url = System.getProperty("integration.test.env.app.url",
//			"http://inactiva-payroll-test.aonsolutions.org:8080/app");
//	String email = System.getProperty("integration.test.env.app.auth", "inactivo");
//	String password = System.getProperty("integration.test.env.app.password", "org");

	//"http://payroll-test.aonsolutions.org:8080/app,inactivo@payroll-test.aonsolutions.org,org", 
	//"http://general-payroll-test.aonsolutions.org:8080/app,inactivo@payroll-test.aonsolutions.org,org", 
	@ParameterizedTest
	@CsvSource({
		"http://inactiva-payroll-test.aonsolutions.org:8080/app,inactivo,org", 
		"http://inactiva-payroll-test.aonsolutions.org:8080/app,admin,org", 
		"http://inactiva-payroll-test.aonsolutions.org:8080/app,inactivo@payroll-test.aonsolutions.org,org", 
	})
	@Order(1)
	public void testDomainInactive(String url, String email, String password) throws AssertionError {
		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.textMatches(By.id("aonLoginToast"), Pattern.compile("El dominio INACTIVA se encuentra actualmente inactivo")));
			
		} catch (Exception e) {
			throw new AssertionError("Error during test execution: " + e.getMessage(), e);
		} finally {
			if (webDriver != null) {
				webDriver.close();
				webDriver.quit();
			}
		}
	}

	
	@Test
	@Order(2)
	public void testLoginIllegalMixOfCollations() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "ñacurutú");
		String password = System.getProperty("integration.test.env.app.password", "ñacurutú");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("UlCompanies")));
			
		} catch (Exception e) {
			throw new AssertionError("Error during test execution: " + e.getMessage(), e);
		} finally {
			if (webDriver != null) {
				webDriver.close();
				webDriver.quit();
			}
		}
	}

	@Test
	@Order(3)
	public void testEnableSupport() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "admin");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonHeaderHelpButton"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonHelpSwitchSupport"))).click();
			
			wait.until(ExpectedConditions.attributeToBe(By.id("aonHelpSwitchSupportInput"), "value", "true"));
			
		} catch (Exception e) {
			throw new AssertionError("Error during test execution: " + e.getMessage(), e);
		} finally {
			if (webDriver != null) {
				webDriver.close();
				webDriver.quit();
			}
		}
	}

	@Test
	@Order(4)
	public void testLoginSupport() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "admin=ñacurutú");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("UlCompanies")));
			
		} catch (Exception e) {
			throw new AssertionError("Error during test execution: " + e.getMessage(), e);
		} finally {
			if (webDriver != null) {
				webDriver.close();
				webDriver.quit();
			}
		}
	}


}
