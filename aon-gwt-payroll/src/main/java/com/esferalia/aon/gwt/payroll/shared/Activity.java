package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Activity implements Serializable {
	
	private Integer id;
	private Integer cnae2009;
	private String description;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCnae2009() {
		return cnae2009;
	}
	
	public void setCnae2009(Integer cnae2009) {
		this.cnae2009 = cnae2009;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
