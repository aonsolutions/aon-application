package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

public class InvoiceImpl implements IInvoice {

	// ***************************** [INVOICE] **
	@Override
	public Invoice save(AONContext ctx, Invoice invoice){
		return ctx.getDslContext().transactionResult(
			configuration -> InvoiceDAO.saveInvoiceAndFinances(ctx, invoice));
	}
	
	@Override
	public Invoice delete(AONContext ctx, Integer invoiceId){
		return ctx.getDslContext().transactionResult(
			configuration -> InvoiceDAO.delete(ctx, invoiceId));
	}
	
}
