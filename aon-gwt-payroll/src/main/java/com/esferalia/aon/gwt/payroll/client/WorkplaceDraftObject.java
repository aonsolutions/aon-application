package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceDraftObject extends AbstractDraftObject {
	
	
	// ------------------------------------------------------------------------
	
	private DomainEnterprisesServiceAsync enterprisesService;
	
	private Enterprise enterprise;
	private Workplace workplace;
	
	private Map<Integer, String> addresses;
	private Map<Integer, String> calendars;
	private List<Agreement> agreements;
	private Map<Integer, String> activities;
	
	private WorkplaceInfo workplaceInfo;
	private WorkplaceInfo workplaceInfo_Old;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public WorkplaceDraftObject(Enterprise enterprise, Workplace workplace, DomainEnterprisesServiceAsync enterprisesService) {
		
		this.enterprisesService = enterprisesService;

		this.enterprise = enterprise;
		this.workplace = workplace;
		this.agreements = new ArrayList<>();
		
		this.undoManager = new UndoManager<Undoable>();
		
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeWorkplace(Consumer<WorkplaceInfo> success, Consumer<Throwable> failure) {
	
		enterprisesService.getWorkplaceInfo(this.workplace.getId(), new AsyncCallback<WorkplaceInfo>() {

			@Override
			public void onSuccess(WorkplaceInfo result) {
				workplaceInfo = result;
				workplaceInfo_Old = new WorkplaceInfo(result);
				
				getAgreements(
						r ->{success.accept(result);},
						f->{}
				);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
		});
		
	}
	
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
		
	public void updateWorkplace(Consumer<WorkplaceInfo> success, Consumer<Throwable> failure){
		
		enterprisesService.setWorkplaceInfo(this.workplaceInfo, new AsyncCallback<WorkplaceInfo>() {
			
			@Override
			public void onSuccess(WorkplaceInfo result) {
				workplaceInfo_Old = new WorkplaceInfo(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	// ---------------------------------------------- GETTERS / SETTERS  -------------------------------------------------
	
	public WorkplaceInfo getWorkplaceInfo(){
		return this.workplaceInfo;
	}
	
	public String getWorkplaceDescription() {
		return getWorkplaceInfo().getDescription();
	}
	
	public Map<Integer, String> getWorkplaceAddresses(){
		return this.addresses;
	}
	
	public Map<Integer, String> getWorkplaceCalendars(){
		return this.calendars;
	}
	
	public List<Agreement> getWorkplaceAgreements(){
		return this.agreements;
	}
	
	public Map<Integer, String> getWorkplaceActivities(){
		return this.activities;
	}
	
	public Integer getWorkplaceAddressIndex(){
		Integer index = 0;
		
		if(!getWorkplaceAddresses().isEmpty() && null != workplaceInfo.getAddressId()) {
			for(Integer value : getWorkplaceAddresses().keySet()){
				if(value.equals(workplaceInfo.getAddressId())) {
					index++;
					break;
				}
				index++;
			}
		}
		
		return index;
	}
	
	public Integer getWorkplaceEconomicConcert(){
		return (int) workplaceInfo.getEconomicConcert() + 1;
	} 
	
	public Integer getWorkplaceCalendarIndex(){
		Integer index = 0;
		
		if(!getWorkplaceCalendars().isEmpty() && null != workplaceInfo.getCalendarId()) {
			for(Integer value : getWorkplaceCalendars().keySet()){
				if(value.equals(workplaceInfo.getCalendarId())) {
					index++;
					break;
				}
				index++;
			}
		}
		return index;
	}
	
	public Integer getWorkplaceAgreementIndex(){
		Integer index = 0;
		
		if(!getWorkplaceAgreements().isEmpty() && null != workplaceInfo.getAgreementId()) {
			for(Agreement agreement : getWorkplaceAgreements()) {
				if(agreement.getId().equals(workplaceInfo.getAgreementId())) {
					index++;
					break;
				}
				index++;
			}
		}
		
		return index;
	}
	
	public Integer getWorkplaceActivityIndex(){
		Integer index = 0;
		
		if(!getWorkplaceActivities().isEmpty() && null != workplaceInfo.getActivityId()) {
			for(Integer value : getWorkplaceActivities().keySet()){
				if(value.equals(workplaceInfo.getActivityId())) {
					index++;
					break;
				}
				index++;
			}
		}
		return index;
	}
	
	public void setWorkplaceDescription(String description) {
		add(workplaceInfo::setDescription, 
			workplaceInfo.getDescription(), 
			description );
		workplaceInfo.setDescription(description);
	}

	public void setWorkplaceAddress(Integer addressId) {
		add(workplaceInfo::setAddressId, 
				workplaceInfo.getAddressId(), 
				addressId);
		workplaceInfo.setAddressId(addressId);
	}

	public void setWorkplaceEconomicConcert(int economicCocncert) {
		add(workplaceInfo::setEconomicConcert, 
				workplaceInfo.getEconomicConcert(), 
				(byte) economicCocncert);
		workplaceInfo.setEconomicConcert((byte) economicCocncert);
	}

	public void setWorkplaceCalendar(Integer calendarId) {
		add(workplaceInfo::setCalendarId, 
				workplaceInfo.getCalendarId(), 
				calendarId);
		workplaceInfo.setCalendarId(calendarId);
	}

	public void setWorkplaceAgreement(Integer agreementId) {
		add(workplaceInfo::setAgreementId, 
				workplaceInfo.getAgreementId(), 
				agreementId);
		workplaceInfo.setAgreementId(agreementId);
	}

	public void setWorkplaceActivity(Integer activityId) {
		add(workplaceInfo::setActivityId, 
				workplaceInfo.getActivityId(), 
				activityId);
		workplaceInfo.setActivityId(activityId);
	}

	public boolean hasChanged() {
		boolean changed = workplaceInfo.hasChanged(workplaceInfo_Old);
		return changed;
	}
	
}
