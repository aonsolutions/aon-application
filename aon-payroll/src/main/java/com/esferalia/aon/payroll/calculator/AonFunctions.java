package com.esferalia.aon.payroll.calculator;

import java.lang.reflect.Method;
import java.util.Date;

import org.mvel2.util.MethodStub;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class AonFunctions {
	
	@Variable(ContextVariable.MIN)
	public static final Double min(Double a, Double b) {
		return Math.min(a, b);
	}
	
	@Variable(ContextVariable.MAX)
	public static final Double max(Double a, Double b) {
		return Math.max(a, b);
	}
	
	@Variable(ContextVariable.UNDEFINED)
	public static final Double undefined(String var) throws UndefinedVariablesException {
		throw new UndefinedVariablesException(var);
	}
	
	@Variable(ContextVariable.TRACE)
	public static final Object trace(String format, Object obj) {
		System.out.printf(format, obj);
		return obj;
	}

	@Variable(ContextVariable.ON_ACCOUNT_AGREEMENT)
	public static final Object onAccountAgreement(Object obj) {
		return obj;
	}

	// ------------------------------------------------------------------------
	// 
	// ------------------------------------------------------------------------
	public static void load(ExpressionContext context, Date startDate, Date endDate){
		for (Method method : AonFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if ( variable != null ) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method);
				context.setVariable(contextVariable, methodStub, startDate, endDate);
			}
		}
	}
	
	
}
