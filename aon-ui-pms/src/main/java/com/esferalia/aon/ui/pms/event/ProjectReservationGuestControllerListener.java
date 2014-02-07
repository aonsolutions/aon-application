package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.enumeration.Country;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class ProjectReservationGuestControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuest to = (ProjectReservationGuest)event.getController().getTo();
		to.setDocumentCountry(Country.ES);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuest to = (ProjectReservationGuest)event.getController().getTo();
		to.setGuestIndex(0);
	}

}