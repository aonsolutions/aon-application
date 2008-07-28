package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class CommercialTrackingController extends BasicController {
	
	private List<SelectItem> activities;

	public List<SelectItem> getActivities() {
		return activities;
	}	

	@SuppressWarnings("unchecked")
	public void refreshActivities() throws ManagerBeanException {
		activities = new LinkedList<SelectItem>();
		IManagerBean activityBean = BeanManager.getManagerBean(CommercialActivity.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(activityBean.getFieldName(ICommercialAlias.COMMERCIAL_ACTIVITY_NAME));
		Iterator<ITransferObject> iter = activityBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CommercialActivity activity = (CommercialActivity)iter.next();
			SelectItem item = new SelectItem(activity, activity.getName());
			activities.add(item);
		}
	}	
	
}