package com.code.aon.ui.audit.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.audit.controller.AuditCollectionsController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainControllerListener extends ControllerAdapter implements IAuditConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		resetApplications();		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		resetApplications();
	}
	
	private void resetApplications() throws ControllerListenerException {
		AuditCollectionsController collections = (AuditCollectionsController) AonUtil.getRegisteredBean(AUDIT_COLLECTIONS_CONTROLLER_NAME);
		try {
			collections.refreshApplications();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}
