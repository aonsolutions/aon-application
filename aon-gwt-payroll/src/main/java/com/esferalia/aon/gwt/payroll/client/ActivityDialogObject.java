package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityDialogObject {
	
	private ActivityInfo activityInfo;
	private DomainEnterprisesServiceAsync enterprisesService;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ActivityDialogObject(Enterprise enterprise, DomainEnterprisesServiceAsync enterprisesService) {
		this.enterprisesService = enterprisesService;
		activityInfo = new ActivityInfo();
		activityInfo.setEnterprise(enterprise.getId());
		activityInfo.setDomain(enterprise.getDomain());
		activityInfo.setRegime("General");
		activityInfo.setActive(false);
	}
	
	public Map<String, String> getAllCNAE2009() {
		return this.activityInfo.getAllCNAE2009();
	}
	
	public String getActivityRegime(){
		return this.activityInfo.getRegime();
	}	
	public Map<Integer, CCCInfo> getCCCs() {
		return this.activityInfo.getCccs();
	}

	public void deleteCCC(Integer cccId) {
		this.activityInfo.deleteCCC(cccId);
	}
	
	public void insertCCC(Integer cccId, String ccc, String cccRegimeCode, String cccAccount, Byte type, String geozone, Boolean useByContracts) {
		this.activityInfo.insertCCC(cccId, ccc, cccRegimeCode, cccAccount, type, geozone, useByContracts);
	}
	
	public void insertCCC(Integer cccId, String ccc, String cccRegimeCode, String cccAccount, Byte type, String geozone) {
		this.activityInfo.insertCCC(cccId, ccc, cccRegimeCode, cccAccount, type, geozone);
	}
	
	

	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------

	public void getCNAE2009(Consumer<Map<String, String>> success, Consumer<Throwable> failure){
		enterprisesService.getCNAE2009(new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				activityInfo.setAllCNAE2009(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void createActivity(Consumer<ActivityInfo> success, Consumer<Throwable> failure){
		enterprisesService.createActivityInfoDataBase(this.activityInfo, new AsyncCallback<ActivityInfo>() {
			
			@Override
			public void onSuccess(ActivityInfo result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// ------------------------------------------------- SET METHODS -------------------------------------------------
	public void setActivityDescription(String description) {
		this.activityInfo.setDescription(description);
	}
	
	public void setActivityCNAE2009(String cnae2009) {
		String cnae2009Code = cnae2009.split(" -")[0];
		String cnae2009Title = cnae2009.split("- ")[1];
		
		this.activityInfo.setCnae2009Code(cnae2009Code);
		this.activityInfo.setCnae2009Title(cnae2009Title);
	}
	
	public void setActivityStartDate(Date startDate) {
		this.activityInfo.setStartDate(startDate);
	}
	
	public void setActivityEndDate(Date endDate) {
		this.activityInfo.setEndDate(endDate);
	}
	
	public void setActivityActive(Boolean active) {
		this.activityInfo.setActive(active);
	}
	
}
