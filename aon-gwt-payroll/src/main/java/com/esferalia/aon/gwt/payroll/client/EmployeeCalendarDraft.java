package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperButton;

public class EmployeeCalendarDraft extends Composite {


	private static EmployeeCalendarDraftUiBinder uiBinder = GWT
			.create(EmployeeCalendarDraftUiBinder.class);

	interface EmployeeCalendarDraftUiBinder extends UiBinder<Widget, EmployeeCalendarDraft> {
	}
	
	@UiField
    MyStyle style;
	
	interface MyStyle extends CssResource {
        String sundayStyle();
        String cellStyle();
        String holidayStyle();
        String dropStyle();
        String isSelectedStyle();
        String doubleBoxStyle();
        String doubleBoxDisableStyle();
        String doubleBoxDisableStyle2();
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
	PaperButton diaVacacionesButton;
	
	@UiField
	PaperButton diaBajaButton;
	
	@UiField
	PaperButton eraseButton;

	private List<Integer> posicionesSeleccionas = new ArrayList<Integer>();
	private int oldHourSelected = 0;
	
	public EmployeeCalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));
		for (int row = 1; row < 25; row+=2)
			mostrarCalendario(row, 117);
		
	}

	// --------------------------------------------- UiHandlers
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int cell = calendarGrid.getCellForEvent(event).getCellIndex();
		//calendarGrid.getWidget(row, cell).setStyleName(style.doubleBoxDisableStyle());
		
		int pos = row*38;
		pos += cell;
		
		if(celdaValida(row, cell)){
			if (event.isControlKeyDown()){
				posicionesSeleccionas.add(pos);
				calendarGrid.getWidget(row, cell).addStyleName(style.isSelectedStyle());
			}else{
				for (Integer posList : posicionesSeleccionas){
					int col = calcularColumna(posList.intValue());
					int fil = calcularFila(posList.intValue());
					calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				}
				posicionesSeleccionas.clear();
				posicionesSeleccionas.add(pos);
				calendarGrid.getWidget(row, cell).addStyleName(style.isSelectedStyle());
			}
		} else if (celdaHoras(row, cell)){
			setNoSelected(oldHourSelected);
			
			//Darle estilo de "seleccionado"
			calendarGrid.getWidget(row, cell).removeStyleName(style.isSelectedStyle());
			calendarGrid.getWidget(row, cell).removeStyleName(style.doubleBoxDisableStyle());
			calendarGrid.getWidget(row, cell).setStyleName(style.doubleBoxStyle());
			
			oldHourSelected = pos;
		}
	}
	

	private void setNoSelected(int oldHourSelected) {
		//Darle estilo de "no seleccionado"
		int col = calcularColumna(oldHourSelected);
		int fil = calcularFila(oldHourSelected);
		calendarGrid.getWidget(fil, col).setStyleName(style.doubleBoxDisableStyle());
		
	}

	
	@UiHandler("eraseButton")
	public void onEraseClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()){
			for (Integer pos : posicionesSeleccionas){
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).removeStyleName(style.dropStyle());
				calendarGrid.getWidget(fil, col).removeStyleName(style.holidayStyle());
				calendarGrid.getWidget(fil, col).removeStyleName(style.sundayStyle());
			}
		}
		posicionesSeleccionas.clear();
		
	}
	
	@UiHandler("diaNoLaborableButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()){
			for (Integer pos : posicionesSeleccionas){
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.sundayStyle());
			}
		}
		posicionesSeleccionas.clear();
		
	}
	
	@UiHandler("diaVacacionesButton")
	public void onVacacionesClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()){
			for (Integer pos : posicionesSeleccionas){
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.holidayStyle());
			}
		}
		posicionesSeleccionas.clear();
		
	}
	
	@UiHandler("diaBajaButton")
	public void onBajaClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()){
			for (Integer pos : posicionesSeleccionas){
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				calendarGrid.getWidget(fil, col).removeStyleName(style.isSelectedStyle());
				calendarGrid.getWidget(fil, col).addStyleName(style.dropStyle());
			}
		}
		posicionesSeleccionas.clear();
		
	}
	
	@UiHandler("lastYearButton")
	public void onLastYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear-1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		limpiarCalendario();
		for (int row = 1; row < 25; row+=2)
			mostrarCalendario(row, parseNewYear);	
	}
	
	@UiHandler("nextYearButton")
	public void onNextYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear+1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		limpiarCalendario();
		for (int row = 1; row < 25; row+=2)
			mostrarCalendario(row, parseNewYear);	
	}
	
	
	// --------------------------------------------- Metodos Auxiliares
	private int calcularNumeroDiaSemana (int dia, int mes, int anio){
		@SuppressWarnings("deprecation")
		Date fecha = new Date(anio, mes, dia);
		@SuppressWarnings("deprecation")
		int numDia = fecha.getDay();
		
		//Tratamiento calendario español, 0 = Lunes, 6 = Domingo
		if (0 == numDia)
			numDia = 7;
		
		return numDia;
	}
	
	@SuppressWarnings("deprecation")
	private boolean comprobarFecha (int dia, int mes, int anio){
		return mes >= 0 && mes <= 11 && anio > 0 && anio < 32768 && dia >= 0 && dia <= (new Date(anio, mes, dia)).getDate(); 
	}
	
	private int calcularUltimoDiaMes (int mes, int anio){
		int ultimo_dia = 28;
		while (comprobarFecha(ultimo_dia+1, mes, anio))
			ultimo_dia++;
	
		return ultimo_dia;
	}
	
	private void mostrarCalendario(int row, int anio){
		
		//Mostrar días del mes
		int contadorDias = 1;
		int mes = row-1;
		int primerDiaMes = calcularNumeroDiaSemana(1, mes, anio);
		int ultimoDiaMes = calcularUltimoDiaMes(mes, anio);
		
		//Escribo la primera fila de la semana
		for (int i = 1; i <=7; i++){
			//Label insetar
			Label diaInfo = new Label();
			diaInfo.setStyleName(style.cellStyle());
			
			if (esDomingo(i))
				diaInfo.setStyleName(style.sundayStyle());
			
			if (i < primerDiaMes){
				diaInfo.setText("");
				calendarGrid.setWidget(row, i, diaInfo);
			}else {
				DoubleBox horas = new DoubleBox();
				horas.setValue(8.00);
				int filaHoras = row+1;
				if (filaHoras % 4 == 0)
					horas.setStyleName(style.doubleBoxDisableStyle2());
				else
					horas.setStyleName(style.doubleBoxDisableStyle());
				
				diaInfo.setText(contadorDias+"");
				calendarGrid.setWidget(row, i, diaInfo);
				calendarGrid.setWidget(filaHoras, i, horas);
				contadorDias++;
			}
		}
		
		int diaActualSemana = 1;
		while (contadorDias <= ultimoDiaMes){
			//Label insetar
			Label diaInfo = new Label(contadorDias+"");
			diaInfo.setStyleName(style.cellStyle());
			
			DoubleBox horas = new DoubleBox();
			horas.setValue(8.00);
			int filaHoras = row+1;
			if (filaHoras % 4 == 0)
				horas.setStyleName(style.doubleBoxDisableStyle2());
			else
				horas.setStyleName(style.doubleBoxDisableStyle());
			
			if (esDomingo(diaActualSemana))
				diaInfo.setStyleName(style.sundayStyle());
			
			calendarGrid.setWidget(row, 7+diaActualSemana, diaInfo);
			calendarGrid.setWidget(filaHoras, 7+diaActualSemana, horas);
			contadorDias++;
			diaActualSemana++;
		}
		
		if((diaActualSemana)<30){
			for (int i=7+diaActualSemana; i<38; i++){
				Label diaInfo = new Label("");
				diaInfo.setStyleName(style.cellStyle());
				calendarGrid.setWidget(row, i, diaInfo);
			}
		}
	}

	private void limpiarCalendario(){
		for (int i=1; i<25; i++)
			for (int j=1; j<38; j++)
				calendarGrid.clearCell(i, j);
	}
	
	private boolean esDomingo(int col){
		return col%7==0 || col%14==0 || col%21==0 || col%28==0 || col%35==0;
	}
	
	private boolean celdaValida(int row, int cell) {
		return row%2 != 0 && cell%38 != 0; 
	}

	private boolean celdaHoras(int row, int cell) {
		return row%2 == 0 || cell%38 != 0;
	}
	
	private int calcularColumna(int pos) {
		return pos%38;
	}

	private int calcularFila(int pos) {
		return pos/38;
	}

}
