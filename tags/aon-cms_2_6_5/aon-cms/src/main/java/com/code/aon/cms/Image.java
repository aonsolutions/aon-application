package com.code.aon.cms;

import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

public class Image implements ITransferObject {

	private String path;

	private String relativePath;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Transient
	public String getThumb() {
		relativePath = relativePath.replaceAll("[ +]", "%20");
		return relativePath + ".thumbnail";
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

}
