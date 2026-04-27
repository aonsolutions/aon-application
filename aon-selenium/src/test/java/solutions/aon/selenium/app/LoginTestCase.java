package solutions.aon.selenium.app;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(OrderAnnotation.class)
public class LoginTestCase extends AppBaseTestCase {

	
	@ParameterizedTest
	@CsvSource({
		"http://inactiva-payroll-test.aonsolutions.org:8080/,inactivo,org,Domain inactiva-payroll-test.aonsolutions.org is currently inactive",
		"http://inactiva-payroll-test.aonsolutions.org:8080/,admin,org,Domain inactiva-payroll-test.aonsolutions.org is currently inactive",
		"http://inactiva-payroll-test.aonsolutions.org:8080/,inactivo@payroll-test.aonsolutions.org,org,Domain inactiva-payroll-test.aonsolutions.org is currently inactive",
		
		"http://inactive-test.aonsolutions.org:8080/,inactive@test-aonsolutions.org,org,Domain inactive-test.aonsolutions.org is currently inactive",
		"http://default-inactive-test.aonsolutions.org:8080/,inactive@test-aonsolutions.org,org,Domain inactive-test.aonsolutions.org is currently inactive",

		"http://expired-multi-test.aonsolutions.org:8080/,86359314,org,The trial period/booking of the domain expired-multi-test.aonsolutions.org has expired. Contact your sales or support assigned for more information.",
		"http://expired-multi-test.aonsolutions.org:8080/,asesor@multi-test.aonsolutions.org,org,The trial period/booking of the domain expired-multi-test.aonsolutions.org has expired. Contact your sales or support assigned for more information.",
		
		"http://multi-test.aonsolutions.org:8080/,inactive,org,User inactive is currently inactive.",
		//"http://factory-inactive-test.aonsolutions.org:8080/?jaas,inactive,org,User inactive is currently inactive."
		
	})
	@Order(1)
	public void testDomainInactive(String url, String email, String password, String message) throws AssertionError {
		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.textMatches(By.id("aonLoginToast"), Pattern.compile(message)));
			
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
				"http://payroll-test.aonsolutions.org:8080/");
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
				"http://payroll-test.aonsolutions.org:8080/");
		String email = System.getProperty("integration.test.env.app.auth", "admin");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
			    "let d = document.getElementById('aonDesktopInvoiceConfigurationDialog'); if(d) d.close();"
			);
			wait.until(ExpectedConditions.elementToBeClickable(By.id("aonHeaderHelpButton"))).click();
			
			WebElement aonHelpSwitchSupportInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonHelpSwitchSupportInput")));
			if ( !aonHelpSwitchSupportInput.isSelected())
				wait.until(ExpectedConditions.elementToBeClickable(By.id("aonHelpSwitchSupport"))).click();
			
			wait.until(ExpectedConditions.elementToBeSelected(By.id("aonHelpSwitchSupportInput")));
			
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
				"http://payroll-test.aonsolutions.org:8080/");
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

	@Test
	@Order(5)
	public void testLoginEnterpriseWithShared() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://general-payroll-test.aonsolutions.org:8080/");
		String email = System.getProperty("integration.test.env.app.auth", "admin");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonDesktopMainContent")));
			
			
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
	@Order(6)
	public void testLoginEnterprisePayer() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payer-inactive-test.aonsolutions.org:8080/");
		String email = System.getProperty("integration.test.env.app.auth", "pagador@inactive-test.aonsolutions.org");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonDesktopMainContent")));
			
			
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
	@Order(7)
	public void testLoginFactoryUser() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://multi-test.aonsolutions.org:8080/");
		String email = System.getProperty("integration.test.env.app.auth", "factory@multi-test.aonsolutions.org");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
			    "let d = document.getElementById('aonDesktopInvoiceConfigurationDialog'); if(d) d.close();"
			);

			wait.until(ExpectedConditions.elementToBeClickable(By.id("invoice"))).click();
			wait.until(ExpectedConditions.elementToBeClickable(By.id("aonInvoiceSidenavOffers"))).click();
			
			WebElement aonJsfAppFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonJsfAppFrame")));
			webDriver.switchTo().frame(aonJsfAppFrame);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonContent:offerList")));
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
	@Order(7)
	public void testLoginParentInactiveChild() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/");
		String email = System.getProperty("integration.test.env.app.auth", "admin");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
			    "let d = document.getElementById('aonDesktopInvoiceConfigurationDialog'); if(d) d.close();"
			);
			
			WebElement aonCompanyTabFilterInactive = wait.until(ExpectedConditions.elementToBeClickable(By.id("aonCompanyTabFilter-inactive")))/*.click()*/;
			new Actions(webDriver).moveToElement(aonCompanyTabFilterInactive).click().build().perform();
			
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#UlCompanies .aonLiSpan"))).click();

			WebElement aonJsfAccountingGraphFrame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonJsfAccountingGraphFrame")));
			webDriver.switchTo().frame(aonJsfAccountingGraphFrame);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("graphForm:accountColumnChartDiv")));
			
			
			
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
	@Order(8)
	public void testAuthEmailDialog() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/");
		String email = System.getProperty("integration.test.env.app.auth", "expired");
		String password = System.getProperty("integration.test.env.app.password", "expired");

		WebDriver webDriver = null;
		try {
			webDriver = newWebDriver();

			login(webDriver, url, email, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonConfigurationUserCardEmail")));
			
			
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
