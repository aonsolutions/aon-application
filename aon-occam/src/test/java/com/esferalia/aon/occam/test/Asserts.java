package com.esferalia.aon.occam.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.TaxBreakdown;
import com.esferalia.aon.occam.api.model.finance.VATTaxRegime;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorContext;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Asserts {
	
	private static final double DELTA = 1e-8;
	
	public static void assertEqualsDouble(String msg,double expected,double actual) {
		assertEquals(msg, expected, actual, DELTA);		
	}
	public static void assertNotEqualsDouble(String msg,double expected,double actual) {
		assertNotEquals(msg, expected, actual, DELTA);		
	}
	
	public static void assertEqualsNulls(String msg,Object expected, Object actual) {
		if ( expected == null) assertNull(msg,actual);
		if ( expected != null) assertNotNull(msg,actual);
	}
	public static void assertEqualsArray(String msg,Object[] expected, Object[] actual) {
		if ( (expected == null || expected.length == 0) 
				&& ( (actual != null && actual.length != 0))) 
				fail( msg + " actual List is not Empty");
			if ( (expected != null && expected.length != 0) 
				&& (actual == null || actual.length == 0))  
				fail( msg + " actual List is Empty");
			if ( expected != null && actual != null) {
				assertEquals(" sizes not fit", expected.length, actual.length);	
			}
	}
	
	public static void assertNullCollection(String msg,Collection<?> actual) {
		assertNull(msg,actual);		
	}
	public static void assertEmptyCollection(String msg,Collection<?> actual) {
		assertNotNull(msg,actual);		
		assertTrue(msg,actual.isEmpty());
	}
	
	public static void assertEqualsCollection(String msg,Collection<?> expected, Collection<?> actual) {
		if ( (expected == null || expected.isEmpty()) 
			&& ( (actual != null && !actual.isEmpty()))) 
			fail( msg + " actual List is not Empty");
		if ( (expected != null && !expected.isEmpty()) 
			&& (actual == null || actual.isEmpty()))  
			fail( msg + " actual List is Empty");
		if ( expected != null && actual != null) {
			assertEquals(" sizes not fit", expected.size(), actual.size());	
		}
	}
	
	public static void assertEqualsMap(String msg,Map<?,?> expected, Map<?,?> actual) {
		if ( (expected == null || expected.isEmpty()) 
			&& ( (actual != null && !actual.isEmpty()))) 
			fail( msg + " actual List is not Empty");
		if ( (expected != null && !expected.isEmpty()) 
			&& (actual == null || actual.isEmpty()))  
			fail( msg + " actual List is Empty");
		if ( expected != null && actual != null) {
			assertEquals(" sizes not fit", expected.size(), actual.size());	
		}
	}
	public static void assertEqualsAccount (Account expected, Account actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Code",expected.getCode(), actual.getCode());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Alias",expected.getAlias(), actual.getAlias());
			assertEquals("EntryEnabled",expected.isEntryEnabled(), actual.isEntryEnabled());
			assertEquals("Level",expected.getLevel(), actual.getLevel());
			assertEquals("Active",expected.isActive(), actual.isActive());
			assertEquals("CostCenter",expected.getCostCenter(), actual.getCostCenter());
		}
	}

	public static void assertEqualsAccountPeriod(AccountPeriod expected, AccountPeriod actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Name",expected.getName(), actual.getName());
			assertEquals("InitiationDate",expected.getInitiationDate(), actual.getInitiationDate());
			assertEquals("Deadline",expected.getDeadline(), actual.getDeadline());
			assertEquals("Status",expected.getStatus(), actual.getStatus());
		}
	}

	public static void assertEqualsScope(Scope expected, Scope actual) {
		assertEqualsNulls( "Scope", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
		}
	}
	
	public static void assertEqualsScopeId(Scope expected, Scope actual) {
		assertEqualsNulls( "Scope", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}

	private static void assertEqualsDomain(Domain expected, Domain actual) {
		assertEqualsNulls( "Domain", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Name",expected.getName(), actual.getName());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Owner",expected.getOwner(), actual.getOwner());
			assertEquals("ParentId",expected.getParentId(), actual.getParentId());
			assertEquals("DomainType",expected.getDomainType(), actual.getDomainType());
			assertEquals("EnableHeredity",expected.isEnableHeredity(), actual.isEnableHeredity());
			assertEquals("DomainManagement",expected.isDomainManagement(), actual.isDomainManagement());
			assertEquals("Active",expected.isActive(), actual.isActive());
			assertEquals("Scope",expected.getScope(), actual.getScope());
			assertEquals("MaxDefinedUsers",expected.getMaxDefinedUsers(), actual.getMaxDefinedUsers());
			assertEquals("DefinedUsers",expected.getDefinedUsers(), actual.getDefinedUsers());
		}
	}

	public static void assertEqualsEnterpriseActivity(EnterpriseActivity expected, EnterpriseActivity actual) {
		assertEqualsNulls( "EnterpriseActivity", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Principal",expected.isPrincipal(), actual.isPrincipal());
			assertEquals("Iae",expected.getIae().getId(), actual.getIae().getId());
			assertEquals("Epigraph",expected.getEpigraph(), actual.getEpigraph());
			assertEquals("Cnae",expected.getCnae(), actual.getCnae());
			assertEquals("CnaeCode",expected.getCnaeCode(), actual.getCnaeCode());
			assertEquals("CnaeDescription",expected.getCnaeDescription(), actual.getCnaeDescription());
		}
	}

	public static void assertEqualsAccountingReportParams(AccountingReportParams expected, AccountingReportParams actual) {
		assertEqualsNulls( "Account", expected, actual);
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("DomainName", expected.getDomainName(), actual.getDomainName());
		assertEquals("User", expected.getUser(), actual.getUser());
		assertEquals("Period", expected.getPeriod(), actual.getPeriod());
		assertEquals("FromDate", expected.getFromDate(), actual.getFromDate());
		assertEquals("ToDate", expected.getToDate(), actual.getToDate());
		assertEqualsAccount(expected.getAccount(), actual.getAccount());
		assertEquals("Level", expected.getLevel(), actual.getLevel());
		assertEquals("Activity", expected.getActivity(), actual.getActivity());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("DocumentNumber", expected.getDocumentNumber(), actual.getDocumentNumber());
		assertEquals("PreviousPeriods", expected.getPreviousPeriods(), actual.getPreviousPeriods()); 
		assertEquals("LowLevelAccountVisible", expected.isLowLevelAccountVisible(), actual.isLowLevelAccountVisible());
		assertEquals("NoActivityAccountVisible", expected.isNoActivityAccountVisible(), actual.isNoActivityAccountVisible());
		assertEquals("NoBalanceAccountExcluded", expected.isNoBalanceAccountExcluded(), actual.isNoBalanceAccountExcluded());
		assertEquals("PercentsEnabled", expected.isPercentsEnabled(), actual.isPercentsEnabled());
		assertEquals("ByMonth", expected.isByMonth(), actual.isByMonth());
		assertEquals("OpeningEntriesExcluded", expected.areOpeningEntriesExcluded(), actual.areOpeningEntriesExcluded()); 
		assertEquals("OperatingEntriesExcluded", expected.areOperatingEntriesExcluded(), actual.areOperatingEntriesExcluded());
		assertEquals("ClosingEntriesExcluded", expected.areClosingEntriesExcluded(), actual.areClosingEntriesExcluded());
		assertEquals("ReverseOrder", expected.isReverseOrder(), actual.isReverseOrder());
		assertEquals("BalanceType", expected.getBalanceType(), actual.getBalanceType());
		assertEqualsAccountPeriod(expected.getSelectedPeriod(), actual.getSelectedPeriod());
		assertEqualsEnterpriseActivity(expected.getSelectedActivity(), actual.getSelectedActivity());
		assertEqualsAccount(expected.getSelectedAccount(), actual.getSelectedAccount());
		assertEquals("BreakdownEnabled", expected.isBreakdownEnabled(), actual.isBreakdownEnabled());
		assertEquals("LedgerAccount", expected.getLedgerAccount(), actual.getLedgerAccount());
		assertEquals("LedgerDebitBalance", expected.getLedgerDebitBalance(), actual.getLedgerDebitBalance(), DELTA);
		assertEquals("LedgerUnpaidBalance", expected.getLedgerUnpaidBalance(), actual.getLedgerUnpaidBalance(), DELTA);
		assertEquals("Consolidation", expected.isConsolidation(), actual.isConsolidation());
		assertEquals("Registry", expected.getRegistry(), actual.getRegistry());
		assertEquals("Output", expected.getOutput(), actual.getOutput());
		assertEquals("VatSummaryType", expected.getVatSummaryType(), actual.getVatSummaryType());
		assertEquals("Percent", expected.getPercent(), actual.getPercent());
		assertEquals("RectificationType", expected.getRectificationType(), actual.getRectificationType());
		assertEquals("Surcharge", expected.getSurcharge(), actual.getSurcharge());
		assertEquals("FarmerRegime", expected.getFarmerRegime(), actual.getFarmerRegime());
		assertEquals("AccrualRegime", expected.getAccrualRegime(), actual.getAccrualRegime());
		assertEquals("Investment", expected.getInvestment(), actual.getInvestment());
		assertEquals("Service", expected.getService(), actual.getService());
		assertEquals("Title", expected.getTitle(), actual.getTitle());
		assertEquals("Subject", expected.getSubject(), actual.getSubject());
		assertEquals("ShowCover", expected.isShowCover(), actual.isShowCover());
		assertEquals("PageOffset", expected.getPageOffset(), actual.getPageOffset());
		assertEquals("PageOffsetText", expected.getPageOffsetText(), actual.getPageOffsetText());
		assertEquals("HideFilter", expected.isHideFilter(), actual.isHideFilter());
		assertEquals("HeaderText", expected.getHeaderText(), actual.getHeaderText());
		assertEquals("HideDateTimeOnFooter", expected.isHideDateTimeOnFooter(), actual.isHideDateTimeOnFooter());
		assertEquals("FooterText", expected.getFooterText(), actual.getFooterText());
		assertEqualsCollection( "Domains Size",expected.getDomains(), actual.getDomains());
		if ( expected.getDomains() != null) {
			for (int i = 0; i < expected.getDomains().size(); i++) {
				assertEqualsDomain(expected.getDomains().get(i), actual.getDomains().get(i));	
			}
		}
		assertEqualsCollection( "CostCenters Size",expected.getCostCenters(), actual.getCostCenters());
		if ( expected.getCostCenters() != null) {
			for (String key : expected.getCostCenters()) {
				assertTrue( "CostCenter", expected.getCostCenters().contains(key) );	
			}
		}
		assertEqualsArray( "Invoices",expected.getInvoices(), actual.getInvoices());
		if ( expected.getInvoices() != null) {
			for (int i = 0; i < expected.getInvoices().length; i++) {
				assertEquals("Invoice -> " + i, expected.getInvoices()[i], actual.getInvoices()[i]);	
			}
		}
		
//		private Integer[] invoices;
		
	}

	public static void assertEqualsRegistry (Registry expected, Registry actual) {
		assertEqualsNulls( "Registry", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEqualsDomain( expected.getDomain(), actual.getDomain());
			assertEquals("Document",expected.getDocument(), actual.getDocument());
			assertEquals("DocumentType",expected.getDocumentType(), actual.getDocumentType());
			assertEquals("DocumentCountry",expected.getDocumentCountry(), actual.getDocumentCountry());
			assertEquals("Name",expected.getName(), actual.getName());
			assertEquals("Alias",expected.getAlias(), actual.getAlias());
			assertEquals("LegalPerson",expected.isLegalPerson(), actual.isLegalPerson());
			assertEquals("Nationality",expected.getNationality(), actual.getNationality());
			assertEquals("SecurityLevel",expected.getSecurityLevel() , actual.getSecurityLevel());
		}
	}
	
	public static void assertEqualsRegistryFull (RegistryFull<?> expected, RegistryFull<?> actual) {
		assertEqualsNulls( "RegistryFull", expected, actual);
		assertEqualsNulls( "RegistryFull Registry", expected.getRegistry(), actual.getRegistry());
		assertEqualsCollection( "RegistryFull Addresses Size",expected.getAddresses(), actual.getAddresses());
		assertEqualsCollection( "RegistryFull Medias Size",expected.getMedias(), actual.getMedias());
		for (int i = 0; i < expected.getAddresses().size(); i++) {
			assertEqualsRegistryAddress(expected.getAddresses().get(i), actual.getAddresses().get(i));	
		}
		assertEquals( "RegistryFull Medias Size", expected.getMedias().size(), actual.getMedias().size());
		for (int i = 0; i < expected.getMedias().size(); i++) {
			assertEqualsRegistryMedia(expected.getMedias().get(i), actual.getMedias().get(i));	
		}
	}
	
	public static void assertEqualsCreditorFull (CreditorFull expected, CreditorFull actual) {
		assertEqualsRegistryFull (expected, actual);
		assertEqualsCreditor(expected.getRegistry(), actual.getRegistry());
	}

	public static void assertEqualsCustomerFull (CustomerFull expected, CustomerFull actual) {
		assertEqualsRegistryFull (expected, actual);
		assertEqualsCustomer(expected.getRegistry(), actual.getRegistry());
	}

	public static void assertEqualsSupplierFull (SupplierFull expected, SupplierFull actual) {
		assertEqualsRegistryFull (expected, actual);
		assertEqualsSupplier(expected.getRegistry(), actual.getRegistry());
	}

	public static void assertEqualsCustomer(Customer expected, Customer actual) {
		assertEqualsNulls( "Customer", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals("Tariff",expected.getTariff(), actual.getTariff());
		assertEquals("Surcharge",expected.isSurcharge(),actual.isSurcharge());
		assertEquals("Withholding",expected.isWithholding(),actual.isWithholding());
		assertEquals("Transaction",expected.getTransaction(),actual.getTransaction());
		assertEquals("Status",expected.getStatus(),actual.getStatus());
		assertEquals("Scope",expected.getScope().getId(),actual.getScope().getId());
		assertEquals("EInvoice",expected.isEInvoice(),actual.isEInvoice());
		assertEquals("InvoicingGroup",expected.getInvoicingGroup(),actual.getInvoicingGroup());
		assertEquals("ProjectGrouped",expected.isProjectGrouped(),actual.isProjectGrouped());
		assertEquals("DeliveryGrouped",expected.isDeliveryGrouped(),actual.isDeliveryGrouped());
		assertEquals("DeliveryValuated",expected.isDeliveryValuated(),actual.isDeliveryValuated());
		assertEquals("Account",expected.getAccount(),actual.getAccount());
	}

	public static void assertEqualsCreditor(Creditor expected, Creditor actual) {
		assertEqualsNulls( "Creditor", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals("Withholding",expected.isWithholding(),actual.isWithholding());
		assertEquals("VatAccrualPayment",expected.isVatAccrualPayment(),actual.isVatAccrualPayment());
		assertEquals("Transaction",expected.getTransaction(),actual.getTransaction());
		assertEquals("Status",expected.getStatus(),actual.getStatus());
		assertEquals("Scope",expected.getScope().getId(),actual.getScope().getId());
		assertEquals("Account",expected.getAccount(),actual.getAccount());
	}

	public static void assertEqualsSeller(Seller expected, Seller actual) {
		assertEqualsNulls( "Seller", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals("CommissionType", expected.getCommissionType().getId(), actual.getCommissionType().getId());
		assertEquals("Scope", expected.getScope().getId(), actual.getScope().getId());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
	}
	
	public static void assertEqualsSupplier(Supplier expected, Supplier actual) {
		assertEqualsNulls( "Supplier", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals("Tariff",expected.getTariff(), actual.getTariff());
		assertEquals("Withholding",expected.isWithholding(),actual.isWithholding());
		assertEquals("WithholdingFarmer",expected.isWithholdingFarmer(),actual.isWithholdingFarmer());
		assertEquals("VatAccrualPayment",expected.isVatAccrualPayment(),actual.isVatAccrualPayment());
		assertEquals("Transaction",expected.getTransaction(),actual.getTransaction());
		assertEquals("Status",expected.getStatus(),actual.getStatus());
		assertEquals("Scope",expected.getScope().getId(),actual.getScope().getId());
		assertEquals("PurchaseValuated",expected.isPurchaseValuated(),actual.isPurchaseValuated());
		assertEquals("Account",expected.getAccount(),actual.getAccount());
	}
	
	public static void assertEqualsTaskHolder(TaskHolder expected, TaskHolder actual) {
		assertEqualsNulls( "TaskHolder", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals("Type",expected.getType(), actual.getType());
		assertEquals("Active",expected.isActive(),actual.isActive());
		assertEquals("UserId",expected.getUserId(),actual.getUserId());
		assertEquals("CostProfile",expected.getCostProfile(),actual.getCostProfile());
	}

	public static void assertEqualsRegistryMedia (RegistryMedia expected, RegistryMedia actual) {
		assertEqualsNulls( "RegistryMedia", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Registry",expected.getRegistry(), actual.getRegistry());
		assertEquals("Media",expected.getMedia(), actual.getMedia());
		assertEquals("Value",expected.getValue(), actual.getValue());
		assertEquals("Comment",expected.getComment(), actual.getComment());
		assertEquals("Raddress",expected.getRaddress(), actual.getRaddress());
		assertEquals("Administrative",expected.isAdministrative(), actual.isAdministrative());
		assertEquals("Commercial",expected.isCommercial(), actual.isCommercial());
		assertEquals("Technical",expected.isTechnical(), actual.isTechnical());
	}

	public static void assertEqualsRegistryAddress(RegistryAddress expected, RegistryAddress actual) {
		assertEqualsNulls( "RegistryAddress", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Registry",expected.getRegistry(), actual.getRegistry());
		assertEquals("Main",expected.isMain(), actual.isMain());
		assertEquals("Recipient",expected.getRecipient(), actual.getRecipient());
		assertEquals("StreetType"
				, expected.getStreetType()==null?null:expected.getStreetType().getAeatCode()
				, actual.getStreetType()==null?null:actual.getStreetType().getAeatCode());
		assertEquals("Address",expected.getAddress(), actual.getAddress());
		assertEquals("Number",expected.getNumber(), actual.getNumber());
		assertEquals("Address2",expected.getAddress2(), actual.getAddress2());
		assertEquals("Address3",expected.getAddress3(), actual.getAddress3());
		assertEquals("Zip",expected.getAddress3(), actual.getAddress3());
		assertEquals("City",expected.getCity(), actual.getCity());
		assertEquals("Geozone",expected.getGeozone(), actual.getGeozone());
		assertEquals("GeozoneCode",expected.getGeozoneCode(), actual.getGeozoneCode());
		assertEquals("GeozoneName",expected.getGeozoneName(), actual.getGeozoneName());
		assertEquals("Alias",expected.getAlias(), actual.getAlias());
		assertEquals("MunicipalityCode",expected.getMunicipalityCode(), actual.getMunicipalityCode());
	}

	public static void assertEqualsTariff(Tariff expected, Tariff actual) {
		assertEqualsNulls( "Tariff", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Code",expected.getCode(), actual.getCode());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Purchase",expected.isPurchase(), actual.isPurchase());
		assertEquals("Discount",expected.getDiscount(), actual.getDiscount(), DELTA);
		assertEquals("Active",expected.isActive(), actual.isActive());
	}
	
	public static void assertEqualsProduct(Product expected, Product actual) {
		assertEqualsNulls( "Product", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain().getId(), actual.getDomain().getId());
		assertEquals("Code",expected.getCode(), actual.getCode());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Brand", expected.getBrand().getId(), actual.getBrand().getId());
		assertEquals("Category", expected.getCategory().getId(), actual.getCategory().getId());
		assertEquals("Status", expected.getStatus().value(), actual.getStatus().value());
		assertEquals("Type", expected.getType().value(), actual.getType().value());
		assertEquals("Kind", expected.getKind().value(), actual.getKind().value());
		assertEquals("Vat", expected.getVat().getId(), actual.getVat().getId());
		assertEquals("Retention", expected.getRetention().getId(), actual.getRetention().getId());
		assertEquals("Inventoriable", expected.isInventoriable(), actual.isInventoriable());
		assertEquals("Serializable", expected.isSerializable(), actual.isSerializable());
		assertEquals("Lotable", expected.isLotable(), actual.isLotable());
		assertEquals("Manufactured", expected.isManufactured(), actual.isManufactured());
		assertEquals("Composition", expected.isComposition(), actual.isComposition());
		assertEquals("CompositionPrice", expected.isCompositionPrice(), actual.isCompositionPrice());
		assertEquals("packaged", expected.isPackaged(), actual.isPackaged());
		assertEquals("salesAccount", expected.getSalesAccount().getId(), actual.getSalesAccount().getId());
		assertEquals("purchaseAccount", expected.getPurchaseAccount().getId(), actual.getPurchaseAccount().getId());		
		assertEquals("Active",expected.isActive(), actual.isActive());
	}
	
	public static void assertEqualsWorkgroup(Workgroup expected, Workgroup actual) {
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Description",expected.getDescription(), actual.getDescription());
		assertEquals("status",expected.getStatus(), actual.getStatus());
	}
	
	public static void assertEqualsProjectType(ProjectType expected, ProjectType actual) {
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Description",expected.getDescription(), actual.getDescription());
		assertEquals("Active",expected.isActive(), actual.isActive());
	}
	
	public static void assertEqualsProject(Project expected, Project actual) {
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain().getId(), actual.getDomain().getId());
		assertEquals("Name", expected.getName(), actual.getName());
		assertEquals("Alias", expected.getAlias(), actual.getAlias());
		assertEquals("Date", AonDateUtils.getDateWithoutTime(expected.getDate()),
				AonDateUtils.getDateWithoutTime(actual.getDate()));
		assertEquals("Type", expected.getType().getId(), actual.getType().getId());
		assertEquals("Tas", expected.isTas(), actual.isTas());
		assertEquals("Commercial", expected.isCommercial(), actual.isCommercial());
		assertEquals("Reservation", expected.isReservation(), actual.isReservation());
		assertEquals("Active", expected.isActive(), actual.isActive());
	}
	
	public static void assertEqualsProjectHolder(ProjectHolder expected, ProjectHolder actual) {
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Project", expected.getProject(), actual.getProject());
		assertEquals("Start Date", AonDateUtils.getDateWithoutTime(expected.getStartDate()), AonDateUtils.getDateWithoutTime(actual.getStartDate()));
		assertEquals("Start Date", AonDateUtils.getHour(expected.getStartDate()), AonDateUtils.getHour(actual.getStartDate()));
		assertEquals("End Date",  AonDateUtils.getDateWithoutTime(expected.getEndDate()),  AonDateUtils.getDateWithoutTime(actual.getEndDate()));
		assertEquals("End Date",  AonDateUtils.getHour(expected.getEndDate()),  AonDateUtils.getHour(actual.getEndDate()));
		assertEquals("Workgroup", expected.getWorkgroup().getId(), actual.getWorkgroup().getId());
		assertEquals("Task Holder", expected.getTaskHolder().getId(), actual.getTaskHolder().getId());
	}
	
	public static void assertEqualsBrand(Brand expected, Brand actual) {
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Name",expected.getName(), actual.getName());
	}
	
	public static void assertEqualsProductCategory(ProductCategory expected, ProductCategory actual) {
		assertEqualsNulls( "ProductCategory", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Detail",expected.getDetail(), actual.getDetail());
		assertEquals("Detail2",expected.getDetail2(), actual.getDetail2());
		assertEquals("Detail3",expected.getDetail3(), actual.getDetail3());
	}
	
	public static void assertEqualsPayMethod(PayMethod expected, PayMethod actual) {
		assertEqualsNulls( "PayMethod", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Type",expected.getType(), actual.getType());
	}
	public static void assertEqualsAccountTrialBalance(AccountTrialBalance expected, AccountTrialBalance actual) {
		assertEqualsNulls( "AccountTrialBalance", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Code",expected.getCode(), actual.getCode());
		assertEquals("Description",expected.getDescription(), actual.getDescription());
		assertEquals("BeforePeriodDebit",expected.getBeforePeriodDebit(), actual.getBeforePeriodDebit(), DELTA);
		assertEquals("BeforePeriodCredit",expected.getBeforePeriodCredit(), actual.getBeforePeriodCredit(), DELTA);
		assertEquals("InPeriodOpeningDebit",expected.getInPeriodOpeningDebit(), actual.getInPeriodOpeningDebit(), DELTA);
		assertEquals("InPeriodOpeningCredit",expected.getInPeriodOpeningCredit(), actual.getInPeriodOpeningCredit (), DELTA);
		assertEquals("InPeriodBeforeDebit",expected.getInPeriodBeforeDebit(), actual.getInPeriodBeforeDebit(), DELTA);
		assertEquals("InPeriodBeforeCredit",expected.getInPeriodBeforeCredit(), actual.getInPeriodBeforeCredit(), DELTA);
		assertEquals("InPeriodDebit",expected.getInPeriodDebit(), actual.getInPeriodDebit(), DELTA);
		assertEquals("InPeriodCredit",expected.getInPeriodCredit(), actual.getInPeriodCredit(), DELTA);
	}
	public static void assertEqualsAccountTrialBalanceReport (AccountTrialBalanceReport expected, AccountTrialBalanceReport actual) {
		assertEqualsNulls( "AccountTrialBalanceReport", expected, actual);
		if (expected != null) {
			assertEqualsAccountingReportParams(expected.getParams(), actual.getParams());
			assertEquals("hasBeforePeriodAmounts",expected.hasBeforePeriodAmounts(), actual.hasBeforePeriodAmounts());	
			assertEquals("hasOpeningAmounts",expected.hasOpeningAmounts(), actual.hasOpeningAmounts());	
			assertEquals("hasInPeriodPreviousAmounts",expected.hasInPeriodPreviousAmounts(), actual.hasInPeriodPreviousAmounts());
			assertEqualsAccountTrialBalance(expected.getTotalBalance(), actual.getTotalBalance());
			assertEqualsMap("Balances", expected.getBalances(), actual.getBalances());
			if ( expected.getBalances() != null) {
				for ( String key : expected.getBalances().keySet()) {
					assertEqualsAccountTrialBalance(expected.getBalances().get(key),actual.getBalances().get(key));
				}
			}
		}
	}
	
	public static void assertEqualsAccountOperatingAccount (AccountOperatingAccount expected, AccountOperatingAccount actual) {
		assertEqualsNulls("AccountOperatingAccount", expected, actual);
		if (expected != null) {
			assertEquals("Code", expected.getCode(), actual.getCode());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Type", expected.getType(), actual.getType());
		}
	}
	public static void assertEqualsAccountOperatingStatement (AccountOperatingStatement expected, AccountOperatingStatement actual) {
		assertEqualsNulls("AccountOperatingStatement", expected, actual);
		if (expected != null) {
			assertEqualsAccountOperatingAccount(expected.getAccount(), actual.getAccount());
			assertEquals("Credit", expected.getCredit(), actual.getCredit(), DELTA);
			assertEquals("Debit", expected.getDebit(), actual.getDebit(), DELTA);
			assertEquals("DebitBalance", expected.getDebitBalance(), actual.getDebitBalance(), DELTA);
			assertEquals("ExpensesRatio", expected.getExpensesRatio(), actual.getExpensesRatio(), DELTA);
			assertEquals("IncreasePercent", expected.getIncreasePercent(), actual.getIncreasePercent(), DELTA);
			assertEquals("Month", expected.getMonth(), actual.getMonth());
			assertEquals("PurchasesRatio", expected.getPurchasesRatio(), actual.getPurchasesRatio(), DELTA);
			assertEquals("SalesRatio", expected.getSalesRatio(), actual.getSalesRatio(), DELTA);
			assertEquals("UnpaidBalance", expected.getUnpaidBalance(), actual.getUnpaidBalance(), DELTA);
		}
	}
	public static void assertEqualsAccountOperatingReport (AccountOperatingReport expected, AccountOperatingReport actual) {
		assertEqualsNulls("AccountOperatingReport", expected, actual);
		if (expected != null ) {
			assertEqualsCollection("AccountOperatingAccounts", expected.getAccounts(), actual.getAccounts());
			assertEqualsCollection("AccountOperatingIntervals", expected.getIntervals(), actual.getIntervals());
			for (DateInterval expectedInterval : expected.getIntervals()) {
				Date start = expectedInterval.getStart();
				Date end = expectedInterval.getEnd();
				String name = expectedInterval.getName();
				
				
				DateInterval actualInterval = actual.getIntervals().stream()
						.filter(inter ->{
							SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
							if (name != null && !name.equals(inter.getName()))
								return false;
							if(start != null) {
								if(!df.format(start).equals(df.format(inter.getStart())))
									return false;
							}
							if(start != null) {
								if(!df.format(end).equals(df.format(inter.getEnd())))
									return false;
							}
							return true;
						})
						.findFirst().orElse(null);
				for (AccountOperatingAccount account : expected.getAccounts()) {
					assertEqualsAccountOperatingStatement(expected.get(account.getCode(), expectedInterval), actual.get(account.getCode(), actualInterval));
				}
			}
		}
	}
	
	public static void assertEqualsRDirStaff(RDirStaff expected, RDirStaff actual) {
		assertEqualsNulls( "RDirStaff", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Registry",expected.getRegistry(), actual.getRegistry());
		assertEquals("Document",expected.getDocument(), actual.getDocument());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("ShareHolder", expected.getShareHolder(), actual.getShareHolder());
		assertEquals("Representative", expected.getRepresentative(), actual.getRepresentative());
		assertEquals("Director", expected.getDirector(), actual.getDirector());
		assertEquals("RepresentativeLabor", expected.getRepresentativeLabor(), actual.getRepresentativeLabor());
		assertEquals("DueDate", expected.getDueDate(), actual.getDueDate());
		assertEquals("PercentShare", expected.getPercentShare(), actual.getPercentShare(), DELTA);
		assertEquals("NominalValue", expected.getNominalValue(), actual.getNominalValue());
		assertEquals("ShareNumber", expected.getShareNumber(), actual.getShareNumber());
		assertEquals("ChargeDescription", expected.getChargeDescription(), actual.getChargeDescription());
	}
	
	public static void assertEqualsCompany(Company expected, Company actual) {
		assertEqualsNulls( "Company", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals("Active",expected.isActive(),actual.isActive());
		assertEquals("Surcharge",expected.isSurcharge(),actual.isSurcharge());
		assertEquals("Withholding",expected.isWithholding(),actual.isWithholding());
		assertEquals("VatAccrualPayment",expected.isVatAccrualPayment(),actual.isVatAccrualPayment());
		assertEquals("eInvoice",expected.iseInvoice(),actual.iseInvoice());
	}
	
	public static void assertEqualsInvoice(Invoice expected, Invoice actual) {
		assertEqualsNulls( "Invoice", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Activity", expected.getActivity().getId(), actual.getActivity().getId());
		assertEquals("Epigraph", expected.getEpigraph(), actual.getEpigraph());
		assertEquals("InvestAsset", expected.getInvestAsset(), actual.getInvestAsset());
		assertEquals("Project", expected.getProject(), actual.getProject());
		assertEquals("Series", expected.getSeries(), actual.getSeries());
		assertEquals("Number", expected.getNumber(), actual.getNumber());
		assertEquals("ReferenceCode", expected.getReferenceCode(), actual.getReferenceCode());
		assertEquals("IssueDate", expected.getIssueDate(), actual.getIssueDate());
		assertEquals("TaxDate", expected.getTaxDate(), actual.getTaxDate());
		assertEquals("RectificationType", expected.getRectificationType(), actual.getRectificationType());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("RectificationInvoice", expected.getRectificationInvoice(), actual.getRectificationInvoice());
		// assertEqualsRegistry(expected.getRegistryData(), actual.getRegistryData());
		assertEquals("Registry", expected.getRegistry(), actual.getRegistry());
		assertEquals("RegistryDocument", expected.getRegistryDocument(), actual.getRegistryDocument());
		assertEquals("RegistryDocumentType", expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
		assertEquals("RegistryDocumentCountry", expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
		assertEquals("RegistryName", expected.getRegistryName(), actual.getRegistryName());
		assertEqualsScopeId(expected.getScope(), actual.getScope());
		assertEquals("Type", expected.getType(), actual.getType());
		assertEquals("Transaction", expected.getTransaction(), actual.getTransaction());
		assertEquals("Recorded", expected.isRecorded(), actual.isRecorded());
		assertEquals("Surcharge", expected.isSurcharge(), actual.isSurcharge());
		assertEquals("Withholding", expected.isWithholding(), actual.isWithholding());
		assertEquals("WithholdingFarmer", expected.isWithholdingFarmer(), actual.isWithholdingFarmer());
		assertEquals("VatAccrualPayment", expected.isVatAccrualPayment(), actual.isVatAccrualPayment());
		assertEquals("Investment", expected.isInvestment(), actual.isInvestment());
		assertEquals("Service", expected.isService(), actual.isService());
		assertEquals("Advance", expected.isAdvance(), actual.isAdvance());
		assertEquals("Signed", expected.isSigned(), actual.isSigned());
		assertEquals("TaxableBase", expected.getTaxableBase(), actual.getTaxableBase(), DELTA);
		assertEquals("VatQuota", expected.getVatQuota(), actual.getVatQuota(), DELTA);
		assertEquals("RetentionQuota", expected.getRetentionQuota(), actual.getRetentionQuota(), DELTA);
		assertEquals("Total", expected.getTotal(), actual.getTotal(), DELTA);
		assertEquals("PosShift", expected.getPosShift(), actual.getPosShift());
		assertEquals("Seller", expected.getSeller(), actual.getSeller());
		assertEquals("SellerName", expected.getSellerName(), actual.getSellerName());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
		assertEqualsInvoiceFiscal(expected.getFiscal(), actual.getFiscal());
	}
	
	public static void assertEqualsInvestAsset(InvestAsset expected, InvestAsset actual) {
		assertEqualsNulls( "InvestAsset", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Description", expected.getDescription(), actual.getDescription());
		assertEqualsEnterpriseActivity( expected.getActivity(), actual.getActivity() );
		assertEquals("Type", expected.getType(), actual.getType());
		assertEquals("Regime", expected.getRegime(), actual.getRegime());
		assertEquals("StartDate", expected.getStartDate(), actual.getStartDate());
		assertEquals("EndDate", expected.getEndDate(), actual.getEndDate());
		assertEquals("VatPercent", expected.getVatPercent(), actual.getVatPercent(), DELTA);
		assertEquals("RetentionPercent", expected.getRetentionPercent(), actual.getRetentionPercent(), DELTA);
		assertEquals("Properties", expected.getProperties(), actual.getProperties());
	}
	
	public static void assertEqualsDiscountExpression(DiscountExpression expected, DiscountExpression actual) {
		assertEqualsNulls( "DiscountExpression", expected, actual);
		assertEquals("DiscountExpr", expected.getDiscountExpr(), actual.getDiscountExpr());
		assertArrayEquals( expected.getDiscounts(), actual.getDiscounts(), DELTA);
	}
	
	public static void assertEqualsInvoiceDetails(List<InvoiceDetail> expected, List<InvoiceDetail> actual) {
		assertEqualsCollection("InvoiceDetails", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsInvoiceDetail(expected.get(i),actual.get(i)));
	}

	public static void assertEqualsInvoiceDetail(InvoiceDetail expected, InvoiceDetail actual) {
		assertEqualsNulls( "InvoiceBreakdown", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEqualsInvoice(expected.getInvoice(), actual.getInvoice());	
		assertEquals("InvestAsset", expected.getInvestAsset(), actual.getInvestAsset());
		assertEqualsInvestAsset(expected.getInvestAssetData(), actual.getInvestAssetData());
		assertEquals("Project", expected.getProject(), actual.getProject());
		assertEquals("ProjectName", expected.getProjectName(), actual.getProjectName());
		assertEqualsSeller(expected.getSeller(), actual.getSeller());
		assertEqualsItem(expected.getItem(), actual.getItem());
		assertEquals("Line", expected.getLine(), actual.getLine());
		assertEquals("Description", expected.getDescription(), actual.getDescription());
		assertEquals("Quantity", expected.getQuantity(), actual.getQuantity(), DELTA);
		assertEquals("Price", expected.getPrice(), actual.getPrice(), DELTA);
		assertEqualsDiscountExpression(expected.getDiscountExpression(), actual.getDiscountExpression());
		assertEquals("TaxableBase", expected.getTaxableBase(), actual.getTaxableBase(), DELTA);
		assertEquals("Taxes", expected.getTaxes(), actual.getTaxes(), DELTA);
		assertEquals("Prepayment", expected.isPrepayment(), actual.isPrepayment());
		assertEquals("Warehouse", expected.getWarehouse(), actual.getWarehouse());
		assertEquals("WarehouseName", expected.getWarehouseName(), actual.getWarehouseName());
		assertEqualsWorkplace(expected.getWorkplace(), actual.getWorkplace());
		assertEqualsAccount(expected.getExpAccount(), actual.getExpAccount());
		assertEqualsInvoiceTaxes( expected.getInvoiceTaxes(), actual.getInvoiceTaxes() );
		assertEquals("Source", expected.getSource(), actual.getSource());
		assertEquals("sourceId", expected.getSourceId(), actual.getSourceId());
		assertEqualsPurchaseDetail( expected.getPurchaseDetail(), actual.getPurchaseDetail() );
		assertEqualsSalesDetail( expected.getSalesDetail(), actual.getSalesDetail() );
		assertEqualsDeliveryDetail( expected.getDeliveryDetail(), actual.getDeliveryDetail() );
		assertEqualsIncomeDetail( expected.getIncomeDetail(), actual.getIncomeDetail() );
		assertEqualsOfferDetail( expected.getOfferDetail(), actual.getOfferDetail() );
	}
	
	public static void assertEqualsInvoiceTaxes(LinkedList<InvoiceTax> expected, LinkedList<InvoiceTax> actual) {
		assertEqualsCollection("Taxes", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsInvoiceTax(expected.get(i),actual.get(i)));
	}
	
	public static void assertEqualsInvoiceTax(InvoiceTax expected, InvoiceTax actual) {
		assertEqualsNulls( "InvoiceTax", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		// assertEquals("InvoiceDetail", expected.getInvoiceDetail(), actual.getInvoiceDetail());	
		assertEquals("TaxType", expected.getTaxType(), actual.getTaxType());
		assertEquals("Base", expected.getBase(), actual.getBase(), DELTA);
		assertEquals("Percentage", expected.getPercentage(), actual.getPercentage(), DELTA);
		assertEquals("Quota", expected.getQuota(), actual.getQuota(), DELTA);
		assertEquals("Surcharge", expected.getSurcharge(), actual.getSurcharge(), DELTA);
		assertEquals("SurchargeQuota", expected.getSurchargeQuota(), actual.getSurchargeQuota(), DELTA);
		assertEquals("DeductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota(), DELTA);
		assertEquals("WithholdingType", expected.getWithholdingType(), actual.getWithholdingType()); 
		assertEquals("VatDeductionType", expected.getVatDeductionType(), actual.getVatDeductionType());
		assertEquals("DeductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent(), DELTA);
		assertEquals("DirectTaxPercent", expected.getDirectTaxPercent(), actual.getDirectTaxPercent(), DELTA);
		assertEquals("Account", expected.getAccount(), actual.getAccount());
	}
	
	public static void assertEqualsTaxBreakdown(TaxBreakdown expected, TaxBreakdown actual) {
		assertEqualsNulls( "TaxBreakdown", expected, actual);
		if ( expected != null) {
			assertEqualsInvoiceBreakdowns( expected.getBreakdown(), actual.getBreakdown() );
		}
	}

	public static void assertEqualsInvoiceBreakdowns(List<InvoiceBreakdown> expected, List<InvoiceBreakdown> actual) {
		assertEqualsCollection("InvoiceBreakdowns", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsInvoiceBreakdown(expected.get(i),actual.get(i)));
	}

	public static void assertEqualsInvoiceBreakdown(InvoiceBreakdown expected, InvoiceBreakdown actual) {
		assertEqualsNulls( "InvoiceBreakdown", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Invoice", expected.getInvoice(), actual.getInvoice());	
		assertEquals("TaxType", expected.getTaxType(), actual.getTaxType());
		assertEquals("Base", expected.getBase(), actual.getBase(), DELTA);
		assertEquals("Percentage", expected.getPercentage(), actual.getPercentage(), DELTA);
		assertEquals("Quota", expected.getQuota(), actual.getQuota(), DELTA);
		assertEquals("Surcharge", expected.getSurcharge(), actual.getSurcharge(), DELTA);
		assertEquals("SurchargeQuota", expected.getSurchargeQuota(), actual.getSurchargeQuota(), DELTA);
		assertEquals("DeductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota(), DELTA);
		assertEquals("WithholdingType", expected.getWithholdingType(), actual.getWithholdingType()); 
		assertEquals("VatDeductionType", expected.getVatDeductionType(), actual.getVatDeductionType());
	}

	public static void assertEqualsInvoiceFiscal(InvoiceFiscal expected, InvoiceFiscal actual) {
		if (expected != null) {
			assertEqualsNulls( "InvoiceFiscal", expected, actual);
			assertEquals("Invoice", expected.getInvoice(), actual.getInvoice());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			for (VATTaxRegime vatRegime : VATTaxRegime.values()) {
				assertEquals("InvoiceFiscal " + vatRegime.getName(),expected.isVatRegimeEnabled(vatRegime),actual.isVatRegimeEnabled(vatRegime));	
			}
		}
	}
	
	public static void assertEqualsFullInvoice(Invoice expected, Invoice actual) {
		assertEqualsNulls( "FullInvoice", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEqualsEnterpriseActivity(expected.getActivity(), actual.getActivity());
		assertEquals("Epigraph", expected.getEpigraph(), actual.getEpigraph());
		assertEquals("InvestAsset", expected.getInvestAsset(), actual.getInvestAsset());
		assertEquals("Project", expected.getProject(), actual.getProject());
		assertEquals("Series", expected.getSeries(), actual.getSeries());
		assertEquals("Number", expected.getNumber(), actual.getNumber());
		assertEquals("ReferenceCode", expected.getReferenceCode(), actual.getReferenceCode());
		assertEquals("IssueDate", expected.getIssueDate(), actual.getIssueDate());
		assertEquals("TaxDate", expected.getTaxDate(), actual.getTaxDate());
		assertEquals("RectificationType", expected.getRectificationType(), actual.getRectificationType());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("RectificationInvoice", expected.getRectificationInvoice(), actual.getRectificationInvoice());
		assertEquals("RectificationInvoiceSeries", expected.getRectificationInvoiceSeries(), actual.getRectificationInvoiceSeries());
		assertEquals("RectificationInvoiceReference", expected.getRectificationInvoiceReference(), actual.getRectificationInvoiceReference());
		assertEquals("RectificationInvoiceNumber", expected.getRectificationInvoiceNumber(), actual.getRectificationInvoiceNumber());		
		assertEquals("RectificationInvoiceDate", expected.getRectificationInvoiceDate(), actual.getRectificationInvoiceDate());
		assertEquals("RegistryAddress", expected.getRegistryAddress(), actual.getRegistryAddress());
		assertEqualsRegistryAddress (expected.getAddress(), actual.getAddress());
		assertEquals("Registry", expected.getRegistry(), actual.getRegistry());
		assertEquals("RegistryDocument", expected.getRegistryDocument(), actual.getRegistryDocument());
		assertEquals("RegistryDocumentType", expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
		assertEquals("RegistryDocumentCountry", expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
		assertEquals("RegistryName", expected.getRegistryName(), actual.getRegistryName());
		assertEqualsAccount(expected.getRegistryAccount(), actual.getRegistryAccount());
		assertEqualsScope(expected.getScope(), actual.getScope());
		assertEquals("Type", expected.getType(), actual.getType());
		assertEquals("Transaction", expected.getTransaction(), actual.getTransaction());
		assertEquals("Recorded", expected.isRecorded(), actual.isRecorded());
		assertEquals("Surcharge", expected.isSurcharge(), actual.isSurcharge());

		assertEquals("Withholding", expected.isWithholding(), actual.isWithholding());
		assertEquals("WithholdingFarmer", expected.isWithholdingFarmer(), actual.isWithholdingFarmer());
		assertEquals("VatAccrualPayment", expected.isVatAccrualPayment(), actual.isVatAccrualPayment());
		assertEquals("Investment", expected.isInvestment(), actual.isInvestment());
		assertEquals("Service", expected.isService(), actual.isService());
		assertEquals("Advance", expected.isAdvance(), actual.isAdvance());
		assertEquals("Signed", expected.isSigned(), actual.isSigned());
		assertEquals("Annulled", expected.isAnnulled(), actual.isAnnulled());
		assertEquals("TaxableBase", expected.getTaxableBase(), actual.getTaxableBase(), DELTA);
		assertEquals("VatQuota", expected.getVatQuota(), actual.getVatQuota(), DELTA);
		assertEquals("RetentionQuota", expected.getRetentionQuota(), actual.getRetentionQuota(), DELTA);
		assertEquals("Total", expected.getTotal(), actual.getTotal(), DELTA);
		assertEquals("PosShift", expected.getPosShift(), actual.getPosShift());
		assertEquals("Seller", expected.getSeller(), actual.getSeller());
		assertEquals("SellerName", expected.getSellerName(), actual.getSellerName());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
		assertEquals("SIIStatus", expected.getSiiStatus(), actual.getSiiStatus());
		
		assertEqualsInvoiceDetails(expected.getDetails(), actual.getDetails());
		assertEqualsFinances(expected.getFinances(), actual.getFinances());
		assertEqualsInvoiceFiscal(expected.getFiscal(), actual.getFiscal());
		assertEquals("TediCategory", expected.getTediCategory(), actual.getTediCategory());
		assertEquals("FileUrl", expected.getFileUrl(), actual.getFileUrl());
		assertEqualsInvoiceInfo(expected.getInvoiceInfo(), actual.getInvoiceInfo());
		assertEqualsInvoiceErrors(expected.getMessages(), actual.getMessages());
		assertEquals("Recordable", expected.isRecordable(), actual.isRecordable());
		assertEquals("Selected", expected.isSelected(), actual.isSelected());
		
		assertEqualsAttach(expected.getAttach().orElse(null), actual.getAttach().orElse(null));
		
		//assertEqualsInvoiceBreakdowns(expected.getBreakdown(), actual.getBreakdown());
		//assertEqualsTaxBreakdown(expected.getTaxBreakdown().orElse(null), actual.getTaxBreakdown().orElse(null));
		//assertEqualsRegistry(expected.getRegistryData(), actual.getRegistryData());
	}
	
	public static void assertEqualsAttach(Attach expected, Attach actual) {
		assertEqualsNulls( "Attach", expected, actual);
		if (expected != null) {
			assertEquals("AttachType", expected.getAttachType(), actual.getAttachType());
			assertEquals("AttachModule", expected.getAttachModule(), actual.getAttachModule());
			assertEquals("AttachURL", expected.getAttachURL(), actual.getAttachURL());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEqualsDomain(expected.getDomain(), actual.getDomain());
			assertEquals("MimeType", expected.getMimeType(), actual.getMimeType());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			// private byte[] data;
			assertEquals("Date", expected.getDate(), actual.getDate());
			assertEquals("Type", expected.getType(), actual.getType());
			assertEquals("DriveId", expected.getDriveId(), actual.getDriveId());
			assertEquals("Scope", expected.getScope(), actual.getScope()); 
			assertEquals("Confidential", expected.getConfidential(), actual.getConfidential());
			assertEquals("Category", expected.getCategory(), actual.getCategory());
			assertEquals("DparentId", expected.getDparentId(), actual.getDparentId());
			assertEquals("SourceBatch", expected.getSourceBatch(), actual.getSourceBatch());
			assertEquals("SourceType", expected.getSourceType(), actual.getSourceType());
			assertEquals("Icon", expected.getIcon(), actual.getIcon());				
			assertEquals("Md5", expected.getMd5(), actual.getMd5());
			assertEquals("IsDrive", expected.getIsDrive(), actual.getIsDrive());
			assertEqualsScope(expected.getFullScope(), actual.getFullScope());
			assertEquals("Category",expected.getCategory(), actual.getCategory());
			assertEqualsTags(expected.getTagList(), actual.getTagList());
		}
		
	}
	
	public static void assertEqualsTags(List<Tag> expected, List<Tag> actual) {
		assertEqualsCollection("InvoiceErrors", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsTag(expected.get(i),actual.get(i)));
	}

	public static void assertEqualsCategory(Category expected, Category actual) {
		assertEqualsNulls( "Category", expected, actual);
		assertEquals("Description", expected.getDescription(), actual.getDescription());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Rattach", expected.getRattach(), actual.getRattach());
		assertEquals("Name", expected.getName(), actual.getName());
		assertEquals("Scope", expected.getScope(), actual.getScope()); 
		assertEquals("Type", expected.getType(), actual.getType());
		assertEquals("Url", expected.getUrl(), actual.getUrl());
		
	}

	public static void assertEqualsInvoiceErrors(List<InvoiceError> expected, List<InvoiceError> actual) {
		assertEqualsCollection("InvoiceErrors", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsInvoiceError(expected.get(i),actual.get(i)));
	}

	public static void assertEqualsInvoiceError(InvoiceError expected, InvoiceError actual) {
		assertEqualsNulls( "InvoiceError", expected, actual);
		assertEquals("Code", expected.getCode(), actual.getCode());
		assertEquals("Level", expected.getLevel(), actual.getLevel());
		assertEqualsInvoiceErrorContext( expected.getContext(), actual.getContext());
		assertEquals("Message", expected.getMessage(), actual.getMessage());
	}
	public static void assertEqualsInvoiceErrorContext(InvoiceErrorContext expected, InvoiceErrorContext actual) {
		assertEquals("Key", expected.getKey(), actual.getKey());
		assertEquals("Line", expected.getLine(), actual.getLine());
	}

	public static void assertEqualsInvoiceInfo(InvoiceInfo expected, InvoiceInfo actual) {
		assertEqualsNulls( "FullInvoice", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Invoice", expected.getInvoice(), actual.getInvoice());
		assertEquals("InvoiceCommunicationType", expected.getType(), actual.getType());
		assertEquals("InvoiceCommunicationStatus", expected.getStatus(), actual.getStatus());
	}
	
	public static void assertEqualsFinances(List<Finance> expected, List<Finance> actual) {
		assertEqualsCollection("Finances", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsFinance(expected.get(i),actual.get(i)));
	}
	
	public static void assertEqualsFinance(Finance expected, Finance actual) {
		assertEqualsNulls( "Finance", expected, actual);
		assertEquals("Dirty", expected.isDirty(), actual.isDirty());
		assertEquals("Selected", expected.isSelected(), actual.isSelected());
		assertEquals("Removed", expected.isRemoved(), actual.isRemoved());
		assertEquals("Id", expected.getId(), actual.getId());
		assertEqualsInvoice( expected.getInvoice(), actual.getInvoice());
		assertEquals("PayMethod", expected.getPayMethod(), actual.getPayMethod());
		assertEquals("PayMethodType", expected.getPayMethodType(), actual.getPayMethodType());
		assertEquals("PayMethodName", expected.getPayMethodName(), actual.getPayMethodName());
		assertEqualsRegistry( expected.getRegistry(), actual.getRegistry());
		assertEqualsScope( expected.getScope(), actual.getScope());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Payment", expected.isPayment(), actual.isPayment());
		assertEquals("RegistryDocument", expected.getRegistryDocument(), actual.getRegistryDocument());
		assertEquals("RegistryDocumentType", expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
		assertEquals("RegistryDocumentCountry", expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
		assertEquals("RegistryName", expected.getRegistryName(), actual.getRegistryName());
		assertEquals("RegistryAccountId", expected.getRegistryAccountId(), actual.getRegistryAccountId());
		assertEquals("RegistryAccountCode", expected.getRegistryAccountCode(), actual.getRegistryAccountCode());
		assertEquals("RegistryAccountDescription", expected.getRegistryAccountDescription(), actual.getRegistryAccountDescription());
		assertEquals("Amount", expected.getAmount(), actual.getAmount(), DELTA);
		assertEquals("Expenses", expected.getExpenses(), actual.getExpenses(), DELTA);
		assertEquals("Concept", expected.getConcept(), actual.getConcept());
		assertEquals("DueDate", expected.getDueDate(), actual.getDueDate());
		assertEqualsBankAccount(expected.getBankAccount(), actual.getBankAccount());
		assertEquals("BankAlias", expected.getBankAlias(), actual.getBankAlias());
		assertEquals("Bic", expected.getBic(), actual.getBic());
		assertEquals("ChequeNumber", expected.getChequeNumber(), actual.getChequeNumber());
		assertEquals("FinanceStatus", expected.getFinanceStatus(), actual.getFinanceStatus());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
		assertEquals("Manual", expected.isManual(), actual.isManual());
		assertEquals("Advance", expected.isAdvance(), actual.isAdvance());
		assertEquals("Payroll", expected.isPayroll(), actual.isPayroll());
		assertEquals("Prepayment", expected.isPrepayment(), actual.isPrepayment());
		assertEquals("SourceId", expected.getSourceId(), actual.getSourceId());
		assertEquals("FinanceGroup", expected.getFinanceGroup(), actual.getFinanceGroup());
	}
		
	public static void assertEmployee(Employee expected, Employee actual) {
		assertEquals("Ccc",expected.getCcc(), actual.getCcc());
		assertEquals("Naf", expected.getNaf(), actual.getNaf());
		assertEquals("Dni",expected.getDni(), actual.getDni());
		assertEquals("StartDate",expected.getStartDate(), actual.getStartDate());
		assertEquals("EndDate",expected.getEndDate(), actual.getEndDate());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Regime",expected.getRegime(), actual.getRegime());
		assertEquals("Factor",expected.getFactor(), actual.getFactor());
		assertEquals("Occupation",expected.getOccupation(), actual.getOccupation());
		assertEquals("ContractType",expected.getContractType(), actual.getContractType());
		assertEquals("Rlce",expected.getRlce(), actual.getRlce());
		assertEquals("QuoteGroup",expected.getQuoteGroup(), actual.getQuoteGroup());
		assertEquals("Category",expected.getCategory(), actual.getCategory());
		assertEquals("Sex",expected.getSex(), actual.getSex());
		assertEquals("BirthDate",expected.getBirthDate(), actual.getBirthDate());
	}

	public static void assertEqualsIrpfBreakdown (IrpfBreakdown expected, IrpfBreakdown actual) {
		assertEqualsNulls( "IrpfBreakdown", expected, actual);
		if (expected != null ) {
			assertEquals("activity", expected.getActivity(), actual.getActivity());
			assertEquals("activityDescription", expected.getActivityDescription(), actual.getActivityDescription());
			assertEquals("epigraph", expected.getEpigraph(), actual.getEpigraph());
			assertEquals("registryDocument", expected.getRegistryDocument(), actual.getRegistryDocument());
			assertEquals("registryDocumentType", expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
			assertEquals("registryDocumentCountry", expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
			assertEquals("name", expected.getName(), actual.getName());
			assertEquals("issueDate", expected.getIssueDate(), actual.getIssueDate());
			assertEquals("chargeDate", expected.getChargeDate(), actual.getChargeDate());
			assertEquals("fromSalary", expected.isFromSalary(), actual.isFromSalary());
			assertEquals("insidePeriod", expected.isInsidePeriod(), actual.isInsidePeriod()); 
			assertEquals("salary", expected.getSalary(), actual.getSalary());
			assertEquals("invoiceType", expected.getInvoiceType(), actual.getInvoiceType());
			assertEquals("invoice", expected.getInvoice(), actual.getInvoice());
			assertEquals("series", expected.getSeries(), actual.getSeries());
			assertEquals("number", expected.getNumber(), actual.getNumber());
			assertEquals("referenceCode", expected.getReferenceCode(), actual.getReferenceCode()); 
			assertEquals("taxDate", expected.getTaxDate(), actual.getTaxDate());
			assertEquals("withholdingType", expected.getWithholdingType(), actual.getWithholdingType()); 
			assertEquals("regime", expected.getIRPFRegime(), actual.getIRPFRegime());
			assertEquals("inKind", expected.isInKind(), actual.isInKind());
			assertEqualsDouble("base", expected.getBase(), actual.getBase());
			assertEqualsDouble("percent", expected.getPercent(), actual.getPercent());
			assertEqualsDouble("quota", expected.getQuota(), actual.getQuota());
			assertEqualsDouble("deductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent());
			assertEqualsDouble("deductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota());
			assertEquals("groupByNif", expected.getGroupedBy(), actual.getGroupedBy());
			assertEquals("zip", expected.getZip(), actual.getZip());
			assertEquals("city", expected.getCity(), actual.getCity());
		}
	}
	
	public static void assertEqualsVatContext (VatContext expected, VatContext actual) {
		assertEqualsNulls( "VatContext", expected, actual);
		if (expected != null ) {
			assertEquals("invoice", expected.getInvoice(), actual.getInvoice());
			assertEquals("activity", expected.getActivity(), actual.getActivity());
			assertEquals("activityDescription", expected.getActivityDescription(), actual.getActivityDescription());
			assertEquals("vatRegime", expected.getVatRegime(), actual.getVatRegime());
			assertEquals("vatSurchargeRegime", expected.isVatSurchargeRegime(), actual.isVatSurchargeRegime());
			assertEquals("epigraph", expected.getEpigraph(), actual.getEpigraph());
			assertEquals("documentNumber", expected.getDocumentNumber(), actual.getDocumentNumber());
			assertEquals("referenceCode", expected.getReferenceCode(), actual.getReferenceCode());
			assertEquals("registryDocument", expected.getRegistryDocument(), actual.getRegistryDocument());
			assertEquals("registryDocumentType", expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
			assertEquals("registryDocumentCountry", expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
			assertEquals("registry", expected.getRegistry(), actual.getRegistry());
			assertEquals("registryName", expected.getRegistryName(), actual.getRegistryName());
			assertEquals("issueDate", expected.getIssueDate(), actual.getIssueDate());
			assertEquals("taxDate", expected.getTaxDate(), actual.getTaxDate());
			assertEquals("creationDate", expected.getCreationDate(), actual.getCreationDate());
			assertEquals("regContableDate", expected.getRegContableDate(), actual.getRegContableDate());
			assertEquals("detailDescription", expected.getDetailDescription(), actual.getDetailDescription());
			assertEquals("insidePeriod", expected.isInsidePeriod(), actual.isInsidePeriod());
			assertEquals("invoiceType", expected.getInvoiceType(), actual.getInvoiceType());
			assertEquals("rectificationType", expected.getRectificationType(), actual.getRectificationType());
			assertEquals("rectificationInvoice", expected.getRectificationInvoice(), actual.getRectificationInvoice());
			assertEquals("service", expected.isService(), actual.isService());
			assertEquals("transaction", expected.getTransaction(), actual.getTransaction());
			assertEquals("investment", expected.isInvestment(), actual.isInvestment());
			assertEquals("vatAccrualRegime", expected.isVatAccrualRegime(), actual.isVatAccrualRegime());
			assertEquals("vatDeductionType", expected.getVatDeductionType(), actual.getVatDeductionType());
			assertEquals("farmerRegime", expected.isFarmerRegime(), actual.isFarmerRegime());
			assertEquals("prepayment", expected.isPrepayment(), actual.isPrepayment());
			assertEquals("vatImportation", expected.isVatImportation(), actual.isVatImportation());
			assertEquals("duaLinked", expected.hasDuaLinked(), actual.hasDuaLinked());
			assertEqualsDouble("base", expected.getBase(), actual.getBase());
			assertEqualsDouble("percentage", expected.getPercentage(), actual.getPercentage());
			assertEqualsDouble("quota", expected.getQuota(), actual.getQuota());
			assertEquals("investAsset", expected.getInvestAsset(), actual.getInvestAsset());
			assertEqualsDouble("deductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent());
			assertEqualsDouble("deductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota());
			assertEquals("surcharge", expected.isSurcharge(), actual.isSurcharge());
			assertEqualsDouble("surchargePercent", expected.getSurchargePercent(), actual.getSurchargePercent());
			assertEqualsDouble("surchargeQuota", expected.getSurchargeQuota(), actual.getSurchargeQuota());
			assertEquals("prorrated", expected.isProrrated(), actual.isProrrated());
			assertEqualsDouble("prorratePercent", expected.getProrratePercent(), actual.getProrratePercent());
			assertEqualsDouble("prorrateQuota", expected.getProrrateQuota(), actual.getProrrateQuota());
			assertEquals("siiStatus", expected.getSiiStatus(), actual.getSiiStatus());
			assertEquals("amortizationDescription", expected.getAmortizationDescription(), actual.getAmortizationDescription());
			assertEquals("amortizationPercentage", expected.getAmortizationPercentage(), actual.getAmortizationPercentage());
			assertEquals("amortizationInitialDate", expected.getAmortizationInitialDate(), actual.getAmortizationInitialDate());
			assertEquals("financePending", expected.isFinancePending(), actual.isFinancePending()); 
			assertEqualsDouble("amount347", expected.getAmount347(), actual.getAmount347());
			assertEquals("hasRetention", expected.hasRetention(), actual.hasRetention());
			assertEquals("rectificateInvoiceTaxDate", expected.getRectificateInvoiceTaxDate(), actual.getRectificateInvoiceTaxDate());
			assertEquals("rectificateYear", expected.getRectificateYear(), actual.getRectificateYear());
			assertEquals("rectificatePeriod", expected.getRectificatePeriod(), actual.getRectificatePeriod());
		}
	}
	
	

	public static <T extends FiscalModel> void assertFiscalModel(T expected, T actual) {
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("DomainName", expected.getDomainName(), actual.getDomainName());
		assertEquals("Year", expected.getYear(), actual.getYear());
		assertEquals("Finance", expected.getFinance(), actual.getFinance());
		assertEquals("Model", expected.getModel(), actual.getModel());
		assertEquals("Period", expected.getPeriod(), actual.getPeriod());
		assertEquals("Administration", expected.getAdministration(), actual.getAdministration());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
		assertEquals("DeclarationResult", expected.getDeclarationResult(), actual.getDeclarationResult());
		assertEquals("DeclarationType", expected.getDeclarationResultType(), actual.getDeclarationResultType());
		assertEquals("Confidential", expected.isConfidential(), actual.isConfidential());
		assertEquals("Complementary", expected.isComplementary(), actual.isComplementary());
		assertEquals("Replacement", expected.isReplacement(), actual.isReplacement());
		assertEquals("WithoutActivity", expected.isWithoutActivity(), actual.isWithoutActivity());
		assertEquals("Number", expected.getNumber(), actual.getNumber());
		assertEquals("ReplacedNumber", expected.getReplacedNumber(), actual.getReplacedNumber());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Document", expected.getDocument(), actual.getDocument());
		assertEquals("Surname", expected.getSurname(), actual.getSurname());
		assertEquals("Name", expected.getName(), actual.getName());
		assertEquals("StreetInitial", expected.getStreetInitial(), actual.getStreetInitial());
		assertEquals("StreetName", expected.getStreetName(), actual.getStreetName());
		assertEquals("StreetNumber", expected.getStreetNumber(), actual.getStreetNumber());
		assertEquals("StreetStair", expected.getStreetStair(), actual.getStreetStair());
		assertEquals("StreetFloor", expected.getStreetFloor(), actual.getStreetFloor());
		assertEquals("StreetDoor", expected.getStreetDoor(), actual.getStreetDoor());
		assertEquals("Phone", expected.getPhone(), actual.getPhone());
		assertEquals("Town", expected.getTown(), actual.getTown());
		assertEquals("Province", expected.getProvince(), actual.getProvince());
		assertEquals("Zip", expected.getZip(), actual.getZip());
		assertEquals("AdmonAeat", expected.getAdmonAeat(), actual.getAdmonAeat());
		assertEquals("ContactPerson", expected.getContactPerson(), actual.getContactPerson());
		assertEquals("ContactPhone", expected.getContactPhone(), actual.getContactPhone());
		assertEquals("ContactCellular", expected.getContactCellular(), actual.getContactCellular());
		assertEquals("ContactEmail", expected.getContactEmail(), actual.getContactEmail());
		assertEquals("Iban", expected.getIban(), actual.getIban());
	}
	public static void assertMod111(Mod111 expected, Mod111 actual) {
		assertFiscalModel(expected, actual);
	}
	public static void assertMod115(Mod115 expected, Mod115 actual) {
		assertFiscalModel(expected, actual);
	}
	public static void assertMod123(Mod123 expected, Mod123 actual) {
		assertFiscalModel(expected, actual);
	}
	public static void assertMod130(Mod130 expected, Mod130 actual) {
		assertFiscalModel(expected, actual);
	}
	public static void assertMod303(Mod303 expected, Mod303 actual) {
		assertFiscalModel(expected, actual);
	}
	public static void assertMod390HF(Mod390HF expected, Mod390HF actual) {
		assertFiscalModel(expected, actual);
	}
	public static void assertMod190(Mod190 expected, Mod190 actual) {
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("DomainName", expected.getDomainName(), actual.getDomainName());
		assertEquals("Enterprise", expected.getEnterprise(), actual.getEnterprise());
		assertEquals("Year", expected.getYear(), actual.getYear());
		assertEquals("Administration", expected.getAdministration(), actual.getAdministration());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
		assertEquals("Confidential", expected.isConfidential(), actual.isConfidential());
		assertEquals("Complementary", expected.isComplementary(), actual.isComplementary());
		assertEquals("Replacement", expected.isReplacement(), actual.isReplacement());
		assertEquals("Receipt", expected.getReceipt(), actual.getReceipt());
		assertEquals("ReplacedReceipt", expected.getReplacedReceipt(), actual.getReplacedReceipt());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Document", expected.getDocument(), actual.getDocument());
		assertEquals("Name", expected.getName(), actual.getName());
		assertEquals("ContactPerson", expected.getContactPerson(), actual.getContactPerson());
		assertEquals("ContactPhone", expected.getContactPhone(), actual.getContactPhone());
		assertEquals("ContactMail", expected.getContactMail(), actual.getContactMail());
		assertEquals("ReceiverCountTotal", expected.getReceiverCountTotal(), actual.getReceiverCountTotal());
		assertEquals("receiptTotal", expected.getReceiptTotal(), actual.getReceiptTotal(), DELTA);
		assertEquals("retentionTotal", expected.getRetentionTotal(), actual.getRetentionTotal(), DELTA);
	}
	
	public static void assertMod349(Mod349 expected, Mod349 actual) {
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Year", expected.getYear(), actual.getYear());
		assertEquals("Period", expected.getPeriod(), actual.getPeriod());
		assertEquals("Administration", expected.getAdministration(), actual.getAdministration());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
		assertEquals("Confidential", expected.isConfidential(), actual.isConfidential());
		assertEquals("Complementary", expected.isComplementary(), actual.isComplementary());
		assertEquals("Replacement", expected.isReplacement(), actual.isReplacement());
		assertEquals("Number", expected.getNumber(), actual.getNumber());
		assertEquals("ReplacedNumber", expected.getReplacedNumber(), actual.getReplacedNumber());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Document", expected.getDocument(), actual.getDocument());
		assertEquals("Name", expected.getName(), actual.getName());
		assertEquals("ContactPerson", expected.getContactPerson(), actual.getContactPerson());
		assertEquals("ContactPhone", expected.getContactPhone(), actual.getContactPhone());
		assertEquals("ContactMail", expected.getContactMail(), actual.getContactMail());
		assertEquals("RepresentativeDocument", expected.getRepresentativeDocument(), actual.getRepresentativeDocument());
	}
	
	public static void assertEqualsOffer(Offer expected, Offer actual) {
		// TODO Completar...
		assertEqualsNulls( "Offer", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Status", expected.getStatus().value(), actual.getStatus().value());
		}
	}
	
	public static void assertEqualsOfferDetail(OfferDetail expected, OfferDetail actual) {
		assertEqualsNulls( "Offer Detail", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Offer", expected.getOffer().getId(), actual.getOffer().getId());
			assertEquals("Line", expected.getLine(), actual.getLine());
			assertEquals("Item", expected.getItem().getId(), actual.getItem().getId());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Quantity", expected.getQuantity(), actual.getQuantity(), DELTA);
			assertEquals("Discount Expression", expected.getDiscountExpression(), actual.getDiscountExpression());
			assertEquals("Status", expected.getStatus().value(), actual.getStatus().value());
		}
	}
	
	public static void assertEqualsDelivery(Delivery expected, Delivery actual) {
		assertEqualsNulls( "Delivery", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals(expected.getAddress().getId(), actual.getAddress().getId());
			assertEquals("BankAccount", expected.getBankAccount(), actual.getBankAccount());
			assertEquals("Bank alias", expected.getBankAlias(), actual.getBankAlias());
			assertEquals("Bic", expected.getBic(), actual.getBic());
			assertEquals("Carrier", expected.getCarrier(), actual.getCarrier());
			assertEquals("Carrier Packing", expected.getCarrierPacking(), actual.getCarrierPacking());
			assertEquals("Comments", expected.getComments(), actual.getComments());
			assertEquals("Creation user", expected.getCreationUser(), actual.getCreationUser());
			assertEquals(expected.getCustomer().getId(), actual.getCustomer().getId());
			assertEquals("Days between pymnt", expected.getDaysBetweenPymnt(), actual.getDaysBetweenPymnt());
			assertEquals("Days to first pymnt", expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt());
			assertEquals("Details", expected.getDetails(), actual.getDetails());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Driver", expected.getDriver(), actual.getDriver());
			assertEquals("Driver document", expected.getDriverDocument(), actual.getDriverDocument());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Modification user", expected.getModificationUser(), actual.getModificationUser());
			assertEquals("Number", expected.getNumber(), actual.getNumber());
			assertEquals("Number of Pymnts", expected.getNumberOfPymnts(), actual.getNumberOfPymnts());
			assertEquals("Number plate", expected.getNumberPlate(), actual.getNumberPlate());
			assertEquals("Packaging", expected.getPackaging(), actual.getPackaging());
			assertEquals("Packaging data", expected.getPackagingData(), actual.getPackagingData());
			assertEquals(expected.getPayMethod().getId(), actual.getPayMethod().getId());
			assertEquals(expected.getProject().getId(), actual.getProject().getId());
			assertEquals("Pymnt days", expected.getPymntDays(), actual.getPymntDays());
			assertEquals("Reference code", expected.getReferenceCode(), actual.getReferenceCode());
			assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
			assertEquals(expected.getScope().getId(), actual.getScope().getId());
			assertEquals("Security level", expected.getSecurityLevel(), actual.getSecurityLevel());
			assertEquals("Series", expected.getSeries(), actual.getSeries());
			assertEquals("Shipping alternative address", expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress());
			assertEquals("Shipping alternative address two", expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2());
			assertEquals("Shipping alternative city", expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity());
			assertEquals("Shipping alternative phone", expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone());
			assertEquals("Shipping alternative recipient", expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient());
			assertEquals("Shipping alternative zip", expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip());
			assertEquals("Shipping contact", expected.getShippingContact(), actual.getShippingContact());
			assertEquals("Shipping period", expected.getShippingPeriod(), actual.getShippingPeriod());
			assertEquals("Shipping period value", expected.getShippingPeriodValue(), actual.getShippingPeriodValue());
			assertEquals("Shipping status", expected.getShippingStatus(), actual.getShippingStatus());
			assertEquals("Shipping status value", expected.getShippingStatusValue(), actual.getShippingStatusValue());
			assertEquals("Status", expected.getStatus(), actual.getStatus());
			assertEquals("Total packages", expected.getTotalPackages(), actual.getTotalPackages());
			assertEquals("Total weight", expected.getTotalWeight(), actual.getTotalWeight());
			assertEquals("Tracking number", expected.getTrackingNumber(), actual.getTrackingNumber());
			assertEquals(expected.getWorkplace().getId(), actual.getWorkplace().getId());
		}
	}
	
	public static void assertEqualsWorkplace(Workplace expected, Workplace actual) {
		assertEqualsNulls("Workplace", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals("Address", expected.getAddress(), actual.getAddress());
			assertEquals("Customer", expected.getCustomer(), actual.getCustomer());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Economicagreement", expected.getEconomicagreement(), actual.getEconomicagreement());
			assertEquals("Enterprise", expected.getEnterprise(), actual.getEnterprise());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Scope", expected.getScope(), actual.getScope());
		}
	}
	
	public static void assertEqualsEdiCodes(EdiCodes expected, EdiCodes actual) {
		assertEqualsNulls("EdiCodes", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals("Bycode", expected.getBycode(), actual.getBycode());
			assertEquals("Company EdiCode", expected.getCompanyEdiCode(), actual.getCompanyEdiCode());
			assertEquals("Customer EdiCode", expected.getCustomerEdiCode(), actual.getCustomerEdiCode());
			assertEquals("Customer EdiHeader", expected.getCustomerEdiHeader(), actual.getCustomerEdiHeader());
			assertEquals("Customer EdiInvoice", expected.getCustomerEdiInvoice(), actual.getCustomerEdiInvoice());
			assertEquals("Customer EdiPoint", expected.getCustomerEdiPoint(), actual.getCustomerEdiPoint());
			assertEquals("Customer Package", expected.getCustomerPackage(), actual.getCustomerPackage());
			assertEquals("Delivery Point EdiCode", expected.getDeliveryPointEdiCode(), actual.getDeliveryPointEdiCode());
			assertEquals("Customer Department", expected.getDepartment(), actual.getDepartment());
			assertEquals("Customer Dpcode", expected.getDpcode(), actual.getDpcode());
			assertEquals("Customer Ivcode", expected.getIvcode(), actual.getIvcode());
			assertEquals("Customer Mrcode", expected.getMrcode(), actual.getMrcode());
			assertEquals("Customer Mscode", expected.getMscode(), actual.getMscode());
			assertEquals("Customer Pwcode", expected.getPwcode(), actual.getPwcode());
			assertEquals("Customer Shcode", expected.getShcode(), actual.getShcode());
			assertEquals("Customer Sucode", expected.getSucode(), actual.getSucode());
			assertEquals("Customer Uccode", expected.getUccode(), actual.getUccode());
		}
	}
	
	public static void assertEqualsDeliveryDetail(DeliveryDetail expected, DeliveryDetail actual) {
		assertEqualsNulls("DeliveryDetail", expected, actual);
		if (expected != null) {
			assertEquals("Creation user", expected.getCreationUser(), actual.getCreationUser());
			assertEquals(expected.getDelivery().getId(), actual.getDelivery().getId());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Discount expression", expected.getDiscountExpression(), actual.getDiscountExpression());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals(expected.getItem().getId(), actual.getItem().getId());
			assertEquals("Line", expected.getLine(), actual.getLine());
			assertEquals("Modification user", expected.getModificationUser(), actual.getModificationUser());
			assertEquals("Price", expected.getPrice(), actual.getPrice());
			//assertEquals("Purchased reference", expected.getPurchaseReference(), actual.getPurchaseReference());
			assertEquals("Quantity", expected.getQuantity(), actual.getQuantity(), DELTA);
			assertEquals("Sales detail", expected.getSalesDetail(), actual.getSalesDetail());
			assertEquals("Sales detail data", expected.getSalesDetailData(), actual.getSalesDetailData());
			assertEquals("Warehouse", expected.getWarehouse(), actual.getWarehouse());
		}
	}
	
	public static void assertEqualsSales(Sales expected, Sales actual) {
		assertEqualsNulls("Sales", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEqualsProject( expected.getProject(), actual.getProject());
		assertEqualsCustomer(expected.getCustomer(), actual.getCustomer());
		assertEquals("Series", expected.getSeries(), actual.getSeries());
		assertEquals("Number", expected.getNumber(), actual.getNumber());
		assertEquals("PurchaseReference", expected.getPurchaseReference(), actual.getPurchaseReference());
		assertEqualsRegistryAddress( expected.getShippingAddress(), actual.getShippingAddress() );
		assertEqualsSeller( expected.getSeller(), actual.getSeller() );
		assertEquals("DiscountExpr", expected.getDiscountExpr(), actual.getDiscountExpr());
		assertEquals("IssueDate", expected.getDate(), actual.getDate());
		assertEqualsPayMethod( expected.getPayMethod(), actual.getPayMethod());
		assertEquals("DocumentType", expected.getDocumentType(), actual.getDocumentType());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
		assertEqualsWorkplace(expected.getWorkplace(), actual.getWorkplace());
		assertEqualsScope( expected.getScope(), actual.getScope());
		assertEquals("NumberOfPymnts", expected.getNumberOfPymnts(), actual.getNumberOfPymnts());
		assertEquals("DaysToFirstPymnt", expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt());
		assertEquals("DaysBetweenPymnts", expected.getDaysBetweenPymnts(), actual.getDaysBetweenPymnts());
		assertEquals("PymntDays", expected.getPymntDays(), actual.getPymntDays());
		assertEquals("BankAccount", expected.getBankAccount(), actual.getBankAccount());
		assertEquals("BankAlias", expected.getBankAlias(), actual.getBankAlias());
		assertEquals("Bic", expected.getBic(), actual.getBic());
		assertEquals("PurchaseGenerated", expected.isPurchaseGenerated(), actual.isPurchaseGenerated());
		assertEquals("DeliveryDate", expected.getDeliveryDate(), actual.getDeliveryDate());
		assertEquals("Carrier", expected.getCarrier(), actual.getCarrier());
		assertEqualsCarrier( expected.getCarrier(), actual.getCarrier());
		assertEquals("CarrierPacking", expected.getCarrierPacking(), actual.getCarrierPacking());
		assertEquals("ShippingAlternativeAddress", expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress());
		assertEquals("ShippingAlternativeAddress2", expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2());
		assertEquals("ShippingAlternativeZip", expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip());
		assertEquals("ShippingAlternativeCity", expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity());
		assertEquals("ShippingAlternativePhone", expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone());
		assertEquals("ShippingAlternativeRecipient", expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient());
		assertEquals("ShippingContact", expected.getShippingContact(), actual.getShippingContact());
		assertEquals("ShippingPeriod", expected.getShippingPeriod(), actual.getShippingPeriod());
	}
	
	@Deprecated
	public static void assertEqualsSalesOLD(Sales expected, Sales actual) {
		// TODO Completar...
		assertEqualsNulls( "Sales", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Status", expected.getStatus().value(), actual.getStatus().value());
		}
	}
	
	@Deprecated
	public static void assertEqualsSalesDetailOLD(SalesDetail expected, SalesDetail actual) {
		// TODO Completar...
		assertEqualsNulls("SalesDetail", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Status", expected.getStatus().value(), actual.getStatus().value());
		}
	}
	
	public static void assertEqualsSalesDetail(SalesDetail expected, SalesDetail actual) {
		assertEqualsNulls("SalesDetail", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEqualsSales( expected.getSales(), actual.getSales() );
			assertEqualsItem( expected.getItem(), actual.getItem() );
			assertEquals("Status", expected.getStatus(), actual.getStatus());
			assertEquals("Line", expected.getLine(), actual.getLine());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Quantity", expected.getQuantity(), actual.getQuantity(), DELTA);
			assertEquals("Price", expected.getPrice(), actual.getPrice(), DELTA);
			assertEqualsDiscountExpression(expected.getDiscountExpression(), actual.getDiscountExpression()); 
			assertEquals("Taxes", expected.getTaxes(), actual.getTaxes(), DELTA);
			assertEquals("OfferDetail", expected.getOfferDetail(), actual.getOfferDetail());
			assertEquals("Delivered", expected.getDelivered(), actual.getDelivered(), DELTA);
			assertEquals("DeliveryDate", expected.getDeliveryDate(), actual.getDeliveryDate());
			assertEqualsCarrier(expected.getCarrier(), actual.getCarrier());
			assertEquals("CarrierPacking", expected.getCarrierPacking(), actual.getCarrierPacking());
			assertEquals("Delivery", expected.getDelivery(), actual.getDelivery());
		}
	}
	
	public static void assertEqualsPurchase(Purchase expected, Purchase actual) {
		assertEqualsNulls("Purchase", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEquals("Project", expected.getProject(), actual.getProject());
		assertEquals("SupplierId", expected.getSupplier(), actual.getSupplier());
		assertEquals("SupplierName", expected.getSupplierName(), actual.getSupplierName());
		assertEqualsSupplier(expected.getSupplier2(), actual.getSupplier2());
		assertEquals("Series", expected.getSeries(), actual.getSeries());
		assertEquals("Number", expected.getNumber(), actual.getNumber());
		assertEquals("PurchaseReference", expected.getPurchaseReference(), actual.getPurchaseReference());
		assertEquals("Address", expected.getAddress(), actual.getAddress());
		assertEquals("DiscountExpr", expected.getDiscountExpr(), actual.getDiscountExpr());
		assertEquals("IssueDate", expected.getIssueDate(), actual.getIssueDate());
		assertEquals("PayMethod", expected.getPayMethod(), actual.getPayMethod());
		assertEquals("DocumentType", expected.getDocumentType(), actual.getDocumentType());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
		assertEquals("Workplace", expected.getWorkplace(), actual.getWorkplace());
		assertEquals("WorkplaceName", expected.getWorkplaceName(), actual.getWorkplaceName());
		assertEquals("Warehouse", expected.getWarehouse(), actual.getWarehouse());
		assertEquals("Scope", expected.getScope(), actual.getScope());
		assertEquals("ScopeName", expected.getScopeName(), actual.getScopeName());
		assertEquals("NumberOfPymnts", expected.getNumberOfPymnts(), actual.getNumberOfPymnts());
		assertEquals("DaysToFirstPymnt", expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt());
		assertEquals("DaysBetweenPymnts", expected.getDaysBetweenPymnts(), actual.getDaysBetweenPymnts());
		assertEquals("PymntDays", expected.getPymntDays(), actual.getPymntDays());
		assertEquals("BankAccount", expected.getBankAccount(), actual.getBankAccount());
		assertEquals("BankAlias", expected.getBankAlias(), actual.getBankAlias());
		assertEquals("Bic", expected.getBic(), actual.getBic());
		assertEquals("EmailCommunication", expected.isEmailCommunication(), actual.isEmailCommunication());
		assertEquals("Carrier", expected.getCarrier(), actual.getCarrier());
		assertEquals("ShippingAlternativeAddress", expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress());
		assertEquals("ShippingAlternativeAddress2", expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2());
		assertEquals("ShippingAlternativeZip", expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip());
		assertEquals("ShippingAlternativeCity", expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity());
		assertEquals("ShippingAlternativePhone", expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone());
		assertEquals("ShippingAlternativeRecipient", expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient());
		assertEquals("ShippingContact", expected.getShippingContact(), actual.getShippingContact());
		assertEquals("ShippingPeriod", expected.getShippingPeriod(), actual.getShippingPeriod());
		assertEquals("CarrierPacking", expected.getCarrierPacking(), actual.getCarrierPacking());
	}
	
	public static void assertEqualsPurchaseDetail(PurchaseDetail expected, PurchaseDetail actual) {
		assertEqualsNulls("PurchaseDetail", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("PurchaseId", expected.getPurchaseId(), actual.getPurchaseId());
			assertEqualsPurchase( expected.getPurchase(), actual.getPurchase() );
			assertEquals("Project", expected.getProject(), actual.getProject());
			assertEquals("ProjectName", expected.getProjectName(), actual.getProjectName());
			assertEquals("Item", expected.getItem(), actual.getItem());
			// assertEquals( expected.getItem(), actual.getItem() );
			assertEquals("Line", expected.getLine(), actual.getLine());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Quantity", expected.getQuantity(), actual.getQuantity(), DELTA);
			assertEquals("Price", expected.getPrice(), actual.getPrice(), DELTA);
			assertEquals("DiscountExpression", expected.getDiscountExpression(), actual.getDiscountExpression()); 
			assertEquals("Taxes", expected.getTaxes(), actual.getTaxes(), DELTA);
			assertEquals("Status", expected.getStatus(), actual.getStatus());
			assertEquals("ProposalDetail", expected.getProposalDetail(), actual.getProposalDetail());
			assertEquals("Source", expected.getSource(), actual.getSource());
			assertEquals("SourceId", expected.getSourceId(), actual.getSourceId());
			assertEquals("Delivered", expected.getDelivered(), actual.getDelivered(), DELTA);
			assertEquals("DeliveryDate", expected.getDeliveryDate(), actual.getDeliveryDate());
		}
	}
	
	public static void assertEqualsIncome(Income expected, Income actual) {
		assertEqualsNulls("Income", expected, actual);
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain(), actual.getDomain());
		assertEqualsProject(expected.getProject(), actual.getProject());
		assertEquals("ReferenceCode", expected.getReferenceCode(), actual.getReferenceCode());
		assertEquals("Supplier", expected.getSupplier(), actual.getSupplier());
		assertEquals("SupplierName", expected.getSupplierName(), actual.getSupplierName());
		assertEqualsSupplier( expected.getSupplier2(), actual.getSupplier2()); 
		assertEquals("Address", expected.getAddress(), actual.getAddress());
		assertEquals("IssueDate", expected.getIssueDate(), actual.getIssueDate());
		assertEquals("PayMethod", expected.getPayMethod(), actual.getPayMethod());
		assertEquals("SecurityLevel", expected.getSecurityLevel(), actual.getSecurityLevel());
		assertEquals("Status", expected.getStatus(), actual.getStatus());
		assertEquals("Comments", expected.getComments(), actual.getComments());
		assertEquals("Remarks", expected.getRemarks(), actual.getRemarks());
		assertEquals("Workplace", expected.getWorkplace(), actual.getWorkplace());
		assertEquals("WorkplaceName", expected.getWorkplaceName(), actual.getWorkplaceName());
		assertEquals("Scope", expected.getScope(), actual.getScope());
		assertEquals("ScopeName", expected.getScopeName(), actual.getScopeName());
		assertEquals("NumberOfPymnts", expected.getNumberOfPymnts(), actual.getNumberOfPymnts());
		assertEquals("DaysToFirstPymnt", expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt());
		assertEquals("DaysBetweenPymnts", expected.getDaysBetweenPymnt(), actual.getDaysBetweenPymnt());
		assertEquals("PymntDays", expected.getPymntDays(), actual.getPymntDays());
		assertEquals("BankAccount", expected.getBankAccount(), actual.getBankAccount());
		assertEquals("BankAlias", expected.getBankAlias(), actual.getBankAlias());
		assertEquals("Bic", expected.getBic(), actual.getBic());
		assertEquals("CarrierPacking", expected.getCarrierPacking(), actual.getCarrierPacking());
		
	}

	public static void assertEqualsIncomeDetail(IncomeDetail expected, IncomeDetail actual) {
		assertEqualsNulls("IncomeDetail", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEqualsIncome( expected.getIncome(), actual.getIncome() );
			assertEqualsProject( expected.getProject(), actual.getProject() );
			assertEquals("Line", expected.getLine(), actual.getLine());
			// assertEqualsItem( expected.getItem(), actual.getItem() );
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Warehouse", expected.getWarehouse(), actual.getWarehouse());
			assertEquals("Quantity", expected.getQuantity(), actual.getQuantity(), DELTA);
			assertEquals("Price", expected.getPrice(), actual.getPrice(), DELTA);
			assertEquals("DiscountExpression", expected.getDiscountExpression(), actual.getDiscountExpression()); 
			assertEquals("PurchaseDetail", expected.getPurchaseDetail(), actual.getPurchaseDetail());
		}
	}
	public static void assertEqualsQuestion(Question expected, Question actual) {
		if (expected != null && actual != null) {
			assertEquals("Id",expected.getId(),actual.getId());
			assertEquals("Domain",expected.getDomain(),actual.getDomain());
			assertEquals("Active",expected.isActive(),actual.isActive());
			assertEquals("Text",expected.getText(),actual.getText());
			assertEquals("Type",expected.getType(),actual.getType());
			assertEquals("Argument",expected.getArgument(),actual.getArgument());
			assertEquals("Alias",expected.getAlias(),actual.getAlias());
		}
	}

	public static void assertEqualsItem(Item expected, Item actual) {
		assertEqualsNulls( "Item", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain().getId(), actual.getDomain().getId());
		assertEquals("Detail",expected.getDetail(), actual.getDetail());
		assertEquals("Detail2",expected.getDetail2(), actual.getDetail2());
		assertEquals("Detail3",expected.getDetail3(), actual.getDetail3());
		assertEquals("Description",expected.getDescription(), actual.getDescription());
		assertEquals("SerialNumber",expected.getSerialNumber(), actual.getSerialNumber());
		assertEquals("SerialDate",expected.getSerialDate(), actual.getSerialDate());
		assertEquals("ExpireDate",expected.getExpireDate(), actual.getExpireDate());
		assertEquals("Barcode",expected.getBarcode(), actual.getBarcode());
		assertEquals("Status",expected.getStatus(), actual.getStatus());
		assertEqualsProduct(expected.getProduct(), actual.getProduct());
		assertEquals("Price",expected.getPrice(), actual.getPrice(), DELTA);
		assertEquals("ExpensesPercent",expected.getExpensesPercent(), actual.getExpensesPercent(), DELTA);
		assertEquals("ExpensesFixed",expected.getExpensesFixed(), actual.getExpensesFixed(), DELTA);
		assertEquals("ProfitPercent",expected.getProfitPercent(), actual.getProfitPercent(), DELTA);
		assertEquals("PurchasePrice",expected.getPurchasePrice(), actual.getPurchasePrice(), DELTA);
		assertEquals("Internet",expected.isInternet(), actual.isInternet());
		assertEqualsTag(expected.getPackFormatTag(), actual.getPackFormatTag());
		assertEquals("PackUnits",expected.getPackUnits(), actual.getPackUnits());
		assertEqualsTag(expected.getPackUnitsTag(), actual.getPackUnitsTag());
		assertEquals("PackMeasurement",expected.getPackMeasurement(), actual.getPackMeasurement(), DELTA);
		assertEqualsTag(expected.getPackMeasurementTag(), actual.getPackMeasurementTag());
		assertEqualsTag(expected.getStockUnitTag(), actual.getStockUnitTag());
	}
	
	public static void assertEqualsTag(Tag expected, Tag actual) {
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Type",expected.getType(), actual.getType());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Color",expected.getColor(), actual.getColor());
		assertEquals("StartDate",expected.getStartDate(), actual.getStartDate());
		assertEquals("EndDate",expected.getEndDate(), actual.getEndDate());
	}
	
	public static void assertEqualsBankAccount(BankAccount expected, BankAccount actual) {
		if (expected != null) {
			assertEquals("BankCode", expected.getBankCode(), actual.getBankCode());
			assertEquals("BankCodeLenght", expected.getBankCodeLength(), actual.getBankCodeLength());
			assertEquals("Bban", expected.getBban(), actual.getBban());
			assertEquals("Bban1", expected.getBban1(), actual.getBban1());
			assertEquals("Bban1", expected.getBban1(), actual.getBban1());
			assertEquals("Bban2", expected.getBban2(), actual.getBban2());
			assertEquals("Bban3", expected.getBban3(), actual.getBban3());
			assertEquals("Bban4", expected.getBban4(), actual.getBban4());
			assertEquals("Bban5", expected.getBban5(), actual.getBban5());
			assertEquals("Bban6", expected.getBban6(), actual.getBban6());
			assertEquals("Bban7", expected.getBban7(), actual.getBban7());
			assertEquals("Bban8", expected.getBban8(), actual.getBban8());
			assertEquals("CCC", expected.getCCC(), actual.getCCC());
			assertEquals("CCC1", expected.getCCC1(), actual.getCCC1());
			assertEquals("CCC2", expected.getCCC2(), actual.getCCC2());
			assertEquals("CCC3", expected.getCCC3(), actual.getCCC3());
			assertEquals("CCC4", expected.getCCC4(), actual.getCCC4());
			assertEquals("Check", expected.getCheck(), actual.getCheck());
			assertEquals("Country", expected.getCountry(), actual.getCountry());
			assertEquals("Iban", expected.getIban(), actual.getIban());
			assertEquals("IbanLength", expected.getIbanLength(), actual.getIbanLength());
			assertEquals("MaskedIban", expected.getMaskedIban(), actual.getMaskedIban());
			assertEquals("PureCCC", expected.getPureCCC(), actual.getPureCCC());
			assertEquals("SeparatedIban", expected.getSeparatedIban(), actual.getSeparatedIban());
		}
	}
	
	public static void assertEqualsRegistryBank(RegistryBank expected, RegistryBank actual) {
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEqualsAccount(expected.getAccount(), actual.getAccount());
			assertEquals("Active", expected.getActive(), actual.getActive());
			assertEquals("Alias", expected.getAlias(), actual.getAlias());
			assertEqualsBankAccount(expected.getBankAccount(), actual.getBankAccount());
			assertEquals("Bic", expected.getBic(), actual.getBic());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("FullName", expected.getFullName(), actual.getFullName());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Registry", expected.getRegistry(), actual.getRegistry());
			assertEquals("Requisition", expected.getRequisition(), actual.getRequisition());
			assertEquals("SepaMandateRef", expected.getSepaMandateRef(), actual.getSepaMandateRef());
			assertEquals("Suffix", expected.getSuffix(), actual.getSuffix());
		}
	}
	
	public static void assertEqualsCarrier(Carrier expected, Carrier actual) {
		if (expected != null && actual != null) {
			assertEquals("Alias", expected.getAlias(), actual.getAlias());
			Asserts.assertEqualsRegistry(expected.get(), actual.get());
			assertEquals("Document", expected.getDocument(), actual.getDocument());
			assertEquals("Country", expected.getDocumentCountry().toString(), actual.getDocumentCountry().toString());
			assertEquals("Document", expected.getDocumentType().toString(), actual.getDocumentType().toString());
			Asserts.assertEqualsDomain(expected.getDomain(), actual.getDomain());
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Name", expected.getName(), actual.getName());
			assertEquals("Document", expected.getDocument(), actual.getDocument());
			assertEquals("Nationality", expected.getNationality().toString(), actual.getNationality().toString());
			Asserts.assertEqualsScope(expected.getScope(), actual.getScope());
			assertEquals("Security Level", expected.getSecurityLevel().toString(), actual.getSecurityLevel().toString());
			assertEquals("Status", expected.getStatus().toString(), actual.getStatus().toString());
		}
	}
	
	
	public static void assertEqualsSeries(Series expected, Series actual) {
		assertEqualsNulls( "Series", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Domain", expected.getDomain(), actual.getDomain());
			assertEquals("Scope", expected.getScope(), actual.getScope());
			assertEquals("Code",expected.getCode(), actual.getCode());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Active",expected.isActive(), actual.isActive());
			assertEquals("Tas",expected.isTas(), actual.isTas());
			assertEquals("Offer",expected.isOffer(), actual.isOffer());
			assertEquals("Sales",expected.isSales(), actual.isSales());
			assertEquals("Delivery",expected.isDelivery(), actual.isDelivery());
			assertEquals("Invoice",expected.isInvoice(), actual.isInvoice());
			assertEquals("Rectification",expected.isRectification(), actual.isRectification()); 
			assertEquals("Pos",expected.isPos(), actual.isPos());
			assertEquals("Security Level", expected.getSecurityLevel().toString(), actual.getSecurityLevel().toString());
		}
	}
	
	public static void assertEqualsInvoiceSeries(InvoiceSeries expected, InvoiceSeries actual) {
		assertEqualsNulls( "InvoiceSeries", expected, actual);
		if (expected != null ) {
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Sales",expected.isSales(), actual.isSales());
			assertEquals("FromNumber", expected.getFromNumber(), actual.getFromNumber());
			assertEquals("ToNumber", expected.getToNumber(), actual.getToNumber());
			assertEquals("Count", expected.getCount(), actual.getCount());
		}
	}
}