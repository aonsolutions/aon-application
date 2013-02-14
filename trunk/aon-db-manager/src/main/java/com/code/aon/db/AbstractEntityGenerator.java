package com.code.aon.db;

import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;

public abstract class AbstractEntityGenerator<E> implements IEntityGenerator<E> {

	private int maxResults;
	
	public Iterator<E> iterator() {
		Iterator<E> it = new ResultIterator();
		return it;
	}

	public int getMaxResults() {
		return maxResults;
	}
	
	@Override
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	private class ResultIterator implements Iterator<E> {

		private int index;
		
		public boolean hasNext() {
			return index < getMaxResults();
		}

		public E next() {
			E element = nextObject();
			index++;
			return element;
		}

		public void remove() {
			 throw new NotImplementedException("Not implemented");				
		}
		
	}
	
}
