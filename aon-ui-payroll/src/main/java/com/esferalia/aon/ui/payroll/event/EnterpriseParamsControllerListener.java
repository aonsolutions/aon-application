package com.esferalia.aon.ui.payroll.event;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.payroll.controller.EnterpriseParamsController;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseParamsControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		loadEnterpriseParams();
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		acceptEnterpriseParams();
	}
	
	private void loadEnterpriseParams(){
		EnterpriseParamsController controller = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
		controller.onLoad(null);
	}
	
	private void acceptEnterpriseParams(){
		EnterpriseParamsController controller = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
		controller.onAccept(null);
	}
	
}
