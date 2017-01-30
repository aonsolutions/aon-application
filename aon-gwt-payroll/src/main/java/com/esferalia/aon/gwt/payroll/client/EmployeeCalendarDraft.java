package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperDialog;
import com.vaadin.polymer.paper.widget.PaperMenu;

public class EmployeeCalendarDraft extends Composite {

	private static EmployeeCalendarDraftUiBinder uiBinder = GWT.create(EmployeeCalendarDraftUiBinder.class);

	interface EmployeeCalendarDraftUiBinder extends UiBinder<Widget, EmployeeCalendarDraft> {
	}
	
	// -------------------------------------------------- INTERFAZ MULTISELECCION CELDAS -----------------------------------------------
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
	PaperButton confirmOk;
	
	@UiField
	PaperDialog confirm;

	@UiField
	MenuItem horasMenuItem;
	
	@UiField
	MenuItem horasButton;
	
	@UiField
	PaperMenu lunesOpt;
	
	@UiField
	PaperMenu martesOpt;
	
	@UiField
	PaperMenu miercolesOpt;
	
	@UiField
	PaperMenu juevesOpt;
	
	@UiField
	PaperMenu viernesOpt;
	
	@UiField
	PaperMenu sabadoOpt;
	
	@UiField
	PaperMenu domingoOpt;

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
		
		horasButton.setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				confirm.setWithBackdrop(true);
				confirm.setHorizontalAlign("auto");
				confirm.setHorizontalOffset(5);
				confirm.setVerticalAlign("auto");
				confirm.setVerticalOffset(5);
				confirm.setVisible(true);
			}
		});
		
		confirmOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				double horasLunes = (((double)lunesOpt.getSelected())+1)*2;
				double horasMartes = (((double)martesOpt.getSelected())+1)*2;
				double horasMiercoles = (((double)miercolesOpt.getSelected())+1)*2;
				double horasJueves = (((double)juevesOpt.getSelected())+1)*2;
				double horasViernes = (((double)viernesOpt.getSelected())+1)*2;
				double horasSabado = (((double)sabadoOpt.getSelected())+1)*2;
				double horasDomingo = (((double)domingoOpt.getSelected())+1)*2;
				actualizarHoras(horasLunes, horasMartes, horasMiercoles, horasJueves, horasViernes, horasSabado, horasDomingo);
				confirm.setVisible(false);
				horasButton.setEnabled(false);
				
			}

			private void actualizarHoras(double horasLunes, double horasMartes, double horasMiercoles, double horasJueves, double horasViernes, double horasSabado, double horasDomingo) {
				for (Integer pos : posicionesSeleccionas) {
					int col = calcularColumna(pos.intValue());
					int row = calcularFila(pos.intValue());
					if (esDomingo(col))
						cells[row+1][col].setHour(row+1, col, horasDomingo);
					else if (esSabado(col))
						cells[row+1][col].setHour(row+1, col, horasSabado);
					else if (esViernes(col))
						cells[row+1][col].setHour(row+1, col, horasViernes);
					else if (esJueves(col))
						cells[row+1][col].setHour(row+1, col, horasJueves);
					else if (esMiercoles(col))
						cells[row+1][col].setHour(row+1, col, horasMiercoles);
					else if (esMartes(col))
						cells[row+1][col].setHour(row+1, col, horasMartes);
					else 
						cells[row+1][col].setHour(row+1, col, horasLunes);
				}
				limpiarSeleccion(posicionesSeleccionas);
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

	private boolean esDomingo(int col) {
		return col==7 || col==14 || col==21 || col==28 || col==35;
	}
	
	private boolean esSabado(int col) {
		return col==6 || col==13 || col==20 || col==27 || col==34;
	}
	
	private boolean esViernes(int col) {
		return col==5 || col==12 || col==19 || col==26 || col==33;
	}
	
	private boolean esJueves(int col) {
		return col==4 || col==11 || col==18 || col==25 || col==32;
	}
	
	private boolean esMiercoles(int col) {
		return col==3 || col==10 || col==17 || col==24 || col==31;
	}
	
	private boolean esMartes(int col) {
		return col==2 || col==9 || col==16 || col==23 || col==30 || col==37;
	}
	
	private boolean esLunes(int col) {
		return col==1 || col==8 || col==15 || col==22 || col==29 || col==36;
	}

	private int calcularColumna(int pos) {
		return pos % 38;
	}

	private int calcularFila(int pos) {
		return pos / 38;
	}

}
