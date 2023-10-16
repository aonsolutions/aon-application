package com.code.aon.ui.fiscal.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.finance.Creditor;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.TaxRegime;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;


public class FiscalParametersController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String FS_DEFAULT_YEAR = "FS_DEFAULT_YEAR";
	private static final String FS_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	private static final String FS_ADMINISTRATION_CODE = "FS_ADMINISTRATION_CODE";
	private static final String FS_TAX_REFUND_REGISTRY = "FS_TAX_REFUND_REGISTRY";
	private static final String FS_TAX_REGIME = "FS_TAX_REGIME";
	private static final String FS_ADMON_CREDITOR = "FS_ADMON_CREDITOR";
	private static final String FS_ADMON_VAT_CREDITOR = "FS_ADMON_VAT_CREDITOR";
	private static final String FS_ADMON_RETENTION_CREDITOR = "FS_ADMON_RETENTION_CREDITOR";
	private static final String FS_PERM_ADDRESS_CHANGES = "FS_PERM_ADDRESS_CHANGES";
	private static final String FS_CONCTACT_PERSON = "FS_CONCTACT_PERSON";
	private static final String FS_CONCTACT_PHONE = "FS_CONCTACT_PHONE";
	private static final String FS_CONCTACT_CELLULAR = "FS_CONCTACT_CELLULAR";
	private static final String FS_CONCTACT_MAIL = "FS_CONCTACT_MAIL";
	private static final String FS_MOD303_BY_DIFFERENCE_DISABLED = "FS_MOD303_BY_DIFFERENCE_DISABLED";
	private static final String FS_CUSTOMER_CHECK_ENABLED = "FS_CUSTOMER_CHECK_ENABLED";
	private static final String FS_PRES_MODEL_AUTO_ENABLED = "FS_PRES_MODEL_AUTO_ENABLED";
	
	private static final String FS_MODEL_CFG_M111 = "FS_MODEL_CFG_M111";
	private static final String FS_MODEL_CFG_M115 = "FS_MODEL_CFG_M115";
	private static final String FS_MODEL_CFG_M123 = "FS_MODEL_CFG_M123";
	private static final String FS_MODEL_CFG_M130 = "FS_MODEL_CFG_M130";
	private static final String FS_MODEL_CFG_M131 = "FS_MODEL_CFG_M131";
	private static final String FS_MODEL_CFG_M303_RS = "FS_MODEL_CFG_M303_RS";
	private static final String FS_MODEL_CFG_M303_RG = "FS_MODEL_CFG_M303_RG";
	private static final String FS_MODEL_CFG_M347 = "FS_MODEL_CFG_M347";
	private static final String FS_MODEL_CFG_M349 = "FS_MODEL_CFG_M349";
	private static final String FS_MODEL_CFG_M390_HF = "FS_MODEL_CFG_M390_HF";
	private static final String FS_MODEL_CFG_M390 = "FS_MODEL_CFG_M390";
	private static final String FS_MODEL_CFG_M180 = "FS_MODEL_CFG_M180";
	private static final String FS_MODEL_CFG_M190 = "FS_MODEL_CFG_M190";
	private static final String FS_MODEL_CFG_M200 = "FS_MODEL_CFG_M200";
	private static final String FS_MODEL_CFG_M202 = "FS_MODEL_CFG_M202";
	private static final String FS_MODEL_CFG_M184 = "FS_MODEL_CFG_M184";
	private static final String FS_MODEL_CFG_M193 = "FS_MODEL_CFG_M193";
	private static final String FS_MODEL_CFG_SII = "FS_MODEL_CFG_SII";
	
	public static final String FISCAL_PARAMS_BEAN_NAME = "fiscalParams";
	
	private Map<String, ApplicationParameter> parameters;
	private IManagerBean managerBean;
	private Company company;
	
	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany( companyController.obtainCompany() );
		}
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Map<String, ApplicationParameter> getParameters() {
		try {
			if (parameters == null) {
				loadParameters();
				loadDefaultParameters();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		return parameters;
	}
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (managerBean == null) {
			managerBean = BeanManager.getManagerBean(ApplicationParameter.class);	
		}
		return managerBean;
	}
	
	private void loadParameters() throws ManagerBeanException {
		parameters = new TreeMap<String, ApplicationParameter>();
		List<ITransferObject> list = getManagerBean().getList(getCriteria());
		for (ITransferObject to: list) {
			ApplicationParameter appParam = (ApplicationParameter) to;
			parameters.put(appParam.getName(), appParam);
		}
	}

	private void loadDefaultParameters() throws ManagerBeanException {
		String[] keys = {FS_DEFAULT_ADMINISTRATION
						,FS_DEFAULT_YEAR
						,FS_ADMINISTRATION_CODE
						,FS_TAX_REFUND_REGISTRY
						,FS_TAX_REGIME
						,FS_ADMON_CREDITOR
						,FS_ADMON_VAT_CREDITOR
						,FS_ADMON_RETENTION_CREDITOR
						,FS_PERM_ADDRESS_CHANGES
						,FS_CONCTACT_PERSON
						,FS_CONCTACT_PHONE
						,FS_CONCTACT_CELLULAR
						,FS_CONCTACT_MAIL
						,FS_MOD303_BY_DIFFERENCE_DISABLED
						,FS_CUSTOMER_CHECK_ENABLED
						,FS_PRES_MODEL_AUTO_ENABLED
						,FS_MODEL_CFG_M111
						,FS_MODEL_CFG_M115
						,FS_MODEL_CFG_M123
						,FS_MODEL_CFG_M130
						,FS_MODEL_CFG_M131
						,FS_MODEL_CFG_M303_RS
						,FS_MODEL_CFG_M303_RG
						,FS_MODEL_CFG_M347
						,FS_MODEL_CFG_M349
						,FS_MODEL_CFG_M390_HF
						,FS_MODEL_CFG_M390
						,FS_MODEL_CFG_M180
						,FS_MODEL_CFG_M190
						,FS_MODEL_CFG_M200
						,FS_MODEL_CFG_M202
						,FS_MODEL_CFG_M184
						,FS_MODEL_CFG_M193
						,FS_MODEL_CFG_SII
						};
		
		for (String key : keys) {
			if (!parameters.containsKey(key)) {
				ApplicationParameter p = new ApplicationParameter();
				p.setName(key);
				p.setValue(null);
				p.setSystemParameter(true);
				getManagerBean().insert(p);
				parameters.put(key, p);
			}
		}
	}

	private Criteria getCriteria() throws ManagerBeanException {
		try {
			Criteria criteria = new Criteria();
			String nameAlias = getManagerBean().getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME);
			criteria.addExpression(nameAlias, "FS_*");
			return criteria;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} 
	}
	public String getDefaultYear() {
		return getParameters().get(FS_DEFAULT_YEAR).getValue(); 
	}
	public void setDefaultYear(String defaultYear) {
		getParameters().get(FS_DEFAULT_YEAR).setValue(defaultYear);
	}
	
	public String getAdministrationCode() {
		return getParameters().get(FS_ADMINISTRATION_CODE).getValue(); 
	}
	public void setAdministrationCode(String defaultYear) {
		getParameters().get(FS_ADMINISTRATION_CODE).setValue(defaultYear);
	}

	public boolean isTaxRefundRegistry() {
		String value = getParameters().get(FS_TAX_REFUND_REGISTRY).getValue();
		return (value!=null && "1".equals(value)); 
	}
	public void setTaxRefundRegistry(boolean taxRefundRegistry) {
		getParameters().get(FS_TAX_REFUND_REGISTRY).setValue(taxRefundRegistry?"1":"0");
	}
	
	public boolean isMod303AvailableByDifferenceDisabled() {
		String value = getParameters().get(FS_MOD303_BY_DIFFERENCE_DISABLED).getValue();
		return (value!=null && "1".equals(value)); 
	}
	public void setMod303AvailableByDifferenceDisabled(boolean mod303ByDifferenceDisabled) {
		getParameters().get(FS_MOD303_BY_DIFFERENCE_DISABLED).setValue(mod303ByDifferenceDisabled?"1":"0");
	}
	
	public boolean isFiscalCustomerCheckEnabled() {
		String value = getParameters().get(FS_CUSTOMER_CHECK_ENABLED).getValue();
		return (value!=null && ("1".equals(value) || Boolean.valueOf(value))); 
	}
	public void setFiscalCustomerCheckEnabled(boolean customerCheckEnabled) {
		getParameters().get(FS_CUSTOMER_CHECK_ENABLED).setValue(customerCheckEnabled?"1":"0");
	}

	public boolean isFiscalPresModelAutoEnabled() {
		String value = getParameters().get(FS_PRES_MODEL_AUTO_ENABLED).getValue();
		return (value!=null && ("1".equals(value) || Boolean.valueOf(value))); 
	}
	public void setFiscalPresModelAutoEnabled(boolean presModelAutoEnabled) {
		getParameters().get(FS_PRES_MODEL_AUTO_ENABLED).setValue(presModelAutoEnabled?"1":"0");
	}

	public boolean isPermanentAddressChanges() {
		String value = getParameters().get(FS_PERM_ADDRESS_CHANGES).getValue();
		return (value!=null && "1".equals(value)); 
	}
	public void setPermanentAddressChanges(boolean permanentAddressChanges) {
		getParameters().get(FS_PERM_ADDRESS_CHANGES).setValue(permanentAddressChanges?"1":"0");
	}

	public TaxRegime getTaxRegime() {
		String value = getParameters().get(FS_TAX_REGIME).getValue();
		TaxRegime taxRegime = null;
		try {
			int v = Integer.parseInt(value);
			taxRegime = TaxRegime.values()[v];
		} catch (NumberFormatException e) {
			
		}
		return taxRegime==null?TaxRegime.BUSINESS_SOCIETY:taxRegime; 
	}
	public void setTaxRegime(TaxRegime taxRegime) {
		getParameters().get(FS_TAX_REGIME).setValue(taxRegime==null?null:Integer.toString(taxRegime.ordinal()));
	}
	
	public Creditor getAdmonCreditor() {
		String value = getParameters().get(FS_ADMON_CREDITOR).getValue();
		Creditor creditor = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
			if (StringUtils.isNotBlank(value)) {
				int id = Integer.parseInt(value);
				creditor = (Creditor) bean.get(id);
			}
			if ( creditor == null ) {
				getParameters().get(FS_ADMON_CREDITOR).setValue(null);
				creditor = (Creditor) bean.createNewTo();	
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			creditor = new Creditor();
		}
		return creditor;
	}
	public void setAdmonCreditor(Creditor creditor) {
		Integer id = (creditor==null?null:creditor.getId()); 
		getParameters().get(FS_ADMON_CREDITOR).setValue(id==null?null:Integer.toString( id ));
	}

	public Creditor getAdmonVatCreditor() {
		String value = getParameters().get(FS_ADMON_VAT_CREDITOR).getValue();
		Creditor creditor = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
			if (StringUtils.isNotBlank(value)) {
				int id = Integer.parseInt(value);
				creditor = (Creditor) bean.get(id);
			}
			if ( creditor == null ) {
				getParameters().get(FS_ADMON_VAT_CREDITOR).setValue(null);
				creditor = (Creditor) bean.createNewTo();	
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			creditor = new Creditor();
		}
		return creditor;
	}
	public void setAdmonVatCreditor(Creditor creditor) {
		Integer id = (creditor==null?null:creditor.getId()); 
		getParameters().get(FS_ADMON_VAT_CREDITOR).setValue(id==null?null:Integer.toString( id ));
	}

	public Creditor getAdmonRetentionCreditor() {
		String value = getParameters().get(FS_ADMON_RETENTION_CREDITOR).getValue();
		Creditor creditor = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
			if (StringUtils.isNotBlank(value)) {
				int id = Integer.parseInt(value);
				creditor = (Creditor) bean.get(id);
			}
			if ( creditor == null ) {
				getParameters().get(FS_ADMON_RETENTION_CREDITOR).setValue(null);
				creditor = (Creditor) bean.createNewTo();	
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			creditor = new Creditor();
		}
		return creditor;
	}
	public void setAdmonRetentionCreditor(Creditor creditor) {
		Integer id = (creditor==null?null:creditor.getId()); 
		getParameters().get(FS_ADMON_RETENTION_CREDITOR).setValue(id==null?null:Integer.toString( id ));
	}

	public Administration getDefaultAdministration() {
		String value = getParameters().get(FS_DEFAULT_ADMINISTRATION).getValue();
		Administration adm = null;
		try {
			int v = Integer.parseInt(value);
			adm = Administration.values()[v];
		} catch (NumberFormatException e) {
			
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			
		}
		return adm; 
	}
	
	public boolean isAdministrationEmpty() {
		return getDefaultAdministration() == null;
	}
	public String getDefaultAdministrationModelName(String model) {
		if (isAdministrationEmpty()) return model; 
		FiscalModelType mod = FiscalModelType.safeValueByName(model);
		if (mod == null) return model;
		com.esferalia.aon.occam.api.model.fiscal.FiscalModel fm = new com.esferalia.aon.occam.api.model.fiscal.FiscalModel();
		com.esferalia.aon.occam.api.model.type.Administration adm = com.esferalia.aon.occam.api.model.type.Administration.values()[ getDefaultAdministration().ordinal() ];
		fm.setAdministration( adm );
		fm.setModel( mod );
		if (mod.isYearly() ) {
			fm.setPeriod( Period.YEAR );
			return FiscalModelUtils.getModelName( fm );
		} else {
			fm.setPeriod( Period.T1 );
			String q = FiscalModelUtils.getModelName( fm );
			fm.setPeriod( Period.M01 );
			String m = FiscalModelUtils.getModelName( fm );
			return (AonStringUtils.equals(q,m)) ? q : (q+"/"+m);
		}
		
	}
	
	public boolean isAeat() {
		return getDefaultAdministration() == null || getDefaultAdministration() == Administration.COMMON_TERRITORY;
	}
	public boolean isAraba() {
		return getDefaultAdministration() == null || getDefaultAdministration() == Administration.ALAVA;
	}
	public boolean isBizkaia() {
		return getDefaultAdministration() == null || getDefaultAdministration() == Administration.BIZKAIA;
	}
	public boolean isGipuzkoa() {
		return getDefaultAdministration() == null || getDefaultAdministration() == Administration.GIPUZKOA;
	}
	public boolean isNavarra() {
		return getDefaultAdministration() == null || getDefaultAdministration() == Administration.NAVARRA;
	}
	
	public void setDefaultAdministration(Administration defaultAdministration) {
		getParameters().get(FS_DEFAULT_ADMINISTRATION).setValue(defaultAdministration ==null?null:Integer.toString(defaultAdministration.ordinal()));
	}

	public boolean isPersonaFisica() {
		String document = getCompany().getDocument();
		return !AonDocumentUtil.isValidCIF(document) || AonDocumentUtil.isAssetCommunity(document)
			|| AonDocumentUtil.isOwnerCommunity(document) || AonDocumentUtil.isCivilSociety(document);
	}
	
	public String getContactPerson() {
		String contact = getParameters().get(FS_CONCTACT_PERSON).getValue();
		if (StringUtils.isBlank(contact)) {
			if (getCompany().getType() == RegistryType.NATURAL) {
				contact = getCompany().getName(); 
			}
		}
		return  contact;
	}
	public void setContactPerson(String contactPerson) {
		getParameters().get(FS_CONCTACT_PERSON).setValue(contactPerson);
	}

	public String getContactPhone() {
		String contact = getParameters().get(FS_CONCTACT_PHONE).getValue();
		if (StringUtils.isBlank(contact)) {
			RegistryMedia media = null;
			try {
				media = getCompany().getPhone();
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			contact = media==null?null:media.getValue(); 
		}
		return contact; 
	}
	public void setContactPhone(String contactPhone) {
		getParameters().get(FS_CONCTACT_PHONE).setValue(contactPhone);
	}

	public String getContactCellular() {
		String contact = getParameters().get(FS_CONCTACT_CELLULAR).getValue();
		if (StringUtils.isBlank(contact)) {
			RegistryMedia media = null;
			try {
				media = getCompany().getCellular();
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			contact = media==null?null:media.getValue(); 
		}
		return contact;  
	}
	public void setContactCellular(String contactCellular) {
		getParameters().get(FS_CONCTACT_CELLULAR).setValue(contactCellular);
	}

	public String getContactMail() {
		String contact = getParameters().get(FS_CONCTACT_MAIL).getValue();
		if (StringUtils.isBlank(contact)) {
			RegistryMedia media = null;
			try {
				media = getCompany().getEmail();
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			contact = media==null?null:media.getValue(); 
		}
		return contact;  
	}
	public void setContactMail(String contactMail) {
		getParameters().get(FS_CONCTACT_MAIL).setValue(contactMail);
	}

	public boolean isCommonTerritoryDefaultAdministration() {
		return (getDefaultAdministration() == Administration.COMMON_TERRITORY);
	}
	
	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
			loadDefaultParameters();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar los parámetros";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public String getMod111() {
		return getParameters().get(FS_MODEL_CFG_M111).getValue();
	}
	public void setMod111(String config) {
		getParameters().get(FS_MODEL_CFG_M111).setValue(config);		
	}
	public String getMod115() {
		return getParameters().get(FS_MODEL_CFG_M115).getValue();
	}
	public void setMod115(String config) {
		getParameters().get(FS_MODEL_CFG_M115).setValue(config);		
	}
	public String getMod123() {
		return getParameters().get(FS_MODEL_CFG_M123).getValue();
	}
	public void setMod123(String config) {
		getParameters().get(FS_MODEL_CFG_M123).setValue(config);		
	}
	public String getMod130() {
		return getParameters().get(FS_MODEL_CFG_M130).getValue();
	}
	public void setMod130(String config) {
		getParameters().get(FS_MODEL_CFG_M130).setValue(config);		
	}
	public String getMod131() {
		return getParameters().get(FS_MODEL_CFG_M131).getValue();
	}
	public void setMod131(String config) {
		getParameters().get(FS_MODEL_CFG_M131).setValue(config);		
	}
	public String getMod303RS() {
		return getParameters().get(FS_MODEL_CFG_M303_RS).getValue();
	}
	public void setMod303RS(String config) {
		getParameters().get(FS_MODEL_CFG_M303_RS).setValue(config);		
	}
	public String getMod303RG() {
		return getParameters().get(FS_MODEL_CFG_M303_RG).getValue();
	}
	public void setMod303RG(String config) {
		getParameters().get(FS_MODEL_CFG_M303_RG).setValue(config);		
	}
	public String getMod347() {
		return getParameters().get(FS_MODEL_CFG_M347).getValue();
	}
	public void setMod347(String config) {
		getParameters().get(FS_MODEL_CFG_M347).setValue(config);		
	}
	public String getMod349() {
		return getParameters().get(FS_MODEL_CFG_M349).getValue();
	}
	public void setMod349(String config) {
		getParameters().get(FS_MODEL_CFG_M349).setValue(config);		
	}
	public String getMod390HF() {
		return getParameters().get(FS_MODEL_CFG_M390_HF).getValue();
	}
	public void setMod390HF(String config) {
		getParameters().get(FS_MODEL_CFG_M390_HF).setValue(config);		
	}
	public String getMod390() {
		return getParameters().get(FS_MODEL_CFG_M390).getValue();
	}
	public void setMod390(String config) {
		getParameters().get(FS_MODEL_CFG_M390).setValue(config);		
	}
	public String getMod180() {
		return getParameters().get(FS_MODEL_CFG_M180).getValue();
	}
	public void setMod180(String config) {
		getParameters().get(FS_MODEL_CFG_M180).setValue(config);		
	}
	public String getMod190() {
		return getParameters().get(FS_MODEL_CFG_M190).getValue();
	}
	public void setMod190(String config) {
		getParameters().get(FS_MODEL_CFG_M190).setValue(config);		
	}
	public String getMod200() {
		return getParameters().get(FS_MODEL_CFG_M200).getValue();
	}
	public void setMod200(String config) {
		getParameters().get(FS_MODEL_CFG_M200).setValue(config);		
	}
	public String getMod202() {
		return getParameters().get(FS_MODEL_CFG_M202).getValue();
	}
	public void setMod202(String config) {
		getParameters().get(FS_MODEL_CFG_M202).setValue(config);		
	}
	public String getMod193() {
		return getParameters().get(FS_MODEL_CFG_M193).getValue();
	}
	public void setMod193(String config) {
		getParameters().get(FS_MODEL_CFG_M193).setValue(config);		
	}
	public String getMod184() {
		return getParameters().get(FS_MODEL_CFG_M184).getValue();
	}
	public void setMod184(String config) {
		getParameters().get(FS_MODEL_CFG_M184).setValue(config);		
	}
	
	public String getSii() {
		return getParameters().get(FS_MODEL_CFG_SII).getValue();		
	}
	public void setSii(String config) {
		getParameters().get(FS_MODEL_CFG_SII).setValue(config);		
	}

	public void onAccept(ActionEvent event) {
		try {
			Collection<ApplicationParameter>params = parameters.values();
			for(ApplicationParameter param : params){
				getManagerBean().update(param);
			}
			loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "Los parámetros no se guardaron correctamente. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

}
