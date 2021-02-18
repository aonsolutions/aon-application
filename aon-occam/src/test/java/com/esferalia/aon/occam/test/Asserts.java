package com.esferalia.aon.occam.test;

import static org.junit.Assert.assertEquals;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;

public class Asserts {
	
	private static final double DELTA = 1e-15;
	
	public static void assertEqualsRegistry (Registry expected, Registry actual) {
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

	public static void assertEqualsCustomer(Customer expected, Customer actual) {
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

	public static void assertEqualsRegistryMedia (RegistryMedia expected, RegistryMedia actual) {
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

	public static void assertEqualsTariff(Tariff expected, Tariff actual) {
		assertEquals("Id",expected.getId(), actual.getId());
		assertEquals("Domain",expected.getDomain(), actual.getDomain());
		assertEquals("Code",expected.getCode(), actual.getCode());
		assertEquals("Name",expected.getName(), actual.getName());
		assertEquals("Purchase",expected.isPurchase(), actual.isPurchase());
		assertEquals("Discount",expected.getDiscount(), actual.getDiscount(), DELTA);
		assertEquals("Active",expected.isActive(), actual.isActive());
	}
	
}
