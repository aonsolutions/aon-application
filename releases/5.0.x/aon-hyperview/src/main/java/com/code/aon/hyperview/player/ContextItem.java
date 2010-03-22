package com.code.aon.hyperview.player;

import com.code.aon.hyperview.enumeration.DataType;

public class ContextItem {
	String key;

	DataType type;

	String value;

	String formattedValue;

	public ContextItem(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.formattedValue==null?this.value:this.formattedValue;
	}

	public String getFormattedValue() {
		return formattedValue;
	}

	public void setFormattedValue(String formattedValue) {
		this.formattedValue = formattedValue;
	}
}