package solutions.aon.selenium.app;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginTestCase extends AppBaseTestCase {

	@Test
	public void testDomainInactiveByDomainUser() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://inactiva-payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "inactivo");
		String password = System.getProperty("integration.test.env.app.password", "org");

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
	public void testDomainInactiveByParentUser() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://inactiva-payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "admin");
		String password = System.getProperty("integration.test.env.app.password", "org");

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
	public void testDomainInactiveByDomainAuthI() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://inactiva-payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "inactivo@payroll-test.aonsolutions.org");
		String password = System.getProperty("integration.test.env.app.password", "org");

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
	@Ignore
	public void testDomainInactiveByDomainAuthII() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "inactivo@payroll-test.aonsolutions.org");
		String password = System.getProperty("integration.test.env.app.password", "org");

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
	@Ignore
	public void testDomainByDomainAuthI() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://general-payroll-test.aonsolutions.org:8080/app");
		String email = System.getProperty("integration.test.env.app.auth", "inactivo@payroll-test.aonsolutions.org");
		String password = System.getProperty("integration.test.env.app.password", "org");

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




}
