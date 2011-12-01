package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.DOCUMENT_MANAGER_CONTROLLER_NAME;

import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;

public class RegistryAttachController extends AttachmentController {

	private RegistryAttachmentType type;
	
	public RegistryAttachmentType getType() {
		return type;
	}

	public void setType(RegistryAttachmentType type) {
		this.type = type;
	}
	
	public long getMaximumSize() {
		DocumentManager dm = (DocumentManager) AonUtil.getRegisteredBean(DOCUMENT_MANAGER_CONTROLLER_NAME);
		return dm.getMaximumDocumentSize();
	}	
	
}