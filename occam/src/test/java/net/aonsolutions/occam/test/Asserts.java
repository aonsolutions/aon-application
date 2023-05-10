package net.aonsolutions.occam.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.invoice.Invoice;

public class Asserts {
	
	private static final double DELTA = 1e-8;
	
	public static void assertEqualsDouble(String msg,double expected,double actual) {
		assertEquals(expected, actual, DELTA, msg);		
	}
	
	public static void assertEqualsNulls(Object expected, Object actual,String msg) {
		if ( expected == null) assertNull(actual,msg);
		if ( expected != null) assertNotNull(actual,msg);
	}

	
	public static void assertNullCollection(String msg,Collection<?> actual) {
		assertNull(actual,msg);		
	}
	public static void assertNotEmpty(Collection<?> expected, String msg) {
		assertNotNull(expected,msg);
		assertFalse(expected.isEmpty(),msg);
	}
	public static void assertEmpty(Collection<?> expected, String msg) {
		assertNotNull(expected,msg);
		assertTrue(expected.isEmpty(),msg);
	}
	
	public static void assertEqualsCollection(Collection<?> expected, Collection<?> actual, String msg) {
		if ( (expected == null || expected.isEmpty()) && ( (actual != null && !actual.isEmpty()))) 
			fail( msg + " actual List is not Empty");
		if ( (expected != null && !expected.isEmpty()) 
			&& (actual == null || actual.isEmpty()))  
			fail( msg + " actual List is Empty");
		if ( expected != null && actual != null) {
			assertEquals(expected.size(), actual.size()," sizes not fit");	
		}
	}
	
	public static void assertEqualsDomain(Domain expected, Domain actual) {
		assertEqualsNulls(expected, actual,"Domain");
		assertEquals(expected.getId(), actual.getId(),"Id");
		assertEquals(expected.getName(), actual.getName(),"Name");
		assertEquals(expected.getDescription(), actual.getDescription(),"Description");
		assertEquals(expected.getOwner(), actual.getOwner(),"Owner");
		assertEqualsNulls(expected.getParent(), actual.getParent(),"Parent Domain");
		if ( expected.getParent().isPresent() && expected.getParent().get() != null) {
			assertEqualsDomain(expected.getParent().get(), actual.getParent().get());
		}
		assertEquals(expected.getType(), actual.getType(),"DomainType");
		assertEquals(expected.getSubDomainSuffix(), actual.getSubDomainSuffix(),"SubDomainSuffix");
		assertEquals(expected.isEnableHeredity(), actual.isEnableHeredity(),"EnableHeredity");
		assertEquals(expected.isDomainManagement(), actual.isDomainManagement(),"DomainManagement");
		assertEquals(expected.isDisableDomainManagement(), actual.isDisableDomainManagement(),"DisableDomainManagement");
		assertEquals(expected.isActive(), actual.isActive(),"Active");
		if ( expected.getScope().isPresent() && expected.getScope().get() != null) {
			assertEqualsScope(expected.getScope().get(), actual.getScope().get());
		}
		assertEquals(expected.getMaxDefinedUsers(), actual.getMaxDefinedUsers(),"MaxDefinedUsers");
		assertEquals(expected.getMaxDocumentSize(), actual.getMaxDocumentSize(),"MaxDocumentSize");
		assertEquals(expected.getMaxTotalDocumentSize(), actual.getMaxTotalDocumentSize(),"MaxTotalDocumentSize");
		assertEquals(expected.getLastAccessUser(), actual.getLastAccessUser(),"LastAccessUser");
		assertEquals(expected.getLastAccessDate(), actual.getLastAccessDate(),"LastAccessDate");
		assertEquals(expected.getExpirationDate(), actual.getExpirationDate(),"ExpirationDate");
		assertEquals(expected.getCreationUser(), actual.getCreationUser(),"CreationUser");
		assertEquals(expected.getCreationDate(), actual.getCreationDate(),"CreationDate");
		assertEquals(expected.getModificationUser(), actual.getModificationUser(),"ModificationUser");
		assertEquals(expected.getModificationDate(), actual.getModificationDate(),"ModificationDate");
		assertEquals(expected.getAonCustomer(), actual.getAonCustomer(),"AonCustomer");
		assertEquals(expected.getAonStatus(), actual.getAonStatus(),"AonStatus");
	}
	
	public static void assertEqualsScope(Scope expected, Scope actual) {
		assertEqualsNulls(expected, actual,"Scope");
		assertEquals(expected.getId(), actual.getId(),"Id");
		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
		assertEquals(expected.getDescription(), actual.getDescription(),"Description");
	}

	public static void assertEqualsUser(User expected, User actual) {
		assertEqualsNulls(expected, actual,"User");
		assertEquals(expected.getId(), actual.getId(),"Id");
		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
		assertEquals(expected.getName(), actual.getName(),"Name");
		assertEquals(expected.getLogin(), actual.getLogin(),"Login");
		assertEquals(expected.isActive(), actual.isActive(),"Active");
	}

	public static void assertEqualsInvoice(Invoice expected, Invoice actual) {
		assertEqualsNulls(expected, actual, "Invoice");
		assertEquals(expected.getId(), actual.getId(),"Id");
		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
		assertEquals(expected.getType(), actual.getType(),"Type");
		assertEquals(expected.getSeries(), actual.getSeries(),"Series");
		assertEquals(expected.getNumber(), actual.getNumber(),"Number");
		assertEquals(expected.getReferenceCode(), actual.getReferenceCode(),"ReferenceCode");
		assertEquals(expected.getIssueDate(), actual.getIssueDate(),"IssueDate");
		assertEquals(expected.getTaxDate(), actual.getTaxDate(),"TaxDate");
		assertEquals(expected.isConfidential(), actual.isConfidential(),"Confidential");
		assertEquals(expected.getRegistry(), actual.getRegistry(),"Registry");
		assertEquals(expected.getRegistryDocument(), actual.getRegistryDocument(),"RegistryDocument");
		assertEquals(expected.getRegistryDocumentType(), actual.getRegistryDocumentType(),"RegistryDocumentType");
		assertEquals(expected.getRegistryDocumentCountry(), actual.getRegistryDocumentCountry(),"RegistryDocumentCountry");
		assertEquals(expected.getRegistryName(), actual.getRegistryName(),"RegistryName");
		assertEquals(expected.getCreationUser(), actual.getCreationUser(),"CreationUser");
		assertEquals(expected.getCreationDate(), actual.getCreationDate(),"CreationDate");
		assertEquals(expected.getModificationUser(), actual.getModificationUser(),"ModificationUser");
		assertEquals(expected.getModificationDate(), actual.getModificationDate(),"ModificationDate");
		assertEqualsActivity(expected.getActivity().orElse(null), actual.getActivity().orElse(null));
	}
	
	public static void assertEqualsActivity(Activity expected, Activity actual) {
		assertEqualsNulls(expected, actual, "Invoice");
		assertEquals(expected.getId(), actual.getId(),"Id");
		assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
		assertEquals(expected.getDescription(), actual.getDescription(),"Description");
		assertEquals(expected.getEpigraph(), actual.getEpigraph(),"Epigraph");
	}
	
}
