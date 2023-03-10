package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseContext;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainMassiveContractsObject {
	
	// -------------------------------------------- Variables
	
	private final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<EmployeeContractInfo> allEmployeesList;
	private List<EmployeeContractInfo> employees;
	private List<ContractData> contractDatas;
	private Map<String, CNO> cnos;
	private Integer domainId;
	private boolean hasValue = true;
	
	private EnterpriseContext enterpriseContext;
	
	// -------------------------------------------- Constructor
	
	public MainMassiveContractsObject() {
		super();
		allEmployeesList = new ArrayList<>();
		employees = new ArrayList<>();
		cnos = new HashMap<>();
	}
	
	// -------------------------------------------- Database Methods
	
	public void getContextInfo(Consumer<EnterpriseContext> success, Consumer<Throwable> failure){
		impl.getEnterpriseContext(new AsyncCallback<EnterpriseContext>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EnterpriseContext enterpriseContextDB) {
				enterpriseContext = enterpriseContextDB;
				success.accept(enterpriseContext);
			}}
		);
	}
	
	public void getEmployees(String dataType, boolean allEmployees, Consumer<List<EmployeeContractInfo>> success, Consumer<Throwable> failure) {
		impl.getEmployeesInfo(allEmployees, new AsyncCallback<List<EmployeeContractInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<EmployeeContractInfo> employeesDB) {
				initEmployeeList(employeesDB);
				employees.sort((o1, o2) -> o1.getEmployeeInfo().getFullName().compareTo(o2.getEmployeeInfo().getFullName()));
				
				impl.getDomain(new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer domainIdDB) {
						domainId = domainIdDB;
						
						impl.getCNOs(new AsyncCallback<Map<String,CNO>>() {
							
							@Override
							public void onSuccess(Map<String, CNO> cnosDB) {
								cnos = cnosDB;
								
								switch (dataType) {
									case "CNO":
										getContractsCNO(s -> success.accept(employeesDB), f -> failure.accept(f));
										break;
									default:
										success.accept(null);
										break;
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								failure.accept(caught);
							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
		});
		
	}
	
	public void updateContractData(String dataType, Consumer<Void> success, Consumer<Throwable> failure) {
		switch (dataType) {
			case "CNO":
				updateContractsCNO(s -> success.accept(s), f -> failure.accept(f));
				break;
			default:
				success.accept(null);
				break;
		}
	}
	
	public void getContractsCNO(Consumer<List<ContractData>> success, Consumer<Throwable> failure) {
		impl.getMassiveCNOs(new AsyncCallback<List<ContractData>>() {
			
			@Override
			public void onSuccess(List<ContractData> contractDatasDB) {
				contractDatas = contractDatasDB;
				success.accept(contractDatasDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void updateContractsCNO(Consumer<Void> success, Consumer<Throwable> failure) {
		impl.updateMassiveCNOs(contractDatas, new AsyncCallback<Void>() {
			
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
	
	// -------------------------------------------- Getters
	
	private void initEmployeeList(List<EmployeeContractInfo> employeesInfoList) {
		allEmployeesList.clear();
		employees.clear();
		allEmployeesList.addAll(employeesInfoList);
		employees.addAll(employeesInfoList);
	}
	
	public void resetEmployeesList() {
		this.employees.clear();
		this.employees.addAll(allEmployeesList);
	}
	
	public void filterEmployeesList(Integer workplaceId) {
		this.employees.clear();
		
		for(EmployeeContractInfo employee : allEmployeesList) {
			Integer employeeWorkplaceId = employee.getContractInfo().getWorkplaceId();
			if(null != employeeWorkplaceId && AonNumberUtils.equals(workplaceId, employeeWorkplaceId))
				this.employees.add(employee);
		}
		
		filterHasValue();
	}
	
	public void filterEmployeesList(String pattern) {
		this.employees.clear();
		
		for(EmployeeContractInfo employee : allEmployeesList)
			if(isEmployeeByPattern(employee, pattern))
				this.employees.add(employee);
	
		filterHasValue();		
	}

	private boolean isEmployeeByPattern(EmployeeContractInfo employee, String pattern) {
		String fullName = employee.getEmployeeInfo().getFullName();
		String document = employee.getEmployeeInfo().getDocument();
		String ssNumber = employee.getEmployeeInfo().getSsNumber();
		
		return AonStringUtils.containsIgnoreCase(fullName, pattern) ||
				(AonStringUtils.isNotBlank(document) && AonStringUtils.containsIgnoreCase(document, pattern)) ||
				(AonStringUtils.isNotBlank(ssNumber) && AonStringUtils.containsIgnoreCase(ssNumber, pattern));
	}
	
	public EnterpriseContext getEnterpriseContext() {
		return enterpriseContext;
	}

	public List<EmployeeContractInfo> getEmployees() {
		return this.employees;
	}
	
	public Map<String, CNO> getCNOs() {
		return this.cnos;
	}
	
	public Integer getDomainId() {
		return this.domainId;
	}

	public String getCNODescription(String cnoCode) {
		CNO cno = this.cnos.get(cnoCode);
		return cno == null ? "" : cno.getCode() + " - " + cno.getTitle();
	}
	
	public Optional<ContractData> getContractData(Integer contractId, String name) {
		Optional<ContractData> contractData = this.contractDatas.stream().filter(contractDataIt -> AonStringUtils.equals(contractDataIt.getName(), name) && contractDataIt.getContract().equals(contractId)).reduce((first, second) -> second);
		return contractData;
	}
	
	// -------------------------------------------- Setters

	public void updateContractCNO(EmployeeContractInfo employee, String cnoCode) {
		Optional<ContractData> contractData = this.contractDatas.stream().filter(contractDataIt -> AonStringUtils.equals(contractDataIt.getName(), "CNO") && contractDataIt.getContract().equals(employee.getContractInfo().getContractId())).reduce((first, second) -> second);
		if(contractData.isPresent()) {
			contractData.get().setExpression("\"" + cnoCode + "\"");
			contractData.get().setModify(true);
			employee.setModify(true);
		} else {
			this.contractDatas.add(new ContractData()
					.setDomain(employee.getEmployeeInfo().getDomain())
					.setContract(employee.getContractInfo().getContractId())
					.setName("CNO")
					.setExpression("\"" + cnoCode + "\"")
					.setStartDate(employee.getContractInfo().getStartDate())
					.setEndDate(employee.getContractInfo().getEndDate())
					.setModify(true)
			);
			employee.setModify(true);
		}
	}

	public void filterHasValue() {
		if(!hasValue) {
			List<EmployeeContractInfo> iterableEmployees = new ArrayList<>(employees);
			this.employees.clear();
			for(EmployeeContractInfo employee : iterableEmployees) {
				Optional<ContractData> contractData = getContractData(employee.getContractInfo().getContractId(), "CNO");
				if(!contractData.isPresent() || AonStringUtils.isBlank(contractData.get().getExpression())) employees.add(employee);
			}
		}
	}

	public void setHasValue(Boolean value) {
		this.hasValue = value;
	}
		
}
