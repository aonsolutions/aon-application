package com.esferalia.aon.file.seres.util.ftp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class FtpFile implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String name;
	
	private String group;
	
	private long size;
	
	private Calendar timestamp;
	

	public String getName() {
		return name;
	}

	public FtpFile setName(String name) {
		this.name = name;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public FtpFile setGroup(String group) {
		this.group = group;
		return this;
	}

	public long getSize() {
		return size;
	}

	public FtpFile setSize(long size) {
		this.size = size;
		return this;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public FtpFile setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public Date getModificationDate() {
		return timestamp.getTime();
	}
	
	
}
