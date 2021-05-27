package com.esferalia.aon.payroll;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public class DelegateCollection<E> extends AbstractCollection<E> {

	private Collection<E> collection;
	
	public DelegateCollection(Collection<E> collection) {
		this.collection = collection;
	}
	
	@Override
	public int size() {
		return collection.size();
	}
	
	@Override
	public Iterator<E> iterator() {
		return collection.iterator();
	}
	
	protected Collection<E> getCollection() {
		return collection;
	}
}
