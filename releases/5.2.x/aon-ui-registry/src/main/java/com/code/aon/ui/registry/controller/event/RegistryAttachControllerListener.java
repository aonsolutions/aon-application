package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryAttachController;

public class RegistryAttachControllerListener extends AttachmentControllerListener {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		super.beforeModelInitialized(event);
		try {
			RegistryAttachController raController = (RegistryAttachController) event.getController();
			if ( raController.getType() != null ) {
				IManagerBean rAttachBean = raController.getManagerBean();
				Criteria criteria = raController.getCriteria();
				criteria.addEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), raController.getType());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanCreated(event);
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		RegistryAttachment attach = (RegistryAttachment) raController.getTo();
		if ( raController.getType() != null ) {
			attach.setRegistryAttachmentType( raController.getType() );
		}
		attach.setCategory(null);		
	}
	
}
