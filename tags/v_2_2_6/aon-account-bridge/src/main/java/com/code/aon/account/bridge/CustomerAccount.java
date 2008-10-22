package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;

/**
 * The Class CustomerAccount.
 */
@Entity
@Table(name="customer_account")
public class CustomerAccount implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The customer. */
	private Customer customer;
	
	/** The account. */
	private Account account;
	
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
	 * Gets the customer.
	 * 
	 * @return the customer
	 */
	@ManyToOne
	@JoinColumn( name="customer", nullable = false)
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
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne
	@JoinColumn( name="account", nullable = false)
	public Account getAccount() {
		return account;
	}

	/**
	 * Sets the account.
	 * 
	 * @param account the account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
}