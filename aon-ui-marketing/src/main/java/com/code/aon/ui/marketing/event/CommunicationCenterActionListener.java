package com.code.aon.ui.marketing.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Action;
import com.code.aon.marketing.Survey;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.CommunicationCenterController;
import com.code.aon.ui.marketing.controller.IMarketingConstants;
import com.code.aon.ui.util.AonUtil;

public class CommunicationCenterActionListener extends ControllerAdapter implements IMarketingConstants {
	
	private CommunicationCenterController getCommunicationCenterController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getCommunicationCenterController().setAction(null);		
		getCommunicationCenterController().setActionTarget(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		Action action = (Action) event.getController().getTo();
		getCommunicationCenterController().setAction( action );
		try {
			if ( action.getSurvey() != null ) {
				IManagerBean bean = BeanManager.getManagerBean(Survey.class);
				Survey survey = (Survey) bean.get( action.getSurvey().getId() );
				getCommunicationCenterController().setSurvey( survey );
			}	
			getCommunicationCenterController().onNextTarget(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}				
	}
	
}
