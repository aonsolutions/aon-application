package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_helper")
public class AccountHelper implements ITransferObject {

	private static final long serialVersionUID = -8784282545685853109L;

	private Integer id;
	private Integer counter;
	private Account account;
	private Account balancingAccount;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_HELPER_ACCOUNT")
	@Index(name = "IDX_ACCOUNT_HELPER_ACCOUNT")	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="balancing_account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_HELPER_BALANCING_ACCOUNT")
	@Index(name = "IDX_ACCOUNT_HELPER_BALANCING_ACCOUNT")		
	public Account getBalancingAccount() {
		return balancingAccount;
	}

	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

}