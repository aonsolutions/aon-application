package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public abstract class EmployeeEventsDraft extends Composite implements ContextMenuHandler {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeEventsDraftUiBinder uiBinder = GWT.create(EmployeeEventsDraftUiBinder.class);

	interface EmployeeEventsDraftUiBinder extends UiBinder<Widget, EmployeeEventsDraft> {}
	
	// ----------------------------------------------- EventTableCell 
	
	private class EventTableCell extends TextBox{
		
		int column;
		int row;
		
		public EventTableCell(int column, int row, String varName) {
			super();
			this.column = column;
			this.row = row;
			
			//ESTILOS
			this.ensureDebugId(varName.toLowerCase()+"_"+column);
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
			
			seeMenu.getAgreementVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAgreementVariablesMenuItem().removeStyleName(style.aonCheck());

			seeMenu.getContractVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getContractVariablesMenuItem().removeStyleName(style.aonCheck());

			seeMenu.getAllVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 2;
			selectedPositions.clear();
			
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
			
			seeMenu.getAgreementVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAgreementVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getContractVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getContractVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getAllVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 1;
			selectedPositions.clear();
			
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
			
			seeMenu.getAgreementVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAgreementVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getContractVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getContractVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getCalendarVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 0;
			selectedPositions.clear();
			
			fillCellsEvents(); 
		}
	}
	
	class AgreementVariablesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			seeMenu.getAgreementVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getAgreementVariablesMenuItem().addStyleName(style.aonCheck());
			
			seeMenu.getEditableVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getEditableVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getContractVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getContractVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getAllVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getCalendarVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 3;
			selectedPositions.clear();
			
			fillCellsEvents(); 
		}
	}
	
	class ContractVariablesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			seeMenu.getContractVariablesMenuItem().addStyleName("aon-MenuItemCheckYes");
			seeMenu.getContractVariablesMenuItem().addStyleName(style.aonCheck());
			
			seeMenu.getEditableVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getEditableVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getAgreementVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAgreementVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getAllVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getAllVariablesMenuItem().removeStyleName(style.aonCheck());
			
			seeMenu.getCalendarVariablesMenuItem().removeStyleName("aon-MenuItemCheckYes");
			seeMenu.getCalendarVariablesMenuItem().removeStyleName(style.aonCheck());
			
			variablesToShow = 4;
			selectedPositions.clear();
			
			fillCellsEvents(); 
		}
	}
	
	class SeeMenu extends ContextMenu {
				
		private MenuItem calendarVariablesMenuItem = null;
		private MenuItem editableVariablesMenuItem = null;
		private MenuItem agreementVariablesMenuItem = null;
		private MenuItem contractVariablesMenuItem = null;
		private MenuItem allVariablesMenuItem = null;
		
		public SeeMenu() {
			
			calendarVariablesMenuItem = addItem("Variables del calendario", new CalendarVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			calendarVariablesMenuItem.ensureDebugId("calendarVariablesMenuItem");
			
			editableVariablesMenuItem = addItem("Variables editables", new EditableVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			editableVariablesMenuItem.ensureDebugId("editableVariablesMenuItem");
			
			agreementVariablesMenuItem = addItem("Variables con datos en convenio", new AgreementVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			agreementVariablesMenuItem.ensureDebugId("agreementVariablesMenuItem");
			
			contractVariablesMenuItem = addItem("Variables con datos en contrato", new ContractVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			contractVariablesMenuItem.ensureDebugId("contractVariablesMenuItem");
			
			allVariablesMenuItem = addItem("Todas las variables", new AllVariablesCommand(), AON.AON_ICON_CMD_BUTTON, style.widthMenuItem(), style.cmdBtn());
			allVariablesMenuItem.ensureDebugId("allVariablesMenuItem");
			
		}

		public MenuItem getCalendarVariablesMenuItem() {
			return calendarVariablesMenuItem;
		}

		public MenuItem getEditableVariablesMenuItem() {
			return editableVariablesMenuItem;
		}
		
		public MenuItem getAgreementVariablesMenuItem() {
			return agreementVariablesMenuItem;
		}
		
		public MenuItem getContractVariablesMenuItem() {
			return contractVariablesMenuItem;
		}

		public MenuItem getAllVariablesMenuItem() {
			return allVariablesMenuItem;
		}
		
		public void hideAgreementMenuItem() {
			this.agreementVariablesMenuItem.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		public void showAgreementMenuItem() {
			this.agreementVariablesMenuItem.getElement().getStyle().clearDisplay();
		}
		
		public void hideContractMenuItem() {
			this.contractVariablesMenuItem.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		public void showContractMenuItem() {
			this.contractVariablesMenuItem.getElement().getStyle().clearDisplay();
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
		String cmdBtn();
		String container();
		String loadingPanel();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	HTMLPanel loadingPanel;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	FlexTable eventsGrid;
	
	// ----------------------------------------------- Variables 
	
	private EmployeeEventsDraftObject employeeEventsDraft;
	
	private OrderedMultiSelectionModel<Integer> selectedPositions = new OrderedMultiSelectionModel<>();
	
	private HashMap<String,Integer> variablesRow = new HashMap<>();
	
	private Integer variablesToShow = 1; // 0 = ALL_VARIABLES -- 1 = EDITABLE_VARS -- 2 = CALENDAR_VARS -- 3 = AGREEMENT_VARS -- 4 = CONTRACT_VARS
	
	private int year = DateUtils.getYear();
	
	private SeeMenu seeMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton newValueButton;
	private AonToolbarButton visibilityButton;
	private ListBox yearLB;
	
	// ----------------------------------------------- Constructor 
	
	protected EmployeeEventsDraft() {
		getToolbarPanel();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		seeMenu = new SeeMenu();
		
		initializeTable();
		
		initLoadingPanel();
		//showLoading();
		
		//Reescribir la accion del boton derecho del ratón dentro de la tabla
		eventsGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		saveButton.setEnabled(false);
		undoAllButton.setEnabled(false);
		
		scrollPanel.setHeight((Window.getClientHeight() - 150) + "px");
		
	}
	
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title);
	}

	// ----------------------------------------------- Constructor.Methods
	
	protected void initLoadingPanel() {
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Obteniendo variables de c\u00E1lculo del trabajador ...");
		loadingL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		loadingPanel.add(loadingBtn);
		loadingPanel.add(loadingL);
	}
	
	private void initializeTable() {
		Label blankLabel = new Label();
		blankLabel.addStyleName(style.firstHeadStyleHide());
		eventsGrid.setWidget(0, 0, blankLabel);
		
		String[] months = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT",
						   "NOV", "DIC"};
		
		for(int i=0; i<months.length; i++){
			eventsGrid.setText(0, i+1, months[i]);
			eventsGrid.getCellFormatter().addStyleName(0, i+1, style.cabeceraStyle());
		}	
	}

	// ----------------------------------------------- UiHandlers 
	
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
			if(AonStringUtils.containsIgnoreCase(variableName, "("))
				variableName = AonStringUtils.split(variableName, '(')[0].trim();
			ArrayList<EmployeeEventsVariable> employeeEventsVariables = employeeEventsDraft.getListEmployeeEventsVaribales(variableName);
			if(Boolean.FALSE.equals(employeeEventsDraft.isCalendarVariable(variableName))) {
				if(null != employeeEventsVariables && !employeeEventsVariables.isEmpty())
					openEventsDialog(variableName, employeeEventsVariables);
				else
					openNewValueDialog(variableName);
			}
		}
		
	}

	// ----------------------------------------------- setEmployeeEventsDraftObject 
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft) {
		
		clearEventsGrid();
		
		this.employeeEventsDraft = employeeEventsDraft;
		
		showLoading();
		
		//Descargar Variables actualizadas
		Integer actualYear = DateUtils.getYear();
		employeeEventsDraft.initializeDBEventsVariables(actualYear,
				r -> {
					initializeYearLB(this.yearLB);
					syncYearLBOptions();
					setSelectedValueLB(yearLB, (year+1900)+"");
					initializeVariablesToShow();
					showEvents();
					
				},t -> {});
		
		initializeToolBar();
	}
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraft, int [] years) {
		
		clearEventsGrid();
		
		this.employeeEventsDraft = employeeEventsDraft;
		
		showLoading();
		
		//Descargar Variables actualizadas
		employeeEventsDraft.initializeDBEventsVariables(years[0],
				r -> {
					yearLB.clear();
					for ( Integer aYear: years )
						yearLB.addItem(aYear.toString(), aYear.toString());
					
					yearLB.addChangeHandler(e -> changeYear());
					yearLB.setSelectedIndex(0);
					this.year = years[0] - 1900;
					
					initializeVariablesToShow();
					showEvents();
					
				},t -> {});
		
		initializeToolBar();
	}

	// ----------------------------------------------- setEmployeeEventsDraftObject.Methods
	
	private void clearEventsGrid() {
		for (int i = eventsGrid.getRowCount() - 1; i > 0; i--)
			eventsGrid.removeRow(i);
	}
	
	public void initializeYearLB(ListBox yearLB) {
		year = DateUtils.getYear();
		
		yearLB.clear();
		
		Integer yearAux = DateUtils.getYear();
		Integer previusYearIII = year - 3;
		Integer previusYearII = year - 2;
		Integer previusYear = year - 1;
		Integer nextYear = year + 1;
		
		yearLB.addItem(nextYear.toString(), nextYear.toString());
		yearLB.addItem(yearAux.toString(), yearAux.toString());
		yearLB.addItem(previusYear.toString(), previusYear.toString());
		yearLB.addItem(previusYearII.toString(), previusYearII.toString());
		yearLB.addItem(previusYearIII.toString(), previusYearIII.toString());
		
		yearLB.addChangeHandler(e -> changeYear());
		
		setSelectedValueLB(yearLB, year+"");
		year = year - 1900;
		
	}
	
	public void initializeToolBar() {
		newValueButton.setVisible(employeeEventsDraft.isEmployeeEvents());
		visibilityButton.setVisible(employeeEventsDraft.isEmployeeEvents());
	}
	
	private void syncYearLBOptions() {
		for(int i= this.yearLB.getItemCount() -1 ; i >= 0; i--) {
			Integer yearAux = Integer.parseInt(this.yearLB.getValue(i));
			Date lastDayOfYear = DateUtils.getLastDayOfYear(yearAux-1900);
			Date firstDayOfYear = DateUtils.getFirstDayOfYear(yearAux-1900);
			if(isOutOfContractPeriod(firstDayOfYear, lastDayOfYear)) {
				this.yearLB.removeItem(i);
			}
		}
		if ( this.yearLB.getItemCount() == 0 ) {
			Date contractEndDate = this.employeeEventsDraft.getContractEndDate();
			Integer contractEndYear = DateUtils.getYear(contractEndDate);
			this.yearLB.addItem(contractEndYear.toString(), contractEndYear.toString());
		}
	}
	
	private boolean isOutOfContractPeriod(Date firstDayOfYear, Date lastDayOfYear) {
		Date contractEndDate = this.employeeEventsDraft.getContractEndDate();
		
		Date contractStartDate = DateUtils.copyDateOnly(this.employeeEventsDraft.getContractStartDate());
		
		return contractStartDate.after(lastDayOfYear) || ( contractEndDate != null && contractEndDate.before(firstDayOfYear) );
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	private void initializeVariablesToShow() {
		if(employeeEventsDraft.getAgreementOnlyVariables().isEmpty())
			seeMenu.hideAgreementMenuItem();
		
		if(employeeEventsDraft.getContractVariables().isEmpty())
			seeMenu.hideContractMenuItem();
		
		this.variablesToShow = 1;
		seeMenu.getEditableVariablesMenuItem().getScheduledCommand().execute();
	}
	
	private void fillCellsEvents() {
		clearEventsGrid();
		
		//Crear nuevas filas con las variables dadas
		for(String contractVar : employeeEventsDraft.getEmployeeContractVariables(getVariablesToShow()))
			createVariableRow(contractVar);
		
	}
	
    private void createVariableRow(String var) {
    	Integer actualMonth = 0;
    	
    	int newRow = eventsGrid.insertRow(eventsGrid.getRowCount());
    	variablesRow.put(var, newRow);
		
		if (newRow % 2 == 1)
			eventsGrid.getRowFormatter().addStyleName(newRow, style.oddRowStyle());
		
		HorizontalPanel headPanel = new HorizontalPanel();
		Label headLabel = new Label(var);
		headLabel.addStyleName(style.firstHeadStyle());
		headLabel.getElement().getStyle().setWidth(270, Unit.PX);
		
		if (Boolean.TRUE.equals(employeeEventsDraft.isCalendarVariable(var))){
			Double acumulateYaer = employeeEventsDraft.getAcumulateYear(var);
			if(AonNumberUtils.notEquals(acumulateYaer, 0.0)) {
				headLabel.setText(var + " (" + acumulateYaer);
			
				if(AonStringUtils.containsIgnoreCase(var, "DIAS"))
					headLabel.setText(headLabel.getText() + " dias)");
				else if(AonStringUtils.containsIgnoreCase(var, "HORA"))
					headLabel.setText(headLabel.getText() + " horas)");
			}
		}
		
		if(Boolean.TRUE.equals(employeeEventsDraft.isAgreementVariable(var))) {
			String value = employeeEventsDraft.getAgreementVariablesValue(var);
			headLabel.setText(headLabel.getText() + " (" + value + ")");
		}
		
		headLabel.ensureDebugId(var.toLowerCase());
		
		headPanel.add(headLabel);
		
		if (Boolean.TRUE.equals(employeeEventsDraft.isCalendarVariable(var))){
			Button calendarButton = new Button();
			calendarButton.setStyleName("aon-editDataTable-button aon-icon-calendar");
			calendarButton.addClickHandler(e -> onShowCalendar());
			
			headPanel.add(calendarButton);
			headLabel.addStyleName(style.setBlockVariableStyle());
		}else {
			headLabel.removeStyleName(style.setBlockVariableStyle());
			headPanel.getElement().getStyle().setWidth(100, Unit.PCT);
			headLabel.getElement().getStyle().setTextAlign(TextAlign.LEFT);
		}
		
		if (newRow % 2 == 1)
			headLabel.addStyleName(style.cellOddFormat());
		else
			headLabel.addStyleName(style.cellFormat());
		
		eventsGrid.setWidget(newRow, 0, headPanel);
		
		//Rellenamos el resto de la fila
		
		ArrayList<EmployeeEventsVariable> varList = this.employeeEventsDraft.getListEmployeeEventsVaribales(var);
		
		for (int col = 1; col < 13 /*eventsGrid.getCellCount(newRow)*/; col++){
			
			EventTableCell eventCell = new EventTableCell(col, newRow, var);
			eventCell.addValueChangeHandler(e -> {
				//Borrar selecciones anteriores
				eraseSelectedPositions();
				selectPosition(eventCell.getRow(), eventCell.getColumn());
				
				if(AonStringUtils.isBlank(eventCell.getValue()) || AonStringUtils.equalsIgnoreCase(eventCell.getValue(), "-")){
					addValueSelectedPositions(null);
					eventCell.setText("-");
				}else
					addValueSelectedPositions(Double.parseDouble(eventCell.getText()));
				
				eraseSelectedPositions();
				eventCell.addStyleName(style.onChange());	
			});
			
			eventCell.addFocusHandler(e -> {
				if(AonStringUtils.isBlank(eventCell.getValue()) || AonStringUtils.equalsIgnoreCase(eventCell.getValue(), "-"))
					eventCell.setValue("");
			});
			
			eventCell.addBlurHandler(e -> {
				if(AonStringUtils.isBlank(eventCell.getValue()))
					eventCell.setValue("-");
			});
			
			if (Boolean.TRUE.equals(employeeEventsDraft.isCalendarVariable(var))){
				eventCell.setBlockVariableStyle();
				eventCell.setEnabled(false);
			} else{
				eventCell.removeBlockVariableStyle();
			}
			
			if(null == varList){
				eventCell.setTextBoxValue("-");
				eventsGrid.setWidget(newRow, col, eventCell);
				continue;
			}
			
			Double accumulateMonth = this.employeeEventsDraft.getAcumulateVariableByMonth(var, actualMonth, Integer.parseInt(yearLB.getSelectedItemText()));
			boolean hasMoreThanOneValue = this.employeeEventsDraft.hasMoreThanOneValue(var, actualMonth, Integer.parseInt(yearLB.getSelectedItemText()));
			
			if (newRow % 2 == 1) {
				eventCell.removeStyleName(style.bgcWhite());
			}else {
				eventCell.addStyleName(style.bgcWhite());
			}
			
			if (null == accumulateMonth){
				eventCell.setTextBoxValue("-");
				eventsGrid.setWidget(newRow, col, eventCell);
				if (newRow % 2 != 1)
					eventsGrid.getWidget(newRow, col).addStyleName(style.bgcWhite());
				actualMonth++;
				continue;
			}
			
			if (hasMoreThanOneValue) {
				eventCell.setTextBoxValue("[+1]");
				eventCell.setTitle("Este mes tiene varios tramos");
				eventCell.setEnabled(true);
				eventCell.addClickHandler(e -> {
					ArrayList<EmployeeEventsVariable> employeeEventsVariables = employeeEventsDraft.getListEmployeeEventsVaribales(var);
					openEventsDialog(var, employeeEventsVariables);
				});
			} else
				eventCell.setTextBoxValue(accumulateMonth.toString());
			
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
		changeYear();
		saveButton.setEnabled(true);
		undoAllButton.setEnabled(true);
	}
	
	private ArrayList<String> getVariablesWithOutCalendar() {
		ArrayList<String> result = new ArrayList<>();
		
		ArrayList<String> allEmployeeVariables = employeeEventsDraft.getAllVariables();
		
		for(String employeeVariable : allEmployeeVariables) {
			if(Boolean.TRUE.equals(employeeEventsDraft.isCalendarVariable(employeeVariable)))
				continue;
			result.add(employeeVariable);
		}
		
		return result;
	}
	
	private void addValueSelectedPositions(Double valueD) {
		int variableRow = calculateRow(selectedPositions.getSelectedList().get(0));
		String variable = eventsGrid.getWidget(variableRow, 0).getElement().getInnerText();
		if(AonStringUtils.containsIgnoreCase(variable, "("))
			variable = AonStringUtils.split(variable, '(')[0].trim();
		
		int colStart = calculateCol(selectedPositions.getSelectedList().get(0));
		Integer monthStart = calculateMonthByColumn(colStart);
		
		int colEnd = calculateCol(selectedPositions.getSelectedList().get(selectedPositions.getSelectedList().size() - 1));
		Integer monthEnd = calculateMonthByColumn(colEnd);
		
		Date startDate = DateUtils.getDate(monthStart, Integer.parseInt(yearLB.getSelectedItemText()));
		DateUtils.resetTime(startDate);
		
		Date endDateAux = DateUtils.getDate(monthEnd, Integer.parseInt(yearLB.getSelectedItemText()));
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

	private void changeYear() {
		String fullYear = this.yearLB.getSelectedValue();
		toolbar.setTitle("Incidencias " + fullYear);
		this.year = Integer.parseInt(fullYear) - 1900;
		fillCellsEvents();
	}
	
	public ArrayList<String> getVariablesToShow(){
		switch (variablesToShow) {
			case 0:
				return employeeEventsDraft.getAllVariables();
			case 1:
				return employeeEventsDraft.getAgreementVariables();
			case 2:
				return employeeEventsDraft.getCalendarVariables();
			case 3:
				return employeeEventsDraft.getAgreementOnlyVariables();
			case 4:
				return employeeEventsDraft.getContractVariables();
			default:
				return new ArrayList<>();
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
	
	// ----------------------------------------------- DataGrid.ContextMenu
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if(!selectedPositions.getSelectedList().isEmpty()){
			ContextMenu menu = new  ContextMenu();
			menu.addItem("A\u00f1adir nuevo valor", () -> openNewValueDialog(null));
			menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		    menu.show();
		}
	}
	
	// ----------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		
		this.toolbar = new AonToolbar("Variables de c\u00e1lculo");
		
		undoAllButton = new AonToolbarButton( "Restaurar últimos valores guardados", AON.CSS.aonIconUndo() );
		undoAllButton.addClickHandler(e -> onUndo());
		toolbar.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.ensureDebugId("employeeEventsDraftSaveButton");
		saveButton.addClickHandler(e -> onSave());
		toolbar.add(saveButton);
		
		newValueButton = new AonToolbarButton( "Nuevo valor", AON.CSS.aonIconAdd() );
		newValueButton.addClickHandler(e -> onNewValue());
		toolbar.add(newValueButton);
		
		visibilityButton = new AonToolbarButton( "Visualizaci\u00F3n", AON.CSS.aonIconVisibility() );
		visibilityButton.addClickHandler(e -> onVisibility(e));
		toolbar.add(visibilityButton);
		
		//Nombre variables para TEST
		newValueButton.ensureDebugId("new_value_complemento_i");
		visibilityButton.ensureDebugId("show_variables_menu_item");
		
		this.yearLB = new ListBox();
		yearLB.ensureDebugId("employeeEventsDraftYearListBox"); 
		toolbar.add(this.yearLB);
		
	}

	// ----------------------------------------------- Toolbar.Methods

	public void onUndo() {
		initUndoAllDialog();
	}
	
	public void onSave() {
		employeeEventsDraft.updateDBCalendar(
				r -> {
					// Descargar Variables actualizadas
//					String selectedYear = this.yearLB.getSelectedValue();
//					Integer actualYear = Integer.parseInt(selectedYear);
					showEvents();
					changeYear();
					saveButton.setEnabled(false);
					undoAllButton.setEnabled(false);
//					employeeEventsDraft.initializeDBEventsVariables(actualYear,
//							s -> {
//								initializeVariablesToShow();
//								showEvents();
//								changeYear();
//								saveButton.setEnabled(false);
//								undoAllButton.setEnabled(false);
//							}, f -> {});
				}, 
				t -> {});
	}
	
	public void onNewValue() {
		openNewValueDialog(null);
	}
	
	private void initUndoAllDialog() {
		AonDialog dialog = new AonDialog("RESTAURAR", new HTML(String.valueOf("\u00BF")+"RESTAURAR INCIDENCIAS con los valores de la \u00FAltima versi\u00F3n guardada?"));
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
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
	
	private void openEventsDialog(String variableName, ArrayList<EmployeeEventsVariable> employeeEventsVariables) {
		new EmployeeEventsDialog(
				variableName, 
				employeeEventsVariables,
				employeeEventsDraft.getContractStartDate(),
				employeeEventsDraft.getContractEndDate()
		) {
			@Override
			protected void onAccept() {
				ArrayList<EmployeeEventsVariable> newEmployeeEventsVariables = getEmployeeEventsVariables();
				employeeEventsDraft.setListEmployeeEventsVaribales(variableName, newEmployeeEventsVariables);
				
				changeYear();
				saveButton.setEnabled(true);
				undoAllButton.setEnabled(true);
			}
		};
	}
	
	private void openNewValueDialog(String variableName){
		if(null == variableName)
			variableName = "Nuevo valor";
		
		ArrayList<String> filterVariables = getVariablesWithOutCalendar();
		EmployeeInputDialog inputDialog = null;
		
		inputDialog = new EmployeeInputDialog(
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
	
	public void onVisibility(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		seeMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		seeMenu.show();
	}
	
	// -------------------------------------------------- DeckPanel.Methods

	protected void showLoading() {
		deckPanel.showWidget(0);
	}
	
	protected void showEvents() {
		deckPanel.showWidget(1);
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		mainPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
		 
	public EmployeeCalendarDraftObject getEmployeeCalendarObject() {
		return employeeEventsDraft.getEmployeeCalendar();
	}
	
	protected abstract void onShowCalendar();


    
}
