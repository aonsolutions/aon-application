package com.code.aon.cms;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;

public class Image implements Serializable {

	private static final long serialVersionUID = 1286073893332097082L;

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

	@Transient
	public String getThumb() {
		return relativePath + ".thumbnail";
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = StringEscapeUtils.escapeHtml(relativePath);
	}

}
