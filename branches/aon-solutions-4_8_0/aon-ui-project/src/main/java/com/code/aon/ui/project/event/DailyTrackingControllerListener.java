package com.code.aon.ui.project.event;

import java.util.Date;
import java.util.LinkedList;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.DailyTrackingController;

public class DailyTrackingControllerListener extends ControllerAdapter {
	
    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
        ((DailyTracking)event.getController().getTo()).setTrackingDate(new Date());
        ((DailyTracking)event.getController().getTo()).setTrackingDuration(new Double(1));
        initialize(event);
    }
    
    private void initialize(ControllerEvent event) {
        DailyTrackingController controller = (DailyTrackingController)event.getController();
        controller.setDossiers(new LinkedList<SelectItem>());
        controller.setActivities(new LinkedList<SelectItem>());
    }

    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        DailyTracking dailyTracking = (DailyTracking)event.getController().getTo();
        User user = UserUtils.getInstance().getLoggedUser();
        if(user != null){
        	dailyTracking.setUser(user);
        }
        else{
        	throw new ControllerListenerException("Error obtaining logged user");
        }
    }

	@Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
        DailyTrackingController controller = (DailyTrackingController)event.getController();
        DailyTracking tracking = (DailyTracking)controller.getTo();

        if (tracking.getCustomer().getId() != null) {
            controller.loadDossiers(tracking.getCustomer().getRegistry().getId());
            controller.loadActivities(tracking.getDossier().getId());
        } else {
            initialize(event);
        }
    }
    
    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
    	try {
    		DailyTrackingController trackingController = (DailyTrackingController)event.getController();
    		if(!trackingController.isMonitor()){
    			IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
    			trackingController.getCriteria().addEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID), UserUtils.getInstance().getLoggedUser().getId());
    		}
    		trackingController.completeCriteria();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
    }
    
}