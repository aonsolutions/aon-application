package com.code.aon.ui.consultant.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.consultant.RegistryDirStaff;
import com.code.aon.customer.Customer;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class RegistryDirStaffControllerListener extends ControllerAdapter {

	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController) AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer) customerController.getTo();
		((RegistryDirStaff) event.getController().getTo()).setRegistry(customer.getRegistry());
	}

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
