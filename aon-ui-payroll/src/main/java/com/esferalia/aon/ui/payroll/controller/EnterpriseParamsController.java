package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseData;
import com.code.aon.company.enumeration.SalaryTemplate;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;

public class EnterpriseParamsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Map<String, EnterpriseData> parameters;

	private Map<String, String> defaultParameters;
	

	public Map<String, EnterpriseData> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, EnterpriseData> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getDefaultParameters() {
		return defaultParameters;
	}

	public void setDefaultParameters(Map<String, String> defaultParameters) {
		this.defaultParameters = defaultParameters;
		if (defaultParameters != null) {
			for (String key : defaultParameters.keySet()) {
				String value = defaultParameters.get(key);
				if ("[NULL]".equals(value)) {
					defaultParameters.put(key,null);	
				}
			}
		}
	}

	public void onAccept(ActionEvent event) {
		try {
			accept();
			acceptAgreement();
		} catch (ManagerBeanException e) {
			String msg = "Unable to save parameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void accept() throws ManagerBeanException{
		IManagerBean managerBean = BeanManager.getManagerBean(EnterpriseData.class);
		Collection<EnterpriseData>params = parameters.values();
		for(EnterpriseData param : params){
			beforeBeanUpdate();
			managerBean.insertOrUpdate(param);
		}
		loadParameters();
	}

	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
			loadAgreementData();
			loadOptionalSalaryTemplatesParam();
		} catch (ManagerBeanException e) {
			String msg = "Unable to load defaultParameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void loadParameters() throws ManagerBeanException{
		parameters = new TreeMap<String, EnterpriseData>();
		IManagerBean managerBean = BeanManager.getManagerBean(EnterpriseData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_DOMAIN), DomainManager.getCurrentDomain());
		List<ITransferObject> list = managerBean.getList(criteria);
		Iterator<ITransferObject> iter = list.iterator();
		while (iter.hasNext()) {
			EnterpriseData appParam = (EnterpriseData) iter.next();
			parameters.put(appParam.getName(), appParam);
		}
		Set<String> keys = defaultParameters.keySet();
		for (String key : keys) {
			if (!parameters.containsKey(key)) {
				EnterpriseData p = new EnterpriseData();
				p.setEnterprise(getEnterprise());
				p.setName(key);
				p.setExpression(defaultParameters.get(key));
				p.setStartDate(null);
				p.setEndDate(null);
				parameters.put(p.getName(), p);
			}
		}
	}

	public EnterpriseData getParameter(String key) throws ManagerBeanException {
		if (parameters == null || parameters.isEmpty()) {
			loadParameters();	
		}
		return parameters.get(key); 		
	}
	
	private Enterprise getEnterprise() {
		IController controller = FormUtil.getController(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		return (Enterprise) controller.getTo();
	}
	
	private void beforeBeanUpdate() throws ManagerBeanException {
		if(!DomainManager.isDomainManagementAvailable()){
			getParameter(ICompanyConstants.REPORT_SALARY_DRAFT_PARAM).setExpression(getDraftTemplateName());
		}
		restoreOptionalTemplatesParam();
	}
	
	private String getDraftTemplateName() throws ManagerBeanException {
		return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getExpression().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
	}
	
	
	// AGREEMENT 
	private EnterpriseData agreementData;
	private Agreement agreement;
	private static final String AGREEMENT = "agreement";
	
	public EnterpriseData getAgreementData() {
		return agreementData;
	}
	public void setAgreementData(EnterpriseData agreementData) {
		this.agreementData = agreementData;
	}
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}

	private void acceptAgreement() throws ManagerBeanException{
		if(getAgreementData()!=null && getAgreement()!=null && getAgreement().getId()!=null){
			IController controller = FormUtil.getController(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
			getAgreementData().setEnterprise((Enterprise) controller.getTo());
			getAgreementData().setName(AGREEMENT);
			String id = (getAgreement()!=null && getAgreement().getId()!=null)?getAgreement().getId().toString():"";
			getAgreementData().setExpression(id);
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseData.class);
			bean.insertOrUpdate(getAgreementData());
		} else if(getAgreementData()!=null && getAgreementData().getExpression()!=null && (getAgreement()==null || getAgreement().getId()==null)){
			try {
				IManagerBean bean = BeanManager.getManagerBean(EnterpriseData.class);
				bean.remove(getAgreementData().getId());
			} catch (NumberFormatException e) {
				// nada
			}
		}
	}

	private void loadAgreementData() throws ManagerBeanException {
		IManagerBean dataBean = BeanManager.getManagerBean(EnterpriseData.class);
		IController controller = FormUtil.getController(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_NAME), AGREEMENT);
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_ENTERPRISE_ID), ((Enterprise) controller.getTo()).getId());
		List<ITransferObject> list = dataBean.getList(criteria);
		IManagerBean agreementBean = BeanManager.getManagerBean(Agreement.class);
		if(list.isEmpty()){
			setAgreementData(new EnterpriseData());
			setAgreement((Agreement) agreementBean.createNewTo());
		} else {
			setAgreementData((EnterpriseData)list.get(0));
			if(StringUtils.isBlank(getAgreementData().getExpression())){
				setAgreement((Agreement) agreementBean.createNewTo());
			} else {
				Agreement agreement = null;
				try {
					agreement = (Agreement) agreementBean.get(Integer.parseInt(getAgreementData().getExpression()));
				} catch ( NumberFormatException e ) {
				}
				if(agreement!=null && agreement.getId()!=null){
					setAgreement((Agreement) agreementBean.get(Integer.parseInt(getAgreementData().getExpression())));
				} else {
					setAgreement((Agreement) agreementBean.createNewTo());
				}
			}
		}
	}
	
	// **********************************
	// OPTIONAL SALARY TEMPLATES
	// **********************************
	private SalaryTemplate[] enabledSalaryTemplates;
	
	private List<SelectItem> optionalSalaryTemplates;
	
	public SalaryTemplate[] getEnabledSalaryTemplates() {
		return enabledSalaryTemplates;
	}

	public void setEnabledSalaryTemplates(SalaryTemplate[] enabledSalaryTemplates) {
		this.enabledSalaryTemplates = enabledSalaryTemplates;
	}
	
	public List<SelectItem> getOptionalSalaryTemplates(){
		if (optionalSalaryTemplates == null) {
			optionalSalaryTemplates = new LinkedList<SelectItem>();
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			SelectItem item = new SelectItem(SalaryTemplate.NOMINASTA, SalaryTemplate.NOMINASTA.getName(locale));
			optionalSalaryTemplates.add(item);
			item = new SelectItem(SalaryTemplate.NOMINASTA_CODINT, SalaryTemplate.NOMINASTA_CODINT.getName(locale));
			optionalSalaryTemplates.add(item);
			item = new SelectItem(SalaryTemplate.NOMINASTA_CONDDIAS, SalaryTemplate.NOMINASTA_CONDDIAS.getName(locale));
			optionalSalaryTemplates.add(item);
			item = new SelectItem(SalaryTemplate.NOMINASTA_LDH, SalaryTemplate.NOMINASTA_LDH.getName(locale));
			optionalSalaryTemplates.add(item);
			item = new SelectItem(SalaryTemplate.IDAZKIAK_ES, SalaryTemplate.IDAZKIAK_ES.getName(locale));
			optionalSalaryTemplates.add(item);
		}
		return optionalSalaryTemplates;
	}
	
	private void restoreOptionalTemplatesParam() throws ManagerBeanException{
		EnterpriseData param = getParameter(AppParam.PAY_REPORT_additional_salary_PAY.getValue());
		if(enabledSalaryTemplates!=null && enabledSalaryTemplates.length>0){
			String value = "";
			for(int i=0; i<enabledSalaryTemplates.length; i++){
				value += enabledSalaryTemplates[i].getValue();
				value += ";";
			}
			param.setExpression(value);
		} else {
			param.setExpression(null);
		}
	}

	private void loadOptionalSalaryTemplatesParam() throws ManagerBeanException {
		EnterpriseData param = getParameter(AppParam.PAY_REPORT_additional_salary_PAY.getValue());
		String[] values = null;
		if(param!=null && param.getExpression()!=null){
			values = StringUtils.split(param.getExpression(), ";");
		}
		if(values!=null){
			enabledSalaryTemplates = new SalaryTemplate[0];
			for(String s: values){
				if(s.equals(SalaryTemplate.NOMINASTA.getValue())){
					enabledSalaryTemplates = (SalaryTemplate[]) ArrayUtils.add(enabledSalaryTemplates, SalaryTemplate.NOMINASTA);
				} else if(s.equals(SalaryTemplate.NOMINASTA_CODINT.getValue())){
					enabledSalaryTemplates = (SalaryTemplate[]) ArrayUtils.add(enabledSalaryTemplates, SalaryTemplate.NOMINASTA_CODINT);
				} else if(s.equals(SalaryTemplate.NOMINASTA_CONDDIAS.getValue())){
					enabledSalaryTemplates = (SalaryTemplate[]) ArrayUtils.add(enabledSalaryTemplates, SalaryTemplate.NOMINASTA_CONDDIAS);
				} else if(s.equals(SalaryTemplate.NOMINASTA_LDH.getValue())){
					enabledSalaryTemplates = (SalaryTemplate[]) ArrayUtils.add(enabledSalaryTemplates, SalaryTemplate.NOMINASTA_LDH);
				} else if(s.equals(SalaryTemplate.IDAZKIAK_ES.getValue())){
					enabledSalaryTemplates = (SalaryTemplate[]) ArrayUtils.add(enabledSalaryTemplates, SalaryTemplate.IDAZKIAK_ES);
				}
			}
		}
	}
	
	
	
}