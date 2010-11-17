package com.code.aon.cms;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Image implements Serializable {

	private static final long serialVersionUID = 1286073893332097082L;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	private File file;

	private String relativePath;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	private Date getLastModified( File file ) {
		long value = file.lastModified();
		if ( value != 0 ) {
			return new Date(value);
		}
		return null;
	}

	public String getThumb() {
		String thumb = relativePath + ".thumbnail"; 
		Date date = getLastModified(file);
		if ( date != null ) {
			thumb += "?lm=" + DATE_FORMAT.format(date);
		}
		return thumb;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

}
