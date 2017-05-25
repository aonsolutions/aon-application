package com.esferalia.aon.payroll.calculator;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.mvel2.util.MethodStub;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.MacroException;

public class ExcelFunctions {

	// ------------------------------------------
	// Relational
	// ------------------------------------------
	@Variable(ContextVariable.TRUE)
	public static final Boolean truE() {
		return true;
	}

	@Variable(ContextVariable.FALSE)
	public static final Boolean falsE() {
		return false;
	}

	@Variable(ContextVariable.NOT)
	public static final Boolean not(Object bool) {
		return !truthValueTesting(bool);
	}

	@Variable(ContextVariable.OR)
	public static final Boolean or(Object... bools) {
		for (Object bool : bools) {
			if (truthValueTesting(bool))
				return true;
		}
		return false;
	}

	@Variable(ContextVariable.AND)
	public static final Boolean and(Object... bools) {
		for (Object bool : bools) {
			if (!truthValueTesting(bool))
				return false;
		}
		return true;
	}

	@Variable(ContextVariable.IF)
	public static final Object If(Object test, Object trueValue,
			Object falseValue) throws MacroException {
		return truthValueTesting(test) ? trueValue : falseValue;

	}

	private static boolean truthValueTesting(Object object) {
		if (object instanceof Boolean)
			return (Boolean) object;
		if (object instanceof Number)
			return ((Number) object).doubleValue() != 0.00;
		if (object instanceof Object[])
			return ((Object[]) object).length > 0;
		if (object instanceof Collection)
			return !((Collection<?>) object).isEmpty();
		return object != null;
	}

	// ------------------------------------------------------------------------
	// Dates

	@Variable(ContextVariable.DAYS)
	public static Long days(Date from, Date to) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(from);
		
		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(to);
		
		return (long) ((fromCalendar.getTimeInMillis() + fromCalendar.get(Calendar.DST_OFFSET)) - (toCalendar.getTimeInMillis() + toCalendar.get(Calendar.DST_OFFSET) )) / (1000 * 60 * 60 * 24);
	}

	@Variable(ContextVariable.DATE)
	public static Date date(int year, int month, int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1); // JANUARY was 0
		calendar.set(Calendar.DATE, date);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}

	@Variable(ContextVariable.MONTH)
	public static Date month(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	//@Variable(ContextVariable.MONTH)
	public static int month(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH)+1;
	}

	@Variable(ContextVariable.YEAR)
	public static Date year(Date date, int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}

	//@Variable(ContextVariable.YEAR)
	public static Date year(java.sql.Date date, int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}
	//@Variable(ContextVariable.YEAR)
	public static int year(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	@Variable(ContextVariable.DAY)
	public static Date day(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.add(Calendar.DAY_OF_MONTH, days);

		return calendar.getTime();
	}

	//@Variable(ContextVariable.DAY)
	public static int day(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	// ------------------------------------------------------------------------
	// Maths

	// Choose double. Almost others Numbers fits in it.

	@Variable(ContextVariable.ABS)
	public static final Double abs(double number) {
		return Math.abs(number);
	}

	@Variable(ContextVariable.INTEGER)
	public static final Integer ceil(double number) {
		return (int) Math.ceil(number);
	}

	@Variable(ContextVariable.POW)
	public static final Double pow(double number, double exp) {
		return Math.pow(number, exp);
	}

	@Variable(ContextVariable.QUOTIENT)
	public static final Integer quotient(double numerator, Double denominator) {
		return (int) (numerator / denominator);
	}

	@Variable(ContextVariable.SQRT)
	public static final Double sqrt(double number) {
		return Math.sqrt(number);
	}

	@Variable(ContextVariable.ROUND)
	public static final Double round(double number, int precision) {
		double factor = Math.pow(10, precision);
		return Math.round(number*factor) / factor;
	}

	// ------------------------------------------
	//
	// ------------------------------------------
	public static void load(ExpressionContext context, Date startDate,
			Date endDate) {
		for (Method method : ExcelFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if (variable != null) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method);
				context.setVariable(contextVariable, methodStub, startDate,
						endDate);
			}
		}
	}

	public static Map<String, Object> load(Map<String, Object> context) {
		for (Method method : ExcelFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if (variable != null) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method);
				context.put(contextVariable.getName(), methodStub);
			}
		}
		return context;
	}
}
