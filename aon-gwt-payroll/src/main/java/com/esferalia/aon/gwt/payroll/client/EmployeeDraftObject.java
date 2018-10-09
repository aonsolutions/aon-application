package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeDraftObject {

	private Employee employee;
	
	private EmployeeInfoDataBase employeeInfo;
	private EmployeeInfoDataBase newEmployeeInfo;
	
	private DomainEmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private List<Agreement> agreements;
	
	public UndoManager<Undoable> undoManager;
	
	// --------------------------------------------- INTERFACE REDO/UNDO -----------------------------------------------
	
	@SuppressWarnings("unused")
	private class CompositeUndoable<T extends Undoable > implements Undoable {

		private Collection<T> undos;

		public CompositeUndoable(Collection<T> undos) {
			this.undos = undos;
		}

		@Override
		public void redo() {
			for (T undo : undos)
				undo.redo();
		}

		@Override
		public void undo() {
			for (T undo : undos){
				undo.undo();
			}
		}

	}
	
	class SetHourEdit implements Undoable {

		private Double oldHour;
		private Double newHour;
		private Date day;
		
		public SetHourEdit(Double oldH, Double newH, Date actualDay) {
			this.oldHour = oldH;
			this.newHour = newH;
			this.day = actualDay;
		}
		
		@Override
		public void undo() {
			//if (oldHour == null)
				//draftMapDaysHour.remove(day);
			//else
				//draftMapDaysHour.put(day, oldHour);	
		}
		
		@Override
		public void redo() {
			//draftMapDaysHour.put(day, newHour);
		}
		
	}
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EmployeeDraftObject(Employee employee, DomainEmployeesServiceAsync employeesService, DomainEnterprisesServiceAsync enterprisesService) {
		
		this.undoManager = new UndoManager<>();
		this.employeesService = employeesService;
		this.enterprisesService = enterprisesService;

		this.employee = employee;
		
		this.employeeInfo = new EmployeeInfoDataBase();
		this.newEmployeeInfo = null;
		
		this.agreements = new ArrayList<>();
	}
	
	public Employee getEmployee(){
		return this.employee;
	}
	
	public EmployeeInfoDataBase getEmployeeInfo(){
		return this.employeeInfo;
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

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeEmployee(Consumer<EmployeeInfoDataBase> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeInfoDataBase(this.employee.getId(), new AsyncCallback<EmployeeInfoDataBase>() {
			
			@Override
			public void onSuccess(EmployeeInfoDataBase result) {
				employeeInfo = result;
				newEmployeeInfo = new EmployeeInfoDataBase(employeeInfo);
//				success.accept(result); //TODO: BORRAR
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
				success.accept(result);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public void updateEmployee(Consumer<EmployeeInfoDataBase> success, Consumer<Throwable> failure){
		employeesService.setEmployeeInfoDataBase(this.newEmployeeInfo, new AsyncCallback<EmployeeInfoDataBase>() {
			
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
	
	public void setContractQuoteAccount(String quote_account) {
		newEmployeeInfo.setQuote_account(quote_account);	
	}
	
	public void setContractType(String contract_type) {
		newEmployeeInfo.setContract_type(contract_type);
	}
	
	public void setContractModel(Integer contractModelId) {
		//Falta buscar en ModelOption el String correspondiente a ese ID
		newEmployeeInfo.setContract_model(contractModelId);	
	}
	
	public void setContractStartDate(Date start_date) {
		newEmployeeInfo.setStart_date(start_date);		
	}
	
	public void setContractEndDate(Date end_date) {
		newEmployeeInfo.setEnd_date(end_date);		
	}
	
	public void setContractSeniorityDate(Date seniority_date) {
		newEmployeeInfo.setSeniority_date(seniority_date);		
	}
	
	public void setContractAgreementId(Integer agreement_table_id) {
		newEmployeeInfo.setAgreement_table_id(agreement_table_id);
	}

	public void setContractAgreementDescription(String agreement) {
		newEmployeeInfo.setAgreement(agreement);	
	}
	
	public void setContractCategory(String category_description) {
		newEmployeeInfo.setCategory_description(category_description);		
	}
	
	public void setContractAgreementLevelId(Integer agreement_level_table_id) {
		newEmployeeInfo.setAgreement_level_table_id(agreement_level_table_id);
	}

	public void setContractAgreementLevelDescription(String agreement_level) {
		newEmployeeInfo.setAgreement_level(agreement_level);		
	}
	
	public void setContractQuoteGroup(String quote_group) {
		newEmployeeInfo.setQuote_group(quote_group);		
	}
	
	public void setContractOccupation(String ocupation) {
		newEmployeeInfo.setOcupation(ocupation);		
	}

	public void setContractJourneyType(Boolean journey_type) {
		newEmployeeInfo.setJourneyType(journey_type);
	}
	
	// EMPLOYEE
	
	public void setEmployeeDocument(String document) {
		newEmployeeInfo.setDocument(document);
	}

	public void setEmployeeDocumentType(String document_type) {
		if(document_type == "DNI")
			newEmployeeInfo.setDocument_type((byte) 0);
		else if(document_type == "CIF")
			newEmployeeInfo.setDocument_type((byte) 1);
		else if(document_type == "Pasaporte")
			newEmployeeInfo.setDocument_type((byte) 3);
	}
	
	public void setNationality(String nationality) {
		newEmployeeInfo.setNationality(nationality);	
	}

	public void setEmployeeName(String name) {
		newEmployeeInfo.setName(name);
	}

	public void setEmployeeFirstSurname(String first_surname) {
		newEmployeeInfo.setFirst_surname(first_surname);
	}

	public void setEmployeeSecondSurname(String second_surname) {
		newEmployeeInfo.setSecond_surname(second_surname);
	}

	public void setEmployeeBirthDate(Date birth_date) {
		newEmployeeInfo.setBirth_date(birth_date);
	}

	public void setEmployeeGender(int gender) {
		newEmployeeInfo.setGender((byte) gender);
	}
	
	public void setEmployeeGenderNull() {
		newEmployeeInfo.setGender(null);
	}

	public void setEmployeeSocialSecurityNum(String social_security_num) {
		newEmployeeInfo.setSocial_security_num(social_security_num);
	}

	public void setEmployeeStreetType(String shortCode) {
		newEmployeeInfo.setStreetType(shortCode);
	}
	
	public void setEmployeeAddress(String address) {
		newEmployeeInfo.setAddress(address);
	}

	public void setEmployeeAddressNumber(String address_number) {
		newEmployeeInfo.setAddress_number(address_number);
	}

	public void setEmployeeAddressZip(String zip_code) {
		newEmployeeInfo.setZip_code(zip_code);
	}

	public void setEmployeeAddressLacality(String location) {
		newEmployeeInfo.setLocality(location);
	}

	public void setEmployeeAddressProvince(String province) {
		newEmployeeInfo.setProvince(province);
		newEmployeeInfo.setGeozone_name(province);
	}

	public void setEmployeePhone(String phone) {
		newEmployeeInfo.setPhone(phone);
	}

	public void setEmployeeMobile(String mobile) {
		newEmployeeInfo.setMobile(mobile);
	}

	public void setEmployeeEmail(String mobile) {
		newEmployeeInfo.setEmail(mobile);
	}

	public void setEmployeePayMethod(String payMethodType) {
		newEmployeeInfo.setTypePayMethod(payMethodType);
	}

	public void setEmployeeAccount(String rbankAccount) {
		newEmployeeInfo.setBankAccount(rbankAccount);
	}

	public void setEmployeeBIC(String rbankBIC) {
		newEmployeeInfo.setBIC(rbankBIC);
	}
	
	
}
