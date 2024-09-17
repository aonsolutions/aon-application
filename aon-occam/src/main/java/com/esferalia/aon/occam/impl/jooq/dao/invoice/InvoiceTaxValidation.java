package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;

class InvoiceTaxValidation {
	
	private InvoiceTaxValidation() {
		
	}
	
	static void validate(AONContext ctx, Invoice inv, InvoiceDetail detail, InvoiceTax tax) throws AonCoreException {
		if ( AonNumberUtils.notEquals( detail.getId(), tax.getInvoiceDetail()) ) {
			inv.addMessage( InvoiceErrorMessages.C511.err(InvoiceErrorKey.TAX_RATE, "InvoiceTax", "InvoiceDetail") );
			throw new AonCoreException(AonError.INVOICE_SAVE_ERROR.getMessage());
		}
		
		if ( AonNumberUtils.notEquals( detail.getDomain(), tax.getDomain()) ) {
			inv.addMessage( InvoiceErrorMessages.C510.err(InvoiceErrorKey.TAX_RATE, "InvoiceTax", "InvoiceDetail") );
			throw new AonCoreException(AonError.INVOICE_SAVE_ERROR.getMessage());
		}
		
	}

}
