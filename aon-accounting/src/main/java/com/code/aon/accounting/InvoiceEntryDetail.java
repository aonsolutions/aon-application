package com.code.aon.accounting;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;

public class InvoiceEntryDetail implements ITransferObject {

	private static final long serialVersionUID = -2335488721813209995L;

	private double taxableBase;

	private double vatPercent;

	private double surchargePercent;

	private double retentionPercent;

	private Account account;

	public InvoiceEntryDetail() {
		super();
	}

	public double getTaxableBase() {
		return taxableBase;
	}

	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}

	public double getVatPercent() {
		return vatPercent;
	}

	public void setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
	}

	public double getSurcharge() {
		return CommonUtil.round(this.getTaxableBase() * this.getSurchargePercent() / 100, 2);
	}

	public double getSurchargePercent() {
		return surchargePercent;
	}

	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}

	public double getRetentionPercent() {
		return retentionPercent;
	}

	public void setRetentionPercent(double retentionPercent) {
		this.retentionPercent = retentionPercent;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public double getVatQuota() {
		double vatQuota = 0.0;
		if(this.getVatPercent() != 0){
			vatQuota = CommonUtil.round(this.getTaxableBase() * getVatPercent() / 100, 2);
		}
		return vatQuota;
	}

	/**
	 * Gets the retention quota.
	 * 
	 * @return the retention quota
	 */
	public double getRetentionQuota() {
		double retentionQuota = 0.0;
		if(this.getRetentionPercent() != 0){
			retentionQuota = CommonUtil.round(this.getTaxableBase() * this.getRetentionPercent() / 100, 2);
		}
		return retentionQuota;
	}

	/**
	 * Gets the total.
	 * 
	 * @return the total
	 */
	public double getTotal() {
		return CommonUtil.round((getTaxableBase() + getSurcharge() + getVatQuota()) - getRetentionQuota(), 2);
	}
	
}