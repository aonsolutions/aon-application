package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Variables.VariablesObject;
import com.esferalia.aon.gwt.payroll.shared.SystemVariable;
import com.esferalia.aon.gwt.payroll.shared.SystemVariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DomainSystemVariablesObject implements VariablesObject<SystemVariable> {
	
	// ----------------------------------------------- Variables 
	
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	private List<SystemVariable> systemVariables;
	private List<SystemVariable> systemVariablesFiltered;
	
	private Integer domainId;
	private Date startDate;
	private Date endDate;
	
	// ----------------------------------------------- Constructor 
	
	public DomainSystemVariablesObject(Integer domainId, Date startDate, Date endDate) {
		this.domainId = domainId;
		this.startDate =  startDate;
		this.endDate = endDate;
		this.systemVariables = new ArrayList<>();
		this.systemVariablesFiltered = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	@Override
	public void getVariables(Consumer<List<SystemVariable>> success, Consumer<Throwable> failure) {
		enterprisesService.getSystemVariables(this.domainId, new AsyncCallback<List<SystemVariable>>() {
			@Override
			public void onSuccess(List<SystemVariable> result) {
				systemVariables = result;
				success.accept(systemVariables);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	@Override
	public void createVariable(SystemVariable contractVariable, Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.createSystemVariable(this.domainId, contractVariable, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	@Override
	public void updateVariables(Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.updateSystemVariables(this.domainId, this.systemVariables, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	@Override
	public List<SystemVariable> getVariables(String yearValue, String monthValue, String variableTypeValue) {
		this.systemVariablesFiltered.clear();
		
		// Se filtra solo por tipo de variable sin fechas
		if(AonStringUtils.isBlank(yearValue)) {
			SystemVariableType contractVariableType = getVariableType(variableTypeValue);
			
			for(SystemVariable contractVariable : systemVariables) {
				if(contractVariable.getId() < 0)
					continue;
				
				if(null == contractVariableType || contractVariableType.equals(contractVariable.getVariableType()))
					this.systemVariablesFiltered.add(contractVariable);
			}
		// Se filtra por tipo de variable y fechas
		} else {
			Integer year = Integer.parseInt(yearValue);
			Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
			Date endDate = DateUtils.getLastDayOfYear(year - 1900);
			
			if(AonStringUtils.isNotBlank(monthValue)) {
				Integer month = Integer.parseInt(monthValue);
				startDate.setMonth(month);
				startDate = DateUtils.getFirstDayOfMonth(startDate);
				endDate = DateUtils.getLastDayOfMonth(startDate);
			}
			
			SystemVariableType contractVariableType = getVariableType(variableTypeValue);
			
			for(SystemVariable contractVariable : systemVariables) {
				if(contractVariable.getId() < 0)
					continue;
				
				if(	(isInPeriod(startDate, endDate, contractVariable.getStartDate(), contractVariable.getEndDate()) ||
					(null != contractVariable.getEndDate() && isInPeriod(startDate, endDate, contractVariable.getEndDate(), contractVariable.getEndDate())) ||
					(null == contractVariable.getEndDate() && (isInPeriod(startDate, endDate, contractVariable.getStartDate(), contractVariable.getEndDate()) || DateUtils.isBeforeOrEquals(contractVariable.getStartDate(), startDate))))
					&& (null == contractVariableType || contractVariableType.equals(contractVariable.getVariableType())))
					
					this.systemVariablesFiltered.add(contractVariable);
			}
		}
		
		this.systemVariablesFiltered.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
		
		return this.systemVariablesFiltered;
	}

	private SystemVariableType getVariableType(String variableTypeValue) {
		switch (variableTypeValue) {
		case "0":
			return SystemVariableType.SYSTEM_DATA;
		default:
			return null;
		}
	}

	private boolean isInPeriod(Date start, Date end, Date varStart, Date varEnd) {
		return null == varStart || 
				(DateUtils.isAfterOrEquals(varStart, start) && DateUtils.isBeforeOrEquals(varStart, end)) || 
				(null == varEnd && DateUtils.isBeforeOrEquals(varStart, end)) ||
				(DateUtils.isBeforeOrEquals(varStart, start) && DateUtils.isAfterOrEquals(varEnd, end));
	}

	@Override
	public void deleteVariable(SystemVariable systemVariableDelete) {
		for(SystemVariable systemVariable : this.systemVariables){
			if(systemVariable.getId().equals(systemVariableDelete.getId())) {
				Integer id = systemVariable.getId();
				systemVariable.setId(id * -1);
			}
		}
	}
	
	// ----------------------------------------------- Getter
	
	@Override
	public Date getStartDate() {
		return this.startDate;
	}
	
	@Override
	public Date getEndDate() {
		return this.endDate;
	}
	
}

