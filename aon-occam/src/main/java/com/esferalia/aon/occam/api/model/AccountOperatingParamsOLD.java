package com.esferalia.aon.occam.api.model;

import java.util.Date;
import java.util.HashSet;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountOperatingParamsOLD implements IAccountParams,Cloneable {

	private static final long serialVersionUID = 8075038329917800745L;
	
	public static String EMPTY_COST_CENTER_ACCOUNT = "CUENTAS SIN CENTRO DE COSTO";
	
	private int domain;
	private Integer period;
	private Date fromDate;
	private Date toDate;
	private int level;
	private Integer activity;
	private SecurityLevel securityLevel;
	private HashSet<String> costCenters;
	
	private boolean percentsEnabled;
	
	private int previousPeriods;
	private boolean byMonth;
	
	
	@Override
	public int getDomain() {
		return domain;
	}
	public AccountOperatingParamsOLD setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public Integer getPeriod() {
		return period;
	}
	public AccountOperatingParamsOLD setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	@Override
	public Date getFromDate() {
		return fromDate;
	}
	public AccountOperatingParamsOLD setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	@Override
	public Date getToDate() {
		return toDate;
	}
	public AccountOperatingParamsOLD setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	@Override
	public Integer getActivity() {
		return activity;
	}
	public AccountOperatingParamsOLD setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	@Override
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountOperatingParamsOLD setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public int getLevel() {
		return level;
	}
	public AccountOperatingParamsOLD setLevel(int level) {
		this.level = level;
		return this;
	}
	
	public int getPreviousPeriods() {
		return previousPeriods;
	}
	public AccountOperatingParamsOLD setPreviousPeriods(int previousPeriods) {
		this.previousPeriods = previousPeriods;
		return this;
	}
	
	public boolean showRatios() {
		return (getPreviousPeriods() == 0);
	}
	
	public boolean showIncreasePercent() {
		return (getPreviousPeriods() > 0);
	}

	public boolean isByMonth() {
		return byMonth;
	}
	public AccountOperatingParamsOLD setByMonth(boolean byMonth) {
		this.byMonth = byMonth;
		return this;
	}
	
	public boolean isPercentsEnabled() {
		return percentsEnabled;
	}
	public AccountOperatingParamsOLD setPercentsEnabled(boolean percentsEnabled) {
		this.percentsEnabled = percentsEnabled;
		return this;
	}
	public HashSet<String> getCostCenters() {
		return costCenters;
	}
	public AccountOperatingParamsOLD setCostCenters(HashSet<String> costCenters) {
		this.costCenters = costCenters;
		return this;
	}
	public AccountOperatingParamsOLD addCostCenter(String costCenter) {
		if (costCenters == null) setCostCenters(new HashSet<String>());
		if (!costCenters.contains(costCenter)) {
			costCenters.add(costCenter);
		}
		return this;
	}
	public AccountOperatingParamsOLD removeCostCenter(String costCenter) {
		if (costCenters == null) setCostCenters(new HashSet<String>());
		if (costCenters.contains(costCenter)) {
			costCenters.remove(costCenter);
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public AccountOperatingParamsOLD clone() {
		return new AccountOperatingParamsOLD()
			.setDomain(getDomain())
			.setPeriod(getPeriod())
			.setFromDate(getFromDate())
			.setToDate(getToDate())
			.setLevel(getLevel())
			.setActivity(getActivity())
			.setSecurityLevel(getSecurityLevel())
			.setPreviousPeriods(getPreviousPeriods())
			.setByMonth(isByMonth())
			.setPercentsEnabled(isPercentsEnabled())
			.setCostCenters((HashSet<String>) getCostCenters().clone())
		;
	}
}
