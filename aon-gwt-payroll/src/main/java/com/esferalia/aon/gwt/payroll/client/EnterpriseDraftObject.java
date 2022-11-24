package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class EnterpriseDraftObject extends AbstractDraftObject {

	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private Enterprise enterprise;
	
	private Map<Integer, String> scopes;
	private List<Agreement> agreements;
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public EnterpriseDraftObject(com.esferalia.aon.gwt.payroll.shared.Enterprise enterprise) {
		this.enterprise = new Enterprise();
		this.enterprise.setId(enterprise.getId());
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

	public void initializeEnterprise(Consumer<Enterprise> success, Consumer<Throwable> failure) {
	
		enterprisesService.getEnterprise(this.enterprise.getId() , new AsyncCallback<Enterprise>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);		
			}

			@Override
			public void onSuccess(Enterprise enterpriseIn) {
				enterprise = enterpriseIn;
				success.accept(enterprise);
			}
		});	
	}
	
	public void saveEnterprise(Consumer<Void> success, Consumer<Throwable> failure) {
		
		enterprisesService.saveEnterprise(this.enterprise , new AsyncCallback<Void>() {

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
	
	public void setScopes(Map<Integer, String> scopesContext) {
		scopes = scopesContext;
	}
	
	public void setAgreements(List<Agreement> agreementsContext) {
		agreements = getActiveAgreements(agreementsContext);
	}
	
	private List<Agreement> getActiveAgreements(List<Agreement> agreements) {
		List<Agreement> activeAgreements = new ArrayList<>();
		for(Agreement agreement : agreements){
			if(agreement.getId() > 0)
				activeAgreements.add(agreement);
		}
		return activeAgreements;
	}
	
	
	// ---------------------------------------------- GETTERS  -------------------------------------------------
	
	public Enterprise getEnterpriseInfo() {
		return this.enterprise;
	}

	public List<Agreement> getEnterpriseAgreements() {
		return agreements;
	}
	
	public Map<Integer, String> getEnterprisecopes(){
		return this.scopes;
	}
	
	public String getName() {
		return this.enterprise.getName();
	}
	
	public String getAlias() {
		return this.enterprise.getAlias();
	}
	
	public Byte getDocumentType() {
		return (byte) this.enterprise.getDocumentType().ordinal();
	}
	
	public String getDocument() {
		return this.enterprise.getDocument();
	}
	
	public String getDocumentCountry() {
		return this.enterprise.getDocumentCountry().getName();
	}
	
	public String getSteetType() {
		return this.enterprise.getAddress().getStreetType().getIneCode();
	}
	
	public String getAddress() {
		return this.enterprise.getAddress().getAddress();
	}
	
	public String getAddressNum() {
		return this.enterprise.getAddress().getNumber();
	}
	
	public String getAddressZip() {
		return this.enterprise.getAddress().getZip();
	}
	
	public String getAddressCity() {
		return this.enterprise.getAddress().getCity();
	}
	
	public String getMunicipalityCode() {
		return this.enterprise.getAddress().getMunicipalityCode();
	}
	
	public String getAddressProvince() {
		return this.enterprise.getAddress().getProvince();
	}
	
	public String getMobile() {
		Optional<RegistryMedia> mobile = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.CELLULAR).findFirst();
		return mobile.isPresent() ? mobile.get().getValue() : null;
	}
	
	public String getPhone() {
		Optional<RegistryMedia> phone = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.FIXED_PHONE).findFirst();
		return phone.isPresent() ? phone.get().getValue() : null;
	}
	
	public String getEmail() {
		Optional<RegistryMedia> email = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.EMAIL).findFirst();
		return email.isPresent() ? email.get().getValue() : null;
	}
	
	public String getWeb() {
		Optional<RegistryMedia> web = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.WEB).findFirst();
		return web.isPresent() ? web.get().getValue() : null;
	}
	
	public Integer getScope() {
		return this.enterprise.getScope();
	}
	
	public String getPaySheetModel() {
		Optional<EnterpriseData> paySheetModel = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_REPORT_salary_PAY")).findFirst();
		return paySheetModel.isPresent() ? paySheetModel.get().getExpression() : null;
	}
	
	public String getCostsModel() {
		Optional<EnterpriseData> costModel = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_REPORT_enterpriseSalary_PAY")).findFirst();
		return costModel.isPresent() ? costModel.get().getExpression() : null;
	}
	
	public String getPaysheetSend() {
		Optional<EnterpriseData> paysheetSend = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySendingMethod_PAY")).findFirst();
		return paysheetSend.isPresent() ? paysheetSend.get().getExpression() : null;
	}
	
	public String getPaysheetSendEmail() {
		Optional<EnterpriseData> paysheetSendEmail = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySending_email_PAY")).findFirst();
		return paysheetSendEmail.isPresent() ? paysheetSendEmail.get().getExpression() : null;
	}
	
	public String getAgreement() {
		Optional<EnterpriseData> agreement = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("agreement")).findFirst();
		return agreement.isPresent() ? agreement.get().getExpression() : null;
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
		add(enterprise::setName, 
			enterprise.getName(), 
			name );
		
		enterprise.setName(name);
	}
	
	public void setAlias(String alias) {
		add(enterprise::setAlias, 
				enterprise.getAlias(), 
			alias );
		
		enterprise.setAlias(alias);
	}
	
	public void setDocumentType(DocumentType documentType) {
		add(enterprise::setDocumentType, 
			enterprise.getDocumentType(), 
			documentType );
		
		enterprise.setDocumentType(documentType);
	}
	
	public void setDocument(String document) {
		add(enterprise::setDocument, 
				enterprise.getDocument(), 
			document );
		
		enterprise.setDocument(document);
	}
	
	public void setNationality(Country country) {
		add(enterprise::setDocumentCountry, 
				enterprise.getDocumentCountry(), 
				country );
		
		enterprise.setDocumentCountry(country);
	}
	
	public void setAddressStreetType(StreetType streetType){
		enterprise.getAddress().setStreetType(streetType);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setAddress(String address) {
		enterprise.getAddress().setAddress(address);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setAddressNum(String number) {
		enterprise.getAddress().setNumber(number);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setAddressZip(String zip) {
		enterprise.getAddress().setZip(zip);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setAddressCity(String city) {
		enterprise.getAddress().setCity(city);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setAddressMunicipalityCode(String municipalityCode) {
		enterprise.getAddress().setMunicipalityCode(municipalityCode);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setAddressProvince(String province) {
		enterprise.getAddress().setProvince(province);
		
		add(enterprise::setAddress, 
			enterprise.getAddress(), 
			enterprise.getAddress() );
		
		enterprise.setAddress(enterprise.getAddress());
	}
	
	public void setMobile (String mobile) {
		Optional<RegistryMedia> mobileRM = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.CELLULAR).findFirst();
		if(mobileRM.isPresent()) {
			mobileRM.get().setValue(mobile);
			mobileRM.get().setRemoved(AonStringUtils.isBlank(mobile));
		} else
			this.enterprise.getMedias().add(new RegistryMedia()
				.setDomain(enterprise.getDomain())
				.setRegistry(enterprise.getId())
				.setMedia(MediaType.CELLULAR)
				.setRaddress(enterprise.getAddress() != null ? enterprise.getAddress().getId() : null)
				.setValue(mobile));
	}
	
	public void setPhone(String phone) {
		Optional<RegistryMedia> phoneRM = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.FIXED_PHONE).findFirst();
		if(phoneRM.isPresent()) {
			phoneRM.get().setValue(phone);
			phoneRM.get().setRemoved(AonStringUtils.isBlank(phone));
		} else
			this.enterprise.getMedias().add(new RegistryMedia()
				.setDomain(enterprise.getDomain())
				.setRegistry(enterprise.getId())
				.setMedia(MediaType.FIXED_PHONE)
				.setRaddress(enterprise.getAddress() != null ? enterprise.getAddress().getId() : null)
				.setValue(phone));
	}
	
	public void setEmail(String email) {
		Optional<RegistryMedia> emailRM = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.EMAIL).findFirst();
		if(emailRM.isPresent()) {
			emailRM.get().setValue(email);
			emailRM.get().setRemoved(AonStringUtils.isBlank(email));
		} else
			this.enterprise.getMedias().add(new RegistryMedia()
				.setDomain(enterprise.getDomain())
				.setRegistry(enterprise.getId())
				.setMedia(MediaType.EMAIL)
				.setRaddress(enterprise.getAddress() != null ? enterprise.getAddress().getId() : null)
				.setValue(email));
	}
	
	public void setWeb(String web) {
		Optional<RegistryMedia> webRM = this.enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.WEB).findFirst();
		if(webRM.isPresent()) {
			webRM.get().setValue(web);
			webRM.get().setRemoved(AonStringUtils.isBlank(web));
		} else 
			this.enterprise.getMedias().add(new RegistryMedia()
				.setDomain(enterprise.getDomain())
				.setRegistry(enterprise.getId())
				.setMedia(MediaType.WEB)
				.setRaddress(enterprise.getAddress() != null ? enterprise.getAddress().getId() : null)
				.setValue(web));
	}
	
	public void setScope(Integer scope) {
		add(enterprise::setScope, 
			enterprise.getScope(), 
			scope );
		
		enterprise.setScope(scope);
	}
	
	public void setPaySheetModel(String paySheetModel) {
		Optional<EnterpriseData> paySheetModelRM = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_REPORT_salary_PAY")).findFirst();
		if(paySheetModelRM.isPresent()) {
			paySheetModelRM.get().setExpression(paySheetModel);
			paySheetModelRM.get().setIsRemoved(AonStringUtils.isBlank(paySheetModel));
		} else
			this.enterprise.getDatas().add(new EnterpriseData()
				.setDomain(enterprise.getDomain())
				.setEnterprise(enterprise.getId())
				.setName("PAY_REPORT_salary_PAY")
				.setExpression(paySheetModel));
	}
	
	public void setCostModel(String costModel) {
		Optional<EnterpriseData> costModelRM = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_REPORT_enterpriseSalary_PAY")).findFirst();
		if(costModelRM.isPresent()) {
			costModelRM.get().setExpression(costModel);
			costModelRM.get().setIsRemoved(AonStringUtils.isBlank(costModel));
		} else
			this.enterprise.getDatas().add(new EnterpriseData()
				.setDomain(enterprise.getDomain())
				.setEnterprise(enterprise.getId())
				.setName("PAY_REPORT_enterpriseSalary_PAY")
				.setExpression(costModel));
	}
	
	public void setPaySheetSendType(String paySheetModelTypeSend) {
		Optional<EnterpriseData> paySheetModelTypeSendRM = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySendingMethod_PAY")).findFirst();
		if(paySheetModelTypeSendRM.isPresent()) {
			paySheetModelTypeSendRM.get().setExpression(paySheetModelTypeSend);
			paySheetModelTypeSendRM.get().setIsRemoved(AonStringUtils.isBlank(paySheetModelTypeSend));
		} else
			this.enterprise.getDatas().add(new EnterpriseData()
				.setDomain(enterprise.getDomain())
				.setEnterprise(enterprise.getId())
				.setName("PAY_salarySendingMethod_PAY")
				.setExpression(paySheetModelTypeSend));
		
		if(!AonStringUtils.equals(paySheetModelTypeSend, "EMAIL")) {
			Optional<EnterpriseData> email = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySending_email_PAY")).findFirst();
			email.ifPresent(emailIt -> emailIt.setIsRemoved(true));
		}
	}
	
	public void setPaySheetSendEmail(String email) {
		Optional<EnterpriseData> emailRM = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySending_email_PAY")).findFirst();
		if(emailRM.isPresent()) {
			emailRM.get().setExpression(email);
			emailRM.get().setIsRemoved(AonStringUtils.isBlank(email));
		} else
			this.enterprise.getDatas().add(new EnterpriseData()
				.setDomain(enterprise.getDomain())
				.setEnterprise(enterprise.getId())
				.setName("PAY_salarySending_email_PAY")
				.setExpression(email));
	}
	
	public void setAgreement(String agreementId) {
		Optional<EnterpriseData> agreementRM = this.enterprise.getDatas().stream().filter(f -> f.getName().equals("agreement")).findFirst();
		if(agreementRM.isPresent()) {
			agreementRM.get().setExpression(agreementId);
			agreementRM.get().setIsRemoved(AonStringUtils.isBlank(agreementId));
		} else
			this.enterprise.getDatas().add(new EnterpriseData()
				.setDomain(enterprise.getDomain())
				.setEnterprise(enterprise.getId())
				.setName("agreement")
				.setExpression(agreementId));
	}
		
}
