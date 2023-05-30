package net.aonsolutions.occam.test.faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.github.javafaker.Faker;

import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonDateUtils;

public class AonRandom {

	private static Faker faker = Faker.instance(Locale.of("es"));
	private static final int REQUIRED = -1;

	// ----------------------- [BOOLEAN]
	public static boolean gt(int threshold) {
		return faker.random().nextInt(0, 100) >= threshold;
	}

	// ----------------------- [STRING]
	public static String uuid(int maxLength) {
		return AonStringUtils.substring(faker.internet().uuid(), 0, maxLength);
	}

	public static String string(int nullThreshold, int minLength, int maxLength) {
		return gt(nullThreshold) ? faker.lorem().characters(minLength, maxLength) : null;
	}

	public static String string(int maxLength) {
		return faker.lorem().characters(REQUIRED, maxLength);
	}

	public static String string(int nullThreshold, int maxLength) {
		return gt(nullThreshold) ? faker.lorem().characters(0, maxLength) : null;
	}

	public static String lorem(int maxLength) {
		return lorem(REQUIRED, maxLength);
	}

	public static String lorem(int nullThreshold, int maxLength) {
		return gt(nullThreshold) ? faker.lorem().characters(1, maxLength) : null;
	}

	public static String domainName() {
		return faker.internet().domainName();
	}

	public static String name(int nullThreshold, int maxLength) {
		return gt(nullThreshold) ? AonStringUtils.abbreviate(faker.name().fullName(), maxLength) : null;
	}

	public static String alias(int nullThreshold, int maxLength) {
		return gt(nullThreshold) ? AonStringUtils.abbreviate(faker.name().username(), maxLength) : null;
	}

	// ----------------------- [NUMBER]
	public static Integer integer() {
		return integer(REQUIRED);
	}

	public static Integer integer(int nullThreshold) {
		return gt(nullThreshold) ? Integer.valueOf(number(0, Integer.MAX_VALUE - 1)) : null;
	}

	public static Integer integer(int nullThreshold, int max) {
		return gt(nullThreshold) ? Integer.valueOf(number(0, 20)) : null;
	}

	public static Integer number(int nullThreshold, int from, int to) {
		return gt(nullThreshold) ? number(from, to) : null;
	}

	public static int number(int to) {
		return faker.random().nextInt(to);
	}

	public static int number(int from, int to) {
		return faker.random().nextInt(from, to);
	}

	public static double percent() {
		return getDouble(0, 100, 0);
	}

	public static double percent(int proecision) {
		return getDouble(0, 100, proecision);
	}

	public static double getDouble(int from, int to) {
		return getDouble(from, to, 2);
	}

	public static double getDouble(int from, int to, int precision) {
		double r = faker.random().nextDouble();
		return AonNumberUtils.round(from + ((to - from) * r), precision);
	}

	public static Double getDouble(int nullThreshold, int from, int to, int precision) {
		return (gt(nullThreshold)) ? getDouble(from, to, precision) : null;
	}
	
	// -----------------------------------------------------------------------------
	// ---------------------------------------------------------------------- [DATE]
	// -----------------------------------------------------------------------------
	public static Date yearDay(int year) {
		return truncate(faker.date().between(AonDateUtils.getYearFirstDay(year), AonDateUtils.getYearLastDay(year)));
	}
	public static Date yearDay(Date date) {
		return yearDay(AonDateUtils.getYear(date));
	}
	public static Date yearDay() {
		return yearDay(today());
	}

	public static Date pastDate(int threshold) {
		return (gt(threshold)) ? truncate(faker.date().past(100, TimeUnit.DAYS, new Date())) : null;
	}

	public static Date today() {
		return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static Date yesterday() {
		return Date.from(LocalDate.now().plusDays(REQUIRED).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static Date tomorrow() {
		return Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static Date rangeDate(Date start, Date end) {
		return faker.date().between(start, end);
	}

	public static Date futureDate(int threshold, Date date) {
		return (gt(threshold)) ? truncate(faker.date().future(100, TimeUnit.DAYS, date)) : null;
	}

	public static Date futureDate() {
		return futureDate(new Date());
	}

	public static Date futureDate(Date date) {
		return futureDate(REQUIRED, new Date());
	}

	public static Date futureDate(int threshold) {
		return futureDate(threshold, new Date());
	}

	private static Date truncate(Date date) {
		return date == null ? null
				: Date.from(
						date.toInstant().atZone(ZoneId.of("Europe/Madrid")).truncatedTo(ChronoUnit.DAYS).toInstant());
	}
	
	// -----------------------------------------------------------------------------
	// --------------------------------------------------------------------- [LISTS]
	// -----------------------------------------------------------------------------
	public static <T> T random(List<T> list) {
		if (list == null || list.isEmpty()) return null;
		return list.get(faker.random().nextInt(0, (list.size() - 1)));
	}

	public static <T> T get(List<T> list) {
		if (AonCollectionUtils.isEmpty(list)) return null;
		return list.get(number(0, (list.size() - 1)));
	}

}
