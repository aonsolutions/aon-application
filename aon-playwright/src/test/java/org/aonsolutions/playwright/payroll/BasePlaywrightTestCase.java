package org.aonsolutions.playwright.payroll;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.SelectOption;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public abstract class BasePlaywrightTestCase {

    protected static final String GWT_DEBUG_ID_PREFIX = "gwt-debug-";
    protected static final String AON_MAIN_MENU_FORM = "aonContent:mainMenuForm";
    protected static final String AON_PAYROLL_MENU_FORM = "aonContent:payrollMenu";
    protected static final int DEFAULT_TIMEOUT = 60_000;

    private static Playwright playwright;
    private static Browser browser;
    protected static Page page;

    @AfterAll
    public static void tearDown() {
        if (playwright != null) {
            playwright.close();
        }
    }

    protected static void setup(String url, String user, String password) {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        BrowserContext context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(DEFAULT_TIMEOUT);
        PlaywrightAssertions.setDefaultAssertionTimeout(DEFAULT_TIMEOUT);

        page.navigate(url);
        page.locator("form[action='j_security_check'] input[name='j_username']").fill(user);
        page.locator("form[action='j_security_check'] input[name='j_password']").fill(password);
        page.locator("form[action='j_security_check'] input[name='login_btn']").click();

        page.locator("a[name='" + AON_MAIN_MENU_FORM + ":menu_payroll']").click();
        page.locator("a[name='" + AON_PAYROLL_MENU_FORM + ":gwt_employee']").click();
    }

    // All lookups use .first() so duplicate IDs in the app don't throw strict mode violations
    protected static Locator locateById(String id) {
        return page.locator("[id='" + GWT_DEBUG_ID_PREFIX + id + "']").first();
    }

    protected static int countById(String id) {
        return page.locator("[id='" + GWT_DEBUG_ID_PREFIX + id + "']").count();
    }

    protected static void wait4Id(String id) {
        locateById(id).waitFor();
    }

    protected static void wait4Regex(String id, String regex) {
        assertThat(locateById(id)).hasText(Pattern.compile(regex));
    }

    protected static void assertText(String id, String text) {
        assertThat(locateById(id)).hasText(text);
    }

    protected static void assertText(String id, double value) throws ParseException {
        String text = locateById(id).textContent().trim();
        double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
        Assertions.assertEquals(value, actual, 0.04);
    }

    protected static void assertValue(String id, String value) {
        assertThat(locateById(id)).hasValue(value);
    }

    protected static void assertValue(String id, double value) throws ParseException {
        String text = locateById(id).inputValue().trim();
        double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
        Assertions.assertEquals(value, actual, 0.04);
    }

    protected static void assertValue(String id, double value, double delta) throws ParseException {
        String text = locateById(id).inputValue().trim();
        double actual = NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
        Assertions.assertEquals(value, actual, delta);
    }

    protected static void assertElement(String id) {
        assertThat(locateById(id)).isVisible();
    }

    // Uses the raw locator without .first() so hasCount(0) means truly absent
    protected static void assertNotElement(String id) {
        assertThat(page.locator("[id='" + GWT_DEBUG_ID_PREFIX + id + "']")).hasCount(0);
    }

    protected static double getValue(String id) throws ParseException {
        String text = locateById(id).inputValue().trim();
        return NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
    }

    protected static double getText(String id) throws ParseException {
        String text = locateById(id).textContent().trim();
        return NumberFormat.getNumberInstance(new Locale("es", "ES")).parse(text).doubleValue();
    }

    protected static void setValue(String id, String text) {
        Locator input = locateById(id);
        input.clear();
        input.fill(text);
        input.press("Tab");
    }

    protected static boolean isDisplayed(String id) {
        return locateById(id).isVisible();
    }

    protected static void check(String id) {
        Locator cb = locateById(id);
        if (!cb.isChecked()) {
            cb.click();
        }
    }

    protected static void uncheck(String id) {
        Locator cb = locateById(id);
        if (cb.isChecked()) {
            cb.click();
        }
    }

    protected static void open(String id) {
        locateById(id).locator("table tr:first-child td:first-child img").first().click();
    }

    protected static void close(String id) {
        locateById(id).locator("table tr:first-child td:first-child img").first().click();
    }

    protected static void draft(String employeeName) {
        String employeeId = normalize(employeeName);
        locateById(employeeId).click();
        assertThat(locateById("employeeNameLabel")).hasText(employeeName);
    }

    protected static void calculate(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        calculate(cal.getTime());
    }

    protected static void calculate(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        calculate(cal.getTime());
    }

    protected static void calculate(Date date) {
        try {
            locateById("typeListBox").selectOption(new SelectOption().setValue("SALARY"));
            locateById("monthListBox").click();
            scroll2MonthListBox(date);
            String monthText = String.format(new Locale("es", "ES"), "%1$tB de %1$tY", date);
            locateById("monthListBox-celllist")
                    .locator("xpath=.//span[text()='" + monthText + "']").first().click();
            Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            wait4Regex("periodLabel",
                    String.format(new Locale("es", "ES"), "[0-9]+/%2$d/%1$d - [0-9]+/%2$d/%1$d", year, month));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void settle(Date date) throws Exception {
        date = resetTime(date);
        locateById("typeListBox").selectOption(new SelectOption().setValue("SETTLE"));
        locateById("dateListBox").click();
        scroll2DateListBox(date);
        String dateText = String.format(new Locale("es", "ES"), "%1$te de %1$tB de %1$tY", date);
        locateById("dateListBox-celllist")
                .locator("xpath=.//span[text()='" + dateText + "']").first().click();
        Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        wait4Regex("periodLabel",
                String.format(new Locale("es", "ES"), "[0-9]+/[0-9]+/%1$d - [0-9]+/%2$d/%1$d", year, month));
    }

    protected static void extra(Date issueDate, Date endDate) throws Exception {
        issueDate = resetTime(issueDate);
        locateById("typeListBox").selectOption(new SelectOption().setValue("EXTRA"));
        locateById("dateListBox").click();
        String dateText = String.format(new Locale("es", "ES"), "%1$te de %1$tB de %1$tY", issueDate);
        Locator dateSpan = locateById("dateListBox-celllist")
                .locator("xpath=.//span[text()='" + dateText + "']").first();
        try {
            dateSpan.click(new Locator.ClickOptions().setTimeout(5000));
        } catch (PlaywrightException e) {
            scroll2DateListBox(issueDate);
            dateSpan.click();
        }
        Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
        cal.setTime(endDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int end = cal.get(Calendar.DAY_OF_MONTH);
        wait4Regex("periodLabel",
                String.format(new Locale("es", "ES"), "[0-9]+/[0-9]+/[0-9]+ - %3$d/%2$d/%1$d", year, month, end));
    }

    protected static void delay(Date startDate, Date endDate) throws Exception {
        locateById("typeListBox").selectOption(new SelectOption().setValue("DELAY"));
        wait4Id("fromMonthListBox");
        locateById("fromMonthListBox").click();
        scroll2FromMonthListBox(startDate);
        String fromText = String.format(new Locale("es", "ES"), "%1$tB de %1$tY", startDate);
        locateById("fromMonthListBox-celllist")
                .locator("xpath=.//span[text()='" + fromText + "']").first().click();
        wait4Id("monthListBox");
        locateById("monthListBox").click();
        scroll2MonthListBox(endDate);
        String monthText = String.format(new Locale("es", "ES"), "%1$tB de %1$tY", endDate);
        locateById("monthListBox-celllist")
                .locator("xpath=.//span[text()='" + monthText + "']").first().click();
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

    protected static void scroll2MonthListBox(Date date) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMMM 'de' yyyy", new Locale("es", "ES"));
        Locator celllist = locateById("monthListBox-celllist");
        Locator firstSpan = celllist.locator("div > div > span").first();
        Date firstDate = fmt.parse(firstSpan.textContent().trim());
        while (firstDate.after(date)) {
            firstSpan.press("PageUp");
            firstDate = fmt.parse(firstSpan.textContent().trim());
        }
        Locator lastSpan = celllist.locator("div > div > span").last();
        Date lastDate = fmt.parse(lastSpan.textContent().trim());
        while (lastDate.before(date)) {
            lastSpan.press("PageDown");
            lastDate = fmt.parse(lastSpan.textContent().trim());
        }
    }

    protected static void scroll2FromMonthListBox(Date date) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMMM 'de' yyyy", new Locale("es", "ES"));
        Locator celllist = locateById("fromMonthListBox-celllist");
        Locator firstSpan = celllist.locator("div > div > span").first();
        Date firstDate = fmt.parse(firstSpan.textContent().trim());
        while (firstDate.after(date)) {
            firstSpan.press("PageUp");
            firstDate = fmt.parse(firstSpan.textContent().trim());
        }
        Locator lastSpan = celllist.locator("div > div > span").last();
        Date lastDate = fmt.parse(lastSpan.textContent().trim());
        while (lastDate.before(date)) {
            lastSpan.press("PageDown");
            lastDate = fmt.parse(lastSpan.textContent().trim());
        }
    }

    protected static void scroll2DateListBox(Date date) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("d 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
        Locator celllist = locateById("dateListBox-celllist");
        Locator firstSpan = celllist.locator("div > div > span").first();
        Date firstDate = fmt.parse(firstSpan.textContent().trim());
        while (firstDate.after(date)) {
            firstSpan.press("PageUp");
            firstDate = fmt.parse(firstSpan.textContent().trim());
        }
        Locator lastSpan = celllist.locator("div > div > span").last();
        Date lastDate = fmt.parse(lastSpan.textContent().trim());
        while (lastDate.before(date)) {
            lastSpan.press("PageDown");
            lastDate = fmt.parse(lastSpan.textContent().trim());
        }
    }

    private static Date resetTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
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
