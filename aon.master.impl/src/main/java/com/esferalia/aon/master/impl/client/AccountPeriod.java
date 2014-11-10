package com.esferalia.aon.master.impl.client;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;

public class AccountPeriod implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Date initiationDate;
	private Date deadline;
	private AccountPeriodStatus status;

	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Date getInitiationDate() {
		return this.initiationDate;
	}
	public void setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
	}

	public Date getDeadline() {
		return this.deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public AccountPeriodStatus getStatus() {
		return this.status;
	}
	public void setStatus(AccountPeriodStatus status) {
		this.status = status;
	}
	
}
