package com.code.aon.academy;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;

/**
 * The Class AlumnLoan.
 */
@Entity
@Table(name="alumn_loan")
public class AlumnLoan implements ITransferObject {

	/** The id. */
	private Integer id;
	
	/** The customer. */
	private Customer customer;
	
	/** The description. */
	private String material;
	
	/** The loan date. */
	private Date loanDate;
	
	/** The end date. */
	private Date endDate;
	
	/** The comments. */
	private String comments;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
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
	 * Gets the customer.
	 * 
	 * @return the customer
	 */
	@ManyToOne
	@JoinColumn(name="customer", nullable=false)
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Sets the customer.
	 * 
	 * @param customer the customer
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(name="material", nullable=false)
	public String getMaterial() {
		return material;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * Gets the loan date.
	 * 
	 * @return the loan date
	 */
	@Column(name="loan_date", nullable=false)
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
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Column(name="comments", length=64)
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments the comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
}