package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Supplier;

public class CreditorSupplier implements Serializable {
	private static final long serialVersionUID = 1L;
	Creditor creditor;
	Supplier supplier;
	String type;
	
	public Creditor getCreditor() {
		return creditor;
	}

	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CreditorSupplier(Creditor c, Supplier s, String type){
		this.creditor = c;
		this.supplier = s;
		this.type = type;
	}
}
