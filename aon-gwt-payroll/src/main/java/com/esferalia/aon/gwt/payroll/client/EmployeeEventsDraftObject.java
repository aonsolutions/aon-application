package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;

public class EmployeeEventsDraftObject {
	
	public interface EVENTimedVariable<V> {
		
		public Date getStartDate();
		public Date getEndDate();
		public V getValue();

	}
	
	public class EmployeeEventsVariable implements EVENTimedVariable<Double>{

		private Date startDate;
		private Date endDate;
		private Double value;
		
		public EmployeeEventsVariable(Date startDate, Date endDate, Double value) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.value = value;
		}
		
		public EmployeeEventsVariable() {
			this.startDate = null;
			this.endDate = null;
			this.value = null;
		}

		@Override
		public Date getStartDate() {
			return this.startDate;
		}

		@Override
		public Date getEndDate() {
			return this.endDate;
		}

		@Override
		public Double getValue() {
			return this.value;
		}
		
	}
	
	// --------------------------------------------- INTERFACE REDO/UNDO -----------------------------------------------
	
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
		
	class SetVariableEdit implements Undoable {

		private EmployeeEventsVariable oldEmployeeEventsVariable;
		private EmployeeEventsVariable newEmployeeEventsVariable;
		private String variable;
		
		public SetVariableEdit(EmployeeEventsVariable oldEmployeeEventsVariable, EmployeeEventsVariable newEmployeeEventsVariable, 
							String variable) {
			this.oldEmployeeEventsVariable = oldEmployeeEventsVariable;
			this.newEmployeeEventsVariable = newEmployeeEventsVariable;
			this.variable = variable;
		}
		
		@Override
		public void undo() {
			draftMapEventsVar.get(this.variable).remove(newEmployeeEventsVariable);
			if (oldEmployeeEventsVariable != null){
				draftMapEventsVar.get(this.variable).add(oldEmployeeEventsVariable);
			}
		}
		
		@Override
		public void redo() {
			if (oldEmployeeEventsVariable != null)
				draftMapEventsVar.get(this.variable).remove(this.oldEmployeeEventsVariable);
			
			draftMapEventsVar.get(this.variable).add(this.newEmployeeEventsVariable);
		}
		
	}
		
	
	/**
	 * DECLARACION DE VARIABLES Y CONSTRUCTOR
	 */
	
	private Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar;
	private Map<String, ArrayList<EmployeeEventsVariable>> draftMapEventsVar;
	private Integer idEmployee;
	private EmployeesServiceAsync employeesService;
	public UndoManager<Undoable> undoManager;
	
	public EmployeeEventsDraftObject(Integer idEmployee, EmployeesServiceAsync employeesService) {
		this.mapEventsVar = new HashMap<String, ArrayList<EmployeeEventsDraftObject.EmployeeEventsVariable>>();
		crearMapaEmployeeEvents();
		this.draftMapEventsVar = new HashMap<String, ArrayList<EmployeeEventsDraftObject.EmployeeEventsVariable>>();
		
		this.idEmployee = idEmployee;
		this.employeesService = employeesService;
		
		this.undoManager = new UndoManager<>();
	}

	/**
	 * GETTERS / SETTERS
	 */
	
	public Map<String, ArrayList<EmployeeEventsVariable>> getMapEventsVar() {
		return mapEventsVar;
	}

	public void setMapEventsVar(Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar) {
		this.mapEventsVar = mapEventsVar;
	}

	public Map<String, ArrayList<EmployeeEventsVariable>> getDraftMapEventsVar() {
		return draftMapEventsVar;
	}

	public void setDraftMapEventsVar(Map<String, ArrayList<EmployeeEventsVariable>> draftMapEventsVar) {
		this.draftMapEventsVar = draftMapEventsVar;
	}

	public Integer getIdEmployee() {
		return idEmployee;
	}

	/**
	 * METODO GETTERS Y SETTERS AUXILIARES
	 * @return 
	 */
	
	public ArrayList<EmployeeEventsVariable> getListEmployeeEventsVaribales (String varName){
		if (null != draftMapEventsVar.get(varName))
			return draftMapEventsVar.get(varName);
		else
			return mapEventsVar.getOrDefault(varName, null);
	}
	
	@SuppressWarnings("deprecation")
	public EmployeeEventsVariable getEmployeeEventsVariableByMonth (String varName, int month, Integer year){
		if (null != draftMapEventsVar.get(varName))
			for (EmployeeEventsVariable e : draftMapEventsVar.get(varName)){
				if(year != e.getStartDate().getYear())
					continue;
				if (month == e.getStartDate().getMonth())
					return e;
			}
		
		if (null != mapEventsVar.get(varName))
			for (EmployeeEventsVariable e : mapEventsVar.get(varName)){
				if(year != e.getStartDate().getYear())
					continue;
				if (month == e.getStartDate().getMonth())
					return e;
			}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public void setValueByMonth(String variableName, Integer month, Double newValue, Integer year) {
		EmployeeEventsVariable oldVar = null;
		for (EmployeeEventsVariable e : draftMapEventsVar.get(variableName)){
			if(month == e.getStartDate().getMonth())
				oldVar = e;
		}
		
		EmployeeEventsVariable newVar = createEmployeeEventsVariable(month, newValue, year);
		
		this.undoManager.add(new SetVariableEdit(oldVar, newVar, variableName));
	}
	
	@SuppressWarnings("deprecation")
	public void setValueByMonths(String variableName, ArrayList<Integer> months, Double newValue, Integer year) {
		List<Undoable> undos = new ArrayList<Undoable>();
		
		for (Integer month : months){
			EmployeeEventsVariable oldVar = null;
			if (null != draftMapEventsVar.get(variableName))
				for (EmployeeEventsVariable e : draftMapEventsVar.get(variableName)){
					if (year != e.getStartDate().getYear())
						continue;
					if(month == e.getStartDate().getMonth()){
						oldVar = e;
					}
				}
			else{
				draftMapEventsVar.put(variableName, new ArrayList<EmployeeEventsVariable>());
			}
			
			EmployeeEventsVariable newVar = createEmployeeEventsVariable(month, newValue, year);
			draftMapEventsVar.get(variableName).remove(oldVar);
			draftMapEventsVar.get(variableName).add(newVar);
			undos.add(new SetVariableEdit(oldVar, newVar, variableName));
		}
		this.undoManager.add(new CompositeUndoable<Undoable>(undos));
		
	}
	
	@SuppressWarnings("deprecation")
	private EmployeeEventsVariable createEmployeeEventsVariable(Integer month, Double newValue, Integer year) {
		Date startDate = new Date(year, month, DateUtils.getFirstDayOfMonth(new Date(year, month, month)).getDate());
		Date endDate = new Date(year, month, DateUtils.getLastDayOfMonth(new Date(year, month, month)).getDate());
		
		return new EmployeeEventsVariable(startDate, endDate, newValue);
	}
	
	public boolean hasChanged(String variableName, EmployeeEventsVariable varMonth) {
		if (null == draftMapEventsVar.get(variableName))
			return false;
		
		return draftMapEventsVar.get(variableName).contains(varMonth);
	}

	/**
	 * METODOS PARA BORRAR
	 */
	
	public void crearMapaEmployeeEvents(){
		ArrayList<EmployeeEventsVariable> dtList = new ArrayList<EmployeeEventsVariable>();
		rellenarLista(dtList);
		mapEventsVar.put("DIAS_TRABAJADOS", dtList);
		
		ArrayList<EmployeeEventsVariable> deList = new ArrayList<EmployeeEventsVariable>();
		rellenarLista(deList);
		mapEventsVar.put("DIAS_EFECTIVOS", deList);
		
		ArrayList<EmployeeEventsVariable> daList = new ArrayList<EmployeeEventsVariable>();
		rellenarListaCaso1(daList);
		mapEventsVar.put("DIAS_AUSENCIA", daList);
		
		ArrayList<EmployeeEventsVariable> htList = new ArrayList<EmployeeEventsVariable>();
		rellenarListaCaso2(htList);
		sortListByStartDate(htList);
		mapEventsVar.put("HORAS_TRABAJADAS", htList);
		
		ArrayList<EmployeeEventsVariable> hcList = new ArrayList<EmployeeEventsVariable>();
		rellenarListaCaso3(hcList);
		sortListByStartDate(hcList);
		mapEventsVar.put("HORAS_COMPLEMENTARIAS", hcList);
		
		ArrayList<EmployeeEventsVariable> dmList = new ArrayList<EmployeeEventsVariable>();
		rellenarListaCaso4(dmList);
		sortListByStartDate(dmList);
		mapEventsVar.put("DIAS_MANUTENCION", dmList);
	}

	private void rellenarLista(ArrayList<EmployeeEventsVariable> list) {
		for (int i = 0; i<12; i++){
			Date startDate = new Date(117, i, DateUtils.getFirstDayOfMonth(new Date(117, i, i)).getDate());
			Date endDate = new Date(117, i, DateUtils.getLastDayOfMonth(new Date(117, i, i)).getDate());
			Double value = i*1.00;
			EmployeeEventsVariable info = new EmployeeEventsVariable(startDate, endDate, value);
			list.add(info);
		}
	}
	
	private void rellenarListaCaso1(ArrayList<EmployeeEventsVariable> list) {
		for (int i = 0; i<12; i++){
			if(i== 0 || i == 5){
				continue;
			}
			
			Date startDate = new Date(117, i, DateUtils.getFirstDayOfMonth(new Date(117, i, i)).getDate());
			Date endDate = new Date(117, i, DateUtils.getLastDayOfMonth(new Date(117, i, i)).getDate());
			Double value = i*1.00;
			EmployeeEventsVariable info = new EmployeeEventsVariable(startDate, endDate, value);
			list.add(info);
		}
	}
	
	private void rellenarListaCaso2(ArrayList<EmployeeEventsVariable> list) {
		for (int i = 11; i>=0; i--){
			Date startDate = new Date(117, i, DateUtils.getFirstDayOfMonth(new Date(117, i, i)).getDate());
			Date endDate = new Date(117, i, DateUtils.getLastDayOfMonth(new Date(117, i, i)).getDate());
			Double value = i*1.00;
			EmployeeEventsVariable info = new EmployeeEventsVariable(startDate, endDate, value);
			list.add(info);
		}	
	}
	
	private void rellenarListaCaso3(ArrayList<EmployeeEventsVariable> list) {
		for (int i = 11; i>=0; i--){
			if(i== 1 || i == 6){
				continue;	
			}
			
			Date startDate = new Date(117, i, DateUtils.getFirstDayOfMonth(new Date(117, i, i)).getDate());
			Date endDate = new Date(117, i, DateUtils.getLastDayOfMonth(new Date(117, i, i)).getDate());
			Double value = i*1.00;
			EmployeeEventsVariable info = new EmployeeEventsVariable(startDate, endDate, value);
			list.add(info);
		}
				
	}
	
	private void rellenarListaCaso4(ArrayList<EmployeeEventsVariable> list) {
		for (int i = 11; i>=0; i--){
			Date startDate = new Date(116, i, DateUtils.getFirstDayOfMonth(new Date(117, i, i)).getDate());
			Date endDate = new Date(116, i, DateUtils.getLastDayOfMonth(new Date(117, i, i)).getDate());
			Double value = i*1.00;
			EmployeeEventsVariable info = new EmployeeEventsVariable(startDate, endDate, value);
			list.add(info);
		}	
	}
	
	private void sortListByStartDate(ArrayList<EmployeeEventsVariable> list){
		Collections.sort(list, new Comparator<EmployeeEventsVariable>(){
			public int compare(EmployeeEventsVariable variable1, EmployeeEventsVariable variable2){
				if (null == variable1.getStartDate() || null == variable2.getStartDate())
			        return 0;
			     
				return variable1.getStartDate().compareTo(variable2.getStartDate());
			}
		});
	}

}

