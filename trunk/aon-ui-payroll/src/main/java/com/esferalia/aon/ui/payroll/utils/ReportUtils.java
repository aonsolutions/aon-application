package com.esferalia.aon.ui.payroll.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRRenderable;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.type.OrderedSetType;

import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class ReportUtils {
	
	public static final <T> List <T> sort ( Collection<T> collection, String property){
		List<T> list = new LinkedList<T>(collection);
		Comparator<T> comparator = 
				new BeanComparator(property);
		Collections.sort(list, comparator);
		return list;
	}

	public static final <T> List <T> sort ( Collection<T> collection, String... properties){
		List<T> list = new LinkedList<T>(collection);
		Comparator<T> comparators [] = 
				new Comparator [properties.length];
		for (int i = 0; i < properties.length; i++) {
			comparators[i] = new BeanComparator(properties[i]);
			
		}
		Comparator<T> comparator = 
				new ChainedComparator<T>(comparators);
		Collections.sort(list, comparator);
		return list;
	}

	public static final <T> List<T> sort ( Collection<T> collection, Comparator<T> comparator){
		List<T> list = new LinkedList<T>(collection);
		Collections.sort(list, comparator);
		return list;
	}
	
	public static final <T> T first( Collection<T> collection, String property, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		for (T t : collection) {
			if ( value == PropertyUtils.getProperty(t, property) ){
				return t;
			}
		}
		return null;
		
	}
	

	public static final <T> List<T> reduce( Collection<T> collection, String property, Object... values) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		List<T> list = new LinkedList<T>();
		for (T t : collection) {
			Object value = PropertyUtils.getProperty(t, property);
			if ( contains(values, value ) ){
				list.add(t);
			}
		}
		Comparator<T> comparator = 
				new BeanComparator(property);
		Collections.sort(list, comparator);
		return list;
	}

	public static JRRenderable getRenderer(RegistryAttachment rattach) {
		System.out.println(rattach.getMimeType() + "-" + rattach.getId() );
		return JRImageRenderer.getInstance(rattach.getData());
	}
	
	private static boolean contains ( Object values [] , Object value ){
		for (int i = 0; i < values.length; i++) {
			if ( values[i] == value )
				return true ;
		}
		return false;
	}
	
	private static class ChainedComparator<T> implements Comparator<T>{
	 
		private Comparator<T> simpleComparators []; 
	    
		public ChainedComparator(Comparator<T>... simpleComparators) {
	        this.simpleComparators = simpleComparators;
	    }
	    
	    public int compare(T o1, T o2) {
	        for (Comparator<T> comparator : simpleComparators) {
	            int result = comparator.compare(o1, o2);
	            if (result != 0) {
	                return result;
	            }
	        }
	        return 0;
	    }
	}	
	
}
