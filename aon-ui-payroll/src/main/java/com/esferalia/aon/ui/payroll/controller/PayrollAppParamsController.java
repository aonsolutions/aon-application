package com.esferalia.aon.ui.payroll.controller;

import java.util.Collection;
import java.util.Iterator;
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
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.TrainingCenter;

public class PayrollAppParamsController{
	
	public final static String SETTLE_VACATION_CONCEPT = "PAY_settle_vacation_concept_PAY";
	public final static String SETTLE_NOTICE_DAY_CONCEPT = "PAY_settle_noticeDay_concept_PAY";
	public final static String SETTLE_COMPENSATION_CONCEPT = "PAY_settle_compens_concept_PAY";
	
	public final static String CONTRATA_USER = "PAY_contrata_user_PAY";
	public final static String CONTRATA_PASSWORD = "PAY_contrata_passwd_PAY";

	public final static String DEFAULT_CONTRACT_CODE = "PAY_default_contractCode_PAY";
	public final static String DEFAULT_TRAINING_CENTER = "PAY_default_trainingCenter_PAY";
	
	private PaymentConcept settleVacationConcept;
	private PaymentConcept settleNoticeDayConcept;
	private PaymentConcept settleCompensationConcept;
	
	private String contrataUser;
	private String contrataPassword;
	private Boolean validContrataLogin;
	
	private TrainingCenter defaultTrainingCenter;
	
	private Map<String, ApplicationParameter> parameters;

	private Map<String, String> defaultParameters;
	
	private boolean skipPayrollData;
	

	public boolean isSkipPayrollData() {
		return skipPayrollData;
	}

	public void setSkipPayrollData(boolean skipPayrollData) {
		this.skipPayrollData = skipPayrollData;
	}

	public Boolean getValidContrataLogin() {
		return validContrataLogin;
	}

	public void setValidContrataLogin(Boolean validContrataLogin) {
		this.validContrataLogin = validContrataLogin;
	}

	public boolean isContrataLoginChecked() {
		return validContrataLogin != null;
	}

	public String getContrataUser() {
		if(contrataUser==null){
			initContrataUser();
		}
		return contrataUser;
	}

	public void setContrataUser(String contrataUser) {
		this.contrataUser = contrataUser;
	}
	
	private void initContrataUser() {
		try {
			if(getParameter(CONTRATA_USER).getValue()!=null){
				setContrataUser(getParameter(CONTRATA_USER).getValue());
			} else {
				setContrataUser("");
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}

	public String getContrataPassword() {
		if(contrataPassword==null){
			initContrataPassword();
		}
		return contrataPassword;
	}

	public void setContrataPassword(String contrataPassword) {
		this.contrataPassword = contrataPassword;
	}
	
	private void initContrataPassword() {
		try {
			if(getParameter(CONTRATA_PASSWORD).getValue()!=null){
				setContrataPassword(getParameter(CONTRATA_PASSWORD).getValue());
			} else {
				setContrataPassword("");
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
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
		setContrataUser(null);
		setContrataPassword(null);
		setValidContrataLogin(null);
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

		// CONTRATA PARAMS
		getParameter(CONTRATA_USER).setValue(getContrataUser());
		getParameter(CONTRATA_PASSWORD).setValue(getContrataPassword());

		// CONTRACT PARAMS
		if(getDefaultTrainingCenter()!=null && getDefaultTrainingCenter().getId()!=null){
			getParameter(DEFAULT_TRAINING_CENTER).setValue(getDefaultTrainingCenter().getId().toString());
		}
	}

	private String getDraftTemplateName() throws ManagerBeanException {
		if( StringUtils.isNotBlank(getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue()) ){
			return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
		}
		return null;
	}
	
	public void validateLogin(ActionEvent event){
		// TODO implementar
		setValidContrataLogin(true);
	}
	
}