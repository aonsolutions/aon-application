package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;

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
		//crearMapaEmployeeEvents();
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
	 * METODOS SYNC BORRADOR
	 */
	
	public static class EventVariable extends StringVariable{
		private static final long serialVersionUID = 1L;
	}
	
	public boolean isMine(com.esferalia.aon.gwt.payroll.shared.Variable v ){
		return v instanceof EventVariable ;
	}
	
	public ArrayList<StringVariable> getVariablesList(Date draftStartDate, Date draftEndDate) {
		ArrayList<StringVariable> variablesList = new ArrayList<StringVariable>();
		
		EventVariable var = null;
		
		for (String name : draftMapEventsVar.keySet()){
			if (null != draftMapEventsVar.get(name)){
				for (EmployeeEventsVariable e : draftMapEventsVar.get(name)){
					
					if(e.getStartDate().before(draftStartDate))
						continue;
					if(e.getEndDate().after(draftEndDate))
						continue;
					
					var = new EventVariable();
					var.setImplicit(false);
					var.setScope(Scope.SALARY); // DRAFT
					var.setName(name);
					var.setStartDate(e.getStartDate());
					var.setEndDate(e.getEndDate());
					var.setExpression(Double.toString(e.getValue()));
					
					variablesList.add(var);
					
					//Window.alert("EVENT VARIABLE = Name :"+var.getName()+", StartDate :"+ var.getStartDate()+", Exp :"+var.getExpression());
				}
			}
		}
		
		return variablesList;
	}
	
	/**
	 * METODOS SYNC DATABASE
	 */
	
	public void initializeDBCalendar(Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		
		employeesService.getEmployeeEvents(this.idEmployee, new AsyncCallback<EmployeeEventsData>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeEventsData result) {
				Map<String, ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>> contractEventsMap = 
						result.getContractEventsList();
				
				for (Entry<String, ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>> entry : contractEventsMap.entrySet()){
					String varName = entry.getKey();
					ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
					
					for(Quartet<java.sql.Date, java.sql.Date, String, String> quarter : entry.getValue()){
						Date startDate = DateUtils.copyDateOnly(quarter.getStartDate());
						Date endDate = DateUtils.copyDateOnly(quarter.getEndDate());
						Double value = Double.parseDouble(quarter.getExpression());
						EmployeeEventsVariable var = new EmployeeEventsVariable(startDate, endDate, value);
						varList.add(var);
					}
					sortListByStartDate(varList);
					mapEventsVar.put(varName, varList);
				}
				
				success.accept(result);
				
			}

			});
	}
	
	public void updateDBCalendar(Consumer<EmployeeEventsUpdate> success, Consumer<Throwable> failure) {
		
		EmployeeEventsUpdate updateInfo = new EmployeeEventsUpdate();
		updateInfo.setVariableEventsList(createVariablesList());
		
		employeesService.setEmployeeEvents(idEmployee, updateInfo, new AsyncCallback<EmployeeEventsUpdate>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
				
			}

			@Override
			public void onSuccess(EmployeeEventsUpdate result) {
				draftMapEventsVar.clear();
				success.accept(result);
				
			}
			
		});
		
	}

	/**
	 * METODOS AUX
	 */
	
	private void sortListByStartDate(ArrayList<EmployeeEventsVariable> list){
		Collections.sort(list, new Comparator<EmployeeEventsVariable>(){
			public int compare(EmployeeEventsVariable variable1, EmployeeEventsVariable variable2){
				if (null == variable1.getStartDate() || null == variable2.getStartDate())
			        return 0;
			     
				return variable1.getStartDate().compareTo(variable2.getStartDate());
			}
		});
	}
	
	private List<Quartet<java.sql.Date, java.sql.Date, String, String>> createVariablesList() {
		List<Quartet<java.sql.Date, java.sql.Date, String, String>> updateList = new ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>();
		Map<String, ArrayList<EmployeeEventsVariable>> updateMap = createUpdateMap();
		
		for(Entry<String, ArrayList<EmployeeEventsVariable>> entry : updateMap.entrySet()){
			Quartet<java.sql.Date, java.sql.Date, String, String> quarterInfo = new Quartet<java.sql.Date, java.sql.Date, String, String>();
			String varName = entry.getKey();
			for(EmployeeEventsVariable eVar : updateMap.get(varName)){
				java.sql.Date startDate = new java.sql.Date(eVar.getStartDate().getTime());
				java.sql.Date endDate = new java.sql.Date(eVar.getEndDate().getTime());
				String value = Double.toString(eVar.getValue());
				quarterInfo.setName(varName).setStartDate(startDate).setEndDate(endDate).setExpression(value);
				updateList.add(quarterInfo);
			}
			
		}
		
		return updateList;
	}

	private Map<String, ArrayList<EmployeeEventsVariable>> createUpdateMap() {
		Map<String, ArrayList<EmployeeEventsVariable>> updateMap = new HashMap<String, ArrayList<EmployeeEventsVariable>>();
		
		for (Entry<String, ArrayList<EmployeeEventsVariable>> entry : mapEventsVar.entrySet()){
			updateMap.put(entry.getKey(), entry.getValue());
		}
		
		//TODO: MIRAR ESTO COMO HACERLO BIEN!!!!!!
		for (Entry<String, ArrayList<EmployeeEventsVariable>> entry : draftMapEventsVar.entrySet()){
			updateMap.put(entry.getKey(), entry.getValue());
		}
		
		return updateMap;
	}

}

