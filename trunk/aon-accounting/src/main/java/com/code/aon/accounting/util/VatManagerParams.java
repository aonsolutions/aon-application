package com.code.aon.accounting.util;

import com.code.aon.accounting.Period;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;

public class VatManagerParams {
	private Period period;
	private SecurityLevel securityLevel;
	private boolean investment;
	private Series fromSeries;
	private Integer fromNumber;
	private Series toSeries;
	private Integer toNumber;

	private Series series;
	private Integer firstNumber;

	private boolean valid;
	private int count;
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public Series getFromSeries() {
		return fromSeries;
	}
	public void setFromSeries(Series fromSeries) {
		this.fromSeries = fromSeries;
	}
	public Integer getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}
	public Series getToSeries() {
		return toSeries;
	}
	public void setToSeries(Series toSeries) {
		this.toSeries = toSeries;
	}
	public Integer getToNumber() {
		return toNumber;
	}
	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}
	public boolean isInvestment() {
		return investment;
	}
	public void setInvestment(boolean investment) {
		this.investment = investment;
	}

	public Series getSeries() {
		return series;
	}
	public void setSeries(Series series) {
		this.series = series;
	}
	public Integer getFirstNumber() {
		return firstNumber;
	}
	public void setFirstNumber(Integer firstNumber) {
		this.firstNumber = firstNumber;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
}
