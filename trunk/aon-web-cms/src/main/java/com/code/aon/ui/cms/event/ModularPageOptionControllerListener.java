package com.code.aon.ui.cms.event;

import com.code.aon.cms.ModularPageOption;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.ModularPageOptionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ModularPageOptionControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ModularPageOptionController controller = (ModularPageOptionController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ModularPageOptionController controller = (ModularPageOptionController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ModularPageOptionController soc = (ModularPageOptionController)event.getController();
		ModularPageOption so = (ModularPageOption)soc.getTo();
		so.setModular_page(soc.getCurrentModularPage());
		so.setPosition(soc.orderedControllerSupport.getLastPosition(soc));
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((ModularPageOptionController)event.getController()).loadCurrentLanguage();
	}
}
