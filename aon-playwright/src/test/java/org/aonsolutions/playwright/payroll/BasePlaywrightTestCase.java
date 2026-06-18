package org.aonsolutions.playwright.payroll;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public abstract class BasePlaywrightTestCase {

    protected static final Logger LOGGER = Logger.getLogger(BasePlaywrightTestCase.class.getName());
    protected static final String GWT_DEBUG_ID_PREFIX = "gwt-debug-";
    protected static final String AON_MAIN_MENU_FORM = "aonContent:mainMenuForm";
    protected static final String AON_PAYROLL_MENU_FORM = "aonContent:payrollMenu";
    private static final double ASSERT_DELTA = 0.04;

    private static Playwright playwright;
    private static Browser browser;
    protected static BrowserContext context;
    protected static Page page;

    @AfterAll
    static void tearDown() {
        if (context != null) {
            context.close();
            context = null;
        }
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    protected static void setup(String url, String user, String password) {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(60000);

        page.navigate(url);

        page.locator("input[name='j_username']").fill(user);
        page.locator("input[name='j_password']").fill(password);
        page.locator("input[name='login_btn']").click();

        page.locator("[name='" + AON_MAIN_MENU_FORM + ":menu_payroll']").waitFor();
        page.locator("[name='" + AON_MAIN_MENU_FORM + ":menu_payroll']").click();

        page.locator("[name='" + AON_PAYROLL_MENU_FORM + ":gwt_employee']").waitFor();
        page.locator("[name='" + AON_PAYROLL_MENU_FORM + ":gwt_employee']").click();
    }

    // -------------------------------------------------------------------------

    protected static Locator locateById(String id) {
        return page.locator("[id='" + GWT_DEBUG_ID_PREFIX + id + "']");
    }

    protected static int countById(String id) {
        return locateById(id).count();
    }

    // -------------------------------------------------------------------------
    // Wait methods

    protected static void wait4Id(String id) {
        locateById(id).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    protected static void wait4Text(String id, String text) {
        wait4Id(id);
        assertThat(locateById(id)).hasText(Pattern.compile("^\\s*" + Pattern.quote(text.trim()) + "\\s*$"));
    }

    protected static void wait4Value(String id, String value) {
        wait4Id(id);
        assertThat(locateById(id)).hasValue(value.trim());
    }

    protected static void wait4Value(String id, Double value) {
        wait4Id(id);
        long start = System.currentTimeMillis();
        while (true) {
            try {
                String inputValue = locateById(id).inputValue().trim();
                double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(inputValue).doubleValue();
                if (Math.abs(value - actual) < 0.4) return;
            } catch (ParseException | PlaywrightException e) {
                // keep polling
            }
            if (System.currentTimeMillis() - start > 30000) {
                Assertions.fail("wait4Value timeout for [" + id + "]: expected ~" + value);
                return;
            }
            page.waitForTimeout(500);
        }
    }

    protected static void wait4Regex(String id, String regex) {
        assertThat(locateById(id)).hasText(Pattern.compile(regex));
    }

    protected static void wait4Disabled(String id, boolean disabled) {
        wait4Id(id);
        if (disabled) {
            assertThat(locateById(id)).isDisabled();
        } else {
            assertThat(locateById(id)).isEnabled();
        }
    }

    protected static void wait4Class(String id, String clazz) {
        assertThat(locateById(id)).hasClass(Pattern.compile(".*" + Pattern.quote(clazz) + ".*"));
    }

    protected static void wait4NoClass(String id, String clazz) {
        assertThat(locateById(id)).not().hasClass(Pattern.compile(".*" + Pattern.quote(clazz) + ".*"));
    }

    // -------------------------------------------------------------------------
    // Assert methods

    protected static void assertNotElement(String id) {
        Assertions.assertEquals(0, locateById(id).count(),
                "Expected element [" + id + "] to not exist");
    }

    protected static void assertElement(String id) {
        Assertions.assertTrue(locateById(id).count() > 0,
                "Expected element [" + id + "] to exist");
    }

    protected static void assertText(String id, String text) {
        assertThat(locateById(id)).hasText(Pattern.compile("^\\s*" + Pattern.quote(text.trim()) + "\\s*$"));
    }

    protected static void assertText(String id, double value) {
        String text = locateById(id).textContent().trim();
        try {
            double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
            Assertions.assertEquals(value, actual, ASSERT_DELTA,
                    "Element [" + id + "] expected " + value + " but was " + actual + " (text: '" + text + "')");
        } catch (ParseException e) {
            Assertions.fail("Cannot parse number from element [" + id + "]: '" + text + "'");
        }
    }

    protected static void assertValue(String id, String value) {
        assertThat(locateById(id)).hasValue(value.trim());
    }

    protected static void assertValue(String id, double value) {
        String inputValue = locateById(id).inputValue().trim();
        try {
            double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(inputValue).doubleValue();
            Assertions.assertEquals(value, actual, ASSERT_DELTA,
                    "Element [" + id + "] expected " + value + " but was " + actual + " (value: '" + inputValue + "')");
        } catch (ParseException e) {
            Assertions.fail("Cannot parse number from element [" + id + "]: '" + inputValue + "'");
        }
    }

    protected static void assertValue(String id, double value, double delta) {
        String inputValue = locateById(id).inputValue().trim();
        try {
            double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(inputValue).doubleValue();
            Assertions.assertEquals(value, actual, delta,
                    "Element [" + id + "] expected " + value + " ±" + delta + " but was " + actual);
        } catch (ParseException e) {
            Assertions.fail("Cannot parse number from element [" + id + "]: '" + inputValue + "'");
        }
    }

    protected static void assertDisabled(String id, boolean disabled) {
        if (disabled) {
            assertThat(locateById(id)).isDisabled();
        } else {
            assertThat(locateById(id)).isEnabled();
        }
    }

    protected static void assertInputDisabled(String id, boolean disabled) {
        if (disabled) {
            assertThat(locateById(id)).isDisabled();
        } else {
            assertThat(locateById(id)).isEnabled();
        }
    }

    // -------------------------------------------------------------------------
    // Value accessors

    protected static double getValue(String id) throws ParseException {
        String value = locateById(id).inputValue().trim();
        return NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(value).doubleValue();
    }

    protected static double getText(String id) throws ParseException {
        String text = locateById(id).textContent().trim();
        return NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
    }

    protected static void setValue(String id, String text) {
        Locator locator = locateById(id);
        locator.focus();
        locator.fill(text);
        locator.blur();
    }

    // -------------------------------------------------------------------------
    // Interaction methods

    protected static void click(String id) {
        locateById(id).click();
        LOGGER.warning("Click on: " + id);
    }

    protected static void check(String id) {
        Locator checkbox = locateById(id);
        if (!checkbox.isChecked()) {
            checkbox.click();
            LOGGER.warning("Check on: " + id);
        } else {
            LOGGER.warning("Checkbox '" + id + "' is already checked");
        }
    }

    protected static void uncheck(String id) {
        Locator checkbox = locateById(id);
        if (checkbox.isChecked()) {
            checkbox.click();
            LOGGER.warning("Uncheck on: " + id);
        } else {
            LOGGER.warning("Checkbox '" + id + "' is already unchecked");
        }
    }

    protected static boolean isDisplayed(String id) {
        try {
            return locateById(id).isVisible();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    protected static void open(String id) {
        locateById(id).locator("table img").first().click();
        LOGGER.warning("Open [" + id + "]");
    }

    protected static void close(String id) {
        locateById(id).locator("table img").first().click();
        LOGGER.warning("Close [" + id + "]");
    }

    protected static void select(String id) {
        locateById(id).click();
        LOGGER.warning("Select [" + id + "]");
    }

    protected static void selectOption(String id, String value) {
        locateById(id).selectOption(value);
    }

    // -------------------------------------------------------------------------
    // Draft / navigation

    protected static void draft(String employeeName) {
        String employeeId = normalize(employeeName);
        select(employeeId);
        wait4Text("employeeNameLabel", employeeName);
    }

    // -------------------------------------------------------------------------
    // Calculate / Settle / Extra / Delay

    protected static void calculate(int month) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calculate(calendar.getTime());
    }

    protected static void calculate(int month, int year) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calculate(calendar.getTime());
    }

    protected static void calculate(Date date) throws ParseException {
        locateById("typeListBox").selectOption("SALARY");
        locateById("monthListBox").click();
        scrollToMonthAndClick("monthListBox", date);

        Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        wait4Regex("periodLabel",
                String.format(new Locale("es", "ES"), "[0-9]+/%2$d/%1$d - [0-9]+/%2$d/%1$d", year, month));
    }

    protected static void settle(Date date) throws ParseException {
        date = resetTime(date);

        locateById("typeListBox").selectOption("SETTLE");
        locateById("dateListBox").click();
        scrollToDateAndClick(date);

        Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        wait4Regex("periodLabel",
                String.format(new Locale("es", "ES"), "[0-9]+/[0-9]+/%1$d - [0-9]+/%2$d/%1$d", year, month));
    }

    protected static void extra(Date issueDate, Date endDate) throws ParseException {
        issueDate = resetTime(issueDate);

        locateById("typeListBox").selectOption("EXTRA");
        locateById("dateListBox").click();

        try {
            clickDateSpan(issueDate);
        } catch (PlaywrightException e) {
            scrollToDateAndClick(issueDate);
        }

        Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
        calendar.setTime(endDate);
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH) + 1;
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        wait4Regex("periodLabel",
                String.format(new Locale("es", "ES"), "[0-9]+/[0-9]+/[0-9]+ - %3$d/%2$d/%1$d", endYear, endMonth, endDay));
    }

    protected static void delay(Date startDate, Date endDate) throws ParseException {
        locateById("typeListBox").selectOption("DELAY");
        wait4Id("fromMonthListBox");
        locateById("fromMonthListBox").click();
        scrollToMonthAndClick("fromMonthListBox", startDate);

        wait4Id("monthListBox");
        locateById("monthListBox").click();
        scrollToMonthAndClick("monthListBox", endDate);

        Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
        cal.setTime(startDate);
        int startYear = cal.get(Calendar.YEAR);
        int startMonth = cal.get(Calendar.MONTH) + 1;
        cal.setTime(endDate);
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH) + 1;
        wait4Regex("periodLabel",
                String.format(new Locale("es", "ES"), "[0-9]+/%2$d/%1$d - [0-9]+/%4$d/%3$d",
                        startYear, startMonth, endYear, endMonth));
    }

    protected static void selectSaveTo(String value) {
        locateById("datesListBox").selectOption(value);
    }

    // -------------------------------------------------------------------------
    // Scroll helpers for GWT CellList widgets

    private static void scrollToMonthAndClick(String listBoxId, Date date) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM 'de' yyyy", new Locale("es", "ES"));
        String targetText = fmt.format(date);

        Locator cellList = locateById(listBoxId + "-celllist");

        Locator firstSpan = cellList.locator("div div span").first();
        Date firstDate = fmt.parse(firstSpan.textContent().trim());
        while (firstDate.after(date)) {
            firstSpan.press("PageUp");
            firstSpan = cellList.locator("div div span").first();
            firstDate = fmt.parse(firstSpan.textContent().trim());
        }

        Locator lastSpan = cellList.locator("div div span").last();
        Date lastDate = fmt.parse(lastSpan.textContent().trim());
        while (lastDate.before(date)) {
            lastSpan.press("PageDown");
            lastSpan = cellList.locator("div div span").last();
            lastDate = fmt.parse(lastSpan.textContent().trim());
        }

        cellList.locator("span").filter(new Locator.FilterOptions().setHasText(targetText)).first().click();
    }

    private static void scrollToDateAndClick(Date date) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

        Locator cellList = locateById("dateListBox-celllist");

        Locator firstSpan = cellList.locator("div div span").first();
        Date firstDate = fmt.parse(firstSpan.textContent().trim());
        while (firstDate.after(date)) {
            firstSpan.press("PageUp");
            firstSpan = cellList.locator("div div span").first();
            firstDate = fmt.parse(firstSpan.textContent().trim());
        }

        Locator lastSpan = cellList.locator("div div span").last();
        Date lastDate = fmt.parse(lastSpan.textContent().trim());
        while (lastDate.before(date)) {
            lastSpan.press("PageDown");
            lastSpan = cellList.locator("div div span").last();
            lastDate = fmt.parse(lastSpan.textContent().trim());
        }

        clickDateSpan(date);
    }

    private static void clickDateSpan(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String targetText = fmt.format(date);
        Locator cellList = locateById("dateListBox-celllist");
        cellList.locator("span").filter(new Locator.FilterOptions().setHasText(targetText)).first().click();
    }

    // -------------------------------------------------------------------------

    protected static Date resetTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    protected static String normalize(String str) {
        return str
                .toLowerCase()
                .replace('á', 'a')
                .replace('é', 'e')
                .replace('í', 'i')
                .replace('ó', 'o')
                .replace('ú', 'u')
                .replace('ñ', 'n')
                .replace('ü', 'u')
                .replaceAll("\\s+", "_");
    }
}
