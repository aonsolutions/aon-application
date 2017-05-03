package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeEventsDraft extends Composite {

	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {
	}
	
	@UiField
	Grid eventsGrid;
	
	public EmployeeEventsDraft() {
		//Inicializamos la vista del gestor de incidencias
		initWidget(uiBinder.createAndBindUi(this));
		
		fillCellsEvents();
	}

	private void fillCellsEvents() {
		for (int row = 1; row < 17; row ++)
			for (int col = 1; col < 13; col ++){
				Label labelEvent = new Label("3.33");
				eventsGrid.setWidget(row, col, labelEvent);
			}

		
	}

}
