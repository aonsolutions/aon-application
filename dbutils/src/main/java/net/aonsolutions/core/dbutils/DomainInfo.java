package net.aonsolutions.core.dbutils;

import org.apache.commons.lang.builder.ToStringBuilder;



public class DomainInfo {

	private Integer id;
	private Integer parent;
	private String name;
	private boolean enableHeredity;
	
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	
	
}