package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "direct_access_i18n")
public class DirectAccessDetail implements ITransferObject {

	private static final long serialVersionUID = 3226755239890903639L;

	private Integer id;

	private DirectAccess directAccess;
	
	private Language language;

	private String label;

	private String description;
	
	private String url;

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
	@JoinColumn(name = "direct_access", nullable = false)
	public DirectAccess getDirectAccess() {
		return directAccess;
	}

	public void setDirectAccess(DirectAccess directAccess) {
		this.directAccess = directAccess;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "label", nullable = false, length = 64)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "url", length = 255)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "description", length = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DirectAccessDetail o = (DirectAccessDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.directAccess, o.directAccess)
				.append(this.label, o.label)
				.append(this.language, o.language)
				.append(this.url, o.url)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)
			.append(directAccess)
			.append(id)	
			.append(label)
			.append(language)
			.append(url)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("description", StringUtils.abbreviate(description, 32)).
			append("directAccess", directAccess.getId()).
			append("id", id).
			append("label", StringUtils.abbreviate(label, 32)).
			append("language", language.getId()).
			append("url", url).
			toString();
	}	
	
}
