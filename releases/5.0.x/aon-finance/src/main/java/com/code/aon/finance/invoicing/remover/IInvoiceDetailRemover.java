package com.code.aon.finance.invoicing.remover;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;

public interface IInvoiceDetailRemover {

	public boolean accept(InvoiceSource source);
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException;
	
}