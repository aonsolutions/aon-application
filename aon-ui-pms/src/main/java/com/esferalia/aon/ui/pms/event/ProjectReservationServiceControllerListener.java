package com.esferalia.aon.ui.pms.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservationService;

public class ProjectReservationServiceControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationService to = (ProjectReservationService)event.getController().getTo();
		to.setServiceIndex(0);
	}

}