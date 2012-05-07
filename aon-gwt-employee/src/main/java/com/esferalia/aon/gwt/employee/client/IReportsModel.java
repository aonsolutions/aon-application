package com.esferalia.aon.gwt.employee.client;

public interface IReportsModel<E> {
	
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
