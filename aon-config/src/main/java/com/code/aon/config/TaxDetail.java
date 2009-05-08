package com.code.aon.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents tax detail
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @version 1.0
 */
@Entity
@Table(name="tax_detail")
public class TaxDetail implements ITransferObject {
	
	private static final long serialVersionUID = 9071575626851196240L;

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
    @ForeignKey(name = "FK_TAX_DETAIL_TAX")
    @Index(name = "IDX_TAX_DETAIL_TAX")    
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
	@Temporal(TemporalType.DATE)
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
	@Temporal(TemporalType.DATE)
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
	@Column(name="value",precision = 15, scale = 3)
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
	@Column(name="surcharge",precision = 15, scale = 3)
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TaxDetail o = (TaxDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.endDate, o.endDate)			
				.append(this.startDate, o.startDate)
				.append(this.surcharge, o.surcharge)				
				.append(this.tax, o.tax)			
				.append(this.value, o.value)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(endDate)		
			.append(id)		
			.append(startDate)		
			.append(surcharge)		
			.append(tax)
			.append(value)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}
