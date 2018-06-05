package com.esferalia.aon.occam.api.model;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountTrialBalanceParamsOLD implements IAccountParams,Cloneable {

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
	public AccountTrialBalanceParamsOLD setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public Integer getPeriod() {
		return period;
	}
	public AccountTrialBalanceParamsOLD setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	@Override
	public Date getFromDate() {
		return fromDate;
	}
	public AccountTrialBalanceParamsOLD setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	@Override
	public Date getToDate() {
		return toDate;
	}
	public AccountTrialBalanceParamsOLD setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public String getCode() {
		return code;
	}
	public AccountTrialBalanceParamsOLD setCode(String code) {
		this.code = code;
		return this;
	}
	
	@Override
	public Integer getActivity() {
		return activity;
	}
	public AccountTrialBalanceParamsOLD setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	@Override
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountTrialBalanceParamsOLD setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public int getLevel() {
		return level;
	}
	public AccountTrialBalanceParamsOLD setLevel(int level) {
		this.level = level;
		return this;
	}
	
	public boolean isLowLevelAccountVisible() {
		return lowLevelAccountVisible;
	}
	public AccountTrialBalanceParamsOLD setLowLevelAccountVisible(boolean lowLevelAccountVisible) {
		this.lowLevelAccountVisible = lowLevelAccountVisible;
		return this;
	}
	
	public boolean isNoActivityAccountVisible() {
		return noActivityAccountVisible;
	}
	public AccountTrialBalanceParamsOLD setNoActivityAccountVisible(boolean noActivityAccountVisible) {
		this.noActivityAccountVisible = noActivityAccountVisible;
		return this;
	}
	
	public AccountTrialBalanceParamsOLD clone() {
		return new AccountTrialBalanceParamsOLD()
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
