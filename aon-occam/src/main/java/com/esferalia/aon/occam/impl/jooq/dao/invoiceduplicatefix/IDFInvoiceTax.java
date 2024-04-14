package com.esferalia.aon.occam.impl.jooq.dao.invoiceduplicatefix;

import java.io.Serializable;

public class IDFInvoiceTax implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private double base;
	
	public IDFInvoiceTax () {
		
	}
	
	public Integer getId() {
		return id;
	}
	
	public IDFInvoiceTax  setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public double getBase() {
		return base;
	}
	
	public IDFInvoiceTax  setBase(double base) {
		this.base = base;
		return this;
	}
	
}
