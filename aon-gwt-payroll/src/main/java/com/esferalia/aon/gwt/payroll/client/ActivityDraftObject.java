package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityDraftObject {
	
	private ActivityInfo activityInfo;
	private DomainEnterprisesServiceAsync enterprisesService;
	private Integer activityId;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ActivityDraftObject(Activity activity, DomainEnterprisesServiceAsync enterprisesService) {
		this.enterprisesService = enterprisesService;
		this.activityId = activity.getId();
	}
	
	public String getActivityDescription() {
		return this.activityInfo.getDescription();
	}
	
	public String getActivityCNAE2009() {
		return this.activityInfo.getCnae2009Code() + " - " + this.activityInfo.getCnae2009Title();
	}
	
	public Map<String, String> getAllCNAE2009() {
		return this.activityInfo.getAllCNAE2009();
	}
	
	public String getActivityRegime() {
		return this.activityInfo.getRegime();
	}
	
	public Boolean getActivityActive() {
		return this.activityInfo.getActive();
	}
	
	public Map<Integer, CCCInfo> getCCCs() {
		return this.activityInfo.getCccs();
	}

	public void deleteCCC(Integer cccId) {
		this.activityInfo.deleteCCC(cccId);
	}
	
	public void insertCCC(Integer cccId, String ccc, String cccRegimeCode, String cccAccount, Byte type, String geozone) {
		this.activityInfo.insertCCC(cccId, ccc, cccRegimeCode, cccAccount, type, geozone);
	}
	
	

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	//Consumer<ActivityInfo> success, Consumer<Throwable> failure
	public void initializeActivity(Consumer<ActivityInfo> success, Consumer<Throwable> failure) {
		enterprisesService.getActivityInfoDataBase(this.activityId, new AsyncCallback<ActivityInfo>() {
			
			@Override
			public void onSuccess(ActivityInfo result) {
				activityInfo = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
//	public void getActivitiesCCC(Consumer<ActivitiesCCC> success, Consumer<Throwable> failure) {
//		enterprisesService.getActivitiesCCC(workplace.getId(), new AsyncCallback<ActivitiesCCC>() {
//			
//			@Override
//			public void onSuccess(ActivitiesCCC result) {
//				activitiesCCC = result;
//				getWorkplaces(
//						r->{success.accept(result);},
//						f->{}
//					);
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				// TODO Auto-generated method stub
//			}
//		});
//	}
//
//	public void updateEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure){
//		new_employeeContractData.setContractInfo(new_contractData);
//		new_employeeContractData.setEmployeeInfo(new_employeeData);
//		
//		employeesService.setEmployeeInfoDataBase(this.new_employeeContractData, new AsyncCallback<EmployeeContractInfo>() {
//			
//			@Override
//			public void onSuccess(EmployeeContractInfo result) {
//				success.accept(result);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				failure.accept(caught);
//			}
//		});
//	}
	
}
