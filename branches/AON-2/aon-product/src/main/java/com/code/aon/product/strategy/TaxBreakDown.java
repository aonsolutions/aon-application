package com.code.aon.product.strategy;

import com.code.aon.product.enumeration.TaxType;

/**
 * Contains info of a tax, the type and the breakdown.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class TaxBreakDown {
	
	/**
	 * The type of tax 
	 */
	private TaxType taxType;
	
	/**
	 * Percent to be applied
	 */
	private double taxPercent;
	
	/**
	 * Surcharge percent
	 */
	private double surchargePercent;
	
	/**
	 * The tax quota
	 */
	private double taxQuota;
	
	/**
	 * The surcharge quota
	 */
	private double surchargeQuota;
	
	/**
	 * Tax base
	 */
	private double base;

	/**
	 * Returns the tax type 
	 * 
	 * @return
	 */
	public TaxType getTaxType() {
		return taxType;
	}

	/**
	 * Assigns the tax type
	 * 
	 * @param taxType
	 */
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	/**
	 * Returns the tax percent 
	 * 
	 * @return
	 */
	public double getTaxPercent() {
		return taxPercent;
	}

	/**
	 * Assigns the tax percent
	 * 
	 * @param taxPercent
	 */
	public void setTaxPercent(double taxPercent) {
		this.taxPercent = taxPercent;
	}

	/**
	 * Returns the surcharge percent 
	 * 
	 * @return surcharge percent
	 */
	public double getSurchargePercent() {
		return surchargePercent;
	}

	/**
	 * Assigns the surcharge percent
	 * 
	 * @param surchargePercent
	 */
	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}

	/**
	 * Returns the tax quota
	 * 
	 * @return tax quota
	 */
	public double getTaxQuota() {
		return taxQuota;
	}

	/**
	 * Assigns the tax quota
	 * 
	 * @param taxQuota
	 */
	public void setTaxQuota(double taxQuota) {
		this.taxQuota = taxQuota;
	}

	/**
	 * Returns the surcharge quota
	 * 
	 * @return surcharge quota
	 */
	public double getSurchargeQuota() {
		return surchargeQuota;
	}

	/**
	 * Assigns the surcharge quota
	 * 
	 * @param surchargeQuota
	 */
	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}

	/**
	 * Returns the base
	 * 
	 * @return the base
	 */
	public double getBase() {
		return base;
	}

	/**
	 * Assigns the base
	 * 
	 * @param base
	 */
	public void setBase(double base) {
		this.base = base;
	}
}
