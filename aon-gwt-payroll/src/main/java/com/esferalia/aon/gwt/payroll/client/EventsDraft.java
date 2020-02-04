package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.EventsDraftObject.EmployeeEventsVariable;
import com.esferalia.aon.gwt.payroll.client.EventsDraftObject.EventEmployee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class EventsDraft extends ResizeComposite {

	interface Binder extends UiBinder<Widget, EventsDraft> {
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String blankHeaderCell();
		String headerCell();
		String oddRowColor();
		String onChange();
		String eventCell();
		String setBlockCellStyle();
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

		public void setOnChangeStyle() {
			this.addStyleName(style.onChange());	
		}

		public void removeOnChangeStyle() {
			this.removeStyleName(style.onChange());	
		}
			
	}

	@UiField
	FlexTable eventsTable;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button undoAllButton;
	
	@UiField
	ListBox typeView;
	
	@UiField
	HorizontalPanel varListViewPanel;
	
	@UiField
	ListBox varListView;
	
	@UiField
	Label dateLabel;
	
	@UiField
	Button previusDateButton;
	
	@UiField
	Button nextDateButton;

	
	private EventsDraftObject draftObject;
	
	private Integer actualMonth;
	private Integer actualYear;
	
	private ArrayList<String> blockedVariables;

	public EventsDraft() {
		initWidget(binder.createAndBindUi(this));
		initializeBlockedVariables();
	}
	
	private void initializeBlockedVariables() {
		this.blockedVariables = new ArrayList<>();
		this.blockedVariables.add("DIAS_VACACIONES");
		this.blockedVariables.add("DIAS_HUELGA");
		this.blockedVariables.add("DIAS_AUSENCIA");
		this.blockedVariables.add("DIAS_ERE");
	}

	// -------------------------------------
	//             UI HANDLER
	// -------------------------------------
	
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
		//Window.alert("GUARDAR");
		this.draftObject.updateEventsWorkplace(
				r -> {
					//Window.alert("CARGANDO");
					setEventsDraftObject(this.draftObject);
					draftObject.undoManager.discardAll();
				}, t -> {}
		);
	}

	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while (draftObject.undoManager.canUndo())
			draftObject.undoManager.undo();
		
		initializeView();
	}
	
	// -------------------------------------
	//        PRIMERA LLAMADA
	// -------------------------------------	

	public void setEventsDraftObject(EventsDraftObject eventsDraftObject) {
		clearEventsTable();
		this.draftObject = eventsDraftObject;
		
		Date currentDate = new Date();
		this.actualMonth = currentDate.getMonth();
		this.actualYear = currentDate.getYear();
		
		fillTypeViewListBox();
		
		this.draftObject.undoManager.addListener(new UndoManager.Listener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onChange(UndoManager undoManager) {
				saveButton.setEnabled(undoManager.canUndo());
				undoAllButton.setEnabled(undoManager.canUndo());
			}
		});
		
		this.draftObject.getWorkPlaceEmployeesDB(this.actualYear,
				r -> { 
					fillVariableListBox();
					initializeView(); 
				},
				t -> {});
		
	}	

	// Metodo para inicializar la vista de la tabla
	private void initializeView() {
//		Integer numVars = 0;
//		for(Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : this.draftObject.getInformation().entrySet()){
//			for(Entry<String, ArrayList<EmployeeEventsVariable>> entryVar : entry.getValue().entrySet()){
//				numVars += entryVar.getValue().size();
//			}
//		}
//		
//		Window.alert("NUMERO DE VARIABLES :"+ numVars);
		
		undoAllButton.setEnabled(this.draftObject.undoManager.canUndo());
		if (typeView.getSelectedItemText() == "MES")
			hide(varListViewPanel);
		
		clearEventsTable();
		fillDateLabel();
		fillEventTable();
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
				String selectItem = typeView.getSelectedItemText();
				if (selectItem == "MES"){
					hide(varListViewPanel);
				}else if (selectItem == "VARIABLE"){
					show(varListViewPanel);
				}
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
		ArrayList<String> variableList = new ArrayList<>(this.draftObject.getAllVariables());
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
	private void fillEventTable() {
		ArrayList<String> variableList = new ArrayList<>(this.draftObject.getAllVariables());
		ArrayList<EventEmployee> employeeList = new ArrayList<>();
		employeeList = this.draftObject.getWorkplaceEmployees();
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

	private ArrayList<String> createMonthList() {
		ArrayList<String> monthList = new ArrayList<>();
		monthList.add("ENERO");
		monthList.add("FEBRERO");
		monthList.add("MARZO");
		monthList.add("ABRIL");
		monthList.add("MAYO");
		monthList.add("JUNIO");
		monthList.add("JULIO");
		monthList.add("AGOSTO");
		monthList.add("SEPTIEMBRE");
		monthList.add("OCTUBRE");
		monthList.add("NOVIEMBRE");
		monthList.add("DICIEMBRE");
		
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
						Double value = Double.parseDouble(eventCell.getValue());
						Date startDate = new Date(actualYear, actualMonth, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						Integer employeeId = draftObject.getEventEmployeeId(eventsTable.getText(row+2, 0));
						String varName = eventsTable.getText(0, eventCell.getColumn());
						
						draftObject.addEvent(employeeId, varName, value, startDate, endDate);
						
						EventTableCell eventCell_Aux = (EventTableCell) eventsTable.getWidget(row+2, eventCell.getColumn());
						eventCell_Aux.setTextBoxValue(value.toString());
						eventCell_Aux.setOnChangeStyle();
					}
					
					eventCell.setTextBoxValue("-");
				}
			});
			
			eventCell.setTextBoxValue("-");
			
			eventsTable.setWidget(1, column+1, eventCell);
		}
		
	}
	
	// Metodo para rellenar la tabla cuando la vista elegida es por mes
	private void initializeTableByMonth(ArrayList<EventEmployee> employeeList, ArrayList<String> variableList) {
		int columns = this.getColCount();
		for(int row=1; row<employeeList.size()+1; row++){
			//Rellenamos primera colunma con los nombres de los empleados
			eventsTable.setText(row+1, 0, employeeList.get(row-1).getCompleteEmployeeName());
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){
				//Rellenamos el resto de columnas con la informacion de cada empleado
				Date findingDate = new Date(this.actualYear, this.actualMonth, 1);
				DateUtils.resetTime(findingDate);
				Integer employeeId = this.draftObject.getEventEmployeeId(employeeList.get(row-1).getCompleteEmployeeName());
				
				EmployeeEventsVariable variable = this.draftObject.getEmployeeVariableByDate(employeeId, findingDate, variableList.get(column));
				EventTableCell eventCell = new EventTableCell(row+1, column+1);
				eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						Double value = null;
						if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
							value = null;
						else
							value = Double.parseDouble(eventCell.getValue());
						
						Date startDate = new Date(actualYear, actualMonth, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						
						Integer employeeId = draftObject.getEventEmployeeId(eventsTable.getText(eventCell.getRow(), 0));
						String varName = eventsTable.getText(0, eventCell.getColumn());
						
						draftObject.addEvent(employeeId, varName, value, startDate, endDate);
						
						eventCell.setOnChangeStyle();
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
				
				if(this.blockedVariables.contains(variableList.get(column))){
					eventCell.setEnabled(false);
					eventCell.setBlockVariableStyle();
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
						Double value = Double.parseDouble(eventCell.getValue());
						Date startDate = new Date(actualYear, eventCell.getColumn()-1, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						Integer employeeId = draftObject.getEventEmployeeId(eventsTable.getText(row+2, 0));
						
						draftObject.addEvent(employeeId, varName, value, startDate, endDate);
						
						EventTableCell eventCell_Aux = (EventTableCell) eventsTable.getWidget(row+2, eventCell.getColumn());
						eventCell_Aux.setTextBoxValue(value.toString());
						eventCell_Aux.setOnChangeStyle();
					}
					
					eventCell.setTextBoxValue("-");
				}
			});
			
			eventCell.setTextBoxValue("-");
			
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
			eventsTable.setText(row+1, 0, employeeList.get(row-1).getCompleteEmployeeName());
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){
				//Rellenamos el resto de columnas con la informacion de cada empleado
				Date findingDate = new Date(this.actualYear, column, 1);
				DateUtils.resetTime(findingDate);
				Integer employeeId = this.draftObject.getEventEmployeeId(employeeList.get(row-1).getCompleteEmployeeName());
				
				EmployeeEventsVariable variable = this.draftObject.getEmployeeVariableByDate(employeeId, findingDate, varName);
				EventTableCell eventCell = new EventTableCell(row+1, column+1);
				eventCell.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						Double value = null;
						if(StringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
							value = null;
						else
							value = Double.parseDouble(eventCell.getValue());
						
						Date startDate = new Date(actualYear, eventCell.getColumn()-1, 1);
						Date endDate = DateUtils.getLastDayOfMonth(startDate);
						
						Integer employeeId = draftObject.getEventEmployeeId(eventsTable.getText(eventCell.getRow(), 0));
						
						draftObject.addEvent(employeeId, varName, value, startDate, endDate);
						
						eventCell.setOnChangeStyle();
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
				
				if(this.blockedVariables.contains(varName)){
					eventCell.setEnabled(false);
					eventCell.setBlockVariableStyle();
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
