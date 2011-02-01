package com.code.aon.ui.commercial.event;

import java.util.Date;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.TargetController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class TargetTrackingControllerListener extends ControllerAdapter implements ICommercialConstants {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		TargetController c = (TargetController)AonUtil.getRegisteredBean(TARGET_CONTROLLER_NAME);
		CommercialTracking ct = (CommercialTracking)getController().getTo();
		ct.setTarget((Target)c.getTo());
		if ( ct.getStatus() == CommercialTrackingStatus.CLOSED ) {
			ct.setEndDate(new Date());
		} else {
			ct.setEndDate(null);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CommercialTracking ct = (CommercialTracking)getController().getTo();
		if ( ct.getStatus() == CommercialTrackingStatus.CLOSED ) {
			ct.setEndDate(new Date());
		} else {
			ct.setEndDate(null);
		}
	}

	
}