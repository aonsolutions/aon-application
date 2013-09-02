package com.code.aon.ui.registry.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.LinesController;

public class RegistryAddressController extends LinesController {

	public List<SelectItem> getMunicipalities(){
		ResourceBundle bundle = ResourceBundle.getBundle(IRegistryConstants.MUNICIPALITIES_BUNDLE_NAME);
		List<SelectItem> municipalities = new LinkedList<SelectItem>();
		if(this.getTo()!=null){
			RegistryAddress address = (RegistryAddress) this.getTo();
			if(address!=null && address.getGeozone()!=null && address.getGeozone().getCode()!=null) {
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