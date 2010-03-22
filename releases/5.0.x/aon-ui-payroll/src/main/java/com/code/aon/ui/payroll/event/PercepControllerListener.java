package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.PercepBasicController;
import com.code.aon.ui.payroll.controller.PercepController;

public class PercepControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		PercepController controller = (PercepController) event.getController();
		try {

			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		PercepController controller = (PercepController) event.getController();
		try {

			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		
		
		PercepBasicController pbc = (PercepBasicController)FormUtil.getController(IPayrollConstants.PERCEP_BASIC_CONTROLLER_NAME);
		try {
			pbc.setModel(this.getController().getModel());
			pbc.onSelect(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {

		((PercepController) getController()).generarNumero(null);
		
		PercepBasicController pbc = (PercepBasicController)FormUtil.getController(IPayrollConstants.PERCEP_BASIC_CONTROLLER_NAME);
		try {
			pbc.setModel(this.getController().getModel());
			pbc.onSelect(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
