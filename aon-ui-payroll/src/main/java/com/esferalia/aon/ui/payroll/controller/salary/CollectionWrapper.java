package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import com.code.aon.common.enumeration.IResourceable;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

public class CollectionWrapper<T> {
	
	private List<T> collection;
	private IResourceable resourceable;
	
	public CollectionWrapper(IResourceable resourceable) {
		this.resourceable = resourceable;
		
		this.collection = new LinkedList<T>();
	}
	
	public String getName(){
		Locale locale = getLocale();
		return resourceable.getName(locale);
	}
	
	public int getCount() {
		return collection.size();
	}
	
	public int getSize() {
		return collection.size();
	}
	
	public void add( T e ) {
		collection.add(e);
	}

	public List<T> getList() {
		return collection;
	}

	public Collection<T> getCollection() {
		return collection;
	}
	
	private Locale getLocale() {
		return FacesContext.getCurrentInstance().getViewRoot().getLocale();
	}
	
	
}
