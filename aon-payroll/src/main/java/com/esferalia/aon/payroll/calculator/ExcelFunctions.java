package com.esferalia.aon.payroll.calculator;

import java.lang.reflect.Method;
import java.util.Date;

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
	public static final Boolean not(boolean bool) {
		return !bool;
	}

	@Variable(ContextVariable.OR)
	public static final Boolean or(boolean... bools) {
		for (Boolean bool : bools) {
			if (bool)
				return true;
		}
		return false;
	}

	@Variable(ContextVariable.AND)
	public static final Boolean and(boolean... bools) {
		for (Boolean bool : bools) {
			if (!bool)
				return false;
		}
		return true;
	}

	@Variable(ContextVariable.IF)
	public static final Object If(boolean bool, Object trueValue,
			Object falseValue) throws MacroException {
		return bool ? trueValue : falseValue;
	}

	// ------------------------------------------
	// Dates
	// ------------------------------------------
	@Variable(ContextVariable.DAYS)
	public static Long days(Date from, Date to){
		return (long) ( from.getTime() - to.getTime() ) / ( 1000 * 60 * 60 * 24 );
	}

	// ------------------------------------------
	// Maths
	// ------------------------------------------
	
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
	public static final Double pow(double number, double exp ) {
		return Math.pow(number , exp);
	}

	@Variable(ContextVariable.QUOTIENT)
	public static final Integer quotient(double numerator, Double denominator ) {
		return (int) ( numerator / denominator );
	}
	
	@Variable(ContextVariable.SQRT)
	public static final Double sqrt(double number ) {
		return Math.sqrt(number);
	}
	
	// ------------------------------------------
	// 
	// ------------------------------------------
	public static void load(ExpressionContext context, Date startDate, Date endDate){
		for (Method method : ExcelFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if ( variable != null ) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method);
				context.setVariable(contextVariable, methodStub, startDate, endDate);
			}
		}
	}
	
	
}
