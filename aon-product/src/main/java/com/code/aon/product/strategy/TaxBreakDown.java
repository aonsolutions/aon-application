package com.code.aon.product.strategy;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;

/**
 * Contains info of a tax, the type and the breakdown.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class TaxBreakDown implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private TaxType taxType;
	private double taxPercent;
	private double surchargePercent;
	private double taxQuota;
	private double surchargeQuota;
	private double base;
	private Account account;
	private Account balancingAccount;
	private VatDeductionType vatDeductionType; 
	private double deductibleBase;
	private double deductiblePercent;
	private double deductibleQuota;

	public TaxType getTaxType() {
		return taxType;
	}
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	public double getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(double taxPercent) {
		this.taxPercent = taxPercent;
	}

	public double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}

	public double getTaxQuota() {
		return taxQuota;
	}
	public void setTaxQuota(double taxQuota) {
		this.taxQuota = taxQuota;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}

	public double getBase() {
		return base;
	}
	public void setBase(double base) {
		this.base = base;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public Account getBalancingAccount() {
		return balancingAccount;
	}
	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	public boolean isVat() {
		return (getTaxType() == TaxType.VAT);
	}

	public boolean isRetention() {
		return (getTaxType() == TaxType.RETENTION);
	}

	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}
	
	public double getDeductibleBase() {
		return deductibleBase;
	}
	public void setDeductibleBase(double deductibleBase) {
		this.deductibleBase = deductibleBase;
	}

	public double getDeductiblePercent() {
		return deductiblePercent;
	}
	public void setDeductiblePercent(double deductiblePercent) {
		this.deductiblePercent = deductiblePercent;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}

}
