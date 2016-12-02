package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.product.Item;

public interface IFinance {
	
	
	// 	****************************************
	// 	**************************** INVOICE ***
	// 	****************************************
	Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter);
	
	Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx,InvoiceFilter filter);
	InvoiceDetail getLastInvoiceDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId);
	InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId);
	LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId);
	LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId, Date date);
	Integer getInvoiceNextNumber(AONContext ctx, Byte[] types, String series);

	Stream<InvoiceDetail> getBoughtProductStream(AONContext ctx, InvoiceFilter filter);
	
	// 	***********************************************
	// 	*************************** INVOICING GROUP ***
	// 	***********************************************

	LinkedList<InvoicingGroup> getInvoicingGroupList(AONContext ctx, InvoicingGroupFilter filter);
	
	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	LinkedList<InvoiceSeries> getInvoiceSeries(AONContext ctx, Date from, Date to, boolean taxDate);
	
	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	public Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter);
	
	public void insertFee(AONContext ctx, Fee f);
	public void insertFee(AONContext ctx, Stream<Fee> fs);	
	public void updateFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Stream<Fee> fs);
}
