package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object for representing an application parameter.
 * 
 * @author Consulting & Development. ecastellano - 17/10/2006
 * 
 */
@Entity
@Table(name = "app_param")
public class ApplicationParameter implements ITransferObject {

	private static final long serialVersionUID = -7241719325009628022L;

	/**
	 * Name of the parameter.
	 */
	private String name;

	/**
	 * Value of the parameter.
	 */
	private String value;

	/**
	 * Default value of the parameter.
	 */
	private String defaultValue;
	
	private boolean systemParameter = false;

	/**
	 * Returns the name of the parameter.
	 * 
	 * @return The name of the parameter.
	 */
	@Id
	@Column(length = 32, nullable = false)
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the parameter.
	 * 
	 * @param name
	 *            The name of the parameter.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of this application parameter.
	 * 
	 * @return The value of this application parameter.
	 */
	@Column(length = 64)
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of this application parameter.
	 * 
	 * @param value
	 *            The value of this application parameter.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the default value of this application parameter.
	 * 
	 * @return The default value of this application parameter.
	 */
	@Transient
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value of this application parameter.
	 * 
	 * @param defaultValue
	 *            The default value of this application parameter.
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns if this application parameter is specified in configuration bean.
	 * 
	 * @return TRUE if this application parameter is specified in configuration bean.
	 */
	@Transient
	public boolean isSystemParameter() {
		return systemParameter;
	}

	/**
	 * Sets if this application parameter is specified in configuration bean.
	 * 
	 * @return TRUE if this application parameter is specified in configuration bean.
	 */
	public void setSystemParameter(boolean systemParameter) {
		this.systemParameter = systemParameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getValue();
	}

}