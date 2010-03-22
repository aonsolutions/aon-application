package com.code.aon.hyperview.model;

import com.code.aon.hyperview.enumeration.DataType;

public class HyperViewAttribute {
	private String label;

	private DataType dataType;

	private String name;

	private int position;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public void convertType(String type) {
		this.dataType = DataType.valueOf(type);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isAccessible() {
		return (getName() != null);
	}

	public boolean isString() {
		return this.dataType == DataType.STRING;
	}

	public boolean isInteger() {
		return this.dataType == DataType.INTEGER;
	}

	public boolean isDouble() {
		return this.dataType == DataType.DOUBLE;
	}

	public boolean isBoolean() {
		return this.dataType == DataType.BOOLEAN;
	}

	public boolean isDate() {
		return this.dataType == DataType.DATE;
	}

	public boolean isTime() {
		return this.dataType == DataType.TIME;
	}

	public boolean isTimestamp() {
		return this.dataType == DataType.TIMESTAMP;
	}

}
