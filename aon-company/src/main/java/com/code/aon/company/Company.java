/*
 * Created on 23-may-2005
 *
 */
package com.code.aon.company;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents the company.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 15-nov-2005
 * @since 1.0
 */
@Entity
@Table(name="company")
@PrimaryKeyJoinColumn(name="registry")
public class Company extends Registry implements ITaxInfo{

	private static final long serialVersionUID = -4187068086094343444L;

	/** Indicates if the company is active or not. */
	private boolean active;    

	/** Indicates if a surcharge has to be applied to the company. */
    private boolean surcharge;

	/** Indicates if a holding has to be applied to the company. */
    private boolean withholding;

    /** Indicates the company calendar identifier. */
    private Integer calendar;

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
    @Column(nullable=true)
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets if is active.
	 * 
	 * @param active the active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Checks if a surcharge has to be applied to the company. 
	 * 
	 * @return true, if is surcharge
	 */
	@Column(nullable=true)
	public boolean isSurcharge() {
		return surcharge;
	}

	/**
	 * Sets if a surcharge has to be applied to the company. 
	 * 
	 * @param surcharge the surcharge
	 */
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}
	
	/**
	 * Checks if a surcharge has to be applied to the company. 
	 * 
	 * @return true, if is surcharge
	 */
	@Column(nullable=true)
	public boolean isWithholding() {
		return withholding;
	}

	/**
	 * Sets if a surcharge has to be applied to the company. 
	 * 
	 * @param surcharge the surcharge
	 */
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}

	/**
	 * @return the calendar
	 */
	public Integer getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Integer calendar) {
		this.calendar = calendar;
	}

	@Transient
	public boolean isTaxFree() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Company o = (Company) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(this.active, o.active)
				.append(this.calendar, o.calendar)
				.append(this.surcharge, o.surcharge)
				.append(this.withholding, o.withholding)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper(super.hashCode())
			.append(active)
			.append(calendar)
			.append(surcharge)
			.append(withholding)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}