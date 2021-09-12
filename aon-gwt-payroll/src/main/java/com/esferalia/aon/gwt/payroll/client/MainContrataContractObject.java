package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataContractObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private List<EmployeeContractInfo> allEmployeesList;
	private List<EmployeeContractInfo> employeesList;
	private List<EmployeeContractInfo> trashEmployeesList;
	
	private Map<String, Integer> employeesFilterMap;
	
	private List<Workplace> workplaces;
	
	private boolean hasCertificateSEPE;
	
	private List<Agreement> agreementsContext;
	private List<Workplace> workplacesContext;
	private ActivitiesCCC activitiesCCCContex;
	private Map<String, String> payMethodsMapContext;
	
	public MainContrataContractObject() {
		super();
		this.allEmployeesList = new ArrayList<EmployeeContractInfo>();
		this.employeesList = new ArrayList<EmployeeContractInfo>();
		this.trashEmployeesList = new ArrayList<EmployeeContractInfo>();
		this.employeesFilterMap = new HashMap<String, Integer>();
		this.workplaces = new ArrayList<Workplace>();
		this.hasCertificateSEPE = false;
	}
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<EmployeeContractInfo>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesInfo(allEmployees, new AsyncCallback<List<EmployeeContractInfo>>() {
			
			@Override
			public void onSuccess(List<EmployeeContractInfo> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				impl.getWorkplaces(new AsyncCallback<List<Workplace>>() {
					
					@Override
					public void onSuccess(List<Workplace> dbWorkplaces) {
						workplacesContext = dbWorkplaces;
						workplaces.clear();
						workplaces.addAll(dbWorkplaces);
						impl.hasCertificateSEPE(new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								// Failure
							}

							@Override
							public void onSuccess(Boolean result) {
								hasCertificateSEPE = result.booleanValue();
								success.accept(employeesInfoList);	
							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Failure
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}
		});
		
	}
	
	public void getContextInfo(){
		impl.getAgreements(0, Integer.MAX_VALUE, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> dbAgreements) {
				agreementsContext = dbAgreements;
				impl.getActivityCCC(new AsyncCallback<ActivitiesCCC>() {

					@Override
					public void onFailure(Throwable caught) {
						// Failure
					}

					@Override
					public void onSuccess(ActivitiesCCC dbActivitiesCCC) {
						activitiesCCCContex = dbActivitiesCCC;
						impl.getPayMethods(new AsyncCallback<Map<String, String>>() {

							@Override
							public void onFailure(Throwable caught) {
								// Failure
							}

							@Override
							public void onSuccess(Map<String, String> dbPayMethods) {
								payMethodsMapContext = dbPayMethods;
							}
						});
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}
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
		employeesList.sort((e1, e2) -> e1.getEmployeeInfo().getFullName().compareTo(e2.getEmployeeInfo().getFullName()));
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
			if( AonStringUtils.containsIgnoreCase(entry.getKey(), value) ||
				AonStringUtils.contains(entry.getKey(), value) ||
				AonStringUtils.equals(entry.getKey(), value) ||
				AonStringUtils.equalsIgnoreCase(entry.getKey(), value)) {
				
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
			public void onFailure(Throwable caught) {
				// Failure
			}
		});	
	}

	public void restoreContract(Integer contractId, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.restoreContract(contractId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void resutl) {
				success.accept(resutl);	
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}
		});	
	}


	public void delete4EverContract(Integer contractId, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.delete4EverContract(contractId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void resutl) {
				success.accept(resutl);	
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}
		});	
	}
	
	public boolean hasCertificateSEPE() {
		return this.hasCertificateSEPE;
	}
	
	// ------------------------------------------------------------------------------------
	//									ENTERPRISE SALARY
	// ------------------------------------------------------------------------------------

	public void getEnterprise(Consumer<com.esferalia.aon.gwt.payroll.shared.Enterprise> success, Consumer<Throwable> failure) {
		employeesService.getEnterprise(new AsyncCallback<com.esferalia.aon.gwt.payroll.shared.Enterprise>() {
			
			@Override
			public void onSuccess(com.esferalia.aon.gwt.payroll.shared.Enterprise enterprise) {
				success.accept(enterprise);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}
		});
	}

	public List<Agreement> getAgreementsContext() {
		return agreementsContext;
	}

	public void setAgreementsContext(List<Agreement> agreementsContext) {
		this.agreementsContext = agreementsContext;
	}

	public List<Workplace> getWorkplacesContext() {
		return workplacesContext;
	}

	public ActivitiesCCC getActivitiesCCCContex() {
		return activitiesCCCContex;
	}

	public Map<String, String> getPayMethodsMapContext() {
		return payMethodsMapContext;
	}

}
		
