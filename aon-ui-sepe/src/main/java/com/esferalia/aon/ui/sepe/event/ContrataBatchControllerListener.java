package com.esferalia.aon.ui.sepe.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.sepe.controller.ContrataController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;

/**
 * Listener added to the ContrataBatchController
 * 
 */
public class ContrataBatchControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContrataBatch batch = (ContrataBatch) this.getController().getTo();
		if(batch.getStatus() == FileStatus.GENERATED){
			ContrataController contrataController = (ContrataController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
			contrataController.initialize(batch);
		}
	}
	
}
