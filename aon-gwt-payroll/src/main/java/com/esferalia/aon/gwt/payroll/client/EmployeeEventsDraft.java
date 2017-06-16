package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.payroll.client.EmployeeEventsDraftObject.EmployeeEventsVariable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
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
		String oddRowStyle();
		String firstHeadStyle();
		String cellFormat();
		String cellOddFormat();
		String isSelectedCell();
		String onChange();
		String setBlockVariableStyle();
	}
	
	
	@UiField
	MenuItem addNewValueMenuItem;
	
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
	Button undoAllButton;
	
	@UiField
	Button undoButton;
	
	@UiField
	Button redoButton;
	
	@UiField
	Button newValueButton;
	
	@UiField
	Button saveButton;
	
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
	
//	@UiField
//	Button calendarButton1;
//	
//	@UiField
//	Button calendarButton2;
//	
//	@UiField
//	Button calendarButton3;
//	
//	@UiField
//	Button calendarButton4;
//	
//	@UiField
//	Button calendarButton6;
//	
//	@UiField
//	Button calendarButton7;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<Integer>();
	private EmployeeEventsDraftObject employeeEventsDraft;
	private ArrayList<String> blockVariableList;
	
	public EmployeeEventsDraft() {
		//Inicializamos la vista del gestor de incidencias
		initWidget(uiBinder.createAndBindUi(this));
		
		//Inicializamos lista de variables que no son modificables desde el gestor de incidencias
		initializeBlockVariablesList();
		
		//Reescribir la accion del boton derecho del ratón dentro de la tabla
		eventsGrid.addDomHandler(this, ContextMenuEvent.getType());
		
//		calendarButton1.addClickHandler(e -> EmployeeTree.showEmployeeCalendar((this.employeeEventsDraft.getEmployeeCalendar())));
//		calendarButton2.addClickHandler(e -> EmployeeTree.showEmployeeCalendar((this.employeeEventsDraft.getEmployeeCalendar())));
//		calendarButton3.addClickHandler(e -> EmployeeTree.showEmployeeCalendar((this.employeeEventsDraft.getEmployeeCalendar())));
//		calendarButton4.addClickHandler(e -> EmployeeTree.showEmployeeCalendar((this.employeeEventsDraft.getEmployeeCalendar())));
//		calendarButton6.addClickHandler(e -> EmployeeTree.showEmployeeCalendar((this.employeeEventsDraft.getEmployeeCalendar())));
//		calendarButton7.addClickHandler(e -> EmployeeTree.showEmployeeCalendar((this.employeeEventsDraft.getEmployeeCalendar())));
		
		//Boton para añadir un nuevo valor
		addNewValueMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				openNewValueDialog();
			}
		});
		
		showYearMenuItem.setStyleName("aon-MenuItemCheckYes", true);
		
		//Boton para analizar que variables se quieren mostrar
		showVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				showVariablesDialog.open();
			}
		});
		
		//#ifndef env.SNAPSHOT
		//saveButton.setVisible(false);
		//#endif
		
		//TODO: para probar el boton de guardar del calendario -> saveButton.setVisible(true);
		saveButton.setVisible(true);
		
	}

	private void initializeBlockVariablesList() {
		this.blockVariableList = new ArrayList<String>();
		this.blockVariableList.add("DIAS_TRABAJADOS");
		this.blockVariableList.add("DIAS_EFECTIVOS");
		this.blockVariableList.add("DIAS_ERE");
		this.blockVariableList.add("DIAS_HUELGA");
		this.blockVariableList.add("DIAS_AUSENCIA");
		this.blockVariableList.add("HORAS_TRABAJADAS");
		this.blockVariableList.add("HORAS_COMPLEMENTARIAS");
		this.blockVariableList.add("DIAS_VACACIONES");
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
		changeYear(0);
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		employeeEventsDraft.updateDBCalendar(r -> 
		{
			setEmployeeEventsDraftObject(employeeEventsDraft);
			employeeEventsDraft.undoManager.discardAll();
		}, t -> {});
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
		
		int row = eventsGrid.getCellForEvent(event).getRowIndex();
		int col = eventsGrid.getCellForEvent(event).getCellIndex();
		
		if (checkBlockVariables(row)){
			return;
		}
		
		newValueButton.setEnabled(true);
		addNewValueMenuItem.setEnabled(true);
		
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

	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		this.employeeEventsDraft.undoManager.undo();
		fillCellsEvents();
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		this.employeeEventsDraft.undoManager.redo();
		fillCellsEvents();
	}
	
	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while (this.employeeEventsDraft.undoManager.canUndo())
			this.employeeEventsDraft.undoManager.undo();
		fillCellsEvents();
	}

	/**
	 * METEDOS AUXILIARES
	 */
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft) {
		
		this.employeeEventsDraft = employeeEventsDraft;
		
		//Window.alert("Employee ID :"+this.employeeEventsDraft.getIdEmployee());
		
		this.employeeEventsDraft.undoManager.addListener(new UndoManager.Listener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onChange(UndoManager undoManager) {
				undoAllButton.setEnabled(undoManager.canUndo());
				undoButton.setEnabled(undoManager.canUndo());
				redoButton.setEnabled(undoManager.canRedo());
				saveButton.setEnabled(undoManager.canUndo());
			}
		});
		
		//Pintar la tabla
		employeeEventsDraft.initializeDBCalendar(
				r -> { fillCellsEvents();
					 }, t -> {});
		
	}
	
	private void fillCellsEvents() {
		
		//Crear nuevas filas con las variables dadas
		for(String var : employeeEventsDraft.getEmployeeContractVariables()){
			createVariableRow(var);
		}
		
	}
	
    private void createVariableRow(String var) {
    	Integer actualYear = Integer.parseInt(yearLabel.getText()) - 1900;
    	Integer actualMonth = 0;
    	
    	int newRow = eventsGrid.insertRow(eventsGrid.getRowCount());
		
		if (newRow % 2 == 1)
			eventsGrid.getRowFormatter().addStyleName(newRow, style.oddRowStyle());
		
		HorizontalPanel headPanel = new HorizontalPanel();
		Label headLabel = new Label(var);
		headLabel.addStyleName(style.firstHeadStyle());
		headPanel.add(headLabel);
		
		if (this.blockVariableList.contains(var)){
			Button calendarButton = new Button();
			calendarButton.setStyleName("aon-editDataTable-button aon-icon-calendar");
			calendarButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					EmployeeTree.showEmployeeCalendar((employeeEventsDraft.getEmployeeCalendar()));
					
				}
			});
			
			headPanel.add(calendarButton);
			headLabel.addStyleName(style.setBlockVariableStyle());
		}else
			headLabel.removeStyleName(style.setBlockVariableStyle());
		
		if (newRow % 2 == 1)
			headLabel.addStyleName(style.cellOddFormat());
		
		eventsGrid.setWidget(newRow, 0, headPanel);
		
		//Rellenamos el resto de la fila
		
		ArrayList<EmployeeEventsVariable> varList = this.employeeEventsDraft.getListEmployeeEventsVaribales(var);
		
		for (int col = 1; col < eventsGrid.getColumnCount(); col++){
			Label eventValue = new Label();
			eventValue.addStyleName(style.cellFormat());
			if (newRow % 2 == 1)
				eventValue.addStyleName(style.cellOddFormat());
			
			if (this.blockVariableList.contains(var))
				eventValue.addStyleName(style.setBlockVariableStyle());
			else
				eventValue.removeStyleName(style.setBlockVariableStyle());
			
			if(null == varList){
				eventValue.setText("-");
				eventsGrid.setWidget(newRow, col, eventValue);
				continue;
			}
			
			EmployeeEventsVariable varMonth = this.employeeEventsDraft.getEmployeeEventsVariableByMonth(var, actualMonth, actualYear);
			
			if (null == varMonth){
				eventValue.setText("-");
				eventsGrid.setWidget(newRow, col, eventValue);
				actualMonth++;
				continue;
			}
			
			if (this.employeeEventsDraft.hasChanged(var, varMonth))
				eventValue.setStyleName(style.onChange());
			else
				eventValue.removeStyleName(style.onChange());
			
			eventValue.setText(varMonth.getValue().toString());
			eventsGrid.setWidget(newRow, col, eventValue);
			actualMonth++;
		}
		
	}

