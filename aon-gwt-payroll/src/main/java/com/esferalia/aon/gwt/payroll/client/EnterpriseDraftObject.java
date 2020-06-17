package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EnterpriseDraftObject extends AbstractDraftObject {

	private DomainEnterprisesServiceAsync enterprisesService;
	private Enterprise enterprise;
	
	private Map<Integer, String> scopes;
	private List<Agreement> agreements;
	
	private EnterpriseInfo enterpriseInfo;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EnterpriseDraftObject(Enterprise enterprise, DomainEnterprisesServiceAsync enterprisesService) {
		
		this.enterprisesService = enterprisesService;
		this.enterprise = enterprise;
		this.undoManager = new UndoManager<Undoable>();
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
	
	public void getAgreements(Consumer<List<Agreement>> success, Consumer<Throwable> failure) {
		enterprisesService.getAgreements(0, Integer.MAX_VALUE, new AsyncCallback<List<Agreement>>() {
			
			@Override
			public void onSuccess(List<Agreement> result) {
				agreements = getActiveAgreements(result);
				
				getEnterpriseScopes(
						s -> {success.accept(result);},
						f -> {}
					);
			}
			
			private List<Agreement> getActiveAgreements(List<Agreement> agreements) {
				List<Agreement> activeAgreements = new ArrayList<>();
				for(Agreement agreement : agreements){
					if(agreement.getId() > 0)
						activeAgreements.add(agreement);
				}
				return activeAgreements;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
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
	
	public void updateEnterprise(Consumer<EnterpriseInfo> success, Consumer<Throwable> failure) {
		
		enterprisesService.updateEnterprise(this.enterpriseInfo , new AsyncCallback<EnterpriseInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
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
	
	public Integer getScopeIndex(){
		Integer index = 0;
		for(Integer value : getEnterprisecopes().keySet()){
			if(value.equals(getScope())) 
				break;
			index ++;
		}
		return index;
	}
	
	public Integer getPaySheetModelIndex() {
		return null == this.enterpriseInfo.getPaysheetModel() ? 0 :  (int)this.enterpriseInfo.getPaysheetModel();
	}
	
	public Integer getCostsModelIndex() {
		return null == this.enterpriseInfo.getCostsModel() ? 0 :  (int)this.enterpriseInfo.getCostsModel();
	}
	
	public Integer getPaysheetSendIndex() {
		return null == this.enterpriseInfo.getPaysheetSendType() ? 0 :  (int)this.enterpriseInfo.getPaysheetSendType();
	}
	
	public String getPaysheetSendEmail() {
		return this.enterpriseInfo.getPaysheetEmail();
	}
	
	public Integer getEnterpriseAgreementIndex(){
		Integer index = 0;
		
		if(!getEnterpriseAgreements().isEmpty() && null != enterpriseInfo.getEnterpriseAgreementId()) {
			for(Agreement agreement : getEnterpriseAgreements()) {
				if(agreement.getId().equals(enterpriseInfo.getEnterpriseAgreementId())) {
					index++;
					break;
				}
				index++;
			}
		}
		
		return index;
	}
	
	public Integer getAgreement() {
		return this.enterpriseInfo.getEnterpriseAgreementId();
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
	
	public void setPaySheetModel(byte paySheetModel) {
		add(enterpriseInfo::setPaysheetModel, 
			enterpriseInfo.getPaysheetModel(), 
			paySheetModel );
		
		enterpriseInfo.setPaysheetModel(paySheetModel);
	}
	
	public void setCostModel(byte costModel) {
		add(enterpriseInfo::setCostsModel, 
			enterpriseInfo.getCostsModel(), 
			costModel );
		
		enterpriseInfo.setCostsModel(costModel);
	}
	
	public void setPaySheetSendType(byte paySheetModelTypeSend) {
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
	
	public void setAgreement(Integer agreementId) {
		if(-1 == agreementId)
			agreementId = null;
		
		add(enterpriseInfo::setEnterpriseAgreementId, 
			enterpriseInfo.getEnterpriseAgreementId(), 
			agreementId);
		
		enterpriseInfo.setEnterpriseAgreementId(agreementId);
	}
		
	// ------------------------------------------------- AUX METHODS -------------------------------------------------
	
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

}
