package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.NAVIGATION_TARGET_FORM;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_COMMERCIAL_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_PROJECT_COMMERCIAL_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.commercial.event.ProjectCommercialSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class TargetProjectCommercialController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private BasicController getProjectController()  {
		return (BasicController) AonUtil.getRegisteredBean(PROJECT_COMMERCIAL_CONTROLLER_NAME);
	}
	
	public void onSelectProject( ActionEvent event ) throws ManagerBeanException {	
		ITransferObject to = (ITransferObject) getSelectedTO();
		BasicController projectBean = getProjectController();
		Integer size = projectBean.getListeners().size();
		for(Integer i = size; i > 0; i--) {
			try {
				ProjectCommercialSearchListener listener = (ProjectCommercialSearchListener) projectBean.getListeners().get(i-1);
				listener.setSeller(null);
				listener.setTarget(null);
			} catch (Exception e) {}	
		}
		projectBean.setBackAction(NAVIGATION_TARGET_FORM);
		projectBean.setBackActionListener(TARGET_PROJECT_COMMERCIAL_CONTROLLER_NAME + ".onBackToTarget");
		projectBean.select(event, to);
	}

	public void onResetProject( ActionEvent event ) throws ManagerBeanException {	
		BasicController projectBean = getProjectController();
		projectBean.onReset(event);
		IController targetController = FormUtil.getController(TARGET_CONTROLLER_NAME);
		Target target = (Target) targetController.getTo();
		ProjectCommercial project = (ProjectCommercial) projectBean.getTo();
		project.setTarget(target);
		projectBean.setBackAction(NAVIGATION_TARGET_FORM);
		projectBean.setBackActionListener(TARGET_PROJECT_COMMERCIAL_CONTROLLER_NAME + ".onBackToTarget");
	}
	
	public void onBackToTarget( ActionEvent event ) {
		BasicController projectBean = getProjectController();
		projectBean.onBack(event);
		initializeModel();
	}

	public void onResetTargetProject( ActionEvent event ) throws ManagerBeanException {	
		BasicController projectBean = (BasicController) AonUtil.getRegisteredBean(TARGET_PROJECT_COMMERCIAL_CONTROLLER_NAME);
		projectBean.onReset(event);
		IController targetController = FormUtil.getController(TARGET_CONTROLLER_NAME);
		Target target = (Target) targetController.getTo();
		ProjectCommercial project = (ProjectCommercial) projectBean.getTo();
		project.setTarget(target);
	}
	
}