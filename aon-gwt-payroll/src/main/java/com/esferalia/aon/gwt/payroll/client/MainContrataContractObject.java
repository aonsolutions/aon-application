package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseContext;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataContractObject {
	
	// ------------------------------------------ Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private List<EmployeeContractInfo> allEmployeesList;
	private List<EmployeeContractInfo> employeesList;
	private List<EmployeeContractInfo> trashEmployeesList;
	
	private boolean hasCertificateSEPE;
	
	private EnterpriseContext enterpriseContext;
	
	private DomainUserRoles domainUserRoles;
	
	// ------------------------------------------ Constructor
	
	public MainContrataContractObject() {
		super();
		this.allEmployeesList = new ArrayList<>();
		this.employeesList = new ArrayList<>();
		this.trashEmployeesList = new ArrayList<>();
		this.hasCertificateSEPE = false;
		this.enterpriseContext = new EnterpriseContext();
	}
	
	// ------------------------------------------ DataBase Methods
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<EmployeeContractInfo>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesInfo(allEmployees, new AsyncCallback<List<EmployeeContractInfo>>() {
			
			@Override
			public void onSuccess(List<EmployeeContractInfo> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				
				impl.hasCertificateSEPE(new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
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
				failure.accept(caught);
			}
		});
		
	}
	
	public void getContextInfo(Consumer<EnterpriseContext> success, Consumer<Throwable> failure){
		impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles domainUserRolesDB) {
				domainUserRoles = domainUserRolesDB;
				
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
			
			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
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
	
	public void getUpdateCert(String regime, String ccc, Consumer<String> success, Consumer<Throwable> failure) {
		impl.getUpdateCert(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getCCCLaboralLife(String regime, String ccc, Date from, Date to, Consumer<String> success, Consumer<Throwable> failure) {
		impl.getCCCLaboralLife(regime, ccc, from, to, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				success.accept(dataURI);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

	// ------------------------------------------ Initialize Methods
	
	private void initEmployeeList(List<EmployeeContractInfo> employeesInfoList) {
		allEmployeesList.clear();
		employeesList.clear();
		allEmployeesList.addAll(employeesInfoList);
		employeesList.addAll(employeesInfoList);
	}
	
	// ------------------------------------------ Getters Methods
	
	public List<EmployeeContractInfo> getEmployeesList(){
		employeesList.sort((e1, e2) -> e1.getEmployeeInfo().getFullName().compareTo(e2.getEmployeeInfo().getFullName()));
		return employeesList;
	}
	
	public List<EmployeeContractInfo> getAllEmployeesList(){
		return allEmployeesList;
	}

	public void resetEmployeesList() {
		this.employeesList.clear();
		this.employeesList.addAll(allEmployeesList);
	}
	
	public void filterEmployeesList(Integer workplaceId) {
		this.employeesList.clear();
		
		for(EmployeeContractInfo employee : allEmployeesList) {
			Integer employeeWorkplaceId = employee.getContractInfo().getWorkplaceId();
			if(null != employeeWorkplaceId && AonNumberUtils.equals(workplaceId, employeeWorkplaceId))
				this.employeesList.add(employee);
		}
	}
	
	public void filterEmployeesList(String pattern) {
		this.employeesList.clear();
		
		for(EmployeeContractInfo employee : allEmployeesList)
			if(isEmployeeByPattern(employee, pattern))
				this.employeesList.add(employee);
			
	}

	private boolean isEmployeeByPattern(EmployeeContractInfo employee, String pattern) {
		String fullName = employee.getEmployeeInfo().getFullName();
		String document = employee.getEmployeeInfo().getDocument();
		String ssNumber = employee.getEmployeeInfo().getSsNumber();
		
		return AonStringUtils.containsIgnoreCase(fullName, pattern) ||
				(AonStringUtils.isNotBlank(document) && AonStringUtils.containsIgnoreCase(document, pattern)) ||
				(AonStringUtils.isNotBlank(ssNumber) && AonStringUtils.containsIgnoreCase(ssNumber, pattern));
	}
	
	public List<Integer> getEmployeesContractIdsByWorkplace(String workplaceIdStr) {
		List<Integer> contractIds = new ArrayList<>();
		Integer workplaceId = Integer.parseInt(workplaceIdStr);
		
		for(EmployeeContractInfo employee : allEmployeesList) {
			if(employee.getContractInfo().getWorkplaceId().equals(workplaceId))
				contractIds.add(employee.getContractInfo().getContractId());
		}
		
		return contractIds;
	}
	
	// ------------------------------------------ DataBase Methods Trash

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
				failure.accept(caught);
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
				failure.accept(caught);
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
				failure.accept(caught);
			}
		});	
	}
		
	// ------------------------------------------ Getters Methods Trash
	
	public List<EmployeeContractInfo> getTrashEmployeesList() {
		return this.trashEmployeesList;
	}

	public boolean hasCertificateSEPE() {
		return this.hasCertificateSEPE;
	}
	
	// ------------------------------------------ DataBase Methods EnterpriseSalary

	public void getEnterprise(Consumer<com.esferalia.aon.gwt.payroll.shared.Enterprise> success, Consumer<Throwable> failure) {
		employeesService.getEnterprise(new AsyncCallback<com.esferalia.aon.gwt.payroll.shared.Enterprise>() {
			
			@Override
			public void onSuccess(com.esferalia.aon.gwt.payroll.shared.Enterprise enterprise) {
				success.accept(enterprise);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	// ------------------------------------------ Auxiliar Methods
	
	public EnterpriseContext getEnterpriseContext() {
		return enterpriseContext;
	}

	public DomainUserRoles getDomainUserRoles() {
		return this.domainUserRoles;
	}

	public boolean isComunica() {
		return this.domainUserRoles.isComunica();
	}

	public boolean hasPayroll() {
		return this.domainUserRoles.isPayrollManager();
	}

	public Pair<String, String> getPrincipalAccount() {
		Optional<CCCInfo> principalAccount = enterpriseContext.getActivitiesCCC().getCccs().values().stream().filter(ccc -> ccc.getType() == (byte)0).findFirst();
		if(principalAccount.isPresent())
			return new Pair<>(getCCCRegimeCode(principalAccount.get().getType()), principalAccount.get().getCcc());
		else if(!enterpriseContext.getActivitiesCCC().getCccs().isEmpty()){
			CCCInfo ccc = enterpriseContext.getActivitiesCCC().getCccs().get(0);
			return new Pair<>(getCCCRegimeCode(ccc.getType()), ccc.getCcc());
		} else return null;
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}
}
		
