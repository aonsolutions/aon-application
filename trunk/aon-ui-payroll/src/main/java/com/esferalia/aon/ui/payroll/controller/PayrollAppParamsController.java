package com.esferalia.aon.ui.payroll.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class PayrollAppParamsController{
	
	public final static String SETTLE_VACATION_CONCEPT = "PAY_settle_vacation_concept_PAY";
	public final static String SETTLE_NOTICE_DAY_CONCEPT = "PAY_settle_noticeDay_concept_PAY";
	public final static String SETTLE_COMPENSATION_CONCEPT = "PAY_settle_compens_concept_PAY";
	
	private PaymentConcept settleVacationConcept;
	private PaymentConcept settleNoticeDayConcept;
	private PaymentConcept settleCompensationConcept;
	
	private Map<String, ApplicationParameter> parameters;

	private Map<String, String> defaultParameters;

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
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_VACATION_CONCEPT).getValue()));
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
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_NOTICE_DAY_CONCEPT).getValue()));
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
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_COMPENSATION_CONCEPT).getValue()));
				setSettleCompensationConcept((PaymentConcept) bean.getList(criteria).get(0));
			} else {
				setSettleCompensationConcept((PaymentConcept) bean.createNewTo());
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
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Collection<ApplicationParameter>params = parameters.values();
		for(ApplicationParameter param : params){
			beforeBeanUpdate();
			managerBean.update(param);
		}
		loadParameters();
		AonUtil.addInfoMessage("Los parámetros se guardaron correctamente.");		
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
		if(getSettleVacationConcept()!=null && getSettleVacationConcept().getCode()!=null){
			getParameter(SETTLE_VACATION_CONCEPT).setValue(getSettleVacationConcept().getId().toString());
		}
		if(getSettleNoticeDayConcept()!=null && getSettleNoticeDayConcept().getCode()!=null){
			getParameter(SETTLE_NOTICE_DAY_CONCEPT).setValue(getSettleNoticeDayConcept().getId().toString());
		}
		if(getSettleCompensationConcept()!=null && getSettleCompensationConcept().getCode()!=null){
			getParameter(SETTLE_COMPENSATION_CONCEPT).setValue(getSettleCompensationConcept().getId().toString());
		}
		getParameter(ICompanyConstants.REPORT_SALARY_DRAFT_PARAM).setValue(getDraftTemplateName());
	}

	private String getDraftTemplateName() throws ManagerBeanException {
		return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
	}

}