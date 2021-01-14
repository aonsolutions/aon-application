package com.esferalia.aon.occam.test;

import static org.junit.Assert.assertEquals;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Registry;

public class Asserts {

	public static void assertEqualsRegistry (Registry expected, Registry actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getDomain().getId(), actual.getDomain().getId());
		assertEquals(expected.getDocument(), actual.getDocument());
		assertEquals(expected.getDocumentType(), actual.getDocumentType());
		assertEquals(expected.getDocumentCountry(), actual.getDocumentCountry());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getAlias(), actual.getAlias());
		assertEquals(expected.isLegalPerson(), actual.isLegalPerson());
		assertEquals(expected.getNationality(), actual.getNationality());
		assertEquals(expected.getSecurityLevel() , actual.getSecurityLevel());
	}

	public static void assertEqualsCustomer(Customer expected, Customer actual) {
		assertEqualsRegistry(expected, actual);
		assertEquals(expected.getTariff(), actual.getTariff());
		assertEquals(expected.isSurcharge(),actual.isSurcharge());
		assertEquals(expected.isWithholding(),actual.isWithholding());
		assertEquals(expected.getTransaction(),actual.getTransaction());
		assertEquals(expected.getStatus(),actual.getStatus());
		assertEquals(expected.getScope(),actual.getScope());
		assertEquals(expected.isEInvoice(),actual.isEInvoice());
		assertEquals(expected.getInvoicingGroup(),actual.getInvoicingGroup());
		assertEquals(expected.isProjectGrouped(),actual.isProjectGrouped());
		assertEquals(expected.isDeliveryGrouped(),actual.isDeliveryGrouped());
		assertEquals(expected.isDeliveryValuated(),actual.isDeliveryValuated());
		assertEquals(expected.getAccount(),actual.getAccount());
	}

	public static void assertEqualsTariff(Tariff expected, Tariff actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getDomain(), actual.getDomain());
		assertEquals(expected.getCode(), actual.getCode());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.isPurchase(), actual.isPurchase());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.isActive(), actual.isActive());
	}
	
}
