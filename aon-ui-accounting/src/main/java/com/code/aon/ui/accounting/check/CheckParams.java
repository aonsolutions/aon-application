package com.code.aon.ui.accounting.check;

import java.io.Serializable;

import com.code.aon.accounting.Period;
import com.code.aon.AonVersion;

public class CheckParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
