package com.code.aon.product;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents tax detail
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @version 1.0
 */
@Entity
@Table(name="tax_detail")
public class TaxDetail implements ITransferObject {
	
    /**
     * Primary key.
     */
	private Integer id;
	
    /**
     * The tax linked.
     */
	private Tax tax;
	
    /**
     * Start date.
     */
	private Date startDate;
	
    /**
     * End date.
     */
	private Date endDate;
	
    /**
     * The value of this tax detail.
     */
	private double value;
	
    /**
     * The surcharge.
     */
	private double surcharge;

	/**
     * Returns unique key.
     * 
     * @return unique key.
     */
	@Id    
    @GeneratedValue
    @Column(nullable=false)
	public Integer getId() {
		return id;
	}

    /**
     * Assigns unique key.
     * 
     * @param id
     *            Unique key.
     */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
     * Returns the tax.
     * 
     * @return tax.
     */
	@ManyToOne
    @JoinColumn(name="tax", nullable=false)
	public Tax getTax() {
		return tax;
	}

    /**
     * Assigns the tax.
     * 
     * @param tax
     *           Tax.
     */
	public void setTax(Tax tax) {
		this.tax = tax;
	}

	/**
     * Returns the start date.
     * 
     * @return startDate.
     */
	@Column(name="start_date")
	public Date getStartDate() {
		return startDate;
	}

    /**
     * Assigns the start date.
     * 
     * @param startDate
     *           start date.
     */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
     * Returns the end date.
     * 
     * @return endDate.
     */
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

    /**
     * Assigns the end date.
     * 
     * @param endDate
     *           end date.
     */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
     * Returns the value.
     * 
     * @return value.
     */
	@Column(name="value")
	public double getValue() {
		return value;
	}

    /**
     * Assigns the value.
     * 
     * @param value
     *           value.
     */
	public void setValue(double value) {
		this.value = value;
	}

	/**
     * Returns the surcharge.
     * 
     * @return surcharge.
     */
	@Column(name="surcharge")
	public double getSurcharge() {
		return surcharge;
	}

    /**
     * Assigns the surcharge.
     * 
     * @param surcharge
     *           surcharge.
     */
	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}

}
