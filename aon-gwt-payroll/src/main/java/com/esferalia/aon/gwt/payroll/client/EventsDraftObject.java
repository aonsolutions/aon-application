package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EventsDraftObject {

	private ArrayList<Integer> workplaceContracts = new ArrayList<Integer>();; 
	
	private ArrayList<EventEmployee> eventEmployees = new ArrayList<EventEmployee>();
	
	private Map<Integer,Map<String, ArrayList<EmployeeEventsVariable>>> mapEventsVar = new HashMap<Integer, Map<String,ArrayList<EmployeeEventsVariable>>>();
	
	private DomainEmployeesServiceAsync employeesService;
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> agreementVariables = new ArrayList<String>();
	private ArrayList<String> calendarVariables  = new ArrayList<String>();
	private ArrayList<String> allVariables  = new ArrayList<String>();
	
	private ArrayList<String> employeeContractVariablesDB  = new ArrayList<String>();
	
	private Integer workplaceId;
	private Integer agreementId;
	
	private Integer contWorkplaceEmployeesId = 0;

	public EventsDraftObject(Integer workplaceId, Integer agreementId,
			DomainEmployeesServiceAsync employeesService, EventMetaData... eventsMetaData) {
		
		this.workplaceId = workplaceId;
		this.agreementId = agreementId;
		this.employeesService = employeesService;
		
		initCalendarVariables();
	}
	
	private void initCalendarVariables() {
		this.calendarVariables = new ArrayList<String>();
		calendarVariables.add("DIAS_TRABAJADOS");
		calendarVariables.add("DIAS_VACACIONES");
		calendarVariables.add("DIAS_INACTIVIDAD");
		calendarVariables.add("DIAS_AUSENCIA");
		calendarVariables.add("DIAS_HUELGA");
		calendarVariables.add("DIAS_ERE");
		calendarVariables.add("DIAS_ERE_FZA");
		calendarVariables.add("DIAS_ERE_FZA_EXON");
	}
	
	public ArrayList<String> getCalendarVariables() {
		return this.calendarVariables;
	}
	
	public Integer getAgreementId() {
		return this.agreementId;
	}
	
	public ArrayList<String> getAgreementVariables() {
		return this.agreementVariables;
	}
	
	public Boolean isCalendarVariable(String var) {
		return calendarVariables.contains(var);
	}
	
	// --------------------------------------------- GETTERS / SETTERS
	
	public ArrayList<String> getEmployeesVariables() {
		// Result List
		ArrayList<String> result = new ArrayList<String>();
		
		for(String var : this.allVariables) {
			if(isCalendarVariable(var))
				continue;
			result.add(var);
		}
		
		return result;
	}
	
	public ArrayList<String> getAllVariables() {
		return this.allVariables;
	}
	
	public Map<Integer,Map<String, ArrayList<EmployeeEventsVariable>>> getMapEventsVar() {
		return mapEventsVar;
	}

	public void setMapEventsVar(Map<Integer,Map<String, ArrayList<EmployeeEventsVariable>>> mapEventsVar) {
		this.mapEventsVar = mapEventsVar;
	}
	
	public ArrayList<EventEmployee> getEventEmployees() {
		return eventEmployees;
	}
	
	// --------------------------------
	//      METODOS SYNC DB
	// --------------------------------
	
	public void getWorkPlaceEmployeesDB(int year, Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure) {
		
		employeesService.getWorkplaceEmployeesEvents(this.workplaceId, new AsyncCallback<WorkplaceEmployees>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				eventEmployees.clear();
				mapEventsVar.clear();
				workplaceContracts.clear();
				
				for(EmployeeInfo employeeDB : result.getWorkplaceEmployees()){
					//Crear empleado e inicializar mapEventsObject
					EventEmployee eventEmployee = new EventEmployee();
					eventEmployee.setContractId(employeeDB.getContractId());
					eventEmployee.setName(employeeDB.getName());
					eventEmployee.setSurName(employeeDB.getSurName());
					eventEmployee.setFullName(employeeDB.getName() + " " + employeeDB.getSurName());
					
					eventEmployees.add(eventEmployee);
					
					mapEventsVar.put(employeeDB.getContractId(), new HashMap<String, ArrayList<EmployeeEventsVariable>>());
					
					workplaceContracts.add(employeeDB.getContractId());
				}
				
				getAgreementVars(year,
					r -> {
							success.accept(result);
						}, 
					f -> {});
				
			}
			
		});
	}
	
	public void getAgreementVars(int year, Consumer<Map<String,String>> success, Consumer<Throwable> failure) {
		
		employeesService.getWorkplaceEventsVariables(workplaceId, agreementId, new Date(year,0,1), new Date(year,11,31), new AsyncCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				agreementVariables.clear();
				allVariables.clear();
				calendarVariables.clear();
				initCalendarVariables();
				
				agreementVariables.add("BONIFICACION_FORMACION_CONTINUA");
				
				for(String var : result.keySet())
					agreementVariables.add(var);
				
				allVariables.addAll(calendarVariables);
				allVariables.addAll(agreementVariables);
				
				initializeDBVariables(allVariables, 
						s -> { 
								success.accept(result);
							}, 
						f -> {}
				);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void initializeDBVariables(ArrayList<String> allVariables, Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		
		contWorkplaceEmployeesId = 0;
		
		for(Integer contractId : workplaceContracts){
		
			employeesService.getEmployeeEventsByContract(contractId, allVariables, new AsyncCallback<EmployeeEventsData>(){
	
				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}
	
				@Override
				public void onSuccess(EmployeeEventsData resultEmployeeEventsData) {
					
					contWorkplaceEmployeesId++;
					
					setEmployeeEventData(contractId, resultEmployeeEventsData);
					
					if(workplaceContracts.size() == contWorkplaceEmployeesId){
						Timer timer = new Timer() {

							@Override
							public void run() {
								success.accept(resultEmployeeEventsData);
								this.cancel();
							}
						};
						
						timer.schedule(1500);
					}
				}
	
				private void setEmployeeEventData(Integer contractId, EmployeeEventsData resultEmployeeEventsData) {
					for(EventEmployee eventEmployee : eventEmployees) {
						if(contractId.equals(eventEmployee.getContractId()) || contractId == eventEmployee.getContractId()) {
							eventEmployee.setEmployeeEventsData(resultEmployeeEventsData);
							mapEventsVar.put(contractId, eventEmployee.getEmployeeEventsData().getEventDateVarList());
						}
					}
				}

			});
		}
	}
	
	// --------------------------------------------- UPDATE DATABASE
	
	public void updateEventsDraft(Consumer<ArrayList<EventEmployee>> success, Consumer<Throwable> failure) {
		
		employeesService.setEventsDraft(eventEmployees, new AsyncCallback<ArrayList<EventEmployee>>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(ArrayList<EventEmployee> result) {
				success.accept(result);
			}
			
		});
		
	}
	
	// --------------------------------------------- EVENT PAGE METHODS
	
	public ArrayList<EmployeeEventsVariable> getListEmployeeEventsVaribales (Integer contractId ,String varName){
		return this.mapEventsVar.get(contractId).get(varName);
	}
	
	@SuppressWarnings("deprecation")
	public EmployeeEventsVariable getEmployeeEventsVariableByMonth (Integer contractId, String varName, int month, Integer year){
		ArrayList<EmployeeEventsVariable> varList = this.mapEventsVar.get(contractId).getOrDefault(varName, null);
		
		if(null != varList) {
			for (EmployeeEventsVariable e : varList){
				if(year != e.getStartDate().getYear())
					continue;
				if (month == e.getStartDate().getMonth())
					return e;
			}
		}
		
		return null;
	}

	public void setValueByMonth(Integer contractId, String variableName, String value, Date startDate, Date endDate) {
//		Window.alert("ADD DATA --> " + variableName + " = " + value + ", Start : " + startDate + " End : " + endDate);
		for(EventEmployee eventEmployee : eventEmployees) {
			if(contractId.equals(eventEmployee.getContractId()) || contractId == eventEmployee.getContractId()) {
				eventEmployee.getEmployeeEventsData().addEventData(variableName, startDate, endDate, value);
				mapEventsVar.put(contractId, eventEmployee.getEmployeeEventsData().getEventDateVarList());
			}
		}
	}

	public EventEmployee getEmployeeByFullName(String fullName) {
		for(EventEmployee eventEmployee : eventEmployees) {
			if(fullName.equals(eventEmployee.getFullName()) || fullName == eventEmployee.getFullName())
					return eventEmployee;
		}
		return null;
	}
	
	// ---------------------------------------------------------------------------------------------------
	//									METODOS GETTER, SETTER, ADD
	// ---------------------------------------------------------------------------------------------------

