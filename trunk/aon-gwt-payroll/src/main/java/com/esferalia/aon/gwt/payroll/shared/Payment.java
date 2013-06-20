package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.Salary.Type;


public class Payment extends Item<Payment.Type> {

	String irpfExpression;
	String quoteExpression;

	public static enum Type implements HasDescription {
		BASE_SALARY, SALARY_SUPPLEMENTS, STRUCTURAL_HOURS, NON_STRUCTURAL_HOURS, SPECIAL_BONUSES, SALARY_IN_KIND, COMPENSATION_OR_PREPAID_EXPENSES, SOCIAL_SECURITY_BENEFITS, MOVING_COMPENSATION, OTHER_NON_WAGE;
		
		
		public String getDescription(){
			return DESCRIPTIONS.get(this);
		}

		static Map<Type, String> DESCRIPTIONS = 
				new HashMap<Type, String>() {
			{
				put(BASE_SALARY,"Salario base");
				put(SALARY_SUPPLEMENTS,"Complementos Salariales");
				put(STRUCTURAL_HOURS,"Horas Extraordinarias");
				put(NON_STRUCTURAL_HOURS,"Horas Extraordinarias ( No extructurales )");
				put(SPECIAL_BONUSES,"Gratificciones extraordinarias");
				put(SALARY_IN_KIND,"Salario en Especie");
				put(COMPENSATION_OR_PREPAID_EXPENSES,"Indemnizaciones o suplidos");
				put(SOCIAL_SECURITY_BENEFITS,"Prestaciones e indemnizaciones a la Seguridad Social");
				put(MOVING_COMPENSATION,"Compensaci\u00f3n por movilidad geogr\u00e1fica");
				put(OTHER_NON_WAGE,"Otras precepciones no salariales");
			}
		};

	}


	public String getIrpfExpression() {
		return irpfExpression;
	}
	
	public void setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
	}

	public String getQuoteExpression() {
		return quoteExpression;
	}
	
	public void setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
	}
	

}