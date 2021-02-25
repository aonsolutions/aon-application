package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityDraftObject extends AbstractDraftObject {
	
	private ActivityInfo activityInfo;
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private Integer activityId;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ActivityDraftObject(Activity activity) {
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
	
	public Date getActivityStartDate() {
		return this.activityInfo.getStartDate();
	}
	
	public Date getActivityEndDate() {
		return this.activityInfo.getEndDate();
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
	
	public void insertCCC(Integer cccId, String ccc, String cccRegimeCode, String cccAccount, Byte type, String geozone, String geozoneCode, Boolean useByContracts) {
		this.activityInfo.insertCCC(cccId, ccc, cccRegimeCode, cccAccount, type, geozone, geozoneCode, useByContracts);
	}
	
	public void insertCCC(Integer cccId, String ccc, String cccRegimeCode, String cccAccount, Byte type, String geozone, String geozoneCode) {
		this.activityInfo.insertCCC(cccId, ccc, cccRegimeCode, cccAccount, type, geozone, geozoneCode);
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

	public void updateActivity(Consumer<ActivityInfo> success, Consumer<Throwable> failure){
		enterprisesService.updateActivityInfoDataBase(this.activityInfo, new AsyncCallback<ActivityInfo>() {
			
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
