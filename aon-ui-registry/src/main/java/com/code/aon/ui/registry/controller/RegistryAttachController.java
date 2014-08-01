package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.DOCUMENT_MANAGER_CONTROLLER_NAME;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAttachController extends AttachmentController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private RegistryAttachmentType type;
	
	private boolean skipHeredity;
	
	public RegistryAttachmentType getType() {
		return type;
	}

	public void setType(RegistryAttachmentType type) {
		this.type = type;
	}
	
	public boolean isSkipHeredity() {
		return skipHeredity;
	}

	public void setSkipHeredity(boolean skipHeredity) {
		this.skipHeredity = skipHeredity;
	}

	public long getMaximumSize() {
		DocumentManager dm = (DocumentManager) AonUtil.getRegisteredBean(DOCUMENT_MANAGER_CONTROLLER_NAME);
		return dm.getMaximumDocumentSize();
	}	
	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		super.clearCriteria();
		if ( getType() != null ) {
			String type = getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
			getCriteria().addEqualExpression(type, getType());
		}
	}
	
}