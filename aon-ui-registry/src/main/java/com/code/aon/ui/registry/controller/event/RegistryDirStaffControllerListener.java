package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryDirStaffControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			RegistryDirStaff rdirStaff = (RegistryDirStaff) event.getController().getTo();
			IManagerBean rdirStaffBean = BeanManager.getManagerBean(RegistryDirStaff.class);
			if (!rdirStaff.isShareHolder()) {
				rdirStaff.setPercentShare(0.0);
				rdirStaff.setShareNumber(new Integer(0));
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
