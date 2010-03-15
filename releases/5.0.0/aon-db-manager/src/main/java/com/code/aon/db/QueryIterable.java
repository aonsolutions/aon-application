package com.code.aon.db;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryIterable<E> implements Iterable<E> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryIterable.class.getName());
	
	private int maxResults;
	
	private Class<?> entity;
	
	private Integer rowCount;
	
	private boolean asElement;
	
	private SessionFactory sessionFactory;
	
	private Session session;
	
	private Session dom4jSession;
	
	private Criteria criteria;
	
	public QueryIterable() {
		this.maxResults = HibernateDataManager.DEFAULT_MAX_EXPORT;
	}
	
	private Class<?> getEntity() {
		return entity;
	}

	public void setEntity(Class<?> entity) {
		this.entity = entity;
		this.rowCount = CountComparator.getRowCount(sessionFactory, entity);
	}

	private int getMaxResults() {
		return maxResults;
	}
	
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public void setAsElement(boolean asElement) {
		this.asElement = asElement;
	}
	
	private boolean isAsElement() {
		return asElement;
	}
	
	public Iterator<E> iterator() {
		Iterator<E> it = new ResultIterator();
		return it;
	}
	
	public SessionFactory getSessionFactory() {
		if ( sessionFactory != null ) {
			return sessionFactory;
		}
		return session.getSessionFactory();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		if ( this.session == null ) {
			this.session = getSessionFactory().openSession();
		}
		return this.session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	private void clearSession() {
		if ( dom4jSession != null ) {
			dom4jSession.clear();
		}
		this.session.clear();
	}

	private void closeSession() {
		if ( this.sessionFactory != null ) {
			this.session.close();
		}
		this.session = null;
		this.criteria = null;
	}

	private void orderById() {
		ClassMetadata cmd = getSessionFactory().getClassMetadata( getEntity() );
		String id = cmd.getIdentifierPropertyName();
		getCriteria().addOrder( Order.asc(id) );
	}
	
	private Criteria createCriteria() {
		Criteria criteria = null;
		if ( isAsElement() ) {
			dom4jSession = getSession().getSession(EntityMode.DOM4J);
			criteria = dom4jSession.createCriteria( getEntity() );
		} else {
			criteria = getSession().createCriteria( getEntity() );
		}
		ResultTransformer ert = new ExportResultTransformer(getEntity());
		criteria.setResultTransformer(ert);
		return criteria;
	}
	
	public Criteria getCriteria() {
		if ( criteria == null ) {
			criteria = createCriteria();
		}
		return criteria;
	}
	
	public Integer getRowCount() {
		return this.rowCount;
	}
	
	private class ResultIterator implements Iterator<E> {

		private int index;
		
		private int offset;
		
		private boolean orderAdded;
		
		private List<E> results;
		
		public ResultIterator() {
			updateResults();
		}
		
		@SuppressWarnings("unchecked")
		private void updateResults() {
			updateCriteria();
			results = getCriteria().list();
			LOGGER.info( "List returned: " + results.size() + " for " + getEntity() );
			index = ( results.size() == 0 ) ? -1 : 0;
		}
		
		private void updateCriteria() {
			if ( (getRowCount() == null) || (getRowCount() > getMaxResults()) ) {
				if (! orderAdded ) {
					orderById();
					this.orderAdded = true;
				}
				getCriteria().setMaxResults( getMaxResults() );			
				getCriteria().setFirstResult( offset );
			}
		}
		
		public boolean hasNext() {
			boolean next = false;
			if ( this.index != -1 ) {
				next = true;
				if ( this.index == results.size() ) {
					if ( this.index < getMaxResults() ) {
						next = false;
					} else {
						offset += results.size();
						clearSession();
						updateResults();
						next = (index != -1);
					}
				}
			}
			if (! next ) {
				this.results = null;
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
