package com.code.aon.ui.asset.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.asset.Feature;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.asset.enumeration.ViewerType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AssetCollectionsController {

	private List<SelectItem> activityStatuses;
	private List<SelectItem> viewerTypes;
	
	public List<SelectItem> getActivityStatuses() {
		if (activityStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			activityStatuses = new LinkedList<SelectItem>();
			for( ActivityStatus o : ActivityStatus.values() ) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				activityStatuses.add(item);			
			}
		}
		return activityStatuses;
	}
	
	public List<SelectItem> getViewerTypes() {
		if (viewerTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			viewerTypes = new LinkedList<SelectItem>();
			for( ViewerType o : ViewerType.values() ) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				viewerTypes.add(item);			
			}
		}
		return viewerTypes;
	}
	
	public List<SelectItem> getFeatures() throws ManagerBeanException {
		return getFeatures(false);
	}
		
	public List<SelectItem> getFeaturesIds() throws ManagerBeanException {
		return getFeatures(true);
	}
		
	private List<SelectItem> getFeatures(boolean onlyId) throws ManagerBeanException {
		List<SelectItem> featuresList = new LinkedList<SelectItem>();
		IManagerBean featureBean = BeanManager.getManagerBean(Feature.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(featureBean.getFieldName(IEntityAlias.FEATURE_NAME));
		for (ITransferObject ito : featureBean.getList(criteria)) {
			Feature feature = (Feature)ito;
			SelectItem item = new SelectItem((onlyId ? feature.getId() : feature), feature.getName());
			featuresList.add(item);
		}
		return featuresList;
	}
		
	public int getFeaturesCount() throws ManagerBeanException {
		return BeanManager.getManagerBean(Feature.class).getCount(null);
	}

}