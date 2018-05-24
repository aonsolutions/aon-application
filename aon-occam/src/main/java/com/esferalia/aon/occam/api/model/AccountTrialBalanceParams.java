package com.esferalia.aon.occam.api.model;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountTrialBalanceParams implements IAccountParams,Cloneable {

	private static final long serialVersionUID = 8075038329917800745L;
	
	private int domain;
	private Integer period;
	private Date fromDate;
	private Date toDate;
	private String code;
	private int level;
	private Integer activity;
	private SecurityLevel securityLevel;

	private boolean lowLevelAccountVisible;
	private boolean noActivityAccountVisible;
	
	@Override
	public int getDomain() {
		return domain;
	}
	public AccountTrialBalanceParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public Integer getPeriod() {
		return period;
	}
	public AccountTrialBalanceParams setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	@Override
	public Date getFromDate() {
		return fromDate;
	}
	public AccountTrialBalanceParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	@Override
	public Date getToDate() {
		return toDate;
	}
	public AccountTrialBalanceParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public String getCode() {
		return code;
	}
	public AccountTrialBalanceParams setCode(String code) {
		this.code = code;
		return this;
	}
	
	@Override
	public Integer getActivity() {
		return activity;
	}
	public AccountTrialBalanceParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	@Override
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountTrialBalanceParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public int getLevel() {
		return level;
	}
	public AccountTrialBalanceParams setLevel(int level) {
		this.level = level;
		return this;
	}
	
	public boolean isLowLevelAccountVisible() {
		return lowLevelAccountVisible;
	}
	public AccountTrialBalanceParams setLowLevelAccountVisible(boolean lowLevelAccountVisible) {
		this.lowLevelAccountVisible = lowLevelAccountVisible;
		return this;
	}
	
	public boolean isNoActivityAccountVisible() {
		return noActivityAccountVisible;
	}
	public AccountTrialBalanceParams setNoActivityAccountVisible(boolean noActivityAccountVisible) {
		this.noActivityAccountVisible = noActivityAccountVisible;
		return this;
	}
	
	public AccountTrialBalanceParams clone() {
		return new AccountTrialBalanceParams()
			.setDomain(getDomain())
			.setPeriod(getPeriod())
			.setFromDate(getFromDate())
			.setToDate(getToDate())
			.setCode(getCode())
			.setLevel(getLevel())
			.setActivity(getActivity())
			.setSecurityLevel(getSecurityLevel())
			.setLowLevelAccountVisible(isLowLevelAccountVisible())
			.setNoActivityAccountVisible(isNoActivityAccountVisible())
		;
	}
}
