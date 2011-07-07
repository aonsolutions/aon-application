package com.code.aon.ui.tas.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.Make;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tas.enumeration.ProjectStatus;

public class TasCollectionsController {

	private List<SelectItem> makes;
	private List<SelectItem> projectStatuses;
	
	public void initializeMakes() {
		makes = null;
	}
	
	public Make getMake() {
		return null;
	}
	public void setMake(Make make) {
	}

	public List<SelectItem> getMakes() throws ManagerBeanException {
		if (makes == null) {
			makes = new LinkedList<SelectItem>();
			IManagerBean makeBean = BeanManager.getManagerBean(Make.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(makeBean.getFieldName(ITASAlias.MAKE_NAME));
			Iterator<ITransferObject> iter = makeBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Make make = (Make) iter.next();
				SelectItem item = new SelectItem(make, make.getName());
				makes.add(item);
			}
		}
		return makes;
	}

	public List<SelectItem> getProjectStatuses() {
		if (projectStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			projectStatuses = new LinkedList<SelectItem>();
			for (ProjectStatus status : ProjectStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				projectStatuses.add(item);
			}
		}
		return projectStatuses;
	}


}