package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EnterpriseDraftObject extends AbstractDraftObject {

	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private Enterprise enterprise;
	
	private Map<Integer, String> scopes;
	private List<Agreement> agreements;
	
	private EnterpriseInfo enterpriseInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EnterpriseDraftObject(Enterprise enterprise) {
		this.enterprise = enterprise;
		this.undoManager = new UndoManager<Undoable>();
	}
	
	// ---------------------------------------------- DATABASE METHODS SYNC  ---------------------------------------------
	
	public void checkStatus(Consumer<EnterpriseStatus> success, Consumer<Throwable> failure) {
		enterprisesService.getEnterpriseStatus(enterprise.getId(), new AsyncCallback<EnterpriseStatus>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept( caught );
			}
			
			 @Override
			public void onSuccess(EnterpriseStatus result) {
				 success.accept(result);
			}
		});
	}

	public void initializeEnterprise(Consumer<EnterpriseInfo> success, Consumer<Throwable> failure) {
	
		enterprisesService.getEnterpriseInfo(this.enterprise.getId() , new AsyncCallback<EnterpriseInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);		
			}

			@Override
			public void onSuccess(EnterpriseInfo enterpriseInfoIn) {
				enterpriseInfo = enterpriseInfoIn;
				agreements = getActiveAgreements(enterpriseInfo.getAgreements());
				scopes = enterpriseInfo.getScopes();
				success.accept(enterpriseInfoIn);
			}
		});	
	}
	
	private List<Agreement> getActiveAgreements(List<Agreement> agreements) {
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement agreement : agreements){
			if(agreement.getId() > 0)
				activeAgreements.add(agreement);
		}
		return activeAgreements;
	}
	
	public void updateEnterprise(Consumer<EnterpriseInfo> success, Consumer<Throwable> failure) {
		
		enterprisesService.updateEnterprise(this.enterpriseInfo , new AsyncCallback<EnterpriseInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);	
			}

			@Override
			public void onSuccess(EnterpriseInfo result) {
				success.accept(result);
			}
		});	
	}
	
	// ---------------------------------------------- GETTERS  -------------------------------------------------
	
	public EnterpriseInfo getEnterpriseInfo() {
		return this.enterpriseInfo;
	}

	public List<Agreement> getEnterpriseAgreements() {
		return agreements;
	}
	
	public Map<Integer, String> getEnterprisecopes(){
		return this.scopes;
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
			if (Country.values()[i].getIso2().equals(iso2))
				return Country.values()[i].getName();
		}
		return null;
	}
	
	public String getSteetType() {
		return this.enterpriseInfo.getStreetType();
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
	
	public String getPaySheetModel() {
		return this.enterpriseInfo.getPaysheetModel();
	}
	
	public String getCostsModel() {
		return this.enterpriseInfo.getCostsModel();
	}
	
	public String getPaysheetSend() {
		return this.enterpriseInfo.getPaysheetSendType();
	}
	
	public String getPaysheetSendEmail() {
		return this.enterpriseInfo.getPaysheetEmail();
	}
	
	public String getAgreement() {
		return this.enterpriseInfo.getEnterpriseAgreementId();
	}
	
	public String getAgreementDescription() {
		String enterpriseAgreementId = getAgreement();
		if(AonStringUtils.isBlank(enterpriseAgreementId))
			return null;
		else {
			Integer agreementId = Integer.parseInt(enterpriseAgreementId);
			for(Agreement agreement : agreements)
				if(agreement.getId().equals(agreementId))
					return agreement.getDescription();
		}
		return null;
	}
	
	// ----------------------------------------------  SETTERS  -------------------------------------------------
	
	public void setName(String name) {
		add(enterpriseInfo::setName, 
			enterpriseInfo.getName(), 
			name );
		
		enterpriseInfo.setName(name);
	}
	
	public void setAlias(String alias) {
		add(enterpriseInfo::setAlias, 
			enterpriseInfo.getAlias(), 
			alias );
		
		enterpriseInfo.setAlias(alias);
	}
	
	public void setDocumentType(String documentType) {
		byte documentTypeByte = getDocumentTypeByte(documentType);
		
		add(enterpriseInfo::setDocumentType, 
			enterpriseInfo.getDocumentType(), 
			documentTypeByte );
		
		enterpriseInfo.setDocumentType(documentTypeByte);
	}
	
	public byte getDocumentTypeByte(String documentType) {
		switch (documentType) {
		case "DNI":
			return (byte) 0;
		case "CIF":
			return (byte) 1;
		case "Pasaporte":
			return (byte) 3;
		default:
			return (byte) 0;
		}
	}
	
	public void setDocument(String document) {
		add(enterpriseInfo::setDocument, 
			enterpriseInfo.getDocument(), 
			document );
		
		enterpriseInfo.setDocument(document);
	}
	
	public void setNationality(String nationality) {
		add(enterpriseInfo::setDocumentCountry, 
			enterpriseInfo.getDocumentCountry(), 
			nationality );
		
		enterpriseInfo.setDocumentCountry(nationality);
	}
	
	public void setAddressStreetType(String streetType){
		add(enterpriseInfo::setStreetType, 
			enterpriseInfo.getStreetType(), 
			streetType);
		
		enterpriseInfo.setStreetType(streetType);
	}
	
	public void setAddress(String address) {
		add(enterpriseInfo::setAddress, 
			enterpriseInfo.getAddress(), 
			address );
		
		enterpriseInfo.setAddress(address);
	}
	
	public void setAddressNum(String addressNum) {
		add(enterpriseInfo::setAddressNum, 
			enterpriseInfo.getAddressNum(), 
			addressNum );
		
		enterpriseInfo.setAddressNum(addressNum);
	}
	
	public void setAddressZip(String addressZip) {
		add(enterpriseInfo::setAddressZip, 
			enterpriseInfo.getAddressZip(), 
			addressZip );
		
		enterpriseInfo.setAddressZip(addressZip);
	}
	
	public void setAddressCity(String addressCity) {
		add(enterpriseInfo::setAddressCity, 
			enterpriseInfo.getAddressCity(), 
			addressCity );
		
		enterpriseInfo.setAddressCity(addressCity);
	}
	
	public void setAddressProvince(String addressProvince) {
		add(enterpriseInfo::setAddressProvince, 
			enterpriseInfo.getAddressProvince(), 
			addressProvince );
		
		enterpriseInfo.setAddressProvince(addressProvince);
	}
	
	public void setMobile (String mobile) {
		add(enterpriseInfo::setMobile, 
			enterpriseInfo.getMobile(), 
			mobile );
		
		enterpriseInfo.setMobile(mobile);
	}
	
	public void setPhone(String phone) {
		add(enterpriseInfo::setPhone, 
			enterpriseInfo.getPhone(), 
			phone );
		
		enterpriseInfo.setPhone(phone);
	}
	
	public void setEmail(String email) {
		add(enterpriseInfo::setEmail, 
			enterpriseInfo.getEmail(), 
			email );
		
		enterpriseInfo.setEmail(email);
	}
	
	public void setWeb(String web) {
		add(enterpriseInfo::setWeb, 
			enterpriseInfo.getWeb(), 
			web );
		
		enterpriseInfo.setWeb(web);
	}
	
	public void setScope(Integer scopeId) {
		add(enterpriseInfo::setScopeId, 
			enterpriseInfo.getScopeId(), 
			scopeId );
		
		enterpriseInfo.setScopeId(scopeId);
	}
	
	public void setPaySheetModel(String paySheetModel) {
		add(enterpriseInfo::setPaysheetModel, 
			enterpriseInfo.getPaysheetModel(), 
			paySheetModel );
		
		enterpriseInfo.setPaysheetModel(paySheetModel);
	}
	
	public void setCostModel(String costModel) {
		add(enterpriseInfo::setCostsModel, 
			enterpriseInfo.getCostsModel(), 
			costModel );
		
		enterpriseInfo.setCostsModel(costModel);
	}
	
	public void setPaySheetSendType(String paySheetModelTypeSend) {
		add(enterpriseInfo::setPaysheetSendType, 
			enterpriseInfo.getPaysheetSendType(), 
			paySheetModelTypeSend );
		
		enterpriseInfo.setPaysheetSendType(paySheetModelTypeSend);
	}
	
	public void setPaySheetSendEmail(String email) {
		add(enterpriseInfo::setPaysheetEmail, 
			enterpriseInfo.getPaysheetEmail(), 
			email );
		
		enterpriseInfo.setPaysheetEmail(email);
	}
	
	public void setAgreement(String agreementId) {
		add(enterpriseInfo::setEnterpriseAgreementId, 
			enterpriseInfo.getEnterpriseAgreementId(), 
			agreementId);
		
		enterpriseInfo.setEnterpriseAgreementId(agreementId);
	}
		
}
