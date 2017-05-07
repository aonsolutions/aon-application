package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeEventsDraft extends Composite {

	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {
	}
	
	@UiField
	Label yearLabel;

	@UiField
	Button lastYearButton;

	@UiField
	Button nextYearButton;
	
	@UiField
	Grid eventsGrid;
	
	
	public EmployeeEventsDraft() {
		//Inicializamos la vista del gestor de incidencias
		initWidget(uiBinder.createAndBindUi(this));
		
		fillCellsEvents();
	}

	/**
	 * UIHANDLERS
	 */
	
	@UiHandler("lastYearButton")
	public void onLastYearClick(ClickEvent event) {
		changeYear(-1);
	}

	@UiHandler("nextYearButton")
	public void onNextYearClick(ClickEvent event) {
		changeYear(1);
	}
	

	/**
	 * METEDOS AUXILIARES
	 */
	
	private void fillCellsEvents() {
		for (int row = 1; row < 17; row ++)
			for (int col = 1; col < 13; col ++){
				Label labelEvent = new Label("333.333");
				eventsGrid.setWidget(row, col, labelEvent);
			}	
	}
	
	private void changeYear(int change) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + change;
		yearLabel.setText(Integer.toString(newYear));
	}
	
}
