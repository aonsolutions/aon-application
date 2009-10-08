package com.code.aon.ui.infoweb.velocity;

import org.apache.commons.lang.StringEscapeUtils;

public class ImageHandler {
	
	private String name;

	private String link;

	private String description;
	
	public ImageHandler (String name, String link, String description) {
		this.name = name;
		this.link = link;
		this.description = description;
	}

	public String getName() {
		return StringEscapeUtils.escapeHtml(name);
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	
}
