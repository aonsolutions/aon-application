package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservationRoom;

public class ProjectReservationRoomControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationRoom to = (ProjectReservationRoom)event.getController().getTo();
		to.setRoomIndex(0);
	}

}