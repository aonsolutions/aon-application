package com.esferalia.aon.salary.expression;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PeriodTestCase {

	@Test
	public void testSubPeriodListOfPeriod() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2010);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		
		Date d1 = calendar.getTime(); 
		
		Period p1 = new Period(d1, null);
		
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, 7);
		Date d2 = calendar.getTime(); 
		Period p2 = new Period(d2, null);
		
		List<Period> sub = Period.sub(p1, Collections.singletonList(p2));
		
		for( Period p: sub )
			System.out.printf("%1$tY/%1$tm/%1$td..%2$tY/%2$tm/%2$td\r\n", p1.getStart(), p.getEnd());
		
		assertTrue(sub.size() == 1);
		assertEquals(sub.get(0).getStart(),p1.getStart());
		
		calendar.setTime(d2);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		assertEquals(sub.get(0).getEnd(),calendar.getTime());
	}

}
