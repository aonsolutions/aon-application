package com.code.aon.accounting.report;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.common.enumeration.SecurityLevel;

public class OperationReportParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String domainName;
	private int domainId;
	
	private Period period;
	private Date fromDate;
	private Date toDate;
	private Date date;
	private boolean expenses;
	private boolean iva = true;
	private boolean irpf = true;

	private boolean coverVisible = false;
	private boolean counterVisible = false;
	private int pageCounter = 0;
	private SecurityLevel securityLevel;
	
	public OperationReportParams(String domainName, int domainId) {
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
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isExpenses() {
		return expenses;
	}
	public void setExpenses(boolean expenses) {
		this.expenses = expenses;
	}
	public boolean isCoverVisible() {
		return coverVisible;
	}
	public void setCoverVisible(boolean coverVisible) {
		this.coverVisible = coverVisible;
	}
	public boolean isCounterVisible() {
		return counterVisible;
	}
	public void setCounterVisible(boolean counterVisible) {
		this.counterVisible = counterVisible;
	}
	public int getPageCounter() {
		return pageCounter;
	}
	public void setPageCounter(int pageCounter) {
		this.pageCounter = pageCounter;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isIva() {
		return iva;
	}
	public void setIva(boolean iva) {
		this.iva = iva;
	}
	public boolean isIrpf() {
		return irpf;
	}
	public void setIrpf(boolean irpf) {
		this.irpf = irpf;
	}
	public void initialize(Period period,SecurityLevel securityLevel) {
		setPeriod(period);
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
		setSecurityLevel(securityLevel);
		setPageCounter(0);
		setCounterVisible(false);
		setCoverVisible(false);
		setExpenses(true);
	}
	
}
