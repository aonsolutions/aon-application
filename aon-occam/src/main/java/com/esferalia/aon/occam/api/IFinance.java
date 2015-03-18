package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;

public interface IFinance {
	
	
	// 	***********************************************
	// 	*********************************** INVOICE ***
	// 	***********************************************
	Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx,InvoiceFilter filter);

	
	
}
