package com.code.aon.finance.invoicing.remover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;

public class AdvanceInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.ADVANCE);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		// NO SE HACE NADA, PORUQE NO TENEMOS UN CAMPO EN INVOICE PARA DECIR SI EL 
		// ANTICIPO YA HA SIDO INCLUIDO EN UNA FACTURA O NO.
	}

}