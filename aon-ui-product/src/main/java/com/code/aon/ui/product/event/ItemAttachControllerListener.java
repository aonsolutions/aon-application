package com.code.aon.ui.product.event;

import com.code.aon.AonVersion;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.product.ItemAttachment;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ItemAttachController;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemAttachControllerListener extends AttachmentControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			ItemAttachController iaController = (ItemAttachController) event.getController();
			if ( iaController.getType() != null ) {
				IManagerBean iAttachBean = iaController.getManagerBean();
				Criteria criteria = iaController.getCriteria();
				criteria.addEqualExpression(iAttachBean.getFieldName(IEntityAlias.ITEM_ATTACHMENT_TYPE), iaController.getType());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);
		ItemAttachController iaController = (ItemAttachController) event.getController();
		ItemAttachment attach = (ItemAttachment) iaController.getTo();
		if ( iaController.getType() != null ) {
			attach.setType( iaController.getType() );
		}
	}

}