package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;

public interface IFinance {
	
	// 	****************************************
	// 	**************************** FINANCES ***
	// 	****************************************
	
	Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter);
	Finance insertFinance(AONContext ctx, Finance finance);
	Stream<Finance> getSiiFinanceStream(AONContext ctx, FinanceFilter filter);
	LinkedList<FinanceTracking> getFinanceTracking(AONContext ctx, Integer finance);
	
	// 	****************************************
	// 	**************************** INVOICE ***
	// 	****************************************
	Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter);
	Invoice insertInvoice(AONContext ctx, Invoice invoice);
	InvoiceDetail insertInvoiceDetail(AONContext ctx, InvoiceDetail invoiceDetail);
	Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilter filter, Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii);
	Stream<InvoiceDetail> getInvoiceMovements(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter,
			ItemFilter iFilter);

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
	
	
	// 	***********************************************
	// 	************************** INVOICE REGISTRY ***
	// 	***********************************************
	public Stream<InvoiceRegistry> getInvoiceRegistries(AONContext ctx, RegistryFilter filter);
	public Stream<Product> getInvoiceProducts(AONContext ctx, ProductFilter filter);
	
	public PayMethod getPayMethod(AONContext ctx, String name);

	public Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId);
	
	// 	***********************************************
	// 	************************** UTILITIES ***
	// 	***********************************************
	public FinanceUtilitiesResult missingFinanceInvoices(AONContext ctx,FinanceUtilitiesParams params);
	public Invoice missingFinanceInvoicesFix(AONContext ctx, Integer invoice);
	public LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice);
	
}
	