package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.Item;

public interface IFinance {
	
	
	// 	***********************************************
	// 	*********************************** INVOICE ***
	// 	***********************************************
	Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx,InvoiceFilter filter);

	InvoiceDetail getLastInvoiceDetail(AONContext ctx, Item item);
	
	LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, Item item, Date startDate);

}
