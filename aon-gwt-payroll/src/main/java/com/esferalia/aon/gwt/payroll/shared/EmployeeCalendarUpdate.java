package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;

public class EmployeeCalendarUpdate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<Date, DayType> mapaTipoDias;
	private HashMap<Date, Double> mapaHorasDias;
	
	public EmployeeCalendarUpdate() {
		super();
	}

	public EmployeeCalendarUpdate(HashMap<Date, DayType> mapaTipoDias, HashMap<Date, Double> mapaHorasDias) {
		super();
		this.mapaTipoDias = mapaTipoDias;
		this.mapaHorasDias = mapaHorasDias;
	}

	public HashMap<Date, DayType> getMapaTipoDias() {
		return mapaTipoDias;
	}

	public EmployeeCalendarUpdate setMapaTipoDias(HashMap<Date, DayType> mapaTipoDias) {
		this.mapaTipoDias = mapaTipoDias;
		return this;
	}

	public HashMap<Date, Double> getMapaHorasDias() {
		return mapaHorasDias;
	}

	public EmployeeCalendarUpdate setMapaHorasDias(HashMap<Date, Double> mapaHorasDias) {
		this.mapaHorasDias = mapaHorasDias;
		return this;
	}
	
	

	
	
}
