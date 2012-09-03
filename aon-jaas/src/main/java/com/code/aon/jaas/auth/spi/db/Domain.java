package com.code.aon.jaas.auth.spi.db;

public class Domain extends BasicInfo {
	
	private String dataBaseName;
	
	private String name;
	
	private Integer parent;

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}
	
}
