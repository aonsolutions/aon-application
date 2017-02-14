package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;

public class EmployeeCalendarUpdate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<java.util.Date, DayType> mapaTipoDias;
	private HashMap<java.util.Date, Double> mapaHorasDias;
	
	public EmployeeCalendarUpdate() {
		super();
	}

	public EmployeeCalendarUpdate(HashMap<java.util.Date, DayType> mapaTipoDias, HashMap<java.util.Date, Double> mapaHorasDias) {
		super();
		this.mapaTipoDias = mapaTipoDias;
		this.mapaHorasDias = mapaHorasDias;
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
	
	

	
	
}
