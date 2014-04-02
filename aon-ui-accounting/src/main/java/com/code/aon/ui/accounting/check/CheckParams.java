package com.code.aon.ui.accounting.check;

import com.code.aon.accounting.Period;

public class CheckParams {
	
	private Period period;
	private String domainName;
	private int domainId;

	public CheckParams(String domainName, int domainId) {
		this.domainName = domainName;
		this.domainId = domainId;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	
	
}
