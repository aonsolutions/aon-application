package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.HasId;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class CalcDialog<T extends HasId<?>> extends SelectDialog<T> {


	interface Binder extends UiBinder<Widget, CalcDialog> {

	}


	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	CheckBox saveCheckBox;

	@UiField
	MonthListBox monthListBox;


	public CalcDialog() {

		// Create a DataGrid

		setCaption("Calcular...");
		setWidget(binder.createAndBindUi(this));

	}

	// ------------------------------------------
	// Handlers
	// ------------------------------------------

	@UiHandler("monthListBox")
	void onMonthChange(ChangeEvent event) {
		// refresh range
		selectDataGrid.setVisibleRange(0, PAGE_SIZE);
	}

	// ------------------------------------------
	// Public
	// ------------------------------------------

	public Date getMonth() {
		return monthListBox.getSelectedMonth();
	}


	public boolean isSaveSelected() {
		return saveCheckBox.getValue();
	}


	public void setMonth(Date startMonth, Date endMonth, Date actualMonth) {
		monthListBox.clear();
		monthListBox.setFirstMonth(startMonth);
		monthListBox.setLastMonth(endMonth);
		monthListBox.setSelectedMonth(actualMonth);
	}

}
