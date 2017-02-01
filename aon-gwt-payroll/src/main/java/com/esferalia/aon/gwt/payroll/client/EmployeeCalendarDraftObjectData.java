package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmployeeCalendarDraftObjectData {

	private Map<Date,Double> mapaDiasHoras;
	private Map<Date, DayType> mapaDiasTipo;
	
	private Map<Date,Double> draftMapaDiasHoras;
	private Map<Date, DayType> draftMapaDiasTipo;
	
	private Date startContract;
	private Date endContract;
	private enum DayType{FREEDAY, HOLIDAY, DROPDAY, STRIKEDAY, EREDAY, REDUCTIONDAY, SUSPENSIONDAY, NOTYPEDAY};
	
	public EmployeeCalendarDraftObjectData(Integer employeeId,
			Date startContract, Date endContract, EmployeesServiceAsync employeesService) {
		// TODO Auto-generated constructor stub
		this.mapaDiasHoras = new HashMap<Date,Double>();
		this.mapaDiasTipo = new HashMap<Date,DayType>();
		
		this.draftMapaDiasHoras = new HashMap<Date,Double>();
		this.draftMapaDiasTipo = new HashMap<Date,DayType>();
		
		this.startContract = startContract;
		this.endContract = endContract;
	}
	
	public DayType getTypeByDay (Date dia){
		DayType typeDayDraft = draftMapaDiasTipo.get(dia);
		
		if (typeDayDraft != null)
			return typeDayDraft;
		else	
			return mapaDiasTipo.getOrDefault(dia, DayType.NOTYPEDAY);
	}
	
	public void setTypeByDay (Date dia, DayType typeDay){
		draftMapaDiasTipo.put(dia, typeDay); 
	} 
	
	public double getHourByDay (Date dia){
		Double hourDayDraft = draftMapaDiasHoras.get(dia);
		
		if (hourDayDraft != null)
			return hourDayDraft;
		else
			return mapaDiasHoras.getOrDefault(dia, (double) 0);
	}
	
	public void setHourByDay (Date dia, Double hour){
		draftMapaDiasHoras.put(dia, hour); 
	} 
	
	public Date getStartDateContract(){
		return startContract;
	}
	
	public Date getEndDateContract(){
		return endContract;
	}
}
