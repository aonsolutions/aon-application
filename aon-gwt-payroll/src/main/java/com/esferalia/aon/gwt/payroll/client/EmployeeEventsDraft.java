package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public class EmployeeEventsDraft extends Composite {

	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cellFormat();
		String cellOddFormat();
		String isSelectedCell();
	}
	
	
	@UiField
	Button newValueButton;
	
	@UiField
	Label yearLabel;

	@UiField
	Button lastYearButton;

	@UiField
	Button nextYearButton;
	
	@UiField
	Grid eventsGrid;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<Integer>();
	
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
	
	@UiHandler("eventsGrid")
	public void onEventsGridClick(ClickEvent event) {
		
		event.preventDefault();
		
		newValueButton.setEnabled(true);
		
		int row = eventsGrid.getCellForEvent(event).getRowIndex();
		int col = eventsGrid.getCellForEvent(event).getCellIndex();
		
		if (col != 0){
			if(event.isControlKeyDown()){ //Cambiar por CTRL
				Integer previusPos = -1;
				
				if(!selectedPositions.getSelectedList().isEmpty()){
					previusPos = selectedPositions.getSelectedList().get(0);
					int previusRow = calculateRow(previusPos);
					if (row == previusRow)
						selectPosition(row,col);
				}
			}else{
				//Borrar selecciones anteriores
				eraseSelectedPositions();
				selectPosition(row,col);
			}
			
		}else{
			//Borrar selecciones anteriores
			eraseSelectedPositions();
			
			//Aplicar estilo seleccionado a una fila entera
			for (int j = 1; j < 13; j++){
				//Guardar en SelectionModel
				int selectPos = (row * 13) + j;
				selectedPositions.setSelected(selectPos, true);
				
				//Aplicar estilo seleccion a posicion
				eventsGrid.getWidget(row, j).setStyleName(style.isSelectedCell());
			
			}
		}
		
	}


	/**
	 * METEDOS AUXILIARES
	 */
	
	private void fillCellsEvents() {
		for (int row = 1; row < 17; row ++)
			for (int col = 1; col < 13; col ++){
				DoubleBox eventValue = new DoubleBox();
				eventValue.setStyleName(style.cellFormat());
				if (row % 2 == 1)
					eventValue.setStyleName(style.cellOddFormat());
				eventValue.setValue(row*col*1.00);
				eventsGrid.setWidget(row, col, eventValue);
			}	
	}
	
	private void changeYear(int change) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + change;
		yearLabel.setText(Integer.toString(newYear));
	}
	
	private void selectPosition(int row, int col) {
		//Guardar en SelectionModel
		int selectPos = (row * 13) + col;
		selectedPositions.setSelected(selectPos, true);
		
		//Aplicar estilo seleccion a posicion
		eventsGrid.getWidget(row, col).setStyleName(style.isSelectedCell());	
	}
	
	private void eraseSelectedPositions() {
		for (Integer position : selectedPositions.getSelectedList()){
			int row = calculateRow(position);
			int col = calculateCol(position);
			
			//Borrar estilos
			eventsGrid.getWidget(row, col).removeStyleName(style.isSelectedCell());
			
			//Aplicar estilo base
			eventsGrid.getWidget(row, col).setStyleName(style.cellFormat());
			if (row % 2 == 1)
				eventsGrid.getWidget(row, col).setStyleName(style.cellOddFormat());
			
		}
		selectedPositions.clear();		
	}

	private int calculateCol(Integer position) {
		return position % 13;
	}

	private int calculateRow(Integer position) {
		return position / 13;
	}
	
}
