package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.enumeration.ContractCode;

public class PayrollAppParamsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PayrollAppParamsController.class);

	private TrainingCenter defaultTrainingCenter;
	private RegistryBank ssPaymentBankAccount;
	private List<ContractCode> availableNewContracts;
	private Boolean fanTestEnvironment;
	private Boolean afiTestEnvironment;
	private String authorizationKey;
	private String ssContactEmail;
	
	private Map<String, ApplicationParameter> parameters;
	private Map<String, String> defaultParameters;
	
	private boolean skipPayrollData;
	

	public String getAuthorizationKey() {
		if(authorizationKey==null){
			initAuthorizationKey();
		}
		return authorizationKey;
	}

	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

	private void initAuthorizationKey() {
		try {
			if(getParameter(AppParam.PAY_authorization_key_PAY.getValue()).getValue()!=null){
				setAuthorizationKey(getParameter(AppParam.PAY_authorization_key_PAY.getValue()).getValue());
			} else {
				setAuthorizationKey("");
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public String getSsContactEmail() {
		if(ssContactEmail==null){
			initSsContactEmail();
		}
		return ssContactEmail;
	}

	public void setSsContactEmail(String ssContactEmail) {
		this.ssContactEmail = ssContactEmail;
	}

	private void initSsContactEmail() {
		try {
			if(getParameter(AppParam.PAY_ss_contact_email_PAY.getValue()).getValue()!=null){
				setSsContactEmail(getParameter(AppParam.PAY_ss_contact_email_PAY.getValue()).getValue());
			} else {
				setSsContactEmail("");
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public Boolean getFanTestEnvironment() {
		if(fanTestEnvironment==null){
			initFanTestEnvironment();
		}
		return fanTestEnvironment;
	}
	
	public void setFanTestEnvironment(Boolean fanTestEnvironment) {
		this.fanTestEnvironment = fanTestEnvironment;
	}
	
	private void initFanTestEnvironment() {
		try {
			if(getParameter(AppParam.PAY_fan_test_env_PAY.getValue()).getValue()!=null){
				setFanTestEnvironment(new Boolean(getParameter(AppParam.PAY_fan_test_env_PAY.getValue()).getValue()));
			} else {
				setFanTestEnvironment(true);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public Boolean getAfiTestEnvironment() {
		if(afiTestEnvironment==null){
			initAfiTestEnvironment();
		}
		return afiTestEnvironment;
	}
	
	public void setAfiTestEnvironment(Boolean afiTestEnvironment) {
		this.afiTestEnvironment = afiTestEnvironment;
	}
	
	private void initAfiTestEnvironment() {
		try {
			if(getParameter(AppParam.PAY_afi_test_env_PAY.getValue()).getValue()!=null){
				setAfiTestEnvironment(new Boolean(getParameter(AppParam.PAY_afi_test_env_PAY.getValue()).getValue()));
			} else {
				setAfiTestEnvironment(true);
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public List<ContractCode> getAvailableNewContracts() {
		if(availableNewContracts==null){
			initAvailableNewContracts();
		}
		return availableNewContracts;
	}
	private void initAvailableNewContracts() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AppParam.PAY_available_contract_codes_PAY.getValue());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), DomainManager.getCurrentDomain());
			List<ITransferObject> list = bean.getList(criteria);
			criteria = null;
			Integer parentDomain = DomainManager.getDomainProvider().getParentDomain();
			if(list.isEmpty() && parentDomain!=null ){
				criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AppParam.PAY_available_contract_codes_PAY.getValue());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), parentDomain);
				list = bean.getList(criteria);
			}
			
			availableNewContracts = new LinkedList<ContractCode>();
			if(!list.isEmpty()){
				ApplicationParameter appParam = (ApplicationParameter) list.get(0);
				String[] codes = appParam.getValue().split(";");
				for(String code: codes){
					availableNewContracts.add(ContractCode.getContractCodeByValue(code));
				}
			}
			
		} catch (ManagerBeanException e) {
			// NADA
		}
	}

	public void setAvailableNewContracts(List<ContractCode> availableNewContracts) {
		this.availableNewContracts = availableNewContracts;
	}

	public boolean isSkipPayrollData() {
		return skipPayrollData;
	}

	public void setSkipPayrollData(boolean skipPayrollData) {
		this.skipPayrollData = skipPayrollData;
	}

	public TrainingCenter getDefaultTrainingCenter() {
		if(defaultTrainingCenter==null){
			initDefaultTrainingCenter();
		}
		return defaultTrainingCenter;
	}
	
	public void setDefaultTrainingCenter(TrainingCenter defaultTrainingCenter) {
		this.defaultTrainingCenter = defaultTrainingCenter;
	}
	
	private void initDefaultTrainingCenter() {
		TrainingCenter trainingCenter = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(TrainingCenter.class);
			String value = getParameter(AppParam.PAY_default_trainingCenter_PAY.getValue()).getValue(); 
			if (value!=null) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_CENTER_ID), Integer.parseInt(value));
				List<ITransferObject> list = bean.getList(criteria);
				if (! list.isEmpty() ) {
					trainingCenter = (TrainingCenter) list.get(0);
				}
			}
			if ( trainingCenter == null ) {
				trainingCenter = (TrainingCenter) bean.createNewTo();
			}
			setDefaultTrainingCenter(trainingCenter);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public RegistryBank getSsPaymentBankAccount() {
		if(ssPaymentBankAccount==null){
			initSsPaymentBankAccount();
		}
		return ssPaymentBankAccount;
	}
	
	public void setSsPaymentBankAccount(RegistryBank ssPaymentBankAccount) {
		this.ssPaymentBankAccount = ssPaymentBankAccount;
	}

	private void initSsPaymentBankAccount() {
		RegistryBank rbank = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
			String value = getParameter(AppParam.PAY_ss_payment_bankAccount_PAY.getValue()).getValue();
			if (value!=null) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_ID), Integer.parseInt(value));
				List<ITransferObject> list = bean.getList(criteria);
				if (! list.isEmpty() ) {
					rbank = (RegistryBank) list.get(0);
				}
			}
			if ( rbank == null ) {
				rbank = (RegistryBank) bean.createNewTo();
			}
			setSsPaymentBankAccount(rbank);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public Map<String, ApplicationParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ApplicationParameter> parameters) {
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
		} catch (ManagerBeanException e) {
			String msg = "Unable to save parameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void accept() throws ManagerBeanException{
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Collection<ApplicationParameter>params = parameters.values();
		beforeBeanUpdate();
		for(ApplicationParameter param : params){
			managerBean.insertOrUpdate(param);
		}
	}

	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "Unable to load defaultParameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void loadParameters() throws ManagerBeanException{
		setDefaultTrainingCenter(null);
		
		List<String> keyList = new LinkedList<String>();
		for(String key: defaultParameters.keySet()){
			keyList.add(key);
		}
		
		parameters = new TreeMap<String, ApplicationParameter>();
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addInExpression(managerBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), keyList);
		List<ITransferObject> list = managerBean.getList(criteria);
		Iterator<ITransferObject> iter = list.iterator();
		while (iter.hasNext()) {
			ApplicationParameter appParam = (ApplicationParameter) iter.next();
			parameters.put(appParam.getName(), appParam);
		}
		Set<String> keys = defaultParameters.keySet();
		for (String key : keys) {
			if (!parameters.containsKey(key)) {
				ApplicationParameter p = new ApplicationParameter();
				p.setName(key);
				p.setValue(defaultParameters.get(key));
				p = (ApplicationParameter) managerBean.insert(p);
				parameters.put(p.getName(), p);
			}
		}
	}
	
	public ApplicationParameter getParameter(String key) throws ManagerBeanException {
		if (parameters == null || parameters.isEmpty()) {
			loadParameters();	
		}
		return parameters.get(key); 		
	}
	
	private void beforeBeanUpdate() throws ManagerBeanException {
		// SALARY PRINT PARAMS
		if( StringUtils.isNotBlank(getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue()) ){
			getParameter(ICompanyConstants.REPORT_SALARY_DRAFT_PARAM).setValue(getDraftTemplateName());
		}

		// CONTRACT PARAMS
		if(getDefaultTrainingCenter()!=null && getDefaultTrainingCenter().getId()!=null){
			getParameter(AppParam.PAY_default_trainingCenter_PAY.getValue()).setValue(getDefaultTrainingCenter().getId().toString());
		} else {
			getParameter(AppParam.PAY_default_trainingCenter_PAY.getValue()).setValue(null);
		}
		
		// SS PAYMENT BANK ACCOUNT
		if(ssPaymentBankAccount!=null && ssPaymentBankAccount.getId()!=null){
			getParameter(AppParam.PAY_ss_payment_bankAccount_PAY.getValue()).setValue(ssPaymentBankAccount.getId().toString());
		} else {
			getParameter(AppParam.PAY_ss_payment_bankAccount_PAY.getValue()).setValue(null);
		}
		if( StringUtils.isNotBlank(getParameter(AppParam.PAY_ss_mutual_PAY.getValue()).getValue()) ){
			getParameter(AppParam.PAY_ss_mutual_PAY.getValue()).setValue(getSSMutual());
		} else {
			getParameter(AppParam.PAY_ss_mutual_PAY.getValue()).setValue(null);
		}
		if( StringUtils.isNotBlank(getParameter(AppParam.PAY_ss_contact_email_PAY.getValue()).getValue()) ){
			getParameter(AppParam.PAY_ss_contact_email_PAY.getValue()).setValue(getSsContactEmail());
		} else {
			getParameter(AppParam.PAY_ss_contact_email_PAY.getValue()).setValue(null);
		}
		getParameter(AppParam.PAY_fan_test_env_PAY.getValue()).setValue(getFanTestEnvironment().toString());
		getParameter(AppParam.PAY_afi_test_env_PAY.getValue()).setValue(getAfiTestEnvironment().toString());
		
	}

	private String getDraftTemplateName() throws ManagerBeanException {
		if( StringUtils.isNotBlank(getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue()) ){
			return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
		}
		return null;
	}
	private String getSSMutual() throws ManagerBeanException {
		if( StringUtils.isNotBlank(getParameter(AppParam.PAY_ss_mutual_PAY.getValue()).getValue()) ){
			return getParameter(AppParam.PAY_ss_mutual_PAY.getValue()).getValue();
		}
		return null;
	}
	
	
	/*
	 * PARENT DOMAIN OVERRIDABLE DATA
	 */
	public boolean isWinsuiteAuthorizationDefined(){
		return StringUtils.isNotBlank(getAuthorizationKey());
	}
	public boolean isParentWinsuiteAuthorizationDefined(){
		String user = getParentAuthorizationKey();
		return StringUtils.isNotBlank(user);
	}
	public String getParentAuthorizationKey(){
		ApplicationParameter ap = obtainParentParamValue(AppParam.PAY_authorization_key_PAY);
		return ap!=null?ap.getValue():null;
	}
	public void onRedefineWinsuiteAuthorization(ActionEvent event){
		setAuthorizationKey(getParentAuthorizationKey());
	}
	
	private ApplicationParameter obtainParentParamValue(AppParam ap){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), DomainManager.getDomainProvider().getParentDomain());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), ap.getValue());
			List<ITransferObject> list = bean.getList(criteria, 0, 1);
			if (! list.isEmpty() ) {
				return (ApplicationParameter) list.get(0);
			}
		} catch ( ManagerBeanException e ) {
		}
		return null;
	}
	
}