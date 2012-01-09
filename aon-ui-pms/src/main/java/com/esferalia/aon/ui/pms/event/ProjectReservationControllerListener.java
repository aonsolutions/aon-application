package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservation reservation = (ProjectReservation)event.getController().getTo();
		reservation.setCreationDate(new Date());
		reservation.setStatus(ReservationStatus.ACTIVE);
	}
}