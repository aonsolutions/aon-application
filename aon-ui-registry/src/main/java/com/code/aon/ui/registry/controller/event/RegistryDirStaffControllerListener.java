package com.code.aon.ui.registry.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryDirStaffLinesController;

public class RegistryDirStaffControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		initializeData(event);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		initializeData(event);
	}

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		refreshTotals(event);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		refreshTotals(event);
	}
	
	private void refreshTotals(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		if (controller instanceof RegistryDirStaffLinesController) {
			RegistryDirStaffLinesController rirStaffcontroller = (RegistryDirStaffLinesController) controller;
			rirStaffcontroller.refreshTotals();
		}
	}
	
	private void initializeData(ControllerEvent event) throws ControllerListenerException {
		try {
			refreshTotals( event );
			RegistryDirStaff rdirStaff = (RegistryDirStaff) event.getController().getTo();
			IManagerBean rdirStaffBean = BeanManager.getManagerBean(RegistryDirStaff.class);
			if (!rdirStaff.isShareHolder()) {
				rdirStaff.setPercentShare(0.0);
				rdirStaff.setShareNumber( 0 );
				rdirStaff.setNominalValue(0.0);
			}
			if(!rdirStaff.isDirector() && !rdirStaff.isRepresentative()){
				rdirStaff.setDueDate(null);
			}
			rdirStaff = (RegistryDirStaff)rdirStaffBean.update(rdirStaff);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
