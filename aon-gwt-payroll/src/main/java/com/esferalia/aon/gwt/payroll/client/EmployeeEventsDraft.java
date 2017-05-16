package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.payroll.client.EmployeeEventsDraftObject.EmployeeEventsVariable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;
import com.vaadin.polymer.iron.widget.IronLabel;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperCheckbox;
import com.vaadin.polymer.paper.widget.PaperDialog;

public class EmployeeEventsDraft extends Composite implements ContextMenuHandler {

	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String ocultarFila();
		String cellFormat();
		String cellOddFormat();
		String isSelectedCell();
	}
	
	@UiField
	MenuItem showVariablesMenuItem;
	
	@UiField
	PaperCheckbox dtCheckBox1;
	
	@UiField
	PaperCheckbox deCheckBox2;
	
	@UiField
	PaperCheckbox deCheckBox3;
	
	@UiField
	PaperCheckbox dhCheckBox4;
	
	@UiField
	PaperCheckbox daCheckBox5;
	
	@UiField
	PaperCheckbox htCheckBox6;
	
	@UiField
	PaperCheckbox hcCheckBox7;
	
	@UiField
	PaperCheckbox dpCheckBox8;
	
	@UiField
	PaperCheckbox dmCheckBox9;
	
	@UiField
	PaperCheckbox dpeCheckBox10;
	
	@UiField
	PaperCheckbox dmeCheckBox11;
	
	@UiField
	PaperCheckbox kmCheckBox12;
	
	@UiField
	PaperCheckbox dvCheckBox13;
	
	@UiField
	PaperCheckbox jrCheckBox14;
	
	@UiField
	PaperCheckbox heCheckBox15;
	
	@UiField
	PaperCheckbox hefCheckBox16;
	
	@UiField
	PaperButton showVariablesDialogOk;
	
	@UiField
	MenuItem showYearMenuItem;
	
	@UiField
	Button newValueButton;
	
	@UiField
	Label yearLabel;
	
	@UiField
	PaperDialog showVariablesDialog;
	
	@UiField
	PaperDialog newValueDialog;
	
	@UiField
	IronLabel nameVariableDialog;
	
	@UiField
	DoubleBox newValueBox;
	
	@UiField
	PaperButton newValueDialogOk;

	@UiField
	Button lastYearButton;

	@UiField
	Button nextYearButton;
	
	@UiField
	Grid eventsGrid;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<Integer>();
	private EmployeeEventsDraftObject employeeEventsDraft;
	
	public EmployeeEventsDraft() {
		//Inicializamos la vista del gestor de incidencias
		initWidget(uiBinder.createAndBindUi(this));
		
		//Reescribir la accion del boton derecho del ratón dentro de la tabla
		eventsGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		showYearMenuItem.setStyleName("aon-MenuItemCheckYes", true);
		
		//Boton para analizar que variables se quieren mostrar
		showVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				showVariablesDialog.open();
			}
		});
		
	}

	/**
	 * UIHANDLERS
	 */
	
	@UiHandler("showVariablesDialogOk")
	public void onShowVariableDialogOkClick(ClickEvent event) {
		Boolean showingVar[] = new Boolean[16];
		fillShowingVarList(showingVar);
		for (int i = 0; i < 16; i++){
			if (showingVar[i]){
				eventsGrid.getRowFormatter().removeStyleName(i+1, style.ocultarFila());
			}else{
				eventsGrid.getRowFormatter().addStyleName(i+1, style.ocultarFila());
			}		
		}
	}

	@UiHandler("newValueButton")
	public void onNewValueClick(ClickEvent event) {
		openNewValueDialog();
	}
	
	@UiHandler("newValueDialogOk")
	public void onNewValueDialogOkClick(ClickEvent event) {
		addValueSelectedPositions(newValueBox.getValue());
		newValueBox.setText("");
		eraseSelectedPositions();
		newValueDialog.close();
	}

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
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft) {
		
		this.employeeEventsDraft = employeeEventsDraft;
		
		Window.alert("Employee ID :"+this.employeeEventsDraft.getIdEmployee());
		
		//Pintar la tabla
		fillCellsEvents();
		
	}
	
	private void fillCellsEvents() {
		for (int row = 1; row < 17; row ++){
			String variableName = eventsGrid.getWidget(row, 0).getElement().getInnerText();
			ArrayList<EmployeeEventsVariable> varList = this.employeeEventsDraft.getListEmployeeEventsVaribales(variableName);
			
			for (int col = 1; col < 13; col ++){
			
				TextBox eventValue = new TextBox();
				eventValue.setStyleName(style.cellFormat());
				if (row % 2 == 1)
					eventValue.setStyleName(style.cellOddFormat());
				
				if(null == varList){
					eventValue.setValue("-");
					eventsGrid.setWidget(row, col, eventValue);
					continue;
				}
				
				EmployeeEventsVariable varMonth = varList.get(col-1);
				
				if(null == varMonth){
					eventValue.setValue("-");
					eventsGrid.setWidget(row, col, eventValue);
					continue;
				}
				
				int month = varMonth.getStartDate().getMonth();				
				if (month == col-1){
					eventValue.setValue(varList.get(col-1).getValue().toString());
				}else{
					eventValue.setValue("-");
				}
				
				eventsGrid.setWidget(row, col, eventValue);
			}	
		}
		

//		for (int row = 1; row < 17; row ++)
//			for (int col = 1; col < 13; col ++){
//				TextBox eventValue = new TextBox();
//				eventValue.setStyleName(style.cellFormat());
//				if (row % 2 == 1)
//					eventValue.setStyleName(style.cellOddFormat());
//				eventValue.setValue(row*col*1.00+"");
//				eventsGrid.setWidget(row, col, eventValue);
//			}	
	}
	
	private void fillShowingVarList(Boolean[] showingVar) {
		showingVar[0] = dtCheckBox1.getChecked();
		showingVar[1] = deCheckBox2.getChecked();
		showingVar[2] = deCheckBox3.getChecked();
		showingVar[3] = dhCheckBox4.getChecked();
		showingVar[4] = daCheckBox5.getChecked();
		showingVar[5] = htCheckBox6.getChecked();
		showingVar[6] = hcCheckBox7.getChecked();
		showingVar[7] = dpCheckBox8.getChecked();
		showingVar[8] = dmCheckBox9.getChecked();
		showingVar[9] = dpeCheckBox10.getChecked();
		showingVar[10] = dmeCheckBox11.getChecked();
		showingVar[11] = kmCheckBox12.getChecked();
		showingVar[12] = dvCheckBox13.getChecked();
		showingVar[13] = jrCheckBox14.getChecked();
		showingVar[14] = heCheckBox15.getChecked();
		showingVar[15] = hefCheckBox16.getChecked();
	}
	
	private void openNewValueDialog(){
		newValueDialog.open();
		int row = calculateRow(selectedPositions.getSelectedList().get(0));
		String variableName = eventsGrid.getWidget(row, 0).getElement().getInnerText();
		nameVariableDialog.getElement().setInnerText(variableName+" : ");
	}
	
	private void addValueSelectedPositions(Double newValue) {
		for (Integer position : selectedPositions.getSelectedList()){
			int row = calculateRow(position);
			int col = calculateCol(position);
			
			TextBox widget = (TextBox) eventsGrid.getWidget(row, col);
			widget.setValue(newValue.toString());
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
	
	/**
	 * METODO PARA GESTIONAR BOTON DERECHO RATON
	 */
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if(!selectedPositions.getSelectedList().isEmpty()){
			ContextMenu menu = new  ContextMenu();
			
			menu.addItem("A"+String.valueOf("\u00f1")+"adir nuevo valor", new Command() {
				@Override
				public void execute() {
					openNewValueDialog();
				}
			});
			
			menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		    menu.show();
		}
	}

}
