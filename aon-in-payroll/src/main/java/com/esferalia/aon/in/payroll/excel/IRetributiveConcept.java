package com.esferalia.aon.in.payroll.excel;

import com.esferalia.aon.salary.enumeration.PaymentType;

public interface IRetributiveConcept {
	
	public static enum RetributionForm {
		MONEY ("Dinero"),
		IN_KIND ("En especie");
		
		private String description;
		
		private RetributionForm (String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
		
	}
	
	public static enum RetributionType {
		BASE_SALARY ("Salario Base"),
		SALARY_COMPLEMET ("Comp. Salarial"),
		EXTRA_SALARY ("Extrasalarial");
		
		private String description;
		
		private RetributionType (String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
		
	}
	
	public PaymentType getType();
	public String getName();
	public String getDescription();
	public RetributionForm getRetributionForm();
	public RetributionType getRetributionType();
	public Boolean isNormalizable();
	public Boolean isAnualizable();
	
	
}
