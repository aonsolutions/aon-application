package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.NAVIGATION_TARGET_FORM;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_PROJECT_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Project;
import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class TargetProjectController extends BasicController {
	
	private BasicController getProjectController()  {
		return (BasicController) AonUtil.getRegisteredBean(PROJECT_CONTROLLER_NAME);
	}
	
	public void onSelectProject( ActionEvent event ) throws ManagerBeanException {	
		ITransferObject to = (ITransferObject) getSelectedTO();
		BasicController projectBean = getProjectController();
		projectBean.setBackAction(NAVIGATION_TARGET_FORM);
		projectBean.setBackActionListener(TARGET_PROJECT_CONTROLLER_NAME + ".onBackToTarget");
		projectBean.select(event, to);
	}

	public void onResetProject( ActionEvent event ) throws ManagerBeanException {	
		BasicController projectBean = getProjectController();
		projectBean.onReset(event);
		IController targetController = FormUtil.getController(TARGET_CONTROLLER_NAME);
		Target target = (Target) targetController.getTo();
		Project project = (Project) projectBean.getTo();
		project.setTarget(target);
		projectBean.setBackAction(NAVIGATION_TARGET_FORM);
		projectBean.setBackActionListener(TARGET_PROJECT_CONTROLLER_NAME + ".onBackToTarget");
	}
	
	public void onBackToTarget( ActionEvent event ) {
		BasicController projectBean = getProjectController();
		projectBean.onBack(event);
		initializeModel();
	}
	
}