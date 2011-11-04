package com.code.aon.document;

import com.code.aon.common.enumeration.MimeType;

/**
 * Interface for documents from Alfresco.
 * 
 * @author atellitu
 *
 */
public interface IAlfrescoDocument extends IAlfrescoTransferObject {
	
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
