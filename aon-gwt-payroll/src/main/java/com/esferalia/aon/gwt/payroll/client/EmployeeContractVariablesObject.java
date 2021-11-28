package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable.VariableType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractVariablesObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private List<ContractVariable> contractVariables;
	private List<ContractVariable> contractVariablesFiltered;
	
	private Integer contractId;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractVariablesObject(Integer contractId) {
		this.contractId = contractId;
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

	public List<ContractVariable> getContractVariables(Integer year, String variableTypeValue) {
		this.contractVariablesFiltered.clear();
		
		Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
		Date endDate = DateUtils.getLastDayOfYear(year - 1900);
		
		VariableType variableType = getVariableType(variableTypeValue);
		
		for(ContractVariable contractVariable : contractVariables) {
			if(contractVariable.getId() < 0)
				continue;
			
			if(	(isInPeriod(startDate, endDate, contractVariable.getStartDate()) ||
				(null != contractVariable.getEndDate() && isInPeriod(startDate, endDate, contractVariable.getEndDate())) ||
				(null == contractVariable.getEndDate() && (isInPeriod(startDate, endDate, contractVariable.getStartDate()) || DateUtils.isBeforeOrEquals(contractVariable.getStartDate(), startDate))))
				&& (null == variableType || variableType.equals(contractVariable.getVariableType())))
				
				this.contractVariablesFiltered.add(contractVariable);
		}
		
		this.contractVariablesFiltered.sort((o1, o2) -> compareString(o1, o2, getContractVariableTypeShort(o1.getVariableType()), getContractVariableTypeShort(o2.getVariableType())));	
		
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

	private boolean isInPeriod(Date start, Date end, Date date) {
		return null == date || (DateUtils.isAfterOrEquals(date, start) && DateUtils.isBeforeOrEquals(date, end));
	}

	public void deleteContractVariable(ContractVariable contractVariableDelete) {
		for(ContractVariable contractVariable : this.contractVariables){
			if(contractVariable.getId().equals(contractVariableDelete.getId())) {
				Integer id = contractVariable.getId();
				contractVariable.setId(id * -1);
			}
		}
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s2.compareTo(s1);
	}
	
	private String getContractVariableTypeShort(VariableType variableType) {
		switch (variableType) {
			case CONTRACT_DATA:
				return "D";
			case CONTRACT_INFO:
				return "I";
			default:
				return "N/D";
		}
	}
	
}

