package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityDialogObject {
	
	private Activity activityInfo;
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ActivityDialogObject(Enterprise enterprise) {
		activityInfo = new Activity();
		activityInfo.setDomain(enterprise.getDomain());
		activityInfo.setEnterprise(enterprise.getId());
		activityInfo.setPrincipal(false);
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------

	public void createActivity(Consumer<Void> success, Consumer<Throwable> failure){
		enterprisesService.saveActivity(this.activityInfo, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// ------------------------------------------------- GET METHODS -------------------------------------------------
	
	public List<EnterpriseCCC> getCCCs() {
		return this.activityInfo.getCccs();
	}
	
	public List<EnterpriseCCC> getActiveCCCs() {
		return getCCCs().stream().filter(ccc -> !ccc.isDeleted()).collect(Collectors.toList());
	}
	
	// ------------------------------------------------- SET METHODS -------------------------------------------------
	
	public void setActivityDescription(String description) {
		this.activityInfo.setDescription(description);
	}
	
	public void setActivityCNAE2009(Integer cnaeId, String cnaeCode, String cnaeTitle) {
		this.activityInfo.setCnae(cnaeId);
		this.activityInfo.setCnaeCode(cnaeCode);
		this.activityInfo.setCnaeDescription(cnaeTitle);
	}
	
	public void setActivityStartDate(Date startDate) {
		this.activityInfo.setStartDate(startDate);
	}
	
	public void setActivityEndDate(Date endDate) {
		this.activityInfo.setEndDate(endDate);
	}
	
	public void setActivityActive(Boolean active) {
		this.activityInfo.setPrincipal(active);
	}
	
	public void deleteCCC(Integer cccId) {
		Optional<EnterpriseCCC> ccc = this.activityInfo.getCccs().stream().filter(cccIt -> cccIt.getId().equals(cccId)).findFirst();
		if(ccc.isPresent()) ccc.get().setDeleted(true);
	}
	
	public void insertCCC(EnterpriseCCC ccc) {
		if(ccc.getId() == null || this.activityInfo.getCccs().isEmpty()) this.activityInfo.getCccs().add(ccc);
		else {
			this.activityInfo.getCccs().removeIf(cccIt -> cccIt.getId().equals(ccc.getId()));
			this.activityInfo.getCccs().add(ccc);
		}
	}

	public Integer getDomain() {
		return activityInfo.getDomain();
	}
	
}
