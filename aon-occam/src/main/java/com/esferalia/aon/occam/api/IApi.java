package com.esferalia.aon.occam.api;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Order.InvoiceOrder;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;

public interface IApi {

	Stream<InvoiceNewPortal> getInvoiceNewPortal(AONContext ctx, InvoiceFilter filter, InvoiceOrder order);
	Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter);
	java.util.Date getInvoiceExpDate(AONContext ctx, Integer id); 
	List<Invoice> getTbaiDeletedInvoices(AONContext ctx);
	long getInvoiceNewPortalCount(AONContext ctx, InvoiceFilter filter);
	void updateInvoiceNote(AONContext ctx,Integer id, String comment);
}
