package com.code.aon.jaas.auth.spi.db;

import java.util.Date;

public class Domain extends BasicInfo {
	
	private String dataBaseName;
	
	private String name;
	
	private Integer parent;
	
	private Date expirationDate;
	
	private Integer scope;

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

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}
	
}
