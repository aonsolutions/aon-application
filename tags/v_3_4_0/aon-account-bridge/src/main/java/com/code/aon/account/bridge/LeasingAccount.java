package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.accounting.Leasing;
import com.code.aon.common.ITransferObject;

/**
 * The Class LeasingAccount.
 */
@Entity
@Table(name="leasing_account")
public class LeasingAccount implements ITransferObject, IAccount {

	private static final long serialVersionUID = -6484054146467508922L;

	/** The id. */
	private Integer id;
	
	/** The leasing. */
	private Leasing leasing;
	
	/** The account. */
	private Account account;

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
	 * Gets the leasing.
	 * 
	 * @return the leasing
	 */
	@ManyToOne
	@JoinColumn( name="leasing", nullable = false)
	public Leasing getLeasing() {
		return leasing;
	}

	/**
	 * Sets the leasing.
	 * 
	 * @param leasing the leasing
	 */
	public void setLeasing(Leasing leasing) {
		this.leasing = leasing;
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