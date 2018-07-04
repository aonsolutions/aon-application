package com.code.aon.ui.product.controller;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class SystemAddInfoController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public Boolean isSystem(String attribute) {
		return attribute.contains("system_");
	}
	
	public String getAttr(String attribute) {
		if(attribute.contains("system_")){
			return attribute.replace("system_", "");
		}
		return attribute;
	}
}