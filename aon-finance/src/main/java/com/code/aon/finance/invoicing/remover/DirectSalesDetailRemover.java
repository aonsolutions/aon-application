package com.code.aon.finance.invoicing.remover;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;

public class DirectSalesDetailRemover implements IInvoiceDetailRemover {
	
	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.DIRECT_SALES);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
	}

}