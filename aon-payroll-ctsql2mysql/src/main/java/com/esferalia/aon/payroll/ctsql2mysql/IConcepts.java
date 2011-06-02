package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;

import com.esferalia.aon.salary.enumeration.PaymentType;

public interface IConcepts {
	
	public static class Concept<T>{
		public T type;
		public Integer id;
		public String code;
		public String quote;
		public String description;
		
		
		public Concept(Integer id, String code, T type, String quote, String description) {
			this.id = id;
			this.code = code;
			this.type = type;
			this.quote = quote;
			this.description = description;
		}
	}
	
	public static class Bonus{
		public Integer id;
		public String calculo;
		public String description;
		public String expression;
		
		
		public Bonus(Integer id, String calculo, String description, String expression) {
			this.id = id;
			this.calculo = calculo;
			this.description = description;
			this.expression = expression;
		}
	}

	public Concept<PaymentType> getPaymentConcept(String codCom)
		throws SQLException;
	
	public Bonus getBonusConcept(Integer cdg)
	throws SQLException;
	
}
