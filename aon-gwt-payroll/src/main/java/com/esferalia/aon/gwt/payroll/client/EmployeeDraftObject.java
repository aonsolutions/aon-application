package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseContext;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Rbank;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDraftObject extends AbstractDraftObject{
	
	// ------------------------------------------------- Variables
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	private Workplace workplace;
	private Employee employee;
		
	private EmployeeContractInfo employeeContractData;
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private EmployeeCalendarDraftObject employeeCalendar;
	
	private EnterpriseContext enterpriseContext;
	
	private DomainUserRoles domainUserRoles;
	
	// ------------------------------------------------- Constructor
	
	public EmployeeDraftObject(Workplace workplace, Employee employee) {

		this.workplace = workplace;
		this.employee = employee;
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		
		this.enterpriseContext = new EnterpriseContext();
		
		this.undoManager = new UndoManager<Undoable>();
	}
	
	public void clearUndoMaganager() {
		this.undoManager.discardAll();
	}
	
	// ------------------------------------------------- Database Methods
	
	public void initializeEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(this.employee.getId(), workplace, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo employeeContractInfo) {
				employeeContractData = employeeContractInfo;
				employeeData = employeeContractInfo.getEmployeeInfo();
				contractData = employeeContractInfo.getContractInfo();
				
				enterprisesService.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
					
					@Override
					public void onSuccess(DomainUserRoles domainUserRolesDB) {
						domainUserRoles = domainUserRolesDB;
						success.accept(employeeContractInfo);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Nothing to do here
					}
				});
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
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});	
	}
	
	public void updateEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
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
	
	public void saveAFIChanges(Date newDate, boolean isChangeContract, String tc2, boolean isQuoteContract, Integer quoteGroup, 
			boolean isOcupationContract, String ocupation, Consumer<String> success, Consumer<Throwable> failure) {
		
			employeesService.setEmployeeAFIChanges(getContractId(),	newDate, isChangeContract, tc2, isQuoteContract, 
					quoteGroup, isOcupationContract, ocupation, new AsyncCallback<String>() {

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
	
	public void downloadTa(Consumer<String> success, Consumer<Throwable> failure) {
		Date date = new Date();
		Integer contractId = employeeContractData.getContractInfo().getContractId();
		Date contractEndDate = employeeContractData.getContractInfo().getEndDate();
		String situation = (null == contractEndDate || DateUtils.isBeforeOrEquals(date, contractEndDate)) ? "ALTA" : "BAJA";
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
	
	public void downloadTaEnd(Consumer<String> success, Consumer<Throwable> failure) {
		downloadTa(success, failure);
	}

	public void checkIdc(Date date, String idc, Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.checkEmployeeIdc(employee.getId(), date, idc, new AsyncCallback<String>() {
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
		employeesService.getEmployeeIdc(employee.getId(), date, new AsyncCallback<String>() {
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
		employeesService.getEmployeeIdcPlNss(employee.getId(), date, new AsyncCallback<String>() {
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
	
	public void getIdcDates(Consumer<List<Date>> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeIdcDates(employee.getId(), null, new AsyncCallback<List<Date>>() {
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
		
		enterprisesService.getLaboralLife(regimen, ccc, employee.getSocialSecurity(), new AsyncCallback<String>() {
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
	
	public void cambioCno(String cno, Date date, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.cambioCno(employeeContractData, cno, date, new AsyncCallback<Void>() {
			
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


	// ------------------------------------------------- Auxiliar Methods (AFI Changes)
	
	public Integer getContractId(){
		return this.contractData.getContractId();
	}
	
	public Integer getDomainId(){
		return this.employeeData.getDomain();
	}
	
	public Integer getWorkplaceId(){
		return this.contractData.getWorkplaceId();
	}
	
	public Boolean hasPayroll(){
		return this.contractData.hasPayroll();
	}
	
	public Date getPayrollDate() {
		return this.contractData.getPayrollDate();
	}
	
	public Date getContractStartDate() {
		return this.contractData.getStartDate();
	}
	
	public Date getContractEndDate() {
		return this.contractData.getEndDate();
	}
	
	// ------------------------------------------------- Auxiliar Methods
	
	public void setEmployeeCalendar(EmployeeCalendarDraftObject employeeCalendarDraftobject) {
		this.employeeCalendar = employeeCalendarDraftobject;
	}
	
	public EmployeeCalendarDraftObject getEmployeeCalendar() {
		return this.employeeCalendar;
	}
	
	public ArrayList<String> getExistingIban(){
		ArrayList<String> ibans = new ArrayList<>();
		for(Rbank rbank : employeeData.getRbanks())
			ibans.add(rbank.getIban());
		
		return ibans;
	}

	// ------------------------------------------------- Getters
	
	public EnterpriseContext getEnterpriseContext() {
		return enterpriseContext;
	}

	public void setEnterpriseContext(EnterpriseContext enterpriseContext) {
		this.enterpriseContext = enterpriseContext;
	}

	public EmployeeContractInfo getEmployeeContractInfo() {
		return this.employeeContractData;
	}
	
	public EmployeeInfo getEmployeeData() {
		return this.employeeData;
	}
	
	public ContractInfo getContractData() {
		return this.contractData;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.enterpriseContext.getAgreements()){
			if(a.getId() >= 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
	}
	
	public String getEmployeeFullName() {
		return this.employeeData.getFullName();
	}
	
	// ------------------------------------------------- Setters
	
	// CONTRACT TABLE
	
	public void setEmployeeDocumentType(String documentTypeIn) {
		Byte documentType = null;
		if(documentTypeIn.equals("DNI"))
			documentType = (byte) 0;
		else if(documentTypeIn.equals("CIF"))
			documentType = (byte) 1;
		else if(documentTypeIn.equals("Pasaporte"))
			documentType = (byte) 3;
		else
			documentType = (byte) 0;
		
		add(employeeData::setDocumentType, 
				employeeData.getDocumentType(), 
				documentType );
		
		employeeData.setDocumentType(documentType);
	}
	
	public void setEmployeeDocument(String document) {
		add(employeeData::setDocument, 
				employeeData.getDocument(), 
				document );
		
		employeeData.setDocument(document);
	}
	
	public void setNationality(String nationality) {
		add(employeeData::setNationality, 
				employeeData.getNationalityCode(), 
				nationality );
		
		employeeData.setNationality(nationality);	
	}
	
	public void setEmployeeSocialSecurityNum(String socialSecurityNum) {
		add(employeeData::setSsNumber, 
				employeeData.getSsNumber(), 
				socialSecurityNum );
		
		employeeData.setSsNumber(socialSecurityNum);
	}
	
	public void setEmployeeName(String name) {
		add(employeeData::setName, 
				employeeData.getName(), 
				name );
		
		employeeData.setName(name);
	}
	
	public void setEmployeeFirstSurname(String firstSurname) {
		add(employeeData::setSurName, 
				employeeData.getSurName(), 
				firstSurname );
		
		employeeData.setSurName(firstSurname);
	}

	public void setEmployeeSecondSurname(String secondSurname) {
		add(employeeData::setSecondSurName, 
				employeeData.getSecondSurName(), 
				secondSurname );
		
		employeeData.setSecondSurName(secondSurname);
	}
	
	public void setSSRegime(byte ssRegime) {
		add(contractData::setSsRegimen, 
				contractData.getSsRegimen(), 
				ssRegime );
		
		contractData.setSsRegimen(ssRegime);
	}
	
	public void setMdTBT(byte mdTbt) {
		add(contractData::setMdTBT, 
				contractData.getMdTBT(), 
				mdTbt );
		
		contractData.setMdTBT(mdTbt);
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
	
	public void setContractActivityId(Integer activityID) {
		add(contractData::setActivityId, 
				contractData.getActivityId(), 
				activityID );
		
		contractData.setActivityId(activityID);
	}

	public void setContractCCCId(Integer cccId) {
		add(contractData::setCccId, 
				contractData.getCccId(), 
				cccId );
		
		contractData.setCccId(cccId);
	}
	
	public void setContractCCCType(byte cccType) {
		add(contractData::setCccType, 
				contractData.getCccType(), 
				cccType );
		
		contractData.setCccType(cccType);
	}
	
	public void setMdCtzInfo(String mdCtz) {
		add(contractData::setMdctz, 
				contractData.getMdctz(), 
				mdCtz );
		
		contractData.setMdctz(mdCtz);
	}
	
	public void setContractWorkplaceId(Integer workplaceId) {
		add(contractData::setWorkplaceId, 
				contractData.getWorkplaceId(), 
				workplaceId );
		
		contractData.setWorkplaceId(workplaceId);
	}
	
	public void setContractType(String contractType) {
		add(contractData::setContractType, 
				contractData.getContractType(), 
				contractType );
		
		contractData.setContractType(contractType);
	}
	
	public void setContractModel(Integer contractModelId) {
		add(contractData::setContractModel, 
				contractData.getContractModel(), 
				contractModelId );
		
		contractData.setContractModel(contractModelId);	
	}
	
	public void setContractStartDate(Date startDate) {
		add(contractData::setStartDate, 
				contractData.getStartDate(), 
				startDate );
		
		contractData.setStartDate(startDate);		
	}
	
	public void setContractEndDate(Date endDate) {
		add(contractData::setEndDate, 
				contractData.getEndDate(), 
				endDate );
		
		contractData.setEndDate(endDate);		
	}
	
	public void setContractSeniorityDate(Date seniorityDate) {
		add(contractData::setSeniorityDate, 
				contractData.getSeniorityDate(), 
				seniorityDate );
		
		contractData.setSeniorityDate(seniorityDate);		
	}
	
	public void setContractAgreementId(Integer agreementTableId) {
		add(contractData::setAgreementId, 
				contractData.getAgreementId(), 
				agreementTableId );
		
		contractData.setAgreementId(agreementTableId);
	}
	
	public void setAgreementSSNumber(String colectiveAgreement) {
		add(contractData::setAgreementColective, 
				contractData.getAgreementColective(), 
				colectiveAgreement );
		
		contractData.setAgreementColective(colectiveAgreement);
	}
	
	public void setContractAgreementLevelId(Integer agreementLevelTableId) {
		add(contractData::setAgreementLevelId, 
				contractData.getAgreementLevelId(), 
				agreementLevelTableId );
		
		contractData.setAgreementLevelId(agreementLevelTableId);
	}
	
	public void setContractCategory(String categoryDescription) {
		add(contractData::setAgreementCategory, 
				contractData.getAgreementCategory(), 
				categoryDescription );
		
		contractData.setAgreementCategory(categoryDescription);		
	}
	
	public void setContractQuoteGroup(String quoteGroup) {
		add(contractData::setQuoteGroup, 
				contractData.getQuoteGroup(), 
				quoteGroup );
		
		contractData.setQuoteGroup(quoteGroup);		
	}
	
	public void setContractQuoteIdxMonth(boolean quoteGroupMonth) {
		add(contractData::setQuoteGroupIdxMonth, 
				contractData.getQuoteGroupIdxMonth(), 
				quoteGroupMonth );
		
		contractData.setQuoteGroupIdxMonth(quoteGroupMonth);
	}

	public void setContractOccupation(String occupation) {
		add(contractData::setOcupation, 
				contractData.getOcupation(), 
				occupation );
		
		contractData.setOcupation(occupation);		
	}
	
	public void setContractRlce(String rlce) {
		add(contractData::setRlce, 
				contractData.getRlce(), 
				rlce );
		
		contractData.setRlce(rlce);		
	}
	
	public void setContractCno(String cno) {
		add(contractData::setCno, 
				contractData.getCno(), 
				cno );
		
		contractData.setCno(cno);		
	}
	
	public void setContractEmployeesColective(String employeesColective) {
		add(contractData::setEmployeesColective, 
				contractData.getEmployeesColective(), 
				employeesColective );
		
		contractData.setEmployeesColective(employeesColective);		
	}
	
	public void setContractJourneyType(Boolean journeyType) {
		Byte journey = Boolean.TRUE.equals(journeyType) ? (byte) 1 : (byte) 0;
		add(contractData::setJourneyType, 
				contractData.getJourneyType(), 
				journey );
		
		contractData.setJourneyType(journey);
	}
	
	public void setPartialityCoef(Double partialityCoef) {
		add(contractData::setPartialityCoef, 
				contractData.getPartialityCoef(), 
				partialityCoef );
		
		contractData.setPartialityCoef(partialityCoef);
	}
	
	public void setContractJourneyDuration(TreeMap<Date, ArrayList<JourneyDuration>> contractJourneyDuration) {
		contractData.setContractJourneyDuration(contractJourneyDuration);
	}
	
	// EMPLOYEE
	
	public void setEmployeeBirthDate(Date birthDate) {
		add(employeeData::setBirthdate, 
				employeeData.getBirthdate(), 
				birthDate );
		
		employeeData.setBirthdate(birthDate);
	}

	public void setEmployeeGender(byte gender) {
		add(employeeData::setGender, 
				employeeData.getGender(), 
				gender );
		
		employeeData.setGender(gender);
	}
	
	public void setEmployeeCivilStatus(byte civilStatus) {
		add(employeeData::setCivilStatus, 
				employeeData.getCivilStatus(), 
				civilStatus );
		
		employeeData.setCivilStatus(civilStatus);
	}

	public void setEmployeeStreetType(String shortCode) {
		add(employeeData::setStreetType, 
				employeeData.getStreetType(), 
				shortCode );
		
		employeeData.setStreetType(shortCode);
	}
	
	public void setEmployeeAddress(String address) {
		add(employeeData::setAddress, 
				employeeData.getAddress(), 
				address );
		
		employeeData.setAddress(address);
	}

	public void setEmployeeAddressNumber(String addressNumber) {
		add(employeeData::setAddresNum, 
				employeeData.getAddresNum(), 
				addressNumber );
		
		employeeData.setAddresNum(addressNumber);
	}
	
	public void setEmployeeAddressInfo(String addressInfo) {
		add(employeeData::setAddressInfo, 
				employeeData.getAddressInfo(), 
				addressInfo );
		
		employeeData.setAddressInfo(addressInfo);
	}

	public void setEmployeeAddressZip(String zipCode) {
		add(employeeData::setAddressZip, 
				employeeData.getAddressZip(), 
				zipCode );
		
		employeeData.setAddressZip(zipCode);
	}

	public void setEmployeeAddressProvince(Integer geozoneId) {
		add(employeeData::setAddressProvinces, 
				employeeData.getAddressProvinces(), 
				geozoneId );
		
		employeeData.setAddressProvinces(geozoneId);
	}

	public void setEmployeeAddressCity(String city) {
		add(employeeData::setAddressCity, 
				employeeData.getAddressCity(), 
				city );
		
		employeeData.setAddressCity(city);
	}

	public void setEmployeeMobile(String mobile) {
		add(employeeData::setMobile, 
				employeeData.getMobile(), 
				mobile );
		
		employeeData.setMobile(mobile);
	}
	
	public void setEmployeePhone(String phone) {
		add(employeeData::setPhone, 
				employeeData.getPhone(), 
				phone );
		
		employeeData.setPhone(phone);
	}

	public void setEmployeeEmail(String email) {
		add(employeeData::setEmail, 
				employeeData.getEmail(), 
				email );
		
		employeeData.setEmail(email);
	}
	
	public void setEmployeePayMethodId(Integer paymethodId) {
		add(employeeData::setPaymethodId, 
				employeeData.getPaymethodId(), 
				paymethodId );
		
		employeeData.setPaymethodId(paymethodId);
	}

	public void setEmployeeAccount(String rbankAccount) {
		add(employeeData::setAccount, 
				employeeData.getAccount(), 
				rbankAccount );
		
		employeeData.setAccount(rbankAccount);
	}
	
	public void setEmployeeBankAlias(String bankAlias) {
		add(employeeData::setBankAlias, 
				employeeData.getBankAlias(), 
				bankAlias );
		
		employeeData.setBankAlias(bankAlias);
	}

	public void setEmployeeBIC(String rbankBIC) {
		add(employeeData::setBic, 
				employeeData.getBic(), 
				rbankBIC );
		
		employeeData.setBic(rbankBIC);
	}
	
	public void setEmployeeRbankId(Integer rbankId) {
		employeeData.setRbankId(rbankId);
	}

	public String getAgreementDescription() {
		Integer agreementId = contractData.getAgreementId();
		if(null == agreementId)
			return null;
		else {
			for(Agreement agreement : this.enterpriseContext.getAgreements())
				if(agreement.getId().equals(agreementId))
					return agreement.getDescription();
		}
		return null;
	}

	public boolean isComunica() {
		return this.domainUserRoles.isComunica();
	}
	
}
