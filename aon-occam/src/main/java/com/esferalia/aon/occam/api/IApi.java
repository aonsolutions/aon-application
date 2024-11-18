package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;
import com.esferalia.aon.watson.util.Pair;

public interface IApi {

	Stream<InvoiceNewPortal> getInvoiceNewPortal(AONContext ctx, InvoiceFilter filter);
	Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter);
	Pair<Date, Date> getInvoicesChartPeriod(AONContext ctx, InvoiceFilter filter);
	Integer getInvoicesCount(AONContext ctx, InvoiceFilter filter);
	java.util.Date getInvoiceExpDate(AONContext ctx, Integer id); 

}
