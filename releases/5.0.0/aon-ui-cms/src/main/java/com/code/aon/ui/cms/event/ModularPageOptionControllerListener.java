package com.code.aon.ui.cms.event;

import com.code.aon.cms.ModularPageOption;
import com.code.aon.ui.cms.controller.ModularPageOptionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ModularPageOptionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ModularPageOptionController soc = (ModularPageOptionController)event.getController();
		ModularPageOption so = (ModularPageOption)soc.getTo();
		so.setModular_page(soc.getCurrentModularPage());
		so.setPosition(soc.getLastPosition());
	}

}
