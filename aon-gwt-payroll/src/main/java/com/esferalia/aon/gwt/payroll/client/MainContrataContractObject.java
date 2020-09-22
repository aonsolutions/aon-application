package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataContractObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<EmployeeContractInfo> allEmployeesList;
	private List<EmployeeContractInfo> employeesList;
	
	private Map<String, Integer> employeesFilterMap;
	
	public MainContrataContractObject() {
		super();
		this.allEmployeesList = new ArrayList<EmployeeContractInfo>();
		this.employeesList = new ArrayList<EmployeeContractInfo>();
		this.employeesFilterMap = new HashMap<String, Integer>();
	}
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<EmployeeContractInfo>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesInfo(allEmployees, new AsyncCallback<List<EmployeeContractInfo>>() {
			
			@Override
			public void onSuccess(List<EmployeeContractInfo> employeesInfoList) {
				initEmployeeList(employeesInfoList);	
				success.accept(employeesInfoList);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	private void initEmployeeList(List<EmployeeContractInfo> employeesInfoList) {
		allEmployeesList.clear();
		employeesList.clear();
		allEmployeesList.addAll(employeesInfoList);
		employeesList.addAll(employeesInfoList);
		
		// Init map
		for(EmployeeContractInfo employee : allEmployeesList) {
			String fullName = employee.getEmployeeInfo().getFullName();
			String document = employee.getEmployeeInfo().getDocument();
			String ssNumber = employee.getEmployeeInfo().getSsNumber();
			Integer contractId = employee.getContractInfo().getContractId();
			
			employeesFilterMap.put(fullName + ", Documento : " + document + ", SS : " + ssNumber, contractId);
		}
	}
	
	public List<EmployeeContractInfo> getEmployeesList(){
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
		
		for(EmployeeContractInfo employee : allEmployeesList) {
			if(employeesContractIds.contains(employee.getContractInfo().getContractId()))
				employeesList.add(employee);
		}
	}
		
}
