package net.aonsolutions.watson.test.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.server.AonDateUtils;
import net.aonsolutions.watson.test.WatsonRandom;


class AonDateUtilsTests {

	@Test()
	void toSqlTest() {
		assertNull(AonDateUtils.toSql(null));
		Date today = AonDateUtils.toSql(today());
		Date today2 = AonDateUtils.toDate(LocalDate.now());
		assertFalse(today.before(today2));
		assertFalse(today.after(today2));
	}
	
	@Test()
	void toTimestampTest() {
		assertNull(AonDateUtils.toTimestamp(null));
		Date now = AonDateUtils.toTimestamp(now());
		Date now2 = java.sql.Timestamp.valueOf(LocalDateTime.now().withNano(0));
		 
		assertFalse(now.before(now2), "not before " + now + " != " + now2);
		assertFalse(now.after(now2), "not after " + now + " != " + now2);
	}
	
	@Test()
	void toLocalDateTest() {
		assertNull(AonDateUtils.toLocalDate(null));
		assertNull(AonDateUtils.toDate(null));
		
		LocalDate localDate = LocalDate.now();
		Date date = today();
		
		assertTrue(localDate.isEqual(AonDateUtils.toLocalDate(date)));
		assertFalse(date.before(AonDateUtils.toDate(localDate)));
		assertFalse(date.after(AonDateUtils.toDate(localDate)));
	}
	
	
	private Date today() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}

	private Date now() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}

	@Test()
	void getYearFirstDayTest() {
		int thisYear = Year.now().get(ChronoField.YEAR);
		LocalDate thisYearFirstDay = LocalDate.of(thisYear,1, 1);
		
		assertNull(AonDateUtils.getYearFirstDay(null));
		
		Date date = AonDateUtils.getYearFirstDay(thisYear);
		LocalDate second = AonDateUtils.toLocalDate(date);
		assertTrue(thisYearFirstDay.isEqual(second));
		
		date = WatsonRandom.getYearDay(new Date());
		date = AonDateUtils.getYearFirstDay(date);
		second = AonDateUtils.toLocalDate(date);
		assertTrue(thisYearFirstDay.isEqual(second));
	}
	
	@Test()
	void getYearLastDayTest() {
		int thisYear = Year.now().get(ChronoField.YEAR);
		LocalDate thisYearLastDay = LocalDate.of(thisYear,12, 31);
		
		assertNull(AonDateUtils.getYearLastDay(null));
		
		Date date = AonDateUtils.getYearLastDay(thisYear);
		LocalDate second = AonDateUtils.toLocalDate(date);
		assertTrue(thisYearLastDay.isEqual(second));
		
		date = WatsonRandom.getYearDay(new Date());
		date = AonDateUtils.getYearLastDay(date);
		second = AonDateUtils.toLocalDate(date);
		assertTrue(thisYearLastDay.isEqual(second));
	}
	
}
