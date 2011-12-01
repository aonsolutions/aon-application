package com.code.aon.ui.audit.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.audit.Application;
import com.code.aon.audit.dao.IAuditAlias;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class AuditCollectionsController {

	private List<SelectItem> auditLevels;
	
	private List<SelectItem> applications;
	
	/**
	 * Gets the audit levels.
	 * 
	 * @return the audit levels
	 */
	public List<SelectItem> getAuditLevels() {
		if ( auditLevels == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			auditLevels = new LinkedList<SelectItem>();
			for (AuditLevel auditLevel : AuditLevel.values()) {
				String name = auditLevel.getName(locale);
				SelectItem item = new SelectItem(auditLevel, name);
				auditLevels.add(item);
			}
		}
		return auditLevels;
	}	
	
	public List<SelectItem> getApplications() {
		return applications;
	}

	public void refreshApplications() throws ManagerBeanException {
		applications = new LinkedList<SelectItem>();
		IManagerBean segmentBean = BeanManager.getManagerBean(Application.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(segmentBean.getFieldName(IAuditAlias.APPLICATION_NAME));
		Iterator<ITransferObject> iter = segmentBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Application application = (Application)iter.next();
			SelectItem item = new SelectItem(application.getId(), application.getName());
			applications.add(item);
		}
	}
	
}
