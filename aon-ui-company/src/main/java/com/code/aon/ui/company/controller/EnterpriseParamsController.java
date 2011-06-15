package com.code.aon.ui.company.controller;

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
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseData;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseParamsController {
	
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
		criteria.addEqualExpression(managerBean.getFieldName(ICompanyAlias.ENTERPRISE_DATA_ENTERPRISE_ID), getEnterprise().getId());
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
		getParameter(ICompanyConstants.REPORT_SALARY_DRAFT_PARAM).setExpression(getDraftTemplateName());
	}
	
	private String getDraftTemplateName() throws ManagerBeanException {
		return getParameter(ICompanyConstants.REPORT_SALARY_PARAM).getExpression().replaceFirst(ICompanyConstants.SALARY, ICompanyConstants.SALARY_DRAFT);
	}
	
}