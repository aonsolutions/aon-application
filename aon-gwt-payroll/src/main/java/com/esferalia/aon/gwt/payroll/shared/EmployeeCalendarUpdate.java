package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;

public class EmployeeCalendarUpdate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<java.util.Date, DayType> mapaTipoDias;
	private HashMap<java.util.Date, Double> mapaHorasDias;
	private Double coeficienteEre;
	
	public EmployeeCalendarUpdate() {
		super();
	}

	public EmployeeCalendarUpdate(HashMap<java.util.Date, DayType> mapaTipoDias, HashMap<java.util.Date, Double> mapaHorasDias,
			Double coeficienteEre) {
		super();
		this.mapaTipoDias = mapaTipoDias;
		this.mapaHorasDias = mapaHorasDias;
		this.coeficienteEre = coeficienteEre;
		
	}

	public HashMap<java.util.Date, DayType> getMapaTipoDias() {
		return mapaTipoDias;
	}

	public EmployeeCalendarUpdate setMapaTipoDias(HashMap<java.util.Date, DayType> mapaTipoDias) {
		this.mapaTipoDias = mapaTipoDias;
		return this;
	}

	public HashMap<java.util.Date, Double> getMapaHorasDias() {
		return mapaHorasDias;
	}

	public EmployeeCalendarUpdate setMapaHorasDias(HashMap<java.util.Date, Double> mapaHorasDias) {
		this.mapaHorasDias = mapaHorasDias;
		return this;
	}

	public Double getCoeficienteEre() {
		return coeficienteEre;
	}

	public EmployeeCalendarUpdate setCoeficienteEre(Double coeficienteEre) {
		this.coeficienteEre = coeficienteEre;
		return this;
	}
	
	
	

	
	
}
