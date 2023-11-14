package com.esferalia.aon.occam.api;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.InvoiceRawDocFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceAndRaw;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;

public interface IApi {

	Stream<InvoiceAndRaw> getInvoiceAndRaw(AONContext ctx, InvoiceFilter filter, InvoiceRawDocFilter filterRawdoc);
	Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter);
	List<Invoice> getTbaiDeletedInvoices(AONContext ctx);

}
