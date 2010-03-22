package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.cms.enumeration.Languages;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "language")
public class Language implements ITransferObject, IPositionObject {

	private Integer id;

	private Languages language;

	private String description;

	private Integer position;

	private boolean defaultLanguage;

	@Id
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "language", nullable = false)
	public Languages getLanguage() {
		return language;
	}

	public void setLanguage(Languages language) {
		this.language = language;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Column(name = "defaultLanguage", nullable = false)
	public boolean isDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(boolean defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
}
