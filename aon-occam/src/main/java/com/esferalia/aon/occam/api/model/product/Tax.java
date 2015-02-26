package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Tax  implements Serializable {

	private static final long serialVersionUID = 5540194869415219773L;
	
	private Integer id;
	private Account salesAccount;
	private Account purchaseAccount;
	private int domain;
	private String name;
	private TaxType type;
	private double percentage;
	private double surcharge;
	private Date startDate;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Account getSalesAccount() {
		return salesAccount;
	}
	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TaxType getType() {
		return type;
	}
	public void setType(TaxType type) {
		this.type = type;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public double getSurcharge() {
		return surcharge;
	}
	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}
	
	
}

