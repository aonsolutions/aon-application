package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.AgreementComunica;
import com.esferalia.aon.gwt.payroll.shared.AgreementComunicaInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ComunicaEnterpriseSettings;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunica;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunicaInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainConfigComunicaObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private ComunicaEnterpriseSettings comunicaEnterpriseSettings;
	private WorkplaceComunica workplaceComunica;
	private MainCCCInfo mainCCCInfo;
	private AgreementComunica agreementComunica;
	
	private Map<Integer, String> addresses;
	private Map<String, String> serviAgreementsMap;
	
	public MainConfigComunicaObject() {
		super();
		this.workplaceComunica = new WorkplaceComunica();
		this.mainCCCInfo = new MainCCCInfo();
		this.agreementComunica = new AgreementComunica();
		this.addresses = new HashMap<Integer, String>();
	}
	
	public void getComunicaEnterpriseSettings(Consumer<ComunicaEnterpriseSettings> success, Consumer<Throwable> failure) {
		impl.getComunicaEnterpriseSettings(new AsyncCallback<ComunicaEnterpriseSettings>() {
			
			@Override
			public void onSuccess(ComunicaEnterpriseSettings comunicaEnterpriseSettingsIn) {
				comunicaEnterpriseSettings = comunicaEnterpriseSettingsIn;
				workplaceComunica = comunicaEnterpriseSettings.getWorkplaceComunica();
				mainCCCInfo = comunicaEnterpriseSettings.getMainCCCInfo();
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
		this.comunicaEnterpriseSettings.setMainCCCInfo(getMainCCCInfo());
		this.comunicaEnterpriseSettings.setAgreementComunica(getAgreementComunica());
	}

	private WorkplaceComunica getWorkplaceComunica() {
		return this.workplaceComunica;
	}

	public MainCCCInfo getMainCCCInfo() {
		return this.mainCCCInfo;
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

	public Collection<CCCInfo> getCCCs() {
		return this.mainCCCInfo.getCccs().values();
	}
	
	public Set<Entry<Integer, String>> getActivities() {
		return this.mainCCCInfo.getActivities().entrySet();
	}

	public void insertCCC(Integer cccId, int activityId, byte cccRegimeType, String cccRegimeCode, String ccc, String province, String provinceCode) {
		this.mainCCCInfo.insertCCC(cccId, activityId, cccRegimeType, cccRegimeCode, ccc, province, provinceCode);
	}

	public void deleteCCC(Integer cccId) {
		this.mainCCCInfo.deleteCCC(cccId);
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
		
}
