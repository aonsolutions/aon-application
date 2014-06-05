package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.ReportData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GPSReportsServiceAsync {

	public abstract void getA3Report(Date month, int workplaces [],
			AsyncCallback<ReportData> callback) throws IllegalArgumentException;

	public abstract void getFTEReport(Date start, Date end, int workplaces [],
			AsyncCallback<ReportData> callback) throws IllegalArgumentException;

	public abstract void getHolidayReport(Date start, Date end, int workplaces [],
			AsyncCallback<ReportData> callback) throws IllegalArgumentException;
}