package com.code.aon.common.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


/**
 * The Class DefaultSessionManager.
 */
public class BasicSessionManager implements ISessionManager {
	
	private SessionFactory sessionFactory;
	
	private Session session;
	
	private boolean mustBeginTransaction;
	
	private boolean mustCloseSession;

	/**
	 * Instantiates a new default session manager.
	 * 
	 * @param sessionFactory the session factory name
	 */
	public BasicSessionManager(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.mustBeginTransaction = true;
		this.mustCloseSession = true;
	}

	@Override
	public void closeSession() {
		this.session.close();
	}

	@Override
	public Session getSession() {
		this.session = this.sessionFactory.openSession();
		return this.session;
	}

	@Override
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}
	
	/**
	 * Sets the session factory.
	 * 
	 * @param sessionFactory the new session factory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	

	@Override
	public boolean mustBeginTransaction() {
		return this.mustBeginTransaction;
	}

	@Override
	public boolean mustCloseSession() {
		return this.mustCloseSession;
	}
	
	/**
	 * Sets the must begin transaction.
	 * 
	 * @param mustBeginTransaction the new must begin transaction
	 */
	public void setMustBeginTransaction(boolean mustBeginTransaction) {
		this.mustBeginTransaction = mustBeginTransaction;
	}

	/**
	 * Sets the must close session.
	 * 
	 * @param mustCloseSession the new must close session
	 */
	public void setMustCloseSession(boolean mustCloseSession) {
		this.mustCloseSession = mustCloseSession;
	}

}
