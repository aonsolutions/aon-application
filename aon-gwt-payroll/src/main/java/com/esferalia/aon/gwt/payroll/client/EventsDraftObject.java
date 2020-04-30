package com.esferalia.aon.gwt.payroll.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EventsDraftObject {
	
	// Clase que contiene la informacion de cada uno de los empleados (Nombre, Apellidos e ID de contrato)
	@SuppressWarnings("serial")
	public class EventEmployee implements Serializable{
		private String name;
		private String surName;
		private String fullName;
		private Integer employeeId;
		private boolean fullJourney;
		
		public EventEmployee() {
			super();
		}
		
		public EventEmployee(String name, String surName, Integer employeeId, boolean fullTime) {
			super();
			this.name = name;
			this.surName = surName;
			this.employeeId = employeeId;
			this.fullName = this.surName + ", " + this.name;
			this.fullJourney = fullTime;
		}
		
		public String getCompleteEmployeeName(){
			return this.fullName;
		}
		
		public Integer getEmployeeId(){
			return this.employeeId;
		}
		
		public boolean isEmployee(String name) {
			if(this.fullName.equals(name))
				return true;
			
			return false;
		}
		
		public boolean isFullTime() {
			return this.fullJourney;
		}
		
	}
	
	public interface EVENTimedVariable<V>{
		public Date getStartDate();
		public Date getEndDate();
		public V getValue();
	}
	
	// Clase que contiene la informacion de una variable (Fecha de inicio, fecha de fin y Valor)
	@SuppressWarnings("serial")
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
			super();
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
		private Integer employeeId;
		
		public SetVariableEdit(EmployeeEventsVariable oldEmployeeEventsVariable, EmployeeEventsVariable newEmployeeEventsVariable, 
							String variable, Integer employeeId) {
			this.oldEmployeeEventsVariable = oldEmployeeEventsVariable;
			this.newEmployeeEventsVariable = newEmployeeEventsVariable;
			this.variable = variable;
			this.employeeId = employeeId;
		}
		
		@Override
		public void undo() {
			draftMapEventsObject.get(this.employeeId).get(this.variable).remove(newEmployeeEventsVariable);
			//draftMapEvents.get(this.variable).remove(newEmployeeEventsVariable);
			if (oldEmployeeEventsVariable != null){
				draftMapEventsObject.get(this.employeeId).get(this.variable).add(oldEmployeeEventsVariable);
				//draftMapEvents.get(this.variable).add(oldEmployeeEventsVariable);
			}
		}
		
		@Override
		public void redo() {
			if (oldEmployeeEventsVariable != null)
				draftMapEventsObject.get(this.employeeId).get(this.variable).remove(this.oldEmployeeEventsVariable);
				//draftMapEvents.get(this.variable).remove(this.oldEmployeeEventsVariable);
			
			draftMapEventsObject.get(this.employeeId).get(this.variable).add(this.newEmployeeEventsVariable);
			//draftMapEvents.get(this.variable).add(this.newEmployeeEventsVariable);
		}
		
	}

	private Map<Integer,Map<String, ArrayList<EmployeeEventsVariable>>> mapEventsObject;
	private Map<Integer,Map<String, ArrayList<EmployeeEventsVariable>>> draftMapEventsObject;
	
	private ArrayList<EventEmployee> workplaceEmployees;
	private ArrayList<Integer> workplaceEmployeesId;
	private int contWorkplaceEmployeesId;
	private Set<String> allVariables;
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> employeeContractVariables;
	private ArrayList<String> employeeContractVariablesDB;
	
	private Integer workplaceId;
	private Integer agreementId;

	public UndoManager<Undoable> undoManager;
	
	private DomainEmployeesServiceAsync employeesServiceAsync;
	
	private Boolean fullJourney;

	public EventsDraftObject(Integer workplaceId, Integer agreeementId,
			DomainEmployeesServiceAsync employeesServiceAsync,
			EventMetaData... eventsMetaData) {
		
		this.mapEventsObject = new HashMap<Integer, Map<String, ArrayList<EmployeeEventsVariable>>>();
		this.draftMapEventsObject = new HashMap<Integer, Map<String, ArrayList<EmployeeEventsVariable>>>();
		
		this.workplaceId = workplaceId;
		this.agreementId = agreeementId;
		this.employeesServiceAsync = employeesServiceAsync;
		
		this.workplaceEmployees = new ArrayList<>();
		this.workplaceEmployeesId = new ArrayList<>();
		this.contWorkplaceEmployeesId = 0;
		this.allVariables = new HashSet<>();
		
		this.employeeContractVariables = new ArrayList<String>();
		this.employeeContractVariablesDB = new ArrayList<String>();	
		
		this.undoManager = new UndoManager<>();
		
		this.fullJourney = false;
	}

	
	// --------------------------------
	//      METODOS SYNC DB
	// --------------------------------
	
	public void getWorkPlaceEmployeesDB(int year, Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure) {
		
		employeesServiceAsync.getWorkplaceEmployeesEvents(this.workplaceId, new AsyncCallback<WorkplaceEmployees>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				workplaceEmployeesId.clear();
				workplaceEmployees.clear();
				mapEventsObject.clear();
				contWorkplaceEmployeesId = 0;
				allVariables.clear();
				
				for(EmployeeInfo employeeDB : result.getWorkplaceEmployees()){
					//Crear empleado e inicializar mapEventsObject
					EventEmployee employee = new EventEmployee(
							employeeDB.getName(), 
							employeeDB.getSurName(), 
							employeeDB.getEmployeeId(),
							employeeDB.getIsFullTime()
					);
					mapEventsObject.put(employeeDB.getEmployeeId(), new HashMap<String, ArrayList<EmployeeEventsVariable>>());
					draftMapEventsObject.put(employeeDB.getEmployeeId(), new HashMap<String, ArrayList<EmployeeEventsVariable>>());
					
					workplaceEmployeesId.add(employee.getEmployeeId());
					workplaceEmployees.add(employee);
				}
				
				
				initializeDBEventsVariables(year,
					r -> {
							success.accept(result);
						}, 
					f -> {});
				
			}
			
		});
	}
	
	public void initializeDBEventsVariables(int year, Consumer<Map<String,String>> success, Consumer<Throwable> failure) {
		
		Map<String, ArrayList<EmployeeEventsVariable>> mapEmployeeEventsVar = new HashMap<String, ArrayList<EmployeeEventsVariable>>();
		
		employeesServiceAsync.getWorkplaceEventsVariables(workplaceId, agreementId, new Date(year,0,1), new Date(year,11,31), new AsyncCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				contWorkplaceEmployeesId++;
				employeeContractVariables.clear();
				
				createAllVariables("DIAS_TRABAJADOS");
				createAllVariables("DIAS_VACACIONES");
				createAllVariables("DIAS_INACTIVIDAD");
				createAllVariables("DIAS_AUSENCIA");
				createAllVariables("DIAS_HUELGA");
				createAllVariables("DIAS_ERE");
				createAllVariables("DIAS_ERE_FZA");
				createAllVariables("DIAS_ERE_FZA_EXON");
				createAllVariables("IMPORTE_HORA_EXTRA");
				createAllVariables("HORAS_FORMACION_PRESENCIAL");
				createAllVariables("HORAS_FORMACION_DISTANCIA");
				createAllVariables("HORAS_TUTORIA");
				createAllVariables("BONIFICACION_TUTORIA");
				createAllVariables("BONIFICACION_FORMACION_CONTINUA");
				createAllVariables("KMS");
				
				employeeContractVariablesDB.add("DIAS_TRABAJADOS");
				employeeContractVariablesDB.add("DIAS_VACACIONES");
				employeeContractVariablesDB.add("DIAS_INACTIVIDAD");
				employeeContractVariablesDB.add("DIAS_AUSENCIA");
				employeeContractVariablesDB.add("DIAS_HUELGA");
				employeeContractVariablesDB.add("DIAS_ERE");
				employeeContractVariablesDB.add("DIAS_ERE_FZA");
				employeeContractVariablesDB.add("DIAS_ERE_FZA_EXON");
				employeeContractVariablesDB.add("IMPORTE_HORA_EXTRA");
				employeeContractVariablesDB.add("HORAS_FORMACION_PRESENCIAL");
				employeeContractVariablesDB.add("HORAS_FORMACION_DISTANCIA");
				employeeContractVariablesDB.add("HORAS_TUTORIA");
				employeeContractVariablesDB.add("BONIFICACION_TUTORIA");
				employeeContractVariablesDB.add("BONIFICACION_FORMACION_CONTINUA");
				employeeContractVariablesDB.add("KMS");
				
				for(Entry<String, String> entry : result.entrySet()){
					createAllVariables(entry.getKey());
					employeeContractVariablesDB.add(entry.getKey());
					ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
					mapEmployeeEventsVar.put(entry.getKey(), varList);
				}
				
				initializeDBCalendar(mapEmployeeEventsVar, 
						s -> { 
								success.accept(result);
							}, 
						f -> {}
				);
			}
			
			private void createAllVariables(String var) {
//				Window.alert(var);
				employeeContractVariables.add(var);
				allVariables.add(var);	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void initializeDBCalendar(Map<String, ArrayList<EmployeeEventsVariable>> mapEmployeeEventsVar, Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		
		for(Integer employeeId : getEmployeesId()){
		
			employeesServiceAsync.getEmployeeEvents(employeeId, this.employeeContractVariablesDB, new AsyncCallback<EmployeeEventsData>(){
	
				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}
	
				@Override
				public void onSuccess(EmployeeEventsData result) {
					
					contWorkplaceEmployeesId++;
					
					for (Entry<String, ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>> entry : result.getContractEventsList().entrySet()){
						
						String varName = entry.getKey();
						ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
						
						if(!entry.getValue().isEmpty()){
							for(Quartet<java.sql.Date, java.sql.Date, String, String> quarter : entry.getValue()){
								Date startDate = DateUtils.copyDateOnly(quarter.getStartDate());
								Date endDate = null;
								if(null != quarter.getEndDate())
									endDate = DateUtils.copyDateOnly(quarter.getEndDate());
								else{ //TODO: MIRAR BIEN QUE HACER CUANDO ENDDATE ES NULL
									endDate = DateUtils.copyDateOnly(new Date((quarter.getStartDate().getYear()+1),quarter.getStartDate().getMonth(),quarter.getStartDate().getDate()));
								}
								Double value = Double.parseDouble(quarter.getExpression());
								if(startDate.getMonth() == endDate.getMonth() && startDate.getYear() == endDate.getMonth()){
									EmployeeEventsVariable eVar = new EmployeeEventsVariable(startDate, endDate, value);
									varList.add(eVar);
								}else{
									for(Date date = startDate; date.before(endDate); DateUtils.addMonths2Date(date, 1)){
										Date auxStartDate = new Date(date.getYear(), date.getMonth(), 1);
										Date auxEndDate = new Date(date.getYear(), date.getMonth()+1, 0);
										EmployeeEventsVariable eVar = new EmployeeEventsVariable(auxStartDate, auxEndDate, value);
										varList.add(eVar);
									}
								}
							}
							sortListByStartDate(varList);
						}
						
						mapEmployeeEventsVar.put(varName, varList);
					}
					
					modifyMapEventsVar();
					
					mapEventsObject.get(employeeId).putAll(mapEmployeeEventsVar);
					draftMapEventsObject.get(employeeId).putAll(mapEmployeeEventsVar);
					
					if(getEmployeesId().size() == contWorkplaceEmployeesId){
//						Window.alert("Map Events Object Size = " + mapEventsObject.size());
						Timer timer = new Timer() {

							@Override
							public void run() {
								success.accept(result);
								this.cancel();
							}
						};
						
						timer.schedule(2000);
//						success.accept(result);
					}
				}
	
				private void modifyMapEventsVar() {
					for (String varName: mapEmployeeEventsVar.keySet()){
						ArrayList<EmployeeEventsVariable> eventVarList = mapEmployeeEventsVar.get(varName);
						if(varName.contains("DIAS")){
							ArrayList<EmployeeEventsVariable> newEventVarList = groupDays(varName, eventVarList);
							mapEmployeeEventsVar.put(varName, newEventVarList);
						}else{
							ArrayList<EmployeeEventsVariable> newEventVarList = checkDuplicateMonths(eventVarList);
							mapEmployeeEventsVar.put(varName, newEventVarList);
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
											days);
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
								EmployeeEventsVariable eVar = new EmployeeEventsVariable(
										DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
										DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
										eventVarList.get(i).getValue());
								newEventsList.add(eVar);
								i++;
							}
						}		
					}
					return newEventsList;
				}
			});
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

	
	// ---------------------------------------------------------------------------------------------------
	//									METODOS GETTER, SETTER, ADD
	// ---------------------------------------------------------------------------------------------------

	public Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> getInformation(){
		return this.mapEventsObject;
	}
	
	public Integer getEventEmployeeId(String employeeName){
		for(EventEmployee employee: workplaceEmployees){
			if(employee.isEmployee(employeeName))
				return employee.getEmployeeId();
		}
		return null;
	}


	public EmployeeEventsVariable getEmployeeVariableByDate(Integer employeeId, Date findingDate, String variableName) {
		Map<String, ArrayList<EmployeeEventsVariable>> draftVariableMap = this.draftMapEventsObject.getOrDefault(employeeId, null);
		if(null != draftVariableMap){
			ArrayList<EmployeeEventsVariable> variableList = draftVariableMap.getOrDefault(variableName, null);
			if(null != variableList){
				for(EmployeeEventsVariable eventVariable : variableList){
					if(eventVariable.getStartDate().equals(findingDate))
						return eventVariable;
				}
			}
		}
		

		Map<String, ArrayList<EmployeeEventsVariable>> variableMap = this.mapEventsObject.getOrDefault(employeeId, null);
		if(null != variableMap){
			ArrayList<EmployeeEventsVariable> variableList = variableMap.getOrDefault(variableName, null);
			if(null != variableList){
				for(EmployeeEventsVariable eventVariable : variableList){
					if(eventVariable.getStartDate().equals(findingDate))
						return eventVariable;
				}
			}
		}
		
		return null;
	}


	public void addEvent(Integer employeeId, String varName, Double value, Date startDate, Date endDate) {
		EmployeeEventsVariable variable = new EmployeeEventsVariable(startDate, endDate, value);
		EmployeeEventsVariable removeVar = null;
		if(this.draftMapEventsObject.containsKey(employeeId)){
			if(this.draftMapEventsObject.get(employeeId).containsKey(varName)){
				for(EmployeeEventsVariable var : this.draftMapEventsObject.get(employeeId).get(varName)){
//					Window.alert("Borrar Var : " + var.getStartDate() + " == " + startDate);
					if(var.getStartDate().equals(startDate)){
						removeVar = var;
						continue;
					}	
				}
				this.draftMapEventsObject.get(employeeId).get(varName).remove(removeVar);
				this.draftMapEventsObject.get(employeeId).get(varName).add(variable);
			}else{
				this.draftMapEventsObject.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
				this.draftMapEventsObject.get(employeeId).get(varName).add(variable);
			}
		}else{
			this.draftMapEventsObject.put(employeeId, new HashMap<String, ArrayList<EmployeeEventsVariable>>());
			this.draftMapEventsObject.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
			this.draftMapEventsObject.get(employeeId).get(varName).add(variable);
		}
		
		this.undoManager.add(new SetVariableEdit(removeVar, variable, varName, employeeId));
	}


	public boolean hasChanged(Integer employeeId, String varName, EmployeeEventsVariable variable) {
		if(null == this.draftMapEventsObject.get(employeeId))
			return false;
		else
			if(null == this.draftMapEventsObject.get(employeeId).get(varName))
				return false;
		
		return this.draftMapEventsObject.get(employeeId).get(varName).contains(variable);
	}
	
	public ArrayList<EventEmployee> getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public Set<String> getAllVariables(){
		return this.allVariables;
	}
	
	public ArrayList<Integer> getEmployeesId(){
		return this.workplaceEmployeesId;
	}
	
	// ---------------------------------------------------------------------------------------------------
	//									  METODO GUARDAR
	// ---------------------------------------------------------------------------------------------------
	
	public void updateEventsWorkplace(Consumer<EventsWorkplace> success, Consumer<Throwable> failure) {
		EventsWorkplace updateEventsWorkplace = new EventsWorkplace();
		updateEventsWorkplace.setUpdateEventsWorkplace(createUpdateEventsWorkplace());

		employeesServiceAsync.setEventsWorkplace(updateEventsWorkplace, new AsyncCallback<EventsWorkplace>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
				
			}

			@Override
			public void onSuccess(EventsWorkplace result) {
				draftMapEventsObject.clear();
				mapEventsObject.clear();
				success.accept(result);
				
			}
			
		});
	}


	private List<Quintet<Integer, String, Date, Date, String>> createUpdateEventsWorkplace() {
		Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> updateMap = new HashMap<Integer, Map<String, ArrayList<EmployeeEventsVariable>>>();
		
//		for (Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : mapEventsObject.entrySet()){
//			updateMap.put(entry.getKey(), entry.getValue());
//		}
		
		for (Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : draftMapEventsObject.entrySet()){
			Integer employeeId = entry.getKey();
			for(Entry<String, ArrayList<EmployeeEventsVariable>> entryVar : entry.getValue().entrySet()){
				String varName = entryVar.getKey();
				for(EmployeeEventsVariable var : entryVar.getValue()){
					addUpdateEventVarToMap(updateMap, employeeId, varName, var);
				}
			}
		}
		
		return convertMapToList(updateMap);
	}


	private List<Quintet<Integer, String, Date, Date, String>> convertMapToList(
			Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> updateMap) {
		
		List<Quintet<Integer, String, Date, Date, String>> updateList = new ArrayList<>();
		
		for (Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : updateMap.entrySet()){
			Integer employeeId = entry.getKey();
			for(Entry<String, ArrayList<EmployeeEventsVariable>> entryVar : entry.getValue().entrySet()){
				String varName = entryVar.getKey();
				for(EmployeeEventsVariable var : entryVar.getValue()){
					java.sql.Date sqlStartDate = new java.sql.Date(var.getStartDate().getTime());
					java.sql.Date sqlEndDate = new java.sql.Date(var.getEndDate().getTime());
					
					updateList.add(new Quintet<Integer, String, Date, Date, String>(employeeId, varName, sqlStartDate, sqlEndDate, var.getValue() == null ? null : var.getValue().toString()));
				}
			}
		}
		
		return updateList;
	}


	private void addUpdateEventVarToMap(Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> updateMap,
			Integer employeeId, String varName, EmployeeEventsVariable var) {
		EmployeeEventsVariable removeVar = null;
		if(updateMap.containsKey(employeeId)){
			if(updateMap.get(employeeId).containsKey(varName)){
				for(EmployeeEventsVariable varDraft : this.draftMapEventsObject.get(employeeId).get(varName)){
					if(varDraft.getStartDate().equals(var.getStartDate())){
						removeVar = varDraft;
						continue;
					}	
				}
				updateMap.get(employeeId).get(varName).remove(removeVar);
				updateMap.get(employeeId).get(varName).add(var);
			}else{
				updateMap.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
				updateMap.get(employeeId).get(varName).add(var);
			}
		}else{
			updateMap.put(employeeId, new HashMap<String, ArrayList<EmployeeEventsVariable>>());
			updateMap.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
			updateMap.get(employeeId).get(varName).add(var);
		}
		
	}
	
	public boolean isFullJourney(Integer employeeId) {
		for(EventEmployee employee : getWorkplaceEmployees()) {
			if(employeeId == employee.getEmployeeId())
				return employee.isFullTime();
		}
		return false;
	}

//	public void save(String event, final SaveCallback callback) {
//	final Events dirtyEvents = getEvents(event);
//	employeesServiceAsync.saveEvents(dirtyEvents, startDate, endDate,
//			new AsyncCallback<Void>() {
//
//				@Override
//				public void onFailure(Throwable caught) {
//					callback.onSaveFailure(caught);
//				}
//
//				@Override
//				public void onSuccess(Void result) {
//					callback.onSaveSucces();
//				}
//
//			});
//}
	
}
