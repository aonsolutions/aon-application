package com.esferalia.aon.occam.api;

import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;

public interface IFinance {
	
	
	// 	***********************************************
	// 	*********************************** INVOICE ***
	// 	***********************************************
	void getInvoiceDetails(AONContext ctx,Consumer<InvoiceDetail> action, InvoiceFilter filter);

	
	
}
