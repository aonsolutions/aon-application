package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFinance;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

public class FinanceImpl implements IFinance {

	@Override
	public Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter) {
		return InvoiceDAO.getInvoiceDetails(ctx, filter);
	}

	@Override
	public InvoiceDetail getLastInvoiceDetail(AONContext ctx, Item item) {
		return InvoiceDAO.getLastInvoiceDetail(ctx, item);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, Item item, Date startDate) {
		return InvoiceDAO.getLastInvoiceDetailList(ctx, item, startDate);
	}
}
