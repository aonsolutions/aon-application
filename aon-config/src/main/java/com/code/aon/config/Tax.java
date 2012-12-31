package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.Account;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.config.enumeration.TaxType;
import com.esferalia.aon.entity.master.TaxDB;

@Entity
@Table(name="tax")
@Heritable
public class Tax extends TaxDB {

	private static final long serialVersionUID = 1L;

	private Account salesAccount;
	private Account purchaseAccount;

	@Transient
	public boolean isVat() {
		return (getType() == TaxType.VAT);
	}

	@Transient
	public boolean isRetention() {
		return (getType() == TaxType.RETENTION);
	}

	@Transient
	public Account getSalesAccount() {
		return salesAccount;
	}

	@Transient
	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}

	@Transient
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}

	@Transient
	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}

}
