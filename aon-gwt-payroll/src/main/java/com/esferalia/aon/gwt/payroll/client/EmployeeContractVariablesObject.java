package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Variables.VariablesObject;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractVariablesObject implements VariablesObject<ContractVariable> {
	
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
	
	@Override
	public void getVariables(Consumer<List<ContractVariable>> success, Consumer<Throwable> failure) {
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
	
	@Override
	public void createVariable(ContractVariable contractVariable, Consumer<Void> success, Consumer<Throwable> failure) {
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
	
	@Override
	public void updateVariables(Consumer<Void> success, Consumer<Throwable> failure) {
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

	@Override
	public List<ContractVariable> getVariables(String yearValue, String monthValue, String variableTypeValue) {
		this.contractVariablesFiltered.clear();
		
		// Se filtra solo por tipo de variable sin fechas
		if(AonStringUtils.isBlank(yearValue)) {
			ContractVariableType contractVariableType = getVariableType(variableTypeValue);
			
			for(ContractVariable contractVariable : contractVariables) {
				if(contractVariable.getId() < 0)
					continue;
				
				if(null == contractVariableType || contractVariableType.equals(contractVariable.getVariableType()))
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
			
			ContractVariableType contractVariableType = getVariableType(variableTypeValue);
			
			for(ContractVariable contractVariable : contractVariables) {
				if(contractVariable.getId() < 0)
					continue;
				
				if(	(isInPeriod(startDate, endDate, contractVariable.getStartDate(), contractVariable.getEndDate()) ||
					(null != contractVariable.getEndDate() && isInPeriod(startDate, endDate, contractVariable.getEndDate(), contractVariable.getEndDate())) ||
					(null == contractVariable.getEndDate() && (isInPeriod(startDate, endDate, contractVariable.getStartDate(), contractVariable.getEndDate()) || DateUtils.isBeforeOrEquals(contractVariable.getStartDate(), startDate))))
					&& (null == contractVariableType || contractVariableType.equals(contractVariable.getVariableType())))
					
					this.contractVariablesFiltered.add(contractVariable);
			}
		}
		
		this.contractVariablesFiltered.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
//		Collections.reverse(this.contractVariablesFiltered);
		
		return this.contractVariablesFiltered;
	}

	private ContractVariableType getVariableType(String variableTypeValue) {
		switch (variableTypeValue) {
		case "0":
			return ContractVariableType.CONTRACT_DATA;
		case "1":
			return ContractVariableType.CONTRACT_INFO;
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
	public void deleteVariable(ContractVariable contractVariableDelete) {
		for(ContractVariable contractVariable : this.contractVariables){
			if(contractVariable.getId().equals(contractVariableDelete.getId())) {
				Integer id = contractVariable.getId();
				contractVariable.setId(id * -1);
			}
		}
	}
	
	// ----------------------------------------------- Getter
	
	@Override
	public Date getStartDate() {
		return this.contractStartDate;
	}
	
	@Override
	public Date getEndDate() {
		return this.contractEndDate;
	}
	
}

