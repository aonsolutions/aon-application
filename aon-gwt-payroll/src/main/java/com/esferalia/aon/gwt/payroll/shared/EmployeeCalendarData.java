package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class EmployeeCalendarData implements Serializable {
	private ArrayList<Quartet<Date, Date, String, String>> listaHorasContrato;
	private ArrayList<Quartet<Date, Date, String, String>> listaTipoDiasContrato;
	
	public EmployeeCalendarData() {
		super();
	}

	public EmployeeCalendarData(ArrayList<Quartet<Date, Date, String, String>> listaHorasContrato,
			ArrayList<Quartet<Date, Date, String, String>> listaTipoDiasContrato) {
		super();
		this.listaHorasContrato = listaHorasContrato;
		this.listaTipoDiasContrato = listaTipoDiasContrato;
	}

	public List<Quartet<Date, Date, String, String>> getListaHorasContrato() {
		return listaHorasContrato;
	}

	public EmployeeCalendarData setListaHorasContrato(ArrayList<Quartet<Date, Date, String, String>> listaHorasContrato) {
		this.listaHorasContrato = listaHorasContrato;
		return this;
	}

	public List<Quartet<Date, Date, String, String>> getListaTipoDiasContrato() {
		return listaTipoDiasContrato;
	}

	public EmployeeCalendarData setListaTipoDiasContrato(ArrayList<Quartet<Date, Date, String, String>> listaTipoDiasContrato) {
		this.listaTipoDiasContrato = listaTipoDiasContrato;
		return this;
	}
	
	
}
