package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeEventsDraftObject {
	
	private static class EmployeeVariablesDrafObject extends  EmployeeEventsDraftObject {
		
		private static final ArrayList<String> EMPTY_LIST = new ArrayList<>();
		
		private EmployeeEventsDraftObject delegate;
		private ArrayList<String> variables = new ArrayList<>();
		
		private EmployeeVariablesDrafObject(Collection<String> variables, EmployeeEventsDraftObject delegate){
			super(delegate.idEmployee);
			this.delegate = delegate;
			this.variables.addAll(variables);
		}

		public boolean isEmployeeEvents() {
			return false;
		}
		
		public ArrayList<String> getCalendarVariables() {
			return EMPTY_LIST;
		}

		public ArrayList<String> getAgreementOnlyVariables() {
			return EMPTY_LIST;
		}

		public ArrayList<String> getContractVariables() {
			return EMPTY_LIST;
		}

		public ArrayList<String> getAllVariables() {
			return variables;
		}

		public ArrayList<String> getAgreementVariables() {
			return EMPTY_LIST;
		}

		public Integer getIdEmployee() {
			return delegate.getIdEmployee();
		}

		public void setEmployeeCalendar(EmployeeCalendarDraftObject employeeCalendarDraftobject) {
			delegate.setEmployeeCalendar(employeeCalendarDraftobject);
		}

		public EmployeeCalendarDraftObject getEmployeeCalendar() {
			return delegate.getEmployeeCalendar();
		}

		public ArrayList<String> getEmployeeContractVariables(ArrayList<String> variablesToShow) {
			return variables;
		}

		public ArrayList<String> getEmployeeContractVariables() {
			return variables;
		}

		public Double getAcumulateYear(String var) {
			return delegate.getAcumulateYear(var);
		}

		public EmployeeEventsVariable getEmployeeEventsVariable(String variableName) {
			return delegate.getEmployeeEventsVariable(variableName);
		}

		public Boolean isCalendarVariable(String var) {
			return delegate.isCalendarVariable(var);
		}

		public Boolean isAgreementVariable(String var) {
			return delegate.isAgreementVariable(var);
		}

		public boolean isContractVariable(String var) {
			return delegate.isContractVariable(var);
		}

		public String getAgreementVariablesValue(String var) {
			return delegate.getAgreementVariablesValue(var);
		}

		public Map<String, ArrayList<EmployeeEventsVariable>> getMapEventsVar() {
			return delegate.getMapEventsVar();
		}

		public void setMapEventsVar(Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar) {
			delegate.setMapEventsVar(mapEventsVar);
		}

		public boolean isFullJourney() {
			return delegate.isFullJourney();
		}

		public String getTC2() {
			return delegate.getTC2();
		}

		public Date getContractStartDate() {
			return delegate.getContractStartDate();
		}

		public Date getContractEndDate() {
			return delegate.getContractEndDate();
		}

		public void initializeDBEventsVariables(int year, Consumer<ContextDescriptor> success,
				Consumer<Throwable> failure) {
			delegate.initializeDBEventsVariables(year, success, failure);
		}

		public void initializeDBCalendar(Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
			delegate.initializeDBCalendar(success, failure);
		}

		public void updateDBCalendar(Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
			delegate.updateDBCalendar(success, failure);
		}

		public ArrayList<EmployeeEventsVariable> getListEmployeeEventsVaribales(String varName) {
			return delegate.getListEmployeeEventsVaribales(varName);
		}

		public void setListEmployeeEventsVaribales(String varName,
				ArrayList<EmployeeEventsVariable> employeeEventsVariables) {
			delegate.setListEmployeeEventsVaribales(varName, employeeEventsVariables);
		}

		public Double getAcumulateVariableByMonth(String varName, int month, Integer year) {
			return delegate.getAcumulateVariableByMonth(varName, month, year);
		}

		public boolean hasMoreThanOneValue(String varName, int month, Integer year) {
			return delegate.hasMoreThanOneValue(varName, month, year);
		}

		public void setValueByMonth(String variableName, String value, Date startDate, Date endDate) {
			delegate.setValueByMonth(variableName, value, startDate, endDate);
		}

		public void setSettleHolidayValueByMonth(String variableName, String value, Date startDate, Date endDate) {
			delegate.setSettleHolidayValueByMonth(variableName, value, startDate, endDate);
		}

		public EmployeeEventsDraftObject getEmployeeEventsDraftObject(String... variables) {
			return delegate.getEmployeeEventsDraftObject(variables);
		}

		
	}
	// ----------------------------------------------- Variables 
	
	private EmployeeEventsData employeeEventsData;
	private Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar;
	
	private Integer idEmployee;
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private EmployeeCalendarDraftObject employeeCalendar;
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> employeeContractVariables;
	
	private ArrayList<String> calendarVariables;
	private ArrayList<String> agreementVariables;
	private Map<String, String> agreementVariablesValue;
	private ArrayList<String> contractVariables;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeEventsDraftObject(Integer idEmployee) {
		this.mapEventsVar = new HashMap<>();
		this.idEmployee = idEmployee;
		this.employeeContractVariables = new ArrayList<>();
		this.calendarVariables = new ArrayList<>();
		this.agreementVariables = new ArrayList<>();
		this.contractVariables = new ArrayList<>();
		this.agreementVariablesValue = new HashMap<>();
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
	
	public boolean isEmployeeEvents() {
		return true;
	}
	
	public ArrayList<String> getCalendarVariables() {
		return this.calendarVariables;
	}
	
	public ArrayList<String> getAgreementOnlyVariables() {
		return this.agreementVariables;
	}
	
	public ArrayList<String> getContractVariables() {
		return this.contractVariables;
	}
	
	public ArrayList<String> getAllVariables() {
		LinkedHashSet<String> allVariablesListAux = new LinkedHashSet<>();
		
		allVariablesListAux.addAll(calendarVariables);
		allVariablesListAux.addAll(agreementVariables);
		allVariablesListAux.addAll(contractVariables);
		allVariablesListAux.addAll(employeeContractVariables);
		
		ArrayList<String> allVariablesList = new ArrayList<>(allVariablesListAux);
		allVariablesList.sort((var1, var2) -> var1.compareTo(var2));
		
		return allVariablesList;
	}

	public ArrayList<String> getAgreementVariables() {
		ArrayList<String> agreementVars = new ArrayList<>();
		for(String var : employeeContractVariables)
			if(Boolean.FALSE.equals(isCalendarVariable(var)))
				agreementVars.add(var);
		
		agreementVars.sort((var1, var2) -> var1.compareTo(var2));
		
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
		ArrayList<String> filterVariablesList = new ArrayList<>();
		
		// Contract Type
		if(!AonStringUtils.equalsIgnoreCase(employeeEventsData.getTC2(), "\"421\"")) {
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
		ArrayList<String> result = new ArrayList<>();
		
		for(String var : variablesToShow) {
			if(filterVariablesList.contains(var))
				continue;
			result.add(var);
		}
		
		result.sort((var1, var2) -> var1.compareTo(var2));
		
		return result;
	}
	
	public ArrayList<String> getEmployeeContractVariables() {
		// Filter variables list
		ArrayList<String> filterVariablesList = new ArrayList<>();
		
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
		ArrayList<String> result = new ArrayList<>();
		
		for(String var : this.employeeContractVariables) {
			if(filterVariablesList.contains(var))
				continue;
			result.add(var);
		}
		
		result.sort((var1, var2) -> var1.compareTo(var2));
		
		return result;
	}
	
	public Double getAcumulateYear(String var) {
		ArrayList<EmployeeEventsVariable> employeeEventsVariables = this.mapEventsVar.get(var);
		Date startDate = DateUtils.getFirstDayOfYear();
		Date endDate = DateUtils.getLastDayOfYear(DateUtils.getFirstDayOfMonth());
		Double accumulateYear = 0.0;
		
		if(null == employeeEventsVariables)
			return accumulateYear;
		
		for(EmployeeEventsVariable employeeEventsVariable : employeeEventsVariables) {
			if(null == employeeEventsVariable.getEndDate()) {
				accumulateYear = getAccumulateYearForNullEndPeriod(accumulateYear, employeeEventsVariable);
				break;
			}
			
			if(DateUtils.isAfterOrEquals(employeeEventsVariable.getStartDate(), startDate) &&
				(null == employeeEventsVariable.getEndDate() || DateUtils.isBeforeOrEquals(employeeEventsVariable.getEndDate(), endDate))) {
				
				Double value = employeeEventsVariable.getValue();
				accumulateYear += value;
			}
		}
		
		return accumulateYear;
	}
	
	private Double getAccumulateYearForNullEndPeriod(Double accumulateYear, EmployeeEventsVariable employeeEventsVariable) {
		Date endDate = DateUtils.getLastDayOfYear(DateUtils.getFirstDayOfMonth());
		int monthsBetween = DateUtils.getMonths(employeeEventsVariable.getStartDate(), endDate);
		Double accumulate = employeeEventsVariable.getValue() * monthsBetween;
		
		return accumulateYear + accumulate;
	}

	public EmployeeEventsVariable getEmployeeEventsVariable(String variableName) {
		ArrayList<EmployeeEventsVariable> employeeEventsVariables = this.mapEventsVar.get(variableName);
		if(employeeEventsVariables.isEmpty())
			return null;
		return employeeEventsVariables.get(0);
	}
	
	public Boolean isCalendarVariable(String var) {
		return calendarVariables.contains(var);
	}
	
	public Boolean isAgreementVariable(String var) {
		return agreementVariables.contains(var);
	}
	
	public boolean isContractVariable(String var){
		return this.employeeContractVariables.contains(var);
	}
	
	public String getAgreementVariablesValue(String var) {
		return agreementVariablesValue.get(var);
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
				
				ArrayList<String> allStaticVariables = new ArrayList<>();
				
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
				
				// Check agreement only variables
				for(Entry<String, ArrayList<VariableDescriptor>> entry : context.getVariableDescriptors().entrySet()) {
					String variableName = entry.getKey();
					ArrayList<VariableDescriptor> variables = entry.getValue();
					if(null == variables || variables.isEmpty())
						continue;
					
					for(VariableDescriptor variableDescriptor : variables) {
						if(variableDescriptor.getScope() == Scope.AGREEMENT && !agreementVariables.contains(variableName)) {
							agreementVariables.add(variableName);
							agreementVariablesValue.put(variableName, variableDescriptor.getExpression());
						}
						
						if(variableDescriptor.getScope() == Scope.CONTRACT && !contractVariables.contains(variableName) && !continueVariable(variableName))
							contractVariables.add(variableName);
					}
				}
				
				initializeDBCalendar(
						s -> success.accept(context), 
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
				
				for(String agreementVariable : agreementVariables)
					employeeContractVariables.remove(agreementVariable);
				
				for(String contractVariable : contractVariables)
					employeeContractVariables.remove(contractVariable);
				
				sortVariablesList();
				
				success.accept(resultEmployeeEventsData);
			}
		});
	}

	private void sortVariablesList() {
		calendarVariables.sort((var1, var2) -> var1.compareTo(var2));
		agreementVariables.sort((var1, var2) -> var1.compareTo(var2));
		contractVariables.sort((var1, var2) -> var1.compareTo(var2));
		employeeContractVariables.sort((var1, var2) -> var1.compareTo(var2));
	}
	
	public void updateDBCalendar(Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		employeesService.setEmployeeEventsByContract(this.idEmployee, employeeEventsData, new AsyncCallback<EmployeeEventsData>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeEventsData result) {
				success.accept(result);
			}
			
		});
		
	}
	
	private Set<String> filterContextVariables(Set<String> contextVariables, ArrayList<String> allStaticVariables) {
		Set<String> resultSet = new LinkedHashSet<>();
		
		// Filter set
		Set<String> filterSet = new LinkedHashSet<>();
		filterSet.add("PAGA_EXTRA_HELP");
		filterSet.add("TC2");
		filterSet.add("SALARIO_VARIABLE_DIA");
		filterSet.add("AÑOS_TRABAJADOS");
		filterSet.add("GRUPO_COTIZACION");
		filterSet.add("ANTIGUEDAD_HELP");
		filterSet.add("GET_VARIABLE");
		filterSet.add("LABORABLE");
		filterSet.add("OS_TRABAJADOS");
		filterSet.add("DIAS_");
		filterSet.add("OCUPACI");
		filterSet.add("COEFICIENTE_PARCIALIDAD");
		filterSet.add("SMI");
		filterSet.add("HIDE");
		filterSet.add("TODO");
		filterSet.add("SEPE_IDE");
		filterSet.add("IDE");
		filterSet.add("COMUNICATION_DATE");
		filterSet.add("ORIGINAL_START_DATE");
		filterSet.add("ORIGINAL_END_DATE");
		
		filterSet.addAll(allStaticVariables);
		
		for(String var : contextVariables) {
			if(contains(filterSet, var))
				continue;
			
			resultSet.add(var);
		}
		
		return resultSet;
	}
	
	private boolean continueVariable(String variableName) {
		// Filter set
		Set<String> filterSet = new LinkedHashSet<>();
		filterSet.add("PAGA_EXTRA_HELP");
		filterSet.add("TC2");
		filterSet.add("SALARIO_VARIABLE_DIA");
		filterSet.add("AÑOS_TRABAJADOS");
		filterSet.add("GRUPO_COTIZACION");
		filterSet.add("ANTIGUEDAD_HELP");
		filterSet.add("GET_VARIABLE");
		filterSet.add("LABORABLE");
		filterSet.add("OS_TRABAJADOS");
		filterSet.add("DIAS_");
		filterSet.add("OCUPACI");
		filterSet.add("COEFICIENTE_PARCIALIDAD");
		filterSet.add("SEPE_IDE");
		filterSet.add("IDE");
		filterSet.add("COMUNICATION_DATE");
		filterSet.add("ORIGINAL_START_DATE");
		filterSet.add("ORIGINAL_END_DATE");
		
		
		for(String filterVar : filterSet){
			if(AonStringUtils.equalsIgnoreCase(filterVar, variableName) || AonStringUtils.containsIgnoreCase(variableName, filterVar))
				return true;
		}
		
		return false;
	}
	
	private boolean contains(Set<String> filterSet, String var) {
		for(String filterVar : filterSet) {
			if(AonStringUtils.equalsIgnoreCase(filterVar, var) || AonStringUtils.containsIgnoreCase(var, filterVar))
				return true;
		}
		return false;
	}

	// ----------------------------------------------- EmployeeEventsDraft.Methods
	
	public ArrayList<EmployeeEventsVariable> getListEmployeeEventsVaribales (String varName){
		return this.mapEventsVar.getOrDefault(varName, null);
	}

	public void setListEmployeeEventsVaribales (String varName, ArrayList<EmployeeEventsVariable> employeeEventsVariables){
		employeeEventsData.setEventData(varName, employeeEventsVariables);
		mapEventsVar.put(varName, employeeEventsVariables);
	}
	
	public Double getAcumulateVariableByMonth (String varName, int month, Integer year){
		ArrayList<EmployeeEventsVariable> varList = this.mapEventsVar.getOrDefault(varName, null);
		Date firstDayMonth = DateUtils.getFirstDayOfMonth(DateUtils.getDate(month, year));
		Date lastDayMonth = DateUtils.getLastDayOfMonth(DateUtils.getDate(month, year));
		Double acumulateMonth = null;
		
		if(null != varList) {
			for (EmployeeEventsVariable e : varList){
				if(null == e.getEndDate() && DateUtils.isBeforeOrEquals(e.getStartDate(), firstDayMonth)) {
					acumulateMonth = e.getValue();
					break;
				}
				
				if(year != DateUtils.getYear(e.getStartDate()))
					continue;
				if ((DateUtils.isAfterOrEquals(e.getStartDate(), firstDayMonth) && DateUtils.isBeforeOrEquals(e.getStartDate(), lastDayMonth)) &&
						null != e.getValue()) {
					Double value = e.getValue();
					acumulateMonth = null == acumulateMonth ? value : (acumulateMonth+value);
				}	
			}
		}
		
		return acumulateMonth;
	}

	public boolean hasMoreThanOneValue (String varName, int month, Integer year){
		ArrayList<EmployeeEventsVariable> varList = this.mapEventsVar.getOrDefault(varName, null);
		Date firstDayMonth = DateUtils.getFirstDayOfMonth(DateUtils.getDate(month, year));
		Date lastDayMonth = DateUtils.getLastDayOfMonth(DateUtils.getDate(month, year));
		Integer values = 0;
		
		if(null != varList) {
			for (EmployeeEventsVariable e : varList){
				if(null == e.getEndDate() && DateUtils.isBeforeOrEquals(e.getStartDate(), firstDayMonth)) {
					values = 1;
					break;
				}
				
				if(year != DateUtils.getYear(e.getStartDate()))
					continue;
				
				if ((DateUtils.isAfterOrEquals(e.getStartDate(), firstDayMonth) && DateUtils.isBeforeOrEquals(e.getStartDate(), lastDayMonth)) 
						&& null != e.getValue())
					values++;	
			}
		}
		
		return values > 1;
	}

	public void setValueByMonth(String variableName, String value, Date startDate, Date endDate) {
		employeeEventsData.addEventData(variableName, startDate, endDate, value);
		mapEventsVar = employeeEventsData.getEventDateVarList();
	}
	
	public void setSettleHolidayValueByMonth(String variableName, String value, Date startDate, Date endDate) {
		employeeEventsData.removeEventData(variableName);
		employeeEventsData.addEventData(variableName, startDate, endDate, value);
		mapEventsVar = employeeEventsData.getEventDateVarList();
	}
	
	public EmployeeEventsDraftObject getEmployeeEventsDraftObject(String ...variables) {
		return new EmployeeVariablesDrafObject(Arrays.asList(variables), this);
	}

}

