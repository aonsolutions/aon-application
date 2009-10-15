package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_entry_detail")
public class AccountEntryDetail implements ITransferObject {

	private static final long serialVersionUID = -1771463579504113080L;

	/** The id. */
	private Integer id;
	
	/** The account entry. */
	private AccountEntry accountEntry;
	
	/** The line. */
	private int line;
	
	/** The account. */
	private Account account;
	
	/** The concept. */
	private String concept;
	
	/** The balancing account. */
	private Account balancingAccount;
	
	/** The debit. */
	private double debit;
	
	/** The credit. */
	private double credit;

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
	 * Gets the account entry.
	 * 
	 * @return the account entry
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account_entry", nullable=false )
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	/**
	 * Sets the account entry.
	 * 
	 * @param accountEntry the account entry
	 */
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	/**
	 * Gets the line.
	 * 
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Sets the line.
	 * 
	 * @param line the line
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
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

	/**
	 * Gets the concept.
	 * 
	 * @return the concept
	 */
	@Column(length=32)
	public String getConcept() {
		return concept;
	}

	/**
	 * Sets the concept.
	 * 
	 * @param concept the concept
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}
	
	/**
	 * Gets the balancing account.
	 * 
	 * @return the balancing account
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="balancing_account")
	public Account getBalancingAccount() {
		return balancingAccount;
	}

	/**
	 * Sets the balancing account.
	 * 
	 * @param balancingAccount the balancing account
	 */
	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	/**
	 * Gets the debit.
	 * 
	 * @return the debit
	 */
	public double getDebit() {
		return debit;
	}

	/**
	 * Sets the debit.
	 * 
	 * @param debit the debit
	 */
	public void setDebit(double debit) {
		this.debit = debit;
	}

	/**
	 * Gets the credit.
	 * 
	 * @return the credit
	 */
	public double getCredit() {
		return credit;
	}

	/**
	 * Sets the credit.
	 * 
	 * @param credit the credit
	 */
	public void setCredit(double credit) {
		this.credit = credit;
	}


	@Transient
	public double getUnpaidBalance() {
		if (getDebit() > getCredit()) {
			return round(getDebit() - getCredit());
		}
		return 0;
	}
	
	@Transient
	public double getCreditBalance() {
		if (getCredit() > getDebit()) {
			return round(getCredit() - getDebit());
		}
		return 0;
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}
	
}