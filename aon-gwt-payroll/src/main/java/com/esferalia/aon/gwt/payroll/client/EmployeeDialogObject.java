package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.common.shared.SocialSecurity;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDialogObject {
	private Workplace workplace;
	private Employee employee;
	
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private EmployeeInfoDataBase employeeInfo;
	private EmployeeInfoDataBase newEmployeeInfo;
	
	private DomainEmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private List<Agreement> agreements;
	private WorkplaceEmployees workplaceEmployees;
	
	List<Workplace> workplaces;
	ActivitiesCCC activitiesCCC;
	
	public EmployeeDialogObject(Workplace workplace, DomainEmployeesServiceAsync employeesService,
			DomainEnterprisesServiceAsync enterprisesService) {
		super();
		this.workplace = workplace;
		this.employeesService = employeesService;
		this.enterprisesService = enterprisesService;
		this.workplaces = new ArrayList<>();
		this.newEmployeeInfo = new EmployeeInfoDataBase();
		
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
	}
	
	public String getWorkplaceName(){
		return this.workplace.getDescription();
	}
	
	public Integer getWorkplaceIndex(){
		Integer index = 0;
		String workplaceName = getWorkplaceName();
		
		for(Workplace workplace : workplaces) {
			if(workplaceName.equals(workplace.getDescription()))
				return index;
			index++;
		}
		
		index = 0;
		return index;
	}
	
	public String getWorkplaceActivity(){
		if(this.workplace.getActivity() == null)
			return "";
		else
			return this.workplace.getActivity().getDescription();
	}
	
	public String getWorkplaceCCC(){
		if(null == this.workplace.getActivity())
			return "";
		else{
			if(this.workplace.getActivity().getCccs() == null)
				return "";
			else
				return this.workplace.getActivity().getCccs().get(0).getCode();
		}
	}
	
	public String getWorkplaceAgreement(){
		if(null == this.workplace.getAgreement())
			return "";
		else
			return this.workplace.getAgreement().getDescription();
	}
	
	public WorkplaceEmployees getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public EmployeeInfo getEmployeeDataByDocument(String document){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(document == employee.getDocument()) {
				employeeData = employee;
				return employeeData;
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
	
	public EmployeeInfo getEmployeeDataByName(String name){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(name == employee.getName()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
	public EmployeeInfo getEmployeeDataBySurName(String surName){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(surName == employee.getSurName()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
	
	public Employee getEmployee(){
		return this.employee;
	}
	
	public String getNewEmployeeInfo(){
		String result = "";
		result += newEmployeeInfo.getName() + ", ";
		result += newEmployeeInfo.getFirst_surname() + ", ";
		result += newEmployeeInfo.getSecond_surname() + ", ";
		result += newEmployeeInfo.getDocument() + ", ";
		result += newEmployeeInfo.getNationality() + ", ";
		result += newEmployeeInfo.getSocial_security_num() + ", ";
		result += newEmployeeInfo.getBirth_date() + ", ";
		result += newEmployeeInfo.getGender() + ", ";
		result += newEmployeeInfo.getAddress() + ", ";
		result += newEmployeeInfo.getAddress_number() + ", ";
		result += newEmployeeInfo.getZip_code() + ", ";
		result += newEmployeeInfo.getLocality() + ", ";
		result += newEmployeeInfo.getProvince() + ", ";
		result += newEmployeeInfo.getMobile() + ", ";
		result += newEmployeeInfo.getPhone() + ", ";
		result += newEmployeeInfo.getEmail() + ", ";
		result += newEmployeeInfo.getTypePayMethod() + ", ";
		result += newEmployeeInfo.getBankAccount() + ", ";
		result += newEmployeeInfo.getBIC();
		return result;
	}
	
	public String getNewEmployeeContractInfo(){
		String result = "";
		result += newEmployeeInfo.getEnterprise_activity() + ", ";
		result += newEmployeeInfo.getQuote_account() + ", ";
		result += newEmployeeInfo.getWorkplace() + ", ";
		result += newEmployeeInfo.getContract_type() + ", ";
		result += newEmployeeInfo.getContract_model() + ", ";
		result += newEmployeeInfo.getStart_date() + ", ";
		result += newEmployeeInfo.getEnd_date() + ", ";
		result += newEmployeeInfo.getSeniority_date() + ", ";
		result += newEmployeeInfo.getAgreement() + ", ";
		result += newEmployeeInfo.getAgreement_level() + ", ";
		result += newEmployeeInfo.getCategory_description() + ", ";
		result += newEmployeeInfo.getQuote_group() + ", ";
		result += newEmployeeInfo.getOcupation() + ", ";
		result += newEmployeeInfo.getJourneyType();
		return result;
	}
	
	public List<Agreement> getAgreements(){
		return this.agreements;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() > 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
	}
	
	public Integer getAgreementIndex(String agreementDescription){
		List<Agreement> activeAgreements = getActiveAgreements();
		for(int i = 0; i<activeAgreements.size(); i++)
			if(activeAgreements.get(i).getDescription() == agreementDescription)
				return i;
		return -1;
	}
	
	public Integer getAgreementId(String agreementName){
		for(Agreement a : getAgreements()){
			if(a.getDescription() == agreementName && a.getId() > 0)
				return a.getId();
		}
		return -1;
	}
	
	public Integer getAgreementLevelId(String agreementName, String agreementLevelName) {
		String levelDescription = (agreementLevelName == null || agreementLevelName == "-") ? null : agreementLevelName.split(" ")[0];
		for(Agreement a : getAgreements()){
			if(a.getDescription() == agreementName && a.getId() > 0)
				for(Level level : a.getLevels()){
					if(level.getId() > 0 && level.getDescription() == levelDescription){
						return level.getId();
					}
				}
		}
		return -1;
	}		

	
	public Set<Level> getLevels(Integer agreementId){
		for(Agreement a : getAgreements()){
			if(a.getId() == agreementId){
				return a.getLevels();
			}	
		}
		return new HashSet<>();
	}
	
	public Map<Integer, Set<String>> getCategories(Integer agreementId, Integer levelId){
		for(Agreement a : getAgreements()){
			if(a.getId() == agreementId){
				return a.getCategoriesMap();
			}	
		}
		return null;
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
	
//	public Integer getAddressesNumb(){
//		return this.addresses.getAddresses().size();
//	}

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	public void getWorkplaceEmployees(Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure) {
		employeesService.getWorkplaceEmployees(workplace.getId(), new AsyncCallback<WorkplaceEmployees>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				workplaceEmployees = result;
				getAgreements(
						r ->{success.accept(result);},
						f->{}
				);
			}
		});
	}
	
	public void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, 0, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = result;
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
		enterprisesService.getWorkplaces(workplace.getId(), new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> result) {
				workplaces = result;
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
		enterprisesService.getActivitiesCCC(workplace.getId(), new AsyncCallback<ActivitiesCCC>() {
			
			@Override
			public void onSuccess(ActivitiesCCC result) {
				activitiesCCC = result;
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void createEmployeeContract(Consumer<EmployeeInfoDataBase> success, Consumer<Throwable> failure){
		employeesService.createEmployeeContract(this.newEmployeeInfo, new AsyncCallback<EmployeeInfoDataBase>() {
			
			@Override
			public void onSuccess(EmployeeInfoDataBase result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------- SETTERS NEW EMPLOYEE INFO -------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// CONTRACT
	
	public void setContractTableId(Integer personIdNum) {
		newEmployeeInfo.setContract_table_id(personIdNum);
	}
	
	public void setEnterprise_Activity(String activity) {
		newEmployeeInfo.setEnterprise_activity(activity);
	}
	
	public void setContractQuoteAccount(String quote_account) {
		newEmployeeInfo.setQuote_account(quote_account);	
	}
	
	public void setWorkplace(String workplace) {
		newEmployeeInfo.setWorkplace(workplace);
	}
	
	public void setContractType(String contract_type) {
		contractData.setContractType(contract_type);
	}
	
	public void setContractModel(Integer contractModelId) {
		//Falta buscar en ModelOption el String correspondiente a ese ID
		//contractData.setContractModel(contractModelId);	
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

	public void setContractAgreementDescription(String agreement) {
		newEmployeeInfo.setAgreement(agreement);	
	}
	
	public void setContractCategory(String category_description) {
		contractData.setAgreementCategory(category_description);		
	}
	
	public void setContractAgreementLevelId(Integer agreement_level_table_id) {
		contractData.setAgreementLevelId(agreement_level_table_id);
	}

	public void setContractAgreementLevelDescription(String agreement_level) {
		newEmployeeInfo.setAgreement_level(agreement_level);		
	}
	
	public void setContractQuoteGroup(Integer quoteGroupIndex) {
		String quoteGroup = getQuoteByIndex(quoteGroupIndex);
		contractData.setQuoteGroup(quoteGroup);		
	}
	
	public void setContractOccupation(Integer occupationIndex) {
		String contractOccupation = getOcupationByIndex(occupationIndex);
		contractData.setOcupation(contractOccupation);		
	}

	public void setContractJourneyType(Boolean journey_type) {
		contractData.setJourneyType(journey_type ? (byte) 1 : (byte) 0);
	}
	
	// EMPLOYEE
	
	public void setEmployeeDocument(String document) {
		employeeData.setDocument(document);
	}

	public void setEmployeeDocumentType(String document_type) {
		if(document_type == "DNI")
			employeeData.setDocumentType((byte) 0);
		else if(document_type == "CIF")
			employeeData.setDocumentType((byte) 1);
		else if(document_type == "Pasaporte")
			employeeData.setDocumentType((byte) 3);
	}
	
	public void setNationality(String nationality) {
		employeeData.setNationality(nationality);	
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

	public void setEmployeeBirthDate(Date birth_date) {
		employeeData.setBirthdate(birth_date);
	}

	public void setEmployeeGender(int gender) {
		employeeData.setGender((byte) gender);
	}
	
	public void setEmployeeGenderNull() {
		newEmployeeInfo.setGender(null);
	}

	public void setEmployeeSocialSecurityNum(String social_security_num) {
		employeeData.setSsNumber(social_security_num);
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

	public void setEmployeeAddressZip(String zip_code) {
		employeeData.setAddressZip(zip_code);
	}

	public void setEmployeeAddressCity(String city) {
		employeeData.setAddressCity(city);
	}

	public void setEmployeeAddressProvince(String province) {
		employeeData.setAddressProvinces(province);
//		newEmployeeInfo.setGeozone_name(province);
	}

	public void setEmployeePhone(String phone) {
		employeeData.setPhone(phone);
	}

	public void setEmployeeMobile(String mobile) {
		employeeData.setMobile(mobile);
	}

	public void setEmployeeEmail(String email) {
		employeeData.setEmail(email);
	}

	public void setEmployeePayMethod(String payMethodType) {
		employeeData.setPayMethodType(payMethodType);
	}

	public void setEmployeeAccount(String rbankAccount) {
		employeeData.setAccount(rbankAccount);
	}

	public void setEmployeeBIC(String bic) {
		employeeData.setBic(bic);
	}

	public void setSSRegime(int ssRegime) {
		contractData.setSsRegimen((byte) ssRegime);
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

	public boolean checkDocumentValidation(String document_type_string, String document_string) {
		if("DNI".equals(document_type_string)){
			Dni dni = new Dni(document_string);
			if(dni.checkDNI())
				return true;
			else
				return false;
		}else {
			return true;
		}
	}

	public boolean checkSSNumValidation(String ssNum_string) {
		SocialSecurity ss = new SocialSecurity(ssNum_string);
		if(ss.checkSS())
			return true;
		else
			return false;
	}

	public Integer getWorkplaceIdByName(String workplaceName) {
		for(Workplace workplace : workplaces)
			if(workplaceName.equals(workplace.getDescription()))
				return workplace.getId();
		
		return null;
	}
	
	public void setContractWorkplaceId(Integer workplaceId) {
		contractData.setWorkplaceId(workplaceId);
	}
	
	private String getQuoteByIndex(Integer index) {
		if(null == index)
			return null;
		
		switch (index) {
		case 1:
			return "\"01\"";
		case 2:
			return "\"02\"";
		case 3:
			return "\"03\"";
		case 4:
			return "\"04\"";
		case 5:
			return "\"05\"";
		case 6:
			return "\"06\"";
		case 7:
			return "\"07\"";
		case 8:
			return "\"08\"";
		case 9:
			return "\"09\"";
		case 10:
			return "\"10\"";
		case 11:
			return "\"11\"";
		default:
			return null;
		}
	}
	
	private String getOcupationByIndex(Integer index) {
		if(null == index)
			return null;
		
		switch (index) {
		case 1:
			return "\"a\"";
		case 2:
			return "\"b\"";
		case 3:
			return "\"d\"";
		case 4:
			return "\"e\"";
		case 5:
			return "\"f\"";
		case 6:
			return "\"g\"";
		case 7:
			return "\"h\"";
		default:
			return null;
		}
	}

	public Integer getActivityIdByName(String activityStr) {
		for(Entry<Integer, String> activityEntry : this.employeeInfo.getEnterpriseActivities().entrySet())
			if(activityStr.equals(activityEntry.getValue()))
				return activityEntry.getKey();
		
		return null;
	}

	public Integer getCCCIdByNumber(String cccStr, byte cccType, String cccGeozoneStr) {
		CCCInfo cccInfo = getCCCInfo(cccStr, cccType, cccGeozoneStr);
		if(null == cccInfo)
			return null;
		else{
			for(Entry<Integer, CCCInfo> cccEntry : this.employeeInfo.getCCCs().entrySet())
				if(cccEntry.getValue().equals(cccInfo)){
					return cccEntry.getKey();
				}
			return null;
		}
	}
	
	private CCCInfo getCCCInfo(String cccStr, byte cccType, String cccGeozoneStr) {
		for(CCCInfo cccInfo : this.employeeInfo.getCCCs().values()){
			if(cccStr.equals(cccInfo.getCcc()) && cccType == cccInfo.getType() && cccGeozoneStr.equals(cccInfo.getGeozone()))
				return cccInfo;
		}
		return null;
	}
	
	private Integer getStreetTypeIndex(String streetType) {
		for(int i=0; i<StreetType.values().length; i++){
			if(streetType == StreetType.values()[i].getShortCode())
				return i;
		}
		return -1;
	}
	
	
		
}
