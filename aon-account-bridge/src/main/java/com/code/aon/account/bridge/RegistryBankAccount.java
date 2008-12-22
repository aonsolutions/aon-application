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
import com.code.aon.common.ITransferObject;
import com.code.aon.registry.RegistryBank;

/**
 * The Class BankAccount.
 */
@Entity
@Table(name="rbank_account")
public class RegistryBankAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -327952223428600895L;

	/** The id. */
	private Integer id;
	
	/** The registryBank. */
	private RegistryBank registryBank;
	
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
	 * Gets the registryBank.
	 * 
	 * @return the registryBank
	 */
	@ManyToOne
	@JoinColumn( name="rbank", nullable = false)
	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	/**
	 * Sets the registryBank.
	 * 
	 * @param registryBank the registryBank
	 */
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
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