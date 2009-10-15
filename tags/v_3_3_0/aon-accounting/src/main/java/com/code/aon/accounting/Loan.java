package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	/** The id. */
	private Integer id;
	
	/** The description. */
	private String description;
	
	/** The loan date. */
	private Date loanDate;
	
	/** The term. */
	private String term;
	
	/** The interest. */
	private String interest;
	
	/** The review. */
	private String review;
	
	/** The amount. */
	private double amount;
	
	/** The expenses. */
	private double expenses;
	
	/** The registry bank. */
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
}