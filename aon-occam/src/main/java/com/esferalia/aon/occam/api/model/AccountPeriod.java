package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;

public class AccountPeriod implements Serializable {

	private static final long serialVersionUID = -1686197262238560337L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Date initiationDate;
	private Date deadline;
	private AccountPeriodStatus status;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	private boolean defaultPeriod;

	public Integer getId() {
		return this.id;
	}
	public AccountPeriod setId(Integer id) {
		this.id = id;
		return this; 
	}

	public Integer getDomain() {
		return this.domain;
	}
	public AccountPeriod setDomain(Integer domain) {
		this.domain = domain;
		return this; 
	}

	public String getName() {
		return this.name;
	}
	public AccountPeriod setName(String name) {
		this.name = name;
		return this; 
	}

	public Date getInitiationDate() {
		return this.initiationDate;
	}
	public AccountPeriod setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
		return this; 
	}

	public Date getDeadline() {
		return this.deadline;
	}
	public AccountPeriod setDeadline(Date deadline) {
		this.deadline = deadline;
		return this; 
	}

	public AccountPeriodStatus getStatus() {
		return this.status;
	}
	public AccountPeriod setStatus(AccountPeriodStatus status) {
		this.status = status;
		return this; 
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	public AccountPeriod setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public AccountPeriod setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public AccountPeriod setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public AccountPeriod setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public boolean isClosed() {
		return this.status == AccountPeriodStatus.CLOSED;
	}
	public boolean isDefaultPeriod() {
		return defaultPeriod;
	}
	public void setDefaultPeriod(boolean defaultPeriod) {
		this.defaultPeriod = defaultPeriod;
	}
}
