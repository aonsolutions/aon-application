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
@Table(name = "config_i18n")
public class ConfigDetail implements ITransferObject {

	private static final long serialVersionUID = 9052549310815134156L;

	private Integer id;

	private Config config;

	private Language language;

	private String sitename;

	private String offline_message;

	private String css;

	private String javascript;

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
	@JoinColumn(name = "config", nullable = false)
	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "sitename", length = 255)
	public String getSitename() {
		return sitename;
	}

	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "offline_message")
	public String getOffline_message() {
		return offline_message;
	}

	public void setOffline_message(String offline_message) {
		this.offline_message = offline_message;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "css")
	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "javascript")
	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "keywords")
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
		final ConfigDetail o = (ConfigDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.config, o.config)
				.append(this.css, o.css)
				.append(this.description, o.description)
				.append(this.javascript, o.javascript)
				.append(this.keywords, o.keywords)
				.append(this.language, o.language)
				.append(this.offline_message, o.offline_message)
				.append(this.sitename, o.sitename)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(config)
			.append(css)
			.append(description)
			.append(id)	
			.append(javascript)
			.append(keywords)
			.append(language)
			.append(offline_message)
			.append(sitename)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("config", config.getId()).
			append("css", StringUtils.abbreviate(css, 32)).
			append("description", StringUtils.abbreviate(description, 32)).
			append("id", id).
			append("javascript", StringUtils.abbreviate(javascript, 32)).
			append("keywords", StringUtils.abbreviate(keywords, 32)).
			append("language", language.getId()).
			append("offline_message", StringUtils.abbreviate(offline_message, 32)).
			append("sitename", StringUtils.abbreviate(sitename, 32)).
			toString();
	}	

}
