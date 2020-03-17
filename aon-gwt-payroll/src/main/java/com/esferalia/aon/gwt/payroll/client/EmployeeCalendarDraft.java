package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayTypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public class EmployeeCalendarDraft extends Composite implements ContextMenuHandler {

	private static EmployeeCalendarDraftUiBinder uiBinder = GWT.create(EmployeeCalendarDraftUiBinder.class);

	interface EmployeeCalendarDraftUiBinder extends UiBinder<Widget, EmployeeCalendarDraft> {
	}
	
// ------------------------------------------------- INTERFAZ TIPO DIAS CELDAS -----------------------------------------------------
	
	public static interface CalendarTypeDayCell{
		void setAsType(DayType daytype, int row, int col);
		void setStyle(int row, int col);
		DayType getType();
	}
	
	public class DayTypeCell implements CalendarTypeDayCell{
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
				
				@Override
				public Void visitSuspensionDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.suspensionStyle());
					return null;
				}
				
				@Override
				public Void visitStrikeDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.strikeStyle());	
					return null;
				}
				
				@Override
				public Void visitReductionDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.reductionStyle());	
					return null;
				}
				
				@Override
				public Void visitHolyDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.holidayStyle());	
					return null;
				}
				
				@Override
				public Void visitFreeDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.sundayStyle());	
					return null;
				}
				
				@Override
				public Void visitNoWorkingDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.nonWorkingStyle());	
					return null;
				}
				
				@Override
				public Void visitEreDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.ereStyle());	
					return null;
				}
				
				@Override
				public Void visitDropDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dropStyle());
					return null;
				}

				@Override
				public Void visitITDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.itStyle());
					return null;
				}
				
				@Override
				public Void visitNoTypeDay(DayType dayType) {
					return null;
				}

				@Override
				public Void visitInactivityDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.inactivityStyle());
					return null;
				}

				@Override
				public Void visitPeonadasDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.peonadasStyle());
					return null;
				}

				@Override
				public Void visitPartialityDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.partialityStyle());
					return null;
				}

			});	
		}
	}
	
	
// ----------------------------------------------- INTERFAZ MULTISELECCION CELDAS -----------------------------------------------
	
	public static interface CalendarTypeCell{
		void select(int row, int col);
		void unSelect(int row, int col);
		void setHour(int row, int col, double newHour);
		double getHour(int row, int col);
		void setOnChange(int row, int col);
		void eraseOnChange(int row, int col);
	}
	
	public class DayCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {
			if (cellsDates[row][col] != null){
				calendarGrid.getWidget(row, col).addStyleName(style.isSelectedStyle());
				selectedDates.setSelected(cellsDates[row][col], true);
			}
		}
		@Override
		public void unSelect(int row, int col) {
			if (cellsDates[row][col] != null) {
				calendarGrid.getWidget(row, col).removeStyleName(style.isSelectedStyle());	
				calendarGrid.getWidget(row, col).setTitle("");
			}
		}
		@Override
		public void setHour(int row, int col, double newHour) {
		}
		@Override
		public double getHour(int row, int col) {
			return 0;
		}
		@Override
		public void setOnChange(int row, int col) {
		}
		@Override
		public void eraseOnChange(int row, int col) {
		}
	}
	
	public class HourCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {
			if (cellsDates[row][col] != null){
				calendarGrid.getWidget(row, col).addStyleName(style.doubleBoxStyle());
				selectedDates.setSelected(cellsDates[row][col], true);
			}
		}
		@Override
		public void unSelect(int row, int col) {
			if (cellsDates[row][col] != null){
				if (row%4 == 0)
					calendarGrid.getWidget(row, col).setStyleName(style.doubleBoxDisableStyle2());
				else
					calendarGrid.getWidget(row, col).setStyleName(style.doubleBoxDisableStyle());
				
			}
		}
		@Override
		public void setHour(int row, int col, double newHour) {
			DoubleBox widget = (DoubleBox)calendarGrid.getWidget(row, col);
			widget.setValue(newHour);	
		}
		@Override
		public double getHour(int row, int col) {
			DoubleBox widget = (DoubleBox)calendarGrid.getWidget(row, col);
			return widget.getValue();
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
		
		private NoneCell() {
		}
		
		@Override
		public void select(int row, int col) {
		}
		@Override
		public void unSelect(int row, int col) {
		}
		@Override
		public void setHour(int row, int col, double newHour) {
		}
		@Override
		public double getHour(int row, int col) {
			return 0;
		}
		@Override
		public void setOnChange(int row, int col) {
		}
		@Override
		public void eraseOnChange(int row, int col) {
		}
	} 

