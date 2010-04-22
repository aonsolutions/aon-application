package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Creditor;

/**
 * The Class CreditorAccount.
 */
@Entity
@Table(name="creditor_account")
public class CreditorAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -1065423464446184552L;

	/** The id. */
	private Integer id;
	
	/** The creditor. */
	private Creditor creditor;
	
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
	 * Gets the creditor.
	 * 
	 * @return the creditor
	 */
	@ManyToOne
	@JoinColumn( name="creditor", nullable = false)
	@ForeignKey(name="FK_CREDITOR_ACCOUNT_CREDITOR")
	@Index(name="IDX_CREDITOR_ACCOUNT_CREDITOR")				
	public Creditor getCreditor() {
		return creditor;
	}

	/**
	 * Sets the creditor.
	 * 
	 * @param creditor the creditor
	 */
	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne
	@JoinColumn( name="account", nullable = false)
	@ForeignKey(name="FK_CREDITOR_ACCOUNT_ACCOUNT")
	@Index(name="IDX_CREDITOR_ACCOUNT_ACCOUNT")			
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
	@Transient
	public ITransferObject getLinkedTo() {
		return getCreditor();
	}
	public void setLinkedTo(ITransferObject to) {
		setCreditor((Creditor) to);
	}	
	@Transient
	public String getAccountDescription() {
		return getCreditor()==null?null:getCreditor().getRegistry().getFullName();
	}
}