package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFinance;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
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
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceUtilitiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

public class FinanceImpl implements IFinance {

	// ------------------------------------- INVOICE
	
	@Override
	public Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getInvoiceStream(ctx, filter));
	}
	
	@Override
	public Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilter filter, Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getSiiInvoiceStream(ctx, filter, pending, aceptada, aceptadaErrores,incorrecta, anulada, sii));
	}
		
	@Override
	public Stream<InvoiceDetail> getInvoiceMovements(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter, ItemFilter iFilter) {
		return InvoiceDAO.getInvoiceDetails(ctx, filter, pFilter, iFilter);
	}

	
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
	
	// ------------------------------------- FEE
	
	@Override
	public Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getFeeStream(ctx, filter));
	}
	
	@Override
	public void insertFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.insert(ctx, f);
		} );		
	}

	@Override
	public void insertFee(AONContext ctx, Stream<Fee> fs) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.insert(ctx, fs);
		} );			
	}

	@Override
	public void updateFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.update(ctx, f);
		} );			
	}

	@Override
	public void deleteFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, f);
		} );			
	}

	@Override
	public void deleteFee(AONContext ctx, Stream<Fee> fs) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, fs);
		} );			
	}

	@Override
	public Stream<InvoiceDetail> getBoughtProductStream(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration
				-> InvoiceDAO.getBoughtProductStream(ctx, filter));
	}

	@Override
	public Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceDAO.getFinanceStream(ctx,filter));

	}
	
	@Override
	public Stream<Finance> getSiiFinanceStream(AONContext ctx, FinanceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceDAO.getSiiFinanceStream(ctx,filter));

	}

	@Override
	public Stream<InvoiceRegistry> getInvoiceRegistries(AONContext ctx, RegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getInvoiceRegistries(ctx, filter));
	}

	@Override
	public Stream<Product> getInvoiceProducts(AONContext ctx, ProductFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getInvoiceProducts(ctx, filter));
	}

	@Override
	public PayMethod getPayMethod(AONContext ctx, String name) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> FinanceDAO.getPayMethod(ctx, name));
	}

	@Override
	public Invoice insertInvoice(AONContext ctx, Invoice invoice) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InvoiceDAO.insert(ctx, invoice));		
	}

	@Override
	public InvoiceDetail insertInvoiceDetail(AONContext ctx, InvoiceDetail invoiceDetail) {
		return ctx.getDslContext().transactionResult(configuration -> 
		InvoiceDAO.insertInvoiceDetail(ctx, invoiceDetail));		
	}

	@Override
	public Finance insertFinance(AONContext ctx, Finance finance) {
			
		return ctx.getDslContext().transactionResult(configuration -> 
		FinanceDAO.insertFinance(ctx, finance));
	}

	@Override
	public Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext().transactionResult(configuration
				-> InvoiceDAO.getInvoiceTaxStream(ctx, invoiceId));
	}

	// UTILITIES
	@Override
	public FinanceUtilitiesResult missingFinanceInvoices(AONContext ctx,FinanceUtilitiesParams params) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceUtilitiesDAO.missingFinanceInvoices( ctx , params));
	}
	@Override
	public Invoice missingFinanceInvoicesFix(AONContext ctx,Integer invoice) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceUtilitiesDAO.missingFinanceInvoicesFix( ctx , invoice));
	}
	
	
}
