package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityDialogObject {
	
	private Activity activityInfo;
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private Map<Integer, String> cnae2009;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ActivityDialogObject(Enterprise enterprise) {
		activityInfo = new Activity();
		activityInfo.setDomain(enterprise.getDomain());
		activityInfo.setEnterprise(enterprise.getId());
		activityInfo.setPrincipal(false);
		this.cnae2009 = new HashMap<>();
	}
	
	public Map<Integer, String> getAllCNAE2009() {
		return this.cnae2009;
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------

	public void getCNAE2009(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure){
		enterprisesService.getCNAE2009(new AsyncCallback<Map<Integer, String>>() {
			
			@Override
			public void onSuccess(Map<Integer, String> result) {
				cnae2009 = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
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
	
	// ------------------------------------------------- SET METHODS -------------------------------------------------
	
	public void setActivityDescription(String description) {
		this.activityInfo.setDescription(description);
	}
	
	public void setActivityCNAE2009(Entry<Integer, String> cnae2009Entry) {
		Integer cnae = cnae2009Entry.getKey();
		String cnae2009Code = cnae2009Entry.getValue().split(" -")[0];
		String cnae2009Title = cnae2009Entry.getValue().split("- ")[1];
		
		this.activityInfo.setCnae(cnae);
		this.activityInfo.setCnaeCode(cnae2009Code);
		this.activityInfo.setCnaeDescription(cnae2009Title);
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
