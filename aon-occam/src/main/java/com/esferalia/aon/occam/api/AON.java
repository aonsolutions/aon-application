package com.esferalia.aon.occam.api;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.Alarm;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Cnae;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Cno;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialActivityFilter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.CommercialTrackingFilter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Expedient;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.Filter.*;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.InvoiceCounter;
import com.esferalia.aon.occam.api.model.InvoiceUserData;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetMassiveParams;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.OldTask;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.ProjectTasFilter;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.TaskHolderParams;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryParams;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RattachTag;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.commission.Commission;
import com.esferalia.aon.occam.api.model.commission.CommissionCategory;
import com.esferalia.aon.occam.api.model.commission.CommissionItem;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.commission.CommissionTypeCommission;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;
import com.esferalia.aon.occam.api.model.config.ConfigBlock;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.ApiConfiguration;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetailExtended;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectTas;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
import com.esferalia.aon.occam.api.model.registry.DomainSigAddInfo;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RecordDataType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.RegistrySegment;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadContent;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.sales.SalesParams;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.tag.TagParams;
import com.esferalia.aon.occam.api.model.target.TargetParams;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffAddInfo;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryInfo;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDelivery;
import com.esferalia.aon.occam.api.model.warehouse.PaturpatQuality;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.UdapaQuality;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.AgreementImpl;
import com.esferalia.aon.occam.impl.jooq.AttachmentImpl;
import com.esferalia.aon.occam.impl.jooq.CalendarImpl;
import com.esferalia.aon.occam.impl.jooq.CommercialImpl;
import com.esferalia.aon.occam.impl.jooq.CommissionImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.EmployeeITImpl;
import com.esferalia.aon.occam.impl.jooq.EnterpriseImpl;
import com.esferalia.aon.occam.impl.jooq.ExpedientImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
import com.esferalia.aon.occam.impl.jooq.GroupwareImpl;
import com.esferalia.aon.occam.impl.jooq.ManagementImpl;
import com.esferalia.aon.occam.impl.jooq.MarketplaceImpl;
import com.esferalia.aon.occam.impl.jooq.NewsImpl;
import com.esferalia.aon.occam.impl.jooq.OfficeImpl;
import com.esferalia.aon.occam.impl.jooq.PersonImpl;
import com.esferalia.aon.occam.impl.jooq.Product2Impl;
import com.esferalia.aon.occam.impl.jooq.ProductImpl;
import com.esferalia.aon.occam.impl.jooq.ProjectImpl;
import com.esferalia.aon.occam.impl.jooq.RawdocImpl;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.SalaryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;
import com.esferalia.aon.occam.impl.jooq.StatsImpl;
import com.esferalia.aon.occam.impl.jooq.SystemImpl;
import com.esferalia.aon.occam.impl.jooq.TaskImpl;
import com.esferalia.aon.occam.impl.jooq.URLShortenerImpl;
import com.esferalia.aon.occam.impl.jooq.WarehouseImpl;
import com.esferalia.aon.occam.server.fbatch.FBatchUtils;
import com.esferalia.aon.occam.server.finance.FinanceUtils;
import com.esferalia.aon.occam.server.rawdoc.RawdocUtils;
import com.esferalia.aon.occam.server.registry.RegistryUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.http.AonURIBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;

public class AON {
	
