package com.code.aon.ui.marketing.event;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.CommunicationCenterController;
import com.code.aon.ui.marketing.controller.IMarketingConstants;
import com.code.aon.ui.util.AonUtil;

public class CommunicationCenterTargetListener extends ControllerAdapter implements IMarketingConstants {
	
	private CommunicationCenterController getCommunicationCenterController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getCommunicationCenterController().setTarget(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Target newTarget = (Target) event.getController().getTo();
			getCommunicationCenterController().initTarget( newTarget );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}
