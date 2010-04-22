package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
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


	private AccountPeriodStatus status;
	
	/**
	 * Gets the ID of this account.
	 * 
	 * @return The ID of this account
	 */
	@Id
	@Column(nullable = false, length=4)
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
	@Temporal(TemporalType.DATE)
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
	@Temporal(TemporalType.DATE)
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

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Column(name="status")
	public AccountPeriodStatus getStatus() {
		return status;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setStatus(AccountPeriodStatus status) {
		this.status = status;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Period o = (Period) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.deadline, o.deadline)
			.append(this.initiationDate, o.initiationDate)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.deadline)
			.append(this.initiationDate)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
