package com.esferalia.aon.in.payroll.excel;

import java.util.LinkedHashMap;
import java.util.Map;

public class AggregatedAnnualYearlyEntry {
	private String employeeName;
	private String nif;
	private String workplace;
	private Map<String, AggregatedAnnualEntry> monthlyEntries;
	
	public AggregatedAnnualYearlyEntry () {
		super();
		monthlyEntries = new LinkedHashMap<> ();
	}
	
	
	public AggregatedAnnualYearlyEntry(String employeeName, String nif, String workplace,
			Map<String, AggregatedAnnualEntry> monthlyEntries) {
		super();
		this.employeeName = employeeName;
		this.nif = nif;
		this.workplace = workplace;
		this.monthlyEntries = monthlyEntries != null ? monthlyEntries : new LinkedHashMap<> ();
	}


	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getWorkplace() {
		return workplace;
	}
	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	public Map<String, AggregatedAnnualEntry> getMonthlyEntries() {
		return monthlyEntries;
	}
	public void setMonthlyEntries(Map<String, AggregatedAnnualEntry> monthlyEntries) {
		this.monthlyEntries = monthlyEntries;
	}
	
	
}
