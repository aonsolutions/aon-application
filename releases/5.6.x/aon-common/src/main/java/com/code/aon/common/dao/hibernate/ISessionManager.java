package com.code.aon.common.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


// TODO: Auto-generated Javadoc
/**
 * The Interface ISessionManager.
 */
public interface ISessionManager {

	/**
	 * Gets the session factory.
	 * 
	 * @return the session factory
	 */
	SessionFactory getSessionFactory();
	
	/**
	 * Gets the session.
	 * 
	 * @return the session
	 * @throws HibernateException the hibernate exception
	 */
	Session getSession();
	
	/**
	 * Close session.
	 */
	void closeSession();
	
	/**
	 * Must close session.
	 * 
	 * @return true, if successful
	 */
	boolean mustCloseSession();
	
	/**
	 * Must begin transaction.
	 * 
	 * @return true, if successful
	 */
	boolean mustBeginTransaction();
	
}
