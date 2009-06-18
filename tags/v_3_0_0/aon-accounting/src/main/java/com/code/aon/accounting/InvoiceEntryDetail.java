package com.code.aon.accounting;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;

public class InvoiceEntryDetail implements ITransferObject {

	private static final long serialVersionUID = -2335488721813209995L;

	/** The taxable base. */
	private double taxableBase;

	/** The vat. */
	private Tax vat;
	
	private double vatPercent;

	/** The surcharge. */
	private double surcharge;

	/** The retention. */
	private Tax retention;

	private double retentionPercent;

	public double getVatPercent() {
		return vatPercent;
	}

	public void setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
	}

	public double getRetentionPercent() {
		return retentionPercent;
	}

	public void setRetentionPercent(double retentionPercent) {
		this.retentionPercent = retentionPercent;
	}

	/** The account. */
	private Account account;

	/**
	 * The constructor.
	 */
	public InvoiceEntryDetail() {
		super();
		Tax vat = new Tax();
		vat.setType(TaxType.VAT);
		this.vat = vat;
		Tax retention = new Tax();
		retention.setType(TaxType.RETENTION);
		this.retention = retention;
	}

	/**
	 * Gets the taxable base.
	 * 
	 * @return the taxable base
	 */
	public double getTaxableBase() {
		return taxableBase;
	}

	/**
	 * Sets the taxable base.
	 * 
	 * @param taxableBase the taxable base
	 */
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}

	/**
	 * Gets the vat.
	 * 
	 * @return the vat
	 */
	public Tax getVat() {
		return vat;
	}

	/**
	 * Sets the vat.
	 * 
	 * @param vat the vat
	 */
	public void setVat(Tax vat) {
		this.vat = vat;
	}

	/**
	 * Gets the surcharge.
	 * 
	 * @return the surcharge
	 */
	public double getSurcharge() {
		return surcharge;
	}

	/**
	 * Sets the surcharge.
	 * 
	 * @param surcharge the surcharge
	 */
	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}
	
	/**
	 * Gets the retention.
	 * 
	 * @return the retention
	 */
	public Tax getRetention() {
		return retention;
	}

	/**
	 * Sets the retention.
	 * 
	 * @param retention the retention
	 */
	public void setRetention(Tax retention) {
		this.retention = retention;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Sets the account.
	 * 
	 * @param account the account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the vat quota.
	 * 
	 * @return the quota
	 */
	public double getVatQuota() {
		double vatQuota = 0.0;
		if(this.getVat() != null){
			vatQuota = round(this.getTaxableBase() * this.getVat().getPercentage() / 100, 2);
		}
		return vatQuota;
	}

	public void calculateSurcharge(boolean surcharge) {
		setSurcharge(0.0);
		if(surcharge){
			if(this.getVat() != null){
				setSurcharge(round(this.getTaxableBase() * this.getVat().getSurcharge() / 100, 2));
			}
		}
	}

	/**
	 * Gets the retention quota.
	 * 
	 * @return the retention quota
	 */
	public double getRetentionQuota() {
		double retentionQuota = 0.0;
		if(this.getRetention() != null){
			retentionQuota = round(this.getTaxableBase() * this.getRetention().getPercentage() / 100, 2);
		}
		return retentionQuota;
	}

	/**
	 * Gets the total.
	 * 
	 * @return the total
	 */
	public double getTotal() {
		return round((getTaxableBase() + getSurcharge() + getVatQuota()) - getRetentionQuota(), 2);
	}
	
	/**
     * Round the <code>value</code> using the <code>precision</code> passed as a parameter
     * 
     * @param value the value
     * @param precision the precision
     * 
     * @return the double
     */
    private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}