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
import com.code.aon.finance.InvoiceDetail;

/**
 * The Class InvoiceDetailAccount.
 */
@Entity
@Table(name="invoice_detail_account")
public class InvoiceDetailAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -7929315870651403627L;

	/** The id. */
	private Integer id;
	
	/** The invoice detail. */
	private InvoiceDetail invoiceDetail;
	
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
	 * Gets the invoice detail.
	 * 
	 * @return the invoice detail
	 */
	@ManyToOne
	@JoinColumn( name="invoice_detail", nullable = false)
	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	/**
	 * Sets the invoice detail.
	 * 
	 * @param invoiceDetail the invoice detail
	 */
	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
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