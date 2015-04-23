package com.code.aon.ui.warehouse.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.LinesController;

public class WarehouseTransferDetailController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public void onRefresh(ActionEvent event) {
		initializeModel();
	}
}