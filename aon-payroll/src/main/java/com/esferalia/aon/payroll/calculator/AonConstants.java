package com.esferalia.aon.payroll.calculator;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DismissalType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.OffType;
import com.esferalia.aon.payroll.enumeration.certificados.TLDCAUSS;
import com.esferalia.aon.salary.enumeration.PaymentType;
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
	@Variable(ContextVariable.RETIREMENT)
	public static DismissalType RETIREMENT = DismissalType.RETIREMENT;
	@Variable(ContextVariable.WORK_COMPLETE)
	public static DismissalType WORK_END = DismissalType.WORK_END;
	@Variable(ContextVariable.TEMP_COMPLETE)
	public static DismissalType TEMP_END_ = DismissalType.TEMP_END;
	@Variable(ContextVariable.CONTRACT_COMPLETE)
	public static DismissalType DEFINITE_END = DismissalType.DEFINITE_END;
	@Variable(ContextVariable.CONDITIONS_CHANGE)
	public static DismissalType CONDITIONS_CHANGE = DismissalType.CONDITIONS_CHANGE;
	@Variable(ContextVariable.NOT_PASS_TRIAL_PERIOD)
	public static DismissalType NOT_PASS_TRIAL_PERIOD = DismissalType.NOT_PASS_TRIAL_PERIOD;
	@Variable(ContextVariable.DEATH_OF_EMPLOYEE)
	public static DismissalType DEATH_OF_EMPLOYEE = DismissalType.DEATH_OF_EMPLOYEE;
	@Variable(ContextVariable.VOLUNTARY_END)
	public static DismissalType VOLUNTARY_END = DismissalType.VOLUNTARY_END;

	@Variable(ContextVariable.NON_WORKING)
	public static Double NON_WORKING = -1.00;
	
	
	@Variable(ContextVariable.NOT_PAID_PERMISSION)
	public static OffType NOT_PAID_PERMISSION = OffType.NOT_PAID_PERMISSION;
	@Variable(ContextVariable.SUSPEND_JOB_AND_SALARY)
	public static OffType SUSPEND_JOB_AND_SALARY = OffType.SUSPEND_JOB_AND_SALARY;

	@Variable(ContextVariable.JANUARY)
	public static Integer JANUARY = 1;
	@Variable(ContextVariable.FEBRUARY)
	public static Integer FEBRUARY = 2;
	@Variable(ContextVariable.MARCH)
	public static Integer MARCH = 3;
	@Variable(ContextVariable.APRIL)
	public static Integer APRIL = 4;
	@Variable(ContextVariable.MAY)
	public static Integer MAY = 5;
	@Variable(ContextVariable.JUNE)
	public static Integer JUNE = 6;
	@Variable(ContextVariable.JULY)
	public static Integer JULY = 7;
	@Variable(ContextVariable.AUGUST)
	public static Integer AUGUST = 8;
	@Variable(ContextVariable.SEPTEMBER)
	public static Integer SEPTEMBER = 9;
	@Variable(ContextVariable.OCTOBER)
	public static Integer OCTOBER = 10;
	@Variable(ContextVariable.NOVEMBER)
	public static Integer NOVEMBER = 11;
	@Variable(ContextVariable.DECEMBER)
	public static Integer DECEMBER = 12;

	@Variable(ContextVariable.CRA_0009)
	public static PaymentType CRA_0009 = PaymentType.CRA_0009;
	@Variable(ContextVariable.CRA_0011)
	public static PaymentType CRA_0011= PaymentType.CRA_0011;
	@Variable(ContextVariable.CRA_0012)
	public static PaymentType CRA_0012 = PaymentType.CRA_0012;
	@Variable(ContextVariable.CRA_0010)
	public static PaymentType CRA_0010 = PaymentType.CRA_0010;
	@Variable(ContextVariable.CRA_0008)
	public static PaymentType CRA_0008 = PaymentType.CRA_0008;
	@Variable(ContextVariable.CRA_0033)
	public static PaymentType CRA_0033 = PaymentType.CRA_0033;

	@Variable(ContextVariable.HOME)
	public static CCCType HOME = CCCType.HOME_EMPLOYEES;
	@Variable(ContextVariable.GENERAL)
	public static CCCType GENERAL = CCCType.PRINCIPAL;
	@Variable(ContextVariable.ARTISTS)
	public static CCCType ARTISTS = CCCType.ARTIST;
	@Variable(ContextVariable.AGRARIAN)
	public static CCCType AGRARIAN = CCCType.AGRICULTURAL;
	@Variable(ContextVariable.FELLOWS)
	public static CCCType FELLOWS = CCCType.FELLOWS;
	@Variable(ContextVariable.LEARNING)
	public static CCCType LEARNING = CCCType.LEARNING;
	@Variable(ContextVariable.TRAINING)
	public static CCCType TRAINING = CCCType.TRAINING;
	@Variable(ContextVariable.REPRESENTATIVES)
	public static CCCType REPRESENTATIVES = CCCType.TRADE_REPRESENTATIVE;
	@Variable(ContextVariable.ASSIMILATE)
	public static CCCType ASSIMILATE = CCCType.ASSIMILATEDS;


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
		
		for ( LeaveType leaveType : LeaveType.values() ) {
			try {
				context.setVariable(leaveType.name(), leaveType, startDate, endDate);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
}
