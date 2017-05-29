package com.esferalia.aon.occam.api;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.Alarm;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialActivityFilter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.CommercialTrackingFilter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.BrandFilter;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.FeeFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductTagFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectCommercialFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.RecordDataFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SeriesFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.StockFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskCommentFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskEventFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderWorkgroupFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseTransferFilter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Question;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.occam.impl.jooq.AgreementImpl;
import com.esferalia.aon.occam.impl.jooq.AttachmentImpl;
import com.esferalia.aon.occam.impl.jooq.CommercialImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
import com.esferalia.aon.occam.impl.jooq.GroupwareImpl;
import com.esferalia.aon.occam.impl.jooq.ManagementImpl;
import com.esferalia.aon.occam.impl.jooq.MarketplaceImpl;
import com.esferalia.aon.occam.impl.jooq.OfficeImpl;
import com.esferalia.aon.occam.impl.jooq.PMSImpl;
import com.esferalia.aon.occam.impl.jooq.ProductImpl;
import com.esferalia.aon.occam.impl.jooq.ProjectImpl;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.SalaryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;
import com.esferalia.aon.occam.impl.jooq.StatsImpl;
import com.esferalia.aon.occam.impl.jooq.SystemImpl;
import com.esferalia.aon.occam.impl.jooq.TaskImpl;
import com.esferalia.aon.occam.impl.jooq.WarehouseImpl;
import com.esferalia.aon.watson.error.AonCoreException;

