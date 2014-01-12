package com.esferalia.aon.payroll.calculator;

import java.lang.reflect.Method;
import java.util.Date;

import org.mvel2.util.MethodStub;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public class AonFunctions {
	

	// ------------------------------------------------------------------------
	// 
	// ------------------------------------------------------------------------
	public static void load(ExpressionContext context, Date startDate, Date endDate){
		for (Method method : AonFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if ( variable != null ) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method);
				context.addVariable(contextVariable, methodStub, startDate, endDate);
			}
		}
	}
	
	
}
