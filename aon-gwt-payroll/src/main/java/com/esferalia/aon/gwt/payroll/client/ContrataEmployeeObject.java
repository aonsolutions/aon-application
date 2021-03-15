package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ContrataEmployeeObject {
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	private Workplace workplace;
	
	private EmployeeContractInfo employeeContractData;
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private WorkplaceEmployees workplaceEmployees;
	
	private List<Agreement> agreements;
	private List<Workplace> workplaces;
	private ActivitiesCCC activitiesCCC;
	
	private Map<String, String> payMethodsMap;
	
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------
	
	public ContrataEmployeeObject() {
		super();
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		
		this.workplaces = new ArrayList<>();
		this.payMethodsMap = new HashMap<String, String>();
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void getWorkplaceEmployees(Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure) {
		employeesService.getWorkplaceEmployees(workplace, new AsyncCallback<WorkplaceEmployees>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				workplaceEmployees = result;
				getAgreements(
						r ->{
							success.accept(result);
						}, f->{}
				);
			}
		});
	}
	
	public void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, Integer.MAX_VALUE, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = result;
				getWorkplaces(
					r->{
						success.accept(result);
					}, f->{}
				);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void getWorkplaces(Consumer<List<Workplace>> success, Consumer<Throwable> failure) {
		enterprisesService.getWorkplaces(workplace, new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> result) {
				workplaces = result;
				getActivitiesCCC(
					r->{
						success.accept(result);
					}, f->{}
				);	
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void getActivitiesCCC(Consumer<ActivitiesCCC> success, Consumer<Throwable> failure) {
		enterprisesService.getActivitiesCCC(workplace, new AsyncCallback<ActivitiesCCC>() {
			
			@Override
			public void onSuccess(ActivitiesCCC result) {
				activitiesCCC = result;
				getPayMethods(
						r->{
							success.accept(result);
						}, f->{}
					);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void getPayMethods(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getPayMethods(new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				payMethodsMap = result;
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void initializeEmployee(Integer contractId, Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(contractId, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				
				// Set default contract start_date & end_date to null
				contractData.setStartDate(null);
				contractData.setEndDate(null);
				
				Map<java.util.Date, ArrayList<JourneyDuration>> journies = new HashMap<>();
				contractData.setContractJourneyDuration(journies);
				
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void getAgreement(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {	
		enterprisesService.getAgreement(agreementId, new AsyncCallback<Agreement>() {
			
			@Override
			public void onSuccess(Agreement result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});	
	}
	
	public void getEmployeeContract(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
		employeeContractData.setEmployeeInfo(employeeData);
		employeeContractData.setContractInfo(contractData);
		
		employeesService.getEmployeeInfoDataBase(contractData.getContractId(), new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				employeeContractData.setEmployeeInfo(employeeData);
				employeeContractData.setContractInfo(contractData);
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void setEmployeeContract(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
		employeeContractData.setEmployeeInfo(employeeData);
		employeeContractData.setContractInfo(contractData);
		
		employeesService.setEmployeeInfoDataBase(this.employeeContractData, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				employeeContractData.setEmployeeInfo(employeeData);
				employeeContractData.setContractInfo(contractData);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void getContractAttachments(Consumer<List<ContractAttach>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		enterprisesService.getContractAttachments(contractId, new AsyncCallback<List<ContractAttach>>() {
			
			@Override
			public void onSuccess(List<ContractAttach> contractAttachments) {
				employeeContractData.setContractAttachments(contractAttachments);
				success.accept(contractAttachments);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void getContractClauses(Consumer<List<ContractClause>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		enterprisesService.getContractClauses(contractId, new AsyncCallback<List<ContractClause>>() {
			
			@Override
			public void onSuccess(List<ContractClause> contractClauses) {
				employeeContractData.setContractClauses(contractClauses);
				success.accept(contractClauses);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void getContractOtherInfo(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		String contractType = employeeContractData.getContractInfo().getContractType();
		enterprisesService.getContractOtherInfo(contractId, contractType, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> contractOtherData) {
				employeeContractData.setContractOtherData(contractOtherData);
				success.accept(contractOtherData);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setContractAttachments(Consumer<List<ContractAttach>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		List<ContractAttach> contractAttachments = employeeContractData.getContractAttachments();
		enterprisesService.setContractAttachments(contractId, contractAttachments, new AsyncCallback<List<ContractAttach>>() {
			
			@Override
			public void onSuccess(List<ContractAttach> contractAttachments) {
				success.accept(contractAttachments);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setContractClauses(Consumer<List<ContractClause>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		List<ContractClause> contractClauses = employeeContractData.getContractClauses();
		enterprisesService.setContractClauses(contractId, contractClauses, new AsyncCallback<List<ContractClause>>() {
			
			@Override
			public void onSuccess(List<ContractClause> contractClauses) {
				success.accept(contractClauses);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setContractOtherInfo(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		String contractType = employeeContractData.getContractInfo().getContractType();
		Map<String, String> contractOtherData = getContractEmployeeInfo().getContractOtherData();
		enterprisesService.setContractOtherInfo(contractId, contractType, contractOtherData, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> contractOtherData) {
				success.accept(contractOtherData);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void getContractSpecificData(Consumer<ContractSpecificData> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		enterprisesService.getContractSpecificData(contractId, new AsyncCallback<ContractSpecificData>() {
			
			@Override
			public void onSuccess(ContractSpecificData result) {
				employeeContractData.setContractSpecificData(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setContractSpecificData(Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.setContractSpecificData(employeeContractData, new AsyncCallback<Void>() {
			
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
	
	public void getContractBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		enterprisesService.getContractBonus(contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> result) {
				employeeContractData.setContractBonus(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setContractBonus(Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.setContractBonus(employeeContractData, new AsyncCallback<Void>() {
			
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
	
	public void deleteContract(Consumer<Void> success, Consumer<Throwable> failure) {
		Employee employeeAux = new Employee();
		employeeAux.setId(getContractData().getContractId());
		if(getContractData().hasPayroll()) {
			employeesService.moveContractId(employeeAux, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}

				@Override
				public void onSuccess(Void result) {
					success.accept(result);
				}
			});
		} else {
			enterprisesService.delete4EverContract(getContractData().getContractId(), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					success.accept(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		
	}
	
	public void downloadTa(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeTa(contractData.getContractId(), new Date(), new AsyncCallback<String>() {
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

	public void downloadIdc(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeIdc(contractData.getContractId(), new Date(), new AsyncCallback<String>() {
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
	
	public void downloadCbc(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCbc(employeeData.getDocument(), contractData.getStartDate(), contractData.getStartDate(), new AsyncCallback<String>() {
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
	
	public void downloadCto(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCto(employeeData.getDocument(), contractData.getStartDate(), contractData.getStartDate(), new AsyncCallback<String>() {
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
	
	public void saveContractExport(Consumer<List<ContractAttach>> success, Consumer<Throwable> failure) {
		employeesService.fillContract(contractData.getContractId(), getContractType(), getFormativeLevel(), new AsyncCallback<List<ContractAttach>>() {
			@Override
			public void onSuccess(List<ContractAttach> result) {
				success.accept(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void checkStatus(Consumer<EmployeeStatus> success, Consumer<Throwable> failure) {
		
		employeesService.getEmployeeStatus(contractData.getContractId(), new AsyncCallback<EmployeeStatus>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept( caught );
			}
			
			 @Override
			public void onSuccess(EmployeeStatus result) {
				 success.accept(result);
			}
		});
	}
	
	public void sendEmployeeAlta(Consumer<Void> success, Consumer<Throwable> failure) {
		
		employeesService.sendEmployeeAlta(employeeContractData, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void sendEmployeeBaja(Consumer<Void> success, Consumer<Throwable> failure) {
		
		employeesService.sendEmployeeBaja(employeeContractData, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public void cambioGrupCtz(String quoteGroup, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.getNafxIpf(employeeContractData.getEmployeeInfo().getDocument(), employeeContractData.getEmployeeInfo().getSurName(), 
				employeeContractData.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String ipf = employeeContractData.getEmployeeInfo().getDocument();
						String nss = result.getNss();
						String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
						String ctaCti = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
						
						employeesService.cambioGrupCtz(ipf, regimen, ctaCti, nss, quoteGroup, date, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								success.accept(result);
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
	}
	
	public void cambioOcupacion(String ocupation, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.getNafxIpf(employeeContractData.getEmployeeInfo().getDocument(), employeeContractData.getEmployeeInfo().getSurName(), 
				employeeContractData.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String ipf = employeeContractData.getEmployeeInfo().getDocument();
						String nss = result.getNss();
						String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
						String ctaCti = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
						
						employeesService.cambioOcupacion(ipf, regimen, ctaCti, nss, ocupation, date, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								success.accept(result);
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});		
	}
	
	public void cambioCatProf(String contract, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.getNafxIpf(employeeContractData.getEmployeeInfo().getDocument(), employeeContractData.getEmployeeInfo().getSurName(), 
				employeeContractData.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String ipf = employeeContractData.getEmployeeInfo().getDocument();
						String nss = result.getNss();
						String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
						String ctaCti = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
						
						employeesService.cambioCatProf(ipf, regimen, ctaCti, nss, contract, date, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								success.accept(result);
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});	
	}
	
	public void getContratoSepe(Consumer<String> success, Consumer<Throwable> failure) {
		String ipf = employeeContractData.getEmployeeInfo().getDocument();
		Date startDate = employeeContractData.getContractInfo().getStartDate();
		Date endDate = employeeContractData.getContractInfo().getStartDate();
		
		enterprisesService.getContratoSepe(ipf, startDate, endDate, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String result) {
				success.accept(result);	
			}
			
		});
		
	}
		
	// ---------------------------------------------- GETTERS  -------------------------------------------------
	
	// TABLA DATOS CONTRATO
	
	public EmployeeInfo getEmployeeData() {
		return this.employeeData;
	}
	
	public ContractInfo getContractData() {
		return this.contractData;
	}
	
	public WorkplaceEmployees getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public Map<Integer, String> getActivities() {
		return activitiesCCC.getActivities();
	}
	
	public Map<String, String> getPayMethods() {
		return payMethodsMap;
	}
	
	public Map<Integer, CCCInfo> getCCCs() {
		return activitiesCCC.getCccs();
	}
	
	public List<Workplace> getWorkplaces() {
		return this.workplaces;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() > 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
	}
	
	public Workplace getWorkplaceObj() {
		return this.workplace;
	}
	
	public Integer getWorkplaceId(){
		Integer workplaceId = null;
		if(null == employeeData.getEmployeeId()){
			workplaceId = null == this.workplace ? null : this.workplace.getId();
			contractData.setWorkplaceId(workplaceId);
		}else
			workplaceId = contractData.getWorkplaceId();
		
		return workplaceId;
	}
	
	public Integer getWorkplaceAgreement(){
		Integer agreementId = null;
		if(null == employeeData.getEmployeeId()){
			Activity activity = null == this.workplace ? null : this.workplace.getActivity();
			if(null == activity)
				return -1;
			else {
				agreementId = activity.getId();
				contractData.setAgreementId(agreementId);
			}
		}else
			agreementId = contractData.getAgreementId();
		
		return agreementId;
	}
	
	public EmployeeInfo getEmployeeDataByDocument(String document){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees()){
			if(document == employee.getDocument()) {
				employeeData = employee;
				return employeeData;
			}
		}
		return this.employeeData;
	}
	
	public EmployeeInfo getEmployeeDataBySSNum(String ssNum){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(ssNum == employee.getSsNumber()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String nameSurname){
		String name = nameSurname.split(", ")[0];
		String surname = nameSurname.split(", ")[1];
		
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(name == employee.getName() && surname == employee.getSurName()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
	public Integer getContractSSRegimen() {
		return this.contractData.getSsRegimen() == null ? 0 : (int) this.contractData.getSsRegimen();
	}
		
	public Integer getContractType() {
		if(null == this.contractData.getContractType())
			return -1;
		else
			return Integer.parseInt(this.contractData.getContractType());
	}
	
	public Double getContractPartialityCoef() {
		return this.contractData.getPartialityCoef();
	}
	
	// ---------------------------------------------- SETTERS  -------------------------------------------------
	
	// CONTRACT TABLE
	
	public void setEmployeeDocumentType(String document_type) {
		if(document_type == "DNI")
			employeeData.setDocumentType((byte) 0);
		else if(document_type == "CIF")
			employeeData.setDocumentType((byte) 1);
		else if(document_type == "Pasaporte")
			employeeData.setDocumentType((byte) 3);
	}
	
	public void setEmployeeDocument(String document) {
		employeeData.setDocument(document);
	}
	
	public void setNationality(String nationality) {
		employeeData.setNationality(nationality);	
	}

	public void setEmployeeSocialSecurityNum(String social_security_num) {
		employeeData.setSsNumber(social_security_num);
	}
	
	public void setEmployeeName(String name) {
		employeeData.setName(name);
	}

	public void setEmployeeFirstSurname(String first_surname) {
		employeeData.setSurName(first_surname);
	}

	public void setEmployeeSecondSurname(String second_surname) {
		employeeData.setSecondSurName(second_surname);
	}

	public void setSSRegime(int ssRegime) {
		if(1 == ssRegime)
			contractData.setSsRegimen((byte) 3);
		else
			contractData.setSsRegimen((byte) ssRegime);
	}
	
	public void setSSRegime(byte ssRegime) {
		contractData.setSsRegimen(ssRegime);
	}
	
	public void setContractActivityId(Integer activityID) {
		contractData.setActivityId(activityID);
	}

	public void setContractCCCId(Integer cccId) {
		contractData.setCccId(cccId);
	}

	public void setContractCCCType(Byte cccType) {
		contractData.setCccType(cccType);
	}
	
	public void setContractMdCtz(String md_ctz) {
		contractData.setMdctz(md_ctz);
	}
	
	public void setContractWorkplaceId(Integer workplaceId) {
		contractData.setWorkplaceId(workplaceId);
	}
	
	public void setActivityInfo(String activityInfo) {
		Integer activityId = null;
		Integer cccId = null;
		Byte cccType = null;
		
		if(null != activityInfo) {
			activityId = Integer.parseInt(activityInfo.split("/")[0]);
			cccId = Integer.parseInt(activityInfo.split("/")[1]);
			cccType = Byte.parseByte(activityInfo.split("/")[2]);
		}
		
		setContractActivityId(activityId);
		setContractCCCId(cccId);
		setContractCCCType(cccType);
	}
	
	public void setMdCTZ(String md_ctz) {
		setContractMdCtz(md_ctz);
	}
	
	public void setContractType(String contract_type) {
		contractData.setContractType(contract_type);
	}
	
	public void setContractModel(Integer ordinal) {
		contractData.setContractModel(ordinal);//ModelOption.values()[ordinal].toString());	
	}
	
	public void setContractStartDate(Date start_date) {
		contractData.setStartDate(start_date);		
	}
	
	public void setContractEndDate(Date end_date) {
		contractData.setEndDate(end_date);		
	}
	
	public void setContractSeniorityDate(Date seniority_date) {
		contractData.setSeniorityDate(seniority_date);		
	}
	
	public void setContractAgreementId(Integer agreement_table_id) {
		contractData.setAgreementId(agreement_table_id);
	}
	
	public void setContractAgreementLevelId(Integer agreement_level_table_id) {
		contractData.setAgreementLevelId(agreement_level_table_id);
	}
	
	public void setContractCategory(String category_description) {
		contractData.setAgreementCategory(category_description);		
	}
	
	public void setContractQuoteGroup(String quoteGroup) {
		contractData.setQuoteGroup(quoteGroup);		
	}
	
	public void setContractOccupation(String occupation) {
		contractData.setOcupation(occupation);		
	}
	
	public void setPartialityCoef(Double partialityCoef) {
		contractData.setPartialityCoef(partialityCoef);
	}

	public void setContractJourneyType(Boolean journey_type) {
		contractData.setJourneyType(journey_type ? (byte) 1 : (byte) 0);
	}
	
	public void setContractJourneyDuration(TreeMap<Date, ArrayList<JourneyDuration>> contractJourneyDuration) {
		contractData.setContractJourneyDuration(contractJourneyDuration);
	}
	
	public  ContractJourneyDuration getContractJourneyDuration() {
		return contractData.getContractJourneyDuration();
	}
	
	// EMPLOYEE TABLE
		
	public void setEmployeeBirthDate(Date birth_date) {
		employeeData.setBirthdate(birth_date);
	}

	public void setEmployeeGender(byte gender) {
		employeeData.setGender(gender);
	}
	
	public void setEmployeeCivilStatus(byte civilStatus) {
		employeeData.setCivilStatus(civilStatus);
	}
	
	public void setEmployeeStreetType(String shortCode) {
		employeeData.setStreetType(shortCode);
	}
	
	public void setEmployeeAddress(String address) {
		employeeData.setAddress(address);
	}

	public void setEmployeeAddressNumber(String address_number) {
		employeeData.setAddresNum(address_number);
	}
	
	public void setEmployeeAddressInfo(String adressInfo) {
		employeeData.setAddressInfo(adressInfo);
	}

	public void setEmployeeAddressZip(String zip_code) {
		employeeData.setAddressZip(zip_code);
	}

	public void setEmployeeAddressCity(String city) {
		employeeData.setAddressCity(city);
	}

	public void setEmployeeAddressProvince(String province) {
		employeeData.setAddressProvinces(province);
	}

	public void setEmployeeMobile(String mobile) {
		employeeData.setMobile(mobile);
	}
	
	public void setEmployeePhone(String phone) {
		employeeData.setPhone(phone);
	}

	public void setEmployeeEmail(String email) {
		employeeData.setEmail(email);
	}

	public void setEmployeePayMethod(String payMethodType) {
		employeeData.setPayMethodType(payMethodType);
	}
	
	public void setEmployeePayMethod(byte payMethodType) {
		employeeData.setPayMethodTypeB(payMethodType);
	}
	
	public void setEmployeeBIC(String bic) {
		employeeData.setBic(bic);
	}

	public void setEmployeeAccount(String rbankAccount) {
		employeeData.setAccount(rbankAccount);
	}
	
	public void setEmployeeBankAlias(String bankAlias) {
		employeeData.setBankAlias(bankAlias);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------- AUXILIAR METHODS --------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------

	public Date getContractStartDate() {
		return this.contractData.getStartDate();
	}
	
	public Date getContractEndDate() {
		return this.contractData.getEndDate();
	}

	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfo) {
		employeeContractData = employeeContractInfo;
		employeeData = employeeContractInfo.getEmployeeInfo();
		contractData = employeeContractInfo.getContractInfo();
	}

	public void addContractOtherData(String name, String value) {
		this.employeeContractData.addContractOtherData(name, value);
	}

	public String getContractOtherData(String name) {
		return this.employeeContractData.getContractOtherData().get(name);
	}

	public Boolean getContractOtherDataCB(String name) {
		String value = this.employeeContractData.getContractOtherData().get(name);
		return null == value ? false : true;
	}
	
	public EmployeeContractInfo getContractEmployeeInfo() {
		return this.employeeContractData;
	}

	public String getEmployeeFullName() {
		return employeeData.getFullName();
	}

	public void setContractOtherData(Map<String, String> contractOtherData) {
		this.employeeContractData.setContractOtherData(contractOtherData);
	}

	public void setEmployeePayMethodId(Integer paymethodId) {
		this.employeeData.setPaymethodId(paymethodId);
	}

	public String getFormativeLevel() {
		return this.employeeContractData.getContractSpecificData().getFormativeLevel();
	}

	public void setAgreementSSNumber(String colectiveAgreement) {
		contractData.setAgreementColective(colectiveAgreement);
	}
		
}
