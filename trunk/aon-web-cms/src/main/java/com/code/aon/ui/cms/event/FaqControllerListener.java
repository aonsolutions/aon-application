package com.code.aon.ui.cms.event;

import com.code.aon.cms.Faq;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.FaqController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FaqControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		FaqController controller = (FaqController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		FaqController controller = (FaqController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FaqController fc = (FaqController)event.getController();
		Faq f = (Faq)event.getController().getTo();
		f.setFaqCategory(fc.getCurrentFaqCategory());
		f.setPosition(fc.orderedControllerSupport.getLastPosition(fc));
	}
	
}
