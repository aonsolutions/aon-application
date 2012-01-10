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

public class AssetCollectionsController {

	private List<SelectItem> activityStatuses;
	private List<SelectItem> viewerTypes;
	private List<SelectItem> featureList;
	
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
	
	public List<SelectItem> getFeatureList() throws ManagerBeanException {
		if (featureList == null) {
			featureList = new LinkedList<SelectItem>();
			IManagerBean bean = BeanManager.getManagerBean(Feature.class);
			for (ITransferObject to : bean.getList(null)) {
				Feature f = (Feature) to;
				SelectItem roomItem = new SelectItem(f, f.getName());
				featureList.add(roomItem);
			}
		}
		return featureList;
	}
	
		
		
}