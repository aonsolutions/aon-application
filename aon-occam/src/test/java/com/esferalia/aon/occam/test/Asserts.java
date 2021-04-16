package com.esferalia.aon.occam.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class Asserts {
	
	private static final double DELTA = 1e-15;
	
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

	private static void assertEqualsDomain(Domain expected, Domain actual) {
		assertEqualsNulls( "Domain", expected, actual);
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

	public static void assertEqualsEnterpriseActivity(EnterpriseActivity expected, EnterpriseActivity actual) {
		assertEqualsNulls( "EnterpriseActivity", expected, actual);
		if (expected != null) {
			assertEquals("Id", expected.getId(), actual.getId());
			assertEquals("Description", expected.getDescription(), actual.getDescription());
			assertEquals("Principal",expected.isPrincipal(), actual.isPrincipal());
			assertEquals("Iae",expected.getIae(), actual.getIae());
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
		assertEquals("Id", expected.getId(), actual.getId());
		assertEquals("Domain", expected.getDomain().getId(), actual.getDomain().getId());
		assertEquals("Document",expected.getDocument(), actual.getDocument());
		assertEquals("DocumentType",expected.getDocumentType(), actual.getDocumentType());
		assertEquals("DocumentCountry",expected.getDocumentCountry(), actual.getDocumentCountry());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Alias",expected.getAlias(), actual.getAlias());
		assertEquals("LegalPerson",expected.isLegalPerson(), actual.isLegalPerson());
		assertEquals("Nationality",expected.getNationality(), actual.getNationality());
		assertEquals("SecurityLevel",expected.getSecurityLevel() , actual.getSecurityLevel());
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
		assertEquals("Scope",expected.getScope(),actual.getScope());
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
		assertEquals("Scope",expected.getScope(),actual.getScope());
		assertEquals("Account",expected.getAccount(),actual.getAccount());
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
		assertEquals("Scope",expected.getScope(),actual.getScope());
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
	public static void assertEqualsAccountTrialBalanceReport(AccountTrialBalanceReport expected, AccountTrialBalanceReport actual) {
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
	
}