	private AON() {
		throw new IllegalStateException("Utility class");
	}
	
	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}

	private static ICommon getCommon() {
		return new CommonImpl();
	}
	
	private static ICommission getCommission() {
		return new CommissionImpl();
	}

	private static ISalary getSalary() {
		return new SalaryImpl();
	}

	private static ISystem getSystem() {
		return new SystemImpl();
	}

	private static IFinance getFinance() {
		return new FinanceImpl();
	}
	
	private static ICalendar getCalendar() {
		return new CalendarImpl();
	}

	private static IRawdoc getRawdoc() {
		return new RawdocImpl();
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
	
	private static IProduct2 getNewProduct() {
		return new Product2Impl();
	}

	@Deprecated
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
	
	private static IPerson getPerson() {
		return new PersonImpl();
	}

	private static ICommercial getCommercial() {
		return new CommercialImpl();
	}

	private static IMarketplace getMarketplace() {
		return new MarketplaceImpl();
	}

	private static ITask getTask() {
		return new TaskImpl();
	}
	
	private static IExpedient getExpedient() {
		return new ExpedientImpl();
	}
	
	private static IEnterprise getEnterprise() {
		return new EnterpriseImpl();
	}
	
	private static IEmployeeIT getEmployeeIT() {
		return new EmployeeITImpl();
	}
	
	private static INews getNews() {
		return new NewsImpl();
	}

	private static IURLShortener getURLShortener() {
		return new URLShortenerImpl();
	}
	
	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}

	// ********************************************
	// *************************** CONFIGURATION **
	// ********************************************
	public static AonConfiguration getConfiguration(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getConfiguration(ctx, null);
		}
	}
	public static AonConfiguration getConfiguration(Occam occam,Date atDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getConfiguration(ctx, atDate);
		}
	}
	public static AonConfiguration getConfiguration(Occam occam, ConfigParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().getConfiguration(ctx, params);
		}
	}

	public static AonConfiguration getFiscalConfiguration(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().getConfiguration(ctx, new Date(), ConfigBlock.FISCAL);
		}
	}
	public static AonConfiguration getConfiguration(String domainName, int domainId, String login) {
		return getConfiguration(domainName, domainId, login, null);
	}
	
	public static AonConfiguration getConfiguration(String domainName, int domainId, String login,Date atDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getConfiguration(ctx, atDate);
		}
	}
	
	public static AonConfiguration getConfiguration(AONContext ctx) {
		return getConfiguration(ctx, null);  
	}
	
	public static AonConfiguration getConfiguration(AONContext ctx, Date atDate) {
		return getCommon().getConfiguration(ctx, atDate);
	}
	
	public static ApplicationParameter saveApplicationParameter(Occam occam, ApplicationParameter ap) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().saveApplicationParameter(ctx, ap);
		}
	}
	
	// ********************************************
	// ******************************** SECURITY **
	// ********************************************
	
	public static LinkedList<User> getUsersByEmail(String domainName, Integer domainId, String userName, String email) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);			
			return getSecurity().getUsersByEmail(ctx, email);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<User> getUsersByScope(String domainName, Integer domainId, String userName, Integer scope) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);			
			return getSecurity().getUsersByScope(ctx, scope);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Domain> getCompaniesByScope(String domainName, Integer domainId, String userName, Integer scope) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);			
			return getSecurity().getCompaniesByScope(ctx, scope);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static User getUser(String domainName, int domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getSecurity().getUser(ctx, login);
		}
	}
	
	public static User saveUser(Domain domain, String login, User user) {
        return saveUser(domain.getName(), domain.getId(), login, user);
    }
	
    public static User saveUser(String domainName, int domainId, String login, User user) {
        try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
            return getSecurity().save(ctx, user);
        } 
    }
    
    @Deprecated
	public static User save(String domainName, int domainId, String login, User user) {
        try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
            return getSecurity().save(ctx, user);
        } 
    }
	
	public static void saveUserWorkgroups(Domain domain, String login, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			getSecurity().saveUserWorkgroups(ctx, user);
		} 
	}
	
	public static void deleteUserWorkgroup(Domain domain, String login, User user, Workgroup workgroup) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			getSecurity().deleteUserWorkgroup(ctx, user, workgroup);
		} 
	}
	
	public static User insertUser(String domainName, int domainId, String login, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().insertUser(ctx, user);
		}
	}
	
	public static String getUserPassword(String domainName, int domainId, String login, Integer userId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUserPassword(ctx, userId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateUserPassword(String domainName, int domainId, String login, Integer userId, String password) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getSecurity().updateUserPassword(ctx, userId, password);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static UserScope getUserScope(String domainName, Integer domainId, String login, Integer userId, Integer scope ) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUserScope(ctx, userId, scope);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Integer[] getUserScopes(String domainName, int domainId,
			String login, Integer userId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUserScopes(ctx, userId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void insertUserScope(String domainName, Integer domainId, String login, UserScope userScope) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getSecurity().insertUserScope(ctx, userScope);
		}
	}

	
	public static void addUserScope(Occam occam, Integer userId, List<Integer> scopes) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			getSecurity().addUserScope(ctx, userId, scopes);
		} 
	}
	
	public static void deleteUserScope(String domainName, Integer domainId, String login, Integer userId, Integer scope) {
		deleteUserScope(domainName, domainId, login, f -> f.getScopeProperty().eq(scope).and(f.getUserIdProperty().eq(userId)));
	}
	
	public static void deleteUserScope(String domainName, Integer domainId, String login, Integer scope) {
		deleteUserScope(domainName, domainId, login, f -> f.getScopeProperty().eq(scope));
	}
	
	public static void deleteUserScope(String domainName, Integer domainId, String login, UserScopeFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getSecurity().deleteUserScope(ctx, filter);
		} 
	}
	
	public static Scope getScope(String domainName, Integer domainId, String login, Integer scopeId) {
		return getScopeStream(domainName, domainId, login, f -> f.getIdProperty().eq(scopeId))
				.findFirst().orElse(new Scope());
	}
	
	public static Stream<Scope> getScopeStream(String domainName, Integer domainId, String login, ScopeFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getScopeStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Scope> getUserScopeStream(String domainName, Integer domainId, String login, Integer userId, ScopeFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUserScopeStream(ctx, userId, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Scope insertScope(String domainName, Integer domainId, String login, Scope scope) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().insertScope(ctx, scope);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Scope saveScope(String domainName, Integer domainId, String login, Scope scope) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().saveScope(ctx, scope);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Scope saveScopeAndAssign(String domainName, Integer domainId, String login, Scope scope, boolean assignAllUsers, ArrayList<User> selectedUsers) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().saveScopeAndAssign(ctx, scope, assignAllUsers, selectedUsers);
		}
	}
	
	public static boolean canScopeBeDeleted(Occam occam, Integer domainId, Integer scopeId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getSecurity().canScopeBeDeleted(ctx, domainId, scopeId);
		}
	}
	public static void reassignScope(Occam occam, Integer domainId, Integer fromScopeId, Integer toScopeId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			getSecurity().reassignScope(ctx, domainId, fromScopeId, toScopeId);
		}
	}
	public static void reassignAndDeleteScope(Occam occam, Integer domainId, Integer fromScopeId, Integer toScopeId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			getSecurity().reassignAndDeleteScope(ctx, domainId, fromScopeId, toScopeId);
		}
	}
	
	public static Integer deleteScope(String domainName, Integer domainId, String login, Integer scopeId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getSecurity().deleteScope(ctx, scopeId);
		}
	}

	public static List<Scope> getScopeList(ScopeParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getSecurity().getScopeList(ctx, params);
		} 
	}

	public static Integer getScopesCount(ScopeParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getSecurity().getScopesCount(ctx, params);
		} 
	}

	public static List<UserScopeFull> getUserScopeFullList(String domainName, Integer domain, String user, Integer scopeId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getSecurity().getUserScopeFullList(ctx, scopeId);
		} 
	}

	// ********************************************
	// ********************************** COMMON **
	// ********************************************

	// --------------------- DOMAIN

	public static Domain getDomain(Occam occam, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getDomain(ctx, domainId);
		} 
	}
	public static Domain getDomain(String schema, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(schema)){
			return getCommon().getDomain(ctx, domainId);
		} 
	}
	
	public static Domain getDomain(String domainName, Integer domainId,
			String user) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getCommon().getDomain(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateDomainScope(String domainName, Integer domainId, String user, Domain domain) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			getCommon().updateDomainScope(ctx, domain);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateDomainOwner(String schema, String domainName, Integer domainId, String owner) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)) {
			getCommon().updateDomainOwner(ctx, domainName, domainId, owner);
		}
	}
	
	public static void updateDomainScopeValue(String domainName, Integer domainId, String user, Integer scope) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, user)) {
			getCommon().updateDomainScopeValue(ctx, domainName, domainId, scope);
		}
	}
	
	public static Domain getCompanyDomain(String domainName, Integer domainId,
			String user, String document) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getCommon().getCompanyDomain(ctx, document);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Domain> getDriveDomainList(String domainName,
			Integer domainId, String login) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDriveDomainList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static LinkedList<Domain> getDomainList(Occam occam, DomainFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().getDomainList(ctx, filter);
		}
	}
	
	public static LinkedList<Domain> getDomainList(String domainName,Integer domainId, String login, DomainFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getDomainList(ctx, filter);
		}
	}

	public static Domain insertDomain(String domainName, int parentDomain,
			String cifEnterprise, String nameEnterprise,
			List<String> messages) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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

	public static ApplicationParameter fetchApplicationParameter(AONContext ctx, 
			AppParam param) {
		return getCommon().fetchOne(ctx, param);
	}
	
	public static ApplicationParameter fetchApplicationParameter(String domainName, Integer domainId, String login,
			AppParam param) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().fetchOne(ctx, param);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ApplicationParameter getApplicationParameter(Occam occam, AppParam param){
		ApplicationParameter ap = fetchApplicationParameter(occam.getDomainName(), occam.getDomain(), occam.getUser(), param);
		return ap != null ? ap : new ApplicationParameter();
	}
	
	public static ApplicationParameter getApplicationParameter(String domainName, Integer domainId, String login, AppParam param){
		ApplicationParameter ap = fetchApplicationParameter(domainName, domainId, login, param);
		return ap != null ? ap : new ApplicationParameter();
	}
	
	public static ApplicationParameter insertApplicationParameter(String domainName, Integer domainId, String login, AppParam param, String value){
		return insertApplicationParameter(domainName, domainId, login, param.getValue(), value);
	}
	
	public static ApplicationParameter insertApplicationParameter(String domainName, Integer domainId, String login, String param, String value){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertApplicationParameter(ctx, param, value);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ApplicationParameter insertApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter) {
		return getCommon().insertApplicationParameter(ctx, applicationParameter);
	}

	public static ApplicationParameter insertApplicationParameter(String domainName, Integer domainId, String login, ApplicationParameter applicationParameter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return insertApplicationParameter(ctx, applicationParameter);
		}
	}
	
	public static ApplicationParameter updateApplicationParameter(String domainName, Integer domainId, String login, ApplicationParameter applicationParameter, ApplicationParameterFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().updateApplicationParameter(ctx, applicationParameter, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteApplicationParameter(String domainName, Integer domainId, String login, ApplicationParameterFilter filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getPersonStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Person> getPersonList(String domainName, Integer domainId, String login, PersonFilter filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getPersonStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	// --------------------------------- ENTERPRISE
	public static Enterprise getEnterprise(String domainName, int domain,
			String login, int id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getCommon().getEnterprise(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static EnterpriseActivity getEnterpriseActivity(String domainName, Integer domainId, String login, Integer id) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getEnterpriseActivity(ctx, id);
		}
	}
	
	public static Stream<EnterpriseActivity> getEnterpriseActivities(String domainName, Integer domainId, String login) { //, EnterpriseActivityFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getEnterpriseActivities(ctx, domainId, new Date());
		}
	}
	
	public static Stream<Company> getUserCompanyStream(String domainName, Integer domainId, String login, Integer[] scopes){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getUserCompanyStream(ctx, scopes);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Company> getUserCompanyStream(String domainName, Integer domainId, String login, Integer[] scopes, CompanyFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getUserCompanyStream(ctx, scopes, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Company> getCompanyStream(String domainName, Integer domainId, String login, CompanyFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCompanyStream(ctx, filter);
		}
	}
	public static Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter){
		return getRegistry().getCompanyStream(ctx, filter);
	}
	
	public static Stream<Company> getCompanyStream(String domainName, Integer domainId, String login, CompanyFilter filter, Integer page, Integer perPage){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getCompanyStream(ctx, filter, page, perPage);
		}
	}

	public static CompanyFull getCompanyFull(Occam occam){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRegistry().getCompanyFull(ctx, occam.getDomain());
		}

	}
	
	public static CompanyFull getCompanyFull(String domainName, Integer domainId, String login){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getCompanyFull(ctx, domainId);
		}

	}
	
	public static CompanyFull getCompanyFull(String domainName, Integer domainId, String login, Integer officeDomain){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getCompanyFull(ctx, officeDomain);
		}
	}
		
	public static Company getCompany(Occam occam, CompanyFilter filter){
		return getCompany(occam.getDomainName(), occam.getDomain(), occam.getUser(), filter);
	}
	public static Company getCompany(Domain domain, User user, CompanyFilter filter){
		return getCompany(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Company getCompany(String domainName, Integer domainId, String login, CompanyFilter filter){
		return getCompanyStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Company());
	}
	public static Company getCompany(AONContext ctx, CompanyFilter filter){
		return getCompanyStream(ctx, filter)
				.findFirst().orElse(new Company());
	}
	
	public static Company getCompanyForDomain(Occam occam) {
		return getCompany(occam.getDomainName(), occam.getDomain(), occam.getUser(), f -> f.getDomainProperty().eq(occam.getDomain()));
	}
	
	public static Company getCompanyForDomain(String domainName, int domainId, String login) {
		return getCompany(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId));
	}

	public static Company saveCompany(Domain domain, User user, Company company) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getRegistry().saveCompany(ctx, company);
		}
	}
	
	public static LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain, String login, int enterprise) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getCommon().getCompanyBanks(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------------------------ RDIRSTAFF	
	
	public static Stream<RDirStaff> getRDirStaffStream(String domainName, Integer domainId, String login, RDirStaffFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRDirStaffStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static RDirStaff getRDirStaff(String domainName, Integer domainId, String login, RDirStaffFilter filter){
		return getRDirStaffStream(domainName, domainId, login, filter)
				.findFirst().orElse(new RDirStaff());
	}
	
	public static RDirStaff insertRDirStaff(String domainName, Integer domainId, String login, RDirStaff rdirstaff) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().insertRDirStaff(ctx, rdirstaff);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ------------------------------------ WORKPLACE

	public static Optional<Workplace> getWorkplace(Occam occam, Integer domainId, Integer workplaceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getWorkplace(ctx, domainId, workplaceId);
		}
	}
	
	/**
	 * @deprecated use getWorkplace(Occam occam, Integer domainId, Integer workplaceId)
	 */
	@Deprecated
	public static Workplace getWorkplace(String domainName, Integer domainId,
			String login, WorkplaceFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getWorkplace(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Workplace> getWorkplaces(Occam occam, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getWorkplaces(ctx, domainId);
		}
	}
	
	/**
	 * @deprecated use getWorkplaces(Occam occam, Integer domainId)
	 */
	@Deprecated
	public static LinkedList<Workplace> getWorkplaceList(String domainName,
			Integer domainId, String login, WorkplaceFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getWorkplaceList(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}	

	public static Workplace saveWorkplace(Domain domain, User user, Workplace workplace) {
		return saveWorkplace(domain.getName(), domain.getId(), user.getLogin(), workplace);
	}
	
	public static Workplace saveWorkplace(Domain domain, String login, Workplace workplace) {
		return saveWorkplace(domain.getName(), domain.getId(), login, workplace);
	}
	
	public static Workplace saveWorkplace(String domainName, Integer domainId, String login, Workplace workplace) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().saveWorkplace(ctx, workplace);
		} 
	}
	
	// ---------- 	PAYROLL WORKPLACE
	
	public static PayrollWorkplace savePayrollWorkplace(Domain domain, User user, PayrollWorkplace payrollWorkplace) {
		return savePayrollWorkplace(domain.getName(), domain.getId(), user.getLogin(), payrollWorkplace);
	}
	
	public static PayrollWorkplace savePayrollWorkplace(Domain domain, String login, PayrollWorkplace payrollWorkplace) {
		return savePayrollWorkplace(domain.getName(), domain.getId(), login, payrollWorkplace);
	}
	
	public static PayrollWorkplace savePayrollWorkplace(String domainName, Integer domainId, String login, PayrollWorkplace payrollWorkplace) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().savePayrollWorkplace(ctx, payrollWorkplace);
		} 
	}
	
	public static PayrollWorkplace getPayrollWorkpalce(String domainName, Integer domainId, String login, PayrollWorkplaceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getPayrollWorkpalce(ctx, filter);
		} 
	}
	
	// --------------------- SIGNATURE

	public static Signature getSignature(String domainName, Integer domainId, String login, Integer signatureId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getSignature(ctx, signatureId);
		}
	}
	
	public static Signature getSignature(String domainName, Integer domainId, String login, SignatureFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getSignature(ctx, filter);
		}
	}
	
	public static LinkedList<Signature> getSignatureList(String domainName, Integer domainId, String login, SignatureFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getSignatures(ctx, filter);
		}
	}
	
	public static LinkedList<Signature> getSignatures(String domainName, Integer domain, String login, SignatureFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			return getCommon().getSignatures(ctx, filter);
		}
	}

	public static void deleteSignature(String domainName, Integer domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			getCommon().deleteSignature(ctx, id);
		}
	}

	public static Signature saveSignature(String domainName, Integer domain, String login, Signature signature) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			return getCommon().saveSignature(ctx, signature);
		}
	}
	
	// ********************************************
	// *************************** MAIL ACCOUNTS **
	// ********************************************

	public static LinkedList<MailAccount> getMailAccounts(String domainName, Integer domain, String login, MailAccountFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			return getCommon().getMailAccounts(ctx, filter);
		}
	}

	public static void deleteMailAccount(String domainName, Integer domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			getCommon().deleteMailAccount(ctx, id);
		}
	}

	public static MailAccount saveMailAccount(String domainName, Integer domain, String login, MailAccount mailAccount) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			return getCommon().saveMailAccount(ctx, mailAccount);
		}
	}
	
	public static MailAccount getMailAccount(String domainName, Integer domainId, String login, Integer signatureId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getMailAccount(ctx, signatureId);
		}
	}
	
	public static MailAccount getMailAccount(String domainName, Integer domainId, String login, MailAccountFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getMailAccount(ctx, filter);
		}
	}

	// ********************************************
	// ********************************* PRODUCT **
	// ********************************************

	// ------------------------------------ PRODUCT

	@Deprecated
	public static OldProduct getProduct(String domainName, Integer domainId, String login, Integer productId) {
		return getProduct(domainName, domainId, login, f -> f.getIdProperty().eq(productId));
	}

	@Deprecated
	public static OldProduct getProduct(String domainName, Integer domainId, String login, ProductFilter filter) {
		return getProductStream(domainName, domainId, login, filter).findFirst().orElse(new OldProduct());
	}

	@Deprecated
	public static LinkedList<OldProduct> getProductList(String domainName, Integer domainId, String login, ProductFilter filter){
		return getProductStream(domainName, domainId, login, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Deprecated
	public static Stream<OldProduct> getProductStream(String domainName, Integer domainId, String login, ProductFilter filter){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getProductStream(ctx, filter);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	@Deprecated
	public static OldProduct insertProduct(String domainName, Integer domainId, String login, OldProduct p) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().insertProduct(ctx, p);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	@Deprecated
	public static void insert(AONContext ctx,OldProduct p) {
		getProduct().insert(ctx, p);
	}

	@Deprecated
	public static LinkedList<OldProduct> insert(AONContext ctx, 
			Stream<OldProduct> ps) {
		return getProduct().insert(ctx, ps);
	}

	@Deprecated
	public static void update(AONContext ctx, OldProduct p) {
		getProduct().update(ctx, p);
	}

	@Deprecated
	public static void delete(AONContext ctx, OldProduct p) {
		getProduct().delete(ctx, p);
	}

	@Deprecated
	public static void delete(AONContext ctx, Stream<OldProduct> ps) {
		getProduct().delete(ctx, ps);
	}

	// ------------------------------------ PRODUCT_TAG
	
	public static Stream<ProductTag> getProductTagStream(String domainName, Integer domainId, String login, ProductTagFilter filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
	
	public static void insertProductTag(Domain domain, String login, Stream<ProductTag> pts) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			getProduct().insertProductTag(ctx, pts);
		}
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
	
	public static void deleteProductTag(Domain domain, String login, Stream<ProductTag> pts) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			getProduct().deleteProductTag(ctx, pts);
		}
	}

	// ------------------------------------ NEW PRODUCT
	
	public static Product getProduct(Domain domain, String login, ProductFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getProduct(ctx, filter);
		}
	}
	
	public static ProductBooking getProductBooking(Domain domain, String login, ProductFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getProductBooking(ctx, filter);
		}
	}
	
	public static Stream<Product> getProductStream(Domain domain, String login, ProductFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getProductStream(ctx, filter);
		}
	}
	
	public static LinkedList<Product> getProductList(Domain domain, String login, ProductFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getProductList(ctx, filter);
		}
	}
	
	public static LinkedList<Product> getProductList(Domain domain, String login, ProductParams params) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getProductList(ctx, params);
		}
	}
	
	public static LinkedList<ProductBooking> getProductBookingList(Domain domain, String login, ProductParams params) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getProductBookingList(ctx, params);
		}
	}
	
	public static Product saveProduct(Domain domain, String login, Product product) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().saveProduct(ctx, product);
		}
	}
	
	public static ProductBooking saveProductBooking(Domain domain, String login, ProductBooking product) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().saveProductBooking(ctx, product);
		}
	}
	
	public static void deleteProduct(Domain domain, String login, Integer productId) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			getNewProduct().deleteProduct(ctx, productId);
		}
	}
	
	public static Product createProduct(Domain domain, String login, Product product, List<ProductTag> productTags, Item item) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().createProduct(ctx, product, productTags, item);
		}
	}
	
	public static ProductBooking createProductBooking(Domain domain, String login, ProductBooking product, List<ProductTag> productTags, Item item) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().createProductBooking(ctx, product, productTags, item);
		}
	}
	
	// ------------------------------------ NEW ITEM

	public static Item getItem(Domain domain, String login, Integer id, Options...options) {
		return getItem(domain, login, f -> f.getIdProperty().eq(id), options);
	}
	
	public static Item getItem(Domain domain, String login, ItemFilter filter, Options...options) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getItem(ctx, filter, options);
		}
	}
	
	public static Stream<Item> getItemStream(Domain domain, String login, ItemFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getItemStream(ctx, filter);
		}
	}
	
	public static LinkedList<Item> getItemList(Domain domain, String login, ItemFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().getItemList(ctx, filter);
		}
	}
	
	public static Item saveItem(Domain domain, String login, Item item) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			return getNewProduct().saveItem(ctx, item);
		}
	}
	
	public static RegistryItem[] saveRItem(Domain domain, User user, RegistryItem ...ritems) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, user)){
			return getNewProduct().saveRItem(ctx, ritems);
		}
	}
	
	public static void deleteRItem(Domain domain, User user, RegistryItemFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, user)){
			getNewProduct().deleteRItem(ctx, filter);
		}
	}
	
	public static void updateRItemQuantity(Domain domain, User user, String quantity, RegistryItemFilter filter) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, user)){
			getNewProduct().updateRItemQuantity(ctx, quantity, filter);
		}
	}
	
	public static void deleteItem(Domain domain, String login, Integer itemId) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, login)){
			getNewProduct().deleteItem(ctx, itemId);
		}
	}
	
	
	// ------------------------------------ ITEM
	
	@Deprecated
	public static LinkedList<OldItem> getItemList(String domainName, Integer domainId, String login, ItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	@Deprecated
	public static LinkedList<OldItem> getFullItemList(String domainName, Integer domainId, String login, ItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getFullItemStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	@Deprecated
	public static OldItem getItem(String domainName, Integer domainId, String login, Integer itemId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, f -> f.getIdProperty().eq(itemId)
					.perPage(1)).findFirst().orElse(new OldItem());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	@Deprecated
	public static OldItem getItem(String domainName, Integer domainId, String login, ItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, filter)
					.findFirst().orElse(new OldItem());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	@Deprecated
	public static Optional<OldItem> getItemOptional(String domainName, Integer domainId, String login, ItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().getItemStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<ItemComposition> getItemCompositionStream(Domain domain, String login, ItemCompositionFilter filter) {
		return getItemCompositionStream(domain.getName(), domain.getId(), login, filter);
	}	
	
	public static Stream<ItemComposition> getItemCompositionStream(String domainName, Integer domainId, String login, ItemCompositionFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getProduct().getItemCompositionStream(ctx, filter);
		}
	}
	
	public static List<ItemComposition> getItemCompositionList(String domainName, Integer domainId, String login, Integer itemId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getProduct().getItemCompositionList(ctx, f -> f.getItemProperty().eq(itemId));
		}
	}

	public static void deletItemComposition(Domain domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getProduct().deletItemComposition(ctx, id);
		}
	}
	
	public static void deletItemCompositions(Domain domain, String user, List<Integer> ids) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getProduct().deletItemCompositions(ctx, ids);
		}
	}

	public static ItemComposition saveItemComposition(Domain domain, String user, ItemComposition itemComposition) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getProduct().saveItemComposition(ctx, itemComposition);
		}
	}

	public static List<ItemComposition> saveItemCompositions(Domain domain, String user, List<ItemComposition> itemCompositions) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getProduct().saveItemCompositions(ctx, itemCompositions);
		}
	}
	
	public static ItemTariff saveItemTariff(Domain domain, String user, ItemTariff itemTariff) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().saveItemTariff(ctx, itemTariff);
		}
	}
	

	@Deprecated
	public static OldItem insertItem(String domainName, Integer domainId, String login, OldItem i) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProduct().insertItem(ctx, i);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	@Deprecated
	public static void deleteItem(String domainName, Integer domainId, String login, OldItem item) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			deleteItem(ctx, item);
		} finally {
			if (ctx != null)
				ctx.close();
		}	}

	@Deprecated
	public static void insertItem(AONContext ctx, OldItem i) {
		getProduct().insertItem(ctx, i);
	}

	@Deprecated
	public static void insertItemWithId(AONContext ctx, OldItem i) {
		getProduct().insertItemWithId(ctx, i);
	}

	@Deprecated
	public static void insertItem(AONContext ctx, Stream<OldItem> is) {
		getProduct().insertItem(ctx, is);
	}

	@Deprecated
	public static void insertItemWithId(AONContext ctx, Stream<OldItem> is) {
		getProduct().insertItemWithId(ctx, is);
	}

	@Deprecated
	public static void updateItem(AONContext ctx, OldItem i) {
		getProduct().updateItem(ctx, i);
	}
	
	@Deprecated
	public static void deleteItem(AONContext ctx, OldItem i) {
		getProduct().deleteItem(ctx, i);
	}

	@Deprecated
	public static void deleteItem(AONContext ctx, Stream<OldItem> is) {
		getProduct().deleteItem(ctx, is);
	}
	
	public static void updateAllTargetItem(Domain domain, User user, InvoiceFilter filter, boolean disable) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(domain, user)){
			getNewProduct().updateAllTargetItem(ctx, filter, disable);
		}
	}
	
	// ------------------------------------ ITEM ADD INFO
	
	public static Optional<ItemAddInfo> getItemAddInfo(String domainName, Integer domainId, String login, ItemAddInfoFilter filter){
		return getItemAddInfoStream(domainName, domainId, login, filter).findFirst();
	}
	
	public static Stream<ItemAddInfo> getItemAddInfoStream(String domainName, Integer domainId, String login, ItemAddInfoFilter filter){
		try (CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getNewProduct().getItemAddInfoStream(ctx, filter);
		}
	}
	
	public static void saveItemAddInfo(String domainName, Integer domainId, String login, ItemAddInfo itemAddInfo){
		try (CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			getNewProduct().saveItemAddInfo(ctx, itemAddInfo);
		}
	}
	
	public static void deleteItemAddInfo(String domainName, Integer domainId, String login, Integer id){
		try (CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			getNewProduct().deleteItemAddInfo(ctx, id);
		}
	}
	
	public static void insertItemAddInfo(String domainName, Integer domainId, String login, ItemAddInfo i) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getProduct().insertItemAddInfo(ctx, i);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateItemAddInfo(String domainName, Integer domainId, String login, ItemAddInfo i) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getProduct().updateItemAddInfo(ctx, i);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// --------------------------------- RADDINFO
	
	public static Optional<RegistryAddInfo> getRegistryAddInfo(String domainName, Integer domainId, String login, RegistryAddInfoFilter filter){
		return getRegistryAddInfoStream(domainName, domainId, login, filter).findFirst();
	}
	
	public static Stream<RegistryAddInfo> getRegistryAddInfoStream(String domainName, Integer domainId, String login, RegistryAddInfoFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistryAddInfoStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static List<DomainSigAddInfo> getDomainSigAddInfo(String domainName, Integer domainId, String login, Integer registry){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getDomainSigAddInfo(ctx, registry);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void insertRegistryAddInfo(String domainName, Integer domainId, String login, RegistryAddInfo raddinfo) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getRegistry().insertRegistryAddInfo(ctx, raddinfo);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void updateRegistryAddInfo(String domainName, Integer domainId, String login, RegistryAddInfo raddinfo) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getRegistry().updateRegistryAddInfo(ctx, raddinfo);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteRegistryAddInfo(Domain domain, String login, Integer raddinfoId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)){
			getRegistry().deleteRegistryAddInfo(ctx, raddinfoId);
		}
	}
	
	public static void deleteRegistryAddInfo(String domainName, Integer domainId, String login, RegistryAddInfoFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getRegistry().deleteRegistryAddInfo(ctx, filter);
		}
	}
	
	public static List<String> getRAddInfoAviableAttributes(String domainName, Integer domain, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)){
			return getRegistry().getRAddInfoAviableAttributes(ctx, f -> f.getDomainProperty().eq(domain));
		}
	}
	
	public static RegistryAddInfo save(Domain domain, User user, RegistryAddInfo registryAddInfo) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getRegistry().saveRegistryAddInfo(ctx, registryAddInfo);
		}
	}
	
	// ------------------------------------ BRAND
	
	public static Brand getBrand(String domainName, Integer domainId, String login, Integer id){
		return getBrand(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Brand getBrand(String domainName, Integer domainId, String login, String name){
		return getBrand(domainName, domainId, login, f -> f.getNameProperty().eq(name)
				.and(f.getDomainProperty().eq(domainId)));
	}
	
	public static Brand getBrand(String domainName, Integer domainId, String login, BrandFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProduct().getBrand(ctx, filter);
		}
	}
	
	public static Stream<Brand> getBrandStream(String domainName, Integer domainId, String login, BrandFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProduct().getBrandStream(ctx, filter);
		}
	}
	
	public static Brand saveBrand(String domainName, Integer domainId, String login, Brand brand) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProduct().saveBrand(ctx, brand);
		}
	}

	public static void deleteBrand(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getProduct().deleteBrand(ctx, id);
		}
	}

	// ------------------------------------ PRODUCT CATEGORY
	
	public static Stream<ProductCategory> getProductCategoryStream(String domainName, Integer domainId, String login, ProductCategoryFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProduct().getProductCategoryStream(ctx, filter);
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
	
	public static ProductCategory saveProductCategory(String domainName, Integer domainId, String login, ProductCategory productCategory) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProduct().saveProductCategory(ctx, productCategory);
		}
	}
	
	public static void deleteProductCategory(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getProduct().deleteProductCategory(ctx, id);
		}
	}

	// ********************************************
	// ********************************* FINANCE **
	// ********************************************
	public static Stream<Invoice> getInvoiceHeaders(Occam occam, InvoiceFilter filter, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getInvoiceHeaders(ctx, filter, offset, limit);
		}
	}
	public static Stream<Invoice> getInvoiceHeaders(AONContext ctx, InvoiceFilter filter, int offset, int limit) {
		return getFinance().getInvoiceHeaders(ctx, filter, offset, limit);
	}
	
	public static Stream<Invoice> getInvoiceHeaders(Occam occam, AccountingReportParams params, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getInvoiceHeaders(ctx, params, offset, limit);
		}
	}
	public static Stream<Invoice> getInvoiceHeaders(AONContext ctx, AccountingReportParams params, int offset, int limit) {
		return getFinance().getInvoiceHeaders(ctx, params, offset, limit);
	}

	public static Stream<Invoice> getInvoiceStream(Occam occam, InvoiceFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceStream(ctx, filter);
		}
	}
	
	public static Stream<Invoice> getInvoiceStream(String domainName, Integer domainId, String login, InvoiceFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getInvoiceStream(ctx, filter);
		}
	}
	
	public static InvoiceCounter getInvoiceCounter(Domain domain, User user) {	
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().getInvoiceCounter(ctx);
		}
	}
	
	public static Invoice acceptInvoice(Occam occam, Invoice invoice){
		return acceptInvoice( occam.getDomainName(), occam.getDomain(), occam.getUser(), invoice, null);
	}

	public static Invoice acceptInvoice(Occam occam, Invoice invoice, Integer rawdocId){
		return acceptInvoice( occam.getDomainName(), occam.getDomain(), occam.getUser(), invoice, rawdocId);
	}

	public static Invoice acceptInvoice(String domainName, Integer domainId, String login, Invoice invoice, Integer rawdocId){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return acceptInvoice(ctx, invoice, rawdocId);
		}
	}
	public static Invoice acceptInvoice(AONContext ctx, Invoice invoice){
		return getFinance().acceptInvoice(ctx, invoice, null);
	}
	public static Invoice acceptInvoice(AONContext ctx, Invoice invoice, Integer rawdocId){
		return getFinance().acceptInvoice(ctx, invoice, rawdocId);
	}

	public static Invoice insertInvoice(Occam occam, Invoice invoice){
		return insertInvoice( occam.getDomainName(), occam.getDomain(), occam.getUser(), invoice);
	}
	
	public static Invoice insertInvoice(String domainName, Integer domainId, String login, Invoice invoice){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().insertInvoice(ctx, invoice);
		}
	}
	
	public static Invoice updateInvoice(String domainName, Integer domainId, String login, Invoice invoice, boolean only){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().updateInvoice(ctx, invoice, only);
		}
	}
	
	public static Invoice updateInvoice(String domainName, Integer domainId, String login, Invoice invoice){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().updateInvoice(ctx, invoice);
		}
	}
	
	public static void deleteInvoice(Occam occam, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getFinance().deleteInvoice(ctx, invoiceId);
		}
	}
	
	public static void deleteInvoice(String domainName, Integer domainId, String login, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getFinance().deleteInvoice(ctx, invoiceId);
		}
	}
	
	@Deprecated
	public static Stream<Invoice> getSiiInvoiceStream(String domainName, Integer domainId, String login, InvoiceFilter filter
			, Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getSiiInvoiceStream(ctx, filter, pending, aceptada, aceptadaErrores, incorrecta, anulada, sii)
					.filter(s -> s.getNumber() >= 0); // No facturas proforma (factura proforma es la que su numero de factura es menor que cero) 
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Invoice> getInvoiceList(String domainName, Integer domainId, String login, InvoiceFilter filter){
		return getInvoiceStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<Invoice> getFullInvoiceList(String domainName, int domainId, String login, List<Integer> ids) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getFullInvoiceList(ctx, ids);
		}
	}
	
	public static Invoice getInvoice(Occam occam, Integer invoiceId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getFullInvoice(ctx, invoiceId);
		}
	}

	public static Invoice getInvoice(Occam occam, InvoiceFilter filter){
		return getInvoiceStream(occam, filter)
			.findFirst().orElse(new Invoice());
	}

	 public static Invoice getInvoice(String domainName, Integer domainId, String login, InvoiceFilter filter){
		return getInvoiceStream(domainName, domainId, login, filter)
			.findFirst().orElse(new Invoice());
	}
	
	public static Invoice getInvoice(String domainName, Integer domainId, String login, Integer id){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getInvoice(ctx, id);
		}
	}
	
	public static Optional<Item> getLastItem( AONContext ctx, Integer registry) {
		return getFinance().getLastItem(ctx, registry);
	}
	
	public static Invoice getLastSaleInvoice(Occam occam, String serie){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getLastSaleInvoice(ctx, serie);
		}
	}
	
	public static Invoice getLastSaleInvoice(Domain domain, User user, String serie){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().getLastSaleInvoice(ctx, serie);
		}
	}

	public static Invoice getLastSaleInvoice(String domainName, Integer domainId, String login, String serie){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getLastSaleInvoice(ctx, serie);
		}
	}

	public static Stream<InvoiceDetail> getInvoiceDetails(String domainName, Integer domainId, String login, InvoiceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getInvoiceDetails(ctx, filter);
		}
	}
	
	public static Stream<InvoiceDetailExtended> getInvoiceDetailsExtended(Occam occam, InvoiceFilter filter) {
		final CloseableAONContext ctx = AONContext.getAONContext(occam);
		return getFinance().getInvoiceDetailsExtended(ctx, filter,() -> {if (ctx != null) ctx.close();});
	}
	
	public static Stream<InvoiceDetail> getInvoiceDetailStream(String domainName, Integer domainId, String login,
			InvoiceFilter filter, ProductFilter pFilter, ItemFilter iFilter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoiceMovements(ctx, filter, pFilter, iFilter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<InvoiceTax> getInvoiceTaxStream(String domainName, Integer domainId, String login,
			Integer invoiceId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getInvoiceTaxStream(ctx, invoiceId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static InvoiceDetail getLastInvoiceDetail(String domainName,
			Integer domainId, String user, OldItem item, Integer workplaceId,
			Integer warehouseId) {
		CloseableAONContext ctx = null;
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
			Integer domainId, String user, OldItem item, Integer workplaceId,
			Integer warehouseId, Date date) {
		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			String months, Integer workplaceId, Integer warehouseId) {
		Calendar calendar = Calendar.getInstance();
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);

		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			String months, Integer workplaceId, Integer warehouseId,
			Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);

		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			Integer workplaceId, Integer warehouseId) {
		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			Integer workplaceId, Integer warehouseId, Date date) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getInvoiceDetailListUntilDate(ctx, item,
					workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void rectifyInvoice(String domainName, Integer domainId, String login, Integer rectifierInvoice, Integer rectifiedInvoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getFinance().rectifyInvoice(ctx, rectifierInvoice, rectifiedInvoice);
		}
	}
	
	public static InvoicingGroup save(String domainName, Integer domainId, String login, InvoicingGroup invoicingGroup) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().save(ctx, invoicingGroup);
		}
	}

	public static Integer getInvoiceNextNumber(String domainName, Integer domainId, String login, Byte[] types, String series) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getInvoiceNextNumber(ctx, types,series);
		}
	}
	public static Integer getInvoiceNextNumber(Occam occam, Byte[] types, String series) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getInvoiceNextNumber(ctx, types,series);
		}
	}
	public static Integer getInvoiceNextNumber(AONContext ctx, Byte[] types, String series) {
			return getFinance().getInvoiceNextNumber(ctx, types,series);
	}
	
	public static Integer getInvoiceMinNumber(String domainName, Integer domainId, String login, InvoiceType type, String series) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getInvoiceMinNumber(ctx, type, series);
		}
	}
	
	public static Stream<InvoiceDetail> getBoughtProductStream(String domainName,
			Integer domainId, String login, InvoiceFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getBoughtProductStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Finance> getFinances(String domainName, int domain, String user, FinanceParams params,int offset, int limit) {
		return getFinancesStream(domainName, domain, user, p -> FinanceUtils.getFilter(p, params),offset,limit)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	public static Stream<Finance> getFinancesStream(String domainName, int domain, String user, FinanceParams params,int offset, int limit) {
		return getFinancesStream(domainName, domain, user, p -> FinanceUtils.getFilter(p, params),offset,limit);
	}
	
	public static Stream<Finance> getFinancesStream(String domainName, int domain, String user, FinanceFilter filter,int offset, int limit) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getFinance().getFinanceStream(ctx, filter,offset,limit);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Finance> getFinanceStream(String domainName, Integer domainId, String login, FinanceFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getFinanceStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Finance insertFinance(String domainName, Integer domainId, String login, Finance finance) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().insertFinance(ctx, finance);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Finance saveFinance(String domainName, Integer domainId, String login, Finance finance) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().saveFinance(ctx, finance);
		} 
	}
	
	public static LinkedList<Finance> getFinanceList(String domainName,
			Integer domainId, String login, FinanceFilter filter) {
		return getFinanceStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Finance> getSiiFinanceStream(String domainName, Integer domainId, String login, FinanceFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFinance().getSiiFinanceStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<Finance> getSiiFinanceList(String domainName,
			Integer domainId, String login, FinanceFilter filter) {
		return getSiiFinanceStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	// ********************************************
	// ****************************** MANAGEMENT **
	// ********************************************
	public static Offer getOffer(String domainName, Integer domainId, String login, OfferFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getOffer(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Offer> getOfferStream(String domainName, Integer domainId, String login, OfferFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getOfferStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<OfferDetail> getOfferDetails(String domainName,
			Integer domainId, String login, OfferFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getOfferDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Offer insertOffer(String domainName, Integer domainId, String login, Offer offer) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertOffer(ctx, offer);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Offer updateOffer(String domainName, Integer domainId, String login, Offer offer) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().updateOffer(ctx, offer);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static OfferDetail insertOfferDetail(String domainName, Integer domainId, String login, OfferDetail offerDetail) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertOfferDetail(ctx, offerDetail);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ------------------ SALES

	// ----- GET SALES STREAM
	
	public static Stream<Sales> getSalesStream(Domain domain, User user, SalesFilter filter, Options... options) {
		return getSalesStream(domain.getName(), domain.getId(),  user.getLogin(), filter, options);
	}
	
	public static Stream<Sales> getSalesStream(Domain domain, String login, SalesFilter filter, Options... options) {
		return getSalesStream(domain.getName(), domain.getId(),  login, filter, options);
	}
	
	public static Stream<Sales> getSalesStream(String domainName, Integer domainId, String login, SalesFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().getSalesStream(ctx, filter, options);
		}
	}
	
	// ----- GET SALES
	
	public static Sales getSales(Domain domain, User user, SalesFilter filter, Options... options) {
		return getSales(domain.getName(), domain.getId(),  user.getLogin(), filter, options);
	}
	
	public static Sales getSales(Domain domain, String login, SalesFilter filter, Options... options) {
		return getSales(domain.getName(), domain.getId(),  login, filter, options);
	}
	
	public static Sales getSales(String domainName, Integer domainId, String login, SalesFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().getSales(ctx, filter, options);
		}
	}
	
	public static List<Sales> getSales(SalesParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())){
			return getManagement().getSales(ctx, params);
		}
	}

	// ----- SAVE SALES
	
	public static Sales saveSales(Domain domain, User user, Sales sales) {
		return saveSales(domain.getName(), domain.getId(), user.getLogin(), sales);
	}

	public static Sales saveSales(Domain domain, String login, Sales sales) {
		return saveSales(domain.getName(), domain.getId(), login, sales);	
	}
	
	public static Sales saveSales(String domainName, Integer domainId, String login, Sales sales) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getManagement().saveSales(ctx, sales);
		} 
	}
	
	// ----- DELETE SALES
	
	public static void deleteSales(Domain domain, User user, Integer salesId) {
		deleteSales(domain.getName(), domain.getId(), user.getLogin(), salesId);
	}

	public static void deleteSales(Domain domain, String login, Integer salesId) {
		deleteSales(domain.getName(), domain.getId(), login, salesId);	
	}
	
	public static void deleteSales(String domainName, Integer domainId, String login, Integer salesId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getManagement().deleteSales(ctx, salesId);
		} 
	}

	// ------------------ SALES DETAIL

	public static Stream<SalesDetail> getSalesDetailStream(String domainName, Integer domainId, String login, SalesDetailFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getManagement().getSalesDetailStream(ctx, filter);
		}
	}
	
	public static Stream<SalesDetail> getSalesDetails(String domainName, Integer domainId, String login, SalesFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getManagement().getSalesDetails(ctx, filter);
		}
	}
	
	public static void updateSalesDetail(String domainName, Integer domainId, String login, SalesDetail salesDetail) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getManagement().updateSalesDetail(ctx, salesDetail);
		}
	}
	
	// ------------------ PURCHASE
	
	public static Stream<Purchase> getPurchaseStream(String domainName, Integer domainId, String login, PurchaseFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getManagement().getPurchaseStream(ctx, filter);
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertPurchase(ctx, purchase);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Purchase updatePurchase(String domainName, Integer domainId, String login, Purchase purchase, PurchaseFilter filter) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().insertPurchaseDetail(ctx, purchaseDetail);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static PurchaseDetail updatePurchaseDetail(String domainName, Integer domainId, String login, PurchaseDetail purchaseDetail, PurchaseDetailFilter filter) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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

	// ----- GET DELIVERY STREAM
	
	public static Stream<Delivery> getDeliveryStream(Domain domain, User user, DeliveryFilter filter, Options... options) {
		return getDeliveryStream(domain.getName(),  domain.getId(), user.getLogin(), filter, options);
	}
	
	public static Stream<Delivery> getDeliveryStream(Domain domain, String login, DeliveryFilter filter, Options... options) {
		return getDeliveryStream(domain.getName(),  domain.getId(),  login, filter, options);
	}
	
	public static Stream<Delivery> getDeliveryStream(String domainName, Integer domainId, String login, DeliveryFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().getDeliveryStream(ctx, filter, options);
		}
	}

	// ----- GET DELIVERY
	
	public static Delivery getDelivery(Domain domain, User user, DeliveryFilter filter, Options...options) {
		return getDelivery(domain.getName(), domain.getId(), user.getLogin(), filter, options);
	}
	
	public static Delivery getDelivery(Domain domain, String login, DeliveryFilter filter, Options...options) {
		return getDelivery(domain.getName(), domain.getId(), login, filter, options);
	}
	
	public static Delivery getDelivery(String domainName, Integer domainId, String login, DeliveryFilter filter, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().getDelivery(ctx, filter, options);
		}
	}

	public static Delivery getDeliveryByPackage(Occam occam, Integer itemPackageId, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().getDeliveryByPackage(ctx, itemPackageId, options);
		}
	}

	// ----- SAVE DELIVERY
	
	public static Delivery saveDelivery(Domain domain, User user, Delivery delivery) {
		return saveDelivery(domain.getName(), domain.getId(),  user.getLogin(), delivery);
	}
	
	public static Delivery saveDelivery(Domain domain, String login, Delivery delivery) {
		return saveDelivery(domain.getName(), domain.getId(), login, delivery);
	}
	
	public static Delivery saveDelivery(String domainName, Integer domainId, String login, Delivery delivery) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().saveDelivery(ctx, delivery);
		}
	}
	
	/**
	 * @deprecated  Replaced by AON.saveDelivery
	 */
	@Deprecated(forRemoval = true )
	public static Delivery insertDelivery(String domainName, Integer domainId, String login, Delivery delivery) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().insertDelivery(ctx, delivery);
		}
	}
	
	/**
	 * @deprecated  Replaced by AON.saveDelivery
	 */
	@Deprecated(forRemoval = true )
	public static Delivery updateDelivery(String domainName, Integer domainId, String login, Delivery delivery, DeliveryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().updateDelivery(ctx, delivery, filter);
		}
	}
	
	/**
	 * @deprecated  Replaced by AON.saveDelivery
	 */
	@Deprecated(forRemoval = true )
	public static Delivery updateDelivery(String domainName, Integer domainId, String login, Delivery delivery) {
		return updateDelivery(domainName, domainId, login, delivery, f -> f.getIdProperty().eq(delivery.getId()));
	}
	
	// ----- DELETE DELIVERY
	
	public static void deleteDelivery(String domainName, Integer domainId, String login, DeliveryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getManagement().deleteDelivery(ctx, filter);
		}
	}
	
	public static void deleteDelivery(Domain domain, User user, Integer id) {
		deleteDelivery(domain.getName(), domain.getId(), user.getLogin(), id);
	}
	
	public static void deleteDelivery(Domain domain, String login, Integer id) {
		deleteDelivery(domain.getName(), domain.getId(), login, id);
	}
	
	public static void deleteDelivery(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getManagement().deleteDelivery(ctx, id);
		}
	}
	
	// -------------------- DELIVERY DETAILS
	
	public static Stream<DeliveryDetail> getDeliveryDetails(String domainName, Integer domainId, String login, DeliveryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getManagement().getDeliveryDetails(ctx, filter);
		} 
	}
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(String domainName, Integer domainId, String login,
			DeliveryFilter deliveryFilter, DeliveryDetailFilter detailFilter, ProductFilter productFilter, ItemFilter itemFilter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getWarehouse().getDeliveryDetailStream(ctx, deliveryFilter, detailFilter, productFilter, itemFilter);
		}	
	}
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(String domainName,
			Integer domainId, String login, DeliveryDetailFilter filter) {
		CloseableAONContext ctx = null;
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
	
	public static DeliveryDetail saveDeliveryDetail(Occam occam, DeliveryDetail deliveryDetail) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getManagement().saveDeliveryDetail(ctx, deliveryDetail);
		} 
	}

	// ********************************************
	// ********************************* PAYROLL **
	// ********************************************

	public static void deleteSalaries(AONContext ctx, 
			SalaryFilter filter) {
		getSalary().deleteSalaries(ctx, filter);
	}

	public static void saveAgreement(AONContext ctx, Agreement... agreements)
			throws AonCoreException {
		getAgreement().save(ctx, agreements);
	}

	public static Stream<Salary> getSalaries(AONContext ctx, 
			SalaryFilter filter) {
		return getSalary().getSalaries(ctx, filter, Salary::new);
	}
	
	public static Stream<Salary> getSalaries(Domain domain, String login, SalaryFilter filter) {
		return getSalary().getSalaries(AONContext.getAONContext(domain, login), filter, Salary::new);
	}

	public static Collection<Salary> saveSalaries(AONContext ctx, 
			Integer domainId, Collection<Salary> salaries) {
		return getSalary().saveSalaries(ctx, domainId, salaries);
	}


	public static Collection<Salary> saveSalaries(String domainName, String login, 
			Integer domainId, Collection<Salary> salaries) {
		try ( CloseableAONContext ctx = AONContext.getAONContext(domainName, login) ){
			return getSalary().saveSalaries(ctx, domainId, salaries);
		}
	}

	public static Stream<Salary> getSalaryData(AONContext ctx, 
			SalaryFilter filter) {
		return getSalary().getSalaryData(ctx, filter, Salary::new);
	}
	
	public static Stream<Salary> getSalaryData(Domain domain, String login, SalaryFilter filter) {
		return getSalary().getSalaryData(AONContext.getAONContext(domain, login), filter, Salary::new);
	}

	public static Stream<Salary> getContractData(AONContext ctx, 
			SalaryFilter filter) {
		return getSalary().getSalaryData(ctx, filter, Salary::new);
	}

	public static Stream<Bonus> getAvailableBonuses(AONContext ctx, 
			BonusFilter filter) {
		return getSystem().getAvailableBonus(ctx, filter, Bonus::new);
	}
	
	public static Collection<FiscalModel> getFiscalModels(AONContext ctx, Salary salary) {
		return getSalary().getFiscalModels(ctx, salary);
	}

	// ********************************************
	// ************************************* FEE **
	// ********************************************
	
	public static Map<String, Workplace> getWorkplacesSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getWorkplacesSuggestion(ctx, searchDomain, query);
		}
	}
	
	public static Map<String, Seller> getSellersSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getSellersSuggestion(ctx, searchDomain, query);
		}
	}
	
	
	public static Map<String, Project> getProjectsSuggestion(String domainName, int domainId, String login, Integer customerId, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getProjectsSuggestion(ctx, searchDomain, customerId, query);
		}
	}
	
	public static Map<String, Fee> getCustomerFeeSuggestion(String domainName, int domainId, String login, Integer itemId, Integer customerId, String customerFeeQuery) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getCustomerFeeSuggestion(ctx, domainId, itemId, customerId, customerFeeQuery);
		}
	}
	
	public static void reorderCustomerFeeLine(String domainName, int domainId, String login, Integer customer) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			getFinance().reorderCustomerFeeLine(ctx, domainId, customer);
		}
	}
	
	public static Stream<Item> getItemsSuggestion(Occam occam, Integer domainId, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getFinance().getItemsSuggestion(ctx, domainId, query);
		}
	}

	/**
	 * @deprecated OldItem is deprecated!!
	 */
	@Deprecated
	public static Map<String, OldItem> getProductsSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getProductsSuggestion(ctx, searchDomain, query);
		}
	}
	
	public static Map<String, Integer> getProductCategoriesSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getProductCategoriesSuggestion(ctx, searchDomain, query);
		}
	}
	
	public static Map<String, Integer> getProductTagsSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getProductTagsSuggestion(ctx, searchDomain, query);
		}
	}

	/**
	 * @deprecated Replaced by getCustomersSuggestion(Occam occam, Integer domainId, String query)
	 */
	@Deprecated
	public static Map<String, Customer> getCustomersSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getFeeCustomersSuggestion(ctx, searchDomain, query);
		}
	}
	
	public static Stream<Customer> getCustomersSuggestion(Occam occam, Integer domainId, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getFinance().getCustomersSuggestion(ctx, domainId, query);
		}
	}
	
	public static Map<Integer, Integer> getCustomerProductsUpdates(String domainName, int domainId, String login, Integer searchDomain, CustomerFeeParams customerFeeParams) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getCustomerProductsUpdates(ctx, searchDomain, customerFeeParams);
		}
	}
	
	public static Stream<Fee> getFeeStream(String domainName, Integer domainId, String login, FeeFilter filter){
		CloseableAONContext ctx = null;
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
	
	public static LinkedList<Fee> getFeeList(String domainName, Integer domainId, String login, CustomerFeeParams customerFeeParams){
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getFeeList(ctx, customerFeeParams);
		}
	}
	
	public static LinkedList<Fee> getFullFeeList(String domainName, Integer domainId, String login, CustomerFeeParams customerFeeParams){
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getFullFeeList(ctx, customerFeeParams);
		}
	}
	
	public static LinkedList<Fee> getFeeList(String domainName, Integer domainId, String login, FeeFilter filter){
		return getFeeStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Fee save(String domainName, Integer domainId, String login, Fee fee) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().save(ctx, fee);
		}
	}
	
	public static Integer saveFees(String domainName, Integer domainId, String login, LinkedList<Fee> feeList) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().saveList(ctx, feeList);
		}
	}
	
	public static Integer saveMassiveFees(String domainName, Integer domainId, String login, Fee fee, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().saveMassiveFees(ctx, fee, params);
		}
	}
	
	public static Fee createCustomerFeeList(String domainName, Integer domainId, String login, Fee fee) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().createCustomerFeeList(ctx, fee);
		}
	}
	
	public static void updateRitemCustomerFee(String domainName, int domainId, String login, Integer customerFee, Integer ritem) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			getFinance().updateRitemCustomerFee(ctx, customerFee, ritem);
		}
	}	

	public static void deleteFee(AONContext ctx, Fee f) {
		getFinance().deleteFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Stream<Fee> fs) {
		getFinance().deleteFee(ctx, fs);
	}
	
	public static void deleteFee(String domainName, int domainId, String login, Stream<Fee> fs) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			getFinance().deleteFee(ctx, fs);;
		}	
	}
	
	public static void deleteFee(String domainName, int domainId, String login, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			getFinance().deleteFee(ctx, params);
		}	
	}
	
	public static Map<Integer, Integer> getMinMaxCustomerFeeYear(String domainName, int domainId, String login, Integer searchDomain) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getMinMaxCustomerFeeYear(ctx, searchDomain);
		}
	}
	
	public static Integer getItemIdByProductCode(String domainName, int domainId, String login, String productCode) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getItemIdByProductCode(ctx, domainId, productCode);
		}
	}

	// ********************************************
	// ****************************** GWT-OFFICE **
	// ********************************************

	public static User getUser(Occam occam, UserFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getSecurity().getUser(ctx, filter);
		} 
    }
	
	public static User getUser(Domain domain, User user, UserFilter filter) {
	    return getUser(domain, user.getLogin(), filter);
    }
	
	public static User getUser(Domain domain, String login, UserFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)){
			return getSecurity().getUser(ctx, filter);
		} 
	}
	
	public static User getUser(String domainName, Integer domainId, String userName, UserFilter filter) {
		return getUserStream(domainName, domainId, userName, filter).findFirst().orElse(new User());
	}
	
	public static User getUser(Integer domainId, String domainName,
			String userName, Integer userId) {
		return getUserStream(domainName, domainId, userName, f -> f.getIdProperty().eq(userId)).findFirst().orElse(new User());
	}
	
	public static Stream<User> getUserStream(Domain domain, User user, UserFilter filter, Options... options) {
        return getUserStream(domain.getName(), domain.getId(), user.getLogin(), filter, options);
    }
	
    public static Stream<User> getUserStream(Domain domain, String login, UserFilter filter, Options... options) {
        return getUserStream(domain.getName(), domain.getId(), login, filter, options);
    }
	public static Stream<User> getUserStream(String domainName, Integer domainId, String userName, UserFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, userName)){
			return getSecurity().getUserStream(ctx, filter, options);
		} 
	}
	
	public static Stream<User> getDomainUserStream(String domainName, Integer domainId, String userName) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, userName)){
			return getSecurity().getDomainUserStream(ctx);
		} 
	}
	
	public static Stream<User> getDomainUserStream(String domainName, Integer domainId, String userName, UserFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, userName)){
			return getSecurity().getDomainUserStream(ctx, filter);
		} 
	}
	
	public static Stream<User> getDomainUserStream(Domain domain, User user, Integer page, Integer perPage, UserFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getSecurity().getDomainUserStream(ctx, page, perPage, filter);
		} 
	}
	
	
	/**
	 * @deprecated  Replaced by AON.getUserStream
	 */
	@Deprecated(forRemoval = true )
	public static List<User> getUsers(Integer domainId, String domainName, String userName) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, userName);			
			return getOffice().getUsers(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Registry> getRegistryStream(String domainName, Integer domainId, String login, RegistryFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getRegistryStream(ctx, filter);
		}
	}
	
	public static Stream<Registry> getAonRegistryStream(String domainName, Integer domainId, String login, RegistryFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getAonRegistryStream(ctx, filter);
		}
	}
	
	public static Registry getRegistry(Domain domain, User user, RegistryFilter filter) {
		return getRegistry(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Registry getRegistry(String domainName, Integer domainId, String login, RegistryFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistry(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Registry getRegistry(String domainName, Integer domainId, String login, Integer id){
		return getRegistry(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	@Deprecated
	public static NotificationInfo getNotificationInfo(String domainName, Integer domainId, String login){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getOffice().getNotificationInfo(ctx);
		} finally{
			if(ctx != null) ctx.close();
		}
	}

	@Deprecated
	public static void insertNotificationInfo(String domainName, Integer domainId, String login,
			NotificationInfo notificationInfo){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getOffice().insertNotificationInfo(ctx, notificationInfo);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	@Deprecated
	public static void insertNotificationInfo(String domainName, Integer domainId, String login,
			String data, AppParam appParam){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		return getAttachStream(domainName, domainId, login, p -> filter.filter(p).limit(0, 1), attachType, true)
				.findFirst().orElse(new Attach());
	}
	
	public static Attach getRawdocAttach(String domainName, Integer domainId, String login, RawdocFilter filter, AttachType attachType) {
		return getRawdocAttachStream(domainName, domainId, login, p -> filter.filter(p).limit(0, 1), attachType)
				.findFirst().orElse(new Attach());
	}
	
	public static Attach getAttach(String domainName, Integer domainId,
			String login, AttachFilter filter, AttachType attachType, Boolean withData) {
		return getAttachStream(domainName, domainId, login, p -> filter.filter(p).limit(0, 1), attachType, withData)
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

	
	public static Stream<Attach> getDocumentalAttachStream(String domainName,
			Integer domainId, String login, AttachFilter filter,
			AttachType attachType, Boolean withData, Options...options) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attachType.equals(AttachType.REGISTRY))
				return getAttachment().getDocumentalRegistryAttachStream(ctx, filter, withData, options);
			
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void setAttach(String domainName, Integer domainId, String login, byte[] data, Integer attachId, AttachType attachType) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);

			if (attachType.equals(AttachType.REGISTRY))
				getAttachment().setRegistryAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.CONTRACT))
				getAttachment().setContractAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.INVOICE))
				getAttachment().setInvoiceAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.ITEM))
				getAttachment().setItemAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.OFFER))
				getAttachment().setOfferAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.PAYROLL))
				getAttachment().setPayrollAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.PROJECT))
				getAttachment().setProjectAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.SEPE))
				getAttachment().setSepeAttachStream(ctx, attachId, data);
			else if (attachType.equals(AttachType.DATA))
				getAttachment().setDataAttachStream(ctx, attachId, data);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Attach> getAttachStream(String domainName,
			Integer domainId, String login, AttachFilter filter,
			AttachType attachType, Boolean withData) {
		CloseableAONContext ctx = null;
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
	
	public static Stream<Attach> getRawdocAttachStream(String domainName,
			Integer domainId, String login, RawdocFilter filter,	AttachType attachType) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			if (attachType.equals(AttachType.RAWDOC))
				return getAttachment().getRawdocAttachStream(ctx, filter);
			return null;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	

	public static Attach save(Domain domain, User user, Attach attach) {
		if(null == attach.getId()) {
			Integer insertId = insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
			attach.setId(insertId);
		} else 
			updateAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
			
		return attach;
	}
	
	@Deprecated
	public static Integer insert(String domainName, Integer domainId, String login, Attach attach){
		return insertAttach(domainName, domainId, login, attach);
	}

	public static Integer insertAttach(String domainName, Integer domainId, String login, Attach attach) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
			else if (attach.getAttachType().equals(AttachType.DATA))
				getAttachment().updateDataAttachData(ctx, attach);

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void updateAttachDriveId(String domainName, Integer domainId,
			String login, Integer attachId, String driveId,
			AttachType attachType) {
		CloseableAONContext ctx = null;
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
			else if (attachType.equals(AttachType.DATA))
				getAttachment().updateDataAttachDriveId(ctx, attachId, driveId);
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
		CloseableAONContext ctx = null;
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

	public static LinkedList<Tag> getRegistryAttachTag(String domainName,
			Integer domainId, String login, Integer rattachId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAttachment().getRegistryAttachmentTag(ctx, rattachId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static RattachTag save(Domain domain, String login, RattachTag rattachTag) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)){
			return getAttachment().save(ctx, rattachTag);
		}
	}
	
	@Deprecated
	public static RattachTag insertRegistryAttachTag(String domainName, Integer domainId, String login, Integer rattachId, Integer tagId) {
		RattachTag rt = new RattachTag()
				.setDomain(domainId)
				.setRattach(rattachId)
				.setTag(new Tag().setId(tagId));
		return save(new Domain().setName(domainName).setId(domainId), login, rt);
	}

	public static void deleteRegistryAttachTag(String domainName,
			Integer domainId, String login, Integer rattachId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getAttachment().deleteRegistryAttachTag(ctx, rattachId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static void deleteTagRegistryAttach(String domainName,
			Integer domainId, String login, Integer tagId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getAttachment().deleteTagRegistryAttach(ctx, tagId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ******************************* WAREHOUSE **
	// ********************************************

	public static Stream<Warehouse> getWarehouseStream(String domainName, Integer domainId, String login, WarehouseFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getWarehouse().getWarehouseStream(ctx, filter);
		}
	}
	
	public static Warehouse getWarehouse(String domainName, Integer domainId, String login, WarehouseFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getWarehouse().getWarehouse(ctx, filter);
		}
	}
	
	public static Warehouse saveWarehouse(String domainName, Integer domainId, String login, Warehouse warehouse) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getWarehouse().saveWarehouse(ctx, warehouse);
		}
	}
	
	public static void deleteWarehouse(String domainName, Integer domainId, String login, Integer warehouseId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getWarehouse().deleteWarehouse(ctx, warehouseId);
		}
	}
	
	
	public static Stream<IncomeDetail> getIncomeDetails(String domainName,
			Integer domainId, String login, IncomeFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getManagement().getIncomeDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Income> getIncomeStream(String domainName, Integer domainId, String login, IncomeFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static LinkedList<Income> getIncomeList(String domainName, Integer domainId, String login, IncomeFilter filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static Optional<Income> insertIncome(String domainName, Integer domainId, String login, Income income){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertIncome(ctx, income);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<Income> deleteIncome(String domainName, Integer domainId, String login, Integer id){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().deleteIncome(ctx, id);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<IncomeDetail> insertIncomeDetail(String domainName, Integer domainId, String login, IncomeDetail incomeDetail){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertIncomeDetail(ctx, incomeDetail);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<IncomeDetail> updateIncomeDetail(String domainName, Integer domainId, String login, IncomeDetail incomeDetail){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().updateIncomeDetail(ctx, incomeDetail);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Optional<IncomeDetail> deleteIncomeDetail(String domainName, Integer domainId, String login, Integer id){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().deleteIncomeDetail(ctx, id);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<IncomeDetail> getIncomeDetailStream(String domainName, Integer domainId, String login, IncomeDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeDetailStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static Stream<IncomeDetail> getIncomeDetailStream(String domainName, Integer domainId, String login,
			IncomeFilter incomeFilter, IncomeDetailFilter detailFilter, ProductFilter productFilter, ItemFilter itemFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getIncomeDetailStream(ctx, incomeFilter, detailFilter, productFilter, itemFilter);
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	public static Optional<IncomeDetail> getIncomeDetail(String domainName, Integer domainId, String login, IncomeDetailFilter filter){
		CloseableAONContext ctx = null;
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
			Integer domainId, String user, OldItem item, Integer workplaceId,
			Integer warehouseId) {
		CloseableAONContext ctx = null;
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
			Integer domainId, String user, OldItem item, Integer workplaceId,
			Integer warehouseId, Date date) {
		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			String months, Integer workplaceId, Integer warehouseId) {
		Calendar calendar = Calendar.getInstance();
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);
		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			String months, Integer workplaceId, Integer warehouseId,
			Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Integer m = Integer.parseInt(months);
		calendar.add(Calendar.MONTH, -m);
		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			Integer workplaceId, Integer warehouseId) {
		CloseableAONContext ctx = null;
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
			String domainName, Integer domainId, String user, OldItem item,
			Integer workplaceId, Integer warehouseId, Date date) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getIncomeDetailListUntilDate(ctx, item,
					workplaceId, warehouseId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static InventoryDetail getInventoryDetail(Occam occam, Integer domain, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getWarehouse().getInventoryDetail(ctx, domain, id);
		}
	}
	
	public static InventoryDetail saveInventoryDetail(Occam occam, InventoryDetail inventoryDetail) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getWarehouse().saveInventoryDetail(ctx, inventoryDetail);
		}
	}
	
	public static Stream<InventoryDetail> getInventoryDetailStream(String domainName, Integer domainId, String login, InventoryDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getInventoryDetailStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<InventoryDetail> getInventoryDetailList(
			String domainName, Integer domainId, String user,
			Integer inventoryId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getWarehouse().getInventoryDetailList(ctx, inventoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Inventory getInventory(Occam occam, Integer domain, Integer id, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().getInventory(ctx, domain, id, options);
		}	
	}
	
	public static List<Inventory> getInventoryList(Occam occam, InventoryFilter filter, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().getInventoryList(ctx, filter, options);
		}	
	}

	public static void updateInventoryDetail(String domainName,
			Integer domainId, String login, InventoryDetail inventoryDetail) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getDepartmentList(ctx, workplaceId, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	// ------------------------------------------------------------------- STATS
	public static StatParams createStatParams(String domainName, int domain,
			String user) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,user);
			return getStats().getStatData(ctx, params);
		} finally {
			if (ctx != null) 
				ctx.close();
		}
	}
	
	public static StatData<String, String, Double> getFinanceStat(String domainName, Integer domainId, String user, FinanceFilter financeFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,user);
			return getStats().getFinanceStat(ctx, financeFilter);
		} finally {
			if (ctx != null) 
				ctx.close();
		}
	}

	public static String getInvoicesReport(String domainName, int domain, String userLogin, StatParams params) {
		CloseableAONContext ctx = AONContext.getAONContext(domainName,domain,userLogin);
		return getStats().getInvoicesReport(ctx, params, () -> {if (ctx != null) ctx.close();});
	}
	
	public static Stream<OldTask> getStatTaskStream(String domainName, Integer domainId, String login, StatParams params){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,login);
			return getStats().getStatTaskStream(ctx, params);
		} finally {
			if (ctx != null) 
				ctx.close();
		}		
	}
	
	public static StatData<Integer, String, Double> getProductStat(String domainName, Integer domainId, String login, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter, SalesFilter salesFilter, PurchaseFilter purchaseFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,login);
			return getStats().getProductStat(ctx, productFilter, itemFilter, invoiceFilter,
					deliveryFilter, salesFilter, purchaseFilter);
		} finally {
			if (ctx != null) 
				ctx.close();
		}		
	}
	
	public static StatData<Integer, String, Double> getProductMovements(String domainName, Integer domainId, String login, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter, IncomeFilter incomeFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,login);
			return getStats().getProductMovements(ctx, productFilter, itemFilter, invoiceFilter,
					deliveryFilter, incomeFilter);
		} finally {
			if (ctx != null) 
				ctx.close();
		}		
	}
	
	public static StatData<Integer, String, Double> getItemMovements(String domainName, Integer domainId, String login, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter, IncomeFilter incomeFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,login);
			return getStats().getItemMovements(ctx, productFilter, itemFilter, invoiceFilter,
					deliveryFilter, incomeFilter);
		} finally {
			if (ctx != null) 
				ctx.close();
		}		
	}
	
	public static StatData<Integer, String, Double> getElaborationMovements(String domainName, Integer domainId, String login, ProductFilter productFilter,
			ItemFilter itemFilter, ElaborationFilter elaborationFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName,domainId,login);
			return getStats().getElaborationMovements(ctx, productFilter, itemFilter, elaborationFilter);
		} finally {
			if (ctx != null) 
				ctx.close();
		}		
	}
	
	// ********************************************
	// ********************************* Project **
	// ********************************************

	public static Stream<Project> getProjectStream(Domain domain, User user, ProjectFilter filter, Integer page, Integer perPage){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().getProjectStream(ctx, filter, page, perPage);
		}	
	}
	
	public static Stream<Project> getProjectStream(Domain domain, User user, ProjectFilter filter){
		return getProjectStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<Project> getProjectStream(Domain domain, String login, ProjectFilter filter){
		return getProjectStream(domain.getName(), domain.getId(), login, filter);
	}

	public static Stream<Project> getProjectStream(String domainName, Integer domainId, String login, ProjectFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectStream(ctx, filter);
		}
	}
	

	public static Optional<ProjectTas> getProjectTas(Occam occam, ProjectTasFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getProject().getProjectTasStream(ctx, filter).findFirst();
		}
	}
	
	public static Stream<ProjectTas> getProjectTasStream(Occam occam, ProjectTasFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getProject().getProjectTasStream(ctx, filter);
		}
	}
	
	public static List<ProjectTas> getProjectTasList(Occam occam, ProjectTasFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getProject().getProjectTasStream(ctx, filter).collect(Collectors.toList());
		}
	}
	
	public static Project getProject(String domainName, Integer domainId, String login, ProjectFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProject(ctx, filter);
		}
	}
	
	public static Project getProjectFull(String domainName, Integer domainId, String login, ProjectFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectFull(ctx, filter);
		}
	}

	public static LinkedList<Project> getProjectList(String domainName, Integer domainId, String login, ProjectFilter filter) {
		return getProjectStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Project saveProject(Domain domain, User user, Project project) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().saveProject(ctx, project);
		}
	}
	
	public static void deleteProject(Domain domain, User user, Integer projectId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getProject().deleteProject(ctx, projectId);
		}
	}
	
	public static List<Project> getProjectList(ProjectParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())){
			return getProject().getProjectList(ctx, params);
		}
	}

	public static Integer getProjectsCount(ProjectParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())){
			return getProject().getProjectsCount(ctx, params);
		}
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.api.AON.saveProject(Domain domain0, User user, Project project)
	 */
	@Deprecated(forRemoval = true )
	public static Integer insertProject(String domainName, Integer domainId,
			String login, Project project) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().insertProject(ctx, project);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ---------- PROJECT TYPE
	
	public static Stream<ProjectType> getProjectTypeStream(Domain domain, User user, ProjectTypeFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().getProjectTypeStream(ctx, filter);
		}
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.api.AON.getProjectType(String domainName, Integer domainId, String login, ProjectTypeFilter filter)
	 */
	@Deprecated(forRemoval = true )
	public static ProjectType getProjectType(String domainName, Integer domainId, String login, String description){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectType(ctx, f-> f.getDomainProperty().eq(domainId).and(f.getDescriptionProperty().eq(description)));
		}
	}
	
	public static ProjectType getProjectType(String domainName, Integer domainId, String login, ProjectTypeFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectType(ctx, filter);
		}
	}

	public static ProjectType saveProjectType(Domain domain, User user, ProjectType projectType){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().saveProjectType(ctx, projectType);
		}
	}
	
	public static ProjectType saveProjectType(String domainName, Integer domainId, String login, ProjectType projectType){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().saveProjectType(ctx, projectType);
		}
	}

	public static void deleteProjectType(Domain domain, User user, Integer projectTypeId){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getProject().deleteProjectType(ctx, projectTypeId);
		}
	}
	
	public static void deleteProjectType(String domainName, Integer domainId, String login, Integer projectTypeId){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getProject().deleteProjectType(ctx, projectTypeId);
		}
	}
	
	// ---------- ACTIVITY TYPE
	
	public static Stream<ActivityType> getActivityTypeStream(Domain domain, User user, ActivityTypeFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().getActivityTypeStream(ctx, filter);
		}
	}
	
	public static ActivityType getActivityType(Domain domain, User user, ActivityTypeFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().getActivityType(ctx, filter);
		}
	}

	public static ActivityType saveActivityType(Domain domain, User user, ActivityType activityType){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProject().saveActivityType(ctx, activityType);
		}
	}
	
	public static void deleteActivityType(Domain domain, User user, Integer activityTypeId){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getProject().deleteActivityType(ctx, activityTypeId);
		}
	}
	
	
	// ---------- PROJECT HOLDER
	
	public static ProjectHolder getProjectHolder(Domain domain, User user, ProjectHolderFilter filter) {
		return getProjectHolder(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static ProjectHolder getProjectHolder(Domain domain, String login, ProjectHolderFilter filter) {
		return getProjectHolder(domain.getName(), domain.getId(), login, filter);
	}
	
	public static ProjectHolder getProjectHolder(String domainName, Integer domainId, String login, ProjectHolderFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectHolder(ctx, filter);
		}
	}
	
	public static Stream<ProjectHolder> getProjectHolderStream(Domain domain, User user, ProjectHolderFilter filter) {
		return getProjectHolderStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<ProjectHolder> getProjectHolderStream(Domain domain, String login, ProjectHolderFilter filter) {
		return getProjectHolderStream(domain.getName(), domain.getId(), login, filter);
	}
	
	public static Stream<ProjectHolder> getProjectHolderStream(String domainName, Integer domainId, String login, ProjectHolderFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectHolderStream(ctx, filter);
		}
	}
	
	public static List<ProjectHolder> getProjectHolderList(Domain domain, User user, ProjectHolderFilter filter) {
		return getProjectHolderList(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static List<ProjectHolder> getProjectHolderList(Domain domain, String login, ProjectHolderFilter filter) {
		return getProjectHolderList(domain.getName(), domain.getId(), login, filter);
	}
	
	public static List<ProjectHolder> getProjectHolderList(String domainName, Integer domainId, String login, ProjectHolderFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getProject().getProjectHolderList(ctx, filter);
		}
	}
	
	public static ProjectHolder saveProjectHolder(Domain domain, User user, ProjectHolder holder) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			return getProject().saveProjectHolder(ctx, holder);
		}
	}
	
	public static void deleteProjectHolder(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			getProject().deleteProjectHolder(ctx, id);
		}
	}
	
	// ---------- PROJECT ACTIVITY
	
	
	public static Stream<ProjectActivity> getProjectActivityStream(Domain domain, User user, ProjectActivityFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			return getProject().getProjectActivityStream(ctx, filter);
		}
	}
	
	public static void deleteProjectActivity(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			getProject().deleteProjectActivity(ctx, id);
		}
	}
	
	public static ProjectActivity saveProjectActivity(Domain domain, User user, ProjectActivity projectActivity) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			return getProject().saveProjectActivity(ctx, projectActivity);
		}
	}
	
	// ---------- PROJECT COMMERCIAL
	
	public static Integer insertProjectCommercial(String domainName, Integer domainId,
			String login, ProjectCommercial projectCommercial) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().insertProjectCommercial(ctx, projectCommercial);
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getProject().getProjectCommercialStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void fixProjectCommercial(String domainName, Integer domainId, String login) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getProject().fixProjectCommercial(ctx);
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
	public static Stock addPackageStock(Occam occam, Integer item, Integer warehouse){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().addPackageStock(ctx, item, warehouse);
		}
	}
	
	public static void movePackageStock(Occam occam, Integer item, Integer sourceWarehouse, Integer destinyWarehouse){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			getWarehouse().movePackageStock(ctx, item, sourceWarehouse, destinyWarehouse);
		}
	}
	

	public static Stock addStock(Domain domain, String login, Integer item, Double quantity, Integer warehouse){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)){
			return getWarehouse().addStock(ctx, item, warehouse, quantity);
		}
	}
	
	public static Stock subtractStock(Domain domain, String login, Integer item, Double quantity, Integer warehouse){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)){
			return getWarehouse().subtractStock(ctx, item, warehouse, quantity);
		}
	}
	
	public static Stock saveStock(Occam occam, Stock stock){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().saveStock(ctx, stock);
		}
	}

	public static Stock getStock(Occam occam, StockFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().getStock(ctx, filter);
		}
	}
	
	public static Stream<Stock> getStockStream(Occam occam, StockFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getWarehouse().getStockStream(ctx, filter);
		}
	}
	
	public static Stream<Stock> getStockStream(String domainName, Integer domainId, String login, StockFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){ 
			return getWarehouse().getStockStream(ctx, filter);
		}
	}
	
	public static Optional<Stock> updateStock(String domainName, Integer domainId, String login, Stock stock){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().updateStock(ctx, stock);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Stock> deleteStock(String domainName, Integer domainId, String login, Integer stockId){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().deleteStock(ctx, stockId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Optional<Stock> insertStock(String domainName, Integer domainId, String login, Stock stock){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertStock(ctx, stock);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer getWarehouseTransferNextNumber(String domainName, Integer domainId, String login, String serie){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertWarehouseTransfer(ctx, warehouseTransfer);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<WarehouseTransferDetail> getWarehouseTransferDetailStream(String domainName, Integer domainId, String login,
			WarehouseTransferFilter filter, ProductFilter pFilter, ItemFilter iFilter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getWarehouseTransferDetailStream(ctx, filter, pFilter, iFilter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer insertWarehouseTransferDetail(String domainName, Integer domainId, String login,
			WarehouseTransferDetail warehouseTransferDetail){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertWarehouseTransferDetail(ctx, warehouseTransferDetail);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer insertWarehouseTransferDetail(String domainName, Integer domainId, String login,
			Stream<WarehouseTransferDetail> warehouseTransferDetail){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getWarehouse().deleteInventory(ctx, inventoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------ SUPPLIER 
	
	public static Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter) {
		return getRegistry().getSupplierStream(ctx, filter);
	}

	public static Stream<Supplier> getSupplierStream(String domainName, Integer domainId, String login, SupplierFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSupplierStream(ctx, filter);
		}
	}
	
	public static Stream<Supplier> getSupplierStream(String domainName, Integer domainId, String login, SupplierFilter filter, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getSupplierStream(ctx, filter, offset, limit);
		}
	}
	
	public static LinkedList<Supplier> getSupplierList(String domainName, Integer domainId, String login, SupplierFilter filter) {
		return getSupplierStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Optional<Supplier> getSupplier(AONContext ctx, SupplierFilter filter) {
		return getSupplierStream(ctx, filter)
				.findFirst();
	}
	public static Optional<Supplier> getSupplier(String domainName, Integer domainId, String login, SupplierFilter filter) {
		return getSupplierStream(domainName, domainId, login, filter)
				.findFirst();
	}
	
	public static Optional<Supplier> getSupplier(String domainName, Integer domainId, String login, Integer id) {
		return getSupplier(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Supplier saveSupplier(String domainName, Integer domainId, String login, Supplier supplier) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().saveSupplier(ctx, supplier);
		}
	}
	
	/**
	 * @deprecated  Replaced by AON.saveCreditor
	 */
	@Deprecated(forRemoval = true )
	public static Supplier insertSupplier(String domainName, Integer domainId, String login, Supplier supplier) {
		return saveSupplier(domainName, domainId, login, supplier);
	}
	
	// ------------------ TARGET 
	
	public static Stream<Target> getTargetStream(String domainName, Integer domainId, String login, TargetFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getTargetStream(ctx, filter);
		}
	}
	
	public static Stream<Target> getTargetStream(String domainName, Integer domainId, String login, TargetFilter filter, int ofs, int limit){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getTargetStream(ctx, filter, ofs, limit);
		} 
	}
	
	public static Optional<Target> getTarget(String domainName, Integer domainId, String login, TargetFilter filter) {
		return getTargetStream(domainName, domainId, login, filter)
				.findFirst();
	}
	
	public static TargetFull getTargetFull(String domainName, Integer domainId, String login, Integer registry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getTargetFull(ctx, registry);
		} 
	}
	
	public static Optional<Target> getTarget(String domainName, Integer domainId, String login, Integer id) {
		return getTarget(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Target insertTarget(String domainName, Integer domainId, String login, Target target) {
		return save(domainName, domainId, login, target);
	}
	
	public static Target save(String domainName, Integer domainId, String login, Target target) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().save(ctx, target);
		}
	}
	
	public static List<TargetFull> getTargetNotUserFull(Domain domain, String user, TargetParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getRegistry().getTargetNotUserFull(ctx, params);
		}
	}
	
	// ------------------ CARRIER 

	public static Carrier getCarrier(Domain domain, User user, Integer id) {
		return getCarrier(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id));
	}
	
	public static Carrier getCarrier(Domain domain, String login, Integer id) {
		return getCarrier(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
	}
	
	public static Carrier getCarrier(String domainName, Integer domainId, String login, Integer id) {
		return getCarrier(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Carrier getCarrier(Domain domain, User user, CarrierFilter filter, Options...options) {
		return getCarrier(domain.getName(), domain.getId(), user.getLogin(), filter, options);
	}
	
	public static Carrier getCarrier(Domain domain, String login, CarrierFilter filter, Options...options) {
		return getCarrier(domain.getName(), domain.getId(), login, filter, options);
	}
	
	public static Carrier getCarrier(String domainName, Integer domainId, String login, CarrierFilter filter, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getCarrier(ctx, filter, options);
		} 
	}

	public static Stream<Carrier> getCarrierStream(Domain domain, User user, CarrierFilter filter, Options...options) {
		return getCarrierStream(domain, user.getLogin(), filter, options);
	}
	
	public static Stream<Carrier> getCarrierStream(Domain domain, String login, CarrierFilter filter, Options...options) {
		return getCarrierStream(domain, login, filter, options);
	}
	
	public static Stream<Carrier> getCarrierStream(String domainName, Integer domainId, String login, CarrierFilter filter, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getCarrierStream(ctx, filter, options);
		} 
	}

	public static Carrier saveCarrier(Domain domain, User user, Carrier carrier) {
		return saveCarrier(domain.getName(), domain.getId(), user.getLogin(), carrier);
	}
	
	public static Carrier saveCarrier(Domain domain, String login, Carrier carrier) {
		return saveCarrier(domain.getName(), domain.getId(), login, carrier);
	}
	
	public static Carrier saveCarrier(String domainName, Integer domainId, String login, Carrier carrier) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().saveCarrier(ctx, carrier);
		}
	}
	
	// ------------------ CARRIER PACKING
	
	public static List<CarrierPacking> getCarrierPackingList(Occam occam, CarrierPackingFilter filter, Options... options) {
		return getCarrierPackingList(occam.getDomainName(), occam.getDomain(), occam.getUser(), filter, options);
	}
	
	public static List<CarrierPacking> getCarrierPackingList(Domain domain, User user, CarrierPackingFilter filter, Options... options) {
		return getCarrierPackingList(domain.getName(), domain.getId(), user.getLogin(), filter, options);
	}

	public static List<CarrierPacking> getCarrierPackingList(Domain domain, String login, CarrierPackingFilter filter, Options... options) {
		return getCarrierPackingList(domain.getName(), domain.getId(), login, filter, options);
	}
		
	public static List<CarrierPacking> getCarrierPackingList(String domainName, Integer domainId, String login, CarrierPackingFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().getCarrierPackingList(ctx, filter, options);
		}
	}
	
	public static Stream<String> getCarrierPackingSeries(String domainName, Integer domainId, String login) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getCarrierPackingSeries(ctx);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static Stream<CarrierPacking> getCarrierPackingStream(String domainName, Integer domainId, String login, CarrierPackingFilter filter) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().insertCarrierPacking(ctx, carrierPacking);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static CarrierPacking updateCarrierPacking(String domainName, Integer domainId, String login, CarrierPacking carrierPacking, CarrierPackingFilter filter) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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

	public static Elaboration getElaboration(Occam occam, ElaborationFilter filter, Options...options) {
		return getElaboration(occam.getDomainName(), occam.getDomain(), occam.getUser(), filter, options);
	}
	
	public static Elaboration getElaboration(Domain domain, User user, ElaborationFilter filter, Options...options) {
		return getElaboration(domain.getName(), domain.getId(), user.getLogin(), filter, options);
	}

	public static Elaboration getElaboration(Domain domain, String login, ElaborationFilter filter, Options...options) {
		return getElaboration(domain.getName(), domain.getId(), login, filter, options);
	}
		
	public static Elaboration getElaboration(String domainName, Integer domainId, String login, ElaborationFilter filter, Options...options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().getElaboration(ctx, filter, options);
		}
	}
		
	
	// ---------- ELABORATION STREAM
		
	public static Stream<Elaboration> getElaborationStream(Occam occam, ElaborationFilter filter) {
		return getElaborationStream(occam.getDomainName(), occam.getDomain(), occam.getUser(), filter);
	}
	
	public static Stream<Elaboration> getElaborationStream(Domain domain, User user, ElaborationFilter filter) {
		return getElaborationStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}

	public static Stream<Elaboration> getElaborationStream(Domain domain, String login, ElaborationFilter filter) {
		return getElaborationStream(domain.getName(), domain.getId(), login, filter);
	}
		
	public static Stream<Elaboration> getElaborationStream(String domainName, Integer domainId, String login, ElaborationFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().getElaborationStream(ctx, filter);
		}
	}
		
	// ---------- ELABORATION LIST
		
	public static List<Elaboration> getElaborationList(Occam occam, ElaborationFilter filter, Options... options) {
		return getElaborationList(occam.getDomainName(), occam.getDomain(), occam.getUser(), filter, options);
	}
		
	public static List<Elaboration> getElaborationList(Domain domain, User user, ElaborationFilter filter, Options... options) {
		return getElaborationList(domain.getName(), domain.getId(), user.getLogin(), filter, options);
	}

	public static List<Elaboration> getElaborationList(Domain domain, String login, ElaborationFilter filter, Options... options) {
		return getElaborationList(domain.getName(), domain.getId(), login, filter, options);
	}
		
	public static List<Elaboration> getElaborationList(String domainName, Integer domainId, String login, ElaborationFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().getElaborationList(ctx, filter, options);
		}
	}
	
	public static Elaboration getFullElaboration(String domainName,
			Integer domainId, String login, Integer id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			Elaboration e = getWarehouse().getElaboration(ctx, id);
			
			Item item = getItem(new Domain().setName(domainName).setId(domainId), login, f -> 
			f.getIdProperty().eq(e.getItem().getId()));
			e.setItem(item);
			
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

	public static Elaboration saveElaboration(Domain domain, User user, Elaboration elaboration) {
		return saveElaboration(domain.getName(), domain.getId(), user.getLogin(), elaboration); 
	}
	
	public static Elaboration saveElaboration(Domain domain, String login, Elaboration elaboration) {
		return saveElaboration(domain.getName(), domain.getId(), login, elaboration); 	
	}
	
	public static Elaboration saveElaboration(String domainName, Integer domainId, String login, Elaboration elaboration) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().saveElaboration(ctx, elaboration);
		}
	}

	public static Elaboration saveElaborationSerial(Occam occam, Elaboration elaboration) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getWarehouse().saveElaborationSerial(ctx, elaboration);
		}
	}
	
	/**
	 * @deprecated  Replaced by saveElaboration
	 */
	@Deprecated(forRemoval = true )
	public static Integer insertElaboration(String domainName, Integer domainId, String login, Elaboration elaboration) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().insertElaboration(ctx, elaboration);
		}
	}
	
	/**
	 * @deprecated  Replaced by saveElaboration
	 */
	@Deprecated(forRemoval = true )
	public static Elaboration updateElaboration(String domainName, Integer domainId, String login, Elaboration elaboration) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().updateElaboration(ctx, elaboration);
		}
	}
	
	public static Elaboration deleteElaboration(Domain domain, User user, Integer elaborationId) {
		return deleteElaboration(domain.getName(), domain.getId(), user.getLogin(), elaborationId);
	}

	public static Elaboration deleteElaboration(Domain domain, String login, Integer elaborationId) {
		return deleteElaboration(domain.getName(), domain.getId(), login, elaborationId);
	}
	
	public static Elaboration deleteElaboration(String domainName, Integer domainId, String login, Integer elaborationId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().deleteElaboration(ctx, elaborationId);
		}
	}
	
	public static Integer getElaborationNextNumber(String domainName, Integer domainId, String login, String series) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().getElaborationNextNumber(ctx, series);
		}
	}
	
	public static Stream<ElaborationDetail> getElaborationDetailStream(String domainName, Integer domainId, String login, ElaborationDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			List<ElaborationDetail> list = getWarehouse().getElaborationDetailList(ctx, filter);
			
			list.forEach(detail -> {
				Item item = getItem(new Domain().setName(domainName).setId(domainId), login, f -> 
				f.getIdProperty().eq(detail.getItem().getId()));
				detail.setItem(item);
				
				detail.setWarehouse(getWarehouse(
						domainName,
						domainId,
						login,
						f -> f.getIdProperty().eq(
								detail.getWarehouse().getId())));
			});
			return list.stream();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static List<ElaborationDetail> getElaborationDetailList(
			String domainName, Integer domainId, String login,
			Integer elaborationId) {
		CloseableAONContext ctx = null;
		try {

			ctx = AONContext.getAONContext(domainName, domainId, login);
			List<ElaborationDetail> list = getWarehouse()
					.getElaborationDetailList(ctx, elaborationId);
			list.forEach(detail -> {
				Item item = getItem(new Domain().setName(domainName).setId(domainId), login, f -> 
					f.getIdProperty().eq(detail.getItem().getId()));
				detail.setItem(item);
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
	
	public static ElaborationDetail getFullElaborationDetail(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			ElaborationDetail d = getWarehouse().getElaborationDetail(ctx, id);
			
			Item item = getItem(new Domain().setName(domainName).setId(domainId), login, f -> 
			f.getIdProperty().eq(d.getItem().getId()));
			d.setItem(item);
			
			d.setWarehouse(getWarehouse(
					domainName,
					domainId,
					login,
					f -> f.getIdProperty().eq(
							d.getWarehouse().getId())));
			return d;
		}
	}

	public static ElaborationDetail saveElaborationDetail(String domainName, Integer domainId, String login, ElaborationDetail detail) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().saveElaborationDetail(ctx, detail, true);
		}
	}
	
	public static ElaborationDetail deleteElaborationDetail(String domainName, Integer domainId, String login, ElaborationDetailFilter filter) {
		CloseableAONContext ctx = null;
		List<ElaborationDetail> list = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			list = getWarehouse().getElaborationDetailList(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
		if(list!=null){
			list.forEach(detail -> deleteElaborationDetail(domainName, domainId, login, detail.getId()));
		}
		return null;
	}
	
	public static ElaborationDetail deleteElaborationDetail(String domainName, Integer domainId, String login, Integer detailId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().deleteElaborationDetail(ctx, detailId, true);	
		}
	}
	
	public static List<ElaborationDetailComposition> getElaborationDetailCompositionList(
			String domainName, Integer domainId, String login,
			Integer elaborationDetailId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			List<ElaborationDetailComposition> list = getWarehouse()
					.getElaborationDetailCompositionList(ctx,
							elaborationDetailId);
			list.forEach(composition -> {
				Item item = getItem(new Domain().setName(domainName).setId(domainId), login, f -> 
				f.getIdProperty().eq(composition.getItem().getId()));
				composition.setItem(item);
				
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
	
	public static Integer insertElaborationDetailComposition(String domainName, Integer domainId, String login, ElaborationDetailComposition composition) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().insertElaborationDetailComposition(ctx, composition, true);
		}
	}

	public static ElaborationDetailComposition updateElaborationDetailComposition(String domainName, Integer domainId,
			String login, ElaborationDetailComposition composition) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().updateElaborationDetailComposition(ctx, composition, true);
		}
	}
	
	public static ElaborationDetailComposition deleteElaborationDetailComposition(String domainName, Integer domainId,
			String login, ElaborationDetailCompositionFilter filter) {
		CloseableAONContext ctx = null;
		List<ElaborationDetailComposition> list = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			list = getWarehouse().getElaborationDetailCompositionList(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
		if(list!=null){
			list.forEach(composition -> deleteElaborationDetailComposition(domainName, domainId, login, composition.getId()));
		}
		return null;		
	}
	
	public static ElaborationDetailComposition deleteElaborationDetailComposition(String domainName, Integer domainId, String login, Integer compositionId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getWarehouse().deleteElaborationDetailComposition(ctx, compositionId, true);
		}
	}
	
	// ********************************************
	// ********************* ELABORATION PACKAGE **
	// ********************************************
	
	public static void deleteElaborationPackage(Domain domain, User user, Integer id) {
		deleteElaborationPackage(domain.getName(), domain.getId(), user.getLogin(), id);
	}
	
	public static void deleteElaborationPackage(Domain domain, String login, Integer id) {
		deleteElaborationPackage(domain.getName(), domain.getId(), login, id);
	}
	
	public static void deleteElaborationPackage(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getWarehouse().deleteElaborationPackage(ctx, id);
		}
	}
	
	// DELETE PACKAGE BY ITEM ID
	
	public static void deletePackage(Domain domain, User user, Integer itemId) {
		deletePackage(domain.getName(), domain.getId(), user.getLogin(), itemId);
	}
	
	public static void deletePackage(Domain domain, String login, Integer itemId) {
		deletePackage(domain.getName(), domain.getId(), login, itemId);
	}
	
	public static void deletePackage(String domainName, Integer domainId, String login, Integer itemId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getWarehouse().deletePackage(ctx, itemId);
		}
	}
	
	// DELETE PACKAGE BY SSCC
	
	public static void deletePackage(Domain domain, User user, String sscc) {
		deletePackage(domain.getName(), domain.getId(), user.getLogin(), sscc);
	}
	
	public static void deletePackage(Domain domain, String login, String sscc) {
		deletePackage(domain.getName(), domain.getId(), login, sscc);
	}
	
	public static void deletePackage(String domainName, Integer domainId, String login, String sscc) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getWarehouse().deletePackage(ctx, sscc);
		}
	}
	
	public static void adjustPackageComposition(Domain domain, User user, ItemComposition ic) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getWarehouse().adjustPackageComposition(ctx, ic);
		}
	}
	
	// ********************************************
	// ******************************** Registry **
	// ********************************************
	
	public static Registry save(String domainName, Integer domainId, String login, Registry registry){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().save(ctx, registry);
		}
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.api.AON.save(String domainName, Integer domainId, String login, Registry registry)
	 */
	@Deprecated(forRemoval = true )
	public static Registry insertRegistry(String domainName, Integer domainId, String login, Registry registry){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().save(ctx, registry);
		}
	}

	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.api.AON.save(String domainName, Integer domainId, String login, Registry registry)
	 */
	@Deprecated(forRemoval = true )
	public static Registry updateRegistry(String domainName, Integer domainId, String login, Registry registry){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().save(ctx, registry);
		}
	}
	
	public static void deleteRegistry(String domainName, Integer domainId, String login, Integer registry){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getRegistry().deleteRegistry(ctx, registry);
		}
	}
	
	// ------------------- CUSTOMER

	public static List<Customer> getCustomerWithoutFee(Domain domain, String login){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)) {
			return getRegistry().getCustomerWithoutFee(ctx);
		}
	}
	
	public static List<Customer> getCustomerWithoutFee(Domain domain, String login, CustomerParams customerParams){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)) {
			return getRegistry().getCustomerWithoutFee(ctx, customerParams);
		}
	}
	
	public static Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter){
		return getRegistry().getCustomerStream(ctx, filter);
	}
	
	public static Stream<Customer> getCustomerStream(String domainName, Integer domainId, String login, CustomerFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCustomerStream(ctx, filter);
		}
	}
	
	public static Stream<Customer> getCustomerStream(String domainName, Integer domainId, String login, CustomerFilter filter, int ofs, int limit){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getCustomers(ctx, filter, ofs, limit);
		} 
	}
	
	public static Stream<Customer> getSigCustomerStream(String domainName, Integer domainId, String login, CustomerFilter filter, int ofs, int limit){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getSigCustomerStream(ctx, filter, ofs, limit);
		} 
	}
	
	public static Stream<Customer> getSigCustomerNotLinkedStream(String domainName, Integer domainId, String login, CustomerFilter filter, int ofs, int limit){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getSigCustomerNotLinkedStream(ctx, filter, ofs, limit);
		} 
	}
	
	public static LinkedList<Customer> getCustomerList(String domainName, Integer domainId, String login, CustomerFilter filter){
		return getCustomerStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Customer getCustomer(AONContext ctx, CustomerFilter filter) {
		return getCustomerStream(ctx, filter)
				.findFirst().orElse(new Customer());
	}
	
	public static Customer getCustomer(String domainName, Integer domainId, String login, CustomerFilter filter){
		return getCustomerStream(domainName, domainId, login, filter)
				.findFirst().orElse(new Customer());
	}
	
	public static Customer getCustomer(String domainName, Integer domainId, String login, Integer registry){
		return getCustomer(domainName, domainId, login, f -> f.getRegistryProperty().eq(registry));
	}
	
	/**
	 * @deprecated  Replaced by AON.saveCustomer
	 */
	public static Customer insertCustomer(String domainName, Integer domainId, String login, Customer customer) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().insertCustomer(ctx, customer);
		}
	}
	
	public static Customer saveCustomer(String domainName, Integer domainId, String login, Customer customer) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().saveCustomer(ctx, customer);
		}
	}
	
	// ------------------- SELLER
	
	public static Stream<Seller> getSellerStream(String domainName, Integer domainId, String login, SellerFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getSellerStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<Seller> getSellerStream(String domainName, Integer domainId, String login, SellerFilter filter, int offset, int limit){
		CloseableAONContext ctx = null;
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
	
	public static List<Seller> getSellerList(SellerParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellerList(ctx, params);
		}
	}
	
	public static Integer getSellerListCount(SellerParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellerListCount(ctx, params);
		}
	}

	public static Seller saveSeller(String domainName, int domain, String user, Seller seller) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().saveSeller(ctx, seller);
		}
	}

	public static void deleteSeller(String domainName, int domain, String user, Integer sellerId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getRegistry().deleteSeller(ctx, sellerId);
		}
	}
	
	// ------------------- SELLER WORKLOAD
	
	public static List<SellerWorkload> getSellerWorkloadList(SellerWorkloadParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellerWorkloadList(ctx, params);
		}
	}
	
	public static Integer getSellerWorkloadListCount(SellerWorkloadParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellerWorkloadListCount(ctx, params);
		}
	}
	
	public static SellerWorkloadContent getSellersWorkloadContent(SellerWorkloadParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellersWorkloadContent(ctx, params);
		}
	}
	
	public static List<Integer> getSellersWorkloadFeesIds(SellerWorkloadParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellersWorkloadFeesIds(ctx, params);
		}
	}
	
	public static List<Integer> getSellersWorkloadInvoiceIds(SellerWorkloadParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellersWorkloadInvoiceIds(ctx, params);
		}
	}
	
	public static List<Integer> getSellersWorkloadInvoicesIds(SellerWorkloadParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getSellersWorkloadFeesIds(ctx, params);
		}
	}
	
	// ------------------- RSELLER
	public static RegistrySeller getRegistrySeller(Domain domain, String login, RegistrySellerFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().getRegistrySeller(ctx, filter);
		}
	}
	
	public static Stream<RegistrySeller> getRegistrySellerStream(Domain domain, String login, RegistrySellerFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().getRegistrySellerStream(ctx, filter);			
		}
	}

	public static Stream<RegistrySeller> getRegistrySellerStream(Domain domain, String login, RegistrySellerFilter filter, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().getRegistrySellerStream(ctx, filter, offset, limit);			
		}
	}
	
	public static RegistrySeller saveRegistrySeller(Domain domain, String login, RegistrySeller registrySeller) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().saveRegistrySeller(ctx, registrySeller);
		}
	}
	
	public static int deleteRegistrySeller(Domain domain, String login, RegistrySellerFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().deleteRegistrySeller(ctx, filter);
		}
	}
	
	
	// ------------------------------------- CATEGORY
	/**
	 * @deprecated  Replaced by AON_SOLUTIONS.getCategory
	 */
	public static Category getCategory(String domainName, Integer domainId,
			String login, Integer categoryId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCategory(ctx, categoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/**
	 * @deprecated  Replaced by AON_SOLUTIONS.getCategoryStream
	 */
	public static Stream<Category> getCategoryStream(String domainName, Integer domainId, String login, CategoryFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCategoryStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/**
	 * @deprecated  Replaced by AON_SOLUTIONS.saveCategory
	 */
	public static Category insertCategory(String domainName, Integer domainId, String login, Category category ) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().insertCategory(ctx, category);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/**
	 * @deprecated  Replaced by AON_SOLUTIONS.saveCategory
	 */
	public static Category updateCategory(String domainName, Integer domainId, String login, Category category ) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().updateCategory(ctx, category);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	

	/**
	 * @deprecated  Replaced by AON_SOLUTIONS.deleteCategory
	 */
	public static Category deleteCategory(String domainName, Integer domainId, String login, Integer categoryId ) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().deleteCategory(ctx, categoryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	/**
	 * @deprecated  Replaced by AON_SOLUTIONS.getCategoryStream
	 */
	public static LinkedList<Category> getCategoryList(String domainName,
			Integer domainId, String login) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getCategoryList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	

	// ------------------------------------- RBANK
	
	/**
	 * @deprecated  Replaced by AON.getRegistryBankStream
	 */
	public static Stream<RegistryBank> getRBankStream(String domainName, Integer domainId, String login, RegistryBankFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRBankStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
		
	/**
	 * @deprecated  Replaced by AON.getRegistryBank
	 */
	public static RegistryBank getRBank(String domainName, Integer domainId, String login, RegistryBankFilter filter) {
		return getRBankStream(domainName, domainId, login, filter)
			.findFirst().orElse(new RegistryBank());
	}
		
	public static LinkedList<RegistryBank> getRBankList(String domainName, Integer domainId, String login, RegistryBankFilter filter) {
		return getRBankStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	/**
	 * @deprecated  Replaced by AON.saveRegistryBank
	 */
	public static RegistryBank insertRBank(String domainName, Integer domainId, String login, RegistryBank rbank) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().insertRBank(ctx, rbank);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/**
	 * @deprecated  Replaced by AON.deleteRegistryBank
	 */
	public static void deleteRBank(String domainName, Integer domainId, String login, Integer id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getRegistry().deleteRBank(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static RegistryBank getRegistryBank(Occam occam, RegistryBankFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getRegistry().getRegistryBank(ctx, filter);
		}
	}
	public static RegistryBank getRegistryBank(Domain domain, String login, RegistryBankFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().getRegistryBank(ctx, filter);
		}
	}
	
	public static Stream<RegistryBank> getRegistryBankStream(Occam occam, RegistryBankFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getRegistry().getRegistryBankStream(ctx, filter);
		}
	}

	public static Stream<RegistryBank> getRegistryBankStream(Domain domain, String login, RegistryBankFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().getRegistryBankStream(ctx, filter);
		}
	}
	
	public static RegistryBank saveRegistryBank(Occam occam, RegistryBank rbank) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getRegistry().saveRegistryBank(ctx, rbank);
		}
	}
	
	public static RegistryBank saveRegistryBank(Domain domain, String login, RegistryBank rbank) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			return getRegistry().saveRegistryBank(ctx, rbank);
		}
	}
	
	public static void deleteRegistryBank(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, login)) {
			getRegistry().deleteRegistryBank(ctx, id);
		}
	}
	
	// ------------------------------------- RPAYMETHOD
	
	/**
	 * @deprecated  Replaced by AON.getRegistryPayMethodStream
	 */
	public static Stream<RegistryPayMethod> getRPayMethodStream(String domainName, Integer domainId, String login, RegistryPayMethodFilter filter) {
		return getRegistryPayMethodStream(domainName, domainId, login, filter);
	}
		
	/**
	 * @deprecated  Replaced by AON.getRegistryPayMethod
	 */
	public static RegistryPayMethod getRPayMethod(String domainName, Integer domainId, String login, RegistryPayMethodFilter filter) {
		return getRegistryPayMethod(domainName, domainId, login, filter);
	}
	
	/**
	 * @deprecated  Replaced by AON.saveRegistryPayMethod
	 */
	public static RegistryPayMethod insertRPayMethod(String domainName, Integer domainId, String login, RegistryPayMethod rpaymethod) {
		return saveRegistryPayMethod(domainName, domainId, login, rpaymethod);
	}
	
	public static RegistryPayMethod getRegistryPayMethod(Domain domain, User user, RegistryPayMethodFilter filter, Options...options) {
		return getRegistryPayMethod(domain.getName(), domain.getId(), user.getLogin(), filter, options);
	}
	
	public static RegistryPayMethod getRegistryPayMethod(Domain domain, String login, RegistryPayMethodFilter filter, Options...options) {
		return getRegistryPayMethod(domain.getName(), domain.getId(), login, filter, options);
	}
	
	public static RegistryPayMethod getRegistryPayMethod(String domainName, Integer domainId, String login, RegistryPayMethodFilter filter, Options...options) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRegistryPayMethod(ctx, filter, options);
		}
	}
	
	public static Stream<RegistryPayMethod> getRegistryPayMethodStream(String domainName, Integer domainId, String login, RegistryPayMethodFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRegistryPayMethodStream(ctx, filter);
		}
	}
	
	public static RegistryPayMethod saveRegistryPayMethod(Domain domain, User user, RegistryPayMethod registryPayMethod) {
		return saveRegistryPayMethod(domain.getName(), domain.getId(), user.getLogin(), registryPayMethod);
	}
	
	public static RegistryPayMethod saveRegistryPayMethod(Domain domain, String login, RegistryPayMethod registryPayMethod) {
		return saveRegistryPayMethod(domain.getName(), domain.getId(), login, registryPayMethod);
	}
	
	public static RegistryPayMethod saveRegistryPayMethod(String domainName, Integer domainId, String login, RegistryPayMethod registryPayMethod) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().saveRegistryPayMethod(ctx, registryPayMethod);
		}
	}
	
	public static void deleteRegistryPayMethod(String domainName, Integer domainId, String login, Integer id) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getRegistry().deleteRegistryPayMethod(ctx, id);
		}
	}
	
	// ------------------------------------- RMEDIA
	
	
	// REGISTRY MEDIA - GET STREAM
	
	/**
	 * @deprecated  Replaced by AON.getStream
	 */
	@Deprecated(forRemoval = true )
	public static Stream<RegistryMedia> getRMediaStream(String domainName, Integer domainId, String login,
			RegistryMediaFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRMediaStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<RegistryMedia> getRegistryMediaStream(Domain domain, User user, RegistryMediaFilter filter) {
		return getStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<RegistryMedia> getStream(Domain domain, User user, RegistryMediaFilter filter) {
		return getStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<RegistryMedia> getStream(Domain domain, String login, RegistryMediaFilter filter) {
		return getStream(domain.getName(), domain.getId(), login, filter);
	}
	
	public static Stream<RegistryMedia> getStream(String domainName, Integer domainId, String login, RegistryMediaFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getStream(ctx, filter);
		}
	}
	
	// REGISTRY MEDIA - GET
		
	/**
	 * @deprecated  Replaced by AON.get
	 */
	@Deprecated(forRemoval = true )
	public static RegistryMedia getRMedia(String domainName, Integer domainId, String login,
			RegistryMediaFilter filter) {
		return getRMediaStream(domainName, domainId, login, filter)
			.findFirst().orElse(new RegistryMedia());
	}
	
	/**
	 * @deprecated  Replaced by AON.get
	 */
	@Deprecated(forRemoval = true )
	public static LinkedList<RegistryMedia> getRMediaList(String domainName, Integer domainId, String login,
			RegistryMediaFilter filter) {
		return getRMediaStream(domainName, domainId, login, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static RegistryMedia getRegistryMedia(Domain domain, User user, RegistryMediaFilter filter) {
		return get(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static RegistryMedia get(Domain domain, User user, RegistryMediaFilter filter) {
		return get(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static RegistryMedia get(Domain domain, String login, RegistryMediaFilter filter) {
		return get(domain.getName(), domain.getId(), login, filter);
	}
	
	public static RegistryMedia get(String domainName, Integer domainId, String login, RegistryMediaFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().get(ctx, filter);
		}
	}

	
	// REGISTRY MEDIA - SAVE

	/**
	 * @deprecated  Replaced by AON.save
	 */
	@Deprecated(forRemoval = true )
	public static RegistryMedia insertRMedia(String domainName, Integer domainId, String login, RegistryMedia rmedia) {
		return save(domainName, domainId, login, rmedia);
	}
	
	/**
	 * @deprecated  Replaced by AON.save
	 */
	@Deprecated(forRemoval = true )
	public static RegistryMedia updateRMedia(String domainName, Integer domainId, String login, RegistryMedia rmedia) {
		return save(domainName, domainId, login, rmedia);
	}
	
	public static RegistryMedia save(Domain domain, User user, RegistryMedia media) {
		return save(domain.getName(), domain.getId(), user.getLogin(), media);
	}
	
	public static RegistryMedia save(Domain domain, String login, RegistryMedia media) {
		return save(domain.getName(), domain.getId(), login, media);
	}
	
	public static RegistryMedia save(String domainName, Integer domainId, String login, RegistryMedia media) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().save(ctx, media);
		}
	}

	// REGISTRY MEDIA - DELETE
	
	public static RegistryMedia deleteRMedia(String domainName, Integer domainId, String login, Integer registry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().deleteRMedia(ctx, registry);
		}
	}
	
	public static void deleteRegistryMedia(String domainName, Integer domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			getRegistry().deleteRegistryMedia(ctx, id);
		}
	}
	
	// ------------------------------------- RNOTE

	// ----- RNOTE GET
	
	public static RegistryNote getRegistryNote(Domain domain, User user, RegistryNoteFilter filter) {
		return getRegistryNote(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static RegistryNote getRegistryNote(Domain domain, String login, RegistryNoteFilter filter) {
		return getRegistryNote(domain.getName(),  domain.getId(), login, filter);
	}

	public static RegistryNote getRegistryNote(String domainName, Integer domainId, String login, RegistryNoteFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRegistryNote(ctx, filter);
		}
	}

	// ----- RNOTE GET STREAM

	public static Stream<RegistryNote> getRegistryNoteStream(Domain domain, User user, RegistryNoteFilter filter) {
		return getRegistryNoteStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<RegistryNote> getRegistryNoteStream(Domain domain, String login, RegistryNoteFilter filter) {
		return getRegistryNoteStream(domain.getName(), domain.getId(), login, filter);
	}
	
	public static Stream<RegistryNote> getRegistryNoteStream(String domainName, Integer domainId, String login, RegistryNoteFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRegistryNoteStream(ctx, filter);
		}
	}

	// ----- RNOTE GET LIST

	public static List<RegistryNote> getRegistryNoteList(Domain domain, User user, RegistryNoteFilter filter) {
		return getRegistryNoteList(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static List<RegistryNote> getRegistryNoteList(Domain domain, String login, RegistryNoteFilter filter) {
		return getRegistryNoteList(domain.getName(),  domain.getId(), login, filter);
	}

	public static List<RegistryNote> getRegistryNoteList(String domainName, Integer domainId, String login, RegistryNoteFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRegistryNoteList(ctx, filter);
		}
	}
	
	// ----- RNOTE SAVE
	
	public static RegistryNote saveRegistryNote(Domain domain, User user, RegistryNote rnote) {
		return saveRegistryNote(domain.getName(), domain.getId(), user.getLogin(), rnote);
	}
	
	public static RegistryNote saveRegistryNote(Domain domain, String login, RegistryNote rnote) {
		return saveRegistryNote(domain.getName(),  domain.getId(),  login, rnote);
	}

	public static RegistryNote saveRegistryNote(String domainName, Integer domainId, String login, RegistryNote rnote) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().saveRegistryNote(ctx, rnote);
		}
	}

	// ----- RNOTE DELETE
	
	public static void deleteRegistryNote(Domain domain, User user, Integer id) {
		deleteRegistryNote(domain.getName(), domain.getId(), user.getLogin(), id);
	}
	
	public static void deleteRegistryNote(Domain domain, String login, Integer id) {
		deleteRegistryNote(domain.getName(), domain.getId(), login, id);
	}
	
	public static void deleteRegistryNote(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getRegistry().deleteRegistryNote(ctx, id);
		}
	}
	
	// ------------------------------------- RITEM
	
	public static Stream<RegistryItem> getRItemStream(String domainName, Integer domainId, String login, RegistryItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<RegistryItem> getRItemList(String domainName, Integer domainId, String login, RegistryItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<RegistryItem> getRItemList(String domainName, Integer domainId, String login, RegistryItemFilter filter, int limit, int offset) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter, limit, offset)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static RegistryItem getRItem(String domainName, Integer domainId, String login, RegistryItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRItemStream(ctx, filter)
				.findFirst().orElse(new RegistryItem());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	//-------------------SEGMENT--------------------

	public static RegistrySegment saveRegistrySegment(Domain domain, User user, RegistrySegment rsegment){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()) ){
			return getRegistry().saveRegistrySegment(ctx, rsegment);
		}
	}

	public static Stream<RegistrySegment> getRegistrySegmentStream(Domain domain, User user, RegistrySegmentFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()) ){
			return getRegistry().getRegistrySegmentStream(ctx, filter);
		}
	}

	public static Stream<Segment> getRSegmentStream(Domain domain, User user, Integer registryId){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()) ){
			return getRegistry().getRSegmentStream(ctx, registryId);
		}
	}
	
	public static Stream<Segment> getSegmentStream(Domain domain, User user, SegmentFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()) ){
			return getRegistry().getSegmentStream(ctx, filter);
		}
	}
	
	public static Stream<Segment> getRSegmentStream(String domainName, Integer domainId, String login,
			Integer registryId){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRSegmentStream(ctx, registryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}

	public static Integer[] getRSegmentStream(String domainName, Integer domainId, String login, RegistrySegmentFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRSegmentStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}
	

//
//	public static Integer[] getRSegmentStream(String domainName, Integer domainId, String login, RegistrySegmentFilter filter){
//		CloseableAONContext ctx = null;
//		try {
//			ctx = AONContext.getAONContext(domainName, domainId, login);
//			return getRegistry().getRSegmentStream(ctx, filter);
//		} finally {
//			if (ctx != null)
//				ctx.close();
//		}		
//	}
		
	
	public static Stream<Seller> getRSellerStream(String domainName, Integer domainId, String login, Integer registryId){
		return getRSellerStream(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId).and(f.getRegistryProperty().eq(registryId)));
	}
	
	public static Stream<Seller> getRSellerStream(String domainName, Integer domainId, String login, RegistrySellerFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRSellerStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}
	
	// ------------------- REGISTRY ADDRESS
	
	public static Stream<RAddress> getRAddressStream(String domainName, Integer domainId, String login, RegistryAddressFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRAddressStream(ctx, filter);
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
	
	public static RAddress insertRAddress(String domainName, Integer domainId, String login, RAddress raddress) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().insertRAddress(ctx, raddress);
		}
 	}

	public static RegistryAddress getRegistryAddress(Domain domain, User user, RegistryAddressFilter filter) {
		return get(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static RegistryAddress get(Domain domain, User user, RegistryAddressFilter filter) {
		return get(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static RegistryAddress get(Domain domain, String login, RegistryAddressFilter filter) {
		return get(domain.getName(), domain.getId(), login, filter);
	}
	
	public static RegistryAddress get(String domainName, Integer domainId, String login, RegistryAddressFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().get(ctx, filter);
		}
	}
	
	public static RegistryAddress getMain(Domain domain, User user, Integer registry) {
		return getMain(domain.getName(), domain.getId(), user.getLogin(), registry);
	}
	
	public static RegistryAddress getMain(Domain domain, String login, Integer registry) {
		return getMain(domain.getName(), domain.getId(), login, registry);
	}
	
	public static RegistryAddress getMain(String domainName, Integer domainId, String login, Integer registry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getMain(ctx, registry);
		}
	}
	
	public static Stream<RegistryAddress> getStream(Domain domain, User user, RegistryAddressFilter filter) {
		return getStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<RegistryAddress> getRegistryAddressStream(Domain domain, User user, RegistryAddressFilter filter) {
		return getStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<RegistryAddress> getStream(Domain domain, String login, RegistryAddressFilter filter) {
		return getStream(domain.getName(), domain.getId(), login, filter);
	}
	
	public static Stream<RegistryAddress> getStream(String domainName, Integer domainId, String login, RegistryAddressFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getStream(ctx, filter);
		}
	}
	
	public static RegistryAddress save(Domain domain, User user, RegistryAddress address) {
		return save(domain.getName(), domain.getId(), user.getLogin(), address);
	}
	
	public static RegistryAddress save(Domain domain, String login, RegistryAddress address) {
		return save(domain.getName(), domain.getId(), login, address);
	}
	
	public static RegistryAddress save(String domainName, Integer domainId, String login, RegistryAddress address) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().save(ctx, address);
		}
	}
	
	public static void deleteRegistryAddress(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getRegistry().deleteRegistryAddress(ctx, id);
		}
	}
	
	// ------------------- RECORD DATA

	@Deprecated(forRemoval = true )
	public static Stream<RecordData> getRecordDataStream(String domainName, Integer domainId, String login, RecordDataFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getRecordDataStream(ctx, filter);
		}
	}

	@Deprecated(forRemoval = true )
	public static RecordData getRecordData(String domainName, Integer domainId, String login, RecordDataFilter filter) {
		return getRecordDataStream(domainName, domainId, login, filter)
			.findFirst().orElse(new RecordData());
	}

	public static Stream<RecordData> getRecordDataStream(Occam occam, RecordDataType type, Integer registryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRegistry().getRecordDataStream(ctx, type, registryId);
		}
	}
	
	public static Stream<RecordData> getRecordDataStream(Occam occam, Integer registryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRegistry().getRecordDataStream(ctx, registryId);
		}
	}
	
	public static RecordData getRecordData(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRegistry().getRecordData(ctx, id);
		}
	}

	public static RecordData saveRecordData(Occam occam, RecordData recordData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRegistry().saveRecordData(ctx, recordData);
		}
	}

	public static void deleteRecordData(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			getRegistry().deleteRecordData(ctx, id);
		}
	}
	
	
	// ********************************************
	// ******************************** CREDITOR **
	// ********************************************

	public static Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter) {
		return getRegistry().getCreditorStream(ctx, filter);
	}
	public static Stream<Creditor> getCreditorStream(String domainName, Integer domainId, String login, CreditorFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCreditorStream(ctx, filter);
		}
	}
	
	public static Stream<Creditor> getCreditorStream(String domainName, Integer domainId, String login, CreditorFilter filter, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().getCreditorStream(ctx, filter, offset, limit);
		}
	}
	
	public static LinkedList<Creditor> getCreditorList(String domainName, Integer domainId, String login, CreditorFilter filter) {
		return getCreditorStream(domainName, domainId, login, filter)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Optional<Creditor> getCreditor(AONContext ctx, CreditorFilter filter) {
		return getCreditorStream(ctx, filter)
				.findFirst();
	}
	public static Optional<Creditor> getCreditor(String domainName, Integer domainId, String login, CreditorFilter filter) {
		return getCreditorStream(domainName, domainId, login, filter)
				.findFirst();
	}
	
	
	public static Creditor saveCreditor(String domainName, Integer domainId, String login, Creditor creditor) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().saveCreditor(ctx, creditor);
		}
	}
	
	/**
	 * @deprecated  Replaced by AON.saveCreditor
	 */
	@Deprecated(forRemoval = true )
	public static Creditor insertCreditor(String domainName, Integer domainId, String login, Creditor creditor) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getRegistry().insertCreditor(ctx, creditor);
		} 
	}
	
	public static Stream<Creditor> getBasicCreditors(Occam occam, CreditorFilter filter) {
		try ( CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getRegistry().getBasicCreditors(ctx, filter);
		}
	}

	// ********************************************
	// ****************************** Commercial **
	// ********************************************

	// -------------------- COMMERCIAL TRACKING

	public static CommercialTracking getCommercialTracking(String domainName,
			Integer domainId, String login, CommercialTrackingFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().getCommercialTracking(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static CommercialTracking save(String domainName, Integer domainId, String login, CommercialTracking commercialTracking) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().save(ctx, commercialTracking);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static CommercialActivity save(String domainName, Integer domainId, String login, CommercialActivity commercialActivity) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommercial().save(ctx, commercialActivity);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<CommercialTracking> getCommercialTrackingStream(String domainName, Integer domainId, String login,	CommercialTrackingFilter filter) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getMarketplace().getMarketplaceTagList(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static MailAccount getMailAccount(Occam occam, MailAccountFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getSecurity().getMailAccount(ctx, filter);
		}
	}

	public static LinkedList<MailAccount> getMailAccountList(String domainName,
			Integer domainId, String login, MailAccountFilter filter) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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

	public static Stream<Tax> getVatStream(Occam occam, Integer domainId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getVatStream(ctx,domainId);
		}
	}
	
	public static Stream<Tax> getWithholdingStream(Occam occam, Integer domainId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getWithholdingStream(ctx,domainId);
		}
	}
	
	public static Stream<Tax> getTaxStream(Occam occam, Integer domainId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getTaxStream(ctx, domainId);
		}
	}

	public static Stream<Tax> getTaxStream(Occam occam, Integer domainId, TaxFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getTaxStream(ctx, domainId, filter);
		}
	}
	
	public static Optional<Tax> getTax(Occam occam, Integer domainId, Integer id){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getCommon().getTax(ctx, domainId, id);
		}
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
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getTagStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Tag> getTagList(TagParams params){
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getCommon().getTagList(ctx, params);
		}
	}

	public static Tag saveTag(Occam occam, Tag tag){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().saveTag(ctx, tag);
		}
	}
	
	public static Tag saveTag(String domainName, Integer domainId, String login, Tag tag){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().saveTag(ctx, tag);
		}
	}
	
	@Deprecated(forRemoval = true )
	public static Tag insertTag(String domainName, Integer domainId, String login, Tag tag) {
		return saveTag(domainName, domainId, login, tag);
	}

	@Deprecated(forRemoval = true )
	public static Tag updateTag(String domainName, Integer domainId, String login, Tag tag) {
		return saveTag(domainName, domainId, login, tag);
	}

	public static void deleteTag(String domainName, Integer domainId, String login, Tag tag) {
		deleteTag(domainName, domainId, login, f -> f.getIdProperty().eq(tag.getId()));
	}
	
	public static void deleteTag(String domainName, Integer domainId, String login, TagFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getCommon().deleteTag(ctx, filter);
		}
	}
	
	//-------------------- TASK
	
	public static Boolean isTaskParent(String domainName, Integer domainId, String login, Integer parentId){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().isTaskParent(ctx, parentId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static OldTask getTask(String domainName, Integer domainId, String login, TaskFilter filter){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTask(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<OldTask> getTaskStream(String domainName, Integer domainId, String login, TaskFilter filter){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskStream(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<OldTask> getTaskStream(String domainName, Integer domainId, String login, TaskFilter filter,  IssueFilter issueFilter){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskStream(ctx, filter, issueFilter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<OldTask> getDuplicateTaskStream(String domainName, Integer domainId, String login, Integer parent){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getDuplicateTaskStream(ctx, parent);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Integer[] getTaskCount(String domainName, Integer domainId, String login, TaskFilter filter,  IssueFilter issueFilter){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskCount(ctx, filter, issueFilter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<OldTask> getTaskList(String domainName, Integer domainId, String login, TaskFilter filter, IssueFilter issueFilter){
		return getTaskStream(domainName, domainId, login, filter, issueFilter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<Tag> getTaskLabelStream(String domainName, Integer domainId, String login, TaskTagFilter filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getLastTaskNumber(ctx);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static OldTask createTask(String domainName, Integer domainId, String login, OldTask task){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return task.setId(getTask().createTask(ctx, task));
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTask(String domainName, Integer domainId, String login, TaskFilter filter){
		CloseableAONContext ctx = null;	
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTask(ctx, filter);
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	public static OldTask updateTask(String domainName, Integer domainId, String login, OldTask task){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateTask(ctx, task);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	//-------------------- TASK COMMENT
	
	public static Stream<TaskComment> getTaskCommentStream(String domainName, Integer domainId, String login, TaskCommentFilter filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getCommentsCount(ctx, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskComment getLastTaskComment(String domainName, Integer domainId, String login, Integer taskId) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().createTaskComment(ctx, taskComment, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskComment updateTaskComment(String domainName, Integer domainId, String login, TaskComment taskComment) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateTaskComment(ctx, taskComment);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskComment(String domainName, Integer domainId, String login, TaskCommentFilter filter){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskComment(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	//-------------------- TASK EVENT
	
	public static TaskEvent getTaskEvent(String domainName, Integer domainId, String login, Integer taskEventId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEvent(ctx, taskEventId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent getTaskEvent(String domainName, Integer domainId, String login, TaskEventFilter filter) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEvent(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent getLastTaskEvent(String domainName, Integer domainId, String login, TaskEventFilter filter) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getLastTaskEvent(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent createTaskEvent(String domainName, Integer domainId, String login, TaskEvent taskEvent, Integer taskId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().createTaskEvent(ctx, taskEvent, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskEvent(String domainName, Integer domainId, String login, TaskEventFilter filter) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskEvent(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static TaskEvent updateTaskEvent(String domainName, Integer domainId, String login, TaskEvent taskEvent, Integer taskCommentId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateTaskEvent(ctx, taskEvent, taskCommentId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<TaskEvent> getTaskEventStream(String domainName, Integer domainId, String login, Integer taskId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEventStream(ctx, taskId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<TaskEvent> getTaskEventList(String domainName, Integer domainId, String login, Integer taskEventId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskEventStream(ctx, taskEventId)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<TaskHolder> getTaskMemberWStream(String domainName, Integer domainId, String login, String filter, Integer workgroupId){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getTaskMemberWStream(ctx, filter, workgroupId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}

	public static Stream<Workgroup> getTaskWorkgroupStream(String domainName, Integer domainId, String login, String filter){
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, taskId, TagType.TASK_TYPE);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deletePriorityTaskTag(String domainName, Integer domainId, String login, Integer taskId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, taskId, TagType.TASK_PRIORITY);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteLabelTaskTag(String domainName, Integer domainId, String login, Integer taskId) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, taskId, TagType.TASK_LABEL);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void createTaskTag(String domainName, Integer domainId, String login, TaskTag taskTag) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().createTaskTag(ctx, taskTag);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskTag(String domainName, Integer domainId, String login, TaskTagFilter filter) {
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().deleteTaskTag(ctx, filter);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	
	public static Stream<Customer> getFilterCustomerStream(String domainName, Integer domainId, String login, String filter){
		CloseableAONContext ctx = null;
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

	// ----- Get Task Holder
	
	public static TaskHolder getTaskHolder(Domain domain, User user, TaskHolderFilter filter){
	    return getTaskHolder(domain.getName(),  domain.getId(), user.getLogin(), filter);
	}
	
	public static TaskHolder getTaskHolder(Domain domain, String login, TaskHolderFilter filter){
	    return getTaskHolder(domain.getName(),  domain.getId(), login, filter);
	}
	
	public static TaskHolder getTaskHolder(String domainName, Integer domainId, String login, TaskHolderFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getTask().getTaskHolder(ctx, filter);
		} 
	}
	
	public static List<Seller> getTaskHolderSellerStream(Domain domain, String user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user)) {
			return getTask().getTaskHolderSellerStream(ctx, domain.getId());
		} 
	}
	
	// ----- Get Task Holder Stream
	
	public static Stream<TaskHolder> getTaskHolderStream(Domain domain, User user, TaskHolderFilter filter, Options...options){
		return getTaskHolderStream(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<TaskHolder> getTaskHolderStream(Domain domain, String login, TaskHolderFilter filter, Options...options){
		return getTaskHolderStream(domain.getName(), domain.getId(), login, filter);
	}

	public static Stream<TaskHolder> getTaskHolderStream(String domainName, Integer domainId, String login, TaskHolderFilter filter, Options...options){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getTask().getTaskHolderStream(ctx, filter, options);
		}
	}
	
	public static List<TaskHolder> getTaskHolderList(TaskHolderParams params){
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return new ArrayList<>(getTask().getTaskHolderList(ctx, params));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<TaskHolder>();
		}
	}
	
	public static Integer getTaskHoldersCount(TaskHolderParams params){
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getTask().getTaskHoldersCount(ctx, params);
		}
	}
	
	// -----
	
	public static Stream<TaskHolder> getTaskHolderWorkgroupStream(Domain domain, User user, TaskHolderFilter filter, Integer workgroupId, int ofs, int limit){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask().getTaskHolderWorkgroupStream(ctx, filter, workgroupId, ofs, limit);
		}
	}
	
	public static List<TaskHolder> getAviableSellerTaskHolders(String domainName, Integer domainId, String login){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getTask().getAviableSellerTaskHolders(ctx);
		}
	}
	
	public static TaskHolder save(String domainName, Integer domainId, String login, TaskHolder taskHolder){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getTask().save(ctx, taskHolder);
		} 
	}
	
	public static TaskHolder updateTaskHolder(String domainName, Integer domainId, String login, TaskHolder taskHolder){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateTaskHolder(ctx, taskHolder);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void insertTaskHolder(String domainName, Integer domainId, String login, TaskHolder taskHolder){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().insertTaskHolder(ctx, taskHolder);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteTaskHolder(String domainName, Integer domainId, String login, Integer taskHolder){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getTask().deleteTaskHolder(ctx, taskHolder);
		}
	}
	
	public static Boolean isTaskHolderWorkgroup(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().isTaskHolderWorkgroup(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void insertTaskHolderWorkgroup(String domainName, Integer domainId, String login, Integer taskHolderId, Integer workgroupId){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getTask().insertTaskHolderWorkgroup(ctx, taskHolderId, workgroupId);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	// ---------- Delete TaskHolderWorkgroup
	
	public static void deleteTaskHolderWorkgroup(Domain domain, User user, TaskHolderWorkgroupFilter filter){
		deleteTaskHolderWorkgroup(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static void deleteTaskHolderWorkgroup(Domain domain, String login, TaskHolderWorkgroupFilter filter){
		deleteTaskHolderWorkgroup(domain.getName(), domain.getId(), login, filter);
	}
	
	public static void deleteTaskHolderWorkgroup(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		try (CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login) ) {
			getTask().deleteTaskHolderWorkgroup(ctx, filter);
		}
	}
	
	public static Stream<Workgroup> getTaskHolderWorkgroupStream(String domainName, Integer domainId, String login, TaskHolderWorkgroupFilter filter){
		CloseableAONContext ctx = null;
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
	
	public static void saveTaskHolderWorkgroups(Domain domain, User user, TaskHolder taskHolder) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			getTask().saveTaskHolderWorkgroups(ctx, taskHolder);
		} 
	}
	
	public static List<TaskHolderWorkgroup> getTaskHolderWorkgroupsList(Domain domain, User user, TaskHolderWorkgroupFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			return getTask().getTaskHolderWorkgroupsList(ctx, filter);
		} 
	}
	
	public static TaskHolderWorkgroup saveTaskHolderWorkgroup(Domain domain, User user, TaskHolderWorkgroup taskHolderWorkgroup) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			return getTask().saveTaskHolderWorkgroup(ctx, taskHolderWorkgroup);
		} 
	}
	
	// ------------------- USER WORKGROUP
	
	public static Stream<UserWorkgroup> getUserWorkgroupStream(String domainName, Integer domainId, String login, UserWorkgroupFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().getUserWorkgroupStream(ctx, filter);
		}

	}
	
	// ------------------- WORKGROUP
	
	public static Workgroup getWorkgroup(String domainName, Integer domainId, String login, WorkgroupFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getWorkgroup(ctx, filter);
		} 
	}
	
	public static Stream<Workgroup> getWorkgroupStream(String domainName, Integer domainId, String login, WorkgroupFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getWorkgroupStream(ctx, filter);
		} 
	}
	
	public static Stream<Workgroup> getWorkgroupByTaskHolderStream(String domainName, Integer domainId, String login, WorkgroupFilter filter, Integer taskHolder){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getWorkgroupByTaskHolderStream(ctx, filter, taskHolder);
		} 
	}
	
	public static List<Workgroup> getWorkgroupList(String domainName, Integer domainId, String login, WorkgroupFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getWorkgroupList(ctx, filter);
		} 
	}
	
	public static Workgroup saveWorkgroup(String domainName, Integer domainId, String login, Workgroup workgroup){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().saveWorkgroup(ctx, workgroup);
		} 
	}
	
	public static void deleteWorkgroup(String domainName, Integer domainId, String login, Integer id){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			 getCommon().deleteWorkgroup(ctx, id);
 		} 
	}
	
	@Deprecated
	public static Workgroup getWorkgroup(String domainName, Integer domainId, String login, Integer wId){
		CloseableAONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().getWorkgroup(ctx, wId);
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	@Deprecated
	public static Workgroup insertWorkgroup(String domainName, Integer domainId, String login, Workgroup workgroup){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().insertWorkgroup(ctx, workgroup);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	@Deprecated
	public static Workgroup updateWorkgroup(String domainName, Integer domainId, String login, Workgroup workgroup){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getTask().updateWorkgroup(ctx, workgroup);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static Stream<com.esferalia.aon.occam.api.model.registry.Question> getRegistryQuestionStream(String domainName, Integer domainId, String login, Integer registry){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistryQuestionStream(ctx, registry);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<RegistryProfile> getRegistryProfileStream(String domainName, Integer domainId, String login, Integer question, Integer registry){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getRegistry().getRegistryProfileStream(ctx, registry, question);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	// ---------- CERTIFICATES
	
		public static LinkedList<Certificate> getAEATCertificates(Occam occam) throws IllegalArgumentException {
			try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
				return getCommon().getAEATCertificates(ctx, occam.getDomain(), occam.getUser())
					.collect(Collectors.toCollection(LinkedList::new));
			}
		}
		
		public static Stream<Certificate> getCertificates(Domain domain, User user, CertificateFilter filter) {
			try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
				return getSecurity().getCertificates(ctx, filter);
			}
		}
		
		public static Certificate getCertificate(Domain domain, User user, String certificateType) {
			return getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), certificateType); 
		}	
		
		public static Certificate getCertificate(String domainName, Integer domainId, String login, Integer userId, String certificateType) {
			try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
				Certificate certificate =  getSecurity().getCertificate(ctx, userId, certificateType);
				if(null == certificate.getData())
					throw new CertificateNotFoundException();
				return certificate;
			} catch (Exception e) {
				throw new IllegalArgumentException("El certificado no se ha podido obtener. Revise que los certificados esten en vigor");
			}
		}
		
		public static Certificate getCertificate(String domainName, Integer domainId, String login, Integer userId) {
			CloseableAONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, login);
				Certificate certificate =  getSecurity().getCertificate(ctx, p -> p.getIdProperty().eq(userId));
				if(null == certificate.getData())
					throw new CertificateNotFoundException();
				return certificate;
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}


		public static Certificate insertCertificate(String domainName, Integer domainId, String login, Integer userId, Certificate certificate) {
			CloseableAONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, login);
				return getSecurity().insertCertificate(ctx, p -> p.getIdProperty().eq(userId), certificate);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static Certificate getCertificateSEPE(String domainName, Integer domainId, String login) throws CertificateNotFoundException {
			CloseableAONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, login);
				Certificate certificate =  getSecurity().getCertificateSEPE(ctx, domainId);
				if(null == certificate || null == certificate.getData())
					throw new CertificateNotFoundException();
				return certificate;
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
	
	// ------------------- CERTIFICATES
	
	public static List<Certificate> getCertificates(String domainName, Integer domainId, String login, Integer userId) throws IllegalArgumentException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getCertificates(ctx, domainId, userId);
		}
	}
	
	public static List<Certificate> getCertificatesWithParent(String domainName, Integer domainId, Integer parentDomainId, String login, Integer userId) throws IllegalArgumentException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getCertificatesWithParent(ctx, domainId, parentDomainId, userId);
		}
	}
	
	public static Certificate getCertificate(String domainName, Integer domainId, String login, AttachFilter attachFilter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getCertificate(ctx, attachFilter);
		}
	}
	
	public static CertificateInfo getCertificateInfo(String domainName, Integer domainId, String login, AttachFilter attachFilter) throws IllegalArgumentException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getCertificateInfo(ctx, attachFilter);
		}
	}
	
	public static CertificateInfo getCertificateInfo(byte[] data, String password) throws IllegalArgumentException {
		return getCommon().getCertificateInfo(data, password);
	}
	
	public static void deleteCertificate(String domainName, Integer domainId, String login, Integer attachId, AttachFilter attachFilter, RegistryAddInfoFilter raddinfoFilter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getCommon().deleteCertificate(ctx, attachId, attachFilter, raddinfoFilter);
		}
	}
	
	public static void saveCertificate(String domainName, Integer domainId, String login, Integer userId, Certificate certificate){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getCommon().saveCertificate(ctx, domainId, userId, certificate);
		}
	}
	
	// ---------- DATA REQUEST
	
	public static Stream<DataRequest> getDataRequestStream(String domainName, Integer domainId, String login, DataRequestFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getDataRequestStream(ctx, filter);
		}
	}
	
	public static DataRequest getDataRequest(String domainName, Integer domainId, String login, DataRequestFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getDataRequest(ctx, filter);
		}
	}
	
	public static DataRequest saveDataRequest(String domainName, Integer domainId, String login, DataRequest dataRequest){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().saveDataRequest(ctx, dataRequest);
		}
	}
	
	// ---------- DATA RESPONSE	
	
	public static Stream<DataResponse> getDataResponseStream(String domainName, Integer domainId, String login, DataResponseSource source, DataResponseFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseStream(ctx, source, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static DataResponse getDataResponse(String domainName, Integer domainId, String login, DataResponseFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getDataResponse(ctx, filter);
		}
	}
	
	@Deprecated
	public static DataResponse getDataResponse(String domainName, Integer domainId, String login, DataResponseSource source, DataResponseFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseStream(ctx, source, filter).findFirst().orElse(null);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponse insertDataResponse(String domainName, Integer domainId, String login, DataResponse dataResponse){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertDataResponse(ctx, dataResponse);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Integer updateDataResponse(String domainName, Integer domainId, String login, DataResponse dataResponse, DataResponseFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().updateDataResponse(ctx, dataResponse, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponse deleteDataResponse(String domainName, Integer domainId, String login, DataResponseFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().deleteDataResponse(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Stream<DataResponseDetail> getDataResponseDetailStream(String domainName, Integer domainId, String login, DataResponseDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseDetailStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static DataResponse getLastDataResponse(String domainName, Integer domainId, String login, DataResponseFilter filter){
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getCommon().getLastDataResponse(ctx, filter);
		}
	}
	
	public static Stream<DataResponseDetail> getLastDataResponseDetailStream(String domainName, Integer domainId, String login, DataResponseFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getLastDataResponseDetailStream(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static Optional<DataResponseDetail> getDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getDataResponseDetailStream(ctx, filter).findFirst();
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponseDetail insertDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetail dataResponseDetail){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().insertDataResponseDetail(ctx, dataResponseDetail);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponseDetail updateDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetail dataResponseDetail, DataResponseDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().updateDataResponseDetail(ctx, dataResponseDetail, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static DataResponseDetail deleteDataResponseDetail(String domainName, Integer domainId, String login, DataResponseDetailFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().deleteDataResponseDetail(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static Stream<OldProduct> getInvoiceProducts(String domainName, int domain, String loggedUser, ProductFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, loggedUser);
			return getFinance().getInvoiceProducts(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ------------------------------------- COMMISSION

	public static Stream<Commission> getCommissionStream(String domainName, Integer domainId, String login, CommissionFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getCommissionStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<CommissionType> getCommissionTypeStream(String domainName, Integer domainId, String login, CommissionTypeFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getCommissionTypeStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<CommissionTypeCommission> getCommissionTypeCommissionStream(String domainName, Integer domainId, String login, CommissionTypeCommissionFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getCommissionTypeCommissionStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<CommissionItem> getCommissionItemStream(String domainName, Integer domainId, String login, CommissionItemFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getCommissionItemStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<CommissionCategory> getCommissionCategoryStream(String domainName, Integer domainId, String login, CommissionCategoryFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getCommissionCategoryStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/*
	 * 		OFFER DETAIL COMMISSION
	 */
	
	public static Stream<OfferDetailCommission> getOfferDetailCommissionStream(String domainName, Integer domainId, String login, OfferDetailCommissionFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getOfferDetailCommissionStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static OfferDetailCommission getOfferDetailCommission(String domainName, Integer domainId, String login, OfferDetailCommissionFilter filter) {
		return getOfferDetailCommissionStream(domainName, domainId, login, filter).findFirst().orElse(null);
	}
	
	public static OfferDetailCommission insertOfferDetailCommission(String domainName, Integer domainId, String login, OfferDetailCommission odc) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().insertOfferDetailCommission(ctx, odc);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static OfferDetailCommission updateOfferDetailCommission(String domainName, Integer domainId, String login, OfferDetailCommission odc) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().updateOfferDetailCommission(ctx, odc);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteOfferDetailCommission(String domainName, Integer domainId, String login, Integer id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommission().deleteOfferDetailCommission(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/*
	 * 		INVOICE DETAIL COMMISSION
	 */
	
	public static Stream<InvoiceDetailCommission> getInvoiceDetailCommissionStream(String domainName, Integer domainId, String login, InvoiceDetailCommissionFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().getInvoiceDetailCommissionStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static InvoiceDetailCommission getInvoiceDetailCommission(String domainName, Integer domainId, String login, InvoiceDetailCommissionFilter filter) {
		return getInvoiceDetailCommissionStream(domainName, domainId, login, filter).findFirst().orElse(null);
	}
	
	public static InvoiceDetailCommission insertInvoiceDetailCommission(String domainName, Integer domainId, String login, InvoiceDetailCommission idc) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().insertInvoiceDetailCommission(ctx, idc);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static InvoiceDetailCommission updateInvoiceDetailCommission(String domainName, Integer domainId, String login, InvoiceDetailCommission idc) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommission().updateInvoiceDetailCommission(ctx, idc);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteInvoiceDetailCommission(String domainName, Integer domainId, String login, Integer id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getCommission().deleteInvoiceDetailCommission(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteInvoiceDetailCommission(String domainName, Integer domainId, String login, InvoiceDetailCommissionFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getCommission().deleteInvoiceDetailCommission(ctx, filter);
		}
	}
	
	public static void deleteInvoiceDetailCommission(String schema, InvoiceDetailCommissionFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)) {
			getCommission().deleteInvoiceDetailCommission(ctx, filter);
		}
	}
	
	/*
	 * 		MAIL TEMPLATE
	 */
	
	public static MailTemplate getMailTemplate(String domainName, Integer domainId, String login, MailTemplateFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getMailTemplateStream(ctx, filter).findFirst().orElse(new MailTemplate());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<MailTemplate> getMailTemplateStream(String domainName, Integer domainId, String login, MailTemplateFilter filter) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getMailTemplateStream(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/*
	 * UDAPA QUALITY
	 */
	
	public static Stream<UdapaQuality> getUdapaQualityStream(String domainName, Integer domainId, String login, Map<String, String[]> map){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getUdapaQualityStream(ctx, map);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	/*
	 * PATURPAT QUALITY
	 */
	
	public static Stream<PaturpatQuality> getPaturpatQualityStream(String domainName, Integer domainId, String login, Map<String, String[]> map){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getWarehouse().getPaturpatQualityStream(ctx, map);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Expedient> getResumeExpedientStream(String domainName, Integer domainId, String login, ProjectFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getExpedient().getResumeExpedientStream(ctx, domainId, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<Expedient> getFullExpedientStream(String domainName, Integer domainId, String login, ProjectFilter filter){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getExpedient().getFullExpedientStream(ctx, domainId, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<FinanceTracking> getFinanceTracking(String domainName, int domainId, String user,
			Integer finance) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getFinanceTracking(ctx, finance);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Finance> getFinancesForInvoice(String domainName, int domainId, String user, Invoice invoice) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getFinancesForInvoice(ctx, invoice);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Finance settleFinance(String domainName, int domainId, String user, Integer finance) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().settleFinance(ctx, finance);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Finance unSettleFinance(String domainName, int domainId, String user, Integer finance) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().unSettleFinance(ctx, finance);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Finance undoFinance(String domainName, int domainId, String user, Integer finance) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().undoFinance(ctx, finance);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FinanceTracking payFinance(String domainName, int domainId, String user, FinanceTracking finance) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().payFinance(ctx, finance);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FinanceTracking returnFinance(String domainName, int domainId, String user, FinanceTracking tracking) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().returnFinance(ctx, tracking);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteFinance(String domainName, int domainId, String user, Integer financeId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			getFinance().deleteFinance(ctx, financeId);
		}
	}	

	public static List<RegistryBank> getRegistryBanks(Domain domain, User user, Integer registry) {
		return getRegistryBanks(domain.getName(), domain.getId(), user.getLogin(), registry);
	}
	
	public static LinkedList<RegistryBank> getRegistryBanks(String domainName, int domainId, String user, Integer registry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, user)){
			return getFinance().getRegistryBanks(ctx, registry);
		}
	}

	public static LinkedList<RegistryBank> getCompanyRegistryBanks(String domainName, int domainId, String user) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFinance().getCompanyRegistryBanks(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<com.esferalia.aon.occam.api.model.Module> getDomainModules(String domainName, Integer domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().getDomainModules(ctx);
		}
	}

	// ------------------------------------- RAWDOC
	public static LinkedList<Rawdoc> getRawdocs(String domainName, int domain, String user, RawdocParams params) {
		return getRawdocStream(domainName, domain, user, p -> RawdocUtils.getFilter(p, params),0,Integer.MAX_VALUE)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	public static LinkedList<Rawdoc> getRawdocs(String domainName, int domain, String user, RawdocParams params, int offset, int limit) {
		return getRawdocStream(domainName, domain, user, p -> RawdocUtils.getFilter(p, params),offset,limit)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	public static LinkedList<Rawdoc> getRawdocs(Occam occam, RawdocParams params, int offset, int limit) {
		return getRawdocStream(occam, p -> RawdocUtils.getFilter(p, params),offset,limit)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	public static Stream<Rawdoc> getRawdocStream(String domainName, int domain, String user, RawdocFilter filter) {
		return getRawdocStream(domainName, domain, user, filter,0,Integer.MAX_VALUE); 
	}
	public static Stream<Rawdoc> getRawdocStream(Occam occam, RawdocFilter filter,int offset, int limit) {
		return getRawdocStream(occam.getDomainName(), occam.getDomain(), occam.getUser(), filter,offset,limit);
	}
	public static Stream<Rawdoc> getRawdocStream(String domainName, int domain, String user, RawdocFilter filter,int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().getRawdocStream(ctx, filter,offset,limit);
		} 
	}
	public static Stream<Rawdoc> getRawdocFullStream(String domainName, int domain, String user, RawdocFilter filter) {
		return getRawdocFullStream(domainName, domain, user, filter,0,Integer.MAX_VALUE); 
	}
	public static Stream<Rawdoc> getRawdocFullStream(String domainName, int domain, String user, RawdocFilter filter,int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().getRawdocFullStream(ctx, filter,offset,limit);
		}
	}
	public static Optional<Rawdoc> getRawdocFull(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRawdoc().getRawdocFull(ctx, id);
		}
	}
	public static Optional<Rawdoc> getRawdocFull(String domainName, int domain, String user, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().getRawdocFull(ctx, id);
		}
	}
	public static RawdocInvoiceCounter getRawdocInvoiceCounter(Domain domain, User user) {	
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getRawdoc().getRawdocInvoiceCounter(ctx);
		}
	}
	public static RawdocUserData getRawdocUserData(String domainName, Integer domainId, String login) {	
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRawdoc().getRawdocUserData(ctx, domainId);
		}
	}
	public static RawdocUserData getRawdocUserData(String token, String schema) {	
		AonToken aonToken = SECURITY.getAonToken(token);
		String domain = AONContext.getSchemaFirstDomain(schema);
		
		if(!AonStringUtils.isBlank(domain)) {
			try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")) {
				return getRawdoc().getRawdocUserData(ctx, aonToken.getAuth());
			}
		} 
		return new RawdocUserData();
	}

	public static InvoiceUserData getInvoiceUserData(String token, String schema) {	
		AonToken aonToken = SECURITY.getAonToken(token);
		String domain = AONContext.getSchemaFirstDomain(schema);
		
		if(!AonStringUtils.isBlank(domain)) {
			try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")) {
				return getFinance().getInvoiceUserData(ctx, aonToken.getAuth());
			}
		} 
		return new InvoiceUserData();
	}

	public static Rawdoc rawdocSave(Occam occam, Rawdoc rawdoc) {
		return rawdocSave(occam.getDomainName(), occam.getDomain(), occam.getUser(),rawdoc);
	}

	public static Rawdoc rawdocSave(String domainName, int domain, String user, Rawdoc rawdoc) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().rawdocSave(ctx, rawdoc);
		}	
	}
	public static Rawdoc rawdocSave(Occam occam, Integer rawdocId, String invoiceJson) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRawdoc().rawdocSave(ctx, rawdocId, invoiceJson);
		}	
	}
	
	public static void rawdocDelete(Occam occam, Integer rawdocId) {
		rawdocDelete(occam.getDomainName(), occam.getDomain(), occam.getUser(),rawdocId);
	}
	
	public static void rawdocDelete(String domainName, int domain, String user, Integer rawdocId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			getRawdoc().rawdocDelete(ctx, domain, rawdocId);
		}
	}
	
	public static void rawdocDelete(String domainName, int domain, String user, RawdocFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			getRawdoc().rawdocDelete(ctx, filter);
		}
	}
	
	public static Rawdoc rawdocAddLogComment(Occam occam, Integer rawdocId, String comment) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRawdoc().addLogComment(ctx, rawdocId, comment);
		}
	}
	
	public static Rawdoc rawdocRestore(Occam occam, Integer rawdocId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getRawdoc().restore(ctx, rawdocId);
		}
	}

	public static Rawdoc rawdocToTrash(Occam occam, Integer rawdocId) {
		return rawdocToTrash(occam.getDomainName(), occam.getDomain(), occam.getUser(),rawdocId);
	}
	public static Rawdoc rawdocToTrash(String domainName, int domain, String user, Integer rawdocId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().toTrash(ctx, rawdocId);
		}
	}

	public static Rawdoc rawdocToRejected(Occam occam, Integer rawdocId, String reason) {
		return rawdocToRejected(occam.getDomainName(), occam.getDomain(), occam.getUser(),rawdocId,reason);
	}
	public static Rawdoc rawdocToRejected(String domainName, int domain, String user, Integer rawdocId, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().toRejected(ctx, rawdocId, reason);
		}
	}

	public static Rawdoc rawdocToInbox(Occam occam, Integer rawdocId) {
		return rawdocToInbox(occam.getDomainName(), occam.getDomain(), occam.getUser(),rawdocId);
	}
	public static Rawdoc rawdocToInbox(String domainName, int domain, String user, Integer rawdocId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().toInbox(ctx, rawdocId);
		}
	}

	public static boolean rawdocHasData(String domainName, int domain, String user, Integer rawdocId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return getRawdoc().rawdocHasData(ctx, rawdocId);
		}
	}
	
	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	public static LinkedList<Customer> getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getCustomers(ctx, p -> RegistryUtils.getFilter(p, params), ofs, limit)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static CustomerFull getCustomerFull(String domainName, int domain, String user, Integer id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getCustomerFull(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static CustomerFull save(String domainName, int domain, String user, CustomerFull customerFull) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().save(ctx, customerFull);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Domain getDomainLinked(String domainName, int domain, String user, Integer customerId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getDomainLinked(ctx, customerId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}	
	
	public static void deleteCustomerFull(String domainName, int domain, String user, Integer id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			getRegistry().deleteCustomerFull(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static List<Domain> getDomainOfficeLinked(Domain domain, String login) {
		Company company = getCompanyForDomain(domain.getName(), domain.getId(), login);
		return getDomainOfficeLinked(company);
	}
	
	public static List<Domain> getDomainOfficeLinked(Company company) {
		LinkedList<Domain> list = new LinkedList<>();
		for(String schema: AONContext.getSchemas()) {
			String domainName = AONContext.getSchemaFirstDomain(schema);
			try (CloseableAONContext ctx = AONContext.getAONContext(domainName, 0, "")){
				List<Domain> offices = getRegistry().getDomainOfficeLinked(ctx, company.getDocument());
				list.addAll(offices);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		return list;
	}

	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	public static LinkedList<Creditor> getCreditors(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getCreditors(ctx, p -> RegistryUtils.getFilter(p, params), ofs, limit)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static CreditorFull getCreditorFull(String domainName, int domain, String user, Integer id){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getCreditorFull(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static CreditorFull save(String domainName, int domain, String user, CreditorFull creditorFull) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().save(ctx, creditorFull);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteCreditorFull(String domainName, int domain, String user, Integer id){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			getRegistry().deleteCreditorFull(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************
	public static LinkedList<Supplier> getSuppliers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getSuppliers(ctx, p -> RegistryUtils.getFilter(p, params), ofs, limit)
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static SupplierFull getSupplierFull(String domainName, int domain, String user, Integer id){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().getSupplierFull(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static SupplierFull save(String domainName, int domain, String user, SupplierFull supplierFull) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getRegistry().save(ctx, supplierFull);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static void deleteSupplierFull(String domainName, int domain, String user, Integer id){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			getRegistry().deleteSupplierFull(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************


	public static LinkedList<PayMethod> getPayMethods(String domainName, Integer domainId, String login) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getPayMethods(ctx);
		}
	}
	
	public static LinkedList<PayMethod> getPayMethods(PayMethodParams params) {
		try(CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getFinance().getPayMethods(ctx, params);
		}
	}
	
	public static PayMethod getPayMethod(String domainName, Integer domainId, String login, PayMethodFilter filter) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().getPayMethod(ctx, filter);
		}
	}
	
	public static PayMethod getPayMethod(String domainName, Integer domainId, String login, String name) {
		return getPayMethod(domainName, domainId, login, f -> f.getNameProperty().eq(name));
	}
	
	public static PayMethod savePayMethod(String domainName, Integer domainId, String login, PayMethod paymethod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().savePayMethod(ctx, paymethod);
		}
	}
	
	public static void deletePayMethod(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getFinance().deletePayMethod(ctx, id); 
		}
	}
	
	public static void groupPayMethod(String domainName, Integer domainId, String login, List<PayMethod> selectedPaymethodList, PayMethod groupedPaymthod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getFinance().groupPayMethod(ctx, selectedPaymethodList, groupedPaymthod); 
		}
	}

	// INVOFOX CONFIGURATION
	
	public static InvofoxConfiguration getInvofoxConfiguration(Domain domain, User user) {
		return getInvofoxConfiguration(domain.getName(), domain.getId(), user.getLogin());
	}
		
	public static InvofoxConfiguration getInvofoxConfiguration(Domain domain, String login) {
		return getInvofoxConfiguration(domain.getName(), domain.getId(), login);
	}
		
	public static InvofoxConfiguration getInvofoxConfiguration(String domainName, Integer domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getInvofoxConfiguration(ctx);
		}
	}
	
	public static InvofoxConfiguration getInvofoxConfiguration(AONContext ctx) {
		return getFinance().getInvofoxConfiguration(ctx);
	}

	public static InvofoxConfiguration saveInvofoxConfiguration(Domain domain, User user, InvofoxConfiguration config) {
		return saveInvofoxConfiguration(domain.getName(), domain.getId(), user.getLogin(), config);
	}
		
	public static InvofoxConfiguration saveInvofoxConfiguration(Domain domain, String login, InvofoxConfiguration config) {
		return saveInvofoxConfiguration(domain.getName(), domain.getId(), login, config);
	}
		
	public static InvofoxConfiguration saveInvofoxConfiguration(String domainName, Integer domainId, String login, InvofoxConfiguration config) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getFinance().saveInvofoxConfiguration(ctx, config);
		}
	}
	
	// INVOICE COMMUNICATION CONFIGURATION
	public static InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration(Occam occam) {
		return getInvoiceCommunicationConfiguration(occam, false);
	}
	
	public static InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration(Occam occam, boolean check) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceCommunicationConfiguration(ctx,occam.getDomain(), check);
		}
	}
	
	public static InvoiceCommunicationConfiguration saveInvoiceCommunicationConfiguration(Occam occam, int domainId, InvoiceCommunicationConfiguration config) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().saveInvoiceCommunicationConfiguration(ctx, domainId, config);
		}
	}
	
	// SII 
	public static void prepareNewSii(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getFinance().prepareNewSii(ctx);
		}
	}
	
	public static void prepareNewSii(Occam occam, Integer year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getFinance().prepareNewSii(ctx, year);
		}
	}

	// COMMUNICATION INVOICES 
	public static LinkedList<Invoice> getCommunicationInvoices(Occam occam, InvoiceCommunicationParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getCommunicationInvoices(ctx,params)
					.collect(Collectors.toCollection(LinkedList::new));
		}
		
	}
	
	
	// GEOZONE
	
	public static GeoZone get(String domainName, Integer domainId, String login, GeoZoneFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().get(ctx, filter);
		}
	}
	
	//--------------PERSON
	public static Person savePerson(Domain domain, String login, Person person) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getPerson().savePerson(ctx, person);
		}
	}

	public static Person getPerson(Occam occam, PersonFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getPerson().getPerson(ctx, filter);
		}
	}
	
	public static Person getPerson(Domain domain, String login, PersonFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getPerson().getPerson(ctx, filter);
		}
	}

	public static Stream<Person> getPersonStream(Domain domain, String login, PersonFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getPerson().getPersonStream(ctx, filter);
		}
	}
	
	public static void deletePerson(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getPerson().deletePerson(ctx, id);
		}
	}
	
	//---------- INVOICE FISCAL

	public static void saveInvoiceFiscal(String domainName, Integer domainId, String login, AonConfiguration config, Invoice invoice) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getFinance().saveInvoiceFiscal(ctx, config, invoice);
		}
	}	
	
	public static void deleteInvoiceFiscal(String schema, Integer invoiceId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)){
			getFinance().deleteInvoiceFiscal(ctx, invoiceId);
		}
	}	
	
	//-------------ENTERPRISE
	public static EnterpriseCCC saveEnterpriseCCC(Domain domain, String login, EnterpriseCCC ec) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getEnterprise().saveEnterpriseCCC(ctx, ec);
		}
	}
	
	public static EnterpriseCCC getEnterpriseCCC(Domain domain, String login, EnterpriseCCCFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getEnterprise().getEnterpriseCCC(ctx, filter);
		}
	}

	public static Stream<EnterpriseCCC> getEnterpriseCCCStream(Domain domain, String login, EnterpriseCCCFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getEnterprise().getEnterpriseCCCStream(ctx, filter);
		}
	}
	
	public static void deleteEnterpriseCCC(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getEnterprise().deleteEnterpriseCCC(ctx, id);
		}
	}
	
	// INVEST ASSET
	
	public static InvestAsset getInvestAsset(Domain domain, User user, InvestAssetFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getInvestAsset(ctx, filter);
		}
	}
	
	public static void assignInvestAsset2Invoice(String domainName, Integer domainId, String login, Integer investAssetId, Invoice invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getNewProduct().assignInvestAsset2Invoice(ctx, investAssetId, invoice);
		}
	}
	
	public static Stream<InvestAsset> getInvestAssetStream(Domain domain, User user, InvestAssetFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getNewProduct().getInvestAssetStream(ctx, filter);
		}
	}
	
	public static Stream<InvestAsset> getInvestAssetStream(String domainName, Integer domainId, String login, InvestAssetFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getNewProduct().getInvestAssetStream(ctx, filter);
		}
	}
	
	public static InvestAsset saveInvestAsset(Domain domain, User user, InvestAsset investAsset) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getNewProduct().saveInvestAsset(ctx, investAsset);
		}
	}
	
	public static void deleteInvestAsset(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getNewProduct().deleteInvestAsset(ctx, id);
		}
	}
	
	//--------------EMPLOYEE IT
	
	public static Optional<EmployeeIT> getEmployeeIT(Domain domain, User user, ContractLeaveFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getEmployeeIT().getEmployeeIT(ctx, filter);
		}
	}
	
	public static Stream<EmployeeIT> getEmployeesIT(Domain domain, User user, ContractLeaveFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getEmployeeIT().getEmployeesIT(ctx, filter);
		}
	}
	
	public static EmployeeIT[] setEmployeeIT(Domain domain, User user, EmployeeIT... employeeITs) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getEmployeeIT().setEmployeeIT(ctx, employeeITs);
		}
	}
	
	public static void removeEmployeeIT(Domain domain, User user, Integer contractLeaveId, Integer ...partIds) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getEmployeeIT().removeEmployeeIT(ctx, contractLeaveId, partIds);
		}
	}
	
	public static void saveFacturaeCodeAsignacion(Domain domain, User user, Integer invoice, Integer registry, String code) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getFinance().saveFacturaeCodeAsignacion(ctx, invoice, registry, code);
		}
	}
	
	public static Stream<InvoiceData> getInvoiceDataStream(Domain domain, User user, InvoiceDataFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().getInvoiceDataStream(ctx, filter);
		}
	}
	
	public static InvoiceData getInvoiceData(Domain domain, User user, InvoiceDataFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().getInvoiceData(ctx, filter);
		}
	}

	public static InvoiceData saveInvoiceData(Domain domain, User user, InvoiceData invoiceData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().saveInvoiceData(ctx, invoiceData);
		}
	}
	
	public static void deleteInvoiceData(String schema, Integer invoiceId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)){
			getFinance().deleteInvoiceData(ctx, invoiceId);
		}
	}	
	
