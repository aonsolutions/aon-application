package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperDialog;

public class EmployeeCalendarDraft extends Composite {

	private static EmployeeCalendarDraftUiBinder uiBinder = GWT.create(EmployeeCalendarDraftUiBinder.class);

	interface EmployeeCalendarDraftUiBinder extends UiBinder<Widget, EmployeeCalendarDraft> {
	}
	
	// --------------------------------------- INTERFAZ MULTISELECCION CELDAS -----------------------------------------------
	public static interface CalendarTypeCell{
		void select(int row, int col);
		void unSelect(int row, int col);
		void setAsSunday(int row, int col);
		void setAsHoliday(int row, int col);
		void setAsDrop(int row, int col);
		void setAsStrike(int row, int col);
		void setAsEre(int row, int col);
		void setAsReduction(int row, int col);
		void setAsSuspension(int row, int col);
		void setHour(int row, int col, double newHour);
		double getHour(int row, int col);
	}
	
	public class DayCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.isSelectedStyle());
			int pos = (row * 38) + col;
			posicionesSeleccionas.add(pos);	
		}
		@Override
		public void unSelect(int row, int col) {
			calendarGrid.getWidget(row, col).removeStyleName(style.isSelectedStyle());	
		}
		@Override
		public void setAsSunday(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.sundayStyle());
		}
		@Override
		public void setAsHoliday(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.holidayStyle());
		}
		@Override
		public void setAsDrop(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.dropStyle());
		}
		@Override
		public void setAsStrike(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.strikeStyle());
		}
		@Override
		public void setAsEre(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.ereStyle());
		}
		@Override
		public void setAsReduction(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.reductionStyle());
		}
		@Override
		public void setAsSuspension(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.suspensionStyle());	
		}
		@Override
		public void setHour(int row, int col, double newHour) {
		}
		@Override
		public double getHour(int row, int col) {
			return 0;
		}
	}
	
	public class HourCell implements CalendarTypeCell{
		@Override
		public void select(int row, int col) {
			calendarGrid.getWidget(row, col).addStyleName(style.doubleBoxStyle());
			int pos = (row * 38) + col;
			posicionesSeleccionas.add(pos);
		}
		@Override
		public void unSelect(int row, int col) {
			if (row%4 == 0)
				calendarGrid.getWidget(row, col).setStyleName(style.doubleBoxDisableStyle2());
			else
				calendarGrid.getWidget(row, col).setStyleName(style.doubleBoxDisableStyle());
		}
		@Override
		public void setAsSunday(int row, int col) {
		}
		@Override
		public void setAsHoliday(int row, int col) {
		}
		@Override
		public void setAsDrop(int row, int col) {
		}
		@Override
		public void setAsStrike(int row, int col) {
		}
		@Override
		public void setAsEre(int row, int col) {
		}
		@Override
		public void setAsReduction(int row, int col) {
		}
		@Override
		public void setAsSuspension(int row, int col) {
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
		public void setAsSunday(int row, int col) {	
		}
		@Override
		public void setAsHoliday(int row, int col) {
		}
		@Override
		public void setAsDrop(int row, int col) {
		}
		@Override
		public void setAsStrike(int row, int col) {
		}
		@Override
		public void setAsEre(int row, int col) {
		}
		@Override
		public void setAsReduction(int row, int col) {
		}
		@Override
		public void setAsSuspension(int row, int col) {
		}
		@Override
		public void setHour(int row, int col, double newHour) {
		}
		@Override
		public double getHour(int row, int col) {
			return 0;
		}
	} 

	//----------------------------------------------------UiFields--------------------------------------------------------------------
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String sundayStyle();

		String cellStyle();

		String ereStyle();

		String strikeStyle();

		String reductionStyle();

		String suspensionStyle();

		String holidayStyle();

		String dropStyle();

		String isSelectedStyle();

		String doubleBoxStyle();

		String doubleBoxDisableStyle();

		String doubleBoxDisableStyle2();

		String ocultarHorasStyle();
		
		String ocultarDivStyle();
	}

	@UiField
	Grid calendarGrid;

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
	PaperButton eraseButton;
	
	@UiField
	PaperButton dialogOk;
	
	@UiField
	Button expandHourBtnL;
	
	@UiField
	Button expandHourBtnM;
	
	@UiField
	Button expandHourBtnX;
	
	@UiField
	Button expandHourBtnJ;
	
	@UiField
	Button expandHourBtnV;
	
	@UiField
	Button expandHourBtnS;
	
	@UiField
	Button expandHourBtnD;
	
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
	MenuItem horasMenuItem;
	
	@UiField
	MenuItem horasButton;
	
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

	private final static int MONDAY = 0;
	private final static int TUESDAY = 1;
	private final static int WEDNESDAY = 2;
	private final static int THURSDAY = 3;
	private final static int FRIDAY = 4;
	private final static int SATURDAY = 5;
	private final static int SUNDAY = 6;
	private List<Integer> posicionesSeleccionas = new ArrayList<Integer>();
	private int oldHourSelected = 0;
	private int mes;
	private int annio;
	private boolean mostrarHoras = true;
	private final CalendarTypeCell cells[][] = new CalendarTypeCell[25][38];
	private final Date cellsDates[][] = new Date[25][38];
	private final SuggestBox suggestOpts[] = new SuggestBox[7];
	private final HTMLPanel divDays[] = new HTMLPanel[7];
	private EmployeeCalendarDraftObjectData calendarEmployeeInfo;
	private Integer startEmployeeContract;
	private Integer endEmployeeContract;
	
	public EmployeeCalendarDraft() {
		
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
		
		initWidget(uiBinder.createAndBindUi(this));
		
		divDays[0] = bloqueLunes;
		divDays[1] = bloqueMartes;
		divDays[2] = bloqueMiercoles;
		divDays[3] = bloqueJueves;
		divDays[4] = bloqueViernes;
		divDays[5] = bloqueSabado;
		divDays[6] = bloqueDomingo;
		
		inicializarCellsCalendar();
		
		//Bloquear boton horas hasta seleccion
		horasButton.setEnabled(false);		
		
		horasMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				mostrarHoras = !mostrarHoras;
				horasMenuItem.setStyleName("aon-MenuItemCheckYes", !mostrarHoras);
				if (mostrarHoras)
					visualizarHoras();
				else
					ocultarHoras();
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
		});
		
		horasButton.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				dialogHoras.open();
				comprobarDiasAMostrar();
			}

			private void comprobarDiasAMostrar() {
				//Ocultar todos los dias
				for (int i=0; i<7; i++)
					divDays[i].addStyleName(style.ocultarDivStyle());
				
				//Gestionar los dias seleccionados (mostrar y actualizar valor)
				for (int i = 0; i < 7; i++){
					final int c =i; 
					Double value = posicionesSeleccionas.stream()
							.filter(p -> es(c, calcularColumna(p.intValue())))
							.map( p-> new Integer[]{calcularFila(p.intValue())+1, calcularColumna(p.intValue())})
							.map(p->cells[p[0]][p[1]].getHour(p[0], p[1]))
							.peek(p -> divDays[c].removeStyleName(style.ocultarDivStyle()))
							.collect(Collectors.reducing(Double.MIN_VALUE,(h1,h2) -> Double.MIN_VALUE == h1 || h2.equals(h1) ? h2: null ))
							;
					
					if(value != null)
						suggestOpts[c].setValue(value+"");
					else
						suggestOpts[c].setValue("");
				}
	
			}
		});
		
	}

	// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		event.preventDefault();
		horasButton.setEnabled(true);
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * 38) + col;
	
		if (event.isControlKeyDown()) { //Pulsa celda con CTRL
			cells[row][col].select(row, col);
			
		} else if (event.isShiftKeyDown()){ //Pulsa celda con SHIFT
			int posicionActual = posicionesSeleccionas.get(0);
			int posicionFin = pos;
			
			if (posicionActual > posicionFin){
				int posAux = posicionActual;
				posicionActual = posicionFin-1;
				posicionFin = posAux;
			}
			
			while (posicionActual != posicionFin){
				cells[calcularFila(posicionActual+1)][calcularColumna(posicionActual+1)]
						.select(calcularFila(posicionActual+1), calcularColumna(posicionActual+1));
				posicionActual++;
			}
			
		} else { //Pulsa una sola celda
			
			for (Integer posList : posicionesSeleccionas) {
				int colSelect = calcularColumna(posList.intValue());
				int filSelect = calcularFila(posList.intValue());
				cells[filSelect][colSelect].unSelect(filSelect, colSelect);
			}
			
			posicionesSeleccionas.clear();
			cells[calcularFila(oldHourSelected)][calcularColumna(oldHourSelected)]
					.unSelect(calcularFila(oldHourSelected), calcularColumna(oldHourSelected));
			cells[row][col].select(row, col);
			oldHourSelected = pos;
		}
		
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
	}

	@UiHandler("eraseButton")
	public void onEraseClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			limpiarEstilos(posicionesSeleccionas);
		}
		posicionesSeleccionas.clear();
	}
	
	@UiHandler("diaNoLaborableButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsSunday(row, column);
		}	
		posicionesSeleccionas.clear();
	}

	@UiHandler("diaVacacionesButton")
	public void onVacacionesClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsHoliday(row, column);
		}
		posicionesSeleccionas.clear();
	}
	
	@UiHandler("diaAusenciaButton")
	public void onDiaAusenciaClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsDrop(row, column);
		}
		posicionesSeleccionas.clear();
	}

	@UiHandler("diaHuelgaButton")
	public void onHuelgaClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsStrike(row, column);
		}
		posicionesSeleccionas.clear();
	}

	@UiHandler("diaEreButton")
	public void onEreClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsEre(row, column);
		}
		posicionesSeleccionas.clear();
	}

	@UiHandler("diaReduccionButton")
	public void onReduccionClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsReduction(row, column);
		}
		posicionesSeleccionas.clear();
	}

	@UiHandler("diaSuspensionButton")
	public void onSuspensionClick(ClickEvent event) {
		limpiarEstilos(posicionesSeleccionas);
		for (Integer pos : posicionesSeleccionas) {
			int column = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			cells[row][column].unSelect(row, column);
			cells[row][column].setAsSuspension(row, column);
		}
		posicionesSeleccionas.clear();
	}

	@UiHandler("lastYearButton")
	public void onLastYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear - 1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		this.annio = parseNewYear;
		limpiarCalendario();
		mes = 0;
		
		inicializarCellsCalendar();
		mostrarCalendarioWidget(this.annio);

	}

	@UiHandler("nextYearButton")
	public void onNextYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + 1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		this.annio = parseNewYear;
		limpiarCalendario();
		mes = 0;
		
		inicializarCellsCalendar();
		mostrarCalendarioWidget(this.annio);
		
	}

	// ---------------------------------------------------------------- Metodos Auxiliares -------------------------------------------
	public void setEmployeeCalendarDraftObject(EmployeeCalendarDraftObjectData calendar) {
		this.calendarEmployeeInfo = calendar;
		this.annio = 117;
		this.mes = 0;
		initCalendar();
	}

	private void initCalendar() {
		this.startEmployeeContract = getStartYearContract(calendarEmployeeInfo.getStartDateContract());
		this.endEmployeeContract = getEndYearContract(calendarEmployeeInfo.getEndDateContract());
		int actualYear = this.annio+1900;
		this.yearLabel.setText(Integer.toString(actualYear));
		limpiarCalendario();
		//inicializarCellsCalendar();
		mostrarCalendarioWidget(this.annio);
		
	}
	
	private void mostrarCalendarioWidget(int annio) {
		this.nextYearButton.setEnabled(true);
		this.lastYearButton.setEnabled(true);
		
		if (annio == startEmployeeContract)
			this.lastYearButton.setEnabled(false);
		if (annio == endEmployeeContract)
			this.nextYearButton.setEnabled(false);
			
			
		//Crear calendario
		for (int row = 1; row < 25; row += 2)
			mostrarCalendario(row, annio);
	}

	private void inicializarCellsCalendar() {
		//Inicializar CalendarTypeCell -> Cells
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 38; column++)
				cells[row][column] = NoneCell.NONE_CELL;
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

			if (es(SUNDAY, i))
				diaInfo.setStyleName(style.sundayStyle());

			if (i < primerDiaMes) {
				diaInfo.setText("");
				calendarGrid.setWidget(row, i, diaInfo);
				
			} else {
				@SuppressWarnings("deprecation")
				Date actualDay = new Date(anio, mes, i);
				DoubleBox horas = new DoubleBox();
				horas.setValue(calendarEmployeeInfo.getHourByDay(actualDay));
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
				contadorDias++;
			}
		}

		int diaActualSemana = 1;
		while (contadorDias <= ultimoDiaMes) {
			// Dia Acutal
			@SuppressWarnings("deprecation")
			Date actualDay = new Date(anio, mes, 7 + diaActualSemana);
			// Label insetar
			Label diaInfo = new Label(contadorDias + "");
			diaInfo.setStyleName(style.cellStyle());
			//CeldaHora
			DoubleBox horas = new DoubleBox();
			horas.setValue(calendarEmployeeInfo.getHourByDay(actualDay));
			int filaHoras = row + 1;
			
			if (filaHoras % 4 == 0)
				horas.setStyleName(style.doubleBoxDisableStyle2());
			else
				horas.setStyleName(style.doubleBoxDisableStyle());

			if (es(SUNDAY, diaActualSemana))
				diaInfo.setStyleName(style.sundayStyle());

			calendarGrid.setWidget(row, 7 + diaActualSemana, diaInfo);
			calendarGrid.setWidget(row + 1, 7 + diaActualSemana, horas);
			cellsDates[row][7 + diaActualSemana] = actualDay;
			cells[row][7 + diaActualSemana] = new DayCell();
			cells[row + 1][7 + diaActualSemana] = new HourCell();
			contadorDias++;
			diaActualSemana++;
		}

		if ((diaActualSemana) < 30) {
			for (int i = 7 + diaActualSemana; i < 38; i++) {
				Label diaInfo = new Label("");
				diaInfo.setStyleName(style.cellStyle());
				calendarGrid.setWidget(row, i, diaInfo);
			}
		}

		mes++;
	}
	
	private void limpiarEstilos(List<Integer> posicionesSeleccionas) {
		for (Integer pos : posicionesSeleccionas) {
			int col = calcularColumna(pos.intValue());
			int fil = calcularFila(pos.intValue());
			calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.sundayStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.holidayStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.dropStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.strikeStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.ereStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.reductionStyle());
			calendarGrid.getWidget(fil, col).removeStyleName(style.suspensionStyle());
		}
	}
	
	private void limpiarSeleccion(List<Integer> posicionesSeleccionas) {
		for (Integer pos : posicionesSeleccionas) {
			int col = calcularColumna(pos.intValue());
			int fil = calcularFila(pos.intValue());
			calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
		}
	}

	
	private void limpiarCalendario() {
		for (int i = 1; i < 25; i++)
			for (int j = 1; j < 38; j++)
				calendarGrid.clearCell(i, j);
	}

	
	private boolean es(int day,int col) {
		return col==day+1 || col==day+8 || col==day+15 || col==day+22 || col==day+29 || col==day+36;
	}

	
	private int calcularColumna(int pos) {
		return pos % 38;
	}

	
	private int calcularFila(int pos) {
		return pos / 38;
	}
	
	
	private void actualizarHoras(double horasLunes, double horasMartes, double horasMiercoles, double horasJueves, double horasViernes, double horasSabado, double horasDomingo) {
		for (Integer pos : posicionesSeleccionas) {
			int col = calcularColumna(pos.intValue());
			int row = calcularFila(pos.intValue());
			if (es(SUNDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasDomingo);
				cells[row+1][col].setHour(row+1, col, horasDomingo);
			}else if (es(SATURDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasSabado);
				cells[row+1][col].setHour(row+1, col, horasSabado);
			}else if (es(FRIDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasViernes);
				cells[row+1][col].setHour(row+1, col, horasViernes);
			}else if (es(THURSDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasJueves);
				cells[row+1][col].setHour(row+1, col, horasJueves);
			}else if (es(WEDNESDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasMiercoles);
				cells[row+1][col].setHour(row+1, col, horasMiercoles);
			}else if (es(TUESDAY, col)){
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasMartes);
				cells[row+1][col].setHour(row+1, col, horasMartes);
			}else{ 
				calendarEmployeeInfo.setHourByDay(cellsDates[row][col], horasLunes);
				cells[row+1][col].setHour(row+1, col, horasLunes);
			}
		}
		limpiarSeleccion(posicionesSeleccionas);
	}

	
}
