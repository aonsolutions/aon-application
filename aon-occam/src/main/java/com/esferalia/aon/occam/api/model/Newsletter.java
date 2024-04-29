package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.security.Scope;

public class Newsletter  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain;
	private String name;
	private Date date;
	private Byte layout;
	private Byte active;
	private String subject;
	private Scope scope;
	private Byte highlightFirst;
	private Integer template;
	private Byte newsSeparator;
	
	public Integer getId() {
		return id;
	}
	public Newsletter setId(Integer id) {
		this.id = id;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public Newsletter setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Newsletter setName(String name) {
		this.name = name;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public Newsletter setDate(Date date) {
		this.date = date;
		return this;
	}
	public Byte getLayout() {
		return layout;
	}
	public Newsletter setLayout(Byte layout) {
		this.layout = layout;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public Newsletter setActive(Byte active) {
		this.active = active;
		return this;
	}
	public String getSubject() {
		return subject;
	}
	public Newsletter setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	public Scope getScope() {
		return scope;
	}
	public Newsletter setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	public Byte getHighlightFirst() {
		return highlightFirst;
	}
	public Newsletter setHighlightFirst(Byte highlightFirst) {
		this.highlightFirst = highlightFirst;
		return this;
	}
	public Integer getTemplate() {
		return template;
	}
	public Newsletter setTemplate(Integer template) {
		this.template = template;
		return this;
	}
	public Byte getNewsSeparator() {
		return newsSeparator;
	}
	public Newsletter setNewsSeparator(Byte newsSeparator) {
		this.newsSeparator = newsSeparator;
		return this;
	}

}

