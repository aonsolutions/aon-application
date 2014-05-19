package com.code.aon.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;

/**
 * @author ecastellano
 * 
 */
public class AonFile {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonFile.class);
	
	private File file;

	private String fileName;
	
	private MimeType mimeType;
	
	private Object key;
	
	private IAttachment attachment;
	
	private boolean dirty;

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
		} else if ( attachment != null ) {
			in = new ByteArrayInputStream(attachment.getData());
		}
		return in; 
	}
	
	/**
	 * Sets the file.
	 * 
	 * @param file the new file
	 */
	public void setFile(File file) {
		clean();
		this.file = file;
	}

	/**
	 * @return byte[]
	 */
	public byte[] getData() {
		if ( file != null ) {
			try {
				return FileUtils.readFileToByteArray(file);
			} catch (IOException e) {
				LOGGER.error( "Error loading "+ file, e);
			}
		} else if ( attachment != null ) {
			return attachment.getData();
		}
		return null;
	}
	
	/**
	 * @param data 
	 */
	public void setData(byte[] data) {
		clean();		
		if (! ArrayUtils.isEmpty(data) ) {
			try {
				this.file = File.createTempFile("aonFile", ".tmp");
				FileUtils.writeByteArrayToFile(this.file, data);
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e);
			}
		}
	}

	public void setAttachment(IAttachment attachment) {
		clean();
		this.attachment = attachment;
		setDirty(false);
	}	

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public long getSize() {
		if ( file != null ) {
			return file.length();
		} else if ( attachment != null ) {
			return attachment.getSize();
		}
		return -1;
	}

	public String getSizeToDisplay() {
		long size = getSize();
		return FileUtils.byteCountToDisplaySize((size >= 0) ? size : 0);
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
		if ( this.key != null ) {
			return this.key.toString();
		} else if ( (this.attachment != null) && (this.attachment.getId() != null) ) {
			return String.valueOf(this.attachment.hashCode());
		}
		return String.valueOf(hashCode());
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
			if ( this.file != null ) {
				mt =  MimeResolver.getMimeType(getFile());
			} else if ( this.attachment != null ) {
				mt =  MimeResolver.getMimeType(this.attachment.getData());	
			}
		}
		return mt;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void clean() {
		setDirty(true);
		FileUtils.deleteQuietly(file);
		this.file = null;
		this.attachment = null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(attachment)
			.append(dirty)
			.append(file)
			.append(fileName)
			.append(key)
			.append(mimeType)
			.toHashCode();
   }   
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).
				append("dirty", dirty).
				append("file", (file != null) ? file.getName() : null).
				append("fileName", fileName).
				append("key", key).
				append("mimeType", mimeType).
				append("size", getSize()).				
				toString();
	}	
	
}