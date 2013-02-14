package com.code.aon.ui.loader;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.AonException;

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

	public String getEntity() {
		return entity;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public boolean isRequired() {
		return required;
	}
	
	public int[] getEnumValues() {
		return enumValues;
	}

	public boolean hasValidation( ) {
		return (isRequired() || (getType() == 0 && getEnumValues() != null && enumValues.length > 0));
	}

	public String validate( Object value ) throws AonException {
		if (isRequired() && value == null) {
			return "El valor '"+getName()+"' es un dato requerido.";
		}
		if (enumValues != null) {
			if (!isRequired() && value == null) {
				return null;
			}
			int v = (Integer) value;
			if (ArrayUtils.indexOf(getEnumValues(), v) == -1) {
				return "El valor '"+ v + "'de la columna '"+getName()+"' no se encuentra en la lista de valores válidos. ("+ArrayUtils.toString(enumValues)+")";	
			}
		}
		return null;
	}
	
}
