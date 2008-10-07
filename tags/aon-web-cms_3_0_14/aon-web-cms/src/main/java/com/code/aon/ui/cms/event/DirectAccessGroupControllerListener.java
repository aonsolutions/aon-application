package com.code.aon.ui.cms.event;

import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.DirectAccessGroupController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DirectAccessGroupControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		DirectAccessGroupController controller = (DirectAccessGroupController)event.getController(); 
		try {
			controller.completeCriteria();
			controller.getCriteria().addOrder(controller.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
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
