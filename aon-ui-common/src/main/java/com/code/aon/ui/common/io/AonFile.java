package com.code.aon.ui.common.io;

import com.code.aon.common.enumeration.MimeType;

/**
 * @author ecastellano
 * 
 */
public class AonFile {

	private byte[] data;

	private String fileName;
	
	private MimeType mimeType;
	
	private Object key;

	/**
	 * @return byte[]
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data 
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		byte[] data = getData();
		if (data != null) {
			return data.length; 
		}
		return 0;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
	    return fileName;
	}	

	/**
	 * @return the file
	 */

	public String getKey() {
		return (key != null) ? this.key.toString() : this.toString();
	}

	/**
	 * Sets the key.
	 * 
	 * @param key the new key
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	/**
	 * Gets the mime type.
	 * 
	 * @return the mime type
	 */
	public MimeType getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the mime type.
	 * 
	 * @param mimeType the new mime type
	 */
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
	
}