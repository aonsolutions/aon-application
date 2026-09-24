package com.esferalia.aon.occam.test;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;
import static com.esferalia.aon.occam.test.OccamAssertions.fail;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Carrier;
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
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueCategory;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueItem;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class Asserts {
	
	private static final double DELTA = 1e-8;
	
	public static void assertEqualsDouble(String msg,double expected,double actual) {
		assertEquals(expected, actual, DELTA, msg);
	}
	public static void assertNotEqualsDouble(String msg,double expected,double actual) {
		// sin equivalente en Jupiter (assertNotEquals con delta); metodo sin uso
		org.junit.Assert.assertNotEquals(msg, expected, actual, DELTA);
	}
	
	public static void assertEqualsNulls(String msg,Object expected, Object actual) {
		if ( expected == null) assertNull(actual, msg);
		if ( expected != null) assertNotNull(actual, msg);
	}
	public static void assertEqualsArray(String msg,Object[] expected, Object[] actual) {
		if ( (expected == null || expected.length == 0) 
				&& ( (actual != null && actual.length != 0))) 
				fail( msg + " actual List is not Empty");
			if ( (expected != null && expected.length != 0) 
				&& (actual == null || actual.length == 0))  
				fail( msg + " actual List is Empty");
			if ( expected != null && actual != null) {
				assertEquals(expected.length, actual.length, " sizes not fit");	
			}
	}
	
	public static void assertNullCollection(String msg,Collection<?> actual) {
		assertNull(actual, msg);
	}
	public static void assertEmptyCollection(String msg,Collection<?> actual) {
		assertNotNull(actual, msg);
		assertTrue(actual.isEmpty(), msg);
	}
	
	public static void assertEqualsCollection(String msg,Collection<?> expected, Collection<?> actual) {
		if ( (expected == null || expected.isEmpty()) 
			&& ( (actual != null && !actual.isEmpty()))) 
			fail( msg + " actual List is not Empty");
		if ( (expected != null && !expected.isEmpty()) 
			&& (actual == null || actual.isEmpty()))  
			fail( msg + " actual List is Empty");
		if ( expected != null && actual != null) {
			assertEquals(expected.size(), actual.size(), " sizes not fit");	
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
			assertEquals(expected.size(), actual.size(), " sizes not fit");	
		}
	}
	public static void assertEqualsAccount (Account expected, Account actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getCode(), actual.getCode(), "Code");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
			assertEquals(expected.isEntryEnabled(), actual.isEntryEnabled(), "EntryEnabled");
			assertEquals(expected.getLevel(), actual.getLevel(), "Level");
			assertEquals(expected.isActive(), actual.isActive(), "Active");
			assertEquals(expected.getCostCenter(), actual.getCostCenter(), "CostCenter");
		}
	}

	public static void assertEqualsAccountPeriod(AccountPeriod expected, AccountPeriod actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getName(), actual.getName(), "Name");
			assertEquals(expected.getInitiationDate(), actual.getInitiationDate(), "InitiationDate");
			assertEquals(expected.getDeadline(), actual.getDeadline(), "Deadline");
			assertEquals(expected.getStatus(), actual.getStatus(), "Status");
		}
	}

	public static void assertEqualsScope(Scope expected, Scope actual) {
		assertEqualsNulls( "Scope", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		}
	}
	
	public static void assertEqualsScopeId(Scope expected, Scope actual) {
		assertEqualsNulls( "Scope", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
		}
	}

	private static void assertEqualsDomain(Domain expected, Domain actual) {
		assertEqualsNulls( "Domain", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		assertEquals(expected.getOwner(), actual.getOwner(), "Owner");
		assertEquals(expected.getParentId(), actual.getParentId(), "ParentId");
		assertEquals(expected.getDomainType(), actual.getDomainType(), "DomainType");
		assertEquals(expected.isEnableHeredity(), actual.isEnableHeredity(), "EnableHeredity");
		assertEquals(expected.isDomainManagement(), actual.isDomainManagement(), "DomainManagement");
		assertEquals(expected.isActive(), actual.isActive(), "Active");
		assertEquals(expected.getScope(), actual.getScope(), "Scope");
		assertEquals(expected.getMaxDefinedUsers(), actual.getMaxDefinedUsers(), "MaxDefinedUsers");
		assertEquals(expected.getDefinedUsers(), actual.getDefinedUsers(), "DefinedUsers");
	}

	public static void assertEqualsEnterpriseActivity(EnterpriseActivity expected, EnterpriseActivity actual) {
		assertEqualsNulls( "EnterpriseActivity", expected, actual);
		if (expected != null) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.isPrincipal(), actual.isPrincipal(), "Principal");
			assertEquals(expected.getIae().getId(), actual.getIae().getId(), "Iae");
			assertEquals(expected.getEpigraph(), actual.getEpigraph(), "Epigraph");
			assertEquals(expected.getCnae(), actual.getCnae(), "Cnae");
			assertEquals(expected.getCnaeCode(), actual.getCnaeCode(), "CnaeCode");
			assertEquals(expected.getCnaeDescription(), actual.getCnaeDescription(), "CnaeDescription");
		}
	}

	public static void assertEqualsAccountingReportParams(AccountingReportParams expected, AccountingReportParams actual) {
		assertEqualsNulls( "Account", expected, actual);
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getDomainName(), actual.getDomainName(), "DomainName");
		assertEquals(expected.getUser(), actual.getUser(), "User");
		assertEquals(expected.getPeriod(), actual.getPeriod(), "Period");
		assertEquals(expected.getFromDate(), actual.getFromDate(), "FromDate");
		assertEquals(expected.getToDate(), actual.getToDate(), "ToDate");
		assertEqualsAccount(expected.getAccount(), actual.getAccount());
		assertEquals(expected.getLevel(), actual.getLevel(), "Level");
		assertEquals(expected.getActivity(), actual.getActivity(), "Activity");
		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(), "SecurityLevel");
		assertEquals(expected.getDocumentNumber(), actual.getDocumentNumber(), "DocumentNumber");
		assertEquals(expected.getPreviousPeriods(), actual.getPreviousPeriods(), "PreviousPeriods"); 
		assertEquals(expected.isLowLevelAccountVisible(), actual.isLowLevelAccountVisible(), "LowLevelAccountVisible");
		assertEquals(expected.isNoActivityAccountVisible(), actual.isNoActivityAccountVisible(), "NoActivityAccountVisible");
		assertEquals(expected.isNoBalanceAccountExcluded(), actual.isNoBalanceAccountExcluded(), "NoBalanceAccountExcluded");
		assertEquals(expected.isPercentsEnabled(), actual.isPercentsEnabled(), "PercentsEnabled");
		assertEquals(expected.isByMonth(), actual.isByMonth(), "ByMonth");
		assertEquals(expected.areOpeningEntriesExcluded(), actual.areOpeningEntriesExcluded(), "OpeningEntriesExcluded"); 
		assertEquals(expected.areOperatingEntriesExcluded(), actual.areOperatingEntriesExcluded(), "OperatingEntriesExcluded");
		assertEquals(expected.areClosingEntriesExcluded(), actual.areClosingEntriesExcluded(), "ClosingEntriesExcluded");
		assertEquals(expected.isReverseOrder(), actual.isReverseOrder(), "ReverseOrder");
		assertEquals(expected.getBalanceType(), actual.getBalanceType(), "BalanceType");
		assertEqualsAccountPeriod(expected.getSelectedPeriod(), actual.getSelectedPeriod());
		assertEqualsEnterpriseActivity(expected.getSelectedActivity(), actual.getSelectedActivity());
		assertEqualsAccount(expected.getSelectedAccount(), actual.getSelectedAccount());
		assertEquals(expected.isBreakdownEnabled(), actual.isBreakdownEnabled(), "BreakdownEnabled");
		assertEquals(expected.getLedgerAccount(), actual.getLedgerAccount(), "LedgerAccount");
		assertEquals(expected.getLedgerDebitBalance(), actual.getLedgerDebitBalance(), DELTA, "LedgerDebitBalance");
		assertEquals(expected.getLedgerUnpaidBalance(), actual.getLedgerUnpaidBalance(), DELTA, "LedgerUnpaidBalance");
		assertEquals(expected.isConsolidation(), actual.isConsolidation(), "Consolidation");
		assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
		assertEquals(expected.getOutput(), actual.getOutput(), "Output");
		assertEquals(expected.getVatSummaryType(), actual.getVatSummaryType(), "VatSummaryType");
		assertEquals(expected.getPercent(), actual.getPercent(), "Percent");
		assertEquals(expected.getRectificationType(), actual.getRectificationType(), "RectificationType");
		assertEquals(expected.getSurcharge(), actual.getSurcharge(), "Surcharge");
		assertEquals(expected.getFarmerRegime(), actual.getFarmerRegime(), "FarmerRegime");
		assertEquals(expected.getAccrualRegime(), actual.getAccrualRegime(), "AccrualRegime");
		assertEquals(expected.getInvestment(), actual.getInvestment(), "Investment");
		assertEquals(expected.getService(), actual.getService(), "Service");
		assertEquals(expected.getTitle(), actual.getTitle(), "Title");
		assertEquals(expected.getSubject(), actual.getSubject(), "Subject");
		assertEquals(expected.isShowCover(), actual.isShowCover(), "ShowCover");
		assertEquals(expected.getPageOffset(), actual.getPageOffset(), "PageOffset");
		assertEquals(expected.getPageOffsetText(), actual.getPageOffsetText(), "PageOffsetText");
		assertEquals(expected.isHideFilter(), actual.isHideFilter(), "HideFilter");
		assertEquals(expected.getHeaderText(), actual.getHeaderText(), "HeaderText");
		assertEquals(expected.isHideDateTimeOnFooter(), actual.isHideDateTimeOnFooter(), "HideDateTimeOnFooter");
		assertEquals(expected.getFooterText(), actual.getFooterText(), "FooterText");
		assertEqualsCollection( "Domains Size",expected.getDomains(), actual.getDomains());
		if ( expected.getDomains() != null) {
			for (int i = 0; i < expected.getDomains().size(); i++) {
				assertEqualsDomain(expected.getDomains().get(i), actual.getDomains().get(i));	
			}
		}
		assertEqualsCollection( "CostCenters Size",expected.getCostCenters(), actual.getCostCenters());
		if ( expected.getCostCenters() != null) {
			for (String key : expected.getCostCenters()) {
				assertTrue(expected.getCostCenters().contains(key), "CostCenter");	
			}
		}
		assertEqualsArray( "Invoices",expected.getInvoices(), actual.getInvoices());
		if ( expected.getInvoices() != null) {
			for (int i = 0; i < expected.getInvoices().length; i++) {
				assertEquals(expected.getInvoices()[i], actual.getInvoices()[i], "Invoice -> " + i);	
			}
		}
		
