package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Company implements Serializable {

	private static final long serialVersionUID = -4970548127101817530L;

	private Integer id;
	private String document;
	private String name;
	
    private Integer domain;
    private boolean active;
	private boolean surcharge;
	private boolean withholding;
	private boolean vatAccrualPayment;
	private boolean eInvoice;

	public Integer getId() {
		return id;
	}

	public Company setId(Integer id) {
		this.id = id;
		return this; 
	}

	public String getDocument() {
		return document;
	}

	public Company setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Company setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}

	public Company setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}

	public Company setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}

	public Company setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Company setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Company setActive(boolean active) {
		this.active = active;
		return this;
	}

	public boolean iseInvoice() {
		return eInvoice;
	}

	public Company seteInvoice(boolean eInvoice) {
		this.eInvoice = eInvoice;
		return this;
	}
	
}
