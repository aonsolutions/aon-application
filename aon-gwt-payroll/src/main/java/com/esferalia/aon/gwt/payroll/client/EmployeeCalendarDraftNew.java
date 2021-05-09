package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayTypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public class EmployeeCalendarDraftNew extends Composite implements ContextMenuHandler {

	private static EmployeeCalendarDraftNewUiBinder uiBinder = GWT.create(EmployeeCalendarDraftNewUiBinder.class);

	interface EmployeeCalendarDraftNewUiBinder extends UiBinder<Widget, EmployeeCalendarDraftNew> {}
	
	// ----------------------------------------------------------------------------------------------------
	//										CALENDAR DAYS TYPE CELL
	// ----------------------------------------------------------------------------------------------------
	
	public static interface CalendarDayTypeCell{
		void setAsType(DayType daytype, int row, int col);
		void setStyle(int row, int col);
		DayType getType();
	}
	
	public class DayTypeCell implements CalendarDayTypeCell{
		public DayType dayType;
		
		public DayTypeCell(DayType dayTypeAux) {
			this.dayType = dayTypeAux;
		}
		
		@Override
		public DayType getType() {
			return this.dayType;
		}
		
		@Override
		public void setAsType(DayType daytype, int row, int col) {
			this.dayType = daytype;
			setStyle(row, col);
		}
		
		@Override
		public void setStyle(int row, int col){
			dayType.visit(new DayTypeVisitor<Void>() {
				// Laborable
				@Override
				public Void visitWorkingDay(DayType dayType) {
					return null;
				}
				
				// No laborable
				@Override
				public Void visitNoWorkingDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.nonWorkingStyle());	
					return null;
				}
				
				// Vacaciones
				@Override
				public Void visitHolyDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.holidayStyle());	
					return null;
				}
				
				// Inactividad
				@Override
				public Void visitInactivityDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.inactivityStyle());
					return null;
				}
				
				// Ausencia
				@Override
				public Void visitDropDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.dropStyle());
					return null;
				}
				
				// Huelga
				@Override
				public Void visitStrikeDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.strikeStyle());	
					return null;
				}
				
				// ERE, ERE Fza, ERE Fza Exonerado
				@Override
				public Void visitEreDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.ereStyle());	
					return null;
				}
				
				@Override
				public Void visitEreFzaDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.ereFzaStyle());	
					return null;
				}
				
				@Override
				public Void visitEreFzaExonDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.ereFzaExonStyle());	
					return null;
				}
				
				@Override
				public Void visitEreFzaExonPartialDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.ereFzaExonStyle());	
					return null;
				}
				
				@Override
				public Void visitEreFzaExonEndDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.ereFzaExonStyle());	
					return null;
				}
				
				// IT
				@Override
				public Void visitITDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.itStyle());
					return null;
				}
				
				// Jornadas Reales
				@Override
				public Void visitRealDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.peonadasStyle());
					return null;
				}
				
				// Jornadas Teoricas
				@Override
				public Void visitIfDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.peonadasStyle());
					return null;
				}

				// Parcialidad
				@Override
				public Void visitPartialityDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.partialityStyle());
					return null;
				}
				
				// Otros
				@Override
				public Void visitFreeDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.freeDayStyle());	
					return null;
				}
				
				@Override
				public Void visitNoTypeDay(DayType dayType) {
					return null;
				}
			});	
		}
	}
	
	// ----------------------------------------------------------------------------------------------------
	//										CALENDAR TYPE CELL
	// ----------------------------------------------------------------------------------------------------
	
	public static interface CalendarTypeCell{
		void select(int row, int col);
		void unSelect(int row, int col);
		void setHour(int row, int col, String newHour);
		String getHour(int row, int col);
		void setOnChange(int row, int col);
		void eraseOnChange(int row, int col);
	}
	
	public class DayCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {
			if (cellsDates[row][col] != null){
				calendarGrid.getWidget(row, col).addStyleName(style.selectedStyle());
				selectedDates.setSelected(cellsDates[row][col], true);
			}
		}
		
		@Override
		public void unSelect(int row, int col) {
			if (cellsDates[row][col] != null) {
				calendarGrid.getWidget(row, col).removeStyleName(style.selectedStyle());
				selectedDates.setSelected(cellsDates[row][col], false);
			}
		}
		
		@Override
		public void setHour(int row, int col, String newHour) {}
		
		@Override
		public String getHour(int row, int col) {
			return "0";
		}
		
		@Override
		public void setOnChange(int row, int col) {}
		
		@Override
		public void eraseOnChange(int row, int col) {}
	}
	
	public class HourCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {}
		
		@Override
		public void unSelect(int row, int col) {}
		
		@Override
		public void setHour(int row, int col, String newHour) {
			Label widget = (Label)calendarGrid.getWidget(row, col);
			widget.setText(newHour);	
		}
		
		@Override
		public String getHour(int row, int col) {
			Label widget = (Label)calendarGrid.getWidget(row, col);
			return widget.getText();
		}
		
		@Override
		public void setOnChange(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.onChange());
			calendarGrid.getCellFormatter().addStyleName(row, col, style.onChange());
		}
		
		@Override
		public void eraseOnChange(int row, int col) {
			calendarGrid.getWidget(row, col).removeStyleName(style.onChange());
			calendarGrid.getCellFormatter().removeStyleName(row, col, style.onChange());	
		}
	}
	
	public class HourComplementaryCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {}
		
		@Override
		public void unSelect(int row, int col) {}
		
		@Override
		public void setHour(int row, int col, String newHour) {
			Label widget = (Label)calendarGrid.getWidget(row, col);
			widget.setText(newHour);	
		}
		
		@Override
		public String getHour(int row, int col) {
			Label widget = (Label)calendarGrid.getWidget(row, col);
			return widget.getText();
		}
		
		@Override
		public void setOnChange(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.onChange());
			calendarGrid.getCellFormatter().addStyleName(row, col, style.onChange());
		}
		
		@Override
		public void eraseOnChange(int row, int col) {
			calendarGrid.getWidget(row, col).removeStyleName(style.onChange());
			calendarGrid.getCellFormatter().removeStyleName(row, col, style.onChange());	
		}
	}
	
	public static class NoneCell implements CalendarTypeCell{
		
		public static NoneCell NONE_CELL = new NoneCell(); 
		
		private NoneCell() {}
		
		@Override
		public void select(int row, int col) {}
		@Override
		public void unSelect(int row, int col) {}
		@Override
		public void setHour(int row, int col, String newHour) {}
		@Override
		public String getHour(int row, int col) {
			return "0";
		}
		@Override
		public void setOnChange(int row, int col) {}
		@Override
		public void eraseOnChange(int row, int col) {}
	}
	
	// ----------------------------------------------- ScheduledCommand (DefinitionMenu)
	
	class NonWorkingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			EmployeeCalendarNonWorkingDialog nonWorkingDialog = new EmployeeCalendarNonWorkingDialog(
					employeeCalendarDraftObject.getNonWorkingDays()) {
				
				@Override
				protected void onAccept() {
					employeeCalendarDraftObject.setNonWorkingDays(getNonWorkingDays());
					onChange();
					changeYear();
				}
			};
			
			nonWorkingDialog.center();
			nonWorkingDialog.show();
		}
	}
	
	class HourCommand implements ScheduledCommand {

		@Override
		public void execute() {
			initHourDialog();
		}
	}
	
	class ShowHourCommand implements ScheduledCommand {

		@Override
		public void execute() {
			definitionMenu.getShowHourMenuItem().setStyleName("aon-MenuItemCheckYes", showHours);
			definitionMenu.getShowHourMenuItem().addStyleName(style.aonCheck());
			
			showHours = !showHours;
			
			if (showHours) {
				showHoursRows();	
			} else {
				hideHoursRows();
			}
		}
	}
	
	class ShowHourExtraComplCommand implements ScheduledCommand {

		@Override
		public void execute() {
			definitionMenu.getShowHourExtraComplMenuItem().setStyleName("aon-MenuItemCheckYes", showHoursExtraCompl);
			definitionMenu.getShowHourExtraComplMenuItem().addStyleName(style.aonCheck());
			
			showHoursExtraCompl = !showHoursExtraCompl;
			
			if (showHoursExtraCompl) {
				showHoursExtraComplRows();	
			} else {
				hideHoursExtraComplRows();
			}
		}
	}
	
	class DefinitionMenu extends ContextMenu {
				
		private MenuItem nonWorkingMenuItem = null;
		private MenuItem hourMenuItem = null;
		private MenuItemSeparator separator;
		private MenuItem showHourMenuItem = null;
		private MenuItem showHourExtraComplMenuItem = null;
		
		public DefinitionMenu() {
			
			nonWorkingMenuItem = addItem("Definir semana laboral", new NonWorkingCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			nonWorkingMenuItem.ensureDebugId("nonWorkingMenuItem");
			
			hourMenuItem = addItem("Definir horas semanales", new HourCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			hourMenuItem.ensureDebugId("hourMenuItem");
			
			separator = addSeparator();
			
			showHourMenuItem = addItem("Ocultar horas", new ShowHourCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			showHourMenuItem.ensureDebugId("showHourMenuItem");
			
			showHourExtraComplMenuItem = addItem("Ocultar horas complementarias", new ShowHourExtraComplCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			showHourExtraComplMenuItem.ensureDebugId("showHourExtraComplMenuItem");
		}

		public MenuItem getNonWorkingMenuItem() {
			return nonWorkingMenuItem;
		}

		public MenuItem getHourMenuItem() {
			return hourMenuItem;
		}

		public MenuItem getShowHourMenuItem() {
			return showHourMenuItem;
		}
		
		public MenuItem getShowHourExtraComplMenuItem() {
			return showHourExtraComplMenuItem;
		}
		
		public void setExtraText() {
			this.showHourExtraComplMenuItem.setText("Ocultar horas extras");
		}
		
		public void setComplementaryText() {
			this.showHourExtraComplMenuItem.setText("Ocultar horas complementarias");
		}
		
		public void hideSeparator() {
			separator.setVisible(false);
		}
		
		public void showSeparator() {
			separator.setVisible(true);
		}
		
	}
	
	// ----------------------------------------------- ScheduledCommand (UtilityMenu)
	
	class EraseEventCommand implements ScheduledCommand {

		@Override
		public void execute() {
			initDatesDialog(DayType.NOTYPEDAY);
		}
	}
	
	class UndoAllCommand implements ScheduledCommand {

		@Override
		public void execute() {
			initUndoAll();
		}
	}
	
	class ResetMenuCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("RESETEAR", new HTML(String.valueOf("\u00BF")+"RESETEAR CALENDARIO con los valores INICIALES? Se BORRARAN todos los cambios realizados."));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					employeeCalendarDraftObject.resetCalendarInfo(
							s -> {
								// Init save and undo all
								onSaved();
								changeYear();
							}, f -> {}
					);
				}
			});
		}
	}
	
	class UtilityMenu extends ContextMenu {
				
		private MenuItem eraseEventMenuItem = null;
		private MenuItem undoAllMenuItem = null;
		private MenuItem resetMenuItem = null;
		
		public UtilityMenu() {
			
			eraseEventMenuItem = addItem("Eliminar valores modificados", new EraseEventCommand(), 
					AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			eraseEventMenuItem.ensureDebugId("eraseEventMenuItem");
			
			undoAllMenuItem = addItem("Restaurar \u00FAltimos valores guardados", new UndoAllCommand(), 
					AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			undoAllMenuItem.ensureDebugId("undoAllMenuItem");
			
			addSeparator();
			
			resetMenuItem = addItem("Resetear calendario", new ResetMenuCommand(), 
					AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			resetMenuItem.ensureDebugId("resetMenuItem");
		}

		public MenuItem getEraseEventMenuItem() {
			return eraseEventMenuItem;
		}

		public MenuItem getUndoAllMenuItem() {
			return undoAllMenuItem;
		}

		public MenuItem getResetMenuItem() {
			return resetMenuItem;
		}
		
	}
	
	// ----------------------------------------------------------------------------------------------------
	//											UI FIELDS
	// ----------------------------------------------------------------------------------------------------
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hourStyle();
		String labelStyle();
		String cursorPointer();
		String aonCheck();
		String hide();
		String selectedStyle();
		String onChange();
		// DayType Style
		String dayTypeButton();
		String nonWorkingStyle();
		String holidayStyle();
		String inactivityStyle();
		String dropStyle();
		String strikeStyle();
		String ereStyle();
		String ereFzaStyle();
		String ereFzaExonStyle();
		String itStyle();
		String peonadasStyle();
		String partialityStyle();
		String freeDayStyle();
		// Out of contract
		String outOfContractStyle();
		String cmd_btn();
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	VerticalPanel mainContainer;
	
	// ---------------------------- Calendar (UiField)
	
	@UiField
	ScrollPanel scrollInfo;
	
	@UiField
	Grid calendarGrid;
	
	// Days Types
	
	@UiField
	Button workingDayButton;
	
	@UiField
	Button nonWorkingDayButton;
	
	@UiField
	Button inactivityDayButton;
	
	@UiField
	Button dropDayButton;
	
	@UiField
	Button holidayDayButton;
	
	@UiField
	Button partialityDayButton;
	
	@UiField
	Button peonadasDayButton;
	
	// ---------------------------- Days Hours (UiField)
	
	@UiField
	Button hourButton;
	
	@UiField
	Button extraHoursButton;
	
	// ---------------------------- Clean and Leyend (UiField)
	
	@UiField
	Button eraseEventButton;
	
	@UiField
	Button leyendButton;
	
	
	private int totalCols = 38;
	private int totalRows = 37;
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	
	private OrderedMultiSelectionModel<Date> selectedDates = new OrderedMultiSelectionModel<Date>();
	
	private final CalendarTypeCell cellsType[][] = new CalendarTypeCell[totalRows][totalCols];
	private final CalendarDayTypeCell cellsDayType[][] = new CalendarDayTypeCell[totalRows][totalCols];
	private final Date cellsDates[][] = new Date[totalRows][totalCols];
	
	private boolean showHours;
	private boolean showHoursExtraCompl;
	private int month;
	private int year = DateUtils.getYear();
	
	private DefinitionMenu definitionMenu;
	private UtilityMenu utilityMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton definitionButton;
	private AonToolbarButton utilityButton;
	private ListBox yearLB;
	
	public EmployeeCalendarDraftNew() {
		toolbar = getToolbarPanel();
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		scrollInfo.setHeight((Window.getClientHeight() - 220) + "px");
		
		definitionMenu = new DefinitionMenu();
		utilityMenu = new UtilityMenu();
		
		// Add contextMenu
		calendarGrid.addDomHandler(this, ContextMenuEvent.getType());
		
	}
	
	public void setContrataEmployeeCalendarHeight(){
		scrollInfo.setHeight((Window.getClientHeight() - 290) + "px");
	}

	// ----------------------------------------------------------------------------------------------------
	//									SET EMPLOYEE CALENDAR DRAFT
	// ----------------------------------------------------------------------------------------------------
	
	public void setEmployeeCalendarDraftObject(EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		this.employeeCalendarDraftObject.initCalendarInfo(s -> {
			// Init save and undo all
			onSaved();
			initializeYearLB(this.yearLB);
			hideYearLBOptions();
			setSelectedValueLB(yearLB, (year+1900)+"");
			changeYear();
		}, f -> {});
		
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	//											UI HANDLERS
	// ----------------------------------------------------------------------------------------------------
	
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		
		event.preventDefault();
		
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * totalCols) + col;
		
		if(cellsType[row][col] == null)
			return;
		
		//Pulsacion celda con SHIFT
		if (event.isShiftKeyDown()){ 
			int initialPosition = calculateDatePosition(selectedDates.getSelectedList().get(0));
			
			if(initialPosition != -1){
				int endPosition = pos;
				
				// Por si seleccionan una fecha anterior a la previamente seleccionada
				if (initialPosition > endPosition){
					int lastPosAux = initialPosition;
					int initialPosAux = endPosition;
					initialPosition = initialPosAux;
					endPosition = lastPosAux;
				}
				
				while (initialPosition <= endPosition){
					int rowIdx = calculatePositionRow(initialPosition);
					int colIdx = calculatePositionCol(initialPosition);
					
					cellsType[rowIdx][colIdx].select(rowIdx, colIdx);
					initialPosition++;	
				}
			}
		
		//Pulsacion una sola celda	
		} else { 
			unSelectSelectedDates();
			cleanSelectedDates();
			
			if (isMonthSelected(row, col)){
				for(int i = 1; i<totalCols; i++) {
					if(null != cellsType[row][i])
						cellsType[row][i].select(row, i);
				}
			}else {
				cellsType[row][col].select(row, col);
			}
			
		}
	
	}
	
	// --------------------------- Days Types
	
	@UiHandler("workingDayButton")
	public void onWorkingDayButtonClick(ClickEvent event) {
		initDatesDialog(DayType.WORKINGDAY);
	}

	@UiHandler("nonWorkingDayButton")
	public void onNonWorkingDayButtonClick(ClickEvent event) {
		initDatesDialog(DayType.NOWORKINGDAY);
	}
	
	@UiHandler("inactivityDayButton")
	public void onInactivityDayButtonClick(ClickEvent event) {
		initiInactivityDialog(DayType.INACTIVITY);
	}

	@UiHandler("dropDayButton")
	public void onDropDayButtonClick(ClickEvent event) {
		initDropDialog(DayType.DROPDAY);
	}

	@UiHandler("holidayDayButton")
	public void onHolidayDayButtonClick(ClickEvent event) {
		initDatesDialog(DayType.HOLIDAY);
	}
	
	@UiHandler("partialityDayButton")
	public void onPartialityDayButtonClick(ClickEvent event) {
		initPartialityDialog(DayType.PARTIALITY);
	}

	@UiHandler("peonadasDayButton")
	public void onPeonadasDayButtonClick(ClickEvent event) {
		initiAgrarianDialog(DayType.REAL_DAYS);
	}
	
	// --------------------------- Days Hours
	
	@UiHandler("hourButton")
	public void onHourButtonClick(ClickEvent event) {
		initHourDialog();
	}

	@UiHandler("extraHoursButton")
	public void onExtraHoursButtonClick(ClickEvent event) {
		initHourExtraComplDialog();
	}
	
	// --------------------------- Clean and Leyend
	
	@UiHandler("eraseEventButton")
	public void onEraseEventButtonClick(ClickEvent event) {
		initDatesDialog(DayType.NOTYPEDAY);
	}

	@UiHandler("leyendButton")
	public void onLeyendButtonClick(ClickEvent event) {
		EmployeeCalendarLeyendDialog leyendDialog = new EmployeeCalendarLeyendDialog();
		leyendDialog.center();
		leyendDialog.show();
	}
	
	

	// ----------------------------------------------------------------------------------------------------
	//											MAIN METHODS
	// ----------------------------------------------------------------------------------------------------

	private void initCalendar() {
		// Set fulltime journey
		if(this.employeeCalendarDraftObject.isFullTimeJourney()) {
			this.showHours = false;
			this.showHoursExtraCompl = true;
			
			hideHoursRows();
			showHoursExtraComplRows();
			
			showElement(definitionMenu.getShowHourExtraComplMenuItem().getElement());
			
			hideElement(definitionMenu.getShowHourMenuItem().getElement());
			hideElement(definitionMenu.getHourMenuItem().getElement());
			hideElement(hourButton.getElement());
			definitionMenu.showSeparator();
			
			definitionMenu.setExtraText();
			
			extraHoursButton.setText("H. Extras");
			definitionMenu.getNonWorkingMenuItem().getElement().getStyle().clearDisplay();
			definitionMenu.getHourMenuItem().getElement().getStyle().setDisplay(Display.NONE);
			
		} else {
			this.showHours = true;
			this.showHoursExtraCompl = true;
			
			showHoursRows();
			showHoursExtraComplRows();
			
			showElement(definitionMenu.getShowHourMenuItem().getElement());
			showElement(definitionMenu.getShowHourExtraComplMenuItem().getElement());
			showElement(definitionMenu.getHourMenuItem().getElement());
			showElement(hourButton.getElement());
			definitionMenu.showSeparator();
			
			definitionMenu.setComplementaryText();
			
			extraHoursButton.setText("H. Complementarias");
			definitionMenu.getNonWorkingMenuItem().getElement().getStyle().setDisplay(Display.NONE);
			definitionMenu.getHourMenuItem().getElement().getStyle().clearDisplay();
		}
		
		// Clear calendar
		cleanCalendar();
		initCellsType();
		initDayTypeCellsType();
		cleanSelectedDates();
		this.month = 0;
		
		// Set agrarian contract
		if(this.employeeCalendarDraftObject.isAgrarianContract()) {
			showElement(peonadasDayButton.getElement());
		} else {
			hideElement(peonadasDayButton.getElement());
		}
		
		//Crear calendario
		for (int row = 1; row < totalRows; row += 3)
			paintCalendar(row);		
	}

	@SuppressWarnings("deprecation")
	private void paintCalendar(int row) {
		// Mostrar días del mes
		Integer contDays = 1;
		
		int firstDayOfMonth = calculateNumberDayOfWeek(new Date(year, month, 1));
		int lastDayOfMonth = calculateLastDayOfMonth(new Date(year, month, 1));
		
		// Primera semana
		for (int i = 1; i <= 7; i++) {
			
			// Label insetar
			Label labelDay = new Label();

			if (i < firstDayOfMonth) {
				labelDay.setText("");
				calendarGrid.setWidget(row, i, labelDay);
				
				// Add to cells
				cellsDates[row][i] = null;
				cellsType[row][i] = new NoneCell();
				cellsType[row + 1][i] = new NoneCell();
				cellsType[row + 2][i] = new NoneCell();
			} else {
				
				Date currentDay = new Date(year, month, contDays);
				DateUtils.resetTime(currentDay);
				
				// Set Label day and Label hours
				labelDay.setText(contDays.toString());
				labelDay.addStyleName(style.cursorPointer());
				labelDay.addStyleName(style.labelStyle());
				
				Label hourDay = new Label();
				hourDay.addStyleName(style.hourStyle());
				hourDay.setText("-");
				
				Label hourExtraComplDay = new Label();
				hourExtraComplDay.addStyleName(style.hourStyle());
				hourExtraComplDay.setText("-");
				
				// Check if currentDay is between contract period
				if(isOutOfContractPeriod(currentDay)) {
					labelDay.addStyleName(style.outOfContractStyle());
					hourDay.addStyleName(style.outOfContractStyle());
					hourExtraComplDay.addStyleName(style.outOfContractStyle());
					cellsType[row][i] = null;
					cellsType[row][i+1] = null;
				} else {
					// Add to cells
					cellsDates[row][i] = currentDay;
					cellsType[row][i] = new DayCell();
					cellsType[row + 1][i] = new HourCell();
					cellsType[row + 2][i] = new HourComplementaryCell();
					
					// ------------ Set Type Day
					
					labelDay.setText(contDays.toString());
					labelDay.addStyleName(style.cursorPointer());
					labelDay.addStyleName(style.labelStyle());
					calendarGrid.setWidget(row, i, labelDay);
					
					// Add day type
					DayType dayType = this.employeeCalendarDraftObject.getDayTypeByDate(currentDay);
					
					// Si es un dia sin tipo y segun el calendario es no laborable
					if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isDefaultNonWorkongDay(currentDay)) {
						dayType = DayType.NOWORKINGDAY;
					}
					
					// Si es un dia sin tipo y es un dia con parcialidad
					if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isPartialityDayTypeByDate(currentDay)) {
						dayType = DayType.PARTIALITY;
					}
					
					// Si es un dia sin tipo y es un dia con parcialidad
					if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isDefaultFreeDay(currentDay)) {
						dayType = DayType.FREEDAY;
					}
					
					// Si es un dia fin ERTE se le ponete notypeday
					if(dayType == DayType.EREFZAEXONENDDAY) {
						dayType = DayType.NOTYPEDAY;
					}
					
					// Poner el titulo al label en funcion del tipo de dia
					setTittleOfDayType(dayType, currentDay, labelDay);
					
					cellsDayType[row][i].setAsType(dayType, row, i);
					
					// ------------ Set Hour Day
					
					// Add hour info is not is empty CalendarHours
					if(!this.employeeCalendarDraftObject.isCalendarHourIsEmpty()) {
						Double dayHour = this.employeeCalendarDraftObject.getHourByDate(currentDay);
						
						if(null != dayHour)
							hourDay.setText(dayHour.toString());
						else
							cellsDayType[row][i].setAsType(DayType.NOWORKINGDAY, row, i);
					}
					
					// ------------ Set Hour Extra / Complementary Hour
					
					// Add hour info is not is empty CalendarHours
					if(!this.employeeCalendarDraftObject.isCalendarHourExtraComplIsEmpty()) {
						Double dayHourExtraCompl = this.employeeCalendarDraftObject.getHourExtraComplByDate(currentDay);
						
						if(null != dayHourExtraCompl)
							hourExtraComplDay.setText(dayHourExtraCompl.toString());
						
					}
					
				}
				
				calendarGrid.setWidget(row, i, labelDay);
				calendarGrid.setWidget(row + 1, i, hourDay);
				calendarGrid.setWidget(row + 2, i, hourExtraComplDay);
				
				// Increase contDays
				contDays++;	
				
			}
		}

		int actualDayOfWeek = 1;
		int column = 0;
		
		while (contDays <= lastDayOfMonth) {
			// Dia Acutal
			Date currentDay = new Date(year, month, contDays);
			DateUtils.resetTime(currentDay);
			
			// Por que ya llevamos 7 dias pintados
			column = 7 + actualDayOfWeek;
			
			// Set Label day and Label hours
			Label labelDay = new Label(contDays.toString());
			labelDay.addStyleName(style.cursorPointer());
			labelDay.addStyleName(style.labelStyle());
			calendarGrid.setWidget(row, column, labelDay);;
			
			Label hourDay = new Label();
			hourDay.addStyleName(style.hourStyle());
			hourDay.setText("-");
			
			Label hourExtraComplDay = new Label();
			hourExtraComplDay.addStyleName(style.hourStyle());
			hourExtraComplDay.setText("-");
			
			// Check if currentDay is between contract period
			if(isOutOfContractPeriod(currentDay)) {
				labelDay.addStyleName(style.outOfContractStyle());
				hourDay.addStyleName(style.outOfContractStyle());
				hourExtraComplDay.addStyleName(style.outOfContractStyle());
				cellsType[row][column] = null;
				cellsType[row][column+1] = null;
			} else {
				// Add to cells
				cellsDates[row][column] = currentDay;
				cellsType[row][column] = new DayCell();
				cellsType[row + 1][column] = new HourCell();
				cellsType[row + 2][column] = new HourComplementaryCell();
				
				// ------------ Set Type Day
				
				// Add day type
				DayType dayType = this.employeeCalendarDraftObject.getDayTypeByDate(currentDay);
				
				// Si es un dia sin tipo y segun el calendario es no laborable
				if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isDefaultNonWorkongDay(currentDay)) {
					dayType = DayType.NOWORKINGDAY;
				}
				
				// Si es un dia sin tipo y es un dia con parcialidad
				if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isPartialityDayTypeByDate(currentDay)) {
					dayType = DayType.PARTIALITY;
				}
				
				// Si es un dia sin tipo y es un dia con parcialidad
				if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isDefaultFreeDay(currentDay)) {
					dayType = DayType.FREEDAY;
				}
				
				// Si es un dia fin ERTE se le ponete notypeday
				if(dayType == DayType.EREFZAEXONENDDAY) {
					dayType = DayType.NOTYPEDAY;
				}
				
				// Poner el titulo al label en funcion del tipo de dia
				setTittleOfDayType(dayType, currentDay, labelDay);
				
				cellsDayType[row][column].setAsType(dayType, row, column);
				
				// ------------ Set Hour Day
				
				// Add hour info is not is empty CalendarHours
				if(!this.employeeCalendarDraftObject.isCalendarHourIsEmpty()) {
					Double dayHour = this.employeeCalendarDraftObject.getHourByDate(currentDay);
					
					if(null != dayHour)
						hourDay.setText(dayHour.toString());
					else
						cellsDayType[row][column].setAsType(DayType.NOWORKINGDAY, row, column);
				}
				
				// ------------ Set Hour Complementary Day
				
				// Add hour info is not is empty CalendarHours
				if(!this.employeeCalendarDraftObject.isCalendarHourExtraComplIsEmpty()) {
					Double dayHourExtraCompl = this.employeeCalendarDraftObject.getHourExtraComplByDate(currentDay);
					
					if(null != dayHourExtraCompl)
						hourExtraComplDay.setText(dayHourExtraCompl.toString());
					
				}
			}
			
			calendarGrid.setWidget(row, column, labelDay);
			calendarGrid.setWidget(row+1, column, hourDay);
			calendarGrid.setWidget(row+2, column, hourExtraComplDay);
			
			contDays++;
			actualDayOfWeek++;
			
		}
		
		// Increment month to paint the next one
		month++;
	}

	// ----------------------------------------------------------------------------------------------------
	//									AUXILIAR METHODS (CALENDAR VIEW)
	// ----------------------------------------------------------------------------------------------------

	private void showHoursRows() {
		for (int i = 2; i < totalRows; i += 3)
			calendarGrid.getRowFormatter().removeStyleName(i, style.hide());	
	}

	private void hideHoursRows() {
		for (int i = 2; i < totalRows; i += 3)
			calendarGrid.getRowFormatter().addStyleName(i, style.hide());
	}
	
	private void showHoursExtraComplRows() {
		for (int i = 3; i < totalRows; i += 3)
			calendarGrid.getRowFormatter().removeStyleName(i, style.hide());	
	}

	private void hideHoursExtraComplRows() {
		for (int i = 3; i < totalRows; i += 3)
			calendarGrid.getRowFormatter().addStyleName(i, style.hide());
	}
	
	private void hideElement(Element element) {
		element.getStyle().setDisplay(Display.NONE);
	}
	
	private void showElement(Element element) {
		element.getStyle().clearDisplay();
	}
	
	private boolean isOutOfContractPeriod(Date date) {
		Date newEndDate = this.employeeCalendarDraftObject.getContractEndDate();
		if(null == newEndDate) {
			Integer nextYear = new Date().getYear() + 2;
			newEndDate = new Date(nextYear, 11, 31);
		}
		
		Date startDate = DateUtils.copyDateOnly(this.employeeCalendarDraftObject.getContractStartDate());
		
		return date.before(startDate) || date.after(newEndDate);
	}
	
	private boolean isOutOfContractView(Date date) {
		Date newEndDate = this.employeeCalendarDraftObject.getContractEndDate();
		if(null == newEndDate) {
			Integer nextYear = new Date().getYear() + 1;
			newEndDate = new Date(nextYear, 11, 31);
		}
		return (date.before(this.employeeCalendarDraftObject.getContractStartDate()) && date.getYear() !=  this.employeeCalendarDraftObject.getContractStartDate().getYear()) || 
				(date.after(newEndDate) && date.getYear() != newEndDate.getYear());
	}
	
	// ----------------------------------------------------------------------------------------------------
	//										SAVE / UNDO ALL
	// ----------------------------------------------------------------------------------------------------
	
	private void onSaved(){
		saveButton.setEnabled(false);
		undoAllButton.setEnabled(false);
	}
	
	private void onChange(){
		saveButton.setEnabled(true);
		undoAllButton.setEnabled(true);
	}
	
	private void initUndoAll() {
		AonDialog dialog = new AonDialog("RESTAURAR", new HTML(String.valueOf("\u00BF")+"RESTAURAR CALENDARIO con los valores de la " + String.valueOf("\u00FA") + "ltima versi" + String.valueOf("\u00F3") + "n guardada?"));
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				employeeCalendarDraftObject.initCalendarInfo(s -> {
					onSaved();
					initCalendar();
				}, f -> {});
			}
		});
	}
	
	// ----------------------------------------------------------------------------------------------------
	//										CHANGE YEAR EVENT
	// ----------------------------------------------------------------------------------------------------
	
	private void changeYear() {
		String fullYear = this.yearLB.getSelectedValue();
		toolbar.setTitle("Calendario " + fullYear);
		this.year = Integer.parseInt(fullYear) - 1900;
		initCalendar();
	}
	
	// ----------------------------------------------------------------------------------------------------
	//									INIT DAY TYPES DIALOG
	// ----------------------------------------------------------------------------------------------------

	private void initDatesDialog(DayType dayType) {
		// Si no hay nada seleccionado en el calendario mostramos dialogo
		if(this.selectedDates.getSelectedList().isEmpty()) {
			String caption = getCaptionByDayType(dayType);
			EmployeeCalendarDatesDialog datesDialog =
				new EmployeeCalendarDatesDialog(
					caption, 
					this.employeeCalendarDraftObject.getContractStartDate(),
					this.employeeCalendarDraftObject.getContractEndDate()) {
				@Override
				protected void onAccept() {
					Date startDate = getStartDate();
					Date endDate = getEndDate();
					Integer daysBetween = getDaysBetween(startDate, endDate);
					addDayType(startDate, endDate, dayType,  Integer.toString(daysBetween));
					if(DayType.NOTYPEDAY == dayType)
						addPartialityDayType(startDate, endDate, dayType, Integer.toString(daysBetween));
				}
			};
			
			datesDialog.center();
			datesDialog.show();
		} else {
			this.selectedDates.getSelectedList().sort(null);
			Date startDate = this.selectedDates.getSelectedList().get(0);
			Date endDate = this.selectedDates.getSelectedList().get(this.selectedDates.getSelectedList().size() - 1);
			Integer daysBetween = getDaysBetween(startDate, endDate);
			addDayType(startDate, endDate, dayType,  Integer.toString(daysBetween));
			if(DayType.NOTYPEDAY == dayType)
				addPartialityDayType(startDate, endDate, dayType, Integer.toString(daysBetween));
		}
		
	}
	
	private void initiInactivityDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
		EmployeeCalendarInactivityDialog inactivityDialog = 
			new EmployeeCalendarInactivityDialog(
				caption, 
				this.selectedDates.getSelectedList(),
				this.employeeCalendarDraftObject.getContractStartDate(),
				this.employeeCalendarDraftObject.getContractEndDate()) {
			
			@Override
			protected void onAccept() {
				Date startDate = getStartDate();
				Date endDate = getEndDate();
				Integer selectedTypeIdx = getTypeIdxInactivity();
				if(selectedTypeIdx <= 3) {
					String inactivityCause = getTypeInactivity();
					addDayType(startDate, endDate, dayType, inactivityCause);
				} else {
					String coefficient = Double.toString(getPercentValue());
					Boolean isEndERTE = isEndERTE();
					switch (selectedTypeIdx) {
						case 4:
							addDayType(startDate, endDate, DayType.EREDAY, coefficient);
							break;
						case 5:
							addDayType(startDate, endDate, DayType.EREFZADAY, coefficient);
							break;
						case 6:
							if(isEndERTE) {
								if(null != endDate) {
									Date newDate = DateUtils.copyDateOnly(endDate);
									DateUtils.addDays2Date(newDate, 1);
									employeeCalendarDraftObject.addDayType(newDate, newDate, DayType.EREFZAEXONENDDAY, "1");
								}
							}
							
							addDayType(startDate, endDate, DayType.EREFZAEXONDAY, coefficient);
							break;
						default:
							break;
					}
				}
				
			}
		};
		inactivityDialog.center();
		inactivityDialog.show();
	}
	
	private void initiAgrarianDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
		EmployeeCalendarAgrarianDialog agrarianDialog = 
			new EmployeeCalendarAgrarianDialog(
				caption, 
				this.selectedDates.getSelectedList(),
				this.employeeCalendarDraftObject.getContractStartDate(),
				this.employeeCalendarDraftObject.getContractEndDate()) {
			
			@Override
			protected void onAccept() {
				Date startDate = getStartDate();
				Date endDate = getEndDate();
				Integer daysBetween = getDaysBetween(startDate, endDate);
				switch (getType()) {
				case "JORNADAS_REALES":
					addDayType(startDate, endDate, DayType.REAL_DAYS, Integer.toString(daysBetween));
					break;
				case "JORNADAS_TEORICAS":
					addDayType(startDate, endDate, DayType.IF_DAYS, Integer.toString(daysBetween));
					break;
				default:
					break;
				}
			}
		};
		agrarianDialog.center();
		agrarianDialog.show();
	}

	private void initDropDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
		EmployeeCalendarPercentDialog percentDialog = new EmployeeCalendarPercentDialog(
				caption, 
				this.selectedDates.getSelectedList(),
				this.employeeCalendarDraftObject.getContractStartDate(),
				this.employeeCalendarDraftObject.getContractEndDate()) {
			
			@Override
			protected void onAccept() {
				Date startDate = getStartDate();
				Date endDate = getEndDate();
				String coefficient = Double.toString(getPercentValue());
				Integer typeDrop = getTypeDrop();
				switch (typeDrop) {
					case 0:
						addDayType(startDate, endDate, DayType.STRIKEDAY, coefficient);
						break;
					case 1:
						addDayType(startDate, endDate, DayType.DROPDAY, coefficient);
						break;
					default:
						break;
				}	
			}
		};
		
		percentDialog.center();
		percentDialog.show();
	}
	
	
	private void initPartialityDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
		EmployeeCalendarPartialityDialog partialityDialog = new EmployeeCalendarPartialityDialog(
				caption,
				this.selectedDates.getSelectedList(),
				this.employeeCalendarDraftObject.getContractStartDate(),
				this.employeeCalendarDraftObject.getContractEndDate()) {
			
			@Override
			protected void onAccept() {
				Date startDate = getStartDate();
				Date endDate = getEndDate();
				String coefficient = Double.toString(getPercentValue());
				addPartialityDayType(startDate, endDate, dayType, coefficient);
			}
		};
		
		partialityDialog.center();
		partialityDialog.show();
	}
	
	private void initHourDialog() {
		EmployeeCalendarHoursDialog hourDialog = new EmployeeCalendarHoursDialog(
				this.selectedDates.getSelectedList(),
				this.employeeCalendarDraftObject.getContractStartDate(),
				this.employeeCalendarDraftObject.getContractEndDate(),
				this.employeeCalendarDraftObject) {
			
			@Override
			protected void onAccept() {
				cleanSelectedDates();
				onChange();
				changeYear();
			}
		};
		
		hourDialog.center();
		hourDialog.show();
	}
	
	private void initHourExtraComplDialog() {
		EmployeeCalendarHoursExtraComplDialog hourDialog = new EmployeeCalendarHoursExtraComplDialog(
				this.selectedDates.getSelectedList(),
				this.employeeCalendarDraftObject.getContractStartDate(),
				this.employeeCalendarDraftObject.getContractEndDate(),
				this.employeeCalendarDraftObject) {
			
			@Override
			protected void onAccept() {
				cleanSelectedDates();
				onChange();
				changeYear();
			}
		};
		
		hourDialog.center();
		hourDialog.show();
	}
	
	// ----------------------------------------------------------------------------------------------------
	//									DAY TYPES DIALOG (AUX METHODS)
	// ----------------------------------------------------------------------------------------------------
	
	private String getCaptionByDayType(DayType dayType) {
		switch (dayType) {
		case NOWORKINGDAY:
			return "DIAS NO LABORABLES";
		case HOLIDAY:
			return "DIAS VACACIONES";
		case INACTIVITY:
			return "DIAS INACTIVIDAD";
		case REAL_DAYS:
			return "JORNADAS";
		case DROPDAY:
			return "DIAS AUSENCIA";
		case PARTIALITY:
			return "DIAS PARCIALIDAD";
		case WORKINGDAY:
			return "DIAS LABORABLES";
		case NOTYPEDAY:
			return "LIMPIAR DIAS";
		default:
			return "";
		}
	}

	private void addDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		employeeCalendarDraftObject.addDayType(startDate, endDate, dayType, expression);
		onChange();
		changeYear();
	}
	
	private void addPartialityDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		employeeCalendarDraftObject.addPartialityDayType(startDate, endDate, dayType, expression);
		onChange();
		changeYear();
	}
	
	// ----------------------------------------------------------------------------------------------------
	//										RESET CALENDAR METHODS
	// ----------------------------------------------------------------------------------------------------
	
	private void cleanCalendar() {
		for (int i = 1; i < totalRows; i++)
			for (int j = 1; j < totalCols; j++)
				calendarGrid.clearCell(i, j);
	}
	
	private void initCellsType() {
		for (int row = 0; row < totalRows; row++)
			for (int column = 0; column < totalCols; column++)
				cellsType[row][column] = new NoneCell();
	}
	
	private void initDayTypeCellsType() {
		for (int row = 0; row < totalRows; row++)
			for (int column = 0; column < totalCols; column++)
				cellsDayType[row][column] = new DayTypeCell(DayType.NOTYPEDAY);
	}
	
	// ----------------------------------------------------------------------------------------------------
	//									PAINT CALENDAR AUX METHODS
	// ----------------------------------------------------------------------------------------------------

	private int calculateNumberDayOfWeek(Date date) {
		DateUtils.resetTime(date);
		int numDay = date.getDay();

		// Tratamiento calendario español, 0 = Lunes, 6 = Domingo
		if (0 == numDay)
			numDay = 7;

		return numDay;
	}
	
	private int calculateLastDayOfMonth(Date date) {
		return DateUtils.getLastDayOfMonth(date).getDate();
	}
	
	private void unSelectSelectedDates() {
		for(Date date : this.selectedDates.getSelectedList()) {
			int position = calculateDatePosition(date);
			int row = calculatePositionRow(position);
			int col = calculatePositionCol(position);
			cellsType[row][col].unSelect(row, col);
		}
	}

	private Integer getDaysBetween(Date startDate, Date endDate) {
		return null == endDate ? 0 : (DateUtils.getDaysBetween(startDate, endDate) + 1);
	}
	
	
	private void cleanSelectedDates() {
		this.selectedDates.clear();
	}
	
	private boolean isMonthSelected(int row, int col) {
		return col==0 && isMonthAux(row);
	}
	
	private boolean isMonthAux(int row) {
		return row == 1 || row == 4 || row == 7 || row == 10 || row == 13 || row == 16 || row == 19 || row == 22 || row == 25 || 
				row == 28 || row == 31 || row == 34;
	}
	
	private int calculateDatePosition(Date date) {
		int row = calculateMonthRow(date.getMonth());
		int pos = -1;
		for(int col = 1; col < totalCols; col++){
			if(date.equals(cellsDates[row][col])){
				pos = (row * totalCols) + col;
				return pos;
			}
		}
		return pos;
	}
	
	private int calculateMonthRow(int month) {
		switch (month) {
		case 0:
			return 1;
		case 1:
			return 4;
		case 2:
			return 7;
		case 3:
			return 10;
		case 4:
			return 13;
		case 5:
			return 16;
		case 6:
			return 19;
		case 7:
			return 22;
		case 8:
			return 25;
		case 9:
			return 28;
		case 10:
			return 31;
		default:
			return 34;
		}
	}
	
	private int calculatePositionCol(int pos) {
		return pos % totalCols;
	}

	private int calculatePositionRow(int pos) {
		return pos / totalCols;
	}
	
	private void setTittleOfDayType(DayType dayType, Date currentDay, Label labelDay) {
		// Si es un dia sin tipo y es un dia con parcialidad
		if(dayType == DayType.PARTIALITY) {
			labelDay.setTitle("Parcialidad : " + this.employeeCalendarDraftObject.getPartialityCoefficientByDate(currentDay));
		}
		
		// Si es un dia de inactividad le ponemos la causa
		if(dayType == DayType.INACTIVITY) {
			labelDay.setTitle("Causa : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de huelga le ponemos el coeficiente
		if(dayType == DayType.STRIKEDAY) {
			labelDay.setTitle("Huelga, coeficiente : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de ausencia le ponemos el coeficiente
		if(dayType == DayType.DROPDAY) {
			labelDay.setTitle("Ausencia injustificada, coeficiente : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de ere le ponemos el coeficiente
		if(dayType == DayType.EREDAY) {
			labelDay.setTitle("ERE, coeficiente : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de ere fuerza mayor le ponemos el coeficiente
		if(dayType == DayType.EREFZADAY) {
			labelDay.setTitle("ERE FZA, coeficiente : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de ere fuerza mayor exoneracion de cuotas le ponemos el coeficiente
		if(dayType == DayType.EREFZAEXONDAY) {
			labelDay.setTitle("ERE FZA Exon, coeficiente : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de ere fuerza mayor exoneracion de cuotas le ponemos el coeficiente
		if(dayType == DayType.EREFZAEXONPARTIALDAY) {
			labelDay.setTitle("ERE FZA Exon Parcial, coeficiente : " + this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
		
		// Si es un dia de ere fuerza mayor exoneracion de cuotas le ponemos el coeficiente
		if(dayType == DayType.EREFZAEXONENDDAY) {
			labelDay.setTitle("ERE FZA Exon FIN");
		}
		
		// Si es un dia festivo le anniadimos la descripcion del festivo
		if(dayType == DayType.FREEDAY) {
			labelDay.setTitle(this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
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
	
	private void hideYearLBOptions() {
		ArrayList<Integer> idxsToDelete = new ArrayList<Integer>();
		for(int i=0; i<this.yearLB.getItemCount(); i++) {
			Integer year = Integer.parseInt(this.yearLB.getValue(i));
			Date lastDayOfYear = DateUtils.getLastDayOfYear(year-1900);
			if(isOutOfContractPeriod(lastDayOfYear)) {
				idxsToDelete.add(i);
			}
		}
		hideOptionYearLB(idxsToDelete);
	}
	
	private void hideOptionYearLB(ArrayList<Integer> idxsToDelete) {
		for(Integer idx : idxsToDelete)
			this.yearLB.removeItem(idx);
	}

	// ----------------------------------------------------------------------------------------------------
	//											CONTEXT MENU
	// ----------------------------------------------------------------------------------------------------
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		ContextMenu menu = new  ContextMenu();
		menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) laborables", new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.WORKINGDAY);
			}
		});
		
		menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) no laborables", new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.NOWORKINGDAY);
			}
		});
		menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) inactividad", new Command() {
			@Override
			public void execute() {
				initiInactivityDialog(DayType.INACTIVITY);
			}
		});
		menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) ausencia", new Command() {
			@Override
			public void execute() {
				initDropDialog(DayType.DROPDAY);
			}
		});
		menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) vacaciones", new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.HOLIDAY);
			}
		});
		menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) parcialidad", new Command() {
			@Override
			public void execute() {
				initPartialityDialog(DayType.PARTIALITY);
			}
		});
		if(this.employeeCalendarDraftObject.isAgrarianContract()) {
			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) peonadas", new Command() {
				@Override
				public void execute() {
					initDatesDialog(DayType.REAL_DAYS);
				}
			});
		}
		if(!this.employeeCalendarDraftObject.isFullTimeJourney()) {
			menu.addSeparator();
			menu.addItem("A"+String.valueOf("\u00f1") + " adir horas", new Command() {
				@Override
				public void execute() {
					initHourDialog();
				}
			});
		}
		menu.addSeparator();
		menu.addItem("Borrar evento(s)", new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.NOTYPEDAY);
			}
		});
		
		menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
	    menu.show();
	}
	
	// ----------------------------------------------- Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Calendario");
		
		undoAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> {
			onUndoAll(e);
		});
		toolbar.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> {
			onSave(e);
		});
		toolbar.add(saveButton);
		
		definitionButton = new AonToolbarButton( "Definicion", AON.CSS.aonIconEditCalendar() );
		definitionButton.addClickHandler(e -> {
			onDefinition(e);
		});
		toolbar.add(definitionButton);
		
		utilityButton = new AonToolbarButton( "Utilidades", AON.CSS.aonIconSettings() );
		utilityButton.addClickHandler(e -> {
			onUtility(e);
		});
		toolbar.add(utilityButton);
		
		Label yearL = new Label("Ejercicio :");
		yearL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		yearL.getElement().getStyle().setMarginRight(5, Unit.PX);
		toolbar.add(yearL);
		
		this.yearLB = new ListBox();
		toolbar.add(this.yearLB);
		
		return toolbar;

	}

	// ----------------------------------------------- Toolbar.Methods
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
	
	public void initializeYearLB(ListBox yearLB) {
		year = DateUtils.getYear();
		
		yearLB.clear();
		
		Integer yearAux = DateUtils.getYear();
		Integer previusYear = year - 1;
		Integer nextYear = year + 1;
		
		yearLB.addItem(nextYear.toString(), nextYear.toString());
		yearLB.addItem(yearAux.toString(), yearAux.toString());
		yearLB.addItem(previusYear.toString(), previusYear.toString());
		
		yearLB.addChangeHandler(e -> {
			changeYear();
		});
		
		setSelectedValueLB(yearLB, year+"");
		year = year - 1900;
		
	}

	public void onUndoAll(ClickEvent e) {
		initUndoAll();
	}
	
	public void onSave(ClickEvent e) {
		this.employeeCalendarDraftObject.saveCalendarInfo(
				s -> {
					onSaved();
					changeYear();
				},
				f -> {}
		);
	}
	
	public void onDefinition(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		definitionMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		definitionMenu.show();
	}
	
	public void onUtility(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		utilityMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		utilityMenu.show();
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		mainContainer.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
}
