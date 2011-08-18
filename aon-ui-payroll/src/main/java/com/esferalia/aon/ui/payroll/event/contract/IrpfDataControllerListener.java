package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;

public class IrpfDataControllerListener extends ControllerAdapter{
	
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IrpfDataController controller = (IrpfDataController) this.getController();
		IrpfData m = (IrpfData) controller.getTo();
		m.setDate(new Date());
		m.setDescendientCount(controller.getDescendientCount());
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		IrpfDataController controller = (IrpfDataController) this.getController();
		IrpfData m = (IrpfData) controller.getTo();
		m.setDescendientCount(controller.getDescendientCount());
	}
		
}
