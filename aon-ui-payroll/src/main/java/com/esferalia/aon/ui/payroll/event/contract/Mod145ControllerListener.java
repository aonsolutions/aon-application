package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.Mod145;
import com.esferalia.aon.ui.payroll.controller.contract.Mod145Controller;

public class Mod145ControllerListener extends ControllerAdapter{
	
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Mod145Controller controller = (Mod145Controller) this.getController();
		Mod145 m = (Mod145) controller.getTo();
		m.setDate(new Date());
		m.setDescendientCount(controller.getDescendientCount());
	}
		
}
