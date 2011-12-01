package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.COMMERCIAL_TRACKING_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.NAVIGATION_PROJECT_COMMERCIAL_FORM;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_COMMERCIAL_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_COMMERCIAL_TRACKING_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class ProjectCommercialTrackingController extends BasicController {
	
	private BasicController getCommercialTracking()  {
		return (BasicController) AonUtil.getRegisteredBean(COMMERCIAL_TRACKING_CONTROLLER_NAME);
	}
	
	public void onSelectTracking( ActionEvent event ) throws ManagerBeanException {	
		ITransferObject to = (ITransferObject) getSelectedTO();
		BasicController ct = getCommercialTracking();
		ct.setBackAction(NAVIGATION_PROJECT_COMMERCIAL_FORM);
		ct.setBackActionListener(PROJECT_COMMERCIAL_TRACKING_CONTROLLER_NAME + ".onBackToProject");
		ct.select(event, to);
	}

	public void onResetTracking( ActionEvent event ) throws ManagerBeanException {	
		BasicController ctBean = getCommercialTracking();
		ctBean.onReset(event);
		IController projectController = FormUtil.getController(PROJECT_COMMERCIAL_CONTROLLER_NAME);
		ProjectCommercial project = (ProjectCommercial) projectController.getTo();
		CommercialTracking ct = (CommercialTracking) ctBean.getTo();
		ct.setProject(project);
		ctBean.setBackAction(NAVIGATION_PROJECT_COMMERCIAL_FORM);
		ctBean.setBackActionListener(PROJECT_COMMERCIAL_TRACKING_CONTROLLER_NAME + ".onBackToProject");
	}
	
	public void onBackToProject( ActionEvent event ) {
		BasicController ct = getCommercialTracking();
		ct.onBack(event);
		initializeModel();
	}
	
}