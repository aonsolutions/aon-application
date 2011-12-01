package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "account_period")
public class Period implements ITransferObject {
	
	private static final long serialVersionUID = -5079105553105179167L;

	private Integer id;
	private String name;
	private Date initiationDate;
	private Date deadline;
	private AccountPeriodStatus status;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(nullable = false, length=10)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "initiation_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getInitiationDate() {
		return initiationDate;
	}
	public void setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
	}

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Column(name="status")
	public AccountPeriodStatus getStatus() {
		return status;
	}
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
			.append(this.name, o.name)
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
			.append(this.name)
			.append(this.deadline)
			.append(this.initiationDate)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
