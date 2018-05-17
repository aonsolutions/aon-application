package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountOperatingParams implements Serializable,Cloneable {

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
	
	
	public int getDomain() {
		return domain;
	}
	public AccountOperatingParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getPeriod() {
		return period;
	}
	public AccountOperatingParams setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public AccountOperatingParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}
	public AccountOperatingParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Integer getActivity() {
		return activity;
	}
	public AccountOperatingParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountOperatingParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public int getLevel() {
		return level;
	}
	public AccountOperatingParams setLevel(int level) {
		this.level = level;
		return this;
	}
	
	public int getPreviousPeriods() {
		return previousPeriods;
	}
	public AccountOperatingParams setPreviousPeriods(int previousPeriods) {
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
	public AccountOperatingParams setByMonth(boolean byMonth) {
		this.byMonth = byMonth;
		return this;
	}
	
	public boolean isPercentsEnabled() {
		return percentsEnabled;
	}
	public AccountOperatingParams setPercentsEnabled(boolean percentsEnabled) {
		this.percentsEnabled = percentsEnabled;
		return this;
	}
	public HashSet<String> getCostCenters() {
		return costCenters;
	}
	public AccountOperatingParams setCostCenters(HashSet<String> costCenters) {
		this.costCenters = costCenters;
		return this;
	}
	public AccountOperatingParams addCostCenter(String costCenter) {
		if (costCenters == null) setCostCenters(new HashSet<String>());
		if (!costCenters.contains(costCenter)) {
			costCenters.add(costCenter);
		}
		return this;
	}
	public AccountOperatingParams removeCostCenter(String costCenter) {
		if (costCenters == null) setCostCenters(new HashSet<String>());
		if (costCenters.contains(costCenter)) {
			costCenters.remove(costCenter);
		}
		return this;
	}
	
	public AccountOperatingParams clone() {
		return new AccountOperatingParams()
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
		;
	}
}
