package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="album_image_i18n")
public class AlbumImageDetail implements ITransferObject {

	private static final long serialVersionUID = 1203337453191251926L;

	private Integer id;
	
	private AlbumImage albumImage;
	
	private Language language;
	
	private String title;
	
	private String description;
	
	private String alt;

	private String alt_thumbnail;

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
	@JoinColumn(name = "album_image", nullable = false)
	public AlbumImage getAlbumImage() {
		return albumImage;
	}

	public void setAlbumImage(AlbumImage albumImage) {
		this.albumImage = albumImage;
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
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Lob
	@Type(type="stringClob")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=64)
	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	@Column(length=64)
	public String getAlt_thumbnail() {
		return alt_thumbnail;
	}

	public void setAlt_thumbnail(String alt_thumbnail) {
		this.alt_thumbnail = alt_thumbnail;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AlbumImageDetail o = (AlbumImageDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.albumImage, o.albumImage)
				.append(this.alt, o.alt)
				.append(this.alt_thumbnail, o.alt_thumbnail)
				.append(this.description, o.description)
				.append(this.language, o.language)
				.append(this.title, o.title)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(albumImage)
			.append(alt)
			.append(alt_thumbnail)
			.append(description)
			.append(id)	
			.append(language)
			.append(title)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("albumImage", albumImage.getId()).
			append("alt", alt).
			append("alt_thumbnail", alt_thumbnail).
			append("description", StringUtils.abbreviate(description, 32)).
			append("id", id).
			append("language", language.getId()).
			append("title", StringUtils.abbreviate(title, 32)).
			toString();
	}	
	
}
