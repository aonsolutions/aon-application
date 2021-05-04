package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeEventsDraftObject {
	
	// ----------------------------------------------- Variables 
	
	private EmployeeEventsData employeeEventsData;
	private Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar;
	
	private Integer idEmployee;
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private EmployeeCalendarDraftObject employeeCalendar;
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> employeeContractVariables;
	
	private ArrayList<String> calendarVariables;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeEventsDraftObject(Integer idEmployee) {
		this.mapEventsVar = new HashMap<String, ArrayList<EmployeeEventsVariable>>();
		this.idEmployee = idEmployee;
		this.employeeContractVariables = new ArrayList<String>();
		this.calendarVariables = new ArrayList<String>();
	}
	
	// ----------------------------------------------- initCalendarVariables 

	private void initCalendarVariables() {
		calendarVariables.clear();
		calendarVariables.add("DIAS_TRABAJADOS");
		calendarVariables.add("DIAS_VACACIONES");
		calendarVariables.add("DIAS_INACTIVIDAD");
		calendarVariables.add("DIAS_AUSENCIA");
		calendarVariables.add("DIAS_HUELGA");
		calendarVariables.add("DIAS_ERE");
		calendarVariables.add("DIAS_ERE_FZA");
		calendarVariables.add("DIAS_ERE_FZA_EXON");
		calendarVariables.add("HORAS_COMPLEMENTARIAS");
		calendarVariables.add("HORAS_EXTRAS");
	}
	
	public ArrayList<String> getCalendarVariables() {
		return this.calendarVariables;
	}
	
	public ArrayList<String> getAllVariables() {
		return this.employeeContractVariables;
	}

	public ArrayList<String> getAgreementVariables() {
		ArrayList<String> agreementVars = new ArrayList<String>();
		for(String var : employeeContractVariables)
			if(!isCalendarVariable(var))
				agreementVars.add(var);
		
		return agreementVars;
	}


	// ----------------------------------------------- EmployeeEventsDraftObject.Methods
	
	public Integer getIdEmployee() {
		return idEmployee;
	}
	
	public void setEmployeeCalendar(EmployeeCalendarDraftObject employeeCalendarDraftobject) {
		this.employeeCalendar = employeeCalendarDraftobject;
	}
	
	public EmployeeCalendarDraftObject getEmployeeCalendar() {
		return this.employeeCalendar;
	}
	
	public ArrayList<String> getEmployeeContractVariables(ArrayList<String> variablesToShow) {
		// Filter variables list
		ArrayList<String> filterVariablesList = new ArrayList<String>();
		
		// Contract Type
		if(employeeEventsData.getTC2() != "\"421\"") {
			filterVariablesList.add("HORAS_FORMACION_PRESENCIAL");
			filterVariablesList.add("HORAS_FORMACION_DISTANCIA");
			filterVariablesList.add("HORAS_TUTORIA");
			filterVariablesList.add("BONIFICACION_TUTORIA");
		}
		
		// Fulltime Journey
		if(isFullJourney())
			filterVariablesList.add("HORAS_COMPLEMENTARIAS");
		else
			filterVariablesList.add("HORAS_EXTRAS");
		
		// Result List
		ArrayList<String> result = new ArrayList<String>();
		
		for(String var : variablesToShow) {
			if(filterVariablesList.contains(var))
				continue;
			result.add(var);
		}
		
		return result;
	}
	
	public ArrayList<String> getEmployeeContractVariables() {
		// Filter variables list
		ArrayList<String> filterVariablesList = new ArrayList<String>();
		
		// Contract Type
		if(employeeEventsData.getTC2() != "\"421\"") {
			filterVariablesList.add("HORAS_FORMACION_PRESENCIAL");
			filterVariablesList.add("HORAS_FORMACION_DISTANCIA");
			filterVariablesList.add("HORAS_TUTORIA");
			filterVariablesList.add("BONIFICACION_TUTORIA");
		}
		
		// Fulltime Journey
		if(isFullJourney())
			filterVariablesList.add("HORAS_COMPLEMENTARIAS");
		else
			filterVariablesList.add("HORAS_EXTRAS");
		
		// Result List
		ArrayList<String> result = new ArrayList<String>();
		
		for(String var : this.employeeContractVariables) {
			if(filterVariablesList.contains(var))
				continue;
			result.add(var);
		}
		
		return result;
	}
	
	public Boolean isCalendarVariable(String var) {
		return calendarVariables.contains(var);
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

	public boolean isFullJourney() {
		return this.employeeEventsData.isFullTimeJourney();
	}
	
	public String getTC2() {
		return this.employeeEventsData.getTC2();
	}
	
	public Date getContractStartDate() {
		return this.employeeEventsData.getContractStartDate();
	}
	
	public Date getContractEndDate() {
		return this.employeeEventsData.getContractEndDate();
	}
	
	// ----------------------------------------------- DataBase.Methods
	
	public void initializeDBEventsVariables(int year, Consumer<ContextDescriptor> success, Consumer<Throwable> failure) {
		
		employeesService.getEmployeeEventsVariables(idEmployee, DateUtils.getDate(0, year), DateUtils.getLastDayOfMonth(DateUtils.getDate(11, year)), 
				new AsyncCallback<ContextDescriptor>() {
			
			@Override
			public void onSuccess(ContextDescriptor context) {
				
				calendarVariables.clear();
				initCalendarVariables();
				
				ArrayList<String> allStaticVariables = new ArrayList<String>();
				
				allStaticVariables.addAll(calendarVariables);
				allStaticVariables.add("IMPORTE_HORA_EXTRA");
				allStaticVariables.add("HORAS_FORMACION_PRESENCIAL");
				allStaticVariables.add("HORAS_FORMACION_DISTANCIA");
				allStaticVariables.add("HORAS_TUTORIA");
				allStaticVariables.add("BONIFICACION_TUTORIA");
				allStaticVariables.add("BONIFICACION_FORMACION_CONTINUA");
				
				employeeContractVariables.clear();
				employeeContractVariables.addAll(allStaticVariables);
				
				Set<String> contextVariables = filterContextVariables(context.getVariables(), allStaticVariables);
				
				for(String varName : contextVariables) {
					employeeContractVariables.add(varName);
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
		
		employeesService.getEmployeeEventsByContract(this.idEmployee, this.employeeContractVariables, new AsyncCallback<EmployeeEventsData>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeEventsData resultEmployeeEventsData) {
				employeeEventsData = resultEmployeeEventsData;
				employeeContractVariables = employeeEventsData.getEmployeeContractVariables();
				mapEventsVar = employeeEventsData.getEventDateVarList();
				success.accept(resultEmployeeEventsData);
			}
		});
	}
	
	public void updateDBCalendar(Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		
		employeesService.setEmployeeEvents(this.idEmployee, employeeEventsData, new AsyncCallback<EmployeeEventsData>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(EmployeeEventsData result) {
				success.accept(result);
			}
			
		});
		
	}
	
	private Set<String> filterContextVariables(Set<String> contextVariables, ArrayList<String> allStaticVariables) {
		Set<String> resultSet = new LinkedHashSet<String>();
		
		// Filter set
		Set<String> filterSet = new LinkedHashSet<String>();
		filterSet.add("PAGA_EXTRA_HELP");
		filterSet.add("TC2");
		filterSet.add("SALARIO_VARIABLE_DIA");
		filterSet.add("AÑOS_TRABAJADOS");
		filterSet.add("GRUPO_COTIZACION");
		filterSet.add("ANTIGUEDAD_HELP");
		filterSet.add("GET_VARIABLE");
		
		filterSet.addAll(allStaticVariables);
		
		for(String var : contextVariables) {
			if(contains(filterSet, var))
				continue;
			
			resultSet.add(var);
		}
		
		return resultSet;
	}
	
	private boolean contains(Set<String> filterSet, String var) {
		for(String filterVar : filterSet) {
			if(filterVar.equals(var) || filterVar == var || var.contains("OS_TRABAJADOS")) // AÑOS_TRABAJADOS
				return true;
		}
		return false;
	}

	// ----------------------------------------------- EmployeeEventsDraft.Methods
	
	public ArrayList<EmployeeEventsVariable> getListEmployeeEventsVaribales (String varName){
		return this.mapEventsVar.getOrDefault(varName, null);
	}
	
	public EmployeeEventsVariable getEmployeeEventsVariableByMonth (String varName, int month, Integer year){
		ArrayList<EmployeeEventsVariable> varList = this.mapEventsVar.getOrDefault(varName, null);
		
		if(null != varList) {
			for (EmployeeEventsVariable e : this.mapEventsVar.get(varName)){
				if(year != DateUtils.getYear(e.getStartDate()))
					continue;
				if (month == DateUtils.getMonth(e.getStartDate()))
					return e;
			}
		}
		
		return null;
	}

	public void setValueByMonth(String variableName, String value, Date startDate, Date endDate) {
		employeeEventsData.addEventData(variableName, startDate, endDate, value);
		mapEventsVar = employeeEventsData.getEventDateVarList();
	}

}

