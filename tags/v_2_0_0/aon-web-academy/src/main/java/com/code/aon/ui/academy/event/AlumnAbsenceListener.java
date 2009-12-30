package com.code.aon.ui.academy.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.academy.controller.AlumnAbsenceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AlumnAbsenceListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			AlumnAbsenceController absenceController = (AlumnAbsenceController)AonUtil.getController("absence");
			absenceController.setCustomer(getCustomer(event));
			absenceController.refresh(null);
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			AlumnAbsenceController absenceController = (AlumnAbsenceController)AonUtil.getController("absence");
			absenceController.setCustomer(getCustomer(event));
			absenceController.refresh(null);
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private Customer getCustomer(ControllerEvent event) throws ManagerBeanException{
		return (Customer)getAlumnController(event).getTo();
	}
	
	private BasicController getAlumnController(ControllerEvent event) throws ManagerBeanException{
		return (BasicController)event.getController();
	}


}
