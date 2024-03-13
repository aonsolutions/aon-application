package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayTypeVisitor;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public class EmployeeCalendarDraftNew extends Composite implements ContextMenuHandler {
	
	// -------------------------------------------- UiBinder

	private static EmployeeCalendarDraftNewUiBinder uiBinder = GWT.create(EmployeeCalendarDraftNewUiBinder.class);

	interface EmployeeCalendarDraftNewUiBinder extends UiBinder<Widget, EmployeeCalendarDraftNew> {}
	
	// -------------------------------------------- Calendar day type cell
	
	public static interface CalendarDayTypeCell{
		void setAsType(DayType daytype, int row, int col);
		void setStyle(int row, int col);
		DayType getType();
	}
	
	public class DayTypeCell implements CalendarDayTypeCell{
		private DayType dayType;
		
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
				
				// Efectivos
				@Override
				public Void visitEffectiveDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.effectiveStyle());	
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
					return visitEreFzaExon();
				}
				
				@Override
				public Void visitEreFzaExonPartialDay(DayType dayType) {
					return visitEreFzaExon();
				}
				
				@Override
				public Void visitEreFzaExonEndDay(DayType dayType) {
					return visitEreFzaExon();
				}
				
				private Void visitEreFzaExon() {
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
					return visitRealAndIfDay();
				}
				
				// Jornadas Teoricas
				@Override
				public Void visitIfDay(DayType dayType) {
					return visitRealAndIfDay();
				}
				
				private Void visitRealAndIfDay() {
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
	
	// -------------------------------------------- Calendar type cell
	
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
		public void setHour(int row, int col, String newHour) {
			// DayCell not implements this method
		}
		
		@Override
		public String getHour(int row, int col) {
			return "0";
		}
		
		@Override
		public void setOnChange(int row, int col) {
			// DayCell not implements this method
		}
		
		@Override
		public void eraseOnChange(int row, int col) {
			// DayCell not implements this method
		}
	}
	
	public class HourCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {
			// HourCell not implements this method
		}
		
		@Override
		public void unSelect(int row, int col) {
			// HourCell not implements this method
		}
		
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
		public void select(int row, int col) {
			// HourComplementaryCell not implements this method
		}
		
		@Override
		public void unSelect(int row, int col) {
			// HourComplementaryCell not implements this method
		}
		
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
		
		public static final NoneCell noneCell = new NoneCell(); 
		
		private NoneCell() {}
		
		@Override
		public void select(int row, int col) {
			// NoneCell not implements this method
		}
		@Override
		public void unSelect(int row, int col) {
			// NoneCell not implements this method
		}
		@Override
		public void setHour(int row, int col, String newHour) {
			// NoneCell not implements this method
		}
		@Override
		public String getHour(int row, int col) {
			return "0";
		}
		@Override
		public void setOnChange(int row, int col) {
			// NoneCell not implements this method
		}
		@Override
		public void eraseOnChange(int row, int col) {
			// NoneCell not implements this method
		}
	}
	
	// -------------------------------------------- ScheduledCommand (DefinitionMenu)
	
	class WorkingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			new EmployeeCalendarNonWorkingDialog(
					employeeCalendarDraftObject.getWorkingDays()) {
				
				@Override
				protected void onAccept() {
					employeeCalendarDraftObject.setWorkingDays(getNonWorkingDays());
					onChange();
					changeYear();
				}
			};
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
	
	class ShowHourExtraCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showHoursExtraCompl = !showHoursExtraCompl;
			
			definitionMenu.getShowHourExtraMenuItem().setStyleName("aon-MenuItemCheckYes", showHoursExtraCompl);
			definitionMenu.getShowHourExtraMenuItem().addStyleName(style.aonCheck());
			
			if (showHoursExtraCompl) {
				showHoursExtraComplRows();	
			} else {
				hideHoursExtraComplRows();
			}
		}
	}
	
	class ShowHourComplCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showHoursExtraCompl = !showHoursExtraCompl;
			
			definitionMenu.getShowHourComplMenuItem().setStyleName("aon-MenuItemCheckYes", showHoursExtraCompl);
			definitionMenu.getShowHourComplMenuItem().addStyleName(style.aonCheck());
			
			if (showHoursExtraCompl) {
				showHoursExtraComplRows();	
			} else {
				hideHoursExtraComplRows();
			}
		}
	}
	
	class DefinitionMenu extends ContextMenu {
				
		private MenuItem workingMenuItem = null;
		private MenuItem hourMenuItem = null;
		private MenuItemSeparator separator;
		private MenuItem showHourMenuItem = null;
		private MenuItem showHourExtraMenuItem = null;
		private MenuItem showHourComplMenuItem = null;
		
		public DefinitionMenu() {
			
			workingMenuItem = addItem("Definir semana laboral", new WorkingCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			workingMenuItem.ensureDebugId("workingMenuItem");
			
			hourMenuItem = addItem("Definir horas semanales", new HourCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			hourMenuItem.ensureDebugId("hourMenuItem");
			
			separator = addSeparator();
			
			showHourMenuItem = addItem("Ocultar horas", new ShowHourCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			showHourMenuItem.ensureDebugId("showHourMenuItem");
			
			showHourExtraMenuItem = addItem("Mostrar horas extras", new ShowHourExtraCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			showHourExtraMenuItem.ensureDebugId("showHourExtraMenuItem");
			
			showHourComplMenuItem = addItem("Mostrar horas complementarias", new ShowHourComplCommand(), 
					AON.CSS.aonIconEditCalendar(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			showHourComplMenuItem.ensureDebugId("showHourComplMenuItem");
		}

		public MenuItem getWorkingMenuItem() {
			return workingMenuItem;
		}

		public MenuItem getHourMenuItem() {
			return hourMenuItem;
		}

		public MenuItem getShowHourMenuItem() {
			return showHourMenuItem;
		}
		
		public MenuItem getShowHourExtraMenuItem() {
			return showHourExtraMenuItem;
		}
		
		public MenuItem getShowHourComplMenuItem() {
			return showHourComplMenuItem;
		}
		
		public void hideSeparator() {
			separator.setVisible(false);
		}
		
		public void showSeparator() {
			separator.setVisible(true);
		}
		
	}
	
	// -------------------------------------------- ScheduledCommand (UtilityMenu)
	
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
			AonDialog dialog = new AonDialog("RESETEAR", new HTML("\u00BFRESETEAR CALENDARIO con los valores INICIALES? Se BORRARAN todos los cambios realizados."));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Hides AonDialog
				}
				
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
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			eraseEventMenuItem.ensureDebugId("eraseEventMenuItem");
			
			undoAllMenuItem = addItem("Restaurar \u00FAltimos valores guardados", new UndoAllCommand(), 
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			undoAllMenuItem.ensureDebugId("undoAllMenuItem");
			
			addSeparator();
			
			resetMenuItem = addItem("Resetear calendario", new ResetMenuCommand(), 
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
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
	
	// -------------------------------------------- UiField
	
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
		String effectiveStyle();
		// Out of contract
		String outOfContractStyle();
		String cmdBtn();
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	VerticalPanel mainContainer;
	
	@UiField
	HTMLPanel daysTypePanel;
	
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
	Button effectiveDayButton;
	
	@UiField
	Button inactivityDayButton;
	
	@UiField
	Button dropDayButton;
	
	@UiField
	Button holidayDayButton;
	
	@UiField
	Button leaveDayButton;
	
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
	
	// -------------------------------------------- Variable
	
	private int totalCols = 38;
	private int totalRows = 37;
	
	private DateTimeFormat fullDateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	private DateTimeFormat monthFormat = DateTimeFormat.getFormat("MM");
	private DateTimeFormat dayOfWeekFormat = DateTimeFormat.getFormat("c");
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	
	private OrderedMultiSelectionModel<Date> selectedDates = new OrderedMultiSelectionModel<>();
	
	private final CalendarTypeCell[][] cellsType = new CalendarTypeCell[totalRows][totalCols];
	private final CalendarDayTypeCell[][] cellsDayType = new CalendarDayTypeCell[totalRows][totalCols];
	private final Date[][] cellsDates = new Date[totalRows][totalCols];
	
	private boolean showHours;
	private boolean showHoursExtraCompl;
	private int month;
	private int year = DateUtils.getYear() - 1900;
	
	private DefinitionMenu definitionMenu;
	private UtilityMenu utilityMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton saveButton;
	private ListBox yearLB;
	
	// -------------------------------------------- Constructor
	
	public EmployeeCalendarDraftNew() {
		getToolbarPanel();
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		scrollInfo.setHeight((Window.getClientHeight() - 260) + "px");
		
		definitionMenu = new DefinitionMenu();
		utilityMenu = new UtilityMenu();
		
		// Add contextMenu
		calendarGrid.addDomHandler(this, ContextMenuEvent.getType());
	}
	
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title );
	}
	
	public void setContrataEmployeeCalendarHeight(){
		scrollInfo.setHeight((Window.getClientHeight() - 290) + "px");
	}

	// -------------------------------------------- setEmployeeCalendarObject
	
	public void setEmployeeCalendarDraftObject(EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		this.employeeCalendarDraftObject.initCalendarInfo(s -> {
			// Init save and undo all
			onSaved();
			initializeYearLB(this.yearLB, this.employeeCalendarDraftObject.getContractStartDate());
			hideYearLBOptions();
			setSelectedValueLB(yearLB, (year+1900)+"");
			
			if(this.employeeCalendarDraftObject.isFullTime()) {
				this.showHours = false;
				this.showHoursExtraCompl = false;
			} else {
				this.showHours = true;
				this.showHoursExtraCompl = false;
			}
			
			changeYear();
		}, f -> {});
		
	}
	
	// -------------------------------------------- UiField
	
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		
		event.preventDefault();
		
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * totalCols) + col;
		
		if(cellsType[row][col] == null) {
			unSelectSelectedDates();
			cleanSelectedDates();
			return;
		}
		
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
					
					DayType dayType = employeeCalendarDraftObject.getDayTypeByDate(cellsDates[rowIdx][colIdx]);
					if(null != dayType && dayType != DayType.BAJAIT)
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
					if(null != cellsType[row][i] && null != cellsDates[row][i]) {
						DayType dayType = employeeCalendarDraftObject.getDayTypeByDate(cellsDates[row][i]);
						if(null != dayType && dayType != DayType.BAJAIT)
							cellsType[row][i].select(row, i);
					}
				}
			}else {
				DayType dayType = employeeCalendarDraftObject.getDayTypeByDate(cellsDates[row][col]);
				if(null != dayType && dayType != DayType.BAJAIT)
					cellsType[row][col].select(row, col);
				else
					openITDialogDate(cellsDates[row][col]);
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
	
	@UiHandler("effectiveDayButton")
	public void onEffeDayButctivetonClick(ClickEvent event) {
		initDatesDialog(DayType.EFFECTIVE);
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
	
	@UiHandler("leaveDayButton")
	public void onLeaveDayButtonClick(ClickEvent event) {
		openITDialog();
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
		new EmployeeCalendarLeyendDialog() {

			@Override
			protected Integer getTotalYearDays(DayType realDays) {
				Integer year = Integer.parseInt(yearLB.getSelectedValue());
				return employeeCalendarDraftObject.getTotalYearDays(year, realDays);
			}
		};
	}

	// -------------------------------------------- Init calendar

	private void initCalendar() {
		// Set fulltime journey
		if(this.employeeCalendarDraftObject.isFullTime()) {
			if(showHours) showHoursRows(); else hideHoursRows();
			if(showHoursExtraCompl) showHoursExtraComplRows(); else hideHoursExtraComplRows();
			
			hideElement(definitionMenu.getShowHourMenuItem().getElement());
			hideElement(definitionMenu.getShowHourComplMenuItem().getElement());
			showElement(definitionMenu.getShowHourExtraMenuItem().getElement());
			hideElement(definitionMenu.getHourMenuItem().getElement());
			hideElement(hourButton.getElement());
			definitionMenu.showSeparator();
			
			extraHoursButton.setText("H. Extras");
			definitionMenu.getWorkingMenuItem().getElement().getStyle().clearDisplay();
			definitionMenu.getHourMenuItem().getElement().getStyle().setDisplay(Display.NONE);
			
		} else {
			if(showHours) showHoursRows(); else hideHoursRows();
			if(showHoursExtraCompl) showHoursExtraComplRows(); else hideHoursExtraComplRows();
			
			showElement(definitionMenu.getShowHourMenuItem().getElement());
			showElement(definitionMenu.getShowHourComplMenuItem().getElement());
			hideElement(definitionMenu.getShowHourExtraMenuItem().getElement());
			showElement(definitionMenu.getHourMenuItem().getElement());
			showElement(hourButton.getElement());
			definitionMenu.showSeparator();
			
			extraHoursButton.setText("H. Compl.");
			extraHoursButton.setTitle("Horas Complementarias");
			definitionMenu.getWorkingMenuItem().getElement().getStyle().setDisplay(Display.NONE);
			definitionMenu.getHourMenuItem().getElement().getStyle().clearDisplay();
		}
		
		// Clear calendar
		cleanCalendar();
		initCellsType();
		initDayTypeCellsType();
		cleanSelectedDates();
		this.month = 0;
		
		// Set agrarian contract
		if(this.employeeCalendarDraftObject.isAgrarian()) {
			showElement(peonadasDayButton.getElement());
		} else {
			hideElement(peonadasDayButton.getElement());
		}
		
		//Crear calendario
		for (int row = 1; row < totalRows; row += 3)
			paintCalendar(row);		
	}

	private void paintCalendar(int row) {
		// Mostrar d�as del mes
		Integer contDays = 1;
		Date date = fullDateFormat.parse(1 + "/" + (month + 1) + "/" + (year + 1900));
		int firstDayOfMonth = calculateNumberDayOfWeek(date);
		int lastDayOfMonth = calculateLastDayOfMonth(date);
		
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
				Date currentDay = fullDateFormat.parse(contDays + "/" + (month + 1) + "/" + (year + 1900));
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
					
					// Si es un dia sin tipo y segun el calendario es no laborable
					if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isDefaultNonWorkongDay(currentDay)) {
						dayType = DayType.NOWORKINGDAY;
					}
					
					// Poner el titulo al label en funcion del tipo de dia
					setTittleOfDayType(dayType, currentDay, labelDay);
					
					cellsDayType[row][i].setAsType(dayType, row, i);
					
					// ------------ Set Hour Day
					
					// Add hour info is not is empty CalendarHours
					if(!this.employeeCalendarDraftObject.isCalendarHourIsEmpty()) {
						Double dayHour = this.employeeCalendarDraftObject.getHourByDate(currentDay);
						
						if(null != dayHour) {
							dayHour = Math.round(dayHour * 10) / 10.0;
							hourDay.setText(dayHour.toString());
						} else
							cellsDayType[row][i].setAsType(DayType.NOWORKINGDAY, row, i);
					}
					
					// ------------ Set Hour Extra / Complementary Hour
					
					// Add hour info is not is empty CalendarHours
					if(!this.employeeCalendarDraftObject.isExtraHourIsEmpty() || !this.employeeCalendarDraftObject.isComplementaryHourIsEmpty()) {
						Double dayHourExtraCompl = this.employeeCalendarDraftObject.isFullTime() ? 
								this.employeeCalendarDraftObject.getExtraHourByDate(currentDay) :
								this.employeeCalendarDraftObject.getComplementaryHourByDate(currentDay);
								
						if(null != dayHourExtraCompl) hourExtraComplDay.setText(dayHourExtraCompl.toString());
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
			Date currentDay = fullDateFormat.parse(contDays + "/" + (month + 1) + "/" + (year + 1900));
			DateUtils.resetTime(currentDay);
			
			// Por que ya llevamos 7 dias pintados
			column = 7 + actualDayOfWeek;
			
			// Set Label day and Label hours
			Label labelDay = new Label(contDays.toString());
			labelDay.addStyleName(style.cursorPointer());
			labelDay.addStyleName(style.labelStyle());
			calendarGrid.setWidget(row, column, labelDay);
			
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
				
				// Si es un dia sin tipo y segun el calendario es no laborable
				if(dayType == DayType.NOTYPEDAY && this.employeeCalendarDraftObject.isDefaultNonWorkongDay(currentDay)) {
					dayType = DayType.NOWORKINGDAY;
				}
				
				// Poner el titulo al label en funcion del tipo de dia
				setTittleOfDayType(dayType, currentDay, labelDay);
				
				cellsDayType[row][column].setAsType(dayType, row, column);
				
				// ------------ Set Hour Day
				
				// Add hour info is not is empty CalendarHours
				if(!this.employeeCalendarDraftObject.isCalendarHourIsEmpty()) {
					Double dayHour = this.employeeCalendarDraftObject.getHourByDate(currentDay);
					
					if(null != dayHour) {
						dayHour = Math.round(dayHour * 10) / 10.0;
						hourDay.setText(dayHour.toString());
					} else
						cellsDayType[row][column].setAsType(DayType.NOWORKINGDAY, row, column);
				}
				
				// ------------ Set Hour Complementary Day
				
				// Add hour info is not is empty CalendarHours
				if(!this.employeeCalendarDraftObject.isExtraHourIsEmpty() || !this.employeeCalendarDraftObject.isComplementaryHourIsEmpty()) {
					Double dayHourExtraCompl = this.employeeCalendarDraftObject.isFullTime() ? 
							this.employeeCalendarDraftObject.getExtraHourByDate(currentDay) :
							this.employeeCalendarDraftObject.getComplementaryHourByDate(currentDay);
							
					if(null != dayHourExtraCompl) hourExtraComplDay.setText(dayHourExtraCompl.toString());
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

	// -------------------------------------------- Show/hide methods

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
	
	// -------------------------------------------- isOutOfContratPeriod
	
	private boolean isOutOfContractPeriod(Date date) {
		Date contractEndDate = this.employeeCalendarDraftObject.getContractEndDate();
		Date newEndDate = null;
		if(null == contractEndDate) {
			Integer nextYear = Integer.parseInt(yearFormat.format(new Date())) + 2;
			newEndDate = fullDateFormat.parse("31/12/"+nextYear);
		} else {
			newEndDate = DateUtils.copyDateOnly(contractEndDate);
		}
		
		Date startDate = DateUtils.copyDateOnly(this.employeeCalendarDraftObject.getContractStartDate());
		
		return date.before(startDate) || date.after(newEndDate);
	}
	
	private boolean isOutOfContractPeriodIncludeCurrentYear(Date date) {
		Date contractEndDate = this.employeeCalendarDraftObject.getContractEndDate();
		Date newEndDate = null;
		if(null == contractEndDate) {
			Integer nextYear = Integer.parseInt(yearFormat.format(new Date())) + 2;
			newEndDate = fullDateFormat.parse("31/12/"+nextYear);
		} else {
			newEndDate = DateUtils.copyDateOnly(contractEndDate);
			newEndDate = DateUtils.getLastDayOfYear(newEndDate);
		}
		
		Date startDate = DateUtils.copyDateOnly(this.employeeCalendarDraftObject.getContractStartDate());
		
		return date.before(startDate) || date.after(newEndDate);
	}
	
	// -------------------------------------------- onSaved/onChange
	
	private void onSaved(){
		saveButton.setEnabled(false);
		undoAllButton.setEnabled(false);
	}
	
	private void onChange(){
		saveButton.setEnabled(true);
		undoAllButton.setEnabled(true);
	}
	
	private void initUndoAll() {
		AonDialog dialog = new AonDialog("RESTAURAR", new HTML("\u00BFRESTAURAR CALENDARIO con los valores de la \u00FAltima versi\u00F3n guardada?"));
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Hide AonDialog
			}
			
			@Override
			public void onAccept() {
				employeeCalendarDraftObject.initCalendarInfo(s -> {
					onSaved();
					initCalendar();
				}, f -> {});
			}
		});
	}
	
	// -------------------------------------------- ChangeYear
	
	private void changeYear() {
		String fullYear = this.yearLB.getSelectedValue();
		this.year = Integer.parseInt(fullYear) - 1900;
		initCalendar();
	}
	
	// -------------------------------------------- InitDates dialog

	private void initDatesDialog(DayType dayType) {
		// Si no hay nada seleccionado en el calendario mostramos dialogo
		if(this.selectedDates.getSelectedList().isEmpty()) {
			String caption = getCaptionByDayType(dayType);
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
							if(Boolean.TRUE.equals(isEndERTE) && null != endDate) {
								Date newDate = DateUtils.copyDateOnly(endDate);
								DateUtils.addDays2Date(newDate, 1);
								employeeCalendarDraftObject.addDayType(newDate, newDate, DayType.EREFZAEXONENDDAY, "1");
							}
							
							addDayType(startDate, endDate, DayType.EREFZAEXONDAY, coefficient);
							break;
						default:
							break;
					}
				}
				
			}
		};
	}
	
	private void initiAgrarianDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
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
	}

	private void initDropDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
		new EmployeeCalendarPercentDialog(
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
	}
	
	private void initPartialityDialog(DayType dayType) {
		String caption = getCaptionByDayType(dayType);
		new EmployeeCalendarPartialityDialog(
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
	}
	
	private void openITDialog() {
		employeeCalendarDraftObject.getEmployeeITInfo(
				s -> {
					ITEmployee itEmployee = employeeCalendarDraftObject.getITEmployee();
			    	ITDialog itDialog = newITDialog();
			    	itDialog.setITEmployee(itEmployee);
				}, f -> {});
	}
	
	private ITDialog newITDialog() {
		ITDialog itDialog = new ITDialog("Creaci\u00F3n") {

    		@Override
			protected void onAccept() {
    			accept(getITEmployee());
			}
    		
    		@Override
			protected void onDelete(IT it) {
    			deleteLeave(getITEmployee(), it);
			}
			
			@Override
			protected void onShowCertitificateIT(IT it) {
				getITCertificatePDF(getITEmployee(), it);
			}

			@Override
			protected void onCommunicateITPart(IT it, ITPart itPart) {}

			@Override
			protected void onRemoveITPartTGSS(ItNotExist ItNotExist) {}
			
			@Override
			protected void onDownloadFDIITPart(IT it, ITPart itPart) {}
    		
    	};
    	
    	itDialog.setEmployeesList(employeeCalendarDraftObject.getActiveEmployeesList());
    	itDialog.initConfirmationsTable();
		itDialog.setModal(true);
    	itDialog.setAnimationEnabled(true);
		itDialog.center();
		itDialog.show();
		
		return itDialog;
	}
	
	private void openITDialogDate(Date itDate) {
		employeeCalendarDraftObject.getEmployeeITInfo(
				s -> openITDialog(itDate), 
				f -> {});
	}
	
	private void openITDialog(Date itDate) {
		IT itInfo = employeeCalendarDraftObject.getITByDate(itDate);
    	ITEmployee itEmployee = employeeCalendarDraftObject.getITEmployee();

    	ITDialog itDialog = new ITDialog("Edici\u00F3n") {
    		
    		@Override
			protected void onAccept() {
				accept(itEmployee);
			}

			@Override
			protected void onDelete(IT it) {
				deleteLeave(itEmployee, it);
			}

			@Override
			protected void onShowCertitificateIT(IT it) {
				getITCertificatePDF(itEmployee, it);
			}

	        @Override
			protected void onCommunicateITPart(IT it, ITPart part) {
				AonDialog dialog = new AonDialog("Info", new HTML("Comunicarlo desde en el apartado Laboral > Partes IT"));
				dialog.info();
			}

			@Override
			protected void onRemoveITPartTGSS(ItNotExist ItNotExist) {
				AonDialog dialog = new AonDialog("Info", new HTML("Comunicarlo desde en el apartado Laboral > Partes IT"));
				dialog.info();
			}

			@Override
			protected void onDownloadFDIITPart(IT it, ITPart itPart) {
				AonDialog dialog = new AonDialog("Info", new HTML("Descargar fichero FIE desde en el apartado Laboral > Partes IT"));
				dialog.info();
			}
			
    	};
    	
    	ITDialogObject itDialogObject = new ITDialogObject(itEmployee);
    	itDialog.setIsUserComunica(employeeCalendarDraftObject.isUserComunica());
    	itDialog.setITDialogObject(itDialogObject, itInfo, true);
	}	
	
	private void initHourDialog() {
		new EmployeeCalendarHoursDialog(
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
	}
	
	private void initHourExtraComplDialog() {
		if(this.employeeCalendarDraftObject.isFullTime()) {
			new CalendarExtraHourDialog(
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
		} else {
			new CalendarComplementaryHourDialog(
					this.selectedDates.getSelectedList(),
					this.employeeCalendarDraftObject.getContractStartDate(),
					this.employeeCalendarDraftObject.getContractEndDate(),
					this.year + 1900,
					this.employeeCalendarDraftObject) {
				
				@Override
				protected void onAccept() {
					cleanSelectedDates();
					onChange();
					changeYear();
				}
			};
		}
	}
	
	// -------------------------------------------- IT methods

	private void accept(ITEmployee itEmployee) {
		employeeCalendarDraftObject.createUpdateITEmployee(itEmployee,
				message -> reloadCalendar(),
				f -> {});
	}
	
	private void deleteLeave(ITEmployee itEmployee, IT it) {
		if(ITDialog.isPartenityPart(it)) {
			employeeCalendarDraftObject.deleteIT(it,
					s -> {
						if(Boolean.TRUE.equals(it.isComunicate()) && employeeCalendarDraftObject.isUserComunica())
							employeeCalendarDraftObject.deleteComunicateIT(itEmployee, it, 
									t -> reloadCalendar(),
									d -> {});
						else
							reloadCalendar();
					},
					f -> {});
		} else {
			employeeCalendarDraftObject.removeIT(itEmployee, it,
					s -> {
						if(Boolean.TRUE.equals(it.isComunicate()) && employeeCalendarDraftObject.isUserComunica())
							employeeCalendarDraftObject.deleteComunicateIT(itEmployee, it, 
									t -> reloadCalendar(),
									d -> {});
						else
							reloadCalendar();
					},
					f -> {});
		}
	}
	
	private void comunicateIT(ITEmployee itEmployee, IT it) {
		employeeCalendarDraftObject.comunicateITBaja(itEmployee, it, t -> {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
		}, d -> {});
	}

	private void comunicatePaternityIT(ITEmployee itEmployee, IT it) {
		employeeCalendarDraftObject.comunicatePaternityIT(itEmployee, it, t -> {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
		}, d -> {});
	}
	
	private void getITCertificatePDF(ITEmployee itEmployee, IT it) {
		employeeCalendarDraftObject.getNafxIpf(itEmployee, s -> {
			String affiliationNumber = s.getNss();
			String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
			String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
			String dateFromStr = fullDateFormat.format(new Date());
			String dateToStr = fullDateFormat.format(new Date());
			String startDateStr = fullDateFormat.format(it.getStartDate());
			
			String fileDownloadURL = GWT.getModuleBaseURL()+ "it_export/";
			String query = "?domainName=" + Wnd.getCurrentDomainNameURL()
			 		+ "&userLogin=" + Wnd.getCurrentUser()
		            + "&affiliationNumber=" + affiliationNumber
		            + "&regime=" + regime
		            + "&contributionAccount=" + contributionAccount
		            + "&dateFromStr=" + dateFromStr
					+ "&dateToStr=" + dateToStr
					+ "&startDateStr=" + startDateStr
					+ "&itType=" + it.getTypeLowPart();
			
			Window.open(fileDownloadURL+query, "ITExporter", "resizable=yes,scrollbars=yes,status=yes");
		}, f -> {});
	}
	
	private void reloadCalendar() {
		employeeCalendarDraftObject.initCalendarInfo(t -> {
			// Init save and undo all
			onSaved();
			changeYear();
		}, f -> {});
	}
	
	// -------------------------------------------- DayTypes dialog auxiliar methods
	
	private String getCaptionByDayType(DayType dayType) {
		switch (dayType) {
		case NOWORKINGDAY:
			return "DIAS NO LABORABLES";
		case HOLIDAY:
			return "DIAS VACACIONES";
		case EFFECTIVE:
			return "DIAS EFECTIVOS";
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
	
	// -------------------------------------------- Reset calendar
	
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
	
	// -------------------------------------------- Calendar view auxiliar methods

	private int calculateNumberDayOfWeek(Date date) {
		DateUtils.resetTime(date);
		try {
			int numDay = Integer.parseInt(dayOfWeekFormat.format(date));
			
			// Tratamiento calendario espa�ol, 0 = Lunes, 6 = Domingo
			if (0 == numDay)
				numDay = 7;

			return numDay;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	@SuppressWarnings("deprecation")
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
		int pos = -1;
		try {
			int row = calculateMonthRow(Integer.parseInt(monthFormat.format(date))-1);
			for(int col = 1; col < totalCols; col++)
				if(date.equals(cellsDates[row][col])) {
					pos = (row * totalCols) + col;
					return pos;
				}
			
			return pos;
		} catch (NumberFormatException e) {
			return pos;
		}
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
		// Si es un dia de baja IT
		if(dayType == DayType.BAJAIT) {
			labelDay.setTitle("Baja IT");
		}
		
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
	
	// -------------------------------------------- Auxiliar methods
	
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
		ArrayList<Integer> idxsToDelete = new ArrayList<>();
		for(int i=0; i<this.yearLB.getItemCount(); i++) {
			Integer yearAux = Integer.parseInt(this.yearLB.getValue(i));
			Date lastDayOfYear = DateUtils.getLastDayOfYear(yearAux-1900);
			if(isOutOfContractPeriodIncludeCurrentYear(lastDayOfYear)) {
				idxsToDelete.add(i);
			}
		}
		hideOptionYearLB(idxsToDelete);
	}
	
	private void hideOptionYearLB(ArrayList<Integer> idxsToDelete) {
		Collections.sort(idxsToDelete, Collections.reverseOrder());
		for(Integer idx : idxsToDelete)
			try {
				this.yearLB.removeItem(idx);
			} catch (Exception e) {
				// Catch exception
			}
	}

	// -------------------------------------------- ContextMenu
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		ContextMenu menu = new  ContextMenu();
		menu.addItem("A\u00f1adir dia(s) laborables", () -> initDatesDialog(DayType.WORKINGDAY));
		menu.addItem("A\u00f1adir dia(s) no laborables", () -> initDatesDialog(DayType.NOWORKINGDAY));
		menu.addItem("A\u00f1adir dia(s) efectivos", () -> initDatesDialog(DayType.EFFECTIVE));
		menu.addItem("A\u00f1adir dia(s) inactividad", () -> initDatesDialog(DayType.INACTIVITY));
		menu.addItem("A\u00f1adir dia(s) ausencia", () -> initDatesDialog(DayType.DROPDAY));
		menu.addItem("A\u00f1adir dia(s) vacaciones", () -> initDatesDialog(DayType.HOLIDAY));
		menu.addItem("A\u00f1adir dia(s) parcialidad", () -> initDatesDialog(DayType.PARTIALITY));
		if(this.employeeCalendarDraftObject.isAgrarian())
			menu.addItem("A\u00f1adir dia(s) peonadas", () -> initDatesDialog(DayType.REAL_DAYS));
		if(!this.employeeCalendarDraftObject.isFullTime()) {
			menu.addSeparator();
			menu.addItem("A\u00f1adir horas", () -> initHourDialog());
		}
		menu.addSeparator();
		menu.addItem("Borrar evento(s)", () -> initDatesDialog(DayType.NOTYPEDAY));
		
		menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
	    menu.show();
	}
	
	// -------------------------------------------- Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		this.toolbar = new AonToolbar("Calendario");
		
		undoAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> onUndoAll());
		toolbar.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> onSave());
		toolbar.add(saveButton);
		
		AonToolbarButton definitionButton = new AonToolbarButton( "Definicion", AON.CSS.aonIconEditCalendar() );
		definitionButton.addClickHandler(e -> onDefinition(e));
		toolbar.add(definitionButton);
		
		AonToolbarButton utilityButton = new AonToolbarButton( "Utilidades", AON.CSS.aonIconSettings() );
		utilityButton.addClickHandler(e -> onUtility(e));
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
	
	public void initializeYearLB(ListBox yearLB, Date contractStartDate) {
		Integer iteratorYear = DateUtils.getYear() + 1;
		Integer contractStartYear = DateUtils.getYear(contractStartDate);
		
		yearLB.clear();
		
		while (iteratorYear >= contractStartYear) {
			yearLB.addItem(iteratorYear.toString(), iteratorYear.toString());
			iteratorYear--;
		}
		
		yearLB.addChangeHandler(e -> changeYear());
		
		setSelectedValueLB(yearLB, DateUtils.getYear()+"");
		
		year = DateUtils.getYear();
		year = year - 1900;
		
	}

	public void onUndoAll() {
		initUndoAll();
	}
	
	public void onSave() {
		this.employeeCalendarDraftObject.saveCalendarInfo(
				s ->
					this.employeeCalendarDraftObject.initCalendarInfo(t -> {
						// Init save and undo all
						onSaved();
						changeYear();
					}, f -> {}),
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
		daysTypePanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
}
