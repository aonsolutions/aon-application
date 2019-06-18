package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.payroll.client.EmployeeEventsDraftObject.EmployeeEventsVariable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public class EmployeeEventsDraft extends Composite implements ContextMenuHandler {

	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String calendarPosition();
		String firstHeadStyleHide();
		String cabeceraStyle();
		String ocultarFila();
		String oddRowStyle();
		String firstHeadStyle();
		String cellFormat();
		String cellOddFormat();
		String isSelectedCell();
		String onChange();
		String setBlockVariableStyle();
		String showVariablesStyle();
		String bgcWhite();
	}
	
	private class EventTableCell extends TextBox{
		
		int column;
		int row;
		
		public EventTableCell(int column, int row, String var) {
			super();
			this.column = column;
			this.row = row;
			
			//ESTILOS
			this.ensureDebugId(var.toLowerCase()+"_"+column);
			this.addStyleName(style.cellFormat());
			if (row % 2 == 1){
				this.removeStyleName(style.cellFormat());
				this.addStyleName(style.cellOddFormat());
			}
		}
		
		public int getColumn() {
			return column;
		}
		
		public int getRow() {
			return row;
		}
		
		public void setTextBoxValue(String text) {
			this.setText(text);
		}

		public void setBlockVariableStyle() {
			this.addStyleName(style.setBlockVariableStyle());
		}

		public void removeBlockVariableStyle() {
			this.removeStyleName(style.setBlockVariableStyle());
		}

		public void setOnChangeStyle() {
			this.setStyleName(style.onChange());	
		}

		public void removeOnChangeStyle() {
			this.removeStyleName(style.onChange());	
		}
			
	}
	
	
	@UiField
	MenuItem addNewValueMenuItem;
	
	@UiField
	MenuItem viewMenuItem;
	
	@UiField
	MenuItem showVariablesMenuItem;
	
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
	Button lastYearButton;

	@UiField
	Button nextYearButton;
	
	@UiField
	FlexTable eventsGrid;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<Integer>();
	private EmployeeEventsDraftObject employeeEventsDraft;
	private ArrayList<String> blockVariableList;
	private HashMap<String,CheckBox> showVariablesMap = new HashMap<String,CheckBox>();
	private HashMap<String,Integer> variablesRow = new HashMap<String,Integer>();
	
	public EmployeeEventsDraft() {
		//Inicializamos la vista del gestor de incidencias
		initWidget(uiBinder.createAndBindUi(this));
		
		//Inicializamos la cabecera de la tabla
		initializeTable();
		
		//Inicializamos lista de variables que no son modificables desde el gestor de incidencias
		initializeBlockVariablesList();
		
		//Reescribir la accion del boton derecho del ratón dentro de la tabla
		eventsGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		//Poner check al tipo de visualizacion
		showYearMenuItem.setStyleName("aon-MenuItemCheckYes", true);
		
		//Boton para añadir un nuevo valor
		addNewValueMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				openNewValueDialog();
			}
		});
		
		//Boton para analizar que variables se quieren mostrar
		showVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				EmployeeCheckBoxDialog checkBoxDialog = new EmployeeCheckBoxDialog(employeeEventsDraft.getEmployeeContractVariables().size()){
					@SuppressWarnings("deprecation")
					@Override
					protected void onAccept() {
						ArrayList<String> variablesLocalStore = new ArrayList<String>();
						
						for(String var : employeeEventsDraft.getEmployeeContractVariables()){
							 CheckBox check = showVariablesMap.get(var);
							 Integer row = calculateRowByVariableName(var);
							 UIObject.ensureDebugId(eventsGrid.getRowFormatter().getElement(row), "row_"+row);
							 
							 if(check.isChecked())
								 eventsGrid.getRowFormatter().removeStyleName(row, EmployeeEventsDraft.this.style.ocultarFila());
							 else{
								 eventsGrid.getRowFormatter().addStyleName(row, EmployeeEventsDraft.this.style.ocultarFila());
								 variablesLocalStore.add(var);
							 }
						}
						
						setVariablesLocalStorage(variablesLocalStore);	
					}
				};
				createShowVariablesDialog(checkBoxDialog);
				checkBoxDialog.show();
				checkBoxDialog.center();
			}

			private void createShowVariablesDialog(EmployeeCheckBoxDialog checkBoxDialog) {
				ArrayList<String> list = getVariablesLocalStorage();
				
				for(String var : employeeEventsDraft.getEmployeeContractVariables()){
					CheckBox checkBox = new CheckBox();
					
					if(list.contains(var))
						checkBox.setChecked(false);
					else
						checkBox.setChecked(true);
					
					showVariablesMap.put(var, checkBox);
					checkBoxDialog.addNewCheckBox(var, checkBox);
				}
			}
		});
		
		//#ifndef env.SNAPSHOT
		saveButton.setVisible(false);
		//#endif
		
		//TODO: para probar el boton de guardar del calendario -> saveButton.setVisible(true);
		saveButton.setVisible(true);
		
	}

	
	private void initializeTable() {
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.firstHeadStyleHide());
		eventsGrid.setWidget(0, 0, blankLabel);
		
		String months[] = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE",
						   "NOVIEMBRE", "DICIEMBRE"};
		
		for(int i=0; i<months.length; i++){
			eventsGrid.setText(0, i+1, months[i]);
			eventsGrid.getCellFormatter().addStyleName(0, i+1, style.cabeceraStyle());
		}	
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
		this.blockVariableList.add("SALARIO_ANUAL");
		this.blockVariableList.add("PAGAS");
	}

	/**
	 * UIHANDLERS
	 */
	
	@UiHandler("newValueButton")
	public void onNewValueClick(ClickEvent event) {
		openNewValueDialog();
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
		//Desactivar funcion predeterminada
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
			for (int j = 1; j < eventsGrid.getCellCount(row); j++){
				//Guardar en SelectionModel
				int selectPos = (row * eventsGrid.getCellCount(row)) + j;
				selectedPositions.setSelected(selectPos, true);
				
				//Aplicar estilo seleccion a posicion
				eventsGrid.getWidget(row, j).setStyleName(style.isSelectedCell());
			
			}
		}
		
	}

	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		this.employeeEventsDraft.undoManager.undo();
		refreshWindow();
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		this.employeeEventsDraft.undoManager.redo();
		refreshWindow();
	}
	
	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while (this.employeeEventsDraft.undoManager.canUndo())
			this.employeeEventsDraft.undoManager.undo();
		refreshWindow();
	}
	
	private void refreshWindow() {
		changeYear(0);
	}

	/**
	 * METEDOS AUXILIARES
	 */
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft) {
		
		clearEventsGrid();
		
		this.employeeEventsDraft = employeeEventsDraft;
		yearLabel.setText(""+ (new Date().getYear() + 1900));
		
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
		
		//Nombre variables para TEST
		newValueButton.ensureDebugId("new_value_complemento_i");
		undoButton.ensureDebugId("undo_complemento_i");
		redoButton.ensureDebugId("redo_complemento_i");
		viewMenuItem.ensureDebugId("view_menu_item");
		showVariablesMenuItem.ensureDebugId("show_variables_menu_item");
		
		//Descargar Variables actualizadas
		Integer actualYear = new Date().getYear();
		employeeEventsDraft.initializeDBEventsVariables(
				actualYear,
				r -> { fillCellsEvents(); },
				t -> {});
	
	
	}
	
	private void clearEventsGrid() {
		for (int i = eventsGrid.getRowCount() - 1; i > 0; i--)
			eventsGrid.removeRow(i);
	}
	
	private void fillCellsEvents() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		Storage storage = Storage.getLocalStorageIfSupported();
		
		if (null != storage){
			String stringList = storage.getItem("NO_MOSTRAR");
			if(null != stringList){
				String[] arrayList = stringList.split(",");
				
				for(int i = 0; i < arrayList.length; i++)
					list.add(arrayList[i]);
			}
			
		}
		
		//Crear nuevas filas con las variables dadas
		for(String var : employeeEventsDraft.getEmployeeContractVariables()){
			createVariableRow(var, list);
		}
		
	}
	
    private void createVariableRow(String var, ArrayList<String> list) {
    	Integer actualYear = Integer.parseInt(yearLabel.getText()) - 1900;
    	Integer actualMonth = 0;
    	
    	int newRow = eventsGrid.insertRow(eventsGrid.getRowCount());
    	variablesRow.put(var, newRow);
		
		if (newRow % 2 == 1)
			eventsGrid.getRowFormatter().addStyleName(newRow, style.oddRowStyle());
		
		HorizontalPanel headPanel = new HorizontalPanel();
		Label headLabel = new Label(var);
		headLabel.addStyleName(style.firstHeadStyle());
		
		/**
		 * CREAR IDs PARA TEST
		 */
		
		headLabel.ensureDebugId(var.toLowerCase());
		
		headPanel.add(headLabel);
		
		if (this.blockVariableList.contains(var)){
			Button calendarButton = new Button();
			calendarButton.setStyleName("aon-editDataTable-button aon-icon-calendar");
			calendarButton.addStyleName(style.calendarPosition());
			calendarButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					EmployeeTree.showEmployeeCalendar(employeeEventsDraft.getEmployeeCalendar());
				}
			});
			
			headPanel.add(calendarButton);
			headLabel.addStyleName(style.setBlockVariableStyle());
		}else
			headLabel.removeStyleName(style.setBlockVariableStyle());
		
		if (newRow % 2 == 1)
			headLabel.addStyleName(style.cellOddFormat());
		else
			headLabel.addStyleName(style.cellFormat());
		
		eventsGrid.setWidget(newRow, 0, headPanel);
		
		
		//Rellenamos el resto de la fila
		
		ArrayList<EmployeeEventsVariable> varList = this.employeeEventsDraft.getListEmployeeEventsVaribales(var);
		
		//Window.alert("Numero de columnas por fila :"+eventsGrid.getCellCount(newRow));
		for (int col = 1; col < 13 /*eventsGrid.getCellCount(newRow)*/; col++){
			
			EventTableCell eventCell = new EventTableCell(col, newRow, var);
			eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					//Borrar selecciones anteriores
					eraseSelectedPositions();
					selectPosition(eventCell.getRow(), eventCell.getColumn());
					if(eventCell.getText() == "" || eventCell.getText() == "-"){
						addValueSelectedPositions(null);
						eventCell.setText("-");
					}else
						addValueSelectedPositions(Double.parseDouble(eventCell.getText()));
					eraseSelectedPositions();
					eventCell.addStyleName(style.onChange());	
				}
			});
			
			if (this.blockVariableList.contains(var)){
				eventCell.setBlockVariableStyle();
				eventCell.setEnabled(false);
			}else{
				eventCell.removeBlockVariableStyle();
			}
			
			if(null == varList){
				eventCell.setTextBoxValue("-");
				eventsGrid.setWidget(newRow, col, eventCell);
				continue;
			}
			
			EmployeeEventsVariable varMonth = this.employeeEventsDraft.getEmployeeEventsVariableByMonth(var, actualMonth, actualYear);
			
			if (newRow % 2 == 1) {
				eventCell.removeStyleName(style.bgcWhite());
			}else {
				eventCell.addStyleName(style.bgcWhite());
			}
			
			if (null == varMonth){
				eventCell.setTextBoxValue("-");
				eventsGrid.setWidget(newRow, col, eventCell);
				if (newRow % 2 != 1)
					eventsGrid.getWidget(newRow, col).addStyleName(style.bgcWhite());
				actualMonth++;
				continue;
			}
			
			if (this.employeeEventsDraft.hasChanged(var, varMonth))
				eventCell.setOnChangeStyle();
			else
				eventCell.removeOnChangeStyle();
			
			eventCell.setTextBoxValue(varMonth.getValue().toString());
			eventsGrid.setWidget(newRow, col, eventCell);
			if (newRow % 2 != 1)
				eventsGrid.getWidget(newRow, col).addStyleName(style.bgcWhite());
			actualMonth++;
			
