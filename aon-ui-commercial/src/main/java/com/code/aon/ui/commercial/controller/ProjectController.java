package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.COMMERCIAL_TRACKING_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.NAVIGATION_PROJECT_FORM;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.PROJECT_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class ProjectController extends BasicController {
	
	public void onSelectTracking( ActionEvent event ) {	
		BasicController ct = (BasicController) AonUtil.getRegisteredBean(COMMERCIAL_TRACKING_CONTROLLER_NAME);
		ct.onSelect(event);
		ct.setBackAction(NAVIGATION_PROJECT_FORM);
		ct.setBackActionListener(PROJECT_CONTROLLER_NAME + ".onBack");
	}
	
}