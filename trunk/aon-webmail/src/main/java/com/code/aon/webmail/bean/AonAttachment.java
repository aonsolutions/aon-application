package com.code.aon.webmail.bean;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

public class AonAttachment {
	
	private static final Logger LOGGER = Logger.getLogger(AonAttachment.class.getName());
	
	private int position;
	
	private Part part;
	
    /**
	 * @return the part
	 */
	public Part getPart() {
		return part;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(Part part) {
		this.part = part;
	}

	public String getFileName(){
		String fileName;
		try {
			fileName = part.getFileName();
		} catch (MessagingException e1) {
			fileName = "Error reading name";
		}
		if (fileName==null)
			fileName = "no_name_file_"+position;
		try {
			if (part.getContentType().startsWith("message/") &&
					!fileName.endsWith(".eml")){
				fileName += ".eml";
			}
		} catch (MessagingException e1) {
		}
		if ((fileName.indexOf("=?iso") >= 0) || 
				(fileName.indexOf("=?ISO") >= 0)){
			try {
				fileName = MimeUtility.decodeWord(fileName);
			}
			catch (Exception e) {
			}
		}
		return fileName;
	}
	
	public String getFileSize(){
		try {
			int size = part.getSize();
			size = size * 75 /100 / 1000;
			return String.valueOf(size)+" Kb";
		} catch (MessagingException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		return "";
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


}
