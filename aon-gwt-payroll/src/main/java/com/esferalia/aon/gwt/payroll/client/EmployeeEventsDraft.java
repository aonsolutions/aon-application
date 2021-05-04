package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public class EmployeeEventsDraft extends Composite implements ContextMenuHandler {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {}
	
	// ----------------------------------------------- EventTableCell 
	
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
			
			fillCellsEvents(); 
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
			
			fillCellsEvents(); 
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
			
			fillCellsEvents(); 
		}
	}
	
	class SeeMenu extends ContextMenu {
				
		private MenuItem calendarVariablesMenuItem = null;
		private MenuItem editableVariablesMenuItem = null;
		private MenuItem allVariablesMenuItem = null;
		
		public SeeMenu() {
			
			calendarVariablesMenuItem = addItem("Variables del calendario", new CalendarVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmd_btn());
			calendarVariablesMenuItem.ensureDebugId("calendarVariablesMenuItem");
			
			editableVariablesMenuItem = addItem("Variables editables", new EditableVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmd_btn());
			editableVariablesMenuItem.ensureDebugId("editableVariablesMenuItem");
			
			allVariablesMenuItem = addItem("Todas las variables", new AllVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmd_btn());
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
	
	// ----------------------------------------------- UiField 
	
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
		String bgcWhite();
		String aonCheck();
		String widthMenuItem();
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	Label yearLabel;
	
	@UiField
	Button lastYearButton;

	@UiField
	Button nextYearButton;
	
	@UiField
	FlexTable eventsGrid;
	
	// ----------------------------------------------- Variables 
	
	private EmployeeEventsDraftObject employeeEventsDraft;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<Integer>();
	
	private HashMap<String,Integer> variablesRow = new HashMap<String,Integer>();
	
	private Integer variablesToShow = 1; // 0 = ALL_VARIABLES -- 1 = EDITABLE_VARS -- 2 = CALENDAR_VARS
	
	private int year = DateUtils.getYear();
	
	private SeeMenu seeMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton newValueButton;
	private AonToolbarButton visibilityButton;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeEventsDraft() {
		toolbar = getToolbarPanel();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		seeMenu = new SeeMenu();
		
		initializeTable();
		
		//Reescribir la accion del boton derecho del ratón dentro de la tabla
		eventsGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		saveButton.setEnabled(false);
		undoAllButton.setEnabled(false);
	}
	
	// ----------------------------------------------- Constructor.Methods
	
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

	// ----------------------------------------------- UiHandlers 
	
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

	// ----------------------------------------------- setEmployeeEventsDraftObject 
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft) {
		
		clearEventsGrid();
		
		this.employeeEventsDraft = employeeEventsDraft;
		yearLabel.setText(DateUtils.getYear()+"");
		
		//Descargar Variables actualizadas
		Integer actualYear = DateUtils.getYear();
		employeeEventsDraft.initializeDBEventsVariables(actualYear,
				r -> { 
					initializeVariablesToShow();
				},t -> {});
	}
	
	// ----------------------------------------------- setEmployeeEventsDraftObject.Methods
	
	private void clearEventsGrid() {
		for (int i = eventsGrid.getRowCount() - 1; i > 0; i--)
			eventsGrid.removeRow(i);
	}

	private void initializeVariablesToShow() {
		this.variablesToShow = 1;
		seeMenu.getEditableVariablesMenuItem().getScheduledCommand().execute();
	}
	
	private void fillCellsEvents() {
		clearEventsGrid();
		
		//Crear nuevas filas con las variables dadas
		for(String var : employeeEventsDraft.getEmployeeContractVariables(getVariablesToShow()))
			createVariableRow(var);
		
	}
	
    private void createVariableRow(String var) {
    	Integer actualYear = Integer.parseInt(yearLabel.getText());
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
					
					if(AonStringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-"){
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
					if(AonStringUtils.isBlank(eventCell.getValue()) || eventCell.getValue() == "-")
						eventCell.setValue("");
				}
			});
			
			eventCell.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					if(AonStringUtils.isBlank(eventCell.getValue()))
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
		
	}
    
    // ----------------------------------------------- EmployeeEventsDraft.Auxiliar Methods
    
   private boolean checkBlockVariables(int row) {
		Element element = eventsGrid.getWidget(row, 0).getElement().getFirstChildElement();
		if(element == null)
			return false;
		
		String variableName = element.getInnerText();
		return employeeEventsDraft.isCalendarVariable(variableName);
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
		
		Integer actualYear = Integer.parseInt(yearLabel.getText());
		
		int colStart = calculateCol(selectedPositions.getSelectedList().get(0));
		Integer monthStart = calculateMonthByColumn(colStart);
		
		int colEnd = calculateCol(selectedPositions.getSelectedList().get(selectedPositions.getSelectedList().size() - 1));
		Integer monthEnd = calculateMonthByColumn(colEnd);
		
		Date startDate = DateUtils.getDate(monthStart, actualYear);
		DateUtils.resetTime(startDate);
		
		Date endDateAux = DateUtils.getDate(monthEnd, actualYear);
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
		
		Date endOfNewYear = DateUtils.getLastDayOfMonth(DateUtils.getDate(11, this.year));
		
		if(isOutOfContractView(endOfNewYear)) {
			this.year -= changeYear;
		} else {
			this.yearLabel.setText(year+"");
			
			int actualYear = DateUtils.getYear();
			
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
			
			fillCellsEvents();
		}
	}
	
	public ArrayList<String> getVariablesToShow(){
		switch (variablesToShow) {
			case 0:
				return employeeEventsDraft.getAllVariables();
			case 1:
				return employeeEventsDraft.getAgreementVariables();
			case 2:
				return employeeEventsDraft.getCalendarVariables();
			default:
				return new ArrayList<String>();
		}
	}
	
	// ----------------------------------------------- DataGrid.Methods
	
	private void selectPosition(int row, int col) {
		//Guardar en SelectionModel
		int selectPos = (row * eventsGrid.getCellCount(row)) + col;
		selectedPositions.setSelected(selectPos, true);
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
			Integer nextYear = DateUtils.getYear() + 1;
			newEndDate = DateUtils.getLastDayOfMonth(DateUtils.getDate(11, nextYear));
		}
		return (date.before(this.employeeEventsDraft.getContractStartDate()) && 
				DateUtils.getYear(date) !=  DateUtils.getYear(this.employeeEventsDraft.getContractStartDate())) || 
				(date.after(newEndDate) && DateUtils.getYear(date) != DateUtils.getYear(newEndDate));
	}
	
	// ----------------------------------------------- DataGrid.ContextMenu
	
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
	
	// ----------------------------------------------- Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Incidencias");
		
		undoAllButton = new AonToolbarButton( "Restaurar últimos valores guardados", AON.CSS.aonIconUndo() );
		undoAllButton.addClickHandler(e -> {
			onUndo(e);
		});
		toolbar.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> {
			onSave(e);
		});
		toolbar.add(saveButton);
		
		newValueButton = new AonToolbarButton( "Nuevo valor", AON.CSS.aonIconAdd() );
		newValueButton.addClickHandler(e -> {
			onNewValue(e);
		});
		toolbar.add(newValueButton);
		
		visibilityButton = new AonToolbarButton( "Visualizaci\u00F3n", AON.CSS.aonIconVisibility() );
		visibilityButton.addClickHandler(e -> {
			onVisibility(e);
		});
		toolbar.add(visibilityButton);
		
		//Nombre variables para TEST
		newValueButton.ensureDebugId("new_value_complemento_i");
		visibilityButton.ensureDebugId("show_variables_menu_item");
		
		return toolbar;

	}

	// ----------------------------------------------- Toolbar.Methods

	private void onUndo(ClickEvent e) {
		initUndoAllDialog();
	}
	
	private void onSave(ClickEvent e) {
		employeeEventsDraft.updateDBCalendar(
				r -> {
					changeYear(0);
					saveButton.setEnabled(false);
					undoAllButton.setEnabled(false);
				}, 
				t -> {});
	}
	
	private void onNewValue(ClickEvent e) {
		openNewValueDialog(null);
	}
	
//	private void onVisibility(ClickEvent e) {
//		createEmployeeCheckBoxDialog();	
//	}
	
	private void initUndoAllDialog() {
		AonDialog dialog = new AonDialog("RESTAURAR", new HTML(String.valueOf("\u00BF")+"RESTAURAR INCIDENCIAS con los valores de la " + String.valueOf("\u00FA") + "ltima versi" + String.valueOf("\u00F3") + "n guardada?"));
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				//clearEventsGrid();
				Integer actualYear = DateUtils.getYear(new Date());
				employeeEventsDraft.initializeDBEventsVariables(
						actualYear,
						r -> { 
							fillCellsEvents();
							saveButton.setEnabled(true);
							undoAllButton.setEnabled(true);
						},
						t -> {});
			}
		});
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
				
				addEventVar(variable, value, startDate, endDate);
			}
		};
		
		inputDialog.show();
		inputDialog.center();
	}
	
	private void onVisibility(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		seeMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		seeMenu.show();
	}
	
}
