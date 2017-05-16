package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
			if (oldEmployeeEventsVariable == null)
				draftMapEventsVar.get(this.variable).remove(oldEmployeeEventsVariable);
			else{
				ArrayList<EmployeeEventsVariable> list = new ArrayList<EmployeeEventsVariable>();
				list.add(this.oldEmployeeEventsVariable);
				draftMapEventsVar.put(this.variable, list);
			}
		}
		
		@Override
		public void redo() {
			draftMapEventsVar.get(this.variable).add(this.newEmployeeEventsVariable);
		}
		
	}
		
	
	/**
	 * DECLARACION DE VARIABLES Y CONSTRUCTOR
	 */
	
	private Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar;
	private Map<String, ArrayList<EmployeeEventsVariable>> draftMapEventsVar;
	private Integer idEmployee;
	
	public EmployeeEventsDraftObject(Integer idEmployee, EmployeesServiceAsync employeesService) {
		this.mapEventsVar = new HashMap<String, ArrayList<EmployeeEventsDraftObject.EmployeeEventsVariable>>();
		crearMapaEmployeeEvents();
		this.draftMapEventsVar = new HashMap<String, ArrayList<EmployeeEventsDraftObject.EmployeeEventsVariable>>();
		this.idEmployee = idEmployee;
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
		
		ArrayList<EmployeeEventsVariable> dmList = new ArrayList<EmployeeEventsVariable>();
		rellenarListaCaso3(dmList);
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
				list.add(null);
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
				list.add(null);
				continue;	
			}
			
			Date startDate = new Date(117, i, DateUtils.getFirstDayOfMonth(new Date(117, i, i)).getDate());
			Date endDate = new Date(117, i, DateUtils.getLastDayOfMonth(new Date(117, i, i)).getDate());
			Double value = i*1.00;
			EmployeeEventsVariable info = new EmployeeEventsVariable(startDate, endDate, value);
			list.add(info);
		}	
		
	}
	
	private void sortListByStartDate(ArrayList<EmployeeEventsVariable> list){
		Collections.sort(list, new Comparator<EmployeeEventsVariable>(){
			public int compare(EmployeeEventsVariable variable1, EmployeeEventsVariable variable2){
				if (variable1.getStartDate() == null || variable2.getStartDate() == null)
			        return 0;
			     
				return variable1.getStartDate().compareTo(variable2.getStartDate());
			}
		});
	}

}

