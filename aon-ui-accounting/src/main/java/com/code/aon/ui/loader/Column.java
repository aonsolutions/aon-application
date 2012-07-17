package com.code.aon.ui.loader;

import org.apache.commons.lang.ArrayUtils;

public class Column {
	private String entity;
	private String name;
	private int type;
	private int length;
	private boolean required;
	private int[] enumValues;

	public Column(String entity,String name,int type,int length, boolean required,int[] enumValues) {
		this.entity = entity;
		this.name = name;
		this.type = type;
		this.length = length;
		this.required = required;
		this.enumValues = enumValues;
	}

	protected String getEntity() {
		return entity;
	}

	protected String getName() {
		return name;
	}

	protected int getType() {
		return type;
	}

	protected int getLength() {
		return length;
	}

	protected boolean isRequired() {
		return required;
	}
	
	protected int[] getEnumValues() {
		return enumValues;
	}

	protected boolean hasValidation( ) {
		return (getType() == 2 && getEnumValues() != null && enumValues.length > 0);
	}

	protected boolean validate( Object value ) {
		int v = (Integer) value;
		return (ArrayUtils.indexOf(getEnumValues(), v) != -1);
	}
	
}
