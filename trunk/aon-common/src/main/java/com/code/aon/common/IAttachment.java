package com.code.aon.common;

import com.code.aon.common.enumeration.MimeType;

/**
 * Interface of Attach Objects.
 * 
 * @author Consulting & Development. 
 *
 */

public interface IAttachment extends ITransferObject, Cloneable {

    /**
     * Return the identifier.
     *
     * @return The identifier.
     */
	Integer getId();

    /**
     * Assign the identifier.
     * 
     * @param primaryKey The identifier.
     */
    void setId(Integer primaryKey);

    /**
     * Return the mime type.
     * 
     * @return The mime type. 
     */
	MimeType getMimeType();

    /**
     * Assign the mime type.
     * 
     * @param mimeType The mime type..
     */
    void setMimeType(MimeType mimeType);

    /**
     * Return attach bytes of data.
     *
     * @return The attach bytes of data.
     */
    byte[] getData();

    /**
     * Assign the attach bytes of data.
     * 
     * @param data The attach bytes of data.
     */
    void setData(byte[] data);
	
    /**
     * Return the attach description.
     * 
     * @return The attach description.
     */
	String getDescription();

    /**
     * Assign the attach description.
     * 
     * @param description The attach description..
     */
	void setDescription(String description);

    /**
     * Return the number of bytes of the attach data.
     * 
     * @return The number of bytes of the attach data. 
     */
	Integer getSize();

    /**
     * Assign the number of bytes of the attach data.
     * 
     * @param size The number of bytes of the attach data.
     */
	void setSize(Integer size);

	/**
	 * Clone the attach object.
	 * 
	 * @return The cloned object.
	 * @throws CloneNotSupportedException
	 */
	Object clone() throws CloneNotSupportedException;
	
}
