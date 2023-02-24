package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainCCCObject {
	
	// -------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private List<Activity> activities; 
	private Integer domainId;
	
	// -------------------------------------------- Constructor
	
	public MainCCCObject() {
		super();
		this.activities = new ArrayList<>();
	}
	
	// -------------------------------------------- Database Methods
	
	public void getMainCCCInfo(Consumer<List<Activity>> success, Consumer<Throwable> failure) {
		impl.getActivities(new AsyncCallback<List<Activity>>() {
			
			@Override
			public void onSuccess(List<Activity> result) {
				activities = result;
				impl.getDomain(new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer domainIdIn) {
						domainId = domainIdIn;
						success.accept(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Nothing to do
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setMainCCCInfo(Consumer<Void> success, Consumer<Throwable> failure) {
		impl.saveActivities(activities, new AsyncCallback<Void>() {
			
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
	
	public void getUpdateCert(String regime, String ccc, Consumer<String> success, Consumer<Throwable> failure) {
		impl.getUpdateCert(regime, ccc, new AsyncCallback<String>() {
			
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
	
	// -------------------------------------------- Getters Methods
	
	public List<EnterpriseCCC> getCCCs() {
		List<EnterpriseCCC> ccccs = new ArrayList<>();
		this.activities.forEach(activity -> ccccs.addAll(activity.getCccs()));
		return ccccs;
	}
	
	public List<EnterpriseCCC> getActiveCCCs() {
		List<EnterpriseCCC> ccccs = new ArrayList<>();
		this.activities.forEach(activity -> ccccs.addAll(activity.getCccs()));
		return ccccs.isEmpty() ? ccccs : ccccs.stream().filter(ccc -> !ccc.isDeleted()).collect(Collectors.toList());
	}
	
	public Set<Entry<Integer, String>> getActivities() {
		Map<Integer, String> activitiesMap = new HashMap<>();
		this.activities.forEach(activity -> activitiesMap.put(activity.getId(), activity.getDescription()));
		return activitiesMap.entrySet();
	}

	public void insertCCC(EnterpriseCCC ccc) {
		Optional<Activity> activityCCC = this.activities.stream().filter(activity -> activity.getId().equals(ccc.getEnterpriseActivity())).findFirst();
		if(activityCCC.isPresent()) {
			activityCCC.get().getCccs().removeIf(cccIt -> cccIt.getId().equals(ccc.getId()));
			activityCCC.get().getCccs().add(ccc);
		}
	}
	
	public void insertActivity(Activity activity) {
		this.activities.add(activity);
	}

	public void deleteCCC(Integer cccId) {
		Optional<EnterpriseCCC> deleteCCC = getCCCs().stream().filter(ccc -> ccc.getId().equals(cccId)).findFirst();
		if(deleteCCC.isPresent()) deleteCCC.get().setDeleted(true);
	}
	
	public Pair<String, String> getPrincipalAccount() {
		Optional<EnterpriseCCC> principalAccount = getCCCs().stream().filter(ccc -> ccc.getType() == (byte)0).findFirst();
		if(principalAccount.isPresent())
			return new Pair<>(getCCCRegimeCode(principalAccount.get().getType()), principalAccount.get().getCcc());
		else if(!getCCCs().isEmpty()){
			EnterpriseCCC ccc = getCCCs().get(0);
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

	public Integer getDomain() {
		return domainId;
	}
		
}
