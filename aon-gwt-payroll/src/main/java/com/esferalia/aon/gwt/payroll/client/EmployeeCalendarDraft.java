package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayTypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperDialog;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public class EmployeeCalendarDraft extends Composite {

	private static EmployeeCalendarDraftUiBinder uiBinder = GWT.create(EmployeeCalendarDraftUiBinder.class);

	interface EmployeeCalendarDraftUiBinder extends UiBinder<Widget, EmployeeCalendarDraft> {
	}
	
// ------------------------------------------------- INTERFAZ TIPO DIAS CELDAS -----------------------------------------------------
	
	public static interface CalendarTypeDayCell{
		void setAsType(DayType daytype, int row, int col);
		void setStyle(int row, int col);
	}
	
	public class DayTypeCell implements CalendarTypeDayCell{
		public DayType dayType;
		
		public DayTypeCell(DayType dayTypeAux) {
			this.dayType = dayTypeAux;
		}
		
		@Override
		public void setAsType(DayType daytype, int row, int col) {
			this.dayType = daytype;
			setStyle(row, col);
		}
		
		@Override
		public void setStyle(int row, int col){
			dayType.visit(new DayTypeVisitor() {
				
				@Override
				public void visitSuspensionDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.suspensionStyle());
				}
				
				@Override
				public void visitStrikeDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.strikeStyle());	
				}
				
				@Override
				public void visitReductionDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.reductionStyle());	
				}
				
				@Override
				public void visitHolyDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.holidayStyle());	
				}
				
				@Override
				public void visitFreeDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.sundayStyle());	
				}
				
				@Override
				public void visitEreDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.ereStyle());	
				}
				
				@Override
				public void visitDropDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.dropStyle());
				}

				@Override
				public void visitITDay(DayType dayType) {
					calendarGrid.getWidget(row, col).addStyleName(style.itStyle());
				}
				
				@Override
				public void visitNoTypeDay(DayType dayType) {
				}
			});
			
			
		}
	}
	
	
