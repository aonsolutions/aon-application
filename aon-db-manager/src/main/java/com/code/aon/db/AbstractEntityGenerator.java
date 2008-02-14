package com.code.aon.db;

import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

public abstract class AbstractEntityGenerator<E> extends AbstractEntityIterable<E> {

	protected abstract Object nextObject();

	public Iterator<E> iterator() {
		Iterator<E> it = new ResultIterator();
		return it;
	}
	
	@Override
	public Session getSession() {
		return null;
	}

	private class ResultIterator implements Iterator<E> {

		private int index;
		
		public boolean hasNext() {
			return index < getMaxResults();
		}

		public E next() {
			E element = (E) nextObject();
			index++;
			return element;
		}

		public void remove() {
			 throw new NotImplementedException("Not implemented");				
		}
		
	}
	
}
