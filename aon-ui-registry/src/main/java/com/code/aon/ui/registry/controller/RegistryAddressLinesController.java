package com.code.aon.ui.registry.controller;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.LinesController;

public class RegistryAddressLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String GOOGLE_MAPS_SEARCH_URL = "http://www.google.com/maps/search/";

	public void onLoadGeozone(ActionEvent event){
		RegistryAddress address = (RegistryAddress) getTo();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}
		
	public String getGoogleMapsURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			RegistryAddress ra = (RegistryAddress) getSelectedTO();
			String params = "";
			try {
				params = ra.getAddress().trim();
				if(ra.getNumber()!=null && !"".equals(ra.getNumber()))
					params += " " + ra.getNumber().trim();
				if(ra.getZip()!=null && !"".equals(ra.getZip()))
					params += " " + ra.getZip().trim();
				if(ra.getCity()!=null && !"".equals(ra.getCity()))
					params += " " + ra.getCity().trim();
				if(ra.getGeozone()!=null && ra.getGeozone().getId()!=null)
					params += " " + ra.getGeozone().getName();
				params = URLEncoder.encode(params, StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
				params = "";
			}
			return IDN.toASCII( GOOGLE_MAPS_SEARCH_URL ) + params;
		}
		return null;
	}

}