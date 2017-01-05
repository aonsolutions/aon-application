package com.esferalia.aon.file.seres.util.ftp;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTPFile;

public class FtpFile extends FTPFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private FTPFile file;

	
	public FtpFile(FTPFile file) {
		this.file = file;
	}

	@Override
	public String getGroup() {
		return this.file.getGroup();
	}

	@Override
	public int getHardLinkCount() {
		return this.file.getHardLinkCount();
	}

	@Override
	public String getLink() {
		return this.file.getLink();
	}

	@Override
	public String getName() {
		return this.file.getName();
	}

	@Override
	public String getRawListing() {
		return this.file.getRawListing();
	}

	@Override
	public long getSize() {
		return this.file.getSize();
	}

	@Override
	public Calendar getTimestamp() {
		return this.file.getTimestamp();
	}

	@Override
	public int getType() {
		return this.file.getType();
	}

	@Override
	public String getUser() {
		return this.file.getUser();
	}

	@Override
	public boolean isDirectory() {
		return this.file.isDirectory();
	}

	@Override
	public boolean isFile() {
		return this.file.isFile();
	}

	@Override
	public boolean isSymbolicLink() {
		return this.file.isSymbolicLink();
	}

	@Override
	public boolean isUnknown() {
		return this.file.isUnknown();
	}

	
	
	
	public Date getModificationDate() {
		return getTimestamp().getTime();
	}
	
	public String getDetailFormatted() {
		StringBuffer bf = new StringBuffer();
		if (isDirectory())
			bf.append("[").append(this.getName()).append("]");
		else 
			bf.append(this.getName());
		bf.append("\t\t").append(getSize());
		bf.append("\t\t").append(formater.format(getTimestamp().getTime()));
		return bf.toString();
	}

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer(this.getName());
		bf.append(" (size: ");
		bf.append(this.getSize());
		bf.append(", group: ");
		bf.append(this.getGroup());
		bf.append(", type: ");
		bf.append(this.getType());
		bf.append(", link: ");
		bf.append(this.getLink());
		bf.append(", timestamp: ");
		bf.append(this.getTimestamp());
		bf.append(")");
		bf.append(" | File: ");
		bf.append(this.isFile());
		bf.append(" | Directory: ");
		bf.append(this.isDirectory());
		bf.append(" | Unknown: ");
		bf.append(this.isUnknown());
		bf.append(" | SymbolicLink: ");
		bf.append(this.isSymbolicLink());
		return bf.toString();
	}

}
