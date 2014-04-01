package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsSalaryResult extends JavaScriptObject {

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat("yyyy-MM-dd");

	protected JsSalaryResult() {
	}

	public final native String getStartDateString() /*-{
		return this.startDate;
	}-*/;

	public final native String getEndDateString() /*-{
		return this.endDate;
	}-*/;

	public final native int getEmployeeId() /*-{
		return this.employeeId;
	}-*/;

	public final native String getEmployeeName() /*-{
		return this.employeeName;
	}-*/;

	public final native int getEnterpriseId() /*-{
		return this.enterpriseName;
	}-*/;

	public final native String getEnterpriseName() /*-{
		return this.enterpriseName;
	}-*/;

	public final native int getWorkplaceId() /*-{
		return this.workplaceId;
	}-*/;

	public final native String getWorkplaceName() /*-{
		return this.workplaceName;
	}-*/;

	public final native double getTotalLiquid() /*-{
		return this.totalLiquid;
	}-*/;

	public final native double getTotalPayment() /*-{
		return this.totalPayment;
	}-*/;

	public final native double getTotalDeduction() /*-{
		return this.totalDeduction;
	}-*/;

	public final native boolean hasCounter() /*-{
		return this.counterTotalLiquid;
	}-*/;

	public final native double getCounterTotalLiquid() /*-{
		return this.counterTotalLiquid;
	}-*/;

	public final native double getCounterTotalPayment() /*-{
		return this.counterTotalPayment;
	}-*/;

	public final native double getCounterTotalDeduction() /*-{
		return this.counterTotalDeduction;
	}-*/;
	
	
	public final Date getStartDate() {
		return DATE_FORMAT.parse(getStartDateString());
	}

	public final Date getEndDate() {
		return DATE_FORMAT.parse(getEndDateString());
	}
	
}
