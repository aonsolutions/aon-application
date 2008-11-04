package com.code.aon.ui.account.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class AccountAppParamsController{
	
	private Map<String, ApplicationParameter> parameters;

	private Map<String, String> defaultParameters;
	

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
	}

	@SuppressWarnings("unused")
	public void onAccept(ActionEvent event) throws ManagerBeanException{
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Collection<ApplicationParameter>params = parameters.values();
		for(ApplicationParameter param : params){
			managerBean.update(param);
		}
		loadParameters(null);
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	public void loadParameters(MenuEvent event) throws ManagerBeanException{
		parameters = new TreeMap<String, ApplicationParameter>();
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		List list = managerBean.getList(null);
		Iterator iter = list.iterator();
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
}