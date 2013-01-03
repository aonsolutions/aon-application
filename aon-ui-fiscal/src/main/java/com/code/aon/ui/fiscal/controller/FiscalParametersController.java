package com.code.aon.ui.fiscal.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.enumeration.TaxRegime;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class FiscalParametersController {
	
	public static final String FISCAL_PARAMS_BEAN_NAME = "fiscalParams";
	
	public static final String FS_DEFAULT_YEAR = "FS_DEFAULT_YEAR";
	public static final String FS_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static final String FS_ADMINISTRATION_CODE = "FS_ADMINISTRATION_CODE";
	public static final String FS_TAX_REFUND_REGISTRY = "FS_TAX_REFUND_REGISTRY";
	public static final String FS_TAX_REGIME = "FS_TAX_REGIME";

	private Map<String, ApplicationParameter> parameters;
	private IManagerBean managerBean;
	
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
						,FS_TAX_REGIME};
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
	
	public TaxRegime getTaxRegime() {
		String value = getParameters().get(FS_TAX_REGIME).getValue();
		TaxRegime taxRegime = null;
		try {
			int v = Integer.parseInt(value);
			taxRegime = TaxRegime.values()[v];
		} catch (NumberFormatException e) {
			
		}
		return taxRegime==null?TaxRegime.EDN:taxRegime; 
	}
	public void setTaxRegime(TaxRegime taxRegime) {
		getParameters().get(FS_TAX_REGIME).setValue(taxRegime==null?null:Integer.toString(taxRegime.ordinal()));
	}

	public Administration getDefaultAdministration() {
		String value = getParameters().get(FS_DEFAULT_ADMINISTRATION).getValue();
		Administration adm = null;
		try {
			int v = Integer.parseInt(value);
			adm = Administration.values()[v];
		} catch (NumberFormatException e) {
			
		}
		return adm; 
	}
	public void setDefaultAdministration(Administration defaultAdministration) {
		getParameters().get(FS_DEFAULT_ADMINISTRATION).setValue(defaultAdministration ==null?null:Integer.toString(defaultAdministration.ordinal()));
	}

	public boolean isCommonTerritoryDefaultAdministration() {
		return (getDefaultAdministration() == Administration.COMMON_TERRITORY);
	}
	
	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
			loadDefaultParameters();
		} catch (ManagerBeanException e) {
			String msg = "Unable to load defaultParameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onAccept(ActionEvent event) throws ManagerBeanException{
		Collection<ApplicationParameter>params = parameters.values();
		for(ApplicationParameter param : params){
			getManagerBean().update(param);
		}
		loadParameters();
		AonUtil.addInfoMessage("Los parámetros se guardaron correctamente.");		
	}

}
