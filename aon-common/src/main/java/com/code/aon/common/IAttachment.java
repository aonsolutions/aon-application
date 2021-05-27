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
     * Return the domain's identifier.
     *
     * @return The identifier.
     */
	int getDomain();

    /**
     * Assign the domain's identifier.
     * 
     * @param domain The domain's identifier.
     */
    void setDomain(int domain);

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
     * Return the Google Drive UID.
     * 
     * @return The Google Drive UID. 
     */
	String getDriveId();

    /**
     * Assign the Google Drive UID.
     * 
     * @param uid The UID.
     */
	void setDriveId(String uid);

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
	
    String getAonType();
    void setAonType(String aonType);

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
