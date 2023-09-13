package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Cno implements Serializable {

	private static final long serialVersionUID = -1293760694660980945L;
	
	private Integer id;
	private String code;
	private String title;

	public Integer getId() {
		return id;
	}

	public Cno setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return code;
	}

	public Cno setCode(String code) {
		this.code = code;
		return this;
	}
	
	public String getTitle() {
		return title;
	}

	public Cno setTitle(String title) {
		this.title = title;
		return this;
	}
	
}
