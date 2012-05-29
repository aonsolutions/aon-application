package com.code.aon.ui.accounting.invoice;

public class Column {
	private String entity;
	private String name;
	private int type;
	private int length;
	private boolean required;
	private int[] enumValues;
	
	protected Column(String entity,String name,int type,int length, boolean required,int[] enumValues) {
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
	
}
