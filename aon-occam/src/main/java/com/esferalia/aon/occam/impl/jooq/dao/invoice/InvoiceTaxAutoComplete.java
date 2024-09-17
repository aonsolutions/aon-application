package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.watson.error.AonCoreException;

class InvoiceTaxAutoComplete {
	
	private InvoiceTaxAutoComplete() {
		
	}
	
	static void complete(AONContext ctx, Invoice inv, InvoiceDetail detail, InvoiceTax tax) throws AonCoreException {
		tax.setDomain(detail.getDomain())
			.setInvoiceDetail(detail.getId());
	}

}
