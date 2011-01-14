package com.code.aon.hyperview.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.code.aon.hyperview.enumeration.DataType;

public class HyperViewParameter {
	String label;

	String name;

	DataType dataType;

	boolean prompt;

	String value;
	
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

	public boolean isPrompt() {
		return prompt;
	}

	public void setPrompt(boolean prompt) {
		this.prompt = prompt;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		return value;
	}

	public void setDefaultValue(String defaultValue) {
		this.value = defaultValue;
	}

	public boolean isString() {
		return getDataType() == DataType.STRING;
	}

	public boolean isBoolean() {
		return getDataType() == DataType.BOOLEAN;
	}

	public boolean isDate() {
		return getDataType() == DataType.DATE;
	}

	public boolean isDouble() {
		return getDataType() == DataType.DOUBLE;
	}

	public boolean isInteger() {
		return getDataType() == DataType.INTEGER;
	}

	public boolean isTime() {
		return getDataType() == DataType.TIME;
	}

	public boolean isTimestamp() {
		return getDataType() == DataType.TIMESTAMP;
	}

	public Boolean getValueAsBoolean() {
		return value == null || ("").equals(value)? null : Boolean.valueOf(value);
	}

	public void setValueAsBoolean(Boolean valueAsBoolean) {
		this.value = valueAsBoolean == null ? null : valueAsBoolean.toString();
	}

	public Date getValueAsDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return value == null || ("").equals(value)? null : formatter.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setValueAsDate(Date valueAsDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		this.value = formatter.format(valueAsDate);
	}

	public Double getValueAsDouble() {
		return value == null || ("").equals(value)? null : Double.valueOf(value);
	}

	public void setValueAsDouble(Double valueAsDouble) {
		this.value = valueAsDouble == null ? null : valueAsDouble.toString();
	}

	public Integer getValueAsInteger() {
		return value == null || ("").equals(value)? null : Integer.valueOf(value);
	}

	public void setValueAsInteger(Integer valueAsInteger) {
		this.value = valueAsInteger == null ? null : valueAsInteger.toString();
	}


}
