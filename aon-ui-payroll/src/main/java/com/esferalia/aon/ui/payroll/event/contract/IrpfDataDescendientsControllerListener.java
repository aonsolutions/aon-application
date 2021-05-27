package com.esferalia.aon.ui.payroll.event.contract;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;

public class IrpfDataDescendientsControllerListener extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataDescendientsControllerListener.class.getName());
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		updateDescendats();
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		updateDescendats();
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
