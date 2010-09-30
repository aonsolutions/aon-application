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
@Table(name = "article_i18n")
public class ArticleDetail implements ITransferObject {

	private static final long serialVersionUID = -4809882881896419110L;

	private Integer id;

	private Article article;
	
	private Language language;

	private String title;
	
	private String subtitle;

	private String content;
	
	private String image_info;

	private String alt;

	private String alt_thumbnail;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "article", nullable = false)
	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "title", nullable = false, length = 255)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "subtitle", length = 255)
	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "content", nullable = false)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	@Column(length=255)
	public String getImage_info() {
		return image_info;
	}

	public void setImage_info(String image_info) {
		this.image_info = image_info;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ArticleDetail o = (ArticleDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alt, o.alt)
				.append(this.alt_thumbnail, o.alt_thumbnail)
				.append(this.article, o.article)
				.append(this.content, o.content)
				.append(this.image_info, o.image_info)
				.append(this.language, o.language)
				.append(this.subtitle, o.subtitle)
				.append(this.title, o.title)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alt)
			.append(alt_thumbnail)
			.append(article)
			.append(content)
			.append(id)	
			.append(image_info)
			.append(language)
			.append(subtitle)
			.append(title)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("alt", alt).
			append("alt_thumbnail", alt_thumbnail).
			append("article", article).
			append("content", StringUtils.abbreviate(content, 32)).
			append("id", id).
			append("image_info", StringUtils.abbreviate(image_info, 32)).
			append("language", language.getId()).
			append("subtitle", StringUtils.abbreviate(subtitle, 32)).
			append("title", StringUtils.abbreviate(title, 32)).
			toString();
	}	
	
}
