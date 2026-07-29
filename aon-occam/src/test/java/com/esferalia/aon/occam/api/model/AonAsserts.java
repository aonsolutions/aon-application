package com.esferalia.aon.occam.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.fail;

import java.util.Comparator;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class AonAsserts {
	private static final String[] SKIP_FIELDS = new String[] {
		 ".*invoiceAddress.*parent"
		,".*creationDate"
		,".*creationUser"
		,".*modificationDate"
		,".*modificationUser"
		,".*dirtySet"
		,".*selected"
	};
	
	private static final Comparator<Date> SAME_DAY = (d1, d2) -> AonDateUtils.isSameDay(d1 , d2) ? 0 : 1;
	
	static <T> void assertEqualsNulls(T expected, T actual, String msg) {
		if ( expected == null) assertNull(actual, msg);
		if ( expected != null) assertNotNull(actual, msg);
	}
	
	private static <T> void assertAon(T expected, T actual, String[] skipFields, String className) {
		if (AonCollectionUtils.isEmpty(skipFields)) {
			skipFields = SKIP_FIELDS;
		} else {
			String[] result = new String[SKIP_FIELDS.length+skipFields.length];
		    int i;
		    for (i=0; i<SKIP_FIELDS.length; i++) result[i] = SKIP_FIELDS[i];
		    int tempIndex =SKIP_FIELDS.length; 
		    for (i=0; i<skipFields.length; i++) result[tempIndex+i] = skipFields[i];
		    skipFields = result; 
		}
		assertEqualsNulls(expected, actual, className);
		if (expected != null) {
			assertThat(expected)
				.usingRecursiveComparison( )
				.ignoringFieldsMatchingRegexes(skipFields)
				.withComparatorForType(SAME_DAY, Date.class)
			.isEqualTo(actual);
		}
	}
	public static <T> void assertClassEquals(T expected, T actual) {
		assertClassEquals(expected, actual, null);	
	}
	public static <T> void assertClassEquals(T expected, T actual, String[] skipFields) {
		String className = "[NULL CLASSES]";
		if (expected != null) className = expected.getClass().getSimpleName(); 
		else if (actual != null) className = actual.getClass().getSimpleName();
		AonAsserts.assertAon(expected, actual, skipFields, className);
	}
	
	public static void assertNotEmptyKeys(String parent, JSONObject json) {
		if ( AonCollectionUtils.isEmpty( json.keySet() )) {
			fail(parent + " is empty");
		}
		for (String name : json.keySet()) {
			String current = parent + ">" + name;
			JSONArray a = json.optJSONArray(name);
			if (a != null) {
				if ( a.length() == 0 ) {
					fail("Array ..: " + current + " is empty");		
				} 
				for ( int i = 0; i < a.length(); i++) {
					Object arrItem = a.get(i);
					if (arrItem != null && arrItem.getClass().isAssignableFrom( JSONObject.class)) {
						assertNotEmptyKeys( current + "["+i+"]" , a.getJSONObject(i));
					}
				}
			} else {
				JSONObject j = json.optJSONObject(name);
				if (j != null) {
					assertNotEmptyKeys( current , j);
				}
			}
		}
		
	}
	
}
