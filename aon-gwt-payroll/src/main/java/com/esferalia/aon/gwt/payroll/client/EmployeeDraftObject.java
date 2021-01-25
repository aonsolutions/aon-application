package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.common.shared.SocialSecurity;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Rbank;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDraftObject extends AbstractDraftObject{
	private Workplace workplace;
	private Employee employee;
		
	private EmployeeContractInfo employeeContractData;
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private DomainEmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private List<Agreement> agreements;
	private List<Workplace> workplaces;
	
	private ActivitiesCCC activitiesCCC;
	
	private Map<String, String> payMethodsMap;
	
	private EmployeeCalendarDraftObject employeeCalendar;
	

		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EmployeeDraftObject(Workplace workplace, Employee employee, DomainEmployeesServiceAsync employeesService, 
			DomainEnterprisesServiceAsync enterprisesService) {
		this.employeesService = employeesService;
		this.enterprisesService = enterprisesService;

		this.workplace = workplace;
		this.employee = employee;
		
		this.agreements = new ArrayList<>();
		this.workplaces = new ArrayList<>();
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		
		this.payMethodsMap = new HashMap<String, String>();
		
		this.undoManager = new UndoManager<Undoable>();
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	public void checkStatus(Consumer<EmployeeStatus> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeStatus(employee.getId(), new AsyncCallback<EmployeeStatus>() {
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
		
	public void initializeEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(this.employee.getId(), new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				employeeContractData.setEmployeeInfo(employeeData);
				employeeContractData.setContractInfo(contractData);

				getAgreements(
						r ->{success.accept(result);},
						f->{}
				);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

	public void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, Integer.MAX_VALUE, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = result;
				getActivitiesCCC(
						r->{success.accept(result);},
						f->{}
					);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
		});	
	}
	
	public void getActivitiesCCC(Consumer<ActivitiesCCC> success, Consumer<Throwable> failure) {
		enterprisesService.getActivitiesCCC(workplace, new AsyncCallback<ActivitiesCCC>() {
			
			@Override
			public void onSuccess(ActivitiesCCC result) {
				activitiesCCC = result;
				getWorkplaces(
						r->{success.accept(result);},
						f->{}
					);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void getWorkplaces(Consumer<List<Workplace>> success, Consumer<Throwable> failure) {
		enterprisesService.getWorkplaces(workplace, new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> result) {
				workplaces = result;
				getPayMethods(
						r->{
							success.accept(result);
						}, f->{}
					);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
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
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub	
			}
		});	
	}
	
	public void updateEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
//		new_employeeContractData.setContractInfo(new_contractData);
//		new_employeeContractData.setEmployeeInfo(new_employeeData);
		
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
	
	public void downloadTa(Consumer<String> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeTa(employee.getId(), new Date(), new AsyncCallback<String>() {
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
	

	// ---------------------------------------------- GETTERS  -------------------------------------------------
	
	public Employee getEmployee() {
		return employee;
	}
	
	public EmployeeContractInfo getEmployeeContractInfo() {
		return this.employeeContractData;
	}
	
	public List<Workplace> getWorkplaces() {
		return this.workplaces;
	}
	
	public List<Agreement> getAgreements(){
		return this.agreements;
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
	
	//AFI CHANGES
	
	public Integer getContractId(){
		return this.contractData.getContractId();
	}
	
	public Integer getDomainId(){
		return this.employeeData.getDomain();
	}
	
	public Boolean hasPayroll(){
		return this.contractData.hasPayroll();
	}
	
	//CONTRACT TABLE
	
	public String getEmployeeDocument(){
		return this.employeeData.getDocument();
	}
	
	public String getEmployeeNationality() {
		return this.employeeData.getNationality();
	}
	
	public String getEmployeeSSNumber() {
		return this.employeeData.getSsNumber();
	}
	
	public String getEmployeeName() {
		return this.employeeData.getName();
	}
	
	public String getEmployeeSurname() {
		return this.employeeData.getSurName();
	}
	
	public String getEmployeeSecondSurname() {
		return this.employeeData.getSecondSurName();
	}
	
	public String getEmployeeFullName() {
		return this.employeeData.getFullName();
	}
	
	public Integer getContractSSRegimen() {
		return (int)this.contractData.getSsRegimen();
	}
	
	public Integer getWorkplaceId(){
		return this.contractData.getWorkplaceId();
	}
	
	public String getContractType() {
		return this.contractData.getContractType();
	}
	
	public Integer getContractTypeN() {
		if(null == this.contractData.getContractType())
			return -1;
		else
			return Integer.parseInt(this.contractData.getContractType());
	}
	
	public Integer getContractModel() {
		return this.contractData.getContractModel();
	}
	
	public Date getContractStartDate() {
		return this.contractData.getStartDate();
	}
	
	public Date getContractEndDate() {
		return this.contractData.getEndDate();
	}
	
	public Date getContractSeniorityDate() {
		return this.contractData.getSeniorityDate();
	}
	
	public Integer getContractAgreement() {
		return this.contractData.getAgreementId();
	}
	
	public Integer getContractAgreementLevelId() {
		return this.contractData.getAgreementLevelId();
	}
	
	public String getContractAgreementCategory() {
		return this.contractData.getAgreementCategory();
	}
	
	public Integer getContractQuoteGroup() {
		return null == this.contractData.getQuoteGroup() ? 0 : Integer.parseInt(this.contractData.getQuoteGroup());
	}
	
	public Integer getContractOcupation() {
		return getCharIndex(this.contractData.getOcupation());
	}
	
	public Integer getContractJourneyType() {
		return (int)this.contractData.getJourneyType();
	}
	
	public ContractJourneyDuration getContractJourneyDuration() {
		return this.contractData.getContractJourneyDuration();
	}
	
	//PERSON TABLE
	
	public Date getEmployeeBirthDate() {
		return this.employeeData.getBirthdate();
	}
	
	public Integer getEmployeeGender() {
		return (int) this.employeeData.getGender();
	}
	
	public Integer getEmployeeCivilStatus() {
		return (int) this.employeeData.getCivilStatus();
	}
	
	public String getEmployeeAddressStreetType(){
		return this.employeeData.getStreetType();
	}
	
	public String getEmployeeAddress() {
		return this.employeeData.getAddress();
	}
	
	public String getEmployeeAddressNumber() {
		return this.employeeData.getAddresNum();
	}
	
	public String getEmployeeAddressInfo() {
		return this.employeeData.getAddressInfo();
	}
	
	public String getEmployeeAddressZip() {
		return this.employeeData.getAddressZip();
	}
	
	public String getEmployeeAddressCity() {
		return this.employeeData.getAddressCity();
	}
	
	public String getEmployeeAddressProvince() {
		return this.employeeData.getAddressProvinces();
	}
	
	public String getEmployeeMobile() {
		return this.employeeData.getMobile();
	}
	
	public String getEmployeePhone() {
		return this.employeeData.getPhone();
	}
	
	public String getEmployeeEmail() {
		return this.employeeData.getEmail();
	}
	
	public Integer getEmployeePayMethod() {
		return getPayMethodIndex(this.employeeData.getPayMethodType());
	}
	
	private int getPayMethodIndex(String payMethodType) {
		switch (payMethodType) {
		case "EFECTIVO":
			return 1;
//		case "GIRO":
//			return 2;
		case "CHEQUE":
			return 2;
		case "TRANSFERENCIA":
			return 3;
		default:
			return 0;
		}
	}
	
	public String getEmployeeBIC() {
		return this.employeeData.getBic();
	}
	
	public String getEmployeeAccount() {
		return this.employeeData.getAccount();
	}
	
	// ---------------------------------------------- SETTERS  -------------------------------------------------
	
	// CONTRACT TABLE
	
	public void setEmployeeDocument(String document) {
		add(employeeData::setDocument, 
				employeeData.getDocument(), 
				document );
		
		employeeData.setDocument(document);
	}
	
	public void setEmployeeDocumentType(String document_type) {
		Byte documentType = null;
		if(document_type == "DNI")
			documentType = (byte) 0;
		else if(document_type == "CIF")
			documentType = (byte) 1;
		else if(document_type == "Pasaporte")
			documentType = (byte) 3;
		else
			documentType = (byte) 0;
		
		add(employeeData::setDocumentType, 
				employeeData.getDocumentType(), 
				documentType );
		
		employeeData.setDocumentType(documentType);
	}
	
	public void setNationality(String nationality) {
		add(employeeData::setNationality, 
				employeeData.getNationality(), 
				nationality );
		
		employeeData.setNationality(nationality);	
	}
	
	public void setEmployeeSocialSecurityNum(String social_security_num) {
		add(employeeData::setSsNumber, 
				employeeData.getSsNumber(), 
				social_security_num );
		
		employeeData.setSsNumber(social_security_num);
	}
	
	public void setEmployeeName(String name) {
		add(employeeData::setName, 
				employeeData.getName(), 
				name );
		
		employeeData.setName(name);
	}
	
	public void setEmployeeFirstSurname(String first_surname) {
		add(employeeData::setSurName, 
				employeeData.getSurName(), 
				first_surname );
		
		employeeData.setSurName(first_surname);
	}

	public void setEmployeeSecondSurname(String second_surname) {
		add(employeeData::setSecondSurName, 
				employeeData.getSecondSurName(), 
				second_surname );
		
		employeeData.setSecondSurName(second_surname);
	}
	
	public void setSSRegime(int ssRegime) {
		Byte ssRegimeB;
		if(1 == ssRegime)
			ssRegimeB = (byte) 3;
		else
			ssRegimeB = (byte) ssRegime;
		
		add(contractData::setSsRegimen, 
				contractData.getSsRegimen(), 
				ssRegimeB );
		
		contractData.setSsRegimen(ssRegimeB);
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
	
	public void setContractWorkplaceId(Integer workplaceId) {
		add(contractData::setWorkplaceId, 
				contractData.getWorkplaceId(), 
				workplaceId );
		
		contractData.setWorkplaceId(workplaceId);
	}

	public void setContractCCCType(byte cccType) {
		add(contractData::setCccType, 
				contractData.getCccType(), 
				cccType );
		
		contractData.setCccType(cccType);
	}
	
	public void setContractType(String contract_type) {
		add(contractData::setContractType, 
				contractData.getContractType(), 
				contract_type );
		
		contractData.setContractType(contract_type);
	}
	
	public void setContractModel(Integer contractModelId) {
		//Falta buscar en ModelOption el String correspondiente a ese ID
		add(contractData::setContractModel, 
				contractData.getContractModel(), 
				contractModelId );
		
		contractData.setContractModel(contractModelId);	
	}
	
	public void setContractStartDate(Date start_date) {
		add(contractData::setStartDate, 
				contractData.getStartDate(), 
				start_date );
		
		contractData.setStartDate(start_date);		
	}
	
	public void setContractEndDate(Date end_date) {
		add(contractData::setEndDate, 
				contractData.getEndDate(), 
				end_date );
		
		contractData.setEndDate(end_date);		
	}
	
	public void setContractSeniorityDate(Date seniority_date) {
		add(contractData::setSeniorityDate, 
				contractData.getSeniorityDate(), 
				seniority_date );
		
		contractData.setSeniorityDate(seniority_date);		
	}
	
	public void setContractAgreementId(Integer agreement_table_id) {
		add(contractData::setAgreementId, 
				contractData.getAgreementId(), 
				agreement_table_id );
		
		contractData.setAgreementId(agreement_table_id);
	}
	
	public void setContractAgreementLevelId(Integer agreement_level_table_id) {
		add(contractData::setAgreementLevelId, 
				contractData.getAgreementLevelId(), 
				agreement_level_table_id );
		
		contractData.setAgreementLevelId(agreement_level_table_id);
	}
	
	public void setContractCategory(String category_description) {
		add(contractData::setAgreementCategory, 
				contractData.getAgreementCategory(), 
				category_description );
		
		contractData.setAgreementCategory(category_description);		
	}
	
	public void setContractQuoteGroup(String quoteGroup) {
		add(contractData::setQuoteGroup, 
				contractData.getQuoteGroup(), 
				quoteGroup );
		
		contractData.setQuoteGroup(quoteGroup);		
	}

	public void setContractOccupation(String occupation) {
		add(contractData::setOcupation, 
				contractData.getOcupation(), 
				occupation );
		
		contractData.setOcupation(occupation);		
	}
	
	public void setContractJourneyType(Boolean journey_type) {
		Byte journey = journey_type ? (byte) 1 : (byte) 0;
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
	
	public void setEmployeeBirthDate(Date birth_date) {
		add(employeeData::setBirthdate, 
				employeeData.getBirthdate(), 
				birth_date );
		
		employeeData.setBirthdate(birth_date);
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

	public void setEmployeeAddressNumber(String address_number) {
		add(employeeData::setAddresNum, 
				employeeData.getAddresNum(), 
				address_number );
		
		employeeData.setAddresNum(address_number);
	}
	
	public void setEmployeeAddressInfo(String addressInfo) {
		add(employeeData::setAddressInfo, 
				employeeData.getAddressInfo(), 
				addressInfo );
		
		employeeData.setAddressInfo(addressInfo);
	}

	public void setEmployeeAddressZip(String zip_code) {
		add(employeeData::setAddressZip, 
				employeeData.getAddressZip(), 
				zip_code );
		
		employeeData.setAddressZip(zip_code);
	}

	public void setEmployeeAddressProvince(String province) {
		add(employeeData::setAddressProvinces, 
				employeeData.getAddressProvinces(), 
				province );
		
		employeeData.setAddressProvinces(province);
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
	
	public void setEmployeePayMethod(byte payMethodType) {
		add(employeeData::setPayMethodTypeB, 
				employeeData.getPayMethodTypeB(), 
				payMethodType );
		
		employeeData.setPayMethodTypeB(payMethodType);
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
	
	// ---------------------------------------------- AUX METHODS  -------------------------------------------------
	

	
	public void setEmployeeCalendar(EmployeeCalendarDraftObject employeeCalendarDraftobject) {
		this.employeeCalendar = employeeCalendarDraftobject;
	}
	
	public Date getPayrollDate() {
		return this.contractData.getPayrollDate();
	}

	
	public EmployeeCalendarDraftObject getEmployeeCalendar() {
		return this.employeeCalendar;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() >= 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
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
	
	public void setMdCtzInfo(String mdCtz) {
		this.contractData.setMdctz(mdCtz);
	}
	
	public int getCccId() {
		return this.contractData.getCccId();
	}

	public int getCccType() {
		return this.contractData.getCccType();
	}
	
	public int getActivityId() {
		return this.contractData.getActivityId();
	}

	public int getCharIndex(String ocupation) {
		switch (ocupation) {
		case "a":
			return 1;
		case "b":
			return 2;
		case "d":
			return 3;
		case "e":
			return 4;
		case "f":
			return 5;
		case "g":
			return 6;
		case "h":
			return 7;
		default:
			return 0;
		}
	}
	
	public boolean checkDocumentValidation(String document_type_string, String document_string) {
		if("DNI".equals(document_type_string)){
			Dni dni = new Dni(document_string);
			if(dni.checkDNI())
				return true;
			else
				return false;
		}else if("" == document_string) {
			return false;
		}else
			return true;
	}
	
	public boolean checkSSNumValidation(String ssNum_string) {
		SocialSecurity ss = new SocialSecurity(ssNum_string);
		if(ss.checkSS())
			return true;
		else
			return false;
	}
	
	public Rbank getRbank(String iban) {
		for(Rbank rbank : employeeData.getRbanks()) {
			if(rbank.getIban().equals(iban))
				return rbank;
		}
		
		return null;
	}
	
	public ArrayList<String> getExistingIban(){
		ArrayList<String> ibans = new ArrayList<String>();
		for(Rbank rbank : employeeData.getRbanks())
			ibans.add(rbank.getIban());
		
		return ibans;
	}

	public void setEmployeePayMethodId(Integer paymethodId) {
		employeeData.setPaymethodId(paymethodId);
	}

	public Integer getPaymethodId() {
		return employeeData.getPaymethodId();
	}

	public String getMdCtz() {
		// TODO Auto-generated method stub
		return contractData.getMdctz();
	}
	
}
