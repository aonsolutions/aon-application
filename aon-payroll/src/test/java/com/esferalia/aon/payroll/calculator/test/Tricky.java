package com.esferalia.aon.payroll.calculator.test;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class Tricky {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Double cgcBase = 0.00;
		Double hExtrasBase = 0.00;
		Double nonHExtrasBase = 0.00;
		
		Map<PaymentType, Double> bases = 
			new HashMap<PaymentType, Double>();
		
		bases.put(PaymentType.BASE_SALARY, cgcBase);
		bases.put(PaymentType.COMPENSATION_OR_PREPAID_EXPENSES, cgcBase);
		bases.put(PaymentType.MOVING_COMPENSATION, cgcBase);
		bases.put(PaymentType.OTHER_NON_WAGE, cgcBase);
		bases.put(PaymentType.SALARY_IN_KIND, cgcBase);
		bases.put(PaymentType.SALARY_SUPPLEMENTS, cgcBase);
		bases.put(PaymentType.SOCIAL_SECURITY_BENEFITS, cgcBase);
		bases.put(PaymentType.SPECIAL_BONUSES, cgcBase);
		
		bases.put(PaymentType.STRUCTURAL_HOURS, hExtrasBase);
		bases.put(PaymentType.NON_STRUCTURAL_HOURS, nonHExtrasBase);
		
		
		Double base = bases.get(PaymentType.STRUCTURAL_HOURS);
		base +=100;

		System.out.println("cgcBase = " + cgcBase );
		System.out.println("hExtrasBase = " + hExtrasBase );
		System.out.println("nonHExtrasBase = " + nonHExtrasBase );
	}

}
