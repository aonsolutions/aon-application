package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsSalaryResult extends JavaScriptObject {
	
	private static DateTimeFormat DATE_FORMAT = null;

	protected JsSalaryResult() {
	}

	public final native String getStartDateString() /*-{
		return this.startDate;
	}-*/;

	public final native String getEndDateString() /*-{
		return this.endDate;
	}-*/;

	public final native String getChargeDateString() /*-{
		return this.chargeDate;
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
		return getDateTimeFormat().parse(
				getStartDateString());
	}

	public final Date getEndDate() {
		return getDateTimeFormat().parse(getEndDateString());
	}

	public final Date getChargeDate() {
		return getDateTimeFormat().parse(getChargeDateString());
	}

	private DateTimeFormat getDateTimeFormat() {
		if (DATE_FORMAT == null)
			DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
		return DATE_FORMAT;
	}
	
	public final String toDebugString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("JsSalaryResult {")
	      .append("\n  employeeId: ").append(getEmployeeId())
	      .append("\n  employeeName: ").append(getEmployeeName())
	      .append("\n  enterpriseId: ").append(getEnterpriseId())
	      .append("\n  enterpriseName: ").append(getEnterpriseName())
	      .append("\n  workplaceId: ").append(getWorkplaceId())
	      .append("\n  workplaceName: ").append(getWorkplaceName())
	      .append("\n  startDate: ").append(getStartDateString())
	      .append("\n  endDate: ").append(getEndDateString())
	      .append("\n  chargeDate: ").append(getChargeDateString())
	      .append("\n  totalLiquid: ").append(getTotalLiquid())
	      .append("\n  totalPayment: ").append(getTotalPayment())
	      .append("\n  totalDeduction: ").append(getTotalDeduction())
	      .append("\n  hasCounter: ").append(hasCounter())
	      .append("\n  counterTotalLiquid: ").append(getCounterTotalLiquid())
	      .append("\n  counterTotalPayment: ").append(getCounterTotalPayment())
	      .append("\n  counterTotalDeduction: ").append(getCounterTotalDeduction())
	      .append("\n}");
	    return sb.toString();
	}


}
