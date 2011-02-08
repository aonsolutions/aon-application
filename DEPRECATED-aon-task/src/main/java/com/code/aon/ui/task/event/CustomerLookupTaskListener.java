package com.code.aon.ui.task.event;

import java.util.LinkedList;

import javax.faces.model.SelectItem;

import com.code.aon.customer.Customer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.TaskController;
import com.code.aon.ui.util.AonUtil;

public class CustomerLookupTaskListener extends ControllerAdapter {
	
	private static final String TASK_CONTROLLER_NAME = "task";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TaskController taskController = (TaskController)AonUtil.getController(TASK_CONTROLLER_NAME);
		if(taskController != null){
			Customer customer = (Customer)event.getController().getTo();
			if (customer != null && customer.getId() != null) {
				taskController.loadDossiers(customer.getId());
	        } else {
	        	taskController.setDossiers(new LinkedList<SelectItem>());
	        }
			taskController.setActivities(new LinkedList<SelectItem>());
		}
	}
}