// ----------------------------------------------------- INTERFAZ MULTISELECCION CELDAS --------------------------------------------------
	
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
				fechasSelecciondas.setSelected(cellsDates[row][col], true);
			}
			//TODO:
			/*int pos = (row * 38) + col;
			posicionesSeleccionas.add(pos);*/	
			
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
				fechasSelecciondas.setSelected(cellsDates[row][col], true);
			}
			//TODO:
			/*int pos = (row * 38) + col;
			posicionesSeleccionas.add(pos);*/
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

		String doubleBoxDisableStyle();

		String doubleBoxDisableStyle2();
		
		String isSelectedStyle();
		
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
	}

	@UiField
	Grid calendarGrid;
	
	@UiField
	MenuItem selectAlldays;
	
	@UiField
	MenuItem selectUntill;
	
	@UiField
	MenuItem horasMenuItem;
	
	@UiField
	MenuItem horasButton;
	
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
	PaperButton diaNoLaborableButton;

	@UiField
	PaperButton diaAusenciaButton;

	@UiField
	PaperButton diaHuelgaButton;

	@UiField
	PaperButton diaEreButton;

	@UiField
	PaperButton diaReduccionButton;

	@UiField
	PaperButton diaVacacionesButton;

	@UiField
	PaperButton diaSuspensionButton;
	
	@UiField
	PaperIconButton infoButton;

	@UiField
	PaperIconButton eraseButton;
	
	@UiField
	PaperDialog dialogUntill;
	
	@UiField
	PaperButton dialogUntillOk;
	
	@UiField
	DateBoxEx fechaFinDialogHour;
	
	@UiField
	PaperButton dialogOk;
	
	@UiField
	PaperButton dialogInfoOk;
	
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
	PaperIconButton hourButton;
	
	@UiField
	HTMLPanel bloqueLunes;
	
	@UiField
	HTMLPanel bloqueMartes;
	
	@UiField
	HTMLPanel bloqueMiercoles;
	
	@UiField
	HTMLPanel bloqueJueves;
	
	@UiField
	HTMLPanel bloqueViernes;
	
	@UiField
	HTMLPanel bloqueSabado;
	
	@UiField
	HTMLPanel bloqueDomingo;
	
	@UiField
	PaperDialog dialogHoras;
	
	@UiField
	PaperDialog dialogInfo;

	@UiField(provided = true)
	SuggestBox lunesOpt;
	
	@UiField(provided = true)
	SuggestBox martesOpt;
	
	@UiField(provided = true)
	SuggestBox miercolesOpt;
	
	@UiField(provided = true)
	SuggestBox juevesOpt;
	
	@UiField(provided = true)
	SuggestBox viernesOpt;
	
	@UiField(provided = true)
	SuggestBox sabadoOpt;
	
	@UiField(provided = true)
	SuggestBox domingoOpt;
	
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
	
	//private List<Integer> _posicionesSeleccionas = new ArrayList<Integer>();
	private OrderedMultiSelectionModel<Date> fechasSelecciondas = new OrderedMultiSelectionModel<Date>();
	
	private int oldHourSelected = 0;
	private int mes;
	private int annio;
	private boolean mostrarHoras;
	
	private final CalendarTypeCell cells[][] = new CalendarTypeCell[25][38];
	private final CalendarTypeDayCell cellsType[][] = new CalendarTypeDayCell[25][38];
	private final Date cellsDates[][] = new Date[25][38];
	
	private final SuggestBox suggestOpts[] = new SuggestBox[7];
	private final HTMLPanel divDays[] = new HTMLPanel[7];
	
	private EmployeeCalendarDraftObjectData calendarEmployeeInfo;
	private Integer startEmployeeContract;
	private Integer endEmployeeContract;
	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	public EmployeeCalendarDraft() {
		
		//Inicializamos todos los SuggestBox para insertar horas nuevas
		ArrayList<String> tipoHoras = new ArrayList<String>();
		MultiWordSuggestOracle oracleL = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleM = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleX = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleJ = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleV = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleS = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleD = new MultiWordSuggestOracle();
		tipoHoras.add("2");
		tipoHoras.add("4");
		tipoHoras.add("6");
		tipoHoras.add("8");
		oracleL.setDefaultSuggestionsFromText(tipoHoras);
		oracleM.setDefaultSuggestionsFromText(tipoHoras);
		oracleX.setDefaultSuggestionsFromText(tipoHoras);
		oracleJ.setDefaultSuggestionsFromText(tipoHoras);
		oracleV.setDefaultSuggestionsFromText(tipoHoras);
		oracleS.setDefaultSuggestionsFromText(tipoHoras);
		oracleD.setDefaultSuggestionsFromText(tipoHoras);
		
		suggestOpts[MONDAY] = new SuggestBox(oracleL);
		suggestOpts[TUESDAY] = new SuggestBox(oracleM);
		suggestOpts[WEDNESDAY] = new SuggestBox(oracleX);
		suggestOpts[THURSDAY] = new SuggestBox(oracleJ);
		suggestOpts[FRIDAY] = new SuggestBox(oracleV);
		suggestOpts[SATURDAY] = new SuggestBox(oracleS);
		suggestOpts[SUNDAY] = new SuggestBox(oracleD);
		
		this.lunesOpt = suggestOpts[MONDAY];
		this.martesOpt = suggestOpts[TUESDAY];
		this.miercolesOpt = suggestOpts[WEDNESDAY];
		this.juevesOpt = suggestOpts[THURSDAY];
		this.viernesOpt = suggestOpts[FRIDAY];
		this.sabadoOpt = suggestOpts[SATURDAY];
		this.domingoOpt = suggestOpts[SUNDAY];
		
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		divDays[0] = bloqueLunes;
		divDays[1] = bloqueMartes;
		divDays[2] = bloqueMiercoles;
		divDays[3] = bloqueJueves;
		divDays[4] = bloqueViernes;
		divDays[5] = bloqueSabado;
		divDays[6] = bloqueDomingo;
		
		inicializarCellsCalendar();
		inicializarCellsTypeCalendar();
		
		//Gestion boton horas
		horasButton.setEnabled(false);
		hourButton.setDisabled(true);
		
		selectAlldays.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				selectAll();
			}

			private void selectAll() {
				Date startDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getStartDateContract());
				Date endDate = DateUtils.copyDateOnly(calendarEmployeeInfo.getEndDateContract());
				while (startDate.before(endDate) || startDate.equals(endDate)) {
					fechasSelecciondas.setSelected(DateUtils.copyDateOnly(startDate), true);
					DateUtils.addDays2Date(startDate, 1);
				}
				pintarSeleccion(fechasSelecciondas.getSelectedList());
			}
			
		});
		
		selectUntill.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				dialogUntill.open();
				anadirFechas();
				
			}
		});
		
		fechaFinDialogHour.getTextBox().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fechaFinDialogHour.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
				
			}
		});
	
		horasButton.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				//TODO: if(!posicionesSeleccionas.isEmpty()){
				if(!fechasSelecciondas.getSelectedList().isEmpty()){
					dialogHoras.open();
					comprobarDiasAMostrar();
				}
			}
		});
		
		//Visualizar y ocultar las horas del calendario
		horasMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				mostrarHoras = !mostrarHoras;
				horasMenuItem.setStyleName("aon-MenuItemCheckYes", mostrarHoras);
				if (mostrarHoras)
					ocultarHoras();		
				else
					visualizarHoras();
			}
		});		
	}

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("dialogUntillOk")
	public void onOkuntillDialogClick(ClickEvent event) {
		if(fechasSelecciondas.getSelectedList().size() == 1){
			Date startDate = fechasSelecciondas.getSelectedList().get(0);
			Date endDate = DateUtils.copyDateOnly(fechaFinDialogHour.getValue());
			while (startDate.before(endDate) || startDate.equals(endDate)) {
				fechasSelecciondas.setSelected(DateUtils.copyDateOnly(startDate), true);
				DateUtils.addDays2Date(startDate, 1);
			}
		}
		pintarSeleccion(fechasSelecciondas.getSelectedList());
	}
	
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		
		event.preventDefault();
		
		horasButton.setEnabled(true);
		hourButton.setDisabled(false);
		
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * 38) + col;
		
		if (null != cellsDates[row][col] && calendarEmployeeInfo.getStartDateContract().after(cellsDates[row][col]))
			return;
	
		//Pulsacion celda con CTRL
		if (event.isControlKeyDown()) { 
			cells[row][col].select(row, col);
		
		//Pulsacion celda con SHIFT
		} else if (event.isShiftKeyDown()){ 
			//TODO: int posicionIncial = posicionesSeleccionas.get(0);
			int posicionIncial = calcularPosicionFecha(fechasSelecciondas.getSelectedList().get(0));
			int posicionFin = pos;
			
			if (posicionIncial > posicionFin){
				int posAux = posicionIncial;
				posicionIncial = posicionFin-1;
				posicionFin = posAux;
			}
			
			while (posicionIncial != posicionFin){
				cells[calcularFila(posicionIncial+1)][calcularColumna(posicionIncial+1)]
						.select(calcularFila(posicionIncial+1), calcularColumna(posicionIncial+1));
				posicionIncial++;
			}
		
		//Pulsacion una sola celda	
		} else { 
			//TODO:
			/*for (Integer posList : posicionesSeleccionas) {
				int colSelect = calcularColumna(posList.intValue());
				int filSelect = calcularFila(posList.intValue());
				cells[filSelect][colSelect].unSelect(filSelect, colSelect);
			}
			posicionesSeleccionas.clear();*/
			
			for (Date date : fechasSelecciondas.getSelectedList()) {
				int posicion = calcularPosicionFecha(date);
				int colSelect = calcularColumna(posicion);
				int filSelect = calcularFila(posicion);
				cells[filSelect][colSelect].unSelect(filSelect, colSelect);
			}
			fechasSelecciondas.clear();
			
			
			cells[calcularFila(oldHourSelected)][calcularColumna(oldHourSelected)]
					.unSelect(calcularFila(oldHourSelected), calcularColumna(oldHourSelected));
			
			if (esMes(row, col)){
				for(int i = 1; i<38; i++)
					cells[row][i].select(row, i);
			}else
				cells[row][col].select(row, col);
			
			oldHourSelected = pos;
		}
	
	}

	@UiHandler("dialogOk")
	public void onConfirmDialogClick(ClickEvent event) {
		double horasLunes = Double.parseDouble(suggestOpts[MONDAY].getValue());
		double horasMartes = Double.parseDouble(suggestOpts[TUESDAY].getValue());
		double horasMiercoles = Double.parseDouble(suggestOpts[WEDNESDAY].getValue());
		double horasJueves = Double.parseDouble(suggestOpts[THURSDAY].getValue());
		double horasViernes = Double.parseDouble(suggestOpts[FRIDAY].getValue());
		double horasSabado = Double.parseDouble(suggestOpts[SATURDAY].getValue());
		double horasDomingo = Double.parseDouble(suggestOpts[SUNDAY].getValue());
		actualizarHoras(horasLunes, horasMartes, horasMiercoles, horasJueves, horasViernes, horasSabado, horasDomingo);
		dialogHoras.close();
		horasButton.setEnabled(false);
		hourButton.setDisabled(true);
		fechasSelecciondas.clear();
		//TODO: posicionesSeleccionas.clear();
		//TODO:
		changeYear(0);
	}
	
	@UiHandler("dialogInfoOk")
	public void onConfirmInfoDialogClick(ClickEvent event) {
		dialogHoras.close();
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
	
	@UiHandler("eraseButton")
	public void onEraseClick(ClickEvent event) {
		//TODO:
		/*if (!posicionesSeleccionas.isEmpty()) {
			limpiarEstilos(posicionesSeleccionas);
		}
		posicionesSeleccionas.clear();*/
		if(!fechasSelecciondas.getSelectedList().isEmpty())
			limpiarEstilos(fechasSelecciondas.getSelectedList());
		fechasSelecciondas.clear();
	}
	
	@UiHandler("diaNoLaborableButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		aplicarEstilosDiasSeleccionadios(DayType.FREEDAY);	
	}

	@UiHandler("diaVacacionesButton")
	public void onVacacionesClick(ClickEvent event) {
		aplicarEstilosDiasSeleccionadios(DayType.HOLIDAY);
	}
	
	@UiHandler("diaEreButton")
	public void onEreClick(ClickEvent event) {
		aplicarEstilosDiasSeleccionadios(DayType.EREDAY);
	}
	
	@UiHandler("diaHuelgaButton")
	public void onHuelgaClick(ClickEvent event) {
		aplicarEstilosDiasSeleccionadios(DayType.STRIKEDAY);
	}
	
	@UiHandler("diaAusenciaButton")
	public void onDiaAusenciaClick(ClickEvent event) {
		//aplicarEstilosDiasSeleccionadios(DayType.DROPDAY);
	}

	@UiHandler("diaReduccionButton")
	public void onReduccionClick(ClickEvent event) {
		//aplicarEstilosDiasSeleccionadios(DayType.REDUCTIONDAY);
	}

	@UiHandler("diaSuspensionButton")
	public void onSuspensionClick(ClickEvent event) {
		//aplicarEstilosDiasSeleccionadios(DayType.SUSPENSIONDAY);
	}
	
	@UiHandler("hourButton")
	public void onHourClick(ClickEvent event) {
		//TODO: if(!posicionesSeleccionas.isEmpty()){
		if(!fechasSelecciondas.getSelectedList().isEmpty()){	
			dialogHoras.open();
			anadirFechas();
			comprobarDiasAMostrar();
		}
	}
	
	@UiHandler("infoButton")
	public void oninfoClick(ClickEvent event) {
		dialogInfo.open();
		dialogInfo.setPositionTarget("center");
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
		
		this.mes = 0;
		
		limpiarEstiloCambios();
		limpiarCalendario();
		mostrarCalendarioWidget(Integer.parseInt(yearLabel.getText())- 1900);
		
		pintarCambiosHorasRealizados(calendarEmployeeInfo.getHourChanges());
		pintarCambiosTiposRealizados(calendarEmployeeInfo.getTypeChanges());
			
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		calendarEmployeeInfo.undoManager.redo();
		
		this.mes = 0;
		
		limpiarEstiloCambios();
		limpiarCalendario();
		mostrarCalendarioWidget(Integer.parseInt(yearLabel.getText())- 1900);
		
		pintarCambiosHorasRealizados(calendarEmployeeInfo.getHourChanges());
		pintarCambiosTiposRealizados(calendarEmployeeInfo.getTypeChanges());
		
	}
	
	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while (calendarEmployeeInfo.undoManager.canUndo())
			calendarEmployeeInfo.undoManager.undo();
		
		this.mes = 0;
		
		limpiarEstiloCambios();
		limpiarCalendario();
		mostrarCalendarioWidget(Integer.parseInt(yearLabel.getText())- 1900);
		
		pintarCambiosHorasRealizados(calendarEmployeeInfo.getHourChanges());
		pintarCambiosTiposRealizados(calendarEmployeeInfo.getTypeChanges());
		
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		calendarEmployeeInfo.actualizarCalendarioBD(r -> 
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
		
		calendar.inicialiazarCalendarioBD(
				r -> { this.mostrarHoras = r.isJornadaCompleta();
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
		
		this.annio = new Date().getYear();
		this.mes = 0;
		this.startEmployeeContract = getStartYearContract(calendarEmployeeInfo.getStartDateContract());
		this.endEmployeeContract = getEndYearContract(calendarEmployeeInfo.getEndDateContract());
		int actualYear = this.annio+1900;
		this.yearLabel.setText(Integer.toString(actualYear));
		
		limpiarEstiloCambios();
		limpiarCalendario();
		inicializarCellsCalendar();
		inicializarCellsTypeCalendar();
		mostrarCalendarioWidget(this.annio);
		
		if (this.mostrarHoras){
			horasMenuItem.setStyleName("aon-MenuItemCheckYes", mostrarHoras);
			ocultarHoras();
		}else{
			horasMenuItem.setStyleName("aon-MenuItemCheckYes", mostrarHoras);
			visualizarHoras();
		}
		
		pintarCambiosHorasRealizados(calendarEmployeeInfo.getHourChanges());
		pintarCambiosTiposRealizados(calendarEmployeeInfo.getTypeChanges());
				
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
	
	private void inicializarCellsCalendar() {
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 38; column++)
				cells[row][column] = NoneCell.NONE_CELL;
	}
	
	private void inicializarCellsTypeCalendar() {
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 38; column++)
				cellsType[row][column] = new DayTypeCell(DayType.NOTYPEDAY);
	}
	
	/**
	 * Metodo que gestiona la movibilidad entre años y llama a pintar el calendario
	 * @param annio : año que debe pintar el calendario
	 */
	@SuppressWarnings("deprecation")
	private void mostrarCalendarioWidget(int annio) {
		//Crear calendario
		for (int row = 1; row < 25; row += 2)
			mostrarCalendario(row, annio);
		
		this.nextYearButton.setEnabled(true);
		this.lastYearButton.setEnabled(true);
		
		if (annio == startEmployeeContract || annio == (new Date().getYear()-1)){
			bloquearDiasFueraDeContrato(calendarEmployeeInfo.getStartDateContract());
			this.lastYearButton.setEnabled(false);
		}
		if (annio == endEmployeeContract || annio == (new Date().getYear()+1)){
			bloquearDiasFueraDeContratoPost(calendarEmployeeInfo.getEndDateContract());
			this.nextYearButton.setEnabled(false);
		}
		
		
	}

	private int calcularNumeroDiaSemana(int dia, int mes, int anio) {
		@SuppressWarnings("deprecation")
		Date fecha = new Date(anio, mes, dia);
		@SuppressWarnings("deprecation")
		int numDia = fecha.getDay();

		// Tratamiento calendario español, 0 = Lunes, 6 = Domingo
		if (0 == numDia)
			numDia = 7;

		return numDia;
	}
	
	@SuppressWarnings("deprecation")
	private boolean comprobarFecha(int dia, int mes, int anio) {
		return mes >= 0 && mes <= 11 && anio > 0 && anio < 32768 && dia >= 0
				&& dia <= (new Date(anio, mes, dia)).getDate();
	}

	private int calcularUltimoDiaMes(int mes, int anio) {
		int ultimo_dia = 28;
		while (comprobarFecha(ultimo_dia + 1, mes, anio))
			ultimo_dia++;

		return ultimo_dia;
	}

	private void mostrarCalendario(int row, int anio) {
		// Mostrar días del mes
		int contadorDias = 1;
		int primerDiaMes = calcularNumeroDiaSemana(1, mes, anio);
		int ultimoDiaMes = calcularUltimoDiaMes(mes, anio);

		// Escribo la primera fila de la semana
		for (int i = 1; i <= 7; i++) {
			// Label insetar
			Label diaInfo = new Label();
			diaInfo.setStyleName(style.cellStyle());

			if (i < primerDiaMes) {
				diaInfo.setText("");
				calendarGrid.setWidget(row, i, diaInfo);
				cells[row + 1][i] = new NoneCell();
			} else {
				@SuppressWarnings("deprecation")
				Date actualDay = new Date(anio, mes, contadorDias);
				
				DateUtils.resetTime(actualDay);
				DoubleBox horas = new DoubleBox();
				horas.setValue(calendarEmployeeInfo.getHourByDay(actualDay));
				DayType dayType = calendarEmployeeInfo.getTypeByDay(actualDay);
				int filaHoras = row + 1;
				if (filaHoras % 4 == 0)
					horas.setStyleName(style.doubleBoxDisableStyle2());
				else
					horas.setStyleName(style.doubleBoxDisableStyle());

				diaInfo.setText(contadorDias + "");
				calendarGrid.setWidget(row, i, diaInfo);
				calendarGrid.setWidget(row + 1, i, horas);
				cellsDates[row][i] = actualDay;
				cells[row][i] = new DayCell();
				cells[row + 1][i] = new HourCell();
				cellsType[row][i].setAsType(dayType, row, i);
				contadorDias++;
			}
		}

		
		int diaActualSemana = 1;
		while (contadorDias <= ultimoDiaMes) {
			// Dia Acutal
			@SuppressWarnings("deprecation")
			Date actualDay = new Date(anio, mes, contadorDias);
			
			DateUtils.resetTime(actualDay);
			// Label insetar
			Label diaInfo = new Label(contadorDias + "");
			diaInfo.setStyleName(style.cellStyle());
			//CeldaHora
			DoubleBox horas = new DoubleBox();
			horas.setValue(calendarEmployeeInfo.getHourByDay(actualDay));
			DayType dayType = calendarEmployeeInfo.getTypeByDay(actualDay);
			
			int filaHoras = row + 1;
			
			if (filaHoras % 4 == 0)
				horas.setStyleName(style.doubleBoxDisableStyle2());
			else
				horas.setStyleName(style.doubleBoxDisableStyle());

			calendarGrid.setWidget(row, 7 + diaActualSemana, diaInfo);
			calendarGrid.setWidget(row + 1, 7 + diaActualSemana, horas);
			cellsDates[row][7 + diaActualSemana] = actualDay;
			cells[row][7 + diaActualSemana] = new DayCell();
			cells[row + 1][7 + diaActualSemana] = new HourCell();
			cellsType[row][7 + diaActualSemana].setAsType(dayType, row, (7 + diaActualSemana));
			
			contadorDias++;
			diaActualSemana++;
			
		}

		if ((diaActualSemana) < 30) {
			for (int i = 7 + diaActualSemana; i < 38; i++) {
				Label diaInfo = new Label("");
				diaInfo.setStyleName(style.cellStyle());
				calendarGrid.setWidget(row, i, diaInfo);
				cells[row][i] = new NoneCell();
			}
		}

		mes++;
	}
	
	/*
	 * Bloque para limpiar estilos de todas las posiciones seleccionas, de una sola posicion,
	 * limpiar estilo de seleccion de las posiciones seleccionadas , limpiar estilo fuera de
	 * contraro al cambiar de año, limpiar el calendario completamente.
	 */
	//TODO:
	/*private void limpiarEstilos(List<Integer> posicionesSeleccionas) {
		for (Integer pos : posicionesSeleccionas) {
			int col = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
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
			calendarEmployeeInfo.setTypeByDay(cellsDates[row][col], DayType.NOTYPEDAY);
		}
	}*/
	
	private void limpiarEstilos(List<Date> fechasSeleccionadas) {
		for (Date date : fechasSeleccionadas) {
			int pos = calcularPosicionFecha(date);
			int col = calcularColumna(pos);
			int row = calcularFila(pos);
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
			calendarEmployeeInfo.setTypeByDay(cellsDates[row][col], DayType.NOTYPEDAY);
		}
	}
	
	private void limpiarEstilo(int row, int col) {
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
	
	//TODO:
	/*private void limpiarSeleccion(List<Integer> posicionesSeleccionas) {
		for (Integer pos : posicionesSeleccionas) {
			int col = calcularColumna(pos.intValue());
			int fil = calcularFila(pos.intValue());
			calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
		}
	}*/
	
	private void limpiarSeleccion(List<Date> fechasSeleccionadas) {
		for (Date date : fechasSeleccionadas) {
			int pos = calcularPosicionFecha(date);
			int col = calcularColumna(pos);
			int fil = calcularFila(pos);
			calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
		}
	}
	
	private void limpiarEstiloCambios() {
		for (int i = 1; i < 25; i++)
			for (int j = 1; j < 38; j++){
				cells[i][j].eraseOnChange(i, j);
				if (null != calendarGrid.getWidget(i, j))
					calendarGrid.getCellFormatter().removeStyleName(i, j, style.setOutOfContractStyle());
			}	
	}

	private void limpiarCalendario() {
		for (int i = 1; i < 25; i++)
			for (int j = 1; j < 38; j++)
				calendarGrid.clearCell(i, j);
	}
	
	/*
	 * Pintar los cambios realizados tanto en las horas como en los tipos de dias, cuando
	 * todavia no se ha guardado.
	 */
	private void pintarCambiosHorasRealizados(Set<Entry<Date, Double>> hourChanges) {
		for (Entry<Date,Double> e : hourChanges){
			for (int i = 1; i < 25; i++)
				for (int j = 1; j < 38; j++){
					if (e.getKey().equals(cellsDates[i][j]))
						cells[i+1][j].setOnChange(i+1, j);
				}		
		}	
	}
	
	private void pintarCambiosTiposRealizados(Set<Entry<Date, DayType>> typeChanges) {
		for (Entry<Date,DayType> e : typeChanges){
			for (int i = 1; i < 25; i++)
				for (int j = 1; j < 38; j++){
					if (e.getKey().equals(cellsDates[i][j]))
						cellsType[i][j].setStyle(i, j);
				}		
		}	
	}

	//TODO:
	/*private boolean es(int day,int col) {
		return col==day+1 || col==day+8 || col==day+15 || col==day+22 || col==day+29 || col==day+36;
	}*/
	
	@SuppressWarnings("deprecation")
	private boolean es(int day,Date date) {
		return day == date.getDay();
	}

	private int calcularColumna(int pos) {
		return pos % 38;
	}

	private int calcularFila(int pos) {
		return pos / 38;
	}
	
	private boolean esMes(int row, int col) {
		return col==0 && esMesAux(row);
	}
	
	private boolean esMesAux(int row) {
		return row == 1 || row == 3 || row == 5 || row == 7 || row == 9 || row == 11 || row == 13 || row == 15 || row == 17 || 
				row == 19 || row == 21 || row == 23;
	}
	
	//TODO:
	/*private void actualizarHoras(double horasLunes, double horasMartes, double horasMiercoles, double horasJueves, double horasViernes, double horasSabado, double horasDomingo) {
		for (Integer pos : posicionesSeleccionas) {
			int col = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			if (es(SUNDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasDomingo);
				cells[row+1][col].setHour(row+1, col, horasDomingo);
				cells[row+1][col].setOnChange(row+1, col);
			}else if (es(SATURDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasSabado);
				cells[row+1][col].setHour(row+1, col, horasSabado);
				cells[row+1][col].setOnChange(row+1, col);
			}else if (es(FRIDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasViernes);
				cells[row+1][col].setHour(row+1, col, horasViernes);
				cells[row+1][col].setOnChange(row+1, col);
			}else if (es(THURSDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasJueves);
				cells[row+1][col].setHour(row+1, col, horasJueves);
				cells[row+1][col].setOnChange(row+1, col);
			}else if (es(WEDNESDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasMiercoles);
				cells[row+1][col].setHour(row+1, col, horasMiercoles);
				cells[row+1][col].setOnChange(row+1, col);
			}else if (es(TUESDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasMartes);
				cells[row+1][col].setHour(row+1, col, horasMartes);
				cells[row+1][col].setOnChange(row+1, col);
			}else{ 
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasLunes);
				cells[row+1][col].setHour(row+1, col, horasLunes);
				cells[row+1][col].setOnChange(row+1, col);
			}
		}
		limpiarSeleccion(posicionesSeleccionas);
		posicionesSeleccionas.clear();
	}*/
	
	private void actualizarHoras(double horasLunes, double horasMartes, double horasMiercoles, double horasJueves, double horasViernes, double horasSabado, double horasDomingo) {
		for (Date date : fechasSelecciondas.getSelectedList()) {
			if (es(0, date)){//DOMINGO
				calendarEmployeeInfo.setHourByDay(date, horasDomingo);
			}else if (es(6, date)){//SABADO
				calendarEmployeeInfo.setHourByDay(date, horasSabado);
			}else if (es(5, date)){//VIERNES
				calendarEmployeeInfo.setHourByDay(date, horasViernes);
			}else if (es(4, date)){//JUEVES
				calendarEmployeeInfo.setHourByDay(date, horasJueves);
			}else if (es(3, date)){//MIERCOLES
				calendarEmployeeInfo.setHourByDay(date, horasMiercoles);
			}else if (es(2, date)){//MARTES
				calendarEmployeeInfo.setHourByDay(date, horasMartes);
			}else{//LUNES 
				calendarEmployeeInfo.setHourByDay(date, horasLunes);
			}
		}
		limpiarSeleccion(fechasSelecciondas.getSelectedList());
		fechasSelecciondas.getSelectedList().clear();
	}
	
	private void comprobarDiasAMostrar() {
		//Ocultar todos los dias
		for (int i=0; i<7; i++)
			divDays[i].addStyleName(style.ocultarDivStyle());
		
		//Gestionar los dias seleccionados (mostrar y actualizar valor)
		for (int i = 0; i < 7; i++){
			final int c =i; 
			try{
				
				Double minValue = Double.MIN_VALUE;
				//TODO:
				/*Double value = posicionesSeleccionas.stream()
						.filter(p -> es(c, calcularColumna(p.intValue())))
						.map( p-> new Integer[]{calcularFila(p.intValue())+1, calcularColumna(p.intValue())})
						.map(p->cells[p[0]][p[1]].getHour(p[0], p[1]))
						.peek(p -> divDays[c].removeStyleName(style.ocultarDivStyle()))
						.collect(Collectors.reducing(Double.MIN_VALUE,(h1,h2) -> minValue.equals(h1) || h2.equals(h1) ? h2: null ))
						;*/
				
				Double value = fechasSelecciondas.getSelectedList().stream()
						.filter(d -> d.getDay() == c)
						.map(d-> calendarEmployeeInfo.getHourByDay(d))
						.peek(p -> divDays[c].removeStyleName(style.ocultarDivStyle()))
						.collect(Collectors.reducing(Double.MIN_VALUE,(h1,h2) -> minValue.equals(h1) || h2.equals(h1) ? h2: null ))
						;

				
				int dia = c-1;
				if(dia == -1)
					dia = 6;
				
				if(value != null)
					suggestOpts[dia].setValue(value+"");
				else
					suggestOpts[dia].setValue("");
			
			} catch (Exception e) {
				Window.alert("Fallo!"+", "+c + "," + e.getMessage());
			}
			
			
		}

	}
	
	private void ocultarHoras() {
		for (int i = 2; i < 25; i += 2) {
			calendarGrid.getRowFormatter().addStyleName(i, style.ocultarHorasStyle());
		}
	}

	private void visualizarHoras() {
		for (int i = 2; i < 25; i += 2) {
			calendarGrid.getRowFormatter().removeStyleName(i, style.ocultarHorasStyle());
		}
	}
	
	//TODO:
	/*private void aplicarEstilosDiasSeleccionadios(DayType dayType) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cellsType[row][column].setAsType(dayType, row, column);
			calendarEmployeeInfo.setTypeByDay(cellsDates[row][column], dayType);
		}	
		posicionesSeleccionas.clear();
		
	}*/
	
	private void aplicarEstilosDiasSeleccionadios(DayType dayType) {
		limpiarEstilos(fechasSelecciondas.getSelectedList());
		for (Date date : fechasSelecciondas.getSelectedList()) {
			int pos = calcularPosicionFecha(date);
			int column = calcularColumna(pos);
			int row = calcularFila(pos);
			cells[row][column].unSelect(row, column);
			cellsType[row][column].setAsType(dayType, row, column);
			calendarEmployeeInfo.setTypeByDay(cellsDates[row][column], dayType);
		}	
		fechasSelecciondas.clear();
		
	}
	
	private void changeYear(int change) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + change;
		yearLabel.setText(Integer.toString(newYear));
		
		int parseNewYear = newYear - 1900;
		this.annio = parseNewYear;
		mes = 0;
		
		limpiarEstiloCambios();
		limpiarCalendario();
		inicializarCellsCalendar();
		inicializarCellsTypeCalendar();
		mostrarCalendarioWidget(this.annio);
		
		pintarCambiosHorasRealizados(calendarEmployeeInfo.getHourChanges());
		pintarCambiosTiposRealizados(calendarEmployeeInfo.getTypeChanges());
		
	}
	
	private void bloquearDiasFueraDeContrato(Date startDateContract) {
		for (int row = 1; row < 25; row+=2)
			for (int column = 1; column < 38; column++){
				if (null != cellsDates[row][column] && 
						(startDateContract.after(cellsDates[row][column]))){
					calendarGrid.getCellFormatter().addStyleName(row, column, style.setOutOfContractStyle());
					calendarGrid.getCellFormatter().addStyleName(row+1, column, style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).addStyleName(style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).setVisible(false);
					limpiarEstilo(row, column);
				}
			}	
	}
	
	private void bloquearDiasFueraDeContratoPost(Date endDateContract) {
		for (int row = 1; row < 25; row+=2)
			for (int column = 1; column < 38; column++){
				if (null != cellsDates[row][column] && 
						(endDateContract.before(cellsDates[row][column]))){
					calendarGrid.getCellFormatter().addStyleName(row, column, style.setOutOfContractStyle());
					calendarGrid.getCellFormatter().addStyleName(row+1, column, style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).addStyleName(style.setOutOfContractStyle());
					calendarGrid.getWidget(row+1, column).setVisible(false);
					limpiarEstilo(row, column);
				}
			}	
	}
	
	private void anadirFechas() {
		//Date endDate = fechaMax(fechasSelecciondas.getSelectedList());
		this.fechaFinDialogHour.setValue(new Date());
	}

	private Date fechaMax(List<Date> fechasSelecciondas) {
		Date auxDate = fechasSelecciondas.get(0);
		
		for (Date date : fechasSelecciondas) {
			if (auxDate.before(date))
					auxDate = date;
		}
		return auxDate;
	}

	private Date fechaMin(List<Date> fechasSelecciondas) {
		Date auxDate = fechasSelecciondas.get(0);
		
		for (Date date : fechasSelecciondas) {
			if (auxDate.after(date))
					auxDate = date;
		}
		return auxDate;
	}
	
	@SuppressWarnings("deprecation")
	private Integer calcularPosicionFecha(Date date){
		int row = cacularFilaFecha(date.getMonth());
		int pos = -1;
		for(int col = 1; col < 38; col++){
			if(date.equals(cellsDates[row][col])){
				pos = (row * 38) + col;
				return pos;
			}
		}
		return pos;
	}
	

	private int cacularFilaFecha(int month) {
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
	
	private void pintarSeleccion(List<Date> fechasSeleccionadas) {
		for(Date date : fechasSeleccionadas){
			int pos = calcularPosicionFecha(date);
			if (pos != -1){
				int col = calcularColumna(pos);
				int row = calcularFila(pos);
				cells[row][col].select(row, col);
			}
		}
		
	}
	
}
