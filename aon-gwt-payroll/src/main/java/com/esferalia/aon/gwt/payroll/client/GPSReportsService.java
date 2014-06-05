package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.ReportData;

public interface GPSReportsService {

	public abstract ReportData getA3Report(Date month, int workplaces [])
			throws IllegalArgumentException;

	public abstract ReportData getFTEReport(Date start , Date end, int workplaces [])
			throws IllegalArgumentException;
	
	public abstract ReportData getHolidayReport(Date start , Date end, int workplaces [])
			throws IllegalArgumentException;
}