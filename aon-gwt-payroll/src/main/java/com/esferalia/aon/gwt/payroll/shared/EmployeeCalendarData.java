package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class EmployeeCalendarData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Quartet<Date, Date, String, String>> listaHorasContrato;
	private ArrayList<Quartet<Date, Date, String, String>> listaTipoDiasContrato;
	private ArrayList<java.util.Date> listaFestivosContrato;
	private ArrayList<Byte> listaNoLaborablesContrato;
	
	public EmployeeCalendarData() {
		super();
	}

	public EmployeeCalendarData(ArrayList<Quartet<Date, Date, String, String>> listaHorasContrato,
			ArrayList<Quartet<Date, Date, String, String>> listaTipoDiasContrato,
			ArrayList<java.util.Date> listaFestivosContrato,
			ArrayList<Byte> listaNoLaborablesContrato) {
		super();
		this.listaHorasContrato = listaHorasContrato;
		this.listaTipoDiasContrato = listaTipoDiasContrato;
		this.listaFestivosContrato = listaFestivosContrato;
		this.listaNoLaborablesContrato = listaNoLaborablesContrato;
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

	public ArrayList<java.util.Date> getListaFestivosContrato() {
		return listaFestivosContrato;
	}

	public EmployeeCalendarData setListaFestivosContrato(ArrayList<java.util.Date> listaFestivosContrato) {
		this.listaFestivosContrato = listaFestivosContrato;
		return this;
	}

	public ArrayList<Byte> getListaNoLaborablesContrato() {
		return listaNoLaborablesContrato;
	}

	public EmployeeCalendarData setListaNoLaborablesContrato(ArrayList<Byte> listaNoLaborablesContrato) {
		this.listaNoLaborablesContrato = listaNoLaborablesContrato;
		return this;
	}
	
	
}
