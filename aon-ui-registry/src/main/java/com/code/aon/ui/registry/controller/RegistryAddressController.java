package com.code.aon.ui.registry.controller;

import java.net.IDN;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.form.LinesController;

public class RegistryAddressController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public List<SelectItem> getMunicipalities(){
		ResourceBundle bundle = ResourceBundle.getBundle(ICommonConstants.MUNICIPALITIES_BUNDLE_NAME);
		List<SelectItem> municipalities = new LinkedList<SelectItem>();
		if(this.getTo()!=null){
			RegistryAddress address = (RegistryAddress) this.getTo();
			if(address!=null && address.getGeozone()!=null && StringUtils.isNotBlank(address.getGeozone().getCode())) {
				TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
				for(String key: tree){
					if(key.startsWith(address.getGeozone().getCode())){
						String name = bundle.getString(key);
						SelectItem item = new SelectItem(key, name);
						municipalities.add(item);
					}
				}
			}
		}
		return municipalities;
	}
	
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
			value += IDN.toASCII(ra.getAddress().toLowerCase().replaceAll("s/n", ""));
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