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
import com.esferalia.aon.payroll.enumeration.LiquidationType;

@Entity
@Table(name="fan_batch")
public class FanBatch implements ITransferObject {
	
	private static final long serialVersionUID = 7580658046113470830L;

	private Integer id;
	private Date date;
	private LiquidationType liquidationType;
	private FileStatus status;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( nullable = false )
    public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "liquidation_type")
	public LiquidationType getLiquidationType() {
		return liquidationType;
	}
	public void setLiquidationType(LiquidationType liquidationType) {
		this.liquidationType = liquidationType;
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
		final FanBatch o = (FanBatch) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.date, o.date)
				.append(this.liquidationType, o.liquidationType)
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(this.date)
			.append(this.liquidationType)
			.append(this.status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
