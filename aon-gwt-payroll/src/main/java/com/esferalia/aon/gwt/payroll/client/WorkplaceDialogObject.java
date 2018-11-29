package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceDialogObject {

	private DomainEnterprisesServiceAsync enterprisesService;
	
	private Enterprise enterprise;
	private List<Agreement> agreements;
	
	private WorkplaceInfo workplaceInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public WorkplaceDialogObject(Enterprise enterprise, DomainEnterprisesServiceAsync enterprisesService) {
		this.enterprisesService = enterprisesService;
		
		this.enterprise = enterprise;
		this.workplaceInfo = new WorkplaceInfo();
		this.agreements = new ArrayList<>();
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
		for(Agreement a : getActiveAgreements()){
			if(a.getDescription() == agreementName && a.getId() > 0)
				return a.getId();
		}
		return -1;
	}
	
	public Map<Integer, String> getWorkplaceAddresses(){
		return this.workplaceInfo.getAddresses();
	}
	
	public Map<Integer, String> getWorkplaceScopes(){
		return this.workplaceInfo.getScopes();
	}
	
	public Map<Integer, String> getWorkplacesCalendars(){
		return this.workplaceInfo.getCalendars();
	}
	
	public Map<Integer, String> getWorkplaceActivities(){
		return this.workplaceInfo.getActivities();
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, 0, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = result;
				getEnterpriseAddresses(
						s -> {success.accept(result);},
						f ->{}
				);	
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void getEnterpriseAddresses(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpiseAddresses(this.enterprise.getId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				workplaceInfo.setAddresses(result);
				getEnterpriseCalendars(
					s -> {success.accept(result);},
					f -> {}
				);
			}
		});
	}
	
	private void getEnterpriseCalendars(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpiseCalendars(this.enterprise.getId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				workplaceInfo.setCalendar(result);
				getEnterpriseActivities(
					s -> {success.accept(result);},
					f -> {}
				);	
			}
		});
	}
	
	private void getEnterpriseActivities(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpiseActivities(this.enterprise.getId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				workplaceInfo.setActivities(result);
				getEnterpriseScopes(
						s -> {success.accept(result);},
						f -> {}
					);
			}
		});
	}
	
	private void getEnterpriseScopes(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpiseScopes(this.enterprise.getId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				workplaceInfo.setScopes(result);
				success.accept(result);
			}
		});
	}
	
	public void createWorkplace(Consumer<WorkplaceInfo> success, Consumer<Throwable> failure){
		
		enterprisesService.createWorkplaceInfo(this.workplaceInfo, this.enterprise.getId(), new AsyncCallback<WorkplaceInfo>() {
			
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
	
	public void setWorkplaceScope(Integer scopeId) {
		workplaceInfo.setScopeId(scopeId);
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
