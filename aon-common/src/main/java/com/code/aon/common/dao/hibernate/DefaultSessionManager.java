package com.code.aon.common.dao.hibernate;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.code.aon.common.AonVersion;

/**
 * The Class DefaultSessionManager.
 */
public class DefaultSessionManager implements ISessionManager, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String sessionFactoryName;

	/**
	 * Instantiates a new default session manager.
	 * 
	 * @param sessionFactoryName the session factory name
	 */
	public DefaultSessionManager(String sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}

	@Override
	public void closeSession() {
		HibernateUtil.closeSession(sessionFactoryName);
	}

	@Override
	public Session getSession() {
		return HibernateUtil.getSession(sessionFactoryName);
	}

	@Override
	public SessionFactory getSessionFactory() {
		return HibernateUtil.getSessionFactory(sessionFactoryName);
	}

	@Override
	public boolean mustBeginTransaction() {
		return HibernateUtil.mustBeginTransaction();
	}

	@Override
	public boolean mustCloseSession() {
		return HibernateUtil.mustCloseSession();
	}
	
	

}
