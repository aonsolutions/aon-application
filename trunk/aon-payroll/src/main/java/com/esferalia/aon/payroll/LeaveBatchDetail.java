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

@Entity
@Table(name="leave_batch_detail")
public class LeaveBatchDetail implements ITransferObject {
	
	private static final long serialVersionUID = 1592306999273993589L;

	private Integer id;
	private LeaveBatch leaveBatch;
	private ContractLeaveDetail contractLeaveDetail;
	
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
	@JoinColumn( name="leave_batch", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_LEAVE_BATCH_DETAIL_LEAVE_BATCH")
	@Index(name = "IDX_LEAVE_BATCH_DETAIL_LEAVE_BATCH")
	public LeaveBatch getLeaveBatch() {
		return leaveBatch;
	}
	public void setLeaveBatch(LeaveBatch leaveBatch) {
		this.leaveBatch = leaveBatch;
	}

	@ManyToOne
	@JoinColumn( name="contract_leave_detail", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL")
	@Index(name = "IDX_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL")
	public ContractLeaveDetail getContractLeaveDetail() {
		return contractLeaveDetail;
	}
	public void setContractLeaveDetail(ContractLeaveDetail contractLeaveDetail) {
		this.contractLeaveDetail = contractLeaveDetail;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final LeaveBatchDetail o = (LeaveBatchDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.leaveBatch, o.leaveBatch)			
				.append(this.contractLeaveDetail, o.contractLeaveDetail)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(this.leaveBatch)			
			.append(this.contractLeaveDetail)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
