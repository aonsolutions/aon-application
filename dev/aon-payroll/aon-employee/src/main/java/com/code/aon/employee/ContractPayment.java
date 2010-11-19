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
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * Transfer Object that represents the contract payment.
 * 
 */
@Entity
@Table(name="contract_payment")
public class ContractPayment implements ITransferObject, IPayment {
	
	private static final long serialVersionUID = 2831598401831457550L;

	private Integer id;

	private Contract contract;

	private PaymentType type;
	
	private String description;

	private String function;
	
	private Date startDate;	

	private Date endDate;	

	
	
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
	@JoinColumn(name = "contract", nullable = false, updatable = false)
	@ForeignKey(name = "FK_DEDUCTION_CONTRACT")
	@Index(name = "FK_DEDUCTION_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Override
	public PaymentType getType() {
		return type;
	}
	
	public void setType(PaymentType type) {
		this.type = type;
	}
	
	@Override
	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	@Column(length = 128)
	public String getFunction() {
		return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date" )
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

	@Override
	@Transient
	public double getAmount() {
		return 0;
	}
	
}
