/*
 * Created on 23-may-2005
 *
 */
package com.code.aon.company;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

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

	/** Indicates if the company is active or not. */
	private boolean active;    

	/** Indicates if a surcharge has to be applied to the company. */
    private boolean surcharge;
    
    /** Indicates the company calendar identifier. */
    private Integer calendar;

    /**
	 * The empty constructor.
	 */
	public Company() {
	}

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
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

	@Transient
	public boolean isWithholding() {
		return false;
	}
}