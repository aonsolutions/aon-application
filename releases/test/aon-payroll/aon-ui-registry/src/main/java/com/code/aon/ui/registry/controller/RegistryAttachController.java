package com.code.aon.ui.registry.controller;

import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.registry.enumeration.RegistryAttachmentType;

public class RegistryAttachController extends AttachmentController {

	private RegistryAttachmentType type;
	
	public RegistryAttachmentType getType() {
		return type;
	}

	public void setType(RegistryAttachmentType type) {
		this.type = type;
	}
	
}