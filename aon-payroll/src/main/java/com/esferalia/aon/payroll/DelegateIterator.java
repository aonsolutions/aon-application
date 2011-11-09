package com.esferalia.aon.payroll;

import java.util.Iterator;

public class DelegateIterator<E> implements Iterator<E> {
	
	private Iterator<E> iterator;
	
	public DelegateIterator(Iterator<E> iterator) {
		this.iterator = iterator;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
		
	}
	
}
