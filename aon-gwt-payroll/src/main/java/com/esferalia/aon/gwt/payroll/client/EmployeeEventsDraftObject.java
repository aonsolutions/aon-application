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
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
	private Date startContractDate;
	private Date endContractDate;
	private DomainEmployeesServiceAsync employeesService;
	public UndoManager<Undoable> undoManager;
	private EmployeeCalendarDraftObject employeeCalendar;
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> employeeContractVariables;
	private ArrayList<String> employeeContractVariablesDB;
	
	public EmployeeEventsDraftObject(Integer idEmployee, Date startContractDate, Date endContractDate, DomainEmployeesServiceAsync employeesService) {
		this.mapEventsVar = new HashMap<String, ArrayList<EmployeeEventsDraftObject.EmployeeEventsVariable>>();
		this.draftMapEventsVar = new HashMap<String, ArrayList<EmployeeEventsDraftObject.EmployeeEventsVariable>>();
		
		this.idEmployee = idEmployee;
		this.startContractDate = startContractDate;
		this.endContractDate = endContractDate;
		this.employeesService = employeesService;
		
		this.employeeContractVariables = new ArrayList<String>();
		this.employeeContractVariablesDB = new ArrayList<String>();
		
		this.undoManager = new UndoManager<>();
	}


	/**
	 * GETTERS / SETTERS
	 */
	
	public Integer getIdEmployee() {
		return idEmployee;
	}
	
	public void setEmployeeCalendar(EmployeeCalendarDraftObject employeeCalendarDraftobject) {
		this.employeeCalendar = employeeCalendarDraftobject;
	}
	
	public EmployeeCalendarDraftObject getEmployeeCalendar() {
		return this.employeeCalendar;
	}
	
	public ArrayList<String> getEmployeeContractVariables() {
		return this.employeeContractVariables;
	}
	
	public boolean isContractVariable(String var){
		return this.employeeContractVariables.contains(var);
	}
	
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
		draftMapEventsVar.get(variableName).remove(oldVar);
		draftMapEventsVar.get(variableName).add(newVar);
		
		this.undoManager.add(new SetVariableEdit(oldVar, newVar, variableName));
	}
	
	@SuppressWarnings("deprecation")
	public void setValueByMonths(String variableName, ArrayList<Integer> months, Double newValue, Integer year) {
		List<Undoable> undos = new ArrayList<Undoable>();
		for (Integer month : months){
			EmployeeEventsVariable oldVar = null;
			if (null != draftMapEventsVar.get(variableName)){
				for (EmployeeEventsVariable e : draftMapEventsVar.get(variableName)){
					if (year != e.getStartDate().getYear())
						continue;
					if(month == e.getStartDate().getMonth())
						oldVar = e;
				}
			}else{
				draftMapEventsVar.put(variableName, new ArrayList<EmployeeEventsVariable>());
			}
			
			EmployeeEventsVariable newVar = createEmployeeEventsVariable(month, newValue, year);
			if(null != oldVar)
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
	
	@SuppressWarnings("deprecation")
	public void initializeDBEventsVariables(int year, Consumer<ContextDescriptor> success, Consumer<Throwable> failure) {
		
		employeesService.getEmployeeEventsVariables(idEmployee, new Date(year,0,1), new Date(year,11,31), 
				new AsyncCallback<ContextDescriptor>() {
			
			@Override
			public void onSuccess(ContextDescriptor context) {
				
				employeeContractVariables.clear();
				
				employeeContractVariables.add("DIAS_VACACIONES");
				employeeContractVariables.add("DIAS_AUSENCIA");
				employeeContractVariables.add("DIAS_HUELGA");
				employeeContractVariables.add("DIAS_ERE");
				employeeContractVariables.add("HORAS_COMPLEMENTARIAS");
				employeeContractVariables.add("HORAS_EXTRAS");
				employeeContractVariables.add("IMPORTE_HORA_EXTRA");
				employeeContractVariables.add("HORAS_FORMACION_PRESENCIAL");
				employeeContractVariables.add("HORAS_FORMACION_DISTANCIA");
				employeeContractVariables.add("HORAS_TUTORIA");
				employeeContractVariables.add("BONIFICACION_TUTORIA");
				employeeContractVariables.add("KMS");
				
				employeeContractVariablesDB.add("DIAS_VACACIONES");
				employeeContractVariablesDB.add("DIAS_AUSENCIA");
				employeeContractVariablesDB.add("DIAS_HUELGA");
				employeeContractVariablesDB.add("DIAS_ERE");
				employeeContractVariablesDB.add("HORAS_COMPLEMENTARIAS");
				employeeContractVariablesDB.add("HORAS_EXTRAS");
				employeeContractVariablesDB.add("IMPORTE_HORA_EXTRA");
				employeeContractVariablesDB.add("HORAS_FORMACION_PRESENCIAL");
				employeeContractVariablesDB.add("HORAS_FORMACION_DISTANCIA");
				employeeContractVariablesDB.add("HORAS_TUTORIA");
				employeeContractVariablesDB.add("BONIFICACION_TUTORIA");
				employeeContractVariablesDB.add("KMS");

				for (String varName : context.getVariables()){
					ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
					
					if(context.getList(varName).isEmpty()){
						mapEventsVar.put(varName, varList);
						employeeContractVariables.add(varName);
						continue;
					}
					
					try {
						for (VariableDescriptor var : context.getList(varName)){
							Date startDate = var.getStartDate();
							Date endDate = var.getEndDate();
							try {
								Double value = Double.valueOf(var.getValue());
								if(startDate.getMonth() == endDate.getMonth()){
									EmployeeEventsVariable eVar = new EmployeeEventsVariable(startDate, endDate, value);
									varList.add(eVar);
								}else{
									for(int i = startDate.getMonth(); i <= endDate.getMonth(); i++){
										Date auxStartDate = new Date(startDate.getYear(), i, 1);
										Date auxEndDate = new Date(startDate.getYear(), i+1, 0);
										EmployeeEventsVariable eVar = new EmployeeEventsVariable(auxStartDate, auxEndDate, value);
										varList.add(eVar);
									}
								}
							}catch (Exception e) {
								throw new Exception();
							}
						}
						
						sortListByStartDate(varList);
						
						mapEventsVar.put(varName, varList);
						
						employeeContractVariables.add(varName);
					}catch (Exception e) {
						continue;
					}
				}
				
				initializeDBCalendar(
						s -> { success.accept(context);}, 
						f -> {}
				);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void initializeDBCalendar(Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		
		employeesService.getEmployeeEventsByContract(this.idEmployee, this.employeeContractVariablesDB, new AsyncCallback<EmployeeEventsData>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeEventsData result) {
				
				for (Entry<String, ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>> entry : result.getContractEventsList().entrySet()){
					
					String varName = entry.getKey();
					ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
					
					if(!entry.getValue().isEmpty()){
						for(Quartet<java.sql.Date, java.sql.Date, String, String> quarter : entry.getValue()){
							Date startDate = DateUtils.copyDateOnly(quarter.getStartDate());
							Date endDate = null;
							if(null != quarter.getEndDate())
								endDate = DateUtils.copyDateOnly(quarter.getEndDate());
							Double value = Double.parseDouble(quarter.getExpression());
//							EmployeeEventsVariable var = new EmployeeEventsVariable(startDate, endDate, value);
//							varList.add(var);
							if(startDate.getMonth() == endDate.getMonth()){
								EmployeeEventsVariable eVar = new EmployeeEventsVariable(startDate, endDate, value);
								varList.add(eVar);
							}else{
								for(int i = startDate.getMonth(); i <= endDate.getMonth(); i++){
									Date auxStartDate = new Date(startDate.getYear(), i, 1);
									Date auxEndDate = new Date(startDate.getYear(), i+1, 0);
									EmployeeEventsVariable eVar = new EmployeeEventsVariable(auxStartDate, auxEndDate, value);
									varList.add(eVar);
								}
							}
						}
						sortListByStartDate(varList);
					}	
					mapEventsVar.put(varName, varList);
				}
				
//				for(String name : mapEventsVar.keySet()){
//					for(EmployeeEventsVariable var: mapEventsVar.get(name)){
//						Window.alert("SIN MODIF : " +name+" = "+var.getValue()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());
//					}
//				}
				
				modifyMapEventsVar();
				
				//IMPRIMIR VARIABLES
//				for(String name : mapEventsVar.keySet()){
//					for(EmployeeEventsVariable var: mapEventsVar.get(name)){
//						Window.alert(name+" = "+var.getValue()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());
//					}
//				}
				
				success.accept(result);
			}

			private void modifyMapEventsVar() {
				for (String varName: mapEventsVar.keySet()){
					ArrayList<EmployeeEventsVariable> eventVarList = mapEventsVar.get(varName);
					if(varName.contains("DIAS")){
						ArrayList<EmployeeEventsVariable> newEventVarList = groupDays(varName, eventVarList);
						mapEventsVar.put(varName, newEventVarList);
					}else{
						ArrayList<EmployeeEventsVariable> newEventVarList = checkDuplicateMonths(eventVarList);
						mapEventsVar.put(varName, newEventVarList);
					}	
				}
			}

			private ArrayList<EmployeeEventsVariable> checkDuplicateMonths(ArrayList<EmployeeEventsVariable> eventVarList) {
				ArrayList<EmployeeEventsVariable> newEventsList = new ArrayList<>();
				int i = 0;
				while(i < eventVarList.size()){
					if(i+1 < eventVarList.size()){
						if(eventVarList.get(i).getStartDate().getMonth() == eventVarList.get(i+1).getStartDate().getMonth()){
							newEventsList.add(eventVarList.get(i+1));
							i+=2;
						}else{
							newEventsList.add(eventVarList.get(i));
							i++;
						}
					}else{
						newEventsList.add(eventVarList.get(i));
						i++;
					}		
				}
				
				return newEventsList;
			}

			private ArrayList<EmployeeEventsVariable> groupDays(String varName, ArrayList<EmployeeEventsVariable> eventVarList) {
				Double days = 0.00;
				ArrayList<EmployeeEventsVariable> newEventsList = new ArrayList<>();
				int i = 0;
				while(i < eventVarList.size()){
					if(i+1 < eventVarList.size()){
						if(eventVarList.get(i).getStartDate().getMonth() == eventVarList.get(i+1).getStartDate().getMonth()){
							days += eventVarList.get(i).getValue();
							i++;
						}else{
							EmployeeEventsVariable eVar;
							if(days == 0)
								 eVar = new EmployeeEventsVariable(
									DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
									DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
									eventVarList.get(i).getValue());
							else{
								eVar = new EmployeeEventsVariable(
										DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
										DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
										days + eventVarList.get(i).getValue());
								days = 0.00;
							}
							newEventsList.add(eVar);
							i++;
						}
					}else{
						if(days != 0){
							days += eventVarList.get(i).getValue();
							EmployeeEventsVariable eVar = new EmployeeEventsVariable(
									DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
									DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
									days);
							days = 0.00;
							newEventsList.add(eVar);
							i++;
						}else{
							newEventsList.add(eventVarList.get(i));
							i++;
						}
					}		
				}
				return newEventsList;
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
			String varName = entry.getKey();
			for(EmployeeEventsVariable eVar : updateMap.get(varName)){
				//Window.alert("UPDATE -> "+varName + " = "+ eVar.getValue() +", StartDate :"+eVar.getStartDate()+", endDate :"+eVar.getEndDate());
				Quartet<java.sql.Date, java.sql.Date, String, String> quarterInfo = new Quartet<java.sql.Date, java.sql.Date, String, String>();
				java.sql.Date startDate = new java.sql.Date(eVar.getStartDate().getTime());
				java.sql.Date endDate = new java.sql.Date(eVar.getEndDate().getTime());
				String value = "";
				if(null == eVar.getValue())
					value = null;
				else
					value = Double.toString(eVar.getValue());
				quarterInfo.setName(varName).setStartDate(startDate).setEndDate(endDate).setExpression(value);
				updateList.add(quarterInfo);
			}
		}
		
		return updateList;
	}
	
	//Metodo para crear el mapa que va a recoer toda la informacion que queremos subir a la base de datos
	private Map<String, ArrayList<EmployeeEventsVariable>> createUpdateMap() {
		Map<String, ArrayList<EmployeeEventsVariable>> updateMap = new HashMap<String, ArrayList<EmployeeEventsVariable>>();
		
		for(String key : mapEventsVar.keySet()){
			ArrayList<EmployeeEventsVariable> resultList = checkResultList(key);
			updateMap.put(key, resultList);
		}
		
		for(String key : draftMapEventsVar.keySet()){
			
			if(null != updateMap.get(key))
				for(EmployeeEventsVariable e : draftMapEventsVar.get(key)){
					updateMap.get(key).add(e);
				}
			else
				updateMap.put(key, draftMapEventsVar.get(key));
		}
		
		return updateMap;
	}

	//Metodo para coger del mapa original solo aquellas entradas que no han sido modificadas
	private ArrayList<EmployeeEventsVariable> checkResultList(String key) {
		ArrayList<EmployeeEventsVariable> resultList = new ArrayList<>();
		
		for(EmployeeEventsVariable e : mapEventsVar.get(key)){
			if(draftContainsDate(e.startDate, key)){
				continue;
			}else{
				resultList.add(e);
			}
		}
		
		return resultList;
	}

	private boolean draftContainsDate(Date startDateMap, String key) {
		if(null != draftMapEventsVar.get(key)){
			for(EmployeeEventsVariable e : draftMapEventsVar.get(key)){
				if(e.startDate.equals(startDateMap))
					return true;
			}
		}
		return false;
	}

}

