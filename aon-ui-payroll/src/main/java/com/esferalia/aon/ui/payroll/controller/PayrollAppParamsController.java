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
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.enumeration.ContractCode;

public class PayrollAppParamsController{

	public final static String SETTLE_VACATION_CONCEPT = "PAY_settle_vacation_concept_PAY";
	public final static String SETTLE_NOTICE_DAY_CONCEPT = "PAY_settle_noticeDay_concept_PAY";
	public final static String SETTLE_COMPENSATION_CONCEPT = "PAY_settle_compens_concept_PAY";
	
	public final static String DEFAULT_CONTRACT_CODE = "PAY_default_contractCode_PAY";
	public final static String DEFAULT_TRAINING_CENTER = "PAY_default_trainingCenter_PAY";

	public final static String AVAILABLE_NEW_CONTRACT_CODES = "PAY_available_contract_codes_PAY";

	
	private PaymentConcept settleVacationConcept;
	private PaymentConcept settleNoticeDayConcept;
	private PaymentConcept settleCompensationConcept;
	
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
	
	public PaymentConcept getSettleVacationConcept() {
		if(settleVacationConcept==null){
			initSettleVacationConcept();
		}
		return settleVacationConcept;
	}
	
	public void setSettleVacationConcept(PaymentConcept settleVacationConcept) {
		this.settleVacationConcept = settleVacationConcept;
	}

	private void initSettleVacationConcept() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			if(getParameter(SETTLE_VACATION_CONCEPT).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_VACATION_CONCEPT).getValue()));
				setSettleVacationConcept((PaymentConcept) bean.getList(criteria).get(0));
			} else {
				setSettleVacationConcept((PaymentConcept) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public PaymentConcept getSettleNoticeDayConcept() {
		if(settleNoticeDayConcept==null){
			initSettleNoticeDayConcept();
		}
		return settleNoticeDayConcept;
	}
	
	public void setSettleNoticeDayConcept(PaymentConcept settleNoticeDayConcept) {
		this.settleNoticeDayConcept = settleNoticeDayConcept;
	}
	
	private void initSettleNoticeDayConcept() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			if(getParameter(SETTLE_NOTICE_DAY_CONCEPT).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_NOTICE_DAY_CONCEPT).getValue()));
				setSettleNoticeDayConcept((PaymentConcept) bean.getList(criteria).get(0));
			} else {
				setSettleNoticeDayConcept((PaymentConcept) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public PaymentConcept getSettleCompensationConcept() {
		if(settleCompensationConcept==null){
			initSettleCompensationConcept();
		}
		return settleCompensationConcept;
	}
	
	public void setSettleCompensationConcept(PaymentConcept settleCompensationConcept) {
		this.settleCompensationConcept = settleCompensationConcept;
	}
	
	private void initSettleCompensationConcept() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			if(getParameter(SETTLE_COMPENSATION_CONCEPT).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_COMPENSATION_CONCEPT).getValue()));
				setSettleCompensationConcept((PaymentConcept) bean.getList(criteria).get(0));
			} else {
				setSettleCompensationConcept((PaymentConcept) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
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
		setSettleVacationConcept(null);
		setSettleNoticeDayConcept(null);
		setSettleCompensationConcept(null);

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
		// SETTLE PARAMS
		if(getSettleVacationConcept()!=null && getSettleVacationConcept().getCode()!=null){
			getParameter(SETTLE_VACATION_CONCEPT).setValue(getSettleVacationConcept().getId().toString());
		}
		if(getSettleNoticeDayConcept()!=null && getSettleNoticeDayConcept().getCode()!=null){
			getParameter(SETTLE_NOTICE_DAY_CONCEPT).setValue(getSettleNoticeDayConcept().getId().toString());
		}
		if(getSettleCompensationConcept()!=null && getSettleCompensationConcept().getCode()!=null){
			getParameter(SETTLE_COMPENSATION_CONCEPT).setValue(getSettleCompensationConcept().getId().toString());
		}

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