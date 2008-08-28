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
@Table(name="album_category_i18n")
public class AlbumCategoryDetail implements ITransferObject {

	private Integer id;
	
	private AlbumCategory albumCategory;
	
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
	@JoinColumn(name = "album_category", nullable = false)
	public AlbumCategory getAlbumCategory() {
		return albumCategory;
	}

	public void setAlbumCategory(AlbumCategory albumCategory) {
		this.albumCategory = albumCategory;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(length=32)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}