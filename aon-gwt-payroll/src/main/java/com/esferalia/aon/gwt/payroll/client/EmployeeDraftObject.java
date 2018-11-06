package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
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
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDraftObject {
	private Workplace workplace;
	private Employee employee;
	
	private EmployeeContractInfo employeeContractData;
	private EmployeeInfo employeeData;
	private ContractInfo contractData;
	
	private EmployeeContractInfo new_employeeContractData;
	private EmployeeInfo new_employeeData;
	private ContractInfo new_contractData;
	
	private DomainEmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private List<Agreement> agreements;
	private List<Workplace> workplaces;
	
	private ActivitiesCCC activitiesCCC;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EmployeeDraftObject(Workplace workplace, Employee employee, DomainEmployeesServiceAsync employeesService, DomainEnterprisesServiceAsync enterprisesService) {
		
		this.employeesService = employeesService;
		this.enterprisesService = enterprisesService;

		this.workplace = workplace;
		this.employee = employee;
		
		this.agreements = new ArrayList<>();
		this.workplaces = new ArrayList<>();
		
		this.employeeContractData = new EmployeeContractInfo();
		this.employeeData = new EmployeeInfo();
		this.contractData = new ContractInfo();
		
		this.new_employeeContractData = new EmployeeContractInfo();
		this.new_employeeData = new EmployeeInfo();
		this.new_contractData = new ContractInfo();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------  METHODS LOGIC WINDOW -------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
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
	
	public Integer getWorkplaceIndex() {
		Integer index = 0;
		
		for(Workplace workplace : workplaces){
			if(this.contractData.getWorkplaceId().equals(workplace.getId()))
				break;
			index++;
		}
		
		return index;
	}
	
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
	
	//PERSON TABLE
	
	public Date getEmployeeBirthDate() {
		return this.employeeData.getBirthdate();
	}
	
	public Integer getEmployeeGender() {
		return (int) this.employeeData.getGender();
	}
	
	public Integer getEmployeeAddressStreetTypeIndex(){
		return getStreetTypeIndex(this.employeeData.getStreetType());
	}
	
	private Integer getStreetTypeIndex(String streetType) {
		for(int i=0; i<StreetType.values().length; i++){
			if(streetType == StreetType.values()[i].getShortCode())
				return i;
		}
		return 0;
	}
	
	public String getEmployeeAddress() {
		return this.employeeData.getAddress();
	}
	
	public String getEmployeeAddressNumber() {
		return this.employeeData.getAddresNum();
	}
	
	public String getEmployeeAddressZip() {
		return this.employeeData.getAddressZip();
	}
	
	public String getEmployeeAddressCity() {
		return this.employeeData.getAddressCity();
	}
	
	public Integer getEmployeeAddressProvince() {
		return ProvinceContract.getProvinceIndex(this.employeeData.getAddressProvinces());
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
		case "GIRO":
			return 2;
		case "CHEQUE":
			return 3;
		case "TRANSFERENCIA":
			return 4;
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
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------- AUX METHODS -------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------

	public Map<Integer, String> getActivities() {
		return activitiesCCC.getActivities();
	}
	
	public Map<Integer, CCCInfo> getCCCs() {
		return activitiesCCC.getCccs();
	}
	
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
	
	public List<Workplace> getWorkplaces() {
		return this.workplaces;
	}
	
	public List<Agreement> getAgreements(){
		return this.agreements;
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
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() > 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
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
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(this.employee.getId(), new AsyncCallback<EmployeeContractInfo>() {
			
			@Override
			public void onSuccess(EmployeeContractInfo result) {
				employeeContractData = result;
				employeeData = result.getEmployeeInfo();
				contractData = result.getContractInfo();
				
				new_employeeContractData = result;
				new_employeeData = result.getEmployeeInfo();
				new_contractData = result.getContractInfo();

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
		enterprisesService.getAgreements(0, 0, new AsyncCallback<List<Agreement>>() {
			
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
		enterprisesService.getActivitiesCCC(workplace.getId(), new AsyncCallback<ActivitiesCCC>() {
			
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
		enterprisesService.getWorkplaces(workplace.getId(), new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> result) {
				workplaces = result;
				success.accept(result);	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void updateEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
		new_employeeContractData.setContractInfo(new_contractData);
		new_employeeContractData.setEmployeeInfo(new_employeeData);
		
		employeesService.setEmployeeInfoDataBase(this.new_employeeContractData, new AsyncCallback<EmployeeContractInfo>() {
			
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
	
	public void setEmployeeDocument(String document) {
		new_employeeData.setDocument(document);
	}
	
	public void setEmployeeDocumentType(String document_type) {
		if(document_type == "DNI")
			new_employeeData.setDocumentType((byte) 0);
		else if(document_type == "CIF")
			new_employeeData.setDocumentType((byte) 1);
		else if(document_type == "Pasaporte")
			new_employeeData.setDocumentType((byte) 3);
		else
			new_employeeData.setDocumentType((byte) 0);
	}
	
	public void setNationality(String nationality) {
		new_employeeData.setNationality(nationality);	
	}
	
	public void setEmployeeSocialSecurityNum(String social_security_num) {
		new_employeeData.setSsNumber(social_security_num);
	}
	
	public void setEmployeeName(String name) {
		new_employeeData.setName(name);
	}
	
	public void setEmployeeFirstSurname(String first_surname) {
		new_employeeData.setSurName(first_surname);
	}

	public void setEmployeeSecondSurname(String second_surname) {
		new_employeeData.setSecondSurName(second_surname);
	}
	
	public void setSSRegime(int ssRegime) {
		if(1 == ssRegime)
			new_contractData.setSsRegimen((byte) 3);
		else
			new_contractData.setSsRegimen((byte) ssRegime);
	}
	
	public void setContractActivityId(Integer activityID) {
		new_contractData.setActivityId(activityID);
	}

	public void setContractCCCId(Integer cccId) {
		new_contractData.setCccId(cccId);
	}

	public void setContractCCCType(byte cccType) {
		new_contractData.setCccType(cccType);
	}
	
	public void setContractWorkplaceId(Integer workplaceId) {
		new_contractData.setWorkplaceId(workplaceId);
	}
	
	public void setContractType(String contract_type) {
		new_contractData.setContractType(contract_type);
	}
	
	public void setContractModel(Integer contractModelId) {
		//Falta buscar en ModelOption el String correspondiente a ese ID
		new_contractData.setContractModel(contractModelId);	
	}
	
	public void setContractStartDate(Date start_date) {
		new_contractData.setStartDate(start_date);		
	}
	
	public void setContractSeniorityDate(Date seniority_date) {
		new_contractData.setSeniorityDate(seniority_date);		
	}
	
	public void setContractEndDate(Date end_date) {
		new_contractData.setEndDate(end_date);		
	}
	
	public void setContractAgreementId(Integer agreement_table_id) {
		new_contractData.setAgreementId(agreement_table_id);
	}
	
	public void setContractAgreementLevelId(Integer agreement_level_table_id) {
		new_contractData.setAgreementLevelId(agreement_level_table_id);
	}
	
	public void setContractCategory(String category_description) {
		new_contractData.setAgreementCategory(category_description);		
	}
	
	public void setContractQuoteGroup(Integer quoteGroupIndex) {
		String quoteGroup = getQuoteByIndex(quoteGroupIndex);
		new_contractData.setQuoteGroup(quoteGroup);		
	}
	
	public void setContractOccupation(Integer occupationIndex) {
		String contractOccupation = getOcupationByIndex(occupationIndex);
		new_contractData.setOcupation(contractOccupation);		
	}
	
	public void setContractJourneyType(Boolean journey_type) {
		new_contractData.setJourneyType(journey_type ? (byte) 1 : (byte) 0);
	}
	
	// EMPLOYEE
	
	public void setEmployeeBirthDate(Date birth_date) {
		new_employeeData.setBirthdate(birth_date);
	}

	public void setEmployeeGender(int gender) {
		new_employeeData.setGender((byte) gender);
	}
	
	public void setEmployeeGenderNull() {
		new_employeeData.setGender(null);
	}

	public void setEmployeeStreetType(String shortCode) {
		new_employeeData.setStreetType(shortCode);
	}
	
	public void setEmployeeAddress(String address) {
		new_employeeData.setAddress(address);
	}

	public void setEmployeeAddressNumber(String address_number) {
		new_employeeData.setAddresNum(address_number);
	}

	public void setEmployeeAddressZip(String zip_code) {
		new_employeeData.setAddressZip(zip_code);
	}

	public void setEmployeeAddressCity(String location) {
		new_employeeData.setAddressCity(location);
	}

	public void setEmployeeAddressProvince(String province) {
		new_employeeData.setAddressProvinces(province);
	}

	public void setEmployeeMobile(String mobile) {
		new_employeeData.setMobile(mobile);
	}
	
	public void setEmployeePhone(String phone) {
		new_employeeData.setPhone(phone);
	}

	public void setEmployeeEmail(String mobile) {
		new_employeeData.setEmail(mobile);
	}

	public void setEmployeePayMethod(String payMethodType) {
		new_employeeData.setPayMethodType(payMethodType);
	}

	public void setEmployeeAccount(String rbankAccount) {
		new_employeeData.setAccount(rbankAccount);
	}

	public void setEmployeeBIC(String rbankBIC) {
		new_employeeData.setBic(rbankBIC);
	}

}
