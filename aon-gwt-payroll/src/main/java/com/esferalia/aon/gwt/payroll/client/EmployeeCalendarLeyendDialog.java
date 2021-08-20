package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarLeyendDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarLeyendDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	Label nonWorkingDays;
	
	@UiField
	Label effectiveDays;
	
	@UiField
	Label inactivityDays;
	
	@UiField
	Label strikeDays;
	
	@UiField
	Label ereDays;
	
	@UiField
	Label ereFZADays;
	
	@UiField
	Label ereFZAExonDays;
	
	@UiField
	Label dropDays;
	
	@UiField
	Label holidayDays;
	
	@UiField
	Label festiveDays;
	
	@UiField
	Label leaveDays;
	
	@UiField
	Label peonadasDays;
	
	public EmployeeCalendarLeyendDialog() {
		setCaption("Leyenda");
		setWidget(binder.createAndBindUi(this));
		initAccumulateTypeDays();
	}

	private void initAccumulateTypeDays() {
		Integer noWorkingDaysCount = getTotalYearDays(DayType.NOWORKINGDAY);
		nonWorkingDays.setText(0 == noWorkingDaysCount ? "" : "(" + noWorkingDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer effectiveDaysCount = getTotalYearDays(DayType.EFFECTIVE);
		effectiveDays.setText(0 == effectiveDaysCount ? "" : "(" + effectiveDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer inactivityDaysCount = getTotalYearDays(DayType.INACTIVITY);
		inactivityDays.setText(0 == inactivityDaysCount ? "" : "(" + inactivityDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer strikeDaysCount = getTotalYearDays(DayType.STRIKEDAY);
		strikeDays.setText(0 == strikeDaysCount ? "" : "(" + strikeDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer ereDaysCount = getTotalYearDays(DayType.EREDAY);
		ereDays.setText(0 == ereDaysCount ? "" : "(" + ereDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer ereFZADaysCount = getTotalYearDays(DayType.EREFZADAY);
		ereFZADays.setText(0 == ereFZADaysCount ? "" : "(" + ereFZADaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer ereFZAExonDaysCount = getTotalYearDays(DayType.EREFZAEXONDAY);
		ereFZAExonDays.setText(0 == ereFZAExonDaysCount ? "" : "(" + ereFZAExonDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer dropDaysCount = getTotalYearDays(DayType.DROPDAY);
		dropDays.setText(0 == dropDaysCount ? "" : "(" + dropDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer festiveDaysCount = getTotalYearDays(DayType.FREEDAY);
		festiveDays.setText(0 == festiveDaysCount ? "" : "(" + festiveDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer leaveDaysCount = getTotalYearDays(DayType.BAJAIT);
		leaveDays.setText(0 == leaveDaysCount ? "" : "(" + leaveDaysCount + " d\u00EDas/a\u00F1o)");
		
		Integer peonadasDaysCount = getTotalYearDays(DayType.REAL_DAYS);
		peonadasDays.setText(0 == peonadasDaysCount ? "" : "(" + peonadasDaysCount + " d\u00EDas/a\u00F1o)");
	}

	protected abstract Integer getTotalYearDays(DayType realDays);

}
