package com.code.aon.ui.config;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class DomainData implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	
	private String logo;

	private String name;
	
	private String description;
	
	private Integer activeUsers;
	
	private Integer maxDefinedUsers;
	
	private boolean enableHeredity;
	
	private boolean aonOne;
	
	private boolean portal;
	
	public DomainData(Integer id, String name, String description, Integer maxDefinedUsers, boolean enableHeredity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.maxDefinedUsers = maxDefinedUsers;
		this.enableHeredity = enableHeredity;
		this.activeUsers = 0;
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

	public boolean isEnableHeredity() {
		return enableHeredity;
	}

	public Integer getActiveUsers() {
		return activeUsers;
	}

	public void setActiveUsers(Integer activeUsers) {
		this.activeUsers = activeUsers;
	}

	public boolean isAonOne() {
		return aonOne;
	}

	public void setAonOne(boolean aonOne) {
		this.aonOne = aonOne;
	}

	public boolean isPortal() {
		return portal;
	}

	public void setPortal(boolean portal) {
		this.portal = portal;
	}

	public Integer getMaxDefinedUsers() {
		return maxDefinedUsers;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