//	public Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> getInformation(){
//		return this.mapEventsObject;
//	}
//	
//	public Integer getEventEmployeeId(String employeeName){
//		for(EventEmployee employee: workplaceEmployees){
//			if(employee.isEmployee(employeeName))
//				return employee.getEmployeeId();
//		}
//		return null;
//	}
//
//
//	public EmployeeEventsVariable getEmployeeVariableByDate(Integer employeeId, Date findingDate, String variableName) {
//		Map<String, ArrayList<EmployeeEventsVariable>> draftVariableMap = this.draftMapEventsObject.getOrDefault(employeeId, null);
//		if(null != draftVariableMap){
//			ArrayList<EmployeeEventsVariable> variableList = draftVariableMap.getOrDefault(variableName, null);
//			if(null != variableList){
//				for(EmployeeEventsVariable eventVariable : variableList){
//					if(eventVariable.getStartDate().equals(findingDate))
//						return eventVariable;
//				}
//			}
//		}
//		
//
//		Map<String, ArrayList<EmployeeEventsVariable>> variableMap = this.mapEventsObject.getOrDefault(employeeId, null);
//		if(null != variableMap){
//			ArrayList<EmployeeEventsVariable> variableList = variableMap.getOrDefault(variableName, null);
//			if(null != variableList){
//				for(EmployeeEventsVariable eventVariable : variableList){
//					if(eventVariable.getStartDate().equals(findingDate))
//						return eventVariable;
//				}
//			}
//		}
//		
//		return null;
//	}
//
//
//	public void addEvent(Integer employeeId, String varName, Double value, Date startDate, Date endDate) {
//		EmployeeEventsVariable variable = new EmployeeEventsVariable(startDate, endDate, value);
//		EmployeeEventsVariable removeVar = null;
//		if(this.draftMapEventsObject.containsKey(employeeId)){
//			if(this.draftMapEventsObject.get(employeeId).containsKey(varName)){
//				for(EmployeeEventsVariable var : this.draftMapEventsObject.get(employeeId).get(varName)){
////					Window.alert("Borrar Var : " + var.getStartDate() + " == " + startDate);
//					if(var.getStartDate().equals(startDate)){
//						removeVar = var;
//						continue;
//					}	
//				}
//				this.draftMapEventsObject.get(employeeId).get(varName).remove(removeVar);
//				this.draftMapEventsObject.get(employeeId).get(varName).add(variable);
//			}else{
//				this.draftMapEventsObject.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
//				this.draftMapEventsObject.get(employeeId).get(varName).add(variable);
//			}
//		}else{
//			this.draftMapEventsObject.put(employeeId, new HashMap<String, ArrayList<EmployeeEventsVariable>>());
//			this.draftMapEventsObject.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
//			this.draftMapEventsObject.get(employeeId).get(varName).add(variable);
//		}
//		
//		this.undoManager.add(new SetVariableEdit(removeVar, variable, varName, employeeId));
//	}
//
//
//	public boolean hasChanged(Integer employeeId, String varName, EmployeeEventsVariable variable) {
//		if(null == this.draftMapEventsObject.get(employeeId))
//			return false;
//		else
//			if(null == this.draftMapEventsObject.get(employeeId).get(varName))
//				return false;
//		
//		return this.draftMapEventsObject.get(employeeId).get(varName).contains(variable);
//	}
//	
//	public ArrayList<EventEmployee> getWorkplaceEmployees(){
//		return this.workplaceEmployees;
//	}
//	
//	public Set<String> getAllVariables(){
//		return this.allVariables;
//	}
//	
//	public ArrayList<Integer> getEmployeesId(){
//		return this.workplaceEmployeesId;
//	}
//	
//	// ---------------------------------------------------------------------------------------------------
//	//									  METODO GUARDAR
//	// ---------------------------------------------------------------------------------------------------
//	
//	public void updateEventsWorkplace(Consumer<EventsWorkplace> success, Consumer<Throwable> failure) {
//		EventsWorkplace updateEventsWorkplace = new EventsWorkplace();
//		updateEventsWorkplace.setUpdateEventsWorkplace(createUpdateEventsWorkplace());
//
//		employeesServiceAsync.setEventsWorkplace(updateEventsWorkplace, new AsyncCallback<EventsWorkplace>(){
//
//			@Override
//			public void onFailure(Throwable caught) {
//				failure.accept(caught);
//				
//			}
//
//			@Override
//			public void onSuccess(EventsWorkplace result) {
//				draftMapEventsObject.clear();
//				mapEventsObject.clear();
//				success.accept(result);
//				
//			}
//			
//		});
//	}
//
//
//	private List<Quintet<Integer, String, Date, Date, String>> createUpdateEventsWorkplace() {
//		Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> updateMap = new HashMap<Integer, Map<String, ArrayList<EmployeeEventsVariable>>>();
//		
////		for (Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : mapEventsObject.entrySet()){
////			updateMap.put(entry.getKey(), entry.getValue());
////		}
//		
//		for (Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : draftMapEventsObject.entrySet()){
//			Integer employeeId = entry.getKey();
//			for(Entry<String, ArrayList<EmployeeEventsVariable>> entryVar : entry.getValue().entrySet()){
//				String varName = entryVar.getKey();
//				for(EmployeeEventsVariable var : entryVar.getValue()){
//					addUpdateEventVarToMap(updateMap, employeeId, varName, var);
//				}
//			}
//		}
//		
//		return convertMapToList(updateMap);
//	}
//
//
//	private List<Quintet<Integer, String, Date, Date, String>> convertMapToList(
//			Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> updateMap) {
//		
//		List<Quintet<Integer, String, Date, Date, String>> updateList = new ArrayList<>();
//		
//		for (Entry<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> entry : updateMap.entrySet()){
//			Integer employeeId = entry.getKey();
//			for(Entry<String, ArrayList<EmployeeEventsVariable>> entryVar : entry.getValue().entrySet()){
//				String varName = entryVar.getKey();
//				for(EmployeeEventsVariable var : entryVar.getValue()){
//					java.sql.Date sqlStartDate = new java.sql.Date(var.getStartDate().getTime());
//					java.sql.Date sqlEndDate = new java.sql.Date(var.getEndDate().getTime());
//					
//					updateList.add(new Quintet<Integer, String, Date, Date, String>(employeeId, varName, sqlStartDate, sqlEndDate, var.getValue() == null ? null : var.getValue().toString()));
//				}
//			}
//		}
//		
//		return updateList;
//	}
//
//
//	private void addUpdateEventVarToMap(Map<Integer, Map<String, ArrayList<EmployeeEventsVariable>>> updateMap,
//			Integer employeeId, String varName, EmployeeEventsVariable var) {
//		EmployeeEventsVariable removeVar = null;
//		if(updateMap.containsKey(employeeId)){
//			if(updateMap.get(employeeId).containsKey(varName)){
//				for(EmployeeEventsVariable varDraft : this.draftMapEventsObject.get(employeeId).get(varName)){
//					if(varDraft.getStartDate().equals(var.getStartDate())){
//						removeVar = varDraft;
//						continue;
//					}	
//				}
//				updateMap.get(employeeId).get(varName).remove(removeVar);
//				updateMap.get(employeeId).get(varName).add(var);
//			}else{
//				updateMap.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
//				updateMap.get(employeeId).get(varName).add(var);
//			}
//		}else{
//			updateMap.put(employeeId, new HashMap<String, ArrayList<EmployeeEventsVariable>>());
//			updateMap.get(employeeId).put(varName, new ArrayList<EmployeeEventsVariable>());
//			updateMap.get(employeeId).get(varName).add(var);
//		}
//		
//	}
//	
//	public boolean isFullJourney(Integer employeeId) {
//		for(EventEmployee employee : getWorkplaceEmployees()) {
//			if(employeeId == employee.getEmployeeId())
//				return employee.isFullTime();
//		}
//		return false;
//	}

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