public class AON {

	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}

	private static ICommon getCommon() {
		return new CommonImpl();
	}

	//	private static IAccounting getAccounting() {
	//		return new AccountingImpl();
	//	}

	private static ISalary getSalary() {
		return new SalaryImpl();
	}

	private static ISystem getSystem() {
		return new SystemImpl();
	}

	// private static IFiscal getFiscal() {
	// return new FiscalImpl();
	// }

	private static IFinance getFinance() {
		return new FinanceImpl();
	}

	private static IManagement getManagement() {
		return new ManagementImpl();
	}

	private static IAgreement getAgreement() {
		return new AgreementImpl();
	}

	private static IProduct getProduct() {
		return new ProductImpl();
	}

	private static IOffice getOffice() {
		return new OfficeImpl();
	}
	
	private static IGroupware getGroupware() {
		return new GroupwareImpl();
	}
	
	private static IAttachment getAttachment() {
		return new AttachmentImpl();
	}

	private static IWarehouse getWarehouse() {
		return new WarehouseImpl();
	}

	private static IStats getStats() {
		return new StatsImpl();
	}

	private static IProject getProject() {
		return new ProjectImpl();
	}

	private static IRegistry getRegistry() {
		return new RegistryImpl();
	}

	private static ICommercial getCommercial() {
		return new CommercialImpl();
	}

	private static IMarketplace getMarketplace() {
		return new MarketplaceImpl();
	}

	private static IPMS getPMS() {
		return new PMSImpl();
	}
	
	private static ITask getTask() {
		return new TaskImpl();
	}

	// ********************************************
	// *************************** CONFIGURATION **
	// ********************************************
	public static AonConfiguration getConfiguration(String domainName, int domainId, String login) {
		return getConfiguration(domainName, domainId, login, null);
	}
	public static AonConfiguration getConfiguration(String domainName, int domainId, String login,Date atDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getConfiguration(ctx, atDate);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	// ********************************************
	// ******************************** SECURITY **
	// ********************************************
	public static User getUser(String domainName, int domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUser(ctx, login);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer[] getUserScopes(String domainName, int domainId,
			String login, Integer userId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUserScopes(ctx, userId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Scope getScope(String domainName, Integer domainId,
			String login, Integer scopeId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getScope(ctx, scopeId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************** COMMON **
	// ********************************************

	// --------------------- DOMAIN

	public static Domain getDomain(String domainName, Integer domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getCommon().getDomain(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Domain getDomain(String domainName, Integer domainId,
			String user, DomainFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getCommon().getDomain(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Domain> getDriveDomainList(String domainName,
			Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDriveDomainList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Domain> getDomainList(String domainName,
			Integer domainId, String login, DomainFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDomainList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Domain insertDomain(String domainName, int parentDomain,
			String cifEnterprise, String nameEnterprise,
			List<String> messages) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, parentDomain, null);
			return getCommon().insertDomain(ctx, parentDomain, cifEnterprise,
					nameEnterprise, messages);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static DomainGserviceaccount getGeneralDomainGserviceaccount(
			String domainName, Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getGeneralDomainGserviceaccount(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static DomainGserviceaccount getDomainGserviceaccount(
			String domainName, Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDomainGserviceaccount(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static HashMap<Integer, DomainGserviceaccount> getDomainGserviceaccountMap(
			String domainName, Integer domainId, String login, Integer parent) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDomainGserviceaccountMap(ctx, parent);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(
			String domainName, Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDomainGserviceaccountList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static DomainGserviceaccount getDomainGserviceaccount(
			String domainName, Integer domainId, String login,
			DomainGserviceaccountFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDomainGserviceaccount(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateDomainGserviceaccount(String domainName,
			Integer domainId, String login, String googleAccount) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().updateDomainGserviceaccount(ctx, googleAccount);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateDomainGserviceaccount(String domainName,
			Integer domainId, String login, DomainGserviceaccount dgsa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().updateDomainGserviceaccount(ctx, dgsa);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteDomainGserviceaccount(String domainName,
			Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().deleteDomainGserviceaccount(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void insertDomainGserviceaccount(String domainName,
			Integer domainId, String login, DomainGserviceaccount dgsa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().insertDomainGserviceaccount(ctx, dgsa);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer[] getSonsDomains(String domainName, Integer domainId,
			String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getSonsDomains(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// --------------------- APPLICATION PARAMETERS
	
	public static Stream<ApplicationParameter> getApplicationParameterStream(String domainName, Integer domainId, String login, ApplicationParameterFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getApplicationParameterStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ApplicationParameter getApplicationParameter(String domainName, Integer domainId, String login, String id) {
		return getApplicationParameterStream(domainName, domainId, login, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getNameProperty().eq(id))
				.perPage(1))
			.findFirst().orElse(new ApplicationParameter()); // TODO Devolver Optional
	}

	public static FiscalParameters getFiscalParameters(String domainName,
			int domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getFiscalParameters(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ApplicationParameter fetchApplicationParameter(AONContext ctx,
			AppParam param) {
		return getCommon().fetchOne(ctx, param);
	}
	
	public static ApplicationParameter fetchApplicationParameter(String domainName, Integer domainId, String login,
			AppParam param) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().fetchOne(ctx, param);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ApplicationParameter getApplicationParamenter(String domainName, Integer domainId, String login, AppParam param){
		ApplicationParameter ap = fetchApplicationParameter(domainName, domainId, login, param);
		return ap != null ? ap : new ApplicationParameter();
	}
	
	public static ApplicationParameter getApplicationParamenter(String domainName, Integer domainId, String login, String id){
		ApplicationParameter ap = getApplicationParameter(domainName, domainId, login, id);
		return ap != null ? ap : new ApplicationParameter();
	}
	
	public static ApplicationParameter insertApplicationParameter(String domainName, Integer domainId, String login, AppParam param, String value){
		return insertApplicationParameter(domainName, domainId, login, param.getValue(), value);
	}
	
	public static ApplicationParameter insertApplicationParameter(String domainName, Integer domainId, String login, String param, String value){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertApplicationParameter(ctx, param, value);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteApplicationParameter(String domainName, Integer domainId, String login, ApplicationParameterFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().deleteApplicationParameter(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// --------------------------------- PERSON
	
	public static Stream<Person> getPersonStream(String domainName, Integer domainId, String login, PersonFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getPersonStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Person> getPersonList(String domainName, Integer domainId, String login, PersonFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getPersonStream(ctx, filter)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Person> getPerson(String domainName, Integer domainId, String login, PersonFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getPersonStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	// --------------------------------- ENTERPRISE
	public static LinkedList<Enterprise> getParentEnterprises(String domainName,
			int domain, String login, String query) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getCommon().getParentEnterprises(ctx, query);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Enterprise getEnterprise(String domainName, int domain,
			String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getCommon().getEnterprise(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Company> getCompanyStream(String domainName, Integer domainId, String login, CompanyFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCompanyStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Company getCompany(String domainName, Integer domainId, String login, CompanyFilter filter){
		return getCompanyStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Company());
	}

	public static Company getCompanyForDomain(String domainName, int domainId, String login) {
		return getCompany(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId));
	}

	public static LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain, String login, int enterprise) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getCommon().getCompanyBanks(ctx, enterprise);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<CompanyBank> getCompanyBanks(String domainName,
			int domain, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getCommon().getCompanyBanks(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------------------------ WORKPLACE

	public static Workplace getWorkplace(String domainName, Integer domainId,
			String login, WorkplaceFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getWorkplace(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Workplace> getWorkplaceList(String domainName,
			Integer domainId, String login, WorkplaceFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getWorkplaceList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}	

	// --------------------- SIGNATURE

	public static Signature getSignature(String domainName, Integer domainId,
			String login, Integer signatureId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getSignature(ctx, signatureId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Signature getSignature(String domainName, Integer domainId,
			String login, SignatureFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getSignature(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Signature> getSignatureList(String domainName, Integer domainId,
			String login, SignatureFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getSignatureList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************* PRODUCT **
	// ********************************************

	// ------------------------------------ PRODUCT

	public static Product getProduct(String domainName, Integer domainId, String login, Integer productId) {
		return getProduct(domainName, domainId, login, f -> f.getIdProperty().eq(productId));
	}
	
	public static Product getProduct(String domainName, Integer domainId, String login, ProductFilter filter) {
		return getProductStream(domainName, domainId, login, filter).findFirst().orElse(new Product());
	}

	public static LinkedList<Product> getProductList(String domainName, Integer domainId, String login, ProductFilter filter){
		return getProductStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Product> getProductStream(String domainName, Integer domainId, String login, ProductFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getProductStream(ctx, filter);
		} finally{
			if(ctx != null) ctx.close();
		}
	}

	public static void insert(AONContext ctx, Product p) {
		getProduct().insert(ctx, p);
	}

	public static void insertWithId(AONContext ctx, Product p) {
		getProduct().insertWithId(ctx, p);
	}

	public static LinkedList<Product> insert(AONContext ctx,
			Stream<Product> ps) {
		return getProduct().insert(ctx, ps);
	}

	public static void insertWithId(AONContext ctx, Stream<Product> ps) {
		getProduct().insertWithId(ctx, ps);
	}

	public static void update(AONContext ctx, Product p) {
		getProduct().update(ctx, p);
	}

	public static void delete(AONContext ctx, Product p) {
		getProduct().delete(ctx, p);
	}

	public static void delete(AONContext ctx, Stream<Product> ps) {
		getProduct().delete(ctx, ps);
	}

	// ------------------------------------ PRODUCT_TAG
	
	public static Stream<ProductTag> getProductTagStream(String domainName, Integer domainId, String login, ProductTagFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getProductTagStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ProductTag getProductTag(String domainName, Integer domainId, String login, ProductTagFilter filter){
		return getProductTagStream(domainName, domainId, login, filter).findFirst().orElse(new ProductTag());
	}

	public static List<String> getProductTags(String domainName, int domainId,
			String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getProductTags(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Map<Integer, String[]> getProductTagMap(String domainName,
			int domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getProductTagMap(ctx);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void insertProductTag(AONContext ctx, ProductTag pt) {
		getProduct().insertProductTag(ctx, pt);
	}

	public static void insertProductTag(AONContext ctx,
			Stream<ProductTag> pts) {
		getProduct().insertProductTag(ctx, pts);
	}

	public static void updateProductTag(AONContext ctx, ProductTag pt) {
		getProduct().updateProductTag(ctx, pt);
	}

	public static void deleteProductTag(AONContext ctx, ProductTag pt) {
		getProduct().deleteProductTag(ctx, pt);
	}

	public static void deleteProductTag(AONContext ctx,
			Stream<ProductTag> pts) {
		getProduct().deleteProductTag(ctx, pts);
	}

	// ------------------------------------ ITEM
	
	public static LinkedList<Item> getItemList(String domainName, Integer domainId, String login, ItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Item> getFullItemList(String domainName, Integer domainId, String login, ItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getFullItemStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Item getItem(String domainName, Integer domainId, String login, Integer itemId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, f -> f.getIdProperty().eq(itemId)
					.perPage(1)).findFirst().orElse(new Item());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Item getItem(String domainName, Integer domainId, String login, ItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, filter)
					.findFirst().orElse(new Item());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Item> getItemOptional(String domainName, Integer domainId, String login, ItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<ItemComposition> getItemCompositionList(String domainName, Integer domainId, String login, Integer itemId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemComposition(ctx, itemId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Item insertItem(String domainName, Integer domainId, String login, Item i) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().insertItem(ctx, i);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteItem(String domainName, Integer domainId, String login, Item item) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			deleteItem(ctx, item);
		} finally {
			if (ctx != null)
				ctx.close();
		}	}

	
	public static void insertItem(AONContext ctx, Item i) {
		getProduct().insertItem(ctx, i);
	}

	public static void insertItemWithId(AONContext ctx, Item i) {
		getProduct().insertItemWithId(ctx, i);
	}

	public static void insertItem(AONContext ctx, Stream<Item> is) {
		getProduct().insertItem(ctx, is);
	}

	public static void insertItemWithId(AONContext ctx, Stream<Item> is) {
		getProduct().insertItemWithId(ctx, is);
	}

	public static void updateItem(AONContext ctx, Item i) {
		getProduct().updateItem(ctx, i);
	}

	public static void deleteItem(AONContext ctx, Item i) {
		getProduct().deleteItem(ctx, i);
	}

	public static void deleteItem(AONContext ctx, Stream<Item> is) {
		getProduct().deleteItem(ctx, is);
	}

	// ------------------------------------ BRAND
	public static Brand getBrand(String domainName, Integer domainId, String login,
			Integer id){
		return getBrand(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Brand getBrand(String domainName, Integer domainId, String login,
			String name){
		return getBrand(domainName, domainId, login, f -> f.getNameProperty().eq(name)
				.and(f.getDomainProperty().eq(domainId)));
	}
	
	public static Stream<Brand> getBrandStream(String domainName, Integer domainId, String login,
			BrandFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getBrandStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	
	public static Brand getBrand(String domainName, Integer domainId, String login,
			BrandFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getBrand(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Brand insertBrand(String domainName, Integer domainId,
			String login, Brand brand) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().insertBrand(ctx, brand);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------------------------ PRODUCT CATEGORY
	
	public static Stream<ProductCategory> getProductCategoryStream(String domainName, Integer domainId, String login, ProductCategoryFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getProductCategoryStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ProductCategory getProductCategory(String domainName, Integer domainId, String login, Integer id){
		return getProductCategory(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static ProductCategory getProductCategory(String domainName, Integer domainId, String login, ProductCategoryFilter filter){
		return getProductCategoryStream(domainName, domainId, login, filter)
				.findFirst().orElse(new ProductCategory());
	}
	
	public static LinkedList<ProductCategory> getProductCategoryList(String domainName, Integer domainId, String login, ProductCategoryFilter filter){
		return getProductCategoryStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static ProductCategory insertProductCategory(String domainName,
			Integer domainId, String login, ProductCategory productCategory) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().insertProductCategory(ctx, productCategory);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************* FINANCE **
	// ********************************************
	
	public static Stream<Invoice> getInvoiceStream(String domainName, Integer domainId, String login, InvoiceFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoiceStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Invoice> getInvoiceList(String domainName, Integer domainId, String login, InvoiceFilter filter){
		return getInvoiceStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Invoice getInvoice(String domainName, Integer domainId, String login, InvoiceFilter filter){
		return getInvoiceStream(domainName, domainId, login, filter)
			.findFirst().orElse(new Invoice());
	}
	
	public static Stream<InvoiceDetail> getInvoiceDetails(String domainName,
			Integer domainId, String login, InvoiceFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoiceDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static InvoiceDetail getLastInvoiceDetail(String domainName,
			Integer domainId, String user, Item item, Integer workplaceId,
			Integer warehouseId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getLastInvoiceDetail(ctx, item, workplaceId,
					warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static InvoiceDetail getLastInvoiceDetailUntilDate(String domainName,
			Integer domainId, String user, Item item, Integer workplaceId,
			Integer warehouseId, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getLastInvoiceDetailUntilDate(ctx, item,
					workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InvoiceDetail> getLastInvoiceDetailList(
			String domainName, Integer domainId, String user, Item item,
			String months, Integer workplaceId, Integer warehouseId) {
		Calendar calendar = Calendar.getInstance();
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getLastInvoiceDetailList(ctx, item,
					calendar.getTime(), workplaceId, warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InvoiceDetail> getLastInvoiceDetailListUntilDate(
			String domainName, Integer domainId, String user, Item item,
			String months, Integer workplaceId, Integer warehouseId,
			Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getLastInvoiceDetailListUntilDate(ctx, item,
					calendar.getTime(), workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InvoiceDetail> getInvoiceDetailList(
			String domainName, Integer domainId, String user, Item item,
			Integer workplaceId, Integer warehouseId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getInvoiceDetailList(ctx, item, workplaceId,
					warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InvoiceDetail> getInvoiceDetailListUntilDate(
			String domainName, Integer domainId, String user, Item item,
			Integer workplaceId, Integer warehouseId, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getInvoiceDetailListUntilDate(ctx, item,
					workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InvoicingGroup> getInvoicingGroupList(
			String domainName, Integer domainId, String login,
			InvoicingGroupFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoicingGroupList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer getInvoiceNextNumber(
			String domainName, Integer domainId, String login,
			Byte[] types, String series) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoiceNextNumber(ctx, types,series);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<InvoiceDetail> getBoughtProductStream(String domainName,
			Integer domainId, String login, InvoiceFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getBoughtProductStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Finance> getFinanceStream(String domainName, Integer domainId, String login, FinanceFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getFinanceStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Finance> getFinanceList(String domainName,
			Integer domainId, String login, FinanceFilter filter) {
		return getFinanceStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	// ********************************************
	// ****************************** MANAGEMENT **
	// ********************************************
	public static Stream<OfferDetail> getOfferDetails(String domainName,
			Integer domainId, String login, OfferFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getOfferDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ------------------ SALES
	public static Stream<Sales> getSalesStream(String domainName,
			Integer domainId, String login, SalesFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getSalesStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Sales getSales(String domainName, Integer domainId,
			String login, SalesFilter filter) {
		Sales sales = getSalesStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Sales());
		sales.setCustomer(getCustomer(domainName, domainId, login, sales
				.getCustomer().getId()));
		return sales;
	}

	public static Stream<SalesDetail> getSalesDetailStream(String domainName,
			Integer domainId, String login, SalesDetailFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			List<SalesDetail> list = getManagement().getSalesDetailStream(ctx,
					filter).collect(Collectors.toList());
			for (SalesDetail detail : list) {
				detail.setSales(getSales(domainName, domainId, login, f -> f
						.getIdProperty().eq(detail.getSales().getId())));
				detail.setItem(getItem(domainName, domainId, login, detail
						.getItem().getId()));
			}
			return list.stream();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<SalesDetail> getSalesDetails(String domainName,
			Integer domainId, String login, SalesFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getSalesDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	
	// ------------------ PURCHASE
	
	public static Stream<Purchase> getPurchaseStream(String domainName,
			Integer domainId, String login, PurchaseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getPurchaseStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Purchase> getPurchaseList(String domainName,
			Integer domainId, String login, PurchaseFilter filter) {
		return getPurchaseStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Purchase getPurchase(String domainName,
			Integer domainId, String login, PurchaseFilter filter) {
		return getPurchaseStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Purchase());
	}
	
	public static Purchase getPurchase(String domainName,
			Integer domainId, String login, Integer id) {
		return getPurchase(domainName, domainId, login, f -> f.getIdProperty().eq(id));	
	}
	
	public static Purchase insertPurchase(String domainName, Integer domainId, String login, Purchase purchase) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertPurchase(ctx, purchase);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Purchase updatePurchase(String domainName, Integer domainId, String login, Purchase purchase, PurchaseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().updatePurchase(ctx, purchase, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Purchase updatePurchase(String domainName, Integer domainId, String login, Purchase purchase) {
		return updatePurchase(domainName, domainId, login, purchase, f -> f.getIdProperty().eq(purchase.getId()));
	}
	
	public static void deletePurchase(String domainName, Integer domainId, String login, PurchaseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getManagement().deletePurchase(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deletePurchase(String domainName, Integer domainId, String login, Integer id) {
		deletePurchase(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	// -------------------- PURCHASE DETAILS
	
	public static Stream<PurchaseDetail> getPurchaseDetailStream(String domainName,
			Integer domainId, String login, PurchaseDetailFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getPurchaseDetailStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<PurchaseDetail> getPurchaseDetails(String domainName,
			Integer domainId, String login, PurchaseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getPurchaseDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<PurchaseDetail> getPurchaseDetailList(String domainName,
			Integer domainId, String login, PurchaseDetailFilter filter) {
		return getPurchaseDetailStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static PurchaseDetail getPurchaseDetail(String domainName,
			Integer domainId, String login, PurchaseDetailFilter filter) {
		return getPurchaseDetailStream(domainName, domainId, login, filter)
				.findFirst().orElse(new PurchaseDetail());
	}
	
	public static PurchaseDetail getPurchaseDetail(String domainName,
			Integer domainId, String login, Integer id) {
		return getPurchaseDetail(domainName, domainId, login, f -> f.getIdProperty().eq(id));	
	}
	
	public static Integer insertPurchaseDetail(String domainName, Integer domainId, String login, PurchaseDetail purchaseDetail) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertPurchaseDetail(ctx, purchaseDetail);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static PurchaseDetail updatePurchaseDetail(String domainName, Integer domainId, String login, PurchaseDetail purchaseDetail, PurchaseDetailFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().updatePurchaseDetail(ctx, purchaseDetail, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static PurchaseDetail updatePurchaseDetail(String domainName, Integer domainId, String login, PurchaseDetail purchaseDetail) {
		return updatePurchaseDetail(domainName, domainId, login, purchaseDetail, f -> f.getIdProperty().eq(purchaseDetail.getId()));
	}
	
	public static void deletePurchaseDetail(String domainName, Integer domainId, String login, PurchaseDetailFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getManagement().deletePurchaseDetail(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deletePurchaseDetail(String domainName, Integer domainId, String login, Integer id) {
		deletePurchaseDetail(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	// ------------------ DELIVERY
	
	public static Stream<DeliveryDetail> getDeliveryDetails(String domainName,
			Integer domainId, String login, DeliveryFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getDeliveryDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Delivery> getDeliveryStream(String domainName,
			Integer domainId, String login, DeliveryFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getDeliveryStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Delivery> getDeliveryList(String domainName,
			Integer domainId, String login, DeliveryFilter filter) {
		return getDeliveryStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Delivery getDelivery(String domainName,
			Integer domainId, String login, DeliveryFilter filter) {
		return getDeliveryStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Delivery());
	}
	
	public static Delivery getDelivery(String domainName,
			Integer domainId, String login, Integer id) {
		return getDelivery(domainName, domainId, login,
				f -> f.getIdProperty().eq(id).perPage(1));	
	}
	
	public static Delivery insertDelivery(String domainName, Integer domainId, String login, Delivery delivery) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertDelivery(ctx, delivery);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Delivery updateDelivery(String domainName, Integer domainId, String login, Delivery delivery, DeliveryFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().updateDelivery(ctx, delivery, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Delivery updateDelivery(String domainName, Integer domainId, String login, Delivery delivery) {
		return updateDelivery(domainName, domainId, login, delivery, f -> f.getIdProperty().eq(delivery.getId()));
	}
	
	public static void deleteDelivery(String domainName, Integer domainId, String login, DeliveryFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getManagement().deleteDelivery(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteDelivery(String domainName, Integer domainId, String login, Integer id) {
		deleteDelivery(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	// -------------------- DELIVERY DETAILS
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(String domainName,
			Integer domainId, String login, DeliveryDetailFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getDeliveryDetailStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	public static LinkedList<DeliveryDetail> getDeliveryDetailList(String domainName,
			Integer domainId, String login, DeliveryDetailFilter filter) {
		return getDeliveryDetailStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
		
	public static DeliveryDetail getDeliveryDetail(String domainName,
			Integer domainId, String login, DeliveryDetailFilter filter) {
		return getDeliveryDetailStream(domainName, domainId, login, filter)
				.findFirst().orElse(new DeliveryDetail());
	}
		
	public static DeliveryDetail getDeliveryDetail(String domainName,
			Integer domainId, String login, Integer id) {
		return getDeliveryDetail(domainName, domainId, login, f -> f.getIdProperty().eq(id));	
	}
	
	// ********************************************
	// ********************************* PAYROLL **
	// ********************************************

	public static void saveAgreement(AONContext ctx, Agreement... agreements)
			throws AonCoreException {
		getAgreement().save(ctx, agreements);
	}

	public static Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter) {
		return getSalary().getSalaries(ctx, filter, Salary::new);
	}

	public static Stream<Salary> getSalaryData(AONContext ctx,
			SalaryFilter filter) {
		return getSalary().getSalaryData(ctx, filter, Salary::new);
	}

	public static Stream<Bonus> getAvailableBonuses(AONContext ctx,
			BonusFilter filter) {
		return getSystem().getAvailableBonus(ctx, filter, Bonus::new);
	}

	// ********************************************
	// ************************************* FEE **
	// ********************************************
	
	public static Stream<Fee> getFeeStream(String domainName, Integer domainId, String login, FeeFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getFeeStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Fee getFee(String domainName, Integer domainId, String login, FeeFilter filter){
		return getFeeStream(domainName, domainId, login, filter)
			.findFirst().orElse(new Fee());
	}
	
	public static LinkedList<Fee> getFeeList(String domainName, Integer domainId, String login, FeeFilter filter){
		return getFeeStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static void insertFee(AONContext ctx, Fee f) {
		getFinance().insertFee(ctx, f);
	}

	public static void insertFee(AONContext ctx, Stream<Fee> fs) {
		getFinance().insertFee(ctx, fs);
	}

	public static void updateFee(AONContext ctx, Fee f) {
		getFinance().updateFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Fee f) {
		getFinance().deleteFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Stream<Fee> fs) {
		getFinance().deleteFee(ctx, fs);
	}

	// ********************************************
	// ****************************** GWT-OFFICE **
	// ********************************************

	// TODO
	
	public static User getUser(Integer domainId, String domainName,
			String userName, Integer userId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getUser(ctx, userId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static List<User> getUsers(Integer domainId, String domainName, String userName) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);			
			return getOffice().getUsers(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Tag editTag(Integer domainId, String domainName,
			String userName, String labelName, Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().editTag(ctx, labelName, tag);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static List<Tag> getTags(Integer domainId, String domainName,
			String userName) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getTags(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static boolean deleteTag(Integer domainId, String domainName,
			String userName, String labelName) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().deleteTag(ctx, labelName);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Tag getTag(Integer domainId, String domainName,
			String userName, String name) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getTag(ctx, name);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static List<Registry> getRegistries(Integer domainId,
			String domainName, String userName) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getRegistries(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	public static Registry getRegistry(String domainName, Integer domainId, String login, String name){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistry(ctx, name);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Registry getRegistryFD(String domainName, Integer domainId, String login, String name){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistry(ctx, domainId, name);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Registry getRegistry(String domainName, Integer domainId, String login, Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistry(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static NotificationInfo getNotificationInfo(String domainName, Integer domainId, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getOffice().getNotificationInfo(ctx);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	public static void insertNotificationInfo(String domainName, Integer domainId, String login,
			NotificationInfo notificationInfo){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getOffice().insertNotificationInfo(ctx, notificationInfo);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	public static void insertNotificationInfo(String domainName, Integer domainId, String login,
			String data, AppParam appParam){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getOffice().insertNotificationInfo(ctx, data, appParam);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	
	// ********************************************
	// ****************************** GROUPWARE **
	// ********************************************
	public static Notice getNotice(String domainName, Integer domainId,
			String login, Integer noticeId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getGroupware().getNotice(ctx, noticeId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer insertNotice(Integer domainId, String domainName,
			String userName, Notice notice) throws Exception {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getGroupware().insertNotice(ctx, notice);
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Alarm getAlarm(String domainName, Integer domainId,
			String login, Integer alarmId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getGroupware().getAlarm(ctx, alarmId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer insertAlarm(Integer domainId, String domainName,
			String userName, Alarm alarm) throws Exception {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getGroupware().insertAlarm(ctx, alarm);
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ********************************************
	// ****************************** ATTACHMENT **
	// ********************************************

	public static Attach getAttach(String domainName, Integer domainId, String login, AttachFilter filter, AttachType attachType) {
		return getAttachStream(domainName, domainId, login, filter, attachType, true)
				.findFirst().orElse(new Attach());
	}
	
	public static Attach getAttach(String domainName, Integer domainId,
			String login, AttachFilter filter, AttachType attachType, Boolean withData) {
		return getAttachStream(domainName, domainId, login, filter, attachType, withData)
				.findFirst().orElse(new Attach());
	}
	public static LinkedList<Attach> getAttachList(String domainName, Integer domainId, String login, AttachFilter filter, AttachType attachType) {
		return getAttachList(domainName, domainId, login, filter, attachType, true);
	}
	
	public static LinkedList<Attach> getAttachList(String domainName,
			Integer domainId, String login, AttachFilter filter, 
			AttachType attachType, Boolean withData) {
		return getAttachStream(domainName, domainId, login, filter, attachType, withData)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Stream<Attach> getAttachStream(String domainName,
			Integer domainId, String login, AttachFilter filter,
			AttachType attachType, Boolean withData) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attachType.equals(AttachType.REGISTRY))
				return getAttachment().getRegistryAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.CONTRACT))
				return getAttachment().getContractAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.INVOICE))
				return getAttachment().getInvoiceAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.ITEM))
				return getAttachment().getItemAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.OFFER))
				return getAttachment().getOfferAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.PAYROLL))
				return getAttachment().getPayrollAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.PROJECT))
				return getAttachment().getProjectAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.SEPE))
				return getAttachment().getSepeAttachStream(ctx, filter, withData);
			else if (attachType.equals(AttachType.DATA))
				return getAttachment().getDataAttachStream(ctx, filter, withData);
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	@Deprecated
	public static Integer insert(String domainName, Integer domainId, String login, Attach attach){
		return insertAttach(domainName, domainId, login, attach);
	}

	public static Integer insertAttach(String domainName, Integer domainId, String login, Attach attach) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attach.getAttachType().equals(AttachType.REGISTRY))
				return getAttachment().insertRegistryAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.CONTRACT))
				return getAttachment().insertContractAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.INVOICE))
				return getAttachment().insertInvoiceAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.ITEM))
				return getAttachment().insertItemAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.OFFER))
				return getAttachment().insertOfferAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.PAYROLL))
				return getAttachment().insertPayrollAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.PROJECT))
				return getAttachment().insertProjectAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.SEPE))
				return getAttachment().insertSepeAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.DATA))
				return getAttachment().insertDataAttach(ctx, attach);
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	@Deprecated
	public static void update(String domainName, Integer domainId, String login, Attach attach) {
		updateAttach(domainName, domainId, login, attach);
	}
	
	public static void updateAttach(String domainName, Integer domainId, String login,
			Attach attach) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attach.getAttachType().equals(AttachType.REGISTRY))
				getAttachment().updateRegistryAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.CONTRACT))
				getAttachment().updateContractAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.INVOICE))
				getAttachment().updateInvoiceAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.ITEM))
				getAttachment().updateItemAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.OFFER))
				getAttachment().updateOfferAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.PAYROLL))
				getAttachment().updatePayrollAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.PROJECT))
				getAttachment().updateProjectAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.SEPE))
				getAttachment().updateSepeAttach(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.DATA))
				getAttachment().updateDataAttach(ctx, attach);

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateAttachData(String domainName, Integer domainId,
			String login, Attach attach) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attach.getAttachType().equals(AttachType.REGISTRY))
				getAttachment().updateRegistryAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.CONTRACT))
				getAttachment().updateContractAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.INVOICE))
				getAttachment().updateInvoiceAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.ITEM))
				getAttachment().updateItemAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.OFFER))
				getAttachment().updateOfferAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.PAYROLL))
				getAttachment().updatePayrollAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.PROJECT))
				getAttachment().updateProjectAttachData(ctx, attach);
			else if (attach.getAttachType().equals(AttachType.SEPE))
				getAttachment().updateSepeAttachData(ctx, attach);

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateAttachDriveId(String domainName, Integer domainId,
			String login, Integer attachId, String driveId,
			AttachType attachType) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			if (attachType.equals(AttachType.REGISTRY))
				getAttachment().updateRegistryAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.CONTRACT))
				getAttachment().updateContractAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.INVOICE))
				getAttachment().updateInvoiceAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.ITEM))
				getAttachment().updateItemAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.OFFER))
				getAttachment().updateOfferAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.PAYROLL))
				getAttachment().updatePayrollAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.PROJECT))
				getAttachment().updateProjectAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.SEPE))
				getAttachment().updateSepeAttachDriveId(ctx, attachId, driveId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	@Deprecated
	public static void delete(String domainName, Integer domainId, String login, AttachFilter filter, AttachType attachType) {
		deleteAttach(domainName, domainId, login, filter, attachType);
	}
	
	public static void deleteAttach(String domainName, Integer domainId, String login,
			AttachFilter filter, AttachType attachType) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attachType.equals(AttachType.REGISTRY))
				getAttachment().deleteRegistryAttach(ctx, filter);
			else if (attachType.equals(AttachType.CONTRACT))
				getAttachment().deleteContractAttach(ctx, filter);
			else if (attachType.equals(AttachType.INVOICE))
				getAttachment().deleteInvoiceAttach(ctx, filter);
			else if (attachType.equals(AttachType.ITEM))
				getAttachment().deleteItemAttach(ctx, filter);
			else if (attachType.equals(AttachType.OFFER))
				getAttachment().deleteOfferAttach(ctx, filter);
			else if (attachType.equals(AttachType.PAYROLL))
				getAttachment().deletePayrollAttach(ctx, filter);
			else if (attachType.equals(AttachType.PROJECT))
				getAttachment().deleteProjectAttach(ctx, filter);
			else if (attachType.equals(AttachType.SEPE))
				getAttachment().deleteSepeAttach(ctx, filter);
			else if (attachType.equals(AttachType.DATA))
				getAttachment().deleteDataAttach(ctx, filter);
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer insertRegistryAttachTag(String domainName,
			Integer domainId, String login, Integer rattachId, Integer tagId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAttachment().insertRegistryAttachTag(ctx, rattachId,
					tagId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteRegistryAttachTag(String domainName,
			Integer domainId, String login, Integer rattachId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getAttachment().deleteRegistryAttachTag(ctx, rattachId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ******************************* WAREHOUSE **
	// ********************************************

	public static Stream<Warehouse> getWarehouseStream(String domainName, Integer domainId, String login, WarehouseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getWarehouseStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Warehouse getWarehouse(String domainName, Integer domainId,
			String login, WarehouseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getWarehouse(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Warehouse> getWarehouseList(String domainName, Integer domainId,
			String login, WarehouseFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getWarehouseStream(ctx, filter)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<IncomeDetail> getIncomeDetails(String domainName,
			Integer domainId, String login, IncomeFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getIncomeDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Income> getIncomeStream(String domainName, Integer domainId, String login, IncomeFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static LinkedList<Income> getIncomeList(String domainName, Integer domainId, String login, IncomeFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeStream(ctx, filter)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static Optional<Income> getIncome(String domainName, Integer domainId, String login, IncomeFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static Optional<Income> insertIncome(String domainName, Integer domainId, String login, Income income){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertIncome(ctx, income);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<IncomeDetail> insertIncomeDetail(String domainName, Integer domainId, String login, IncomeDetail incomeDetail){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertIncomeDetail(ctx, incomeDetail);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<IncomeDetail> updateIncomeDetail(String domainName, Integer domainId, String login, IncomeDetail incomeDetail){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().updateIncomeDetail(ctx, incomeDetail);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<IncomeDetail> deleteIncomeDetail(String domainName, Integer domainId, String login, Integer id){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().deleteIncomeDetail(ctx, id);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<IncomeDetail> getIncomeDetailStream(String domainName, Integer domainId, String login, IncomeDetailFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeDetailStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static Optional<IncomeDetail> getIncomeDetail(String domainName, Integer domainId, String login, IncomeDetailFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeDetailStream(ctx, filter)
					.findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static IncomeDetail getLastIncomeDetail(String domainName,
			Integer domainId, String user, Item item, Integer workplaceId,
			Integer warehouseId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getLastIncomeDetail(ctx, item, workplaceId,
					warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static IncomeDetail getLastIncomeDetailUntilDate(String domainName,
			Integer domainId, String user, Item item, Integer workplaceId,
			Integer warehouseId, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getLastIncomeDetailUntilDate(ctx, item,
					workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<IncomeDetail> getLastIncomeDetailList(
			String domainName, Integer domainId, String user, Item item,
			String months, Integer workplaceId, Integer warehouseId) {
		Calendar calendar = Calendar.getInstance();
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getLastIncomeDetailList(ctx, item,
					calendar.getTime(), workplaceId, warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<IncomeDetail> getLastIncomeDetailListUntilDate(
			String domainName, Integer domainId, String user, Item item,
			String months, Integer workplaceId, Integer warehouseId,
			Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getLastIncomeDetailListUntilDate(ctx, item,
					calendar.getTime(), workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<IncomeDetail> getIncomeDetailList(
			String domainName, Integer domainId, String user, Item item,
			Integer workplaceId, Integer warehouseId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getIncomeDetailList(ctx, item, workplaceId,
					warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<IncomeDetail> getIncomeDetailListUntilDate(
			String domainName, Integer domainId, String user, Item item,
			Integer workplaceId, Integer warehouseId, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getIncomeDetailListUntilDate(ctx, item,
					workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InventoryDetail> getInventoryDetailList(
			String domainName, Integer domainId, String user,
			Integer inventoryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getInventoryDetailList(ctx, inventoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Inventory> getInventoryList(String domainName,
			Integer domainId, String login, Date startDate, Date endDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getInventoryList(ctx, startDate, endDate);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateInventoryDetail(String domainName,
			Integer domainId, String login, InventoryDetail inventoryDetail) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().updateInventoryDetail(ctx, inventoryDetail);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateZeroInventoryDetail(String domainName,
			Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().updateZeroInventoryDetail(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Department getDepartment(String domainName, Integer domainId,
			String login, Integer workplaceId, DepartmentFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getDepartment(ctx, workplaceId, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Department> getDepartmentList(String domainName,
			Integer domainId, String login, Integer workplaceId,
			DepartmentFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getDepartmentList(ctx, workplaceId, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Series> getSeriesDeliveryList(String domainName,
			Integer domainId, String login, Integer scopeId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getSeriesDeliveryList(ctx, scopeId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Series getSeries(String domainName, Integer domainId, String login, SeriesFilter filter){
		return getSeriesStream(domainName, domainId, login, filter).findFirst().orElse(new Series());
	}
	
	public static LinkedList<Series> getSeriesList(String domainName, Integer domainId, String login, SeriesFilter filter){
		return getSeriesStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Series> getSeriesStream(String domainName,Integer domainId, String login, SeriesFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getSeriesStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------------------------------------------------------- STATS
	public static StatParams createStatParams(String domainName, int domain,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getStats().createStatParams(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static StatData<String, String, Double> getStatData(String domainName,
			Integer domainId, String user, StatParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,user);
			return getStats().getStatData(ctx, params);
		} finally {
			if (ctx != null) 
				ctx.close();
		}
	}

	public static String getInvoicesReport(String domainName, int domain, String userLogin, StatParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domain,userLogin);
			return getStats().getInvoicesReport(ctx, params);
		} finally {
			if (ctx != null) 
				ctx.close();
		}
	}
	
	public static Stream<Task> getStatTaskStream(String domainName, Integer domainId, String login, StatParams params){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,login);
			return getStats().getStatTaskStream(ctx, params);
		} finally {
			if (ctx != null) 
				ctx.close();
		}		
	}
	// ********************************************
	// ********************************* Project **
	// ********************************************

	public static Stream<Project> getProjectStream(String domainName, Integer domainId, String login, ProjectFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Project getProject(String domainName, Integer domainId, String login, ProjectFilter filter) {
		return getProjectStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Project());
	}

	public static LinkedList<Project> getProjectList(String domainName, Integer domainId, String login, ProjectFilter filter) {
		return getProjectStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Integer insertProject(String domainName, Integer domainId,
			String login, Project project) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().insertProject(ctx, project);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ProjectReservation getProjectReservation(String domainName,
			Integer domainId, String login, Integer projectId) {
		return getProjectReservation(domainName, domainId, login, f -> f.getProjectProperty().eq(projectId));
	}
	
	public static ProjectReservation getProjectReservation(String domainName,
			Integer domainId, String login, ProjectReservationFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectReservation(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	
	public static Stream<ProjectReservation> getProjectReservationStream(String domainName,
			Integer domainId, String login, ProjectReservationFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectReservationStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<ProjectCommercial> getProjectCommercialStream(String domainName,
			Integer domainId, String login, ProjectCommercialFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectCommercialStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<ProjectCommercial> getProjectCommercialList(String domainName, Integer domainId, String login, ProjectCommercialFilter filter) {
		return getProjectCommercialStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static ProjectCommercial getProjectCommercial(String domainName, Integer domainId, String login, ProjectCommercialFilter filter) {
		return getProjectCommercialStream(domainName, domainId, login, filter)
				.findFirst().orElse(new ProjectCommercial());
	}

	// ********************************************
	// ******************************* Warehouse **
	// ********************************************

	public static Double addStock(Domain domain, String login, Integer item, Double quantity, Integer warehouse){
		return stock(domain, login, item, quantity, warehouse);
	}
	
	public static Double substractStock(Domain domain, String login, Integer item, Double quantity, Integer warehouse){
		return stock(domain, login, item, -quantity, warehouse);
	}
	
	private static Double stock(Domain domain, String login, Integer item, Double quantity, Integer warehouse) {
		Double q = quantity;
		Optional<Stock> stockOptional = getStockStream(domain.getName(), domain.getId(), login, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getItemProperty().eq(item))
				.and(f.getWarehouseProperty().eq(warehouse))).findFirst();
		if(stockOptional.isPresent()){
			Stock stock = stockOptional.get();
			q = stock.getQuantity() + quantity;
			if(q == 0.0){
				AON.deleteStock(domain.getName(), domain.getId(), login, stock.getId());
			} else {
				stock.setQuantity(q);
				AON.updateStock(domain.getName(), domain.getId(), login, stock);
			}
		} else {
			Stock stock = new Stock().setDomain(domain.getId())
					.setItem(item)
					.setQuantity(quantity)
					.setWarehouse(warehouse);
			AON.insertStock(domain.getName(), domain.getId(), login, stock);
		}
		return q;
	}

	public static LinkedList<Stock> getStockList(String domainName, Integer domainId, String login, 
			StockFilter filter){
		return getStockStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Stock> getStockStream(String domainName, Integer domainId, String login, 
			StockFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getStockStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Stock> updateStock(String domainName, Integer domainId, String login, Stock stock){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().updateStock(ctx, stock);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Stock> deleteStock(String domainName, Integer domainId, String login, Integer stockId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().deleteStock(ctx, stockId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Stock> insertStock(String domainName, Integer domainId, String login, Stock stock){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertStock(ctx, stock);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer getWarehouseTransferNextNumber(String domainName, Integer domainId, String login, String serie){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getWarehouseTransferNextNumber(ctx, serie);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteWarehouseTransfer(String domainName,
			Integer domainId, String login, WarehouseTransferFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().deleteWarehouseTransfer(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateWarehouseTransfer(String domainName, Integer domainId, String login, 
			WarehouseTransfer warehouseTransfer){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().updateWarehouseTransfer(ctx, warehouseTransfer);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<WarehouseTransfer> getWarehouseTransferStream(String domainName, Integer domainId, String login,
			WarehouseTransferFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getWarehouseTransferStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static WarehouseTransfer getWarehouseTransfer(String domainName, Integer domainId, String login,
			WarehouseTransferFilter filter){
		return getWarehouseTransferStream(domainName, domainId, login, filter).findFirst().orElse(new WarehouseTransfer());
	}
	
	public static LinkedList<WarehouseTransfer> getWarehouseTransferList(String domainName, Integer domainId, String login,
			WarehouseTransferFilter filter){
		return getWarehouseTransferStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Integer insertWarehouseTransfer(String domainName, Integer domainId, String login,
			WarehouseTransfer warehouseTransfer){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertWarehouseTransfer(ctx, warehouseTransfer);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer insertWarehouseTransferDetail(String domainName, Integer domainId, String login,
			WarehouseTransferDetail warehouseTransferDetail){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertWarehouseTransferDetail(ctx, warehouseTransferDetail);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateInventory(String domainName, Integer domainId,
			String login, Inventory inventory) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().updateInventory(ctx, inventory);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Inventory> getTwoLastInventory(String domainName,
			Integer domainId, String login, Integer warehouseId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getTwoLastInventory(ctx, warehouseId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteInventory(String domainName, Integer domainId,
			String login, Integer inventoryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().deleteInventory(ctx, inventoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------ SUPPLIER 
	
	public static Stream<Supplier> getSupplierStream(String domainName, Integer domainId, String login, SupplierFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getSupplierStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Supplier> getSupplierList(String domainName, Integer domainId, String login, SupplierFilter filter) {
		return getSupplierStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Optional<Supplier> getSupplier(String domainName, Integer domainId, String login, SupplierFilter filter) {
		return getSupplierStream(domainName, domainId, login, filter)
				.findFirst();
	}
	
	public static Optional<Supplier> getSupplier(String domainName, Integer domainId, String login, Integer id) {
		return getSupplier(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	
	// ------------------ CARRIER 
	
	public static Stream<Carrier> getCarrierStream(String domainName, Integer domainId, String login, CarrierFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCarrierStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Carrier> getCarrierList(String domainName, Integer domainId, String login, CarrierFilter filter) {
		return getCarrierStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Carrier getCarrier(String domainName, Integer domainId, String login, CarrierFilter filter) {
		return getCarrierStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Carrier());
	}
	
	public static Carrier getCarrier(String domainName, Integer domainId, String login, Integer id) {
		return getCarrier(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	// ------------------ CARRIER PACKING
	
	public static Stream<CarrierPacking> getCarrierPackingStream(String domainName, Integer domainId, String login, CarrierPackingFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getCarrierPackingStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<CarrierPacking> getCarrierPackingList(String domainName, Integer domainId, String login, CarrierPackingFilter filter) {
		return getCarrierPackingStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static CarrierPacking getCarrierPacking(String domainName, Integer domainId, String login, CarrierPackingFilter filter) {
		return getCarrierPackingStream(domainName, domainId, login, filter)
				.findFirst().orElse(new CarrierPacking());
	}
	
	public static CarrierPacking getCarrierPacking(String domainName, Integer domainId, String login, Integer id) {
		return getCarrierPacking(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Integer insertCarrierPacking(String domainName, Integer domainId, String login, CarrierPacking carrierPacking) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertCarrierPacking(ctx, carrierPacking);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static CarrierPacking updateCarrierPacking(String domainName, Integer domainId, String login, CarrierPacking carrierPacking, CarrierPackingFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().updateCarrierPacking(ctx, carrierPacking, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static CarrierPacking updateCarrierPacking(String domainName, Integer domainId, String login, CarrierPacking carrierPacking) {
		return updateCarrierPacking(domainName, domainId, login, carrierPacking, f -> f.getIdProperty().eq(carrierPacking.getId()));
	}
	
	public static void deleteCarrierPacking(String domainName, Integer domainId, String login, CarrierPackingFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().deleteCarrierPacking(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteCarrierPacking(String domainName, Integer domainId, String login, Integer id) {
		deleteCarrierPacking(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	// ------------------ ELABORATION
	
	public static Stream<Elaboration> getElaborationStream(String domainName, Integer domainId, String login,
			ElaborationFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getElaborationStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Elaboration getFullElaboration(String domainName,
			Integer domainId, String login, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			Elaboration e = getWarehouse().getElaboration(ctx, id);
			e.setItem(AON.getItem(domainName, domainId, login, e.getItem()
					.getId()));
			e.setWarehouse(getWarehouse(
					domainName,
					domainId,
					login,
					f -> f.getIdProperty().eq(
							e.getWarehouse().getId())));
			return e;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	
	public static Integer insertElaboration(String domainName, Integer domainId, String login, Elaboration elaboration) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertElaboration(ctx, elaboration);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Elaboration updateElaboration(String domainName, Integer domainId, String login, Elaboration elaboration) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().updateElaboration(ctx, elaboration);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Elaboration deleteElaboration(String domainName, Integer domainId, String login, Integer elaborationId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().deleteElaboration(ctx, elaborationId);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Integer getElaborationNextNumber(
			String domainName, Integer domainId, String login,
			String series) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getElaborationNextNumber(ctx, series);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static List<ElaborationDetail> getElaborationDetailList(
			String domainName, Integer domainId, String login,
			Integer elaborationId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			List<ElaborationDetail> list = getWarehouse()
					.getElaborationDetailList(ctx, elaborationId);
			list.forEach(detail -> {
				detail.setItem(AON.getItem(domainName, domainId, login, detail
						.getItem().getId()));
				detail.setWarehouse(getWarehouse(
						domainName,
						domainId,
						login,
						f -> f.getIdProperty().eq(
								detail.getWarehouse().getId())));
			});
			return list;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ElaborationDetail getFullElaborationDetail(String domainName,
			Integer domainId, String login, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			ElaborationDetail d = getWarehouse().getElaborationDetail(ctx, id);
			d.setItem(AON.getItem(domainName, domainId, login, d.getItem()
					.getId()));
			d.setWarehouse(getWarehouse(
					domainName,
					domainId,
					login,
					f -> f.getIdProperty().eq(
							d.getWarehouse().getId())));
			return d;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer insertElaborationDetail(String domainName, Integer domainId, String login, ElaborationDetail detail) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			int id = getWarehouse().insertElaborationDetail(ctx, detail);
			// TODO AON.addStock 
//			AON.addStock(getDomain(domainName, domainId, login), login,
//					detail.getItem().getId(), detail.getQuantity(), detail.getWarehouse().getId());
			return id;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static ElaborationDetail updateElaborationDetail(String domainName, Integer domainId, String login, ElaborationDetail detail) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			detail = getWarehouse().updateElaborationDetail(ctx, detail);
			// TODO AON.addStock && AON.substractStock  
//			Integer id = detail.getId();
//			Double oldQuantity = AON.getElaborationStream(domainName, domainId, login,
//					f -> f.getIdProperty().eq(id))
//					.findFirst().get().getQuantity();
//			AON.substractStock(getDomain(domainName, domainId, login), login,
//					detail.getItem().getId(), oldQuantity, detail.getWarehouse().getId());
//			AON.addStock(getDomain(domainName, domainId, login), login,
//					detail.getItem().getId(), detail.getQuantity(), detail.getWarehouse().getId());
			return detail;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static ElaborationDetail deleteElaborationDetail(String domainName, Integer domainId, String login, ElaborationDetailFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			ElaborationDetail detail = getWarehouse().deleteElaborationDetail(ctx, filter);
			// TODO AON.substractStock 
//			AON.substractStock(getDomain(domainName, domainId, login), login,
//					detail.getItem().getId(), detail.getQuantity(), detail.getWarehouse().getId());
			return detail;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static List<ElaborationDetailComposition> getElaborationDetailCompositionList(
			String domainName, Integer domainId, String login,
			Integer elaborationDetailId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			List<ElaborationDetailComposition> list = getWarehouse()
					.getElaborationDetailCompositionList(ctx,
							elaborationDetailId);
			list.forEach(composition -> {
				composition.setItem(AON.getItem(domainName, domainId, login, composition
						.getItem().getId()));
				composition.setWarehouse(getWarehouse(
						domainName,
						domainId,
						login,
						f -> f.getIdProperty().eq(
								composition.getWarehouse().getId())));
			});
			return list;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer insertElaborationDetailComposition(String domainName, Integer domainId, String login,
			ElaborationDetailComposition composition) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			// TODO AON.addStock  
			return getWarehouse().insertElaborationDetailComposition(ctx, composition);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ElaborationDetailComposition updateElaborationDetailComposition(String domainName, Integer domainId,
			String login, ElaborationDetailComposition composition) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			// TODO AON.addStock && AON.substractStock  
			return getWarehouse().updateElaborationDetailComposition(ctx, composition);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ElaborationDetailComposition deleteElaborationDetailComposition(String domainName, Integer domainId,
			String login, ElaborationDetailCompositionFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			// TODO AON.substractStock  
			return getWarehouse().deleteElaborationDetailComposition(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ********************************************
	// ******************************** Registry **
	// ********************************************
	
	public static Registry insertRegistry(String domainName, Integer domainId, String login, Registry registry){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().insertRegistry(ctx, registry);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Registry updateRegistry(String domainName, Integer domainId, String login, Registry registry){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().updateRegistry(ctx, registry);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Registry deleteRegistry(String domainName, Integer domainId, String login, Integer registry){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().deleteRegistry(ctx, registry);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	// ------------------- CUSTOMER
	
	public static Stream<Customer> getCustomerStream(String domainName, Integer domainId, String login, CustomerFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCustomerStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Customer> getCustomerList(String domainName, Integer domainId, String login, CustomerFilter filter){
		return getCustomerStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Customer getCustomer(String domainName, Integer domainId, String login, CustomerFilter filter){
		return getCustomerStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Customer());
	}
	
	public static Customer getCustomer(String domainName, Integer domainId, String login, Integer registry){
		return getCustomer(domainName, domainId, login, f -> f.getRegistryProperty().eq(registry));
	}
	
	// ------------------- SELLER
	
	public static Stream<Seller> getSellerStream(String domainName, Integer domainId, String login, SellerFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getSellerStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Seller> getSellerList(String domainName, Integer domainId, String login, SellerFilter filter){
		return getSellerStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Seller getSeller(String domainName, Integer domainId, String login, SellerFilter filter){
		return getSellerStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Seller());
	}
	
	public static Seller getSeller(String domainName, Integer domainId, String login, Integer registry){
		return getSeller(domainName, domainId, login, f -> f.getRegistryProperty().eq(registry));
	}
	// ------------------------------------- CATEGORY

	public static Category getCategory(String domainName, Integer domainId,
			String login, Integer categoryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCategory(ctx, categoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Category> getCategoryStream(String domainName, Integer domainId, String login, CategoryFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCategoryStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Category> getCategoryList(String domainName,
			Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCategoryList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ------------------------------------- RMEDIA
	
	public static Stream<RegistryMedia> getRMediaStream(String domainName, Integer domainId, String login,
			RegistryMediaFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRMediaStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	public static RegistryMedia getRMedia(String domainName, Integer domainId, String login,
			RegistryMediaFilter filter) {
		return getRMediaStream(domainName, domainId, login, filter)
			.findFirst().orElse(new RegistryMedia());
	}
		
	public static LinkedList<RegistryMedia> getRMediaList(String domainName, Integer domainId, String login,
			RegistryMediaFilter filter) {
		return getRMediaStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static RegistryMedia insertRMedia(String domainName, Integer domainId, String login, RegistryMedia rmedia) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().insertRMedia(ctx, rmedia);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static RegistryMedia updateRMedia(String domainName, Integer domainId, String login, RegistryMedia rmedia) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().updateRMedia(ctx, rmedia);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static RegistryMedia deleteRMedia(String domainName, Integer domainId, String login, Integer registry) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().deleteRMedia(ctx, registry);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	// ------------------------------------- RNOTE
	
	public static Stream<RegistryNote> getRNoteStream(String domainName, Integer domainId, String login,
			RegistryNoteFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRNoteStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	public static RegistryNote getRNote(String domainName, Integer domainId, String login,
			RegistryNoteFilter filter) {
		return getRNoteStream(domainName, domainId, login, filter)
			.findFirst().orElse(new RegistryNote());
	}
		
	public static LinkedList<RegistryNote> getRNoteList(String domainName, Integer domainId, String login,
			RegistryNoteFilter filter) {
		return getRNoteStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	// ------------------------------------- RITEM
	
	public static Stream<RegistryItem> getRItemStream(String domainName, Integer domainId, String login, RegistryItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<RegistryItem> getRItemList(String domainName, Integer domainId, String login, RegistryItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static RegistryItem getRItem(String domainName, Integer domainId, String login, RegistryItemFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter)
				.findFirst().orElse(new RegistryItem());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Segment> getRSegmentStream(String domainName, Integer domainId, String login,
			Integer registryId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRSegmentStream(ctx, registryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}
	
	public static Stream<Seller> getRSellerStream(String domainName, Integer domainId, String login,
			Integer registryId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRSellerStream(ctx, registryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}
	
	public static Stream<RAddress> getRAddressStream(String domainName, Integer domainId, String login, RegistryAddressFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRAddressStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}

	public static RAddress getRAddress(String domainName, Integer domainId, String login, RegistryAddressFilter filter){
		return getRAddressStream(domainName, domainId, login, filter)
				.findFirst().orElse(new RAddress());
	}
	
	public static RAddress getRAddres(String domainName, Integer domainId, String login,
			Integer registryId){
		return getRAddressStream(domainName, domainId, login, f -> f.getRegistryProperty().eq(registryId))
				.findFirst().orElse(new RAddress());
	}
	
	// ------------------- RECORD DATA
	
	public static Stream<RecordData> getRecordDataStream(String domainName, Integer domainId, String login, RecordDataFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRecordDataStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	public static RecordData getRecordData(String domainName, Integer domainId, String login, RecordDataFilter filter) {
		return getRecordDataStream(domainName, domainId, login, filter)
			.findFirst().orElse(new RecordData());
	}
		
	public static LinkedList<RecordData> getRecordDataList(String domainName, Integer domainId, String login, RecordDataFilter filter) {
		return getRecordDataStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	
	// ********************************************
	// ******************************** CREDITOR **
	// ********************************************

	public static Stream<Creditor> getBasicCreditors(String domainName,
			int domainId, String login, CreditorFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getBasicCreditors(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	// ********************************************
	// ****************************** Commercial **
	// ********************************************

	// -------------------- COMMERCIAL TRACKING

	public static CommercialTracking getCommercialTracking(String domainName,
			Integer domainId, String login, CommercialTrackingFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getCommercialTracking(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<CommercialTracking> getCommercialTrackingStream(String domainName, Integer domainId, String login,	CommercialTrackingFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getCommercialTrackingStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<CommercialTracking> getCommercialTrackingList(String domainName, Integer domainId, String login, CommercialTrackingFilter filter) {
		return getCommercialTrackingStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static void updateEventId(String domainName, Integer domainId,
			String login, Integer ctId, String eventId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommercial().updateEventId(ctx, ctId, eventId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// -------------------- COMMERCIAL ACTIVITY

	public static CommercialActivity getCommercialActivity(String domainName,
			Integer domainId, String login, CommercialActivityFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getCommercialActivity(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<CommercialActivity> getCommercialActivityList(
			String domainName, Integer domainId, String login,
			CommercialActivityFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getCommercialActivityList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ***************************** Marketplace **
	// ********************************************

	public static LinkedList<Tag> getMatketplaceTagList(String domainName,
			Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getMarketplace().getMarketplaceTagList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InvoiceSeries> getInvoiceSeries(String domainName,
			int domainId, String login, Date from, Date to, boolean taxDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoiceSeries(ctx, from, to, taxDate);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static MailAccount getMailAccount(String domainName,
			Integer domainId, String login, MailAccountFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getMailAccount(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<MailAccount> getMailAccountList(String domainName,
			Integer domainId, String login, MailAccountFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getMailAccountList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Contact getContact(String domainName, Integer domainId,
			String login, ContactFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getContact(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Contact> getContactList(String domainName,
			Integer domainId, String login, ContactFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getContactList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getContactEmail(String domainName, Integer domainId,
			String login, Integer contactDataId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getContactEmail(ctx, contactDataId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ************************************* TAX **
	// ********************************************

	public static Stream<Tax> getTaxStream(String domainName, Integer domainId, String login, TaxFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getTaxStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Tax> getTaxList(String domainName, Integer domainId, String login, TaxFilter filter){
		return getTaxStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Tax getTax(String domainName, Integer domainId, String login, Integer id){
		return getTax(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Tax getTax(String domainName, Integer domainId, String login, String name){
		return getTax(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId)
				.and(f.getNameProperty().eq(name)));
	}
	
	public static Tax getTax(String domainName, Integer domainId, String login, TaxFilter filter){
		return getTaxStream(domainName, domainId, login, filter).findFirst().orElse(new Tax());
	}
	
	// ********************************************
	// ************************************ TAGS **
	// ********************************************
	
	public static Tag getTag(String domainName, Integer domainId, String login,
			Integer tagId){
		return getTag(domainName, domainId, login, f-> f.getIdProperty().eq(tagId));
	}

	public static Tag getTag(String domainName, Integer domainId, String login, 
			TagFilter filter){
		return getTagStream(domainName, domainId, login, filter).findFirst().orElse(new Tag());	
	}
	
	public static LinkedList<Tag> getTagList(String domainName, Integer domainId, String login,
			TagFilter filter){
		return getTagStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Tag> getTagStream(String domainName, Integer domainId, String login,
			TagFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getTagStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Tag insertTag(String domainName, Integer domainId, String login, Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertTag(ctx, tag);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateTag(String domainName, Integer domainId, String login,
			Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().updateTag(ctx, tag);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteTag(String domainName, Integer domainId, String login,
			Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().deleteTag(ctx, f -> f.getIdProperty().eq(tag.getId()));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteTag(String domainName, Integer domainId, String login, TagFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().deleteTag(ctx, filter);
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	//-------------------- TASK
	
	public static Boolean isTaskParent(String domainName, Integer domainId, String login, Integer parentId){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().isTaskParent(ctx, parentId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Task getTask(String domainName, Integer domainId, String login, TaskFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTask(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<Task> getTaskStream(String domainName, Integer domainId, String login, TaskFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<Task> getTaskStream(String domainName, Integer domainId, String login, TaskFilter filter,  IssueFilter issueFilter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskStream(ctx, filter, issueFilter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<Task> getDuplicateTaskStream(String domainName, Integer domainId, String login, Integer parent){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getDuplicateTaskStream(ctx, parent);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Integer[] getTaskCount(String domainName, Integer domainId, String login, TaskFilter filter,  IssueFilter issueFilter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskCount(ctx, filter, issueFilter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Task> getTaskList(String domainName, Integer domainId, String login, TaskFilter filter, IssueFilter issueFilter){
		return getTaskStream(domainName, domainId, login, filter, issueFilter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Tag> getTaskLabelStream(String domainName, Integer domainId, String login, TaskTagFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskLabelStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Tag> getTaskLabelList(String domainName, Integer domainId, String login, TaskTagFilter filter){
		return getTaskLabelStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Integer getLastTaskNumber(String domainName, Integer domainId, String login) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getLastTaskNumber(ctx);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Task createTask(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return task.setId(getTask().createTask(ctx, task));
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTask(String domainName, Integer domainId, String login, TaskFilter filter){
		AONContext ctx = null;	
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTask(ctx, filter);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	public static void updateTaskUser(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().updateTaskUser(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void updateTaskStatus(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().updateTaskStatus(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void updateTaskParent(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().updateTaskParent(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void updateTaskDescription(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().updateTaskDescription(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void updateTaskTitle(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().updateTaskTitle(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void updateTaskPriority(String domainName, Integer domainId, String login, Task task){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().updateTaskPriority(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	//-------------------- TASK COMMENT
	
	public static Stream<TaskComment> getTaskCommentStream(String domainName, Integer domainId, String login, TaskCommentFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskCommentStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskComment getTaskComment(String domainName, Integer domainId, String login, TaskCommentFilter filter){
		return getTaskCommentStream(domainName, domainId, login, filter).findFirst().orElse(new TaskComment());
	}
	
	public static LinkedList<TaskComment> getTaskCommentList(String domainName, Integer domainId, String login, TaskCommentFilter filter){
		return getTaskCommentStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Integer getCommentsCount(String domainName, Integer domainId, String login, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getCommentsCount(ctx, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskComment getLastTaskComment(String domainName, Integer domainId, String login, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getLastTaskComment(ctx, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<TaskComment> getTaskCommentStream(String domainName, Integer domainId, String login, Integer taskId) {
		return getTaskCommentStream(domainName, domainId, login, f -> f.getTaskProperty().eq(taskId));
	}
	
	public static LinkedList<TaskComment> getTaskCommentList(String domainName, Integer domainId, String login, Integer taskId) {
		return getTaskCommentStream(domainName, domainId, login, taskId)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static TaskComment getTaskComment(String domainName, Integer domainId, String login, Integer taskCommentId) {
		return getTaskCommentStream(domainName, domainId, login, f -> f.getIdProperty().eq(taskCommentId))
			.findFirst().orElse(new TaskComment());
	}
	
	public static TaskComment createTaskComment(String domainName, Integer domainId, String login, TaskComment taskComment, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().createTaskComment(ctx, taskComment, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskComment updateTaskComment(String domainName, Integer domainId, String login, TaskComment taskComment) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateTaskComment(ctx, taskComment);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskComment(String domainName, Integer domainId, String login, TaskCommentFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskComment(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	//-------------------- TASK EVENT
	
	public static TaskEvent getTaskEvent(String domainName, Integer domainId, String login, Integer taskEventId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEvent(ctx, taskEventId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent getTaskEvent(String domainName, Integer domainId, String login, TaskEventFilter filter) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEvent(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent getLastTaskEvent(String domainName, Integer domainId, String login, TaskEventFilter filter) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getLastTaskEvent(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent createTaskEvent(String domainName, Integer domainId, String login, TaskEvent taskEvent, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().createTaskEvent(ctx, taskEvent, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskEvent(String domainName, Integer domainId, String login, TaskEventFilter filter) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskEvent(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent updateTaskEvent(String domainName, Integer domainId, String login, TaskEvent taskEvent, Integer taskCommentId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateTaskEvent(ctx, taskEvent, taskCommentId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<TaskEvent> getTaskEventStream(String domainName, Integer domainId, String login, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEventStream(ctx, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<TaskEvent> getTaskEventList(String domainName, Integer domainId, String login, Integer taskEventId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEventStream(ctx, taskEventId)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<TaskHolder> getTaskMemberWStream(String domainName, Integer domainId, String login, String filter, Integer workgroupId){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskMemberWStream(ctx, filter, workgroupId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<Workgroup> getTaskWorkgroupStream(String domainName, Integer domainId, String login, String filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskWorkgroupStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Workgroup> getTaskWorkgroupList(String domainName, Integer domainId, String login, String filter){
		return getTaskWorkgroupStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));	
	}
	
	public static void deleteTypeTaskTag(String domainName, Integer domainId, String login, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, taskId, TagType.TASK_TYPE);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deletePriorityTaskTag(String domainName, Integer domainId, String login, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, taskId, TagType.TASK_PRIORITY);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteLabelTaskTag(String domainName, Integer domainId, String login, Integer taskId) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, taskId, TagType.TASK_LABEL);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void createTaskTag(String domainName, Integer domainId, String login, TaskTag taskTag) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().createTaskTag(ctx, taskTag);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskTag(String domainName, Integer domainId, String login, TaskTagFilter filter) {
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<Customer> getFilterCustomerStream(String domainName, Integer domainId, String login, String filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getFilterCustomerStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Customer> getFilterCustomerList(String domainName, Integer domainId, String login, String filter){
		return getFilterCustomerStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	// ------------------- TASK HOLDER

	public static Stream<TaskHolder> getTaskHolderStream(String domainName, Integer domainId, String login, TaskHolderFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskHolderStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static TaskHolder getTaskHolder(String domainName, Integer domainId, String login, TaskHolderFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskHolderStream(ctx, filter).findFirst().orElse(new TaskHolder());
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void insertTaskHolder(String domainName, Integer domainId, String login, TaskHolder taskHolder){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().insertTaskHolder(ctx, taskHolder);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskHolder(String domainName, Integer domainId, String login, Integer taskHolder){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskHolder(ctx, taskHolder);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Boolean isTaskHolderWorkgroup(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().isTaskHolderWorkgroup(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void insertTaskHolderWorkgroup(String domainName, Integer domainId, String login, Integer taskHolderId, Integer workgroupId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().insertTaskHolderWorkgroup(ctx, taskHolderId, workgroupId);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskHolderWorkgroup(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskHolderWorkgroup(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<Workgroup> getTaskHolderWorkgroupStream(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskHolderWorkgroupStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Workgroup> getTaskHolderWorkgroupList(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		return getTaskHolderWorkgroupStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	// ------------------- WORKGROUP
	public static Workgroup getWorkgroup(String domainName, Integer domainId, String login, Integer wId){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getWorkgroup(ctx, wId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Workgroup insertWorkgroup(String domainName, Integer domainId, String login, Workgroup workgroup){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().insertWorkgroup(ctx, workgroup);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Workgroup updateWorkgroup(String domainName, Integer domainId, String login, Workgroup workgroup){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateWorkgroup(ctx, workgroup);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Workgroup deleteWorkgroup(String domainName, Integer domainId, String login, Integer workgroup){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().deleteWorkgroup(ctx, workgroup);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<Question> getRegistryQuestionStream(String domainName, Integer domainId, String login, Integer registry){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistryQuestionStream(ctx, registry);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<RegistryProfile> getRegistryProfileStream(String domainName, Integer domainId, String login, Integer question, Integer registry){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistryProfileStream(ctx, registry, question);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<DataResponse> getDataResponseStream(String domainName, Integer domainId, String login, DataResponseFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponse getDataResponse(String domainName, Integer domainId, String login, DataResponseFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseStream(ctx, filter).findFirst().orElse(null);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponse insertDataResponse(String domainName, Integer domainId, String login, DataResponse dataResponse){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertDataResponse(ctx, dataResponse);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponse updateDataResponse(String domainName, Integer domainId, String login, DataResponse dataResponse, DataResponseFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().updateDataResponse(ctx, dataResponse, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponse deleteDataResponse(String domainName, Integer domainId, String login, DataResponseFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().deleteDataResponse(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<DataResponseDetail> getDataResponseDetailStream(String domainName, Integer domainId, String login, DataResponseDetailFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseDetailStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static Optional<DataResponseDetail> getDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetailFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseDetailStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponseDetail insertDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetail dataResponseDetail){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertDataResponseDetail(ctx, dataResponseDetail);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponseDetail updateDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetail dataResponseDetail, DataResponseDetailFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().updateDataResponseDetail(ctx, dataResponseDetail, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponseDetail deleteDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetailFilter filter){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().deleteDataResponseDetail(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

}
