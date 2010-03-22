package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.NominadevController;

public class NominadevControllerListener extends ControllerAdapter implements
		IPayrollConstants {


@Override
public void afterBeanReset(ControllerEvent event)
		throws ControllerListenerException {

	((NominadevController)getController()).generateCdg();
}

@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	((NominadevController)getController()).generateCdg();

}
}
