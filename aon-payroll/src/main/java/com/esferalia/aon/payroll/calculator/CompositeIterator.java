package com.esferalia.aon.payroll.calculator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CompositeIterator<E> extends AbstractIterator<E> {
	
	private int child; 
	private Iterator<E> childs [];
	
	
	public CompositeIterator(Iterator<E> ... childs) {
		this.child = 0;
		this.childs = childs;
	}
	
	@Override
	public boolean hasNext() {
		if ( child >= childs.length ){
			return false;
		}
		
		if ( childs[child].hasNext()) {
			return true;
		}
		child++;
		return hasNext();
	}

	@Override
	public E next() {
		try {
			return childs[child].next();
		} catch ( IndexOutOfBoundsException e ) {
			throw new NoSuchElementException(e.getMessage());
		}
	}

}
