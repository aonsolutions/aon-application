package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.common.shared.SocialSecurity;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDialogObject {
	private Workplace workplace;
	
	private EmployeeContractInfo employeeContractData;
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private DomainEmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private List<Agreement> agreements;
	private WorkplaceEmployees workplaceEmployees;
	
	private List<Workplace> workplaces;
	private ActivitiesCCC activitiesCCC;
	
	public EmployeeDialogObject(Workplace workplace, DomainEmployeesServiceAsync employeesService,
			DomainEnterprisesServiceAsync enterprisesService) {
		super();
		this.workplace = workplace;
		this.employeesService = employeesService;
		this.enterprisesService = enterprisesService;
		this.workplaces = new ArrayList<>();
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
	}
	

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------  METHODS ------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// TABLA DATOS CONTRATO
	
	public EmployeeInfo getEmployeeData() {
		return this.employeeData;
	}
	
	public ContractInfo getContractData() {
		return this.contractData;
	}
	
	public EmployeeInfo getEmployeeDataByDocument(String document){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(document == employee.getDocument()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
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
	
	public EmployeeInfo getEmployeeDataBySSNum(String ssNum){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(ssNum == employee.getSsNumber()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
	public boolean checkSSNumValidation(String ssNum_string) {
		SocialSecurity ss = new SocialSecurity(ssNum_string);
		if(ss.checkSS())
			return true;
		else
			return false;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String name, String surname){
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(name == employee.getName() && surname == employee.getSurName()) {
				employeeData = employee;
				return employeeData;
			}
		return this.employeeData;
	}
	
//	public EmployeeInfo getEmployeeDataBySurName(String surName){
//		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
//			if(surName == employee.getSurName()) {
//				employeeData = employee;
//				return employeeData;
//			}
//		return this.employeeData;
//	}
	
	public Integer getActivityIdByName(String activityStr) {
		for(Entry<Integer, String> activityEntry : activitiesCCC.getActivities().entrySet()){
			if(activityStr.equals(activityEntry.getValue()))
				return activityEntry.getKey();
		}
		return null;
	}
	
	public Integer getCCCIdByNumber(String cccStr, byte cccType, String cccGeozoneStr) {
		CCCInfo cccInfo = getCCCInfo(cccStr, cccType, cccGeozoneStr);
		if(null == cccInfo)
			return null;
		else{
			for(Entry<Integer, CCCInfo> cccEntry : activitiesCCC.getCccs().entrySet())
				if(cccEntry.getValue().equals(cccInfo)){
					return cccEntry.getKey();
				}
			return null;
		}
	}
	
	private CCCInfo getCCCInfo(String cccStr, byte cccType, String cccGeozoneStr) {
		for(CCCInfo cccInfo : activitiesCCC.getCccs().values()){
			if(cccStr.equals(cccInfo.getCcc()) && cccType == cccInfo.getType() && cccGeozoneStr.equals(cccInfo.getGeozone()))
				return cccInfo;
		}
		return null;
	}
	
	public Integer getWorkplaceIdByName(String workplaceName) {
		for(Workplace workplace : workplaces)
			if(workplaceName.equals(workplace.getDescription()))
				return workplace.getId();
		
		return null;
	}
	
	public List<Agreement> getAgreements(){
		return this.agreements;
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
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------  METHODS LOGIC WINDOW -------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	public Date getContractOldStartDate() {
		return this.contractData.getOldStartDate();
	}
	
	public void setOldContractStartDate(Date startDate) {
		this.contractData.setOldStartDate(startDate);;
	}
	
	public Date getContractOldEndDate() {
		return this.contractData.getOldEndDate();
	}
	
	public void setOldContractEndDate(Date endDate) {
		this.contractData.setOldEndDate(endDate);;
	}
	
	//CONTRACT TABLE
	
		public String getEmployeeDocument(){
			return this.employeeData.getDocument();
		}
		
		public String getEmployeeNationality() {
			return getNationality(this.employeeData.getNationality());
		}
		
		private String getNationality(String iso2) {
			for (int i = 0; i < Country.values().length; i++) {
				if (Country.values()[i].getIso2() == iso2)
					return Country.values()[i].getName();
			}
			return null;
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
		
		public Integer getContractSSRegimen() {
			return (int)this.contractData.getSsRegimen();
		}
		
		public Integer getContractActivityCCC() {
			return getActivityAccountIndex();
		}
		
		public Integer getActivityAccountIndex() {
			if(null == this.contractData.getActivityId())
				return 0;
			
			Integer index = 0;
			
			if(null == this.contractData.getCccId())
				return 0;
			
			for(Entry<Integer,String> entry : getActivities().entrySet())
				if(entry.getKey().equals(this.contractData.getActivityId()))
					for(CCCInfo cccInfo :  getCCCsByActivity(entry.getKey()))
						if(cccInfo.getCCCId().equals(this.contractData.getCccId())) {
							index++;
							return index;
						}else
							index++;
				else
					index += getCCCsByActivity(entry.getKey()).size();
				
			return 0;
		}
		
		private List<CCCInfo> getCCCsByActivity(Integer activityId) {
			List<CCCInfo> cccs = new ArrayList<>();
			for(CCCInfo cccInfo : getCCCs().values())
				if(cccInfo.getActivityId().equals(activityId))
					cccs.add(cccInfo);
			return cccs;
		}

		public Integer getContractWorkplace() {
			return getWorkplaceIndex();
		}
		
//		public Integer getWorkplaceIndex() {
//			Integer index = 0;
//			
//			for(Workplace workplace : workplaces){
//				if(this.contractData.getWorkplaceId().equals(workplace.getId()))
//					break;
//				index++;
//			}
//			
//			return index;
//		}
		
		public Integer getContractType() {
			return Integer.parseInt(this.contractData.getContractType());
		}
		
		public Integer getContractModel() {
			return this.contractData.getContractModel();
		}
		
		public Date getContractStartDate() {
			return this.contractData.getStartDate();
		}
		
		public Date getContractSeniorityDate() {
			return this.contractData.getSeniorityDate();
		}
		
		public Date getContractEndDate() {
			return this.contractData.getEndDate();
		}
		
		public Integer getContractAgreementId() {
			return this.contractData.getAgreementId();
		}
		
		public Integer getContractAgreement() {
			return getAgreementIndex(this.contractData.getAgreementId());
		}
		
		public Integer getAgreementIndex(Integer agreementId){
			if(null == agreementId)
				return -1;
			else {
				List<Agreement> activeAgreements = getActiveAgreements();
				for(int i = 0; i< activeAgreements.size(); i++) {
					if(activeAgreements.get(i).getId().equals(agreementId))
						return i;
				}	
				return -1;
			}
		}
		
		public Integer getAgreementLevel() {
			return getAgreementLevelCategoryIndex(this.contractData.getAgreementId(), this.contractData.getAgreementLevelId(), this.contractData.getAgreementCategory());
		}
		
		private Integer getAgreementLevelCategoryIndex(Integer agreementId, Integer agreementLevelId, String category_description) {
			if (null == agreementId || null == agreementLevelId || category_description == "")
				return -1;

			Integer result = 0;
			for (Agreement a : getActiveAgreements()) {
				if (a.getId() > 0 && a.getId().equals(agreementId)) {
					Set<Level> levels = a.getLevels();
					for (Level levelRecord : levels) {
						Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
						for (String categoryRecord : categories) {
							if (levelRecord.getId().equals(agreementLevelId) && categoryRecord == category_description)
								return result;
							else
								result++;
						}
					}
				}
			}

			result = 0;
			for (Agreement a : getActiveAgreements()) {
				if (a.getId() > 0 && a.getId().equals(agreementId)) {
					Set<Level> levels = a.getLevels();
					for (Level levelRecord : levels) {
						Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
						for (String categoryRecord : categories) {
							if (levelRecord.getId().equals(agreementLevelId))
								return result;
							else
								result++;
						}
					}
				}
			}
			return -1;
		}
		
		public String getContractAgreementCategory() {
			return this.contractData.getAgreementCategory();
		}
		
		public Integer getContractQuoteGroup() {
			return Integer.parseInt(this.contractData.getQuoteGroup());
		}
		
		public Integer getContractOcupation() {
			return getCharIndex(this.contractData.getOcupation());
		}
		
		private int getCharIndex(String ocupation) {
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
		
		public Integer getContractJourneyType() {
			return (int)this.contractData.getJourneyType();
		}
	
	public WorkplaceEmployees getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public Map<Integer, String> getActivities() {
		return activitiesCCC.getActivities();
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
	
	public String getWorkplaceName(){
		return this.workplace.getDescription();
	}
	
	public Integer getAgreementIndex(String agreementDescription){
		List<Agreement> activeAgreements = getActiveAgreements();
		for(int i = 0; i<activeAgreements.size(); i++)
			if(activeAgreements.get(i).getDescription() == agreementDescription)
				return i;
		return -1;
	}
	
	public String getWorkplaceAgreement(){
		if(null == this.workplace.getAgreement())
			return "";
		else
			return this.workplace.getAgreement().getDescription();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------  DATABASE METHODS SYNC -------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	public void initializeEmployee(Integer contractId, Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(contractId, new AsyncCallback<EmployeeContractInfo>() {
			
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
	
	public void createEmployeeContract(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
		employeeContractData.setEmployeeInfo(employeeData);
		employeeContractData.setContractInfo(contractData);
		
		employeesService.createEmployeeContract(employeeContractData, new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
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
	
	// EMPLOYEE TABLE
		
	public void setEmployeeBirthDate(Date birth_date) {
		employeeData.setBirthdate(birth_date);
	}

	public void setEmployeeGender(int gender) {
		employeeData.setGender((byte) gender);
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
	
	public void setEmployeeBIC(String bic) {
		employeeData.setBic(bic);
	}

	public void setEmployeeAccount(String rbankAccount) {
		employeeData.setAccount(rbankAccount);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------- AUXILIAR METHODS --------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------

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
		
}