// ------------------------------------------------------------- UiFields ----------------------------------------------------------------
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String borderButtonUpperMenuStyle();
		String imagenButtonUpperMenuStyle();
		String paddingEraseButtonUpperMenuStyle();
		String paddingLeyendButtonUpperMenuStyle();
		String doubleBoxStyle();
		String doubleBoxLargeStyle();
		String doubleBoxDisableStyle();
		String doubleBoxDisableStyle2();
		String isSelectedStyle();
		String nonWorkingStyle();
		String sundayStyle();
		String holidayStyle();
		String partialityStyle();
		String ereStyle();
		String strikeStyle();
		String reductionStyle();
		String dropStyle();
		String suspensionStyle();
		String itStyle();
		String inactivityStyle();
		String peonadasStyle();
		String cellStyle();
		String ocultarHorasStyle();
		String onChange();
		String setOutOfContractStyle();
		String pointer();
		String hide();
		String blockStyle();
	}

	
	@UiField
	HorizontalPanel upperRightMenu;
	
	@UiField
	Grid calendarGrid;
	
	@UiField
	MenuItem archivoMenuItem;
	
	@UiField
	MenuItem nonWorkingDayMenuItem;
	
	@UiField
	MenuItem holidayDayMenuItem;
	
	@UiField
	MenuItem inactivityDayMenuItem;
	
	@UiField
	MenuItem dropDayMenuItem;
	
	@UiField
	MenuItem eraseEventMenuItem;
	
	@UiField
	MenuItem selectAllDaysMenuItem;
	
	@UiField
	MenuItem selectUntillMenuItem;
	
	@UiField
	MenuItem selectAllSaturdaysMenuItem;
	
	@UiField
	MenuItem selectAllSundaysMenuItem;
	
	@UiField
	MenuItem hourMenuItem;
	
	@UiField
	MenuItem viewMenuItem;
	
	@UiField
	MenuItem showHourMenuItem;
	
	@UiField
	Button undoButton;
	
	@UiField
	Button redoButton;
	
	@UiField
	Button undoAllButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button resetButton;

	@UiField
	Label yearLabel;

	@UiField
	Button lastYearButton;

	@UiField
	Button nextYearButton;

	@UiField
	Button nonWorkingDayButton;
	
	@UiField
	Button inactivityDayButton;
	
	@UiField
	Button peonadasDayButton;
	
	@UiField
	Button dropDayButton;
	
	@UiField
	Button holidayDayButton;
	
	@UiField
	Button partialityDayButton;
	
	@UiField
	HorizontalPanel extraHoursButtonPanel;
	
	@UiField
	Button extraHoursButton;
	
	@UiField
	Button eraseEventButton;
	
	@UiField
	Button leyendButton;
	
	@UiField
	HorizontalPanel hourButtonPanel;
	
	@UiField
	Button hourButton;
		
	@UiField
	Label totalHours;
	
	@UiField
	ScrollPanel scrollInfo;
	
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
	private OrderedMultiSelectionModel<Date> selectedDates = new OrderedMultiSelectionModel<Date>();
	
	private int oldHourSelected = 0;
	private int month;
	private int year;
	private boolean showHours;
	private boolean fullTimeJourney;
	
	private final CalendarTypeCell cells[][] = new CalendarTypeCell[25][38];
	private final CalendarTypeDayCell cellsType[][] = new CalendarTypeDayCell[25][38];
	private final Date cellsDates[][] = new Date[25][38];
		
	private Double listOldHours[] = new Double[7];
	
	private EmployeeCalendarDraftObjectData calendarEmployeeInfo;
	private Integer startEmployeeContract;
	private Integer endEmployeeContract;
	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	public EmployeeCalendarDraft() {
		
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		calendarGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		initializeMenuItems();
		initializeUpperMenu();
		initializeCellsCalendar();
		initializeCellsTypeCalendar();
		
		//Gestion boton horas
		hourMenuItem.setEnabled(false);
		hourButton.setEnabled(false);	
		
		//#ifndef env.SNAPSHOT
		saveButton.setVisible(false);
		//#endif
		
		//TODO: para probar el boton de guardar del calendario -> saveButton.setVisible(true);
		saveButton.setVisible(true);
	}

	private void initializeMenuItems() {
		
		nonWorkingDayMenuItem.setScheduledCommand(new Command() {	
			@Override
			public void execute() {
				addNoWorkingDay();	
			}
		});
		
		holidayDayMenuItem.setScheduledCommand(new Command() {		
			@Override
			public void execute() {
				addHolidays();	
			}
		});
		
		inactivityDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				EmployeeCalendarInactivityDialog inactivityDialog = new EmployeeCalendarInactivityDialog("D"+String.valueOf("\u00cd")+"as de inactivad"/*, hours.toString()*/) {
					@Override
					protected void onAccept() {
						Date startDate = this.getStartDate();
						Date endDate = this.getEndDate();
						if(endDate == null){
							endDate = DateUtils.getLastDayOfYear(new Date());
							DateUtils.addYears2Date(endDate, 2);
						}
			
						String typeInactivity = this.getTypeInactivity();
						
						while (startDate.before(endDate) || startDate.equals(endDate)) {
							selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
							DateUtils.addDays2Date(startDate, 1);
						}
						
						addInactivityDays(typeInactivity);
					}
				};
								
				if(!selectedDates.getSelectedList().isEmpty()){
					Collections.sort(selectedDates.getSelectedList());
					inactivityDialog.setStartDate(selectedDates.getSelectedList().get(0));
					inactivityDialog.setEndDate(selectedDates.getSelectedList().get(selectedDates.getSelectedList().size()-1));
				}
				
				inactivityDialog.show();
				inactivityDialog.center();
			}
		});
		
		dropDayMenuItem.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				String hours = "8";
				
				if(!fullTimeJourney) {
					Date date = selectedDates.getSelectedList().get(0);
					Double hoursDouble = calendarEmployeeInfo.getHourByDay(date);
					hours = hoursDouble.toString();
				}
				
				EmployeeCalendarPercentDialog percentDialog = new EmployeeCalendarPercentDialog("Dias Ausencia", hours) {
					
					@Override
					protected void onAccept() {
						double cs = this.getPercentValue();
						switch (this.getTypeDrop()) {
						case 0:
							addStrikeDay(cs);
							break;
						case 1:
							addEreDay(cs);
							break;
						case 2:
							addDropDay(cs);
							break;
						default:
							break;
						}
					}
					
				};

				percentDialog.show();
				percentDialog.center();
			}	
		});
		
		eraseEventMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				cleanSelectedDates();
			}
			
		});
		
		selectAllDaysMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				cleanCalendarSelectedDates();
				selectAll();
			}

			@SuppressWarnings("deprecation")
			private void selectAll() {
				Date startDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getStartDateContract());
				Date endDate;
				if(null == calendarEmployeeInfo.getEndDateContract() && 0 == calendarEmployeeInfo.getMapSize()){
					Integer actualYear = new Date().getYear();
					endDate = new Date(actualYear+1,11,31);
				} else if(null == calendarEmployeeInfo.getEndDateContract()){
					endDate = calendarEmployeeInfo.getLastMapDate();
				}else
					endDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getEndDateContract());
				
				while (startDate.before(endDate) || startDate.equals(endDate)) {
					selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
					DateUtils.addDays2Date(startDate, 1);
				}
				paintAllDates(selectedDates.getSelectedList());
				hourButton.setEnabled(true);
			}
			
		});
		
		selectUntillMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				EmployeeCalendarUntillDialog untillDialog = new EmployeeCalendarUntillDialog("Seleccion hasta") {
					
					@Override
					protected void onAccept() {
						if(selectedDates.getSelectedList().size() == 1){
							Date startDate = DateUtils.copyDateOnly(selectedDates.getSelectedList().get(0));
							Date endDate = DateUtils.copyDateOnly(this.getSelectedDate());
							while (startDate.before(endDate) || startDate.equals(endDate)) {
								selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
								DateUtils.addDays2Date(startDate, 1);
							}
						}
						paintSelectedDates(selectedDates.getSelectedList());
						
					}
				};
				untillDialog.setDefaultDate(new Date());
				untillDialog.show();
				untillDialog.center();
			}
		});
		
		selectAllSaturdaysMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				cleanCalendarSelectedDates();
				selectAllSaturdays();	
			}

			private void selectAllSaturdays() {
				Date startDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getStartDateContract());
				Date endDate;
				if(null == calendarEmployeeInfo.getEndDateContract() && 0 == calendarEmployeeInfo.getMapSize()){
					Integer actualYear = new Date().getYear();
					endDate = new Date(actualYear+1,11,31);
				} else if(null == calendarEmployeeInfo.getEndDateContract()){
					endDate = calendarEmployeeInfo.getLastMapDate();
				}else
					endDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getEndDateContract());
				
				while (startDate.before(endDate) || startDate.equals(endDate)) {
					if(startDate.getDay() == 6)
						selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
					DateUtils.addDays2Date(startDate, 1);
				}
				paintAllDates(selectedDates.getSelectedList());
				hourButton.setEnabled(true);
			}
		});
		
		selectAllSundaysMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				cleanCalendarSelectedDates();
				selectAllSundays();	
			}

			private void selectAllSundays() {
				Date startDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getStartDateContract());
				Date endDate;
				if(null == calendarEmployeeInfo.getEndDateContract() && 0 == calendarEmployeeInfo.getMapSize()){
					Integer actualYear = new Date().getYear();
					endDate = new Date(actualYear+1,11,31);
				} else if(null == calendarEmployeeInfo.getEndDateContract()){
					endDate = calendarEmployeeInfo.getLastMapDate();
				}else
					endDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getEndDateContract());
				
				while (startDate.before(endDate) || startDate.equals(endDate)) {
					if(startDate.getDay() == 0)
						selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
					DateUtils.addDays2Date(startDate, 1);
				}
				paintAllDates(selectedDates.getSelectedList());
				hourButton.setEnabled(true);
			}
		});
	
		hourMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				if(!selectedDates.getSelectedList().isEmpty()){
					EmployeeCalendarHoursDialog hourDialog = new EmployeeCalendarHoursDialog("Horas", selectedDates, calendarEmployeeInfo) {
						
						@Override
						protected void onAccept() {
							listOldHours = getListOldHours();
							
							updateHours(this.getMondayHours(), 
										this.getTuesdayHours(), 
										this.getWednesdayHours(), 
										this.getThursdayHours(), 
										this.getFridayHours(), 
										this.getSaturdayHours(), 
										this.getSundayHours());
							
							hourMenuItem.setEnabled(false);
							hourButton.setEnabled(false);
							selectedDates.clear();
							changeYear(0);
							
						}
					};
					hourDialog.show();
					hourDialog.center();
				}
			}
		});
		
		//Visualizar y ocultar las horas del calendario
		showHourMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				showHours = !showHours;
				showHourMenuItem.setStyleName("aon-MenuItemCheckYes", showHours);
				if (showHours)
					hideHours();		
				else
					showHours();
			}
		});	
	}

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	private void initializeUpperMenu() {
		extraHoursButton.addStyleName(style.borderButtonUpperMenuStyle());
		extraHoursButton.addStyleName(style.imagenButtonUpperMenuStyle());
		extraHoursButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EmployeeCalendarExtraDialog extraDialog = new EmployeeCalendarExtraDialog("Horas extras") {
					
					@Override
					protected void onAccept() {
						String month = this.getSelectedMonth();
						Integer intMonth = calculateIntByMonth(month);
						Integer intYear = Integer.parseInt(yearLabel.getText())- 1900;
						Double extraHourMonth = this.getExtraHours();
						calendarEmployeeInfo.setExtraHourByMonth(extraHourMonth, intMonth, intYear);
						hourMenuItem.setEnabled(false);
						hourButton.setEnabled(false);
						selectedDates.clear();
						changeYear(0);
					}
				};
				extraDialog.show();
				extraDialog.center();
			}
		});
		
		hideElement(extraHoursButtonPanel);
		hourButton.addStyleName(style.borderButtonUpperMenuStyle());
		hourButton.addStyleName(style.imagenButtonUpperMenuStyle());
	}
	
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		
		event.preventDefault();
		
		hourMenuItem.setEnabled(true);
		hourButton.setEnabled(true);
		
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * 39) + col;
		
		if (null != cellsDates[row][col] && calendarEmployeeInfo.getStartDateContract().after(cellsDates[row][col]))
			return;
		
		//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY))
		if (cellsType[row][col].getType().equals(DayType.BAJAIT) /*|| cellsType[row][col].getType().equals(DayType.FREEDAY)*/)
			return;
	
		//Pulsacion celda con CTRL
		if (event.isControlKeyDown()) { 
			//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY))
			if (!(cellsType[row][col].getType().equals(DayType.BAJAIT) /*|| cellsType[row][col].getType().equals(DayType.FREEDAY)*/))
				cells[row][col].select(row, col);
		
		//Pulsacion celda con SHIFT
		} else if (event.isShiftKeyDown()){ 
			int initialPosition = calculateDatePosition(selectedDates.getSelectedList().get(0));
			if(initialPosition != -1){
				int endPosition = pos;
				
				if (initialPosition > endPosition){
					int posAux = initialPosition;
					initialPosition = endPosition-1;
					endPosition = posAux;
				}
				
				while (initialPosition != endPosition){
					//TODO: poner este codigo para bloquear no laborables -> || cellsType[calcularFila(posicionIncial+1)][calcularColumna(posicionIncial+1)].getType().equals(DayType.NOWORKINGDAY))
					if (!(cellsType[calculatePositionRow(initialPosition+1)][calculatePositionCol(initialPosition+1)].getType().equals(DayType.BAJAIT)
							/* || cellsType[calculatePositionRow(initialPosition+1)][calculatePositionCol(initialPosition+1)].getType().equals(DayType.FREEDAY)*/)){
						cells[calculatePositionRow(initialPosition+1)][calculatePositionCol(initialPosition+1)]
								.select(calculatePositionRow(initialPosition+1), calculatePositionCol(initialPosition+1));
					}
					initialPosition++;	
				}
			}
		
		//Pulsacion una sola celda	
		} else { 			
			cleanCalendarSelectedDates();
			
			cells[calculatePositionRow(oldHourSelected)][calculatePositionCol(oldHourSelected)]
					.unSelect(calculatePositionRow(oldHourSelected), calculatePositionCol(oldHourSelected));
			
			if (isMonth(row, col)){
				for(int i = 1; i<38; i++)
					//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][i].getType().equals(DayType.NOWORKINGDAY))
					if (!(cellsType[row][i].getType().equals(DayType.BAJAIT) /*|| cellsType[row][i].getType().equals(DayType.FREEDAY)*/))
						cells[row][i].select(row, i);
			}else
				//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY))
				if (!(cellsType[row][col].getType().equals(DayType.BAJAIT) /*|| cellsType[row][col].getType().equals(DayType.FREEDAY)*/))
					cells[row][col].select(row, col);
			
			oldHourSelected = pos;
		}
	
	}

	private void cleanCalendarSelectedDates() {
		for (Date date : selectedDates.getSelectedList()) {
			int posicion = calculateDatePosition(date);
			if(posicion != -1){
				int colSelect = calculatePositionCol(posicion);
				int filSelect = calculatePositionRow(posicion);
				cells[filSelect][colSelect].unSelect(filSelect, colSelect);
		
			}
		}
		
		selectedDates.clear();	
	}
		
	@UiHandler("calendarGrid")
	public void onDragStart(DragStartEvent event) {
		//TODO: ver como hacer el drag con el raton en vez de con shift
	}
	
	@UiHandler("eraseEventButton")
	public void onEraseClick(ClickEvent event) {
		cleanSelectedDates();
	}
	
	@UiHandler("leyendButton")
	public void onLeyendButtonPanelClick(ClickEvent event) {
		EmployeeCalendarLeyendDialog leyendDialog = new EmployeeCalendarLeyendDialog();
		leyendDialog.show();
		leyendDialog.center();
	}

	@UiHandler("nonWorkingDayButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		addNoWorkingDay();	
	}

	@UiHandler("holidayDayButton")
	public void onVacacionesClick(ClickEvent event) {
		addHolidays();
	}
	
	@UiHandler("partialityDayButton")
	public void onPartialityClick(ClickEvent event) {
		EmployeeCalendarPartialityDialog partialityDialog = new EmployeeCalendarPartialityDialog("Coeficiente Parcialidad") {
			
			@Override
			protected void onAccept() {
				Date startDate = this.getStartDate();
				Date endDate = this.getEndDate();
				
				if(endDate == null){
					endDate = DateUtils.getLastDayOfYear(new Date());
					DateUtils.addYears2Date(endDate, 2);
				}
				
				Double partialityCoeficient = getPartiality();
				
				cleanCalendarSelectedDates();
				
				while (startDate.before(endDate) || startDate.equals(endDate)) {
					selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
					DateUtils.addDays2Date(startDate, 1);
				}
				
				addPartialityDays(partialityCoeficient);
			}
		};
		
		if(!selectedDates.getSelectedList().isEmpty()){
			Collections.sort(selectedDates.getSelectedList());
			partialityDialog.setStartDate(selectedDates.getSelectedList().get(0));
			partialityDialog.setEndDate(selectedDates.getSelectedList().get(selectedDates.getSelectedList().size()-1));
		}
		
		partialityDialog.center();
		partialityDialog.show();
	}
	
	@UiHandler("peonadasDayButton")
	public void onPeonadasClick(ClickEvent event) {
		addPeonadas();
	}
	
	@UiHandler("dropDayButton")
	public void onDropDaysClick(ClickEvent event) {
		EmployeeCalendarPercentDialog percentDialog;
		if(fullTimeJourney){
			percentDialog = new EmployeeCalendarPercentDialog("Dias Ausencia", "8") {
				
				@Override
				protected void onAccept() {
					double cs = this.getPercentValue();
					switch (this.getTypeDrop()) {
					case 0:
						addStrikeDay(cs);
						break;
					case 1:
						addEreDay(cs);
						break;
					case 2:
						addDropDay(cs);
						break;
					default:
						break;
					}
				}
				
			};
		}else{
			Date date = selectedDates.getSelectedList().get(0);
			Double hours = calendarEmployeeInfo.getHourByDay(date);
			percentDialog = new EmployeeCalendarPercentDialog("Dias Ausencia", hours.toString()) {
				
				@Override
				protected void onAccept() {
					double cs = this.getPercentValue();
					switch (this.getTypeDrop()) {
					case 0:
						addStrikeDay(cs);
						break;
					case 1:
						addEreDay(cs);
						break;
					case 2:
						addDropDay(cs);
						break;
					default:
						break;
					}
				}
				
			};
		}

//		percentDialog.setLabelText("Horas:");
		percentDialog.show();
		percentDialog.center();
	}
	
	@UiHandler("inactivityDayButton")
	public void onInactivityClick(ClickEvent event) {		

		EmployeeCalendarInactivityDialog inactivityDialog = new EmployeeCalendarInactivityDialog("D"+String.valueOf("\u00cd")+"as de inactivad"/*, hours.toString()*/) {
			@Override
			protected void onAccept() {
				Date startDate = this.getStartDate();
				Date endDate = this.getEndDate();
				if(endDate == null){
					endDate = DateUtils.getLastDayOfYear(new Date());
					DateUtils.addYears2Date(endDate, 2);
					//Window.alert(endDate.toGMTString());
				}
				String typeInactivity = this.getTypeInactivity();
				
				while (startDate.before(endDate) || startDate.equals(endDate)) {
					selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
					DateUtils.addDays2Date(startDate, 1);
				}
				
				addInactivityDays(typeInactivity);
			}
		};
						
		if(!selectedDates.getSelectedList().isEmpty()){
			Collections.sort(selectedDates.getSelectedList());
			inactivityDialog.setStartDate(selectedDates.getSelectedList().get(0));
			inactivityDialog.setEndDate(selectedDates.getSelectedList().get(selectedDates.getSelectedList().size()-1));
		}
		
		inactivityDialog.show();
		inactivityDialog.center();
		
	}
		
	@UiHandler("hourButton")
	public void onHourClick(ClickEvent event) {
		if(!selectedDates.getSelectedList().isEmpty()){
			EmployeeCalendarHoursDialog hourDialog = new EmployeeCalendarHoursDialog("Horas", selectedDates, calendarEmployeeInfo) {
				
				@Override
				protected void onAccept() {
					listOldHours = getListOldHours();
					
					updateHours(this.getMondayHours(), 
								this.getTuesdayHours(), 
								this.getWednesdayHours(), 
								this.getThursdayHours(), 
								this.getFridayHours(), 
								this.getSaturdayHours(), 
								this.getSundayHours());
					
					hourMenuItem.setEnabled(false);
					hourButton.setEnabled(false);
					selectedDates.clear();
					changeYear(0);
					
				}
			};
			hourDialog.show();
			hourDialog.center();
		}
	}
		
	@UiHandler("lastYearButton")
	public void onLastYearClick(ClickEvent event) {
		changeYear(-1);
	}

	@UiHandler("nextYearButton")
	public void onNextYearClick(ClickEvent event) {
		changeYear(1);
	}
	
	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		calendarEmployeeInfo.undoManager.undo();
		
		this.month = 0;
		
		cleanStyleChanges();
		cleanCalendar();
		showCalendarWidget(Integer.parseInt(yearLabel.getText())- 1900);
		
		paintMadeHourChanges(calendarEmployeeInfo.getChangesHours());
		paintMadeTypeChanges(calendarEmployeeInfo.getChangesTypes());
		paintMadeExtraHoursChanges(calendarEmployeeInfo.getChangesExtraHours());
			
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		calendarEmployeeInfo.undoManager.redo();
		
		this.month = 0;
		
		cleanStyleChanges();
		cleanCalendar();
		showCalendarWidget(Integer.parseInt(yearLabel.getText())- 1900);
		
		paintMadeHourChanges(calendarEmployeeInfo.getChangesHours());
		paintMadeTypeChanges(calendarEmployeeInfo.getChangesTypes());
		paintMadeExtraHoursChanges(calendarEmployeeInfo.getChangesExtraHours());
		
	}
	
	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while (calendarEmployeeInfo.undoManager.canUndo())
			calendarEmployeeInfo.undoManager.undo();
		
		this.month = 0;
		
		cleanStyleChanges();
		cleanCalendar();
		showCalendarWidget(Integer.parseInt(yearLabel.getText())- 1900);
		
		paintMadeHourChanges(calendarEmployeeInfo.getChangesHours());
		paintMadeTypeChanges(calendarEmployeeInfo.getChangesTypes());
		paintMadeExtraHoursChanges(calendarEmployeeInfo.getChangesExtraHours());
		
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		calendarEmployeeInfo.updateDBCalendar(r -> 
		{
			setEmployeeCalendarDraftObject(calendarEmployeeInfo);
			calendarEmployeeInfo.undoManager.discardAll();
		}, t -> {});
	}
	
	@UiHandler("resetButton")
	void onResetButtonClick(ClickEvent event) {
		AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", String.valueOf("\u00BF")+"Realmente desea resetear el calendario?", "", "") {
			
			@Override
			protected void onAccept() {
				calendarEmployeeInfo.resetCalendar(r -> 
				{
					setEmployeeCalendarDraftObject(calendarEmployeeInfo);
					calendarEmployeeInfo.undoManager.discardAll();
				}, t -> {});
			}
		};
		
		dialog.center();
		dialog.show();
	}
	