//		private Integer[] invoices;
		
	}

	public static void assertEqualsRegistry (Registry expected, Registry actual) {
		assertEqualsNulls( "Registry", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain().getId(), actual.getDomain().getId(), "Domain");
			assertEquals(expected.getDocument(), actual.getDocument(), "Document");
			assertEquals(expected.getDocumentType(), actual.getDocumentType(), "DocumentType");
			assertEquals(expected.getDocumentCountry(), actual.getDocumentCountry(), "DocumentCountry");
			assertEquals(expected.getName(), actual.getName(), "Name");
			assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
			assertEquals(expected.isLegalPerson(), actual.isLegalPerson(), "LegalPerson");
			assertEquals(expected.getNationality(), actual.getNationality(), "Nationality");
			assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(), "SecurityLevel");
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
		assertEquals(expected.getMedias().size(), actual.getMedias().size(), "RegistryFull Medias Size");
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
		if (expected != null ) {
			assertEqualsRegistry(expected, actual);
			assertEquals(expected.getTariff(), actual.getTariff(), "Tariff");
			assertEquals(expected.isSurcharge(), actual.isSurcharge(), "Surcharge");
			assertEquals(expected.isWithholding(), actual.isWithholding(), "Withholding");
			assertEquals(expected.getTransaction(), actual.getTransaction(), "Transaction");
			assertEquals(expected.getStatus(), actual.getStatus(), "Status");
			assertEquals(expected.getScope().getId(), actual.getScope().getId(), "Scope");
			assertEquals(expected.isEInvoice(), actual.isEInvoice(), "EInvoice");
			assertEquals(expected.getInvoicingGroup(), actual.getInvoicingGroup(), "InvoicingGroup");
			assertEquals(expected.isProjectGrouped(), actual.isProjectGrouped(), "ProjectGrouped");
			assertEquals(expected.isDeliveryGrouped(), actual.isDeliveryGrouped(), "DeliveryGrouped");
			assertEquals(expected.isDeliveryValuated(), actual.isDeliveryValuated(), "DeliveryValuated");
			assertEquals(expected.getAccount(), actual.getAccount(), "Account");
		}
	}

	public static void assertEqualsCreditor(Creditor expected, Creditor actual) {
		assertEqualsNulls( "Creditor", expected, actual);
		if (expected != null) {
			assertEqualsRegistry(expected, actual);
			assertEquals(expected.isWithholding(), actual.isWithholding(), "Withholding");
			assertEquals(expected.isVatAccrualPayment(), actual.isVatAccrualPayment(), "VatAccrualPayment");
			assertEquals(expected.getTransaction(), actual.getTransaction(), "Transaction");
			assertEquals(expected.getStatus(), actual.getStatus(), "Status");
			assertEquals(expected.getScope().getId(), actual.getScope().getId(), "Scope");
			assertEquals(expected.getAccount(), actual.getAccount(), "Account");
		}
	}

	public static void assertEqualsSeller(Seller expected, Seller actual) {
		assertEqualsNulls( "Seller", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals(expected.getCommissionType().getId(), actual.getCommissionType().getId(), "CommissionType");
		assertEquals(expected.getScope().getId(), actual.getScope().getId(), "Scope");
		assertEquals(expected.getStatus(), actual.getStatus(), "Status");
	}
	
	public static void assertEqualsSupplier(Supplier expected, Supplier actual) {
		assertEqualsNulls( "Supplier", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals(expected.getTariff(), actual.getTariff(), "Tariff");
		assertEquals(expected.isWithholding(), actual.isWithholding(), "Withholding");
		assertEquals(expected.isWithholdingFarmer(), actual.isWithholdingFarmer(), "WithholdingFarmer");
		assertEquals(expected.isVatAccrualPayment(), actual.isVatAccrualPayment(), "VatAccrualPayment");
		assertEquals(expected.getTransaction(), actual.getTransaction(), "Transaction");
		assertEquals(expected.getStatus(), actual.getStatus(), "Status");
		assertEquals(expected.getScope().getId(), actual.getScope().getId(), "Scope");
		assertEquals(expected.isPurchaseValuated(), actual.isPurchaseValuated(), "PurchaseValuated");
		assertEquals(expected.getAccount(), actual.getAccount(), "Account");
	}
	
	public static void assertEqualsTaskHolder(TaskHolder expected, TaskHolder actual) {
		assertEqualsNulls( "TaskHolder", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals(expected.getType(), actual.getType(), "Type");
		assertEquals(expected.isActive(), actual.isActive(), "Active");
		assertEquals(expected.getUserId(), actual.getUserId(), "UserId");
		assertEquals(expected.getCostProfile(), actual.getCostProfile(), "CostProfile");
	}

	public static void assertEqualsRegistryMedia (RegistryMedia expected, RegistryMedia actual) {
		assertEqualsNulls( "RegistryMedia", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
		assertEquals(expected.getMedia(), actual.getMedia(), "Media");
		assertEquals(expected.getValue(), actual.getValue(), "Value");
		assertEquals(expected.getComment(), actual.getComment(), "Comment");
		assertEquals(expected.getRaddress(), actual.getRaddress(), "Raddress");
		assertEquals(expected.isAdministrative(), actual.isAdministrative(), "Administrative");
		assertEquals(expected.isCommercial(), actual.isCommercial(), "Commercial");
		assertEquals(expected.isTechnical(), actual.isTechnical(), "Technical");
	}

	public static void assertEqualsRegistryAddress(RegistryAddress expected, RegistryAddress actual) {
		assertEqualsNulls( "RegistryAddress", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
		assertEquals(expected.isMain(), actual.isMain(), "Main");
		assertEquals(expected.getRecipient(), actual.getRecipient(), "Recipient");
		assertEquals(expected.getStreetType()==null?null:expected.getStreetType().getAeatCode(), actual.getStreetType()==null?null:actual.getStreetType().getAeatCode(), "StreetType");
		assertEquals(expected.getAddress(), actual.getAddress(), "Address");
		assertEquals(expected.getNumber(), actual.getNumber(), "Number");
		assertEquals(expected.getAddress2(), actual.getAddress2(), "Address2");
		assertEquals(expected.getAddress3(), actual.getAddress3(), "Address3");
		assertEquals(expected.getAddress3(), actual.getAddress3(), "Zip");
		assertEquals(expected.getCity(), actual.getCity(), "City");
		assertEquals(expected.getGeozone(), actual.getGeozone(), "Geozone");
		assertEquals(expected.getGeozoneCode(), actual.getGeozoneCode(), "GeozoneCode");
		assertEquals(expected.getGeozoneName(), actual.getGeozoneName(), "GeozoneName");
		assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
		assertEquals(expected.getMunicipalityCode(), actual.getMunicipalityCode(), "MunicipalityCode");
	}

	public static void assertEqualsTariff(Tariff expected, Tariff actual) {
		assertEqualsNulls( "Tariff", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getCode(), actual.getCode(), "Code");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.isPurchase(), actual.isPurchase(), "Purchase");
		assertEquals(expected.getDiscount(), actual.getDiscount(), DELTA, "Discount");
		assertEquals(expected.isActive(), actual.isActive(), "Active");
	}

	public static void assertEqualsCatalogue(Catalogue expected, Catalogue actual) {
		assertEqualsNulls("Catalogue", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.isPurchase(), actual.isPurchase(), "Purchase");
		assertEquals(expected.getStart(), actual.getStart(), "Start");
		assertEquals(expected.getEnd(), actual.getEnd(), "End");
	}

	public static void assertEqualsCatalogueItem(CatalogueItem expected, CatalogueItem actual) {
		assertEqualsNulls("CatalogueItem", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getCatalogue(), actual.getCatalogue(), "Catalogue");
		assertEquals(expected.getProduct(), actual.getProduct(), "Product");
		assertEquals(expected.getItem(), actual.getItem(), "Item");
		assertEquals(expected.getQuantity(), actual.getQuantity(), DELTA, "Quantity");
		assertEquals(expected.getPrice(), actual.getPrice(), DELTA, "Price");
		assertEquals(expected.getDiscount(), actual.getDiscount(), DELTA, "Discount");
	}

	public static void assertEqualsCatalogueCategory(CatalogueCategory expected, CatalogueCategory actual) {
		assertEqualsNulls("CatalogueCategory", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getCatalogue(), actual.getCatalogue(), "Catalogue");
		assertEquals(expected.getCategory(), actual.getCategory(), "Category");
		assertEquals(expected.getQuantity(), actual.getQuantity(), DELTA, "Quantity");
		assertEquals(expected.getDiscount(), actual.getDiscount(), DELTA, "Discount");
	}
	
	public static void assertEqualsProduct(Product expected, Product actual) {
		assertEqualsNulls( "Product", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain().getId(), actual.getDomain().getId(), "Domain");
		assertEquals(expected.getCode(), actual.getCode(), "Code");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getBrand().getId(), actual.getBrand().getId(), "Brand");
		assertEquals(expected.getCategory().getId(), actual.getCategory().getId(), "Category");
		assertEquals(expected.getStatus().value(), actual.getStatus().value(), "Status");
		assertEquals(expected.getType().value(), actual.getType().value(), "Type");
		assertEquals(expected.getKind().value(), actual.getKind().value(), "Kind");
		assertEquals(expected.getVat().getId(), actual.getVat().getId(), "Vat");
		assertEquals(expected.getRetention().getId(), actual.getRetention().getId(), "Retention");
		assertEquals(expected.isInventoriable(), actual.isInventoriable(), "Inventoriable");
		assertEquals(expected.isSerializable(), actual.isSerializable(), "Serializable");
		assertEquals(expected.isLotable(), actual.isLotable(), "Lotable");
		assertEquals(expected.isManufactured(), actual.isManufactured(), "Manufactured");
		assertEquals(expected.isComposition(), actual.isComposition(), "Composition");
		assertEquals(expected.isCompositionPrice(), actual.isCompositionPrice(), "CompositionPrice");
		assertEquals(expected.isPackaged(), actual.isPackaged(), "packaged");
		assertEquals(expected.getSalesAccount().getId(), actual.getSalesAccount().getId(), "salesAccount");
		assertEquals(expected.getPurchaseAccount().getId(), actual.getPurchaseAccount().getId(), "purchaseAccount");		
		assertEquals(expected.isActive(), actual.isActive(), "Active");
	}

	public static void assertEqualsWarehouse(Warehouse expected, Warehouse actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getDepartment(), actual.getDepartment(), "Department");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getWorkplace(), actual.getWorkplace(), "Workplace");
		assertEquals(expected.isActive(), actual.isActive(), "Active");
	}
	
	public static void assertEqualsWorkgroup(Workgroup expected, Workgroup actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		assertEquals(expected.getStatus(), actual.getStatus(), "status");
	}
	
	public static void assertEqualsProjectType(ProjectType expected, ProjectType actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		assertEquals(expected.isActive(), actual.isActive(), "Active");
	}
	
	public static void assertEqualsProject(Project expected, Project actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain().getId(), actual.getDomain().getId(), "Domain");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
		assertEquals(AonDateUtils.getDateWithoutTime(expected.getDate()), AonDateUtils.getDateWithoutTime(actual.getDate()), "Date");
		assertEquals(expected.getType().getId(), actual.getType().getId(), "Type");
		assertEquals(expected.isTas(), actual.isTas(), "Tas");
		assertEquals(expected.isCommercial(), actual.isCommercial(), "Commercial");
		assertEquals(expected.isReservation(), actual.isReservation(), "Reservation");
		assertEquals(expected.isActive(), actual.isActive(), "Active");
	}
	
	public static void assertEqualsProjectHolder(ProjectHolder expected, ProjectHolder actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getProject(), actual.getProject(), "Project");
		assertEquals(AonDateUtils.getDateWithoutTime(expected.getStartDate()), AonDateUtils.getDateWithoutTime(actual.getStartDate()), "Start Date");
		assertEquals(AonDateUtils.getHour(expected.getStartDate()), AonDateUtils.getHour(actual.getStartDate()), "Start Date");
		assertEquals(AonDateUtils.getDateWithoutTime(expected.getEndDate()), AonDateUtils.getDateWithoutTime(actual.getEndDate()), "End Date");
		assertEquals(AonDateUtils.getHour(expected.getEndDate()), AonDateUtils.getHour(actual.getEndDate()), "End Date");
		assertEquals(expected.getWorkgroup().getId(), actual.getWorkgroup().getId(), "Workgroup");
		assertEquals(expected.getTaskHolder().getId(), actual.getTaskHolder().getId(), "Task Holder");
	}
	
	public static void assertEqualsBrand(Brand expected, Brand actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getName(), actual.getName(), "Name");
	}
	
	public static void assertEqualsProductCategory(ProductCategory expected, ProductCategory actual) {
		assertEqualsNulls( "ProductCategory", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getDetail(), actual.getDetail(), "Detail");
		assertEquals(expected.getDetail2(), actual.getDetail2(), "Detail2");
		assertEquals(expected.getDetail3(), actual.getDetail3(), "Detail3");
	}
	
	public static void assertEqualsPayMethod(PayMethod expected, PayMethod actual) {
		assertEqualsNulls( "PayMethod", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getType(), actual.getType(), "Type");
	}
	public static void assertEqualsAccountTrialBalance(AccountTrialBalance expected, AccountTrialBalance actual) {
		assertEqualsNulls( "AccountTrialBalance", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getCode(), actual.getCode(), "Code");
		assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		assertEquals(expected.getBeforePeriodDebit(), actual.getBeforePeriodDebit(), DELTA, "BeforePeriodDebit");
		assertEquals(expected.getBeforePeriodCredit(), actual.getBeforePeriodCredit(), DELTA, "BeforePeriodCredit");
		assertEquals(expected.getInPeriodOpeningDebit(), actual.getInPeriodOpeningDebit(), DELTA, "InPeriodOpeningDebit");
		assertEquals(expected.getInPeriodOpeningCredit(), actual.getInPeriodOpeningCredit (), DELTA, "InPeriodOpeningCredit");
		assertEquals(expected.getInPeriodBeforeDebit(), actual.getInPeriodBeforeDebit(), DELTA, "InPeriodBeforeDebit");
		assertEquals(expected.getInPeriodBeforeCredit(), actual.getInPeriodBeforeCredit(), DELTA, "InPeriodBeforeCredit");
		assertEquals(expected.getInPeriodDebit(), actual.getInPeriodDebit(), DELTA, "InPeriodDebit");
		assertEquals(expected.getInPeriodCredit(), actual.getInPeriodCredit(), DELTA, "InPeriodCredit");
	}
	public static void assertEqualsAccountTrialBalanceReport (AccountTrialBalanceReport expected, AccountTrialBalanceReport actual) {
		assertEqualsNulls( "AccountTrialBalanceReport", expected, actual);
		if (expected != null) {
			assertEqualsAccountingReportParams(expected.getParams(), actual.getParams());
			assertEquals(expected.hasBeforePeriodAmounts(), actual.hasBeforePeriodAmounts(), "hasBeforePeriodAmounts");	
			assertEquals(expected.hasOpeningAmounts(), actual.hasOpeningAmounts(), "hasOpeningAmounts");	
			assertEquals(expected.hasInPeriodPreviousAmounts(), actual.hasInPeriodPreviousAmounts(), "hasInPeriodPreviousAmounts");
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
			assertEquals(expected.getCode(), actual.getCode(), "Code");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getType(), actual.getType(), "Type");
		}
	}
	public static void assertEqualsAccountOperatingStatement (AccountOperatingStatement expected, AccountOperatingStatement actual) {
		assertEqualsNulls("AccountOperatingStatement", expected, actual);
		if (expected != null) {
			assertEqualsAccountOperatingAccount(expected.getAccount(), actual.getAccount());
			assertEquals(expected.getCredit(), actual.getCredit(), DELTA, "Credit");
			assertEquals(expected.getDebit(), actual.getDebit(), DELTA, "Debit");
			assertEquals(expected.getDebitBalance(), actual.getDebitBalance(), DELTA, "DebitBalance");
			assertEquals(expected.getExpensesRatio(), actual.getExpensesRatio(), DELTA, "ExpensesRatio");
			assertEquals(expected.getIncreasePercent(), actual.getIncreasePercent(), DELTA, "IncreasePercent");
			assertEquals(expected.getMonth(), actual.getMonth(), "Month");
			assertEquals(expected.getPurchasesRatio(), actual.getPurchasesRatio(), DELTA, "PurchasesRatio");
			assertEquals(expected.getSalesRatio(), actual.getSalesRatio(), DELTA, "SalesRatio");
			assertEquals(expected.getUnpaidBalance(), actual.getUnpaidBalance(), DELTA, "UnpaidBalance");
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
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
		assertEquals(expected.getDocument(), actual.getDocument(), "Document");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getShareHolder(), actual.getShareHolder(), "ShareHolder");
		assertEquals(expected.getRepresentative(), actual.getRepresentative(), "Representative");
		assertEquals(expected.getDirector(), actual.getDirector(), "Director");
		assertEquals(expected.getRepresentativeLabor(), actual.getRepresentativeLabor(), "RepresentativeLabor");
		assertEquals(expected.getDueDate(), actual.getDueDate(), "DueDate");
		assertEquals(expected.getPercentShare(), actual.getPercentShare(), DELTA, "PercentShare");
		assertEquals(expected.getNominalValue(), actual.getNominalValue(), "NominalValue");
		assertEquals(expected.getShareNumber(), actual.getShareNumber(), "ShareNumber");
		assertEquals(expected.getChargeDescription(), actual.getChargeDescription(), "ChargeDescription");
	}
	
	public static void assertEqualsCompany(Company expected, Company actual) {
		assertEqualsNulls( "Company", expected, actual);
		assertEqualsRegistry(expected, actual);
		assertEquals(expected.isActive(), actual.isActive(), "Active");
		assertEquals(expected.isSurcharge(), actual.isSurcharge(), "Surcharge");
		assertEquals(expected.isWithholding(), actual.isWithholding(), "Withholding");
		assertEquals(expected.isVatAccrualPayment(), actual.isVatAccrualPayment(), "VatAccrualPayment");
		assertEquals(expected.iseInvoice(), actual.iseInvoice(), "eInvoice");
	}
	
	public static void assertEqualsInvoice(Invoice expected, Invoice actual) {
		assertEqualsNulls( "Invoice", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getActivity().getId(), actual.getActivity().getId(), "Activity");
		assertEquals(expected.getEpigraph(), actual.getEpigraph(), "Epigraph");
		assertEquals(expected.getInvestAsset(), actual.getInvestAsset(), "InvestAsset");
		assertEquals(expected.getProject(), actual.getProject(), "Project");
		assertEquals(expected.getSeries(), actual.getSeries(), "Series");
		assertEquals(expected.getNumber(), actual.getNumber(), "Number");
		assertEquals(expected.getReferenceCode(), actual.getReferenceCode(), "ReferenceCode");
		assertEquals(expected.getIssueDate(), actual.getIssueDate(), "IssueDate");
		assertEquals(expected.getTaxDate(), actual.getTaxDate(), "TaxDate");
		assertEquals(expected.getRectificationType(), actual.getRectificationType(), "RectificationType");
		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(), "SecurityLevel");
		assertEquals(expected.getRectificationInvoice(), actual.getRectificationInvoice(), "RectificationInvoice");
		// assertEqualsRegistry(expected.getRegistryData(), actual.getRegistryData());
		assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
		assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(), "RegistryDocument");
		assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(), "RegistryDocumentType");
		assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(), "RegistryDocumentCountry");
		assertEquals(expected.getRegistryName(), actual.getRegistryName(), "RegistryName");
		assertEqualsScopeId(expected.getScope(), actual.getScope());
		assertEquals(expected.getType(), actual.getType(), "Type");
		assertEquals(expected.getTransaction(), actual.getTransaction(), "Transaction");
		assertEquals(expected.isRecorded(), actual.isRecorded(), "Recorded");
		assertEquals(expected.isSurcharge(), actual.isSurcharge(), "Surcharge");
		assertEquals(expected.isWithholding(), actual.isWithholding(), "Withholding");
		assertEquals(expected.isWithholdingFarmer(), actual.isWithholdingFarmer(), "WithholdingFarmer");
		assertEquals(expected.isVatAccrualPayment(), actual.isVatAccrualPayment(), "VatAccrualPayment");
		assertEquals(expected.isInvestment(), actual.isInvestment(), "Investment");
		assertEquals(expected.isService(), actual.isService(), "Service");
		assertEquals(expected.isAdvance(), actual.isAdvance(), "Advance");
		assertEquals(expected.isSigned(), actual.isSigned(), "Signed");
		assertEquals(expected.getTaxableBase(), actual.getTaxableBase(), DELTA, "TaxableBase");
		assertEquals(expected.getVatQuota(), actual.getVatQuota(), DELTA, "VatQuota");
		assertEquals(expected.getRetentionQuota(), actual.getRetentionQuota(), DELTA, "RetentionQuota");
		assertEquals(expected.getTotal(), actual.getTotal(), DELTA, "Total");
		assertEquals(expected.getPosShift(), actual.getPosShift(), "PosShift");
		assertEquals(expected.getSeller(), actual.getSeller(), "Seller");
		assertEquals(expected.getSellerName(), actual.getSellerName(), "SellerName");
		assertEquals(expected.getComments(), actual.getComments(), "Comments");
		assertEquals(expected.getRemarks(), actual.getRemarks(), "Remarks");
		assertEqualsInvoiceFiscal(expected.getFiscal(), actual.getFiscal());
	}
	
	public static void assertEqualsInvoiceBreakdown(InvoiceBreakdown expected, InvoiceBreakdown actual) {
		assertEqualsNulls( "InvoiceBreakdown", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getInvoice(), actual.getInvoice(), "Invoice");	
		assertEquals(expected.getTaxType(), actual.getTaxType(), "TaxType");
		assertEquals(expected.getBase(), actual.getBase(), DELTA, "Base");
		assertEquals(expected.getPercentage(), actual.getPercentage(), DELTA, "Percentage");
		assertEquals(expected.getQuota(), actual.getQuota(), DELTA, "Quota");
		assertEquals(expected.getSurcharge(), actual.getSurcharge(), DELTA, "Surcharge");
		assertEquals(expected.getSurchargeQuota(), actual.getSurchargeQuota(), DELTA, "SurchargeQuota");
		assertEquals(expected.getDeductibleQuota(), actual.getDeductibleQuota(), DELTA, "DeductibleQuota");
		assertEquals(expected.getWithholdingType(), actual.getWithholdingType(), "WithholdingType"); 
		assertEquals(expected.getVatDeductionType(), actual.getVatDeductionType(), "VatDeductionType");
		
	}

	public static void assertEqualsInvoiceFiscal(InvoiceFiscal expected, InvoiceFiscal actual) {
		if (expected != null) {
			assertEqualsNulls( "InvoiceFiscal", expected, actual);
			assertEquals(expected.getInvoice(), actual.getInvoice(), "Invoice");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			for (VATTaxRegime vatRegime : VATTaxRegime.values()) {
				assertEquals(expected.isVatRegimeEnabled(vatRegime), actual.isVatRegimeEnabled(vatRegime), "InvoiceFiscal " + vatRegime.getName());	
			}
		}
	}
	
	public static void assertEmployee(Employee expected, Employee actual) {
		assertEquals(expected.getCcc(), actual.getCcc(), "Ccc");
		assertEquals(expected.getNaf(), actual.getNaf(), "Naf");
		assertEquals(expected.getDni(), actual.getDni(), "Dni");
		assertEquals(expected.getStartDate(), actual.getStartDate(), "StartDate");
		assertEquals(expected.getEndDate(), actual.getEndDate(), "EndDate");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getRegime(), actual.getRegime(), "Regime");
		assertEquals(expected.getFactor(), actual.getFactor(), "Factor");
		assertEquals(expected.getOccupation(), actual.getOccupation(), "Occupation");
		assertEquals(expected.getContractType(), actual.getContractType(), "ContractType");
		assertEquals(expected.getRlce(), actual.getRlce(), "Rlce");
		assertEquals(expected.getQuoteGroup(), actual.getQuoteGroup(), "QuoteGroup");
		assertEquals(expected.getCategory(), actual.getCategory(), "Category");
		assertEquals(expected.getSex(), actual.getSex(), "Sex");
		assertEquals(expected.getBirthDate(), actual.getBirthDate(), "BirthDate");
	}

	public static void assertEqualsIrpfBreakdown (IrpfBreakdown expected, IrpfBreakdown actual) {
		assertEqualsNulls( "IrpfBreakdown", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getActivity(), actual.getActivity(), "activity");
			assertEquals(expected.getActivityDescription(), actual.getActivityDescription(), "activityDescription");
			assertEquals(expected.getEpigraph(), actual.getEpigraph(), "epigraph");
			assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(), "registryDocument");
			assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(), "registryDocumentType");
			assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(), "registryDocumentCountry");
			assertEquals(expected.getName(), actual.getName(), "name");
			assertEquals(expected.getIssueDate(), actual.getIssueDate(), "issueDate");
			assertEquals(expected.getChargeDate(), actual.getChargeDate(), "chargeDate");
			assertEquals(expected.isFromSalary(), actual.isFromSalary(), "fromSalary");
			assertEquals(expected.isInsidePeriod(), actual.isInsidePeriod(), "insidePeriod"); 
			assertEquals(expected.getSalary(), actual.getSalary(), "salary");
			assertEquals(expected.getInvoiceType(), actual.getInvoiceType(), "invoiceType");
			assertEquals(expected.getInvoice(), actual.getInvoice(), "invoice");
			assertEquals(expected.getSeries(), actual.getSeries(), "series");
			assertEquals(expected.getNumber(), actual.getNumber(), "number");
			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(), "referenceCode"); 
			assertEquals(expected.getTaxDate(), actual.getTaxDate(), "taxDate");
			assertEquals(expected.getWithholdingType(), actual.getWithholdingType(), "withholdingType"); 
			assertEquals(expected.getIRPFRegime(), actual.getIRPFRegime(), "regime");
			assertEquals(expected.isInKind(), actual.isInKind(), "inKind");
			assertEqualsDouble("base", expected.getBase(), actual.getBase());
			assertEqualsDouble("percent", expected.getPercent(), actual.getPercent());
			assertEqualsDouble("quota", expected.getQuota(), actual.getQuota());
			assertEqualsDouble("deductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent());
			assertEqualsDouble("deductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota());
			assertEquals(expected.getGroupedBy(), actual.getGroupedBy(), "groupByNif");
			assertEquals(expected.getZip(), actual.getZip(), "zip");
			assertEquals(expected.getCity(), actual.getCity(), "city");
		}
	}
	
	public static void assertEqualsVatContext (VatContext expected, VatContext actual) {
		assertEqualsNulls( "VatContext", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getInvoice(), actual.getInvoice(), "invoice");
			assertEquals(expected.getActivity(), actual.getActivity(), "activity");
			assertEquals(expected.getActivityDescription(), actual.getActivityDescription(), "activityDescription");
			assertEquals(expected.getVatRegime(), actual.getVatRegime(), "vatRegime");
			assertEquals(expected.isVatSurchargeRegime(), actual.isVatSurchargeRegime(), "vatSurchargeRegime");
			assertEquals(expected.getEpigraph(), actual.getEpigraph(), "epigraph");
			assertEquals(expected.getDocumentNumber(), actual.getDocumentNumber(), "documentNumber");
			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(), "referenceCode");
			assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(), "registryDocument");
			assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(), "registryDocumentType");
			assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(), "registryDocumentCountry");
			assertEquals(expected.getRegistry(), actual.getRegistry(), "registry");
			assertEquals(expected.getRegistryName(), actual.getRegistryName(), "registryName");
			assertEquals(expected.getIssueDate(), actual.getIssueDate(), "issueDate");
			assertEquals(expected.getTaxDate(), actual.getTaxDate(), "taxDate");
			assertEquals(expected.getCreationDate(), actual.getCreationDate(), "creationDate");
			assertEquals(expected.getRegContableDate(), actual.getRegContableDate(), "regContableDate");
			assertEquals(expected.getDetailDescription(), actual.getDetailDescription(), "detailDescription");
			assertEquals(expected.isInsidePeriod(), actual.isInsidePeriod(), "insidePeriod");
			assertEquals(expected.getInvoiceType(), actual.getInvoiceType(), "invoiceType");
			assertEquals(expected.getRectificationType(), actual.getRectificationType(), "rectificationType");
			assertEquals(expected.getRectificationInvoice(), actual.getRectificationInvoice(), "rectificationInvoice");
			assertEquals(expected.isService(), actual.isService(), "service");
			assertEquals(expected.getTransaction(), actual.getTransaction(), "transaction");
			assertEquals(expected.isInvestment(), actual.isInvestment(), "investment");
			assertEquals(expected.isVatAccrualRegime(), actual.isVatAccrualRegime(), "vatAccrualRegime");
			assertEquals(expected.getVatDeductionType(), actual.getVatDeductionType(), "vatDeductionType");
			assertEquals(expected.isFarmerRegime(), actual.isFarmerRegime(), "farmerRegime");
			assertEquals(expected.isPrepayment(), actual.isPrepayment(), "prepayment");
			assertEquals(expected.isVatImportation(), actual.isVatImportation(), "vatImportation");
			assertEquals(expected.hasDuaLinked(), actual.hasDuaLinked(), "duaLinked");
			assertEqualsDouble("base", expected.getBase(), actual.getBase());
			assertEqualsDouble("percentage", expected.getPercentage(), actual.getPercentage());
			assertEqualsDouble("quota", expected.getQuota(), actual.getQuota());
			assertEquals(expected.getInvestAsset(), actual.getInvestAsset(), "investAsset");
			assertEqualsDouble("deductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent());
			assertEqualsDouble("deductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota());
			assertEquals(expected.isSurcharge(), actual.isSurcharge(), "surcharge");
			assertEqualsDouble("surchargePercent", expected.getSurchargePercent(), actual.getSurchargePercent());
			assertEqualsDouble("surchargeQuota", expected.getSurchargeQuota(), actual.getSurchargeQuota());
			assertEquals(expected.isProrrated(), actual.isProrrated(), "prorrated");
			assertEqualsDouble("prorratePercent", expected.getProrratePercent(), actual.getProrratePercent());
			assertEqualsDouble("prorrateQuota", expected.getProrrateQuota(), actual.getProrrateQuota());
			assertEquals(expected.getSiiStatus(), actual.getSiiStatus(), "siiStatus");
			assertEquals(expected.getAmortizationDescription(), actual.getAmortizationDescription(), "amortizationDescription");
			assertEquals(expected.getAmortizationPercentage(), actual.getAmortizationPercentage(), "amortizationPercentage");
			assertEquals(expected.getAmortizationInitialDate(), actual.getAmortizationInitialDate(), "amortizationInitialDate");
			assertEquals(expected.isFinancePending(), actual.isFinancePending(), "financePending"); 
			assertEqualsDouble("amount347", expected.getAmount347(), actual.getAmount347());
			assertEquals(expected.hasRetention(), actual.hasRetention(), "hasRetention");
			assertEquals(expected.getRectificateInvoiceTaxDate(), actual.getRectificateInvoiceTaxDate(), "rectificateInvoiceTaxDate");
			assertEquals(expected.getRectificateYear(), actual.getRectificateYear(), "rectificateYear");
			assertEquals(expected.getRectificatePeriod(), actual.getRectificatePeriod(), "rectificatePeriod");
		}
	}
	
	

	public static <T extends FiscalModel> void assertFiscalModel(T expected, T actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getDomainName(), actual.getDomainName(), "DomainName");
		assertEquals(expected.getYear(), actual.getYear(), "Year");
		assertEquals(expected.getFinance(), actual.getFinance(), "Finance");
		assertEquals(expected.getModel(), actual.getModel(), "Model");
		assertEquals(expected.getPeriod(), actual.getPeriod(), "Period");
		assertEquals(expected.getAdministration(), actual.getAdministration(), "Administration");
		assertEquals(expected.getStatus(), actual.getStatus(), "Status");
		assertEquals(expected.getDeclarationResult(), actual.getDeclarationResult(), "DeclarationResult");
		assertEquals(expected.getDeclarationResultType(), actual.getDeclarationResultType(), "DeclarationType");
		assertEquals(expected.isConfidential(), actual.isConfidential(), "Confidential");
		assertEquals(expected.isComplementary(), actual.isComplementary(), "Complementary");
		assertEquals(expected.isReplacement(), actual.isReplacement(), "Replacement");
		assertEquals(expected.isWithoutActivity(), actual.isWithoutActivity(), "WithoutActivity");
		assertEquals(expected.getNumber(), actual.getNumber(), "Number");
		assertEquals(expected.getReplacedNumber(), actual.getReplacedNumber(), "ReplacedNumber");
		assertEquals(expected.getComments(), actual.getComments(), "Comments");
		assertEquals(expected.getDocument(), actual.getDocument(), "Document");
		assertEquals(expected.getSurname(), actual.getSurname(), "Surname");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getStreetInitial(), actual.getStreetInitial(), "StreetInitial");
		assertEquals(expected.getStreetName(), actual.getStreetName(), "StreetName");
		assertEquals(expected.getStreetNumber(), actual.getStreetNumber(), "StreetNumber");
		assertEquals(expected.getStreetStair(), actual.getStreetStair(), "StreetStair");
		assertEquals(expected.getStreetFloor(), actual.getStreetFloor(), "StreetFloor");
		assertEquals(expected.getStreetDoor(), actual.getStreetDoor(), "StreetDoor");
		assertEquals(expected.getPhone(), actual.getPhone(), "Phone");
		assertEquals(expected.getTown(), actual.getTown(), "Town");
		assertEquals(expected.getProvince(), actual.getProvince(), "Province");
		assertEquals(expected.getZip(), actual.getZip(), "Zip");
		assertEquals(expected.getAdmonAeat(), actual.getAdmonAeat(), "AdmonAeat");
		assertEquals(expected.getContactPerson(), actual.getContactPerson(), "ContactPerson");
		assertEquals(expected.getContactPhone(), actual.getContactPhone(), "ContactPhone");
		assertEquals(expected.getContactCellular(), actual.getContactCellular(), "ContactCellular");
		assertEquals(expected.getContactEmail(), actual.getContactEmail(), "ContactEmail");
		assertEquals(expected.getIban(), actual.getIban(), "Iban");
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
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getDomainName(), actual.getDomainName(), "DomainName");
		assertEquals(expected.getEnterprise(), actual.getEnterprise(), "Enterprise");
		assertEquals(expected.getYear(), actual.getYear(), "Year");
		assertEquals(expected.getAdministration(), actual.getAdministration(), "Administration");
		assertEquals(expected.getStatus(), actual.getStatus(), "Status");
		assertEquals(expected.isConfidential(), actual.isConfidential(), "Confidential");
		assertEquals(expected.isComplementary(), actual.isComplementary(), "Complementary");
		assertEquals(expected.isReplacement(), actual.isReplacement(), "Replacement");
		assertEquals(expected.getReceipt(), actual.getReceipt(), "Receipt");
		assertEquals(expected.getReplacedReceipt(), actual.getReplacedReceipt(), "ReplacedReceipt");
		assertEquals(expected.getComments(), actual.getComments(), "Comments");
		assertEquals(expected.getDocument(), actual.getDocument(), "Document");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getContactPerson(), actual.getContactPerson(), "ContactPerson");
		assertEquals(expected.getContactPhone(), actual.getContactPhone(), "ContactPhone");
		assertEquals(expected.getContactMail(), actual.getContactMail(), "ContactMail");
		assertEquals(expected.getReceiverCountTotal(), actual.getReceiverCountTotal(), "ReceiverCountTotal");
		assertEquals(expected.getReceiptTotal(), actual.getReceiptTotal(), DELTA, "receiptTotal");
		assertEquals(expected.getRetentionTotal(), actual.getRetentionTotal(), DELTA, "retentionTotal");
	}
	
	public static void assertMod349(Mod349 expected, Mod349 actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getYear(), actual.getYear(), "Year");
		assertEquals(expected.getPeriod(), actual.getPeriod(), "Period");
		assertEquals(expected.getAdministration(), actual.getAdministration(), "Administration");
		assertEquals(expected.getStatus(), actual.getStatus(), "Status");
		assertEquals(expected.isConfidential(), actual.isConfidential(), "Confidential");
		assertEquals(expected.isComplementary(), actual.isComplementary(), "Complementary");
		assertEquals(expected.isReplacement(), actual.isReplacement(), "Replacement");
		assertEquals(expected.getNumber(), actual.getNumber(), "Number");
		assertEquals(expected.getReplacedNumber(), actual.getReplacedNumber(), "ReplacedNumber");
		assertEquals(expected.getComments(), actual.getComments(), "Comments");
		assertEquals(expected.getDocument(), actual.getDocument(), "Document");
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getContactPerson(), actual.getContactPerson(), "ContactPerson");
		assertEquals(expected.getContactPhone(), actual.getContactPhone(), "ContactPhone");
		assertEquals(expected.getContactMail(), actual.getContactMail(), "ContactMail");
		assertEquals(expected.getRepresentativeDocument(), actual.getRepresentativeDocument(), "RepresentativeDocument");
	}
	
	public static void assertEqualsOffer(Offer expected, Offer actual) {
		// TODO Completar...
		assertEqualsNulls( "Offer", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getStatus().value(), actual.getStatus().value(), "Status");
		}
	}
	
	public static void assertEqualsOfferDetail(OfferDetail expected, OfferDetail actual) {
		assertEqualsNulls( "Offer Detail", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getOffer().getId(), actual.getOffer().getId(), "Offer");
			assertEquals(expected.getLine(), actual.getLine(), "Line");
			assertEquals(expected.getItem().getId(), actual.getItem().getId(), "Item");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.getQuantity(), actual.getQuantity(), DELTA, "Quantity");
			assertEquals(expected.getDiscountExpression(), actual.getDiscountExpression(), "Discount Expression");
			assertEquals(expected.getStatus().value(), actual.getStatus().value(), "Status");
		}
	}
	
	public static void assertEqualsDelivery(Delivery expected, Delivery actual) {
		assertEqualsNulls( "Delivery", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals(expected.getAddress().getId(), actual.getAddress().getId());
			assertEquals(expected.getBankAccount(), actual.getBankAccount(), "BankAccount");
			assertEquals(expected.getBankAlias(), actual.getBankAlias(), "Bank alias");
			assertEquals(expected.getBic(), actual.getBic(), "Bic");
			assertEquals(expected.getCarrier(), actual.getCarrier(), "Carrier");
			assertEquals(expected.getCarrierPacking(), actual.getCarrierPacking(), "Carrier Packing");
			assertEquals(expected.getComments(), actual.getComments(), "Comments");
			assertEquals(expected.getCreationUser(), actual.getCreationUser(), "Creation user");
			assertEquals(expected.getCustomer().getId(), actual.getCustomer().getId());
			assertEquals(expected.getDaysBetweenPymnt(), actual.getDaysBetweenPymnt(), "Days between pymnt");
			assertEquals(expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt(), "Days to first pymnt");
			assertEquals(expected.getDetails(), actual.getDetails(), "Details");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getDriver(), actual.getDriver(), "Driver");
			assertEquals(expected.getDriverDocument(), actual.getDriverDocument(), "Driver document");
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getModificationUser(), actual.getModificationUser(), "Modification user");
			assertEquals(expected.getNumber(), actual.getNumber(), "Number");
			assertEquals(expected.getNumberOfPymnts(), actual.getNumberOfPymnts(), "Number of Pymnts");
			assertEquals(expected.getNumberPlate(), actual.getNumberPlate(), "Number plate");
			assertEquals(expected.getPackaging(), actual.getPackaging(), "Packaging");
			assertEquals(expected.getPackagingData(), actual.getPackagingData(), "Packaging data");
			assertEquals(expected.getPayMethod().getId(), actual.getPayMethod().getId());
			assertEquals(expected.getProject().getId(), actual.getProject().getId());
			assertEquals(expected.getPymntDays(), actual.getPymntDays(), "Pymnt days");
			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(), "Reference code");
			assertEquals(expected.getRemarks(), actual.getRemarks(), "Remarks");
			assertEquals(expected.getScope().getId(), actual.getScope().getId());
			assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(), "Security level");
			assertEquals(expected.getSeries(), actual.getSeries(), "Series");
			assertEquals(expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress(), "Shipping alternative address");
			assertEquals(expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2(), "Shipping alternative address two");
			assertEquals(expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity(), "Shipping alternative city");
			assertEquals(expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone(), "Shipping alternative phone");
			assertEquals(expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient(), "Shipping alternative recipient");
			assertEquals(expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip(), "Shipping alternative zip");
			assertEquals(expected.getShippingContact(), actual.getShippingContact(), "Shipping contact");
			assertEquals(expected.getShippingPeriod(), actual.getShippingPeriod(), "Shipping period");
			assertEquals(expected.getShippingPeriodValue(), actual.getShippingPeriodValue(), "Shipping period value");
			assertEquals(expected.getShippingStatus(), actual.getShippingStatus(), "Shipping status");
			assertEquals(expected.getShippingStatusValue(), actual.getShippingStatusValue(), "Shipping status value");
			assertEquals(expected.getStatus(), actual.getStatus(), "Status");
			assertEquals(expected.getTotalPackages(), actual.getTotalPackages(), "Total packages");
			assertEquals(expected.getTotalWeight(), actual.getTotalWeight(), "Total weight");
			assertEquals(expected.getTrackingNumber(), actual.getTrackingNumber(), "Tracking number");
			assertEquals(expected.getWorkplace().getId(), actual.getWorkplace().getId());
		}
	}
	
	public static void assertEqualsWorkplace(Workplace expected, Workplace actual) {
		assertEqualsNulls("Workplace", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals(expected.getAddress() != null ? expected.getAddress().getId() : null, actual.getAddress() != null ? actual.getAddress().getId() : null, "Address");
			assertEquals(expected.getCustomer(), actual.getCustomer(), "Customer");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getEconomicAgreement(), actual.getEconomicAgreement(), "Economicagreement");
			assertEquals(expected.getEnterprise(), actual.getEnterprise(), "Enterprise");
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getScope(), actual.getScope(), "Scope");
		}
	}
	
	public static void assertEqualsEdiCodes(EdiCodes expected, EdiCodes actual) {
		assertEqualsNulls("EdiCodes", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals(expected.getBycode(), actual.getBycode(), "Bycode");
			assertEquals(expected.getCompanyEdiCode(), actual.getCompanyEdiCode(), "Company EdiCode");
			assertEquals(expected.getCustomerEdiCode(), actual.getCustomerEdiCode(), "Customer EdiCode");
			assertEquals(expected.getCustomerEdiHeader(), actual.getCustomerEdiHeader(), "Customer EdiHeader");
			assertEquals(expected.getCustomerEdiInvoice(), actual.getCustomerEdiInvoice(), "Customer EdiInvoice");
			assertEquals(expected.getCustomerEdiPoint(), actual.getCustomerEdiPoint(), "Customer EdiPoint");
			assertEquals(expected.getCustomerPackage(), actual.getCustomerPackage(), "Customer Package");
			assertEquals(expected.getDeliveryPointEdiCode(), actual.getDeliveryPointEdiCode(), "Delivery Point EdiCode");
			assertEquals(expected.getDepartment(), actual.getDepartment(), "Customer Department");
			assertEquals(expected.getDpcode(), actual.getDpcode(), "Customer Dpcode");
			assertEquals(expected.getIvcode(), actual.getIvcode(), "Customer Ivcode");
			assertEquals(expected.getMrcode(), actual.getMrcode(), "Customer Mrcode");
			assertEquals(expected.getMscode(), actual.getMscode(), "Customer Mscode");
			assertEquals(expected.getPwcode(), actual.getPwcode(), "Customer Pwcode");
			assertEquals(expected.getShcode(), actual.getShcode(), "Customer Shcode");
			assertEquals(expected.getSucode(), actual.getSucode(), "Customer Sucode");
			assertEquals(expected.getUccode(), actual.getUccode(), "Customer Uccode");
		}
	}
	
	public static void assertEqualsDeliveryDetail(DeliveryDetail expected, DeliveryDetail actual) {
		assertEqualsNulls("DeliveryDetail", expected, actual);
		
		if (expected != null && actual != null) {
			assertEquals(expected.getCreationUser(), actual.getCreationUser(), "Creation user");
			assertEquals(expected.getDelivery().getId(), actual.getDelivery().getId());
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.getDiscountExpression().getDiscountExpr(), actual.getDiscountExpression().getDiscountExpr(), "Discount expression");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getItem().getId(), actual.getItem().getId());
			assertEquals(expected.getLine(), actual.getLine(), "Line");
			assertEquals(expected.getModificationUser(), actual.getModificationUser(), "Modification user");
			assertEquals(expected.getPrice(), actual.getPrice(), "Price");
			//assertEquals(expected.getPurchaseReference(), actual.getPurchaseReference(), "Purchased reference");
			assertEquals(expected.getQuantity(), actual.getQuantity(), DELTA, "Quantity");
			assertEquals(expected.getSalesDetail(), actual.getSalesDetail(), "Sales detail");
			assertEquals(expected.getSalesDetailData(), actual.getSalesDetailData(), "Sales detail data");
			assertEquals(expected.getWarehouse(), actual.getWarehouse(), "Warehouse");
		}
	}
	
	public static void assertEqualsSales(Sales expected, Sales actual) {
		// TODO Completar...
		assertEqualsNulls( "Sales", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getStatus().value(), actual.getStatus().value(), "Status");
		}
	}
	
	public static void assertEqualsSalesDetail(SalesDetail expected, SalesDetail actual) {
		// TODO Completar...
		assertEqualsNulls("SalesDetail", expected, actual);
		if (expected != null) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getStatus().value(), actual.getStatus().value(), "Status");
		}
	}
	
	public static void assertEqualsQuestion(Question expected, Question actual) {
		if (expected != null && actual != null) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.isActive(), actual.isActive(), "Active");
			assertEquals(expected.getText(), actual.getText(), "Text");
			assertEquals(expected.getType(), actual.getType(), "Type");
			assertEquals(expected.getArgument(), actual.getArgument(), "Argument");
			assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
		}
	}

	public static void assertEqualsItem(Item expected, Item actual) {
		assertEqualsNulls( "Item", expected, actual);
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain().getId(), actual.getDomain().getId(), "Domain");
		assertEquals(expected.getDetail(), actual.getDetail(), "Detail");
		assertEquals(expected.getDetail2(), actual.getDetail2(), "Detail2");
		assertEquals(expected.getDetail3(), actual.getDetail3(), "Detail3");
		assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		assertEquals(expected.getSerialNumber(), actual.getSerialNumber(), "SerialNumber");
		assertEquals(expected.getSerialDate(), actual.getSerialDate(), "SerialDate");
		assertEquals(expected.getExpireDate(), actual.getExpireDate(), "ExpireDate");
		assertEquals(expected.getBarcode(), actual.getBarcode(), "Barcode");
		assertEquals(expected.getStatus(), actual.getStatus(), "Status");
		assertEqualsProduct(expected.getProduct(), actual.getProduct());
		assertEquals(expected.getPrice(), actual.getPrice(), DELTA, "Price");
		assertEquals(expected.getExpensesPercent(), actual.getExpensesPercent(), DELTA, "ExpensesPercent");
		assertEquals(expected.getExpensesFixed(), actual.getExpensesFixed(), DELTA, "ExpensesFixed");
		assertEquals(expected.getProfitPercent(), actual.getProfitPercent(), DELTA, "ProfitPercent");
		assertEquals(expected.getPurchasePrice(), actual.getPurchasePrice(), DELTA, "PurchasePrice");
		assertEquals(expected.isInternet(), actual.isInternet(), "Internet");
		assertEqualsTag(expected.getPackFormatTag(), actual.getPackFormatTag());
		assertEquals(expected.getPackUnits(), actual.getPackUnits(), "PackUnits");
		assertEqualsTag(expected.getPackUnitsTag(), actual.getPackUnitsTag());
		assertEquals(expected.getPackMeasurement(), actual.getPackMeasurement(), DELTA, "PackMeasurement");
		assertEqualsTag(expected.getPackMeasurementTag(), actual.getPackMeasurementTag());
		assertEqualsTag(expected.getStockUnitTag(), actual.getStockUnitTag());
	}
	
	public static void assertEqualsTag(Tag expected, Tag actual) {
		assertEquals(expected.getId(), actual.getId(), "Id");
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		if(expected.getType() != null && actual.getType() != null) {
			assertEquals(expected.getType().value(), actual.getType().value(), "Type");
		} else {
			assertEquals(expected.getType(), actual.getType(), "Type");
		}
		assertEquals(expected.getName(), actual.getName(), "Name");
		assertEquals(expected.getColor(), actual.getColor(), "Color");
		assertEquals(expected.getStartDate(), actual.getStartDate(), "StartDate");
		assertEquals(expected.getEndDate(), actual.getEndDate(), "EndDate");
	}
	
	public static void assertEqualsBankAccount(BankAccount expected, BankAccount actual) {
		if (expected != null) {
			assertEquals(expected.getBankCode(), actual.getBankCode(), "BankCode");
			assertEquals(expected.getBankCodeLength(), actual.getBankCodeLength(), "BankCodeLenght");
			assertEquals(expected.getBban(), actual.getBban(), "Bban");
			assertEquals(expected.getBban1(), actual.getBban1(), "Bban1");
			assertEquals(expected.getBban1(), actual.getBban1(), "Bban1");
			assertEquals(expected.getBban2(), actual.getBban2(), "Bban2");
			assertEquals(expected.getBban3(), actual.getBban3(), "Bban3");
			assertEquals(expected.getBban4(), actual.getBban4(), "Bban4");
			assertEquals(expected.getBban5(), actual.getBban5(), "Bban5");
			assertEquals(expected.getBban6(), actual.getBban6(), "Bban6");
			assertEquals(expected.getBban7(), actual.getBban7(), "Bban7");
			assertEquals(expected.getBban8(), actual.getBban8(), "Bban8");
			assertEquals(expected.getCCC(), actual.getCCC(), "CCC");
			assertEquals(expected.getCCC1(), actual.getCCC1(), "CCC1");
			assertEquals(expected.getCCC2(), actual.getCCC2(), "CCC2");
			assertEquals(expected.getCCC3(), actual.getCCC3(), "CCC3");
			assertEquals(expected.getCCC4(), actual.getCCC4(), "CCC4");
			assertEquals(expected.getCheck(), actual.getCheck(), "Check");
			assertEquals(expected.getCountry(), actual.getCountry(), "Country");
			assertEquals(expected.getIban(), actual.getIban(), "Iban");
			assertEquals(expected.getIbanLength(), actual.getIbanLength(), "IbanLength");
			assertEquals(expected.getMaskedIban(), actual.getMaskedIban(), "MaskedIban");
			assertEquals(expected.getPureCCC(), actual.getPureCCC(), "PureCCC");
			assertEquals(expected.getSeparatedIban(), actual.getSeparatedIban(), "SeparatedIban");
		}
	}
	
	public static void assertEqualsRegistryBank(RegistryBank expected, RegistryBank actual) {
		assertEqualsNulls( "RegistryBank", expected, actual);
		if (expected != null) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEqualsAccount(expected.getAccount(), actual.getAccount());
			assertEquals(expected.getActive(), actual.getActive(), "Active");
			assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
			assertEqualsBankAccount(expected.getBankAccount(), actual.getBankAccount());
			assertEquals(expected.getBic(), actual.getBic(), "Bic");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getFullName(), actual.getFullName(), "FullName");
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
			assertEquals(expected.getRequisition(), actual.getRequisition(), "Requisition");
			assertEquals(expected.getSepaMandateRef(), actual.getSepaMandateRef(), "SepaMandateRef");
			assertEquals(expected.getSuffix(), actual.getSuffix(), "Suffix");
		}
	}
	
	public static void assertEqualsCarrier(Carrier expected, Carrier actual) {
		if (expected != null && actual != null) {
			assertEquals(expected.getAlias(), actual.getAlias(), "Alias");
			Asserts.assertEqualsRegistry(expected.get(), actual.get());
			assertEquals(expected.getDocument(), actual.getDocument(), "Document");
			assertEquals(expected.getDocumentCountry().toString(), actual.getDocumentCountry().toString(), "Country");
			assertEquals(expected.getDocumentType().toString(), actual.getDocumentType().toString(), "Document");
			Asserts.assertEqualsDomain(expected.getDomain(), actual.getDomain());
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getName(), actual.getName(), "Name");
			assertEquals(expected.getDocument(), actual.getDocument(), "Document");
			assertEquals(expected.getNationality().toString(), actual.getNationality().toString(), "Nationality");
			Asserts.assertEqualsScope(expected.getScope(), actual.getScope());
			assertEquals(expected.getSecurityLevel().toString(), actual.getSecurityLevel().toString(), "Security Level");
			assertEquals(expected.getStatus().toString(), actual.getStatus().toString(), "Status");
		}
	}
	
	
	public static void assertEqualsSeries(Series expected, Series actual) {
		assertEqualsNulls( "Series", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getScope(), actual.getScope(), "Scope");
			assertEquals(expected.getCode(), actual.getCode(), "Code");
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.isActive(), actual.isActive(), "Active");
			assertEquals(expected.isTas(), actual.isTas(), "Tas");
			assertEquals(expected.isOffer(), actual.isOffer(), "Offer");
			assertEquals(expected.isSales(), actual.isSales(), "Sales");
			assertEquals(expected.isDelivery(), actual.isDelivery(), "Delivery");
			assertEquals(expected.isInvoice(), actual.isInvoice(), "Invoice");
			assertEquals(expected.isRectification(), actual.isRectification(), "Rectification"); 
			assertEquals(expected.isPos(), actual.isPos(), "Pos");
			assertEquals(expected.getSecurityLevel().toString(), actual.getSecurityLevel().toString(), "Security Level");
		}
	}
	
	public static void assertEqualsInvoiceSeries(InvoiceSeries expected, InvoiceSeries actual) {
		assertEqualsNulls( "InvoiceSeries", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getDescription(), actual.getDescription(), "Description");
			assertEquals(expected.isSales(), actual.isSales(), "Sales");
			assertEquals(expected.getFromNumber(), actual.getFromNumber(), "FromNumber");
			assertEquals(expected.getToNumber(), actual.getToNumber(), "ToNumber");
			assertEquals(expected.getCount(), actual.getCount(), "Count");
		}
	}
	
	public static void assertEqualsInvoiceData(InvoiceData expected, InvoiceData actual) {
		assertEqualsNulls( "InvoiceData", expected, actual);
		if (expected != null ) {
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getInvoice(), actual.getInvoice(), "Invoice");
			assertEquals(expected.getName(), actual.getName(), "Name");
			assertEquals(expected.getValue(), actual.getValue(), "Value");
//			assertEquals(expected.getStartDate(), actual.getStartDate(), "StartDate");
//			assertEquals(expected.getEndDate(), actual.getEndDate(), "EndDate");
			
		}
	}
	
	public static void assertNotEmptyKeys(String parent, JSONObject json) {
		if ( AonCollectionUtils.isEmpty( json.keySet() )) {
			fail(parent + " is empty");
		}
		for (String name : json.keySet()) {
			String current = parent + " -> " + name;
			JSONArray a = json.optJSONArray(name);
			if (a != null) {
				if ( a.length() == 0 ) {
					fail("Array ..: " + current + " is empty");		
				} 
				for ( int i = 0; i < a.length(); i++) {
					assertNotEmptyKeys( current + "["+i+"]" , a.getJSONObject(i));
				}
			} else {
				JSONObject j = json.optJSONObject(name);
				if (j != null) {
					assertNotEmptyKeys( current , j);
				}
			}
		}
	}

	public static void assertAccountingExpense(AccountingExpense expected, AccountingExpense actual) {
		assertEqualsNulls( "AccountingExpense", expected, actual);
		if (expected != null) {
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEqualsCreditor(expected.getCreditor().orElse(null), actual.getCreditor().orElse(null));
			assertEquals(expected.getDate(), actual.getDate(), "Date");
			assertEquals(expected.getActivity(), actual.getActivity(), "Activity");
			assertEqualsAccount(expected.getExpAccount().orElse(null), actual.getExpAccount().orElse(null)); 
			assertEquals(expected.getConcept(), actual.getConcept(), "Concept");
			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(), "ReferenceCode"); 
			assertEquals(expected.getAmount(), actual.getAmount(), DELTA, "Amount");
			assertEqualsRegistryBank(expected.getBank().orElse(null), actual.getBank().orElse(null));
			assertEqualsAccount(expected.getCashAccount().orElse(null), actual.getCashAccount().orElse(null));
			assertEquals(expected.getComments(), actual.getComments(), "Comments"); 
		}
	}

	public static void assertAccountingIncome(AccountingIncome expected, AccountingIncome actual) {
		assertEqualsNulls( "AccountingIncome", expected, actual);
		if (expected != null) {
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEqualsCustomer(expected.getCustomer().orElse(null), actual.getCustomer().orElse(null));
			assertEquals(expected.getDate(), actual.getDate(), "Date");
			assertEquals(expected.getActivity(), actual.getActivity(), "Activity");
			assertEqualsAccount(expected.getExpAccount().orElse(null), actual.getExpAccount().orElse(null)); 
			assertEquals(expected.getConcept(), actual.getConcept(), "Concept");
			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(), "ReferenceCode"); 
			assertEquals(expected.getAmount(), actual.getAmount(), DELTA, "Amount");
			assertEqualsRegistryBank(expected.getBank().orElse(null), actual.getBank().orElse(null));
			assertEqualsAccount(expected.getCashAccount().orElse(null), actual.getCashAccount().orElse(null));
			assertEquals(expected.getComments(), actual.getComments(), "Comments"); 
		}
	}
	
	public static void assertAccountEntry(AccountEntry expected, AccountEntry actual) {
		assertEqualsNulls( "AccountEntry", expected, actual);
		if (expected != null) {
			assertEquals(expected.getDomain(), actual.getDomain(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getPeriod(), actual.getPeriod(), "Period");
			assertEquals(expected.getPeriodName(), actual.getPeriodName(), "PeriodName");
			assertEquals(expected.getPeriodStatus(), actual.getPeriodStatus(), "PeriodStatus");
			assertEquals(expected.getPeriodStatus(), actual.getPeriodStatus(), "PeriodStatus");
			assertEquals(expected.getEntryDate(), actual.getEntryDate(), "EntryDate");
			assertEquals(expected.getEntryType(), actual.getEntryType(), "EntryType");
			assertEquals(expected.getActivity(), actual.getActivity(), "Activity");
			assertEquals(expected.getActivityDescription(), actual.getActivityDescription(), "ActivityDescription");
			assertEquals(expected.getJournal(), actual.getJournal(), "Journal");
			assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(), "SecurityLevel");
			assertEquals(expected.getComments(), actual.getComments(), "Comments"); 
			for (int i = 0; i < expected.getDetails().size(); i++) {
				assertEqualsAccountEntryDetail(expected.getDetails().get(i), actual.getDetails().get(i));	
			}
		}
	}
	public static void assertEqualsAccountEntryDetail(AccountEntryDetail expected, AccountEntryDetail actual) {
		assertEqualsNulls( "AccountEntryDetail", expected, actual);
		if (expected != null) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getAccountEntry(), actual.getAccountEntry(), "AccountEntry");
			assertEquals(expected.getAccountId(), actual.getAccountId(), "Account");
			assertEquals(expected.getAccountCode(), actual.getAccountCode(), "AccountCode");
			assertEquals(expected.getAccountDescription(), actual.getAccountDescription(), "AccountDescription");
			assertEquals(expected.getLine(), actual.getLine(), "Line");
			assertEquals(expected.getConcept(), actual.getConcept(), "Concept");
			assertEquals(expected.getDebit(), actual.getDebit(), DELTA, "Debit");
			assertEquals(expected.getCredit(), actual.getCredit(), DELTA, "Credit");
			assertEquals(expected.getBalancingAccountId(), actual.getBalancingAccountId(), "BalancingAccount");
			assertEquals(expected.getBalancingAccountCode(), actual.getBalancingAccountCode(), "BalancingAccountCode");
			assertEquals(expected.getBalancingAccountDescription(), actual.getBalancingAccountDescription(), "BalancingAccountDescription");
			assertEquals(expected.getDocumentNumber(), actual.getDocumentNumber(), "DocumentNumber");
		}
	}
	
	public static void assertEqualsRawdoc(Rawdoc expected, Rawdoc actual) {
		assertEqualsNulls( "Rawdoc", expected, actual);
		if (expected != null) {
			assertEquals(expected.getId(), actual.getId(), "Id");
			assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
			assertEquals(expected.getNature(), actual.getNature(), "Nature");
			assertEquals(expected.getType(), actual.getType(), "Type");
			assertEquals(expected.getStatus(), actual.getStatus(), "Status");
			assertEquals(expected.getJson(), actual.getJson(), "Json");
			assertEquals(expected.getLog(), actual.getLog(), "log");
			assertEquals(expected.getMimeType(), actual.getMimeType(), "mimeType");
			assertEquals(expected.getData(), actual.getData(), "data");
			assertEquals(expected.getS3Key(), actual.getS3Key(), "s3Key");
			// assertEquals(expected.getTediInvoice(), actual.getTediInvoice(), "tediInvoice");
			assertEqualsInvoice(expected.getInvoice(),actual.getInvoice());
		}
	}
	
	public static void assertMod421(Mod421 expected, Mod421 actual) {
		assertFiscalModel(expected, actual);
	}
	
	
}