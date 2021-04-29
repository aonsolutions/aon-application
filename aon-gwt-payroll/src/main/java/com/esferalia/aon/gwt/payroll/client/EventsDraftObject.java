package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EventsDraftObject {
	
	// ----------------------------------------------- Variables

	private ArrayList<Integer> workplaceContracts = new ArrayList<Integer>();; 
	
	private ArrayList<EventEmployee> eventEmployees = new ArrayList<EventEmployee>();
	
	private Map<Integer,Map<String, ArrayList<EmployeeEventsVariable>>> mapEventsVar = new HashMap<Integer, Map<String,ArrayList<EmployeeEventsVariable>>>();
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> agreementVariables = new ArrayList<String>();
	private ArrayList<String> calendarVariables  = new ArrayList<String>();
	private ArrayList<String> allVariables  = new ArrayList<String>();
	
	private Integer workplaceId;
	private Integer agreementId;
	
	private Integer contWorkplaceEmployeesId = 0;

	// ----------------------------------------------- Constructor
	
	public EventsDraftObject(Integer workplaceId, Integer agreementId, EventMetaData... eventsMetaData) {
		this.workplaceId = workplaceId;
		this.agreementId = agreementId;
		
		initCalendarVariables();
	}
	
	// ----------------------------------------------- initCalendarVariables
	
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
	
	// ----------------------------------------------- Getters / Setters
	
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
	
	// ----------------------------------------------- DataBase Methods
	
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
		
		employeesService.getWorkplaceEventsVariables(workplaceId, agreementId, DateUtils.getDate(0, year) , DateUtils.getLastDayOfMonth(DateUtils.getDate(11, year)), new AsyncCallback<Map<String,String>>() {
			
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
	
	// ----------------------------------------------- EvenstDraft.Methods
	
	public ArrayList<EmployeeEventsVariable> getListEmployeeEventsVaribales (Integer contractId ,String varName){
		return this.mapEventsVar.get(contractId).get(varName);
	}
	
	public EmployeeEventsVariable getEmployeeEventsVariableByMonth (Integer contractId, String varName, int month, Integer year){
		ArrayList<EmployeeEventsVariable> varList = this.mapEventsVar.get(contractId).getOrDefault(varName, null);
		
		if(null != varList) {
			for (EmployeeEventsVariable e : varList){
				if(year != DateUtils.getYear(e.getStartDate()))
					continue;
				if (month == DateUtils.getMonth(e.getStartDate()))
					return e;
			}
		}
		
		return null;
	}

	public void setValueByMonth(Integer contractId, String variableName, String value, Date startDate, Date endDate) {
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
	
}
