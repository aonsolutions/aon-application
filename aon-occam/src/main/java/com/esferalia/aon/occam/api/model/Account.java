package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Account implements Serializable {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private String code;
	private String description;
	private String alias;
	private boolean entryEnabled;
	private byte level;
	private boolean active;
	private String costCenter;
	
	private boolean hasRegistry;

	public Integer getId() {
		return id;
	}

	public Account setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Account setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}

	public Account setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Account setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public Account setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public boolean isEntryEnabled() {
		return entryEnabled;
	}

	public Account setEntryEnabled(boolean entryEnabled) {
		this.entryEnabled = entryEnabled;
		return this;
	}

	public byte getLevel() {
		return level;
	}

	public Account setLevel(byte level) {
		this.level = level;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Account setActive(boolean active) {
		this.active = active;
		return this;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public Account setCostCenter(String costCenter) {
		this.costCenter = costCenter;
		return this;
	}

	public boolean hasRegistry() {
		return hasRegistry;
	}

	public Account setHasRegistry(boolean hasRegistry) {
		this.hasRegistry = hasRegistry;
		return this;
	}

	public String getFullName() {
		return (AonStringUtils.join(
				 AonStringUtils.defaultString(getCode())
				,AonStringUtils.isNotBlank(getDescription())
					?(AonStringUtils.SPACE + '-' + AonStringUtils.SPACE + getDescription())  
					:(AonStringUtils.EMPTY)
								   )
				);
	}
	
	public Account clone() {
		return new Account()
			.setId( getId() )
			.setDomain( getDomain())
			.setCode( getCode())
			.setDescription( getDescription())
			.setAlias( getAlias())
			.setEntryEnabled( isEntryEnabled())
			.setLevel( getLevel())
			.setActive( isActive())
			.setCostCenter( getCostCenter())
			.setHasRegistry( hasRegistry())
			;
	}
}
