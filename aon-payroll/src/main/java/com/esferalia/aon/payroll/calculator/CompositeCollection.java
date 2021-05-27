package com.esferalia.aon.payroll.calculator;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CompositeCollection<E> extends AbstractCollection<E> {

	
	private Collection<E> collections [];
	
	public CompositeCollection(Collection<E> ... collections) {
		this.collections = collections;
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return new CompositeIterator<E>(getIterators());
	}

	@Override
	public int size() {
		int size = 0;
		for (int i = 0; i < collections.length; i++) {
			size += collections[i].size();		
		}
		return size;
	}
	
	protected List<Iterator<E>> getIterators () {
		List<Iterator<E>> iterators = 
				new ArrayList<Iterator<E>>(collections.length);
		for (int i = 0; i < collections.length; i++) {
			iterators.add(collections[i].iterator());
		}
		return iterators;
		
	}
	
	protected static class CompositeIterator<E> implements Iterator<E> {
		
		int level ;
		private List<Iterator<E>> iterators ;

		
		public CompositeIterator(List<Iterator<E>> iterators) {
			this.level = 0;
			this.iterators = iterators;
		}

		@Override
		public boolean hasNext() {
			for ( ; level < iterators.size(); level++){
				if ( iterators.get(level).hasNext() ){
					return true;
				}
			}
			return false;
		}

		@Override
		public E next() {
			return iterators.get(level).next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		
	}
	
}
