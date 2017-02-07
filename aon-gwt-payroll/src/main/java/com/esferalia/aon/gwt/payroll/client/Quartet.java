package com.esferalia.aon.gwt.payroll.client;

public class Quartet<A, B, C, D> {
	private A start_date;
	private B end_date;
	private C name;
	private D expression;

	public Quartet() {
		super();
	}
	
	public Quartet(A start_date, B end_date, C name, D expression) {
		super();
		this.start_date = start_date;
		this.end_date = end_date;
		this.name = name;
		this.expression = expression;
	}

	public int hashCode() {
		int hashStratDate = start_date != null ? start_date.hashCode() : 0;
		int hashEndDate = end_date != null ? end_date.hashCode() : 0;
		int hashName = name != null ? name.hashCode() : 0;
		int hashExpression = expression != null ? expression.hashCode() : 0;

		return (hashStratDate + hashEndDate + hashName + hashExpression) 
				* hashStratDate + hashEndDate + hashName + hashExpression;
	}

	public boolean equals(Object other) {
    	if (other instanceof Quartet) {
    		Quartet<?, ?, ?, ?> otherQuartet = (Quartet<?, ?, ?, ?>) other;
    		return 
    		((  this.start_date == otherQuartet.start_date ||
    			( this.start_date != null && otherQuartet.start_date != null &&
    			  this.start_date.equals(otherQuartet.start_date))) &&
    		 (	this.end_date == otherQuartet.end_date ||
    			( this.end_date != null && otherQuartet.end_date != null &&
    			  this.end_date.equals(otherQuartet.end_date))) &&
    		 (	this.name == otherQuartet.name ||
	 			( this.name != null && otherQuartet.name != null &&
	 			  this.name.equals(otherQuartet.name))) && 
    		 (	this.expression == otherQuartet.expression ||
	 			( this.expression != null && otherQuartet.expression != null &&
	 			  this.expression.equals(otherQuartet.expression)))
    		);
    	}

    	return false;
    }

	public String toString() {
		return "(Fecha incio: " + this.start_date + 
				", Fecha fin " + this.end_date + 
				", Name: " + this.name +
				", Horas: " + this.expression + ")";
	}

	public A getStartDate() {
		return start_date;
	}

	public Quartet<A, B, C, D> setStartDate(A start_date) {
		this.start_date = start_date;
		return this;
	}

	public B getEndDate() {
		return end_date;
	}

	public Quartet<A, B, C, D> setEndDate(B end_date) {
		this.end_date = end_date;
		return this;
	}
	
	public C getName() {
		return name;
	}

	public Quartet<A, B, C, D> setName(C name) {
		this.name = name;
		return this;
	}
	
	public D getExpression() {
		return expression;
	}

	public Quartet<A, B, C, D> setExpression(D expression) {
		this.expression = expression;
		return this;
	}
}
