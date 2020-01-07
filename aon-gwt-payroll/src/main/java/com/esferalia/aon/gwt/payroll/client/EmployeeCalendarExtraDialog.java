package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarExtraDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarExtraDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bottonExpandStyle();
	}
	
	@UiField(provided = true)
	SuggestBox monthOpt;
	
	@UiField
	Button expandMonths;
	
	@UiField
	DoubleBox extraHoursBox;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------

	public EmployeeCalendarExtraDialog(String caption) {
		setCaption(caption);
		
		ArrayList<String> suggestMonths = new ArrayList<String>();
		MultiWordSuggestOracle oracleMonths = new MultiWordSuggestOracle();
		suggestMonths.add("Enero");
		suggestMonths.add("Febrero");
		suggestMonths.add("Marzo");
		suggestMonths.add("Abril");
		suggestMonths.add("Mayo");
		suggestMonths.add("Junio");
		suggestMonths.add("Julio");
		suggestMonths.add("Agosto");
		suggestMonths.add("Septiembre");
		suggestMonths.add("Octubre");
		suggestMonths.add("Noviembre");
		suggestMonths.add("Diciembre");
		oracleMonths.setDefaultSuggestionsFromText(suggestMonths);
		monthOpt = new SuggestBox(oracleMonths);
		
		setWidget(binder.createAndBindUi(this));
		
		expandMonths.setStyleName("aon-icon-down-arrow");
		expandMonths.addStyleName(style.bottonExpandStyle());
		
		expandMonths.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				monthOpt.showSuggestionList();	
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});	
		
	}

	protected abstract void onAccept();
	
	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHODS ----------------------------------
	// -------------------------------------------------------------------------------
	
	public String getSelectedMonth() {
		return monthOpt.getValue();
	}
	
	public double getExtraHours(){
		return extraHoursBox.getValue();
	}
}
