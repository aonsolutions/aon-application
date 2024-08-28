package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IApi;
import com.esferalia.aon.occam.api.model.Order.InvoiceOrder;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;
import com.esferalia.aon.occam.impl.jooq.dao.api.InvoiceApiDAO;

public class ApiImpl implements IApi {

	@Override
	public Stream<InvoiceNewPortal> getInvoiceNewPortal(AONContext ctx, InvoiceFilter filter, InvoiceOrder order) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoiceNewPortal(ctx, filter, order));
	}

	@Override
	public Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoices(ctx, filter));
	}
	
	@Override
	public Date getInvoiceExpDate(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceApiDAO.getInvoiceExpDate(ctx, id));
	}
	
	@Override
	public List<Invoice> getTbaiDeletedInvoices(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceServalFixDAO.getTbaiDeletedInvoices(ctx));
	}
	
	@Override
	public long getInvoiceNewPortalCount(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().transactionResult(
					configuration -> InvoiceApiDAO.getInvoiceNewPortalCount(ctx, filter));
				
	}
	@Override
	public void updateInvoiceNote(AONContext ctx,Integer id, String comment) {
		ctx.getDslContext().transaction(
				configuration -> InvoiceApiDAO.updateInvoiceNote(ctx, id,comment));
	}

}
