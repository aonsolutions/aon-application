package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.Td;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class EventsDraft extends ResizeComposite {

	interface Binder extends UiBinder<Widget, EventsDraft> {
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String blankHeaderCell();
		String headerCell();
		String oddRowColor();
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	FlexTable eventsTable;
	
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
	private MultiSelectionModel<Td> selectionModel;
	
	private Integer actualMonth;
	private Integer actualYear;
	

	public EventsDraft() {
		initWidget(binder.createAndBindUi(this));
		this.selectionModel = new MultiSelectionModel<Td>();
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
	}
	
	// -------------------------------------
	//        PRIMERA LLAMADA
	// -------------------------------------	

	public void setEventsDraftObject(EventsDraftObject eventsDraftObject) {
		eventsTable.clear();
		this.draftObject = eventsDraftObject;
		
		Date currentDate = new Date();
		this.actualMonth = currentDate.getMonth();
		this.actualYear = currentDate.getYear();
		
		fillTypeViewListBox();
		fillVariableListBox();
		
		this.draftObject.getWorkPlaceEmployeesDB(this.actualYear,
				r -> { initializeView(); },
				t -> {});
		
	}	

	private void initializeView() {
		Window.alert("TAMAÑO ALL VARIABLES :"+this.draftObject.getAllVariables().size());
		for(String var : this.draftObject.getAllVariables())
			Window.alert(var);
		
		clearEventsTable();
		fillDateLabel();
		fillEventTable();
		setStyleEventTable();
	}

	// -------------------------------------
	//        METODOS AUXILIARES
	// -------------------------------------
	
	private void fillTypeViewListBox() {
		typeView.clear();
		typeView.addItem("MES");
		typeView.addItem("VARIABLE");
		
		typeView.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				eventsTable.clear();
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
	
	private void fillVariableListBox() {
		varListView.clear();
		// TODO: BORRAR ESTAS VARIABLES CUANDO SE CONSIGAN DE LA BD.
		varListView.addItem("DIAS_HUELGA");
		varListView.addItem("HORAS_EXTRAS");
		varListView.addItem("VENTAS");
	}
	
	private static void hide(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

	private static void show(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.VISIBLE);
	}
	
	private void fillDateLabel() {
		String selectItem = typeView.getSelectedItemText();
		if (selectItem == "MES"){
			dateLabel.setText((this.actualMonth+1)+"/"+(this.actualYear+1900));
		}else if (selectItem == "VARIABLE"){
			dateLabel.setText((this.actualYear+1900)+"");
		}
	}
	
	private void fillEventTable() {
		//TODO: BORRAR ESTOS DOS METODOS CUANDO SE RECIBA LA INFO DE LA BD
		ArrayList<String> variableList = createVariableList();
		ArrayList<String> employeeList = this.draftObject.getWorkplaceEmployees();
		ArrayList<String> monthList = createMonthList();
		
		//ArrayList<String> employeeList = createEmployeeList();
		
		String selectItem = typeView.getSelectedItemText();
		if (selectItem == "MES"){
			initializeFirstRow(variableList);
			initializeTable(employeeList);
		}else if (selectItem == "VARIABLE"){
			initializeFirstRow(monthList);
			initializeTable(employeeList);
		}
		
		
	}

	private ArrayList<String> createVariableList() {
		ArrayList<String> variableList = new ArrayList<>();
		variableList.add("DIAS_HUELGA");
		variableList.add("DIAS_VACACIONES");
		variableList.add("VENTAS");
		variableList.add("HORAS_EXTRAS");
		variableList.add("DIAS_PECNORTA");
		variableList.add("HORAS_COMPLEMENTARIAS");
		
		return variableList;
	}
	
	private ArrayList<String> createEmployeeList() {
		ArrayList<String> employeeList = new ArrayList<>();
		employeeList.add("JOSÉ LUIS VALDEPEÑAS");
		employeeList.add("PATRICIA CALVO");
		employeeList.add("ERNESTO CABALLERO");
		employeeList.add("JAVIER SOLERA");
		
		return employeeList;
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
	
	private void initializeFirstRow(ArrayList<String> headerList) {
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.blankHeaderCell());
		eventsTable.setWidget(0, 0, blankLabel);
		
		for(int i=0; i<headerList.size(); i++){
			eventsTable.setText(0, i+1, headerList.get(i));
			eventsTable.getCellFormatter().addStyleName(0, i+1, style.headerCell());
		}	
	}
	
	private void initializeTable(ArrayList<String> employeeList) {
		int columns = this.getColCount();
		for(int row=0; row<employeeList.size(); row++){
			eventsTable.setText(row+1, 0, employeeList.get(row));
			eventsTable.getCellFormatter().addStyleName(row+1, 0, style.headerCell());
			
			for(int column=0; column<columns; column++){
				eventsTable.setText(row+1, column+1, "-");
			}
		}	
	}
	
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
