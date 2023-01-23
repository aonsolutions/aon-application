package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Cnae implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String code;
	private String title;
	
	public Cnae() {
	}
	public Cnae(Integer id,String code,String title) {
		setId(id);
		setCode(code);
		setTitle(title);
	}
	
	public Integer getId() {
		return id;
	}
	
	public Cnae setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	
	public Cnae setCode(String code) {
		this.code = code;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Cnae setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getCode() == null && getTitle() == null;
	}

}
