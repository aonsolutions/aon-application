package com.code.aon.ui.groupware.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.AlarmController;

public class AlarmControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		AlarmController controller = (AlarmController) event.getController();
		controller.initAlarm();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AlarmController controller = (AlarmController) event.getController();
		Alarm alarm = (Alarm) controller.getTo();
		controller.setDelayTime(null);
		alarm.setStatus(AlarmStatus.READ);
		try {
			controller.getManagerBean().update(alarm);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		controller.updatePendingCount();
	}

}