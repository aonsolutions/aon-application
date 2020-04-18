package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayTypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
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
				
				// IT
				@Override
				public Void visitITDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dayTypeButton());
					calendarGrid.getWidget(row, col).addStyleName(style.itStyle());
					return null;
				}
				
				// Peonadas
				@Override
				public Void visitPeonadasDay(DayType dayType) {
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
	}
	
	// ---------------------------- MenuItem (UiField)
	
	@UiField
	MenuItem workingDayMenuItem;
	
	@UiField
	MenuItem nonWorkingDayMenuItem;
	
	@UiField
	MenuItem inactivityDayMenuItem;
	
	@UiField
	MenuItem dropDayMenuItem;
	
	@UiField
	MenuItem holidayDayMenuItem;
	
	@UiField
	MenuItem partialityDayMenuItem;
	
	@UiField
	MenuItem peonadasDayMenuItem;
	
	@UiField
	MenuItem eraseEventMenuItem;
	
	@UiField
	MenuItem hourMenuItem;
	
	@UiField
	MenuItem viewMenuItem;
	
	@UiField
	MenuItem showHourMenuItem;
	
	@UiField
	MenuItem settingMenuItem;
	
	@UiField
	MenuItem nonWorkingMenuItem;
	
	// ---------------------------- Save / Reset
	
	Button resetButton;
	
	Button saveButton;
	
	// ---------------------------- Change Year (UiField)
	
	@UiField
	Button lastYearButton;
	
	@UiField
	Button nextYearButton;
	
	@UiField
	Label yearLabel;
	
	// ---------------------------- Calendar (UiField)
	
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
	private int totalRows = 25;
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	
	private OrderedMultiSelectionModel<Date> selectedDates = new OrderedMultiSelectionModel<Date>();
	
	private final CalendarTypeCell cellsType[][] = new CalendarTypeCell[totalRows][totalCols];
	private final CalendarDayTypeCell cellsDayType[][] = new CalendarDayTypeCell[totalRows][totalCols];
	private final Date cellsDates[][] = new Date[totalRows][totalCols];
	
	private boolean showHours;
	private int month;
	private int year = new Date().getYear();
	
	public EmployeeCalendarDraftNew() {
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		// Add contextMenu
		calendarGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		// Inicializamos menu item
		initMenuItem();
	}

	// ----------------------------------------------------------------------------------------------------
	//									SET EMPLOYEE CALENDAR DRAFT
	// ----------------------------------------------------------------------------------------------------
	
	public void setEmployeeCalendarDraftObject(EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		this.employeeCalendarDraftObject.initCalendarInfo(s -> {
			initCalendar();
		}, f -> {});
		
	}
	
	// ----------------------------------------------------------------------------------------------------
	//											MENU ITEM
	// ----------------------------------------------------------------------------------------------------
	
	private void initMenuItem() {
		// Init DayTypes MenuItem
		workingDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.WORKINGDAY);
			}
		});
		
		nonWorkingDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.NOWORKINGDAY);
			}
		});
		
		inactivityDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initiInactivityDialog(DayType.INACTIVITY);
			}
		});
		
		dropDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initDropDialog(DayType.DROPDAY);
			}
		});
		
		holidayDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.HOLIDAY);
			}
		});
		
		partialityDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initPartialityDialog(DayType.PARTIALITY);
			}
		});
		
		peonadasDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.PEONADAS);
			}
		});
		
		eraseEventMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initDatesDialog(DayType.NOTYPEDAY);
			}
		});
		
		hourMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				initHourDialog();
			}
		});
		
		// Show hours menuItem
		showHourMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				showHourMenuItem.setStyleName("aon-MenuItemCheckYes", showHours);
				showHourMenuItem.addStyleName(style.aonCheck());
				
				showHours = !showHours;
				
				if (showHours) {
					showHoursRows();	
				} else {
					hideHoursRows();
				}
			}
		});
		
		// Set working and nonworking days
		nonWorkingMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				EmployeeCalendarNonWorkingDialog nonWorkingDialog = new EmployeeCalendarNonWorkingDialog(
						employeeCalendarDraftObject.getNonWorkingDays()) {
					
					@Override
					protected void onAccept() {
						employeeCalendarDraftObject.setNonWorkingDays(getNonWorkingDays());
						changeYear(0);
					}
				};
				
				nonWorkingDialog.center();
				nonWorkingDialog.show();
			}
		});
	}
	
	// ----------------------------------------------------------------------------------------------------
	//											UI HANDLERS
	// ----------------------------------------------------------------------------------------------------
	
	@UiHandler("resetButton")
	public void onResetButtonClick(ClickEvent event) {
		this.employeeCalendarDraftObject.resetCalendarInfo(
				s -> {
					changeYear(0);
				}, f -> {}
		);
	}
	
	@UiHandler("saveButton")
	public void onSaveButtonClick(ClickEvent event) {
		this.employeeCalendarDraftObject.saveCalendarInfo(
				s -> {
					changeYear(0);
				},
				f -> {}
		);
	}
	
	@UiHandler("lastYearButton")
	public void onLastYearButtonClick(ClickEvent event) {
		changeYear(-1);
	}

	@UiHandler("nextYearButton")
	public void onNextYearButtonClick(ClickEvent event) {
		changeYear(1);
	}
	
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
			}else
				cellsType[row][col].select(row, col);
			
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
		initDatesDialog(DayType.PEONADAS);
	}
	
	// --------------------------- Days Hours
	
	@UiHandler("hourButton")
	public void onHourButtonClick(ClickEvent event) {
		initHourDialog();
	}

	@UiHandler("extraHoursButton")
	public void onExtraHoursButtonClick(ClickEvent event) {
		EmployeeCalendarExtraDialog extraDialog = new EmployeeCalendarExtraDialog(
				this.year,
				this.employeeCalendarDraftObject);
		
		extraDialog.center();
		extraDialog.show();
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
			hideHoursRows();
			showElement(settingMenuItem.getElement());
			hideElement(viewMenuItem.getElement());
			hideElement(hourMenuItem.getElement());
			hideElement(hourButton.getElement());
			extraHoursButton.setText("H. Extras");
		} else {
			this.showHours = true;
			showHoursRows();
			hideElement(settingMenuItem.getElement());
			showElement(viewMenuItem.getElement());
			showElement(hourMenuItem.getElement());
			showElement(hourButton.getElement());
			extraHoursButton.setText("H. Complementarias");
		}
		
		// Set agrarian contract
		if(this.employeeCalendarDraftObject.isAgrarianContract()) {
			showElement(peonadasDayButton.getElement());
			showElement(peonadasDayMenuItem.getElement());
		} else {
			hideElement(peonadasDayButton.getElement());
			hideElement(peonadasDayMenuItem.getElement());
		}
		
		// Clear calendar
		cleanCalendar();
		initCellsType();
		initDayTypeCellsType();
		cleanSelectedDates();
		this.month = 0;
		//Crear calendario
		for (int row = 1; row < totalRows; row += 2)
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
				
				// Check if currentDay is between contract period
				if(isOutOfContractPeriod(currentDay)) {
					labelDay.addStyleName(style.outOfContractStyle());
					hourDay.addStyleName(style.outOfContractStyle());
					cellsType[row][i] = null;
					cellsType[row][i+1] = null;
				} else {
					// Add to cells
					cellsDates[row][i] = currentDay;
					cellsType[row][i] = new DayCell();
					cellsType[row + 1][i] = new HourCell();
					
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
					
				}
				
				calendarGrid.setWidget(row, i, labelDay);
				calendarGrid.setWidget(row + 1, i, hourDay);
				
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
			
			// Check if currentDay is between contract period
			if(isOutOfContractPeriod(currentDay)) {
				labelDay.addStyleName(style.outOfContractStyle());
				hourDay.addStyleName(style.outOfContractStyle());
				cellsType[row][column] = null;
				cellsType[row][column+1] = null;
			} else {
				// Add to cells
				cellsDates[row][column] = currentDay;
				cellsType[row][column] = new DayCell();
				cellsType[row + 1][column] = new HourCell();
				
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
			}
			
			calendarGrid.setWidget(row, column, labelDay);
			calendarGrid.setWidget(row+1, column, hourDay);
			
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
		for (int i = 2; i < totalRows; i += 2) {
			calendarGrid.getRowFormatter().removeStyleName(i, style.hide());
		}
	}

	private void hideHoursRows() {
		for (int i = 2; i < totalRows; i += 2) {
			calendarGrid.getRowFormatter().addStyleName(i, style.hide());
		}
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
			Integer nextYear = new Date().getYear() + 1;
			newEndDate = new Date(nextYear, 11, 31);
		}
		return date.before(this.employeeCalendarDraftObject.getContractStartDate()) || date.after(newEndDate);
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
	//										CHANGE YEAR EVENT
	// ----------------------------------------------------------------------------------------------------
	
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
			
			initCalendar();	
		}
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
					switch (selectedTypeIdx) {
						case 4:
							addDayType(startDate, endDate, DayType.EREDAY, coefficient);
							break;
						case 5:
							addDayType(startDate, endDate, DayType.EREFZADAY, coefficient);
							break;
						case 6:
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
					case 2:
						addDayType(startDate, endDate, DayType.EREDAY, coefficient);
						break;
					case 3:
						addDayType(startDate, endDate, DayType.EREFZADAY, coefficient);
						break;
					case 4:
						addDayType(startDate, endDate, DayType.EREFZAEXONDAY, coefficient);
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
				changeYear(0);
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
		case PEONADAS:
			return "DIAS PEONADAS";
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
		changeYear(0);
	}
	
	private void addPartialityDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		employeeCalendarDraftObject.addPartialityDayType(startDate, endDate, dayType, expression);
		changeYear(0);
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
				cellsType[row][column] = new NoneCell();;
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
		return row == 1 || row == 3 || row == 5 || row == 7 || row == 9 || row == 11 || row == 13 || row == 15 || row == 17 || 
				row == 19 || row == 21 || row == 23;
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
			return 3;
		case 2:
			return 5;
		case 3:
			return 7;
		case 4:
			return 9;
		case 5:
			return 11;
		case 6:
			return 13;
		case 7:
			return 15;
		case 8:
			return 17;
		case 9:
			return 19;
		case 10:
			return 21;
		default:
			return 23;
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
		
		// Si es un dia festivo le anniadimos la descripcion del festivo
		if(dayType == DayType.FREEDAY) {
			labelDay.setTitle(this.employeeCalendarDraftObject.getExpressionByDate(currentDay));
		}
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
					initDatesDialog(DayType.PEONADAS);
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
}
