package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDialogObject {
	
	// ------------------------------------------------- Variables
	
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
	
	// ------------------------------------------------- Constructor
	
	public EmployeeDialogObject(Workplace workplace) {
		super();
		
		this.workplace = workplace;
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		
		this.workplaces = new ArrayList<>();
		this.payMethodsMap = new HashMap<>();
	}
	
	// ------------------------------------------------- Database Methods
	
	public void getWorkplaceEmployees(Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure) {
		employeesService.getWorkplaceEmployees(workplace, new AsyncCallback<WorkplaceEmployees>() {

			@Override
			public void onSuccess(WorkplaceEmployees workplaceEmployeesIn) {
				workplaceEmployees = workplaceEmployeesIn;
				agreements = workplaceEmployees.getAgreements();
				workplaces = workplaceEmployees.getWorkplaces();
				activitiesCCC = workplaceEmployees.getActivitiesCCC();
				payMethodsMap = workplaceEmployees.getPayMethods();
				
				success.accept(workplaceEmployeesIn);
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
	
	public void initializeEmployee(Integer contractId, Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(contractId, workplace, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				
				// Set default contract start_date & end_date to null
				contractData.setStartDate(null);
				contractData.setEndDate(null);
				
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}
	
	public void createEmployeeContract(IntConsumer success, Consumer<Throwable> failure){
		employeeContractData.setEmployeeInfo(employeeData);
		employeeContractData.setContractInfo(contractData);
		
		employeesService.createEmployeeContract(employeeContractData, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				success.accept(result.getContractInfo().getContractId());
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}
	
	// ------------------------------------------------- Auxiliar Methods
	
	public void resetEmptyInfo() {
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		this.contractData.setWorkplaceId(null == workplace ? null : workplace.getId());
	}

	public Date getContractStartDate() {
		return this.contractData.getStartDate();
	}
	
	public Date getContractEndDate() {
		return this.contractData.getEndDate();
	}
	
	// ------------------------------------------------- Getters
	
	public EmployeeInfo getEmployeeData() {
		return this.employeeData;
	}
	
	public ContractInfo getContractData() {
		return this.contractData;
	}
	
	public WorkplaceEmployees getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() > 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
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
	
	public List<Workplace> getWorkplaces() {
		return this.workplaces;
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
			agreementId = activity.getId();
			contractData.setAgreementId(agreementId);
		}else
			agreementId = contractData.getAgreementId();
		
		return agreementId;
	}
	
	// ------------------------------------------------- Get Employee by info
	
	public EmployeeInfo getEmployeeDataByDocument(String document){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees()){
			if(document.equals(employee.getDocument())) {
				employeeData = employee;
				return employeeData;
			}
		}
		return this.employeeData;
	}
	
	public EmployeeInfo getEmployeeDataBySSNum(String ssNum){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(ssNum.equals(employee.getSsNumber())) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String nameSurname){
		String name = nameSurname.split(", ")[0];
		String surname = nameSurname.split(", ")[1];
		
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(name.equals(employee.getName()) && surname.equals(employee.getSurName())) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
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

	public void setSSRegime(int ssRegime) {
		if(1 == ssRegime)
			contractData.setSsRegimen((byte) 3);
		else
			contractData.setSsRegimen((byte) ssRegime);
	}
	
	public void setSSRegime(byte ssRegime) {
		contractData.setSsRegimen(ssRegime);
	}
	
	public void setMdTBT(byte mdTBT) {
		contractData.setMdTBT(mdTBT);
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
	
	public void setMdCtzInfo(String mdCtz) {
		contractData.setMdctz(mdCtz);
	}
	
	public void setContractType(String contractType) {
		contractData.setContractType(contractType);
	}
	
	public void setContractModel(Integer ordinal) {
		contractData.setContractModel(ordinal);
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

	public void setEmployeePayMethod(String payMethodType) {
		employeeData.setPayMethodType(payMethodType);
	}
	
	public void setEmployeePayMethodId(Integer paymethodId) {
		this.employeeData.setPaymethodId(paymethodId);
	}
	
	public void setEmployeeBIC(String bic) {
		employeeData.setBic(bic);
	}
	
	public void setEmployeeBankAlias(String bankAlias) {
		employeeData.setBankAlias(bankAlias);
	}

	public void setEmployeeAccount(String rbankAccount) {
		employeeData.setAccount(rbankAccount);
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
