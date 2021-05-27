package com.esferalia.aon.occam.api.model;

import java.util.Date;

public class MailTemplate {
	
	Integer id;
	Integer domain;
	Integer scope;
	String name;
	Boolean active;
	Date creationDate;
	String subject;
	String width;
	String titleColor;
	String backgroundColor;
	Integer headerTemplate;
	Integer footerTemplate;
	
	public Integer getId() {
		return id;
	}
	public MailTemplate setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public MailTemplate setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public MailTemplate setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public String getName() {
		return name;
	}
	public MailTemplate setName(String name) {
		this.name = name;
		return this;
	}
	public Boolean isActive() {
		return active;
	}
	public MailTemplate setActive(Boolean active) {
		this.active = active;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public MailTemplate setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getSubject() {
		return subject;
	}
	public MailTemplate setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	public String getWidth() {
		return width;
	}
	public MailTemplate setWidth(String width) {
		this.width = width;
		return this;
	}
	public String getTitleColor() {
		return titleColor;
	}
	public MailTemplate setTitleColor(String titleColor) {
		this.titleColor = titleColor;
		return this;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public MailTemplate setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	public Integer getHeaderTemplate() {
		return headerTemplate;
	}
	public MailTemplate setHeaderTemplate(Integer headerTemplate) {
		this.headerTemplate = headerTemplate;
		return this;
	}
	public Integer getFooterTemplate() {
		return footerTemplate;
	}
	public MailTemplate setFooterTemplate(Integer footerTemplate) {
		this.footerTemplate = footerTemplate;
		return this;
	}
	
	public Boolean isEmpty() {
		return getId() == null;
	}
	
}

