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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents the contract bonus.
 * 
 */
@Entity
@Table(name="contract_bonus")
public class ContractBonus implements ITransferObject {

	private static final long serialVersionUID = 2383468613109549918L;

	private Integer id;
	private Contract contract;
	private BonusConcept bonusConcept;	
	private String description;
	private String expression;
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
	
	@ManyToOne
	@JoinColumn(name = "bonus_concept")
	@ForeignKey(name = "FK_CONTRACT_BONUS_BONUS_CONCEPT")
	@Index(name = "IDX_CONTRACT_BONUS_BONUS_CONCEPT")
	public BonusConcept getBonusConcept() {
		return bonusConcept;
	}
	public void setBonusConcept(BonusConcept bonusConcept) {
		this.bonusConcept = bonusConcept;
	}

	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(length = 128)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable = false )
    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.DATE)
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
		final ContractBonus o = (ContractBonus) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.bonusConcept, o.bonusConcept)
				.append(this.description, o.description)
				.append(this.expression, o.expression)
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
			.append(bonusConcept)
			.append(description)
			.append(expression)
			.append(startDate)
			.append(endDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}
