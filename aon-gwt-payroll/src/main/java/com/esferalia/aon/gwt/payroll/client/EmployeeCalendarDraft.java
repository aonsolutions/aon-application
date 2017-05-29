package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogBar;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayTypeVisitor;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;
import com.vaadin.polymer.iron.widget.IronLabel;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperDialog;
import com.vaadin.polymer.paper.widget.PaperIconButton;

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
			if (cellsDates[row][col] != null)
				calendarGrid.getWidget(row, col).removeStyleName(style.isSelectedStyle());	
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
		String doubleBoxStyle();

		String doubleBoxLargeStyle();

		String doubleBoxDisableStyle();

		String doubleBoxDisableStyle2();
		
		String isSelectedStyle();
		
		String nonWorkingStyle();
		
		String sundayStyle();

		String holidayStyle();
		
		String ereStyle();

		String strikeStyle();
		
		String reductionStyle();
		
		String dropStyle();
		
		String suspensionStyle();
		
		String itStyle();
		
		String cellStyle();

		String ocultarHorasStyle();
		
		String ocultarDivStyle();
		
		String onChange();
		
		String setOutOfContractStyle();	
		
		String fechaDialogZIndex();
		
		String  pointer();
	}

	@UiField
	Grid calendarGrid;
	
	@UiField
	MenuItem nonWorkingDayMenuItem;
	
	@UiField
	MenuItem holidayDayMenuItem;
	
	@UiField
	MenuItem strikeDayMenuItem;
	
	@UiField
	MenuItem ereDayMenuItem;
	
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
	PaperDialog dialogUntill;
	
	@UiField
	CustomDialogBar customDialogBarUntill;
	
	@UiField
	DateBoxEx endDateBoxDialogUntill;
	
	@UiField
	PaperButton dialogUntillOk;
	
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
	Label yearLabel;

	@UiField
	Button lastYearButton;

	@UiField
	Button nextYearButton;

	@UiField
	PaperButton nonWorkingDayButton;

	@UiField
	PaperButton festiveDayButton;
	
	@UiField
	PaperButton dropDayButton;
	
	@UiField
	PaperDialog dropDialog;
	
	@UiField
	ListBox dropMenu;
	
	@UiField
	PaperButton dropDialogOk;
	
	@UiField
	PaperButton strikeDayButton;

	@UiField
	PaperDialog strikeDialog;
	
	@UiField
	DoubleBox strikePercentBox;
	
	@UiField
	PaperButton strikeDialogOk;
	
	@UiField
	PaperButton ereDayButton;
	
	@UiField
	PaperDialog ereDialog;
	
	@UiField
	DoubleBox erePercentBox;
	
	@UiField
	PaperButton ereDialogOk;

	@UiField
	PaperButton reductionDayButton;

	@UiField
	PaperButton holidayDayButton;

	@UiField
	PaperButton suspensionDayButton;
	
	@UiField
	PaperIconButton leyendButton;
	
	@UiField
	PaperDialog dialogLeyend;
	
	@UiField
	CustomDialogBar customDialogBarLeyend;

	@UiField
	PaperButton dialogLeyendOk;
	
	@UiField
	PaperIconButton eraseEventButton;
	
	@UiField
	PaperIconButton hourButton;
	
	@UiField
	PaperDialog hourDialog;
	
	@UiField
	CustomDialogBar customDialogBarHour;
	
	@UiField
	PaperButton hourDialogOk;
	
	@UiField
	PaperIconButton expandHourBtnL;
	
	@UiField
	PaperIconButton expandHourBtnM;
	
	@UiField
	PaperIconButton expandHourBtnX;
	
	@UiField
	PaperIconButton expandHourBtnJ;
	
	@UiField
	PaperIconButton expandHourBtnV;
	
	@UiField
	PaperIconButton expandHourBtnS;
	
	@UiField
	PaperIconButton expandHourBtnD;
	
	@UiField
	IronLabel hourButtonBlock;
	
	@UiField
	HTMLPanel mondayBlock;
	
	@UiField
	HTMLPanel tuesdayBlock;
	
	@UiField
	HTMLPanel wendsdayBlock;
	
	@UiField
	HTMLPanel thursdayBlock;
	
	@UiField
	HTMLPanel fridayBlock;
	
	@UiField
	HTMLPanel saturdayBlock;
	
	@UiField
	HTMLPanel sundayBlock;

	@UiField(provided = true)
	SuggestBox mondayOpt;
	
	@UiField(provided = true)
	SuggestBox tuesdayOpt;
	
	@UiField(provided = true)
	SuggestBox wendsdayOpt;
	
	@UiField(provided = true)
	SuggestBox thursdayOpt;
	
	@UiField(provided = true)
	SuggestBox fridayOpt;
	
	@UiField(provided = true)
	SuggestBox saturdayOpt;
	
	@UiField(provided = true)
	SuggestBox sundayOpt;
	
	@UiField
	Label totalHours;
	
	@UiField
	ScrollPanel scrollInfo;
	
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
	
	private final static int MONDAY = 0;
	private final static int TUESDAY = 1;
	private final static int WEDNESDAY = 2;
	private final static int THURSDAY = 3;
	private final static int FRIDAY = 4;
	private final static int SATURDAY = 5;
	private final static int SUNDAY = 6;
	
	private OrderedMultiSelectionModel<Date> selectedDates = new OrderedMultiSelectionModel<Date>();
	
	private int oldHourSelected = 0;
	private int month;
	private int year;
	private boolean showHours;
	private boolean fullTimeJourney;
	
	private final CalendarTypeCell cells[][] = new CalendarTypeCell[25][38];
	private final CalendarTypeDayCell cellsType[][] = new CalendarTypeDayCell[25][38];
	private final Date cellsDates[][] = new Date[25][38];
	
	private final SuggestBox suggestOpts[] = new SuggestBox[7];
	private final HTMLPanel divDays[] = new HTMLPanel[7];
	
	private double listOldHours[] = new double[7];
	
	private EmployeeCalendarDraftObjectData calendarEmployeeInfo;
	private Integer startEmployeeContract;
	private Integer endEmployeeContract;
	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	public EmployeeCalendarDraft() {
		
		//Inicializamos todos los SuggestBox para insertar horas nuevas
		ArrayList<String> suggestHours = new ArrayList<String>();
		MultiWordSuggestOracle oracleL = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleM = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleX = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleJ = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleV = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleS = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleD = new MultiWordSuggestOracle();
		suggestHours.add("2");
		suggestHours.add("4");
		suggestHours.add("6");
		suggestHours.add("8");
		oracleL.setDefaultSuggestionsFromText(suggestHours);
		oracleM.setDefaultSuggestionsFromText(suggestHours);
		oracleX.setDefaultSuggestionsFromText(suggestHours);
		oracleJ.setDefaultSuggestionsFromText(suggestHours);
		oracleV.setDefaultSuggestionsFromText(suggestHours);
		oracleS.setDefaultSuggestionsFromText(suggestHours);
		oracleD.setDefaultSuggestionsFromText(suggestHours);
		
		suggestOpts[MONDAY] = new SuggestBox(oracleL);
		suggestOpts[TUESDAY] = new SuggestBox(oracleM);
		suggestOpts[WEDNESDAY] = new SuggestBox(oracleX);
		suggestOpts[THURSDAY] = new SuggestBox(oracleJ);
		suggestOpts[FRIDAY] = new SuggestBox(oracleV);
		suggestOpts[SATURDAY] = new SuggestBox(oracleS);
		suggestOpts[SUNDAY] = new SuggestBox(oracleD);
		
		this.mondayOpt = suggestOpts[MONDAY];
		this.tuesdayOpt = suggestOpts[TUESDAY];
		this.wendsdayOpt = suggestOpts[WEDNESDAY];
		this.thursdayOpt = suggestOpts[THURSDAY];
		this.fridayOpt = suggestOpts[FRIDAY];
		this.saturdayOpt = suggestOpts[SATURDAY];
		this.sundayOpt = suggestOpts[SUNDAY];
		
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		calendarGrid.addDomHandler(this, ContextMenuEvent.getType());
		
		divDays[0] = mondayBlock;
		divDays[1] = tuesdayBlock;
		divDays[2] = wendsdayBlock;
		divDays[3] = thursdayBlock;
		divDays[4] = fridayBlock;
		divDays[5] = saturdayBlock;
		divDays[6] = sundayBlock;
		
		initializeCellsCalendar();
		initializeCellsTypeCalendar();
		
		//Gestion tipo de ausencia
		dropMenu.addItem("No remunerado");
		dropMenu.addItem("Remunerado");
		
		//Gestion boton horas
		hourMenuItem.setEnabled(false);
		hourButton.setDisabled(true);
		
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
		
		strikeDayMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				strikeDialog.open();	
			}
			
		});
		
		ereDayMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				ereDialog.open();
			}
			
		});
		
