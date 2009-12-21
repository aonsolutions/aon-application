package com.code.aon.ui.cms.event;

import com.code.aon.cms.GenericPage;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class GenericPageControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}
	
	private void assignSection(ControllerEvent event){
		GenericPage page = (GenericPage)event.getController().getTo();
		if (page.getSection().getId()==-1){
			page.setSection(null);
		}
	}
}