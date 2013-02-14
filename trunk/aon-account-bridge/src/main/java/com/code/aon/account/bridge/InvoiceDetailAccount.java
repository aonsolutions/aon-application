package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.InvoiceDetail;
import com.esferalia.aon.entity.master.InvoiceDetailAccountDB;

@Entity
@Table(name="invoice_detail_account")
public class InvoiceDetailAccount extends InvoiceDetailAccountDB implements IAccount {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getInvoiceDetail();
	}
	public void setLinkedTo(ITransferObject to) {
		setInvoiceDetail((InvoiceDetail) to);
	}	

	@Transient
	public String getAccountDescription() {
		return null;
	}

}