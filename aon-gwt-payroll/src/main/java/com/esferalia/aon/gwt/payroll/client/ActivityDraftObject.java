package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivityDraftObject extends AbstractDraftObject {
	
	// ------------------------------------------- Variables
	
	private Activity activity;
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private Integer domain;
		
	// ------------------------------------------- Constructor	
	
	public ActivityDraftObject(Integer activityId) {
		this.activity = new Activity();
		this.activity.setId(activityId);
	}
	
	// ------------------------------------------- Getter Methods
	
	public Integer getDomain() {
		return this.domain;
	}
	
	public String getActivityDescription() {
		return this.activity.getDescription();
	}
	
	public String getActivityCNAE2009() {
		if(AonStringUtils.isBlank(this.activity.getCnaeCode())) return "";
		return this.activity.getCnaeCode() + " - " + this.activity.getCnaeDescription();
	}
	
	public Date getActivityStartDate() {
		return this.activity.getStartDate();
	}
	
	public Date getActivityEndDate() {
		return this.activity.getEndDate();
	}
	
	public Boolean getActivityPrincipal() {
		return this.activity.isPrincipal();
	}
	
	public List<EnterpriseCCC> getCCCs() {
		return this.activity.getCccs();
	}
	
	public List<EnterpriseCCC> getActiveCCCs() {
		return getCCCs().stream().filter(ccc -> !ccc.isDeleted()).collect(Collectors.toList());
	}
	
	// ------------------------------------------- DataBase Methods

	public void initializeActivity(Consumer<Activity> success, Consumer<Throwable> failure) {
		enterprisesService.getActivity(activity.getId(), new AsyncCallback<Activity>() {
			
			@Override
			public void onSuccess(Activity result) {
				domain = result.getDomain();
				activity = result;
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	public void updateActivity(Consumer<Void> success, Consumer<Throwable> failure){
		enterprisesService.saveActivity(activity, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
		});
	}
	
	public void getDeleteCCCMessage(Set<Integer> cccIdSet, Consumer<String> success, Consumer<Throwable> failure) {
		ArrayList<Integer> cccIds = new ArrayList<>();
		cccIds.addAll(cccIdSet);
		
		enterprisesService.getDeleteCCCMessage(cccIds, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getUpdateCert(String regime, String ccc, Consumer<String> success, Consumer<Throwable> failure) {
		enterprisesService.getUpdateCert(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
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
		this.activity.setDescription(description);
	}
	
	public void setActivityCNAE2009(Integer cnaeId, String cnaeCode, String cnaeTitle) {
		this.activity.setCnae(cnaeId);
		this.activity.setCnaeCode(cnaeCode);
		this.activity.setCnaeDescription(cnaeTitle);
	}
	
	public void setActivityStartDate(Date startDate) {
		this.activity.setStartDate(startDate);
	}
	
	public void setActivityEndDate(Date endDate) {
		this.activity.setEndDate(endDate);
	}
	
	public void setActivityIsPrincipal(Boolean isPrincipal) {
		this.activity.setPrincipal(isPrincipal);
	}

	public Set<Entry<Integer, String>> getActivities(){
		HashMap<Integer, String> activities = new HashMap<>();
		activities.put(activity.getId(), activity.getDescription());
		return activities.entrySet();
	}

	public void deleteCCC(Integer cccId) {
		Optional<EnterpriseCCC> ccc = this.activity.getCccs().stream().filter(cccIt -> cccIt.getId().equals(cccId)).findFirst();
		if(ccc.isPresent()) ccc.get().setDeleted(true);
	}
	
	public void insertCCC(EnterpriseCCC ccc) {
		if(ccc.getId() == null) this.activity.getCccs().add(ccc);
		else {
			this.activity.getCccs().removeIf(cccIt -> cccIt.getId().equals(ccc.getId()));
			this.activity.getCccs().add(ccc);
		}
	}
	
	public Pair<String, String> getPrincipalAccount() {
		Optional<EnterpriseCCC> principalAccount = activity.getCccs().stream().filter(ccc -> ccc.getType() == (byte)0).findFirst();
		if(principalAccount.isPresent())
			return new Pair<>(getCCCRegimeCode(principalAccount.get().getType()), principalAccount.get().getCcc());
		else if(!activity.getCccs().isEmpty()){
			EnterpriseCCC ccc = activity.getCccs().get(0);
			return new Pair<>(getCCCRegimeCode(ccc.getType()), ccc.getCcc());
		} else return null;
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}

}
