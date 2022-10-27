package es.aonsolutions.aio.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import com.code.aon.account.Account;

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
	public static void assertNotEmptyCollection(String msg,Collection<?> actual) {
		assertNotNull(msg,actual);		
		assertTrue(msg,!actual.isEmpty());
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
}
