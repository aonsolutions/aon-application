package com.code.aon.ui.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	
	private String document;
	
	private List<String> cccs ;
	
	public DomainData(Integer id, String name, String description, Date expirationDate, boolean active, boolean enableHeredity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.expirationDate = expirationDate;
		this.active = active;
		this.enableHeredity = enableHeredity;
		this.cccs = new ArrayList<String>();
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
	
	public String getDocument() {
		return document;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	public void setDocument(String document) {
		this.document = document;
	}
	
	public boolean isExpired() {
		if ( this.expirationDate != null ) {
			return this.expirationDate.compareTo(new Date()) < 0;
		}
		return false;
	}	
	
	public void addCCC(String ccc ) {
		cccs.add(ccc);
	}
	
	public String getCccs() {
		return cccs.stream().collect(Collectors.joining(","));
	}

}
