package com.code.aon.ui.config;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class DomainData implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	
	private String logo;

	private String name;
	
	private String description;
	
	private boolean active;
	
	private boolean enableHeredity;
	
	private Date expirationDate;
	
	public DomainData(Integer id, String name, String description, Date expirationDate, boolean active, boolean enableHeredity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.expirationDate = expirationDate;
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
	
	public boolean isExpired() {
		if ( this.expirationDate != null ) {
			return this.expirationDate.compareTo(new Date()) < 0;
		}
		return false;
	}	

}
