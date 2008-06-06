package com.code.aon.ui.config.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ui.util.AonUtil;

/**
 * Extension of the
 * <code>com.code.aon.ui.common.controller.ConfigurationController</code> that
 * persists the parameters on the corresponding system.
 * 
 * @author Consulting & Development. euke - 17/10/2006
 */
public class ApplicationParameterController {

	/** The new name of the parameter. */
	private String newName = new String();

	/** The new value of the parameter. */
	private String newValue = new String();

	/** The default parameters. */
	private Map<String, String> defaultParameters;

	/** The non-default parameters. */
	private Map<String, ApplicationParameter> parameters;

	/** The list of parameters that have been checked. */
	private List<ApplicationParameter> checkList;

	/** The manager bean. */
	private IManagerBean managerBean;

	/** The model. */
	private DataModel model;

	/**
	 * Gets all the parameters.
	 * 
	 * @return the parameters
	 */
	public Map<String, ApplicationParameter> getParameters() {
		try {
			if (parameters == null) {
				loadParameters();
				loadDefaultParameters();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		return parameters;
	}

	/**
	 * Load non-default parameters.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private void loadParameters() throws ManagerBeanException {
		parameters = new TreeMap<String, ApplicationParameter>();
		managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		List list = managerBean.getList(null);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			ApplicationParameter appParam = (ApplicationParameter) iter.next();
			parameters.put(appParam.getName(), appParam);
		}
	}

	/**
	 * Load default parameters.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	private void loadDefaultParameters() throws ManagerBeanException {
		Set<String> keys = defaultParameters.keySet();
		for (String key : keys) {
			String defaultValue = defaultParameters.get(key);
			if (parameters.containsKey(key)) {
				ApplicationParameter p = parameters.get(key);
				p.setDefaultValue(defaultValue);
				p.setSystemParameter(true);
			} else {
				ApplicationParameter p = new ApplicationParameter();
				p.setName(key);
				p.setValue(defaultValue);
				p.setDefaultValue(defaultValue);
				p.setSystemParameter(true);
				managerBean.insert(p);
				parameters.put(key, p);
			}
		}
	}

	/**
	 * Sets the parameters.
	 * 
	 * @param parameters the parameters
	 */
	public void setParameters(Map<String, ApplicationParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public DataModel getModel() {
		Collection<ApplicationParameter> c = getParameters().values();
		List<ApplicationParameter> list = new LinkedList<ApplicationParameter>();
		list.addAll(c);
		model = new ListDataModel(list);
		checkList = new LinkedList<ApplicationParameter>();
		return model;
	}

	/**
	 * On accept. Adds any parameter that have been created, and updates all the existing parameters
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onAccept(ActionEvent event) {
		try {
			if (newName != null && !"".equals(newName)) {
				ApplicationParameter ap = new ApplicationParameter();
				ap.setName(newName);
				ap.setValue(newValue);
				managerBean.insert(ap);
				newName = new String();
				newValue = new String();
			}
			for (ApplicationParameter appParam : parameters.values()) {
				String name = appParam.getName();
				String value = appParam.getValue();
				Object o = managerBean.get(name);
				if (o == null) {
					managerBean.insert(appParam);
				} else {
					ApplicationParameter ap = (ApplicationParameter) o;
					String v = ap.getValue();
					if ((v != null && !v.equals(value)) || (v == null && value != null)) {
						ap.setValue(value);
						managerBean.update(ap);
					}
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		parameters = null;
		getParameters();
	}

	/**
	 * On reset. Set the default value to default parameters, and reset the controller.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onReset(ActionEvent event) {
		try {
			Set<String> keys = defaultParameters.keySet();
			for (String key : keys) {
				String defaultValue = defaultParameters.get(key);
				if (parameters.containsKey(key)) {
					ApplicationParameter p = parameters.get(key);
					p.setValue(defaultValue);
					managerBean.update(p);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		parameters = null;
		getParameters();
	}

	/**
	 * On remove. Removes the parameters that have been checked
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onRemove(ActionEvent event) {
		try {
			for (ApplicationParameter param : checkList) {
				managerBean.remove(param);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		parameters = null;
		getParameters();
	}

	/**
	 * Row selected. Adds the parameter that have been checked to the checkList
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void rowSelected(ValueChangeEvent event) {
		Boolean b = (Boolean) event.getNewValue();
		if (b.booleanValue()) {
			ApplicationParameter appParam = (ApplicationParameter) model.getRowData();
			checkList.add(appParam);
		}
	}

	/**
	 * Gets the new name of the parameter.
	 * 
	 * @return the new name
	 */
	public String getNewName() {
		return newName;
	}

	/**
	 * Sets the new name of the parameter.
	 * 
	 * @param newName the new name
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/**
	 * Gets the new value of the parameter.
	 * 
	 * @return the new value
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * Sets the new value of the parameter.
	 * 
	 * @param newValue the new value
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	/**
	 * Gets the default parameters.
	 * 
	 * @return the default parameters
	 */
	public Map<String, String> getDefaultParameters() {
		return defaultParameters;
	}

	/**
	 * Sets the default parameters.
	 * 
	 * @param defaultParameters the default parameters
	 */
	public void setDefaultParameters(Map<String, String> defaultParameters) {
		this.defaultParameters = defaultParameters;
	}
}