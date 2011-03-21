package com.esferalia.aon.payroll.calculator;

import java.util.Iterator;

public abstract class HierarchyIterator<E> extends AbstractIterator<E> {

	
	private E next; 
	private int cur; 
	
	private Iterator<E> childs [];
	

	public HierarchyIterator(Iterator<E> ... childs) {
		this.childs = childs;
		this.cur = 0;
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
			cur++;
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
		return next(childs[cur].next()); 
	}
	
	private boolean childHasNext() {
		return childs[cur].hasNext() ;
	}

	private boolean isAfterLast() {
		return cur >= childs.length ;
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	protected abstract E next(E e);
}
