package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;
import com.esferalia.aon.payroll.enumeration.FileStatus;

@Entity
@Table(name = "certifica2_batch")
public class Certifica2Batch implements ITransferObject {

	private static final long serialVersionUID = -5948322093046943029L;

	private Integer id;
	private Enterprise enterprise;
	private Date date;
	private FileStatus status;
	private String sign;
	
	@Id     
	@GeneratedValue
    @Column(name="id", unique=true, nullable=false, length=10)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = Enterprise.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "enterprise", nullable = false)
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date", nullable = false)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "status", length = 1)
	public FileStatus getStatus() {
		return status;
	}
	public void setStatus(FileStatus status) {
		this.status = status;
	}

	@Column(name = "sign", length = 30)
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Certifica2Batch o = (Certifica2Batch) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.enterprise, o.enterprise)	
				.append(this.date, o.date)
				.append(this.status, o.status)
				.append(this.sign, o.sign)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(this.enterprise)
				.append(this.date)
				.append(this.status)
				.append(this.sign)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

	
	
}
