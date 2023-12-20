package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;

public class NordigenAccountDetail implements Serializable {
	
	private static final long serialVersionUID = -3879480833728917316L;
	
	private String resourceId;
	private String iban;
	private String currency;	
	private String ownerName;
	private String name;
	private NordigenCashAccountType cashAccountType;
	private String product;
	private String status;
	private String bic;
	
	public String getResourceId() {
		return resourceId;
	}
	public NordigenAccountDetail setResourceId(String resourceId) {
		this.resourceId = resourceId;
		return this;
	}	
	
	public String getIban() {
		return iban;
	}
	public NordigenAccountDetail setIban(String iban) {
		this.iban = iban;
		return this;
	}

	public String getCurrency() {
		return currency;
	}
	public NordigenAccountDetail setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	public NordigenAccountDetail setOwnerName(String ownerName) {
		this.ownerName = ownerName;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public NordigenAccountDetail setName(String name) {
		this.name = name;
		return this;
	}
	public NordigenCashAccountType getCashAccountType() {
		return cashAccountType;
	}
	public NordigenAccountDetail setCashAccountType(NordigenCashAccountType cashAccountType) {
		this.cashAccountType = cashAccountType;
		return this;
	}
	public String getProduct() {
		return product;
	}
	public NordigenAccountDetail setProduct(String product) {
		this.product = product;
		return this;
	}
	public String getStatus() {
		return status;
	}
	public NordigenAccountDetail setStatus(String status) {
		this.status = status;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public NordigenAccountDetail setBic(String bic) {
		this.bic = bic;
		return this;
	}
	
	
}
