package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class CalcDialog<T extends HasId<?>> extends SelectDialog<T> {


	interface Binder extends UiBinder<Widget, CalcDialog> {

	}


	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	CheckBox saveCheckBox;

	@UiField
	MonthListBox monthListBox;

	@UiField
	RadioButton keepRadioButton;
	@UiField
	RadioButton overwriteRadioButton;
//	@UiField
//	RadioButton duplicateRadioButton;



	public CalcDialog() {

		// Create a DataGrid
		super();
		setCaption("Calcular...");
		setWidget(binder.createAndBindUi(this));
		monthListBox.setSelectedMonth(new Date());
	}

	// --------------------------------------------------------------- Handlers
	
	@Override
	public void show() {
		monthListBox.onResizeDropDownPopup();
		super.show();
	}

	@UiHandler("saveCheckBox")
	void onSaveClicked(ClickEvent event) {
		
		keepRadioButton.setEnabled(saveCheckBox.getValue());
		overwriteRadioButton.setEnabled(saveCheckBox.getValue());
//		duplicateRadioButton.setEnabled(saveCheckBox.getValue());
	
	}
	@UiHandler("monthListBox")
	void onMonthChange(ChangeEvent event) {
		// refresh range
		selectDataGrid.setVisibleRange(0, PAGE_SIZE);
	}

	// ----------------------------------------------------------------- Public

	public Date getMonth() {
		return monthListBox.getSelectedMonth();
	}


	public void setMonth(Date month) {
		monthListBox.setSelectedMonth(month);
	}

	public void setStartMonth(Date month) {
		monthListBox.setFirstMonth(month);
	}
	
	public void setEndMonth(Date month) {
		monthListBox.setLastMonth(month);
	}
	
	public void setCalculatedMonths(Set<Date> months){
		monthListBox.setHighLightMonths(months);
	}
	
	public boolean isSaveSelected() {
		return saveCheckBox.getValue();
	}
	
	public boolean isOverwriteSelected(){
		return overwriteRadioButton.getValue();
	}
	
	public boolean isDuplicateSelected(){
		return false; //duplicateRadioButton.getValue();
	}

	
}