//		diaAusencia.setScheduledCommand(new Command() {
//			
//			@Override
//			public void execute() {
//				dropDialog.open();
//			}
//			
//		});
		
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
				hourButton.setDisabled(false);
			}
			
		});
		
		selectUntillMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				cleanCalendarSelectedDates();
				dialogUntill.open();
				addDates();
				
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
				hourButton.setDisabled(false);
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
				hourButton.setDisabled(false);
			}
		});
		
		endDateBoxDialogUntill.getTextBox().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				endDateBoxDialogUntill.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
				
			}
		});
	
		hourMenuItem.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				if(!selectedDates.getSelectedList().isEmpty()){
					hourDialog.open();
					checkShowingUpDays();
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
		
		customDialogBarHour.addCloseHandler(()->{hourDialog.close();});
		customDialogBarUntill.addCloseHandler(()->{dialogUntill.close();});
		customDialogBarLeyend.addCloseHandler(()->{dialogLeyend.close();});
		
		//#ifndef env.SNAPSHOT
		saveButton.setVisible(false);
		//#endif
		
		//TODO: para probar el boton de guardar del calendario -> saveButton.setVisible(true);
		//saveButton.setVisible(true);
	}

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("dialogUntillOk")
	public void onOkuntillDialogClick(ClickEvent event) {
		if(selectedDates.getSelectedList().size() == 1){
			Date startDate = DateUtils.copyDateOnly(selectedDates.getSelectedList().get(0));
			Date endDate = DateUtils.copyDateOnly(endDateBoxDialogUntill.getValue());
			while (startDate.before(endDate) || startDate.equals(endDate)) {
				selectedDates.setSelected(DateUtils.copyDateOnly(startDate), true);
				DateUtils.addDays2Date(startDate, 1);
			}
		}
		paintSelectedDates(selectedDates.getSelectedList());
	}
	
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		
		event.preventDefault();
		
		hourMenuItem.setEnabled(true);
		hourButton.setDisabled(false);
		
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * 39) + col;
		
		if (null != cellsDates[row][col] && calendarEmployeeInfo.getStartDateContract().after(cellsDates[row][col]))
			return;
		
		//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY))
		if (cellsType[row][col].getType().equals(DayType.BAJAIT) || cellsType[row][col].getType().equals(DayType.FREEDAY))
			return;
	
		//Pulsacion celda con CTRL
		if (event.isControlKeyDown()) { 
			//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY))
			if (!(cellsType[row][col].getType().equals(DayType.BAJAIT) || cellsType[row][col].getType().equals(DayType.FREEDAY)))
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
							|| cellsType[calculatePositionRow(initialPosition+1)][calculatePositionCol(initialPosition+1)].getType().equals(DayType.FREEDAY))){
						cells[calculatePositionRow(initialPosition+1)][calculatePositionCol(initialPosition+1)]
								.select(calculatePositionRow(initialPosition+1), calculatePositionCol(initialPosition+1));
					}
					initialPosition++;	
				}
			}
		
		//Pulsacion una sola celda	
		} else { 
//			for (Date date : selectedDates.getSelectedList()) {
//				int posicion = calculateDatePosition(date);
//				if(posicion != -1){
//					int colSelect = calculatePositionCol(posicion);
//					int filSelect = calculatePositionRow(posicion);
//					cells[filSelect][colSelect].unSelect(filSelect, colSelect);
//			
//				}
//			}
//			
//			selectedDates.clear();
			
			cleanCalendarSelectedDates();
			
			cells[calculatePositionRow(oldHourSelected)][calculatePositionCol(oldHourSelected)]
					.unSelect(calculatePositionRow(oldHourSelected), calculatePositionCol(oldHourSelected));
			
			if (isMonth(row, col)){
				for(int i = 1; i<38; i++)
					//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][i].getType().equals(DayType.NOWORKINGDAY))
					if (!(cellsType[row][i].getType().equals(DayType.BAJAIT) || cellsType[row][i].getType().equals(DayType.FREEDAY)))
						cells[row][i].select(row, i);
			}else
				//TODO: poner este codigo para bloquear no laborables -> || cellsType[row][col].getType().equals(DayType.NOWORKINGDAY))
				if (!(cellsType[row][col].getType().equals(DayType.BAJAIT) || cellsType[row][col].getType().equals(DayType.FREEDAY)))
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

	@UiHandler("hourDialogOk")
	public void onConfirmDialogClick(ClickEvent event) {
		double mondayHour = Double.parseDouble(suggestOpts[MONDAY].getValue());
		double tuesdayHour = Double.parseDouble(suggestOpts[TUESDAY].getValue());
		double wendsdayHour = Double.parseDouble(suggestOpts[WEDNESDAY].getValue());
		double thursdayHour = Double.parseDouble(suggestOpts[THURSDAY].getValue());
		double fridayHour = Double.parseDouble(suggestOpts[FRIDAY].getValue());
		double saturdayHour = Double.parseDouble(suggestOpts[SATURDAY].getValue());
		double sundayHour = Double.parseDouble(suggestOpts[SUNDAY].getValue());
		
		updateHours(mondayHour, tuesdayHour, wendsdayHour, thursdayHour, fridayHour, saturdayHour, sundayHour);
		hourDialog.close();
		hourMenuItem.setEnabled(false);
		hourButton.setDisabled(true);
		selectedDates.clear();
		changeYear(0);
	}
	
	@UiHandler("dialogLeyendOk")
	public void onConfirmInfoDialogClick(ClickEvent event) {
		dialogLeyend.close();
	}
	
	@UiHandler("expandHourBtnL")
	public void onExpandHourLClick(ClickEvent event) {
		suggestOpts[MONDAY].showSuggestionList();	
	}
	
	@UiHandler("expandHourBtnM")
	public void onExpandHourMClick(ClickEvent event) {
		suggestOpts[TUESDAY].showSuggestionList();	
	}
	
	@UiHandler("expandHourBtnX")
	public void onExpandHourXClick(ClickEvent event) {
		suggestOpts[WEDNESDAY].showSuggestionList();	
	}
	
	@UiHandler("expandHourBtnJ")
	public void onExpandHourJClick(ClickEvent event) {
		suggestOpts[THURSDAY].showSuggestionList();	
	}
	
	@UiHandler("expandHourBtnV")
	public void onExpandHourVClick(ClickEvent event) {
		suggestOpts[FRIDAY].showSuggestionList();	
	}
	
	@UiHandler("expandHourBtnS")
	public void onExpandHourSClick(ClickEvent event) {
		suggestOpts[SATURDAY].showSuggestionList();	
	}
	
	@UiHandler("expandHourBtnD")
	public void onExpandHourDClick(ClickEvent event) {
		suggestOpts[SUNDAY].showSuggestionList();	
	}
	
	@UiHandler("calendarGrid")
	public void onDragStart(DragStartEvent event) {
		//TODO: ver como hacer el drag con el raton en vez de con shift
	}
	
	@UiHandler("eraseEventButton")
	public void onEraseClick(ClickEvent event) {
		cleanSelectedDates();
	}

	@UiHandler("nonWorkingDayButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		addNoWorkingDay();	
	}

	@UiHandler("festiveDayButton")
	public void onDiaFestivoClick(ClickEvent event) {
		//addFreeDay();	
	}

	@UiHandler("holidayDayButton")
	public void onVacacionesClick(ClickEvent event) {
		addHolidays();
	}
	
	@UiHandler("strikeDayButton")
	public void onStrikeClick(ClickEvent event) {
		//strikeDialog.open();
		addStrikeDay(0);
	}
	
	@UiHandler("strikeDialogOk")
	public void onDialogStrikeClick(ClickEvent event) {
		double cs = Double.parseDouble(strikePercentBox.getText());
		//Window.alert("Porcentaje Huelga :"+cs);
		strikeDialog.close();
		addStrikeDay(cs);
	}
	
	@UiHandler("ereDayButton")
	public void onEreClick(ClickEvent event) {
		ereDialog.open();
	}
	
	@UiHandler("ereDialogOk")
	public void onDialogEreaClick(ClickEvent event) {
		double ce = Double.parseDouble(erePercentBox.getText());
		ereDialog.close();
		addEreDay(ce);
	}
	
	@UiHandler("dropDayButton")
	public void onDiaAusenciaClick(ClickEvent event) {
		//dropDialog.open();
	}

	@UiHandler("dropDialogOk")
	public void ondropDialogClick(ClickEvent event) {
		//TODO: mirar que hacer con el tipo de ausencia
		//dropMenu.getSelectedItemText();
		dropDialog.close();	
		addDropDay();
	}
	
	
	@UiHandler("reductionDayButton")
	public void onReduccionClick(ClickEvent event) {
		//aplicarEstilosDiasSeleccionadios(DayType.REDUCTIONDAY);
	}

	@UiHandler("suspensionDayButton")
	public void onSuspensionClick(ClickEvent event) {
		//aplicarEstilosDiasSeleccionadios(DayType.SUSPENSIONDAY);
	}
	
	@UiHandler("hourButton")
	public void onHourClick(ClickEvent event) {
		if(!selectedDates.getSelectedList().isEmpty()){	
			hourDialog.open();
			checkShowingUpDays();
		}
	}
	
	@UiHandler("leyendButton")
	public void oninfoClick(ClickEvent event) {
		dialogLeyend.open();
		dialogLeyend.setPositionTarget("center");
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
		
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		calendarEmployeeInfo.updateDBCalendar(r -> 
		{
			setEmployeeCalendarDraftObject(calendarEmployeeInfo);
			calendarEmployeeInfo.undoManager.discardAll();
		}, t -> {});
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
			hourButtonBlock.setVisible(false);
			viewMenuItem.setVisible(false);
		}else{
			hourMenuItem.setVisible(true);
			hourButtonBlock.setVisible(true);
			viewMenuItem.setVisible(true);
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
				calendarGrid.setWidget(row, i, labelDay);
				cells[row + 1][i] = new NoneCell();
			} else {
				@SuppressWarnings("deprecation")
				Date actualDay = new Date(year, month, contDays);
				
				DateUtils.resetTime(actualDay);
				DayType dayType = calendarEmployeeInfo.getTypeByDay(actualDay);
				TextBox textHour = new TextBox();
				DoubleBox doubleHour = new DoubleBox();
				
				if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
					textHour.setValue("-");
				}else{
					doubleHour.setEnabled(false);
					double hourByDay = calendarEmployeeInfo.getHourByDay(actualDay);
					doubleHour.setValue(hourByDay);
					monthHours += hourByDay;
					doubleHour.setEnabled(false);
				}
				
				if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
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
				
				if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
					calendarGrid.setWidget(row + 1, i, textHour);
				}else{
					calendarGrid.setWidget(row + 1, i, doubleHour);
				}
				
				cellsDates[row][i] = actualDay;
				cells[row][i] = new DayCell();
				cells[row + 1][i] = new HourCell();
				
				if (DayType.BAJAIT == dayType || DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
					calendarGrid.getCellFormatter().addStyleName(row+1, i, style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, i).addStyleName(style.setOutOfContractStyle());
				}
				
				if(DayType.FREEDAY == dayType){
					calendarGrid.getWidget(row, i).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
					calendarGrid.getWidget(row+1, i).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
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
			labelDay.setStyleName(style.cellStyle());
			labelDay.addStyleName(style.pointer());
			
			DayType dayType = calendarEmployeeInfo.getTypeByDay(actualDay);
			
			TextBox textHour = new TextBox();
			DoubleBox doubleHour = new DoubleBox();
			
			if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				textHour.setValue("-");
			}else{
				doubleHour.setEnabled(false);
				double hourByDay = calendarEmployeeInfo.getHourByDay(actualDay);
				doubleHour.setValue(hourByDay);
				monthHours += hourByDay;
				doubleHour.setEnabled(false);
			}
			
			if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
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
			
			if (DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				calendarGrid.setWidget(row + 1, 7 + actualDayOfWeek, textHour);
			}else{
				calendarGrid.setWidget(row + 1, 7 + actualDayOfWeek, doubleHour);
			}
			
			cellsDates[row][7 + actualDayOfWeek] = actualDay;
			cells[row][7 + actualDayOfWeek] = new DayCell();
			cells[row + 1][7 + actualDayOfWeek] = new HourCell();
			
			if (DayType.BAJAIT == dayType || DayType.NOWORKINGDAY == dayType || calendarEmployeeInfo.getHourByDay(actualDay)==-1.0){
				calendarGrid.getCellFormatter().addStyleName(row+1, 7 + actualDayOfWeek, style.setOutOfContractStyle());
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).addStyleName(style.setOutOfContractStyle());
			}
			
			if(DayType.FREEDAY == dayType){
				calendarGrid.getWidget(row, 7 + actualDayOfWeek).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
				calendarGrid.getWidget(row+1, 7 + actualDayOfWeek).setTitle(calendarEmployeeInfo.getDescriptionFestive(actualDay));
			}
			
			cellsType[row][7 + actualDayOfWeek].setAsType(dayType, row, (7 + actualDayOfWeek));
			contDays++;
			actualDayOfWeek++;
			
		}

		if ((actualDayOfWeek) < 30) {
			for (int i = 7 + actualDayOfWeek; i < 38; i++) {
				Label labelDay = new Label();
				labelDay.setText("");
				labelDay.setStyleName(style.cellStyle());
				calendarGrid.setWidget(row, i, labelDay);
				cells[row][i] = new NoneCell();
			}
		}
		
		//Poner horas totales mensuales en la ultima columna
		if(0 != monthHours)
			setMonthHours(row, monthHours);
		
		month++;
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
		for (int i = 1; i < 25; i++)
			for (int j = 1; j < 39; j++){
				cells[i][j].eraseOnChange(i, j);
				if (null != calendarGrid.getWidget(i, j))
					calendarGrid.getCellFormatter().removeStyleName(i, j, style.setOutOfContractStyle());
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
	
	private void updateHours(double mondayHour, double tuesdayHour, double wendsdayHour, double thursdayHour, double fridayHour, 
			double saturdayHour, double sundayHour) {

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
	
	private void checkNonWorkingDay(Date date, double hora) {
		if(hora == Double.parseDouble("-1")){
			calendarEmployeeInfo.setTypeByDay(date, DayType.NOWORKINGDAY);
		}	
	}

	private void checkShowingUpDays() {
		//Ocultar todos los dias
		for (int i=0; i<7; i++)
			divDays[i].addStyleName(style.ocultarDivStyle());
		
		//Gestionar los dias seleccionados (mostrar y actualizar valor)
		for (int i = 0; i < 7; i++){
			final int c =i; 
			try{
				
				Double minValue = Double.MIN_VALUE;
				int days [] = {1,2,3,4,5,6,0};
				
				@SuppressWarnings("deprecation")
				Double value = selectedDates.getSelectedList().stream()
						.filter(d -> d.getDay() == days[c])
						.map(d-> calendarEmployeeInfo.getHourByDay(d))
						.peek(p -> divDays[c].removeStyleName(style.ocultarDivStyle()))
						.collect(Collectors.reducing(Double.MIN_VALUE,(h1,h2) -> minValue.equals(h1) || h2.equals(h1) ? h2: null ))
						;

				
				if(value != null && value != Double.MAX_VALUE){
					suggestOpts[c].setValue(value+"");
					listOldHours[c] = value;
				}else{
					suggestOpts[c].setValue("");
					listOldHours[c] = -1;
				}
				
			} catch (Exception e) {
				Window.alert("Fallo!"+", "+c + "," + e.getMessage());
			}
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

	private void applyNonWorkingDayTypeSelectedDates(double hour, DayType nonWorkingDay) {
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
	
	private void addDates() {
		//Date endDate = fechaMax(selectedDates.getSelectedList());
		this.endDateBoxDialogUntill.setValue(new Date());
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
	private void addNoWorkingDay() {
		applyNonWorkingDayTypeSelectedDates(-1.00, DayType.NOWORKINGDAY);
	}
	private void addStrikeDay(double cs) {
		applyStrikeDayTypeSelectedDates(cs, DayType.STRIKEDAY);
	}
	private void addEreDay(double ce) {
		applyEREDayTypeSelectedDates(ce, DayType.EREDAY);
	}
	private void addDropDay() {
		applyDayTypeSelectedDates(DayType.DROPDAY);
	}

	private void cleanSelectedDates() {
		if(!selectedDates.getSelectedList().isEmpty())
			setWorkingStyles(selectedDates.getSelectedList());
		
		hourMenuItem.setEnabled(false);
		hourButton.setDisabled(true);
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
				cellsType[row][col].setAsType(DayType.NOTYPEDAY, row, col);
				Double hour = calendarEmployeeInfo.getHourByDay(date);
				if (-1 == hour)
					hour = 0.0;
				compositeH.put(date, hour);
				compositeT.put(date, DayType.NOTYPEDAY);
			}
		}
		calendarEmployeeInfo.setWorkingDays(compositeH, compositeT);
//		calendarEmployeeInfo.setHourByDay(compositeH);
//		calendarEmployeeInfo.setTypeByDay(compositeT);
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
					strikeDialog.open();
				}
			});
			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) ERE", new Command() {
				@Override
				public void execute() {
					ereDialog.open();
				}
			});
//			menu.addItem("A"+String.valueOf("\u00f1")+"adir dia(s) Ausencia", new Command() {
//				@Override
//				public void execute() {
//					addDropDay();
//				}
//			});
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
	
}
