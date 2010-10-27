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
import com.code.aon.employee.enumeration.ContractCode;

/**
 * Transfer Object that represents the contract data.
 * 
 */
@Entity
@Table(name="contract_data")
public class ContractData implements ITransferObject {
	
	private static final long serialVersionUID = 8545553544570810101L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Integer id;
	
	@ManyToOne
    @JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_DATA_CONTRACT")
	@Index(name = "FK_CONTRACT_DATA_CONTRACT")
	private Contract contract;
	
	@Column(nullable=false)
	private ContractCode code;
	
	@Column(length=64)
	private String description;
	
	@Column(length=64)
	private String conditions;

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    private Date startDate;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date" )
    private Date endDate;	
	
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the contract.
	 * 
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * Sets the contract.
	 * 
	 * @param contract the new contract
	 */
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	public ContractCode getCode() {
		return code;
	}

	public void setCode(ContractCode code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractData o = (ContractData) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)			
				.append(this.code, o.code)			
				.append(this.description, o.description)			
				.append(this.conditions, o.conditions)			
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(contract)
			.append(code)			
			.append(description)			
			.append(conditions)			
			.append(startDate)
			.append(endDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
