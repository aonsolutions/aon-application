package com.esferalia.aon.gwt.common.bean;

import java.io.Serializable;

public class GWT implements Serializable {
	
	private static final long serialVersionUID = 4970259845694805821L;
	
	private String entryPoint;
	
	
	public String getEntryPoint() {
		return entryPoint;
	}
	
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}
	
}
