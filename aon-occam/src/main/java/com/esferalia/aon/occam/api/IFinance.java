package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocUserData;
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
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public interface IFinance {
	
	// 	****************************************
	// 	**************************** FINANCES ***
	// 	****************************************
	
	Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter);
	Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter, int offset, int limit);
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
	InvoicingGroup save(AONContext ctx, InvoicingGroup invoicingGroup);
	
	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	LinkedList<InvoiceSeries> getInvoiceSeries(AONContext ctx, Date from, Date to, boolean taxDate);
	
	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	public Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter);
	
	public Fee save(AONContext ctx, Fee fee);
	public void deleteFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Stream<Fee> fs);
	
	
	// 	***********************************************
	// 	************************** INVOICE REGISTRY ***
	// 	***********************************************
	public Stream<InvoiceRegistry> getInvoiceRegistries(AONContext ctx, RegistryFilter filter);
	public Stream<Product> getInvoiceProducts(AONContext ctx, ProductFilter filter);
	public Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId);
	
	// 	***********************************************
	// 	************************** UTILITIES ***
	// 	***********************************************
	public FinanceUtilitiesResult missingFinanceInvoices(AONContext ctx,FinanceUtilitiesParams params);
	public Invoice missingFinanceInvoicesFix(AONContext ctx, Integer invoice);
	public FinanceUtilitiesResult financeInvoiceIntegrity(AONContext ctx);
	public Finance financeInvoiceIntegrityFix(AONContext ctx, Finance finance);
	
	public LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice);
	public Finance settleFinance(AONContext ctx, Integer finance);
	public Finance undoFinance(AONContext ctx, Integer finance);
	public FinanceTracking payFinance(AONContext ctx, FinanceTracking tracking);
	public FinanceTracking returnFinance(AONContext ctx, FinanceTracking tracking);
	
	public LinkedList<RegistryBank> getRegistryBanks(AONContext ctx, Integer registry);
	public LinkedList<RegistryBank> getCompanyRegistryBanks(AONContext ctx);
	
	// 	***********************************************
	// 	************************** RAWDOC *************
	// 	***********************************************
	public Stream<Rawdoc> getRawdocStream(AONContext ctx, RawdocFilter filter, int offset, int limit);
	public Stream<Rawdoc> getRawdocFullStream(AONContext ctx, RawdocFilter filter, int offset, int limit);
	public LinkedList<RawdocDomainData> getRawdocDomainData(AONContext ctx, int searchDomain);
	public RawdocUserData getRawdocUserData(AONContext ctx, byte[] auth);
	public RawdocUserData getRawdocUserData(AONContext ctx, int searchDomain);
	public Rawdoc getRawdocFull(AONContext ctx, int id);
	public Rawdoc rawdocSave(AONContext ctx, Rawdoc rawdoc);
	void rawdocDelete(AONContext ctx, RawdocFilter filter);
	void rawdocDelete(AONContext ctx, Integer domain, Integer rawdocId);
	void rawdocToDraft(AONContext ctx, Integer rawdocId);
	void rawdocToRejected(AONContext ctx, Integer rawdocId, String reason);
	void rawdocToInbox(AONContext ctx, Integer rawdocId);
	boolean rawdocHasData(AONContext ctx, Integer rawdocId);
	
	// 	***********************************************
	// 	************************** PAY_METHOD *********
	// 	***********************************************
	public PayMethod getPayMethod(AONContext ctx, String name);
	public LinkedList<PayMethod> getPayMethods(AONContext ctx);
	public PayMethod savePayMethod(AONContext ctx, PayMethod payMethod);
	public void deletePayMethod(AONContext ctx,Integer id);
	
}
	