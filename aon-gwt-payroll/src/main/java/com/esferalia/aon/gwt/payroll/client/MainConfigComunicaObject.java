package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.payroll.shared.AgreementComunica;
import com.esferalia.aon.gwt.payroll.shared.AgreementComunicaInfo;
import com.esferalia.aon.gwt.payroll.shared.ComunicaEnterpriseSettings;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunica;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunicaInfo;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainConfigComunicaObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private ComunicaEnterpriseSettings comunicaEnterpriseSettings;
	private WorkplaceComunica workplaceComunica;
	private List<Activity> activities; 
	private AgreementComunica agreementComunica;
	
	private Map<Integer, String> addresses;
	private Map<String, String> serviAgreementsMap;
	
	public MainConfigComunicaObject() {
		super();
		this.workplaceComunica = new WorkplaceComunica();
		this.activities = new ArrayList<>();
		this.agreementComunica = new AgreementComunica();
		this.addresses = new HashMap<>();
	}
	
	public void getComunicaEnterpriseSettings(Consumer<ComunicaEnterpriseSettings> success, Consumer<Throwable> failure) {
		impl.getComunicaEnterpriseSettings(new AsyncCallback<ComunicaEnterpriseSettings>() {
			
			@Override
			public void onSuccess(ComunicaEnterpriseSettings comunicaEnterpriseSettingsIn) {
				comunicaEnterpriseSettings = comunicaEnterpriseSettingsIn;
				workplaceComunica = comunicaEnterpriseSettings.getWorkplaceComunica();
				activities = comunicaEnterpriseSettings.getActivities();
				agreementComunica = comunicaEnterpriseSettings.getAgreementComunica();
				
				getEnterpriseAddresses(
						s -> {
							success.accept(comunicaEnterpriseSettings);
						},f ->{});
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void getEnterpriseAddresses(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		impl.getEnterpiseAddresses(comunicaEnterpriseSettings.getEnterpriseId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				addresses = result;
				getServiAgreements(s -> {
					success.accept(result);
				}, f -> {});
			}
		});
	}
	
	private void getServiAgreements(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		impl.getServiAgreements(new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				serviAgreementsMap = new HashMap<String, String>();
				serviAgreementsMap.putAll(result);
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
			
		});
	}
	
	public void setComunicaEnterpriseSettings(Consumer<Void> success, Consumer<Throwable> failure) {
		setComunicaEnterpriseSettings();
		impl.setComunicaEnterpriseSettings(this.comunicaEnterpriseSettings, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				getComunicaEnterpriseSettings(s -> {
					success.accept(result);
				}, f ->{});
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void setComunicaEnterpriseSettings() {
		this.comunicaEnterpriseSettings.setWorkplaceComunica(getWorkplaceComunica());
		this.comunicaEnterpriseSettings.setActivities(activities);
		this.comunicaEnterpriseSettings.setAgreementComunica(getAgreementComunica());
	}

	private WorkplaceComunica getWorkplaceComunica() {
		return this.workplaceComunica;
	}
	
	private AgreementComunica getAgreementComunica() {
		return this.agreementComunica;
	}
	
	public Map<Integer, String> getAddresses() {
		return addresses;
	}
	
	public Map<String, String> getServiAgreements() {
		return serviAgreementsMap;
	}
	
	// --------------------------------------------------- CCCInfo.Methods

	public Collection<WorkplaceComunicaInfo> getWorkplaces() {
		return this.workplaceComunica.getWorkplaces().values();
	}
	
	public void insertWorkplace(Integer workplaceId, String description, Integer addressId) {
		this.workplaceComunica.insertWokplace(workplaceId, description, addressId);
	}

	public void deleteWorkplace(Integer workplaceId) {
		this.workplaceComunica.deleteWorkplace(workplaceId);
	}
	
	// --------------------------------------------------- CCCInfo.Methods

	public List<EnterpriseCCC> getCCCs() {
		List<EnterpriseCCC> ccccs = new ArrayList<>();
		this.activities.forEach(activity -> ccccs.addAll(activity.getCccs()));
		return ccccs;
	}
	
	public List<EnterpriseCCC> getActiveCCCs() {
		return getCCCs().stream().filter(ccc -> !ccc.isDeleted()).collect(Collectors.toList());
	}
	
	public Set<Entry<Integer, String>> getActivities() {
		Map<Integer, String> activitiesMap = new HashMap<>();
		this.activities.forEach(activity -> activitiesMap.put(activity.getId(), activity.getDescription()));
		return activitiesMap.entrySet();
	}

	public void insertCCC(EnterpriseCCC ccc) {
		Optional<Activity> activityCCC = this.activities.stream().filter(activity -> activity.getId().equals(ccc.getEnterpriseActivity())).findFirst();
		if(activityCCC.isPresent()) {
			if(ccc.getId() == null) activityCCC.get().getCccs().add(ccc);
			else {
				activityCCC.get().getCccs().removeIf(cccIt -> cccIt.getId().equals(ccc.getId()));
				activityCCC.get().getCccs().add(ccc);
			}
		}
	}

	public void deleteCCC(Integer cccId) {
		Optional<EnterpriseCCC> deleteCCC = getCCCs().stream().filter(ccc -> ccc.getId().equals(cccId)).findFirst();
		if(deleteCCC.isPresent()) deleteCCC.get().setDeleted(true);
	}
	
	// --------------------------------------------------- CCCInfo.Methods

	public Collection<AgreementComunicaInfo> getAgreements() {
		return this.agreementComunica.getAgreements().values();
	}
	
	public void insertAgreement(Integer agreementId, String description, String ssNumber) {
		this.agreementComunica.insertAgreement(agreementId, description, ssNumber);
	}

	public void deleteAgreement(Integer agreementId) {
		this.agreementComunica.deleteAgreement(agreementId);
	}

	public Integer getDomain() {
		return activities.isEmpty() ? null : activities.get(0).getDomain();
	}
		
}
