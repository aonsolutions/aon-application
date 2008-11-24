package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Invoice;

/**
 * ITransfer object that relates an AccountEntry with an Invoice
 */
@Entity
@Table(name="account_entry_invoice")
public class AccountEntryInvoice implements ITransferObject {

	private static final long serialVersionUID = 7908519901654709415L;

	/** The id. */
	private Integer id;
	
	/** The account entry. */
	private AccountEntry accountEntry;
	
	/** The invoice. */
	private Invoice invoice;

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
	 * Gets the account entry.
	 * 
	 * @return the account entry
	 */
	@ManyToOne
	@JoinColumn( name="account_entry", nullable = false)
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
	 * Gets the invoice.
	 * 
	 * @return the invoice
	 */
	@ManyToOne
	@JoinColumn( name="invoice", nullable = false)
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * Sets the invoice.
	 * 
	 * @param invoice the invoice
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
}