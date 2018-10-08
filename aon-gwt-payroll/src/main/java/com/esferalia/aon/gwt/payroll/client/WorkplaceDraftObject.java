package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceDraftObject {

	private EmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private Workplace workplace;
	private List<Agreement> agreements;
	
	private WorkplaceInfo workplaceInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public WorkplaceDraftObject(Workplace workplace, EmployeesServiceAsync employeesService, DomainEnterprisesServiceAsync enterprisesService) {
		
		this.employeesService = employeesService;
		this.enterprisesService = enterprisesService;

		this.workplace = workplace;
		this.agreements = new ArrayList<>();
		
	}
	
	public WorkplaceInfo getWorkplaceInfo(){
		return this.workplaceInfo;
	}
	
	public List<Agreement> getAgreements(){
		return this.agreements;
	}
	
	public String getWorkplaceAgreementDescription(){
		return this.workplaceInfo.getAgreementDescription();
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
	
	public Map<Integer, String> getWorkplaceAddresses(){
		return this.workplaceInfo.getAddresses();
	}
	
	public Integer getWorkplaceAddressIndex(){
		Integer index = 0;
		for(Integer value : this.workplaceInfo.getAddresses().keySet()){
			if(value.equals(workplaceInfo.getAddressId())) 
				break;
			index ++;
		}
		return index;
	}
	
	public Integer getWorkplaceEconomicConcert(){
		return (int) workplaceInfo.getEconomicConcert() + 1;
	} 
	
	public Map<Integer, String> getWorkplacesCalendars(){
		return this.workplaceInfo.getCalendars();
	}
	
	public Integer getWorkplaceCalendarIndex(){
		Integer index = 0;
		for(Integer value : this.workplaceInfo.getCalendars().keySet()){
			if(value.equals(workplaceInfo.getCalendarId())) 
				break;
			index ++;
		}
		return index;
	}
	
	public Map<Integer, String> getWorkplaceActivities(){
		return this.workplaceInfo.getActivities();
	}
	
	public Integer getWorkplaceActivityIndex(){
		Integer index = 0;
		for(Integer value : this.workplaceInfo.getActivities().keySet()){
			if(value.equals(workplaceInfo.getActivityId())) 
				break;
			index ++;
		}
		return index;
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeWorkplace(Consumer<WorkplaceInfo> success, Consumer<Throwable> failure) {
	
		enterprisesService.getWorkplaceInfo(this.workplace.getId(), new AsyncCallback<WorkplaceInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onSuccess(WorkplaceInfo result) {
				workplaceInfo = result;
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
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	public void updateWorkplace(Consumer<WorkplaceInfo> success, Consumer<Throwable> failure){
		
		enterprisesService.setWorkplaceInfo(this.workplaceInfo, new AsyncCallback<WorkplaceInfo>() {
			
			@Override
			public void onSuccess(WorkplaceInfo result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void setWorkplaceDescription(String description) {
		workplaceInfo.setDescription(description);
	}

	public void setWorkplaceAddress(Integer addressId) {
		workplaceInfo.setAddressId(addressId);
	}

	public void setWorkplaceEconomicConcert(int economicCocncert) {
		workplaceInfo.setEconomicConcert((byte) economicCocncert);
	}

	public void setWorkplaceActive(Boolean active) {
		if (active) workplaceInfo.setActive((byte) 1);
		else workplaceInfo.setActive((byte) 0);
	}

	public void setWorkplaceCalendar(Integer calendarId) {
		workplaceInfo.setCalendarId(calendarId);
	}

	public void setWorkplaceAgreement(Integer agreementId) {
		workplaceInfo.setAgreementId(agreementId);
	}

	public void setWorkplaceActivity(Integer activityId) {
		workplaceInfo.setActivityId(activityId);
	}
	
}
