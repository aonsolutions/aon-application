package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataITObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private List<ITEmployee> allEmployeesList;
	private List<ITEmployee> employeesList;
	private List<IT> allITsList;
	private List<IT> itsList;
	
	private Map<String, Integer> employeesFilterMap;
	private Map<String, Integer> itsFilterMap;
	
	public MainContrataITObject() {
		super();
		this.allEmployeesList = new ArrayList<ITEmployee>();
		this.employeesList = new ArrayList<ITEmployee>();
		this.employeesFilterMap = new HashMap<String, Integer>();
		this.allITsList = new ArrayList<IT>();
		this.itsList = new ArrayList<IT>();
		this.itsFilterMap = new HashMap<String, Integer>();
	}
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesITInfo(allEmployees, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				initITList(employeesInfoList);
				success.accept(employeesInfoList);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void getEmployeesInfo(Integer itIds [], Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesITInfo(itIds, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				initITList(employeesInfoList);
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
	
	public void setEmployeesInfo(List<ITEmployee> employeesInfoList, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
		initEmployeeList(employeesInfoList);
		initITList(employeesInfoList);
		success.accept(employeesInfoList);	
	}
	
	private void initEmployeeList(List<ITEmployee> employeesInfoList) {
		allEmployeesList.clear();
		employeesList.clear();
		allEmployeesList.addAll(employeesInfoList);
		employeesList.addAll(employeesInfoList);
		
		employeesFilterMap.clear();
		// Init map
		for(ITEmployee employee : allEmployeesList) {
			String fullName = employee.getEmployeeInfo().getFullName();
			String document = employee.getEmployeeInfo().getDocument();
			String ssNumber = employee.getEmployeeInfo().getSsNumber();
			Integer contractId = employee.getContractInfo().getContractId();
			employeesFilterMap.put(fullName + ", Documento : " + document + ", SS : " + ssNumber, contractId);
		}
	}
	
	private void initITList(List<ITEmployee> employeesInfoList) {
		allITsList.clear();
		itsList.clear();
		
		Date currentDate = new Date();
		
		itsFilterMap.clear();
		// Init map
		for(ITEmployee employee : allEmployeesList) {
			String fullName = employee.getEmployeeInfo().getFullName();
			for(IT it : employee.getIts()) {
				it.setFullName(fullName);
				
				allITsList.add(it);
				if(it.getEndDate() == null || it.getEndDate().after(currentDate)) {
					itsList.add(it);
					String description = fullName + (StringUtils.isBlank(it.getDescription()) ? "" : " " + it.getDescription())
							+ " " + parseShortLowCauseByte(it.getTypeLowPart()) + " (" + formatFullDate.format(it.getStartDate()) + ")" ;
					itsFilterMap.put(description, it.getId());
				}
			}
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
	
	public List<IT> getITsList(){
		return itsList;
	}
	
	public Map<String, Integer> getITsMap(){
		return itsFilterMap;
	}

	public void resetITsList() {
		this.itsList.clear();
		
		// Init map
		for(IT it : this.allITsList) {
			this.itsList.add(it);
		}
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
	
	public List<Integer> getITsContractIds(String value) {
		List<Integer> itIds = new ArrayList<Integer>();
		
		for(Entry<String, Integer> entry : itsFilterMap.entrySet()) {
			if(StringUtils.containsIgnoreCase(entry.getKey(), value) ||
			   StringUtils.contains(entry.getKey(), value) ||
			   StringUtils.equals(entry.getKey(), value) ||
			   StringUtils.equalsIgnoreCase(entry.getKey(), value)) {
				
				itIds.add(entry.getValue());
			}
		}
		
		return itIds;
	}

	public void filterITsList(List<Integer> itsContractIds) {
		itsList.clear();
		
		for(IT it : allITsList) {
			if(itsContractIds.contains(it.getId()))
				itsList.add(it);
		}
	}

	public ITEmployee getEmployeeITInfo(Integer itId) {
		for(ITEmployee itEmployee : this.employeesList) {
			for(IT it : itEmployee.getIts())
				if(it.getId() == itId || it.getId().equals(itId))
					return itEmployee;
		}
		
		return null;
	}

	public void setITsList(Boolean allITs) {
		this.itsList.clear();
		
		if(allITs) {
			// Init map
			for(ITEmployee employee : allEmployeesList) {
				for(IT it : employee.getIts()) {
					itsList.add(it);
					allITsList.add(it);
				}
			}
		} else {
			Date currentDate = new Date();
			
			// Init map
			for(ITEmployee employee : allEmployeesList) {
				for(IT it : employee.getIts()) {
					if(it.getEndDate() == null || it.getEndDate().after(currentDate)) {
						itsList.add(it);
						allITsList.add(it);
					}
					
				}
			}
		}
		
	}
	
	private String parseShortLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "ECC";
			case (byte)1:
				return "ATT";
			case (byte)2:
				return "MAT";
			case (byte)3:
				return "PAT";
			case (byte)4:
				return "REM";
			case (byte)5:
				return "RLA";
			case (byte)6:
				return "ANL";
			case (byte)7:
				return "ECC";
			case (byte)8:
				return "COV";
			default:
				return "-";
		}
	}
	
		
}
