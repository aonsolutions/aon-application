package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataContractObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<EmployeeContractInfo> allEmployeesList;
	private List<EmployeeContractInfo> employeesList;
	private List<EmployeeContractInfo> trashEmployeesList;
	
	private Map<String, Integer> employeesFilterMap;
	
	private List<Workplace> workplaces;
	
	public MainContrataContractObject() {
		super();
		this.allEmployeesList = new ArrayList<EmployeeContractInfo>();
		this.employeesList = new ArrayList<EmployeeContractInfo>();
		this.trashEmployeesList = new ArrayList<EmployeeContractInfo>();
		this.employeesFilterMap = new HashMap<String, Integer>();
		this.workplaces = new ArrayList<Workplace>();
	}
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<EmployeeContractInfo>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesInfo(allEmployees, new AsyncCallback<List<EmployeeContractInfo>>() {
			
			@Override
			public void onSuccess(List<EmployeeContractInfo> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				impl.getWorkplaces(null, new AsyncCallback<List<Workplace>>() {
					
					@Override
					public void onSuccess(List<Workplace> result) {
						workplaces.clear();
						workplaces.addAll(result);
						success.accept(employeesInfoList);	
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void getEmployeeInfo(Integer contractId, Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
		
		impl.getEmployeeInfo(contractId, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo employeeContractInfo) {
				success.accept(employeeContractInfo);
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void checkStatus(Consumer<EnterpriseStatus> success, Consumer<Throwable> failure) {
		
		impl.getEnterpriseStatus(null, new AsyncCallback<EnterpriseStatus>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept( caught );
			}
			
			 @Override
			public void onSuccess(EnterpriseStatus result) {
				 success.accept(result);
			}
		});
	}

	private void initEmployeeList(List<EmployeeContractInfo> employeesInfoList) {
		allEmployeesList.clear();
		employeesList.clear();
		allEmployeesList.addAll(employeesInfoList);
		employeesList.addAll(employeesInfoList);
		employeesFilterMap.clear();
		
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
		employeesList.sort(new Comparator<EmployeeContractInfo>() {
			@Override
			public int compare(EmployeeContractInfo e1, EmployeeContractInfo e2) {
				return e1.getEmployeeInfo().getFullName().compareTo(e2.getEmployeeInfo().getFullName());
			}
		});
		
		return employeesList;
	}
	
	public List<Workplace> getWorkplaces(){
		return this.workplaces;
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
	
	public List<Integer> getEmployeesContractIdsByWorkplace(String workplaceIdStr) {
		List<Integer> contractIds = new ArrayList<Integer>();
		Integer workplaceId = Integer.parseInt(workplaceIdStr);
		
		for(EmployeeContractInfo employee : allEmployeesList) {
			if(employee.getContractInfo().getWorkplaceId() == workplaceId || employee.getContractInfo().getWorkplaceId().equals(workplaceId))
				contractIds.add(employee.getContractInfo().getContractId());
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
	
	// ------------------------------------------------------------------------------------
	//									TRASH EMPLOYEES
	// ------------------------------------------------------------------------------------

	public List<EmployeeContractInfo> getTrashEmployeesList() {
		return this.trashEmployeesList;
	}

	public void getTrashEmployeesInfo(Consumer<List<EmployeeContractInfo>> success, Consumer<Throwable> failure) {
		impl.getTrashEmployeesInfo(new AsyncCallback<List<EmployeeContractInfo>>() {
			
			@Override
			public void onSuccess(List<EmployeeContractInfo> employeesInfoList) {
				trashEmployeesList.clear();
				trashEmployeesList.addAll(employeesInfoList);
				success.accept(employeesInfoList);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});	
	}

	public void restoreContract(Integer contractId, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.restoreContract(contractId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void resutl) {
				success.accept(resutl);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});	
	}


	public void delete4EverContract(Integer contractId, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.delete4EverContract(contractId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void resutl) {
				success.accept(resutl);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});	
	}

}
		
