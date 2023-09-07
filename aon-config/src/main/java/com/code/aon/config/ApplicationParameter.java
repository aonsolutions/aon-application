package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ApplicationParameterDB;

@Entity
@Table(name="app_param", uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class ApplicationParameter extends ApplicationParameterDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
