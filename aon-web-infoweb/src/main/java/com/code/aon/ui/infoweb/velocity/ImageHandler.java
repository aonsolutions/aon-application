package com.code.aon.ui.infoweb.velocity;

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
		return name;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	
}
