package com.code.aon.webmail.bean;

import java.io.InputStream;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;

public class AonAttachment {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AonAttachment.class);
	
	private int position;
	
	private String fileName;
	
	private BodyPart part;
	
    public AonAttachment(BodyPart part, int position) {
		this.position = position;
		this.part = part;
	}

	/**
	 * @return the part
	 */
	public BodyPart getPart() {
		return part;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(BodyPart part) {
		this.part = part;
	}

	public String getFileName(){
		if ( this.fileName == null ) {
			try {
				fileName = part.getFileName();
			} catch (MessagingException e1) {
				fileName = "Error reading name";
			}
			if (fileName==null) {
				fileName = "no_name_file_"+position;
			}
			try {
				if (part.getContentType().startsWith("message/") &&
						!fileName.endsWith(".eml")){
					fileName += ".eml";
				}
			} catch (MessagingException e1) {
				LOGGER.debug(e1.getMessage(), e1);
			}
			if ( StringUtils.containsIgnoreCase(fileName, "=?") ) {
				try {
					fileName = MimeUtility.decodeWord(fileName);
				} catch (Exception e) {
					LOGGER.debug(e.getMessage(), e);
				}
			}
		}
		return fileName;
	}
	
	public String getFileSize(){
		try {
			int size = part.getSize();
			size = size * 75 /100 / 1000;
			return size +" Kb";
		} catch (MessagingException e) {
			LOGGER.error(e.getMessage(), e );
		}
		return "";
	}

	public InputStream getInputStream(){
		try {
			return part.getInputStream();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e );
		}
		return null;
	}
	
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Gets the mime type.
	 *
	 * @return the mime type
	 */
	public MimeType getMimeType() {
		MimeType type = null;
		try {
			String value = StringUtils.lowerCase(this.part.getContentType());
			value = StringUtils.substringBefore(value, ";");
			type = MimeType.get(value);
			if ( type == null ) {
				type = MimeResolver.getMimeTypeByExtension( getFileName() );
			}
		} catch (MessagingException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return type;
	}
	
}