//	private void fillCellsEvents() {
//		Integer actualYear = Integer.parseInt(yearLabel.getText()) - 1900;
//		Boolean blockVariable = false;
//		for (int row = 1; row < 15; row ++){
//			Integer actualMonth = 0;
//			
//			String variableName = "";
//			Element element = eventsGrid.getWidget(row, 0).getElement().getFirstChildElement();
//			if(null != element)
//				variableName = element.getInnerText();
//			else
//				variableName = eventsGrid.getWidget(row, 0).getElement().getInnerText();
//			
//			if(this.employeeEventsDraft.isContractVariable(variableName)){
//				eventsGrid.getRowFormatter().addStyleName(row, style.ocultarFila());
//				continue;
//			}
//			
//			blockVariable = checkBlockVariables(row);
//			
//			if (blockVariable)
//				eventsGrid.getWidget(row, 0).setStyleName(style.setBlockVariableStyle());
//			else
//				eventsGrid.getWidget(row, 0).removeStyleName(style.setBlockVariableStyle());
//			
//			ArrayList<EmployeeEventsVariable> varList = this.employeeEventsDraft.getListEmployeeEventsVaribales(variableName);
//			
//			for (int col = 1; col < 13; col ++){
//			
//				Label eventValue = new Label();
//				eventValue.setStyleName(style.cellFormat());
//				if (row % 2 == 1)
//					eventValue.setStyleName(style.cellOddFormat());
//				
//				if (blockVariable)
//					eventValue.setStyleName(style.setBlockVariableStyle());
//				else
//					eventValue.removeStyleName(style.setBlockVariableStyle());
//				
//				if(null == varList){
//					eventValue.setText("-");
//					eventsGrid.setWidget(row, col, eventValue);
//					continue;
//				}
//				
//				EmployeeEventsVariable varMonth = this.employeeEventsDraft.getEmployeeEventsVariableByMonth(variableName, actualMonth, actualYear);
//				
//				if (null == varMonth){
//					eventValue.setText("-");
//					eventsGrid.setWidget(row, col, eventValue);
//					actualMonth++;
//					continue;
//				}
//				
//				if (this.employeeEventsDraft.hasChanged(variableName, varMonth))
//					eventValue.setStyleName(style.onChange());
//				else
//					eventValue.removeStyleName(style.onChange());
//				
//				eventValue.setText(varMonth.getValue().toString());
//				eventsGrid.setWidget(row, col, eventValue);
//				actualMonth++;
//			}	
//		}
//		
//	}
	
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
	
	private boolean checkBlockVariables(int row) {
		Element element = eventsGrid.getWidget(row, 0).getElement().getFirstChildElement();
		if(element == null)
			return false;
		
		String variableName = element.getInnerText();
		return this.blockVariableList.contains(variableName);
	}
	
	private void openNewValueDialog(){
		newValueDialog.open();
		int row = calculateRow(selectedPositions.getSelectedList().get(0));
		String variableName = eventsGrid.getWidget(row, 0).getElement().getInnerText();
		nameVariableDialog.getElement().setInnerText(variableName+" : ");
		//TODO: PORQUE NO FUNCIONA!!!
		newValueBox.setFocus(true);
	}
	
	private void addValueSelectedPositions(Double newValue) {
		ArrayList<Integer> months = new ArrayList<Integer>();
		int variableRow = calculateRow(selectedPositions.getSelectedList().get(0));
		String variableName = eventsGrid.getWidget(variableRow, 0).getElement().getInnerText();
		Integer actualYear = Integer.parseInt(yearLabel.getText()) - 1900;
		
		for (Integer position : selectedPositions.getSelectedList()){
			int col = calculateCol(position);
			Integer month = calculateMonthByColumn(col);
			
			months.add(month);
		}

		this.employeeEventsDraft.setValueByMonths(variableName, months, newValue, actualYear);
		
	}
	
	private Integer calculateMonthByColumn(int col) {
		return col-1;
	}

	private void changeYear(int change) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + change;
		yearLabel.setText(Integer.toString(newYear));
		fillCellsEvents();
		newValueButton.setEnabled(false);
		addNewValueMenuItem.setEnabled(false);
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
