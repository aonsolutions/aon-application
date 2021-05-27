package com.esferalia.aon.payroll.calculator.sql;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.esferalia.aon.payroll.calculator.AbstractIterator;

public class FilterCollection<E> extends AbstractIterator<E> {

	public interface Filter<E> {
		boolean accept(E e);
	}

	private FilterCollection.Filter<E> filter;
	private Iterator<E> iterator;

	private E next;

	public FilterCollection(FilterCollection.Filter<E> filter, Collection<E> collection) {
		this.filter = filter;
		this.iterator = collection.iterator();
	}

	@Override
	public boolean hasNext() {
		if (next == null) {
			next = _next();
		}
		return next != null;
	}

	@Override
	public E next() {
		if (hasNext()) {
			E e = next;
			next = null;
			return e;
		} else {
			throw new NoSuchElementException();
		}
	}

	private E _next() {
		while (iterator.hasNext()) {
			E e = (E) iterator.next();
			if (filter.accept(e)) {
				return e;
			}
		}
		return null;
	}

}