package com.code.aon.ui.groupware.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.DailyTracking;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.groupware.controller.DailyTrackingController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DailyTrackingControllerListener extends ControllerAdapter {

	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	@Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			DailyTrackingController trackingController = (DailyTrackingController)event.getController();
			DailyTracking dt = (DailyTracking) trackingController.getTo(); 
			dt.setTrackingDate(new Date());
			dt.setTrackingDuration(new Double(1));
			dt.setTaskHolder( getGroupwareUtils().getCurrentTaskHolder() );
			try {
				trackingController.loadProjects(null);
			} catch (ManagerBeanException e) {
				String msg = "Error al cargar la lista de proyectos";
				AonUtil.addErrorMessage(msg);
				throw new ControllerListenerException(msg);
			}
			try {
				trackingController.loadActivityTypes(null);
			} catch (ManagerBeanException e) {
				String msg = "Error al cargar la lista de tipos de actividades";
				AonUtil.addErrorMessage(msg);
				throw new ControllerListenerException(msg);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( "No se pudo inicializar el parte de trabajo. " + e.getMessage()); 
		}
    }
    
    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
    	try {
    		DailyTrackingController trackingController = (DailyTrackingController)event.getController();
    		IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
    		if(!trackingController.isMonitor()){
    			TaskHolder taskHolder = getGroupwareUtils().getCurrentTaskHolder();
    			trackingController.getCriteria().addEqualExpression(dailyTrackingBean.getFieldName(IEntityAlias.DAILY_TRACKING_TASK_HOLDER_ID), taskHolder.getId() );
    		}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
    }
    
    /*	
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
    
*/   
}