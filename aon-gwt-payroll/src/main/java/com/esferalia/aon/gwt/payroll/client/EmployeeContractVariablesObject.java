package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable.VariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractVariablesObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private List<ContractVariable> contractVariables;
	private List<ContractVariable> contractVariablesFiltered;
	
	private Integer contractId;
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractVariablesObject(Integer contractId, Date contractStartDate, Date contractEndDate) {
		this.contractId = contractId;
		this.contractStartDate =  contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractVariables = new ArrayList<>();
		this.contractVariablesFiltered = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getContractVariables(Consumer<List<ContractVariable>> success, Consumer<Throwable> failure) {
		employeesService.getContractVariables(this.contractId, new AsyncCallback<List<ContractVariable>>() {
			@Override
			public void onSuccess(List<ContractVariable> result) {
				contractVariables = result;
				success.accept(contractVariables);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void createContractVariable(ContractVariable contractVariable, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.createContractVariable(this.contractId, contractVariable, new AsyncCallback<Void>() {
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
	
	public void updateContractVariables(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.updateContractVariables(this.contractVariables, new AsyncCallback<Void>() {
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

	public List<ContractVariable> getContractVariables(String yearValue, String monthValue, String variableTypeValue) {
		this.contractVariablesFiltered.clear();
		
		// Se filtra solo por tipo de variable sin fechas
		if(AonStringUtils.isBlank(yearValue)) {
			VariableType variableType = getVariableType(variableTypeValue);
			
			for(ContractVariable contractVariable : contractVariables) {
				if(contractVariable.getId() < 0)
					continue;
				
				if(null == variableType || variableType.equals(contractVariable.getVariableType()))
					this.contractVariablesFiltered.add(contractVariable);
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
			
			VariableType variableType = getVariableType(variableTypeValue);
			
			for(ContractVariable contractVariable : contractVariables) {
				if(contractVariable.getId() < 0)
					continue;
				
				if(	(isInPeriod(startDate, endDate, contractVariable.getStartDate(), contractVariable.getEndDate()) ||
					(null != contractVariable.getEndDate() && isInPeriod(startDate, endDate, contractVariable.getEndDate(), contractVariable.getEndDate())) ||
					(null == contractVariable.getEndDate() && (isInPeriod(startDate, endDate, contractVariable.getStartDate(), contractVariable.getEndDate()) || DateUtils.isBeforeOrEquals(contractVariable.getStartDate(), startDate))))
					&& (null == variableType || variableType.equals(contractVariable.getVariableType())))
					
					this.contractVariablesFiltered.add(contractVariable);
			}
		}
		
		this.contractVariablesFiltered.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
		Collections.reverse(this.contractVariablesFiltered);
		
		return this.contractVariablesFiltered;
	}

	private VariableType getVariableType(String variableTypeValue) {
		switch (variableTypeValue) {
		case "0":
			return VariableType.CONTRACT_DATA;
		case "1":
			return VariableType.CONTRACT_INFO;
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

	public void deleteContractVariable(ContractVariable contractVariableDelete) {
		for(ContractVariable contractVariable : this.contractVariables){
			if(contractVariable.getId().equals(contractVariableDelete.getId())) {
				Integer id = contractVariable.getId();
				contractVariable.setId(id * -1);
			}
		}
	}
	
	// ----------------------------------------------- Getter
	
	public Date getContractStartDate() {
		return this.contractStartDate;
	}
	
	public Date getContractEndDate() {
		return this.contractEndDate;
	}
	
}

