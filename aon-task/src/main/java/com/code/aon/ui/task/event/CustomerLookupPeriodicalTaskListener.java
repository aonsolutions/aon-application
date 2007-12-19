package com.code.aon.ui.task.event;

import java.util.LinkedList;

import javax.faces.model.SelectItem;

import com.code.aon.customer.Customer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.PeriodicalTaskController;
import com.code.aon.ui.util.AonUtil;

public class CustomerLookupPeriodicalTaskListener extends ControllerAdapter {
	
	private static final String PERIODICAL_TASK_CONTROLLER_NAME = "periodTask";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PeriodicalTaskController taskController = (PeriodicalTaskController) AonUtil.getController(PERIODICAL_TASK_CONTROLLER_NAME);
		if(taskController != null){
			Customer customer = (Customer) event.getController().getTo();
			if (customer != null && customer.getId() != null) {
				taskController.loadDossiers(customer.getId());
	        } else {
	        	taskController.setDossiers(new LinkedList<SelectItem>());
	        }
			taskController.setActivities(new LinkedList<SelectItem>());
		}
	}
	
}