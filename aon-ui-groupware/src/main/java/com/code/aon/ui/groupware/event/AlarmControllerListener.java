package com.code.aon.ui.groupware.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.AlarmController;

public class AlarmControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		AlarmController controller = (AlarmController) event.getController();
		controller.initAlarm();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AlarmController controller = (AlarmController) event.getController();
		controller.setDelayTime(null);
		try {
			Alarm alarm = (Alarm) controller.getTo();
			alarm.setStatus(AlarmStatus.READ);
			controller.getManagerBean().update(alarm);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		controller.updatePendingCount();
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		Alarm alarm = (Alarm) event.getController().getTo();
		alarm.setStatus(AlarmStatus.PENDING);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AlarmController controller = (AlarmController) event.getController();
		try {
			controller.updateModels();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}