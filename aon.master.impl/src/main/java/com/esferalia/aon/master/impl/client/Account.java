package com.esferalia.aon.master.impl.client;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class Account implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public Account() {
		
	}

	public Account(Integer id, Integer domain,
			String code, String description, String alias,
			boolean entryEnabled, int level,
			boolean active, String costCenter) {
		setId(id);
		setDomain(domain);
		setCode(code);
		setDescription(description);
		setAlias(alias);
		setEntryEnabled(entryEnabled);
		setLevel(level);
		setActive(active);
		setCostCenter(costCenter);
	}	
	
	private Integer id;
	private int domain;
	private String code;
	private String description;
	private String alias;
	private boolean entryEnabled;
	private int level;
	private boolean active;
	private String costCenter;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isEntryEnabled() {
		return entryEnabled;
	}

	public void setEntryEnabled(boolean entryEnabled) {
		this.entryEnabled = entryEnabled;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	
}
