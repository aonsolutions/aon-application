package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilterOLD;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;

public interface IApi {

	Stream<InvoiceNewPortal> getInvoiceNewPortal(AONContext ctx, InvoiceFilterOLD filter);
	Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilterOLD filter);
	java.util.Date getInvoiceExpDate(AONContext ctx, Integer id); 

}
