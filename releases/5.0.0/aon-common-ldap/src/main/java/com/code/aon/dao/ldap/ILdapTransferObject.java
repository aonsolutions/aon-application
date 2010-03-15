package com.code.aon.dao.ldap;

import javax.naming.Name;

import com.code.aon.common.ITransferObject;

/**
 * Interface for ITransferObject from LDAP.
 * 
 * @author atellitu
 *
 */
public interface ILdapTransferObject extends ITransferObject {
	
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
