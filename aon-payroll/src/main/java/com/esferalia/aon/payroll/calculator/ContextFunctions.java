package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTHS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WARNING;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CHECK;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.mvel2.util.MethodStub;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementContextFactory;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.CheckException;

public class ContextFunctions {

	private static final String MONTHS_IMPL = "MESESIMPL";

	public static void check(boolean condition, String msg) throws CheckException{
		if ( !condition ) {
			throw new CheckException(msg);
		}
	}

	public static void checkVar(String name, boolean condition, String msg) throws CheckException{
		if ( !condition ) {
			throw new InvalidVariable(name, msg);
		}
	}

	public static void warning(String msg) throws CheckException{
		throw new CheckException(msg);		
	}

	
	public static int getMonths(Date start, Date end , double days){
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);
		
		int months = 0;
	
		while ( days > 0 && ( startCalendar.compareTo(endCalendar) <= 0 ) ){
			days -= CommonUtil.daysInMonth(startCalendar.getTime());
			startCalendar.add(Calendar.MONTH, 1);
			months++;
		}
		
		return months ;
	}
	
	
	
	private static void loadMonthsFunction(ExpressionContext context, Date startDate, Date endDate) throws ExpressionException {
		try {
			
			Method months =  ContextFunctions.class.getMethod(
					"getMonths", 
					Date.class, 
					Date.class, 
					double.class);
			
			MethodStub monthsStub = new MethodStub(months);
			
			context.addVariable( MONTHS_IMPL , monthsStub, startDate, endDate);
			
			String functionScript =  
					String.format("%s = def (days) { %s(%s, %s, days) };", 
							MONTHS,
							MONTHS_IMPL, 
							START, 
							END  );
			
			context.eval(functionScript, startDate, endDate);
			
		} catch ( SecurityException e) {
		} catch( NoSuchMethodException e ){
		}
	}
	
	private static void loadWarnFunction(ExpressionContext context, Date startDate, Date endDate) throws ExpressionException {

		// WARNING function
		try {
			Method warning =  ContextFunctions.class.getMethod(
					"warning", 
					String.class);
			
			MethodStub warningStub = new MethodStub(warning);
			
			context.addVariable( WARNING , warningStub, startDate, endDate);
		} catch ( SecurityException e) {
		} catch( NoSuchMethodException e ){
		}
	}

	private static void loadCheckFunction(ExpressionContext context, Date startDate, Date endDate) throws ExpressionException {

		// WARNING function
		try {
			Method check =  ContextFunctions.class.getMethod(
					"check", 
					boolean.class, 
					String.class);
			
			MethodStub warningStub = new MethodStub(check);
			
			context.addVariable( CHECK , warningStub, startDate, endDate);
		} catch ( SecurityException e) {
		} catch( NoSuchMethodException e ){
		}
	}

	private static void loadCheckVarFunction(ExpressionContext context, Date startDate, Date endDate) throws ExpressionException {

		// WARNING function
		try {
			Method checkVar =  ContextFunctions.class.getMethod(
					"checkVar", 
					String.class,
					boolean.class, 
					String.class);
			
			MethodStub warningStub = new MethodStub(checkVar);
			
			context.addVariable( ContextVariable.CHECK_VAR , warningStub, startDate, endDate);
		} catch ( SecurityException e) {
		} catch( NoSuchMethodException e ){
		}
	}

	public static void loadFunctions(ExpressionContext context, Date startDate, Date endDate) throws ExpressionException {
		loadCheckFunction(context, startDate, endDate);
		loadCheckVarFunction(context, startDate, endDate);
		loadWarnFunction(context, startDate, endDate);
		loadMonthsFunction(context, startDate, endDate);
	}
}
