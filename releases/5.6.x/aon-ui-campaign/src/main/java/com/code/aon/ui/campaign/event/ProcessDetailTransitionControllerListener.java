package com.code.aon.ui.campaign.event;

import com.code.aon.campaign.ProcessDetailTransition;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProcessDetailTransitionControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		ProcessDetailTransition pdt = (ProcessDetailTransition) controller.getTo();
		
		if (pdt.getNextProcessDetail() != null &&
				pdt.getNextProcessDetail().getId() == pdt.getProcessDetail().getId()) {
			throw new ControllerListenerException( "No se puede realizar esta transición. la acción es la misma."); 
		}
		
	}
	
}
