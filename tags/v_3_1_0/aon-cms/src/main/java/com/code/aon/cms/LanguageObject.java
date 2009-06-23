package com.code.aon.cms;

import com.code.aon.cms.enumeration.Languages;
import com.code.aon.common.ITransferObject;

public class LanguageObject implements ITransferObject {

	private Languages language;

	private String description;

	private Integer position;

	private boolean defaultLanguage;
	
	private boolean selected;
	
	public Languages getLanguage() {
		return language;
	}

	public void setLanguage(Languages language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public boolean isDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(boolean defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
