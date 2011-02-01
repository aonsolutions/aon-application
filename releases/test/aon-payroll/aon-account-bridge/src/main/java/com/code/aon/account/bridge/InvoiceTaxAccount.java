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
import com.code.aon.finance.InvoiceTax;

@Entity
@Table(name="invoice_tax_account")
public class InvoiceTaxAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -7929315870651403627L;

	private Integer id;
	private InvoiceTax invoiceTax;
	private Account account;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="invoice_tax", nullable = false)
	@ForeignKey(name="FK_INVOICE_TAX_ACCOUNT_INVOICE_TAX")
	@Index(name="IDX_INVOICE_TAX_ACCOUNT_INVOICE_TAX")											
	public InvoiceTax getInvoiceTax() {
		return invoiceTax;
	}
	public void setInvoiceTax(InvoiceTax invoiceTax) {
		this.invoiceTax = invoiceTax;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_INVOICE_TAX_ACCOUNT_ACCOUNT")
	@Index(name="IDX_INVOICE_TAX_ACCOUNT_ACCOUNT")										
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getInvoiceTax();
	}
	public void setLinkedTo(ITransferObject to) {
		setInvoiceTax((InvoiceTax) to);
	}	

	@Transient
	public String getAccountDescription() {
		return null;
	}

}