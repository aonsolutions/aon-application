package com.code.aon.ui.registry.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.common.ICommonConstants;

public class RegistryAddressController extends RegistryAddressLinesController {
	
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

}