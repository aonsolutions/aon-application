package solutions.aon.selenium.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DomainUserRolesTestCase extends AppBaseTestCase {

	@Test
	public void testGlobalManagerUser() throws MalformedURLException, URISyntaxException {
		String url = System.getProperty("integration.test.env.app.url",
				"http://payroll-test.aonsolutions.org:8080/app");
		String user = System.getProperty("integration.test.env.app.user", "asesor");
		String password = System.getProperty("integration.test.env.app.password", "org");

		WebDriver webDriver = null;
		try {
			// webDriver = newChromeDriver();
			// webDriver = newFirefoxDriver();
			webDriver = newRemoteDriver();

			login(webDriver, url, user, password);

			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("UlCompanies")));

			assertTopMenu(webDriver, wait, "enterpriseMenu", "accountingMenu", "fiscalMenu", "payrollMenu");
			assertSideMenu(webDriver, wait, "home", "apps", "documental");
			assertCompaniesTabs(webDriver, wait, "Activas", "Inactivas", "Despacho");

			selectEnterprise(webDriver, wait, "office", "DESPACHO");
			//assertTopMenuEmpty(webDriver, wait);
			assertTopMenuHidden(webDriver, wait);
			listCompanies(webDriver, wait);

			selectEnterprise(webDriver, wait, "active", "RÉGIMEN GENERAL");
			assertTopMenu(webDriver, wait, "accountingMenu", "fiscalMenu", "payrollMenu");
			assertSideMenu(webDriver, wait, "home", "apps", "new", "documental", "note", "warehouse" /*only for local*/ );

		} finally {
			if (webDriver != null) {
				webDriver.close();
				webDriver.quit();
			}
		}
	}

	private void listCompanies(WebDriver webDriver, WebDriverWait wait) {
		wait.until(ExpectedConditions.elementToBeClickable(By.id("aonHeaderCompanyListButton"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("UlCompanies")));
	}

	private void selectEnterprise(WebDriver webDriver, WebDriverWait wait, String tab, String enterprise) {

		WebElement aonCompanyTabFilter = webDriver.findElement(By.id("aonCompanyTabFilter-" + tab));
		aonCompanyTabFilter.click();
		webDriver.findElement(By.xpath("//ul[@id='UlCompanies']/li/span/span[text()='" + enterprise + "']")).click();

		wait.until(ExpectedConditions.stalenessOf(aonCompanyTabFilter));
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//span[@id='aonHeaderCompanyName' and text() = '" + enterprise + "' ]")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonDesktopMainContent")));

	}

	private void assertTopMenuHidden(WebDriver webDriver, WebDriverWait wait) {
		assertFalse(webDriver.findElement(By.id("aonMenuTopnav")).isDisplayed());
	}

	private void assertNotTopMenu(WebDriver webDriver, WebDriverWait wait) {
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("aonTopMenuDiv")));
	}

	private void assertTopMenuEmpty(WebDriver webDriver, WebDriverWait wait) {
		WebElement aonTopMenuDiv = webDriver.findElement(By.id("aonTopMenuDiv"));
		List<WebElement> topMenuElements = aonTopMenuDiv.findElements(By.xpath("child::*"));
		assertEquals(0, topMenuElements.size());
	}

	private void assertTopMenu(WebDriver webDriver, WebDriverWait wait, String... ids) {
		WebElement aonTopMenuDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonTopMenuDiv")));//webDriver.findElement(By.id("aonTopMenuDiv"));
		List<WebElement> topMenuElements = aonTopMenuDiv.findElements(By.xpath("child::*"));
		List<String> topMenuExpectedIds = Arrays.stream(ids).map(id -> "aonMenuBar-" + id).toList();
		assertEquals(topMenuExpectedIds.size(), topMenuElements.size());
		topMenuElements.forEach(
				menu -> assertTrue(menu.getAttribute("id"), topMenuExpectedIds.contains(menu.getAttribute("id"))));
	}

	private void assertSideMenu(WebDriver webDriver, WebDriverWait wait, String... ids) {
		WebElement aonMenuSidenav = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aonMenuSidenav")));//webDriver.findElement(By.id("aonMenuSidenav"));

		List<WebElement> sideMenuElements = aonMenuSidenav
				.findElements(By.xpath("ul/li[starts-with(@id,'aonMenuList-')]"));
		List<String> sideMenuElementsIds = Arrays.stream(ids).map(id -> "aonMenuList-" + id).toList();
		assertEquals(sideMenuElementsIds.size(), sideMenuElements.size());
		sideMenuElements.forEach(
				menu -> assertTrue(menu.getAttribute("id"), sideMenuElementsIds.contains(menu.getAttribute("id"))));
	}

	private void assertCompaniesTabs(WebDriver webDriver, WebDriverWait wait, String... texts) {
		List<String> companyTabsExpectedTexts = Arrays.stream(texts).toList();
		WebElement aonCompanyTabFilter = webDriver.findElement(By.id("aonCompanyTabFilter"));
		List<WebElement> aonTabItems = aonCompanyTabFilter
				.findElements(By.xpath("//*[@class='aonTabItem']/*[contains(@class,'aonTabItemText')]"));
		assertEquals(companyTabsExpectedTexts.size(), aonTabItems.size());
		aonTabItems.forEach(tab -> assertTrue(tab.getText(),
				companyTabsExpectedTexts.stream().anyMatch(text -> tab.getText().startsWith(text))));
	}

}
