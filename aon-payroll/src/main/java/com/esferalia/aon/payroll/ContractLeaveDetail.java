package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;

@Entity
@Table(name = "contract_leave_detail")
public class ContractLeaveDetail implements ITransferObject {
	
	private static final long serialVersionUID = 1664466137111759628L;

	private Integer id; 
	private LeaveReportType type;
	private ContractLeave contractLeave;
	private String collegeNumber;
	private Integer confirmOrder;
	private String cias;
	private Date date;
	private boolean processed;

	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=4)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public LeaveReportType getType() {
		return type;
	}
	public void setType(LeaveReportType type) {
		this.type = type;
	}
	
	@ManyToOne
    @JoinColumn( name="contract_leave", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE")
	public ContractLeave getContractLeave() {
		return contractLeave;
	}
	public void setContractLeave(ContractLeave contractLeave) {
		this.contractLeave = contractLeave;
	}
	
	@Column(name="college_number", length=8)
	public String getCollegeNumber() {
		return collegeNumber;
	}
	public void setCollegeNumber(String collegeNumber) {
		this.collegeNumber = collegeNumber;
	}
	
	@Column(name="confirm_order", length=2)
	public Integer getConfirmOrder() {
		return confirmOrder;
	}
	public void setConfirmOrder(Integer confirmOrder) {
		this.confirmOrder = confirmOrder;
	}
	
	@Column(length=11)
	public String getCias() {
		return cias;
	}
	public void setCias(String cias) {
		this.cias = cias;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractLeaveDetail o = (ContractLeaveDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.type, o.type)
				.append(this.contractLeave, o.contractLeave)
				.append(this.collegeNumber, o.collegeNumber)
				.append(this.cias, o.cias)
				.append(this.date, o.date)
				.append(this.processed, o.processed)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(type)
			.append(contractLeave)
			.append(collegeNumber)
			.append(cias)
			.append(date)
			.append(processed)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	@Transient
	public boolean isConfirm(){
		return getType()==LeaveReportType.CONFIRM;
	}
	
}

