package com.code.aon.employee;

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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.employee.enumeration.PaymentType;

/**
 * Transfer Object that represents the contract payment.
 * 
 */
@Entity
@Table(name="contract_payment")
public class ContractPayment implements ITransferObject {
	
	private static final long serialVersionUID = 2831598401831457550L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "contract", nullable = false, updatable = false)
	@ForeignKey(name = "FK_DEDUCTION_CONTRACT")
	@Index(name = "FK_DEDUCTION_CONTRACT")
	private Contract contract;

	private PaymentType type;
	
	@Column(length = 64)
	private String description;

	@Column(length = 128)
	private String function;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    private Date startDate;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date" )
    private Date endDate;	

	
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Contract getContract() {
		return contract;
	}
	
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	public PaymentType getType() {
		return type;
	}
	
	public void setType(PaymentType type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFunction() {
		return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractPayment o = (ContractPayment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.type, o.type)
				.append(this.description, o.description)
				.append(this.function, o.function)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(contract)
			.append(type)
			.append(description)
			.append(function)
			.append(startDate)
			.append(endDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
