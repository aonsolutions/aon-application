package com.esferalia.aon.occam.test;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.invoice.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetail;
import com.esferalia.aon.occam.api.model.invoice.InvoiceMin;
import com.esferalia.aon.occam.api.model.invoice.InvoiceTax;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceAsserts {
	
	private static final double DELTA = 1e-8;

	private static void assertEqualsNulls(String msg,Object expected, Object actual) {
		if ( expected == null) assertNull(msg, actual);
		if ( expected != null) assertNotNull(msg, actual);
	}

	private static void assertEqualsDouble(String msg,double expected,double actual) {
		assertEquals(msg, expected, actual, DELTA);		
	}
	
	private static void assertEqualsDate(String msg,Date expected,Date actual) {
		assertEqualsNulls(msg, expected, actual);
		if (expected != null) {
			LocalDate expectedLD;
			if (expected instanceof java.sql.Date sqlExpected) {
				expectedLD = sqlExpected.toLocalDate();
			} else {
				expectedLD = LocalDate.ofInstant(expected.toInstant(), ZoneId.systemDefault());
			}
			LocalDate actualLD;
			if (actual instanceof java.sql.Date sqlActual) {
				actualLD = sqlActual.toLocalDate();
			} else {
				actualLD = LocalDate.ofInstant(actual.toInstant(), ZoneId.systemDefault());
			}
			assertTrue(expectedLD.isEqual(actualLD));
		}
	}

	private static void assertEqualsCollection(String msg,Collection<?> expected, Collection<?> actual) {
		if ( AonCollectionUtils.isEmpty(expected) && AonCollectionUtils.isNotEmpty(actual)) {
			fail( msg + " actual List is not Empty");
		}
		if ( AonCollectionUtils.isNotEmpty(expected) && AonCollectionUtils.isEmpty(actual)) {
			System.out.println("expected size ..:" +  AonCollectionUtils.size( expected) );
			fail( msg + " actual List is Empty");
		}
		assertEquals("Sizes not fit",AonCollectionUtils.size( expected), AonCollectionUtils.size( actual ));	
	}

	private static void assertEqualsDomainId(Domain expected, Domain actual) {
		assertEqualsNulls( "Domain", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsDomain(Domain expected, Domain actual) {
		assertEqualsNulls( "Domain", expected, actual);
		if (expected != null) {
			assertEquals("Id",expected.getId(), actual.getId());
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
	
	private static void assertEqualsScopeId(Scope expected, Scope actual) {
		assertEqualsNulls( "Scope", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsScope(Scope expected, Scope actual) {
		assertEqualsNulls( "Scope", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
		}
	}
	
	private static void assertEqualsWorkplace(Workplace expected, Workplace actual) {
		assertEqualsNulls("Workplace", expected, actual);
		if (expected != null && actual != null) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Scope",expected.getScope(), actual.getScope());
			assertEquals("Address",expected.getAddress(), actual.getAddress());
			assertEquals("Customer",expected.getCustomer(), actual.getCustomer());
			assertEquals("Economicagreement",expected.getEconomicagreement(), actual.getEconomicagreement());
			assertEquals("Enterprise",expected.getEnterprise(), actual.getEnterprise());
		}
	}

	private static void assertEqualsWarehouse(Warehouse expected, Warehouse actual) {
		assertEqualsNulls("Warehouse", expected, actual);
		if (expected != null && actual != null) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Department",expected.getDepartment(), actual.getDepartment());
			assertEquals("Name",expected.getName(), actual.getName());
			assertEquals("Workplace",expected.getWorkplace(), actual.getWorkplace());
			assertEquals("Active",expected.isActive(), actual.isActive());
		}
	}

	private static void assertEqualsEnterpriseActivityId(EnterpriseActivity expected, EnterpriseActivity actual) {
		assertEqualsNulls( "Activity", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsEnterpriseActivity(EnterpriseActivity expected, EnterpriseActivity actual) {
		assertEqualsNulls( "EnterpriseActivity", expected, actual);
		if (expected != null) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Principal",expected.isPrincipal(), actual.isPrincipal());
			assertEquals("Iae",expected.getIae().getId(), actual.getIae().getId());
			assertEquals("Epigraph",expected.getEpigraph(), actual.getEpigraph());
			assertEquals("Cnae",expected.getCnae(), actual.getCnae());
			assertEquals("CnaeCode",expected.getCnaeCode(), actual.getCnaeCode());
			assertEquals("CnaeDescription",expected.getCnaeDescription(), actual.getCnaeDescription());
		}
	}

	private static void assertEqualsInvestAssetId(InvestAsset expected, InvestAsset actual) {
		assertEqualsNulls( "InvestAsset", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsInvestAsset(InvestAsset expected, InvestAsset actual) {
		assertEqualsNulls( "InvestAsset", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEqualsEnterpriseActivity( expected.getActivity(), actual.getActivity());
			assertEquals("Type",expected.getType(), actual.getType());
			assertEquals("Regime",expected.getRegime(), actual.getRegime());
			assertEquals("StartDate",expected.getStartDate(), actual.getStartDate());
			assertEquals("EndDate",expected.getEndDate(), actual.getEndDate());
			assertEqualsDouble("VatPercent",expected.getVatPercent(), actual.getVatPercent());
			assertEqualsDouble("RetentionPercent",expected.getRetentionPercent(), actual.getRetentionPercent());
			assertEquals("Properties",expected.getProperties(), actual.getProperties());
		}
	}

	private static void assertEqualsInvoiceMinId(InvoiceMin expected, InvoiceMin actual) {
		assertEqualsNulls( "InvoiceMin", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}
	public static void assertEqualsInvoiceMin(InvoiceMin expected, InvoiceMin actual) {
		assertEqualsNulls( "Invoice", expected, actual);
		if (expected != null) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Activity",expected.getActivity(), actual.getActivity());
			assertEquals("ActivityEpigraph",expected.getActivityEpigraph(), actual.getActivityEpigraph());
			assertEquals("ActivityName",expected.getActivityName(), actual.getActivityName());
			assertEquals("Type",expected.getType(), actual.getType());
			assertEquals("Series",expected.getSeries(), actual.getSeries());
			assertEquals("Number",expected.getNumber(), actual.getNumber());
			assertEquals("ReferenceCode",expected.getReferenceCode(), actual.getReferenceCode());
			assertEqualsDate("IssueDate",expected.getIssueDate(), actual.getIssueDate());
			assertEqualsDate("TaxDate",expected.getTaxDate(), actual.getTaxDate());
			assertEquals("Transaction",expected.getTransaction(), actual.getTransaction());
			assertEquals("Registry",expected.getRegistry(), actual.getRegistry());
			assertEquals("RegistryDocument",expected.getRegistryDocument(), actual.getRegistryDocument());
			assertEquals("RegistryDocumentType",expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
			assertEquals("RegistryDocumentCountry",expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
			assertEquals("RegistryName",expected.getRegistryName(), actual.getRegistryName());
			assertEquals("SecurityLevel",expected.getSecurityLevel(), actual.getSecurityLevel());
			assertEquals(expected.getScope(), actual.getScope());
			assertEquals("Recorded",expected.isRecorded(), actual.isRecorded());
			assertEqualsDouble("Total",expected.getTotal(), actual.getTotal());
			assertEquals("RectificationType",expected.getRectificationType(), actual.getRectificationType());
			assertEquals("RectificationInvoiceId",expected.getRectificationInvoiceId(), actual.getRectificationInvoiceId());
		}
	}

	private static void assertEqualsAccountId(Account expected, Account actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsAccount (Account expected, Account actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Code",expected.getCode(), actual.getCode());
			assertEquals("Description",expected.getDescription(), actual.getDescription());
			assertEquals("Alias",expected.getAlias(), actual.getAlias());
			assertEquals("EntryEnabled",expected.isEntryEnabled(), actual.isEntryEnabled());
			assertEquals("Level",expected.getLevel(), actual.getLevel());
			assertEquals("Active",expected.isActive(), actual.isActive());
			assertEquals("CostCenter",expected.getCostCenter(), actual.getCostCenter());
		}
	}

	private static void assertEqualsRegistryAddressId(RegistryAddress expected, RegistryAddress actual) {
		assertEqualsNulls( "RegistryAddress", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsRegistryAddress(RegistryAddress expected, RegistryAddress actual) {
		assertEqualsNulls( "RegistryAddress", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Registry",expected.getRegistry(), actual.getRegistry());
			assertEquals("Main",expected.isMain(), actual.isMain());
			assertEquals("Recipient",expected.getRecipient(), actual.getRecipient());
			assertEquals("StreetType",expected.getStreetType(), actual.getStreetType());
			assertEquals("Address",expected.getAddress(), actual.getAddress());
			assertEquals("Number",expected.getNumber(), actual.getNumber());
			assertEquals("Address2",expected.getAddress2(), actual.getAddress2());
			assertEquals("Address3",expected.getAddress3(), actual.getAddress3());
			assertEquals("Zip",expected.getZip(), actual.getZip());
			assertEquals("City",expected.getCity(), actual.getCity());
			assertEquals("Geozone",expected.getGeozone(), actual.getGeozone());
			assertEquals("GeozoneCode",expected.getGeozoneCode(), actual.getGeozoneCode());
			assertEquals("GeozoneName",expected.getGeozoneName(), actual.getGeozoneName());
			assertEquals("Alias",expected.getAlias(), actual.getAlias());
			assertEquals("MunicipalityCode",expected.getMunicipalityCode(), actual.getMunicipalityCode());
		}
	}
	
	private static void assertEqualsRegistry (Registry expected, Registry actual) {
		assertEqualsNulls( "Registry", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
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

	private static void assertEqualsSellerId(Seller expected, Seller actual) {
		assertEqualsNulls( "Seller", expected, actual);
		if (expected != null ) {
			assertEquals("Id", expected.getId(), actual.getId());
		}
	}
	private static void assertEqualsSeller(Seller expected, Seller actual) {
		assertEqualsNulls( "Seller", expected, actual);
		if (expected != null) {
			assertEqualsRegistry(expected, actual);
			assertEquals("CommissionType",expected.getCommissionType().getId(), actual.getCommissionType().getId());
			assertEquals("Scope",expected.getScope().getId(), actual.getScope().getId());
			assertEquals("Status",expected.getStatus(), actual.getStatus());
		}
	}
	
	private static void assertEqualsBrand(Brand expected, Brand actual) {
		assertEqualsNulls( "Brand", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Name",expected.getName(), actual.getName());
		}
	}
	
	private static void assertEqualsProductCategory(ProductCategory expected, ProductCategory actual) {
		assertEqualsNulls( "ProductCategory", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Name",expected.getName(), actual.getName());
			assertEquals("Detail",expected.getDetail(), actual.getDetail());
			assertEquals("Detail2",expected.getDetail2(), actual.getDetail2());
			assertEquals("Detail3",expected.getDetail3(), actual.getDetail3());
		}
	}
	
	private static void assertEqualsProduct(Product expected, Product actual) {
		assertEqualsNulls( "Product", expected, actual);
		if (expected != null ) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEqualsDomainId(expected.getDomain(), actual.getDomain());
			assertEquals("Code",expected.getCode(), actual.getCode());
			assertEquals("Name",expected.getName(), actual.getName());
			assertEqualsBrand(expected.getBrand(), actual.getBrand());
			assertEqualsProductCategory(expected.getCategory(), actual.getCategory());
			assertEquals("Status",expected.getStatus().value(), actual.getStatus().value());
			assertEquals("Type",expected.getType().value(), actual.getType().value());
			assertEquals("Kind",expected.getKind().value(), actual.getKind().value());
			assertEquals("Vat",expected.getVat().getId(), actual.getVat().getId());
			assertEquals("Retention",expected.getRetention().getId(), actual.getRetention().getId());
			assertEquals("Inventoriable",expected.isInventoriable(), actual.isInventoriable());
			assertEquals("Serializable",expected.isSerializable(), actual.isSerializable());
			assertEquals("Lotable",expected.isLotable(), actual.isLotable());
			assertEquals("Manufactured",expected.isManufactured(), actual.isManufactured());
			assertEquals("Composition",expected.isComposition(), actual.isComposition());
			assertEquals("CompositionPrice",expected.isCompositionPrice(), actual.isCompositionPrice());
			assertEquals("packaged",expected.isPackaged(), actual.isPackaged());
			assertEqualsAccount(expected.getSalesAccount(), actual.getSalesAccount());
			assertEqualsAccount(expected.getPurchaseAccount(), actual.getPurchaseAccount());		
			assertEquals("Active",expected.isActive(), actual.isActive());
		}
	}

	private static void assertEqualsTag(Tag expected, Tag actual) {
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Type",expected.getType(), actual.getType());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Color",expected.getColor(), actual.getColor());
		assertEquals("StartDate",expected.getStartDate(), actual.getStartDate());
		assertEquals("EndDate",expected.getEndDate(), actual.getEndDate());
	}
	
	private static void assertEqualsItem(Item expected, Item actual) {
		assertEqualsNulls( "Item", expected, actual);
		if (expected != null) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEqualsDomainId(expected.getDomain(), actual.getDomain());
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
			assertEqualsDouble("Price",expected.getPrice(), actual.getPrice());
			assertEqualsDouble("ExpensesPercent",expected.getExpensesPercent(), actual.getExpensesPercent());
			assertEqualsDouble("ExpensesFixed",expected.getExpensesFixed(), actual.getExpensesFixed());
			assertEqualsDouble("ProfitPercent",expected.getProfitPercent(), actual.getProfitPercent());
			assertEqualsDouble("PurchasePrice",expected.getPurchasePrice(), actual.getPurchasePrice());
			assertEquals("Internet",expected.isInternet(), actual.isInternet());
			assertEqualsTag(expected.getPackFormatTag(), actual.getPackFormatTag());
			assertEquals("PackUnits",expected.getPackUnits(), actual.getPackUnits());
			assertEqualsTag(expected.getPackUnitsTag(), actual.getPackUnitsTag());
			assertEqualsDouble("PackMeasurement", expected.getPackMeasurement(), actual.getPackMeasurement());
			assertEqualsTag(expected.getPackMeasurementTag(), actual.getPackMeasurementTag());
			assertEqualsTag(expected.getStockUnitTag(), actual.getStockUnitTag());
		}
	}

	
	public static void assertEqualsInvoice(Invoice expected, Invoice actual) {
		assertEqualsInvoice(expected, actual, false);
	}
	
	public static void assertEqualsInvoice(Invoice expected, Invoice actual, boolean onlyIds) {
		assertEqualsNulls( "FullInvoice", expected, actual);
		if (expected != null) {
			assertEquals("Id",expected.getId(), actual.getId());
			assertEquals("Domain",expected.getDomain(), actual.getDomain());
			assertEquals("Project",expected.getProject(), actual.getProject());
			assertEquals("Type",expected.getType(), actual.getType());
			assertEquals("Series",expected.getSeries(), actual.getSeries());
			assertEquals("Number",expected.getNumber(), actual.getNumber());
			assertEquals("ReferenceCode",expected.getReferenceCode(), actual.getReferenceCode());
			assertEqualsDate("IssueDate",expected.getIssueDate(), actual.getIssueDate());
			assertEqualsDate("TaxDate",expected.getTaxDate(), actual.getTaxDate());
			assertEquals("RectificationType", expected.getRectificationType(), actual.getRectificationType());
			assertEquals("SecurityLevel",expected.getSecurityLevel(), actual.getSecurityLevel());
			assertEquals("Registry",expected.getRegistry(), actual.getRegistry());
			assertEquals("RegistryDocument",expected.getRegistryDocument(), actual.getRegistryDocument());
			assertEquals("RegistryDocumentType",expected.getRegistryDocumentType(), actual.getRegistryDocumentType());
			assertEquals("RegistryDocumentCountry",expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry());
			assertEquals("RegistryName",expected.getRegistryName(), actual.getRegistryName());
			assertEquals("Transaction", expected.getTransaction(), actual.getTransaction());
			assertEquals("Recorded",expected.isRecorded(), actual.isRecorded());
			assertEquals("Surcharge",expected.isSurcharge(), actual.isSurcharge());
			assertEquals("Withholding",expected.isWithholding(), actual.isWithholding());
			assertEquals("WithholdingFarmer",expected.isWithholdingFarmer(), actual.isWithholdingFarmer());
			assertEquals("VatAccrualPayment",expected.isVatAccrualPayment(), actual.isVatAccrualPayment());
			assertEquals("Investment",expected.isInvestment(), actual.isInvestment());
			assertEquals("Service",expected.isService(), actual.isService());
			assertEquals("Signed",expected.isSigned(), actual.isSigned());
			assertEquals("Annulled",expected.isAnnulled(), actual.isAnnulled());
			assertEqualsDouble("TaxableBase",expected.getTaxableBase(), actual.getTaxableBase());
			assertEqualsDouble("VatQuota",expected.getVatQuota(), actual.getVatQuota());
			assertEqualsDouble("RetentionQuota",expected.getRetentionQuota(), actual.getRetentionQuota());
			assertEqualsDouble("Total",expected.getTotal(), actual.getTotal());
			assertEquals("Comments",expected.getComments(), actual.getComments());
			assertEquals("Remarks",expected.getRemarks(), actual.getRemarks());
			
			assertEquals("CreationUser",expected.getCreationUser(), actual.getCreationUser());
			assertEquals("CreationDate",expected.getCreationDate(), actual.getCreationDate());			
			assertEquals("ModificationUser",expected.getModificationUser(), actual.getModificationUser());
			assertEquals("ModificationDate",expected.getModificationDate(), actual.getModificationDate());			

			if (onlyIds) {
				assertEqualsEnterpriseActivityId(expected.getActivity().orElse(null), actual.getActivity().orElse(null));
				assertEqualsInvestAssetId(expected.getInvestAsset().orElse(null), actual.getInvestAsset().orElse(null));
				assertEqualsInvoiceMinId(expected.getRectificationInvoice().orElse(null), actual.getRectificationInvoice().orElse(null));
				assertEqualsAccountId(expected.getRegistryAccount().orElse(null), actual.getRegistryAccount().orElse(null));
				assertEqualsRegistryAddressId(expected.getRegistryAddress().orElse(null), actual.getRegistryAddress().orElse(null));
				assertEqualsScopeId(expected.getScope(), actual.getScope() );
				assertEqualsSellerId(expected.getSeller().orElse(null), actual.getSeller().orElse(null) );
			} else {
				assertEqualsEnterpriseActivity(expected.getActivity().orElse(null), actual.getActivity().orElse(null));
				assertEqualsInvestAsset(expected.getInvestAsset().orElse(null), actual.getInvestAsset().orElse(null));
				assertEqualsInvoiceMin(expected.getRectificationInvoice().orElse(null), actual.getRectificationInvoice().orElse(null));
				assertEqualsAccount(expected.getRegistryAccount().orElse(null), actual.getRegistryAccount().orElse(null));
				assertEqualsRegistryAddress(expected.getRegistryAddress().orElse(null), actual.getRegistryAddress().orElse(null));
				assertEqualsScope(expected.getScope(), actual.getScope() );
				assertEqualsSeller(expected.getSeller().orElse(null), actual.getSeller().orElse(null) );
			}
			assertEqualsInvoiceDetailStream(expected.getDetails(), actual.getDetails());
			/*
	private LinkedList<InvoiceDetail> details;
	private TaxBreakdown taxBreakdown;
	
	private List<Finance> finances;
	private InvoiceFiscal fiscal;
	private InvoiceInfo invoiceInfo;
	private Attach attach;
	
	private Integer rawdocId;
			 */
			
//			// assertEqualsRegistryAddress (expected.getAddress(), actual.getAddress());
//			assertEqualsFinances(expected.getFinances(), actual.getFinances());
//			assertEqualsInvoiceFiscal(expected.getFiscal(), actual.getFiscal());
//			assertEquals(expected.getTediCategory(), actual.getTediCategory(),"TediCategory");
//			assertEquals(expected.getFileUrl(), actual.getFileUrl(),"FileUrl");
//			assertEqualsInvoiceInfo(expected.getInvoiceInfo(), actual.getInvoiceInfo());
//			assertEqualsInvoiceErrors(expected.getMessages(), actual.getMessages());
//			assertEquals(expected.isRecordable(), actual.isRecordable(),"Recordable");
//			assertEquals(expected.isSelected(), actual.isSelected(),"Selected");
//			
//			assertEqualsAttach(expected.getAttach().orElse(null), actual.getAttach().orElse(null));
//			
//			//assertEqualsInvoiceBreakdowns(expected.getBreakdown(), actual.getBreakdown());
//			//assertEqualsTaxBreakdown(expected.getTaxBreakdown().orElse(null), actual.getTaxBreakdown().orElse(null));
//			//assertEqualsRegistry(expected.getRegistryData(), actual.getRegistryData());
//			
//			assertEqualsInvoiceFiscal(expected.getFiscal(), actual.getFiscal());
			
		}
	}
	
	private static void assertEqualsDiscountExpression(DiscountExpression expected, DiscountExpression actual) {
		assertEqualsNulls( "DiscountExpression", expected, actual);
		assertEquals("DiscountExpr",expected.getDiscountExpr(), actual.getDiscountExpr());
		assertArrayEquals( expected.getDiscounts(), actual.getDiscounts(), DELTA);
	}
	
	private static void assertEqualsInvoiceDetailStream(Stream<InvoiceDetail> expected, Stream<InvoiceDetail> actual) {
		assertEqualsInvoiceDetails(
			expected.collect(Collectors.toCollection(LinkedList::new))
			,actual.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static void assertEqualsInvoiceDetails(List<InvoiceDetail> expected, List<InvoiceDetail> actual) {
		assertEqualsCollection("InvoiceDetails", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsInvoiceDetail(expected.get(i),actual.get(i)));
	}
	private static void assertEqualsInvoiceDetail(InvoiceDetail expected, InvoiceDetail actual) {
		assertEqualsNulls( "InvoiceDetail", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Invoice",expected.getInvoice(), actual.getInvoice());	
		assertEqualsInvestAsset(expected.getInvestAsset().orElse(null), actual.getInvestAsset().orElse(null));
		assertEquals("Project",expected.getProject(), actual.getProject());
		assertEquals("Line",expected.getLine(), actual.getLine());
		assertEqualsItem(expected.getItem().orElse(null), actual.getItem().orElse(null));
		assertEquals("Description",expected.getDescription(), actual.getDescription());
		assertEqualsDouble("Quantity",expected.getQuantity(), actual.getQuantity());
		assertEqualsDouble("Price",expected.getPrice(), actual.getPrice());
		assertEqualsDiscountExpression(expected.getDiscountExpression().orElse(null), actual.getDiscountExpression().orElse(null));
		assertEqualsDouble("TaxableBase",expected.getTaxableBase(), actual.getTaxableBase());
		assertEqualsDouble("Taxes",expected.getTaxes(), actual.getTaxes());
		assertEquals("Prepayment",expected.isPrepayment(), actual.isPrepayment());
		
		assertEqualsSeller(expected.getSeller().orElse(null), actual.getSeller().orElse(null));
		assertEqualsWorkplace(expected.getWorkplace(), actual.getWorkplace());
		assertEqualsWarehouse(expected.getWarehouse().orElse(null), actual.getWarehouse().orElse(null));
		assertEquals("Source",expected.getSource(), actual.getSource());
		assertEquals("sourceId",expected.getSourceId(), actual.getSourceId());
		assertEqualsAccount(expected.getExpAccount().orElse(null), actual.getExpAccount().orElse(null));
		assertEqualsInvoiceTaxStream( expected.getInvoiceTaxes(), actual.getInvoiceTaxes() );
	}
	
	private static void assertEqualsInvoiceTaxStream(Stream<InvoiceTax> expected, Stream<InvoiceTax> actual) {
		assertEqualsInvoiceTaxes(
			expected.collect(Collectors.toCollection(LinkedList::new))
			,actual.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static void assertEqualsInvoiceTaxes(List<InvoiceTax> expected, List<InvoiceTax> actual) {
		assertEqualsCollection("InvoiceTaxes", expected, actual);
		IntStream.range(0, expected.size())
	    	.forEach( i -> assertEqualsInvoiceTax(expected.get(i),actual.get(i)));
	}
	private static void assertEqualsInvoiceTax(InvoiceTax expected, InvoiceTax actual) {
		assertEqualsNulls( "InvoiceTax", expected, actual);
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("InvoiceDetail",expected.getInvoiceDetail(), actual.getInvoiceDetail());	
		assertEquals("TaxType",expected.getTaxType(), actual.getTaxType());
		assertEqualsDouble("Base",expected.getBase(), actual.getBase());
		assertEqualsDouble("Percentage",expected.getPercentage(), actual.getPercentage());
		assertEqualsDouble("Quota",expected.getQuota(), actual.getQuota());
		assertEqualsDouble("Surcharge",expected.getSurcharge(), actual.getSurcharge());
		assertEqualsDouble("SurchargeQuota",expected.getSurchargeQuota(), actual.getSurchargeQuota());
		assertEqualsDouble("DeductibleQuota",expected.getDeductibleQuota(), actual.getDeductibleQuota());
		assertEquals("WithholdingType",expected.getWithholdingType(), actual.getWithholdingType()); 
		assertEquals("VatDeductionType",expected.getVatDeductionType(), actual.getVatDeductionType());
		assertEqualsDouble("DeductiblePercent",expected.getDeductiblePercent(), actual.getDeductiblePercent());
		assertEqualsDouble("DirectTaxPercent",expected.getDirectTaxPercent(), actual.getDirectTaxPercent());
		assertEqualsAccount(expected.getOutputAccount().orElse(null), actual.getOutputAccount().orElse(null));
		assertEqualsAccount(expected.getInputAccount().orElse(null), actual.getInputAccount().orElse(null));
		assertEqualsAccount(expected.getAdjAccount().orElse(null), actual.getAdjAccount().orElse(null));
		assertEqualsAccount(expected.getAdjDirectTaxAccount().orElse(null), actual.getAdjDirectTaxAccount().orElse(null));
		assertEqualsAccount(expected.getWithholdingAccount().orElse(null), actual.getWithholdingAccount().orElse(null));
	}
	
	
	//// ---------------------------------------------------------------------
	//// ---------------------------------------------------------------------
	//// ---------------------------------------------------------------------
	//// ---------------------------------------------------------------------
	
//	
//	private static void assertNotEqualsDouble(String msg,double expected,double actual) {
//		assertNotEquals(expected, actual, DELTA, msg);		
//	}
//	
//	private static void assertEqualsArray(String msg,Object[] expected, Object[] actual) {
//		if ( (expected == null || expected.length == 0) 
//				&& ( (actual != null && actual.length != 0))) 
//				fail( msg + " actual List is not Empty");
//			if ( (expected != null && expected.length != 0) 
//				&& (actual == null || actual.length == 0))  
//				fail( msg + " actual List is Empty");
//			if ( expected != null && actual != null) {
//				assertEquals(expected.length, actual.length, " sizes not fit");	
//			}
//	}
//	
//	private static void assertNullCollection(String msg,Collection<?> actual) {
//		assertNull(actual, msg);		
//	}
//	private static void assertEmptyCollection(String msg,Collection<?> actual) {
//		assertNotNull(actual, msg);		
//		assertTrue(actual.isEmpty(), msg);
//	}
//	private static void assertNotEmptyCollection(String msg,Collection<?> actual) {
//		assertNotNull(actual, msg);		
//		assertFalse(actual.isEmpty(), msg);
//	}
//	
//	
//	private static void assertEqualsMap(String msg,Map<?,?> expected, Map<?,?> actual) {
//		if ( (expected == null || expected.isEmpty()) 
//			&& ( (actual != null && !actual.isEmpty()))) 
//			fail( msg + " actual List is not Empty");
//		if ( (expected != null && !expected.isEmpty()) 
//			&& (actual == null || actual.isEmpty()))  
//			fail( msg + " actual List is Empty");
//		if ( expected != null && actual != null) {
//			assertEquals(expected.size(), actual.size(), " sizes not fit");	
//		}
//	}
//
//	private static void assertEqualsAccountPeriod(AccountPeriod expected, AccountPeriod actual) {
//		assertEqualsNulls( "Account", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getId(), actual.getId(), "Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getName(), actual.getName(),"Name");
//			assertEquals(expected.getInitiationDate(), actual.getInitiationDate(),"InitiationDate");
//			assertEquals(expected.getDeadline(), actual.getDeadline(),"Deadline");
//			assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		}
//	}
//
//
//	private static void assertEqualsAccountingReportParams(AccountingReportParams expected, AccountingReportParams actual) {
//		assertEqualsNulls( "Account", expected, actual);
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getDomainName(), actual.getDomainName(),"DomainName");
//		assertEquals(expected.getUser(), actual.getUser(),"User");
//		assertEquals(expected.getPeriod(), actual.getPeriod(),"Period");
//		assertEquals(expected.getFromDate(), actual.getFromDate(),"FromDate");
//		assertEquals(expected.getToDate(), actual.getToDate(),"ToDate");
//		assertEqualsAccount(expected.getAccount(), actual.getAccount());
//		assertEquals(expected.getLevel(), actual.getLevel(),"Level");
//		assertEquals(expected.getActivity(), actual.getActivity(),"Activity");
//		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"SecurityLevel");
//		assertEquals(expected.getDocumentNumber(), actual.getDocumentNumber(),"DocumentNumber");
//		assertEquals(expected.getPreviousPeriods(), actual.getPreviousPeriods(),"PreviousPeriods"); 
//		assertEquals(expected.isLowLevelAccountVisible(), actual.isLowLevelAccountVisible(),"LowLevelAccountVisible");
//		assertEquals(expected.isNoActivityAccountVisible(), actual.isNoActivityAccountVisible(),"NoActivityAccountVisible");
//		assertEquals(expected.isNoBalanceAccountExcluded(), actual.isNoBalanceAccountExcluded(),"NoBalanceAccountExcluded");
//		assertEquals(expected.isPercentsEnabled(), actual.isPercentsEnabled(),"PercentsEnabled");
//		assertEquals(expected.isByMonth(), actual.isByMonth(),"ByMonth");
//		assertEquals(expected.areOpeningEntriesExcluded(), actual.areOpeningEntriesExcluded(),"OpeningEntriesExcluded"); 
//		assertEquals(expected.areOperatingEntriesExcluded(), actual.areOperatingEntriesExcluded(),"OperatingEntriesExcluded");
//		assertEquals(expected.areClosingEntriesExcluded(), actual.areClosingEntriesExcluded(),"ClosingEntriesExcluded");
//		assertEquals(expected.isReverseOrder(), actual.isReverseOrder(),"ReverseOrder");
//		assertEquals(expected.getBalanceType(), actual.getBalanceType(),"BalanceType");
//		assertEqualsAccountPeriod(expected.getSelectedPeriod(), actual.getSelectedPeriod());
//		assertEqualsEnterpriseActivity(expected.getSelectedActivity(), actual.getSelectedActivity());
//		assertEqualsAccount(expected.getSelectedAccount(), actual.getSelectedAccount());
//		assertEquals(expected.isBreakdownEnabled(), actual.isBreakdownEnabled(),"BreakdownEnabled");
//		assertEquals(expected.getLedgerAccount(), actual.getLedgerAccount(),"LedgerAccount");
//		assertEquals(expected.getLedgerDebitBalance(), actual.getLedgerDebitBalance(), DELTA,"LedgerDebitBalance");
//		assertEquals(expected.getLedgerUnpaidBalance(), actual.getLedgerUnpaidBalance(), DELTA,"LedgerUnpaidBalance");
//		assertEquals(expected.isConsolidation(), actual.isConsolidation(),"Consolidation");
//		assertEquals(expected.getRegistry(), actual.getRegistry(),"Registry");
//		assertEquals(expected.getOutput(), actual.getOutput(),"Output");
//		assertEquals(expected.getVatSummaryType(), actual.getVatSummaryType(),"VatSummaryType");
//		assertEquals(expected.getPercent(), actual.getPercent(),"Percent");
//		assertEquals(expected.getRectificationType(), actual.getRectificationType(),"RectificationType");
//		assertEquals(expected.getSurcharge(), actual.getSurcharge(),"Surcharge");
//		assertEquals(expected.getFarmerRegime(), actual.getFarmerRegime(),"FarmerRegime");
//		assertEquals(expected.getAccrualRegime(), actual.getAccrualRegime(),"AccrualRegime");
//		assertEquals(expected.getInvestment(), actual.getInvestment(),"Investment");
//		assertEquals(expected.getService(), actual.getService(),"Service");
//		assertEquals(expected.getTitle(), actual.getTitle(),"Title");
//		assertEquals(expected.getSubject(), actual.getSubject(),"Subject");
//		assertEquals(expected.isShowCover(), actual.isShowCover(),"ShowCover");
//		assertEquals(expected.getPageOffset(), actual.getPageOffset(),"PageOffset");
//		assertEquals(expected.getPageOffsetText(), actual.getPageOffsetText(),"PageOffsetText");
//		assertEquals(expected.isHideFilter(), actual.isHideFilter(),"HideFilter");
//		assertEquals(expected.getHeaderText(), actual.getHeaderText(),"HeaderText");
//		assertEquals(expected.isHideDateTimeOnFooter(), actual.isHideDateTimeOnFooter(),"HideDateTimeOnFooter");
//		assertEquals(expected.getFooterText(), actual.getFooterText(),"FooterText");
//		assertEqualsCollection( "Domains Size",expected.getDomains(), actual.getDomains());
//		if ( expected.getDomains() != null) {
//			for (int i = 0; i < expected.getDomains().size(); i++) {
//				assertEqualsDomain(expected.getDomains().get(i), actual.getDomains().get(i));	
//			}
//		}
//		assertEqualsCollection( "CostCenters Size",expected.getCostCenters(), actual.getCostCenters());
//		if ( expected.getCostCenters() != null) {
//			for (String key : expected.getCostCenters()) {
//				assertTrue( expected.getCostCenters().contains(key) ,"CostCenter");	
//			}
//		}
//		assertEqualsArray( "Invoices",expected.getInvoices(), actual.getInvoices());
//		if ( expected.getInvoices() != null) {
//			for (int i = 0; i < expected.getInvoices().length; i++) {
//				assertEquals(expected.getInvoices()[i], actual.getInvoices()[i],"Invoice -> " + i);	
//			}
//		}
//	}
//
//	private static void assertEqualsRegistryFull (RegistryFull<?> expected, RegistryFull<?> actual) {
//		assertEqualsNulls( "RegistryFull", expected, actual);
//		assertEqualsNulls( "RegistryFull Registry", expected.getRegistry(), actual.getRegistry());
//		assertEqualsCollection( "RegistryFull Addresses Size",expected.getAddresses(), actual.getAddresses());
//		assertEqualsCollection( "RegistryFull Medias Size",expected.getMedias(), actual.getMedias());
//		for (int i = 0; i < expected.getAddresses().size(); i++) {
//			assertEqualsRegistryAddress(expected.getAddresses().get(i), actual.getAddresses().get(i));	
//		}
//		assertEquals(expected.getMedias().size(), actual.getMedias().size(), "RegistryFull Medias Size");
//		for (int i = 0; i < expected.getMedias().size(); i++) {
//			assertEqualsRegistryMedia(expected.getMedias().get(i), actual.getMedias().get(i));	
//		}
//	}
//	
//	private static void assertEqualsCreditorFull (CreditorFull expected, CreditorFull actual) {
//		assertEqualsRegistryFull (expected, actual);
//		assertEqualsCreditor(expected.getRegistry(), actual.getRegistry());
//	}
//
//	private static void assertEqualsCustomerFull (CustomerFull expected, CustomerFull actual) {
//		assertEqualsRegistryFull (expected, actual);
//		assertEqualsCustomer(expected.getRegistry(), actual.getRegistry());
//	}
//
//	private static void assertEqualsSupplierFull (SupplierFull expected, SupplierFull actual) {
//		assertEqualsRegistryFull (expected, actual);
//		assertEqualsSupplier(expected.getRegistry(), actual.getRegistry());
//	}
//
//	private static void assertEqualsCustomer(Customer expected, Customer actual) {
//		assertEqualsNulls( "Customer", expected, actual);
//		assertEqualsRegistry(expected, actual);
//		assertEquals(expected.getTariff(), actual.getTariff(),"Tariff");
//		assertEquals(expected.isSurcharge(),actual.isSurcharge(),"Surcharge");
//		assertEquals(expected.isWithholding(),actual.isWithholding(),"Withholding");
//		assertEquals(expected.getTransaction(),actual.getTransaction(),"Transaction");
//		assertEquals(expected.getStatus(),actual.getStatus(),"Status");
//		assertEquals(expected.getScope().getId(),actual.getScope().getId(),"Scope");
//		assertEquals(expected.isEInvoice(),actual.isEInvoice(),"EInvoice");
//		assertEquals(expected.getInvoicingGroup(),actual.getInvoicingGroup(),"InvoicingGroup");
//		assertEquals(expected.isProjectGrouped(),actual.isProjectGrouped(),"ProjectGrouped");
//		assertEquals(expected.isDeliveryGrouped(),actual.isDeliveryGrouped(),"DeliveryGrouped");
//		assertEquals(expected.isDeliveryValuated(),actual.isDeliveryValuated(),"DeliveryValuated");
//		assertEquals(expected.getAccount(),actual.getAccount(),"Account");
//	}
//
//	private static void assertEqualsCreditor(Creditor expected, Creditor actual) {
//		assertEqualsNulls( "Creditor", expected, actual);
//		assertEqualsRegistry(expected, actual);
//		assertEquals(expected.isWithholding(),actual.isWithholding(),"Withholding");
//		assertEquals(expected.isVatAccrualPayment(),actual.isVatAccrualPayment(),"VatAccrualPayment");
//		assertEquals(expected.getTransaction(),actual.getTransaction(),"Transaction");
//		assertEquals(expected.getStatus(),actual.getStatus(),"Status");
//		assertEquals(expected.getScope().getId(),actual.getScope().getId(),"Scope");
//		assertEquals(expected.getAccount(),actual.getAccount(),"Account");
//	}
//
//	private static void assertEqualsSupplier(Supplier expected, Supplier actual) {
//		assertEqualsNulls( "Supplier", expected, actual);
//		assertEqualsRegistry(expected, actual);
//		assertEquals(expected.getTariff(), actual.getTariff(),"Tariff");
//		assertEquals(expected.isWithholding(),actual.isWithholding(),"Withholding");
//		assertEquals(expected.isWithholdingFarmer(),actual.isWithholdingFarmer(),"WithholdingFarmer");
//		assertEquals(expected.isVatAccrualPayment(),actual.isVatAccrualPayment(),"VatAccrualPayment");
//		assertEquals(expected.getTransaction(),actual.getTransaction(),"Transaction");
//		assertEquals(expected.getStatus(),actual.getStatus(),"Status");
//		assertEquals(expected.getScope().getId(),actual.getScope().getId(),"Scope");
//		assertEquals(expected.isPurchaseValuated(),actual.isPurchaseValuated(),"PurchaseValuated");
//		assertEquals(expected.getAccount(),actual.getAccount(),"Account");
//	}
//	
//	private static void assertEqualsTaskHolder(TaskHolder expected, TaskHolder actual) {
//		assertEqualsNulls( "TaskHolder", expected, actual);
//		assertEqualsRegistry(expected, actual);
//		assertEquals(expected.getType(), actual.getType(),"Type");
//		assertEquals(expected.isActive(),actual.isActive(),"Active");
//		assertEquals(expected.getUserId(),actual.getUserId(),"UserId");
//		assertEquals(expected.getCostProfile(),actual.getCostProfile(),"CostProfile");
//	}
//
//	private static void assertEqualsRegistryMedia (RegistryMedia expected, RegistryMedia actual) {
//		assertEqualsNulls( "RegistryMedia", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getRegistry(), actual.getRegistry(),"Registry");
//		assertEquals(expected.getMedia(), actual.getMedia(),"Media");
//		assertEquals(expected.getValue(), actual.getValue(),"Value");
//		assertEquals(expected.getComment(), actual.getComment(),"Comment");
//		assertEquals(expected.getRaddress(), actual.getRaddress(),"Raddress");
//		assertEquals(expected.isAdministrative(), actual.isAdministrative(),"Administrative");
//		assertEquals(expected.isCommercial(), actual.isCommercial(),"Commercial");
//		assertEquals(expected.isTechnical(), actual.isTechnical(),"Technical");
//	}
//
//	private static void assertEqualsTariff(Tariff expected, Tariff actual) {
//		assertEqualsNulls( "Tariff", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getCode(), actual.getCode(),"Code");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.isPurchase(), actual.isPurchase(),"Purchase");
//		assertEquals(expected.getDiscount(), actual.getDiscount(), DELTA,"Discount");
//		assertEquals(expected.isActive(), actual.isActive(),"Active");
//	}
//	
//	private static void assertEqualsWorkgroup(Workgroup expected, Workgroup actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//		assertEquals(expected.getStatus(), actual.getStatus(),"status");
//	}
//	
//	private static void assertEqualsProjectType(ProjectType expected, ProjectType actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//		assertEquals(expected.isActive(), actual.isActive(),"Active");
//	}
//	
//	private static void assertEqualsProject(Project expected, Project actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain().getId(), actual.getDomain().getId(),"Domain");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getAlias(), actual.getAlias(),"Alias");
//		assertEquals(AonDateUtils.getDateWithoutTime(expected.getDate()),
//				AonDateUtils.getDateWithoutTime(actual.getDate()),"Date");
//		assertEquals(expected.getType().getId(), actual.getType().getId(),"Type");
//		assertEquals(expected.isTas(), actual.isTas(),"Tas");
//		assertEquals(expected.isCommercial(), actual.isCommercial(),"Commercial");
//		assertEquals(expected.isReservation(), actual.isReservation(),"Reservation");
//		assertEquals(expected.isActive(), actual.isActive(),"Active");
//	}
//	
//	private static void assertEqualsProjectHolder(ProjectHolder expected, ProjectHolder actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getProject(), actual.getProject(),"Project");
//		assertEquals(AonDateUtils.getDateWithoutTime(expected.getStartDate()), AonDateUtils.getDateWithoutTime(actual.getStartDate()),"Start Date");
//		assertEquals(AonDateUtils.getHour(expected.getStartDate()), AonDateUtils.getHour(actual.getStartDate()),"Start Date");
//		assertEquals(AonDateUtils.getDateWithoutTime(expected.getEndDate()),  AonDateUtils.getDateWithoutTime(actual.getEndDate()),"End Date");
//		assertEquals(AonDateUtils.getHour(expected.getEndDate()),  AonDateUtils.getHour(actual.getEndDate()),"End Date");
//		assertEquals(expected.getWorkgroup().getId(), actual.getWorkgroup().getId(),"Workgroup");
//		assertEquals(expected.getTaskHolder().getId(), actual.getTaskHolder().getId(),"Task Holder");
//	}
//	
//	
//	
//	private static void assertEqualsPayMethod(PayMethod expected, PayMethod actual) {
//		assertEqualsNulls( "PayMethod", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getType(), actual.getType(),"Type");
//	}
//	private static void assertEqualsAccountTrialBalance(AccountTrialBalance expected, AccountTrialBalance actual) {
//		assertEqualsNulls( "AccountTrialBalance", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getCode(), actual.getCode(),"Code");
//		assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//		assertEquals(expected.getBeforePeriodDebit(), actual.getBeforePeriodDebit(), DELTA,"BeforePeriodDebit");
//		assertEquals(expected.getBeforePeriodCredit(), actual.getBeforePeriodCredit(), DELTA,"BeforePeriodCredit");
//		assertEquals(expected.getInPeriodOpeningDebit(), actual.getInPeriodOpeningDebit(), DELTA,"InPeriodOpeningDebit");
//		assertEquals(expected.getInPeriodOpeningCredit(), actual.getInPeriodOpeningCredit (), DELTA,"InPeriodOpeningCredit");
//		assertEquals(expected.getInPeriodBeforeDebit(), actual.getInPeriodBeforeDebit(), DELTA,"InPeriodBeforeDebit");
//		assertEquals(expected.getInPeriodBeforeCredit(), actual.getInPeriodBeforeCredit(), DELTA,"InPeriodBeforeCredit");
//		assertEquals(expected.getInPeriodDebit(), actual.getInPeriodDebit(), DELTA,"InPeriodDebit");
//		assertEquals(expected.getInPeriodCredit(), actual.getInPeriodCredit(), DELTA,"InPeriodCredit");
//	}
//	private static void assertEqualsAccountTrialBalanceReport (AccountTrialBalanceReport expected, AccountTrialBalanceReport actual) {
//		assertEqualsNulls( "AccountTrialBalanceReport", expected, actual);
//		if (expected != null) {
//			assertEqualsAccountingReportParams(expected.getParams(), actual.getParams());
//			assertEquals(expected.hasBeforePeriodAmounts(), actual.hasBeforePeriodAmounts(),"hasBeforePeriodAmounts");	
//			assertEquals(expected.hasOpeningAmounts(), actual.hasOpeningAmounts(),"hasOpeningAmounts");	
//			assertEquals(expected.hasInPeriodPreviousAmounts(), actual.hasInPeriodPreviousAmounts(),"hasOpeningAmounts");
//			assertEqualsAccountTrialBalance(expected.getTotalBalance(), actual.getTotalBalance());
//			assertEqualsMap("Balances", expected.getBalances(), actual.getBalances());
//			if ( expected.getBalances() != null) {
//				for ( String key : expected.getBalances().keySet()) {
//					assertEqualsAccountTrialBalance(expected.getBalances().get(key),actual.getBalances().get(key));
//				}
//			}
//		}
//	}
//	
//	private static void assertEqualsAccountOperatingAccount (AccountOperatingAccount expected, AccountOperatingAccount actual) {
//		assertEqualsNulls("AccountOperatingAccount", expected, actual);
//		if (expected != null) {
//			assertEquals(expected.getCode(), actual.getCode(),"Code");
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getType(), actual.getType(),"Type");
//		}
//	}
//	private static void assertEqualsAccountOperatingStatement (AccountOperatingStatement expected, AccountOperatingStatement actual) {
//		assertEqualsNulls("AccountOperatingStatement", expected, actual);
//		if (expected != null) {
//			assertEqualsAccountOperatingAccount(expected.getAccount(), actual.getAccount());
//			assertEquals(expected.getCredit(), actual.getCredit(), DELTA,"Credit");
//			assertEquals(expected.getDebit(), actual.getDebit(), DELTA,"Debit");
//			assertEquals(expected.getDebitBalance(), actual.getDebitBalance(), DELTA,"DebitBalance");
//			assertEquals(expected.getExpensesRatio(), actual.getExpensesRatio(), DELTA,"ExpensesRatio");
//			assertEquals(expected.getIncreasePercent(), actual.getIncreasePercent(), DELTA,"IncreasePercent");
//			assertEquals(expected.getMonth(), actual.getMonth(),"Month");
//			assertEquals(expected.getPurchasesRatio(), actual.getPurchasesRatio(), DELTA,"PurchasesRatio");
//			assertEquals(expected.getSalesRatio(), actual.getSalesRatio(), DELTA,"SalesRatio");
//			assertEquals(expected.getUnpaidBalance(), actual.getUnpaidBalance(), DELTA,"UnpaidBalance");
//		}
//	}
//	private static void assertEqualsAccountOperatingReport (AccountOperatingReport expected, AccountOperatingReport actual) {
//		assertEqualsNulls("AccountOperatingReport", expected, actual);
//		if (expected != null ) {
//			assertEqualsCollection("AccountOperatingAccounts", expected.getAccounts(), actual.getAccounts());
//			assertEqualsCollection("AccountOperatingIntervals", expected.getIntervals(), actual.getIntervals());
//			for (DateInterval expectedInterval : expected.getIntervals()) {
//				Date start = expectedInterval.getStart();
//				Date end = expectedInterval.getEnd();
//				String name = expectedInterval.getName();
//				
//				
//				DateInterval actualInterval = actual.getIntervals().stream()
//						.filter(inter ->{
//							SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//							if (name != null && !name.equals(inter.getName()))
//								return false;
//							if (start != null && !df.format(start).equals(df.format(inter.getStart()))) {
//									return false;
//							}
//							if (end != null && !df.format(end).equals(df.format(inter.getEnd()))) {
//									return false;
//							}
//							return true;
//						})
//						.findFirst().orElse(null);
//				for (AccountOperatingAccount account : expected.getAccounts()) {
//					assertEqualsAccountOperatingStatement(expected.get(account.getCode(), expectedInterval), actual.get(account.getCode(), actualInterval));
//				}
//			}
//		}
//	}
//	
//	private static void assertEqualsRDirStaff(RDirStaff expected, RDirStaff actual) {
//		assertEqualsNulls( "RDirStaff", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getRegistry(), actual.getRegistry(),"Registry");
//		assertEquals(expected.getDocument(), actual.getDocument(),"Document");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getShareHolder(), actual.getShareHolder(),"ShareHolder");
//		assertEquals(expected.getRepresentative(), actual.getRepresentative(),"Representative");
//		assertEquals(expected.getDirector(), actual.getDirector(),"Director");
//		assertEquals(expected.getRepresentativeLabor(), actual.getRepresentativeLabor(),"RepresentativeLabor");
//		assertEquals(expected.getDueDate(), actual.getDueDate(),"DueDate");
//		assertEquals(expected.getPercentShare(), actual.getPercentShare(), DELTA,"PercentShare");
//		assertEquals(expected.getNominalValue(), actual.getNominalValue(),"NominalValue");
//		assertEquals(expected.getShareNumber(), actual.getShareNumber(),"ShareNumber");
//		assertEquals(expected.getChargeDescription(), actual.getChargeDescription(),"ChargeDescription");
//	}
//	
//	private static void assertEqualsCompany(Company expected, Company actual) {
//		assertEqualsNulls( "Company", expected, actual);
//		assertEqualsRegistry(expected, actual);
//		assertEquals(expected.isActive(),actual.isActive(),"Active");
//		assertEquals(expected.isSurcharge(),actual.isSurcharge(),"Surcharge");
//		assertEquals(expected.isWithholding(),actual.isWithholding(),"Withholding");
//		assertEquals(expected.isVatAccrualPayment(),actual.isVatAccrualPayment(),"VatAccrualPayment");
//		assertEquals(expected.iseInvoice(),actual.iseInvoice(),"eInvoice");
//	}
//	
//
//	private static void assertEqualsInvoice(Invoice expected, Invoice actual) {
//		assertEqualsNulls( "Invoice", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEqualsActivityId(expected.getActivity().orElse(null), actual.getActivity().orElse(null));
//		assertEquals(expected.getInvestAsset(), actual.getInvestAsset(),"InvestAsset");
//		assertEquals(expected.getProject(), actual.getProject(),"Project");
//		assertEquals(expected.getSeries(), actual.getSeries(),"Series");
//		assertEquals(expected.getNumber(), actual.getNumber(),"Number");
//		assertEquals(expected.getReferenceCode(), actual.getReferenceCode(),"ReferenceCode");
//		assertEqualsDate("IssueDate",expected.getIssueDate(), actual.getIssueDate());
//		assertEqualsDate("TaxDate",expected.getTaxDate(), actual.getTaxDate());
//		assertEquals(expected.getRectificationType(), actual.getRectificationType(),"RectificationType");
//		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"SecurityLevel");
//		// assertEquals(expected.getRectificationInvoice(), actual.getRectificationInvoice(),"RectificationInvoice");
//		assertEqualsInvoiceMin(expected.getRectificationInvoice().orElse(null), actual.getRectificationInvoice().orElse(null));
//		assertEquals(expected.getRegistry(), actual.getRegistry(),"Registry");
//		assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(),"RegistryDocument");
//		assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(),"RegistryDocumentType");
//		assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(),"RegistryDocumentCountry");
//		assertEquals(expected.getRegistryName(), actual.getRegistryName(),"RegistryName");
//		assertEquals(expected.getRegistryAddress(), actual.getRegistryAddress(),"RegistryAddress");
//		assertEqualsScopeId(expected.getScope(), actual.getScope());
//		assertEquals(expected.getType(), actual.getType(),"Type");
//		assertEquals(expected.getTransaction(), actual.getTransaction(),"Transaction");
//		assertEquals(expected.isRecorded(), actual.isRecorded(),"Recorded");
//		assertEquals(expected.isSurcharge(), actual.isSurcharge(),"Surcharge");
//		assertEquals(expected.isWithholding(), actual.isWithholding(),"Withholding");
//		assertEquals(expected.isWithholdingFarmer(), actual.isWithholdingFarmer(),"WithholdingFarmer");
//		assertEquals(expected.isVatAccrualPayment(), actual.isVatAccrualPayment(),"VatAccrualPayment");
//		assertEquals(expected.isInvestment(), actual.isInvestment(),"Investment");
//		assertEquals(expected.isService(), actual.isService(),"Service");
//		assertEquals(expected.isAdvance(), actual.isAdvance(),"Advance");
//		assertEquals(expected.isSigned(), actual.isSigned(),"Signed");
//		assertEquals(expected.getTaxableBase(), actual.getTaxableBase(), DELTA,"TaxableBase");
//		assertEquals(expected.getVatQuota(), actual.getVatQuota(), DELTA,"VatQuota");
//		assertEquals(expected.getRetentionQuota(), actual.getRetentionQuota(), DELTA,"RetentionQuota");
//		assertEquals(expected.getTotal(), actual.getTotal(), DELTA,"Total");
//		assertEquals(expected.getPosShift(), actual.getPosShift(),"PosShift");
//		assertEquals(expected.getSeller(), actual.getSeller(),"Seller");
//		assertEquals(expected.getSellerName(), actual.getSellerName(),"SellerName");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getRemarks(), actual.getRemarks(),"Remarks");
//		assertEquals(expected.isAnnulled(), actual.isAnnulled(),"Annulled");
//		assertEquals(expected.getSiiStatus(), actual.getSiiStatus(),"SIIStatus");
//		assertEqualsInvoiceFiscal(expected.getFiscal(), actual.getFiscal());
//	}
//	
//	
//	
//
//	private static void assertEqualsInvoiceId(Invoice expected, Invoice actual) {
//		assertEqualsNulls( "Invoice", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//		}
//	}
//
//	
//	private static void assertEqualsInvoiceTaxes(List<InvoiceTax> expected, List<InvoiceTax> actual) {
//		assertEqualsCollection("Taxes", expected, actual);
//		IntStream.range(0, expected.size())
//	    	.forEach( i -> assertEqualsInvoiceTax(expected.get(i),actual.get(i)));
//	}
//	
//	
//	private static void assertEqualsTaxBreakdown(TaxBreakdown expected, TaxBreakdown actual) {
//		assertEqualsNulls( "TaxBreakdown", expected, actual);
//		if ( expected != null) {
//			assertEqualsInvoiceBreakdowns( expected.getBreakdown(), actual.getBreakdown() );
//		}
//	}
//
//	private static void assertEqualsInvoiceBreakdowns(List<InvoiceBreakdown> expected, List<InvoiceBreakdown> actual) {
//		assertEqualsCollection("InvoiceBreakdowns", expected, actual);
//		IntStream.range(0, expected.size())
//	    	.forEach( i -> assertEqualsInvoiceBreakdown(expected.get(i),actual.get(i)));
//	}
//
//	private static void assertEqualsInvoiceBreakdown(InvoiceBreakdown expected, InvoiceBreakdown actual) {
//		assertEqualsNulls( "InvoiceBreakdown", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getInvoice(), actual.getInvoice(),"Invoice");	
//		assertEquals(expected.getTaxType(), actual.getTaxType(),"TaxType");
//		assertEquals(expected.getBase(), actual.getBase(), DELTA,"Base");
//		assertEquals(expected.getPercentage(), actual.getPercentage(), DELTA,"Percentage");
//		assertEquals(expected.getQuota(), actual.getQuota(), DELTA,"Quota");
//		assertEquals(expected.getSurcharge(), actual.getSurcharge(), DELTA,"Surcharge");
//		assertEquals(expected.getSurchargeQuota(), actual.getSurchargeQuota(), DELTA,"SurchargeQuota");
//		assertEquals(expected.getDeductibleQuota(), actual.getDeductibleQuota(), DELTA,"DeductibleQuota");
//		assertEquals(expected.getWithholdingType(), actual.getWithholdingType(),"WithholdingType"); 
//		assertEquals(expected.getVatDeductionType(), actual.getVatDeductionType(),"VatDeductionType");
//	}
//
//	private static void assertEqualsInvoiceFiscal(InvoiceFiscal expected, InvoiceFiscal actual) {
//		if (expected != null) {
//			assertEqualsNulls( "InvoiceFiscal", expected, actual);
//			assertEquals(expected.getInvoice(), actual.getInvoice(),"Invoice");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			for (VATTaxRegime vatRegime : VATTaxRegime.values()) {
//				assertEquals(expected.isVatRegimeEnabled(vatRegime),actual.isVatRegimeEnabled(vatRegime),"InvoiceFiscal " + vatRegime.getName());	
//			}
//		}
//	}
//	
//	
//	private static void assertEqualsAttach(Attach expected, Attach actual) {
//		assertEqualsNulls( "Attach", expected, actual);
//		if (expected != null) {
//			assertEquals(expected.getAttachType(), actual.getAttachType(),"AttachType");
//			assertEquals(expected.getAttachModule(), actual.getAttachModule(),"AttachModule");
//			assertEquals(expected.getAttachURL(), actual.getAttachURL(),"AttachURL");
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEqualsDomainId(expected.getDomain(), actual.getDomain());
//			assertEquals(expected.getMimeType(), actual.getMimeType(),"MimeType");
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			// private byte[] data;
//			assertEqualsDate("Date",expected.getDate(), actual.getDate());
//			assertEquals(expected.getType(), actual.getType(),"Type");
//			assertEquals(expected.getDriveId(), actual.getDriveId(),"DriveId");
//			assertEquals(expected.getScope(), actual.getScope(),"Scope"); 
//			assertEquals(expected.getConfidential(), actual.getConfidential(),"Confidential");
//			assertEquals(expected.getCategory(), actual.getCategory(),"Category");
//			assertEquals(expected.getDparentId(), actual.getDparentId(),"DparentId");
//			assertEquals(expected.getSourceBatch(), actual.getSourceBatch(),"SourceBatch");
//			assertEquals(expected.getSourceType(), actual.getSourceType(),"SourceType");
//			assertEquals(expected.getIcon(), actual.getIcon(),"Icon");				
//			assertEquals(expected.getMd5(), actual.getMd5(),"Md5");
//			assertEquals(expected.getIsDrive(), actual.getIsDrive(),"IsDrive");
//			assertEqualsScope(expected.getFullScope(), actual.getFullScope());
//			assertEquals(expected.getCategory(), actual.getCategory(),"Category");
//			assertEqualsTags(expected.getTagList(), actual.getTagList());
//		}
//		
//	}
//	
//	private static void assertEqualsTags(List<Tag> expected, List<Tag> actual) {
//		assertEqualsCollection("InvoiceErrors", expected, actual);
//		IntStream.range(0, expected.size())
//	    	.forEach( i -> assertEqualsTag(expected.get(i),actual.get(i)));
//	}
//
//
//	private static void assertEqualsInvoiceErrors(List<InvoiceError> expected, List<InvoiceError> actual) {
//		assertEqualsCollection("InvoiceErrors", expected, actual);
//		IntStream.range(0, expected.size())
//	    	.forEach( i -> assertEqualsInvoiceError(expected.get(i),actual.get(i)));
//	}
//
//	private static void assertEqualsInvoiceError(InvoiceError expected, InvoiceError actual) {
//		assertEqualsNulls( "InvoiceError", expected, actual);
//		assertEquals(expected.getCode(), actual.getCode(),"Code");
//		assertEquals(expected.getLevel(), actual.getLevel(),"Level");
//		assertEqualsInvoiceErrorContext( expected.getContext(), actual.getContext());
//		assertEquals(expected.getMessage(), actual.getMessage(),"Message");
//	}
//	private static void assertEqualsInvoiceErrorContext(InvoiceErrorContext expected, InvoiceErrorContext actual) {
//		assertEquals(expected.getKey(), actual.getKey(),"Key");
//		assertEquals(expected.getLine(), actual.getLine(),"Line");
//	}
//
//	private static void assertEqualsInvoiceInfo(InvoiceInfo expected, InvoiceInfo actual) {
//		assertEqualsNulls( "FullInvoice", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getInvoice(), actual.getInvoice(),"Invoice");
//		assertEquals(expected.getType(), actual.getType(),"InvoiceCommunicationType");
//		assertEquals(expected.getStatus(), actual.getStatus(),"InvoiceCommunicationStatus");
//	}
//	
//	private static void assertEqualsFinances(List<Finance> expected, List<Finance> actual) {
//		assertEqualsCollection("Finances", expected, actual);
//		IntStream.range(0, expected.size())
//	    	.forEach( i -> assertEqualsFinance(expected.get(i),actual.get(i)));
//	}
//	
//	private static void assertEqualsFinance(Finance expected, Finance actual) {
//		assertEqualsNulls( "Finance", expected, actual);
//		// assertEquals(expected.isDirty(), actual.isDirty(),"Dirty");
//		assertEquals(expected.isSelected(), actual.isSelected(),"Selected");
//		assertEquals(expected.isRemoved(), actual.isRemoved(),"Removed");
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEqualsInvoiceId( expected.getInvoice(), actual.getInvoice());
//		assertEquals(expected.getPayMethod(), actual.getPayMethod(),"PayMethod");
//		assertEquals(expected.getPayMethodType(), actual.getPayMethodType(),"PayMethodType");
//		assertEquals(expected.getPayMethodName(), actual.getPayMethodName(),"PayMethodName");
//		assertEqualsRegistryId( expected.getRegistry(), actual.getRegistry());
//		assertEqualsScope( expected.getScope(), actual.getScope());
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.isPayment(), actual.isPayment(),"Payment");
//		assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(),"RegistryDocument");
//		assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(),"RegistryDocumentType");
//		assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(),"RegistryDocumentCountry");
//		assertEquals(expected.getRegistryName(), actual.getRegistryName(),"RegistryName");
//		assertEquals(expected.getRegistryAccountId(), actual.getRegistryAccountId(),"RegistryAccountId");
//		assertEquals(expected.getRegistryAccountCode(), actual.getRegistryAccountCode(),"RegistryAccountCode");
//		assertEquals(expected.getRegistryAccountDescription(), actual.getRegistryAccountDescription(),"RegistryAccountDescription");
//		assertEquals(expected.getAmount(), actual.getAmount(), DELTA,"Amount");
//		assertEquals(expected.getExpenses(), actual.getExpenses(), DELTA,"Expenses");
//		assertEquals(expected.getConcept(), actual.getConcept(),"Concept");
//		assertEqualsDate("DueDate", expected.getDueDate(), actual.getDueDate());
//		assertEqualsBankAccount(expected.getBankAccount(), actual.getBankAccount());
//		assertEquals(expected.getBankAlias(), actual.getBankAlias(),"BankAlias");
//		assertEquals(expected.getBic(), actual.getBic(),"Bic");
//		assertEquals(expected.getChequeNumber(), actual.getChequeNumber(),"ChequeNumber");
//		assertEquals(expected.getFinanceStatus(), actual.getFinanceStatus(),"FinanceStatus");
//		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"SecurityLevel");
//		assertEquals(expected.getRemarks(), actual.getRemarks(),"Remarks");
//		assertEquals(expected.isManual(), actual.isManual(),"Manual");
//		assertEquals(expected.isAdvance(), actual.isAdvance(),"Advance");
//		assertEquals(expected.isPayroll(), actual.isPayroll(),"Payroll");
//		assertEquals(expected.isPrepayment(), actual.isPrepayment(),"Prepayment");
//		assertEquals(expected.getSourceId(), actual.getSourceId(),"SourceId");
//		assertEquals(expected.getFinanceGroup(), actual.getFinanceGroup(),"FinanceGroup");
//	}
//		
//	private static void assertEmployee(Employee expected, Employee actual) {
//		assertEquals(expected.getCcc(), actual.getCcc(),"Ccc");
//		assertEquals(expected.getNaf(), actual.getNaf(),"Naf");
//		assertEquals(expected.getDni(), actual.getDni(),"Dni");
//		assertEquals(expected.getStartDate(), actual.getStartDate(),"StartDate");
//		assertEquals(expected.getEndDate(), actual.getEndDate(),"EndDate");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getRegime(), actual.getRegime(),"Regime");
//		assertEquals(expected.getFactor(), actual.getFactor(),"Factor");
//		assertEquals(expected.getOccupation(), actual.getOccupation(),"Occupation");
//		assertEquals(expected.getContractType(), actual.getContractType(),"ContractType");
//		assertEquals(expected.getRlce(), actual.getRlce(),"Rlce");
//		assertEquals(expected.getQuoteGroup(), actual.getQuoteGroup(),"QuoteGroup");
//		assertEquals(expected.getCategory(), actual.getCategory(),"Category");
//		assertEquals(expected.getSex(), actual.getSex(),"Sex");
//		assertEquals(expected.getBirthDate(), actual.getBirthDate(),"BirthDate");
//	}
//
//	private static void assertEqualsIrpfBreakdown (IrpfBreakdown expected, IrpfBreakdown actual) {
//		assertEqualsNulls( "IrpfBreakdown", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getActivity(), actual.getActivity(),"activity");
//			assertEquals(expected.getActivityDescription(), actual.getActivityDescription(),"activityDescription");
//			assertEquals(expected.getEpigraph(), actual.getEpigraph(),"epigraph");
//			assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(),"registryDocument");
//			assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(),"registryDocumentType");
//			assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(),"registryDocumentCountry");
//			assertEquals(expected.getName(), actual.getName(),"name");
//			assertEquals(expected.getIssueDate(), actual.getIssueDate(),"issueDate");
//			assertEquals(expected.getChargeDate(), actual.getChargeDate(),"chargeDate");
//			assertEquals(expected.isFromSalary(), actual.isFromSalary(),"fromSalary");
//			assertEquals(expected.isInsidePeriod(), actual.isInsidePeriod(),"insidePeriod"); 
//			assertEquals(expected.getSalary(), actual.getSalary(),"salary");
//			assertEquals(expected.getInvoiceType(), actual.getInvoiceType(),"invoiceType");
//			assertEquals(expected.getInvoice(), actual.getInvoice(),"invoice");
//			assertEquals(expected.getSeries(), actual.getSeries(),"series");
//			assertEquals(expected.getNumber(), actual.getNumber(),"number");
//			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(),"referenceCode"); 
//			assertEquals(expected.getTaxDate(), actual.getTaxDate(),"taxDate");
//			assertEquals(expected.getWithholdingType(), actual.getWithholdingType(),"withholdingType"); 
//			assertEquals(expected.getIRPFRegime(), actual.getIRPFRegime(),"regime");
//			assertEquals(expected.isInKind(), actual.isInKind(),"inKind");
//			assertEqualsDouble("base", expected.getBase(), actual.getBase());
//			assertEqualsDouble("percent", expected.getPercent(), actual.getPercent());
//			assertEqualsDouble("quota", expected.getQuota(), actual.getQuota());
//			assertEqualsDouble("deductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent());
//			assertEqualsDouble("deductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota());
//			assertEquals(expected.getGroupedBy(), actual.getGroupedBy(),"groupByNif");
//			assertEquals(expected.getZip(), actual.getZip(),"zip");
//			assertEquals(expected.getCity(), actual.getCity(),"city");
//		}
//	}
//	
//	private static void assertEqualsVatContext (VatContext expected, VatContext actual) {
//		assertEqualsNulls( "VatContext", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getInvoice(), actual.getInvoice(),"invoice");
//			assertEquals(expected.getActivity(), actual.getActivity(),"activity");
//			assertEquals(expected.getActivityDescription(), actual.getActivityDescription(),"activityDescription");
//			assertEquals(expected.getVatRegime(), actual.getVatRegime(),"vatRegime");
//			assertEquals(expected.isVatSurchargeRegime(), actual.isVatSurchargeRegime(),"vatSurchargeRegime");
//			assertEquals(expected.getEpigraph(), actual.getEpigraph(),"epigraph");
//			assertEquals(expected.getDocumentNumber(), actual.getDocumentNumber(),"documentNumber");
//			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(),"referenceCode");
//			assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(),"registryDocument");
//			assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(),"registryDocumentType");
//			assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(),"registryDocumentCountry");
//			assertEquals(expected.getRegistry(), actual.getRegistry(),"registry");
//			assertEquals(expected.getRegistryName(), actual.getRegistryName(),"registryName");
//			assertEquals(expected.getIssueDate(), actual.getIssueDate(),"issueDate");
//			assertEquals(expected.getTaxDate(), actual.getTaxDate(),"taxDate");
//			assertEquals(expected.getCreationDate(), actual.getCreationDate(),"creationDate");
//			assertEquals(expected.getRegContableDate(), actual.getRegContableDate(),"regContableDate");
//			assertEquals(expected.getDetailDescription(), actual.getDetailDescription(),"detailDescription");
//			assertEquals(expected.isInsidePeriod(), actual.isInsidePeriod(),"insidePeriod");
//			assertEquals(expected.getInvoiceType(), actual.getInvoiceType(),"invoiceType");
//			assertEquals(expected.getRectificationType(), actual.getRectificationType(),"rectificationType");
//			assertEquals(expected.getRectificationInvoice(), actual.getRectificationInvoice(),"rectificationInvoice");
//			assertEquals(expected.isService(), actual.isService(),"service");
//			assertEquals(expected.getTransaction(), actual.getTransaction(),"transaction");
//			assertEquals(expected.isInvestment(), actual.isInvestment(),"investment");
//			assertEquals(expected.isVatAccrualRegime(), actual.isVatAccrualRegime(),"vatAccrualRegime");
//			assertEquals(expected.getVatDeductionType(), actual.getVatDeductionType(),"vatDeductionType");
//			assertEquals(expected.isFarmerRegime(), actual.isFarmerRegime(),"farmerRegime");
//			assertEquals(expected.isPrepayment(), actual.isPrepayment(),"prepayment");
//			assertEquals(expected.isVatImportation(), actual.isVatImportation(),"vatImportation");
//			assertEquals(expected.hasDuaLinked(), actual.hasDuaLinked(),"duaLinked");
//			assertEqualsDouble("base", expected.getBase(), actual.getBase());
//			assertEqualsDouble("percentage", expected.getPercentage(), actual.getPercentage());
//			assertEqualsDouble("quota", expected.getQuota(), actual.getQuota());
//			assertEquals(expected.getInvestAsset(), actual.getInvestAsset(),"investAsset");
//			assertEqualsDouble("deductiblePercent", expected.getDeductiblePercent(), actual.getDeductiblePercent());
//			assertEqualsDouble("deductibleQuota", expected.getDeductibleQuota(), actual.getDeductibleQuota());
//			assertEquals(expected.isSurcharge(), actual.isSurcharge(),"surcharge");
//			assertEqualsDouble("surchargePercent", expected.getSurchargePercent(), actual.getSurchargePercent());
//			assertEqualsDouble("surchargeQuota", expected.getSurchargeQuota(), actual.getSurchargeQuota());
//			assertEquals(expected.isProrrated(), actual.isProrrated(),"prorrated");
//			assertEqualsDouble("prorratePercent", expected.getProrratePercent(), actual.getProrratePercent());
//			assertEqualsDouble("prorrateQuota", expected.getProrrateQuota(), actual.getProrrateQuota());
//			assertEquals(expected.getSiiStatus(), actual.getSiiStatus(),"siiStatus");
//			assertEquals(expected.getAmortizationDescription(), actual.getAmortizationDescription(),"amortizationDescription");
//			assertEquals(expected.getAmortizationPercentage(), actual.getAmortizationPercentage(),"amortizationPercentage");
//			assertEquals(expected.getAmortizationInitialDate(), actual.getAmortizationInitialDate(),"amortizationInitialDate");
//			assertEquals(expected.isFinancePending(), actual.isFinancePending(),"financePending"); 
//			assertEqualsDouble("amount347", expected.getAmount347(), actual.getAmount347());
//			assertEquals(expected.hasRetention(), actual.hasRetention(),"hasRetention");
//			assertEquals(expected.getRectificateInvoiceTaxDate(), actual.getRectificateInvoiceTaxDate(),"rectificateInvoiceTaxDate");
//			assertEquals(expected.getRectificateYear(), actual.getRectificateYear(),"rectificateYear");
//			assertEquals(expected.getRectificatePeriod(), actual.getRectificatePeriod(),"rectificatePeriod");
//		}
//	}
//	
//	
//
//	private static <T extends FiscalModel> void assertFiscalModel(T expected, T actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getDomainName(), actual.getDomainName(),"DomainName");
//		assertEquals(expected.getYear(), actual.getYear(),"Year");
//		assertEquals(expected.getFinance(), actual.getFinance(),"Finance");
//		assertEquals(expected.getModel(), actual.getModel(),"Model");
//		assertEquals(expected.getPeriod(), actual.getPeriod(),"Period");
//		assertEquals(expected.getAdministration(), actual.getAdministration(),"Administration");
//		assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		assertEquals(expected.getDeclarationResult(), actual.getDeclarationResult(),"DeclarationResult");
//		assertEquals(expected.getDeclarationResultType(), actual.getDeclarationResultType(),"DeclarationType");
//		assertEquals(expected.isConfidential(), actual.isConfidential(),"Confidential");
//		assertEquals(expected.isComplementary(), actual.isComplementary(),"Complementary");
//		assertEquals(expected.isReplacement(), actual.isReplacement(),"Replacement");
//		assertEquals(expected.isWithoutActivity(), actual.isWithoutActivity(),"WithoutActivity");
//		assertEquals(expected.getNumber(), actual.getNumber(),"Number");
//		assertEquals(expected.getReplacedNumber(), actual.getReplacedNumber(),"ReplacedNumber");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getDocument(), actual.getDocument(),"Document");
//		assertEquals(expected.getSurname(), actual.getSurname(),"Surname");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getStreetInitial(), actual.getStreetInitial(),"StreetInitial");
//		assertEquals(expected.getStreetName(), actual.getStreetName(),"StreetName");
//		assertEquals(expected.getStreetNumber(), actual.getStreetNumber(),"StreetNumber");
//		assertEquals(expected.getStreetStair(), actual.getStreetStair(),"StreetStair");
//		assertEquals(expected.getStreetFloor(), actual.getStreetFloor(),"StreetFloor");
//		assertEquals(expected.getStreetDoor(), actual.getStreetDoor(),"StreetDoor");
//		assertEquals(expected.getPhone(), actual.getPhone(),"Phone");
//		assertEquals(expected.getTown(), actual.getTown(),"Town");
//		assertEquals(expected.getProvince(), actual.getProvince(),"Province");
//		assertEquals(expected.getZip(), actual.getZip(),"Zip");
//		assertEquals(expected.getAdmonAeat(), actual.getAdmonAeat(),"AdmonAeat");
//		assertEquals(expected.getContactPerson(), actual.getContactPerson(),"ContactPerson");
//		assertEquals(expected.getContactPhone(), actual.getContactPhone(),"ContactPhone");
//		assertEquals(expected.getContactCellular(), actual.getContactCellular(),"ContactCellular");
//		assertEquals(expected.getContactEmail(), actual.getContactEmail(),"ContactEmail");
//		assertEquals(expected.getIban(), actual.getIban(),"Iban");
//	}
//	
//	private static void assertMod111(Mod111 expected, Mod111 actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod115(Mod115 expected, Mod115 actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod123(Mod123 expected, Mod123 actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod130(Mod130 expected, Mod130 actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod131(Mod131 expected, Mod131 actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod303(Mod303 expected, Mod303 actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod390HF(Mod390HF expected, Mod390HF actual) {
//		assertFiscalModel(expected, actual);
//	}
//	private static void assertMod190(Mod190 expected, Mod190 actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getDomainName(), actual.getDomainName(),"DomainName");
//		assertEquals(expected.getEnterprise(), actual.getEnterprise(),"Enterprise");
//		assertEquals(expected.getYear(), actual.getYear(),"Year");
//		assertEquals(expected.getAdministration(), actual.getAdministration(),"Administration");
//		assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		assertEquals(expected.isConfidential(), actual.isConfidential(),"Confidential");
//		assertEquals(expected.isComplementary(), actual.isComplementary(),"Complementary");
//		assertEquals(expected.isReplacement(), actual.isReplacement(),"Replacement");
//		assertEquals(expected.getReceipt(), actual.getReceipt(),"Receipt");
//		assertEquals(expected.getReplacedReceipt(), actual.getReplacedReceipt(),"ReplacedReceipt");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getDocument(), actual.getDocument(),"Document");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getContactPerson(), actual.getContactPerson(),"ContactPerson");
//		assertEquals(expected.getContactPhone(), actual.getContactPhone(),"ContactPhone");
//		assertEquals(expected.getContactMail(), actual.getContactMail(),"ContactMail");
//		assertEquals(expected.getReceiverCountTotal(), actual.getReceiverCountTotal(),"ReceiverCountTotal");
//		assertEqualsDouble("receiptTotal", expected.getReceiptTotal(), actual.getReceiptTotal());
//		assertEqualsDouble("retentionTotal", expected.getRetentionTotal(), actual.getRetentionTotal());
//	}
//	
//	private static void assertMod349(Mod349 expected, Mod349 actual) {
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getYear(), actual.getYear(),"Year");
//		assertEquals(expected.getPeriod(), actual.getPeriod(),"Period");
//		assertEquals(expected.getAdministration(), actual.getAdministration(),"Administration");
//		assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		assertEquals(expected.isConfidential(), actual.isConfidential(),"Confidential");
//		assertEquals(expected.isComplementary(), actual.isComplementary(),"Complementary");
//		assertEquals(expected.isReplacement(), actual.isReplacement(),"Replacement");
//		assertEquals(expected.getNumber(), actual.getNumber(),"Number");
//		assertEquals(expected.getReplacedNumber(), actual.getReplacedNumber(),"ReplacedNumber");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getDocument(), actual.getDocument(),"Document");
//		assertEquals(expected.getName(), actual.getName(),"Name");
//		assertEquals(expected.getContactPerson(), actual.getContactPerson(),"ContactPerson");
//		assertEquals(expected.getContactPhone(), actual.getContactPhone(),"ContactPhone");
//		assertEquals(expected.getContactMail(), actual.getContactMail(),"ContactMail");
//		assertEquals(expected.getRepresentativeDocument(), actual.getRepresentativeDocument(),"RepresentativeDocument");
//	}
//	
//	private static void assertEqualsOffer(Offer expected, Offer actual) {
//		assertEqualsNulls( "Offer", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getStatus().value(), actual.getStatus().value(),"Status");
//		}
//	}
//	
//	private static void assertEqualsOfferDetail(OfferDetail expected, OfferDetail actual) {
//		assertEqualsNulls( "Offer Detail", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getOffer().getId(), actual.getOffer().getId(),"Offer");
//			assertEquals(expected.getLine(), actual.getLine(),"Line");
//			assertEquals(expected.getItem().getId(), actual.getItem().getId(),"Item");
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEqualsDouble("Quantity", expected.getQuantity(), actual.getQuantity());
//			assertEquals(expected.getDiscountExpression(), actual.getDiscountExpression(),"Discount Expression");
//			assertEquals(expected.getStatus().value(), actual.getStatus().value(),"Status");
//		}
//	}
//	
//	private static void assertEqualsDelivery(Delivery expected, Delivery actual) {
//		assertEqualsNulls( "Delivery", expected, actual);
//		
//		if (expected != null && actual != null) {
//			assertEquals(expected.getAddress().getId(), actual.getAddress().getId());
//			assertEquals(expected.getBankAccount(), actual.getBankAccount(),"BankAccount");
//			assertEquals(expected.getBankAlias(), actual.getBankAlias(),"Bank alias");
//			assertEquals(expected.getBic(), actual.getBic(),"Bic");
//			assertEquals(expected.getCarrier(), actual.getCarrier(),"Carrier");
//			assertEquals(expected.getCarrierPacking(), actual.getCarrierPacking(),"Carrier Packing");
//			assertEquals(expected.getComments(), actual.getComments(),"Comments");
//			assertEquals(expected.getCreationUser(), actual.getCreationUser(),"Creation user");
//			assertEquals(expected.getCustomer().getId(), actual.getCustomer().getId());
//			assertEquals(expected.getDaysBetweenPymnt(), actual.getDaysBetweenPymnt(),"Days between pymnt");
//			assertEquals(expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt(),"Days to first pymnt");
//			assertEquals(expected.getDetails(), actual.getDetails(),"Details");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getDriver(), actual.getDriver(),"Driver");
//			assertEquals(expected.getDriverDocument(), actual.getDriverDocument(),"Driver document");
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getModificationUser(), actual.getModificationUser(),"Modification user");
//			assertEquals(expected.getNumber(), actual.getNumber(),"Number");
//			assertEquals(expected.getNumberOfPymnts(), actual.getNumberOfPymnts(),"Number of Pymnts");
//			assertEquals(expected.getNumberPlate(), actual.getNumberPlate(),"Number plate");
//			assertEquals(expected.getPackaging(), actual.getPackaging(),"Packaging");
//			assertEquals(expected.getPackagingData(), actual.getPackagingData(),"Packaging data");
//			assertEquals(expected.getPayMethod().getId(), actual.getPayMethod().getId());
//			assertEquals(expected.getProject().getId(), actual.getProject().getId());
//			assertEquals(expected.getPymntDays(), actual.getPymntDays(),"Pymnt days");
//			assertEquals(expected.getReferenceCode(), actual.getReferenceCode(),"Reference code");
//			assertEquals(expected.getRemarks(), actual.getRemarks(),"Remarks");
//			assertEquals(expected.getScope().getId(), actual.getScope().getId());
//			assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"Security level");
//			assertEquals(expected.getSeries(), actual.getSeries(),"Series");
//			assertEquals(expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress(),"Shipping alternative address");
//			assertEquals(expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2(),"Shipping alternative address two");
//			assertEquals(expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity(),"Shipping alternative city");
//			assertEquals(expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone(),"Shipping alternative phone");
//			assertEquals(expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient(),"Shipping alternative recipient");
//			assertEquals(expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip(),"Shipping alternative zip");
//			assertEquals(expected.getShippingContact(), actual.getShippingContact(),"Shipping contact");
//			assertEquals(expected.getShippingPeriod(), actual.getShippingPeriod(),"Shipping period");
//			assertEquals(expected.getShippingPeriodValue(), actual.getShippingPeriodValue(),"Shipping period value");
//			assertEquals(expected.getShippingStatus(), actual.getShippingStatus(),"Shipping status");
//			assertEquals(expected.getShippingStatusValue(), actual.getShippingStatusValue(),"Shipping status value");
//			assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//			assertEquals(expected.getTotalPackages(), actual.getTotalPackages(),"Total packages");
//			assertEquals(expected.getTotalWeight(), actual.getTotalWeight(),"Total weight");
//			assertEquals(expected.getTrackingNumber(), actual.getTrackingNumber(),"Tracking number");
//			assertEquals(expected.getWorkplace().getId(), actual.getWorkplace().getId());
//		}
//	}
//	
//	
//
//	private static void assertEqualsEdiCodes(EdiCodes expected, EdiCodes actual) {
//		assertEqualsNulls("EdiCodes", expected, actual);
//		
//		if (expected != null && actual != null) {
//			assertEquals(expected.getBycode(), actual.getBycode(),"Bycode");
//			assertEquals(expected.getCompanyEdiCode(), actual.getCompanyEdiCode(),"Company EdiCode");
//			assertEquals(expected.getCustomerEdiCode(), actual.getCustomerEdiCode(),"Customer EdiCode");
//			assertEquals(expected.getCustomerEdiHeader(), actual.getCustomerEdiHeader(),"Customer EdiHeader");
//			assertEquals(expected.getCustomerEdiInvoice(), actual.getCustomerEdiInvoice(),"Customer EdiInvoice");
//			assertEquals(expected.getCustomerEdiPoint(), actual.getCustomerEdiPoint(),"Customer EdiPoint");
//			assertEquals(expected.getCustomerPackage(), actual.getCustomerPackage(),"Customer Package");
//			assertEquals(expected.getDeliveryPointEdiCode(), actual.getDeliveryPointEdiCode(),"Delivery Point EdiCode");
//			assertEquals(expected.getDepartment(), actual.getDepartment(),"Customer Department");
//			assertEquals(expected.getDpcode(), actual.getDpcode(),"Customer Dpcode");
//			assertEquals(expected.getIvcode(), actual.getIvcode(),"Customer Ivcode");
//			assertEquals(expected.getMrcode(), actual.getMrcode(),"Customer Mrcode");
//			assertEquals(expected.getMscode(), actual.getMscode(),"Customer Mscode");
//			assertEquals(expected.getPwcode(), actual.getPwcode(),"Customer Pwcode");
//			assertEquals(expected.getShcode(), actual.getShcode(),"Customer Shcode");
//			assertEquals(expected.getSucode(), actual.getSucode(),"Customer Sucode");
//			assertEquals(expected.getUccode(), actual.getUccode(),"Customer Uccode");
//		}
//	}
//	
//	private static void assertEqualsDeliveryDetail(DeliveryDetail expected, DeliveryDetail actual) {
//		assertEqualsNulls("DeliveryDetail", expected, actual);
//		if (expected != null) {
//			assertEquals(expected.getCreationUser(), actual.getCreationUser(),"Creation user");
//			assertEquals(expected.getDelivery().getId(), actual.getDelivery().getId());
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEquals(expected.getDiscountExpression(), actual.getDiscountExpression(),"Discount expression");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getItem().getId(), actual.getItem().getId());
//			assertEquals(expected.getLine(), actual.getLine(),"Line");
//			assertEquals(expected.getModificationUser(), actual.getModificationUser(),"Modification user");
//			assertEquals(expected.getPrice(), actual.getPrice(),"Price");
//			//assertEquals("Purchased reference", expected.getPurchaseReference(), actual.getPurchaseReference());
//			assertEqualsDouble("Quantity", expected.getQuantity(), actual.getQuantity());
//			assertEquals(expected.getSalesDetail(), actual.getSalesDetail(),"Sales detail");
//			assertEquals(expected.getSalesDetailData(), actual.getSalesDetailData(),"Sales detail data");
//			assertEquals(expected.getWarehouse(), actual.getWarehouse(),"Warehouse");
//		}
//	}
//	
//	private static void assertEqualsSales(Sales expected, Sales actual) {
//		assertEqualsNulls("Sales",expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEqualsProject( expected.getProject(), actual.getProject());
//		assertEqualsCustomer(expected.getCustomer(), actual.getCustomer());
//		assertEquals(expected.getSeries(), actual.getSeries(),"Series");
//		assertEquals(expected.getNumber(), actual.getNumber(),"Number");
//		assertEquals(expected.getPurchaseReference(), actual.getPurchaseReference(),"PurchaseReference");
//		assertEqualsRegistryAddress( expected.getShippingAddress(), actual.getShippingAddress() );
//		assertEqualsSeller( expected.getSeller(), actual.getSeller() );
//		assertEquals(expected.getDiscountExpr(), actual.getDiscountExpr(),"DiscountExpr");
//		assertEquals(expected.getDate(), actual.getDate(),"IssueDate");
//		assertEqualsPayMethod(expected.getPayMethod(), actual.getPayMethod());
//		assertEquals(expected.getDocumentType(), actual.getDocumentType(),"DocumentType");
//		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"SecurityLevel");
//		assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getRemarks(), actual.getRemarks(),"Remarks");
//		assertEqualsWorkplace(expected.getWorkplace(), actual.getWorkplace());
//		assertEqualsScope( expected.getScope(), actual.getScope());
//		assertEquals(expected.getNumberOfPymnts(), actual.getNumberOfPymnts(),"NumberOfPymnts");
//		assertEquals(expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt(),"DaysToFirstPymnt");
//		assertEquals(expected.getDaysBetweenPymnts(), actual.getDaysBetweenPymnts(),"DaysBetweenPymnts");
//		assertEquals(expected.getPymntDays(), actual.getPymntDays(),"PymntDays");
//		assertEquals(expected.getBankAccount(), actual.getBankAccount(),"BankAccount");
//		assertEquals(expected.getBankAlias(), actual.getBankAlias(),"BankAlias");
//		assertEquals(expected.getBic(), actual.getBic(),"Bic");
//		assertEquals(expected.isPurchaseGenerated(), actual.isPurchaseGenerated(),"PurchaseGenerated");
//		assertEquals(expected.getDeliveryDate(), actual.getDeliveryDate(),"DeliveryDate");
//		assertEquals(expected.getCarrier(), actual.getCarrier(),"Carrier");
//		assertEqualsCarrier( expected.getCarrier(), actual.getCarrier());
//		assertEquals(expected.getCarrierPacking(), actual.getCarrierPacking(),"CarrierPacking");
//		assertEquals(expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress(),"ShippingAlternativeAddress");
//		assertEquals(expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2(),"ShippingAlternativeAddress2");
//		assertEquals(expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip(),"ShippingAlternativeZip");
//		assertEquals(expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity(),"ShippingAlternativeCity");
//		assertEquals(expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone(),"ShippingAlternativePhone");
//		assertEquals(expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient(),"ShippingAlternativeRecipient");
//		assertEquals(expected.getShippingContact(), actual.getShippingContact(),"ShippingContact");
//		assertEquals(expected.getShippingPeriod(), actual.getShippingPeriod(),"ShippingPeriod");
//	}
//	
//	private static void assertEqualsSalesDetail(SalesDetail expected, SalesDetail actual) {
//		assertEqualsNulls("SalesDetail", expected, actual);
//		if (expected != null) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEqualsSales( expected.getSales(), actual.getSales() );
//			assertEqualsItem( expected.getItem(), actual.getItem() );
//			assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//			assertEquals(expected.getLine(), actual.getLine(),"Line");
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEqualsDouble("Quantity", expected.getQuantity(), actual.getQuantity());
//			assertEqualsDouble("Price", expected.getPrice(), actual.getPrice());
//			assertEqualsDiscountExpression(expected.getDiscountExpression(), actual.getDiscountExpression()); 
//			assertEqualsDouble("Taxes", expected.getTaxes(), actual.getTaxes());
//			assertEquals(expected.getOfferDetail(), actual.getOfferDetail(),"OfferDetail");
//			assertEqualsDouble("Delivered", expected.getDelivered(), actual.getDelivered());
//			assertEquals(expected.getDeliveryDate(), actual.getDeliveryDate(),"DeliveryDate");
//			assertEqualsCarrier(expected.getCarrier(), actual.getCarrier());
//			assertEquals(expected.getCarrierPacking(), actual.getCarrierPacking(),"CarrierPacking");
//			assertEquals(expected.getDelivery(), actual.getDelivery(),"Delivery");
//		}
//	}
//	
//	private static void assertEqualsPurchase(Purchase expected, Purchase actual) {
//		assertEqualsNulls("Purchase", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEquals(expected.getProject(), actual.getProject(),"Project");
//		assertEquals(expected.getSupplier(), actual.getSupplier(),"SupplierId");
//		assertEquals(expected.getSupplierName(), actual.getSupplierName(),"SupplierName");
//		assertEqualsSupplier(expected.getSupplier2(), actual.getSupplier2());
//		assertEquals(expected.getSeries(), actual.getSeries(),"Series");
//		assertEquals(expected.getNumber(), actual.getNumber(),"Number");
//		assertEquals(expected.getPurchaseReference(), actual.getPurchaseReference(),"PurchaseReference");
//		assertEquals(expected.getAddress(), actual.getAddress(),"Address");
//		assertEquals(expected.getDiscountExpr(), actual.getDiscountExpr(),"DiscountExpr");
//		assertEquals(expected.getIssueDate(), actual.getIssueDate(),"IssueDate");
//		assertEquals(expected.getPayMethod(), actual.getPayMethod(),"PayMethod");
//		assertEquals(expected.getDocumentType(), actual.getDocumentType(),"DocumentType");
//		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"SecurityLevel");
//		assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getRemarks(), actual.getRemarks(),"Remarks");
//		assertEquals(expected.getWorkplace(), actual.getWorkplace(),"Workplace");
//		assertEquals(expected.getWorkplaceName(), actual.getWorkplaceName(),"WorkplaceName");
//		assertEquals(expected.getWarehouse(), actual.getWarehouse(),"Warehouse");
//		assertEquals(expected.getScope(), actual.getScope(),"Scope");
//		assertEquals(expected.getScopeName(), actual.getScopeName(),"ScopeName");
//		assertEquals(expected.getNumberOfPymnts(), actual.getNumberOfPymnts(),"NumberOfPymnts");
//		assertEquals(expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt(),"DaysToFirstPymnt");
//		assertEquals(expected.getDaysBetweenPymnts(), actual.getDaysBetweenPymnts(),"DaysBetweenPymnts");
//		assertEquals(expected.getPymntDays(), actual.getPymntDays(),"PymntDays");
//		assertEquals(expected.getBankAccount(), actual.getBankAccount(),"BankAccount");
//		assertEquals(expected.getBankAlias(), actual.getBankAlias(),"BankAlias");
//		assertEquals(expected.getBic(), actual.getBic(),"Bic");
//		assertEquals(expected.isEmailCommunication(), actual.isEmailCommunication(),"EmailCommunication");
//		assertEquals(expected.getCarrier(), actual.getCarrier(),"Carrier");
//		assertEquals(expected.getShippingAlternativeAddress(), actual.getShippingAlternativeAddress(),"ShippingAlternativeAddress");
//		assertEquals(expected.getShippingAlternativeAddress2(), actual.getShippingAlternativeAddress2(),"ShippingAlternativeAddress2");
//		assertEquals(expected.getShippingAlternativeZip(), actual.getShippingAlternativeZip(),"ShippingAlternativeZip");
//		assertEquals(expected.getShippingAlternativeCity(), actual.getShippingAlternativeCity(),"ShippingAlternativeCity");
//		assertEquals(expected.getShippingAlternativePhone(), actual.getShippingAlternativePhone(),"ShippingAlternativePhone");
//		assertEquals(expected.getShippingAlternativeRecipient(), actual.getShippingAlternativeRecipient(),"ShippingAlternativeRecipient");
//		assertEquals(expected.getShippingContact(), actual.getShippingContact(),"ShippingContact");
//		assertEquals(expected.getShippingPeriod(), actual.getShippingPeriod(),"ShippingPeriod");
//		assertEquals(expected.getCarrierPacking(), actual.getCarrierPacking(),"CarrierPacking");
//	}
//	
//	private static void assertEqualsPurchaseDetail(PurchaseDetail expected, PurchaseDetail actual) {
//		assertEqualsNulls("PurchaseDetail", expected, actual);
//		if (expected != null) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getPurchaseId(), actual.getPurchaseId(),"PurchaseId");
//			assertEqualsPurchase( expected.getPurchase(), actual.getPurchase() );
//			assertEquals(expected.getProject(), actual.getProject(),"Project");
//			assertEquals(expected.getProjectName(), actual.getProjectName(),"ProjectName");
//			assertEquals(expected.getItem(), actual.getItem(),"Item");
//			// assertEquals( expected.getItem(), actual.getItem() );
//			assertEquals(expected.getLine(), actual.getLine(),"Line");
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEqualsDouble("Quantity", expected.getQuantity(), actual.getQuantity());
//			assertEqualsDouble("Price", expected.getPrice(), actual.getPrice());
//			assertEquals(expected.getDiscountExpression(), actual.getDiscountExpression(),"DiscountExpression"); 
//			assertEqualsDouble("Taxes", expected.getTaxes(), actual.getTaxes());
//			assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//			assertEquals(expected.getProposalDetail(), actual.getProposalDetail(),"ProposalDetail");
//			assertEquals(expected.getSource(), actual.getSource(),"Source");
//			assertEquals(expected.getSourceId(), actual.getSourceId(),"SourceId");
//			assertEqualsDouble("Delivered", expected.getDelivered(), actual.getDelivered());
//			assertEquals(expected.getDeliveryDate(), actual.getDeliveryDate(),"DeliveryDate");
//		}
//	}
//	
//	private static void assertEqualsIncome(Income expected, Income actual) {
//		assertEqualsNulls("Income", expected, actual);
//		assertEquals(expected.getId(), actual.getId(),"Id");
//		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//		assertEqualsProject(expected.getProject(), actual.getProject());
//		assertEquals(expected.getReferenceCode(), actual.getReferenceCode(),"ReferenceCode");
//		assertEquals(expected.getSupplier(), actual.getSupplier(),"Supplier");
//		assertEquals(expected.getSupplierName(), actual.getSupplierName(),"SupplierName");
//		assertEqualsSupplier( expected.getSupplier2(), actual.getSupplier2()); 
//		assertEquals(expected.getAddress(), actual.getAddress(),"Address");
//		assertEquals(expected.getIssueDate(), actual.getIssueDate(),"IssueDate");
//		assertEquals(expected.getPayMethod(), actual.getPayMethod(),"PayMethod");
//		assertEquals(expected.getSecurityLevel(), actual.getSecurityLevel(),"SecurityLevel");
//		assertEquals(expected.getStatus(), actual.getStatus(),"Status");
//		assertEquals(expected.getComments(), actual.getComments(),"Comments");
//		assertEquals(expected.getRemarks(), actual.getRemarks(),"Remarks");
//		assertEquals(expected.getWorkplace(), actual.getWorkplace(),"Workplace");
//		assertEquals(expected.getWorkplaceName(), actual.getWorkplaceName(),"WorkplaceName");
//		assertEquals(expected.getScope(), actual.getScope(),"Scope");
//		assertEquals(expected.getScopeName(), actual.getScopeName(),"ScopeName");
//		assertEquals(expected.getNumberOfPymnts(), actual.getNumberOfPymnts(),"NumberOfPymnts");
//		assertEquals(expected.getDaysToFirstPymnt(), actual.getDaysToFirstPymnt(),"DaysToFirstPymnt");
//		assertEquals(expected.getDaysBetweenPymnt(), actual.getDaysBetweenPymnt(),"DaysBetweenPymnts");
//		assertEquals(expected.getPymntDays(), actual.getPymntDays(),"PymntDays");
//		assertEquals(expected.getBankAccount(), actual.getBankAccount(),"BankAccount");
//		assertEquals(expected.getBankAlias(), actual.getBankAlias(),"BankAlias");
//		assertEquals(expected.getBic(), actual.getBic(),"Bic");
//		assertEquals(expected.getCarrierPacking(), actual.getCarrierPacking(),"CarrierPacking");
//		
//	}
//
//	private static void assertEqualsIncomeDetail(IncomeDetail expected, IncomeDetail actual) {
//		assertEqualsNulls("IncomeDetail", expected, actual);
//		if (expected != null) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEqualsIncome( expected.getIncome(), actual.getIncome() );
//			assertEqualsProject( expected.getProject(), actual.getProject() );
//			assertEquals(expected.getLine(), actual.getLine(),"Line");
//			// assertEqualsItem( expected.getItem(), actual.getItem() );
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEquals(expected.getWarehouse(), actual.getWarehouse(),"Warehouse");
//			assertEqualsDouble("Quantity", expected.getQuantity(), actual.getQuantity());
//			assertEqualsDouble("Price", expected.getPrice(), actual.getPrice());
//			assertEquals(expected.getDiscountExpression(), actual.getDiscountExpression(),"DiscountExpression"); 
//			assertEquals(expected.getPurchaseDetail(), actual.getPurchaseDetail(),"PurchaseDetail");
//		}
//	}
//	private static void assertEqualsQuestion(Question expected, Question actual) {
//		if (expected != null && actual != null) {
//			assertEquals(expected.getId(),actual.getId(),"Id");
//			assertEquals(expected.getDomain(),actual.getDomain(),"Domain");
//			assertEquals(expected.isActive(),actual.isActive(),"Active");
//			assertEquals(expected.getText(),actual.getText(),"Text");
//			assertEquals(expected.getType(),actual.getType(),"Type");
//			assertEquals(expected.getArgument(),actual.getArgument(),"Argument");
//			assertEquals(expected.getAlias(),actual.getAlias(),"Alias");
//		}
//	}
//
//	
//	
//	private static void assertEqualsBankAccount(BankAccount expected, BankAccount actual) {
//		if (expected != null) {
//			assertEquals(expected.getBankCode(), actual.getBankCode(),"BankCode");
//			assertEquals(expected.getBankCodeLength(), actual.getBankCodeLength(),"BankCodeLenght");
//			assertEquals(expected.getBban(), actual.getBban(),"Bban");
//			assertEquals(expected.getBban1(), actual.getBban1(),"Bban1");
//			assertEquals(expected.getBban1(), actual.getBban1(),"Bban1");
//			assertEquals(expected.getBban2(), actual.getBban2(),"Bban2");
//			assertEquals(expected.getBban3(), actual.getBban3(),"Bban3");
//			assertEquals(expected.getBban4(), actual.getBban4(),"Bban4");
//			assertEquals(expected.getBban5(), actual.getBban5(),"Bban5");
//			assertEquals(expected.getBban6(), actual.getBban6(),"Bban6");
//			assertEquals(expected.getBban7(), actual.getBban7(),"Bban7");
//			assertEquals(expected.getBban8(), actual.getBban8(),"Bban8");
//			assertEquals(expected.getCCC(), actual.getCCC(),"CCC");
//			assertEquals(expected.getCCC1(), actual.getCCC1(),"CCC1");
//			assertEquals(expected.getCCC2(), actual.getCCC2(),"CCC2");
//			assertEquals(expected.getCCC3(), actual.getCCC3(),"CCC3");
//			assertEquals(expected.getCCC4(), actual.getCCC4(),"CCC4");
//			assertEquals(expected.getCheck(), actual.getCheck(),"Check");
//			assertEquals(expected.getCountry(), actual.getCountry(),"Country");
//			assertEquals(expected.getIban(), actual.getIban(),"Iban");
//			assertEquals(expected.getIbanLength(), actual.getIbanLength(),"IbanLength");
//			assertEquals(expected.getMaskedIban(), actual.getMaskedIban(),"MaskedIban");
//			assertEquals(expected.getPureCCC(), actual.getPureCCC(),"PureCCC");
//			assertEquals(expected.getSeparatedIban(), actual.getSeparatedIban(),"SeparatedIban");
//		}
//	}
//	
//	private static void assertEqualsRegistryBank(RegistryBank expected, RegistryBank actual) {
//		if (expected != null) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEqualsAccount(expected.getAccount(), actual.getAccount());
//			assertEquals(expected.getActive(), actual.getActive(),"Active");
//			assertEquals(expected.getAlias(), actual.getAlias(),"Alias");
//			assertEqualsBankAccount(expected.getBankAccount(), actual.getBankAccount());
//			assertEquals(expected.getBic(), actual.getBic(),"Bic");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getFullName(), actual.getFullName(),"FullName");
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getRegistry(), actual.getRegistry(),"Registry");
//			assertEquals(expected.getRequisition(), actual.getRequisition(),"Requisition");
//			assertEquals(expected.getSepaMandateRef(), actual.getSepaMandateRef(),"SepaMandateRef");
//			assertEquals(expected.getSuffix(), actual.getSuffix(),"Suffix");
//		}
//	}
//	
//	private static void assertEqualsCarrier(Carrier expected, Carrier actual) {
//		if (expected != null && actual != null) {
//			assertEquals(expected.getAlias(), actual.getAlias(),"Alias");
//			Asserts.assertEqualsRegistry(expected.get(), actual.get());
//			assertEquals(expected.getDocument(), actual.getDocument(),"Document");
//			assertEquals(expected.getDocumentCountry().toString(), actual.getDocumentCountry().toString(),"Country");
//			assertEquals(expected.getDocumentType().toString(), actual.getDocumentType().toString(),"Document");
//			Asserts.assertEqualsDomain(expected.getDomain(), actual.getDomain());
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getName(), actual.getName(),"Name");
//			assertEquals(expected.getDocument(), actual.getDocument(),"Document");
//			assertEquals(expected.getNationality().toString(), actual.getNationality().toString(),"Nationality");
//			Asserts.assertEqualsScope(expected.getScope(), actual.getScope());
//			assertEquals(expected.getSecurityLevel().toString(), actual.getSecurityLevel().toString(),"Security Level");
//			assertEquals(expected.getStatus().toString(), actual.getStatus().toString(),"Status");
//		}
//	}
//	
//	
//	private static void assertEqualsSeries(Series expected, Series actual) {
//		assertEqualsNulls( "Series", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getId(), actual.getId(),"Id");
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getScope(), actual.getScope(),"Scope");
//			assertEquals(expected.getCode(), actual.getCode(),"Code");
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEquals(expected.isActive(), actual.isActive(),"Active");
//			assertEquals(expected.isTas(), actual.isTas(),"Tas");
//			assertEquals(expected.isOffer(), actual.isOffer(),"Offer");
//			assertEquals(expected.isSales(), actual.isSales(),"Sales");
//			assertEquals(expected.isDelivery(), actual.isDelivery(),"Delivery");
//			assertEquals(expected.isInvoice(), actual.isInvoice(),"Invoice");
//			assertEquals(expected.isRectification(), actual.isRectification(),"Rectification"); 
//			assertEquals(expected.isPos(), actual.isPos(),"Pos");
//			assertEquals(expected.getSecurityLevel().toString(), actual.getSecurityLevel().toString(),"Security Level");
//		}
//	}
//	
//	private static void assertEqualsInvoiceSeries(InvoiceSeries expected, InvoiceSeries actual) {
//		assertEqualsNulls( "InvoiceSeries", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getDescription(), actual.getDescription(),"Description");
//			assertEquals(expected.isSales(), actual.isSales(),"Sales");
//			assertEquals(expected.getFromNumber(), actual.getFromNumber(),"FromNumber");
//			assertEquals(expected.getToNumber(), actual.getToNumber(),"ToNumber");
//			assertEquals(expected.getCount(), actual.getCount(),"Count");
//		}
//	}
//	
//	private static void assertsRegistryData(Registry reg, Invoice invoice) {
//		assertNotNull(reg);
//		assertNotNull(invoice);
//		assertEquals( reg.getId(), invoice.getRegistry() );
//		assertEquals( reg.getDocumentType(), invoice.getRegistryDocumentType());
//		assertEquals( reg.getDocumentCountry(), invoice.getRegistryDocumentCountry());
//		assertEquals( reg.getDocument(), invoice.getRegistryDocument());
//		assertEquals( reg.getName(), invoice.getRegistryName());
// 	}
//
//	private static void assertEqualsInvoiceData(InvoiceData expected, InvoiceData actual) {
//		assertEqualsNulls( "InvoiceData", expected, actual);
//		if (expected != null ) {
//			assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
//			assertEquals(expected.getInvoice(), actual.getInvoice(),"Invoice");
//			assertEquals(expected.getName(), actual.getName(),"Name");
//			assertEquals(expected.getValue(), actual.getValue(),"Value");
////			assertEquals("StartDate",  expected.getStartDate(), actual.getStartDate());
////			assertEquals("EndDate", expected.getEndDate(), actual.getEndDate());
//		}
//	}
}