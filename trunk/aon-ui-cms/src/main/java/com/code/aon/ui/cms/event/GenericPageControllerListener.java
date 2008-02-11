package com.code.aon.ui.cms.event;

import com.code.aon.cms.GenericPage;
import com.code.aon.ui.cms.controller.GenericPageController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class GenericPageControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		GenericPageController controller = (GenericPageController)event.getController();
		GenericPage page = (GenericPage)controller.getTo();
		if (page.getSection().getId()==-1){
			page.setSection(null);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		GenericPageController controller = (GenericPageController)event.getController();
		GenericPage page = (GenericPage)controller.getTo();
		if (page.getSection().getId()==-1){
			page.setSection(null);
		}
	}

}