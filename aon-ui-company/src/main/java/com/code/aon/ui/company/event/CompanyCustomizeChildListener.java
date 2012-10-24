package com.code.aon.ui.company.event;

import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CUSTOMIZE_CONTROLLER_NAME;

import com.code.aon.ui.company.controller.CompanyCustomizeController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the CompanyController.
 */
public class CompanyCustomizeChildListener extends LinesControllerListener {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);
		CompanyCustomizeController ccc = (CompanyCustomizeController) AonUtil.getRegisteredBean(COMPANY_CUSTOMIZE_CONTROLLER_NAME);
		ccc.init();
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanUpdated(event);
		CompanyCustomizeController ccc = (CompanyCustomizeController) AonUtil.getRegisteredBean(COMPANY_CUSTOMIZE_CONTROLLER_NAME);
		ccc.save();
	}	

}
