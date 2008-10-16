package com.code.aon.ui.marketing.event;

import com.code.aon.marketing.Survey;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.CommunicationCenterController;
import com.code.aon.ui.marketing.controller.IMarketingConstants;
import com.code.aon.ui.util.AonUtil;

public class CommunicationCenterSurveyListener extends ControllerAdapter implements IMarketingConstants {
	
	private CommunicationCenterController getCommunicationCenterController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getCommunicationCenterController().setSurvey(null);
	}
	
}
