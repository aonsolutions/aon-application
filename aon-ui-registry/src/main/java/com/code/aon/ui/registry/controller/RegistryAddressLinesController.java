package com.code.aon.ui.registry.controller;

import java.net.IDN;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
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
	
	public String getCurrentURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			RegistryAddress ra = (RegistryAddress) getSelectedTO();
			String value = IDN.toASCII("http://www.google.es/maps/place/");
			value += IDN.toASCII(ra.getAddress().toLowerCase().replaceAll("s/n", "").trim());
			if(ra.getNumber()!=null && !"".equals(ra.getNumber())){
				value += IDN.toASCII(",+"+ra.getNumber());
			}
			if(ra.getZip()!=null && !"".equals(ra.getZip())){
				value += IDN.toASCII(",+"+ra.getZip());
			}
			if(ra.getCity()!=null && !"".equals(ra.getCity())){
				value += IDN.toASCII(",+"+ra.getCity());
			}
			if(ra.getGeozone()!=null && ra.getGeozone().getId()!=null){
				value += IDN.toASCII(",+"+ra.getGeozone().getName());
			}
			return value;
		}
		return null;
	}

}