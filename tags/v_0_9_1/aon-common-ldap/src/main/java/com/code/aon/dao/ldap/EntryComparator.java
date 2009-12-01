package com.code.aon.dao.ldap;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.ldap.Entry;

public class EntryComparator implements Comparator<Entry> {
	
	private String name;
	
	private boolean ascending;
	
	public EntryComparator(String name, boolean ascending) {
		this.name = name;
		this.ascending = ascending;
	}

	private Object getValue( Entry entry ) {
		if ( entry.containsKey(name) ) {
			return entry.getAsObject(name);
		}
		return null;
	}
	
	@Override
	public int compare(Entry o1, Entry o2) {
		String value1 = ObjectUtils.toString( getValue(o1) );
		String value2 = ObjectUtils.toString( getValue(o2) );
		int result = 0;
		if ( ascending ) {
			result = value1.compareTo(value2);
		} else {
			result = value2.compareTo(value1);
		}
		return result;
	}

}
