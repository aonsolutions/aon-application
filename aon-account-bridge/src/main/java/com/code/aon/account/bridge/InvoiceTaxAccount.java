package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.InvoiceTax;
import com.esferalia.aon.entity.master.InvoiceTaxAccountDB;

@Entity
@Table(name="invoice_tax_account")
public class InvoiceTaxAccount extends InvoiceTaxAccountDB implements IAccount {
	
	private static final long serialVersionUID = 1L;

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