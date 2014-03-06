package com.code.aon.ui.academy.event;

import static com.code.aon.ui.academy.controller.IAcademyConstants.CUSTOMER_ABSENCE_CONTROLLER_NAME;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.academy.controller.CustomerAbsenceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerAbsenceChildListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			setCustomer(event);
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			setCustomer(event);			
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private void setCustomer(ControllerEvent event) throws ManagerBeanException {
		Customer customer = (Customer) event.getController().getTo();
		CustomerAbsenceController absenceController = (CustomerAbsenceController)AonUtil.getRegisteredBean(CUSTOMER_ABSENCE_CONTROLLER_NAME);
		absenceController.setCustomer(customer);
		absenceController.refresh(null);
	}

}
