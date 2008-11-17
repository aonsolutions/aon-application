package com.code.aon.ui.infoweb.velocity;

import com.code.aon.infoweb.enumeration.WebInfoPageType;

public class MenuOptionHandler {

	private String label;
	
	private String link;

	public MenuOptionHandler (String name, String description) {
		this.label = name;
		this.link = description;
	}

	public String getLabel() {
		return label;
	}

	public String getLink() {
		return link;
	}

}
