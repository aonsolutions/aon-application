package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class EventsDraft extends ResizeComposite {

	interface Binder extends UiBinder<Widget, EventsDraft> {}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String blankHeaderCell();
		String headerCell();
		String oddRowColor();
		String onChange();
		String eventCell();
		String setBlockCellStyle();
		String aonCheck();
		String pointer();
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	private class EventTableCell extends TextBox{
		
		int column;
		int row;
		
		public EventTableCell(int row, int column) {
			super();
			this.column = column;
			this.row = row;
			
			//ESTILOS
			this.addStyleName(style.eventCell());
			if (row % 2 != 0){
				this.addStyleName(style.oddRowColor());
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
			this.addStyleName(style.setBlockCellStyle());
		}

		public void removeBlockVariableStyle() {
			this.removeStyleName(style.setBlockCellStyle());
		}
			
	}
	
	@UiField
	MenuItem calendarVariablesMenuItem;
	
	@UiField
	MenuItem editableVariablesMenuItem;
	
	@UiField
	MenuItem allVariablesMenuItem;

	@UiField
	FlexTable eventsTable;
	
	@UiField
	Button saveButton;
	
	@UiField
	MenuItem undoAllMenuItem;
	
	@UiField
	Button undoAllButton;
	
	@UiField
	ListBox typeView;
	
	@UiField
	HTMLPanel varListViewPanel;
	
	@UiField
	ListBox varListView;
	
	@UiField
	Button newValueButton;
	
	@UiField
	Label dateLabel;
	
	@UiField
	Button previusDateButton;
	
	@UiField
	Button nextDateButton;

	
	private EventsDraftObject eventsDraftObject;
	
	private Integer actualMonth;
	private Integer actualYear;
	
	private Integer variablesToShow = 0; // 0 = ALL_VARIABLES -- 1 = EDITABLE_VARS -- 2 = CALENDAR_VARS

	public EventsDraft() {
		initWidget(binder.createAndBindUi(this));
		
		calendarVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				calendarVariablesMenuItem.addStyleName("aon-MenuItemCheckYes");
				calendarVariablesMenuItem.addStyleName(style.aonCheck());
				
				editableVariablesMenuItem.removeStyleName("aon-MenuItemCheckYes");
				editableVariablesMenuItem.removeStyleName(style.aonCheck());
				
				allVariablesMenuItem.removeStyleName("aon-MenuItemCheckYes");
				allVariablesMenuItem.removeStyleName(style.aonCheck());
				
				variablesToShow = 2;
				
				initializeView(); 
			}
		});
		
		editableVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				editableVariablesMenuItem.addStyleName("aon-MenuItemCheckYes");
				editableVariablesMenuItem.addStyleName(style.aonCheck());
				
				calendarVariablesMenuItem.removeStyleName("aon-MenuItemCheckYes");
				calendarVariablesMenuItem.removeStyleName(style.aonCheck());
				
				allVariablesMenuItem.removeStyleName("aon-MenuItemCheckYes");
				allVariablesMenuItem.removeStyleName(style.aonCheck());
				
				variablesToShow = 1;
				
				initializeView(); 
			}
		});
		
		allVariablesMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				allVariablesMenuItem.addStyleName("aon-MenuItemCheckYes");
				allVariablesMenuItem.addStyleName(style.aonCheck());
				
				editableVariablesMenuItem.removeStyleName("aon-MenuItemCheckYes");
				editableVariablesMenuItem.removeStyleName(style.aonCheck());
				
				calendarVariablesMenuItem.removeStyleName("aon-MenuItemCheckYes");
				calendarVariablesMenuItem.removeStyleName(style.aonCheck());
				
				variablesToShow = 0;
				
				initializeView(); 
			}
		});
		
		undoAllMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initUndoAllDialog();
			}
		});
	}

	// -------------------------------------
	//             UI HANDLER
	// -------------------------------------
	@UiHandler("newValueButton")
	public void onNewValueButtonClick(ClickEvent event) {
		EventsInputDialog dialog = new EventsInputDialog(
				"Nuevo valor",
				eventsDraftObject.getAgreementVariables(),
				eventsDraftObject.getEventEmployees()
				) {
			
			@Override
			protected void onAccept() {
				EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(getEmployeeSelected());
				Integer contractId = eventEmployee.getContractId();
				 
				String varName = getVariableName();
				String value = getValue();
				
				Date startDate = getStartDate();
				Date endDate = getEndDate();
				
				addNewValue(contractId, varName, value, startDate, endDate);
				changeYear(0);
			}
		};
		
		dialog.center();
		dialog.show();
	}
	
	@UiHandler("previusDateButton")
	public void onPreviusDateClick(ClickEvent event) {
		changeYear(-1);
	}
	
	@UiHandler("nextDateButton")
	public void onNextDateClick(ClickEvent event) {
		changeYear(1);
	}

	private void changeYear(int changeDate) {
		String selectItem = typeView.getSelectedItemText();
		if (selectItem == "MES"){
			Date actualDate = new Date(this.actualYear, this.actualMonth, 1);
			DateUtils.addMonths2Date(actualDate, changeDate);
			
			this.actualYear = actualDate.getYear();
			this.actualMonth = actualDate.getMonth();
			
			dateLabel.setText((this.actualMonth+1)+"/"+(this.actualYear+1900));
		
		}else if (selectItem == "VARIABLE"){
			this.actualYear += changeDate;
			dateLabel.setText((this.actualYear+1900)+"");
		}
		
		initializeView();
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		eventsDraftObject.updateEventsDraft(r -> 
		{
			changeYear(0);
			saveButton.setEnabled(false);
			undoAllButton.setEnabled(false);
		}, t -> {});
	}

	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		initUndoAllDialog();
	}
	
	private void initUndoAllDialog() {
		AonDialog dialog = new AonDialog("RESTAURAR", new HTML(String.valueOf("\u00BF")+"RESTAURAR INCIDENCIAS con los valores de la " + String.valueOf("\u00FA") + "ltima versi" + String.valueOf("\u00F3") + "n guardada?"));
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				Date currentDate = new Date();
				actualMonth = currentDate.getMonth();
				actualYear = currentDate.getYear();
				
				fillTypeViewListBox();
				
				eventsDraftObject.getWorkPlaceEmployeesDB(actualYear,
						r -> { 
							fillVariableListBox();
							initializeView(); 
							saveButton.setEnabled(false);
							undoAllButton.setEnabled(false);
						},
						t -> {});
			}
		});	
	}
	
	// -------------------------------------
	//        PRIMERA LLAMADA
	// -------------------------------------	

	public void setEventsDraftObject(EventsDraftObject eventsDraftObject) {
		clearEventsTable();
		this.eventsDraftObject = eventsDraftObject;
		
		Date currentDate = new Date();
		this.actualMonth = currentDate.getMonth();
		this.actualYear = currentDate.getYear();
		
		fillTypeViewListBox();
		
		this.eventsDraftObject.getWorkPlaceEmployeesDB(this.actualYear,
				r -> { 
					initializeVariablesToShow();
					fillVariableListBox();
					initializeView(); 
					saveButton.setEnabled(false);
					undoAllButton.setEnabled(false);
				},
				t -> {});
		
	}	

	private void initializeVariablesToShow() {
		if(null == this.eventsDraftObject.getAgreementId()) {
			this.variablesToShow = 2;
			calendarVariablesMenuItem.addStyleName("aon-MenuItemCheckYes");
			calendarVariablesMenuItem.addStyleName(style.aonCheck());
		}else {
			this.variablesToShow = 1;
			editableVariablesMenuItem.addStyleName("aon-MenuItemCheckYes");
			editableVariablesMenuItem.addStyleName(style.aonCheck());
		}
	}

	// Metodo para inicializar la vista de la tabla
	private void initializeView() {
		if (typeView.getSelectedItemText() == "MES")
			hide(varListViewPanel);
		else
			show(varListViewPanel);
		
		clearEventsTable();
		fillDateLabel();
		fillEventTable(getVariablesToShow());
		setStyleEventTable();
	}

	// -------------------------------------
	//        METODOS AUXILIARES
	// -------------------------------------
	
	// Metodo para inicializar los tipos de vista y que debe ocurrir cuando se elige cada una
	private void fillTypeViewListBox() {
		typeView.clear();
		typeView.addItem("MES");
		typeView.addItem("VARIABLE");
		
		typeView.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				initializeView();
			}
		});
	}
	
	private static void hide(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

	private static void show(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.VISIBLE);
	}
	
	// Metodo para inicializar los tipos de variables y que debe ocurrir cuando se elige cada uno
	private void fillVariableListBox() {
		varListView.clear();
		ArrayList<String> variableList = new ArrayList<>(this.eventsDraftObject.getAllVariables());
		for(String var : variableList)
			varListView.addItem(var);
		
		varListView.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				initializeView();	
			}
		});
	}
	
	// Metodo para modifica la forma en la que se muestra la fecha
	private void fillDateLabel() {
		String selectItem = typeView.getSelectedItemText();
		if (selectItem == "MES"){
			dateLabel.setText((this.actualMonth+1)+"/"+(this.actualYear+1900));
		}else if (selectItem == "VARIABLE"){
			dateLabel.setText((this.actualYear+1900)+"");
		}
	}
	
	// Metodo para recoger la informacion de las cabeceras e inicializar la tabla en funcion del tipo de vista
