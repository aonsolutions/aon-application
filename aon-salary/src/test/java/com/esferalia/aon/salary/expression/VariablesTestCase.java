package com.esferalia.aon.salary.expression;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class VariablesTestCase {

	@Test
	public void testPut() {

		Variables variables = new Variables(var -> Collections.emptyList());

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date month_start = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date month_end = calendar.getTime();
		Period month = new Period(month_start, month_end);
		
		// Test one, unique value
		variables.put("TEST_VARS", new TimedObject<Double>(100.00, month));
		List<ITimedVariable<?>> test_vars = variables.get("TEST_VARS");
		assertEquals(test_vars.size(), 1);
		assertEquals(test_vars.get(0).getPeriod(), month);
		assertEquals(test_vars.get(0).getValue(month), 100.00);
		

		calendar.set(Calendar.DAY_OF_MONTH,10);
		Date month_tenth = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,20);
		Date month_twentieth = calendar.getTime();
		
		calendar.set(Calendar.DAY_OF_MONTH,9);
		Date month_ninth = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,21);
		Date month_twentyfirst = calendar.getTime();

		// Test put a value in the middle of existing one
		variables.put("TEST_VARS", new TimedObject<Double>(200.00, new Period(month_tenth,month_twentieth)));
		test_vars = variables.get("TEST_VARS");
		assertEquals(test_vars.size(), 3);
		assertEquals(test_vars.get(0).getPeriod(), new Period(month_start,month_ninth));
		assertEquals(test_vars.get(0).getValue(new Period(month_start,month_ninth)), 100.00);
		assertEquals(test_vars.get(1).getPeriod(), new Period(month_tenth,month_twentieth));
		assertEquals(test_vars.get(1).getValue(new Period(month_tenth,month_twentieth)), 200.00);
		assertEquals(test_vars.get(2).getPeriod(), new Period(month_twentyfirst,month_end));
		assertEquals(test_vars.get(2).getValue(new Period(month_twentyfirst,month_end)), 100.00);
		

		// Test put a value in the start of existing one
		variables.put("TEST_VARS", new TimedObject<Double>(1.00, new Period(month_start,month_start)));
		test_vars = variables.get("TEST_VARS");
		assertEquals(test_vars.size(), 4);
		assertEquals(test_vars.get(0).getPeriod(), new Period(month_start,month_start));
		assertEquals(test_vars.get(0).getValue(new Period(month_start,month_start)), 1.00);

		// Test put a value in the end of existing one
		variables.put("TEST_VARS", new TimedObject<Double>(1000.00, new Period(month_end,month_end)));
		test_vars = variables.get("TEST_VARS");
		assertEquals(test_vars.size(), 5);
		assertEquals(test_vars.get(4).getPeriod(), new Period(month_end,month_end));
		assertEquals(test_vars.get(4).getValue(new Period(month_end,month_end)), 1000.00);

		calendar.set(Calendar.DAY_OF_MONTH,2);
		Date month_second = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-1);
		Date month_penultime = calendar.getTime();
	
		assertEquals(test_vars.get(0).getPeriod(), new Period(month_start,month_start));
		assertEquals(test_vars.get(0).getValue(new Period(month_start,month_start)), 1.00);
		assertEquals(test_vars.get(1).getPeriod(), new Period(month_second,month_ninth));
		assertEquals(test_vars.get(1).getValue(new Period(month_second,month_ninth)), 100.00);
		assertEquals(test_vars.get(2).getPeriod(), new Period(month_tenth,month_twentieth));
		assertEquals(test_vars.get(2).getValue(new Period(month_tenth,month_twentieth)), 200.00);
		assertEquals(test_vars.get(3).getPeriod(), new Period(month_twentyfirst,month_penultime));
		assertEquals(test_vars.get(3).getValue(new Period(month_twentyfirst,month_penultime)), 100.00);
		assertEquals(test_vars.get(4).getPeriod(), new Period(month_end,month_end));
		assertEquals(test_vars.get(4).getValue(new Period(month_end,month_end)), 1000.00);
	}

}