//			Window.alert("Text Box Value :"+eventCell.getTextBox().getText());
		}
		
		if(list.contains(var))
			eventsGrid.getRowFormatter().addStyleName(newRow, style.ocultarFila());
		else
			eventsGrid.getRowFormatter().removeStyleName(newRow, style.ocultarFila());
		
	}
    
    private Integer calculateRowByVariableName(String var) {
    	return variablesRow.get(var);
	}
	
	private boolean checkBlockVariables(int row) {
		Element element = eventsGrid.getWidget(row, 0).getElement().getFirstChildElement();
		if(element == null)
			return false;
		
		String variableName = element.getInnerText();
		return this.blockVariableList.contains(variableName);
	}
	
	private void openNewValueDialog(){
		int row = calculateRow(selectedPositions.getSelectedList().get(0));
		String variableName = eventsGrid.getWidget(row, 0).getElement().getInnerText();
		
		EmployeeInputDialog inputDialog = new EmployeeInputDialog(){
			@Override
			protected void onAccept() {
				// TODO Auto-generated method stub	
				String value = this.getValue();
				setNewValue(Double.parseDouble(value));
			}
		};
		inputDialog.setNameLabel(variableName+" : ");	
		inputDialog.show();
		inputDialog.center();
		inputDialog.setFocusOnValueTextBox(true);
	}
	
	protected void setNewValue(Double value) {
		addValueSelectedPositions(value);
		eraseSelectedPositions();
		changeYear(0);	
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
		clearEventsGrid();
		fillCellsEvents();
		newValueButton.setEnabled(false);
		addNewValueMenuItem.setEnabled(false);
	}
	
	private void selectPosition(int row, int col) {
		//Guardar en SelectionModel
		int selectPos = (row * eventsGrid.getCellCount(row)) + col;
		selectedPositions.setSelected(selectPos, true);
		
		//Aplicar estilo seleccion a posicion
		//eventsGrid.getWidget(row, col).setStyleName(style.isSelectedCell());	
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
		return position % eventsGrid.getCellCount(calculateRow(position));
	}

	private int calculateRow(Integer position) {
		return position / eventsGrid.getCellCount(0);
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
	
	/**
	 * GETTER Y SETTER LOCAL STORAGE 
	 */
	
	protected ArrayList<String> getVariablesLocalStorage() {
		ArrayList<String> list = new ArrayList<String>();
		
		Storage storage = Storage.getLocalStorageIfSupported();
		
		if (null != storage){
			String stringList = storage.getItem("NO_MOSTRAR");
			if(null != stringList){
				String[] arrayList = stringList.split(",");
				
				for(int i = 0; i < arrayList.length; i++)
					list.add(arrayList[i]);
			}
			
		}
		
		return list;
	}
	
	private void setVariablesLocalStorage(ArrayList<String> variablesLocalStore) {
		Storage storage = Storage.getLocalStorageIfSupported();
		
		if(null != storage){
			String noShowVar = "";
			for(String var : variablesLocalStore)
				noShowVar += var+",";
			
			storage.removeItem("NO_MOSTRAR");
			storage.setItem("NO_MOSTRAR", noShowVar);
		}
	}



}
