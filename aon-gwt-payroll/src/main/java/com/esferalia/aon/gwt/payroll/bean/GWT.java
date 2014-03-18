package com.esferalia.aon.gwt.payroll.bean;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class GWT implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String entryPoint;
	
	
	public String getEntryPoint() {
		return entryPoint;
	}
	
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}
	
}
