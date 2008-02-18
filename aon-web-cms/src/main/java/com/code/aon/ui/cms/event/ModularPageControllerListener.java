package com.code.aon.ui.cms.event;

import com.code.aon.cms.ModularPage;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ModularPageControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}

	private void assignSection(ControllerEvent event){
		ModularPage page = (ModularPage)event.getController().getTo();
		if (page.getSection().getId()==-1){
			page.setSection(null);
		}
	}
}