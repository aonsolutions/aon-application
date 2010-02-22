package com.code.aon.ui.cms.event;

import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.DirectAccessController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DirectAccessControllerListener extends ControllerAdapter implements ICMSConstants {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		DirectAccessController controller = (DirectAccessController)event.getController(); 
		try {
			controller.completeCriteria();
			controller.getCriteria().addOrder(controller.getFieldName(ICMSAlias.DIRECT_ACCESS_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		FormUtil.getController(DIRECT_ACCESS).onSearch(null);
	}
}
