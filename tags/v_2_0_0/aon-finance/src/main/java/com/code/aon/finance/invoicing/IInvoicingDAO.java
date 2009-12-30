package com.code.aon.finance.invoicing;

import java.util.Collection;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;

public interface IInvoicingDAO {

	public Invoice insertInvoice(Invoice invoice);
	
	public void insertInvoiceDetail(InvoiceDetail invoiceDetail);
	
	public void createFinances(Invoice invoice) throws ManagerBeanException;
	
	public Collection getCollection();
	
	public void updateSource(ITransferObject to);
}
