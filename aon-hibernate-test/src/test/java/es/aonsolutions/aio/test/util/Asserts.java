package es.aonsolutions.aio.test.util;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.code.aon.account.Account;

public class Asserts {
	
	private static final double DELTA = 1e-8;
	
	public static void assertEqualsDouble(String msg,double expected,double actual) {
		Assertions.assertEquals(expected, actual, DELTA ,msg);		
	}
	public static void assertNotEqualsDouble(String msg,double expected,double actual) {
		Assertions.assertNotEquals(expected, actual, DELTA, msg);		
	}
	
	public static void assertEqualsNulls(String msg,Object expected, Object actual) {
		if ( expected == null) Assertions.assertNull(actual,msg);
		if ( expected != null) Assertions.assertNotNull(actual,msg);
	}
	public static void assertEqualsArray(String msg,Object[] expected, Object[] actual) {
		if ( (expected == null || expected.length == 0) 
				&& ( (actual != null && actual.length != 0))) 
				Assertions.fail( msg + " actual List is not Empty");
			if ( (expected != null && expected.length != 0) 
				&& (actual == null || actual.length == 0))  
				Assertions.fail( msg + " actual List is Empty");
			if ( expected != null && actual != null) {
				Assertions.assertEquals( expected.length, actual.length, " sizes not fit");	
			}
	}
	
	public static void assertNullCollection(String msg,Collection<?> actual) {
		Assertions.assertNull(actual,msg);		
	}
	public static void assertEmptyCollection(String msg,Collection<?> actual) {
		Assertions.assertNotNull(actual,msg);		
		Assertions.assertTrue(actual.isEmpty(),msg);
	}
	public static void assertNotEmptyCollection(String msg,Collection<?> actual) {
		Assertions.assertNotNull(actual,msg);		
		Assertions.assertFalse(actual.isEmpty(),msg);
	}
	
	public static void assertEqualsCollection(String msg,Collection<?> expected, Collection<?> actual) {
		if ( (expected == null || expected.isEmpty()) 
			&& ( (actual != null && !actual.isEmpty()))) 
			Assertions.fail( msg + " actual List is not Empty");
		if ( (expected != null && !expected.isEmpty()) 
			&& (actual == null || actual.isEmpty()))  
			Assertions.fail( msg + " actual List is Empty");
		if ( expected != null && actual != null) {
			Assertions.assertEquals(expected.size(), actual.size(), " sizes not fit");	
		}
	}
	
	public static void assertEqualsMap(String msg,Map<?,?> expected, Map<?,?> actual) {
		if ( (expected == null || expected.isEmpty()) 
			&& ( (actual != null && !actual.isEmpty()))) 
			Assertions.fail( msg + " actual List is not Empty");
		if ( (expected != null && !expected.isEmpty()) 
			&& (actual == null || actual.isEmpty()))  
			Assertions.fail( msg + " actual List is Empty");
		if ( expected != null && actual != null) {
			Assertions.assertEquals(expected.size(), actual.size(), " sizes not fit");	
		}
	}
	
	public static void assertEqualsAccount (Account expected, Account actual) {
		assertEqualsNulls( "Account", expected, actual);
		if (expected != null ) {
			Assertions.assertEquals(expected.getId(), actual.getId(),"Id");
			Assertions.assertEquals(expected.getDomain(), actual.getDomain(),"Domain");
			Assertions.assertEquals(expected.getCode(), actual.getCode(),"Code");
			Assertions.assertEquals(expected.getDescription(), actual.getDescription(),"Description");
			Assertions.assertEquals(expected.getAlias(), actual.getAlias(),"Alias");
			Assertions.assertEquals(expected.isEntryEnabled(), actual.isEntryEnabled(),"EntryEnabled");
			Assertions.assertEquals(expected.getLevel(), actual.getLevel(),"Level");
			Assertions.assertEquals(expected.isActive(), actual.isActive(),"Active");
			Assertions.assertEquals(expected.getCostCenter(), actual.getCostCenter(),"CostCenter");
		}
	}
}
