package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.registry.controller.event.PersonFormListener;
import com.code.aon.ui.util.AonUtil;

public class PersonController extends RegistryController {

	public void onLoadGeozone(ActionEvent event){
		PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME); 
		RegistryAddress address = personForm.getMainAddress();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}
	
	public List<SelectItem> getMunicipalities(){
		PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME);
		return personForm.getMunicipalities();
	}

}
