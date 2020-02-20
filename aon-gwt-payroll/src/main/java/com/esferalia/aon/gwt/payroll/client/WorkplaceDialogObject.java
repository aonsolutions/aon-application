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
	
	private Map<Integer, String> addresses;
	private Map<Integer, String> calendars;
	private List<Agreement> agreements;
	private Map<Integer, String> activities;
	
	private WorkplaceInfo workplaceInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public WorkplaceDialogObject(Enterprise enterprise, DomainEnterprisesServiceAsync enterprisesService) {
		this.enterprisesService = enterprisesService;
		
		this.enterprise = enterprise;
		this.workplaceInfo = new WorkplaceInfo();
		this.agreements = new ArrayList<>();
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, Integer.MAX_VALUE, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = getActiveAgreements(result);
				
				getEnterpriseAddresses(
						s -> {success.accept(result);},
						f ->{}
				);	
			}
			
			private List<Agreement> getActiveAgreements(List<Agreement> agreements) {
				List<Agreement> activeAgreements = new ArrayList<>();
				for(Agreement agreement : agreements){
					if(agreement.getId() > 0)
						activeAgreements.add(agreement);
				}
				return activeAgreements;
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
				addresses = result;
				
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
				calendars = result;

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
				activities = result;
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
	
	// ---------------------------------------------- GETTERS / SETTERS  -------------------------------------------------
	
	public Map<Integer, String> getWorkplaceAddresses(){
		return this.addresses;
	}
	
	public Map<Integer, String> getWorkplacesCalendars(){
		return this.calendars;
	}
	
	public List<Agreement> getWorkplacesAgreements(){
		return this.agreements;
	}
	
	public Map<Integer, String> getWorkplaceActivities(){
		return this.activities;
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
