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
	
	public final static String SETTLE_CONCEPT = "PAY_settle_concept_PAY";
	
	private PaymentConcept paymentConcept;
	
	private Map<String, ApplicationParameter> parameters;

	private Map<String, String> defaultParameters;

	public PaymentConcept getPaymentConcept() {
		if(paymentConcept==null){
			initPaymentConcept();
		}
		return paymentConcept;
	}

	private void initPaymentConcept() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			if(getParameter(SETTLE_CONCEPT).getValue()!=null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(getParameter(SETTLE_CONCEPT).getValue()));
				setPaymentConcept((PaymentConcept) bean.getList(criteria).get(0));
			} else {
				setPaymentConcept((PaymentConcept) bean.createNewTo());
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}

	public void setPaymentConcept(PaymentConcept paymentConcept) {
		this.paymentConcept = paymentConcept;
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
		if(getPaymentConcept()!=null && getPaymentConcept().getCode()!=null){
			getParameter(SETTLE_CONCEPT).setValue(getPaymentConcept().getId().toString());
		}
		getParameter(ICompanyConstants.REPORT_SALARY_DRAFT_PARAM).setValue(getDraftTemplateName());
	}

	private String getDraftTemplateName() throws ManagerBeanException {
		return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getValue().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
	}

}