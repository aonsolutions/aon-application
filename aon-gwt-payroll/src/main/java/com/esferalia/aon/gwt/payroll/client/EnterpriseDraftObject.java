package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.Base64Utils;

public class EnterpriseDraftObject {

	private DomainEnterprisesServiceAsync enterprisesService;
	private Enterprise enterprise;
	
	private Map<Integer, String> scopes;
	private List<Agreement> agreements;
	private Map<Integer, String> calendars;
	
	private EnterpriseInfo enterpriseInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EnterpriseDraftObject(Enterprise enterprise, DomainEnterprisesServiceAsync enterprisesService) {
		
		this.enterprisesService = enterprisesService;
		this.enterprise = enterprise;
	}
	
	public List<Agreement> getActiveAgreements(){
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement a : this.agreements){
			if(a.getId() > 0)
				activeAgreements.add(a);
		}
		return activeAgreements;
	}
	
	public Integer getAgreementId(String agreementName){
		for(Agreement a : getActiveAgreements()){
			if(a.getDescription() == agreementName && a.getId() > 0)
				return a.getId();
		}
		return -1;
	}
	

	public int getAgreementIndex() {
		if(null == getAgreement())
			return -1;
		
		List<Agreement> activeAgreements = getActiveAgreements();
		for(int i = 0; i<activeAgreements.size(); i++) {
			if(activeAgreements.get(i).getId().equals(getAgreement()))
				return i;
		}
		return -1;
	}
	
	public Map<Integer, String> getEnterprisecopes(){
		return this.scopes;
	}
	
	public Map<Integer, String> getEnterpriseCalendars(){
		return this.calendars;
	}
	
	public String getName() {
		return this.enterpriseInfo.getName();
	}
	
	public String getAlias() {
		return this.enterpriseInfo.getAlias();
	}
	
	public Byte getDocumentType() {
		return this.enterpriseInfo.getDocumentType();
	}
	
	public String getDocument() {
		return this.enterpriseInfo.getDocument();
	}
	
	public String getDocumentCountry() {
		return getNationality(this.enterpriseInfo.getDocumentCountry());
	}
	
	private String getNationality(String iso2) {
		for (int i = 0; i < Country.values().length; i++) {
			if (Country.values()[i].getIso2() == iso2)
				return Country.values()[i].getName();
		}
		return null;
	}
	
	public String getSteetType() {
		return this.enterpriseInfo.getStreetType();
	}
	
	public Integer getAddressStreetTypeIndex(){
		return getStreetTypeIndex(getSteetType());
	}
	
	private Integer getStreetTypeIndex(String streetType) {
		for(int i=0; i<StreetType.values().length; i++){
			if(streetType == StreetType.values()[i].getShortCode())
				return i;
		}
		return 0;
	}
	
	public String getAddress() {
		return this.enterpriseInfo.getAddress();
	}
	
	public String getAddressNum() {
		return this.enterpriseInfo.getAddressNum();
	}
	
	public String getAddressZip() {
		return this.enterpriseInfo.getAddressZip();
	}
	
	public String getAddressCity() {
		return this.enterpriseInfo.getAddressCity();
	}
	
	public String getAddressProvince() {
		return this.enterpriseInfo.getAddressProvince();
	}
	
	public Integer getAddressProvinceIndex() {
		return ProvinceContract.getProvinceIndex(getAddressProvince());
	}
	
	public String getPhone() {
		return this.enterpriseInfo.getPhone();
	}
	
	public String getMobile() {
		return this.enterpriseInfo.getMobile();
	}
	
	public String getEmail() {
		return this.enterpriseInfo.getEmail();
	}
	
	public String getWeb() {
		return this.enterpriseInfo.getWeb();
	}
	
	public Integer getScope() {
		return this.enterpriseInfo.getScopeId();
	}
	
	public Integer getPaySheetModelIndex() {
		return (int)this.enterpriseInfo.getPaysheetModel();
	}
	
	public Integer getCostsModelIndex() {
		return (int)this.enterpriseInfo.getCostsModel();
	}
	
	public Integer getPaysheetSendIndex() {
		return (int)this.enterpriseInfo.getPaysheetSendType();
	}
	
	public String getPaysheetSendEmail() {
		return this.enterpriseInfo.getPaysheetEmail();
	}
	
	public Integer getAgreement() {
		return Integer.parseInt(this.enterpriseInfo.getEnterpriseAgreement());
	}
	
	public Integer getCalendar() {
		return this.enterpriseInfo.getCalendarId();
	}
	
	public Integer getCalendarIndex(){
		Integer index = 0;
		for(Integer value : getEnterpriseCalendars().keySet()){
			if(value.equals(getCalendar())) 
				break;
			index ++;
		}
		return index;
	}
	
	public boolean checkDocumentValidation(String document_type_string, String document_string) {
		if("DNI".equals(document_type_string)){
			Dni dni = new Dni(document_string);
			if(dni.checkDNI())
				return true;
			else
				return false;
		}else if("" == document_string) {
			return false;
		}else
			return true;
	}
	
	public Integer getScopeIndex(){
		Integer index = 0;
		for(Integer value : getEnterprisecopes().keySet()){
			if(value.equals(getScope())) 
				break;
			index ++;
		}
		return index;
	}
	
	public String getLogo() {
		return this.enterpriseInfo.getLogo();
	}
	
	public String getSignature() {
		return this.enterpriseInfo.getSignature();
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void initializeEnterprise(Consumer<EnterpriseInfo> success, Consumer<Throwable> failure) {
	
		enterprisesService.getEnterpriseInfo(this.enterprise.getId() , new AsyncCallback<EnterpriseInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onSuccess(EnterpriseInfo result) {
				enterpriseInfo = result;
				
				getAgreements(
						r ->{success.accept(result);},
						f->{}
				);
			}
		});	
	}
	
	private void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, 0, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = result;
				
				getEnterpriseCalendars(
						s -> {success.accept(result);},
						f ->{}
				);	
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void getEnterpriseCalendars(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpiseCalendars(this.enterprise.getId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				calendars = result;
				
				getEnterpriseScopes(
					s -> {success.accept(result);},
					f -> {}
				);	
			}
		});
	}
	
	private void getEnterpriseScopes(Consumer<Map<Integer, String>> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpiseScopes(this.enterprise.getId(), new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				scopes = result;
				
				success.accept(result);
			}
		});
	}
	
//	public void updateEnterprise(Consumer<WorkplaceInfo> success, Consumer<Throwable> failure){
//		
//		enterprisesService.setWorkplaceInfo(this.workplaceInfo, new AsyncCallback<WorkplaceInfo>() {
//			
//			@Override
//			public void onSuccess(WorkplaceInfo result) {
//				workplaceInfo_Old = new WorkplaceInfo(result);
//				success.accept(result);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				failure.accept(caught);
//			}
//		});
//		
//	}
	
	public void setDocument(String document) {
		enterpriseInfo.setDocument(document);
	}

	public void setDocumentType(String documentType) {
		if(documentType == "DNI")
			enterpriseInfo.setDocumentType((byte) 0);
		else if(documentType == "CIF")
			enterpriseInfo.setDocumentType((byte) 1);
		else if(documentType == "Pasaporte")
			enterpriseInfo.setDocumentType((byte) 3);
		else
			enterpriseInfo.setDocumentType((byte) 0);
	}

	public void setScope(Integer scopeId) {
		enterpriseInfo.setScopeId(scopeId);
	}
	
}
