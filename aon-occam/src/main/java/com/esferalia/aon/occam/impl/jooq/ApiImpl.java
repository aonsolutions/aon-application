package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IApi;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.impl.jooq.dao.api.InvoiceApiDAO;

public class ApiImpl implements IApi {

	@Override
	public Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoices(ctx, filter));
	}

	@Override
	public void deleteInvoice(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> InvoiceApiDAO.deleteInvoice(ctx, id));
	}

	@Override
	public Invoice insertInvoice(AONContext ctx, Invoice invoice) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.insertInvoice(ctx, invoice));
	}

	
}
