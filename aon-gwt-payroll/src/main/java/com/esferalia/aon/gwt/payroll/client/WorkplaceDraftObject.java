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
	private DomainEmployeesServiceAsync employeesService;
	
	private Enterprise enterprise;
	private Workplace workplace;
	
	private Map<Integer, String> addresses;
	private List<Agreement> agreements;
	private Map<Integer, String> activities;
	
	private WorkplaceInfo workplaceInfo;
	private WorkplaceInfo workplaceInfo_Old;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public WorkplaceDraftObject(Enterprise enterprise, Workplace workplace, DomainEnterprisesServiceAsync enterprisesService, DomainEmployeesServiceAsync employeesService) {
		
		this.enterprisesService = enterprisesService;
		this.employeesService = employeesService;

		this.enterprise = enterprise;
		this.workplace = workplace;
		this.agreements = new ArrayList<>();
		
		this.undoManager = new UndoManager<Undoable>();
		
		
	}
	
	public CalendarDraftObjectData getCalendarDraftObjectData() {
		return new CalendarDraftObjectData(workplace.getId());
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
	
	public List<Agreement> getWorkplaceAgreements(){
		return this.agreements;
	}
	
	public Map<Integer, String> getWorkplaceActivities(){
		return this.activities;
	}
	
	public String getWorkplaceAddress(){
		Integer addressId = workplaceInfo.getAddressId();
		return null == addressId ? null : addressId.toString();
	}
	
	public String getWorkplaceEconomicConcert(){
		Integer economicConcert = (int)workplaceInfo.getEconomicConcert();
		return economicConcert.toString();
	} 
	
	public String getWorkplaceAgreement(){
		Integer agreeementId = workplaceInfo.getAgreementId();
		return null == agreeementId ? null : agreeementId.toString();
	}
	
	public String getWorkplaceActivity(){
		Integer activityId = workplaceInfo.getActivityId();
		return null == activityId ? null : activityId.toString();
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
