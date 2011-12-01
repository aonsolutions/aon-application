package com.code.aon.ui.infoweb.velocity;


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

	public void setLabel(String label) {
		this.label = label;
	}

}
