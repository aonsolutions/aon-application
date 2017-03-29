package com.esferalia.aon.ui.pms.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.ui.pms.controller.ProjectReservationController;

public class ProjectReservationAttachControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController reservationController = (ProjectReservationController)((LinesController)event.getController()).getMasterController();
		ProjectReservation reservation = (ProjectReservation)reservationController.getTo();
		reservation.setLastConexFlowOperation(null);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController reservationController = (ProjectReservationController)((LinesController)event.getController()).getMasterController();
		ProjectReservation reservation = (ProjectReservation)reservationController.getTo();
		reservation.setLastConexFlowOperation(null);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController reservationController = (ProjectReservationController)((LinesController)event.getController()).getMasterController();
		ProjectReservation reservation = (ProjectReservation)reservationController.getTo();
		reservation.setLastConexFlowOperation(null);
	}

}