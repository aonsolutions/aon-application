package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="sport_category_i18n")
public class SportCategoryDetail implements ITransferObject {

	private Integer id;
	
	private SportCategory sportCategory;
	
	private Language language;
	
	private String description;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sport_category", nullable = false)
	public SportCategory getSportCategory() {
		return sportCategory;
	}

	public void setSportCategory(SportCategory sportCategory) {
		this.sportCategory = sportCategory;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(length=128)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}