// -------------------------------------------------------------- METODOS DE LA CLASE ----------------------------------------------------
	
	/**
	 * Metodo al que se llama cada vez que se quiere iniciar el calendario.
	 * @param calendar : objeto que contiene la informacion que debe mostrar el calendario
	 */
	public void setEmployeeCalendarDraftObject(EmployeeCalendarDraftObjectData calendar) {
		
		this.calendarEmployeeInfo = calendar;
		
		//Window.alert("IdEmployee :"+calendar.getEmployeeId());
		
		this.calendarEmployeeInfo.undoManager.addListener(new UndoManager.Listener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onChange(UndoManager undoManager) {
				redoButton.setEnabled(undoManager.canRedo());
				undoButton.setEnabled(undoManager.canUndo());
				saveButton.setEnabled(undoManager.canUndo());
				undoAllButton.setEnabled(undoManager.canUndo());
			}
		});
		
		calendar.initializeDBCalendar(
				r -> { this.showHours = calendarEmployeeInfo.isFullTimeJourney();
					   this.fullTimeJourney = calendarEmployeeInfo.isFullTimeJourney();
					   initCalendar();
					 }, t -> {});
	
		archivoMenuItem.ensureDebugId("archivo_mi");
		holidayDayMenuItem.ensureDebugId("holiday_mi");
		undoButton.ensureDebugId("undo_btn");
		redoButton.ensureDebugId("redo_btn");
	}
	
	/**
	 * Metodo que gestiona la variables de la clase a la hora de inicializar
	 */
	@SuppressWarnings("deprecation")
	private void initCalendar() {
		redoButton.setEnabled(this.calendarEmployeeInfo.undoManager.canRedo());
		undoButton.setEnabled(this.calendarEmployeeInfo.undoManager.canUndo());
		undoAllButton.setEnabled(this.calendarEmployeeInfo.undoManager.canUndo());
		
		this.year = new Date().getYear();
		this.month = 0;
		this.startEmployeeContract = getStartYearContract(calendarEmployeeInfo.getStartDateContract());
		this.endEmployeeContract = getEndYearContract(calendarEmployeeInfo.getEndDateContract());
		int actualYear = this.year+1900;
		this.yearLabel.setText(Integer.toString(actualYear));
		
		if(this.fullTimeJourney)
			totalHours.setText("Horas Extras");
		else{
			totalHours.setText("Horas Mensuales");
		}
		
		if(this.calendarEmployeeInfo.getContractType() == 7) {
			this.peonadasDayButton.removeStyleName(style.hide());
		}else {
			this.peonadasDayButton.addStyleName(style.hide());
		}
		
		cleanStyleChanges();
		cleanCalendar();
		initializeCellsCalendar();
		initializeCellsTypeCalendar();
		showCalendarWidget(this.year);
		
		if (this.showHours){
			showHourMenuItem.setStyleName("aon-MenuItemCheckYes", showHours);
			hideHours();
		}else{
			showHourMenuItem.setStyleName("aon-MenuItemCheckYes", showHours);
			showHours();
		}
		
		if(this.fullTimeJourney){
			hourMenuItem.setVisible(false);
			hideElement(hourButtonPanel);
			showElement(extraHoursButtonPanel);
			viewMenuItem.setVisible(false);
		}else{
			hourMenuItem.setVisible(true);
			showElement(hourButtonPanel);
			hideElement(extraHoursButtonPanel);
			viewMenuItem.setVisible(true);
		}
		
		if (!fullTimeJourney)
			if(calendarEmployeeInfo.isMapHoursEmpty()) {
				if(calendarEmployeeInfo.isMapPartialityEmpty()) {
					hourButton.getElement().getStyle().clearDisplay();
					partialityDayButton.getElement().getStyle().clearDisplay();
				}else {
					hourButton.getElement().getStyle().setDisplay(Display.NONE);
				}
			}else{
				hourButton.getElement().getStyle().clearDisplay();
				partialityDayButton.getElement().getStyle().setDisplay(Display.NONE);
			}
		
		paintMadeHourChanges(calendarEmployeeInfo.getChangesHours());
		paintMadeTypeChanges(calendarEmployeeInfo.getChangesTypes());
				
	}
	
	@SuppressWarnings("deprecation")
	private Integer getStartYearContract(Date startDateContract) {
		return startDateContract.getYear();
	}
	
	@SuppressWarnings("deprecation")
	private Integer getEndYearContract(Date endDateContract) {
		if(endDateContract != null)
			return endDateContract.getYear();
		else
			return Integer.MAX_VALUE;
	}
	
	private void initializeCellsCalendar() {
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 39; column++)
				cells[row][column] = NoneCell.NONE_CELL;
	}
	
	private void initializeCellsTypeCalendar() {
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 39; column++)
				cellsType[row][column] = new DayTypeCell(DayType.NOTYPEDAY);
	}
	
	/**
	 * Metodo que gestiona la movibilidad entre años y llama a pintar el calendario
	 * @param year : año que debe pintar el calendario
	 */
	@SuppressWarnings("deprecation")
	private void showCalendarWidget(int year) {
		//Crear calendario
		for (int row = 1; row < 25; row += 2)
			showCalendar(row, year);
		
		this.nextYearButton.setEnabled(true);
		this.lastYearButton.setEnabled(true);
		
		if (year == startEmployeeContract || year == ((new Date().getYear())-1)){
			blockBeforeOutOfContractDays(calendarEmployeeInfo.getStartDateContract());
			this.lastYearButton.setEnabled(false);
		}
		if (year == endEmployeeContract || year == ((new Date().getYear())+1)){
			this.nextYearButton.setEnabled(false);
			if(endEmployeeContract != null)
				blockAfterOutOfContractDays(calendarEmployeeInfo.getEndDateContract());
		}
		
		
	}

	private int calcutaNumberDayOfWeek(int day, int month, int year) {
		@SuppressWarnings("deprecation")
		Date date = new Date(year, month, day);
		@SuppressWarnings("deprecation")
		int numDay = date.getDay();

		// Tratamiento calendario español, 0 = Lunes, 6 = Domingo
		if (0 == numDay)
			numDay = 7;

		return numDay;
	}
	
	@SuppressWarnings("deprecation")
	private boolean checkDate(int day, int month, int year) {
		return month >= 0 && month <= 11 && year > 0 && year < 32768 && day >= 0
				&& day <= (new Date(year, month, day)).getDate();
	}

	private int calculateLastDayOfMonth(int month, int year) {
		int lastDay = 28;
		while (checkDate(lastDay + 1, month, year))
			lastDay++;

		return lastDay;
	}

	private void showCalendar(int row, int year) {
		// Mostrar días del mes
		int contDays = 1;
		double monthHours = 0;
		int firstDayOfMonth = calcutaNumberDayOfWeek(1, month, year);
		int lastDayOfMonth = calculateLastDayOfMonth(month, year);

		// Escribo la primera fila de la semana
		for (int i = 1; i <= 7; i++) {
			// Label insetar
			Label labelDay = new Label();
			labelDay.setStyleName(style.cellStyle());

			if (i < firstDayOfMonth) {
				labelDay.setText("");
				labelDay.ensureDebugId("0_"+month);
				calendarGrid.setWidget(row, i, labelDay);
				cells[row + 1][i] = new NoneCell();
			} else {
				@SuppressWarnings("deprecation")
				Date actualDay = new Date(year, month, contDays);
				labelDay.ensureDebugId(actualDay.getDate()+"_"+month);
				
				DateUtils.resetTime(actualDay);
				DayType dayType = calendarEmployeeInfo.getTypeByDay(actualDay);
				TextBox textHour = new TextBox();
				DoubleBox doubleHour = new DoubleBox();
				
				//if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay) == null){
					textHour.setValue("-");
				}else{
					doubleHour.setEnabled(false);
					double hourByDay = calendarEmployeeInfo.getHourByDay(actualDay);
					doubleHour.ensureDebugId(actualDay.getDate()+"_"+month+"_"+hourByDay+"_hour");
					doubleHour.setValue(hourByDay);
					monthHours += hourByDay;
					doubleHour.setEnabled(false);
				}
				
				//if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
					int hourRow = row + 1;
					if (hourRow % 4 == 0)
						textHour.setStyleName(style.doubleBoxDisableStyle2());
					else
						textHour.setStyleName(style.doubleBoxDisableStyle());
					
					textHour.setStyleName(style.doubleBoxLargeStyle(), textHour.getText() != null && textHour.getText().length() > 2);
					
				}else{
					int hourRow = row + 1;
					if (hourRow % 4 == 0)
						doubleHour.setStyleName(style.doubleBoxDisableStyle2());
					else
						doubleHour.setStyleName(style.doubleBoxDisableStyle());
					
					doubleHour.setStyleName(style.doubleBoxLargeStyle(), doubleHour.getText() != null && doubleHour.getText().length() > 2);
					
				}
				
				labelDay.setText(contDays + "");
				labelDay.addStyleName(style.pointer());
				calendarGrid.setWidget(row, i, labelDay);
				
				//if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
					calendarGrid.setWidget(row + 1, i, textHour);
				}else{
					calendarGrid.setWidget(row + 1, i, doubleHour);
				}
				
				cellsDates[row][i] = actualDay;
				cells[row][i] = new DayCell();
				cells[row + 1][i] = new HourCell();
				
				//if (DayType.BAJAIT == dayType || DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				if (DayType.BAJAIT == dayType || DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
					calendarGrid.getCellFormatter().addStyleName(row+1, i, style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, i).addStyleName(style.setOutOfContractStyle());
				}
				
				if(DayType.FREEDAY == dayType){
					calendarGrid.getWidget(row, i).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
					calendarGrid.getWidget(row+1, i).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
				}
				
				if(DayType.INACTIVITY == dayType){
					calendarGrid.getWidget(row, i).setTitle(calendarEmployeeInfo.getDescriptionInactivity(actualDay));
					calendarGrid.getWidget(row+1, i).setTitle(calendarEmployeeInfo.getDescriptionInactivity(actualDay));
				}
				
				if(DayType.STRIKEDAY == dayType){
					calendarGrid.getWidget(row, i).setTitle("HUELGA");
					calendarGrid.getWidget(row+1, i).setTitle("HUELGA");
				}
				
				if(DayType.EREDAY == dayType){
					calendarGrid.getWidget(row, i).setTitle("ERE");
					calendarGrid.getWidget(row+1, i).setTitle("ERE");
				}
				
				if(DayType.DROPDAY == dayType){
					calendarGrid.getWidget(row, i).setTitle("AUSENCIA INJUSTIFICADA");
					calendarGrid.getWidget(row+1, i).setTitle("AUSENCIA INJUSTIFICADA");
				}
				
				if(DayType.PARTIALITY == dayType){
					Double parcialityCoeficient = calendarEmployeeInfo.getPartialityCoeficient(actualDay);
					
					calendarGrid.getWidget(row, i).setTitle("Parcialidad : " + parcialityCoeficient);
					calendarGrid.getWidget(row+1, i).setTitle("Parcialidad : " + parcialityCoeficient);
					
					calendarGrid.getWidget(row, i).getElement().getStyle().setOpacity(parcialityCoeficient);
				}
				
				cellsType[row][i].setAsType(dayType, row, i);
				contDays++;
				
			}
		}

		
		int actualDayOfWeek = 1;
		while (contDays <= lastDayOfMonth) {
			// Dia Acutal
			@SuppressWarnings("deprecation")
			Date actualDay = new Date(year, month, contDays);
			
			DateUtils.resetTime(actualDay);
			
			// Label insetar
			Label labelDay = new Label(contDays + "");
			labelDay.ensureDebugId(actualDay.getDate()+"_"+month);
			labelDay.setStyleName(style.cellStyle());
			labelDay.addStyleName(style.pointer());
			
			DayType dayType = calendarEmployeeInfo.getTypeByDay(actualDay);
			
			TextBox textHour = new TextBox();
			DoubleBox doubleHour = new DoubleBox();
			
			//if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
			if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
				textHour.setValue("-");
			}else{
				doubleHour.setEnabled(false);
				double hourByDay = calendarEmployeeInfo.getHourByDay(actualDay);
				doubleHour.ensureDebugId(actualDay.getDate()+"_"+month+"_"+hourByDay+"_hour");
				doubleHour.setValue(hourByDay);
				monthHours += hourByDay;
				doubleHour.setEnabled(false);
			}
			
			//if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
			if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
				int hourRow = row + 1;
				if (hourRow % 4 == 0)
					textHour.setStyleName(style.doubleBoxDisableStyle2());
				else
					textHour.setStyleName(style.doubleBoxDisableStyle());
				
				textHour.setStyleName(style.doubleBoxLargeStyle(), textHour.getText() != null && textHour.getText().length() > 2);
				
			}else{
				int hourRow = row + 1;
				if (hourRow % 4 == 0)
					doubleHour.setStyleName(style.doubleBoxDisableStyle2());
				else
					doubleHour.setStyleName(style.doubleBoxDisableStyle());
				
				doubleHour.setStyleName(style.doubleBoxLargeStyle(), doubleHour.getText() != null && doubleHour.getText().length() > 2);
				
			}
			
			calendarGrid.setWidget(row, 7 + actualDayOfWeek, labelDay);
			
			//if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
			if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
				calendarGrid.setWidget(row + 1, 7 + actualDayOfWeek, textHour);
			}else{
				calendarGrid.setWidget(row + 1, 7 + actualDayOfWeek, doubleHour);
			}
			
			cellsDates[row][7 + actualDayOfWeek] = actualDay;
			cells[row][7 + actualDayOfWeek] = new DayCell();
			cells[row + 1][7 + actualDayOfWeek] = new HourCell();
			
			//if (DayType.BAJAIT == dayType || DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
			if (DayType.BAJAIT == dayType || DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==null){
				calendarGrid.getCellFormatter().addStyleName(row+1, 7 + actualDayOfWeek, style.setOutOfContractStyle());
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).addStyleName(style.setOutOfContractStyle());
			}
			
			if(DayType.FREEDAY == dayType){
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
			}
			
			if(DayType.INACTIVITY == dayType){
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle(calendarEmployeeInfo.getDescriptionInactivity(actualDay));
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle(calendarEmployeeInfo.getDescriptionInactivity(actualDay));
			}
			
			if(DayType.STRIKEDAY == dayType){
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle("HUELGA");
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle("HUELGA");
			}
			
			if(DayType.EREDAY == dayType){
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle("ERE");
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle("ERE");
			}
			
			if(DayType.DROPDAY == dayType){
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle("AUSENCIA INJUSTIFICADA");
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle("AUSENCIA INJUSTIFICADA");
			}
			
			if(DayType.PARTIALITY == dayType){
				Double parcialityCoeficient = calendarEmployeeInfo.getPartialityCoeficient(actualDay);
				
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle("Parcialidad : " + parcialityCoeficient);
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle("Parcialidad : " + parcialityCoeficient);
				
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).getElement().getStyle().setOpacity(parcialityCoeficient);
			}
			
			
			cellsType[row][7 + actualDayOfWeek].setAsType(dayType, row, (7 + actualDayOfWeek));
			contDays++;
			actualDayOfWeek++;
			
		}

		if ((actualDayOfWeek) < 30) {
			for (int i = 7 + actualDayOfWeek; i < 38; i++) {
				Label labelDay = new Label();
				labelDay.ensureDebugId("0_"+month);
				labelDay.setText("");
				labelDay.setStyleName(style.cellStyle());
				calendarGrid.setWidget(row, i, labelDay);
				cells[row][i] = new NoneCell();
			}
		}
		
		//Poner horas totales o extras mensuales en la ultima columna
		if (fullTimeJourney){
			setExtraHoursMonth(row);
		}else{
			if(0 != monthHours)
				setMonthHours(row, monthHours);
		}
		
		// Ver si tiene parcialiad y que tipo de contrato es
		if (!fullTimeJourney)
			if(calendarEmployeeInfo.isMapHoursEmpty()) {
				if(calendarEmployeeInfo.isMapPartialityEmpty()) {
					calendarGrid.getRowFormatter().getElement(row+1).getStyle().clearDisplay();
				}else {
					calendarGrid.getRowFormatter().getElement(row+1).getStyle().setDisplay(Display.NONE);
				}
			}else{
				calendarGrid.getRowFormatter().getElement(row+1).getStyle().clearDisplay();
			}
		
		month++;
	}
	
	private void setExtraHoursMonth(int row) {
		Label labelDay = new Label();
		String month = calendarGrid.getWidget(row, 0).getElement().getInnerText();
		Date auxDate = new Date(Integer.parseInt(yearLabel.getText())- 1900, calculateIntByMonth(month), 1);
		DateUtils.resetTime(auxDate);
		double monthExtraHoursRound = roundDecimal(calendarEmployeeInfo.getExtraHourByMonth(auxDate), 2);
		labelDay.setText(Double.toString(monthExtraHoursRound));
		labelDay.setStyleName(style.cellStyle());
		calendarGrid.setWidget(row, 38, labelDay);
		cells[row][38] = new NoneCell();	
	}

	private void setMonthHours(int row, double monthHours) {
		Label labelDay = new Label();
		double monthHoursRound = roundDecimal(monthHours, 2);
		labelDay.setText(Double.toString(monthHoursRound));
		labelDay.setStyleName(style.cellStyle());
		calendarGrid.setWidget(row, 38, labelDay);
		cells[row][38] = new NoneCell();
	}

	private double roundDecimal(double monthHours, int decimalNum) {
		double parteEntera, resultado;
		resultado = monthHours;
		parteEntera = Math.floor(resultado);
		resultado = (resultado-parteEntera)*Math.pow(10, decimalNum);
		resultado = Math.round(resultado);
		resultado = (resultado/Math.pow(10, decimalNum))+parteEntera;
		return resultado;	
	}

	/**
	 * Bloque para limpiar estilos de todas las posiciones seleccionas, de una sola posicion,
	 * limpiar estilo de seleccion de las posiciones seleccionadas , limpiar estilo fuera de
	 * contraro al cambiar de año, limpiar el calendario completamente.
	 */
	private void cleanStyles(List<Date> selectedDates) {
		for (Date date : selectedDates) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int col = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				calendarGrid.getWidget(row, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.sundayStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.holidayStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.dropStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.strikeStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.ereStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.reductionStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.suspensionStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.itStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.nonWorkingStyle());
				cellsType[row][col].setAsType(DayType.NOTYPEDAY, row, col);
			}
		}
	}
	
	private void cleanOneDayStyle(int row, int col) {
		calendarGrid.getWidget(row, col).removeStyleName(style.isSelectedStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.sundayStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.holidayStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.dropStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.strikeStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.ereStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.reductionStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.suspensionStyle());
		calendarGrid.getWidget(row, col).removeStyleName(style.itStyle());
		calendarGrid.getWidget(row, col).setStyleName(style.blockStyle());
		cellsType[row][col].setAsType(DayType.NOTYPEDAY, row, col);
	}
	
	private void cleanSelectStyleSelectedDates(List<Date> selectedDates) {
		for (Date date : selectedDates) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int col = calculatePositionCol(pos);
				int fil = calculatePositionRow(pos);
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
			}
		}
	}
	
	private void cleanStyleChanges() {
		for (int i = 1; i < 25; i++){
			for (int j = 1; j < 38; j++){
				cells[i][j].eraseOnChange(i, j);
				if (null != calendarGrid.getWidget(i, j))
					calendarGrid.getCellFormatter().removeStyleName(i, j, style.setOutOfContractStyle());
			}
			if (null != calendarGrid.getWidget(i, 38)){
				calendarGrid.getWidget(i, 38).removeStyleName(style.onChange());
				calendarGrid.getCellFormatter().removeStyleName(i, 38, style.onChange());
			}
		}	
	}

	private void cleanCalendar() {
		for (int i = 1; i < 25; i++)
			for (int j = 1; j < 39; j++)
				calendarGrid.clearCell(i, j);
	}
	
	/**
	 * Pintar los cambios realizados tanto en las horas como en los tipos de dias, cuando
	 * todavia no se ha guardado.
	 */
	private void paintMadeHourChanges(Set<Entry<Date, Double>> hourChanges) {
		for (Entry<Date,Double> e : hourChanges){
			for (int i = 1; i < 25; i++)
				for (int j = 1; j < 39; j++){
					if (e.getKey().equals(cellsDates[i][j]))
						cells[i+1][j].setOnChange(i+1, j);
				}		
		}	
	}
	
	private void paintMadeExtraHoursChanges(Set<Entry<Date, Double>> changesExtraHours) {
		for (Entry<Date,Double> e : changesExtraHours){
			int row = calculateRowByMonth(e.getKey().getMonth());
			if (row != -1){
				calendarGrid.getWidget(row, 38).addStyleName(style.onChange());
				calendarGrid.getCellFormatter().addStyleName(row, 38, style.onChange());
			}
		}
		
	}
	
	private int calculateRowByMonth(int month) {
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
		case 11:
			return 23;
		default:
			return -1;
		}
	}
	
	private int calculateIntByMonth(String month) {
		switch (month) {
		case "Enero":
			return 0;
		case "Febrero":
			return 1;
		case "Marzo":
			return 2;
		case "Abril":
			return 3;
		case "Mayo":
			return 4;
		case "Junio":
			return 5;
		case "Julio":
			return 6;
		case "Agosto":
			return 7;
		case "Septiembre":
			return 8;
		case "Octubre":
			return 9;
		case "Noviembre":
			return 10;
		case "Diciembre":
			return 11;
		default:
			return -1;
		}
	}

	private void paintMadeTypeChanges(Set<Entry<Date, DayType>> typeChanges) {
		for (Entry<Date,DayType> e : typeChanges){
			for (int i = 1; i < 25; i++)
				for (int j = 1; j < 39; j++){
					if (e.getKey().equals(cellsDates[i][j]))
						cellsType[i][j].setStyle(i, j);
				}		
		}	
	}

	@SuppressWarnings("deprecation")
	private boolean es(int day,Date date) {
		return day == date.getDay();
	}

	private int calculatePositionCol(int pos) {
		return pos % 39;
	}

	private int calculatePositionRow(int pos) {
		return pos / 39;
	}
	
	private boolean isMonth(int row, int col) {
		return col==0 && isMonthAux(row);
	}
	
	private boolean isMonthAux(int row) {
		return row == 1 || row == 3 || row == 5 || row == 7 || row == 9 || row == 11 || row == 13 || row == 15 || row == 17 || 
				row == 19 || row == 21 || row == 23;
	}
	
	private void updateHours(Double mondayHour, Double tuesdayHour, Double wendsdayHour, Double thursdayHour, Double fridayHour, 
			Double saturdayHour, Double sundayHour) {

		if(selectedDates.getSelectedList().size() == 1){
			for (Date date : selectedDates.getSelectedList()) {
				if (es(0, date) && listOldHours[6] != sundayHour){//DOMINGO
					checkNonWorkingDay(date, sundayHour);
					calendarEmployeeInfo.setHourByDay(date, sundayHour);
				}else if (es(6, date) && listOldHours[5] != saturdayHour){//SABADO
					checkNonWorkingDay(date, saturdayHour);
					calendarEmployeeInfo.setHourByDay(date, saturdayHour);
				}else if (es(5, date) && listOldHours[4] != fridayHour){//VIERNES
					checkNonWorkingDay(date, fridayHour);
					calendarEmployeeInfo.setHourByDay(date, fridayHour);
				}else if (es(4, date) && listOldHours[3] != thursdayHour){//JUEVES
					checkNonWorkingDay(date, thursdayHour);
					calendarEmployeeInfo.setHourByDay(date, thursdayHour);
				}else if (es(3, date) && listOldHours[2] != wendsdayHour){//MIERCOLES
					checkNonWorkingDay(date, wendsdayHour);
					calendarEmployeeInfo.setHourByDay(date, wendsdayHour);
				}else if (es(2, date) && listOldHours[1] != tuesdayHour){//MARTES
					checkNonWorkingDay(date, tuesdayHour);
					calendarEmployeeInfo.setHourByDay(date, tuesdayHour);
				}else if (es(1, date) && listOldHours[0] != mondayHour){//LUNES 
					checkNonWorkingDay(date, mondayHour);
					calendarEmployeeInfo.setHourByDay(date, mondayHour);
				}
			}
		}else{
			HashMap<Date, Double> composite = new HashMap<Date, Double>();
			for (Date date : selectedDates.getSelectedList()) {
				if (es(0, date) && listOldHours[6] != sundayHour){//DOMINGO
					checkNonWorkingDay(date, sundayHour);
					composite.put(date, sundayHour);
				}else if (es(6, date) && listOldHours[5] != saturdayHour){//SABADO
					checkNonWorkingDay(date, saturdayHour);
					composite.put(date, saturdayHour);
				}else if (es(5, date) && listOldHours[4] != fridayHour){//VIERNES
					checkNonWorkingDay(date, fridayHour);
					composite.put(date, fridayHour);
				}else if (es(4, date) && listOldHours[3] != thursdayHour){//JUEVES
					checkNonWorkingDay(date, thursdayHour);
					composite.put(date, thursdayHour);
				}else if (es(3, date) && listOldHours[2] != wendsdayHour){//MIERCOLES
					checkNonWorkingDay(date, wendsdayHour);
					composite.put(date, wendsdayHour);
				}else if (es(2, date) && listOldHours[1] != tuesdayHour){//MARTES
					checkNonWorkingDay(date, tuesdayHour);
					composite.put(date, tuesdayHour);
				}else if (es(1, date) && listOldHours[0] != mondayHour){//LUNES 
					checkNonWorkingDay(date, mondayHour);
					composite.put(date, mondayHour);
				}
			}
			calendarEmployeeInfo.setHourByDay(composite);
		}
		cleanSelectStyleSelectedDates(selectedDates.getSelectedList());
		selectedDates.getSelectedList().clear();
	}
	
	private void checkNonWorkingDay(Date date, Double hora) {
		//if(hora == Double.parseDouble("-1")){
		if(hora == null){
			calendarEmployeeInfo.setTypeByDay(date, DayType.NOWORKINGDAY);
		}
		//if(hora != Double.parseDouble("-1")){
		if(hora != null){
			Integer pos = calculateDatePosition(date);
			if( cellsType[calculatePositionRow(pos)][calculatePositionCol(pos)] != null &&
				cellsType[calculatePositionRow(pos)][calculatePositionCol(pos)].getType().equals(DayType.NOWORKINGDAY))
				calendarEmployeeInfo.setTypeByDay(date, DayType.NOTYPEDAY);
		}
	}
	
	private void hideHours() {
		for (int i = 2; i < 25; i += 2) {
			calendarGrid.getRowFormatter().addStyleName(i, style.ocultarHorasStyle());
		}
	}

	private void showHours() {
		for (int i = 2; i < 25; i += 2) {
			calendarGrid.getRowFormatter().removeStyleName(i, style.ocultarHorasStyle());
		}
	}
	
	private void applyDayTypeSelectedDates(DayType dayType) {
		cleanStyles(selectedDates.getSelectedList());
		if(selectedDates.getSelectedList().size()==1){
			for (Date date : selectedDates.getSelectedList()) {
				if(isWeekEnd(date) && dayType.equals(DayType.HOLIDAY)){	
					int pos = calculateDatePosition(date);
					if(pos != -1){
						int column = calculatePositionCol(pos);
						int row = calculatePositionRow(pos);
						cells[row][column].unSelect(row, column);
						cellsType[row][column].setAsType(dayType, row, column);
						calendarEmployeeInfo.setTypeByDay(cellsDates[row][column], dayType);
					}
				}else if(!isWeekEnd(date)){
					int pos = calculateDatePosition(date);
					if(pos != -1){
						int column = calculatePositionCol(pos);
						int row = calculatePositionRow(pos);
						cells[row][column].unSelect(row, column);
						cellsType[row][column].setAsType(dayType, row, column);
						calendarEmployeeInfo.setTypeByDay(cellsDates[row][column], dayType);
					}
				}
			}	
		}else{
			HashMap<Date, DayType> composite = new HashMap<Date, DayType>();
			for (Date date : selectedDates.getSelectedList()) {
				if(isWeekEnd(date) && dayType.equals(DayType.HOLIDAY)){
					int pos = calculateDatePosition(date);
					if(pos != -1){
						int column = calculatePositionCol(pos);
						int row = calculatePositionRow(pos);
						cells[row][column].unSelect(row, column);
						cellsType[row][column].setAsType(dayType, row, column);
						composite.put(date, dayType);
					}
				}else if(!isWeekEnd(date)){
					int pos = calculateDatePosition(date);
					if(pos != -1){
						int column = calculatePositionCol(pos);
						int row = calculatePositionRow(pos);
						cells[row][column].unSelect(row, column);
						cellsType[row][column].setAsType(dayType, row, column);
						composite.put(date, dayType);
					}
				}
			}
			calendarEmployeeInfo.setTypeByDay(composite);
		}
		selectedDates.clear();
		
	}
	
	private boolean isWeekEnd(Date date) {
		return date.getDay() == 0 || date.getDay() == 6;
	}

	private void applyNonWorkingDayTypeSelectedDates(Double hour, DayType nonWorkingDay) {
		cleanStyles(selectedDates.getSelectedList());
		List<Date> nonWorkingDatesList = new LinkedList<Date>();
		
		for (Date date : selectedDates.getSelectedList()) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int column = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][column].unSelect(row, column);
				cellsType[row][column].setAsType(nonWorkingDay, row, column);
				nonWorkingDatesList.add(cellsDates[row][column]);
			}
		}
		calendarEmployeeInfo.setNonWorkingDays(nonWorkingDatesList, nonWorkingDay, hour);
		selectedDates.clear();
	}
	
	private void applyEREDayTypeSelectedDates(double ce, DayType ereday) {
		cleanStyles(selectedDates.getSelectedList());
		List<Date> ereDatesList = new LinkedList<Date>();
		
		for (Date date : selectedDates.getSelectedList()) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int column = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][column].unSelect(row, column);
				cellsType[row][column].setAsType(ereday, row, column);
				ereDatesList.add(cellsDates[row][column]);
			}
		}
		calendarEmployeeInfo.setEreCoefficientDays(ereDatesList, ereday, ce);
		selectedDates.clear();
	}
	
	private void applyDropDayTypeSelectedDates(double ce, DayType dropday) {
		cleanStyles(selectedDates.getSelectedList());
		List<Date> dropDatesList = new LinkedList<Date>();
		
		for (Date date : selectedDates.getSelectedList()) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int column = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][column].unSelect(row, column);
				cellsType[row][column].setAsType(dropday, row, column);
				dropDatesList.add(cellsDates[row][column]);
			}
		}
		calendarEmployeeInfo.setDropCoefficientDays(dropDatesList, dropday, ce);
		selectedDates.clear();
	}
	
	private void applyInactivityDaySelectedDates(DayType inactivity, String typeInactivity/*, double cs*/) {
		cleanStyles(selectedDates.getSelectedList());
		List<Date> dates = new LinkedList<Date>();
		
		for (Date date : selectedDates.getSelectedList()) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int column = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][column].unSelect(row, column);
				cellsType[row][column].setAsType(inactivity, row, column);
				dates.add(cellsDates[row][column]);
			}
		}
		calendarEmployeeInfo.setInactiveDays(dates, inactivity, typeInactivity);
