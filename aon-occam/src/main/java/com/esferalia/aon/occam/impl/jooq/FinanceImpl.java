package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFinance;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

public class FinanceImpl implements IFinance {

	// ------------------------------------- INVOICE DETAIL
	
	@Override
	public Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter) {
		return InvoiceDAO.getInvoiceDetails(ctx, filter);
	}

	@Override
	public InvoiceDetail getLastInvoiceDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId) {
		return InvoiceDAO.getLastInvoiceDetail(ctx, item, workplaceId, warehouseId);
	}
	
	@Override
	public InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date) {
		return InvoiceDAO.getLastInvoiceDetailUntilDate(ctx, item, workplaceId, warehouseId, date);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId) {
		return InvoiceDAO.getLastInvoiceDetailList(ctx, item, startDate, workplaceId, warehouseId);
	}

	@Override
	public LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId, Date date) {
		return InvoiceDAO.getLastInvoiceDetailListUntilDate(ctx, item, startDate, workplaceId, warehouseId, date);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, Item item, Integer workplaceId,
			Integer warehouseId) {
		return InvoiceDAO.getInvoiceDetailList(ctx, item, workplaceId, warehouseId);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, Item item, Integer workplaceId,
			Integer warehouseId, Date date) {
		return InvoiceDAO.getInvoiceDetailListUntilDate(ctx, item, workplaceId, warehouseId, date);
	}
	
	@Override
	public Integer getInvoiceNextNumber(AONContext ctx, Byte[] types, String series) {
		return InvoiceDAO.getNextNumber(ctx, types, series);
	}
	// ------------------------------------- INVOICING GROUP
	
	@Override
	public LinkedList<InvoicingGroup> getInvoicingGroupList(AONContext ctx, InvoicingGroupFilter filter){
		return InvoiceDAO.getInvoicingGroupList(ctx, filter);
	}

	// ------------------------------------- INVOICE SERIES
	@Override
	public LinkedList<InvoiceSeries> getInvoiceSeries(AONContext ctx, Date from, Date to, boolean taxDate) {
		return InvoiceDAO.getInvoiceSeries(ctx, from, to, taxDate);
	}

}
