package com.esferalia.aon.occam.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;

public class Asserts {
	
	private static final double DELTA = 1e-15;
	
	public static void assertEqualsNulls(String msg,Object expected, Object actual) {
		if ( expected == null) assertNull(msg,actual);
		if ( expected != null) assertNotNull(msg,actual);
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
	
	public static void assertEqualsRegistryFull (RegistryFull expected, RegistryFull actual) {
		assertEqualsNulls( "RegistryFull", expected, actual);
		
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
	
}
