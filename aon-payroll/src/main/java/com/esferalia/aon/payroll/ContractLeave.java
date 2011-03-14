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
import com.esferalia.aon.payroll.enumeration.DischargeCause;
import com.esferalia.aon.payroll.enumeration.LeaveType;

@Entity
@Table(name = "contract_leave")
public class ContractLeave implements ITransferObject {
	
	private static final long serialVersionUID = -8260379243476087391L;

	private Integer id; 
	private LeaveType type;
	private Contract contract;
	private String description;
	private Date startDate;
	private Date endDate;
	private Double dailyCgcBase;
	private Double dailyCgpBase;
	private Double dailyRegBase;
	private ContractLeave parent;
	private DischargeCause dischargeCause;

	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=4)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public LeaveType getType() {
		return type;
	}
	public void setType(LeaveType type) {
		this.type = type;
	}
	
	@ManyToOne
    @JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_LEAVE_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Column(length=64)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name = "daily_cgc_base", precision = 15, scale = 3)
	public Double getDailyCgcBase() {
		return dailyCgcBase;
	}
	public void setDailyCgcBase(Double dailyCgcBase) {
		this.dailyCgcBase = dailyCgcBase;
	}
	
	@Column(name = "daily_cgp_base", precision = 15, scale = 3)
	public Double getDailyCgpBase() {
		return dailyCgpBase;
	}
	public void setDailyCgpBase(Double dailyCgpBase) {
		this.dailyCgpBase = dailyCgpBase;
	}
	
	@Column(name = "daily_reg_base", precision = 15, scale = 3)
	public Double getDailyRegBase() {
		return dailyRegBase;
	}
	public void setDailyRegBase(Double dailyRegBase) {
		this.dailyRegBase = dailyRegBase;
	}
	
	@ManyToOne
    @JoinColumn( name="parent", updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_LEAVE_CONTRACT_LEAVE")
	public ContractLeave getParent() {
		return parent;
	}
	public void setParent(ContractLeave parent) {
		this.parent = parent;
	}
	
	@Transient
	public DischargeCause getDischargeCause() {
		return dischargeCause;
	}
	public void setDischargeCause(DischargeCause dischargeCause) {
		this.dischargeCause = dischargeCause;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractLeave o = (ContractLeave) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.type, o.type)
				.append(this.contract, o.contract)
				.append(this.description, o.description)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.dailyCgcBase, o.dailyCgcBase)
				.append(this.dailyCgpBase, o.dailyCgpBase)
				.append(this.dailyRegBase, o.dailyRegBase)
				.append(this.parent, o.parent)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(type)
			.append(contract)
			.append(description)
			.append(startDate)
			.append(endDate)
			.append(this.dailyCgcBase)
			.append(this.dailyCgpBase)
			.append(this.dailyRegBase)
			.append(this.parent)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}

