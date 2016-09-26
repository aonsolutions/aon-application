package com.esferalia.aon.payroll.calculator;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DismissalType;
import com.esferalia.aon.payroll.enumeration.certificados.TLDCAUSS;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;


public class AonConstants {
	
	@Variable(ContextVariable.NULL)
	public static DismissalType NULL = null;
	@Variable(ContextVariable.UNFAIR)
	public static DismissalType UNFAIR = DismissalType.UNFAIR;
	@Variable(ContextVariable.OBJECTIVE)
	public static DismissalType OBJECTIVE = DismissalType.OBJECTIVE;
	@Variable(ContextVariable.WORK_COMPLETE)
	public static DismissalType WORK_END = DismissalType.WORK_END;
	@Variable(ContextVariable.TEMP_COMPLETE)
	public static DismissalType TEMP_END_ = DismissalType.TEMP_END;
	@Variable(ContextVariable.CONTRACT_COMPLETE)
	public static DismissalType DEFINITE_END = DismissalType.DEFINITE_END;
	@Variable(ContextVariable.CONDITIONS_CHANGE)
	public static DismissalType CONDITIONS_CHANGE = DismissalType.CONDITIONS_CHANGE;

	@Variable(ContextVariable.NON_WORKING)
	public static Double NON_WORKING = -1.00;
	
	
	// ------------------------------------------------------------------------
	// 
	// ------------------------------------------------------------------------
	public static void load(ExpressionContext context, Date startDate, Date endDate){
		for (Field field: AonConstants.class.getDeclaredFields()) {
			Variable variable = field.getAnnotation(Variable.class);
			if ( variable != null ) {
				ContextVariable contextVariable = variable.value();
				try {
					context.setVariable(contextVariable, field.get(null), startDate, endDate);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
