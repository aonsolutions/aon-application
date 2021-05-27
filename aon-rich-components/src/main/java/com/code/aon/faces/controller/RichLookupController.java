package com.code.aon.faces.controller;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.BasicController;

public class RichLookupController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void setLookupTo(ITransferObject value) {
		super.setTo(value);
	}

}