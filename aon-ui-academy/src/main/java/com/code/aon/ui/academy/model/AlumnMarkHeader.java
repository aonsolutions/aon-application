package com.code.aon.ui.academy.model;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class AlumnMarkHeader implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int position; 
	
	private String code;
	
	private String description;

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}