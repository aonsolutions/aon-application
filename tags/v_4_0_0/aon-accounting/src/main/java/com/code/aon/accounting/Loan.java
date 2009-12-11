package com.code.aon.accounting;

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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.registry.RegistryBank;

/**
 * The Class Loan.
 */
@Entity
@Table(name="loan")
public class Loan implements ITransferObject {

	private static final long serialVersionUID = 6332808498569171281L;

	private Integer id;
	private String description;
	private Date loanDate;
	private String term;
	private String interest;
	private String review;
	private double amount;
	private double expenses;
	private RegistryBank registryBank;
	private SecurityLevel securityLevel;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Column(nullable=false)
	@GeneratedValue
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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length=64)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the loan date.
	 * 
	 * @return the loan date
	 */
	@Column(name="loan_date",nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getLoanDate() {
		return loanDate;
	}

	/**
	 * Sets the loan date.
	 * 
	 * @param loanDate the loan date
	 */
	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	/**
	 * Gets the term.
	 * 
	 * @return the term
	 */
	@Column(length=16)
	public String getTerm() {
		return term;
	}

	/**
	 * Sets the term.
	 * 
	 * @param term the term
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Gets the interest.
	 * 
	 * @return the interest
	 */
	@Column(length=16)
	public String getInterest() {
		return interest;
	}

	/**
	 * Sets the interest.
	 * 
	 * @param interest the interest
	 */
	public void setInterest(String interest) {
		this.interest = interest;
	}

	/**
	 * Gets the review.
	 * 
	 * @return the review
	 */
	@Column(length=16)
	public String getReview() {
		return review;
	}

	/**
	 * Sets the review.
	 * 
	 * @param review the review
	 */
	public void setReview(String review) {
		this.review = review;
	}

	/**
	 * Gets the amount.
	 * 
	 * @return the amount
	 */
	@Column(nullable=true, precision=15, scale=3)
	public double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 * 
	 * @param amount the amount
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Gets the expenses.
	 * 
	 * @return the expenses
	 */
	@Column(nullable=true, precision=15, scale=3)
	public double getExpenses() {
		return expenses;
	}

	/**
	 * Sets the expenses.
	 * 
	 * @param expenses the expenses
	 */
	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	/**
	 * Gets the registry bank.
	 * 
	 * @return the registry bank
	 */
    @ManyToOne
    @JoinColumn(name="rbank", nullable=false)
	@ForeignKey(name = "FK_LOAN_RBANK")
	@Index(name = "IDX_LOAN_RBANK")			    
    public RegistryBank getRegistryBank() {
		return registryBank;
	}

	/**
	 * Sets the registry bank.
	 * 
	 * @param registryBank the registry bank
	 */
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Loan o = (Loan) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.description, o.description)
			.append(this.loanDate, o.loanDate )
			.append(this.term, o.term )
			.append(this.interest, o.interest )
			.append(this.review, o.review  )
			.append(this.amount, o.amount )
			.append(this.expenses, o.expenses )
			.append(this.registryBank, o.registryBank )
			.append(this.securityLevel, o.securityLevel )
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.loanDate)
			.append(this.term)
			.append(this.interest)
			.append(this.review)
			.append(this.amount)
			.append(this.expenses)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}