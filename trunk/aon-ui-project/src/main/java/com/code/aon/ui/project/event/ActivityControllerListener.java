package com.code.aon.ui.project.event;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Activity;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.ActivityController;

public class ActivityControllerListener extends ControllerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActivityControllerListener.class);
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		checkActivityType(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			if(activityTypeChanged((Activity)event.getController().getTo())){
				checkActivityType(event);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error updating activity with id= " + ((Activity)event.getController().getTo()).getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkActivityType(ControllerEvent event) throws ControllerListenerException {
		ActivityController activityController = (ActivityController)event.getController();
		Activity currentActivity = (Activity)activityController.getTo();
		try {
			Iterator iter = ((List)activityController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				Activity activity = (Activity)iter.next();
				if(activity.getActivityType().getId().equals(currentActivity.getActivityType().getId())){
					throw new ControllerListenerException("Ya existe una actividad de tipo: " + currentActivity.getActivityType().getDescription());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining ActivityController model", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean activityTypeChanged(Activity activity) throws ManagerBeanException {
		IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ID), activity.getId());
		Iterator iter = activityBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			Activity dbActivity = (Activity)iter.next();
			return !activity.getActivityType().getId().equals(dbActivity.getActivityType().getId());
		}
		return true;
	}
}