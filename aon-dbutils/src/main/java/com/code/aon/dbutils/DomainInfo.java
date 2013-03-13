package com.code.aon.dbutils;

import org.apache.commons.lang.ArrayUtils;


public class DomainInfo {

	private Integer id;
	private Integer parent;
	private String name;
	private boolean enableHeredity;
	private Integer[] domainIds;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getParent() {
		return parent;
	}
	
	public void setParent(Integer parent) {
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEnableHeredity() {
		return enableHeredity;
	}
	
	public void setEnableHeredity(boolean enableHeredity) {
		this.enableHeredity = enableHeredity;
	}

	public Integer[] getDomainIds() {
		if ( this.domainIds == null ) {
			if ( (getParent() != null) && isEnableHeredity() ) {
				this.domainIds = new Integer[]{getId(), getParent()};
			} else {
				this.domainIds = new Integer[]{getId()};
			}
		}
		return this.domainIds;
	}
	
	public boolean isValidDomain( Integer id ) {
		return ArrayUtils.contains( getDomainIds(), id );
	}
	
}
