package com.code.aon.ui.common.io;

/**
 * @author ecastellano
 * 
 */
public class AonFile {

	private byte[] data;

	private String fileName;

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
		return this.toString();
	}

}


