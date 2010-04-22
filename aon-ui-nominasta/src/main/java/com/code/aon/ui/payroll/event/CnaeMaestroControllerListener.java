package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.CnaeMaestroController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;


public class CnaeMaestroControllerListener extends ControllerAdapter implements IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterBeanCreated");
		super.afterBeanCreated(event);
	}

	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterBeanReset");
		super.afterBeanReset(event);
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterModelInitialized");
		((CnaeMaestroController) getController()).initializeOcupacionesList();
		super.afterModelInitialized(event);
	}
	
	@Override
	public void beforeEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		//event.getController().getTo().
		//CnaeMaestroController cnaeMaestro = (CnaeMaestroController) event.getController();
		//((CnaeMaestroController) getController()).initializeOcupacionesList();
		((CnaeMaestroController) getController()).initializeAsignedLists();
		
	}
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		super.beforeBeanCreated(event);
	}

}
