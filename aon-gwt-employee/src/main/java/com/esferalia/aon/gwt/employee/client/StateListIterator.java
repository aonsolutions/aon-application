package com.esferalia.aon.gwt.employee.client;
import java.util.ListIterator;


public class StateListIterator<E> implements ListIterator<E> {
	
	private E current;
	private ListIterator<E> listIterator;
	
	
	public StateListIterator(ListIterator<E> listIterator) {
		this.listIterator = listIterator;
	}
	
	public E current(){
		return  current;
	}
	
	@Override
	public boolean hasNext() {
		return listIterator.hasNext();
	}

	@Override
	public E next() {
		current = listIterator.next();
		return current;
	}

	@Override
	public boolean hasPrevious() {
		return listIterator.hasPrevious();
	}

	@Override
	public E previous() {
		current = listIterator.previous();
		return current;
	}

	@Override
	public int nextIndex() {
		return listIterator.nextIndex();
	}

	@Override
	public int previousIndex() {
		return listIterator.previousIndex();
	}

	@Override
	public void remove() {
		listIterator.remove();
	}

	@Override
	public void set(E e) {
		listIterator.set(e);
		current = e;
	}

	@Override
	public void add(E e) {
		listIterator.add(e);
	}

}
