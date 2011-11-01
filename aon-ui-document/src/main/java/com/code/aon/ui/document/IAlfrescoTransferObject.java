package com.code.aon.ui.document;

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
	String getId();
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	void setId( String id );
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	String getPath();
	
	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	void setPath( String path );
	
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
