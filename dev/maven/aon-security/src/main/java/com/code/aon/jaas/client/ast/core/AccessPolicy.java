/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.jaas.client.ast.core;

import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IAccessPolicy;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 10-mar-2005
 * @since 1.0
 *
 */

public class AccessPolicy implements IAccessPolicy {

	private static final long serialVersionUID = 8637253952903616553L;

	/** Security domain identifier. This can be Nominal or Concurrent. */
	private String id;

	/** Maximum number of users defined for the domain. */
	int maxDefinedUsers;
	/** Maximun number of users allowed to access application in each domain. */
	int maxAllowedUsers;
	/** Maximum number of sessions that a user has for the domain. */
	int maxSessions4User;
	/** If the maximum sessions for user has exceeded has to throw an exception. */
	boolean exceptionThrowableIfMaximumExceeded = true;

	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param maxAllowedUsers the maxAllowedUsers to set
	 */
	public void setMaxAllowedUsers(int maxAllowedUsers) {
		this.maxAllowedUsers = maxAllowedUsers;
	}

	/**
	 * Este método es debido a problemas que tiene la clase <code>Digester</code> para conseguir
	 * un tipo primitivo cuando se trata de un  valor y no de un atributo.
	 * 
	 * @param maxUsers The maxUsers to set.
	 */
	public void setMaxAllowedUsers(String maxUsers) {
		this.maxAllowedUsers = Integer.parseInt(maxUsers);
	}

	/**
	 * @param maxDefinedUsers the maxDefinedUsers to set
	 */
	public void setMaxDefinedUsers(int maxDefinedUsers) {
		this.maxDefinedUsers = maxDefinedUsers;
	}

	/**
	 * Este método es debido a problemas que tiene la clase <code>Digester</code> para conseguir
	 * un tipo primitivo cuando se trata de un  valor y no de un atributo.
	 * 
	 * @param maxUsers The maxUsers to set.
	 */
	public void setMaxDefinedUsers(String maxUsers) {
		this.maxDefinedUsers = Integer.parseInt(maxUsers);
	}

	/**
	 * @param maxSessions4User the maxSessions4User to set
	 */
	public void setMaxSessions4User(int maxSessions4User) {
		this.maxSessions4User = maxSessions4User;
	}

	/**
	 * Este método es debido a problemas que tiene la clase <code>Digester</code> para conseguir
	 * un tipo primitivo cuando se trata de un  valor y no de un atributo.
	 * 
	 * @param maxUsers The maxUsers to set.
	 */
	public void setMaxSessions4User(String maxUsers) {
		setMaxSessions4User( Integer.parseInt(maxUsers) );
	}

	/**
	 * @param exceptionThrowableIfMaximumExceeded the exceptionThrowableIfMaximumExceeded to set
	 */
	public void setExceptionThrowableIfMaximumExceeded(boolean exceptionThrowableIfMaximumExceeded) {
		this.exceptionThrowableIfMaximumExceeded = exceptionThrowableIfMaximumExceeded;
	}

	/**
	 * Este método es debido a problemas que tiene la clase <code>Digester</code> para conseguir
	 * un tipo primitivo cuando se trata de un  valor y no de un atributo.
	 */
	public void setExceptionThrowableIfMaximumExceeded(String exceptionThrowableIfMaximumExceeded) {
		this.exceptionThrowableIfMaximumExceeded = Boolean.parseBoolean( exceptionThrowableIfMaximumExceeded );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IPolicy#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#getMaxAllowedUsers()
	 */
	public int getMaxAllowedUsers() {
		return maxAllowedUsers;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#getMaxDefinedUsers()
	 */
	public int getMaxDefinedUsers() {
		return maxDefinedUsers;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#getMaxSessions4User()
	 */
	public int getMaxSessions4User() {
		return maxSessions4User;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#isExceptionThrowableIfMaximumExceeded()
	 */
	public boolean isExceptionThrowableIfMaximumExceeded() {
		return exceptionThrowableIfMaximumExceeded;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
		visitor.visitAccessPolicy(this);
	}

	/**
	 * @return the default IAccessPolicy interface.
	 */
	public static final IAccessPolicy getInstance() {
		AccessPolicy ap = new AccessPolicy();
		ap.setId( IAccessPolicy.ACCESS_POLICY[0] );
		ap.setMaxDefinedUsers(-1);
		ap.setMaxAllowedUsers(-1);
		ap.setMaxSessions4User(1);
		return ap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IAccessPolicy: " + this.id 
			+ " [Defined:" + this.maxDefinedUsers 
			+ " Allowed:" + this.maxAllowedUsers 
			+ " Sessions4User:" + this.maxSessions4User 
			+ " ExceptionIfMaximumExceeded:" + this.exceptionThrowableIfMaximumExceeded + "]";
	}

}