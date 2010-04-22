package com.code.aon.ui.cms.event;

import com.code.aon.cms.Section;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SectionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		assignNullValues(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		assignNullValues(event);
	}
	
	private void assignNullValues(ControllerEvent event){
		Section to = (Section)event.getController().getTo();
		if (to.getParent_().getId()==-1){
			to.setParent_(null);
		}
		if (to.getHeader().getId()==-1){
			to.setHeader(null);
		}
		if (to.getFooter().getId()==-1){
			to.setFooter(null);
		}
		if (to.getMenu().getId()==-1){
			to.setMenu(null);
		}
		if (to.getMenu_alt().getId()==-1){
			to.setMenu_alt(null);
		}
		if (to.getSidebar().getId()==-1){
			to.setSidebar(null);
		}
	}

}
