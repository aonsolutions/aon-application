package com.code.aon.ui.config.controller;

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
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ql.Criteria;

public class ConfigCollectionsController {
	
	private List<SelectItem> workGroupStatuses;

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSeries() throws ManagerBeanException{
		List<SelectItem> series = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addOrder(seriesBean.getFieldName(IConfigAlias.SERIES_ID));
		Iterator iter = seriesBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Series serie = (Series)iter.next();
			SelectItem item = new SelectItem(serie.getId(),serie.getId().toString());
			series.add(item);
		}
		return series;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getScopes() throws ManagerBeanException {
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(scopeBean.getFieldName(IConfigAlias.SCOPE_DESCRIPTION));
		Iterator iter = scopeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Scope scope = (Scope)iter.next();
			SelectItem item = new SelectItem(scope.getId(), scope.getDescription());
			scopes.add(item);
		}
		return scopes;
	}
	
	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
		Iterator<ITransferObject> iter = workGroupBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			WorkGroup workGroup = (WorkGroup) iter.next();
			SelectItem item = new SelectItem(workGroup.getId(), workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}

	public List<SelectItem> getWorkGroupStatuses() {
		if(workGroupStatuses == null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			workGroupStatuses = new LinkedList<SelectItem>();
			for (WorkGroupStatus status : WorkGroupStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				workGroupStatuses.add(item);
			}
		}
		return workGroupStatuses;
	}
}