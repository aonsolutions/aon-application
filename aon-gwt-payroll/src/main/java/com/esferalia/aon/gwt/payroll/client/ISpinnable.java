package com.esferalia.aon.gwt.payroll.client;

public interface ISpinnable<E> {
	
	E current();
	int currentIndex();
	int size();
	boolean hasPrevious();
	void first();
	void previous();
	void next();
	void last();
	boolean hasNext();
	
}
