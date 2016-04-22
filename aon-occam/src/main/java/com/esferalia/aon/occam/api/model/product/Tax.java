package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Tax implements Serializable, HasAudit {

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
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	
	public Integer getId() {
		return id;
	}
	public Tax setId(Integer id) {
		this.id = id;
		return this;
	}
	public Account getSalesAccount() {
		return salesAccount;
	}
	public Tax setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}
	public Tax setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Tax setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Tax setName(String name) {
		this.name = name;
		return this;
	}
	public TaxType getType() {
		return type;
	}
	public Tax setType(TaxType type) {
		this.type = type;
		return this;
	}
	public double getPercentage() {
		return percentage;
	}
	public Tax setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}
	public double getSurcharge() {
		return surcharge;
	}
	public Tax setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Tax setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public Tax setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
		return this;
	}
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public Tax setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Tax setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Tax setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Tax setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Tax setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
}

