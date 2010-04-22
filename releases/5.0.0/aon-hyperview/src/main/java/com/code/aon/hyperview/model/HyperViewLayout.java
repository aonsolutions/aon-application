package com.code.aon.hyperview.model;

import com.code.aon.hyperview.enumeration.LayoutType;

public class HyperViewLayout {
	private LayoutType type;

	public LayoutType getType() {
		return type;
	}

	public void setType(LayoutType type) {
		this.type = type;
	}
	
	public void convertType(String type) {
		this.type = LayoutType.valueOf(type);
	}

}
