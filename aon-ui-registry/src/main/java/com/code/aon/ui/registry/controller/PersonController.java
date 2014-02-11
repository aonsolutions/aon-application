package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.ui.registry.controller.event.PersonFormListener;
import com.code.aon.ui.util.AonUtil;

public class PersonController extends RegistryController {

	public List<SelectItem> getMunicipalities(){
		PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME);
		return personForm.getMunicipalities();
	}

}
