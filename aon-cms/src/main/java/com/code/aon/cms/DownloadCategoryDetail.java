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
@Table(name="download_category_i18n")
public class DownloadCategoryDetail implements ITransferObject{
	
	private Integer id;
	
	private DownloadCategory downloadCategory;
	
	private Language language;
	
	private String label;

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
	@JoinColumn(name = "download_category", nullable = false)
	public DownloadCategory getDownloadCategory() {
		return downloadCategory;
	}

	public void setDownloadCategory(DownloadCategory downloadCategory) {
		this.downloadCategory = downloadCategory;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(length=64)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}