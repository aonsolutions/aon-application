package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IApi;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilterOLD;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;
import com.esferalia.aon.occam.impl.jooq.dao.api.InvoiceApiDAO;

public class ApiImpl implements IApi {

	@Override
	public Stream<InvoiceNewPortal> getInvoiceNewPortal(AONContext ctx, InvoiceFilterOLD filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoiceNewPortal(ctx, filter));
	}

	@Override
	public Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilterOLD filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoices(ctx, filter));
	}
	
	@Override
	public Date getInvoiceExpDate(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoiceExpDate(ctx, id));
	}

}
