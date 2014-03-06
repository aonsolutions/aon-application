package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.LinesController;

public class RegistryAddressLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onLoadGeozone(ActionEvent event){
		RegistryAddress address = (RegistryAddress) getTo();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}

}