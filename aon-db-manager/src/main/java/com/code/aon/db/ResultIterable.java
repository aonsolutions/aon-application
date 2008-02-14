package com.code.aon.db;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.metadata.ClassMetadata;

public class ResultIterable<E> extends AbstractEntityIterable<E> {

	private Session session;
	
	public Iterator<E> iterator() {
		Iterator<E> it = new ResultIterator();
		return it;
	}
	
	public Session getSession() {
		if ( session == null ) {
			session = getSessionFactory().openSession();
		}
		return session;
	}

	private void closeSession() {
		session.close();
		session = null;
	}
	
	private class ResultIterator implements Iterator<E> {

		private int index;
		
		private int offset;
		
		private List<E> results;
		
		public ResultIterator() {
			updateResults();
		}
		
		private void updateResults() {
			results = createCriteria().list();
			index = ( results.size() == 0 ) ? -1 : 0;
		}
		
		private void addOrder( Criteria criteria ) {
			ClassMetadata cmd = getSessionFactory().getClassMetadata( getEntity() );
			String id = cmd.getIdentifierPropertyName();
			criteria.addOrder( Order.asc(id) );
		}
		
		private Criteria createCriteria() {
			Criteria criteria = null;
			if ( isAsElement() ) {
				Session dom4jSession = getSession().getSession(EntityMode.DOM4J);
				criteria = dom4jSession.createCriteria( getEntity() );
			} else {
				criteria = getSession().createCriteria( getEntity() );
			}
			criteria.setFirstResult( offset );
			criteria.setMaxResults( getMaxResults() );
			addOrder( criteria );
			return criteria;
		}
		
		public boolean hasNext() {
			boolean next = false;
			if ( this.index != -1 ) {
				next = true;
				if ( this.index == results.size() ) {
					if ( this.index < getMaxResults() ) {
						next = false;
					} else {
						closeSession();
						offset += results.size();
						updateResults();
						next = (index != -1);
					}
				}
			}
			if (! next ) {
				closeSession();
			}
			return next;
		}
		
		private List<E> getResults() {
			if ( this.results == null ) {
				updateResults();
			}
			return this.results;
		}

		public E next() {
			E element = null;
			if ( this.index != -1 ) {
				element = getResults().get(index++);
			}
			return element;
		}

		public void remove() {
			 throw new NotImplementedException("Not implemented");				
		}
		
	}
	
}
