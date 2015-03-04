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
	
	
	private Integer id;
	private String description;
	
	private Map<Date, String> holidays;
	
	public HolidayDraft() {
		
		this.holidays = new LinkedHashMap<Date, String>();
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addHoliday(Date date, String description) {
		holidays.put(date, description);
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public Map<Date, String> getHolidaysMap() {
		return Collections.unmodifiableMap(holidays);
	}
	
	
	
}
