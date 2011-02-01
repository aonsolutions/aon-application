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
@Table(name="banner_i18n")
public class BannerDetail implements ITransferObject {
	
	private Integer id;
	
	private Banner banner;
	
	private Language language;
	
	private String label;
	
	private String url;
	
	private String image;
	
	private String description;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "banner", nullable = false)
	public Banner getBanner() {
		return banner;
	}

	public void setBanner(Banner banner) {
		this.banner = banner;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(length=255, nullable=false)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(length=255)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Column(length=255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}