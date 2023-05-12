package net.aonsolutions.watson.server;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class AonDateUtils {

	private AonDateUtils() {

	}

	public static java.sql.Date toSql(Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	public static Timestamp toTimestamp(Date date) {
		return date == null ? null : new Timestamp(date.getTime());
	}

	public static LocalDate toLocalDate(Date date) {
		if (date == null)
			return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date toDate(LocalDate localDate) {
		if (localDate == null)
			return null;
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static Date getYearFirstDay(int year) {
		return toDate(Year.of(year).atDay(1));
	}

	public static Date getYearFirstDay(Date date) {
		if (date == null)
			return null;
		return toDate(toLocalDate(date).withDayOfYear(1));
	}

	public static Date getYearLastDay(int year) {
		return toDate(Year.of(year).atDay(1).with(TemporalAdjusters.lastDayOfYear()));
	}

	public static Date getYearLastDay(Date date) {
		if (date == null)
			return null;
		return toDate(toLocalDate(date).with(TemporalAdjusters.lastDayOfYear()));
	}

}
