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
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.employee.enumeration.ContractCode;
import com.code.aon.employee.enumeration.QuoteGroup;

/**
 * Transfer Object that represents the contract data.
 * 
 */
@Entity
@Table(name="contract_data")
public class ContractData implements ITransferObject {
	
	private static final long serialVersionUID = 8545553544570810101L;

	private Integer id;
	
	private Contract contract;
	
	private ContractCode code;
	
	private String description;
	
	private String conditions;

	private Date startDate;	

	private Date endDate;	
	
	private QuoteGroup quoteGroup;
	
	private String category;
	
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
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
	@ManyToOne
    @JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_DATA_CONTRACT")
	@Index(name = "FK_CONTRACT_DATA_CONTRACT")
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
	
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.employee.enumeration.ContractCode") })
	@Column(nullable=false)
	public ContractCode getCode() {
		return code;
	}

	public void setCode(ContractCode code) {
		this.code = code;
	}

	@Column(length=64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=64)
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
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable = false )
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
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
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
	
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.employee.enumeration.QuoteGroup") })
	@Column( name = "quote_group" )
	public QuoteGroup getQuoteGroup() {
		return quoteGroup;
	}
	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}
	
//	@ManyToOne
//    @JoinColumn( name="category", updatable = false )	
//	@ForeignKey(name = "FK_CONTRACT_DATA_CATEGORY")
//	@Index(name = "FK_CONTRACT_DATA_CATEGORY")
	@Column(length=64)
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
				.append(this.quoteGroup, o.quoteGroup)
				.append(this.category, o.category)
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
			.append(quoteGroup)
			.append(category)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
