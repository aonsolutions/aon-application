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
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
		this.payMethodsMap = new HashMap<>();
	}
	
	// ------------------------------------------------- Database Methods
	
	public void getAgreement(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {	
		enterprisesService.getAgreement(agreementId, new AsyncCallback<Agreement>() {
			
			@Override
			public void onSuccess(Agreement result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});	
	}
	
	// ------------------------------------------------- Database Methods (Employee)
	
	public void getEmployeeContract(Integer contractId, Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(contractId, workplace, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
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
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// ------------------------------------------------- Database Methods (Employee Delete)
	
	public void deleteContract(Consumer<Void> success, Consumer<Throwable> failure) {
		Employee employeeAux = new Employee();
		employeeAux.setId(getContractData().getContractId());
		
		if(Boolean.TRUE.equals(getContractData().hasPayroll())) {
			
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
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}
			});
			
		}	
	}
	
	public void delete4EverContract(Consumer<Void> success, Consumer<Throwable> failure) {
		enterprisesService.delete4EverContract(getContractData().getContractId(), new AsyncCallback<Void>() {
			
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
	
	// ------------------------------------------------- Database Methods (Specific Data)
	
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
	
	// ------------------------------------------------- Database Methods (Other Data)
	
	public void getContractOtherInfo(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		Integer contractType = Integer.parseInt(contractData.getContractType());
		
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
	
	// ------------------------------------------------- Database Methods (Salaries)
	
	public void getEmployeeSalaryObject(Consumer<EmployeeSalaryObject> success) {
		EmployeeSalaryObject employeeSalaryObject = new EmployeeSalaryObject(contractData.getContractId(), employeeData.getFullName());
		success.accept(employeeSalaryObject);
	}
	
	// ------------------------------------------------- Database Methods (Calendar)
	
	public void getEmployeeCalendarObject(Consumer<EmployeeCalendarDraftObject> success) {
		EmployeeCalendarDraftObject employeeCalendarDraftObject = new EmployeeCalendarDraftObject(contractData.getContractId());
		success.accept(employeeCalendarDraftObject);
	}
	
	// ------------------------------------------------- Database Methods (IRPF)
	
	public void getEmployeeContractIrpfObject(Consumer<EmployeeContractIrpfObject> success) {
		EmployeeContractIrpfObject employeeContractIrpfObject = new EmployeeContractIrpfObject(contractData.getContractId(), employeeData.getFullName(), employeeData.getDocument(), employeeData.getSsNumber(), contractData.getStartDate());
		success.accept(employeeContractIrpfObject);
	}
	
	// ------------------------------------------------- Database Methods (Mod145)
	
	public void getMod145Object(Consumer<Mod145Object> success) {
		Mod145Object mod145Object = new Mod145Object(contractData.getContractId(), employeeData.getDomain());
		success.accept(mod145Object);
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
	
	// ------------------------------------------------- Database Methods (Export Contract)
	
	public void saveContractExport(boolean isTransform, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.fillContract(contractData.getContractId(), Integer.parseInt(contractData.getContractType()), getFormativeLevel(), isTransform, new AsyncCallback<Void>() {
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
	
	// ------------------------------------------------- Database Methods (SEPE Get files)

	public void downloadCbc(Consumer<String> success, Consumer<Throwable> failure) {
		ContractTypeRecord contractTypeRecord = null;
		try {
			ContractType contractType = new ContractType();
			contractTypeRecord = contractType.getContractType(Integer.parseInt(contractData.getContractType()));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		if(null == contractTypeRecord || contractTypeRecord.isTransform())
			downloadCbcTransform(success, failure);
		else
			downloadCbcContract(success, failure);
	}
	
	public void downloadCbcContract(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCbc(employeeData.getDocument(), contractData.getContractId(), contractData.getStartDate(), contractData.getStartDate(), contractData.getSepeId(), new AsyncCallback<String>() {
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
	
	public void downloadCbcTransform(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCbcTransform(contractData.getEnterpriseCIF(), employeeData.getDocument(), contractData.getContractId(), contractData.getStartDate(), contractData.getSepeId(), new AsyncCallback<String>() {
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
		downloadCtoContract(success, failure);
	}
	
	public void downloadCtoContract(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCto(employeeData.getDocument(), contractData.getContractId(), contractData.getStartDate(), contractData.getStartDate(), contractData.getSepeId(), new AsyncCallback<String>() {
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
	
	public void downloadCtoTransform(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCtoTransform(contractData.getEnterpriseCIF(), employeeData.getDocument(), contractData.getContractId(), contractData.getOriginalStartDate(), contractData.getSepeTransformId(), new AsyncCallback<String>() {
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
	
	public void downloadCtoExtension(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeCtoExtension(contractData.getEnterpriseCIF(), employeeData.getDocument(), contractData.getContractId(), contractData.getOriginalStartDate(), null, contractData.getSepeExtensionId(), new AsyncCallback<String>() {
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
	
	public void getCertifica2PDF(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getCertifica2PDF(
				employeeContractData.getContractInfo().getContractId(),
				employeeContractData.getEmployeeInfo().getDocument(),
				employeeContractData.getContractInfo().getEndDate(), 
				new AsyncCallback<String>() {

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
	
	// ------------------------------------------------- Database Methods (SEPE Comunications)
	
	public void sendBasicCopy(Consumer<Void> success, Consumer<Throwable> failure) {
		ContractTypeRecord contractTypeRecord = null;
		try {
			ContractType contractType = new ContractType();
			contractTypeRecord = contractType.getContractType(Integer.parseInt(contractData.getContractType()));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		if(null == contractTypeRecord || contractTypeRecord.isTransform())
			employeesService.sendContractoCBTransformSEPE(employeeContractData, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}

				@Override
				public void onSuccess(Void result) {
					success.accept(result);
				}
				
			});
		else
			employeesService.sendContractoCBSEPE(employeeContractData, new AsyncCallback<Void>() {
	
				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}
	
				@Override
				public void onSuccess(Void result) {
					success.accept(result);
				}
				
			});
	}
	
	public void sendContract(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.sendContractoSEPE(employeeContractData, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void sendContractTransform(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.sendContractTransform(employeeContractData, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void sendContractExtension(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.sendContractExtension(employeeContractData, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void removeContractTransform(Consumer<Void> success, Consumer<Throwable> failure) {
		String transformIde = employeeContractData.getContractInfo().getSepeTransformId();
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		
		employeesService.removeContractTransform(transformIde, contractId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void removeContract(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.removeContractoSEPE(employeeContractData, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void getComunicationInfo() {
		String document = employeeContractData.getEmployeeInfo().getDocument();
		Date fini = employeeContractData.getContractInfo().getStartDate();
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		
		employeesService.getSepeComunicationData(document, fini, contractId, new AsyncCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	// ------------------------------------------------- Database Methods (TGSS Comunications)
	
	public void sendEmployeeAlta(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.sendEmployeeAlta(employeeContractData, new AsyncCallback<Void>() {
			
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
	
	public void sendEmployeeBaja(String settleReason, Consumer<Void> success, Consumer<Throwable> failure) {
		employeeContractData.getContractInfo().setSettleReason(settleReason);
		employeesService.sendEmployeeBaja(employeeContractData, new AsyncCallback<Void>() {
			
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
	
	public void cambioGrupCtz(String quoteGroup, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.cambioGrupCtz(employeeContractData, quoteGroup, date, new AsyncCallback<Void>() {
			
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
	
	public void cambioCoef(String coef, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.cambioCoef(employeeContractData, coef, date, new AsyncCallback<Void>() {
			
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
	
	public void cambioContrato(String tc2, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.cambioContrato(employeeContractData, tc2, date, new AsyncCallback<Void>() {
			
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
	
	public void cambioOcupacion(String ocupation, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.cambioOcupacion(employeeContractData, ocupation, date, new AsyncCallback<Void>() {
			
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
	
	public void cambioCatProf(String contract, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.cambioCatProf(employeeContractData, contract, date, new AsyncCallback<Void>() {
			
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
	
	public void movPrevDelete(Consumer<Void> success, Consumer<Throwable> failure) {
		String situation = employeeContractData.getContractInfo().isTGSSActive() ? "ALTA" : "BAJA";
		String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
		String ctaCti = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
		String nss = employeeContractData.getEmployeeInfo().getSsNumber();
		Date fecha = AonStringUtils.equalsIgnoreCase(situation, "ALTA") ? employeeContractData.getContractInfo().getEndDate() : employeeContractData.getContractInfo().getStartDate();
		
		employeesService.movPrevDelete(situation, regimen, ctaCti, nss, fecha, new AsyncCallback<Void>() {
			
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
	
	public void altaConsolidadaDelete(Consumer<Void> success, Consumer<Throwable> failure) {
		String situation = "ALTA";
		String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
		String ctaCti = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
		String nss = employeeContractData.getEmployeeInfo().getSsNumber();
		Date fecha = AonStringUtils.equalsIgnoreCase(situation, "ALTA") ? employeeContractData.getContractInfo().getEndDate() : employeeContractData.getContractInfo().getStartDate();
		
		employeesService.altaConsolidadaDelete(situation, regimen, ctaCti, nss, fecha, new AsyncCallback<Void>() {
			
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
	
	// ------------------------------------------------- Database Methods (TGSS Get files)
	
	public void downloadTa(Consumer<String> success, Consumer<Throwable> failure, String situation) {
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
		String ctaCti = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
		String nss = employeeContractData.getEmployeeInfo().getSsNumber();
		Date fecha = AonStringUtils.equalsIgnoreCase(situation, "ALTA") ? employeeContractData.getContractInfo().getStartDate() : employeeContractData.getContractInfo().getEndDate();
		
		employeesService.getEmployeeTa(contractId, situation, regimen, ctaCti, nss, fecha, new AsyncCallback<String>() {
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
	
	public void downloadIdc(Date date, Consumer<String> success, Consumer<Throwable> failure) {
		date = null == date ? new Date() : date;
		date = checkPrevAlta() ? new Date() : date;
		employeesService.getEmployeeIdc(contractData.getContractId(), date, new AsyncCallback<String>() {
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

	public void downloadIdcPlNss(Date date, Consumer<String> success, Consumer<Throwable> failure) {
		date = checkPrevAlta() ? new Date() : date;
		employeesService.getEmployeeIdcPlNss(contractData.getContractId(), date, new AsyncCallback<String>() {
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
	
	public void getIdcDates(Integer contractId, Consumer<List<Date>> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeIdcDates(contractId, null, new AsyncCallback<List<Date>>() {
			@Override
			public void onSuccess(List<Date> result) {
				success.accept(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void downloadLaboralLife(Consumer<String> success, Consumer<Throwable> failure) {
		String regimen = employeeContractData.getContractInfo().getCompleteCCC().substring(0, 4);
		String ccc = employeeContractData.getContractInfo().getCompleteCCC().substring(4, employeeContractData.getContractInfo().getCompleteCCC().length());
		
		enterprisesService.getLaboralLife(regimen, ccc, employeeData.getSsNumber(), new AsyncCallback<String>() {
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
	
	private boolean checkPrevAlta() {
		Date currentDate = new Date();
		Date startDate = contractData.getStartDate();
		return DateUtils.isAfterOrEquals(startDate, currentDate) && !DateUtils.equals(startDate, currentDate);
	}
	
	// ------------------------------------------------- Database Methods (Delete Extension)
	
	public void deleteContractExtension(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.deleteContractExtension(contractData.getContractId(), new AsyncCallback<Void>() {
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
	
	// ------------------------------------------------- Database Methods (Delete Transform)
	
	public void deleteContractTransform(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.deleteContractTransform(contractData.getContractId(), new AsyncCallback<Void>() {
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
	
	public Integer getContractId() {
		return this.contractData.getContractId();
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
	
	public void setEmployeeDocumentType(String documentType) {
		if(documentType.equals("DNI"))
			employeeData.setDocumentType((byte) 0);
		else if(documentType.equals("CIF"))
			employeeData.setDocumentType((byte) 1);
		else if(documentType.equals("Pasaporte"))
			employeeData.setDocumentType((byte) 3);
	}
	
	public void setEmployeeDocument(String document) {
		employeeData.setDocument(document);
	}
	
	public void setNationality(String nationality) {
		employeeData.setNationality(nationality);	
	}

	public void setEmployeeSocialSecurityNum(String socialSecurityNum) {
		employeeData.setSsNumber(socialSecurityNum);
	}
	
	public void setEmployeeName(String name) {
		employeeData.setName(name);
	}

	public void setEmployeeFirstSurname(String firstSurname) {
		employeeData.setSurName(firstSurname);
	}

	public void setEmployeeSecondSurname(String secondSurname) {
		employeeData.setSecondSurName(secondSurname);
	}
	
	public void setSSRegime(byte ssRegime) {
		contractData.setSsRegimen(ssRegime);
	}
	
	public void setMdTBT(String tbtType) {
		contractData.setMdTBT(Byte.parseByte(tbtType));
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
	
	public void setContractMdCtz(String mdCtz) {
		contractData.setMdctz(mdCtz);
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
	
	public void setContractType(String contractType) {
		contractData.setContractType(contractType);
	}
	
	public void setContractModel(Integer ordinal) {
		contractData.setContractModel(ordinal); //ModelOption.values()[ordinal].toString());	
	}
	
	public void setContractStartDate(Date startDate) {
		contractData.setStartDate(startDate);		
	}
	
	public void setContractEndDate(Date endDate) {
		contractData.setEndDate(endDate);		
	}
	
	public void setContractSeniorityDate(Date seniorityDate) {
		contractData.setSeniorityDate(seniorityDate);		
	}
	
	public void setContractAgreementId(Integer agreementTableId) {
		contractData.setAgreementId(agreementTableId);
	}
	
	public void setAgreementSSNumber(String colectiveAgreement) {
		contractData.setAgreementColective(colectiveAgreement);
	}
	
	public void setContractAgreementLevelId(Integer agreementLevelTableId) {
		contractData.setAgreementLevelId(agreementLevelTableId);
	}
	
	public void setContractCategory(String categoryDescription) {
		contractData.setAgreementCategory(categoryDescription);		
	}
	
	public void setContractQuoteGroup(String quoteGroup) {
		contractData.setQuoteGroup(quoteGroup);		
	}
	
	public void setContractQuoteIdxMonth(boolean quoteGroupMonth) {
		contractData.setQuoteGroupIdxMonth(quoteGroupMonth);
	}
	
	public void setContractOccupation(String occupation) {
		contractData.setOcupation(occupation);		
	}
	
	public void setContractRlce(String rlce) {
		contractData.setRlce(rlce);
	}
	
	public void setContractCno(String cno) {
		contractData.setCno(cno);
	}
	
	public void setContractEmployeesColective(String employeesColective) {
		contractData.setEmployeesColective(employeesColective);
	}
	
	public void setPartialityCoef(Double partialityCoef) {
		contractData.setPartialityCoef(partialityCoef);
	}

	public void setContractJourneyType(Boolean journeyType) {
		contractData.setJourneyType(Boolean.TRUE.equals(journeyType) ? (byte) 1 : (byte) 0);
	}
	
	public void setContractJourneyDuration(TreeMap<Date, ArrayList<JourneyDuration>> contractJourneyDuration) {
		contractData.setContractJourneyDuration(contractJourneyDuration);
	}
	
	// EMPLOYEE TABLE
		
	public void setEmployeeBirthDate(Date birthDate) {
		employeeData.setBirthdate(birthDate);
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

	public void setEmployeeAddressNumber(String addressNumber) {
		employeeData.setAddresNum(addressNumber);
	}
	
	public void setEmployeeAddressInfo(String adressInfo) {
		employeeData.setAddressInfo(adressInfo);
	}

	public void setEmployeeAddressZip(String zipCode) {
		employeeData.setAddressZip(zipCode);
	}
	
	public void setEmployeeAddressProvince(Integer geozoneId) {
		employeeData.setAddressProvinces(geozoneId);
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

	public void setAgreements(List<Agreement> agreements) {
		this.agreements = agreements;
	}

	public void setWorkplaces(List<Workplace> workplaces) {
		this.workplaces = workplaces;
	}

	public void setActivitiesCCC(ActivitiesCCC activitiesCCC) {
		this.activitiesCCC = activitiesCCC;
	}

	public void setPayMethodsMap(Map<String, String> payMethodsMap) {
		this.payMethodsMap = payMethodsMap;
	}

	public String getAgreementDescription() {
		Integer agreementId = contractData.getAgreementId();
		if(null == agreementId)
			return null;
		else {
			for(Agreement agreement : agreements)
				if(agreement.getId().equals(agreementId))
					return agreement.getDescription();
		}
		return null;
	}

}
