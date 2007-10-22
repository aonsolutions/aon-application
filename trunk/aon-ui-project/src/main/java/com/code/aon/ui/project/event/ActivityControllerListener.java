package com.code.aon.ui.project.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Activity;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.ActivityController;

public class ActivityControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(ActivityControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		checkActivityType(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		checkActivityType(event);
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
			LOGGER.log(Level.SEVERE, "Error obtaining ActivityController model", e);
		}
	}
}
