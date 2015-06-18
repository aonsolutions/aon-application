package com.esferalia.aon.ui.pms.event;

import com.code.aon.AonVersion;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class WorkPlaceDepartmentControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		WorkplaceDepartment wpd = (WorkplaceDepartment) event.getController().getTo();
		wpd.setDepartment(wpd.getWarehouse().getDepartment());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		WorkplaceDepartment wpd = (WorkplaceDepartment) event.getController().getTo();
		wpd.setDepartment(wpd.getWarehouse().getDepartment());
	}

}
