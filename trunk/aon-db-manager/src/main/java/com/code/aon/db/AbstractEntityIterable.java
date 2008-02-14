package com.code.aon.db;

import org.hibernate.SessionFactory;

public abstract class AbstractEntityIterable<E> implements IEntityIterable<E> {

	private SessionFactory sessionFactory;

	private int maxResults;
	
	private Class entity;
	
	private boolean asElement;

	public AbstractEntityIterable() {
		this.maxResults = HibernateDataManager.DEFAULT_MAX_EXPORT;
	}
	
	@Override
	public void flushed() {
	}


	public Class getEntity() {
		return entity;
	}
	
	@Override
	public void setEntity(Class entity) {
		this.entity = entity;
	}

	public int getMaxResults() {
		return maxResults;
	}
	
	@Override
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public boolean isAsElement() {
		return asElement;
	}
	
	@Override	
	public void setAsElement(boolean asElement) {
		this.asElement = asElement;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
