package com.code.aon.ui.webinfo.velocity;

import com.code.aon.webinfo.enumeration.WebInfoPageType;

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
