package com.esferalia.aon.ui.payroll.controller;

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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.enumeration.ContractCode;

public class PayrollAppParamsController{

	public final static String DEFAULT_CONTRACT_CODE = "PAY_default_contractCode_PAY";
	public final static String DEFAULT_TRAINING_CENTER = "PAY_default_trainingCenter_PAY";

	public final static String AVAILABLE_NEW_CONTRACT_CODES = "PAY_available_contract_codes_PAY";

	private TrainingCenter defaultTrainingCenter;
	
	private List<ContractCode> availableNewContracts;
	
	private Map<String, ApplicationParameter> parameters;

	private Map<String, String> defaultParameters;
	
	private boolean skipPayrollData;
	

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
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AVAILABLE_NEW_CONTRACT_CODES);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN), DomainManager.getCurrentDomain());
			List<ITransferObject> list = bean.getList(criteria);
			criteria = null;
			Integer parentDomain = DomainManager.getDomainProvider().getParentDomain();
			if(list.isEmpty() && parentDomain!=null ){
				criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AVAILABLE_NEW_CONTRACT_CODES);
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
			if(getParameter(DEFAULT_TRAINING_CENTER).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_CENTER_ID), Integer.parseInt(getParameter(DEFAULT_TRAINING_CENTER).getValue()));
				setDefaultTrainingCenter((TrainingCenter) bean.getList(criteria).get(0));
			} else {
				setDefaultTrainingCenter((TrainingCenter) bean.createNewTo());
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

	public void onAccept(ActionEvent event) throws ManagerBeanException{
		accept();
		loadParameters();
		AonUtil.addInfoMessage("Los parámetros se guardaron correctamente.");		
	}
	
	public void accept() throws ManagerBeanException{
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Collection<ApplicationParameter>params = parameters.values();
		beforeBeanUpdate();
		for(ApplicationParameter param : params){
			managerBean.update(param);
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
		
		parameters = new TreeMap<String, ApplicationParameter>();
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		List<ITransferObject> list = managerBean.getList(null);
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
			getParameter(DEFAULT_TRAINING_CENTER).setValue(getDefaultTrainingCenter().getId().toString());
		} else {
			getParameter(DEFAULT_TRAINING_CENTER).setValue(null);
		}
	}

	private String getDraftTemplateName() throws ManagerBeanException {
		if( StringUtils.isNotBlank(getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue()) ){
			return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
		}
		return null;
	}
	
	
}