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

//	public final static String DEFAULT_CONTRACT_CODE_KEY 	= "PAY_default_contractCode_PAY";
//	public final static String DEFAULT_TRAINING_CENTER_KEY 	= "PAY_default_trainingCenter_PAY";
//	public final static String SS_PAYMENT_BANK_ACCOUNT_KEY 	= "PAY_ss_payment_bankAccount_PAY";
//	public final static String SS_MUTUAL_KEY				= "PAY_ss_mutual_PAY";
//
//	public final static String AVAILABLE_NEW_CONTRACT_CODES = "PAY_available_contract_codes_PAY";
//	public final static String FAN_TEST_ENVIRONMENT_ACTIVE	= "PAY_fan_test_env_PAY";
//	public final static String AFI_TEST_ENVIRONMENT_ACTIVE	= "PAY_afi_test_env_PAY";
//	
//	public final static String REPORT_ADDITIONAL_SALAY_TEMPLATES 	= "PAY_REPORT_additional_salary_PAY";

	private TrainingCenter defaultTrainingCenter;
	private RegistryBank ssPaymentBankAccount;
	private List<ContractCode> availableNewContracts;
	private Boolean fanTestEnvironment;
	private Boolean afiTestEnvironment;
	
	private Map<String, ApplicationParameter> parameters;
	private Map<String, String> defaultParameters;
	
	private boolean skipPayrollData;
	

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
			// NADA
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
		try {
			IManagerBean bean = BeanManager.getManagerBean(TrainingCenter.class);
			if(getParameter(AppParam.PAY_default_trainingCenter_PAY.getValue()).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_CENTER_ID), Integer.parseInt(getParameter(AppParam.PAY_default_trainingCenter_PAY.getValue()).getValue()));
				setDefaultTrainingCenter((TrainingCenter) bean.getList(criteria).get(0));
			} else {
				setDefaultTrainingCenter((TrainingCenter) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			// NADA
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
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
			if(getParameter(AppParam.PAY_ss_payment_bankAccount_PAY.getValue()).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_ID), Integer.parseInt(getParameter(AppParam.PAY_ss_payment_bankAccount_PAY.getValue()).getValue()));
				setSsPaymentBankAccount((RegistryBank) bean.getList(criteria).get(0));
			} else {
				setSsPaymentBankAccount((RegistryBank) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			// NADA
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
	
}