package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.HasId;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class ReportDialog<T extends HasId<?>> extends SelectDialog<T> {


	interface Binder extends UiBinder<Widget, ReportDialog> {

	}


	private static final Binder binder = GWT.create(Binder.class);

	@UiField(provided=true)
	DateBox endDateBox;
	@UiField(provided=true)
	DateBox startDateBox;

	public ReportDialog() {

		Date today = new Date();
		DateBox.Format dateFormat = new DateBox.DefaultFormat(
				DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM));
		startDateBox = new DateBox(new DatePicker(), today, dateFormat );
		endDateBox = new DateBox(new DatePicker(), today, dateFormat );
		
		setWidget(binder.createAndBindUi(this));

	}

	// ------------------------------------------
	// Public
	// ------------------------------------------


	public Date getStartDate(){
		return startDateBox.getValue();
	}

	public Date getEndDate(){
		return endDateBox.getValue();
	}

	public void setEndDate(Date endDate) {
		endDateBox.setValue(endDate);
	}
	

	public void setStartDate(Date startDate) {
		startDateBox.setValue(startDate);
	}

}
