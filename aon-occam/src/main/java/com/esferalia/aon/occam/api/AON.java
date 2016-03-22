package com.esferalia.aon.occam.api;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialActivityFilter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.CommercialTrackingFilter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachQueryProperties;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroupFilter;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeContainer;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.AgreementImpl;
import com.esferalia.aon.occam.impl.jooq.AttachmentImpl;
import com.esferalia.aon.occam.impl.jooq.CommercialImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FeeImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
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
import com.esferalia.aon.occam.impl.jooq.WarehouseImpl;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class AON {

	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}

	private static ICommon getCommon() {
		return new CommonImpl();
	}

	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}

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

	private static IFee getFee() {
		return new FeeImpl();
	}

	private static IOffice getOffice() {
		return new OfficeImpl();
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

	public static Company getCompanyForDomain(String domainName, int domainId,
			String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getCompany(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<CompanyBank> getCompanyBanks(String domainName,
			int domain, String login, int enterprise) {
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

	// ------------------------------------ PRODUCT
	public static Product getProduct(String domainName, Integer domainId,
			String login, Integer productId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct(ctx, productId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
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

	// ********************************************
	// ****************************** ACCOUNTING **
	// ********************************************
	public static Account getAccount(AONContext ctx, Integer id) {
		return getAccounting().getAccount(ctx, id);
	}

	public static Account getAccount(AONContext ctx, String code) {
		return getAccounting().getAccount(ctx, code);
	}

	public static Account getAccount(String domainName, int domainId,
			String code, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccount(ctx, code);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Account> getAccounts(String domainName, int domainId,
			String login, AccountFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccounting().getAccounts(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	// ********************************************
	// ********************************* PRODUCT **
	// ********************************************

	// ------------------------------------ PRODUCT

	public static Product getProduct(AONContext ctx, Integer id) {
		return getProduct().getProduct(ctx, id);
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

	public static Item getItem(String domainName, Integer domainId,
			String login, Integer itemId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItem(ctx, itemId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

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
	// ****************************** ACCOUNTING **
	// ********************************************

	// ----------------------------- ACCOUNT PERIOD
	public static AccountPeriod fetchPeriod(AONContext ctx, Date date) {
		return getAccounting().fetchPeriod(ctx, date);
	}

	public static AccountPeriod fetchPeriodByYear(AONContext ctx, int year) {
		return getAccounting().fetchPeriodByYear(ctx, year);
	}

	public static AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return getAccounting().fetchPeriod(ctx, id);
	}

	public static void insert(AONContext ctx, AccountPeriod ap) {
		getAccounting().insert(ctx, ap);
	}

	public static void update(AONContext ctx, AccountPeriod ap) {
		getAccounting().update(ctx, ap);
	}

	public static void delete(AONContext ctx, AccountPeriod ap) {
		getAccounting().delete(ctx, ap);
	}

	// ------------------------------ ACCOUNT PERIOD
	public static LinkedList<AccountPeriod> getDomainPeriods(String domainName,
			int domain, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getDomainPeriods(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------------------ ACCOUNT ENTRY
	public static LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, String user, final AccountEntryParams params,
			int offset, int limit) throws AonCoreException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountEntries(ctx, params, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountEntry save(String domainName, int domain, String user,
			AccountEntry ae) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			Integer id = getAccounting().save(ctx, ae);
			AccountEntry saved = getAccounting().getAccountEntry(ctx, id);
			return saved;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, String user, AccountEntryFilter filter, int offset,
			int limit) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccountEntries(ctx, filter, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	public static Stream<AccountEntry> getAccountEntries(AONContext ctx,
			AccountEntryFilter filter, int offset, int numberOfRows) {
		return getAccounting().getAccountEntries(ctx, filter, offset,
				numberOfRows);
	}

	public static boolean existsAnyEntry(AONContext ctx, Integer period,
			AccountEntryType accountEntryType) {
		return getAccounting().existsAnyEntry(ctx, period, accountEntryType);
	}

	public static void deleteAccountEntry(String domainName, int domain,
			String user, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			getAccounting().delete(ctx, id);
			;
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	public static LinkedHashMap<String, AccountBalance> getAccountBalances(
			AONContext ctx, AccMiningParameters params)
					throws AonCoreException {
		return getAccounting().getAccountBalances(ctx, params);
	}

	/**
	 * Inserta en el borrador contable un apunte de nóminas con los datos leídos
	 * desde nóminas:
	 * 
	 * @param ctx
	 *            Contexto de AON
	 * @param enterprise
	 *            Código de empresa
	 * @param from
	 *            Fecha desde la cual leer las nóminas.
	 * @param to
	 *            Fecha hasta la cual leer las nóminas.
	 * @param concept
	 *            Concepto que aparecerá en el apunte contable. Si el valor es
	 *            NULL, entonces "NÓMINAS" a piñon fijo.
	 * @param registryBank
	 *            Código del banco de la empresa por la que se pagarán las
	 *            nóminas, Si el valor es NULL la partida se destinará a
	 *            "Remuneraciones pendientes de pago"
	 * @return
	 * @return El apunte contable grabado.
	 */
	public static List<Integer> insertSalaryEntries(String domainName,
			int domain, String user, Date from, Date to, String concept,
			Integer registryBank) {
		return getAccounting().insertSalaryEntries(domainName, domain, user,
				from, to, concept, registryBank);
	}

	public static Stream<AccountEntry> getSalaryEntries(AONContext ctx,
			Date from, Date to, String concept, Integer registryBank) {
		Company company = getCommon().getCompany(ctx, ctx.getDomainId());
		return getSalary()
				.getSalaryAccountEntries(ctx, company.getId(), from, to,
						concept, registryBank)
				.map(sae -> getAccounting().getAccountEntry(ctx, sae));
	}

	// ------------------------------ ACCOUNT STATEMENT
	public static AccountStatementReport getAccountStatement(String domainName,
			int domain, String user, AccountStatementParams params) {
		AONContext ctx = null;
		try {
			AccountStatementReport report = new AccountStatementReport();
			ctx = AONContext.getAONContext(domainName, domain, user);
			report.setFrom(params.getFromDate());
			report.setTo(params.getToDate());
			report.setAccount(AON.getAccount(ctx, params.getAccount()));
			report.setSummary(getAccounting().getAccountBalance(ctx, params)
					.collect(Collectors.toCollection(LinkedList::new)));
			report.setDetails(getAccounting().getAccountStatement(ctx, params)
					.collect(Collectors.toCollection(LinkedList::new)));
			report = AccountStatementDAO.calculate(report);
			return report;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<AccountStatement> getAccountBalance(String domainName,
			int domain, String user, AccountStatementParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountBalance(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************* FINANCE **
	// ********************************************
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

	public static void insertFee(AONContext ctx, Fee f) {
		getFee().insertFee(ctx, f);
	}

	public static void insertFee(AONContext ctx, Stream<Fee> fs) {
		getFee().insertFee(ctx, fs);
	}

	public static void updateFee(AONContext ctx, Fee f) {
		getFee().updateFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Fee f) {
		getFee().deleteFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Stream<Fee> fs) {
		getFee().deleteFee(ctx, fs);
	}

	// ********************************************
	// ****************************** GWT-OFFICE **
	// ********************************************

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
	
	public static List<Notice> getNotices(Integer domainId, String domainName, String userName, NoticeFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getNotices(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static int getSelectedCount(Integer domainId, String domainName, String userName, NoticeFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getSelectedCount(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice createComment(Integer domainId, String domainName,
			String userName, Integer noticeHeadId, Notice comment) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().createComment(ctx, noticeHeadId, comment);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice editComment(Integer domainId, String domainName,
			String userName, Integer commentId, String body) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().editComment(ctx, commentId, body);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice addNewNotice(Integer domainId, String domainName,
			String userName, Notice notice) throws Exception {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().addNewNotice(ctx, notice);

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice editNotice(Integer domainId, String domainName,
			String userName, Notice notice) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().editNotice(ctx, notice);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice changeNoticeStatus(Integer domainId, String domainName,
			String userName, Notice notice) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().changeNoticeStatus(ctx, notice);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Tag addNewTag(Integer domainId, String domainName,
			String userName, Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().addNewTag(ctx, tag);
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

	public static List<RegistryMedia> getRMedias(Integer domainId,
			String domainName, String userName) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().getRMedias(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice addLabelsToAnIssue(Integer domainId, String domainName,
			String userName, Integer issueId, List<Tag> tagList) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().addLabelsToAnIssue(ctx, issueId, tagList);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static boolean removeLabelFromIssue(Integer domainId,
			String domainName, String userName, Integer issueId, Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().removeLabelFromIssue(ctx, issueId, tag);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Notice replaceLabelFromIssue(Integer domainId,
			String domainName, String userName, Integer issueId,
			List<Tag> addLabels, List<Tag> deletedTags) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);
			return getOffice().replaceLabelsForIssue(ctx, issueId, addLabels,
					deletedTags);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ****************************** ATTACHMENT **
	// ********************************************

	public static Attach getAttach(String domainName, Integer domainId,
			String login, AttachFilter filter, AttachType attachType) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			Attach attach = new Attach();

			if (attachType.equals(AttachType.REGISTRY))
				attach = getAttachment().getRegistryAttach(ctx, filter);
			else if (attachType.equals(AttachType.CONTRACT))
				attach = getAttachment().getContractAttach(ctx, filter);
			else if (attachType.equals(AttachType.INVOICE))
				attach = getAttachment().getInvoiceAttach(ctx, filter);
			else if (attachType.equals(AttachType.ITEM))
				attach = getAttachment().getItemAttach(ctx, filter);
			else if (attachType.equals(AttachType.OFFER))
				attach = getAttachment().getOfferAttach(ctx, filter);
			else if (attachType.equals(AttachType.PAYROLL))
				attach = getAttachment().getPayrollAttach(ctx, filter);
			else if (attachType.equals(AttachType.PROJECT))
				attach = getAttachment().getProjectAttach(ctx, filter);
			else if (attachType.equals(AttachType.SEPE))
				attach = getAttachment().getSepeAttach(ctx, filter);

			return attach;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Attach> getAttachList(String domainName,
			Integer domainId, String login, AttachFilter filter,
			AttachType attachType) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			LinkedList<Attach> attachList = new LinkedList<Attach>();

			if (attachType.equals(AttachType.REGISTRY))
				attachList = getAttachment().getRegistryAttachList(ctx, filter);
			else if (attachType.equals(AttachType.CONTRACT))
				attachList = getAttachment().getContractAttachList(ctx, filter);
			else if (attachType.equals(AttachType.INVOICE))
				attachList = getAttachment().getInvoiceAttachList(ctx, filter);
			else if (attachType.equals(AttachType.ITEM))
				attachList = getAttachment().getItemAttachList(ctx, filter);
			else if (attachType.equals(AttachType.OFFER))
				attachList = getAttachment().getOfferAttachList(ctx, filter);
			else if (attachType.equals(AttachType.PAYROLL))
				attachList = getAttachment().getPayrollAttachList(ctx, filter);
			else if (attachType.equals(AttachType.PROJECT))
				attachList = getAttachment().getProjectAttachList(ctx, filter);
			else if (attachType.equals(AttachType.SEPE))
				attachList = getAttachment().getSepeAttachList(ctx, filter);

			return attachList;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Attach> getAttachStream(String domainName,
			Integer domainId, String login, AttachFilter filter,
			AttachType attachType) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attachType.equals(AttachType.REGISTRY))
				return getAttachment().getRegistryAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.CONTRACT))
				return getAttachment().getContractAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.INVOICE))
				return getAttachment().getInvoiceAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.ITEM))
				return getAttachment().getItemAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.OFFER))
				return getAttachment().getOfferAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.PAYROLL))
				return getAttachment().getPayrollAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.PROJECT))
				return getAttachment().getProjectAttachStream(ctx, filter);
			else if (attachType.equals(AttachType.SEPE))
				return getAttachment().getSepeAttachStream(ctx, filter);
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Attach> getAttachList(String domainName,
			Integer domainId, String login, AttachFilter filter,
			AttachType attachType, AttachQueryProperties aqp) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			LinkedList<Attach> attachList = new LinkedList<Attach>();

			if (attachType.equals(AttachType.REGISTRY))
				attachList = getAttachment().getRegistryAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.CONTRACT))
				attachList = getAttachment().getContractAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.INVOICE))
				attachList = getAttachment().getInvoiceAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.ITEM))
				attachList = getAttachment().getItemAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.OFFER))
				attachList = getAttachment().getOfferAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.PAYROLL))
				attachList = getAttachment().getPayrollAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.PROJECT))
				attachList = getAttachment().getProjectAttachList(ctx, filter,
						aqp);
			else if (attachType.equals(AttachType.SEPE))
				attachList = getAttachment().getSepeAttachList(ctx, filter,
						aqp);

			return attachList;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer insert(String domainName, Integer domainId,
			String login, Attach attach) {
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
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void update(String domainName, Integer domainId, String login,
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

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateAttachData(String domainName, Integer domainId,
			String login, Attach attach) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(attach.getDomain().getName(),
					attach.getDomain().getId(), login);

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
				getAttachment().updateRegistryAttachDriveId(ctx, attachId,
						driveId);
			else if (attachType.equals(AttachType.CONTRACT))
				getAttachment().updateContractAttachDriveId(ctx, attachId,
						driveId);
			else if (attachType.equals(AttachType.INVOICE))
				getAttachment().updateInvoiceAttachDriveId(ctx, attachId,
						driveId);
			else if (attachType.equals(AttachType.ITEM))
				getAttachment().updateItemAttachDriveId(ctx, attachId, driveId);
			else if (attachType.equals(AttachType.OFFER))
				getAttachment().updateOfferAttachDriveId(ctx, attachId,
						driveId);
			else if (attachType.equals(AttachType.PAYROLL))
				getAttachment().updatePayrollAttachDriveId(ctx, attachId,
						driveId);
			else if (attachType.equals(AttachType.PROJECT))
				getAttachment().updateProjectAttachDriveId(ctx, attachId,
						driveId);
			else if (attachType.equals(AttachType.SEPE))
				getAttachment().updateSepeAttachDriveId(ctx, attachId, driveId);

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void delete(String domainName, Integer domainId, String login,
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

	public static StatData<Integer, InvoiceType, Double> getYearInvoiceTypeData(
			StatParams params, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(params.getDomainName(),
					params.getDomain(), user);
			return getStats().getYearInvoiceTypeData(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static StatData<Integer, InvoiceType, Double> getMonthInvoiceTypeData(
			StatParams params, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(params.getDomainName(),
					params.getDomain(), user);
			return getStats().getMonthInvoiceTypeData(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************* Project **
	// ********************************************

	public static Project getProject(String domainName, Integer domainId,
			String login, ProjectFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProject(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Project> getProjectList(String domainName,
			Integer domainId, String login, ProjectFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
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
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectReservation(ctx, projectId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ******************************* Warehouse **
	// ********************************************

	public static void deleteWarehouseTransfer(String domainName,
			Integer domainId, String login, Integer inventoryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().deleteWarehouseTransfer(ctx, inventoryId);
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

	// ********************************************
	// ******************************** Registry **
	// ********************************************

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

	public static LinkedList<CommercialTracking> getCommercialTrackingList(
			String domainName, Integer domainId, String login,
			CommercialTrackingFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getCommercialTrackingList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
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

	// -------------------- SELLER

	public static Seller getSeller(String domainName, Integer domainId,
			String login, Integer sellerId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getSeller(ctx, sellerId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ***************************** Marketplace **
	// ********************************************

	public static void updateTag(String domainName, Integer domainId,
			String login, Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().updateTag(ctx, tag);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteTag(String domainName, Integer domainId,
			String login, Tag tag) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommon().deleteTag(ctx, tag);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

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

	public static void deleteReservationCreditCard(String domainName,
			Integer domainId, String login, Integer reservationId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getPMS().deleteReservationCreditCard(ctx, reservationId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

}
