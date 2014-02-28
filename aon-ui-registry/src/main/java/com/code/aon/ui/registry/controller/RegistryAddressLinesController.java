package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.LinesController;

public class RegistryAddressLinesController extends LinesController {

	public void onLoadGeozone(ActionEvent event){
		RegistryAddress address = (RegistryAddress) getTo();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}

}