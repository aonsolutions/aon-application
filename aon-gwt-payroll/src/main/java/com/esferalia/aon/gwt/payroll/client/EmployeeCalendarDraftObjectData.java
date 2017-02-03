package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.Undoable;

public class EmployeeCalendarDraftObjectData {

	private Map<Date,Double> mapaDiasHoras;
	private Map<Date, DayType> mapaDiasTipo;
	
	private Map<Date,Double> draftMapaDiasHoras;
	private Map<Date, DayType> draftMapaDiasTipo;
	
	private Date startContract;
	private Date endContract;
	
	public UndoManager<Undoable> undoManager;
	
	private enum DayType{FREEDAY, HOLIDAY, DROPDAY, STRIKEDAY, EREDAY, REDUCTIONDAY, SUSPENSIONDAY, NOTYPEDAY};
	
	// --------------------------------------------- INTERFAZ REDO/UNDO -----------------------------------------------
	
	private class CompositeUndoable<T extends Undoable > implements Undoable {

		private Collection<T> undos;

		public CompositeUndoable(Collection<T> undos) {
			this.undos = undos;
		}

		@Override
		public void redo() {
			for (T undo : undos)
				undo.redo();
		}

		@Override
		public void undo() {
			for (T undo : undos){
				undo.undo();
			}
		}

	}
	
	class SetHourEdit implements Undoable {

		private Double oldHour;
		private Double newHour;
		private Date day;
		
		public SetHourEdit(Double oldH, Double newH, Date actualDay) {
			this.oldHour = oldH;
			this.newHour = newH;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			draftMapaDiasHoras.put(day, oldHour);
		}
		
		@Override
		public void redo() {
			draftMapaDiasHoras.put(day, newHour);
		}
	}
	
	class SetTypeEdit implements Undoable {

		private DayType oldType;
		private DayType newType;
		private Date day;
		
		public SetTypeEdit(DayType oldT, DayType newT, Date actualDay) {
			this.oldType = oldT;
			this.newType = newT;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			draftMapaDiasTipo.put(day, oldType);
		}
		
		@Override
		public void redo() {
			draftMapaDiasTipo.put(day, newType);
		}
	}
	
	// ---------------------------------------------- METODOS DE LA CLASE ---------------------------------------------	
	public EmployeeCalendarDraftObjectData(Integer employeeId,
			Date startContract, Date endContract, EmployeesServiceAsync employeesService) {
		this.undoManager = new UndoManager<>();
		
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
		DayType old = draftMapaDiasTipo.put(dia, typeDay);
		undoManager.add(new SetTypeEdit(old, typeDay, dia));
	} 
	
	public void setTypeByDay (Map<Date, DayType> types){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, DayType> entry : types.entrySet()) {
			DayType old = draftMapaDiasTipo.put(entry.getKey(), entry.getValue());
			undos.add(new SetTypeEdit(old, entry.getValue(), entry.getKey()));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	}
	

	public double getHourByDay (Date dia){
		Double hourDayDraft = draftMapaDiasHoras.get(dia);
		
		if (hourDayDraft != null)
			return hourDayDraft;
		else
			return mapaDiasHoras.getOrDefault(dia, (double) 0);
	}
	
	public void setHourByDay (Date dia, Double hour){
		Double old = draftMapaDiasHoras.put(dia, hour);
		undoManager.add(new SetHourEdit(old, hour, dia));
	} 
	
	public void setHourByDay (Map<Date, Double> hours){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, Double> entry : hours.entrySet()) {
			Double old = draftMapaDiasHoras.put(entry.getKey(), entry.getValue());
			undos.add(new SetHourEdit(old, entry.getValue(), entry.getKey()));
		}
		undoManager.add(new CompositeUndoable<Undoable>(undos));
	} 

	public Set<Entry<Date, Double>> getHourChanges(){
		return draftMapaDiasHoras.entrySet();
	}
	
	public Set<Entry<Date, DayType>> getTypeChanges(){
		return draftMapaDiasTipo.entrySet();
	}
	
	public Date getStartDateContract(){
		return startContract;
	}
	
	public Date getEndDateContract(){
		return endContract;
	}
}
