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
@Table(name = "generic_page_i18n")
public class GenericPageDetail implements ITransferObject {

	private static final long serialVersionUID = 477023284133510768L;

	private Integer id;

	private GenericPage generic_page;
	
	private Language language;

	private String title;

	private String content;
	
	private String description;
	
	private String keywords;

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
	@JoinColumn(name = "generic_page", nullable = false)
	public GenericPage getGeneric_page() {
		return generic_page;
	}

	public void setGeneric_page(GenericPage generic_page) {
		this.generic_page = generic_page;
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

	@Lob
	@Type(type="stringClob")   	
	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "description", length = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "keywords", length = 255)
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final GenericPageDetail o = (GenericPageDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.content, o.content)
				.append(this.description, o.description)
				.append(this.generic_page, o.generic_page)
				.append(this.keywords, o.keywords)				
				.append(this.language, o.language)
				.append(this.title, o.title)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(content)
			.append(description)
			.append(generic_page)
			.append(id)	
			.append(keywords)
			.append(language)
			.append(title)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("content", StringUtils.abbreviate(content, 32)).
			append("description", StringUtils.abbreviate(description, 32)).
			append("generic_page", generic_page.getId()).
			append("id", id).
			append("keywords", StringUtils.abbreviate(keywords, 32)).
			append("language", language.getId()).
			append("title", StringUtils.abbreviate(title, 32)).
			toString();
	}	
	
}
