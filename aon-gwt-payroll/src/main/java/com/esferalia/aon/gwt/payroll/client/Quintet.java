package com.esferalia.aon.gwt.payroll.client;

import java.io.Serializable;

public class Quintet<A, B, C, D, E> implements Serializable{
	private A contractId;
	private B varName;
	private C start_date;
	private D end_date;
	private E expression;

	public Quintet() {
		super();
	}
	
	public Quintet(A contractId, B varName, C start_date, D end_date, E expression) {
		super();
		this.contractId = contractId;
		this.varName = varName;
		this.start_date = start_date;
		this.end_date = end_date;
		this.expression = expression;
	}

	public int hashCode() {
		int hashContractId = contractId != null ? contractId.hashCode() : 0;
		int hashVarName = varName != null ? varName.hashCode() : 0;
		int hashStratDate = start_date != null ? start_date.hashCode() : 0;
		int hashEndDate = end_date != null ? end_date.hashCode() : 0;
		int hashExpression = expression != null ? expression.hashCode() : 0;

		return (hashContractId + hashVarName + hashStratDate + hashEndDate + hashExpression) 
				* hashContractId + hashVarName + hashStratDate + hashEndDate + hashExpression;
	}

	public boolean equals(Object other) {
    	if (other instanceof Quintet) {
    		Quintet<?, ?, ?, ?, ?> otherQuartet = (Quintet<?, ?, ?, ?, ?>) other;
    		return 
    		((	this.contractId == otherQuartet.contractId ||
    	 			( this.contractId != null && otherQuartet.contractId != null &&
  	 			  this.contractId.equals(otherQuartet.contractId))) && 
    		 (	this.varName == otherQuartet.varName ||
    	 		( this.varName != null && otherQuartet.varName != null &&
  	 			  this.varName.equals(otherQuartet.varName))) && 
    		 (  this.start_date == otherQuartet.start_date ||
    			( this.start_date != null && otherQuartet.start_date != null &&
    			  this.start_date.equals(otherQuartet.start_date))) &&
    		 (	this.end_date == otherQuartet.end_date ||
    			( this.end_date != null && otherQuartet.end_date != null &&
    			  this.end_date.equals(otherQuartet.end_date))) &&
    		 (	this.expression == otherQuartet.expression ||
	 			( this.expression != null && otherQuartet.expression != null &&
	 			  this.expression.equals(otherQuartet.expression)))
    		);
    	}

    	return false;
    }

	public String toString() {
		return "( Contract id :"+this.contractId
				+", Variable Name :" + this.varName 
				+ ", Fecha incio :" + this.start_date 
				+", Fecha fin :" + this.end_date
				+", Horas :" + this.expression + ")";
	}

	public A getContractId() {
		return contractId;
	}

	public void setContractId(A contractId) {
		this.contractId = contractId;
	}

	public B getVarName() {
		return varName;
	}

	public void setVarName(B varName) {
		this.varName = varName;
	}

	public C getStart_date() {
		return start_date;
	}

	public void setStart_date(C start_date) {
		this.start_date = start_date;
	}

	public D getEnd_date() {
		return end_date;
	}

	public void setEnd_date(D end_date) {
		this.end_date = end_date;
	}

	public E getExpression() {
		return expression;
	}

	public void setExpression(E expression) {
		this.expression = expression;
	}
}
