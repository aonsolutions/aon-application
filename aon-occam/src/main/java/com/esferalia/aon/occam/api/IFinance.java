package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.PayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.InvoiceCounter;
import com.esferalia.aon.occam.api.model.InvoiceUserData;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetailExtended;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public interface IFinance {
	
	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	List<InvoiceSeries> getInvoiceSeries(AONContext ctx, int domain, Date from, Date to);
	List<InvoiceSeries> getInvoiceSalesSeries(AONContext ctx, int domain);

	//-------------------------------------------
	//-------------------------------------------
	//-------------------------------------------
	
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
	Invoice validateInvoice(AONContext ctx, Invoice invoice, Integer rawdocId);

	Invoice getFullInvoice(AONContext ctx, Integer id);
	List<Invoice>getFullInvoiceList(AONContext ctx, List<Integer> ids);
	Stream<Invoice> getInvoiceHeaders(AONContext ctx, AccountingReportParams params, boolean includeAnnulled, int offset, int limit);
	Stream<Invoice> getInvoiceHeaders(AONContext ctx, InvoiceFilter filter, int offset, int limit);
	Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter);
	Invoice insertInvoice(AONContext ctx, Invoice invoice);
	Invoice updateInvoice(AONContext ctx, Invoice invoice);
	Invoice updateInvoice(AONContext ctx, Invoice invoice, boolean only);
	@Deprecated Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilter filter, Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii);
	Stream<InvoiceDetail> getInvoiceMovements(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter,
			ItemFilter iFilter);
	Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx,InvoiceFilter filter);
	Stream<InvoiceDetailExtended> getInvoiceDetailsExtended(AONContext ctx,InvoiceFilter filter, IDAOCallback callback);
	InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, Integer itemId, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, Integer itemId, Date startDate, Integer workplaceId, Integer warehouseId, Date date);
	LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, Integer itemId, Integer workplaceId, Integer warehouseId, Date date);
	Integer getInvoiceNextNumber(AONContext ctx, Byte[] types, String series);
	Integer getInvoiceMinNumber(AONContext ctx, InvoiceType type, String series);
	Integer getInvoiceMinNumber(AONContext ctx, Byte[] types, String series);
	
	Stream<InvoiceDetail> getBoughtProductStream(AONContext ctx, InvoiceFilter filter);
	
	void rectifyInvoice(AONContext ctx, Integer rectifierInvoice, Integer rectifiedInvoice);
	
	InvoiceCounter getInvoiceCounter(AONContext ctx);
	
	public InvoiceUserData getInvoiceUserData(AONContext ctx, byte[] auth);
	
	// 	***********************************************
	// 	*************************** INVOICING GROUP ***
	// 	***********************************************
	public Stream<InvoicingGroup> getInvoicingGroups(AONContext ctx, Integer domainId);
	public Stream<InvoicingGroup> getInvoicingGroupsByName(AONContext ctx, Integer domainId, String name);
	public Stream<InvoicingGroup> getInvoicingGroupsSuggestion(AONContext ctx, Integer domainId, String query);
	public InvoicingGroup save(AONContext ctx, InvoicingGroup invoicingGroup);
	
	// 	***********************************************
	// 	********************************* CUSTOMER ****
	// 	***********************************************
	/**
	 * @deprecated Use {@link #getCustomersSuggestion(AONContext, Integer, String)} instead
	 * 
	 */
	@Deprecated
	public Map<String, Customer> getFeeCustomersSuggestion(AONContext ctx, int domainId, String query);
	public Stream<Customer> getCustomersSuggestion(AONContext ctx, Integer domainId, String query);

	// 	***********************************************
	// 	************************************* ITEM ****
	// 	***********************************************
	Stream<Item> getItemsSuggestion(AONContext ctx, Integer domainId, String query);

	// 	***********************************************
	// 	**************************** INVOICE SERIES ***
	// 	***********************************************
	
	public Map<String, Workplace> getWorkplacesSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Seller> getSellersSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Project> getProjectsSuggestion(CloseableAONContext ctx, int domainId, Integer customerId, String query);
	
	public Map<String, Fee> getCustomerFeeSuggestion(CloseableAONContext ctx, int domainId, Integer itemId, Integer customerId, String customerFeeQuery);
	
	public void reorderCustomerFeeLine(CloseableAONContext ctx, int domainId, Integer customer);
	
	public Map<String, OldItem> getProductsSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Integer> getProductCategoriesSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<String, Integer> getProductTagsSuggestion(CloseableAONContext ctx, int domainId, String query);
	public Map<Integer, Integer> getCustomerProductsUpdates(CloseableAONContext ctx, int domainId, CustomerFeeParams customerFeeParams);
	
	public LinkedList<Fee> getFeeList(CloseableAONContext ctx, CustomerFeeParams customerFeeParams);
	public LinkedList<Fee> getFullFeeList(CloseableAONContext ctx, CustomerFeeParams customerFeeParams);
	public Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter);
	
	public Fee save(AONContext ctx, Fee fee);
	public Integer saveList(AONContext ctx, LinkedList<Fee> feeList);
	public Integer saveMassiveFees(AONContext ctx, Fee fee, CustomerFeeParams customerFeeParams);
	public Fee createCustomerFeeList(AONContext ctx, Fee fee);
	public void updateRitemCustomerFee(CloseableAONContext ctx, Integer customerFee, Integer ritem);
	public void deleteFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Stream<Fee> fs);
	public void deleteFee(AONContext ctx,CustomerFeeParams customerFeeParams);
	
	public Map<Integer, Integer> getMinMaxCustomerFeeYear(CloseableAONContext ctx, int domainId);
	
	public Integer getItemIdByProductCode(CloseableAONContext ctx, int domainId, String productCode);
	
	// 	***********************************************
	// 	************************** INVOICE REGISTRY ***
	// 	***********************************************
	public Stream<InvoiceRegistry> getInvoiceRegistries(AONContext ctx, Integer domainId, String query);
	public Stream<Product> getInvoiceProducts(AONContext ctx, ProductFilter filter);
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
	public FinanceUtilitiesResult activityIntegrity(AONContext ctx, Integer domain);
	public void activityIntegrityFix(AONContext ctx, Integer invoiceId, boolean useInvoiceActivity);

	public LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice);
	public Finance settleFinance(AONContext ctx, Integer finance);
	public Finance unSettleFinance(AONContext ctx, Integer finance);
	public Finance undoFinance(AONContext ctx, Integer finance);
	public FinanceTracking payFinance(AONContext ctx, FinanceTracking tracking);
	public FinanceTracking returnFinance(AONContext ctx, FinanceTracking tracking);
	void deleteFinance(CloseableAONContext ctx, Integer financeId);
	
	public LinkedList<RegistryBank> getRegistryBanks(AONContext ctx, Integer registry);
	public LinkedList<RegistryBank> getCompanyRegistryBanks(AONContext ctx);
	
	
	// 	***********************************************
	// 	************************** PAY_METHOD *********
	// 	***********************************************
	public PayMethod getPayMethod(AONContext ctx, PayMethodFilter filter);
	public LinkedList<PayMethod> getPayMethods(AONContext ctx);
	public LinkedList<PayMethod> getPayMethods(AONContext ctx, PayMethodParams params);
	public PayMethod savePayMethod(AONContext ctx, PayMethod payMethod);
	public void deletePayMethod(AONContext ctx,Integer id);
	public void groupPayMethod(AONContext ctx, List<PayMethod> selectedPaymethodList, PayMethod groupedPaymthod);
	
	// 	***********************************************
	// 	********* PRINT INVOICE CONFIGURATION *********
	// 	***********************************************

	public PrintInvoiceConfiguration getPrintInvoiceConfiguration(AONContext ctx, Boolean withData);
	public PrintInvoiceConfiguration getPrintInvoiceConfiguration(AONContext ctx, Integer officeDomain, Boolean withData);
	public PrintInvoiceConfiguration savePrintInvoiceConfiguration(AONContext ctx, PrintInvoiceConfiguration pic);
	
	// 	***********************************************
	// 	********** INVOFOX CONFIGURATION **************
	// 	***********************************************

	public InvofoxConfiguration getInvofoxConfiguration(AONContext ctx);
	public InvofoxConfiguration saveInvofoxConfiguration(AONContext ctx, InvofoxConfiguration config);

	// 	********************************************
	// 	********** INVOICE COMMUNICATION ***********
	// 	********************************************
	
	public Stream<Invoice> getCommunicationInvoices(AONContext ctx, InvoiceCommunicationParams params);
	public InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration(AONContext ctx, int invoice, boolean check);
	public InvoiceCommunicationConfiguration saveInvoiceCommunicationConfiguration(AONContext ctx, int domainId , InvoiceCommunicationConfiguration config);
	public void prepareNewSii(AONContext ctx);
	public void prepareNewSii(AONContext ctx, Integer year);
	
	// 	***********************************************
	// 	***************** INVOICE FISCAL **************
	// 	***********************************************
	public void saveInvoiceFiscal(AONContext ctx, AonConfiguration config, Invoice invoice);
	public void deleteInvoiceFiscal(AONContext ctx, Integer id);
	
	public void saveFacturaeCodeAsignacion(AONContext ctx, Integer invoice, Integer registry, String code);

	public Stream<InvoiceData> getInvoiceDataStream(AONContext ctx, InvoiceDataFilter filter);
	public InvoiceData getInvoiceData(AONContext ctx, InvoiceDataFilter filter);
	public InvoiceData saveInvoiceData(AONContext ctx, InvoiceData invoiceData);
	public void deleteInvoiceData(AONContext ctx, Integer invoiceId);
	
	public Optional<InvoiceInfo> getInvoiceInfo(AONContext ctx, Integer invoiceId, InvoiceCommunicationType type);
	public InvoiceInfo saveInvoiceInfo(AONContext ctx, InvoiceInfo invoiceInfo);
	public void deleteInvoiceInfo(AONContext ctx, Integer invoiceId);
	
	public Stream<InvoiceCommunicationTracking> getInvoiceCommunicationTrackings(AONContext ctx, Integer invoiceId);
	public InvoiceCommunicationTracking saveInvoiceCommunicationTracking(AONContext ctx, InvoiceCommunicationTracking invoiceCommunicationTracking);
	public void deleteInvoiceCommunicationTracking(AONContext ctx, Integer invoiceId);
	
	// 	***********************************************
	// 	***************** INVOICE BATCH ***************
	// 	***********************************************
	
	public InvoiceBatch saveInvoiceBatch(AONContext ctx, InvoiceBatch invoiceBatch);
	public InvoiceBatchDetail saveInvoiceBatchDetail(AONContext ctx, InvoiceBatchDetail invoiceBatchDetail);
	
	// 	***********************************************
	// 	***************** BOOKING CHECK ***************
	// 	***********************************************

	LinkedList<BookingCheck> getBookingWithoutFeeList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getFeeWithoutBookingList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getCustomerBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params);
	LinkedList<BookingCheck> getCustomerChildBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params);
	void saveBookingCheck(CloseableAONContext ctx, BookingCheck bookingCheck);
	void deleteBookingList(CloseableAONContext ctx, LinkedList<BookingCheck> selectedBookings);
	
	// 	***********************************************
	// 	*********** VENCIMIENTO NOMINAS ***************
	// 	***********************************************
	
	void createSettleSalaries(CloseableAONContext ctx, Date date);
	Integer createSepaFile(CloseableAONContext ctx, Integer fbatchId);
	
	LinkedList<FBatch> getFBatches(CloseableAONContext ctx, FBatchFilter filter, int offset, int limit);
	FBatch getFBatch(CloseableAONContext ctx, Integer fbatchId);
	void deleteFBatches(CloseableAONContext ctx, LinkedList<Integer> fBatchIds);
	FBatch createUpdateFBatch(CloseableAONContext ctx, FBatch fBatch);
	FBatch recordFBatch(AONContext ctx, Integer fBatchId, Date paymentDate);
	FBatch unrecordFBatch(AONContext ctx, Integer fBatchId);
	AccountEntry getFBatchAccountEntry(AONContext ctx, Integer fBatchId);

	
	// 	***********************************************
	// 	*********** COBROS Y PAGOS CARD ***************
	// 	***********************************************
	
	Double getFinanceGroupStatus(CloseableAONContext ctx, FinanceFilter filter);
	Optional<Item> getLastItem(AONContext ctx, Integer registry);
	
	// 	***********************************************
	// 	************** INVOICE CLOSING ****************
	// 	***********************************************
	
	Stream<InvoiceBatch> getInvoiceClosing(CloseableAONContext ctx);
	void saveInvoiceClosing(CloseableAONContext ctx, InvoiceBatch invoiceBatch);
	

	// 	***********************************************
	// 	************** INVOICE CONSOLE ****************
	// 	***********************************************
	List<InvoiceConsole> getInvoiceHeaders(AONContext ctx, InvoiceConsoleParams params);
	
	// 	****************************************
	// 	************************ INVOICE DOC ***
	// 	****************************************
	Optional<InvoiceDoc> getInvoiceDoc(AONContext ctx, int domain, Integer invoiceId);	
	void saveInvoiceDoc(AONContext ctx, InvoiceDoc invoiceDoc);
	
	// ********************************************
	// ********************************** SERIES **
	// ********************************************
	Stream<Series> getSeriesSuggestion(AONContext ctx, Integer domainId, String query);
	
	void fixInvoice(AONContext ctx, int domain);
}
	