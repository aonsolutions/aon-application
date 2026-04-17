package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
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

	// ----------------------------------------------- UiBinder 
	
	interface Binder extends UiBinder<Widget, EventsDraft> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- EventTableCell 
	
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
			
	}
	
	// ----------------------------------------------- ScheduledCommand (See)
	
	class CalendarVariablesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			seeMenu.getCalendarVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().addStyleName(style.aonCheck());
			
			seeMenu.getEditableVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getEditableVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getAllVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 2;
			
			initializeView(); 
		}
	}
	
	class EditableVariablesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			seeMenu.getEditableVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getEditableVariablesMenuItem().addStyleName(style.aonCheck());
			
			seeMenu.getCalendarVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getAllVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 1;
			
			initializeView(); 
		}
	}
	
	class AllVariablesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			seeMenu.getAllVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().addStyleName(style.aonCheck());
			
			seeMenu.getEditableVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getEditableVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getCalendarVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 0;
			
			initializeView(); 
		}
	}
	
	class SeeMenu extends ContextMenu {
				
		private MenuItem calendarVariablesMenuItem = null;
		private MenuItem editableVariablesMenuItem = null;
		private MenuItem allVariablesMenuItem = null;
		
		public SeeMenu() {
			
			calendarVariablesMenuItem = addItem("Consultar variables calendario", new CalendarVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			calendarVariablesMenuItem.ensureDebugId("calendarVariablesMenuItem");
			
			editableVariablesMenuItem = addItem("Editar variables convenio", new EditableVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			editableVariablesMenuItem.ensureDebugId("editableVariablesMenuItem");
			
			allVariablesMenuItem = addItem("Todas las variables", new AllVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			allVariablesMenuItem.ensureDebugId("allVariablesMenuItem");
			
		}

		public MenuItem getCalendarVariablesMenuItem() {
			return calendarVariablesMenuItem;
		}

		public MenuItem getEditableVariablesMenuItem() {
			return editableVariablesMenuItem;
		}

		public MenuItem getAllVariablesMenuItem() {
			return allVariablesMenuItem;
		}
		
	}
	
	// ----------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String blankHeaderCell();
		String headerCell();
		String oddRowColor();
		String eventCell();
		String setBlockCellStyle();
		String aonCheck();
		String pointer();
		String cmdBtn();
		String flexVariables();
		String widthMenuItem();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	Label dateLabel;
	
	@UiField
	Button previusDateButton;
	
	@UiField
	Button nextDateButton;
	
	@UiField
	FlexTable eventsTable;

	// ----------------------------------------------- Variables
	
	private EventsDraftObject eventsDraftObject;
	
	private Integer actualMonth;
	private Integer actualYear;
	
	private Integer variablesToShow = 0; // 0 = ALL_VARIABLES -- 1 = EDITABLE_VARS -- 2 = CALENDAR_VARS
	
	private SeeMenu seeMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton visibilityBtn;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton newValueButton;
	
	private ListBox typeView = new ListBox();
	private HTMLPanel varListViewPanel = new HTMLPanel("");
	private ListBox varListView = new ListBox();

	// ----------------------------------------------- Constructor
	
	public EventsDraft() {
		initWidget(binder.createAndBindUi(this));
		
		getToolbarPanel();
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		seeMenu = new SeeMenu();
	}

	// ----------------------------------------------- UiHandler
	
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
		if (AonStringUtils.equalsIgnoreCase(selectItem, "MES")){
			Date actualDate = DateUtils.getDate(this.actualMonth, this.actualYear);
			DateUtils.addMonths2Date(actualDate, changeDate);
			
			this.actualYear = DateUtils.getYear(actualDate); 
			this.actualMonth =  DateUtils.getMonth(actualDate);
			
			dateLabel.setText((this.actualMonth+1)+"/"+(this.actualYear));
		
		}else if (AonStringUtils.equalsIgnoreCase(selectItem, "VARIABLE")){
			this.actualYear += changeDate;
			dateLabel.setText((this.actualYear)+"");
		}
		
		initializeView();
	}
	
	// ----------------------------------------------- setEventsDraftObject	

	public void setEventsDraftObject(EventsDraftObject eventsDraftObject) {
		clearEventsTable();
		this.eventsDraftObject = eventsDraftObject;
		Date currentDate = new Date();
		this.actualYear = DateUtils.getYear(currentDate); 
		this.actualMonth =  DateUtils.getMonth(currentDate);
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
	
	// ----------------------------------------------- setEventsDraftObject.Methods

	private void fillTypeViewListBox() {
		typeView.clear();
		typeView.addItem("MES");
		typeView.addItem("VARIABLE");
		
		typeView.addChangeHandler(e -> initializeView());
	}
	
	private void initializeVariablesToShow() {
		if(null == this.eventsDraftObject.getAgreementId()) {
			this.variablesToShow = 2;
			seeMenu.getCalendarVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().addStyleName(style.aonCheck());
			seeMenu.getEditableVariablesMenuItem().setEnabled(false);
			seeMenu.getEditableVariablesMenuItem().setTitle("Deshabilitado: no existe convenio asociado al CT");
		}else {
			this.variablesToShow = 1;
			seeMenu.getEditableVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getEditableVariablesMenuItem().addStyleName(style.aonCheck());
		}
	}
	
	private void fillVariableListBox() {
		varListView.clear();
		ArrayList<String> variableList = new ArrayList<>(this.eventsDraftObject.getAllVariables());
		for(String varName : variableList)
			varListView.addItem(varName);
		
		varListView.addChangeHandler(e -> initializeView());
	}

	private void initializeView() {
		if (AonStringUtils.equalsIgnoreCase(typeView.getSelectedItemText(), "MES"))
			hide(varListViewPanel);
		else
			show(varListViewPanel);
		clearEventsTable();
		fillDateLabel();
		fillEventTable(getVariablesToShow());
		setStyleEventTable();
		
	}

	// ----------------------------------------------- initializeView.Methods
	
	private void clearEventsTable() {
		eventsTable.removeAllRows();
	}
	
	private void fillDateLabel() {
		String selectItem = typeView.getSelectedItemText();
		if (AonStringUtils.equalsIgnoreCase(selectItem, "MES")){
			dateLabel.setText((this.actualMonth+1)+"/"+(this.actualYear));
		}else if (AonStringUtils.equalsIgnoreCase(selectItem, "VARIABLE")){
			dateLabel.setText((this.actualYear)+"");
		}
	}
	
	private void fillEventTable(ArrayList<String> variableList) {
		ArrayList<EventEmployee> employeeList = this.eventsDraftObject.getEventEmployees();
		
		ArrayList<String> monthList = new ArrayList<>();
		monthList = createMonthList();
		
		String selectItem = typeView.getSelectedItemText();
		if (AonStringUtils.equalsIgnoreCase(selectItem, "MES")){
			initializeFirstRow(variableList);
			initializeValueForAllByMonth(employeeList, variableList);
			initializeTableByMonth(employeeList, variableList);
		}else if (AonStringUtils.equalsIgnoreCase(selectItem, "VARIABLE")){
			initializeFirstRow(monthList);
			initializeValueForAllByVar(employeeList);
			initializeTableByVar(employeeList);
		}		
	}
	
	private void setStyleEventTable() {
		int rows = this.getRowCount();
		for(int row=0; row<rows; row++){
			if(row % 2 != 0)
				eventsTable.getRowFormatter().addStyleName(row, style.oddRowColor());
		}
		
	}
	
	// ----------------------------------------------- fillEventTable.Methods
	
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
			eventCell.addValueChangeHandler(e -> {
				for(int row=0; row<employeeList.size(); row++){
					String value = null;
					if(AonStringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
						value = null;
					else
						value = eventCell.getValue();
					Date startDate = DateUtils.getDate(actualMonth, actualYear);
					Date endDate = DateUtils.getLastDayOfMonth(startDate);
					
					EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(row+2, 0));
					Integer contractId = eventEmployee.getContractId();
					 
					String varName = eventsTable.getText(0, eventCell.getColumn());
					
					addNewValue(contractId, varName, value, startDate, endDate);
					
					EventTableCell eventCell_Aux = (EventTableCell) eventsTable.getWidget(row+2, eventCell.getColumn());
					eventCell_Aux.setTextBoxValue(value.toString());
				}
				
				eventCell.setTextBoxValue("-");
			});
			
			eventCell.addFocusHandler(e -> {
				if(AonStringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
					eventCell.setValue("");
			});
			
			eventCell.addBlurHandler(e -> {
				if(AonStringUtils.isBlank(eventCell.getValue()))
					eventCell.setValue("-");
			});
			
			eventCell.setTextBoxValue("-");
			
			if(Boolean.TRUE.equals(eventsDraftObject.isCalendarVariable(eventsTable.getText(0, eventCell.getColumn())))){
				eventCell.setEnabled(false);
				eventCell.setBlockVariableStyle();
			} else {
				eventCell.addStyleName(style.pointer());
			}
			
			eventsTable.setWidget(1, column+1, eventCell);
		}
		
	}
	
	private void initializeTableByMonth(ArrayList<EventEmployee> employeeList, ArrayList<String> variableList) {
		int columns = this.getColCount();
		for(int row=1; row<employeeList.size()+1; row++){
			
			// Check AgreementId
			Integer agreementIdEmployee = employeeList.get(row-1).getAgreementId();
			if((this.variablesToShow == 1 || this.variablesToShow == 0)  && null != this.eventsDraftObject.getAgreementId() && !AonNumberUtils.equals(agreementIdEmployee, this.eventsDraftObject.getAgreementId()))
				continue;
			
			//Rellenamos primera colunma con los nombres de los empleados
			eventsTable.setText(row+1, 0, employeeList.get(row-1).getFullName());
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){				
				//Rellenamos el resto de columnas con la informacion de cada empleado
				Date findingDate =  DateUtils.getDate(actualMonth, actualYear);
				DateUtils.resetTime(findingDate);
				
				EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(employeeList.get(row-1).getFullName());
				Integer contractId = eventEmployee.getContractId();
				
				EmployeeEventsVariable variable = eventsDraftObject.getEmployeeEventsVariableByMonth(contractId, variableList.get(column), DateUtils.getMonth(findingDate), DateUtils.getYear(findingDate));
				
				EventTableCell eventCell = new EventTableCell(row+1, column+1);
				eventCell.addValueChangeHandler(e -> {
					String value = null;
					if(AonStringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
						value = null;
					else
						value = eventCell.getValue();
					
					Date startDate =  DateUtils.getDate(actualMonth, actualYear);
					Date endDate = DateUtils.getLastDayOfMonth(startDate);
					
					EventEmployee eventEmployeeAux = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(eventCell.getRow(), 0));
					Integer contractIdAux = eventEmployeeAux.getContractId();
					
					String varName = eventsTable.getText(0, eventCell.getColumn());
					
					addNewValue(contractIdAux, varName, value, startDate, endDate);
				});
				
				eventCell.addFocusHandler(e -> {
					if(AonStringUtils.isBlank(eventCell.getValue()) || AonStringUtils.equalsIgnoreCase(eventCell.getValue(), "-"))
						eventCell.setValue("");
				});
				
				eventCell.addBlurHandler(e -> {
					if(AonStringUtils.isBlank(eventCell.getValue()))
						eventCell.setValue("-");
				});
				
				eventCell.setTextBoxValue(null == variable ? "-" : variable.getValue().toString());
				
				if(Boolean.TRUE.equals(eventsDraftObject.isCalendarVariable(variableList.get(column)))){
					eventCell.setEnabled(false);
					eventCell.setBlockVariableStyle();
				} else {
					eventCell.addStyleName(style.pointer());
				}
				
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
			eventCell.addValueChangeHandler(e -> {
				for(int row=0; row<employeeList.size(); row++){
					String varName = varListView.getSelectedValue();
					String value = null;
					if(AonStringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
						value = null;
					else
						value = eventCell.getValue();
					Date startDate =  DateUtils.getDate(eventCell.getColumn()-1, actualYear);
					Date endDate = DateUtils.getLastDayOfMonth(startDate);
					
					EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(row+2, 0));
					Integer contractId = eventEmployee.getContractId();
					
					addNewValue(contractId, varName, value, startDate, endDate);
					
					EventTableCell eventCell_Aux = (EventTableCell) eventsTable.getWidget(row+2, eventCell.getColumn());
					eventCell_Aux.setTextBoxValue(value.toString());
				}
				
				eventCell.setTextBoxValue("-");
			});
			
			eventCell.addFocusHandler(e -> {
				if(AonStringUtils.isBlank(eventCell.getValue()) || AonStringUtils.equalsIgnoreCase(eventCell.getValue(),"-"))
					eventCell.setValue("");
			});
			
			eventCell.addBlurHandler(e -> {
				if(AonStringUtils.isBlank(eventCell.getValue()))
					eventCell.setValue("-");
			});
			
			eventCell.setTextBoxValue("-");
			
			if(Boolean.TRUE.equals(eventsDraftObject.isCalendarVariable(varListView.getSelectedValue()))){
				eventCell.setEnabled(false);
				eventCell.setBlockVariableStyle();
			} else {
				eventCell.addStyleName(style.pointer());
			}
			
			eventsTable.setWidget(1, column+1, eventCell);
		}
		
	}
	
	private void initializeTableByVar(ArrayList<EventEmployee> employeeList) {
		//Cogemos la variable seleccionada en el momento de la creacion
		String varName = varListView.getSelectedValue();
		int columns = this.getColCount();
		
		for(int row=1; row<employeeList.size()+1; row++){
			//Rellenamos primera colunma con los nombres de los empleados
			eventsTable.setText(row+1, 0, employeeList.get(row-1).getFullName());
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){
				
				// Check AgreementId
				Integer agreementIdEmployee = employeeList.get(row-1).getAgreementId();
				if((this.variablesToShow == 1 || this.variablesToShow == 0) && null != this.eventsDraftObject.getAgreementId() && !AonNumberUtils.equals(agreementIdEmployee, this.eventsDraftObject.getAgreementId()))
					continue;
				
				
				//Rellenamos el resto de columnas con la informacion de cada empleado
				Date findingDate = DateUtils.getDate(column, this.actualYear);
				DateUtils.resetTime(findingDate);
				
				EventEmployee eventEmployee = eventsDraftObject.getEmployeeByFullName(employeeList.get(row-1).getFullName());
				Integer contractId = eventEmployee.getContractId();
				
				EmployeeEventsVariable variable = eventsDraftObject.getEmployeeEventsVariableByMonth(contractId, varName, DateUtils.getMonth(findingDate), DateUtils.getYear(findingDate));
				
				EventTableCell eventCell = new EventTableCell(row+1, column+1);
				eventCell.addValueChangeHandler(e -> {
					String value = null;
					if(AonStringUtils.isBlank(eventCell.getValue()) || AonStringUtils.equalsIgnoreCase(eventCell.getValue(),"-"))
						value = null;
					else
						value = eventCell.getValue();
					
					Date startDate = DateUtils.getDate(eventCell.getColumn()-1, actualYear);
					Date endDate = DateUtils.getLastDayOfMonth(startDate);
					
					EventEmployee eventEmployeeAux = eventsDraftObject.getEmployeeByFullName(eventsTable.getText(eventCell.getRow(), 0));
					Integer contractIdAux = eventEmployeeAux.getContractId();
					
					addNewValue(contractIdAux, varName, value, startDate, endDate);
				});
				
				eventCell.addFocusHandler(e -> {
					if(AonStringUtils.isBlank(eventCell.getValue()) || AonStringUtils.equalsIgnoreCase(eventCell.getValue(),"-"))
						eventCell.setValue("");
				});
				
				eventCell.addBlurHandler(e -> {
					if(AonStringUtils.isBlank(eventCell.getValue()))
						eventCell.setValue("-");
				});
				
				eventCell.setTextBoxValue(null == variable ? "-" : variable.getValue().toString());
				
				if(Boolean.TRUE.equals(eventsDraftObject.isCalendarVariable(varListView.getSelectedValue()))){
					eventCell.setEnabled(false);
					eventCell.setBlockVariableStyle();
				} else {
					eventCell.addStyleName(style.pointer());
				}
				
				eventsTable.setWidget(row+1, column+1, eventCell);
			}
		}
	}
	
	// ----------------------------------------------- EventsDraft.Auxiliar Methods
	
	private static void hide(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

	private static void show(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.VISIBLE);
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
				return new ArrayList<>();
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
	
	private void addNewValue(Integer contractId, String varName, String value, Date startDate,
			Date endDate) {
		eventsDraftObject.setValueByMonth(contractId, varName, value, startDate, endDate);
		saveButton.setEnabled(true);
		undoAllButton.setEnabled(true);
	}
	
	private int getColCount() {
		return eventsTable.getCellCount(0)-1;
	}
	
	private int getRowCount() {
		return eventsTable.getRowCount();
	}
	
	// ----------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		
		this.toolbar = new AonToolbar("Variables de c\u00e1lculo");
		
		undoAllButton = new AonToolbarButton( "Restaurar últimos valores guardados", AON.CSS.aonIconUndo() );
		undoAllButton.addClickHandler(e -> onUndo());
		toolbar.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> onSave());
		toolbar.add(saveButton);
		
		newValueButton = new AonToolbarButton( "Nuevo valor", AON.CSS.aonIconAdd() );
		newValueButton.addClickHandler(e -> onNewValue());
		toolbar.add(newValueButton);
		
		visibilityBtn = new AonToolbarButton( "Visualizaci\u00F3n", AON.CSS.aonIconVisibility() );
		visibilityBtn.addClickHandler(e -> onVisibility(e));
		toolbar.add(visibilityBtn);
		
		Label type = new Label("Tipo vista:");
		type.getElement().getStyle().setMarginRight(5, Unit.PX);
		type.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		toolbar.add(type);
		
		toolbar.add(typeView);
		
		Label variable = new Label("Tipo vista:");
		variable.getElement().getStyle().setMarginRight(5, Unit.PX);
		variable.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		toolbar.add(variable);
		
		varListViewPanel.addStyleName(style.flexVariables());
		varListViewPanel.add(variable);
		varListViewPanel.add(varListView);
		hide(varListViewPanel);
		
		toolbar.add(varListViewPanel);

	}

	// ----------------------------------------------- Toolbar.Methods

	private void onUndo() {
		initUndoAllDialog();
	}
	
	private void onSave() {
		eventsDraftObject.updateEventsDraft(
				r -> {
					changeYear(0);
					saveButton.setEnabled(false);
					undoAllButton.setEnabled(false);
				}, 
				t -> {});
	}
	
	private void onNewValue() {
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

	private void onVisibility(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		seeMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		seeMenu.show();
	}
	
	// ----------------------------------------------- Toolbar.Auxiliar Methods
	
	private void initUndoAllDialog() {
		AonDialog dialog = new AonDialog("RESTAURAR", new HTML("\u00BFRESTAURAR INCIDENCIAS con los valores de la \u00FAltima versi\u00F3n guardada?"));
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				Date currentDate = new Date();
				actualMonth = DateUtils.getMonth(currentDate);
				actualYear = DateUtils.getYear(currentDate);
				
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
	
}