//	public static InvoiceInfo getInvoiceInfo(Domain domain, User user, InvoiceInfoFilter filter) {
//		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
//			return getFinance().getInvoiceInfo(ctx, filter);
//		}
//	}
//
	public static Optional<InvoiceInfo> getInvoiceInfo(Occam occam, Integer invoiceId, InvoiceCommunicationType type) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceInfo(ctx, invoiceId, type);
		}
	}

	public static InvoiceInfo saveInvoiceInfo(Occam occam, InvoiceInfo invoiceInfo) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().saveInvoiceInfo(ctx, invoiceInfo);
		}
	}
	
	public static void deleteInvoiceInfo(String schema, Integer invoiceId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)){
			getFinance().deleteInvoiceInfo(ctx, invoiceId);
		}
	}	

	public static Stream<InvoiceCommunicationTracking> getInvoiceCommunicationTrackings(Occam occam, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceCommunicationTrackings(ctx, invoiceId);
		}
	}
	
	public static InvoiceCommunicationTracking saveInvoiceCommunicationTracking(Domain domain, User user, InvoiceCommunicationTracking invoiceCommunicationTracking) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().saveInvoiceCommunicationTracking(ctx, invoiceCommunicationTracking);
		}
	}
	
	public static void deleteInvoiceCommunicationTracking(String schema, Integer invoiceId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)){
			getFinance().deleteInvoiceCommunicationTracking(ctx, invoiceId);
		}
	}	
	
	public static InvoiceBatch saveInvoiceBatch(Domain domain, User user, InvoiceBatch invoiceBatch) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().saveInvoiceBatch(ctx, invoiceBatch);
		}
	}
	
	public static InvoiceBatchDetail saveInvoiceBatchDetail(Domain domain, User user, InvoiceBatchDetail invoiceBatchDetail) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().saveInvoiceBatchDetail(ctx, invoiceBatchDetail);
		}
	}

	public static Booking getBooking(Domain domain, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getSecurity().getBooking(ctx, domain);
		}
	}
	
	public static Booking saveBooking(Domain domain, User user, Booking booking) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getSecurity().saveBooking(ctx, booking);
		}
	}
	
	public static Packaging getPackaging(Domain domain, User user, String barcode) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getWarehouse().getPackaging(ctx, barcode);
		}
	}
	
	public static List<Packaging> savePackaging(Domain domain, User user, Packaging packaging) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getWarehouse().savePackaging(ctx, packaging);
		}
	}
	
	public static DeliveryPackaging getDeliveryPackaging(Domain domain, User user, String sscc, Integer delivery, Integer product) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getWarehouse().getDeliveryPackaging(ctx, sscc, delivery, product);
		}
	}
	
	public static PackagingDelivery saveDeliveryPackaging(Domain domain, User user, PackagingDelivery packaging) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getWarehouse().saveDeliveryPackaging(ctx, packaging);
		}
	}
	
	public static void acceptDeliveryPackaging(Domain domain, User user, Integer deliveryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getWarehouse().acceptDeliveryPackaging(ctx, deliveryId);
		}
	}
	
	public static void deleteDeliveryPackaging(Domain domain, User user, Integer deliveryId, String sscc) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getWarehouse().deleteDeliveryPackaging(ctx, deliveryId, sscc);
		}
	}
	
	public static void subtractDeliveryPackagingComposition(Domain domain, User user, Integer deliveryId, ItemComposition composition, String destiny, Double quantity, boolean skipDestiny) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getWarehouse().subtractDeliveryPackagingComposition(ctx, deliveryId, composition, destiny, quantity, skipDestiny);
		}
	}
	
	public static void addDeliveryPackagingComposition(Domain domain, User user, Integer deliveryId, ItemComposition composition, String source, Double quantity, boolean skipSource) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getWarehouse().addDeliveryPackagingComposition(ctx, deliveryId, composition, source, quantity, skipSource);
		}
	}
	
	// ---------- DOMAIN LINKED

	public static List<DomainLinked> getDomainLinkedList(String domainName, Integer domainId, String login, Integer registry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().getDomainLinkedList(ctx, registry);
		}
	}
	
	public static DomainLinked saveDomainLinked(String domainName, Integer domainId, String login, DomainLinked domainLinked) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().saveDomainLinked(ctx, domainLinked);
		}
	}
	
	public static Domain updateDomainStatus(String domainName, Integer domainId, String login, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getRegistry().updateDomainStatus(ctx, domain);
		}
	}
	
	public static void updateDomainCustomer(String domainName, Integer domainId, String login, Integer customer) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getRegistry().updateDomainCustomer(ctx, domainId, customer);
		}
	}
	
	public static Domain saveDomainStatus(String domainName, int domain, String user, RegistryStatus newStatus, Date newExpDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().saveDomainStatus(ctx, domain, newStatus, newExpDate);
		}
	}
	
	// ---------------- Enterprise Data

	public static EnterpriseData getEnterpriseData(Domain domain, User user, EnterpriseDataFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getEnterprise().getEnterpriseData(ctx, filter);
		}
	}
	
	public static EnterpriseData saveEnterpriseData(Domain domain, User user, EnterpriseData enterpriseData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getEnterprise().saveEnterpriseData(ctx, enterpriseData);
		}
	}
	
	public static LinkedList<EnterpriseData> getEnterpriseDataList(String domainName, Integer domainId, String login, EnterpriseDataFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getEnterprise().getEnterpriseDataList(ctx, filter);
		}
	}
	
	public static void insertEnterpriseData(String domainName, Integer domainId, String login, List<EnterpriseData> enterpriseData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getEnterprise().insertEnterpriseData(ctx, enterpriseData);
		}
	}


	public static void updateEnterpriseData(String domainName, Integer domainId, String login, EnterpriseData enterpriseData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getEnterprise().updateEnterpriseData(ctx, enterpriseData);
		}
	}


	public static void deleteEnterpriseData(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getEnterprise().deleteEnterpriseData(ctx, id);
		}
	}

	public static List<Cno> getCno(String domainName, Integer domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getCno(ctx);
		}
	}
	
	// ---------------- Cost Center

	public static List<ApplicationParameter> getCostCenters(String domainName, int domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getCostCenters(ctx);
		}
	}
	
	public static void saveCostCenter(String domainName, int domainId, String login, ApplicationParameter costCenter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getCommon().saveCostCenter(ctx, costCenter);
		}
	}
	
	public static void deleteCostCenter(String domainName, int domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getCommon().deleteCostCenter(ctx, id);
		}
	}
	
	// ---------------- Invest Asset

	public static List<InvestAsset> getInvestAssetList(InvestAssetParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getNewProduct().getInvestAssetList(ctx, params);
		}
	}

	public static void deleteInvestAsset(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getNewProduct().deleteInvestAsset(ctx, id);
		}
	}

	public static InvestAsset saveInvestAsset(String domainName, int domain, String user, InvestAsset investAsset) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getNewProduct().saveInvestAsset(ctx, investAsset);
		}
	}

	public static InvestAsset getInvestAsset(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getNewProduct().getInvestAsset(ctx, id);
		}
	}

	// ---------------- Question
	
	public static List<Question> getQuestionList(QuestionParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getQuestionList(ctx, params);
		}
	}

	public static void deleteQuestion(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getRegistry().deleteQuestion(ctx, id);
		}
	}

	public static Question saveQuestion(String domainName, int domain, String user, Question question) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().saveQuestion(ctx, question);
		}
	}

	public static Question getQuestion(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().getQuestion(ctx, id);
		}
	}
	
	// ---------------- RegistryProfile
	
	public static void saveRegistryProfile(Domain domain, String user, Integer registryId, String questionAlias, String value) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getDescription(), domain.getId(), user)) {
			getRegistry().saveRegistryProfile(ctx, registryId, questionAlias, value);
		}
	}
	
	// ---------------- BookingCheck

	public static LinkedList<BookingCheck> getBookingWithoutFeeList(String domainName, int domain, String user, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getFinance().getBookingWithoutFeeList(ctx, params);
		}
	}
	
	public static LinkedList<BookingCheck> getFeeWithoutBookingList(String domainName, int domain, String user, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getFinance().getFeeWithoutBookingList(ctx, params);
		}
	}
	
	public static LinkedList<BookingCheck> getBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getFinance().getBookingCheckList(ctx, params);
		}
	}
	
	public static LinkedList<BookingCheck> getCustomerBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getFinance().getCustomerBookingCheckList(ctx, params);
		}
	}
	
	public static LinkedList<BookingCheck> getCustomerChildBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getFinance().getCustomerChildBookingCheckList(ctx, params);
		}
	}

	public static void saveBookingCheck(String domainName, int domain, String user, BookingCheck bookingCheck) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			getFinance().saveBookingCheck(ctx, bookingCheck);
		}
	}

	public static void deleteBookingList(String domainName, int domain, String user, LinkedList<BookingCheck> selectedBookings) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			getFinance().deleteBookingList(ctx, selectedBookings);
		}
	}
	
	// DELIVERY INFO
	
	public static DeliveryInfo getDeliveryInfo(Domain domain, User user, DeliveryInfoFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getWarehouse().getDeliveryInfo(ctx, filter);
		}
	}

	public static DeliveryInfo saveDeliveryInfo(Domain domain, User user, DeliveryInfo deliveryInfo) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getWarehouse().saveDeliveryInfo(ctx, deliveryInfo);
		}
	}
	
	public static void deleteDeliveryInfo(String schema, Integer deliveryId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)){
			getWarehouse().deleteDeliveryInfo(ctx, deliveryId);
		}
	}
	
	// VENCIMIENTO NOMINAS

	public static void createSettleSalaries(String domainName, int domainId, String user, Date date) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			getFinance().createSettleSalaries(ctx, date);
		}
	}	
	
	public static Integer createSepaFile(String domainName, int domainId, String user, Integer fbatchId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			return getFinance().createSepaFile(ctx, fbatchId);
		}
	}

	public static LinkedList<FBatch> getFBatches(String domainName, int domainId, String user, FBatchParams params, int offset, int limit) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			return getFinance().getFBatches(ctx, p -> FBatchUtils.getFilter(p, params), offset, limit);
		}
	}
	
	public static FBatch getFBatch(String domainName, int domainId, String user, Integer fbatchId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			return getFinance().getFBatch(ctx, fbatchId);
		}
	}

	public static void deleteFBatches(String domainName, int domainId, String user, LinkedList<Integer> fBatchIds) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			getFinance().deleteFBatches(ctx, fBatchIds);
		}
	}

	public static FBatch createUpdateFBatch(String domainName, int domainId, String user, FBatch fBatch) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, user)){
			return getFinance().createUpdateFBatch(ctx, fBatch);
		}
	}
	
	public static FBatch recordFBatch(Occam occam, Integer fBatchId, Date paymentDate) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getFinance().recordFBatch(ctx, fBatchId, paymentDate);
		}
	}

	public static FBatch unrecordFBatch(Occam occam, Integer fBatchId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getFinance().unrecordFBatch(ctx, fBatchId);
		}
	}
	
	public static AccountEntry getFBatchAccountEntry(Occam occam, Integer fBatchId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getFinance().getFBatchAccountEntry(ctx, fBatchId);
		}
	}

	// ---------------- Marketing Campaign
	
	public static List<MarketingCampaign> getMarketingCampaignlist(MarketingCompaignParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getMarketingCampaignlist(ctx, params);
		}
	}

	public static void deleteMarketingCampaign(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getRegistry().deleteMarketingCampaign(ctx, id);
		}
	}

	public static MarketingCampaign saveMarketingCampaign(String domainName, int domain, String user, MarketingCampaign marketingCampaign) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().saveMarketingCampaign(ctx, marketingCampaign);
		}
	}

	public static MarketingCampaign getMarketingCampaign(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().getMarketingCampaign(ctx, id);
		}
	}
	
	// ---------------- Marketing Action

	public static List<MarketingAction> getMarketingActions(MarketingActionParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getMarketingActions(ctx, params);
		}
	}
	
	public static MarketingAction getMarketingAction(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().getMarketingAction(ctx, id);
		}
	}

	public static void deleteMarketingAction(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getRegistry().deleteMarketingAction(ctx, id);
		}
	}

	public static MarketingAction saveMarketingAction(String domainName, int domain, String user, MarketingAction marketingAction) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().saveMarketingAction(ctx, marketingAction);
		}
	}
	
	public static List<News> getNewsStream(String domainName, int domain, String user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getNews().getNewsStream(ctx, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
		}
	}

	public static List<Newsletter> getNewsletterStream(String domainName, int domain, String user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().getNewsletterStream(ctx, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
		}
	}

	public static List<Survey> getSurveyStream(String domainName, int domain, String user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().getSurveyStream(ctx, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
		}
	}
	
	// ---------------- Marketing Action Target

	public static List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getMarketingActionTargets(ctx, params);
		}
	}
	
	public static List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetMassiveParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getRegistry().getMarketingActionTargets(ctx, params);
		}
	}

	public static void deleteMarketingActionTarget(String domainName, int domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getRegistry().deleteMarketingActionTarget(ctx, id);
		}
	}

	public static MarketingActionTarget saveMarketingActionTarget(String domainName, int domain, String user, MarketingActionTarget marketingActionTarget) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().saveMarketingActionTarget(ctx, marketingActionTarget);
		}
	}

	public static List<Target> getTargetSuggestion(String domainName, int domain, String user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().getTargetStream(ctx, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
		}
	}

	public static Stream<GeoZone> geozoneStream(String domainName, Integer domain, String user, GeoZoneFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getRegistry().geozoneStream(ctx, filter);
		}
	}
	
	// ---------------- Project Commercial

	public static ProjectCommercial saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getProject().saveProjectCommercial(ctx, projectCommercial);
		}
	}

	/// ****************************************************************
	/// ****************************************************************
	/// ****************************************************************
	/// ****************************************************************
	
	public static LinkedList<Series> getSeriesDeliveryList(String domainName,
			Integer domainId, String login, Integer scopeId) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getCommon().getSeriesDeliveryList(ctx, scopeId);
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getSeriesStream(ctx, filter);
		}
	}
	
	public static  String getURL(String shortUrl ){
		return getURL(URI.create(shortUrl));
	}

	public static  String getURL(URI shortUri ) {
		Map<String, String[]> parameterMap = new AonURIBuilder(shortUri).getQueryParamsMap();
		String domain = parameterMap.getOrDefault("domain", new String[] {shortUri.getHost()})[0];
		try ( Connection connection = AonDataSource.getInstance().getConnection(domain) ) {
			return getURLShortener().getURL(new AONContext(connection), shortUri.toString());
		} catch (SQLException | AonConnectionException e) {
			throw new RuntimeException(e);
		} 
	}

	public static  String getShortURL(String path, String url ) {
		return getShortURL(path, URI.create(url), null);
	}
	
	public static  String getShortURL(String path, String url, Date expirationDate ) {
		return getShortURL(path, URI.create(url), expirationDate);
	}
	
	public static  String getShortURL(String path, URI uri, Date expirationDate)  {
		new AonURIBuilder(uri).getQueryParams();
		Map<String, String[]> parameterMap = new AonURIBuilder(uri).getQueryParamsMap();
		String domain = parameterMap.getOrDefault("domain", new String[] {uri.getHost()})[0];
		try ( Connection connection = AonDataSource.getInstance().getConnection(domain) ) {
			return getURLShortener().getShortURL(new AONContext(connection), path, uri.toString(), expirationDate);
		} catch (SQLException | AonConnectionException e) {
			throw new RuntimeException(e);
		} 
	}
	
	// INVOICE CLOSING
	
	public static Stream<InvoiceBatch> getInvoiceClosing(Domain domain, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getFinance().getInvoiceClosing(ctx);
		}
	}
	
	public static void saveInvoiceClosing(Domain domain, User user, InvoiceBatch invoiceBatch) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getFinance().saveInvoiceClosing(ctx, invoiceBatch);
		}
	}
	
	// TARIFF / ITEM TARIFF

	public static Stream<Tariff> getTariffStream(Domain domain, String user, TariffFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getTariffStream(ctx, filter);
		}
	}
	
	public static List<Tariff> getTariffList(Domain domain, String user, TariffParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getTariffList(ctx, params);
		}
	}

	public static void deleteTariff(Domain domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getNewProduct().deleteTariff(ctx, id);
		}
	}

	public static Tariff saveTariff(Domain domain, String user, Tariff tariff) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().saveTariff(ctx, tariff);
		}
	}

	public static Stream<ItemTariff> getItemTariffStream(Domain domain, String user, ItemTariffFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getItemTariffStream(ctx, filter);
		}
	}

	public static void deleteItemTariff(Domain domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getNewProduct().deleteItemTariff(ctx, id);
		}
	}

	public static List<TariffAddInfo> getTariffAddInfoList(Domain domain, String user, Integer tariffId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getTariffAddInfoList(ctx, tariffId);
		}
	}

	public static TariffAddInfo saveTariffAddInfo(Domain domain, String user, TariffAddInfo tariffAddInfo) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().saveTariffAddInfo(ctx, tariffAddInfo);
		}
	}

	public static void deleteTariffAddInfo(Domain domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getNewProduct().deleteTariffAddInfo(ctx, id);
		}
	}

	public static List<TariffCatalogue> getTariffCatalgueList(Domain domain, String user, Integer tariffId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getTariffCatalgueList(ctx, tariffId);
		}
	}

	public static TariffCatalogue saveTariffCatalogue(Domain domain, String user, TariffCatalogue tariffCatalogue) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().saveTariffCatalogue(ctx, tariffCatalogue);
		}
	}

	public static void deleteTariffCatalogue(Domain domain, String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			getNewProduct().deleteTariffCatalogue(ctx, id);
		}
	}

	public static List<Catalogue> getCatalogueList(Domain domain, String user, CatalogueFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)) {
			return getNewProduct().getCatalogueList(ctx, filter);
		}
	}

	// INVOICE DOC
	
	public static Optional<InvoiceDoc> getInvoiceDoc(Occam occam, int domain, int invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceDoc(ctx, domain, invoiceId);
		}
	}
	
	public static void saveInvoiceDoc(Occam occam, InvoiceDoc invoiceDoc) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getFinance().saveInvoiceDoc(ctx, invoiceDoc);
		}
	}
	
	public static List<ActivitySummaryObject> getActivitySummary(String domainName, Integer domainId, String login, Integer parentDomainId,  Integer userId, ActivitySummaryParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getCommon().getActivitySummary(ctx, domainId, parentDomainId, userId, params);
		}
	}

	// API CONFIGURATION
	
	public static ApiConfiguration getApiConfiguration(Occam occam, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().getApiConfiguration(ctx, domainId);
		}
	}

	public static void saveBookingApp(String domainName, int domainId, String login, DomainApp aonApp, boolean active) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getSecurity().saveBookingApp(ctx, aonApp, active);
		}
	}

	public static void deleteBookingApp(String domainName, int domainId, String login, DomainApp aonApp) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getSecurity().deleteBookingApp(ctx, aonApp);
		}
	}

	public static void createBookingApp(String domainName, int domainId, String login, DomainApp aonApp) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			getSecurity().saveBookingApp(ctx, aonApp, true);
		}
	}
	
	public static List<RegistryRelationship> getRegistryRelationships(String domainName, int domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getSecurity().getRegistryRelationships(ctx, domainId);
		}
	}

	public static void createBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, ProductBooking product, Fee newFee) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getNewProduct().createBookingProduct(ctx, domainName, domain, user, customerRelatedRegistry, product, newFee);
		}
	}
	
	public static void updateBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Optional<Fee> oldFee, ProductBooking product, Fee newFee) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getNewProduct().updateBookingProduct(ctx, domainName, domain, user, customerRelatedRegistry, oldFee, product, newFee);
		}
	}
	
	public static void removeBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Optional<Fee> oldFee, ProductBooking product) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getNewProduct().removeBookingProduct(ctx, domainName, domain, user, customerRelatedRegistry, oldFee, product);
		}
	}

	public static void requestBookingInfo(String domainName, int domain, String user, ProductBooking product, Integer customerRegistry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getNewProduct().requestBookingInfo(ctx, domainName, domain, user, product, customerRegistry);
		}
	}

	public static Stream<DomainCompany> getAviableDomainsForSync(String domainName, Integer domain, String user, boolean isSig, DomainFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getSecurity().getAviableDomainsForSync(ctx, isSig, filter);
		}
	}
	
	// ********************************************
	// ************************* INVOICING GROUP **
	// ********************************************
	
	public static Stream<InvoicingGroup> getInvoicingGroups(Occam occam, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getFinance().getInvoicingGroups(ctx, domainId);
		}
	}
	public static Stream<InvoicingGroup> getInvoicingGroups(Occam occam, Integer domainId, String name) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getFinance().getInvoicingGroupsByName(ctx, domainId, name);
		}
	}
	public static Stream<InvoicingGroup> getInvoicingGroupsSuggestion(Occam occam, Integer domainId, String query) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getFinance().getInvoicingGroupsSuggestion(ctx, domainId, query);
		}
	}
	
	/**
	 * @deprecated use {@link #getInvoicingGroupsSuggestion(Occam, Integer, String)}
	 */
	@Deprecated
	public static Stream<InvoicingGroup> getInvoicingGroupsSuggestion(String domainName, int domainId, String login, Integer searchDomain, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getInvoicingGroupsSuggestion(ctx, searchDomain, query);
		}
	}
	
	public static InvoicingGroup save(Occam occam, InvoicingGroup invoicingGroup) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getFinance().save(ctx, invoicingGroup);
		}
	}
	
	// ******************************************
	// ********************** INVOICE REGISTRY **
	// ******************************************
	public static Stream<InvoiceRegistry> getInvoiceRegistriesSuggestion(Occam occam, Integer domainId, String query) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getFinance().getInvoiceRegistries(ctx, domainId, query);
		}
	}
	
	public static void fixInvoice(Occam occam, int domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			getFinance().fixInvoice(ctx, domain);
		}
	}
	
	// ***************************************
	// ***************************** SERIES **
	// ***************************************
	public static Stream<Series> getSeriesSuggestion(Occam occam, Integer domainId, String query) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getFinance().getSeriesSuggestion(ctx, domainId, query);
		}
	}
	
	// ***************************************
	// *************************** CALENDAR **
	// ***************************************

	public static List<com.esferalia.aon.occam.api.model.calendar.Calendar> getCalendar(String domainName, Integer domainId, String userLogin, Integer workplace) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCalendar().getCalendar(ctx, domainId, workplace);
		}
	}

	public static List<Holiday> getHolidays(String domainName, Integer domainId, String userLogin, Integer parentDomain) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCalendar().getHolidays(ctx, domainId, parentDomain);
		}
	}

	public static void deleteHolidayDetail(String domainName, Integer domainId, String userLogin, Integer id) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			getCalendar().deleteHolidayDetail(ctx, id);
		}
	}

	public static com.esferalia.aon.occam.api.model.calendar.Calendar saveCalendar(String domainName, Integer domainId, String userLogin, com.esferalia.aon.occam.api.model.calendar.Calendar calendar) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCalendar().saveCalendar(ctx, domainId, calendar);
		}
	}

	public static void setPayrollWorkplaceCalendar(String domainName, Integer domainId, String userLogin, Integer workplaceId, Integer calendarId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			getCalendar().setPayrollWorkplaceCalendar(ctx, domainId, workplaceId, calendarId);
		}
	}
	
	// ***************************************
	// ********************** REGISTY ENTRY **
	// ***************************************

	public static List<com.esferalia.aon.occam.api.model.calendar.Calendar> getCalendars(String domainName, Integer domainId, String userLogin) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCalendar().getCalendars(ctx, domainId);
		}
	}

	public static List<Agreement> getAgreements(String domainName, Integer domainId, String userLogin) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getAgreement().getAgreements(ctx, domainId);
		}
	}

	public static void deleteWrokplace(String domainName, Integer domainId, String userLogin, Integer workplaceId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			getCommon().deleteWorkplace(ctx, workplaceId);
		}
	}

	public static List<Activity> getEnterpriseActivities(String domainName, Integer domainId, String userLogin, Integer registry) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCommon().getEnterpriseActivities(ctx, registry);
		}
	}

	public static Activity saveEnterpriseActivity(String domainName, Integer domainId, String userLogin, Activity enterpriseActivity) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCommon().saveEnterpriseActivity(ctx, enterpriseActivity);
		}
	}

	public static void deleteEnterpriseActivity(String domainName, Integer domainId, String userLogin, Integer id) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			getCommon().deleteEnterpriseActivity(ctx, id);
		}
	}

	public static List<Cnae2009> getCnae2009List(String domainName, Integer domainId, String userLogin) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCommon().getCnae2009List(ctx, domainId);
		}
	}
	
	public static List<Cnae> getCnae2025List(String domainName, Integer domainId, String userLogin) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCommon().getCnae2025List(ctx, domainId);
		}
	}

	public static List<Iae> getIaeList(String domainName, Integer domainId, String userLogin) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, userLogin)){
			return getCommon().getIaeList(ctx, domainId);
		}
	}

	public static List<RDirStaff> getRDirStaffs(String domainName, Integer domain, String user, Integer registry) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getCommon().getRDirStaffs(ctx, domain, registry);
		}
	}

	public static RDirStaff saveRDirStaff(String domainName, Integer domain, String user, RDirStaff rDirStaff) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getCommon().saveRDirStaff(ctx, rDirStaff);
		}
	}

	public static void deleteRDirStaff(String domainName, Integer domain, String user, Integer id) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			getCommon().deleteRDirStaff(ctx, id);
		}
	}

	public static List<RecordData> getRecordDatas(String domainName, Integer domain, String user, Integer registry, boolean witdhData) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getCommon().getRecordDatas(ctx, domain, registry, witdhData);
		}
	}
	
	public static RecordData getFullRecordData(String domainName, Integer domain, String user, Integer recordDataId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getCommon().getFullRecordData(ctx, domain, recordDataId);
		}
	}

	public static RecordData saveRecordData(String domainName, Integer domain, String user, RecordData recordData) throws AonCoreException {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getCommon().saveRecordData(ctx, domain, recordData);
		}
	}

	public static void deleteRecordData(String domainName, Integer domain, String user, Integer id, boolean deleteData) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			getCommon().deleteRecordData(ctx, domain, id, deleteData);
		}
	}

	public static void updateRecordDataAttach(String domainName, Integer domain, String user, Integer recordDataId, Integer attachId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			getCommon().updateRecordDataAttach(ctx, domain, recordDataId, attachId);
		}
	}

	public static void deleteRecordDataAttach(String domainName, Integer domain, String user, Integer recordDataId) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			getCommon().deleteRecordDataAttach(ctx, domain, recordDataId);
		}
	}

	public static List<Account> getAccountsForBank(String domainName, Integer domain, String user) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getAccounting().getAccounts(ctx, 
					f -> f.getDomainProperty().eq(domain)
							.and(f.getLevelProperty().eq((byte)5))
							.and(f.getCodeProperty().like("5720%").or(f.getCodeProperty().like("5201%")))
					).collect(Collectors.toList());
		}
	}

	public static List<Account> getAviablesAccountsForBank(String domainName, Integer domain, String user) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getAccounting().getAviablesAccountsForBank(ctx, 
					f -> f.getDomainProperty().eq(domain)
							.and(f.getLevelProperty().eq((byte)5))
							.and(f.getCodeProperty().like("5720%").or(f.getCodeProperty().like("5201%")))
					).collect(Collectors.toList());
		}
	}

	public static Account createAccountsForBank(String domainName, Integer domain, String user, String alias, String suffixCode) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getAccounting().createAccountsForBank(ctx, domain, alias, suffixCode);
		}
	}
	
	public static List<Account> getAccountsForRegistry(String domainName, Integer domain, String user, RegistrySource source, String pattern) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			List<Account> accounts = new ArrayList<Account>();
			switch (source) {
				case CUSTOMER:
					accounts = getAccounting().getAccounts(ctx, 
							f -> f.getDomainProperty().eq(domain)
								.and(f.getLevelProperty().eq((byte)5))
								.and(f.getCodeProperty().like("4300%").and(f.getDescriptionProperty().like("%" + pattern + "%")))
							).collect(Collectors.toList());
					return accounts;
				case CREDITOR:
					accounts = getAccounting().getAccounts(ctx, 
							f -> f.getDomainProperty().eq(domain)
								.and(f.getLevelProperty().eq((byte)5))
								.and(f.getCodeProperty().like("4100%").and(f.getDescriptionProperty().like("%" + pattern + "%")))
							).collect(Collectors.toList());
					return accounts;
				case SUPPLIER:
					accounts = getAccounting().getAccounts(ctx, 
							f -> f.getDomainProperty().eq(domain)
								.and(f.getLevelProperty().eq((byte)5))
								.and(f.getCodeProperty().like("4000%").and(f.getDescriptionProperty().like("%" + pattern + "%")))
							).collect(Collectors.toList());
					return accounts;
				default:
					throw new IllegalArgumentException("Unexpected value: " + source);
			}
		}
	}

	public static Account createRegistryAccount(String domainName, Integer domain, String user, String registryName, String registryAlias, RegistrySource registrySource) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domain, user)){
			return getCommon().createRegistryAccount(ctx, domain, registryName, registryAlias, registrySource);
		}
	}
}
