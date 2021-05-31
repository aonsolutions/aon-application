package com.esferalia.aon.gwt.payroll.tools;

import static org.junit.Assert.assertEquals;

public class TestTools {

	public static <T extends Object> void assertWithLog(String title, String message,T object, T object2) {
		assertEquals("[ " + title + " ] " + message + " || Values [ " + object + " >> " + object2 + " ]", object , object2);
	}
	
	
	
}
