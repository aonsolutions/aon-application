/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.ACCESS_POLICY;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.manager.enumeration.AccessPolicyType;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 10-mar-20059
 * @since 1.0
 *
 */
@EntryObject(mainObjectClass=ACCESS_POLICY, objectClasses={TOP})
public class AccessPolicy implements ILdapTransferObject {

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
	private boolean exceptionThrowableIfMaximumExceeded;
	
	public AccessPolicy() {
		this.commonName = AccessPolicyType.CONCURRENT.getName();
		this.exceptionThrowableIfMaximumExceeded = true;
		this.maxAllowedUsers = 999;
		this.maxDefinedUsers = 999;
		this.maxSessions4User = 999;
	}

	@Id
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
	@Attribute(name= COMMON_NAME_ATTRIBUTE, nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}	

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AccessPolicy o = (AccessPolicy) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.exceptionThrowableIfMaximumExceeded, o.exceptionThrowableIfMaximumExceeded)
				.append(this.maxAllowedUsers, o.maxAllowedUsers)				
				.append(this.maxDefinedUsers, o.maxDefinedUsers)
				.append(this.maxSessions4User, o.maxSessions4User)				
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(exceptionThrowableIfMaximumExceeded)
			.append(maxAllowedUsers)	
			.append(maxDefinedUsers)			
			.append(maxSessions4User)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}