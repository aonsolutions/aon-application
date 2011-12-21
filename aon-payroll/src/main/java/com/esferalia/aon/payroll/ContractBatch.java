package com.esferalia.aon.payroll;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.payroll.enumeration.FileStatus;

@Entity
@Table(name = "contract_batch")
public class ContractBatch implements ITransferObject {
	
	private static final long serialVersionUID = -7639337685373561673L;

	private Integer id; 
	private Date date; 
	private Date redNotifyDate;
	private String redNotifyId;
	private Date redResponseDate;
	private String redResponseId;
	private FileStatus status;

	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=4)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date", nullable = false)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "red_notify_date")
	public Date getRedNotifyDate() {
		return redNotifyDate;
	}
	public void setRedNotifyDate(Date redNotifyDate) {
		this.redNotifyDate = redNotifyDate;
	}
	
	@Column(name = "red_notify_id", length=32)
	public String getRedNotifyId() {
		return redNotifyId;
	}
	public void setRedNotifyId(String redNotifyId) {
		this.redNotifyId = redNotifyId;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "red_response_date")
	public Date getRedResponseDate() {
		return redResponseDate;
	}
	public void setRedResponseDate(Date redResponseDate) {
		this.redResponseDate = redResponseDate;
	}
	
	@Column(name = "red_response_id", length=32)
	public String getRedResponseId() {
		return redResponseId;
	}
	public void setRedResponseId(String redResponseId) {
		this.redResponseId = redResponseId;
	}
	
	public FileStatus getStatus() {
		return status;
	}
	public void setStatus(FileStatus status) {
		this.status = status;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractBatch o = (ContractBatch) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.date, o.date)
				.append(this.redNotifyDate, o.redNotifyDate)
				.append(this.redNotifyId, o.redNotifyId)
				.append(this.redResponseDate, o.redResponseDate)
				.append(this.redResponseId, o.redResponseId)
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id )
			.append(date) 
			.append(redNotifyDate)
			.append(redNotifyId)
			.append(redResponseDate)
			.append(redResponseId)
			.append(status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}

