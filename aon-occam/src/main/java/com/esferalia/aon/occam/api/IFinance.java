package com.esferalia.aon.occam.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.PayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceTracking;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public interface IFinance {
	
	// 	****************************************
	// 	**************************** FINANCES ***
	// 	****************************************
	
	Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter);
	Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter, int offset, int limit);
	Finance insertFinance(AONContext ctx, Finance finance);
	Finance saveFinance(AONContext ctx, Finance finance);
	Stream<Finance> getSiiFinanceStream(AONContext ctx, FinanceFilter filter);
	LinkedList<FinanceTracking> getFinanceTracking(AONContext ctx, Integer finance);
	
	// 	****************************************
	// 	**************************** INVOICE ***
	// 	****************************************
	Invoice getLastSaleInvoice(AONContext ctx, String serie);
	Invoice getInvoice(AONContext ctx, Integer id);
	void deleteInvoice(AONContext ctx, Integer invoiceId);
	
	Invoice acceptInvoice(AONContext ctx, Invoice invoice, Integer rawdocId);
	Invoice getFullInvoice(AONContext ctx, Integer id);
	Stream<Invoice> getInvoiceHeaders(AONContext ctx, AccountingReportParams params, int offset, int limit);
	Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter);
	Invoice insertInvoice(AONContext ctx, Invoice invoice);
	Invoice updateInvoice(AONContext ctx, Invoice invoice);
	Invoice updateInvoice(AONContext ctx, Invoice invoice, boolean only);
	InvoiceDetail insertInvoiceDetail(AONContext ctx, InvoiceDetail invoiceDetail);
	Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilter filter, Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii);
	Stream<InvoiceDetail> getInvoiceMovements(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter,
			ItemFilter iFilter);

	Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx,InvoiceFilter filter);
	ArrayList<InvoiceDetail> getInvoiceDetailsList(AONContext ctx,InvoiceFilter filter);
	InvoiceDetail getLastInvoiceDetail(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId);
	InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId);
	LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId);
	LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date);
	Integer getInvoiceNextNumber(AONContext ctx, Byte[] types, String series);
	Integer getInvoiceMinNumber(AONContext ctx, InvoiceType type, String series);
	Integer getInvoiceMinNumber(AONContext ctx, Byte[] types, String series);
	
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
	List<InvoiceSeries> getInvoiceSalesSeries(AONContext ctx);
	
	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	
	public Map<String, Workplace> getWorkplacesSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Seller> getSellersSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Project> getProjectsSuggestion(CloseableAONContext ctx, int domainId, Integer customerId, String query);

	public Map<String, Customer> getCustomersSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, OldItem> getProductsSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Integer> getProductCategoriesSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Integer> getProductTagsSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<Integer, Integer> getCustomerProductsUpdates(CloseableAONContext ctx, int domainId, CustomerFeeParams customerFeeParams);
	
	public LinkedList<Fee> getFeeList(CloseableAONContext ctx, CustomerFeeParams customerFeeParams);
	public Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter);
	
	public Fee save(AONContext ctx, Fee fee);
	public Integer saveList(AONContext ctx, LinkedList<Fee> feeList);
	public Integer saveMassiveFees(AONContext ctx, Fee fee, CustomerFeeParams customerFeeParams);
	public void createCustomerFeeList(AONContext ctx, Fee fee);
	public void deleteFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Stream<Fee> fs);
	public void deleteFee(AONContext ctx,CustomerFeeParams customerFeeParams);
	
	public Map<Integer, Integer> getMinMaxCustomerFeeYear(CloseableAONContext ctx, int domainId);
	
	public Integer getItemIdByProductCode(CloseableAONContext ctx, int domainId, String productCode);
	
	// 	***********************************************
	// 	************************** INVOICE REGISTRY ***
	// 	***********************************************
	public Stream<InvoiceRegistry> getInvoiceRegistries(AONContext ctx, RegistryFilter filter);
	public Stream<OldProduct> getInvoiceProducts(AONContext ctx, ProductFilter filter);
	public Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId);
	
	// 	***********************************************
	// 	************************** UTILITIES ***
	// 	***********************************************
	public void updateActivity(AONContext ctx, Integer invoiceId, Integer activity);
	public void updateWithholdingType(AONContext ctx, Integer invoiceId, WithholdingType newType);
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
	public PayMethod getPayMethod(AONContext ctx, PayMethodFilter filter);
	public LinkedList<PayMethod> getPayMethods(AONContext ctx);
	public PayMethod savePayMethod(AONContext ctx, PayMethod payMethod);
	public void deletePayMethod(AONContext ctx,Integer id);
	
	// 	***********************************************
	// 	********* PRINT INVOICE CONFIGURATION *********
	// 	***********************************************

	public PrintInvoiceConfiguration getPrintInvoiceConfiguration(AONContext ctx, Boolean withData);
	public PrintInvoiceConfiguration savePrintInvoiceConfiguration(AONContext ctx, PrintInvoiceConfiguration pic);
	
	// 	***********************************************
	// 	********** TICKET BAI CONFIGURATION ***********
	// 	***********************************************

	public TbaiConfiguration getTbaiConfiguration(AONContext ctx);
	public TbaiConfiguration saveTbaiConfiguration(AONContext ctx, TbaiConfiguration config);
	
	// 	***********************************************
	// 	************* SII CONFIGURATION ***************
	// 	***********************************************

	public SiiConfiguration getSiiConfiguration(AONContext ctx);
	public SiiConfiguration saveSiiConfiguration(AONContext ctx, SiiConfiguration config);

	
	// 	***********************************************
	// 	***************** INVOICE FISCAL **************
	// 	***********************************************
	public void saveInvoiceFiscal(AONContext ctx, AonConfiguration config, Invoice invoice);
	public void deleteInvoiceFiscal(AONContext ctx, Integer id);
	
	public void saveFacturaeCodeAsignacion(AONContext ctx, Integer invoice, Integer registry, String code);
	
	public InvoiceInfo getInvoiceInfo(AONContext ctx, InvoiceInfoFilter filter);
	public InvoiceInfo saveInvoiceInfo(AONContext ctx, InvoiceInfo invoiceInfo);
	public void deleteInvoiceInfo(AONContext ctx, Integer invoiceId);
	
	public Stream<InvoiceTracking> getInvoiceTrackingStream(AONContext ctx, InvoiceTrackingFilter filter);
	public List<InvoiceTracking> getInvoiceTrackingList(AONContext ctx, InvoiceTrackingFilter filter);
	public InvoiceTracking getInvoiceTracking(AONContext ctx, InvoiceTrackingFilter filter);
	public InvoiceTracking saveInvoiceTracking(AONContext ctx, InvoiceTracking invoiceTracking);
	public void deleteInvoiceTracking(AONContext ctx, Integer invoiceId);
	
	// 	***********************************************
	// 	***************** BOOKING CHECK ***************
	// 	***********************************************

	LinkedList<BookingCheck> getBookingWithoutFeeList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getFeeWithoutBookingList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getCustomerBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params);
	void saveBookingCheck(CloseableAONContext ctx, BookingCheck bookingCheck);
	void deleteBookingList(CloseableAONContext ctx, LinkedList<BookingCheck> selectedBookings);
	
	// 	***********************************************
	// 	*********** VENCIMIENTO NOMINAS ***************
	// 	***********************************************
	
	void createSettleSalaries(CloseableAONContext ctx, Date date);

}
	