package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataITObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<ITEmployee> allEmployeesList;
	private List<ITEmployee> employeesList;
	
	private Map<String, Integer> employeesFilterMap;
	
	public MainContrataITObject() {
		super();
		this.allEmployeesList = new ArrayList<ITEmployee>();
		this.employeesList = new ArrayList<ITEmployee>();
		this.employeesFilterMap = new HashMap<String, Integer>();
	}
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesITInfo(allEmployees, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				initEmployeeList(employeesInfoList);	
				success.accept(employeesInfoList);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void deleteIT(IT it, Consumer<String> success, Consumer<Throwable> failure) {
		impl.deleteIT(it.getId(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(message);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}
	
	public void createUpdateITEmployee(ITEmployee employeeITInfo, Consumer<String> success, Consumer<Throwable> failure) {
		impl.createUpdateITEmployee(employeeITInfo, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(message);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}
	
	private void initEmployeeList(List<ITEmployee> employeesInfoList) {
		allEmployeesList.clear();
		employeesList.clear();
		allEmployeesList.addAll(employeesInfoList);
		employeesList.addAll(employeesInfoList);
		
		// Init map
		for(ITEmployee employee : allEmployeesList) {
			String fullName = employee.getEmployeeInfo().getFullName();
			String document = employee.getEmployeeInfo().getDocument();
			String ssNumber = employee.getEmployeeInfo().getSsNumber();
			Integer contractId = employee.getContractInfo().getContractId();
			
			employeesFilterMap.put(fullName + ", Documento : " + document + ", SS : " + ssNumber, contractId);
		}
	}
	
	public List<ITEmployee> getEmployeesList(){
		return employeesList;
	}
	
	public Map<String, Integer> getEmployeesMap(){
		return employeesFilterMap;
	}

	public void resetEmployeesList() {
		this.employeesList.clear();
		this.employeesList.addAll(allEmployeesList);
	}

	public List<Integer> getEmployeesContractIds(String value) {
		List<Integer> contractIds = new ArrayList<Integer>();
		
		for(Entry<String, Integer> entry : employeesFilterMap.entrySet()) {
			if(StringUtils.containsIgnoreCase(entry.getKey(), value) ||
			   StringUtils.contains(entry.getKey(), value) ||
			   StringUtils.equals(entry.getKey(), value) ||
			   StringUtils.equalsIgnoreCase(entry.getKey(), value)) {
				
				contractIds.add(entry.getValue());
			}
		}
		
		return contractIds;
	}

	public void filterEmployeesList(List<Integer> employeesContractIds) {
		employeesList.clear();
		
		for(ITEmployee employee : allEmployeesList) {
			if(employeesContractIds.contains(employee.getContractInfo().getContractId()))
				employeesList.add(employee);
		}
	}
	
		
}
