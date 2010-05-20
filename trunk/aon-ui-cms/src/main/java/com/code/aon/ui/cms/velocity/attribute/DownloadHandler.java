package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.DownloadDetail;

public class DownloadHandler {

	private String file;
	
	private String description;
	
	private String title;

	public DownloadHandler (DownloadDetail detail) {
		file = detail.getFile();
		description = detail.getDescription();
		title = detail.getTitle();
	}

	public String getFile() {
		return file;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}
	
	
	
}
