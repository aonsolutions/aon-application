package com.esferalia.aon.in.payroll.excel;

import java.util.Date;

public class ContractDataExcel {
	private Date date;
	private String document;
	private String name;
	private String expression;
	
	public ContractDataExcel() {
		super();
	}
	public Date getDate() {
		return date;
	}
	public ContractDataExcel setDate(Date date) {
		this.date = date;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public ContractDataExcel setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getName() {
		return name;
	}
	public ContractDataExcel setName(String name) {
		this.name = name;
		return this;
	}
	public String getExpression() {
		return expression;
	}
	public ContractDataExcel setExpression(String expression) {
		this.expression = expression;
		return this;
	}
	@Override
	public String toString() {
		return "date : " + date + ", document : " + document + ", name : " + name + ", expression : " + expression;
	}
	
}