//	private void fillEventTable() {
//		ArrayList<String> variableList = new ArrayList<>(eventsDraftObject.getAllVariables());
//		ArrayList<EventEmployee> employeeList = this.eventsDraftObject.getEventEmployees();
//		
//		ArrayList<String> monthList = new ArrayList<>();
//		monthList = createMonthList();
//		
////		Window.alert("Numero de empleados :"+employeeList.size());
//		
//		String selectItem = typeView.getSelectedItemText();
//		if (selectItem == "MES"){
//			initializeFirstRow(variableList);
//			initializeValueForAllByMonth(employeeList, variableList);
//			initializeTableByMonth(employeeList, variableList);
//		}else if (selectItem == "VARIABLE"){
//			initializeFirstRow(monthList);
//			initializeValueForAllByVar(employeeList);
//			initializeTableByVar(employeeList);
//		}		
//	}
	
	private void fillEventTable(ArrayList<String> variableList) {
		ArrayList<EventEmployee> employeeList = this.eventsDraftObject.getEventEmployees();
		
		ArrayList<String> monthList = new ArrayList<>();
		monthList = createMonthList();
		
//		Window.alert("Numero de empleados :"+employeeList.size());
		
		String selectItem = typeView.getSelectedItemText();
		if (selectItem == "MES"){
			initializeFirstRow(variableList);
			initializeValueForAllByMonth(employeeList, variableList);
			initializeTableByMonth(employeeList, variableList);
		}else if (selectItem == "VARIABLE"){
			initializeFirstRow(monthList);
			initializeValueForAllByVar(employeeList);
			initializeTableByVar(employeeList);
		}		
	}
	
	public ArrayList<String> getVariablesToShow(){
		switch (variablesToShow) {
			case 0:
				return eventsDraftObject.getAllVariables();
			case 1:
				return eventsDraftObject.getAgreementVariables();
			case 2:
				return eventsDraftObject.getCalendarVariables();
			default:
				return new ArrayList<String>();
		}
	}

	private ArrayList<String> createMonthList() {
		ArrayList<String> monthList = new ArrayList<>();
		monthList.add("ENE");
		monthList.add("FEB");
		monthList.add("MAR");
		monthList.add("ABR");
		monthList.add("MAY");
		monthList.add("JUN");
		monthList.add("JUL");
		monthList.add("AGO");
		monthList.add("SEP");
		monthList.add("OCT");
		monthList.add("NOV");
		monthList.add("DIC");
		
		return monthList;
	}
	
	// Metodo para rellenar la primera fila de la tabla, headerList cambia en funcion del tipo de vista
	private void initializeFirstRow(ArrayList<String> headerList) {
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.blankHeaderCell());
		eventsTable.setWidget(0, 0, blankLabel);
		
		for(int i=0; i<headerList.size(); i++){
			eventsTable.setText(0, i+1, headerList.get(i));
			eventsTable.getCellFormatter().addStyleName(0, i+1, style.headerCell());
		}	
	}
	
	private void addNewValue(Integer contractId, String varName, String value, Date startDate,
			Date endDate) {
		eventsDraftObject.setValueByMonth(contractId, varName, value, startDate, endDate);
		saveButton.setEnabled(true);
		undoAllButton.setEnabled(true);
	}
	
	private void initializeValueForAllByMonth(ArrayList<EventEmployee> employeeList, ArrayList<String> variableList) {
		int columns = this.getColCount();
		
		//Rellenamos primera colunma con campos blancos
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.blankHeaderCell());
		eventsTable.setWidget(1, 0, blankLabel);
		
		for(int column=0; column<columns; column++){
			EventTableCell eventCell = new EventTableCell(1, column+1);
			eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					for(int row=0; row<employeeList.size(); row++){
						String value = null;
						if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
							value = null;
						else
							value = eventCell.getValue();
						Date startDate = new Date(actualYear, actualMonth, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						
						EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(row+2, 0));
						Integer contractId = eventEmployee.getContractId();
						 
						String varName = eventsTable.getText(0, eventCell.getColumn());
						
						addNewValue(contractId, varName, value, startDate, endDate);
						
						EventTableCell eventCell_Aux = (EventTableCell) eventsTable.getWidget(row+2, eventCell.getColumn());
						eventCell_Aux.setTextBoxValue(value.toString());
					}
					
					eventCell.setTextBoxValue("-");
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
			
			eventCell.setTextBoxValue("-");
			
			if(eventsDraftObject.isCalendarVariable(eventsTable.getText(0, eventCell.getColumn()))){
				eventCell.setEnabled(false);
				eventCell.setBlockVariableStyle();
			} else {
				eventCell.addStyleName(style.pointer());
			}
			
			eventsTable.setWidget(1, column+1, eventCell);
		}
		
	}
	
	// Metodo para rellenar la tabla cuando la vista elegida es por mes
	private void initializeTableByMonth(ArrayList<EventEmployee> employeeList, ArrayList<String> variableList) {
		int columns = this.getColCount();
		for(int row=1; row<employeeList.size()+1; row++){
			//Rellenamos primera colunma con los nombres de los empleados
			eventsTable.setText(row+1, 0, employeeList.get(row-1).getFullName());
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){				
				//Rellenamos el resto de columnas con la informacion de cada empleado
				Date findingDate = new Date(this.actualYear, this.actualMonth, 1);
				DateUtils.resetTime(findingDate);
				
				EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(employeeList.get(row-1).getFullName());
				Integer contractId = eventEmployee.getContractId();
				
				EmployeeEventsVariable variable = eventsDraftObject.getEmployeeEventsVariableByMonth(contractId, variableList.get(column), findingDate.getMonth(), findingDate.getYear());
				
				EventTableCell eventCell = new EventTableCell(row+1, column+1);
				eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						String value = null;
						if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
							value = null;
						else
							value = eventCell.getValue();
						
						Date startDate = new Date(actualYear, actualMonth, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						
						EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(eventCell.getRow(), 0));
						Integer contractId = eventEmployee.getContractId();
						
						String varName = eventsTable.getText(0, eventCell.getColumn());
						
						addNewValue(contractId, varName, value, startDate, endDate);
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
				
				if(null == variable){
					eventCell.setTextBoxValue("-");
				}else{
					eventCell.setTextBoxValue(variable.getValue().toString());
				}
				
				if(eventsDraftObject.isCalendarVariable(variableList.get(column))){
					eventCell.setEnabled(false);
					eventCell.setBlockVariableStyle();
				} else {
					eventCell.addStyleName(style.pointer());
				}
				
//				if(this.draftObject.hasChanged(employeeId, variableList.get(column), variable)){
//					eventCell.setOnChangeStyle();
//				}
				
				eventsTable.setWidget(row+1, column+1, eventCell);
			}
		}
		
	}
	
	private void initializeValueForAllByVar(ArrayList<EventEmployee> employeeList) {
		int columns = this.getColCount();
		
		//Rellenamos primera colunma con campos blancos
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.blankHeaderCell());
		eventsTable.setWidget(1, 0, blankLabel);
		
		for(int column=0; column<columns; column++){
			EventTableCell eventCell = new EventTableCell(1, column+1);
			eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					for(int row=0; row<employeeList.size(); row++){
						String varName = varListView.getSelectedValue();
						String value = null;
						if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
							value = null;
						else
							value = eventCell.getValue();
						Date startDate = new Date(actualYear, eventCell.getColumn()-1, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						
						EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(row+2, 0));
						Integer contractId = eventEmployee.getContractId();
						
						addNewValue(contractId, varName, value, startDate, endDate);
						
						EventTableCell eventCell_Aux = (EventTableCell) eventsTable.getWidget(row+2, eventCell.getColumn());
						eventCell_Aux.setTextBoxValue(value.toString());
					}
					
					eventCell.setTextBoxValue("-");
					
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
			
			eventCell.setTextBoxValue("-");
			
			if(eventsDraftObject.isCalendarVariable(varListView.getSelectedValue())){
				eventCell.setEnabled(false);
				eventCell.setBlockVariableStyle();
			} else {
				eventCell.addStyleName(style.pointer());
			}
			
			eventsTable.setWidget(1, column+1, eventCell);
		}
		
	}
	
	// Metodo para rellenar la tabla cuando la vista elegida es por variable
	private void initializeTableByVar(ArrayList<EventEmployee> employeeList) {
		//Cogemos la variable seleccionada en el momento de la creacion
		String varName = varListView.getSelectedValue();
		int columns = this.getColCount();
		
		for(int row=1; row<employeeList.size()+1; row++){
			//Rellenamos primera colunma con los nombres de los empleados
			eventsTable.setText(row+1, 0, employeeList.get(row-1).getFullName());
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){
				
				//Rellenamos el resto de columnas con la informacion de cada empleado
				Date findingDate = new Date(this.actualYear, column, 1);
				DateUtils.resetTime(findingDate);
				
				EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(employeeList.get(row-1).getFullName());
				Integer contractId = eventEmployee.getContractId();
				
				EmployeeEventsVariable variable = eventsDraftObject.getEmployeeEventsVariableByMonth(contractId, varName, findingDate.getMonth(), findingDate.getYear());
				
				EventTableCell eventCell = new EventTableCell(row+1, column+1);
				eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						String value = null;
						if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
							value = null;
						else
							value = eventCell.getValue();
						
						Date startDate = new Date(actualYear, eventCell.getColumn()-1, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						
						EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(eventCell.getRow(), 0));
						Integer contractId = eventEmployee.getContractId();
						
						addNewValue(contractId, varName, value, startDate, endDate);
						
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
				
				if(null == variable){
					eventCell.setTextBoxValue("-");
				}else{
					eventCell.setTextBoxValue(variable.getValue().toString());
				}
				
				if(eventsDraftObject.isCalendarVariable(varListView.getSelectedValue())){
					eventCell.setEnabled(false);
					eventCell.setBlockVariableStyle();
				} else {
					eventCell.addStyleName(style.pointer());
				}
				
//				if(this.draftObject.hasChanged(employeeId, varName, variable)){
//					eventCell.setOnChangeStyle();
//				}
				
				eventsTable.setWidget(row+1, column+1, eventCell);
			}
		}
	}
	
	// Metodo para aplicar un color de fondo a las filas impares de la tabla
	private void setStyleEventTable() {
		int rows = this.getRowCount();
		for(int row=0; row<rows; row++){
			if(row % 2 != 0)
				eventsTable.getRowFormatter().addStyleName(row, style.oddRowColor());
		}
		
	}
	
	// -------------------------------------
	//        METODOS TABLA
	// -------------------------------------
	
	private void clearEventsTable() {
		eventsTable.removeAllRows();
	}

	private int getColCount() {
		return eventsTable.getCellCount(0)-1;
	}
	
	private int getRowCount() {
		return eventsTable.getRowCount();
	}

	private boolean isFirstRow(int row) {
		return row == 1;
	}

	private boolean isLastCol(int col) {
		return (col == (eventsTable.getCellCount(0) - 1));
	}

	private static boolean equals(Object obj1, Object obj2) {
		if (obj1 == obj2)
			return true;
		if (obj1 == null)
			return false;
		if (obj2 == null)
			return false;
		return obj1.equals(obj2);
	}
	
}
