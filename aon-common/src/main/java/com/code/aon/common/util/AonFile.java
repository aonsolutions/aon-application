package com.code.aon.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.enumeration.MimeType;

/**
 * @author ecastellano
 * 
 */
public class AonFile {

	private byte[] data;
	
	private File file;

	private String fileName;
	
	private MimeType mimeType;
	
	private Object key;

	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Open stream.
	 * 
	 * @return the input stream
	 * @throws IOException 
	 */
	public InputStream openStream() throws IOException {
		InputStream in = null;
		if ( file != null ) {
			in = new BufferedInputStream(new FileInputStream(file));
		} else if (! ArrayUtils.isEmpty(data) ) {
			in = new ByteArrayInputStream(data);
		}
		return in; 
	}
	
	/**
	 * Sets the file.
	 * 
	 * @param file the new file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return byte[]
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return byte[]
	 * @throws IOException 
	 */
	public byte[] getOrReadData() throws IOException {
		if ( ArrayUtils.isEmpty(this.data) && (file != null) ) {
			setData( FileUtils.readFileToByteArray(file) );
		}
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
	public long getSize() {
		if ( file != null ) {
			return file.length();
		} else if (! ArrayUtils.isEmpty(data) ) {
			return data.length;
		}
		return -1;
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
		if ( (fileName == null) && (file != null) ) {
			return file.getName();
		}
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
	
	/**
	 * Resolver mime type.
	 * 
	 * @return the mime type
	 */
	public MimeType resolveMimeType() {
		MimeType mt = MimeResolver.getMimeTypeByExtension(getFileName());
		if ( mt == null ) {
			if ( getFile() != null ) {
				mt =  MimeResolver.getMimeType(getFile());
			} else if (! ArrayUtils.isEmpty(getData()) ) {
				mt =  MimeResolver.getMimeType(getData());
			}
		}
		return mt;
	}	
	
}