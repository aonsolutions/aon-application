package com.code.aon.accounting;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;

public class InvoiceEntryDetail implements ITransferObject {

	private static final long serialVersionUID = -2335488721813209995L;

	private double taxableBase;
	private double vatPercent;
	private double vatQuota;
	private double surchargePercent;
	private double surchargeQuota;
	private double retentionPercent;
	private double retentionQuota;
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

	public double getVatQuota() {
		return vatQuota;
	}
	public void setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
	}


	public double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}


	public double getRetentionPercent() {
		return retentionPercent;
	}
	public void setRetentionPercent(double retentionPercent) {
		this.retentionPercent = retentionPercent;
	}

	public double getRetentionQuota() {
		return retentionQuota;
	}
	public void setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public void calculate() {
		setVatQuota(0.0);
		setSurchargeQuota(0.0);
		setRetentionQuota(0.0);
		
		if(getVatPercent() != 0){
			setVatQuota(CommonUtil.round(getTaxableBase() * getVatPercent() / 100, 2));
		}
		if(getSurchargePercent() != 0){
			setSurchargePercent( CommonUtil.round(getTaxableBase() * getSurchargePercent() / 100, 2));
		}
		if(getRetentionPercent() != 0){
			setRetentionQuota( CommonUtil.round(getTaxableBase() * getRetentionPercent() / 100, 2));
		}
	}
	
	public double getTotal() {
		return CommonUtil.round((getTaxableBase() + getSurchargeQuota() + getVatQuota()) - getRetentionQuota(), 2);
	}
	
}