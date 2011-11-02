package com.code.aon.ui.document;

import org.alfresco.webservice.types.Reference;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.MimeType;

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
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();
	
	/**
	 * Gets the mime type.
	 *
	 * @return the mime type
	 */
	MimeType getMimeType();

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	byte[] getData();
	
}
