package com.code.aon.ui.cms.event;

import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.FaqController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FaqControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		FaqController controller = (FaqController)event.getController(); 
		try {
			controller.completeCriteria();
			controller.getCriteria().addOrder(controller.getFieldName(ICMSAlias.FAQ_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
