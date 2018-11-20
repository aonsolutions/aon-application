package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;

public class ActivityDraftObject {
	
	private ActivityInfo activityInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ActivityDraftObject(Activity activity, DomainEnterprisesServiceAsync domainEnterprisesServiceAsync) {
		this.activityInfo = new ActivityInfo(true);
	}
	
	public String getActivityDescription() {
		return this.activityInfo.getDescription();
	}
	
	public Integer getActivityCNAE2009() {
		return this.activityInfo.getCnae2009();
	}
	
	public String getActivityCNAE2009Name() {
		return "6201 - Actividades de programacion informatica";
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
	
	public void insertCCC(Integer cccId, String ccc, Byte type, String geozone) {
		this.activityInfo.insertCCC(cccId, ccc, type, geozone);
	}
	
	

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
//	public void initializeEmployee(Consumer<EmployeeContractInfo> success, Consumer<Throwable> failure) {
//		employeesService.getEmployeeInfoDataBase(this.employee.getId(), new AsyncCallback<EmployeeContractInfo>() {
//			
//			@Override
//			public void onSuccess(EmployeeContractInfo result) {
//				employeeContractData = result;
//				employeeData = result.getEmployeeInfo();
//				contractData = result.getContractInfo();
//				
//				new_employeeContractData = result;
//				new_employeeData = result.getEmployeeInfo();
//				new_contractData = result.getContractInfo();
//
//				getAgreements(
//						r ->{success.accept(result);},
//						f->{}
//				);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				failure.accept(caught);
//			}
//		});
//	}
//	
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
