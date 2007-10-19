package com.code.aon.account;

import com.code.aon.common.ITransferObject;
import com.code.aon.product.Tax;
import com.code.aon.product.enumeration.TaxType;

/**
 * The Class AccountInvoiceDetail.
 */
public class AccountInvoiceDetail implements ITransferObject {

	/** The vat. */
	private Tax vat;
	
	/** The retention. */
	private Tax retention;
	
	/** The taxable base. */
	private double taxableBase;
	
	/**
	 * The empty constructor.
	 * 
	 */
	public AccountInvoiceDetail() {
		super();
		Tax vat = new Tax();
		vat.setType(TaxType.VAT);
		this.vat = vat;
		Tax retention = new Tax();
		retention.setType(TaxType.RETENTION);
		this.retention = retention;
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

	/**
	 * Gets the retention quota.
	 * 
	 * @return the retention quota
	 */
	public double getRetentionQuota() {
		double retentionQuota = 0.0;
		if(this.getVat() != null){
			retentionQuota = round(this.getTaxableBase() * this.getRetention().getPercentage() / 100, 2);
		}
		return retentionQuota;
	}

	/**
	 * Gets the surcharge.
	 * 
	 * @return the surcharge
	 */
	public double getSurcharge() {
		double surcharge = 0.0;
		if(this.getVat() != null){
			surcharge = round(this.getTaxableBase() * this.getVat().getSurcharge() / 100, 2);
		}
		return surcharge;
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