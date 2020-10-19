package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class CNO implements Serializable {
	private Integer id;
	private String code;
	private String title;
	
	public CNO() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
