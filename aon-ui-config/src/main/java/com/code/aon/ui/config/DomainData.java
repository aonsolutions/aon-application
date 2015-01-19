package com.code.aon.ui.config;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class DomainData implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	
	private String logo;

	private String name;
	
	private String description;
	
	private boolean active;
	
	private boolean enableHeredity;
	
	public DomainData(Integer id, String name, String description, boolean active, boolean enableHeredity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.active = active;
		this.enableHeredity = enableHeredity;
	}

	public Integer getId() {
		return id;
	}
	
	public String getLogo() {
		return logo;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return active;
	}	

	public boolean isEnableHeredity() {
		return enableHeredity;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
