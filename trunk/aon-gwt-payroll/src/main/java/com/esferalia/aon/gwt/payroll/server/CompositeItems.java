package com.esferalia.aon.gwt.payroll.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.payroll.calculator.CompositeCollection;

public class CompositeItems<T extends Item<?>> extends CompositeCollection<T> {
	
	private class MyPredicate implements Predicate {
		
		Set<Integer> ids = new HashSet<Integer>();
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean evaluate(Object obj) {
			return ids.add(((T)obj).getId());
		}
	}
	
	public CompositeItems(Collection<T> ...collections) {
		super(collections);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return new FilterIterator(super.iterator(), new MyPredicate());
	}
	
	
	
}

