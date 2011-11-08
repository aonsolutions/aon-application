package com.code.aon.document;

import org.alfresco.webservice.types.Reference;

import com.code.aon.common.ITransferObject;

/**
 * Interface for ITransferObject from LDAP.
 * 
 * @author atellitu
 *
 */
public interface IAlfrescoTransferObject extends ITransferObject {
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	Reference getId();
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	void setId( Reference id );
	
}