//		calendarEmployeeInfo.setInactivityCoefficientDays(dates, inactivity, typeInactivity, cs);
		selectedDates.clear();
		
	}
	

	private void applyPartialityDaySelectedDate(DayType partiality, Double partialityCoeficient) {
		cleanStyles(selectedDates.getSelectedList());
		List<Date> dates = new LinkedList<Date>();
		
		for (Date date : selectedDates.getSelectedList()) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int column = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][column].unSelect(row, column);
				cellsType[row][column].setAsType(partiality, row, column);
				calendarGrid.getWidget(row, column).setTitle("Parcialidad : " + partialityCoeficient);
				dates.add(cellsDates[row][column]);
			}
		}
		
		calendarEmployeeInfo.setPartialityDays(dates, partiality, partialityCoeficient);
		selectedDates.clear();
	}
	
	private void applyStrikeDayTypeSelectedDates(double cs, DayType strikeDay) {
		cleanStyles(selectedDates.getSelectedList());
		List<Date> strikeDatesList = new LinkedList<Date>();
		
		for (Date date : selectedDates.getSelectedList()) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int column = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][column].unSelect(row, column);
				cellsType[row][column].setAsType(strikeDay, row, column);
				strikeDatesList.add(cellsDates[row][column]);
			}
		}
		calendarEmployeeInfo.setStrikeCoefficientDays(strikeDatesList, strikeDay, cs);
		selectedDates.clear();
	}
	
	private void changeYear(int change) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + change;
		yearLabel.setText(Integer.toString(newYear));
		
		int parseNewYear = newYear - 1900;
		this.year = parseNewYear;
		month = 0;
		
		cleanStyleChanges();
		cleanCalendar();
		initializeCellsCalendar();
		initializeCellsTypeCalendar();
		showCalendarWidget(this.year);
		
		paintMadeHourChanges(calendarEmployeeInfo.getChangesHours());
		paintMadeTypeChanges(calendarEmployeeInfo.getChangesTypes());
		paintMadeExtraHoursChanges(calendarEmployeeInfo.getChangesExtraHours());
		
	}
	
	private void blockBeforeOutOfContractDays(Date startDateContract) {
		for (int row = 1; row < 25; row+=2)
			for (int column = 1; column < 39; column++){
				if (null != cellsDates[row][column] && 
						(startDateContract.after(cellsDates[row][column]))){
					calendarGrid.getCellFormatter().addStyleName(row, column, style.setOutOfContractStyle());
					calendarGrid.getCellFormatter().addStyleName(row+1, column, style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).addStyleName(style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).setVisible(false);
					cleanOneDayStyle(row, column);
				}
			}	
	}
	
	private void blockAfterOutOfContractDays(Date endDateContract) {
		for (int row = 1; row < 25; row+=2)
			for (int column = 1; column < 39; column++){
				if (null != cellsDates[row][column] && 
						(endDateContract.before(cellsDates[row][column]))){
					calendarGrid.getCellFormatter().addStyleName(row, column, style.setOutOfContractStyle());
					calendarGrid.getCellFormatter().addStyleName(row+1, column, style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).addStyleName(style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).setVisible(false);
					cleanOneDayStyle(row, column);
				}
			}	
	}

	@SuppressWarnings("deprecation")
	private Integer calculateDatePosition(Date date){
		int row = calculateMonthRow(date.getMonth());
		int pos = -1;
		for(int col = 1; col < 39; col++){
			if(date.equals(cellsDates[row][col])){
				pos = (row * 39) + col;
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
	
	private void paintSelectedDates(List<Date> selectedDatesList) {
		for(Date date : selectedDatesList){
			int pos = calculateDatePosition(date);
			if (pos != -1){
				int col = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				//if (!(cellsType[row][col].getType().equals(DayType.BAJAIT) || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY)))
				if (!(cellsType[row][col].getType().equals(DayType.BAJAIT) || cellsType[row][col].getType().equals(DayType.FREEDAY)))
					cells[row][col].select(row, col);
				else
					if(selectedDates.isSelected(date))
						selectedDates.setSelected(DateUtils.copyDateOnly(date), false);	
			}
		}	
	}
	
	private void paintAllDates(List<Date> selectedDatesList) {
		for(Date date : selectedDatesList){
			int pos = calculateDatePosition(date);
			if (pos != -1){
				int col = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				cells[row][col].select(row, col);
			}
		}	
	}
	
	// ------------------------------------------------------------- GESTION EVENTOS ---------------------------------------------------------------------
	
	private void addHolidays() {
		applyDayTypeSelectedDates(DayType.HOLIDAY);
	}
	
	private void addPeonadas() {
		applyDayTypeSelectedDates(DayType.PEONADAS);
	}
	
	private void addNoWorkingDay() {
		//applyNonWorkingDayTypeSelectedDates(-1.00, DayType.NOWORKINGDAY);
		applyNonWorkingDayTypeSelectedDates(null, DayType.NOWORKINGDAY);
	}
	private void addStrikeDay(double cs) {
		applyStrikeDayTypeSelectedDates(cs, DayType.STRIKEDAY);
	}
	private void addEreDay(double ce) {
		applyEREDayTypeSelectedDates(ce, DayType.EREDAY);
	}
	private void addInactivityDays(String typeInactivity) {
		applyInactivityDaySelectedDates(DayType.INACTIVITY, typeInactivity);
	}
	private void addPartialityDays(Double partialityCoeficient) {
		applyPartialityDaySelectedDate(DayType.PARTIALITY, partialityCoeficient);
	}

//	private void addInactivityDays(String typeInactivity, double ce) {
//		applyInactivityDaySelectedDates(DayType.INACTIVITY, typeInactivity, ce);
//	}
	
//	private void addDropDay() {
//		applyDayTypeSelectedDates(DayType.DROPDAY);
//	}
	
	private void addDropDay(double ce) {
		applyDropDayTypeSelectedDates(ce, DayType.DROPDAY);
	}

	private void cleanSelectedDates() {
		if(!selectedDates.getSelectedList().isEmpty())
			setWorkingStyles(selectedDates.getSelectedList());
		
		hourMenuItem.setEnabled(false);
		hourButton.setEnabled(false);
		selectedDates.clear();
		changeYear(0);
	}

	private void setWorkingStyles(List<Date> selectedList) {
		HashMap<Date, Double> compositeH = new HashMap<Date, Double>();
		HashMap<Date, DayType> compositeT = new HashMap<Date, DayType>();
		for (Date date : selectedList) {
			int pos = calculateDatePosition(date);
			if(pos != -1){
				int col = calculatePositionCol(pos);
				int row = calculatePositionRow(pos);
				calendarGrid.getWidget(row, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.sundayStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.holidayStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.dropStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.strikeStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.ereStyle());
				calendarGrid.getWidget(row, col).removeStyleName(style.nonWorkingStyle());
				if(cellsType[row][col].getType().equals(DayType.FREEDAY)) {
					calendarEmployeeInfo.setFestiveWorking(date);
				}
				cellsType[row][col].setAsType(DayType.NOTYPEDAY, row, col);
				Double hour = calendarEmployeeInfo.getHourByDay(date);
				//if (-1 == hour)
				if (null == hour)
					hour = 0.0;
				compositeH.put(date, hour);
				compositeT.put(date, DayType.NOTYPEDAY);
			}
		}
		calendarEmployeeInfo.setWorkingDays(compositeH, compositeT);
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if(!selectedDates.getSelectedList().isEmpty()){
			ContextMenu menu = new  ContextMenu();
			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) no laborables", new Command() {
				@Override
				public void execute() {
					addNoWorkingDay();
				}
			});
			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) vacaciones", new Command() {
				@Override
				public void execute() {
					addHolidays();
				}
			});
			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) huelga", new Command() {
				@Override
				public void execute() {
					EmployeeCalendarPercentDialog strikeDialog;
					if(fullTimeJourney){
						strikeDialog = new EmployeeCalendarPercentDialog("Horas Huelga", "8") {
							
							@Override
							protected void onAccept() {
								double cs = this.getPercentValue();
								addStrikeDay(cs);
							}
						};
					}
					else{
						Date date = selectedDates.getSelectedList().get(0);
						Double hours = calendarEmployeeInfo.getHourByDay(date);
						strikeDialog = new EmployeeCalendarPercentDialog("Horas Huelga", hours.toString()) {
							
							@Override
							protected void onAccept() {
								double cs = this.getPercentValue();
								addStrikeDay(cs);
							}
						};
					}
//					strikeDialog.setLabelText("Horas Huelga:");
					strikeDialog.show();
					strikeDialog.center();	
				}
			});
			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) ERE", new Command() {
				@Override
				public void execute() {
					EmployeeCalendarPercentDialog ereDialog;
					if(fullTimeJourney){
						ereDialog = new EmployeeCalendarPercentDialog("Horas ERE", "8") {
							
							@Override
							protected void onAccept() {
								double cs = this.getPercentValue();
								addEreDay(cs);
							}
						};
					}else{
						Date date = selectedDates.getSelectedList().get(0);
						Double hours = calendarEmployeeInfo.getHourByDay(date);
						ereDialog = new EmployeeCalendarPercentDialog("Horas Huelga", hours.toString()) {
							
							@Override
							protected void onAccept() {
								double cs = this.getPercentValue();
								addEreDay(cs);
							}
						};
					}
//					ereDialog.setLabelText("Horas ERE:");
					ereDialog.show();
					ereDialog.center();
				}
			});
			menu.addSeparator();
			menu.addItem("Borrar evento(s)", new Command() {
				@Override
				public void execute() {
					cleanSelectedDates();
				}
			});
			
			menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		    menu.show();
		}
	}
	
	
	private void hideElement(Widget widget){
		widget.addStyleName(style.hide());
	}
	
	private void showElement(Widget widget){
		widget.removeStyleName(style.hide());
	}
}
