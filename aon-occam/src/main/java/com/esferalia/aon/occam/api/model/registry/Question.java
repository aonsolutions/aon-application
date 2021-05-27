package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class Question implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Byte active;
	private String questionText;
	private Byte type;
	private String argument;
	private String alias;
	
	public Integer getId() {
		return id;
	}
	public Question setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Question setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public Question setActive(Byte active) {
		this.active = active;
		return this;
	}
	public String getQuestionText() {
		return questionText;
	}
	public Question setQuestionText(String questionText) {
		this.questionText = questionText;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public Question setType(Byte type) {
		this.type = type;
		return this;
	}
	public String getArgument() {
		return argument;
	}
	public Question setArgument(String argument) {
		this.argument = argument;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public Question setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	
}
