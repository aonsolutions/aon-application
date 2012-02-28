package com.esferalia.aon.ui.payroll.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.company.Enterprise;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;

public class ReportUtils {

	
	public static final <T> SortedSet<T> sort ( Set<T> set, String property){
		Comparator<T> comparator = 
				new BeanComparator(property);
		SortedSet<T> sortedSet = new TreeSet<T>(comparator);
		sortedSet.addAll(set);
		return sortedSet;
	}

	public static final <T> SortedSet<T> sort ( Set<T> set, Comparator<T> comparator){
		SortedSet<T> sortedSet = 
				new TreeSet<T>(comparator);
		sortedSet.addAll(set);
		return sortedSet;
	}
	
	public static final <T> T first( Set<T> set, String property, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		for (T t : set) {
			if ( value == PropertyUtils.getProperty(t, property) ){
				return t;
			}
		}
		return null;
		
	}
	
	
}
