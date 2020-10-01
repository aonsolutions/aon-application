package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;

public interface IApi {

	Stream<JSONObject> getDataResponseInvoices(AONContext ctx, DataResponseFilter filter);
	Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter);
	void deleteInvoice(AONContext ctx, Integer id);
	Invoice insertInvoice(AONContext ctx, Invoice invoice);
	
}
