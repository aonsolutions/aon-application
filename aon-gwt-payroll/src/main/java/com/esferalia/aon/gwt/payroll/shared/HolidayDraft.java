package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class HolidayDraft implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1325839155341913285L;
	
	private Map<Date, String> localHolidays;	
	private Map<Date, String> autonomiHolidays;	
	private Map<Date, String> statalHolidays;
	private Map<Date, String> enterpriseHolidays;
	
	private String localTitle;
	private String autonomiTitle;
	private String statalTitle;
	private String enterpriseTitle;
	
	public HolidayDraft() {
		localHolidays = new LinkedHashMap<Date, String>();
		autonomiHolidays = new LinkedHashMap<Date, String>();
		statalHolidays = new LinkedHashMap<Date, String>();
		
		this.enterpriseTitle = new String("Propios Empresa");
	}
	
	public void setLocalTitle(String title) {
		this.localTitle = title;
	}
	
	public void setAutonomiTitle(String title) {
		this.autonomiTitle = title;
	}
	
	public void setStatalTitle(String title) {
		this.statalTitle = title;
	}
	
	public void addLocalHoliday(Map<Date, String> map) {
		this.localHolidays.putAll(map);
	}
	
	public void addAutonomiHolidays(Map<Date, String> map) {
		this.autonomiHolidays.putAll(map);
	}
	
	public void addStatalHoliday(Map<Date, String> map) {
		this.statalHolidays.putAll(map);
	}
	
	public void addEnterpriseHoliday(Map<Date, String> map) {
		this.enterpriseHolidays.putAll(map);
	}
	
	public String getLocalTitle() {
		return localTitle;
	}
	
	public String getAutonomiTitle() {
		return autonomiTitle;
	}
	
	public String getStatalTitle() {
		return statalTitle;
	}
	
	public String getEnterpriseTitle() {
		return enterpriseTitle;
	}
	
	public Map<Date, String> getLocalHolidays() {
		return Collections.unmodifiableMap(localHolidays);
	}
	
	public Map<Date, String> getAutonomicHolidays() {
		return Collections.unmodifiableMap(autonomiHolidays);
	}
	
	public Map<Date, String> getStatalHolidays() {
		return Collections.unmodifiableMap(statalHolidays);
	}
	
	public Map<Date, String> getEnterpriseHolidays() {
		return Collections.unmodifiableMap(enterpriseHolidays);
	}
}
