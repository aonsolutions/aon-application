package com.code.aon.accounting.util;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.accounting.Period;
import com.code.aon.common.AonVersion;

public class PeriodEntriesParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Period period;
	private boolean confidentialEntryPresent;

	private boolean operatingEntry;
	private Date operatingDate;
	private String operatingConcept;

	private boolean closingEntry;
	private Date closingDate;
	private String closingConcept;

	private boolean openingEntry;
	private Period openingPeriod;
	private boolean openingPeriodCreationEnabled;
	private Date openingDate;
	private String openingConcept;
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public boolean isConfidentialEntryPresent() {
		return confidentialEntryPresent;
	}
	public void setConfidentialEntryPresent(boolean confidentialEntryPresent) {
		this.confidentialEntryPresent = confidentialEntryPresent;
	}
	
	public boolean isOperatingEntry() {
		return operatingEntry;
	}
	public void setOperatingEntry(boolean operatingEntry) {
		this.operatingEntry = operatingEntry;
	}
	
	public Date getOperatingDate() {
		return operatingDate;
	}
	public void setOperatingDate(Date operatingDate) {
		this.operatingDate = operatingDate;
	}
	
	public String getOperatingConcept() {
		return operatingConcept;
	}
	public void setOperatingConcept(String operatingConcept) {
		this.operatingConcept = operatingConcept;
	}
	public boolean isClosingEntry() {
		return closingEntry;
	}
	public void setClosingEntry(boolean closingEntry) {
		this.closingEntry = closingEntry;
	}
	
	public Date getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}
	
	public String getClosingConcept() {
		return closingConcept;
	}
	public void setClosingConcept(String closingConcept) {
		this.closingConcept = closingConcept;
	}
	
	public boolean isOpeningEntry() {
		return openingEntry;
	}
	public void setOpeningEntry(boolean openingEntry) {
		this.openingEntry = openingEntry;
	}
	
	public Period getOpeningPeriod() {
		return openingPeriod;
	}
	public void setOpeningPeriod(Period openingPeriod) {
		this.openingPeriod = openingPeriod;
	}

	public boolean isOpeningPeriodCreationEnabled() {
		return openingPeriodCreationEnabled;
	}
	public void setOpeningPeriodCreationEnabled(boolean openingPeriodCreationEnabled) {
		this.openingPeriodCreationEnabled = openingPeriodCreationEnabled;
	}
	
	public Date getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

	public String getOpeningConcept() {
		return openingConcept;
	}
	public void setOpeningConcept(String openingConcept) {
		this.openingConcept = openingConcept;
	}
	
	public void initialize() {
		setConfidentialEntryPresent(false);
		setOperatingEntry(false);
		setOperatingDate(null);
		setOperatingConcept(null);
		setClosingEntry(false);
		setClosingDate(null);
		setClosingConcept(null);
		setOpeningEntry(false);
		setOpeningPeriod(null);
		setOpeningPeriodCreationEnabled(false);
		setOpeningDate(null);
		setOpeningConcept(null);
	}
	
}
