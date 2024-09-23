package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.IFinance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceCommunicationTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDataFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.PayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.InvoiceCounter;
import com.esferalia.aon.occam.api.model.Order.RawdocOrder;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetailExtended;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.BookingCheckDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FBatchDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceUtilitiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvofoxConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceFiscalDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceSIIDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PrintInvoiceConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SettleSalariesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SiiConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TbaiConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;

public class FinanceImpl implements IFinance {

	// ------------------------------------- INVOICE
	@Override
	public Optional<Item> getLastItem(AONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(
			configuration -> InvoiceDAO.getLastItem(ctx, registry));
	}
	
	@Override
	public Invoice getLastSaleInvoice(AONContext ctx, String serie) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getLastSaleInvoice(ctx, serie));
	}
	
	@Override
	public Invoice getInvoice(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getInvoice(ctx, id));
	}
	
	@Override
	public void deleteInvoice(AONContext ctx, Integer invoiceId) {
		ctx.getDslContext().transaction(configuration -> 
			InvoiceDAO.delete(ctx, invoiceId));
	}
	
	@Override
	public Invoice getFullInvoice(AONContext ctx, Integer id){
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getFullInvoice(ctx, id));
	}
	
	@Override
	public Invoice acceptInvoice(AONContext ctx, Invoice invoice, Integer rawdocId){
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.accept(ctx, invoice, rawdocId));
	}
	
	@Override
	public Invoice validateInvoice(AONContext ctx, Invoice invoice, Integer rawdocId){
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.validate(ctx, invoice, rawdocId));
	}
	
	@Override
	public Stream<Invoice> getInvoiceHeaders(AONContext ctx, InvoiceFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(
			configuration -> InvoiceDAO.getInvoiceHeaders(ctx, filter, offset, limit));
	}
	@Override
	public Stream<Invoice> getInvoiceHeaders(AONContext ctx, AccountingReportParams params, int offset, int limit) {
		return ctx.getDslContext().transactionResult(
			configuration -> InvoiceDAO.getInvoiceHeaders(ctx, params, offset, limit));
	}
	@Override
	public Stream<Invoice> getInvoiceStream(AONContext ctx, InvoiceFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getInvoiceStream(ctx, filter));
	}
	
	@Override
	public Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilter filter, Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceSIIDAO.getSiiInvoiceStream(ctx, filter, pending, aceptada, aceptadaErrores,incorrecta, anulada, sii));
	}
		
	@Override
	public Stream<InvoiceDetail> getInvoiceMovements(AONContext ctx, InvoiceFilter filter, ProductFilter pFilter, ItemFilter iFilter) {
		return InvoiceDAO.getInvoiceDetails(ctx, filter, pFilter, iFilter);
	}

	@Override
	public void rectifyInvoice(AONContext ctx, Integer rectifierInvoice, Integer rectifiedInvoice) {
		ctx.getDslContext().transaction(configuration -> InvoiceDAO.rectify(ctx, rectifierInvoice, rectifiedInvoice));
	}
	
	@Override
	public InvoiceCounter getInvoiceCounter(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDAO.getCounter(ctx));
	}
	
	
	// ------------------------------------- INVOICE DETAIL
	
	@Override
	public Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter) {
		return InvoiceDAO.getInvoiceDetails(ctx, filter);
	}
	
	@Override
	public Stream<InvoiceDetailExtended> getInvoiceDetailsExtended(AONContext ctx, InvoiceFilter filter, IDAOCallback callback) {
		return InvoiceDAO.getInvoiceDetailsExtended(ctx, filter, callback);
	}

	@Override
	public InvoiceDetail getLastInvoiceDetail(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId) {
		return InvoiceOLDDAO.getLastInvoiceDetail(ctx, item, workplaceId, warehouseId);
	}
	
	@Override
	public InvoiceDetail getLastInvoiceDetailUntilDate(AONContext ctx, OldItem item, Integer workplaceId, Integer warehouseId, Date date) {
		return InvoiceOLDDAO.getLastInvoiceDetailUntilDate(ctx, item, workplaceId, warehouseId, date);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getLastInvoiceDetailList(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId) {
		return InvoiceOLDDAO.getLastInvoiceDetailList(ctx, item, startDate, workplaceId, warehouseId);
	}

	@Override
	public LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(AONContext ctx, OldItem item, Date startDate, Integer workplaceId, Integer warehouseId, Date date) {
		return InvoiceOLDDAO.getLastInvoiceDetailListUntilDate(ctx, item, startDate, workplaceId, warehouseId, date);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getInvoiceDetailList(AONContext ctx, OldItem item, Integer workplaceId,
			Integer warehouseId) {
		return InvoiceOLDDAO.getInvoiceDetailList(ctx, item, workplaceId, warehouseId);
	}
	
	@Override
	public LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(AONContext ctx, OldItem item, Integer workplaceId,
			Integer warehouseId, Date date) {
		return InvoiceOLDDAO.getInvoiceDetailListUntilDate(ctx, item, workplaceId, warehouseId, date);
	}

	@Override
	public Integer getInvoiceMinNumber(AONContext ctx, Byte[] types, String series) {
		return InvoiceDAO.getMinNumber(ctx, types, series);
	}
	
	@Override
	public Integer getInvoiceMinNumber(AONContext ctx, InvoiceType type, String series) {
		return InvoiceDAO.getMinNumber(ctx, type, series);
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

	@Override
	public InvoicingGroup save(AONContext ctx, InvoicingGroup invoicingGroup) {
		return ctx.getDslContext().transactionResult(configuration
				-> InvoiceDAO.save(ctx, invoicingGroup));
	}
	
	// ------------------------------------- INVOICE SERIES
	@Override
	public List<InvoiceSeries> getInvoiceSeries(AONContext ctx, int domain, Date from, Date to) {
		return InvoiceDAO.getInvoiceSeries(ctx, domain, from, to)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public List<InvoiceSeries> getInvoiceSalesSeries(AONContext ctx, int domain) {
		return InvoiceDAO.getInvoiceSeries(ctx, domain, null, null)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	// ------------------------------------- FEE
	
	@Override
	public Map<String, Workplace> getWorkplacesSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getWorkplacesSuggestion(ctx, domainId, query));
	}

	@Override
	public Map<String, Seller> getSellersSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getSellersSuggestion(ctx, domainId, query));
	}

	@Override
	public Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getInvoicingGroupsSuggestion(ctx, domainId, query));
	}
	
	@Override
	public Map<String, Project> getProjectsSuggestion(CloseableAONContext ctx, int domainId, Integer customerId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getProjectsSuggestion(ctx, domainId, customerId, query));
	}
	
	@Override
	public Map<String, Fee> getCustomerFeeSuggestion(CloseableAONContext ctx, int domainId, Integer itemId, Integer customerId, String customerFeeQuery) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getCustomerFeeSuggestion(ctx, domainId, itemId, customerId, customerFeeQuery));
	}
	
	@Override
	public void reorderCustomerFeeLine(CloseableAONContext ctx, int domainId, Integer customer) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.reorderCustomerFeeLine(ctx, domainId, customer);
		});
	}
	
	@Override
	public Map<String, Customer> getCustomersSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getCustomersSuggestion(ctx, domainId, query));
	}

	@Override
	public Map<String, OldItem> getProductsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getProductsSuggestion(ctx, domainId, query));
	}
	
	@Override
	public Map<String, Integer> getProductCategoriesSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getProductCategoriesSuggestion(ctx, domainId, query));
	}

	@Override
	public Map<String, Integer> getProductTagsSuggestion(CloseableAONContext ctx, int domainId, String query) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getProductTagsSuggestion(ctx, domainId, query));
	}
	
	@Override
	public Map<Integer, Integer> getCustomerProductsUpdates(CloseableAONContext ctx, int domainId, CustomerFeeParams customerFeeParamsy) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getCustomerProductsUpdates(ctx, customerFeeParamsy));
	}

	@Override
	public LinkedList<Fee> getFeeList(CloseableAONContext ctx, CustomerFeeParams customerFeeParams) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getFeeList(ctx, customerFeeParams));
	}
	
	@Override
	public LinkedList<Fee> getFullFeeList(CloseableAONContext ctx, CustomerFeeParams customerFeeParams) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getFullFeeList(ctx, customerFeeParams));
	}
	
	@Override
	public Stream<Fee> getFeeStream(AONContext ctx, FeeFilter filter) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getFeeStream(ctx, filter));
	}
	
	@Override
	public Fee save(AONContext ctx, Fee fee) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.save(ctx, fee));
	}
	
	@Override
	public Integer saveList(AONContext ctx, LinkedList<Fee> feeList) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.saveList(ctx, feeList));
	}
	
	@Override
	public Integer saveMassiveFees(AONContext ctx, Fee fee, CustomerFeeParams customerFeeParams) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.saveMassiveFees(ctx, fee, customerFeeParams));
	}
	
	@Override
	public Fee createCustomerFeeList(AONContext ctx, Fee fee) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.createCustomerFeeList(ctx, fee));
	}
	
	@Override
	public void updateRitemCustomerFee(CloseableAONContext ctx, Integer customerFee, Integer ritem) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.updateRitemCustomerFee(ctx, customerFee, ritem);
		} );
	}
	
	@Override
	public void deleteFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, f);
		} );			
	}

	@Override
	public void deleteFee(AONContext ctx, CustomerFeeParams customerFeeParams) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, customerFeeParams);
		} );			
	}

	@Override
	public void deleteFee(AONContext ctx, Stream<Fee> fs) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, fs);
		} );			
	}
	
	@Override
	public Map<Integer, Integer> getMinMaxCustomerFeeYear(CloseableAONContext ctx, int domainId) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getMinMaxCustomerFeeYear(ctx, domainId));
	}
	
	@Override
	public Integer getItemIdByProductCode(CloseableAONContext ctx, int domainId, String productCode) {
		return ctx.getDslContext().transactionResult(configuration
				-> FeeDAO.getItemIdByProductCode(ctx, domainId, productCode));
	}

	@Override
	public Stream<InvoiceDetail> getBoughtProductStream(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration
				-> InvoiceDAO.getBoughtProductStream(ctx, filter));
	}

	@Override
	public Stream<Finance> getFinanceStream(AONContext ctx, FinanceFilter filter,int offset,int limit) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceDAO.fetch(ctx,filter,offset,limit));

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
	public Stream<OldProduct> getInvoiceProducts(AONContext ctx, ProductFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> InvoiceOLDDAO.getInvoiceProducts(ctx, filter));
	}

	@Override
	public Invoice insertInvoice(AONContext ctx, Invoice invoice) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InvoiceDAO.insert(ctx, invoice));		
	}

	@Override
	public Invoice updateInvoice(AONContext ctx, Invoice invoice, boolean only) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InvoiceDAO.update(ctx, invoice, only));		
	}

	
	@Override
	public Invoice updateInvoice(AONContext ctx, Invoice invoice) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InvoiceDAO.update(ctx, invoice));		
	}

	@Override
	public Finance insertFinance(AONContext ctx, Finance finance) {
		return ctx.getDslContext().transactionResult(configuration ->
			FinanceDAO.getFinance(ctx, FinanceDAO.insert(ctx, finance))
		);
	}
	
	@Override
	public Finance saveFinance(AONContext ctx, Finance finance) {		
		return ctx.getDslContext().transactionResult(configuration ->
			FinanceDAO.getFinance(ctx, FinanceDAO.save(ctx, finance))
		);
	}

	@Override
	public Stream<InvoiceTax> getInvoiceTaxStream(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext().transactionResult(configuration
				-> InvoiceDAO.getInvoiceTaxStream(ctx, invoiceId));
	}

	// UTILITIES
	@Override
	public void updateWithholdingType(AONContext ctx, Integer invoiceId, WithholdingType newType) {
		ctx.getDslContext().transaction(configuration
				-> InvoiceDAO.updateWithholdingType( ctx , invoiceId, newType));
	}
	@Override
	public void updateActivity(AONContext ctx, Integer invoiceId, Integer activity) {
		ctx.getDslContext().transaction(configuration
				-> InvoiceDAO.updateActivity( ctx , invoiceId, activity));
	}
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
	@Override
	public FinanceUtilitiesResult financeInvoiceIntegrity(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceUtilitiesDAO.financeInvoiceIntegrity( ctx ));
	}
	@Override
	public Finance financeInvoiceIntegrityFix(AONContext ctx, Finance finance) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceUtilitiesDAO.financeInvoiceIntegrityFix( ctx , finance));
	}
	
 	
	@Override
	public LinkedList<FinanceTracking> getFinanceTracking(AONContext ctx, Integer finance) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceTrackingDAO.getFinanceTrackings(ctx, finance));
	}

	@Override
	public LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice) {
		return ctx.getDslContext().transactionResult(configuration
				-> FinanceDAO.getFinancesForInvoice(ctx, invoice));
	}

	@Override
	public Finance settleFinance(AONContext ctx, Integer finance) {
		return ctx.getDslContext().transactionResult(configuration -> {
			FinanceTrackingDAO.settle(ctx, finance);
			return FinanceDAO.getFinance(ctx, finance);
		});			
	}
	
	@Override
	public Finance unSettleFinance(AONContext ctx, Integer finance) {
		return ctx.getDslContext().transactionResult(configuration -> {
			FinanceTrackingDAO.unSettle(ctx, finance);
			return FinanceDAO.getFinance(ctx, finance);
		});			
	}
	
	@Override
	public Finance undoFinance(AONContext ctx, Integer finance) {
		return ctx.getDslContext().transactionResult(configuration -> {
			FinanceTrackingDAO.undo(ctx, finance);
			return FinanceDAO.getFinance(ctx, finance);
		});			
	}

	@Override
	public FinanceTracking payFinance(AONContext ctx, FinanceTracking tracking) {
		return ctx.getDslContext().transactionResult(configuration -> {
			return  FinanceTrackingDAO.pay(ctx, tracking);
		});			
	}

	@Override
	public FinanceTracking returnFinance(AONContext ctx, FinanceTracking tracking) {
		return ctx.getDslContext().transactionResult(configuration -> {
			return  FinanceTrackingDAO.returnFinance(ctx, tracking);
		});			
	}

	@Override
	public LinkedList<RegistryBank> getRegistryBanks(AONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(configuration -> {
			return RegistryOldDAO.getRBankStream(ctx, filter -> filter.getRegistryProperty().eq(registry))
					.collect(Collectors.toCollection(LinkedList::new));
		});			
	}

	@Override
	public LinkedList<RegistryBank> getCompanyRegistryBanks(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration -> {
			Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
			return RegistryOldDAO.getRBankStream(ctx, filter -> filter.getRegistryProperty().eq(company.getId()))
					.collect(Collectors.toCollection(LinkedList::new));
		});
	}

	// ------------------------------------- RAWDOC
	@Override
	public Stream<Rawdoc> getRawdocStream(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.get(ctx,filter,offset,limit));
	}
	
	@Override
	public Stream<Rawdoc> getRawdocFullStream(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getFull(ctx, filter, offset, limit));
	}
	@Override
	public Stream<Rawdoc> getRawdocNewPortal(AONContext ctx, RawdocFilter filter , Integer page, Integer perPage , boolean ticket, RawdocOrder order){
		return ctx.getDslContext().transactionResult(configuration -> RawdocDAO.getRawdocNewPortal(ctx, filter , page, perPage ,ticket, order));
	}
	
	@Override
	public Rawdoc getRawdocByid(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration -> RawdocDAO.getRawdocById(ctx, id));
	}
	@Override
	public long getRawdocCount(AONContext ctx , RawdocFilter filter , boolean ticket) {
		return RawdocDAO.getRawdocCount(ctx, filter, ticket);
	}
	@Override
	public LinkedList<RawdocDomainData> getRawdocDomainData(AONContext ctx, int searchDomain) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getDomainData(ctx,searchDomain));
	}
	
	@Override
	public RawdocUserData getRawdocUserData(AONContext ctx, byte[] auth) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getUserData(ctx, auth));
	}
	
	@Override
	public RawdocUserData getRawdocUserData(AONContext ctx, int searchDomain) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getUserData(ctx, searchDomain));
	}
	
	@Override
	public RawdocInvoiceCounter getRawdocInvoiceCounter(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getInvoiceCounter(ctx));
	}
	
	@Override
	public Rawdoc getRawdocFull(AONContext ctx, int id) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getFull(ctx, id));
	}
	
	@Override
	public Rawdoc rawdocSave(AONContext ctx, Rawdoc rawdoc) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.save(ctx, rawdoc));			
	}
	
	@Override
	public void rawdocDelete(AONContext ctx, RawdocFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.delete(ctx, filter);
		} );			
	}
	
	@Override
	public void rawdocDelete(AONContext ctx, Integer domain, Integer rawdocId) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.delete(ctx, domain, rawdocId);
		} );			
	}
	
	@Override
	public void rawdocToDraft(AONContext ctx, Integer rawdocId) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.toDraft(ctx, rawdocId);
		} );			
	}

	@Override
	public void rawdocToRejected(AONContext ctx, Integer rawdocId, String reason) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.toRejected(ctx, rawdocId, reason);
		} );			
	}

	@Override
	public void rawdocToInbox(AONContext ctx, Integer rawdocId) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.toInbox(ctx, rawdocId);
		} );			
	}
	@Override
	public boolean rawdocHasData(AONContext ctx, Integer rawdocId) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.hasData(ctx, rawdocId));
	}
	
	
	// ------------------------------------- PAY METHOD
	
	@Override
	public PayMethod getPayMethod(AONContext ctx, PayMethodFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> PayMethodDAO.get(ctx, filter));
	}
	
	@Override
	public LinkedList<PayMethod> getPayMethods(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> PayMethodDAO.getOrderByNames(ctx));
	}

	@Override
	public PayMethod savePayMethod(AONContext ctx, PayMethod paymethod) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> PayMethodDAO.save(ctx, paymethod));
	}
	@Override
	public void deletePayMethod(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> PayMethodDAO.delete(ctx, id));
	}

	@Override
	public PrintInvoiceConfiguration getPrintInvoiceConfiguration(AONContext ctx, Boolean withData) {
		return ctx.getDslContext().transactionResult(
				configuration -> PrintInvoiceConfigurationDAO.get(ctx, withData));
	}

	@Override
	public PrintInvoiceConfiguration savePrintInvoiceConfiguration(AONContext ctx, PrintInvoiceConfiguration pic) {
		return ctx.getDslContext().transactionResult(
				configuration -> PrintInvoiceConfigurationDAO.save(ctx, pic));
	}

	// ---------- TBAI CONFIGURATION
	
	@Override
	public InvofoxConfiguration getInvofoxConfiguration(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvofoxConfigurationDAO.get(ctx));
	}

	@Override
	public InvofoxConfiguration saveInvofoxConfiguration(AONContext ctx, InvofoxConfiguration config) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvofoxConfigurationDAO.save(ctx, config));
	}
	
	// ---------- TBAI CONFIGURATION
	
	@Override
	public TbaiConfiguration getTbaiConfiguration(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> TbaiConfigurationDAO.get(ctx));
	}

	@Override
	public TbaiConfiguration saveTbaiConfiguration(AONContext ctx, TbaiConfiguration pic) {
		return ctx.getDslContext().transactionResult(
				configuration -> TbaiConfigurationDAO.save(ctx, pic));
	}

	// ---------- SII CONFIGURATION
	
	@Override
	public SiiConfiguration getSiiConfiguration(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> SiiConfigurationDAO.get(ctx));
	}

	@Override
	public SiiConfiguration saveSiiConfiguration(AONContext ctx, SiiConfiguration pic) {
		return ctx.getDslContext().transactionResult(
				configuration -> SiiConfigurationDAO.save(ctx, pic));
	}

	@Override
	public void saveInvoiceFiscal(AONContext ctx, AonConfiguration config, Invoice invoice) {
		ctx.getDslContext().transaction(
				configuration -> InvoiceFiscalDAO.save(ctx, config, invoice));
	}
	
	@Override
	public void deleteInvoiceFiscal(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> InvoiceFiscalDAO.delete(ctx, id));
	}

	@Override
	public void saveFacturaeCodeAsignacion(AONContext ctx, Integer invoice, Integer registry, String code) {
		ctx.getDslContext().transaction(
				configuration -> InvoiceDAO.saveFacturaeCodeAsignacion(ctx, invoice, registry, code));		
	}

	@Override
	public InvoiceCommunicationTracking saveInvoiceCommunicationTracking(AONContext ctx, InvoiceCommunicationTracking invoiceCommunicationTracking) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceCommunicationTrackingDAO.save(ctx, invoiceCommunicationTracking));				
	}
	
	@Override
	public void deleteInvoiceCommunicationTracking(AONContext ctx, Integer invoiceId) {
		ctx.getDslContext().transaction(
				configuration -> InvoiceCommunicationTrackingDAO.delete(ctx, invoiceId));
	}

	@Override
	public Stream<InvoiceData> getInvoiceDataStream(AONContext ctx, InvoiceDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDataDAO.getStream(ctx, filter));
	}
	
	@Override
	public InvoiceData getInvoiceData(AONContext ctx, InvoiceDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDataDAO.get(ctx, filter));
	}
	
	@Override
	public InvoiceData saveInvoiceData(AONContext ctx, InvoiceData invoiceData) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceDataDAO.save(ctx, invoiceData));				
	}
	
	@Override
	public InvoiceInfo getInvoiceInfo(AONContext ctx, InvoiceInfoFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceInfoDAO.get(ctx, filter));
	}
	
	@Override
	public InvoiceInfo saveInvoiceInfo(AONContext ctx, InvoiceInfo invoiceInfo) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceInfoDAO.save(ctx, invoiceInfo));				
	}

	@Override
	public void deleteInvoiceInfo(AONContext ctx, Integer invoiceId) {
		ctx.getDslContext().transaction(
				configuration -> InvoiceInfoDAO.delete(ctx, f -> f.getInvoiceProperty().eq(invoiceId)));
	}

	@Override
	public Stream<InvoiceCommunicationTracking> getInvoiceCommunicationTrackingStream(AONContext ctx, InvoiceCommunicationTrackingFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceCommunicationTrackingDAO.getStream(ctx, filter));				
	}

	@Override
	public List<InvoiceCommunicationTracking> getInvoiceCommunicationTrackingList(AONContext ctx, InvoiceCommunicationTrackingFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceCommunicationTrackingDAO.getList(ctx, filter));		
	}

	@Override
	public InvoiceCommunicationTracking getInvoiceCommunicationTracking(AONContext ctx, InvoiceCommunicationTrackingFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> InvoiceCommunicationTrackingDAO.get(ctx, filter));		
	}
	
	// ---------- BOOKING CHECK

	@Override
	public LinkedList<BookingCheck> getBookingWithoutFeeList(CloseableAONContext ctx, CustomerFeeParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> BookingCheckDAO.getBookingWithoutFeeList(ctx, params));	
	}
	
	@Override
	public LinkedList<BookingCheck> getFeeWithoutBookingList(CloseableAONContext ctx, CustomerFeeParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> BookingCheckDAO.getFeeWithoutBookingList(ctx, params));	
	}
	
	@Override
	public LinkedList<BookingCheck> getBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> BookingCheckDAO.getBookingCheckList(ctx, params));	
	}
	
	@Override
	public LinkedList<BookingCheck> getCustomerBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> BookingCheckDAO.getCustomerBookingCheckList(ctx, params));	
	}
	
	@Override
	public LinkedList<BookingCheck> getCustomerChildBookingCheckList(CloseableAONContext ctx, CustomerFeeParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> BookingCheckDAO.getCustomerChildBookingCheckList(ctx, params));	
	}

	@Override
	public void saveBookingCheck(CloseableAONContext ctx, BookingCheck bookingCheck) {
		ctx.getDslContext().transaction(
				configuration -> BookingCheckDAO.save(ctx, bookingCheck));
	}

	@Override
	public void deleteBookingList(CloseableAONContext ctx, LinkedList<BookingCheck> selectedBookings) {
		ctx.getDslContext().transaction(
				configuration -> BookingCheckDAO.delete(ctx, selectedBookings));
	}
	
	// ---------- VENCIMIENTO NOMINAS

	@Override
	public void createSettleSalaries(CloseableAONContext ctx, Date date) {
		ctx.getDslContext().transaction(
				configuration -> SettleSalariesDAO.createSettleSalaries(ctx, date));
	}
	

	@Override
	public void deleteFinance(CloseableAONContext ctx, Integer financeId) {
		ctx.getDslContext().transaction(
				configuration -> FinanceDAO.delete(ctx, financeId));
	}
	
	@Override
	public Integer createSepaFile(CloseableAONContext ctx, Integer fbatchId) {
		return ctx.getDslContext().transactionResult(
				configuration -> SettleSalariesDAO.createSepaFile(ctx, fbatchId));
	}

	@Override
	public LinkedList<FBatch> getFBatches(CloseableAONContext ctx, FBatchFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(
				configuration -> FBatchDAO.getList(ctx, filter, offset, limit));	
	}
	
	@Override
	public FBatch getFBatch(CloseableAONContext ctx, Integer fbatchId) {
		return ctx.getDslContext().transactionResult(
				configuration -> FBatchDAO.get(ctx, fbatchId));	
	}

	@Override
	public void deleteFBatches(CloseableAONContext ctx, LinkedList<Integer> fBatchIds) {
		ctx.getDslContext().transaction(
				configuration -> FBatchDAO.delete(ctx, fBatchIds));
	}

	@Override
	public FBatch createUpdateFBatch(CloseableAONContext ctx, FBatch fBatch) {
		return ctx.getDslContext().transactionResult(
				configuration -> FBatchDAO.save(ctx, fBatch));	
	}


	// ---------- COBROS Y PAGOS CARD
	
	@Override
	public Double getFinanceGroupStatus(CloseableAONContext ctx, FinanceFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> FinanceDAO.getFinanceGroupStatus(ctx, filter));	
	}
	
}
