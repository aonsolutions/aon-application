package com.code.aon.ui.project.controller.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProjectActivityControllerListener extends ControllerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProjectActivityControllerListener.class);
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		checkActivityType(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			if(activityTypeChanged((ProjectActivity)event.getController().getTo())){
				checkActivityType(event);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkActivityType(ControllerEvent event) throws ControllerListenerException {
		ProjectActivity currentActivity = (ProjectActivity) event.getController().getTo();
		try {
			List<ProjectActivity> list= ((List<ProjectActivity>) event.getController().getModel().getWrappedData());
			for (ProjectActivity activity:list ) {
				if(activity.getActivityType().getId().equals(currentActivity.getActivityType().getId())){
					throw new ControllerListenerException("Ya existe una actividad de tipo: " + currentActivity.getActivityType().getDescription());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	private boolean activityTypeChanged(ProjectActivity activity) throws ManagerBeanException {
		IManagerBean activityBean = BeanManager.getManagerBean(ProjectActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.PROJECT_ACTIVITY_ID), activity.getId());
		List<ITransferObject> list = activityBean.getList(criteria, 0, 1);
		for (ITransferObject to:list) {
			ProjectActivity pa = (ProjectActivity) to;
			return !activity.getActivityType().getId().equals(pa.getActivityType().getId());
		}
		return true;
	}
}