package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents a Expense.
 * 
 * @author Esferalia. David Uriarte - 20-nov-2009
 * @since 1.0
 */
@Entity
@Table(name="expense")
public class Expense implements ITransferObject {
	

	/** The id. */
	private Integer id;
	
	/** The description. */
	private String description;
	
	/** The price. */
	private Double unitPrice;
	
	
	/**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	 /**
     * Gets the description.
     * 
     * @return the description
     */
	@Column(length=64)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the price.
     * 
     * @return the price
     */
    @Column(name="unit_price")
	public Double getUnitPrice() {
		return unitPrice;
	}

	 /**
     * Sets the price.
     * 
     * @param price the price
     */
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Expense o = (Expense) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.unitPrice, o.unitPrice)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)
			.append(id)
			.append(unitPrice)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}