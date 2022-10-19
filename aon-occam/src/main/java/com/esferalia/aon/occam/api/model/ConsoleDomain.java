package com.esferalia.aon.occam.api.model;

public class ConsoleDomain extends Domain {

	private static final long serialVersionUID = 1093889839212920172L;
	private String schema;
	private Integer childCount;
	private Integer activeChildCount;
	private boolean remoteAccessEnabled;
	
	public String getSchema() {
		return schema;
	}
	public ConsoleDomain setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public Integer getChildCount() {
		return childCount;
	}
	public ConsoleDomain setChildCount(Integer childCount) {
		this.childCount = childCount;
		return this;
	}
	
	public Integer getActiveChildCount() {
		return activeChildCount;
	}
	public ConsoleDomain setActiveChildCount(Integer activeChildCount) {
		this.activeChildCount = activeChildCount;
		return this;
	}
	
	public boolean isRemoteAccessEnabled() {
		return remoteAccessEnabled;
	}
	public ConsoleDomain setRemoteAccessEnabled(boolean remoteAccessEnabled) {
		this.remoteAccessEnabled = remoteAccessEnabled;
		return this;
	}
}
