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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
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

	private List<Integer> posicionesSeleccionas = new ArrayList<Integer>();
	
	public EmployeeCalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));
		for (int row = 1; row < 13; row++)
			mostrarCalendario(row, 117);
		
	}

	// --------------------------------------------- UiHandlers
	@UiHandler("calendarGrid")
	public void onCalendarClick(ClickEvent event) {
		int row = calendarGrid.getCellForEvent(event).getRowIndex();
		int cell = calendarGrid.getCellForEvent(event).getCellIndex();
		
		Window.alert("Fila: " + row + ", Columna: "+ cell);
		int pos = row*38;
		pos += cell;
		Window.alert("Posicion: "+pos);
		if(!celdaInvalida(row, cell)){
			if (event.isControlKeyDown())
				posicionesSeleccionas.add(pos);
			else{
				posicionesSeleccionas.clear();
				posicionesSeleccionas.add(pos);
			}
		}
	}
	
	@UiHandler("diaNoLaborableButton")
	public void onDiaNoLaborableClick(ClickEvent event) {
		if (!posicionesSeleccionas.isEmpty()){
			for (Integer pos : posicionesSeleccionas){
				int col = calcularColumna(pos.intValue());
				int fil = calcularFila(pos.intValue());
				
				calendarGrid.getWidget(col, fil).addStyleName(style.sundayStyle());
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
		for (int row = 1; row < 13; row++)
			mostrarCalendario(row, parseNewYear);	
	}
	
	
	@UiHandler("nextYearButton")
	public void onNextYearClick(ClickEvent event) {
		int actualYear = Integer.parseInt(yearLabel.getText());
		int newYear = actualYear+1;
		yearLabel.setText(Integer.toString(newYear));
		int parseNewYear = newYear - 1900;
		for (int row = 1; row < 13; row++)
			mostrarCalendario(row, parseNewYear);	
	}
	
	
	// --------------------------------------------- MetodosAuxiliares
	
	// --------------------------------------------- Metodos Auxiliares
	private int calcularNumeroDiaSemana (int dia, int mes, int anio){
		@SuppressWarnings("deprecation")
		Date fecha = new Date(anio, mes, dia);
		@SuppressWarnings("deprecation")
		int numDia = fecha.getDay();
		//Window.alert("Mes: "+mes+", Dia inicio: "+numDia);
		
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
				diaInfo.setText(contadorDias+"");
				calendarGrid.setWidget(row, i, diaInfo);
				contadorDias++;
			}
		}
		
		int diaActualSemana = 1;
		while (contadorDias <= ultimoDiaMes){
			//Label insetar
			Label diaInfo = new Label(contadorDias+"");
			diaInfo.setStyleName(style.cellStyle());
			
			if (esDomingo(diaActualSemana))
				diaInfo.setStyleName(style.sundayStyle());
			
			calendarGrid.setWidget(row, 7+diaActualSemana, diaInfo);
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

	private boolean esDomingo(int col){
		return col%7==0 || col%14==0 || col%21==0 || col%28==0 || col%35==0;
	}
	
	
	private boolean celdaInvalida(int row, int cell) {
		return row == 0 || cell%38 == 0; 
	}

	
	private int calcularColumna(int pos) {
		return pos/38;
	}

	private int calcularFila(int pos) {
		return pos/38;
	}

}
