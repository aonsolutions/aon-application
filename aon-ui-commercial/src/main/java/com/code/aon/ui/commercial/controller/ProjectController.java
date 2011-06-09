package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.COMMERCIAL_TRACKING_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.NAVIGATION_PROJECT_FORM;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_TRACKING_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class ProjectController extends BasicController {
	
	public void onSelectTracking( ActionEvent event ) throws ManagerBeanException {	
		BasicController pt = (BasicController) AonUtil.getRegisteredBean(PROJECT_TRACKING_CONTROLLER_NAME);
		ITransferObject to = (ITransferObject) pt.getModel().getRowData();
		BasicController ct = (BasicController) AonUtil.getRegisteredBean(COMMERCIAL_TRACKING_CONTROLLER_NAME);
		ct.setBackAction(NAVIGATION_PROJECT_FORM);
		ct.setBackActionListener(PROJECT_CONTROLLER_NAME + ".onBack");
		ct.select(event, to);
	}
	
}