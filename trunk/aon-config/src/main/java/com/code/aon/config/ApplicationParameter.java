package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.esferalia.aon.entity.master.ApplicationParameterDB;

@Entity
@Table(name="app_param", uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class ApplicationParameter extends ApplicationParameterDB {
	
	private static final long serialVersionUID = 1L;
	
	private String defaultValue;
	private boolean systemParameter = false;
	
	public ApplicationParameter() {
	}

	public ApplicationParameter(String name, String value) {
		setName(name);
		setValue(value);
	}

	@Transient
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Transient
	public boolean isSystemParameter() {
		return systemParameter;
	}
	public void setSystemParameter(boolean systemParameter) {
		this.systemParameter = systemParameter;
	}
} 
