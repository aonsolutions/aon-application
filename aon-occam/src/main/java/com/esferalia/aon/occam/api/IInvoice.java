package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public interface IInvoice {
	
	Invoice save(AONContext ctx, Invoice invoice);
	Invoice delete(AONContext ctx, Integer invoiceId);
	
}
	