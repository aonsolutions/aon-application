package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
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
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public class ContrataEmployeeObject {
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	private Workplace workplace;
	
	private EmployeeContractInfo employeeContractData;
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private List<Agreement> agreements;
	private List<Workplace> workplaces;
	private ActivitiesCCC activitiesCCC;
	
	private Map<String, String> payMethodsMap;
	
	// ------------------------------------------------- Constructor
	
	public ContrataEmployeeObject() {
		super();
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		
		this.workplaces = new ArrayList<>();
		this.payMethodsMap = new HashMap<String, String>();
	}
	
	// ------------------------------------------------- Database Methods
	
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
	
	public void getEmployeeSalaryObject(Consumer<EmployeeSalaryObject> success, Consumer<Throwable> failure) {
		EmployeeSalaryObject employeeSalaryObject = new EmployeeSalaryObject(contractData.getContractId(), employeeData.getFullName());
		success.accept(employeeSalaryObject);
	}
	
	public void getEmployeeCalendarObject(Consumer<EmployeeCalendarDraftObject> success, Consumer<Throwable> failure) {
		EmployeeCalendarDraftObject employeeCalendarDraftObject = new EmployeeCalendarDraftObject(contractData.getContractId(), contractData.getStartDate(), contractData.getEndDate(), employeesService);
		success.accept(employeeCalendarDraftObject);
	}
	
	public void getEmployeeEventsObject(Consumer<EmployeeEventsDraftObject> success, Consumer<Throwable> failure) {
		EmployeeEventsDraftObject employeeEventsDraftObject = new EmployeeEventsDraftObject(contractData.getContractId());
		success.accept(employeeEventsDraftObject);
	}
	
	public void getSalaryDraftObject(Consumer<SalaryDraftObject> success, Consumer<Throwable> failure) {
		employeesService.getEmployee(contractData.getContractId(), new AsyncCallback<Employee>() {
			@Override
			public void onSuccess(Employee employee) {
				Date salaryDate = DateUtils.before(DateUtils.after(new Date(), employee.getStartDate()), employee.getEndDate());
				Date startDate = DateUtils.getFirstDayOfMonth(salaryDate);
				Date endDate = DateUtils.getLastDayOfMonth(salaryDate);
				Date issueDate = endDate;

				SalaryDraft salaryDraft = new SalaryDraft();
				salaryDraft.setEmployee(employee);
				salaryDraft.setStartDate(startDate);
				salaryDraft.setEndDate(endDate);
				salaryDraft.setIssueDate(issueDate);
				salaryDraft.setType(Type.SALARY);

				SalaryDraftObject salaryDraftObject = new SalaryDraftObject(salaryDraft, employeesService);
				success.accept(salaryDraftObject);
			}

			@Override
			public void onFailure(Throwable caught) {
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
	
	public void getSSBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		enterprisesService.getEmployeeSSBonuses(contractId, new AsyncCallback<List<SSBonusData>>() {
			
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
		employeesService.getEmployeeIdcPlNss(contractData.getContractId(), new Date(), new AsyncCallback<String>() {
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
		employeesService.fillContract(contractData.getContractId(), Integer.parseInt(contractData.getContractType()), getFormativeLevel(), new AsyncCallback<List<ContractAttach>>() {
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
	
	// ------------------------------------------------- Database Methods (CheckStatus)
	
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
	
	// ------------------------------------------------- Database Methods (SEPE & TGSS comunication)
	
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
	
	// ------------------------------------------------- Auxiliar Methods
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfo) {
		employeeContractData = employeeContractInfo;
		employeeData = employeeContractInfo.getEmployeeInfo();
		contractData = employeeContractInfo.getContractInfo();
	}
	
	public EmployeeContractInfo getContractEmployeeInfo() {
		return this.employeeContractData;
	}
	
	public void setContractOtherData(Map<String, String> contractOtherData) {
		this.employeeContractData.setContractOtherData(contractOtherData);
	}
	
	public Date getContractStartDate() {
		return this.contractData.getStartDate();
	}
	
	public Date getContractEndDate() {
		return this.contractData.getEndDate();
	}

	public String getEmployeeFullName() {
		return employeeData.getFullName();
	}

	public String getFormativeLevel() {
		return this.employeeContractData.getContractSpecificData().getFormativeLevel();
	}
		
	// ------------------------------------------------- Getters
	
	public EmployeeInfo getEmployeeData() {
		return this.employeeData;
	}
	
	public ContractInfo getContractData() {
		return this.contractData;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() > 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
	}
	
	public List<Workplace> getWorkplaces() {
		return this.workplaces;
	}
	
	public Map<Integer, String> getActivities() {
		return activitiesCCC.getActivities();
	}
	
	public Map<Integer, CCCInfo> getCCCs() {
		return activitiesCCC.getCccs();
	}
	
	public Map<String, String> getPayMethods() {
		return payMethodsMap;
	}
	
	public  ContractJourneyDuration getContractJourneyDuration() {
		return contractData.getContractJourneyDuration();
	}
	
	// ------------------------------------------------- Setters
	
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
	
	public void setContractType(String contract_type) {
		contractData.setContractType(contract_type);
	}
	
	public void setContractModel(Integer ordinal) {
		contractData.setContractModel(ordinal); //ModelOption.values()[ordinal].toString());	
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
	
	public void setAgreementSSNumber(String colectiveAgreement) {
		contractData.setAgreementColective(colectiveAgreement);
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
	
	public void setEmployeeAddressProvince(String province) {
		employeeData.setAddressProvinces(province);
	}

	public void setEmployeeAddressCity(String city) {
		employeeData.setAddressCity(city);
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

	public void setEmployeePayMethodId(Integer paymethodId) {
		this.employeeData.setPaymethodId(paymethodId);
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

}
