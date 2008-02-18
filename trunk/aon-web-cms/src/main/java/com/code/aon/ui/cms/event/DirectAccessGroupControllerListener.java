package com.code.aon.ui.cms.event;

import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DirectAccessGroupControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DirectAccessGroup directAccessGroup = (DirectAccessGroup)event.getController().getTo();
		directAccessGroup.setActive(true);
		assignSection(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		assignSection(event);
	}
	
	private void assignSection(ControllerEvent event){
		DirectAccessGroup to = (DirectAccessGroup)event.getController().getTo();
		if (to.getSection().getId()==-1){
			to.setSection(null);
		}
	}

}
