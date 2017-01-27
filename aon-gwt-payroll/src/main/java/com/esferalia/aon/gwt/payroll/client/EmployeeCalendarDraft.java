package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperButton;

public class EmployeeCalendarDraft extends Composite {

	private static EmployeeCalendarDraftUiBinder uiBinder = GWT.create(EmployeeCalendarDraftUiBinder.class);

	interface EmployeeCalendarDraftUiBinder extends UiBinder<Widget, EmployeeCalendarDraft> {
	}
	
	// -------------------------------------------------- INTERFAZ MULTISELECCION CELDAS -----------------------------------------------
	public static interface CalendarTypeCell{
		void select(int row, int col);
		void unSelect(int row, int col);
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
			calendarGrid.getWidget(row, col).setStyleName(style.doubleBoxDisableStyle());
			//calendarGrid.getWidget(row, col).setStyleName(style.doubleBoxDisableStyle2());
			
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
	} 

	//--------------------------------------------------------UiFields--------------------------------------------------------------------
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
	MenuItem horasMenuItem;

	private List<Integer> posicionesSeleccionas = new ArrayList<Integer>();
	private int oldHourSelected = 0;
	int mes = 0;
	boolean mostrarHoras = true;
	CalendarTypeCell cells[][] = new CalendarTypeCell[25][38];
	
	
	public EmployeeCalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));
		
		//Inicializar CalendarTypeCell -> Cells
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 38; column++)
				cells[row][column] = NoneCell.NONE_CELL;
		
		//Crear calendario
		for (int row = 1; row < 25; row += 2)
			mostrarCalendario(row, 117);
		
		// HandlerRegistration hr =
		// calendarGrid.addClickHandler(this::onCalendarClick);
		// hr.removeHandler();
		
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
	}

	// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		event.preventDefault();
		
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int col = calendarGrid.getCellForEvent(event).getCellIndex();
		int pos = (row * 38) + col;
	
		if (event.isControlKeyDown()) { //Pulsa celda con CTRL
			cells[row][col].select(row, col);
			
		} else if (event.isShiftKeyDown()){
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

	@UiHandler("calendarGrid")
	public void onDragStart(DragStartEvent event) {
		//TODO: ver como hacer el drag con el raton en vez de con shift
	}

	@UiHandler("eraseButton")
	public void onEraseClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
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
		posicionesSeleccionas.clear();
	}

	@UiHandler("diaNoLaborableButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.sundayStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("diaAusenciaButton")
	public void onDiaAusenciaClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.dropStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("diaHuelgaButton")
	public void onHuelgaClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.strikeStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("diaEreButton")
	public void onEreClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.ereStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("diaReduccionButton")
	public void onReduccionClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.reductionStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("diaVacacionesButton")
	public void onVacacionesClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.holidayStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("diaSuspensionButton")
	public void onSuspensionClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()) {
			for (Integer pos : posicionesSeleccionas) {
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.suspensionStyle());
			}
		}
		posicionesSeleccionas.clear();

	}

	@UiHandler("lastYearButton")
	public void onLastYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear - 1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		limpiarCalendario();
		mes = 0;
		//Inicializar CalendarTypeCell -> Cells
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 38; column++)
				cells[row][column] = NoneCell.NONE_CELL;
		for (int row = 1; row < 25; row += 2)
			mostrarCalendario(row, parseNewYear);
	}

	@UiHandler("nextYearButton")
	public void onNextYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear + 1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		limpiarCalendario();
		mes = 0;
		//Inicializar CalendarTypeCell -> Cells
		for (int row = 0; row < 25; row++)
			for (int column = 0; column < 38; column++)
				cells[row][column] = NoneCell.NONE_CELL;

		for (int row = 1; row < 25; row += 2)
			mostrarCalendario(row, parseNewYear);
	}

	// ---------------------------------------------------------------- Metodos Auxiliares -------------------------------------------
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

			if (esDomingo(i))
				diaInfo.setStyleName(style.sundayStyle());

			if (i < primerDiaMes) {
				diaInfo.setText("");
				calendarGrid.setWidget(row, i, diaInfo);
				
			} else {
				DoubleBox horas = new DoubleBox();
				horas.setValue(8.00);
				int filaHoras = row + 1;
				if (filaHoras % 4 == 0)
					horas.setStyleName(style.doubleBoxDisableStyle2());
				else
					horas.setStyleName(style.doubleBoxDisableStyle());

				diaInfo.setText(contadorDias + "");
				calendarGrid.setWidget(row, i, diaInfo);
				calendarGrid.setWidget(row + 1, i, horas);
				cells[row][i] = new DayCell();
				cells[row + 1][i] = new HourCell();
				contadorDias++;
			}
		}

		int diaActualSemana = 1;
		while (contadorDias <= ultimoDiaMes) {
			// Label insetar
			Label diaInfo = new Label(contadorDias + "");
			diaInfo.setStyleName(style.cellStyle());
			//CeldaHora
			DoubleBox horas = new DoubleBox();
			horas.setValue(8.00);
			int filaHoras = row + 1;
			
			if (filaHoras % 4 == 0)
				horas.setStyleName(style.doubleBoxDisableStyle2());
			else
				horas.setStyleName(style.doubleBoxDisableStyle());

			if (esDomingo(diaActualSemana))
				diaInfo.setStyleName(style.sundayStyle());

			calendarGrid.setWidget(row, 7 + diaActualSemana, diaInfo);
			calendarGrid.setWidget(row + 1, 7 + diaActualSemana, horas);
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

	private void limpiarCalendario() {
		for (int i = 1; i < 25; i++)
			for (int j = 1; j < 38; j++)
				calendarGrid.clearCell(i, j);
	}

	private boolean esDomingo(int col) {
		return col % 7 == 0 || col % 14 == 0 || col % 21 == 0 || col % 28 == 0 || col % 35 == 0;
	}

	private int calcularColumna(int pos) {
		return pos % 38;
	}

	private int calcularFila(int pos) {
		return pos / 38;
	}

}
