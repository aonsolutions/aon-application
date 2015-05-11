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
	public boolean isClosed() {
		return this.status == AccountPeriodStatus.CLOSED;
	}
	
}
