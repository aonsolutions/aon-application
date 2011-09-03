package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;

@Entity
@Table(name="fan_batch_detail")
public class FanBatchDetail implements ITransferObject {
	
	private static final long serialVersionUID = 3558714709151557229L;

	private Integer id;
	private FanBatch fanBatch;
	private Enterprise enterprise;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn( name="fan_batch", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_FAN_BATCH_DETAIL_FAN_BATCH")
	@Index(name = "IDX_FAN_BATCH_DETAIL_FAN_BATCH")
	public FanBatch getFanBatch() {
		return fanBatch;
	}
	public void setFanBatch(FanBatch fanBatch) {
		this.fanBatch = fanBatch;
	}

	@ManyToOne
	@JoinColumn( name="enterprise", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_FAN_BATCH_DETAIL_ENTERPRISE")
	@Index(name = "IDX_FAN_BATCH_DETAIL_ENTERPRISE")
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final FanBatchDetail o = (FanBatchDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.fanBatch, o.fanBatch)			
				.append(this.enterprise, o.enterprise)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(this.fanBatch)			
			.append(this.enterprise)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
