package com.code.aon.ui.audit.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AuditCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> auditLevels;
	
	private List<SelectItem> applications;
	
	private List<SelectItem> modules;
	
	/**
	 * Gets the audit levels.
	 * 
	 * @return the audit levels
	 */
	public List<SelectItem> getAuditLevels() {
		if ( auditLevels == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			auditLevels = new LinkedList<SelectItem>();
			for (AuditLevel auditLevel : AuditLevel.values()) {
				String name = auditLevel.getName(locale);
				SelectItem item = new SelectItem(auditLevel, name);
				auditLevels.add(item);
			}
		}
		return auditLevels;
	}	

	/**
	 * Gets the modules.
	 * 
	 * @return the modules
	 */
	public List<SelectItem> getModules() {
		if ( modules == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			modules = new LinkedList<SelectItem>();
			for (Module module : Module.values()) {
				String name = module.getName(locale);
				SelectItem item = new SelectItem(module, name);
				modules.add(item);					
			}
			AonUtil.sortSelectItems(modules);
		}
		return modules;
	}	
	
	public List<SelectItem> getApplications() {
		return applications;
	}

	public void refreshApplications() throws ManagerBeanException {
		applications = new LinkedList<SelectItem>();
		IManagerBean segmentBean = BeanManager.getManagerBean(Application.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(segmentBean.getFieldName(IEntityAlias.APPLICATION_NAME));
		Iterator<ITransferObject> iter = segmentBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Application application = (Application)iter.next();
			SelectItem item = new SelectItem(application.getId(), application.getName());
			applications.add(item);
		}
	}
	
}
