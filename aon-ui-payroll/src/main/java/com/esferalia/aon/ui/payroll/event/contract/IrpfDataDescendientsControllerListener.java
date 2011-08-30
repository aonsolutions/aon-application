package com.esferalia.aon.ui.payroll.event.contract;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;

public class IrpfDataDescendientsControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataDescendientsControllerListener.class.getName());
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		updateDescendats();
		calculateIrpf();
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		updateDescendats();
		calculateIrpf();
	}
	
	private void calculateIrpf() {
		IrpfDataController controller = (IrpfDataController) ((LinesController) getController()).getMasterController();
		controller.onCalculateIrpf(null);
	}
	private void updateDescendats() {
		try {
			IrpfData data = (IrpfData) ((LinesController) getController()).getMasterController().getTo();
			data.setDescendientCount(getController().getRowCount());
			getController().getManagerBean().update(data);
		} catch (ManagerBeanException e) {
			String msg = "Error al actualizar los datos de irpf";
			LOGGER.error(msg);
		}
	}
		
}
