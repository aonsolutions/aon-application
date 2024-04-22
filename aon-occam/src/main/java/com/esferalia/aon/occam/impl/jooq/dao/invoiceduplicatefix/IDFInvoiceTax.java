package com.esferalia.aon.occam.impl.jooq.dao.invoiceduplicatefix;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.TaxType;

public class IDFInvoiceTax implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private double base;
	private TaxType type;
	
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
	
	public TaxType getType() {
		return type;
	}
	
	public IDFInvoiceTax setType(TaxType type) {
		this.type = type;
		return this;
	}
}
