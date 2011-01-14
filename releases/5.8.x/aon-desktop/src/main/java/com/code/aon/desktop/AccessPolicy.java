/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.desktop;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.IAonObjectClasses;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 10-mar-20059
 * @since 1.0
 *
 */
@EntryObject(mainObjectClass=IAonObjectClasses.ACCESS_POLICY, objectClasses={IAonObjectClasses.TOP})
public class AccessPolicy implements ITransferObject {

	private static final long serialVersionUID = -9075569722189572290L;

	/** Security domain identifier. This can be Nominal or Concurrent. */
	private Name id;
	
	private String commonName;	

	/** Maximum number of users defined for the domain. */
	private Integer maxDefinedUsers;

	/** Maximun number of users allowed to access application in each domain. */
	private Integer maxAllowedUsers;

	/** Maximum number of sessions that a user has for the domain. */
	private Integer maxSessions4User;

	/** If the maximum sessions for user has exceeded has to throw an exception. */
	private boolean exceptionThrowableIfMaximumExceeded = true;

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)	
	public Name getId() {
		return id;
	}
	
	/**
	 * @param id The id to set.
	 */
	public void setId(Name id) {
		this.id = id;
	}

	@RDN
	@Attribute(name="cn",nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}	

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#getMaxAllowedUsers()
	 */
	@Attribute(name="maxAllowedUsers",nullable=false)
	public Integer getMaxAllowedUsers() {
		return maxAllowedUsers;
	}
	
	/**
	 * @param maxAllowedUsers the maxAllowedUsers to set
	 */
	public void setMaxAllowedUsers(Integer maxAllowedUsers) {
		this.maxAllowedUsers = maxAllowedUsers;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#getMaxDefinedUsers()
	 */
	@Attribute(name="maxDefinedUsers",nullable=false)
	public Integer getMaxDefinedUsers() {
		return maxDefinedUsers;
	}
	
	/**
	 * @param maxDefinedUsers the maxDefinedUsers to set
	 */
	public void setMaxDefinedUsers(Integer maxDefinedUsers) {
		this.maxDefinedUsers = maxDefinedUsers;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#getMaxSessions4User()
	 */
	@Attribute(name="maxSessions4User",nullable=false)
	public Integer getMaxSessions4User() {
		return maxSessions4User;
	}
	
	/**
	 * @param maxSessions4User the maxSessions4User to set
	 */
	public void setMaxSessions4User(Integer maxSessions4User) {
		this.maxSessions4User = maxSessions4User;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAccessPolicy#isExceptionThrowableIfMaximumExceeded()
	 */
	@Attribute(name="exceptionThrowableIfMaximumExceeded",nullable=false)
	public boolean isExceptionThrowableIfMaximumExceeded() {
		return exceptionThrowableIfMaximumExceeded;
	}
	
	/**
	 * @param exceptionThrowableIfMaximumExceeded the exceptionThrowableIfMaximumExceeded to set
	 */
	public void setExceptionThrowableIfMaximumExceeded(boolean exceptionThrowableIfMaximumExceeded) {
		this.exceptionThrowableIfMaximumExceeded = exceptionThrowableIfMaximumExceeded;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Domain) {
			AccessPolicy o = (AccessPolicy) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.id != null) ? id.hashCode() : super.hashCode();
	}	
		
}