package com.code.aon.ui.project.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.TaskMonitorController;

public class TaskMonitorControllerListener extends ControllerAdapter{

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		TaskMonitorController monitorController = (TaskMonitorController)event.getController();
		monitorController.setCustomer(initializeCustomer());
	}

	@Override
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		try {
			TaskMonitorController monitorController = (TaskMonitorController)event.getController();
			if(monitorController.getCustomer() != null && monitorController.getCustomer().getId() != null){
				monitorController.getCriteria().addEqualExpression(monitorController.getFieldName(IProjectAlias.TASK_CUSTOMER_ID), monitorController.getCustomer().getId());
			}	
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding criteria beforeBeanReset", e);
		}
	}

	private Customer initializeCustomer() {
		Customer customer = new Customer();
		customer.setRegistry(new Registry());
		return customer;
	}
}