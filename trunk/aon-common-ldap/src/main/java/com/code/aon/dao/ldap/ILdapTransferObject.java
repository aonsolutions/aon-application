package com.code.aon.dao.ldap;

import javax.naming.Name;

import com.code.aon.common.ITransferObject;
import com.code.aon.ldap.ILdapConstants;

/**
 * Interface for ITransferObject from LDAP.
 * 
 * @author atellitu
 *
 */
public interface ILdapTransferObject extends ITransferObject, ILdapConstants {
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	Name getId();
	
	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	void setId(Name id); 
}
