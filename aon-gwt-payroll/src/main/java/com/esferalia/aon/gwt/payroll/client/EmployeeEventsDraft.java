package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
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

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {}
	
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
		
	}
	
	@UiField
	MenuItem showVariablesMenuItem;
	
	@UiField
	MenuItem undoAllMenuItem;
	
	@UiField
	Button undoAllButton;
	
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
	
	// ---------------------------- VARIABLES
	
	private EmployeeEventsDraftObject employeeEventsDraft;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<Integer>();
	
	private HashMap<String,CheckBox> showVariablesMap = new HashMap<String,CheckBox>();
	private HashMap<String,Integer> variablesRow = new HashMap<String,Integer>();
	
	private int year = new Date().getYear();
	// ---------------------------- CONSTRUCTOR
	
	public EmployeeEventsDraft() {
		//Inicializamos la vista del gestor de incidencias
		initWidget(uiBinder.createAndBindUi(this));
		
		//Inicializamos la cabecera de la tabla
		initializeTable();
		
		//Reescribir la accion del boton derecho del ratón dentro de la tabla
		eventsGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		//Boton para analizar que variables se quieren mostrar
		showVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				createEmployeeCheckBoxDialog();	
			}
		});
		
		undoAllMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initUndoAllDialog();
			}
		});
		
		//#ifndef env.SNAPSHOT
		saveButton.setVisible(false);
		//#endif
		
		//TODO: para probar el boton de guardar del calendario -> saveButton.setVisible(true);
		saveButton.setVisible(true);
		
		saveButton.setEnabled(false);
		undoAllButton.setEnabled(false);
		
	}

	// ---------------------------- UI HANDLERS
	
	@UiHandler("newValueButton")
	public void onNewValueClick(ClickEvent event) {
		openNewValueDialog(null);
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		employeeEventsDraft.updateDBCalendar(r -> 
		{
			changeYear(0);
			saveButton.setEnabled(false);
			undoAllButton.setEnabled(false);
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
			
			String variableName = eventsGrid.getWidget(row, 0).getElement().getInnerText();
			openNewValueDialog(variableName);
		}
		
	}
	
	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		initUndoAllDialog();
	}

	// ---------------------------- SET EMPLOYEE EVENTS DRAFT
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft) {
		
		clearEventsGrid();
		
		this.employeeEventsDraft = employeeEventsDraft;
		yearLabel.setText(""+ (new Date().getYear() + 1900));
		
		//Nombre variables para TEST
		newValueButton.ensureDebugId("new_value_complemento_i");
		showVariablesMenuItem.ensureDebugId("show_variables_menu_item");
		
		//Descargar Variables actualizadas
		Integer actualYear = new Date().getYear();
		employeeEventsDraft.initializeDBEventsVariables(
				actualYear,
				r -> { fillCellsEvents(); },
				t -> {});
	}
	
	// ---------------------------- AUX METHODS (PAINT TABLE)
	
	private void clearEventsGrid() {
		for (int i = eventsGrid.getRowCount() - 1; i > 0; i--)
			eventsGrid.removeRow(i);
	}

	private void initializeTable() {
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.firstHeadStyleHide());
		eventsGrid.setWidget(0, 0, blankLabel);
		
		String months[] = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT",
						   "NOV", "DIC"};
		
		for(int i=0; i<months.length; i++){
			eventsGrid.setText(0, i+1, months[i]);
			eventsGrid.getCellFormatter().addStyleName(0, i+1, style.cabeceraStyle());
		}	
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
		
		headLabel.ensureDebugId(var.toLowerCase());
		
		headPanel.add(headLabel);
		
		if (employeeEventsDraft.isCalendarVariable(var)){
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
		}else {
			headLabel.removeStyleName(style.setBlockVariableStyle());
			headPanel.getElement().getStyle().setWidth(100, Unit.PCT);
			headLabel.getElement().getStyle().setTextAlign(TextAlign.LEFT);;
		}
		
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
					if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-"){
						addValueSelectedPositions(null);
						eventCell.setText("-");
					}else
						addValueSelectedPositions(Double.parseDouble(eventCell.getText()));
					eraseSelectedPositions();
					eventCell.addStyleName(style.onChange());	
				}
			});
			
			eventCell.addFocusHandler(new FocusHandler() {
				
				@Override
				public void onFocus(FocusEvent event) {
					if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
						eventCell.setValue("");
				}
			});
			
			eventCell.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					if(StringUtils.isBlank(eventCell.getValue()))
						eventCell.setValue("-");
				}
			});
			
			if (employeeEventsDraft.isCalendarVariable(var)){
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
			
			eventCell.setTextBoxValue(varMonth.getValue().toString());
			eventsGrid.setWidget(newRow, col, eventCell);
			if (newRow % 2 != 1)
				eventsGrid.getWidget(newRow, col).addStyleName(style.bgcWhite());
			actualMonth++;
		}
		
		if(list.contains(var))
			eventsGrid.getRowFormatter().addStyleName(newRow, style.ocultarFila());
		else
			eventsGrid.getRowFormatter().removeStyleName(newRow, style.ocultarFila());
		
	}
    
 // ---------------------------- AUX METHODS
    
    private Integer calculateRowByVariableName(String var) {
    	return variablesRow.get(var);
	}
	
	private boolean checkBlockVariables(int row) {
		Element element = eventsGrid.getWidget(row, 0).getElement().getFirstChildElement();
		if(element == null)
			return false;
		
		String variableName = element.getInnerText();
		return employeeEventsDraft.isCalendarVariable(variableName);
	}

	private void initUndoAllDialog() {
		AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", String.valueOf("\u00BF")+"RESTAURAR INCIDENCIAS con los valores de la " + String.valueOf("\u00FA") + "ltima versi" + String.valueOf("\u00F3") + "n guardada?") {
			@Override
			protected void onAccept() {
				clearEventsGrid();
				Integer actualYear = new Date().getYear();
				employeeEventsDraft.initializeDBEventsVariables(
						actualYear,
						r -> { 
							fillCellsEvents();
							saveButton.setEnabled(true);
							undoAllButton.setEnabled(true);
						},
						t -> {});
			}
		};
		
		dialog.center();
		dialog.show();	
	}
	
	private void openNewValueDialog(String variableName){
		if(null == variableName)
			variableName = "Nuevo valor";
		
		ArrayList<String> filterVariables = getVariablesWithOutContract();
		
		EmployeeInputDialog inputDialog = new EmployeeInputDialog(
				variableName, 
				employeeEventsDraft.getContractStartDate(),
				employeeEventsDraft.getContractEndDate(),
				filterVariables,
				null){
			@Override
			protected void onAccept() {
				Date startDate = getStartDate();
				Date endDate = getEndDate();
				String value = getValue();
				String variable = getVariableName();
				
//				Window.alert("startDate : " + startDate + " endDate : " + endDate + " -> " + variable + " = " + value);
				
				addEventVar(variable, value, startDate, endDate);
			}
		};
		
		inputDialog.show();
		inputDialog.center();
	}
	
	private void addEventVar(String variable, String value, Date startDate, Date endDate) {
		employeeEventsDraft.setValueByMonth(variable, value, startDate, endDate);
		changeYear(0);
		saveButton.setEnabled(true);
		undoAllButton.setEnabled(true);
	}
	
	private ArrayList<String> getVariablesWithOutContract() {
		ArrayList<String> result = new ArrayList<String>();
		
		ArrayList<String> allEmployeeVariables = employeeEventsDraft.getEmployeeContractVariables();
		ArrayList<String> calendarVariables = employeeEventsDraft.getCalendarVariables();
		
		for(String var : allEmployeeVariables) {
			if(calendarVariables.contains(var))
				continue;
			result.add(var);
		}
		
		return result;
	}
	
	private void addValueSelectedPositions(Double valueD) {
		int variableRow = calculateRow(selectedPositions.getSelectedList().get(0));
		String variable = eventsGrid.getWidget(variableRow, 0).getElement().getInnerText();
		
		Integer actualYear = Integer.parseInt(yearLabel.getText()) - 1900;
		
		int colStart = calculateCol(selectedPositions.getSelectedList().get(0));
		Integer monthStart = calculateMonthByColumn(colStart);
		
		int colEnd = calculateCol(selectedPositions.getSelectedList().get(selectedPositions.getSelectedList().size() - 1));
		Integer monthEnd = calculateMonthByColumn(colEnd);
		
		Date startDate = new Date(actualYear, monthStart, 1);
		DateUtils.resetTime(startDate);
		
		Date endDateAux = new Date(actualYear, monthEnd, 1);
		Date endDate = DateUtils.getLastDayOfMonth(endDateAux);
		DateUtils.resetTime(endDate);
		
		String value = null;
		if(null != valueD)
			value = valueD.toString();
		
		addEventVar(variable, value, startDate, endDate);
	}
	
	private Integer calculateMonthByColumn(int col) {
		return col-1;
	}

	private void changeYear(int changeYear) {
		this.year += changeYear;
		
		Date endOfNewYear = new Date(this.year, 11, 31);
		
		if(isOutOfContractView(endOfNewYear)) {
			this.year -= changeYear;
		} else {
			this.yearLabel.setText((year + 1900)+"");
			
			int actualYear = new Date().getYear();
			
			if(actualYear - year == 1) {
				lastYearButton.setEnabled(false);
				nextYearButton.setEnabled(true);
			}else if (actualYear - year == -1) {
				lastYearButton.setEnabled(true);
				nextYearButton.setEnabled(false);
			} else {
				lastYearButton.setEnabled(true);
				nextYearButton.setEnabled(true);
			}
			
			clearEventsGrid();
			fillCellsEvents();
		}
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
	
	private boolean isOutOfContractView(Date date) {
		Date newEndDate = this.employeeEventsDraft.getContractEndDate();
		if(null == newEndDate) {
			Integer nextYear = new Date().getYear() + 1;
			newEndDate = new Date(nextYear, 11, 31);
		}
		return (date.before(this.employeeEventsDraft.getContractStartDate()) && date.getYear() !=  this.employeeEventsDraft.getContractStartDate().getYear()) || 
				(date.after(newEndDate) && date.getYear() != newEndDate.getYear());
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
					openNewValueDialog(null);
				}
			});
			
			menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		    menu.show();
		}
	}

	// ---------------------------- SHOW / HIDE VARIABLES
	
	private void createEmployeeCheckBoxDialog() {
		EmployeeCheckBoxDialog checkBoxDialog = new EmployeeCheckBoxDialog(){
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
	
	// ---------------------------- LOCAL STORAGE
	
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
