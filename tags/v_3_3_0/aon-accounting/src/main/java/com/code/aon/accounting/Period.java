package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Entity class for representing an accounting period.
 * 
 * @author Consulting & Development. ecastellano - 22/01/2007
 * 
 */
@Entity
@Table(name = "account_period")
public class Period implements ITransferObject {

	private static final long serialVersionUID = -5079105553105179167L;

	/**
	 * The ID of this accounting period
	 */
	private String id;

	/**
	 * The initation date of this accounting period.
	 */
	private Date initiationDate;

	/**
	 * The final date of this accounting period.
	 */
	private Date deadline;

	/**
	 * Gets the ID of this account.
	 * 
	 * @return The ID of this account
	 */
	@Id
	@Column(nullable = false)
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID of this account.
	 * 
	 * @param id
	 *            The ID of this account.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the initation date of this accounting period.
	 * 
	 * @return The initation date of this accounting period.
	 */
	@Column(name = "initiation_date", nullable = false)
	public Date getInitiationDate() {
		return initiationDate;
	}

	/**
	 * Sets the description of this account.
	 * 
	 * @param initiationDate
	 *            The description of this account.
	 */
	public void setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
	}

	/**
	 * Gets the deadline of this accounting period.
	 * 
	 * @return The deadline of this accounting period.
	 */
	@Column(nullable = false)
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * Sets the deadline of this accounting period.
	 * 
	 * @param deadline
	 *            The deadline of this accounting period.
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

}
