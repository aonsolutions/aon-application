package com.esferalia.aon.payroll.irpf.sql;

import java.util.Iterator;
import com.esferalia.aon.payroll.calculator.AbstractIterator;

public abstract class HierarchyMonthIterator<E> extends AbstractIterator<E> {

	
	private E next; 
	private int level;
	private Iterator<E> childs [];
	

	public HierarchyMonthIterator(Iterator<E> ... childs) {
		this.childs = childs;
		this.next = null;
	}
	
	@Override
	public boolean hasNext() {
		if ( next != null ){
			return true;
		}
		if ( isAfterLast() ){
			return false;
		}
		if ( ! childHasNext() ){
			level++;
		} 
		else {
			next = childNext();
		}
		return hasNext();
	}

	@Override
	public E next() {
		E e = next;
		next = null;
		return e;
	}
	
	private E childNext() {
		return next(childs[level].next()); 
	}
	
	private boolean childHasNext() {
		return childs[level].hasNext() ;
	}

	private boolean isAfterLast() {
		return level >= childs.length ;
	}
	
	protected int getLevel() {
		return level;
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	protected abstract E next(E e);
}
