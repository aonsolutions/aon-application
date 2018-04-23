package com.code.aon.ui.marketing.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.registry.controller.RegistryAttachController;

public class HtmlTemplateController extends RegistryAttachController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private boolean showVariableWindow;
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		AonFile aonFile = new AonFile();
		aonFile.setMimeType(MimeType.MIME_HTML);
		setAonFile(aonFile);
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		RegistryAttachment attach = (RegistryAttachment) getTo();
		AonFile aonFile = new AonFile();
		aonFile.setMimeType(MimeType.MIME_HTML);
		aonFile.setAttachment( attach );
		setAonFile(aonFile);		
	}
	
	public boolean isShowVariableWindow() {
		return showVariableWindow;
	}

	public void setShowVariableWindow(boolean showVariableWindow) {
		this.showVariableWindow = showVariableWindow;
	}
	
	public void onVariablesInfo(ActionEvent event) {
		setShowVariableWindow(true);
	}
}
