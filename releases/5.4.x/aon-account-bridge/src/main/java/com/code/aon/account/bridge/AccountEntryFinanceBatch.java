package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.FinanceBatch;

/**
 * The Class AccountEntryFinanceBatch.
 */
@Entity
@Table(name="account_entry_fbatch")
public class AccountEntryFinanceBatch implements ITransferObject {

	private static final long serialVersionUID = 7131853142761580207L;

	/** The id. */
	private Integer id;
	
	/** The account entry. */
	private AccountEntry accountEntry;
	
	/** The finance batch. */
	private FinanceBatch financeBatch;


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
	 * Gets the account entry.
	 * 
	 * @return the account entry
	 */
	@ManyToOne
	@JoinColumn( name="account_entry", nullable = false)
	@ForeignKey(name="FK_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY")
	@Index(name="IDX_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY")													
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
	 * Gets the finance batch.
	 * 
	 * @return the finance batch
	 */
	@ManyToOne
	@JoinColumn( name="fbatch", nullable = false)
	@ForeignKey(name="FK_ACCOUNT_ENTRY_FBATCH_FBATCH")
	@Index(name="IDX_ACCOUNT_ENTRY_FBATCH_FBATCH")														
	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}

	/**
	 * Sets the finance batch.
	 * 
	 * @param financeBatch the finance batch
	 */
	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}
}