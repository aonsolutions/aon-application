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
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class EmployeeCalendarDraftObjectData {

	private Map<Date,Double> mapaDiasHoras;
	private Map<Date, DayType> mapaDiasTipo;
	
	private Map<Date,Double> draftMapaDiasHoras;
	private Map<Date, DayType> draftMapaDiasTipo;
	
	private Date startContract;
	private Date endContract;
	
	private Integer employeeId;
	private EmployeesServiceAsync employeesService;

	private com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft;
	
	public UndoManager<Undoable> undoManager;
	
	
	
	public static interface DayTypeVisitor{
		void visitFreeDay(DayType dayType);
		void visitHolyDay(DayType dayType);
		void visitDropDay(DayType dayType);
		void visitEreDay(DayType dayType);
		void visitStrikeDay(DayType dayType);
		void visitReductionDay(DayType dayType);
		void visitSuspensionDay(DayType dayType);
		void visitNoTypeDay(DayType dayType);
	}
	
	public static enum DayType{
		FREEDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitFreeDay(this);
			}
		}, 
		HOLIDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitHolyDay(this);
			}
		}, 
		DROPDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitDropDay(this);
			}
		}, 
		STRIKEDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitStrikeDay(this);
			}
		}, 
		EREDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitEreDay(this);
			}
		}, 
		REDUCTIONDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitReductionDay(this);
			}
		}, 
		SUSPENSIONDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitSuspensionDay(this);
			}
		}, 
		NOTYPEDAY {
			@Override
			public void visit(DayTypeVisitor visitor) {
				visitor.visitNoTypeDay(this);
			}
		};
		
		public abstract void visit(DayTypeVisitor visitor); 
		
	};
	
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
		private StringVariable variable;
		
		public SetHourEdit(Double oldH, Double newH, Date actualDay, StringVariable var) {
			this.oldHour = oldH;
			this.newHour = newH;
			this.day = actualDay;
			this.variable = var;
		}
		
		@Override
		public void undo() {
			if (oldHour == null)
				draftMapaDiasHoras.remove(day);
			else
				draftMapaDiasHoras.put(day, oldHour);
			
			salaryDraft.removeDraftVariable(this.variable);
		}
		
		@Override
		public void redo() {
			draftMapaDiasHoras.put(day, newHour);
			salaryDraft.addDraftVariable(this.variable);
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
			if (oldType == null)
				draftMapaDiasTipo.remove(day);
			else
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

		this.employeeId = employeeId;
		this.employeesService = employeesService;
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
		
		@SuppressWarnings("deprecation")
		String name = calcularDiaSemana (dia.getDay()-1);
		
		StringVariable var = new StringVariable();
		var.setImplicit(false);
		var.setScope(Scope.SALARY); // DRAFT
		var.setName(name);
		var.setEndDate(dia);
		var.setStartDate(dia);
		var.setExpression(Double.toString(hour));
		
		this.salaryDraft.addDraftVariable(var);
		
		Window.alert("Horas: "+Double.toString(hour)+"Name: "+name+", Dia: "+dia.toGMTString());
		
		undoManager.add(new SetHourEdit(old, hour, dia, var));
	} 

	public void setHourByDay (Map<Date, Double> hours){
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Map.Entry<Date, Double> entry : hours.entrySet()) {
			Double old = draftMapaDiasHoras.put(entry.getKey(), entry.getValue());
			
			@SuppressWarnings("deprecation")
			String name = calcularDiaSemana (entry.getKey().getDay());
			
			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(name);
			var.setEndDate(entry.getKey());
			var.setStartDate(entry.getKey());
			var.setExpression(Double.toString(entry.getValue()));
			
			this.salaryDraft.addDraftVariable(var);
			
			undos.add(new SetHourEdit(old, entry.getValue(), entry.getKey(), var));
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

	public void setSalaryDraft(com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft2) {
		this.salaryDraft = salaryDraft2;
	}
	
	private String calcularDiaSemana(int day) {
		String result = "";
		switch (day) {
		case 0:
			result = "HORAS_DOMINGO";
			break;
		case 1:
			result = "HORAS_LUNES";
			break;
		case 2:
			result = "HORAS_MARTES";
			break;
		case 3:
			result = "HORAS_MIERCOLES";
			break;
		case 4:
			result = "HORAS_JUEVES";
			break;
		case 5:
			result = "HORAS_VIERNES";
			break;
		default:
			result = "HORAS_SABADO";
			break;
		}
		
		return result;
	}
	
	private void init() {
		employeesService.getEmployeeCalendar(employeeId, 
			new AsyncCallback<EmployeeCalendarData>() {
			
			@Override
			public void onSuccess(EmployeeCalendarData result) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
